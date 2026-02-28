# 02. Observability（Interceptor 计时 vs Actuator 指标）

## 导读

本章围绕「02：Observability（Interceptor 计时 vs Actuator 指标）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootWebMvcObservabilityLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcObservabilityLabTest`

## 机制主线

本模块提供一条最小闭环：

1. 对 `/api/advanced/**` 注册 `TimingInterceptor`，把耗时写入响应头 `X-Lab-Elapsed-Ms`（用于直观感受观测点）。
2. 引入 Actuator 并最小暴露端点（`health/info/metrics`），通过 `/actuator/metrics/http.server.requests` 验证指标存在。

## 源码与断点

- Interceptor 链路入口：
  - `org.springframework.web.servlet.DispatcherServlet#doDispatch`
  - `org.springframework.web.servlet.HandlerExecutionChain#applyPreHandle`
- 指标链路（理解即可，不要求背源码）：
  - 关键是知道 metrics 来自“整体运行时”，而不是只来自 controller 方法体。

## 最小可运行实验（Lab）

- Lab：`BootWebMvcObservabilityLabTest`
  - 访问 `/api/advanced/contract/ping`，断言 `X-Lab-Elapsed-Ms` 存在
  - 访问 `/actuator/metrics/http.server.requests`，断言指标可读取

对应源码：
- `TimingInterceptor`：`spring-boot-modules/spring-boot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part08_security_observability/TimingInterceptor.java`
- `ObservabilityWebMvcConfig`：`spring-boot-modules/spring-boot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part08_security_observability/ObservabilityWebMvcConfig.java`
- `application.properties`：暴露 actuator 端点（教学最小集合）

## 常见坑与边界

- 没看到 `/actuator/metrics`：先看 `management.endpoints.web.exposure.include` 是否包含 `metrics`。
- “我加了 interceptor，但没有 header”：确认 path pattern（本模块只对 `/api/advanced/**` 生效），以及请求是否真的走到了 MVC handler。
- 指标数据为空：先触发几次请求再看 metrics（指标通常需要发生请求才会出现数据点）。

## 小结与下一章

- 下一章进入 Appendix：把常见坑与排障套路收敛成清单，遇到 400/401/403/404/406/415 时能快速定位。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcObservabilityLabTest`

上一章：[part-08-security-observability/01-security-filterchain-and-mvc.md](01-security-filterchain-and-mvc.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](../appendix/01-common-pitfalls.md)

<!-- BOOKIFY:END -->

