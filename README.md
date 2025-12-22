# 校园墙系统

一个基于 Spring Boot + Vue 的校园综合服务平台，包含论坛、二手交易、拼车、社团招新等功能。

## 技术栈

### 后端
- Spring Boot 4.0
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- Redis
- Knife4j (API文档)

### 前端
- Vue 3
- (待开发)

## 功能模块

### 已实现
- ✅ 用户注册登录
- ✅ JWT 认证授权
- ✅ 三角色权限管理（普通用户、社团管理员、超级管理员）
- ✅ API 文档集成

### 待开发
- 📝 论坛模块
- 📚 二手书本交易
- 🚗 拼车信息发布
- 🎭 社团招新
- 💬 失物招领
- 🎉 活动发布
- 💼 兼职信息
- 🏠 租房信息

## 角色权限说明

### 1. 普通用户 (USER)
- 发布/编辑/删除自己的内容
- 浏览所有公开内容
- 评论、点赞、收藏
- 申请加入社团
- 个人信息管理

### 2. 社团管理员 (CLUB_ADMIN)
- 拥有普通用户的所有权限
- 管理自己社团的信息
- 审核社团成员申请
- 发布社团活动

### 3. 超级管理员 (SUPER_ADMIN)
- 用户管理（封禁、解封）
- 内容审核
- 社团管理
- 系统配置

## 快速开始

### 1. 环境要求
- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 2. 数据库配置

创建数据库：
```sql
CREATE DATABASE campus_wall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

修改 `application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_wall?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 你的密码
```

### 3. Redis 配置

确保 Redis 服务已启动，默认配置：
- host: localhost
- port: 6379
- password: (无密码)

### 4. 启动项目

```bash
# 安装依赖
mvn clean install

# 启动项目
mvn spring-boot:run
```

### 5. 访问 API 文档

启动成功后访问：http://localhost:8080/api/doc.html

## API 接口

### 认证接口

#### 用户注册
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456",
  "nickname": "测试用户",
  "phone": "13800138000",
  "email": "test@example.com"
}
```

#### 用户登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}
```

返回：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "testuser",
      "nickname": "测试用户",
      "role": "USER"
    }
  }
}
```

### 用户接口

所有用户接口需要在请求头中携带 Token：
```
Authorization: Bearer {token}
```

#### 获取当前用户信息
```
GET /api/user/current
```

#### 更新用户信息
```
PUT /api/user/{id}
Content-Type: application/json

{
  "nickname": "新昵称",
  "phone": "13900139000",
  "email": "newemail@example.com"
}
```

## 项目结构

```
src/main/java/com/example/biyesheji/
├── common/              # 公共类
│   └── Result.java      # 统一响应结果
├── config/              # 配置类
│   ├── SecurityConfig.java
│   └── Knife4jConfig.java
├── controller/          # 控制器
│   ├── AuthController.java
│   └── UserController.java
├── dto/                 # 数据传输对象
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RegisterRequest.java
│   └── UserDTO.java
├── entity/              # 实体类
│   └── User.java
├── enums/               # 枚举类
│   └── UserRole.java
├── exception/           # 异常处理
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── repository/          # 数据访问层
│   └── UserRepository.java
├── security/            # 安全相关
│   ├── JwtAuthenticationFilter.java
│   ├── JwtAccessDeniedHandler.java
│   └── JwtAuthenticationEntryPoint.java
├── service/             # 业务逻辑层
│   └── UserService.java
└── utils/               # 工具类
    └── JwtUtil.java
```

## 测试账号

系统预置了三个测试账号（密码都是 `123456`）：

| 用户名 | 角色 | 说明 |
|--------|------|------|
| admin | SUPER_ADMIN | 超级管理员 |
| club_admin | CLUB_ADMIN | 社团管理员 |
| user001 | USER | 普通用户 |

## 开发计划

- [ ] 完善用户模块（头像上传、密码修改等）
- [ ] 开发论坛模块
- [ ] 开发二手交易模块
- [ ] 开发拼车模块
- [ ] 开发社团招新模块
- [ ] 集成文件上传（OSS）
- [ ] 添加消息通知功能
- [ ] 前端 Vue 项目开发

## 注意事项

1. JWT Secret 密钥在生产环境中应该使用更复杂的字符串
2. 数据库密码不要提交到版本控制系统
3. Redis 在生产环境建议设置密码
4. 建议使用 HTTPS 协议部署

## 联系方式

如有问题，请联系开发者。
