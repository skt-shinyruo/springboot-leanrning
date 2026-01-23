# springboot-business-case（Capstone 综合案例）

这是一个“综合/真实”一点的练习模块，用来把常见的 Spring Boot 能力串起来，并刻意暴露一些**机制层面的关键点**。

本模块默认启动一个 HTTP API（端口 `8084`），更贴近真实业务的“入口/边界”学习方式。

## 你将学到什么

- Web MVC：Controller + 请求绑定 + JSON
- Validation：`@Valid` 边界校验 + 错误响应
- JPA：Entity/Repository + H2（内存库）
- Tx：`@Transactional` 提交/回滚、事务边界
- Events：同步事件 vs `@TransactionalEventListener`（after-commit）
- AOP：代理生效、Advice 记录调用

## 前置知识

- 建议先完成：`springboot-web-mvc`、`springboot-data-jpa`、`spring-core-tx`、`spring-core-events`、`spring-core-aop`
- 至少能解释：代理（proxy）、事务边界、同步事件与 after-commit 的差异

## 关键命令

### 运行

```bash
mvn -pl :springboot-business-case spring-boot:run
```

### 快速验证（创建订单）

```bash
curl -s -X POST 'http://localhost:8084/api/orders' \
  -H 'Content-Type: application/json' \
  -d '{"customer":"Alice","sku":"SKU-1","quantity":2}'
```

### 测试

```bash
mvn -pl :springboot-business-case test
```

## 推荐 docs 阅读顺序

（docs 目录页：[`docs/README.md`](../../docs/business-case/springboot-business-case/README.md)）

建议按“先跑通链路 → 再拆机制”的顺序：

1. 导读：`docs/part-00-guide/00-deep-dive-guide.md`
2. 案例架构与主线：`docs/part-01-business-case/01-architecture-and-flow.md`
3. 常见坑清单：`docs/appendix/90-common-pitfalls.md`
4. 自测题：`docs/appendix/99-self-check.md`
5. 先跑接口 + 跑 `BootBusinessCaseLabTest`：把“现象”固定下来
6. 出现疑问时按链路拆解：
   - 代理/AOP：`docs/aop/spring-core-aop/part-01-proxy-fundamentals/01-aop-proxy-mental-model.md`、`docs/aop/spring-core-aop/part-01-proxy-fundamentals/03-self-invocation.md`
   - 事务：`docs/tx/spring-core-tx/part-01-transaction-basics/01-transaction-boundary.md`、`docs/tx/spring-core-tx/part-01-transaction-basics/02-transactional-proxy.md`
   - 事件与事务时机：`docs/events/spring-core-events/part-02-async-and-transactional/07-transactional-event-listener.md`

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
