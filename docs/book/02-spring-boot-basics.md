# 02 Spring Boot Basics：启动、配置与可验证的最小闭环

## 学习目标

- 能解释一个 Spring Boot 应用从 `SpringApplication#run` 到容器就绪的大致阶段划分。
- 能把“配置从哪里来、如何覆盖、如何绑定”为类型安全对象，跑成可回归的断言。
- 知道配置问题的最短排障路径：现象 → 配置源/优先级 → 断点/证据链。

## 概念框架

- **启动主线**：`SpringApplication#run` → Environment 准备 → ApplicationContext 刷新 → Bean 创建与装配。
- **配置源（PropertySources）**：命令行参数、环境变量、配置文件、默认值等共同参与“最终值”的决策。
- **Profiles**：控制配置片段与 Bean 条件生效的开关，常与覆盖规则一起出现。
- **配置绑定**：`@ConfigurationProperties` 把字符串配置绑定为类型安全对象（含转换/校验/默认值）。

本章与后续章节的关系：

- 配置的结果最终体现在 **Bean 装配与代理边界**：下一章进入 [03 Beans](03-spring-core-beans.md)。
- 配置常用于 Web、数据、观测等模块的开关：在 [06 Web MVC](06-spring-boot-web-mvc.md)、[13 Observability](13-observability-and-actuator.md) 会反复出现。

## 实验入口

- Book Matrix（主线入口）：
  - `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
  - 测试类：[`BootBasicsBookMatrixLabTest.java`](../../spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBookMatrixLabTest.java)
- 模块目录页（顺读主线）：
  - [`spring-boot-basics/docs/README.md`](../../spring-boot-modules/spring-boot-basics/docs/README.md)
- 导航型文档（用于定位断点与分支，而非背结论）：
  - 主线时间线：[`part-00-guide/01-mainline-timeline.md`](../../spring-boot-modules/spring-boot-basics/docs/part-00-guide/01-mainline-timeline.md)
  - 断点地图：[`part-00-guide/04-breakpoint-map.md`](../../spring-boot-modules/spring-boot-basics/docs/part-00-guide/04-breakpoint-map.md)
  - 常见坑：[`appendix/01-common-pitfalls.md`](../../spring-boot-modules/spring-boot-basics/docs/appendix/01-common-pitfalls.md)

## 常见误区

- 以为“只要写在 `application.yml` 就一定生效”。需要用 **配置源优先级** 与 **Profile 激活条件** 把事实跑出来。
- 把 `@Value` 当成 `@ConfigurationProperties` 的等价替代。两者的绑定与可测试性差异很大。
- 遇到配置问题只看日志不下断点。配置决策通常发生在启动早期，断点能更快看到“最终值来自哪里”。

## 练习

- 练习 1（把覆盖规则跑成事实）：
  - 运行 `BootBasicsBookMatrixLabTest`，定位其中与 profile/覆盖相关的断言；
  - 对照模块文档的“关键分支矩阵”，把每个分支写成一句 If/Then 规则（只写你能用测试验证的部分）。
- 练习 2（把绑定边界跑成事实）：
  - 选择一个 `@ConfigurationProperties` 相关断言，记录：默认值、转换失败表现、缺失字段行为。

## 小结

- Boot 基础阶段的关键产出是：能解释“最终配置值来自哪里”，并能在断点处观察到决策过程。
- 配置的后果最终体现为 Bean 装配、代理、事务、Web 行为；因此下一章进入容器主线。

## 延伸阅读

- 下一章（配置 → Bean 装配）：[`03-spring-core-beans.md`](03-spring-core-beans.md)
- Boot 自动配置（条件装配/回退策略，扩展阅读）：[`../../spring-boot-modules/spring-boot-autoconfiguration/docs/README.md`](../../spring-boot-modules/spring-boot-autoconfiguration/docs/README.md)
- 术语对照（Environment / PropertySource / Profile）：[`91-glossary.md`](91-glossary.md)

---

[← 上一章](01-getting-started.md) | [目录](README.md) | [下一章 →](03-spring-core-beans.md)

