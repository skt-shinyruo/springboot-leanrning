# 06 Spring Boot Web MVC：从请求到响应的主链路

## 学习目标

- 能按请求主线描述 Web MVC 的关键阶段：选路、参数解析/绑定、消息转换、异常收敛、响应输出。
- 能解释并复现常见 HTTP 分支：400/404/406/415，以及它们分别发生在链路的哪一段。
- 能把校验、异常处理、过滤器/拦截器顺序等“工程问题”落到可调试的证据链入口。

## 概念框架

- **请求主线（概览）**：
  - `FilterChain` → `DispatcherServlet` → `HandlerMapping`/`HandlerAdapter` → 参数解析与绑定 → 返回值处理与消息转换。
- **错误分支（概览）**：
  - 400：绑定/转换/校验失败、请求体解析失败等。
  - 404：未匹配到 handler。
  - 406/415：内容协商与消息转换（Accept/Content-Type）不满足。
- **边界与顺序**：
  - Filter 与 Interceptor 的职责不同；顺序问题通常需要“断点 + 分支矩阵”来收敛。
- **与 Validation 的关系**：
  - Web 入参校验与错误响应形状紧耦合（下一章 [07 Validation](07-spring-core-validation.md)）。

## 实验入口

- Book Matrix（主线入口）：
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
  - 测试类：[`BootWebMvcBookMatrixLabTest.java`](../../spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcBookMatrixLabTest.java)
- 模块目录页（顺读主线）：
  - [`spring-boot-web-mvc/docs/README.md`](../../spring-boot-modules/spring-boot-web-mvc/docs/README.md)
- 导航型文档（用于快速定位链路与断点）：
  - 请求调用链速览：[`02-dispatcherservlet/03-webmvc-request-call-chain.md`](../../spring-boot-modules/spring-boot-web-mvc/docs/02-dispatcherservlet/03-webmvc-request-call-chain.md)
  - 断点图：[`14-testing-observability/06-breakpoint-map.md`](../../spring-boot-modules/spring-boot-web-mvc/docs/14-testing-observability/06-breakpoint-map.md)
  - 常见坑：[`appendix/01-common-pitfalls.md`](../../spring-boot-modules/spring-boot-web-mvc/docs/appendix/01-common-pitfalls.md)

## 常见误区

- 把所有客户端错误都当成“业务校验失败”。应先分型：400/404/406/415 属于不同链路段。
- 认为 `@ControllerAdvice` 一定全局生效。实际存在匹配范围与顺序问题，需用断点/分支矩阵验证。
- 只用日志猜测绑定/转换问题。消息转换与参数解析的分支多，断点通常比日志更快收敛。

## 练习

- 练习 1（分支分型）：
  - 运行 `BootWebMvcBookMatrixLabTest`；
  - 任选一个错误分支（400/406/415），写清：
  - 触发条件（输入长什么样）；
  - 发生在链路哪一段（对照“请求调用链速览”）。
- 练习 2（把 Validation 接回主线）：
  - 选择一个入参校验场景，记录：
  - 约束从哪里来（注解/自定义约束）；
  - 错误响应形状由谁决定（异常收敛/错误映射）。

## 小结

- Web MVC 的学习目标是“把请求主线跑通”，并能把常见错误分支定位到最短链路段。
- 下一章进入 Validation，把“约束模型 → 触发 → 违规结果”从 Web 到方法层串起来。

## 延伸阅读

- 下一章（校验与代理边界）：[`07-spring-core-validation.md`](07-spring-core-validation.md)
- 安全与 FilterChain（Web 主线扩展）：[`14-spring-boot-security.md`](14-spring-boot-security.md)
- 观测与指标（请求主线的观测信号）：[`13-observability-and-actuator.md`](13-observability-and-actuator.md)

---

[← 上一章](05-spring-core-tx.md) | [目录](README.md) | [下一章 →](07-spring-core-validation.md)
