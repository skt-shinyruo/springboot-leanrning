# spring-boot-business-case（Capstone 综合案例）

这是一个“综合/真实”一点的练习模块，用来把常见的 Spring Boot 能力串起来，并刻意暴露一些**机制层面的关键点**。

本模块默认启动一个 HTTP API（端口 `8084`），更贴近真实业务的“入口/边界”学习方式。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix-self-check.md)

## 本模块的学习产出

- Web MVC：Controller + 请求绑定 + JSON
- Validation：`@Valid` 边界校验 + 错误响应
- JPA：Entity/Repository + H2（内存库）
- Tx：`@Transactional` 提交/回滚、事务边界
- Events：同步事件 vs `@TransactionalEventListener`（after-commit）
- AOP：代理生效、Advice 记录调用

## 前置知识

- 建议先完成：`spring-boot-web-mvc`、`spring-boot-data-jpa`、`spring-core-tx`、`spring-core-events`、`spring-core-aop`
- 至少能解释：代理（proxy）、事务边界、同步事件与 after-commit 的差异

## 关键命令

### 运行

```bash
mvn -pl :spring-boot-business-case spring-boot:run
```

### 快速验证（创建订单）

```bash
curl -s -X POST 'http://localhost:8084/api/orders' \
  -H 'Content-Type: application/json' \
  -d '{"customer":"Alice","sku":"SKU-1","quantity":2}'
```

### 测试

```bash
mvn -pl :spring-boot-business-case test
```

## 推荐 docs 阅读顺序

（目录：见本 README 的「目录（唯一顺序来源）」）

建议按“先跑通链路 → 再拆机制”的顺序：

1. [导读](docs/guide-deep-dive-guide.md)
2. [案例架构与主线](docs/business-case-architecture-and-flow.md)
3. [常见坑清单](docs/appendix-common-pitfalls.md)
4. [自测题](docs/appendix-self-check.md)
5. 先跑接口 + 跑 `BootBusinessCaseLabTest`：把“现象”固定下来
6. 出现疑问时按链路拆解：
   - 代理/AOP：[`AOP 心智模型`](../../spring-core-modules/spring-core-aop/docs/proxy-fundamentals-aop-proxy-mental-model.md)、[`self-invocation`](../../spring-core-modules/spring-core-aop/docs/proxy-fundamentals-self-invocation.md)
   - 事务：[`事务边界`](../../spring-core-modules/spring-core-tx/docs/transaction-basics-transaction-boundary.md)、[`@Transactional 也是代理`](../../spring-core-modules/spring-core-tx/docs/transaction-basics-transactional-proxy.md)
   - 事件与事务时机：[`@TransactionalEventListener`](../../spring-core-modules/spring-core-events/docs/async-and-transactional-transactional-event-listener.md)

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐⭐⭐=综合。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseLabTest.java` | 从 HTTP 入口串起 Validation/JPA/Tx/Events/AOP | ⭐⭐⭐ | 跟断点：Controller → Service → Repo → Listener → Aspect |
| Exercise | `src/test/java/com/learning/springboot/bootbusinesscase/part00_guide/BootBusinessCaseExerciseTest.java` | 新增接口/传播行为/自调用陷阱等综合练习 | ⭐⭐⭐ | 从“新增查询接口 + 测试”开始 |

## 常见 Debug 路径

- 观察 Service Bean 是否为 AOP 代理：`AopUtils.isAopProxy(bean)`
- 对比同步事件与 `@TransactionalEventListener` 的触发时机（commit/rollback）
- 在测试里用断点跟进：Controller → Service → Repository → Listener → Aspect
- 遇到“事务不生效”：优先排查是否自调用绕过代理（AOP/Tx 常见坑）

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Business Case（综合案例）：把模块串成系统

本模块提供一个端到端的综合案例，用来把 Web、Security、Tx、JPA、Events 等分散主题串成一条可运行链路。它的价值不在于“再学一套 API”，而在于把边界放回真实调用链：一个请求从进入控制器到落库、发事件、回响应，中间哪些位置是代理边界、事务边界与一致性边界。

---

### 10 分钟入口：先跑通一次端到端链路
- `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`

运行后应能回答：端到端流程中关键的边界落在什么位置；异常与回滚如何影响最终结果；并发时哪些状态会出现竞争与可见性差异。

### 从这里开始（建议顺序）
1. [主线时间线](docs/guide-mainline-timeline.md)
2. [深挖导读](docs/guide-deep-dive-guide.md)

### 顺读主线
- [架构与端到端流转](docs/business-case-architecture-and-flow.md)

### 进阶入口（排障/关键分支）
- 断点地图（排障优先）：[04-breakpoint-map.md](docs/guide-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](docs/guide-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](docs/appendix-common-pitfalls.md)
- 自检清单：[02-self-check.md](docs/appendix-self-check.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-business-case -Dtest=*ExerciseSolutionTest test`
- 并发/性能（并发下的业务边界证据链）：`mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseConcurrentOrderPlacementLabTest test`

---

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)
