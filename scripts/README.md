# scripts/ — 验证与压测脚本

这些脚本不依赖任何第三方 Python 包（只用标准库），直接 `python xxx.py` 就能跑。
**前提：后端已经在跑，且本机 MySQL / Redis 可用。**

## 环境变量（全部可选，默认值适配本地开发）

| 变量 | 默认值 | 说明 |
|---|---|---|
| `APP_BASE` | `http://127.0.0.1:8081` | 后端地址 |
| `DB_NAME` | `ry_tushu` | 数据库名 |
| `DB_USERNAME` | `root` | 数据库账号 |
| `DB_PASSWORD` | 空 | 数据库口令 |
| `MYSQL_BIN` | `mysql` | mysql 客户端路径（不在 PATH 里时指定） |
| `REDIS_HOST` / `REDIS_PORT` | `127.0.0.1` / `6379` | Redis 地址 |

```bash
# 例：Windows 上跑
set DB_PASSWORD=你的密码
set MYSQL_BIN=C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe
python verify_api.py
```

## 脚本清单

| 脚本 | 作用 | 规模 |
|---|---|---|
| `verify_api.py` | 端到端业务验证：登录、发布→审核→上架、越权防护、订单状态机、并发抢单、缓存主动失效 | **35 项断言** |
| `cache_verify.py` | 缓存专项：读命中、写路径主动失效、TTL 兜底、值可正确反序列化 | **13 项断言** |
| `verify_logout.py` | 验证 `/logout`（Spring Security filter 实现）：返回成功 + token 真的失效 | 4 步 |
| `bench_goods.py` | 压测商品详情接口，对比缓存开 / 关的 QPS 与延迟 | 多进程压测 |
| `api_audit.py` | 前后端接口对账：前端调用的 url 是否都能在后端找到 | 静态扫描 |

## 关键设计

**验证码不靠 OCR**：`/captchaImage` 拿到 `uuid` 后，直接去 Redis 读 `captcha_codes:{uuid}`
拿答案。脚本里自带一个 20 行的 RESP 协议解析器（`parse_resp`），不依赖 redis-py。
> 注意取值要**按 RESP 的长度字段截取**再剥引号 —— 直接 `strip('"')` 会留下内层引号
> （验证码是 JSON 序列化存的，返回形如 `$3\r\n"4"\r\n`）。

**断言看数据库而不是只看接口返回**：比如"下单后商品被锁定"是去 `idle_goods` 表
查 `goods_status`，不是只看接口 `code=200`。

**压测为什么要多进程**：单进程 Python 受 GIL 限制，客户端可能先成为瓶颈，
把服务端的差距掩盖掉。`bench_goods.py` 默认 8 进程 × 8 线程 = 64 并发，并且有 2 秒预热。

## 压测结果（本机实测，2026-09-23）

商品详情接口 `/app/goods/{id}`，64 并发、持续 10 秒、5 条商品数据：

| 指标 | 缓存关 | 缓存开 | 变化 |
|---|---|---|---|
| QPS | 2628 | **19044** | **×7.2** |
| 平均延迟 | 22.29 ms | 3.12 ms | ↓ 86% |
| P50 | 21.32 ms | 2.28 ms | ↓ 89% |
| P95 | 32.86 ms | 7.22 ms | ↓ 78% |
| P99 | 43.87 ms | 19.63 ms | ↓ 55% |
| 失败请求 | 0 | 0 | — |

复现方式（缓存开关由 `biz.cache.enabled` 控制）：

```bash
# 缓存关
java -jar ruoyi-admin.jar --server.port=8081 --biz.cache.enabled=false
python bench_goods.py --path /app/goods/100 --concurrency 64 --duration 10 --label 缓存关

# 缓存开（默认）
java -jar ruoyi-admin.jar --server.port=8081
python bench_goods.py --path /app/goods/100 --concurrency 64 --duration 10 --label 缓存开
```

> 差距主要来自**数据库连接池**（Druid `maxActive=20`）：缓存关闭时每个请求都要抢连接，
> 并发一上来就排队；缓存命中后请求根本不碰数据库。
> 这也说明缓存的价值不只是"单次查询快"，而是**把压力从数据库转移出去**。
