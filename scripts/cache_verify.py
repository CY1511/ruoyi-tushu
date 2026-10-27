# -*- coding: utf-8 -*-
"""验证 cache-aside：读命中、写路径主动失效、TTL 兜底、值可正确反序列化"""
import json
import socket
import subprocess
import sys
import time
import urllib.error
import urllib.request

import os

# ============================================================
# 本脚本所有外部依赖都可通过环境变量覆盖，换机器不用改代码：
#   APP_BASE     后端地址，默认 http://127.0.0.1:8081
#   DB_NAME      数据库名，默认 ry_tushu
#   DB_USERNAME  数据库账号，默认 root
#   DB_PASSWORD  数据库口令，默认空
#   MYSQL_BIN    mysql 客户端可执行文件，默认直接用 PATH 里的 mysql
#   REDIS_HOST   Redis 地址，默认 127.0.0.1
#   REDIS_PORT   Redis 端口，默认 6379
# ============================================================
BASE = os.environ.get("APP_BASE", "http://127.0.0.1:8081")
DB = os.environ.get("DB_NAME", "ry_tushu")
DB_USER = os.environ.get("DB_USERNAME", "root")
DB_PASSWORD = os.environ.get("DB_PASSWORD", "")
MYSQL = os.environ.get("MYSQL_BIN", "mysql")
REDIS_HOST = os.environ.get("REDIS_HOST", "127.0.0.1")
REDIS_PORT = int(os.environ.get("REDIS_PORT", "6379"))
PASS, FAIL = [], []


def check(name, ok, detail=""):
    (PASS if ok else FAIL).append(name)
    print(("  [OK]   " if ok else "  [FAIL] ") + name + (("  -> " + str(detail)) if detail else ""))


def parse_resp(buf, pos=0):
    t = buf[pos:pos + 1]
    end = buf.find(b"\r\n", pos)
    line = buf[pos + 1:end]
    if t == b"+":
        return line.decode(), end + 2
    if t == b"-":
        return ("ERR", line.decode()), end + 2
    if t == b":":
        return int(line), end + 2
    if t == b"$":
        n = int(line)
        return (None, end + 2) if n == -1 else (buf[end + 2:end + 2 + n].decode("utf-8", "ignore"), end + 2 + n + 2)
    if t == b"*":
        n = int(line)
        out, p = [], end + 2
        for _ in range(n):
            v, p = parse_resp(buf, p)
            out.append(v)
        return out, p
    return None, len(buf)


def rcmd(*args):
    payload = b"*%d\r\n" % len(args)
    for a in args:
        b = a.encode("utf-8") if isinstance(a, str) else a
        payload += b"$%d\r\n%s\r\n" % (len(b), b)
    s = socket.create_connection((REDIS_HOST, REDIS_PORT), timeout=5)
    s.sendall(payload)
    time.sleep(0.05)
    buf = b""
    try:
        while True:
            c = s.recv(65536)
            if not c:
                break
            buf += c
            if len(c) < 65536:
                break
    except socket.timeout:
        pass
    s.close()
    return parse_resp(buf)[0]


def http(method, path, body=None, token=None):
    data = json.dumps(body).encode("utf-8") if body is not None else None
    req = urllib.request.Request(BASE + path, data=data, method=method)
    req.add_header("Content-Type", "application/json")
    if token:
        req.add_header("Authorization", "Bearer " + token)
    try:
        with urllib.request.urlopen(req, timeout=20) as r:
            return json.loads(r.read().decode("utf-8"))
    except urllib.error.HTTPError as e:
        return json.loads(e.read().decode("utf-8", "ignore"))


def sql(q):
    r = subprocess.run([MYSQL, "-u" + DB_USER, "-p" + DB_PASSWORD,
                        "--default-character-set=utf8mb4",
                        "-N", "-B", DB, "-e", q], capture_output=True, text=True,
                       encoding="utf-8", errors="ignore")
    return r.stdout.strip()


def login(u, p):
    uuid = http("GET", "/captchaImage")["uuid"]
    code = rcmd("GET", "captcha_codes:" + uuid)
    if isinstance(code, str):
        code = code.strip('"')
    return http("POST", "/login", {"username": u, "password": p, "code": code, "uuid": uuid}).get("token")


print("=" * 70)
print("缓存 cache-aside 验证")
print("=" * 70)
admin = login("admin", "admin123")
seller = login("sellertest", "abc123456")
# 拿卖家自己的商品来验证，避免动到别人的数据；没有则先跑 verify_api.py 建数据
gid = sql("select g.goods_id from idle_goods g "
          "join sys_user u on u.user_id = g.publisher_id and u.user_name = 'sellertest' "
          "order by g.goods_id desc limit 1")
if not gid:
    print("[SETUP FAIL] 没找到 sellertest 的商品，请先执行 verify_api.py 准备测试数据")
    sys.exit(1)
gkey = "idleGoods:" + gid
ckey = "idleCategory:all"
sql("update idle_goods set audit_status='1', goods_status='1' where goods_id=%s" % gid)

print("\n【1】分类缓存：读命中 + TTL + 写失效")
rcmd("DEL", ckey)
r1 = http("GET", "/app/category/tree")
check("首次访问分类树（缓存未命中，查库）", r1.get("code") == 200 and len(r1["data"]) > 0,
      "顶级分类=%s" % len(r1.get("data") or []))
check("回填后 Redis 出现 idleCategory:all", rcmd("EXISTS", ckey) == 1)
ttl = rcmd("TTL", ckey)
check("带兜底 TTL（约 2 小时）", isinstance(ttl, int) and 7000 < ttl <= 7200, "TTL=%s" % ttl)
r2 = http("GET", "/app/category/tree")
check("二次访问走缓存且内容一致",
      r2.get("code") == 200 and len(r2["data"]) == len(r1["data"]),
      "%s vs %s" % (len(r1["data"]), len(r2.get("data") or [])))
before_ttl = rcmd("TTL", ckey)
time.sleep(4)
after_ttl = rcmd("TTL", ckey)
# 只断言"TTL 变小了"不够：key 不存在时 TTL 返回 -2，同样算变小，
# 缓存功能整个删掉这条也照样绿。必须要求 key 还在、且只是自然衰减。
check("二次访问命中缓存（key 仍在且 TTL 只自然衰减，未被重建）",
      isinstance(before_ttl, int) and isinstance(after_ttl, int) and 0 < after_ttl < before_ttl,
      "before=%s after=%s" % (before_ttl, after_ttl))
r = http("PUT", "/system/category", {"categoryId": 1, "categoryName": "公共课教材", "status": "0"}, token=admin)
check("改分类成功且缓存被主动删除", r.get("code") == 200 and rcmd("EXISTS", ckey) == 0,
      "msg=%s EXISTS=%s" % (r.get("msg"), rcmd("EXISTS", ckey)))

print("\n【2】商品详情缓存：读命中 + 值可反序列化")
rcmd("DEL", gkey)
r1 = http("GET", "/app/goods/%s" % gid)
check("首次访问详情（未命中，查库）", r1.get("code") == 200, r1.get("msg"))
check("回填后 Redis 出现 %s" % gkey, rcmd("EXISTS", gkey) == 1)
ttl = rcmd("TTL", gkey)
check("带兜底 TTL（约 10 分钟）", isinstance(ttl, int) and 500 < ttl <= 600, "TTL=%s" % ttl)
r2 = http("GET", "/app/goods/%s" % gid)
db_name = sql("select goods_name from idle_goods where goods_id=%s" % gid)
check("二次访问走缓存，字段解出来正确（JSON 反序列化没丢数据）",
      r2.get("code") == 200 and (r2.get("data") or {}).get("goodsName") == db_name,
      "缓存返回=%s 库中=%s" % ((r2.get("data") or {}).get("goodsName"), db_name))

print("\n【3】写路径主动失效")
before = rcmd("TTL", gkey)
time.sleep(4)
r = http("PUT", "/app/goods", {"goodsId": int(gid), "goodsName": db_name, "price": 17.00}, token=seller)
check("卖家改商品后缓存被删（TTL 未回落说明没被重建）",
      r.get("code") == 200 and rcmd("EXISTS", gkey) == 0,
      "msg=%s EXISTS=%s beforeTTL=%s" % (r.get("msg"), rcmd("EXISTS", gkey), before))
sql("update idle_goods set audit_status='1', goods_status='1' where goods_id=%s" % gid)
new_price = sql("select price from idle_goods where goods_id=%s" % gid)
r3 = http("GET", "/app/goods/%s" % gid)
got_price = (r3.get("data") or {}).get("price")
# 缓存删掉只是手段，真正的验收标准是"下一次读到的是新值"
check("改价后立即再读，拿到的是新价格（缓存里没有旧值残留）",
      got_price is not None and abs(float(got_price) - float(new_price)) < 0.001,
      "接口返回=%s 库中=%s" % (got_price, new_price))
http("GET", "/app/goods/%s" % gid)
r = http("PUT", "/system/goods/offline/%s" % gid, token=admin)
check("管理员下架后缓存被删", r.get("code") == 200 and rcmd("EXISTS", gkey) == 0,
      "msg=%s EXISTS=%s" % (r.get("msg"), rcmd("EXISTS", gkey)))
sql("update idle_goods set audit_status='1', goods_status='1' where goods_id=%s" % gid)

http("GET", "/app/goods/%s" % gid)
r = http("PUT", "/system/goods/online/%s" % gid, token=admin)
check("管理员上架后缓存被删", r.get("code") == 200 and rcmd("EXISTS", gkey) == 0,
      "msg=%s EXISTS=%s" % (r.get("msg"), rcmd("EXISTS", gkey)))

print("\n" + "=" * 70)
print("通过 %d 项，失败 %d 项" % (len(PASS), len(FAIL)))
for f in FAIL:
    print("  失败: " + f)
print("=" * 70)
# 失败要反映到进程退出码，否则调用方 / CI 看不出来
sys.exit(1 if FAIL else 0)
