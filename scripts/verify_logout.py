# -*- coding: utf-8 -*-
"""验证 /logout 真实可用：登录 -> 调 /logout -> 确认 ①返回成功 ②Redis 里的 token 已删 ③旧 token 再访问被拒"""
import base64
import json
import os
import re
import socket
import sys
import time
import urllib.error
import urllib.request

BASE = os.environ.get("APP_BASE", "http://127.0.0.1:8081")
REDIS_HOST = os.environ.get("REDIS_HOST", "127.0.0.1")
REDIS_PORT = int(os.environ.get("REDIS_PORT", "6379"))


def redis_raw(*args):
    payload = b"*%d\r\n" % len(args)
    for a in args:
        b = a.encode("utf-8")
        payload += b"$%d\r\n%s\r\n" % (len(b), b)
    s = socket.create_connection((REDIS_HOST, REDIS_PORT), timeout=5)
    s.sendall(payload)
    time.sleep(0.05)
    d = s.recv(65536)
    s.close()
    return d


def redis_exists(key):
    return redis_raw("EXISTS", key).strip().startswith(b":1")


def jwt_uuid(token):
    """JWT payload 里的 login_user_key 就是 Redis key 的那段 uuid
    （对应 Constants.LOGIN_USER_KEY，见 TokenService#getLoginUser）"""
    payload = token.split(".")[1]
    payload += "=" * (-len(payload) % 4)
    claims = json.loads(base64.urlsafe_b64decode(payload))
    return claims.get("login_user_key")


def redis_get(key):
    p = b"*2\r\n$3\r\nGET\r\n$%d\r\n%s\r\n" % (len(key), key.encode())
    s = socket.create_connection((REDIS_HOST, REDIS_PORT), timeout=5)
    s.sendall(p)
    time.sleep(0.05)
    d = s.recv(65536)
    s.close()
    t = d.decode("utf-8", "ignore")
    m = re.match(r"^\$(\d+)\r\n", t)
    if m:
        return t[m.end():m.end() + int(m.group(1))]
    return None


def http(method, path, body=None, token=None):
    data = json.dumps(body).encode() if body else None
    req = urllib.request.Request(BASE + path, data=data, method=method)
    req.add_header("Content-Type", "application/json")
    if token:
        req.add_header("Authorization", "Bearer " + token)
    try:
        with urllib.request.urlopen(req, timeout=15) as r:
            return r.status, json.loads(r.read().decode())
    except urllib.error.HTTPError as e:
        return e.code, json.loads(e.read().decode("utf-8", "ignore"))


st, cap = http("GET", "/captchaImage")
uuid = cap["uuid"]
code = redis_get("captcha_codes:" + uuid)
# 验证码用 JSON 序列化器存进 Redis，取出来是带引号的字符串（"4"），必须剥掉
if isinstance(code, str):
    code = code.strip().strip('"')

st, r = http("POST", "/login", {"username": "admin", "password": "admin123",
                                "code": code, "uuid": uuid})
token = r.get("token")
print("1) admin 登录 -> %s" % ("成功" if token else "失败: %s" % r.get("msg")))
if not token:
    raise SystemExit(1)

# 登录后 Redis 里应有 login_tokens:{uuid}。原来这行是 `... if False else None`，
# 等于 docstring 承诺的"确认 Redis 里的 token 已删"根本没执行，现在补回来。
uuid_claim = jwt_uuid(token)
if not uuid_claim:
    print("[SETUP FAIL] JWT payload 里没有 login_user_key，claim 命名与 TokenService 对不上")
    sys.exit(1)
key = "login_tokens:" + uuid_claim
st, before = http("GET", "/getInfo", token=token)
print("2) 登出前用该 token 访问 /getInfo -> code=%s；Redis %s 存在=%s"
      % (before.get("code"), key, redis_exists(key)))

st, out = http("POST", "/logout", token=token)
print("3) POST /logout -> HTTP %s body=%s" % (st, json.dumps(out, ensure_ascii=False)[:120]))

st, after = http("GET", "/getInfo", token=token)
msg = after.get("msg") or ""
print("4) 登出后再用同一 token 访问 /getInfo -> code=%s msg=%s" % (after.get("code"), msg))

redis_cleared = not redis_exists(key)
token_rejected = after.get("code") == 401
# 三条都要成立；不接受"code != 200 或 msg 里带某个词"这种宽松判定
ok = out.get("code") == 200 and redis_cleared and token_rejected
print()
print("   Redis 中该 token 已删除=%s，旧 token 再访问返回 401=%s" % (redis_cleared, token_rejected))
print("结论：/logout %s" % ("可用（返回成功 + Redis 已清 + token 真失效）" if ok else "行为异常，需要复查"))
sys.exit(0 if ok else 1)
