# Nacos 接入开发文档

> 适用范围：后端开发接入 Nacos 配置中心与服务注册；Codex 后续负责服务器配置发布、容器部署和联调验收。
> 当前项目：Java 18 + Spring Boot 3.3.6 + Spring Cloud 2023.0.4 + Docker Compose 单机部署。
> 当前 Nacos：服务器 `192.168.0.254` 已部署 `nacos/nacos-server:v2.4.3`，但业务服务尚未真正接入。

## 1. 背景与目标

当前 Nacos 控制台里没有配置和服务实例，原因不是 Nacos 安装失败，而是后端服务还没有接入 Nacos：

- 项目依赖中未统一引入 Spring Cloud Alibaba Nacos Config / Discovery。
- 多数服务只使用本地 `application.yml` 和 Docker Compose 环境变量。
- 网关路由当前通过 `MALL_ROUTE_*_URI` 指向 Docker 容器服务名。
- Nacos 服务列表为空，说明服务没有注册到 Nacos。
- Nacos 配置列表为空，说明配置没有发布到 Nacos，也没有服务读取 Nacos 配置。

本次目标分两阶段完成：

1. 先接入 Nacos 配置中心，让服务能从 Nacos 读取统一配置。
2. 再接入 Nacos 服务注册与发现，让网关通过服务名转发。

## 2. 接入原则

- 分阶段接入，先配置中心，后服务注册，避免一次性改动导致所有服务无法启动。
- 第一阶段保留 Docker Compose 环境变量兜底，确认稳定后再逐步减少重复配置。
- 密码、密钥、Token 不写入代码仓库；真实值由服务器 `.env` 或 Nacos 测试环境配置承载。
- Nacos 当前测试阶段可以对外开放，但一旦放入数据库密码、Redis 密码等敏感配置，必须开启 Nacos 鉴权或限制访问来源。
- 网关路由在第一阶段继续使用固定容器服务名，第二阶段再切换为 `lb://服务名`。

## 3. 推荐实施阶段

### 阶段一：配置中心接入

后端服务启动时从 Nacos 读取配置，Nacos 中能看到各服务配置文件。
此阶段不改变服务调用方式，网关仍然转发到 `http://mall-auth:18080` 这类固定地址。

验收结果：

- Nacos 配置列表有 `mall-common.yaml` 和各服务配置。
- 服务启动日志能看到 Nacos Config 加载记录。
- 后台、移动端、接口访问不受影响。
- Docker Compose 环境变量仍可作为兜底。

### 阶段二：服务注册与发现

所有后端服务和网关注册到 Nacos，网关路由切换为 `lb://mall-auth`、`lb://mall-system` 等服务名。

验收结果：

- Nacos 服务列表能看到全部服务实例。
- 网关通过 Nacos 服务发现转发请求。
- 停止某个服务后，Nacos 对应实例会下线。
- 恢复服务后，Nacos 对应实例会重新上线。

## 4. Nacos 命名空间与配置规划

当前测试环境建议：

| 项目 | 值 |
|------|----|
| Namespace | `mall-test` |
| Group | `MALL_GROUP` |
| 配置格式 | `yaml` |
| Nacos 内网地址 | `mall-nacos:8848` |
| Nacos 测试访问地址 | `http://192.168.0.254:8848/nacos` |

后续生产环境建议单独创建：

| 项目 | 值 |
|------|----|
| Namespace | `mall-prod` |
| Group | `MALL_GROUP` |
| 配置格式 | `yaml` |

## 5. Data ID 设计

公共配置：

| Data ID | 用途 |
|---------|------|
| `mall-common.yaml` | 公共中间件、MyBatis Plus、日志、通用开关 |

服务配置：

| Data ID | 对应服务 | 端口 |
|---------|----------|------|
| `mall-gateway.yaml` | `mall-gateway` | `18000` |
| `mall-auth.yaml` | `mall-auth` | `18080` |
| `mall-system.yaml` | `mall-system` | `18081` |
| `mall-operation.yaml` | `mall-operation` | `18082` |
| `mall-product.yaml` | `mall-product` | `18083` |
| `mall-sale.yaml` | `mall-sale` | `18084` |
| `mall-trade.yaml` | `mall-trade` | `18085` |
| `mall-payment.yaml` | `mall-payment` | `18086` |
| `mall-aftersale.yaml` | `mall-aftersale` | `18087` |
| `mall-supplier.yaml` | `mall-supplier` | `18088` |
| `mall-finance.yaml` | `mall-finance` | `18089` |
| `mall-wms.yaml` | `mall-wms` | `18090` |
| `mall-station.yaml` | `mall-station` | `18091` |
| `mall-user.yaml` | `mall-user` | `18092` |

## 6. 后端开发需要调整的内容

### 6.1 根 POM

在根 `pom.xml` 中增加 Spring Cloud Alibaba 版本属性和 BOM。
实施前需要后端开发根据当前 Spring Boot / Spring Cloud 版本确认兼容版本，不能随意使用 `latest`。

当前项目版本：

| 组件 | 当前版本 |
|------|----------|
| Spring Boot | `3.3.6` |
| Spring Cloud | `2023.0.4` |
| Java | `18` |
| Nacos Server | `2.4.3` |

建议优先选用 Spring Cloud Alibaba `2023.0.x` 系列，并以 Maven 编译和启动验证为准。

### 6.2 各后端服务依赖

可运行后端服务需要接入：

- `spring-cloud-starter-alibaba-nacos-config`
- `spring-cloud-starter-alibaba-nacos-discovery`

网关额外需要确认：

- `spring-cloud-starter-loadbalancer`
- Spring Cloud Gateway 能识别 `lb://` 路由。

不建议把 Nacos 依赖放进 `mall-common` 后强行让所有模块继承，因为 `mall-common` 是公共代码模块，不是启动配置模块。建议在每个可运行服务模块显式声明，或者由一个专门的启动父依赖模块统一管理。

### 6.3 启动配置方式

Spring Boot 3.x 推荐通过 `spring.config.import` 接入 Nacos 配置。
当前项目已有部分 `bootstrap.yml`，后端开发需要统一策略，避免同一个服务同时用两套配置入口造成启动顺序混乱。

建议统一采用：

```yaml
spring:
  application:
    name: mall-auth
  config:
    import:
      - optional:nacos:mall-common.yaml?group=MALL_GROUP
      - optional:nacos:mall-auth.yaml?group=MALL_GROUP
  cloud:
    nacos:
      server-addr: ${NACOS_SERVER_ADDR:127.0.0.1:8848}
      username: ${NACOS_USERNAME:nacos}
      password: ${NACOS_PASSWORD:nacos}
      config:
        namespace: ${NACOS_NAMESPACE:}
        group: ${NACOS_GROUP:MALL_GROUP}
        file-extension: yaml
      discovery:
        namespace: ${NACOS_NAMESPACE:}
        group: ${NACOS_GROUP:MALL_GROUP}
```

每个服务必须把 `spring.application.name` 改成自己的服务名，例如：

- `mall-auth`
- `mall-system`
- `mall-operation`
- `mall-product`
- `mall-sale`
- `mall-trade`
- `mall-payment`
- `mall-aftersale`
- `mall-supplier`
- `mall-finance`
- `mall-wms`
- `mall-station`
- `mall-user`
- `mall-gateway`

### 6.4 本地配置保留策略

`application.yml` 保留最小启动配置：

- `spring.application.name`
- `spring.config.import`
- `spring.cloud.nacos.*`
- 本地开发兜底配置

以下内容迁移到 Nacos：

- 数据库连接
- Redis 连接
- RocketMQ NameServer
- Elasticsearch 地址
- MinIO 地址和账号
- XXL-Job Admin 地址和执行器配置
- MyBatis Plus 公共配置
- 网关路由配置

### 6.5 网关路由调整

阶段一保持现有固定地址：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: mall-auth
          uri: http://mall-auth:18080
          predicates:
            - Path=/api/auth/**
```

阶段二切换为 Nacos 服务发现：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: mall-auth
          uri: lb://mall-auth
          predicates:
            - Path=/api/auth/**
```

所有路由按现有路径保持不变：

| 路径 | 服务 |
|------|------|
| `/api/auth/**` | `mall-auth` |
| `/api/system/**` | `mall-system` |
| `/api/operation/**` | `mall-operation` |
| `/api/product/**` | `mall-product` |
| `/api/sale/**` | `mall-sale` |
| `/api/trade/**` | `mall-trade` |
| `/api/payment/**` | `mall-payment` |
| `/api/aftersale/**` | `mall-aftersale` |
| `/api/supplier/**` | `mall-supplier` |
| `/api/finance/**` | `mall-finance` |
| `/api/wms/**` | `mall-wms` |
| `/api/station/**` | `mall-station` |
| `/api/user/**` | `mall-user` |

## 7. Nacos 配置内容建议

### 7.1 `mall-common.yaml`

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://mall-mysql:3306/mall?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: ${MYSQL_APP_USER:mall}
    password: ${MYSQL_APP_PASSWORD:}
  data:
    redis:
      host: mall-redis
      port: 6379
      password: ${REDIS_PASSWORD:}
      database: 0

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      logic-delete-field: is_deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

rocketmq:
  name-server: mall-rocketmq-namesrv:9876

mall:
  minio:
    endpoint: http://mall-minio:9000
    access-key: ${MINIO_ROOT_USER:}
    secret-key: ${MINIO_ROOT_PASSWORD:}
  elasticsearch:
    uris: http://mall-elasticsearch:9200
  xxl-job:
    admin-addresses: http://mall-xxl-job:8080/xxl-job-admin
```

说明：上面是真实部署结构的配置示例，密码使用环境变量占位，不建议把真实密码提交进仓库。

### 7.2 单服务配置示例

`mall-auth.yaml`：

```yaml
server:
  port: 18080

spring:
  application:
    name: mall-auth
```

`mall-gateway.yaml` 阶段一：

```yaml
server:
  port: 18000

spring:
  application:
    name: mall-gateway
  cloud:
    gateway:
      routes:
        - id: mall-auth
          uri: http://mall-auth:18080
          predicates:
            - Path=/api/auth/**
```

`mall-gateway.yaml` 阶段二：

```yaml
server:
  port: 18000

spring:
  application:
    name: mall-gateway
  cloud:
    gateway:
      routes:
        - id: mall-auth
          uri: lb://mall-auth
          predicates:
            - Path=/api/auth/**
```

实际文件需要补齐全部 13 个业务服务路由。

## 8. 后端开发交付清单

后端开发调整完后，需要交付以下内容：

1. 根 `pom.xml` 已引入 Spring Cloud Alibaba BOM，版本与 Spring Boot 3.3.6 / Spring Cloud 2023.0.4 兼容。
2. 所有可运行服务模块已引入 Nacos Config / Discovery。
3. 网关已具备 `lb://` 路由能力。
4. 所有服务都有正确的 `spring.application.name`。
5. 所有服务能读取 `mall-common.yaml` 和自身服务配置。
6. 本地 `application.yml` 中没有写死生产密码。
7. 本地运行时，Nacos 不可用也能通过 `optional:nacos:` 和本地兜底配置启动到可诊断状态。
8. 执行 `mvn compile` 通过。
9. 执行 `mvn test` 通过，或明确列出失败用例与原因。
10. 提供变更说明：改了哪些模块、哪些配置迁移到 Nacos、是否需要部署脚本同步调整。

## 9. 后端开发完成后 Codex 需要继续做的事

后端代码调整完成后，Codex 需要接手以下部署和验收工作：

### 9.1 代码检查

- 检查 POM 依赖版本是否兼容。
- 检查是否存在真实密码提交到仓库。
- 检查 `application.yml` / `bootstrap.yml` 是否有重复、冲突配置。
- 检查各服务 `spring.application.name` 是否与 Docker 容器名、Nacos 服务名一致。
- 检查网关路由路径是否仍与前端 `/api/**` 规划一致。

### 9.2 服务器配置

- 在服务器 Nacos 创建 `mall-test` 命名空间。
- 发布 `mall-common.yaml` 和所有服务配置。
- 必要时开启 Nacos 鉴权，并更新 Compose 环境变量。
- 调整 `/opt/mall-deploy/source/deploy/docker-compose.yml`，给后端容器注入：
  - `NACOS_SERVER_ADDR=mall-nacos:8848`
  - `NACOS_NAMESPACE=<mall-test namespace id>`
  - `NACOS_GROUP=MALL_GROUP`
  - `NACOS_USERNAME=<nacos username>`
  - `NACOS_PASSWORD=<nacos password>`
- 继续保留数据库、Redis 等环境变量作为阶段一兜底。

### 9.3 容器构建与启动

- 在服务器重新构建后端镜像。
- 按顺序启动：
  1. MySQL
  2. Redis
  3. Nacos
  4. RocketMQ
  5. Elasticsearch
  6. MinIO
  7. XXL-Job
  8. 后端服务
  9. Gateway
  10. Nginx
- 检查所有容器是否 `running` 或 `healthy`。

### 9.4 Nacos 验收

- 配置列表能看到全部 Data ID。
- 服务列表能看到全部后端服务和网关。
- 每个服务实例 IP、端口、分组、命名空间正确。
- 服务重启后能重新注册。
- 停止服务后 Nacos 能下线实例。

### 9.5 接口和前端验收

- 验证 `http://192.168.0.254/api/auth/**` 能到 `mall-auth`。
- 验证 `http://192.168.0.254/api/system/**` 能到 `mall-system`。
- 验证其余业务服务路径不被网关吞掉。
- 验证以下页面仍能打开：
  - `http://192.168.0.254/admin/`
  - `http://192.168.0.254/wms/`
  - `http://192.168.0.254/user/`
  - `http://192.168.0.254/workbench/`
  - `http://192.168.0.254/warehouse/`
- 验证 Cloudflare 临时公网地址仍可访问后台和移动端页面。

### 9.6 文档同步

- 更新 `docs/部署访问地址与账号.md`。
- 如开启 Nacos 鉴权，补充 Nacos 登录账号、访问方式和注意事项。
- 补充 Nacos 配置发布和回滚步骤。

## 10. 风险与注意事项

| 风险 | 说明 | 处理方式 |
|------|------|----------|
| 版本不兼容 | Spring Boot 3.3.6、Spring Cloud 2023.0.4、Spring Cloud Alibaba 版本必须匹配 | 后端开发先做 Maven 编译和单服务启动验证 |
| Nacos 裸奔 | 当前测试阶段 Nacos 端口对外开放，放入密码后风险较高 | 接入真实敏感配置前开启鉴权或限制访问 |
| 启动顺序问题 | Nacos 未启动时，服务可能无法读取配置 | 使用 `optional:nacos:`，Compose 增加 Nacos 依赖和健康检查 |
| 配置覆盖混乱 | 本地配置、环境变量、Nacos 配置同时存在可能互相覆盖 | 阶段一保留兜底，阶段二整理最终配置来源 |
| 网关切换风险 | 从固定地址切到 `lb://` 后依赖服务发现 | 先让服务注册稳定，再切网关 |
| 密码泄露 | Nacos 配置、Git 仓库、日志都有泄露可能 | 文档和代码只放占位符，真实值放服务器环境 |

## 11. 建议验收顺序

1. 后端本地 `mvn compile`。
2. 后端本地 `mvn test`。
3. 单服务连接 Nacos 配置中心启动。
4. 全服务连接 Nacos 配置中心启动。
5. Nacos 服务注册验证。
6. Gateway 固定地址路由验证。
7. Gateway `lb://` 路由验证。
8. Nginx `/api/**` 代理验证。
9. 五个前端入口验证。
10. 公网临时地址验证。

## 12. 结论

后端开发先完成代码侧接入，重点是依赖、启动配置、服务名、网关 `lb://` 能力。
代码调整完成后，Codex 继续负责服务器上的 Nacos 命名空间、配置发布、Compose 调整、容器重建、服务注册检查、接口验收和访问文档更新。
