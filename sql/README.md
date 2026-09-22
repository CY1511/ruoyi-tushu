# 数据库脚本说明

> 若依书铺（校园教材闲置交易平台）· 基于 RuoYi-Vue v3.9.2
> 本目录是**唯一的 SQL 来源**，各模块 `resources/` 下不再保留脚本副本。

## 一、脚本清单与执行顺序

全新部署时**必须按下面顺序**执行，缺一步都会导致启动后报 `Table 'xxx' doesn't exist`。

| 顺序 | 脚本 | 内容 | 是否必需 |
|---|---|---|---|
| ① | `ry_20260417.sql` | 若依框架表（`sys_*`、`gen_*`）+ 系统菜单、字典、角色、admin 账号 | 必需 |
| ② | `quartz.sql` | 定时任务表（`qrtz_*`） | 必需（框架启动即依赖） |
| ③ | `v1.0.0_business_tables.sql` | 业务表全量：`idle_*` 系列 + `book/course/cart/orders` 系列 + `wallet` + `trade`，含索引与分类初始化数据 | 必需 |
| ④ | `v1.2.0_order_status_log.sql` | 订单状态流转日志表 | 必需（状态机依赖） |
| ⑤ | `v1.3.0_business_menu.sql` | 业务菜单与按钮权限 | 必需（否则管理端看不到菜单） |

`v1.0.1_business_index.sql` 是**增量索引脚本**，只给已经跑起来的老库补索引用；
全新部署走 ③ 即可，③ 里索引已经带上了。脚本用存储过程判断表和索引是否已存在，可重复执行。

### 一键执行（MySQL 5.7 / 8.0，Git Bash）

```bash
mysql -uroot -p -e "CREATE DATABASE ry_tushu DEFAULT CHARACTER SET utf8mb4;"

mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/ry_20260417.sql
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/quartz.sql
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/v1.0.0_business_tables.sql
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/v1.2.0_order_status_log.sql
mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < sql/v1.3.0_business_menu.sql
```

Windows 命令提示符把 `\` 换成 `^`，或直接用 `source` 逐个执行。

执行完记得把库名同步到 `ruoyi-admin/src/main/resources/application-druid.yml` 的 `master.url`。

## 二、表结构的"定稿"依据

`v1.0.0_business_tables.sql` 里的字段**不是猜的**：每个字段都来自 Mapper XML 的
`resultMap` 与 `insert` 语句，逐列比对过，保证与 Java 代码一致。

> 反面教材：仓库原来有一个 `ruoyi-admin/src/main/resources/schema.sql`，
> 里面 `idle_` 出现次数为 0，而实体、Mapper、Controller 用的全是 `Idle*`。
> 照着它建库，后端一启动查询就报 `Table 'idle_goods' doesn't exist`。该文件已删除，
> 内容合并进 `v1.0.0_business_tables.sql`。

本次顺手修掉的几处结构不一致（照着原脚本建库会真实报错的）：

| 表 | 问题 | 处理 |
|---|---|---|
| `idle_goods` / `idle_order` / `idle_category` / `idle_favorite` | 全仓库没有 CREATE 语句 | 按代码补齐 |
| `trade` | `TradeMapper.xml` 在用，任何脚本里都没有建表语句 | 补齐 |
| `course` | Mapper 插入 `(name, code, department, university, credit)`，原脚本没有 `code`/`department` 列 | 补列 |
| `book` / `order_item` | `condition` 是 MySQL 保留字，Mapper 里裸用会报 1064 语法错误 | Mapper 加反引号 |
| `sys_config` | 原脚本用 `config_id=100` 重复插入 `sys.account.registerUser`，与框架自带配置项冲突 | 改为 `UPDATE` 已有行 |

## 三、两代业务模型并存（技术债，需决策）

仓库里目前有两套互不相干的业务模型，两边的代码都还在跑：

| | 代次 | 表 | 管理端页面 | 说明 |
|---|---|---|---|---|
| ① | **闲置交易（主线）** | `idle_goods`、`idle_order`、`idle_category`、`idle_favorite` | 有（商品/订单/分类/收藏） | 实体是 `Idle*`，接口 `/system/goods`、`/app/goods` |
| ② | 旧版教材交易 | `book`、`course`、`cart`、`orders`、`order_item`、`favorites`、`review`、`message`、`address`、`user` | 无 | 实体是 `Book`/`Cart`/`Orders`，接口 `/system/book` 等 |

本脚本两代都给全，保证克隆后**一定能启动**。
若要真正"定稿一套"，建议删掉第 ② 代（删 `Book*`/`Cart*`/`Orders*` 等 Controller+Service+Mapper+实体），
能显著降低维护成本 —— 但这属于结构性改动，需单独评估后再动。

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
