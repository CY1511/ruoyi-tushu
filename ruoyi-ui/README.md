# 若依书铺 - 管理端

校园教材闲置交易平台的**管理端**（运营后台），Vue 2 + Element UI，独立于学生端 H5（`../ruoyi-app`）构建与运行。

## 技术栈

| 项 | 版本 |
|---|---|
| Vue | 2.6.12 |
| Element UI | 2.15.14 |
| 构建 | vue-cli 4.4 + webpack 4 |
| 状态管理 / 路由 | Vuex 3 + Vue Router 3 |

## 功能

| 模块 | 说明 |
|---|---|
| 商品管理 | 列表、新增、编辑、删除、导出、**审核（通过 / 驳回）**、上架、下架 |
| 订单管理 | 列表、导出、**状态流转**（只允许合法流转）、**流转记录时间线** |
| 分类管理 | 商品分类增删改查 |
| 收藏管理 | 收藏记录查询、删除、导出 |
| 系统管理 | 用户、角色、菜单、部门、岗位、字典、参数、通知（若依框架自带） |
| 系统监控 | 在线用户、定时任务、数据监控、服务监控、缓存监控、登录日志、操作日志 |

## 开发

```bash
cd ruoyi-ui
npm install --registry=https://registry.npmmirror.com
npm run dev
```

浏览器访问 <http://localhost:80>。

> **Node 17 及以上必须加环境变量**，否则 webpack 4 会因为 OpenSSL 3 报
> `error:0308010C:digital envelope routines::unsupported`：
>
> ```bash
> # Windows PowerShell
> $env:NODE_OPTIONS="--openssl-legacy-provider"; npm run dev
> # bash / Git Bash
> export NODE_OPTIONS=--openssl-legacy-provider && npm run dev
> ```

## 接口代理

`vue.config.js` 里把 `/dev-api` 代理到后端 `http://localhost:8081`（读 `VUE_APP_BASE_API`）。
后端换端口时必须同步改这里，否则登录会一直转圈。另外 `/v3/api-docs` 也做了代理，指向 springdoc。

## 构建

```bash
npm run build:stage   # 测试环境，读 .env.staging，接口前缀 /stage-api
npm run build:prod    # 生产环境，读 .env.production，接口前缀 /prod-api
```

产物在 `dist/`，由 Nginx 托管，反向代理规则见仓库根目录 `DEPLOY.md`。

## 默认账号

`admin / admin123`（数据库初始化脚本自带，生产环境务必改掉）。

## 相关文档

- 项目总说明：[`../README.md`](../README.md)
- 接口字段说明：[`../docs/商品管理接口文档.md`](../docs/商品管理接口文档.md)
- 业务流程：[`../docs/业务流程.md`](../docs/业务流程.md)
- 部署与上线：[`../DEPLOY.md`](../DEPLOY.md)
