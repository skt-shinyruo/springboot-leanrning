# 10 Spring Boot Web Client：HTTP 调用、错误处理与可测试性

## 学习目标

- 能跑通一个最小 HTTP 调用闭环（RestClient/WebClient），并能解释拦截器/过滤器在链路中的生效位置。
- 能把错误处理与超时重试写成可回归验证（而不是只靠日志观察）。
- 能用 MockWebServer 等方式把外部依赖变成可控输入，固定边界与分支。

## 概念框架

- **两类客户端形态**：
  - RestClient：同步调用，适合大多数传统 MVC 项目边界。
  - WebClient：响应式调用，适合需要背压/流式处理的场景，但也更容易引入阻塞/线程模型误用。
- **错误处理**：
  - 将 4xx/5xx 与网络异常映射为领域异常，避免把“外部故障”渗透进业务语义。
- **超时与重试**：
  - 默认不应依赖“无限等待”；超时与重试需要确定性验证，避免假绿。
- **可测试性**：
  - 外部系统必须被替换为可控输入；否则测试不稳定、不具备回归价值。

## 实验入口

- Book Matrix（主线入口）：
  - `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`
  - 测试类：[`BootWebClientBookMatrixLabTest.java`](../../spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientBookMatrixLabTest.java)
- 模块目录页（顺读主线）：
  - [`spring-boot-web-client/README.md`](../../spring-boot-modules/spring-boot-web-client/README.md)
- 导航型文档（用于定位“拦截链/错误映射/测试入口”）：
  - Web Client 调用链：[`part-00-guide/03-webclient-call-chain.md`](../../spring-boot-modules/spring-boot-web-client/docs/guide-webclient-call-chain.md)
  - 错误处理：[`part-01-web-client/03-error-handling.md`](../../spring-boot-modules/spring-boot-web-client/docs/web-client-error-handling.md)
  - MockWebServer 测试：[`part-01-web-client/05-testing-with-mockwebserver.md`](../../spring-boot-modules/spring-boot-web-client/docs/web-client-testing-with-mockwebserver.md)

## 常见误区

- 把错误处理写成“打印日志 + 返回 null”。错误应被显式建模并可测试，避免在业务层形成隐式分支。
- 没有设置超时/重试策略，导致生产故障表现为“线程堆积/请求悬挂”，且难以复现。
- 在 WebClient 场景里随意阻塞（例如 `.block()`）而不验证线程模型与上下文传播，导致隐蔽死锁/性能问题。

## 练习

- 练习 1（错误映射固定）：
  - 运行 `BootWebClientBookMatrixLabTest`；
  - 选择一个 4xx/5xx 分支，记录：
  - 外部响应如何被映射为领域异常；
  - 对应的断言如何固定了行为。
- 练习 2（超时策略复盘）：
  - 从模块文档选择一个超时/重试入口；
  - 写出“超时发生时系统应该做什么”的规则，并在测试中验证。

## 小结

- Web Client 模块的关键产出是：把外部依赖边界变成可控输入，并把错误/超时/重试写成可回归规则。
- 下一章进入 Async & Scheduling，把跨线程边界与上下文传播问题补齐。

## 延伸阅读

- 服务端请求主线（对照边界）：[`06-spring-boot-web-mvc.md`](06-spring-boot-web-mvc.md)
- 下一章（异步与上下文传播）：[`11-spring-boot-async-scheduling.md`](11-spring-boot-async-scheduling.md)
- 观测与日志（客户端指标/日志关联）：[`13-observability-and-actuator.md`](13-observability-and-actuator.md)

---

[← 上一章](09-spring-boot-data-jpa.md) | [目录](README.md) | [下一章 →](11-spring-boot-async-scheduling.md)

