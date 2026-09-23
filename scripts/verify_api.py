# -*- coding: utf-8 -*-
"""
若依书铺 · 端到端接口验证
真实调用运行中的后端 + 真实读写 MySQL / Redis，逐项打印结果。
"""
import io
import json
import socket
import subprocess
import sys
import time
import urllib.error
import urllib.parse
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


# ---------------------------------------------------------------- Redis
def redis_cmd(*args):
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
            chunk = s.recv(65536)
            if not chunk:
                break
            buf += chunk
            if len(chunk) < 65536:
                break
    except socket.timeout:
        pass
    s.close()
    return parse_resp(buf)[0]


def parse_resp(buf, pos=0):
    if pos >= len(buf):
        return None, pos
    t = buf[pos:pos + 1]
    end = buf.find(b"\r\n", pos)
    line = buf[pos + 1:end]
    if t == b"+":
        return line.decode("utf-8", "ignore"), end + 2
    if t == b"-":
        return ("ERR", line.decode("utf-8", "ignore")), end + 2
    if t == b":":
        return int(line), end + 2
    if t == b"$":
        n = int(line)
        if n == -1:
            return None, end + 2
        return buf[end + 2:end + 2 + n].decode("utf-8", "ignore"), end + 2 + n + 2
    if t == b"*":
        n = int(line)
        out = []
        p = end + 2
        for _ in range(n):
            v, p = parse_resp(buf, p)
            out.append(v)
        return out, p
    return None, len(buf)


# ---------------------------------------------------------------- MySQL
def sql(query):
    r = subprocess.run([MYSQL, "-u" + DB_USER, "-p" + DB_PASSWORD,
                        "--default-character-set=utf8mb4",
                        "-N", "-B", DB, "-e", query],
                       capture_output=True, text=True, encoding="utf-8", errors="ignore")
    return r.stdout.strip()


# ---------------------------------------------------------------- HTTP
def http(method, path, body=None, token=None, base=BASE):
    url = base + path
    data = json.dumps(body).encode("utf-8") if body is not None else None
    req = urllib.request.Request(url, data=data, method=method)
    req.add_header("Content-Type", "application/json")
    if token:
        req.add_header("Authorization", "Bearer " + token)
    try:
        with urllib.request.urlopen(req, timeout=20) as resp:
            return json.loads(resp.read().decode("utf-8"))
    except urllib.error.HTTPError as e:
        raw = e.read().decode("utf-8", "ignore")
        try:
            return json.loads(raw)
        except Exception:
            return {"code": e.code, "msg": raw[:200]}


def captcha():
    """取验证码：uuid 由接口返回，答案从 Redis 里读（说明验证码确实走了 Redis）"""
    r = http("GET", "/captchaImage")
    uuid = r.get("uuid")
    if not uuid:
        return None, None
    val = redis_cmd("GET", "captcha_codes:" + uuid)
    if isinstance(val, str):
        val = val.strip('"')
    return uuid, val


def login(username, password, token_base=BASE):
    uuid, code = captcha()
    return http("POST", "/login", {"username": username, "password": password,
                                   "code": code, "uuid": uuid}, base=token_base)


print("=" * 70)
print("若依书铺 · 端到端验证")
print("=" * 70)

# ---------------------------------------------------------------- 1. 登录
print("\n【1】管理端登录（验证码走 Redis + JWT 下发）")
uuid, code = captcha()
check("GET /captchaImage 返回 uuid", bool(uuid), uuid)
check("验证码答案可从 Redis 读出（captcha_codes:*）", bool(code), code)
r = http("POST", "/login", {"username": "admin", "password": "admin123",
                            "code": code, "uuid": uuid})
admin_token = r.get("token")
check("admin/admin123 登录成功", bool(admin_token), r.get("msg"))

# ---------------------------------------------------------------- 2. 公开接口 + 分类缓存
print("\n【2】公开接口与分类缓存")
redis_cmd("DEL", "idleCategory:all")
r = http("GET", "/app/category/tree")
check("GET /app/category/tree 返回分类树", r.get("code") == 200 and len(r.get("data") or []) > 0,
      "顶级分类数=%s" % len(r.get("data") or []))
hit = redis_cmd("EXISTS", "idleCategory:all")
check("首次调用后 Redis 出现 idleCategory:all", hit == 1, "EXISTS=%s" % hit)

# ---------------------------------------------------------------- 3. 建两个用户
print("\n【3】注册买家/卖家并登录")


def register_and_login(username, nickname):
    uuid2, code2 = captcha()
    r = http("POST", "/register", {"username": username, "password": "abc123456",
                                   "confirmPassword": "abc123456",
                                   "code": code2, "uuid": uuid2})
    if r.get("code") != 200:
        print("     注册返回:", r.get("msg") or r.get("code"))
    r = login(username, "abc123456")
    return r.get("token"), r.get("msg")


seller_token, msg = register_and_login("sellertest", "卖家测试")
check("卖家账号注册并登录", bool(seller_token), msg)
buyer_token, msg = register_and_login("buyertest", "买家测试")
check("买家账号注册并登录", bool(buyer_token), msg)

# ---------------------------------------------------------------- 4. 发布商品
print("\n【4】卖家发布商品 -> 管理员审核 -> 上架")
r = http("POST", "/app/goods", {
    "goodsName": "数据结构（C语言版）",
    "categoryId": 1,
    "price": 18.50,
    "originalPrice": 45.00,
    "qualityLevel": "九成新",
    "courseName": "数据结构",
    "isbn": "9787302121777",
    "description": "大三下用完，无笔记",
    "publisherId": 999999,          # 恶意传值：服务端应忽略
    "auditStatus": "1",             # 恶意传值：服务端应忽略
    "goodsStatus": "1"
}, token=seller_token)
check("POST /app/goods 发布商品", r.get("code") == 200, r.get("msg"))
row = sql("select goods_id, publisher_id, audit_status, goods_status from idle_goods "
          "where goods_name='数据结构（C语言版）' order by goods_id desc limit 1")
gid, pub_id, audit_st, goods_st = (row.split("\t") + ["", "", "", ""])[:4]
seller_id = sql("select user_id from sys_user where user_name='sellertest'")
check("商品已入库且属于发布者", pub_id == seller_id, "publisher_id=%s 期望=%s" % (pub_id, seller_id))
check("服务端忽略了请求体伪造的 auditStatus/goodsStatus",
      audit_st == "0" and goods_st == "0", "audit_status=%s goods_status=%s" % (audit_st, goods_st))

r = http("PUT", "/system/goods/audit", {"goodsId": int(gid), "auditStatus": "1",
                                        "auditRemark": "资料齐全，通过"}, token=admin_token)
check("PUT /system/goods/audit 审核通过", r.get("code") == 200, r.get("msg"))
row = sql("select audit_status, goods_status, audit_user, "
          "(select user_name from sys_user where user_id=cast(audit_user as unsigned)) "
          "from idle_goods where goods_id=%s" % gid)
a_st, g_st, a_user, a_name = (row.split("\t") + [""] * 4)[:4]
check("审核后状态为 通过/上架", a_st == "1" and g_st == "1", "audit=%s goods=%s" % (a_st, g_st))
check("审核人名称正确联出（修复前恒为空）", a_name == "admin",
      "audit_user=%s audit_user_name=%s" % (a_user, a_name))

# ---------------------------------------------------------------- 5. 商品详情缓存
print("\n【5】商品详情缓存与主动失效")
redis_cmd("DEL", "idleGoods:%s" % gid)
r1 = http("GET", "/app/goods/%s" % gid)
hit = redis_cmd("EXISTS", "idleGoods:%s" % gid)
check("匿名访问详情成功且写入缓存 idleGoods:%s" % gid,
      r1.get("code") == 200 and hit == 1, "EXISTS=%s" % hit)
ttl = redis_cmd("TTL", "idleGoods:%s" % gid)
check("缓存带 TTL（兜底过期）", isinstance(ttl, int) and 0 < ttl <= 600, "TTL=%s" % ttl)

r = http("PUT", "/app/goods", {"goodsId": int(gid), "goodsName": "数据结构（C语言版）第2版",
                               "price": 17.00}, token=seller_token)
after = redis_cmd("EXISTS", "idleGoods:%s" % gid)
check("卖家修改商品后缓存被主动清除", r.get("code") == 200 and after == 0,
      "msg=%s EXISTS=%s" % (r.get("msg"), after))
sql("update idle_goods set audit_status='1', goods_status='1' where goods_id=%s" % gid)

# ---------------------------------------------------------------- 6. 越权修复
print("\n【6】越权防护")
r = http("PUT", "/app/goods", {"goodsId": int(gid), "goodsName": "我要改别人的"}, token=buyer_token)
check("买家用 PUT /app/goods 改他人商品被拒", r.get("code") == 403,
      "code=%s msg=%s" % (r.get("code"), r.get("msg")))
r = http("DELETE", "/app/goods/%s" % gid, token=buyer_token)
check("买家删他人商品被拒", r.get("code") == 403, "code=%s msg=%s" % (r.get("code"), r.get("msg")))

# ---------------------------------------------------------------- 7. 下单 + 状态机
print("\n【7】订单状态机")
r = http("POST", "/app/order", {"goodsId": int(gid), "orderPrice": 0.01}, token=buyer_token)
check("买家下单成功", r.get("code") == 200, r.get("msg"))
order = sql("select order_id, order_status, order_price, seller_id, buyer_id from idle_order "
            "order by order_id desc limit 1")
oid, ost, oprice, oseller, obuyer = (order.split("\t") + [""] * 5)[:5]
check("下单后状态为 0 待确认", ost == "0", ost)
check("成交价以服务端商品价格为准，忽略请求体的 0.01",
      oprice.startswith("17"), "order_price=%s" % oprice)
g_st = sql("select goods_status from idle_goods where goods_id=%s" % gid)
check("商品被锁定为 2 已预订（条件更新抢单）", g_st == "2", g_st)
logs = sql("select count(*) from idle_order_status_log where order_id=%s" % oid)
check("写下单流转日志", logs == "1", "日志条数=%s" % logs)

# 非法流转：0 -> 2 直接完成
r = http("PUT", "/system/order/changeStatus", {"orderId": int(oid), "orderStatus": "2"},
         token=admin_token)
check("非法流转 待确认->已完成 被拦截", r.get("code") == 500 and "不能从" in str(r.get("msg")),
      "code=%s msg=%s" % (r.get("code"), r.get("msg")))

# 通用修改接口不允许改状态
r = http("PUT", "/system/order", {"orderId": int(oid), "orderStatus": "3"}, token=admin_token)
check("通用修改接口拒绝携带 orderStatus", r.get("code") == 500 and "状态" in str(r.get("msg")),
      "msg=%s" % r.get("msg"))

# 买家不能替卖家接单
r = http("PUT", "/app/order/accept/%s" % oid, token=buyer_token)
check("买家替卖家确认接单被拒", r.get("code") == 403, "code=%s msg=%s" % (r.get("code"), r.get("msg")))

# 正常链路
r = http("PUT", "/app/order/accept/%s" % oid, token=seller_token)
check("卖家确认接单 0->1", r.get("code") == 200, r.get("msg"))
check("接单后订单状态=1", sql("select order_status from idle_order where order_id=%s" % oid) == "1")

r = http("PUT", "/app/order/confirm/%s" % oid, token=buyer_token)
check("买家确认收货 1->2", r.get("code") == 200, r.get("msg"))
check("确认后订单状态=2 且商品=3 已售出",
      sql("select order_status from idle_order where order_id=%s" % oid) == "2" and
      sql("select goods_status from idle_goods where goods_id=%s" % gid) == "3")
r = http("PUT", "/app/order/cancel/%s?reason=" % oid + urllib.parse.quote("不想要了"), token=buyer_token)
check("终态订单再取消被拒（2 是终态）", r.get("code") == 500 and "不能从" in str(r.get("msg")),
      "msg=%s" % r.get("msg"))

logs = sql("select count(*) from idle_order_status_log where order_id=%s" % oid)
roles = sql("select group_concat(distinct operator_role) from idle_order_status_log where order_id=%s" % oid)
check("三次合法流转各落一条日志", logs == "3", "日志条数=%s 角色=%s" % (logs, roles))
check("流转日志记录了操作人角色", "BUYER" in roles and "SELLER" in roles, roles)

# 管理端可流转状态查询
r = http("GET", "/system/order/nextStatus/%s" % oid, token=admin_token)
check("GET /system/order/nextStatus 终态返回空选项",
      r.get("code") == 200 and (r.get("data") == [] or r.get("data") is None),
      "data=%s" % r.get("data"))

# ---------------------------------------------------------------- 8. 并发抢单
print("\n【8】并发下单（条件更新抢单）")
sql("update idle_goods set goods_status='1', audit_status='1' where goods_id=%s" % gid)
sql("delete from idle_order where goods_id=%s" % gid)
before = sql("select count(*) from idle_order where goods_id=%s" % gid)
ok_cnt, err_cnt = 0, 0
for i in range(5):
    rr = http("POST", "/app/order", {"goodsId": int(gid)}, token=buyer_token)
    if rr.get("code") == 200:
        ok_cnt += 1
    else:
        err_cnt += 1
after_cnt = sql("select count(*) from idle_order where goods_id=%s" % gid)
check("同一商品 5 次下单只成功 1 单",
      ok_cnt == 1 and err_cnt == 4 and after_cnt == "1",
      "成功=%s 失败=%s 库中订单=%s" % (ok_cnt, err_cnt, after_cnt))

print("\n" + "=" * 70)
print("通过 %d 项，失败 %d 项" % (len(PASS), len(FAIL)))
if FAIL:
    print("失败项：")
    for f in FAIL:
        print("  - " + f)
print("=" * 70)
