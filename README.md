<p align="center">
  <img alt="logo" src="https://oscimg.oschina.net/oscnet/up-d3d0a9303e11d522a06cd263f3079027715.png">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">若依书铺</h1>
<h4 align="center">基于 RuoYi-Vue 的校园教材闲置交易平台</h4>
<p align="center">
  <a href="https://github.com/CY1511/rouyi-tushu"><img src="https://img.shields.io/badge/RuoYi--Vue-v3.9.2-brightgreen.svg"></a>
  <a href="./LICENSE"><img src="https://img.shields.io/badge/license-MIT-blue.svg"></a>
  <a href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html"><img src="https://img.shields.io/badge/JDK-17+-orange.svg"></a>
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring_Boot-4.0.6-brightgreen.svg"></a>
</p>

## 项目简介

若依书铺是一个面向高校学生的教材闲置交易与二手物品交易平台，基于 [若依（RuoYi-Vue）](https://gitee.com/y_project/RuoYi-Vue) 快速开发框架二次开发。支持教材买卖、购物车、订单管理、支付钱包、收藏评价、闲置物品发布等核心功能，并配有移动端小程序（ruoyi-app）。

- 前端：Vue 2 + Element UI（Web 管理后台） / Vue 2 + uni-app（移动端）
- 后端：Spring Boot 4.x + Spring Security + JWT + Redis
- 数据库：MySQL 8.0+

## 功能模块

### Web 管理端（ruoyi-ui）
1. 教材管理 — 上架/下架、成色评估、价格建议、库存管理
2. 订单管理 — 订单列表、发货、退款审核、物流追踪
3. 分类管理 — 教材分类、课程绑定、分类排序
4. 用户管理 — 用户列表、角色权限、账号状态
5. 收藏夹管理 — 用户收藏记录
6. 评价管理 — 订单评价审核
7. 消息中心 — 系统消息、交易通知
8. 若依系统内置功能（用户、角色、菜单、字典、操作日志等）

### 移动端（ruoyi-app）
1. 教材浏览与搜索 — 按分类、价格、院校筛选
2. 购物车与下单 — 加入购物车、提交订单
3. 订单管理 — 待付款、待发货、待收货、已完成
4. 钱包功能 — 余额充值、收支明细、提现
5. 个人信息 — 个人资料、收货地址、我的收藏
6. 评价与收藏 — 评价已购教材、收藏感兴趣的商品

### 核心特色功能
- **价格评估**：基于课程关联、市场比价给出建议售价区间
- **课程绑定教材**：支持按院校专业课程查找配套教材
- **成色分级**：全新 / 九成 / 八成 / 七成四级成色标准
- **交易安全**：订单状态机管理 + 钱包担保交易

## 技术栈

| 层级 | 技术选型 |
|:---|:---|
| 后端框架 | Spring Boot 4.0.6 |
| 权限认证 | Spring Security + JWT |
| 数据库 | MySQL 8.0+ |
| 缓存 | Redis |
| 连接池 | Druid |
| ORM | MyBatis |
| 分页插件 | PageHelper |
| API 文档 | Springdoc OpenAPI (Swagger UI) |
| Web 前端 | Vue 2 + Element UI + Vuex + Vue Router |
| 移动端 | uni-app + Vuex + uView UI |
| 代码生成 | Velocity 模板引擎 |

## 项目结构

```
RuoYi-Vue-master/
├── ruoyi-admin/          # 后端主模块（Spring Boot 应用）
│   └── src/main/java/com/ruoyi/
│       ├── system/       # 业务模块（Book, Order, Cart, Wallet 等）
│       ├── web/          # Web 控制器层
│       └── RuoYiApplication.java
├── ruoyi-common/         # 公共工具类
├── ruoyi-framework/      # 框架核心（安全、配置、拦截器）
├── ruoyi-system/         # 系统模块（若依原生功能）
├── ruoyi-generator/      # 代码生成器
├── ruoyi-quartz/         # 定时任务
├── ruoyi-ui/             # Web 管理前端（Vue 2 + Element UI）
├── ruoyi-app/            # 移动端（uni-app）
├── sql/                  # 数据库初始化脚本
├── ruoyi-admin/src/main/resources/
│   ├── application.yml   # 主配置文件
│   ├── application-druid.yml  # Druid 数据源配置
│   ├── schema.sql        # 业务表结构
│   └── sql/              # 补充 SQL 脚本
└── pom.xml               # Maven 多模块根配置
```

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Node.js 16+（前端构建）

## 快速开始

### 1. 初始化数据库

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS ry_vue CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 导入若依基础表结构
mysql -u root -p ry_vue < sql/ry_20260417.sql

# 导入业务表结构
mysql -u root -p ry_vue < ruoyi-admin/src/main/resources/schema.sql

# 导入补充数据
mysql -u root -p ry_vue < ruoyi-admin/src/main/resources/sql/app_tables.sql
```

### 2. 修改配置

编辑 `ruoyi-admin/src/main/resources/application-druid.yml`，修改数据库连接信息：

```yaml
master:
    url: jdbc:mysql://localhost:3306/ry_vue?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
    username: root
    password: 你的数据库密码
```

编辑 `ruoyi-admin/src/main/resources/application.yml`，修改 Redis 密码（如有）和 JWT 密钥：

```yaml
spring:
  data:
    redis:
      password: 你的Redis密码

token:
  secret: ${JWT_SECRET:请替换为高强度随机密钥}
```

### 3. 启动后端

```bash
cd ruoyi-admin
mvn spring-boot:run
# 或打包后运行：mvn package && java -jar target/ruoyi-admin.jar
# 服务默认端口：8081
# Swagger 文档：http://localhost:8081/swagger-ui.html
```

### 4. 启动 Web 管理端

```bash
cd ruoyi-ui
npm install
npm run dev
# 访问地址：http://localhost:80
```

### 5. 启动移动端（可选）

```bash
cd ruoyi-app
npm install
npm run dev:h5
# 或 HBuilderX 运行到小程序/APP
```

## 默认账号

| 角色 | 用户名 | 密码 |
|:---|:---|:---|
| 管理员 | admin | admin123 |

> 首次部署后请立即修改默认密码。

## 许可证

MIT License — 见 [LICENSE](./LICENSE) 文件
