# Spring Boot Web MVC：目录

## 导读

本页是「Spring Boot Web MVC：目录」的目录页，建议以“先跑后读”的方式使用：先选一个可运行入口把现象跑通，再按主线章节顺读，把每个结论落到可回归的断言。


> 建议把它当成一本“请求主线的书”来读：先拿到知识地图与断点图，再沿着 DispatcherServlet 主线把选路/绑定/转换/异常收敛跑通，最后扩展到 REST、文件、异步、测试与安全观测。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)
3. [请求调用链速览（先知道断点该打在哪）](part-00-guide/03-webmvc-request-call-chain.md)
4. [知识地图（先看全图）](part-00-guide/05-knowledge-map.md)
5. [断点图（排障优先）](part-00-guide/06-breakpoint-map.md)

## 顺读主线（先把请求跑通）

- [DispatcherServlet 调用链（含参数解析/绑定）](part-03-web-mvc-internals/01-dispatcherservlet-call-chain.md)
- [消息转换与返回值](part-03-web-mvc-internals/03-message-converters-and-return-values.md)
- [异常收敛与错误流](part-03-web-mvc-internals/04-exception-resolvers-and-error-flow.md)
- [@ControllerAdvice 匹配与顺序](part-03-web-mvc-internals/05-controlleradvice-matching-and-ordering.md)

## 回到应用层（把常见需求接回主线）

- [校验与错误塑形](part-01-web-mvc/01-validation-and-error-shaping.md)
- [异常处理](part-01-web-mvc/02-exception-handling.md)
- [绑定与 Converter](part-01-web-mvc/03-binding-and-converters.md)
- [拦截器与过滤器顺序](part-01-web-mvc/04-interceptor-and-filter-ordering.md)
- [异步拦截器生命周期](part-01-web-mvc/05-interceptor-async-lifecycle.md)

## 场景扩展（按需挑选）

- View MVC（模板与错误页）：从 [01](part-02-view-mvc/01-thymeleaf-and-view-resolver.md) 开始
- REST 合同（406/415/Jackson）：从 [01](part-04-rest-contract/01-content-negotiation-406-415.md) 开始
- 真实 HTTP 场景（CORS/上传/下载/静态资源）：从 [01](part-05-real-world-http/01-cors-preflight.md) 开始
- 异步与 SSE：从 [01](part-06-async-sse/01-servlet-async-and-testing.md) 开始
- 测试与排障：[01](part-07-testing-debugging/01-webmvc-testing-and-troubleshooting.md)
- 安全与观测：从 [01](part-08-security-observability/01-security-filterchain-and-mvc.md) 开始

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[06-breakpoint-map.md](part-00-guide/06-breakpoint-map.md)
- 请求调用链速览（快速定位）：[03-webmvc-request-call-chain.md](part-00-guide/03-webmvc-request-call-chain.md)
- 关键分支矩阵（If/Then 收敛）：[04-branch-decision-matrix.md](part-00-guide/04-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
- 可跑入口（Branch Matrix - 错误分支矩阵 400/406/415）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-boot-web-mvc -Dtest=*ExerciseSolutionTest test`
- 可跑入口（并发/性能 Lab - RequestScope 隔离 / 并发请求边界）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcRequestScopeIsolationLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
