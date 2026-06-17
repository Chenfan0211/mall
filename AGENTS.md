# AGENTS.md

> 本文件是编码智能体（Codex / Claude Code / Copilot 等）的项目指令文件。
> 修改此文件后无需重启，下次会话自动生效。

---

## 1. 项目身份

社区团购商城全链路系统：商品 → 发布 → 下单 → 采购入库 → 仓储履约 → 配送 → 售后 → 分账提现。

后端 Java 18 + Spring Boot + Spring Cloud，前端 Vue3，数据库 MySQL 8，中间件 Redis / Nacos / RocketMQ / XXL-Job / Elasticsearch。

---

## 2. 常用命令

```bash
mvn clean install -DskipTests          # 全量构建
mvn compile                            # 编译
mvn test                               # 全量测试
mvn test -pl mall-order                # 单模块测试
mvn dependency:tree                    # 依赖树
mvn spotless:apply                     # 格式化
```

每次改动代码后，必须先 `mvn compile` 确认编译通过，再 `mvn test` 确认无回归。

---

## 3. 代码风格

| 规则 | 要求 |
|------|------|
| 缩进 | 4 空格 |
| 编码 | UTF-8 |
| 行宽 | 120 字符 |
| 类名 | PascalCase |
| 方法名/变量名 | camelCase |
| 常量 | UPPER_SNAKE_CASE |
| 包名 | 全小写 |
| 类型声明 | 显式类型，避免 var |
| 返回策略 | 优先 early return，减少 else |
| 注解 | 重写方法必须加 @Override |
| 不可变 | 参数和局部变量尽量 final |
| 空值 | 集合和字符串操作前必须检查 null |

---

## 4. 分层架构

### Controller — 只做入口，不写逻辑

- 参数校验用 @Validated
- 返回值统一 Result<T>
- 权限控制用 @PreAuthorize
- 禁止在 Controller 写业务逻辑

### Service — 业务逻辑唯一归属

- @Transactional 标在类上
- 复杂业务拆成私有方法，保持 public 方法清晰
- 调用外部服务（Dubbo/HTTP）做降级和熔断
- 修改实体后必须检查对应 XML 映射

### DAO/Mapper — MyBatis Plus 为主，XML 辅助

- 继承 BaseMapper<T>，单表 CRUD 用 MyBatis Plus 内置方法（save / saveBatch / updateById / removeByIds 等）
- 批量操作用 MyBatis Plus 内置方法：saveBatch / updateBatchById / removeByIds
- 复杂查询用 QueryWrapper / LambdaQueryWrapper 链式构建
- 多表关联、动态 SQL 等内置方法搞不定的，才写在 XML 中
- XML 放在 resources/mapper/ 下
- resultMap 优先于 resultType
- 分页用 MyBatis Plus 的 Page + IPage
- 动态 SQL 用 <where> + <if>
- 严禁代码拼接 SQL

### Entity / DTO / VO — 严格分离

- Entity：与数据库表一一对应，不对外暴露
- VO：接口入参，接收前端数据
- DTO：接口出参，返回前端数据
- 转换用 MapStruct，Mapper 接口加 @Mapper(componentModel = "spring")

---

## 5. 框架约定

### Dubbo

- 接口定义在 api 模块，实现在 service 模块
- 新接口必须配 dubbo 协议
- 超时统一 provider 端配置
- 序列化用 hessian2
- 注册中心 Nacos

### Redis

- Key 命名：`mall:模块:业务:标识`（如 mall:goods:detail:10086）
- 必须设过期时间
- 缓存穿透用布隆过滤器，击穿用互斥锁，雪崩用随机过期
- 分布式锁用 Redisson

### RocketMQ

- Topic：MALL_模块_业务（如 MALL_ORDER_PAY_SUCCESS）
- 消费者组：mall_模块_group
- 消费必须幂等
- 延迟消息处理超时场景

### XXL-Job

- 任务命名：mall_模块_功能
- 必须幂等，支持重跑
- 关键任务记执行日志

### Elasticsearch

- 商品变更后异步同步 ES（通过 MQ 解耦）
- 搜索走联想词 + 分词

---

## 6. 数据库规范

- 表名/字段名用下划线命名
- 每张表必须有：id、create_time、update_time
- 金额字段用 DECIMAL，禁止 FLOAT/DOUBLE
- 状态字段用 TINYINT + 注释说明含义
- 软删除用 is_deleted TINYINT(1)
- 禁止在代码拼接 SQL，必须参数化
- 数据库变更走迁移脚本，禁止直接改表

### SQL 文件管理

- 所有建表（CREATE TABLE）、修改表结构（ALTER TABLE / ADD COLUMN 等）、初始化数据（INSERT）的 SQL **统一放在一个文件** `Sql/mall.sql` 中
- `mall.sql` 内按模块分段，每段用注释分隔，格式如下：

```sql
-- ============================
-- 模块：订单中心
-- 创建人：xxx
-- 创建日期：2026-06-16
-- ============================

CREATE TABLE mall_order ( ... );

-- ----------------------------
-- 修改：mall_order 新增支付方式字段
-- 日期：2026-06-17
-- 操作人：xxx
-- ----------------------------
ALTER TABLE mall_order ADD COLUMN pay_type TINYINT COMMENT '支付方式 1微信 2支付宝';

-- ----------------------------
-- 初始化：订单状态字典数据
-- 日期：2026-06-17
-- 操作人：xxx
-- ----------------------------
INSERT INTO mall_dict (type, code, name) VALUES ('order_status', 1, '未支付');
```

- 每次新增 SQL 操作时追加到 `mall.sql` 末尾，**不要覆盖已有内容**
- 每段 SQL 前必须加注释：操作类型、涉及表、日期、操作人、用途说明
- 如果 `Sql/` 目录不存在，自动创建

### SQL 操作日志

- `Sql/` 目录下必须维护一个 `Sql/changelog.md` 文件，记录所有 SQL 操作日志
- 每次对 `mall.sql` 新增或修改 SQL 后，必须在 `changelog.md` 中追加一条记录
- 日志格式：

```markdown
| 日期 | 操作类型 | 涉及表 | 操作人 | 用途说明 |
|------|----------|--------|--------|----------|
| 2026-06-16 | 建表 | mall_order | xxx | 创建订单主表 |
| 2026-06-17 | 改表 | mall_order | xxx | 新增支付方式字段 |
| 2026-06-17 | 初始化 | mall_dict | xxx | 订单状态字典数据 |
```

- 禁止在 `mall.sql` 之外执行 DDL 操作

---

## 7. 接口规范

### 统一返回

```java
Result<T>
├── code: int      // 0=成功，非0=错误码
├── message: String // 中文描述
└── data: T        // 业务数据
```

- 成功：Result.success(data)
- 失败：Result.fail(ErrorCode)

### 日期时间

- 数据库：DATETIME
- Java：LocalDateTime
- 序列化：yyyy-MM-dd HH:mm:ss
- 传参：时间戳（毫秒）或 ISO 格式

### 分页

- 入参：pageNum + pageSize
- 出参：total + list

---

## 8. 业务规则

### 订单状态机

```
未支付 ──支付成功──→ 已付款(待发货) ──出库──→ 已发货(待收货) ──确认收货──→ 已完成
  │                                                          ↑
  ├── 15分钟超时/手动取消 → 未支付取消                        │
  └── 已付款未发货取消 → 已支付取消                           │
                                                             │
已发货后禁止取消，只能走售后 ───────────────────────────────────┘
```

- 未支付 15 分钟自动取消（RocketMQ 延迟消息）
- 支付最后 5 分钟公众号模板消息提醒
- 发货后不允许取消，只能售后

### 售后

- 仅退款：审核通过 → 直接退款
- 退货退款：审核通过 → 退货 → 退款
- 支持部分退款
- 退款失败支持重试

### 商品发布

- 配送日期必须大于销售结束时间
- 必须指定采购仓
- 支持自提点白名单
- 发布时可按城市调价
- 发布后自动生成联想词 + 同步 ES

### 库存

- 出库按 FIFO 扣减
- 入库后更新 SKU 在库数量 + 库存流水
- 盘点差异审核后按 FIFO 调整

### 分账

- 订单完成后才分账（非支付后）
- 分账方：自提点佣金、供应商佣金
- 提现需财务审核

---

## 9. 异常处理

- 自定义业务异常继承 RuntimeException，必须包含错误码 + 中文描述
- 全局异常处理器：@ControllerAdvice + @ExceptionHandler
- 禁止空 catch 块（吞异常）
- 禁止在 catch 中只写 e.printStackTrace()
- 日志必须记录完整异常堆栈

---

## 10. 日志规范

- SLF4J + Logback
- 禁止 System.out.println
- 禁止日志输出敏感信息（密码、密钥、手机号、身份证）
- 格式：`log.info("[模块/方法] - 操作描述: key={}, value={}", key, value)`
- 错误日志：`log.error("[模块/方法] - 操作失败: errorMsg={}", e.getMessage(), e)`

---

## 11. 测试规范

- JUnit 5 + Mockito
- 测试方法命名：方法名_场景_预期（如 createOrder_success、payOrder_expired）
- 测试结构：given / when / then
- Service 层必须写单测
- **每个接口（Controller 方法）必须写测试用例**，覆盖正常流程和异常流程
- 接口测试用 MockMvc 模拟 HTTP 请求，验证响应状态码、返回结构、业务字段
- 接口测试必须覆盖的场景：
  - 正常入参 → 返回成功
  - 必填字段缺失 → 返回参数校验失败
  - 非法/边界值入参 → 返回业务异常
  - 无权限访问 → 返回权限拒绝
- Service 层测试用 Mockito mock 依赖，专注验证业务逻辑
- 修改代码后必须跑相关测试确认无回归
- 核心业务逻辑覆盖率目标 80%+

---

## 12. Git 规范

- commit message：`[模块] 操作描述`（如 [订单] 新增自动取消未支付订单功能）
- 提交前必须编译通过 + 测试通过
- 分支命名：feature/xxx、bugfix/xxx、hotfix/xxx
- 禁止提交 .class / .iml / target / .idea

---

## 13. 禁止事项

- 禁止硬编码密码、密钥、Token
- 禁止 System.out.println
- 禁止代码拼接 SQL
- 禁止空 catch 块
- 禁止未经审核直接修改公共接口
- 禁止删除数据库迁移脚本
- 禁止金额使用 FLOAT/DOUBLE
- 禁止在 Controller 写业务逻辑
- 禁止提交 TODO 到主分支
- 禁止新增接口不写测试用例
- 禁止新增功能不写功能清单文档
- 禁止 SQL 不放 Sql/ 目录
- 禁止 UI 未定版就写代码
- 禁止 UI 变更后不更新功能清单

---

## 14. 工作流

### 设计先行，UI 定版再写代码

- **当前阶段：UI 交互原型设计阶段，禁止写代码**
- 项目开发必须严格遵循：功能清单 → UI 交互原型 → 代码实现
- **UI 交互原型未定版前，只做以下工作：**
  - 梳理功能清单（`docs/功能清单-模块名.md`）
  - 设计 UI 交互原型
  - 讨论业务规则和流程
  - 规划技术方案
- **UI 交互原型定版后，用户明确通知"可以开始写代码"才开始编码**
- 未收到写代码指令前，禁止生成任何业务代码（建表 SQL、实体类、Service、Controller 等）

### 功能清单与 UI 原型联动

- 功能清单和 UI 交互原型必须保持一致
- UI 交互原型发生变更时（增删页面、调整字段、修改交互流程），必须同步更新对应的功能清单文档
- 更新功能清单时，在文档末尾追加变更记录：

```markdown
## 变更记录

| 日期 | 变更内容 | 关联UI页面 |
|------|----------|-----------|
| 2026-06-16 | 新增"商品评价"功能 | 商品详情页 |
| 2026-06-17 | 移除"分享赚佣金"入口 | 首页 |
```

- 功能清单新增/删除功能时，必须同步说明 UI 原型的对应调整

### 计划先行
- 3 步以上的任务，先制定计划再动手
- 架构变更必须先讨论方案
- 发现偏差立即停止，重新评估

### 完成前必须验证
- 编译通过
- 测试通过
- 检查修改影响范围
- 自问：资深工程师会批准这个改动吗？

### 功能清单必须输出
- 每个新功能开发完成后，必须在 `docs/` 目录下输出功能清单文档
- 文档命名：`docs/功能清单-模块名.md`（如 docs/功能清单-订单.md、docs/功能清单-商品.md）
- 文档内容必须包含：
  - 模块名称
  - 功能列表：每个方法/接口的名称、用途描述、请求路径、入参说明、出参说明
  - 业务规则：该功能涉及的核心业务规则
  - 依赖关系：该功能依赖的其他模块或外部服务
- 已有模块新增方法时，更新对应的功能清单文档，追加新方法描述
- 如果 `docs/` 目录不存在，自动创建

### 持续改进
- 用户纠正同一错误两次以上 → 更新此文件
- 常见陷阱记录到 tasks/lessons.md
- 每次会话开始时回顾相关 lessons

---

## 15. 权限与数据隔离

| 角色 | 可见数据 |
|------|----------|
| 平台管理员 | 全部 |
| 城市运营 | 当前城市及下级 |
| 供应商 | 自身商品/订单/佣金 |
| 采购员 | 负责的商品/采购单 |
| 自提点 | 当前自提点订单/佣金 |
| 仓库角色 | 当前仓库库存/任务 |

数据隔离通过 MyBatis 拦截器统一处理，禁止在业务代码中手动过滤。

---

## 16. 多端差异备忘

| 端 | 角色 | 核心能力 |
|----|------|----------|
| 运营后台 | 运营/采购/财务/仓管/管理员 | 全量管理 |
| 用户小程序 | C端用户 | 浏览/下单/支付/售后 |
| 自提点小程序 | 团点负责人 | 订单汇总/佣金/收退货 |
| 供应商小程序 | 供应商 | 销售数据/售后/佣金/提现 |
| 仓配 H5 | 收货/拣货/复检/装车/司机 | 入库/出库/盘点/配送 |

不同端调用同一个 Service，通过角色和数据权限控制差异。

---

## 17.Skills 使用约定
- 涉及新框架 / 新版本 API：必须先用 context7 查最新文档
- 需要外部数据：用 firecrawl
- 接到 PRD 或复杂需求：用 task-master 拆任务
- 不要在没跟我确认的情况下执行会改文件的 skill

---

## 测试标记
- 当用户问"测试标记"时，必须回复："AGNETS模版 生效2222"

---

_最后更新：2026-06-16_
