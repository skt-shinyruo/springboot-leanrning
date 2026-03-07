# 14 Spring Boot Security：FilterChain、401/403 分支与方法安全代理

## 学习目标

- 能解释安全主线：请求进入后如何匹配到 `SecurityFilterChain`，以及认证/授权在链路中的位置。
- 能把 401/403/CSRF 等常见分支跑成事实，并能说明它们发生在链路哪一段、由什么条件触发。
- 能理解方法安全为何依赖代理边界，并能用 AOP 心智模型排障“注解不生效”类问题。

## 概念框架

- **两层模型**：
  - Web 层（FilterChain）：请求级别的认证与授权。
  - 方法层（Method Security）：方法调用级别的访问控制（通常依赖代理拦截）。
- **分支分型**：
  - 401：未认证（缺失/无效凭证）。
  - 403：已认证但无权限（或被 CSRF 等策略拒绝）。
  - CSRF：默认策略在部分写请求上引入额外分支，需要明确“是否适用当前 API 形态”。
- **顺序与匹配范围**：
  - 安全问题很多时候是“匹配范围与顺序”的问题：先确认链路，再谈规则表达。

## 实验入口

- Book Matrix（主线入口）：
  - `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`
  - 测试类：[`BootSecurityBookMatrixLabTest.java`](../../spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityBookMatrixLabTest.java)
- 模块目录页（顺读主线）：
  - [`spring-boot-security/README.md`](../../spring-boot-modules/spring-boot-security/README.md)
- 导航型文档（用于定位链路与顺序）：
  - FilterChain 与顺序：[`part-01-security/04-filter-chain-and-order.md`](../../spring-boot-modules/spring-boot-security/docs/security-filter-chain-and-order.md)
  - 方法安全与代理：[`part-01-security/03-method-security-and-proxy.md`](../../spring-boot-modules/spring-boot-security/docs/security-method-security-and-proxy.md)
  - 常见坑：[`appendix/01-common-pitfalls.md`](../../spring-boot-modules/spring-boot-security/docs/appendix-common-pitfalls.md)

## 常见误区

- 把 401 与 403 混为一谈。应先分型：是否已认证、是否有权限、是否被 CSRF 等策略拒绝。
- 规则写得很复杂但不验证匹配范围与顺序。建议优先用“分支矩阵 + 断点”把链路压到最短。
- 方法安全注解不生效时只改注解参数。先确认是否经过代理（参见 [04 AOP](04-spring-core-aop.md)）。

## 练习

- 练习 1（401/403 分支证据链）：
  - 运行 `BootSecurityBookMatrixLabTest`；
  - 选择一个 401 与一个 403 场景，分别写清：
  - 触发条件（请求长什么样）；
  - 发生在 FilterChain 的哪一段（对照模块文档的断点图/顺序章节）。
- 练习 2（方法安全与代理边界）：
  - 选择一个方法安全入口，回答两问：
  - 调用是否经过代理？
  - self-invocation 是否会绕过？

## 小结

- Security 的排障路径是：先分型（401/403/CSRF）→ 再确认匹配范围与顺序 → 最后再调整规则表达。
- 本书主线到此结束；后续从索引与术语开始做复盘与排障收敛。

## 延伸阅读

- Web 请求主线（与安全链路叠加）：[`06-spring-boot-web-mvc.md`](06-spring-boot-web-mvc.md)
- AOP 代理边界（方法安全前置）：[`04-spring-core-aop.md`](04-spring-core-aop.md)
- 观测端点与访问控制：[`13-observability-and-actuator.md`](13-observability-and-actuator.md)
- Troubleshooting Index：[`90-troubleshooting-index.md`](90-troubleshooting-index.md)

---

[← 上一章](13-observability-and-actuator.md) | [目录](README.md) | [下一章 →](90-troubleshooting-index.md)

