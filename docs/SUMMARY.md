# 目录（SSOT）

本文件是文档站的导航来源（SSOT）：侧边栏的顺序与层级以此处为准。

**对读者：**

- 建议从“封面”（根 `README.md`）开始，先完成一个模块的最小闭环，再按目录继续补齐机制与边界。
- 每个模块的根 `README.md` 是该模块的目录页与阅读顺序（唯一顺序来源）；模块内 `docs/` 目录保持扁平。

**对维护者：**

- `<!--nav-->` **之后**的列表会被文档站解析为侧边栏导航；如需调整目录结构，请在该区域修改。
- 链接使用相对本文件所在目录的路径（指向仓库根请以 `../` 开头）。
- 文档正文以各模块 `*/docs/*.md` 为准；仓库根 `docs/` 包含 `SUMMARY.md` + `book/`（全书主线），其中 `SUMMARY.md` 作为全站导航（SSOT）。

<!--nav-->
- [封面](../README.md)
- [写作规范（维护者）：教材式 / 中性口吻](writing-style-guide.md)
- 全书主线（教材式顺读）
  - [全书主线（总览）](book/README.md)
  - [01. Getting Started：如何在本仓库学习（入口与证据链）](book/01-getting-started.md)
  - [02. Spring Boot Basics：启动、配置与最小闭环](book/02-spring-boot-basics.md)
  - [03. Spring Core Beans：IoC 容器、Bean 生命周期与扩展点](book/03-spring-core-beans.md)
  - [04. Spring Core AOP：切面、代理与“边界”](book/04-spring-core-aop.md)
  - [05. Spring Core Tx：事务边界、传播与回滚](book/05-spring-core-tx.md)
  - [06. Spring Boot Web MVC：请求主链路与错误形状](book/06-spring-boot-web-mvc.md)
  - [07. Spring Core Validation：约束、Violation 与方法校验边界](book/07-spring-core-validation.md)
  - [08. Spring Boot Testing：切片、全量上下文与证据链](book/08-spring-boot-testing.md)
  - [09. Spring Boot Data JPA：Persistence Context、flush 与脏检查](book/09-spring-boot-data-jpa.md)
  - [10. Spring Boot Web Client：超时/重试与可测试性](book/10-spring-boot-web-client.md)
  - [11. Spring Boot Async & Scheduling：线程边界与上下文传播](book/11-spring-boot-async-scheduling.md)
  - [12. Spring Boot Cache：缓存语义、key 与边界](book/12-spring-boot-cache.md)
  - [13. Observability & Actuator：日志/指标/端点与排障入口](book/13-observability-and-actuator.md)
  - [14. Spring Boot Security：401/403、链路与常见陷阱](book/14-spring-boot-security.md)
  - 附录
    - [90. Troubleshooting Index：从现象到最短证据链入口](book/90-troubleshooting-index.md)
    - [91. Glossary：术语对照表](book/91-glossary.md)
    - [92. References：参考资料与延伸阅读](book/92-references.md)
- 模块文档
  - Spring Boot（应用层）
    - [spring-boot-basics](../spring-boot-modules/spring-boot-basics/README.md)
    - [spring-boot-web-mvc](../spring-boot-modules/spring-boot-web-mvc/README.md)
    - [spring-boot-testing](../spring-boot-modules/spring-boot-testing/README.md)
    - [spring-boot-data-jpa](../spring-boot-modules/spring-boot-data-jpa/README.md)
    - [spring-boot-web-client](../spring-boot-modules/spring-boot-web-client/README.md)
    - [spring-boot-async-scheduling](../spring-boot-modules/spring-boot-async-scheduling/README.md)
    - [spring-boot-cache](../spring-boot-modules/spring-boot-cache/README.md)
    - [spring-boot-observability](../spring-boot-modules/spring-boot-observability/README.md)
    - [spring-boot-actuator](../spring-boot-modules/spring-boot-actuator/README.md)
    - [spring-boot-logging](../spring-boot-modules/spring-boot-logging/README.md)
    - [spring-boot-security](../spring-boot-modules/spring-boot-security/README.md)
    - [spring-boot-autoconfiguration](../spring-boot-modules/spring-boot-autoconfiguration/README.md)
    - [spring-boot-business-case](../spring-boot-modules/spring-boot-business-case/README.md)
  - Spring Core（基础/机制）
    - [spring-core-beans](../spring-core-modules/spring-core-beans/README.md)
    - [spring-core-aop](../spring-core-modules/spring-core-aop/README.md)
    - [spring-core-aop-weaving](../spring-core-modules/spring-core-aop-weaving/README.md)
    - [spring-core-tx](../spring-core-modules/spring-core-tx/README.md)
    - [spring-core-validation](../spring-core-modules/spring-core-validation/README.md)
    - [spring-core-events](../spring-core-modules/spring-core-events/README.md)
    - [spring-core-profiles](../spring-core-modules/spring-core-profiles/README.md)
    - [spring-core-resources](../spring-core-modules/spring-core-resources/README.md)
    - [spring-core-spel](../spring-core-modules/spring-core-spel/README.md)
