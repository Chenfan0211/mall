# 商城项目 — 完整目录结构

> 技术栈：Java 18 · Spring Boot · Spring Cloud · Vue3 · TypeScript · 微信小程序 · MySQL · Redis · RocketMQ · Nacos · Elasticsearch · Docker

---

## 整体结构（Monorepo）

```
mall-platform/
├── services/                # 后端微服务（Spring Boot）
├── web/                     # 前端（Vue3 + Vite + TS）
├── miniapp/                 # 微信小程序（4个端）
├── contracts/               # 接口契约（OpenAPI / Proto）
├── shared/                  # 公共代码（DTO / 枚举 / 工具类）
├── infra/                   # 基础设施（Docker / K8s / Helm）
├── docs/                    # 文档（架构 / ADR / Runbook）
├── scripts/                 # 脚本（部署 / 数据初始化 / 工具）
└── pom.xml                  # 根 POM（Maven 聚合）
```

---

## 一、后端微服务 `services/`

每个服务内部采用标准 Spring Boot 分层结构。

```
services/
├── gateway/                    ← Spring Cloud Gateway（统一入口）
│   ├── src/main/java/com/mall/gateway/
│   │   ├── filter/             # 鉴权过滤器、限流过滤器
│   │   ├── config/             # 路由配置、CORS 配置
│   │   └── GatewayApplication.java
│   ├── pom.xml
│   └── Dockerfile
│
├── auth-service/               ← 认证服务（JWT + 微信登录 + 多角色）
│   ├── src/main/java/com/mall/auth/
│   │   ├── domain/             # 用户实体、角色实体
│   │   ├── application/        # 登录用例、Token 刷新
│   │   ├── interface/          # REST Controller
│   │   ├── infrastructure/     # JWT 工具、微信 SDK、Redis Session
│   │   └── AuthApplication.java
│   ├── pom.xml
│   └── Dockerfile
│
├── user-service/               ← 用户服务
│   ├── src/main/java/com/mall/user/
│   │   ├── domain/             # User, Address, City, PickupPoint
│   │   ├── application/        # 切换城市、切换自提点、申请自提点
│   │   ├── interface/
│   │   ├── infrastructure/     # MyBatis Mapper、Redis 缓存
│   │   └── UserApplication.java
│   ├── pom.xml
│   └── Dockerfile
│
├── product-service/            ← 商品中心
│   ├── src/main/java/com/mall/product/
│   │   ├── domain/             # Product, Sku, Spec, Category, CityPrice
│   │   ├── application/        # 商品建档、规格绑定、价格维护
│   │   ├── interface/
│   │   ├── infrastructure/     # MySQL + ES 同步（RocketMQ 消费）
│   │   └── ProductApplication.java
│   ├── pom.xml
│   └── Dockerfile
│
├── order-service/              ← 订单中心
│   ├── src/main/java/com/mall/order/
│   │   ├── domain/             # Order, OrderItem, OrderStatus
│   │   ├── application/        # 创建订单、取消订单、状态流转
│   │   ├── interface/
│   │   ├── infrastructure/     # MySQL、Redis 锁（防重复下单）
│   │   └── OrderApplication.java
│   ├── pom.xml
│   └── Dockerfile
│
├── payment-service/            ← 支付服务（微信支付）
│   ├── src/main/java/com/mall/payment/
│   │   ├── domain/             # Payment, Refund
│   │   ├── application/        # 发起支付、支付回调、退款
│   │   ├── interface/
│   │   ├── infrastructure/     # 微信支付 SDK、支付流水
│   │   └── PaymentApplication.java
│   ├── pom.xml
│   └── Dockerfile
│
├── promotion-service/           ← 营销中心
│   ├── src/main/java/com/mall/promotion/
│   │   ├── domain/             # Coupon, Activity, RecommendRule
│   │   ├── application/        # 发券、用券、活动页配置
│   │   ├── interface/
│   │   ├── infrastructure/
│   │   └── PromotionApplication.java
│   ├── pom.xml
│   └── Dockerfile
│
├── aftersale-service/           ← 售后中心
│   ├── src/main/java/com/mall/aftersale/
│   │   ├── domain/             # Aftersale, AftersaleStatus
│   │   ├── application/        # 申请售后、审核、退货、退款
│   │   ├── interface/
│   │   ├── infrastructure/
│   │   └── AftersaleApplication.java
│   ├── pom.xml
│   └── Dockerfile
│
├── commission-service/          ← 分账中心
│   ├── src/main/java/com/mall/commission/
│   │   ├── domain/             # Commission, Withdraw, Account
│   │   ├── application/        # 分账计算、提现申请、财务审核
│   │   ├── interface/
│   │   ├── infrastructure/
│   │   └── CommissionApplication.java
│   ├── pom.xml
│   └── Dockerfile
│
├── base-service/                ← 基础资料服务
│   ├── src/main/java/com/mall/base/
│   │   ├── domain/             # Supplier, PickupPoint, City, Warehouse, Buyer
│   │   ├── application/        # 创建资料、生成账号
│   │   ├── interface/
│   │   ├── infrastructure/
│   │   └── BaseApplication.java
│   ├── pom.xml
│   └── Dockerfile
│
├── report-service/              ← 报表中心
│   ├── src/main/java/com/mall/report/
│   │   ├── domain/             # Report, ExportTask
│   │   ├── application/        # 订单报表、分佣报表、库存报表
│   │   ├── interface/
│   │   ├── infrastructure/     # 大查询（只读从库）、Excel 导出
│   │   └── ReportApplication.java
│   ├── pom.xml
│   └── Dockerfile
│
├── notify-service/              ← 消息通知服务
│   ├── src/main/java/com/mall/notify/
│   │   ├── domain/             # MessageTemplate, NotifyLog
│   │   ├── application/        # 公众号推送、订阅消息、短信
│   │   ├── interface/
│   │   ├── infrastructure/     # 微信公众号 SDK
│   │   └── NotifyApplication.java
│   ├── pom.xml
│   └── Dockerfile
│
├── wms-service/                 ← WMS 核心服务
│   ├── src/main/java/com/mall/wms/
│   │   ├── domain/             # InboundOrder, OutboundOrder, Inventory, StockLocation
│   │   ├── application/        # 采购入库、波次下放、拣货、复检、盘点、补货
│   │   ├── interface/
│   │   ├── infrastructure/     # MySQL、Redis（库存缓存）、RocketMQ
│   │   └── WmsApplication.java
│   ├── pom.xml
│   └── Dockerfile
│
├── search-service/              ← 搜索服务（Elasticsearch）
│   ├── src/main/java/com/mall/search/
│   │   ├── domain/             # SearchIndex, SuggestWord
│   │   ├── application/        # 商品搜索、联想词、ES 同步
│   │   ├── interface/
│   │   ├── infrastructure/     # Elasticsearch RestHighLevelClient
│   │   └── SearchApplication.java
│   ├── pom.xml
│   └── Dockerfile
│
└── job-service/                 ← 定时任务服务（XXL-Job Executor）
    ├── src/main/java/com/mall/job/
    │   ├── handler/            # 未支付取消、库存检测、缓存刷新
    │   ├── config/
    │   └── JobApplication.java
    ├── pom.xml
    └── Dockerfile
```

---

## 二、前端 `web/`

```
web/
├── admin/                       ← 运营后台（Vue3 + Vite + TS + Element Plus）
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   │   ├── product/        # 商品中心、类目中心
│   │   │   ├── base/           # 基础资料（供应商、自提点、城市、仓库）
│   │   │   ├── publish/        # 商品发布
│   │   │   ├── order/          # 订单中心
│   │   │   ├── aftersale/      # 售后中心
│   │   │   ├── commission/     # 分账中心
│   │   │   ├── promotion/      # 营销中心
│   │   │   ├── report/         # 报表中心
│   │   │   └── auth/           # 权限中心
│   │   ├── api/                # 接口调用（按服务拆分）
│   │   ├── components/         # 公共组件
│   │   ├── store/              # Pinia 状态管理
│   │   ├── router/             # 路由配置
│   │   ├── utils/              # 工具函数
│   │   └── types/              # TypeScript 类型定义
│   ├── public/
│   ├── vite.config.ts
│   └── package.json
│
└── wms/                         ← 仓库后台 / H5（Vue3 + Vite + TS）
    ├── src/
    │   ├── views/
    │   │   ├── inbound/         # 采购订单、入库单、收货
    │   │   ├── outbound/       # 销售单、波次下放、拣货、复检、装车
    │   │   ├── inventory/      # 实时库存、库存流水、移库、报损
    │   │   ├──盘点/             # 盘点计划、全盘/差异/移动盘点
    │   │   ├── replenishment/  # 补货管理
    │   │   ├── fruit/           # 水果仓专区
    │   │   └── system/         # 仓库管理、库区、权限
    │   ├── api/
    │   ├── components/
    │   ├── store/
    │   ├── router/
    │   └── types/
    ├── public/
    ├── vite.config.ts
    └── package.json
```

---

## 三、微信小程序 `miniapp/`

```
miniapp/
├── user/                        ← 用户小程序（uni-app 或原生）
│   ├── pages/
│   │   ├── index/              # 首页（商品列表、轮播图、弹券）
│   │   ├── category/           # 展示类目
│   │   ├── search/             # 商品搜索（联想词）
│   │   ├── product/            # 商品详情
│   │   ├── cart/               # 购物车
│   │   ├── order/              # 订单（确认、支付、列表、详情）
│   │   ├── aftersale/          # 售后申请
│   │   ├── coupon/             # 优惠券
│   │   ├── address/            # 收货地址
│   │   └── mine/               # 个人中心（城市/自提点切换、申请自提点）
│   ├── components/             # 公共组件
│   ├── utils/                  # 请求封装、微信 SDK
│   ├── store/                  # Pinia / Vuex
│   ├── app.json
│   └── project.config.json
│
├── pickup-point/                ← 自提点小程序
│   ├── pages/
│   │   ├── index/              # 工作台首页
│   │   ├── order-summary/      # 订单汇总
│   │   ├── delivery-summary/   # 配送汇总
│   │   ├── commission/         # 佣金明细、余额
│   │   ├── withdraw/           # 提现申请、记录
│   │   ├── shortage/           # 缺货标记
│   │   ├── vacation/           # 休假申请
│   │   └── aftersale/          # 收退货、售后处理
│   ├── components/
│   ├── utils/
│   ├── app.json
│   └── project.config.json
│
├── supplier/                    ← 供应商小程序
│   ├── pages/
│   │   ├── index/              # 工作台
│   │   ├── sales/              # 销售数据
│   │   ├── aftersale/          # 售后单
│   │   ├── product/            # 商品信息
│   │   ├── commission/         # 佣金、余额、提现
│   │   ├── invoice/            # 开发票
│   │   └── delivery-note/      # 送货单
│   ├── components/
│   ├── utils/
│   ├── app.json
│   └── project.config.json
│
└── warehouse/                   ← 仓库小程序（WMS 移动端）
    ├── pages/
    │   ├── index/              # 工作台（按角色展示功能入口）
    │   ├── inbound/            # 采购订单、收货任务
    │   ├── outbound/           # 拣货任务、复检任务
    │   ├── loading/            # 装车任务
    │   ├── delivery/           # 司机配送、团点签到
    │   ├── inventory/          # 库存查询、移库、报损
    │   ├── stocktaking/        # 盘点任务
    │   └── replenishment/     # 补货任务
    ├── components/
    ├── utils/
    ├── app.json
    └── project.config.json
```

---

## 四、接口契约 `contracts/`

```
contracts/
├── openapi/                     # OpenAPI 3.0 接口定义（按服务拆分）
│   ├── gateway.yaml
│   ├── auth.yaml
│   ├── user.yaml
│   ├── product.yaml
│   ├── order.yaml
│   ├── payment.yaml
│   ├── promotion.yaml
│   ├── aftersale.yaml
│   ├── commission.yaml
│   ├── base.yaml
│   ├── report.yaml
│   ├── notify.yaml
│   ├── wms.yaml
│   └── search.yaml
└── proto/                       # gRPC Proto 定义（高性能内部调用）
    ├── order.proto
    ├── payment.proto
    └── notify.proto
```

---

## 五、公共代码 `shared/`

```
shared/
├── common/                       # Java 公共模块（被所有服务依赖）
│   ├── src/main/java/com/mall/common/
│   │   ├── domain/             # 公共实体（BaseEntity、IdGenerator）
│   │   ├── dto/                # 公共 DTO（PageRequest、ApiResponse）
│   │   ├── enums/              # 公共枚举（OrderStatus、RoleType）
│   │   ├── exception/          # 统一异常处理
│   │   ├── util/               # 工具类（DateUtil、SnowflakeId）
│   │   └── config/             # 公共配置（MyBatis、Redis、RocketMQ）
│   └── pom.xml
│
├── sdk/                          # 前端/小程序 TypeScript SDK（从 OpenAPI 生成）
│   ├── user-sdk/
│   ├── order-sdk/
│   ├── product-sdk/
│   └── wms-sdk/
│
└── types/                        # 共享 TypeScript 类型定义
    ├── api.ts                    # 接口响应类型
    ├── enums.ts                  # 枚举类型
    └── models.ts                 # 业务模型类型
```

---

## 六、基础设施 `infra/`

```
infra/
├── docker-compose.yml            # 本地开发：启动全部中间件 + 所有服务
├── docker-compose-dev.yml       # 开发环境（仅中间件）
├── docker-compose-prod.yml      # 生产环境
│
├── mysql/
│   ├── init/                    # 初始化 SQL（建库、建表、初始数据）
│   └── conf/                    # MySQL 配置文件
│
├── redis/
│   └── conf/
│
├── rocketmq/
│   └── conf/
│
├── nacos/
│   ├── config/                  # Nacos 配置文件（按服务、按环境）
│   │   ├── dev/
│   │   ├── staging/
│   │   └── prod/
│   └── init.d/
│
├── elasticsearch/
│   ├── mappings/                # ES 索引映射
│   └── conf/
│
├── nginx/
│   ├── conf.d/
│   │   ├── admin.conf           # 运营后台反向代理
│   │   ├── wms.conf             # 仓库后台反向代理
│   │   └── api.conf             # API 网关反向代理
│   └── nginx.conf
│
├── k8s/                          # Kubernetes 部署文件
│   ├── base/
│   ├── dev/
│   ├── staging/
│   └── prod/
│
└── helm/                         # Helm Charts（K8s 包管理）
    ├── gateway/
    ├── auth-service/
    ├── order-service/
    └── ...
```

---

## 七、文档 `docs/`

```
docs/
├── architecture.md               # 系统架构总览
├── runbook.md                    # 运维手册（部署、回滚、故障排查）
├── erd.md                        # 数据库 ER 图
├── flow/                         # 业务流程图（Mermaid）
│   ├── order-flow.md
│   ├── wms-inbound-flow.md
│   ├── wms-outbound-flow.md
│   ├── aftersale-flow.md
│   └── commission-flow.md
├── adr/                          # 架构决策记录（Architecture Decision Record）
│   ├── 0001-why-spring-cloud.md
│   ├── 0002-why-mysql-plus-es.md
│   ├── 0003-service-split-strategy.md
│   └── 0004-payment-integration.md
└── api-migration.md              # 接口变更记录
```

---

## 八、脚本 `scripts/`

```
scripts/
├── dev/                          # 本地开发脚本
│   ├── start-all.sh              # 启动全部服务（docker-compose）
│   ├── start-service.sh          # 启动单个服务
│   └── init-db.sh                # 初始化数据库
│
├── deploy/                       # 部署脚本
│   ├── build.sh                 # Maven 打包
│   ├── docker-build.sh          # 构建 Docker 镜像
│   └── deploy-prod.sh           # 生产部署
│
├── data/                         # 数据脚本
│   ├── seed/                    # 初始数据（城市、类目、管理员账号）
│   └── migrate/                 # 数据库迁移脚本
│
└── tools/                        # 工具脚本
    ├── generate-openapi.sh      # 从代码生成 OpenAPI 文档
    └── sync-es.sh               # 手动触发 ES 同步
```

---

## 关键设计说明

### 服务拆分原则
- **按业务域拆分**，不按技术层拆分
- `order-service` 包含订单完整生命周期，不把支付拆出去（支付有独立 `payment-service`）
- WMS 作为独立域，`wms-service` 内部再按功能分层

### 数据权限隔离
```
平台管理员  → 全部数据
城市角色    → 当前城市及下级
供应商      → 自己商品/订单/售后/佣金
自提点      → 自己订单/配送/佣金
仓库角色    → 当前仓库及授权库区
司机        → 自己配送任务
```
在 `base-service` 中统一管理，各服务通过 `data_scope` 字段过滤。

### 消息驱动（RocketMQ）
```
商品变更    → ES 同步（search-service 消费）
支付成功    → 生成佣金预估（commission-service 消费）
订单完成    → 分账（commission-service 消费）
分账完成    → 公众号通知（notify-service 消费）
库存变更    → 缓存刷新（各服务消费）
```
