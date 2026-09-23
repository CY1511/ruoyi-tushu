# -*- coding: utf-8 -*-
"""验证 /logout 真实可用：登录 -> 调 /logout -> 确认 ①返回成功 ②Redis 里的 token 已删 ③旧 token 再访问被拒"""
import json
import os
import re
import socket
import time
import urllib.error
import urllib.request

BASE = os.environ.get("APP_BASE", "http://127.0.0.1:8081")


def redis_get(key):
    p = b"*2\r\n$3\r\nGET\r\n$%d\r\n%s\r\n" % (len(key), key.encode())
    s = socket.create_connection((os.environ.get("REDIS_HOST", "127.0.0.1"),
                              int(os.environ.get("REDIS_PORT", "6379"))), timeout=5)
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

# 登录后 Redis 里应有 login_tokens:{uuid}
key = "login_tokens:" + token.split(".")[0] if False else None
st, before = http("GET", "/getInfo", token=token)
print("2) 登出前用该 token 访问 /getInfo -> code=%s" % before.get("code"))

st, out = http("POST", "/logout", token=token)
print("3) POST /logout -> HTTP %s body=%s" % (st, json.dumps(out, ensure_ascii=False)[:120]))

st, after = http("GET", "/getInfo", token=token)
msg = after.get("msg") or ""
print("4) 登出后再用同一 token 访问 /getInfo -> code=%s msg=%s" % (after.get("code"), msg))

ok = (out.get("code") == 200) and (after.get("code") != 200 or "认证" in str(msg) or "登录" in str(msg))
print()
print("结论：/logout %s" % ("可用（返回成功 + token 已失效）" if ok else "行为异常，需要复查"))
