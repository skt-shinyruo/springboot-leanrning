# 13 Observability & Actuator：日志、指标与端点（排障入口聚合）

本章聚合三个主题模块：

- Logging：日志系统初始化、级别决策与 MDC/分类
- Observability：metrics/observations 的链路与信号产生点
- Actuator：端点暴露、访问控制与自检入口

## 学习目标

- 能解释三类观测信号各自解决什么问题：日志（定位）/ 指标（趋势与告警）/ 追踪（跨服务链路）。
- 能定位“信号从哪里来”：HTTP 请求如何产生 observation/meter、日志级别如何决定、端点为何不可访问。
- 能把观测与前面章节串起来：Web 请求主线、异步上下文、缓存命中、数据访问行为，都应有可观测入口。

## 概念框架

- **Logging（日志）**：
  - 日志系统初始化与级别决策属于启动期行为，经常与配置覆盖相关（参见 [02 Boot Basics](02-spring-boot-basics.md)）。
  - MDC 用于关联上下文；跨线程需要显式传播（参见 [11 Async](11-spring-boot-async-scheduling.md)）。
- **Observability（指标/观察）**：
  - HTTP 指标一般来自请求主线的观测点（参见 [06 Web MVC](06-spring-boot-web-mvc.md)）。
- **Actuator（端点）**：
  - 端点暴露是“配置 + 安全 + 访问路径”的组合结果；需要先分型再排障（与 [14 Security](14-spring-boot-security.md) 有强关联）。

## 实验入口

### Actuator

- `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`
- 测试类：[`BootActuatorBookMatrixLabTest.java`](../../spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorBookMatrixLabTest.java)
- 模块目录页：[`spring-boot-actuator/README.md`](../../spring-boot-modules/spring-boot-actuator/README.md)

### Observability

- `mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBookMatrixLabTest test`
- 测试类：[`BootObservabilityBookMatrixLabTest.java`](../../spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityBookMatrixLabTest.java)
- 模块目录页：[`spring-boot-observability/README.md`](../../spring-boot-modules/spring-boot-observability/README.md)

### Logging

- `mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBookMatrixLabTest test`
- 测试类：[`BootLoggingBookMatrixLabTest.java`](../../spring-boot-modules/spring-boot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingBookMatrixLabTest.java)
- 模块目录页：[`spring-boot-logging/README.md`](../../spring-boot-modules/spring-boot-logging/README.md)

## 常见误区

- 把“看不见指标/端点”当成“依赖没引入”。更多时候是暴露配置、访问控制或匹配范围的问题。
- 以为日志级别调整是“运行时立刻生效且全局一致”。级别决策与 logger 分类、配置覆盖密切相关，需要用调用链/断点验证。
- 在异步/线程池场景里使用 MDC 或上下文信息而不验证传播策略，导致关联丢失或串线（参见 [11 Async](11-spring-boot-async-scheduling.md)）。

## 练习

- 练习 1（端点分型）：
  - 运行 `BootActuatorBookMatrixLabTest`；
  - 遇到端点不可访问时，先写清三问：
  - 端点是否暴露（exposure）？
  - 是否被安全拦截（认证/授权）？
  - 请求路径与端点映射是否匹配？
- 练习 2（把观测信号接回请求主线）：
  - 运行 `BootObservabilityBookMatrixLabTest`；
  - 对照 Web MVC 的请求主线文档，标出“观测点出现的位置”，并写清它观察的是什么。

## 小结

- 观测模块的价值不在“知道有哪些概念”，而在“能快速定位信号产生点与丢失点”，形成可重复的排障路径。
- 下一章进入 Security，把访问控制与顺序问题补齐：观测端点的可用性通常离不开安全配置。

## 延伸阅读

- 配置覆盖（影响日志/端点暴露）：[`02-spring-boot-basics.md`](02-spring-boot-basics.md)
- Web 请求主线（HTTP 指标与错误分支）：[`06-spring-boot-web-mvc.md`](06-spring-boot-web-mvc.md)
- 下一章（FilterChain 与方法安全）：[`14-spring-boot-security.md`](14-spring-boot-security.md)

---

[← 上一章](12-spring-boot-cache.md) | [目录](README.md) | [下一章 →](14-spring-boot-security.md)

