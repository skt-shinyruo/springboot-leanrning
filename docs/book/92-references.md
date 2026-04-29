# 92 参考资料：官方资料与仓库内对照入口

## 本章要回答的问题

- 知道“遇到一个结论”时应该去哪类资料交叉验证（官方参考 / 仓库模块文档 / 可运行测试）。
- 能用“版本一致”的资料排除误导（Spring Boot 3.x 与 Spring Framework 6.x 的语义变化要特别注意）。
- 能在需要更深证据链时快速找到调用链/断点图/分支矩阵入口。

## 主线框架

- **仓库内资料**：
  - 模块根 `README.md`：导航与入口（目录/阅读顺序 SSOT）；正文与断点在模块 `docs/*.md`。
  - `*BookMatrixLabTest`：主线可运行入口；用来固定事实与回归验证。
  - 本目录 `docs/book/`：跨模块聚合与指引，不复制正文。
- **官方资料**：
  - 作为“概念定义与边界”的参考，但以本仓库代码与测试为最终验证。
- **工具资料**：
  - Maven/Surefire、JUnit、Hibernate、Micrometer 等影响可运行入口与观测行为。

## 实验入口

当需要把“参考资料中的结论”落到本仓库事实时，优先选择对应模块的 Book Matrix：

- Boot Basics：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- Beans：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`
- AOP：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`
- Tx：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`
- Web MVC：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
- Data JPA：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
- Security：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`

## 常见误区

- 混用不同大版本的官方文档（例如 Spring 5.x 与 6.x），导致结论与本仓库行为不一致。
- 只引用“概念定义”，不回到可运行入口验证边界。每个结论都应落到至少一个可回归断言。
- 直接复制外部最佳实践而不验证依赖链路与顺序（AOP/事务/安全/校验都高度依赖边界与顺序）。

## 验证练习

- 练习 1（参考 → 事实）：
  - 从官方文档选一个主题（例如 `@Transactional` 回滚规则），在本仓库找到对应模块章节与 Book Matrix，写出“可验证事实清单”（3 条即可）。
- 练习 2（调用链定位）：
  - 从任一模块的 `docs/guide-*.md` 找到调用链文档，挑一个关键方法，记录：
  - 为什么这个方法是断点锚点；
  - 观察哪几个变量可以决定分支。

## 小结

- 参考资料用于定义概念与边界；本仓库的测试用于把概念跑成事实。
- 学习的闭环是：参考 → 入口 → 断点/分支 → 回归断言。

## 延伸阅读

### 仓库内入口（优先）

- 仓库根导读：[`../../README.md`](../../README.md)
- 全站导航（SSOT）：[`../../docs/SUMMARY.md`](../SUMMARY.md)
- 模块文档（示例入口）：
  - Boot Basics：[`../../spring-boot-modules/spring-boot-basics/README.md`](../../spring-boot-modules/spring-boot-basics/README.md)
  - Beans：[`../../spring-core-modules/spring-core-beans/README.md`](../../spring-core-modules/spring-core-beans/README.md)
  - Web MVC：[`../../spring-boot-modules/spring-boot-web-mvc/README.md`](../../spring-boot-modules/spring-boot-web-mvc/README.md)

### 官方参考（按主题）

- Spring Boot Reference Documentation：https://docs.spring.io/spring-boot/reference/
- Spring Framework Reference Documentation：https://docs.spring.io/spring-framework/reference/
- Spring Security Reference Documentation：https://docs.spring.io/spring-security/reference/
- Jakarta Bean Validation：https://jakarta.ee/specifications/bean-validation/
- JUnit 5 User Guide：https://junit.org/junit5/docs/current/user-guide/
- Maven Surefire Plugin（`-Dtest` 等行为与匹配规则）：https://maven.apache.org/surefire/maven-surefire-plugin/
- Hibernate ORM Documentation：https://hibernate.org/orm/documentation/
- Micrometer Documentation：https://micrometer.io/docs

---

[← 上一章](91-glossary.md) | [目录](README.md) | [下一章 →](01-getting-started.md)
