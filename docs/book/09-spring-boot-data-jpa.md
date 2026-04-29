# 09 Spring Boot Data JPA：实体状态、持久化上下文与事务语义

## 本章要回答的问题

- 能解释实体状态与持久化上下文（Persistence Context）如何影响“看到的数据”和“写入的时机”。
- 能理解并复现 flush/脏检查、N+1 等高频问题，并知道最短验证路径。
- 能把 Data JPA 的行为与事务边界、测试切片联系起来（尤其是 `@DataJpaTest` 与事务默认语义）。

## 主线框架

- **实体状态**：Transient / Managed / Detached 等状态变化，决定了“是否受上下文追踪”。
- **持久化上下文**：
  - 一级缓存与 identity map：同一上下文内的重复读取可能是“缓存假象”，不是数据库事实。
  - 脏检查：决定何时产生 SQL 更新。
- **flush**：
  - flush 决定“SQL 何时发出去”，与事务提交并非同一概念。
- **Fetching 与 N+1**：
  - fetching 策略决定关联加载方式；N+1 常需要结合 SQL 日志与断点验证。
- **与 Tx 的关系**：
  - 大量现象只有在明确事务边界后才可解释：见 [05 Tx](05-spring-core-tx.md)。

## 实验入口

- Book Matrix（主线入口）：
  - `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
  - 测试类：[`BootDataJpaBookMatrixLabTest.java`](../../spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaBookMatrixLabTest.java)
- 模块目录页（顺读主线）：
  - [`spring-boot-data-jpa/README.md`](../../spring-boot-modules/spring-boot-data-jpa/README.md)
- 导航型文档（用于快速定位“状态/上下文/flush/N+1”）：
  - 实体状态：[`data-jpa-entity-states.md`](../../spring-boot-modules/spring-boot-data-jpa/docs/data-jpa-entity-states.md)
  - 持久化上下文：[`data-jpa-persistence-context.md`](../../spring-boot-modules/spring-boot-data-jpa/docs/data-jpa-persistence-context.md)
  - flush 与可见性：[`data-jpa-flush-and-visibility.md`](../../spring-boot-modules/spring-boot-data-jpa/docs/data-jpa-flush-and-visibility.md)
  - 常见坑：[`appendix-common-pitfalls.md`](../../spring-boot-modules/spring-boot-data-jpa/docs/appendix-common-pitfalls.md)

## 常见误区

- 把“读到的值”当成数据库事实。需要先判断：是否命中持久化上下文的一致性语义（一级缓存）。
- 以为 flush = commit。flush 是 SQL 发送时机，commit 是事务边界的完成动作（两者经常需要分别验证）。
- 把 N+1 只当成“SQL 太多”。需要用 fetching 策略与访问路径把触发条件复现出来，再谈优化。

## 验证练习

- 练习 1（上下文假象识别）：
  - 运行 `BootDataJpaBookMatrixLabTest`；
  - 选择一个“读到的值不符合预期”的场景，先回答两问：
  - 这是一级缓存语义还是数据库语义？
  - 事务边界在哪里（必要时回看 [05 Tx](05-spring-core-tx.md)）？
- 练习 2（把 N+1 变成确定性证据）：
  - 选择一个 fetching/N+1 相关入口，记录：
  - 触发路径（哪些访问触发了额外查询）；
  - 如何用测试/SQL 调试把它固定下来。

## 小结

- Data JPA 的学习核心是：把“状态/上下文/flush/脏检查”的行为跑成事实，并能解释它们与事务边界的耦合。
- 下一章进入 Web Client，把“向外部系统发请求”的边界补齐，并把错误处理与测试策略固化下来。

## 延伸阅读

- 事务边界（决定一致性与回滚）：[`05-spring-core-tx.md`](05-spring-core-tx.md)
- 测试切片选择（`@DataJpaTest`）：[`08-spring-boot-testing.md`](08-spring-boot-testing.md)
- 观测与日志（SQL 调试与指标）：[`13-observability-and-actuator.md`](13-observability-and-actuator.md)

---

[← 上一章](08-spring-boot-testing.md) | [目录](README.md) | [下一章 →](10-spring-boot-web-client.md)
