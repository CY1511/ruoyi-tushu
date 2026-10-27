# 若依书铺 · 部署指南

本地开发怎么跑起来看 [`README.md`](README.md)（建库顺序、两个前端的端口、三个环境坑都写在那里）。
本文只管两件事：**从零部署一套可用环境**，以及**上生产要改掉什么**。

---

## 一、环境要求

| 组件 | 最低版本 | 备注 |
|---|---|---|
| JDK | 17 | 21 LTS 实测可用 |
| Maven | 3.6+ | 多模块构建，建议在仓库根目录执行 |
| MySQL | 5.7 / 8.0 | 字符集必须是 `utf8mb4` |
| Redis | 3.2+ | 业务缓存 + 验证码 + 登录令牌 |
| Node.js | 16+ | `ruoyi-ui` 是 webpack4，`ruoyi-app` 是 webpack5 |

> Node 17+ 跑 `ruoyi-ui`（webpack4）必须带 `NODE_OPTIONS=--openssl-legacy-provider`，
> 否则报 `ERR_OSSL_EVP_UNSUPPORTED`。`ruoyi-app` 不需要。

---

## 二、建库

库名默认 `ry_tushu`，必须按顺序执行（完整说明见 [`sql/README.md`](sql/README.md)）：

```bash
mysql -uroot -p -e "CREATE DATABASE ry_tushu DEFAULT CHARACTER SET utf8mb4;"

for f in ry_20260417.sql quartz.sql v1.0.0_business_tables.sql \
         v1.2.0_order_status_log.sql v1.3.0_business_menu.sql v1.4.0_drop_legacy_and_index.sql; do
  mysql -uroot -p --default-character-set=utf8mb4 ry_tushu < "sql/$f"
done
```

老库（跑过 v1.0.0 的）只需要补执行 `sql/v1.4.0_drop_legacy_and_index.sql`：
它会删掉早期 `book/course/cart/orders/user` 那套表的残留数据、清理孤儿收藏、补商品列表复合索引，幂等可重复执行。

---

## 三、配置：一律用环境变量，不要改 yml

所有可调项在 `application.yml` / `application-druid.yml` 里都是 `${ENV:默认值}` 形式，
把口令写进配置文件再提交是事故源头（历史上 DEPLOY 文档教过这种写法，已纠正）。

| 环境变量 | 默认值 | 生产必须改 |
|---|---|---|
| `DB_HOST` / `DB_PORT` / `DB_NAME` | `localhost` / `3306` / `ry_tushu` | 按需 |
| `DB_USERNAME` / `DB_PASSWORD` | `root` / 空 | **是** |
| `JWT_SECRET` | 内置开发默认值 | **是**（不改的话任何人都能用公开默认值伪造令牌；且 HS512 要求 **至少 64 字节**，短了应用直接启动失败） |
| `SWAGGER_ENABLED` | `true` | **改成 `false`** |
| `DRUID_USERNAME` / `DRUID_PASSWORD` | `admin` / 空 | **是**（关掉监控台也要设，见下） |
| `LOG_LEVEL_APP` | `debug`（会打印全部 SQL） | **改成 `info`** |

生成一个高强度密钥（**至少 64 字节**，`base64 64` 出来是 88 个字符，够用）：

```bash
openssl rand -base64 64
```

---

## 四、启动后端

```bash
# 仓库根目录构建（不要在 ruoyi-admin 子目录里 package，兄弟模块找不到）
mvn -B -DskipTests clean package

# 运行
DB_HOST=127.0.0.1 DB_USERNAME=app_rw DB_PASSWORD='***' \
JWT_SECRET='***' SWAGGER_ENABLED=false LOG_LEVEL_APP=info \
nohup java -jar ruoyi-admin/target/ruoyi-admin.jar > app.log 2>&1 &

tail -f app.log        # 看到 Started RuoYiApplication 即成功
```

Windows 下同理，用 `set`/`$env:` 设置变量，或直接改 `ry.bat` / `ry.sh` 里的启动参数
（`ry.sh` 已是 LF 换行，Linux 下 `sh ry.sh` 可正常执行）。

验证：

```bash
curl http://localhost:8081/app/category/tree     # 返回 JSON 即通
```

---

## 五、两个前端

| 工程 | 说明 | 开发端口 | 生产前缀 |
|---|---|---|---|
| `ruoyi-ui` | 管理端（Vue2 + Element UI + webpack4） | 80 | `/prod-api` |
| `ruoyi-app` | 学生端 H5（Vue2 + webpack5） | 84 | `/prod-api` |

```bash
# 管理端
cd ruoyi-ui && npm ci && export NODE_OPTIONS=--openssl-legacy-provider && npm run dev

# C 端 H5（脚本名是 dev / build，不存在 dev:h5）
cd ruoyi-app && npm ci && npm run dev
```

生产打包（两边的 `VUE_APP_BASE_API` 已由 `.env.production` 定为 `/prod-api`，不用改代码）：

```bash
cd ruoyi-ui  && npm run build:prod   # 产物 ruoyi-ui/dist
cd ruoyi-app && npm run build        # 产物 ruoyi-app/dist，已关闭 source map
```

> 两个前端的 dev 代理目标写死 `http://localhost:8081`。后端换端口时必须同步改
> `ruoyi-ui/vue.config.js` 和 `ruoyi-app/vue.config.js`，否则页面能打开但接口全 404。

---

## 六、Nginx（两个前端 + 后端一起挂）

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 管理端
    location / {
        root /opt/ruoyi/ruoyi-ui/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # C 端 H5（路径前缀自定，这里用 /app-h5/）
    location /app-h5/ {
        alias /opt/ruoyi/ruoyi-app/dist/;
        index index.html;
        try_files $uri $uri/ /app-h5/index.html;
    }

    # 后端 API：两个前端的 /prod-api 都打到同一个后端
    location /prod-api/ {
        proxy_pass http://127.0.0.1:8081/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 上传的图片（后端 /profile/** 映射到 ruoyi.profile 目录）
    location /prod-api/profile/ {
        alias /home/ruoyi/uploadPath/;
    }
}
```

上传目录由 `application.yml` 的 `ruoyi.profile` 决定（默认 `./uploadPath`），
生产建议改成绝对路径，并保证运行用户有写权限。

---

## 七、上线前检查清单

安全项，缺一条都算带病上线：

- [ ] 已修改 `admin` 默认密码（`admin/admin123` 是公开信息）
- [ ] 已设置 `JWT_SECRET`（不能用仓库里的默认值）
- [ ] `SWAGGER_ENABLED=false`：关掉后 `/v3/api-docs`、`/swagger-ui` 返回 401
- [ ] Druid 监控台关闭：`spring.datasource.druid.statViewServlet.enabled=false`
      或至少设强口令（默认 `admin` / 空密码）
- [ ] `LOG_LEVEL_APP=info`，不往日志里灌全量 SQL
- [ ] 数据库账号按最小权限授予（不要给 root 远程），`DB_PASSWORD` 只存在于环境变量
- [ ] `sql/v1.4.0_drop_legacy_and_index.sql` 已执行（早期 `user` 表带明文口令，别留在生产库）
- [ ] 上传目录不可执行、且不在 web 根目录下裸奔

功能回归（后端 + Redis + MySQL 就绪后跑，说明见 [`scripts/README.md`](scripts/README.md)）：

```bash
python scripts/verify_api.py       # 退出码 0 才算过
python scripts/cache_verify.py
python scripts/verify_logout.py
```

---

## 八、常见问题

| 现象 | 原因与处理 |
|---|---|
| 前端页面正常但接口全 404 | 后端端口不是 8081，或代理目标没同步改 |
| `ERR_OSSL_EVP_UNSUPPORTED` | Node 17+ 跑 webpack4，加 `NODE_OPTIONS=--openssl-legacy-provider` |
| 打包成功但跑起来还是旧代码 | Windows 会锁 jar：先杀 `java` 进程再 `mvn package` |
| 删掉的 Mapper XML 仍在启动时报 `Could not resolve type alias` | 只 `package` 不清 `target`：旧 XML 留在 `target/classes`，且 `ruoyi-admin` 的 fat jar 可能还嵌着旧版 `ruoyi-system`。用 `mvn clean package` |
| 后端起了随机端口 | 环境里注入了 `SERVER__PORT`/`SERVER__HOST`，清掉并显式 `--server.port=8081` |
| 中文数据存成 `???` | 建库或连接缺 `utf8mb4`，导入时带 `--default-character-set=utf8mb4` |
| `Table 'idle_goods' doesn't exist` | 建库脚本没按顺序跑全，见 [`sql/README.md`](sql/README.md) |
