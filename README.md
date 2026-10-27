# 若依书铺（校园教材闲置交易平台）

基于 **RuoYi-Vue v3.9.2** 二次开发的校园教材闲置交易平台：学生在平台上发布闲置教材、
互相交易，管理端负责商品审核、订单与分类管理。

后端 Spring Boot 4 + MyBatis + MySQL + Redis，管理端 Vue2 + Element UI，
另有一套面向学生的 H5 端（`ruoyi-app`）。

---

## 一、功能

### C 端（学生，`/app/**` 接口 + `ruoyi-app` H5）

| 模块 | 说明 |
|---|---|
| 商品 | 发布、编辑、删除、**上架/下架**、按分类/关键词浏览、详情查看 |
| 商品审核 | 发布后进入待审核，审核通过才在列表露出；**编辑**后重新进入待审核；**下架不回退审核状态**（独立接口，见下） |
| 收藏 | 收藏/取消收藏、我的收藏、是否已收藏 |
| 订单 | 下单、卖家接单、买家确认收货、取消订单、买卖双方订单列表 |
| 我的 | 我发布的商品、统计数据（发布数/订单数/收藏数） |

### 管理端（`/system/**` 接口 + `ruoyi-ui`）

| 模块 | 说明 |
|---|---|
| 商品管理 | 列表、新增、编辑、删除、导出、**审核（通过/驳回）**、上架、下架 |
| 订单管理 | 列表、导出、**状态流转（只允许合法流转）**、**流转记录时间线** |
| 分类管理 | 商品分类增删改查 |
| 收藏管理 | 收藏记录查询、删除、导出 |
| 系统管理 | 沿用若依的用户/角色/菜单/部门/岗位/字典/参数/通知公告 |
| 系统监控 | 在线用户、定时任务、数据监控、服务监控、缓存监控 |

---

## 二、技术栈

| 层面 | 选型 |
|---|---|
| 后端 | Spring Boot 4.0.6 · JDK 17（实测 JDK 21 亦可）· Maven 多模块 |
| 安全 | Spring Security + JWT（无状态）· `@PreAuthorize` 权限注解 · BCrypt 存密码 |
| 持久层 | MyBatis + XML Mapper · PageHelper 分页 · Druid 连接池 |
| 缓存 | Redis（验证码/令牌 + **业务数据缓存**） |
| 数据库 | MySQL 5.7 / 8.0 |
| 接口文档 | springdoc-openapi（生产环境可一键关闭） |
| 管理端前端 | Vue 2.6 · Element UI 2.15 · Vue CLI |
| 学生端 H5 | Vue 2（`ruoyi-app`，独立前端工程，不参与 Maven 构建） |

---

## 三、目录结构

```
ruoyi-tushu
├── ruoyi-admin        # 启动类、web 层（controller）、配置文件
├── ruoyi-framework    # 框架核心：Security / Redis / AOP / 数据源等配置
├── ruoyi-system       # 业务核心：domain / mapper / service（业务代码都在这里）
├── ruoyi-common       # 通用工具、常量、注解、异常
├── ruoyi-quartz       # 定时任务
├── ruoyi-generator    # 代码生成器
├── ruoyi-ui           # 管理端前端（Vue2 + Element UI）
├── ruoyi-app          # 学生端 H5（Vue2，独立工程）
├── sql                # 数据库脚本（唯一来源，见 sql/README.md）
├── scripts            # 接口验证、缓存验证、压测脚本（见 scripts/README.md）
├── docs               # 业务流程与接口文档
└── bin / ry.sh        # 构建与启动脚本
```

> 业务代码的边界是明确划分的：`ruoyi-system` 只放 domain/mapper/service，
> `ruoyi-admin` 只放启动类、配置与 controller，`ruoyi-framework` 不放业务代码。

---

## 四、快速启动

### 0. 一键启动（Windows 本地开发）

本机项目根目录有两个便捷脚本（**含本机数据库口令，已加入 .gitignore，不会进仓库**，换机器需要自己重建）：

| 脚本 | 作用 |
|---|---|
| `start-dev.bat` | 清理残留进程 → 缺 jar 则自动打包 → 起后端 8081 → 两侧前端缺依赖则自动装 → 起管理端 80 与 C 端 84 |
| `stop-dev.bat` | 按端口精确结束进程（不会误杀其他 java / node） |

脚本内已处理四个环境坑，手动启动时同样需要注意：

1. **随机端口**：沙箱/IDE 注入的 `SERVER__PORT`、`SERVER__HOST` 会把 Spring Boot 顶到随机端口，导致前端代理打不通，必须清掉后用 `--server.port=8081` 指定；
2. **jar 被锁**：只要 `java` 进程还开着，Windows 会锁住 jar，`mvn package` 会报成功但产物根本没换，跑起来还是旧代码 —— 打包前必须先杀进程；
3. **Node 17+ 跑 webpack4**：前端必须带 `NODE_OPTIONS=--openssl-legacy-provider`，否则报 `ERR_OSSL_EVP_UNSUPPORTED`；
4. **删过文件必须 `clean package`**：只 `package` 时，`target/classes` 里被删掉的 Mapper XML 不会消失，
   打进 jar 后 MyBatis 仍然去解析它，结果是"代码删干净了、启动却报 `Could not resolve type alias`"；
   而且 `ruoyi-admin` 的 jar 插件对自己模块外的变化不敏感，可能继续嵌着旧的 `ruoyi-system`。
   改了 `ruoyi-system` 却看不到效果，先怀疑这一条。

### 1. 建库（必须按顺序执行）

```bash
mysql -uroot -p -e "CREATE DATABASE ry_tushu DEFAULT CHARACTER SET utf8mb4;"

mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/ry_20260417.sql            # 框架表 + 系统菜单
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/quartz.sql                 # 定时任务表
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/v1.0.0_business_tables.sql # 业务表 + 初始化数据
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/v1.2.0_order_status_log.sql# 订单流转日志表
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/v1.3.0_business_menu.sql   # 业务菜单与按钮权限
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/v1.4.0_drop_legacy_and_index.sql # 清理二代残留 + 商品列表复合索引
```

脚本清单、表结构定稿依据、二代模型的移除说明都写在 **[`sql/README.md`](sql/README.md)**。

### 2. 配置数据源（不要改代码，用环境变量）

`application-druid.yml` 里连接信息全部走环境变量，默认连本地 `ry_tushu`：

| 环境变量 | 默认值 | 说明 |
|---|---|---|
| `DB_HOST` / `DB_PORT` | `localhost` / `3306` | 数据库地址 |
| `DB_NAME` | `ry_tushu` | 库名 |
| `DB_USERNAME` / `DB_PASSWORD` | `root` / 空 | 账号密码 |
| `JWT_SECRET` | 内置默认值 | **生产必须替换**（默认值在仓库里，等于公开）；HS512 要求 **≥ 64 字节** |
| `SWAGGER_ENABLED` | `true` | 生产建议 `false`，关掉后文档/Druid 一律 401 |
| `LOG_LEVEL_APP` | `debug` | 生产建议 `info`；`debug` 会把每条 SQL 打进日志 |
| `DRUID_USERNAME` / `DRUID_PASSWORD` | `admin` / 空 | 用监控台就必须设强口令 |

### 3. 启动后端

```bash
# 需 Redis 与 MySQL 已就绪（Redis 默认 localhost:6379）
DB_USERNAME=root DB_PASSWORD=你的密码 mvn -pl ruoyi-admin -am spring-boot:run

# 或打包后运行
mvn -DskipTests clean package
DB_USERNAME=root DB_PASSWORD=你的密码 java -jar ruoyi-admin/target/ruoyi-admin.jar
```

- 后端端口：**8081**（`application.yml` 的 `server.port`）
- 接口文档：<http://localhost:8081/swagger-ui.html>（`swagger.enabled=false` 时不可访问）

### 4. 启动前端（管理端与 C 端是两个独立工程，按需启动）

```bash
# 管理端（ruoyi-ui）：vue-cli 4 + webpack4，端口 80，代理 /dev-api → localhost:8081
cd ruoyi-ui  && npm install && npm run dev
# Node 17+ 必须加：export NODE_OPTIONS=--openssl-legacy-provider

# C 端 H5（ruoyi-app）：vue-cli 5 + webpack5，端口 84（写在 ruoyi-app/vue.config.js 里），
#                     同样代理 /dev-api → localhost:8081
cd ruoyi-app && npm install && npm run dev
```

> **两个前端都依赖后端跑在 8081**：`vue.config.js` 里的代理目标写死为 `http://localhost:8081`。
> 后端换端口的话，页面能打开但所有接口 404。

### 5. 默认账号

| 账号 | 密码 | 说明 |
|---|---|---|
| `admin` | `admin123` | 超级管理员（拥有 `*:*:*`，无需分配菜单权限），管理端与 C 端都可登录 |
| `sellertest` | `abc123456` | C 端卖家测试账号（端到端验证脚本创建） |
| `buyertest` | `abc123456` | C 端买家测试账号（端到端验证脚本创建） |
| 自助注册 | — | C 端用户（框架参数 `sys.account.registerUser` 已置为 true） |

> 生产部署请第一时间修改 admin 密码、替换 `JWT_SECRET`、关闭接口文档与 Druid 监控台。

---

## 五、两个核心设计

### 1. 订单状态机

订单状态不再"前端传什么就是什么"，服务端只放行合法流转：

```
        卖家接单              买家确认收货
 0 待确认 ───────► 1 交易中 ──────────────► 2 已完成（终态）
    │                 │
    └──── 取消 ────────┴──► 3 已取消（终态）
```

- 状态枚举与合法流转表集中在 `IdleOrderStatus`，业务代码里不再散落 `if`
- 每次成功流转都往 `idle_order_status_log` 写一条记录（订单号、变更前后状态、
  操作人、角色 BUYER/SELLER/ADMIN、备注、时间），与订单更新**同一事务**
- **事务注解挂在每个对外入口上**（`acceptOrder` / `confirmReceipt` / `cancelOrder` /
  `changeStatusByAdmin`），不能只挂在内部的 `changeStatus` 上 —— 那些入口是用
  `this.changeStatus(...)` 调它的，Spring 代理拦不到类内部调用，只标内部方法等于没标
- 通用修改接口 `updateIdleOrder` 拒绝携带 `orderStatus`，从入口堵死绕过校验
- 管理端「流转」弹窗的可选状态由后端 `GET /system/order/nextStatus/{orderId}` 下发，
  界面上根本选不出非法状态
- 商品侧同样补了状态守卫：`auditIdleGoods` 只接受"待审核"的商品、审核结果只能是
  通过/驳回，上架必须是已过审 —— 前端"只在待审核时显示审核按钮"是体验优化，
  不能当成唯一屏障

### 2. 缓存设计（cache-aside + 主动失效）

| 缓存对象 | key | TTL | 失效时机 |
|---|---|---|---|
| 商品详情 | `idleGoods:{goodsId}` | 10 分钟 | 改/审/上下架/被下单/删除 |
| 启用分类列表 | `idleCategory:all` | 2 小时 | 分类增/删/改 |

- **正确性靠主动失效**：商品一下架缓存立刻删掉，否则会出现"已下架商品还能被看到并下单"
- **失效动作延迟到事务提交之后**：在事务里就删缓存，提交前的并发读会用旧行数据把缓存
  重新填回去，脏窗口反而拉到 TTL 到期。统一走 `runAfterCommit(...)`
  （有事务就注册 afterCommit 回调，没事务则立即删）
- **TTL 只做兜底**：万一某条写路径漏了删除，最多脏 10 分钟
- 缓存的是**原始行数据**，可见性判断（未过审/已下架只有发布者可见）留在请求级逻辑里，
  不把"某个用户看到的视图"缓存进 Redis
- 压测开关 `--biz.cache.enabled=false` 同时管着商品详情和分类列表两条读路径

> 实现上没有用 `@Cacheable/@CacheEvict`，原因写在 `IdleGoodsServiceImpl` 类注释里：
> 本机 Redis 3.2（Windows 版）+ Spring Data Redis 4 的组合下，
> `RedisCache.evict()/clear()` 实测是**静默空操作**（代码探针验证：`cache.get` 命中为 true，
> evict 后 key 仍在 Redis，而同进程 `RedisTemplate.delete` 正常）。改用框架自带
> `RedisCache` 手写 cache-aside，读写删三条路径都有用例覆盖，且不挑 Redis 版本。

---

## 六、如何验证

验证脚本都在 [`scripts/`](scripts/README.md)，可以直接复跑；**只要有断言失败，脚本就以退出码 1 结束**，
不看输出内容也能被 CI / `&&` 串联卡住。

| 手段 | 覆盖 | 依赖 |
|---|---|---|
| `mvn -B -DskipTests package` | 7 个模块编译打包 | JDK 17+；GitHub Actions 每次 push 自动跑 |
| `python scripts/verify_api.py` | 业务闭环 + 越权 + 状态机 + 并发抢单 + 收藏 + 分类缓存 + 商品上下架 | 后端 + MySQL + Redis |
| `python scripts/cache_verify.py` | 缓存读命中 / 写路径失效 / 失效后读到新值 / TTL 兜底 | 同上（需先跑 `verify_api.py` 建数据） |
| `python scripts/verify_logout.py` | `/logout` 后 Redis 里令牌真删除、旧 token 再访问返 401 | 后端 + Redis |
| `python scripts/bench_goods.py` | 缓存开 / 关的 QPS 与延迟对比 | 后端 |
| `cd ruoyi-app && npm run build` | H5 编译 | Node 16+ |

断言都是**回查 MySQL / Redis**，不是只看接口 `code=200`。覆盖的关键点：

- 验证码（读 Redis）→ 登录 → JWT 鉴权 → `/logout`（token 真的失效）
- 商品发布（服务端忽略请求体伪造的 `auditStatus` / `publisherId`）→ 管理员审核 → 上架
- 越权防护：买家改 / 删他人商品 403；买家替卖家确认接单 403；非交易方取消订单被拒
- 订单状态机：合法流转落日志、非法流转被拦截、终态不可再改、通用接口拒绝携带 `orderStatus`
- **抢单是真并发**：`GRAB_THREADS`（默认 8）个线程卡在 `threading.Barrier` 上同一瞬间下单，
  断言"成功 1 单、库里 1 条订单"。旧脚本是串行 `for` 循环点 5 次，那只能证明
  "第二次因为状态已变而失败"，证明不了竞态下的条件更新只放行一条 —— 本次已改写
- 每个流转入口都在同一事务里"改订单 + 同步商品 + 写日志"（事务注解必须挂在入口方法上，
  只标内部 `changeStatus` 会被类内部调用绕过代理）
- 收藏闭环：切换 / 我的收藏 / 管理端按商品筛选 / **删除商品时级联清理收藏**
- **下架不回退审核状态**；未过审商品不能直接上架；重复审核被服务端状态守卫拦截
- 商品详情缓存写入 / TTL / 主动失效 / 失效后下一次读到新值
- 接口文档 / Druid 监控台开关：关闭后 `/v3/api-docs`、`/swagger-ui`、`/druid` 均返回 401，
  业务接口不受影响
- 数据库脚本按 [`sql/README.md`](sql/README.md) 的顺序从零执行；`v1.4.0` 幂等，可重复执行

> 断言条数不写死在这里：用例只增不删，写死的数字一定会过期。
> 跑一遍看最后一行「通过 N 项，失败 0 项」即可。

> 最近一次全量复跑（2026-10-27，本机 MySQL 5.7 + Redis）：
> 六个建库脚本零报错、共 38 张表；`verify_api.py` **55 项通过 / 0 失败**、
> `cache_verify.py` **14 项通过 / 0 失败**、`verify_logout.py` 三项确认；
> 已删除的二代接口带 token 访问返回 `No static resource`，匿名访问统一 401。

### 缓存压测（`scripts/bench_goods.py`，64 并发 × 10 秒，2026-09-23 本机实测）

| 指标 | 缓存关 | 缓存开 | 变化 |
|---|---|---|---|
| QPS | 2628 | **19044** | **×7.2** |
| 平均延迟 | 22.29 ms | 3.12 ms | ↓ 86% |
| P95 | 32.86 ms | 7.22 ms | ↓ 78% |

复现：加 `--biz.cache.enabled=false` 启动即为"缓存关"。
差距主要来自数据库连接池（Druid `maxActive=20`）—— 缓存命中后请求根本不碰数据库。

---

## 七、已知技术债（后续计划）

> 已了结的两项：
>
> 1. **两代业务模型并存**。`book/course/cart/orders/user` 一套（无管理端页面、
>    无前端调用，但接口全部可路由，其中 `/user/login` 是明文口令比对、`/user/update` 无归属校验）
>    已在 v1.4.0 连同代码与表一起删除，详见 [`sql/README.md`](sql/README.md) 第三节。
> 2. **jjwt 0.9.1 + Jackson 2/3 双栈**。Spring Boot 4 把 Jackson 换到 3.x（groupId 变成
>    `tools.jackson`），但 `jjwt 0.9.1`（2017 年的单一 jar）会传递依赖
>    `com.fasterxml.jackson.core:jackson-databind:2.x`，导致 classpath 里同时躺着两套 databind。
>    已升级到 `jjwt 0.12.6` 并把序列化适配器从 `jjwt-jackson` 换成 `jjwt-gson` ——
>    后者只依赖 gson，不碰 Jackson，Jackson 2 的 `databind` / `core` 因此彻底消失
>    （只留 `jackson-annotations 2.21`，那是 Jackson 3 自己的传递依赖）。
>    同时 `TokenService` 从 `signWith(SignatureAlgorithm, String)` + `parseClaimsJws`
>    改成 `signWith(SecretKey, SIG.HS512)` + `parser().verifyWith(...).parseSignedClaims(...)`。
>    验证方式：`mvn -o -pl ruoyi-common dependency:tree | grep jackson` 应当只剩 `tools.jackson`。

1. **前端技术栈偏旧**：Vue 2.6 已于 2023 年底停止维护，中期建议升级 Vue 3 + Element Plus + Vite。
   顺带能解决两个前端各写一份 `request` / token / 登录态的问题（`ruoyi-ui` 用 Cookie、`ruoyi-app` 用 localStorage）。
2. **状态标签没有字典化**：商品状态、成色、订单状态的文案与配色在两个前端各抄了一遍
   （合起来十几处），SQL 里也没有对应的 `sys_dict_type`。改一次要动十几个文件，
   建议升级前端时统一改成字典驱动。
3. **没有测试层**：验证靠打真实服务的 Python 脚本，必须本机有 MySQL + Redis 才跑得起来；
   没有 JUnit 单测，CI 只做编译打包（`mapper XML` 写错、状态机改坏这类问题拦不住）。
   要补测试的话优先级：状态机纯逻辑 → 越权路径 → 缓存失效时序。
4. **迁移仍是手工 SQL**：`v1.x.y_*.sql` 命名像迁移，实际靠人按顺序执行，
   没有 flyway / liquibase，也没有 down 脚本，`v1.1.0` 还是空缺号。
5. **钱包功能只有表与 Mapper**：`Wallet` / `WalletRecord` 没有 Service 与页面，
   要做的话重点是余额扣减的并发安全（`SELECT ... FOR UPDATE` 或乐观锁 + 流水表）。
6. **JWT 默认密钥在仓库里**：`application.yml` 里 `JWT_SECRET` 的兜底值是仓库中的明文，
   生产不替换等于用公开密钥签名。另外 HS512 要求密钥至少 64 字节，`TokenService`
   启动时会校验长度并 fail-fast —— 自定义 `JWT_SECRET` 时别塞个短口令进去，
   生成方式见 [`DEPLOY.md`](DEPLOY.md) 的密钥生成一节。
7. **FastJSON2 并非冗余**：它被 `FastJson2JsonRedisSerializer` 用于 Redis 值序列化，
   移除需要同时把 Redis 序列化切到 Jackson，收益不大，暂不动。
8. **Druid 监控台**：开发默认开启，生产请通过
   `spring.datasource.druid.statViewServlet.enabled=false` 关闭，或至少设置强口令
   （默认 `admin` / 空密码）。完整上线清单见 [`DEPLOY.md`](DEPLOY.md) 第七节。
9. **图片依赖接口前缀**：上传文件存的是 `/profile/...` 相对路径，前端渲染时必须拼
   `VUE_APP_BASE_API`（C 端已统一到 `ruoyi-app/src/utils/image.js`），
   nginx 也要转发 `/prod-api/profile/`，否则图片 404。

---

## 八、接口文档

- 在线文档：启动后访问 `/swagger-ui.html`，右上角分组可切
  「闲置交易（业务）」/「系统管理（框架）」/「测试模块」（`application.yml` 的 `springdoc.group-configs`）。
  业务 Controller 没有写 `@Operation` 注解，在线文档给出的是接口清单
  （路径、方法、参数与返回模型），字段级含义看下面这份 markdown。
- 商品 / 订单 / 收藏接口详细说明：[`docs/商品管理接口文档.md`](docs/商品管理接口文档.md)
- 业务流程（角色、商品生命周期、订单流转、异常分支）：[`docs/业务流程.md`](docs/业务流程.md)
- 部署与上线检查清单：[`DEPLOY.md`](DEPLOY.md)
- 数据库脚本顺序与表结构依据：[`sql/README.md`](sql/README.md)
- 验证与压测脚本：[`scripts/README.md`](scripts/README.md)

## 九、致谢

框架来自 [RuoYi-Vue](http://ruoyi.vip)，遵循 MIT 协议。
