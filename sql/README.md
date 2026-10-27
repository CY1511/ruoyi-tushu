# 数据库脚本说明

> 若依书铺（校园教材闲置交易平台）· 基于 RuoYi-Vue v3.9.2
> 本目录是**唯一的 SQL 来源**，各模块 `resources/` 下不再保留脚本副本。

## 一、脚本清单与执行顺序

全新部署时**必须按下面顺序**执行，缺一步都会导致启动后报 `Table 'xxx' doesn't exist`。

| 顺序 | 脚本 | 内容 | 是否必需 |
|---|---|---|---|
| ① | `ry_20260417.sql` | 若依框架表（`sys_*`、`gen_*`）+ 系统菜单、字典、角色、admin 账号 | 必需 |
| ② | `quartz.sql` | 定时任务表（`qrtz_*`） | 必需（框架启动即依赖） |
| ③ | `v1.0.0_business_tables.sql` | 业务表全量：`idle_*` 系列 + 预留的 `wallet`/`wallet_record`，含索引与分类初始化数据 | 必需 |
| ④ | `v1.2.0_order_status_log.sql` | 订单状态流转日志表 | 必需（状态机依赖） |
| ⑤ | `v1.3.0_business_menu.sql` | 业务菜单与按钮权限 | 必需（否则管理端看不到菜单） |
| ⑥ | `v1.4.0_drop_legacy_and_index.sql` | 删除二代 `book/course/cart/orders/user` 等表、清理孤儿收藏、补商品列表复合索引 | 老库升级必需；新库执行也安全（幂等） |

`v1.0.1_business_index.sql` 是**增量索引脚本**，只给已经跑起来的老库补索引用；
全新部署走 ③ 即可，③ 里索引已经带上了。脚本用存储过程判断表和索引是否已存在，可重复执行。

> v1.0.1 里"idle_goods 的 `audit_status` / `goods_status` 拆开建比复合更实用"这个判断，
> 对 C 端列表并不成立（那条查询两个状态列都是等值条件，还要按 `create_time` 排序）。
> v1.4.0 已补上复合索引 `(audit_status, goods_status, create_time)`，
> 并删掉被它前缀覆盖的冗余单列索引 `idx_audit`。

### 一键执行（MySQL 5.7 / 8.0，Git Bash）

```bash
mysql -uroot -p -e "CREATE DATABASE ry_tushu DEFAULT CHARACTER SET utf8mb4;"

mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/ry_20260417.sql
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/quartz.sql
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/v1.0.0_business_tables.sql
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/v1.2.0_order_status_log.sql
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/v1.3.0_business_menu.sql
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/v1.4.0_drop_legacy_and_index.sql
```

Windows 命令提示符把 `\` 换成 `^`，或直接用 `source` 逐个执行。

执行完记得把库名同步到 `ruoyi-admin/src/main/resources/application-druid.yml` 的 `master.url`。

## 二、表结构的"定稿"依据

`v1.0.0_business_tables.sql` 里 `idle_*` 系列的字段**不是猜的**：每个字段都来自 Mapper XML 的
`resultMap` 与 `insert` 语句，逐列比对过，保证与 Java 代码一致。

> 例外：`wallet` / `wallet_record` 是预留表，只有 `WalletMapper.java` 空接口，
> 没有 XML、Service 和页面，字段是按"余额 + 流水"的常规形态设计的，不代表已实现。

> 反面教材：仓库原来有一个 `ruoyi-admin/src/main/resources/schema.sql`，
> 里面 `idle_` 出现次数为 0，而实体、Mapper、Controller 用的全是 `Idle*`。
> 照着它建库，后端一启动查询就报 `Table 'idle_goods' doesn't exist`。该文件已删除，
> 内容合并进 `v1.0.0_business_tables.sql`。

建库脚本历史上修掉的结构不一致（照着旧脚本建库会真实报错，留在这里当变更记录）：

| 表 | 问题 | 处理 |
|---|---|---|
| `idle_goods` / `idle_order` / `idle_category` / `idle_favorite` | 全仓库没有 CREATE 语句 | 按代码补齐 |
| `sys_config` | 原脚本用 `config_id=100` 重复插入 `sys.account.registerUser`，与框架自带配置项冲突 | 改为 `UPDATE` 已有行 |

## 三、只保留一代业务模型

早期版本还有一套 `book/course/cart/orders/user` 模型：**没有管理端页面、没有前端调用**，
但接口全部可路由，其中 `/user/login` 还带着明文口令比对和
`/user/update` 无归属校验（可改任意用户密码、余额）。属于典型的"死代码 + 活漏洞"。

v1.4.0 已连同代码一起删除：

- 删除的表：`user`、`category`、`book`、`cart`、`orders`、`order_item`、`address`、
  `favorites`、`review`、`message`、`course`、`course_book`、`trade`
- 保留的表：`idle_*`（主线）、`wallet`/`wallet_record`（预留功能，见技术债）

老库执行 `v1.4.0_drop_legacy_and_index.sql` 即可，脚本幂等、可重复执行。

## 四、订单状态的准确取值（以代码为准）

订单状态是 `VARCHAR(2)`，取值如下：

| 值 | 含义 | 由谁触发 |
|---|---|---|
| 0 | 待确认 | 买家下单 |
| 1 | 交易中 | 卖家确认接单 |
| 2 | 已完成 | 买家确认收货（终态） |
| 3 | 已取消 | 买家/卖家取消（终态） |

合法流转：`0→1`、`0→3`、`1→2`、`1→3`。非法流转由 Service 层的状态机拦截并抛 `ServiceException`。

商品状态 `idle_goods.goods_status`：`0` 待审核 → `1` 上架 → `2` 已预订 → `3` 已售出 / `4` 已下架。
商品审核状态 `idle_goods.audit_status`：`0` 待审核 → `1` 通过 / `2` 驳回。
