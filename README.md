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
| 商品 | 发布、编辑、删除、按分类/关键词浏览、详情查看 |
| 商品审核 | 发布后进入待审核，审核通过才在列表露出；编辑后重新进入待审核 |
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
rouyi-tushu
├── ruoyi-admin        # 启动类、web 层（controller）、配置文件
├── ruoyi-framework    # 框架核心：Security / Redis / AOP / 数据源等配置
├── ruoyi-system       # 业务核心：domain / mapper / service（业务代码都在这里）
├── ruoyi-common       # 通用工具、常量、注解、异常
├── ruoyi-quartz       # 定时任务
├── ruoyi-generator    # 代码生成器
├── ruoyi-ui           # 管理端前端（Vue2 + Element UI）
├── ruoyi-app          # 学生端 H5（Vue2，独立工程）
├── sql                # 数据库脚本（唯一来源，见 sql/README.md）
└── doc / bin / ry.sh  # 部署文档与启动脚本
```

> 业务代码的边界是明确划分的：`ruoyi-system` 只放 domain/mapper/service，
> `ruoyi-admin` 只放启动类、配置与 controller，`ruoyi-framework` 不放业务代码。

---

## 四、快速启动

### 0. 一键启动（Windows 本地开发）

本机项目根目录有两个便捷脚本（**含本机数据库口令，已加入 .gitignore，不会进仓库**，换机器需要自己重建）：

| 脚本 | 作用 |
|---|---|
| `start-dev.bat` | 清理残留进程 → 缺 jar 则自动打包 → 起后端 8081 → 缺依赖则自动装 → 起前端 80 |
| `stop-dev.bat` | 按端口精确结束进程（不会误杀其他 java / node） |

脚本内已处理三个环境坑，手动启动时同样需要注意：

1. **随机端口**：沙箱/IDE 注入的 `SERVER__PORT`、`SERVER__HOST` 会把 Spring Boot 顶到随机端口，导致前端代理打不通，必须清掉后用 `--server.port=8081` 指定；
2. **jar 被锁**：只要 `java` 进程还开着，Windows 会锁住 jar，`mvn package` 会报成功但产物根本没换，跑起来还是旧代码 —— 打包前必须先杀进程；
3. **Node 17+ 跑 webpack4**：前端必须带 `NODE_OPTIONS=--openssl-legacy-provider`，否则报 `ERR_OSSL_EVP_UNSUPPORTED`。

### 1. 建库（必须按顺序执行）

```bash
mysql -uroot -p -e "CREATE DATABASE ry_tushu DEFAULT CHARACTER SET utf8mb4;"

mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/ry_20260417.sql            # 框架表 + 系统菜单
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/quartz.sql                 # 定时任务表
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/v1.0.0_business_tables.sql # 业务表 + 初始化数据
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/v1.2.0_order_status_log.sql# 订单流转日志表
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/v1.3.0_business_menu.sql   # 业务菜单与按钮权限
```

脚本清单、表结构定稿依据、两代业务模型的说明都写在 **[`sql/README.md`](sql/README.md)**。

### 2. 配置数据源（不要改代码，用环境变量）

`application-druid.yml` 里连接信息全部走环境变量，默认连本地 `ry_tushu`：

| 环境变量 | 默认值 | 说明 |
|---|---|---|
| `DB_HOST` / `DB_PORT` | `localhost` / `3306` | 数据库地址 |
| `DB_NAME` | `ry_tushu` | 库名 |
| `DB_USERNAME` / `DB_PASSWORD` | `root` / 空 | 账号密码 |
| `JWT_SECRET` | 内置默认值 | **生产必须替换** |
| `SWAGGER_ENABLED` | `true` | 生产建议 `false` |

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

### 4. 启动前端

```bash
cd ruoyi-ui  && npm install && npm run dev     # 管理端，默认 80 端口
cd ruoyi-app && npm install && npm run serve   # 学生端 H5
```

### 5. 默认账号

| 账号 | 密码 | 角色 |
|---|---|---|
| `admin` | `admin123` | 超级管理员（拥有 `*:*:*`，无需分配菜单权限） |
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
- 通用修改接口 `updateIdleOrder` 拒绝携带 `orderStatus`，从入口堵死绕过校验
- 管理端「流转」弹窗的可选状态由后端 `GET /system/order/nextStatus/{orderId}` 下发，
  界面上根本选不出非法状态

### 2. 缓存设计（cache-aside + 主动失效）

| 缓存对象 | key | TTL | 失效时机 |
|---|---|---|---|
| 商品详情 | `idleGoods:{goodsId}` | 10 分钟 | 改/审/上下架/被下单/删除 |
| 启用分类列表 | `idleCategory:all` | 2 小时 | 分类增/删/改 |

- **正确性靠主动失效**：商品一下架缓存立刻删掉，否则会出现"已下架商品还能被看到并下单"
- **TTL 只做兜底**：万一某条写路径漏了删除，最多脏 10 分钟
- 缓存的是**原始行数据**，可见性判断（未过审/已下架只有发布者可见）留在请求级逻辑里，
  不把"某个用户看到的视图"缓存进 Redis

> 实现上没有用 `@Cacheable/@CacheEvict`，原因写在 `IdleGoodsServiceImpl` 类注释里：
> 本机 Redis 3.2（Windows 版）+ Spring Data Redis 4 的组合下，
> `RedisCache.evict()/clear()` 实测是**静默空操作**（代码探针验证：`cache.get` 命中为 true，
> evict 后 key 仍在 Redis，而同进程 `RedisTemplate.delete` 正常）。改用框架自带
> `RedisCache` 手写 cache-aside，读写删三条路径都有用例覆盖，且不挑 Redis 版本。

---

## 六、已验证的内容

以下都是在本地真机跑出来的，不是"理论上应该可以"：

```bash
mvn clean package                     # BUILD SUCCESS，7 个模块
java -jar ruoyi-admin.jar             # Started RuoYiApplication in 11.7s
```

- 数据库脚本从零执行：51 张表，零报错；分类、菜单、字典等初始化数据齐全
- 端到端接口用例 **35 项全通过**，覆盖：
  - 验证码（读 Redis）→ 登录 → JWT 鉴权
  - 商品发布（忽略请求体伪造的 `auditStatus`/`publisherId`）→ 管理员审核 → 上架
  - 商品详情缓存写入 / TTL / 主动失效
  - 越权防护（买家改/删他人商品均返回 403）
  - 订单状态机：合法流转落日志、非法流转被拦截、终态不可再改、
    买家不能替卖家接单、通用接口拒绝改状态
  - 并发下单：同一商品 5 次下单只成功 1 单（条件更新抢单）
- 接口文档 / Druid 监控台开关：关闭后 `/v3/api-docs`、`/swagger-ui`、`/druid` 均返回 401，
  业务接口不受影响

---

## 七、已知技术债（后续计划）

1. **两代业务模型并存**：`idle_*`（教材闲置交易，当前主线，有管理端页面）与
   `book/course/cart/orders`（早期版本，代码仍在、无管理端页面）。
   建议后续删掉第 ② 代代码与表，能显著降低维护成本 —— 属于结构性改动，需单独评估。
2. **前端技术栈偏旧**：Vue 2.6 已于 2023 年底停止维护，中期建议升级 Vue 3 + Element Plus + Vite。
3. **钱包功能只有表与 Mapper**：`Wallet`/`WalletRecord` 没有 Service 与页面，
   要做的话重点是余额扣减的并发安全（`SELECT ... FOR UPDATE` 或乐观锁 + 流水表）。
4. **FastJSON2 并非冗余**：它被 `FastJson2JsonRedisSerializer` 用于 Redis 值序列化，
   移除需要同时把 Redis 序列化切到 Jackson，收益不大，暂不动。
5. **Druid 监控台**：开发默认开启，生产请通过
   `spring.datasource.druid.statViewServlet.enabled=false` 关闭（安全放行会一并收掉）。

---

## 八、接口文档

- 在线文档：启动后访问 `/swagger-ui.html`
- 商品管理接口文档：[`商品管理接口文档.md`](商品管理接口文档.md)
- 部署说明：[`DEPLOY.md`](DEPLOY.md)

## 九、致谢

框架来自 [RuoYi-Vue](http://ruoyi.vip)，遵循 MIT 协议。
