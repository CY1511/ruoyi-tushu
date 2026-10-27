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
| `GRAB_THREADS` | `8` | 抢单并发用例的线程数 |

**执行顺序**：先 `verify_api.py` 再 `cache_verify.py` —— 后者的数据前置是前者创建的
卖家商品；数据不全时 `cache_verify.py` 会直接以退出码 1 报"请先执行 verify_api.py"。

**退出码**：三个验证脚本只要有断言失败就以非 0 退出（`1`），方便 CI 和 shell 里
`&&` 串联判断；全通过才是 0。

```bash
# 例：Windows 上跑
set DB_PASSWORD=你的密码
set MYSQL_BIN=C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe
python verify_api.py
```

## 脚本清单

| 脚本 | 作用 | 规模 |
|---|---|---|
| `verify_api.py` | 端到端业务验证：登录、发布→审核→上架、越权防护、订单状态机、真并发抢单、商品下架/上架、收藏闭环与级联清理、分类缓存失效 | **55 项断言** |
| `cache_verify.py` | 缓存专项：读命中、写路径主动失效、失效后读到新值、TTL 兜底、值可正确反序列化 | **14 项断言** |
| `verify_logout.py` | 验证 `/logout`：接口返成功 + **Redis 里 `login_tokens:{uuid}` 真被删** + 旧 token 再访问返 401 | 3 项确认 |
| `bench_goods.py` | 压测商品详情接口，对比缓存开 / 关的 QPS 与延迟 | 多进程压测 |
| `api_audit.py` | 前后端接口对账：前端调用的 url 是否都能在后端找到 | 静态扫描 |

## 关键设计

**验证码不靠 OCR**：`/captchaImage` 拿到 `uuid` 后，直接去 Redis 读 `captcha_codes:{uuid}`
拿答案。脚本里自带一个 20 行的 RESP 协议解析器（`parse_resp`），不依赖 redis-py。
> 注意取值要**按 RESP 的长度字段截取**再剥引号 —— 直接 `strip('"')` 会留下内层引号
> （验证码是 JSON 序列化存的，返回形如 `$3\r\n"4"\r\n`）。

**断言看数据库而不是只看接口返回**：比如"下单后商品被锁定"是去 `idle_goods` 表
查 `goods_status`，不是只看接口 `code=200`。

**抢单用例是真并发**：`GRAB_THREADS` 个线程卡在 `threading.Barrier` 上，倒数同时冲出去
下单，再断言"成功 1 单、库里 1 条订单"。串行 for 循环只能证明"第二次因为状态变了被拒"，
证明不了条件更新在竞态下只放行一条 —— 这两种写法结果看起来一样，含金量完全不同。

**缓存失效的最终验收是"读到新值"**：`cache_verify.py` 除了看 key 有没有被删，
还会在改价后立刻再读一次详情，断言接口返回的价格等于库里新的 `price`。
只断言 `EXISTS=0` 或 `TTL 变小` 的话，把缓存整个删掉脚本也照样绿。

**看接口的 `code`，不要看 HTTP 状态**：若依的鉴权失败是用 HTTP 200 + body `{"code":401}` 返回的
（`renderString` 只设了 Content-Type）。所以"越权被拒""token 已失效"这类断言一律解析 JSON body，
`curl -o /dev/null -w '%{http_code}'` 在这里会给你全是 200 的假象。

**Redis 里令牌 key 怎么定位**：`login_tokens:` 后面那串不是 token 本身，而是 JWT payload 里的
`login_user_key` 声明（`Constants.LOGIN_USER_KEY`）。`verify_logout.py` 会解出这段再 `EXISTS` 检查，
所以它能真正证明"退出后服务端把令牌删掉了"，而不只是"再访问被拒"。

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
