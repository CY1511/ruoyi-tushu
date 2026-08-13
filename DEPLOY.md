# 若依书铺 - 部署测试指南

## 环境要求

| 组件 | 最低版本 | 建议版本 | 状态 |
|------|----------|----------|------|
| JDK | 17 | 21 LTS | ✅ 已安装 (21.0.11) |
| Maven | 3.6+ | 3.9.x | ❌ 未安装 |
| MySQL | 8.0+ | 8.0 | ❌ 未安装 |
| Redis | 6.0+ | 7.x | ❌ 未安装 |
| Node.js | 16+ | 20 LTS | ✅ 已安装 (22.16.0) |

## 第一步：安装缺失组件

### 1.1 安装 Maven

```bash
# Windows (使用 scoop)
scoop install maven

# 或手动下载
# 下载地址: https://maven.apache.org/download.cgi
# 设置环境变量 MVN_HOME 和 PATH
```

### 1.2 安装 MySQL

```bash
# Windows (使用 scoop)
scoop install mysql

# 或从官网下载: https://dev.mysql.com/downloads/mysql/
# 安装后初始化数据库:
mysql -u root -p
CREATE DATABASE IF NOT EXISTS ry_vue CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 1.3 安装 Redis

```bash
# Windows (使用 scoop)
scoop install redis

# 或从官网下载: https://github.com/microsoftarchive/redis/releases
# 安装后启动服务:
redis-server
```

## 第二步：数据库初始化

```bash
# 1. 登录 MySQL
mysql -u root -p

# 2. 选择数据库
USE ry_vue;

# 3. 导入若依基础表结构（如有）
# SOURCE /path/to/sql/ry_base.sql;

# 4. 导入业务表结构
SOURCE D:/books/RuoYi-Vue-master/ruoyi-admin/src/main/resources/schema.sql;

# 5. 导入补充表结构
SOURCE D:/books/RuoYi-Vue-master/ruoyi-admin/src/main/resources/sql/app_tables.sql;

# 6. 补齐审计字段
SOURCE D:/books/RuoYi-Vue-master/ruoyi-admin/src/main/resources/sql/fix_columns.sql;

# 7. 退出 MySQL
EXIT;
```

## 第三步：修改配置文件

### 3.1 数据库配置

编辑 `ruoyi-admin/src/main/resources/application-druid.yml`：

```yaml
master:
    url: jdbc:mysql://localhost:3306/ry_vue?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: 你的数据库密码  # 修改这里
```

### 3.2 Redis 配置（如有密码）

编辑 `ruoyi-admin/src/main/resources/application.yml`：

```yaml
spring:
  data:
    redis:
      password: 你的Redis密码  # 如有密码则填写
```

### 3.3 JWT 密钥

使用环境变量或修改配置：

```bash
# Windows PowerShell
$env:JWT_SECRET="你的高强度随机密钥"

# 或编辑 application.yml
token:
  secret: 你的高强度随机密钥
```

## 第四步：启动后端服务

### 4.1 使用 Maven 启动

```bash
cd ruoyi-admin
mvn spring-boot:run
```

### 4.2 或打包后运行

```bash
cd ruoyi-admin
mvn clean package -DskipTests
java -jar target/ruoyi-admin.jar
```

### 4.3 验证后端启动

```bash
# 访问 Swagger 文档
curl http://localhost:8081/swagger-ui.html

# 测试 API
curl http://localhost:8081/app/category/tree
```

## 第五步：启动前端服务

### 5.1 Web 管理端

```bash
cd ruoyi-ui
npm install
npm run dev
# 访问 http://localhost:80
```

### 5.2 移动端（可选）

```bash
cd ruoyi-app
npm install
npm run dev:h5
# 访问 http://localhost:8080
```

## 第六步：功能测试

### 6.1 管理端功能测试

| 功能模块 | 测试路径 | 预期结果 |
|----------|----------|----------|
| 教材管理 | `/system/goods` | 显示教材列表 |
| 订单管理 | `/system/order` | 显示订单列表 |
| 分类管理 | `/system/category` | 显示分类树 |
| 用户管理 | `/system/user` | 显示用户列表 |
| 收藏管理 | `/system/favorite` | 显示收藏列表 |

### 6.2 移动端功能测试

| 功能模块 | 测试路径 | 预期结果 |
|----------|----------|----------|
| 首页 | `/home` | 显示教材推荐 |
| 分类浏览 | `/category` | 显示分类列表 |
| 发布商品 | `/publish` | 显示发布表单 |
| 订单列表 | `/order` | 显示我的订单 |
| 个人中心 | `/profile` | 显示用户信息 |

### 6.3 API 测试

```bash
# 测试分类接口
curl http://localhost:8081/app/category/tree

# 测试教材列表接口
curl http://localhost:8081/app/goods/list?pageNum=1&pageSize=10

# 测试教材详情接口
curl http://localhost:8081/app/goods/1
```

## 常见问题排查

### 7.1 数据库连接失败

```bash
# 检查 MySQL 服务是否启动
netstat -an | grep 3306

# 检查数据库是否存在
mysql -u root -p -e "SHOW DATABASES;"

# 检查用户权限
mysql -u root -p -e "SELECT User, Host FROM mysql.user;"
```

### 7.2 Redis 连接失败

```bash
# 检查 Redis 服务是否启动
netstat -an | grep 6379

# 测试 Redis 连接
redis-cli ping
```

### 7.3 端口被占用

```bash
# 查看端口占用
netstat -ano | findstr :8081
netstat -ano | findstr :80

# 修改端口（application.yml）
server:
  port: 8082
```

### 7.4 跨域问题

检查 `application.yml` 中的 CORS 配置：

```yaml
referer:
  allowed-domains: localhost,127.0.0.1
```

## 部署到生产环境

### 8.1 后端打包部署

```bash
# 打包
mvn clean package -DskipTests

# 生成可执行 jar
java -jar ruoyi-admin/target/ruoyi-admin.jar

# 或使用 nohup 后台运行
nohup java -jar ruoyi-admin/target/ruoyi-admin.jar > app.log 2>&1 &
```

### 8.2 前端打包部署

```bash
cd ruoyi-ui
npm run build:prod

# 将 dist 目录部署到 Nginx
# 配置示例见 nginx.conf
```

### 8.3 Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态资源
    location / {
        root /path/to/ruoyi-ui/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 代理
    location /prod-api/ {
        proxy_pass http://localhost:8081/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # Swagger 文档
    location /swagger-ui.html {
        proxy_pass http://localhost:8081/swagger-ui.html;
    }
}
```

## 测试清单

- [ ] 数据库连接正常
- [ ] Redis 连接正常
- [ ] 后端服务启动成功
- [ ] 前端服务启动成功
- [ ] 教材列表接口正常
- [ ] 教材详情接口正常
- [ ] 分类接口正常
- [ ] 用户登录接口正常
- [ ] 管理端功能测试通过
- [ ] 移动端功能测试通过
- [ ] 文件上传功能正常
- [ ] 订单创建流程正常
- [ ] 收藏功能正常
- [ ] 搜索功能正常

## 联系支持

如有问题，请查看日志：
- 后端日志：`ruoyi-admin/logs/app.log`
- 前端日志：浏览器控制台
