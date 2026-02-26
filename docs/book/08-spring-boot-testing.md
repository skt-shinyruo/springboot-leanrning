# 08 Spring Boot Testing：切片选择、边界控制与可回归验证

## 学习目标

- 能为一个需求选择合适的测试切片（slice），并说明为什么它足够、为什么它更快/更稳定。
- 能理解并验证 TestContext 缓存、`@MockBean` 覆盖与真实 Bean 的边界。
- 能把“测试失败”分解为：上下文装配问题 / Web 链路问题 / 数据与事务边界问题。

## 概念框架

- **切片（slice）**：只加载与目标相关的部分上下文，用更小代价固定行为。
  - Web 层：关注请求主线与序列化/错误形状（对应 [06 Web MVC](06-spring-boot-web-mvc.md)）。
  - 数据层：关注持久化上下文与事务语义（对应 [09 Data JPA](09-spring-boot-data-jpa.md) 与 [05 Tx](05-spring-core-tx.md)）。
- **边界控制**：
  - `@MockBean`：在完整上下文中替换真实 Bean（需要明确“替换对象是谁、替换发生在哪一段”）。
  - TestContext cache：复用能提升速度，但也可能隐藏“状态泄漏/配置污染”的问题。
- **可回归的验收**：
  - 目标不是“测到代码覆盖率”，而是“把机制与边界跑成事实并可重复验证”。

## 实验入口

- Book Matrix（主线入口）：
  - `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`
  - 测试类：[`BootTestingBookMatrixLabTest.java`](../../spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingBookMatrixLabTest.java)
- 模块目录页（顺读主线）：
  - [`spring-boot-testing/docs/README.md`](../../spring-boot-modules/spring-boot-testing/docs/README.md)
- 导航型文档（用于选择 slice 与定位覆盖边界）：
  - 主线时间线：[`part-00-guide/01-mainline-timeline.md`](../../spring-boot-modules/spring-boot-testing/docs/part-00-guide/01-mainline-timeline.md)
  - 断点地图：[`part-00-guide/04-breakpoint-map.md`](../../spring-boot-modules/spring-boot-testing/docs/part-00-guide/04-breakpoint-map.md)
  - 常见坑：[`appendix/01-common-pitfalls.md`](../../spring-boot-modules/spring-boot-testing/docs/appendix/01-common-pitfalls.md)

## 常见误区

- 只用 `@SpringBootTest` 解决所有测试问题。完整上下文很强，但也更慢、更难定位边界。
- 过度依赖 `@MockBean`，导致测试通过但真实系统行为失真。应先用 slice 固定边界，再用少量全量测试兜底。
- 忽视 TestContext cache 的影响：同一 JVM 进程里，不同测试之间可能共享上下文，导致“偶发”问题。

## 练习

- 练习 1（切片选择复盘）：
  - 运行 `BootTestingBookMatrixLabTest`；
  - 选择一个 Web 相关入口，把它对应到 [06 Web MVC](06-spring-boot-web-mvc.md) 的链路分型（400/406/415/异常收敛）。
- 练习 2（边界控制复盘）：
  - 选择一个 `@MockBean` 场景，写清：
    - 替换的是哪个 Bean（类型/名称）；
    - 替换发生在上下文哪个阶段（对照 Beans/AOP 的代理边界，必要时回看 [03](03-spring-core-beans.md) / [04](04-spring-core-aop.md)）。

## 小结

- 测试模块的主线是：先选对切片，再把边界控制好，让机制理解可以被重复验证。
- 接下来进入 Data JPA，把“持久化上下文 + 事务 + 测试策略”串成同一条证据链。

## 延伸阅读

- 下一章（数据访问与持久化上下文）：[`09-spring-boot-data-jpa.md`](09-spring-boot-data-jpa.md)
- Web 请求主线（适配 slice）：[`06-spring-boot-web-mvc.md`](06-spring-boot-web-mvc.md)
- 事务边界（决定一致性与可见性）：[`05-spring-core-tx.md`](05-spring-core-tx.md)

---

[← 上一章](07-spring-core-validation.md) | [目录](README.md) | [下一章 →](09-spring-boot-data-jpa.md)

