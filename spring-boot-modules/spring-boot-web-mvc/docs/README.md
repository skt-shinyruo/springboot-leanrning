# Spring Boot Web MVC：请求主线、绑定与错误形状

本模块围绕一次 HTTP 请求在 Spring MVC 中的完整旅程展开：从 FilterChain 进入，到 `DispatcherServlet#doDispatch` 选路与执行，再到参数解析、绑定与消息转换，最后收敛为一致的异常与错误响应形状。重点不在“记住有哪些注解”，而在建立可复用的排障路径：看到 400/406/415 等错误时，能够在最短调用链与断点上确认分支与根因。

---

## 10 分钟入口：先把一条请求链路跑通

- `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`

运行后应能回答三个事实问题：

1. 请求进入 MVC 的关键入口在哪里（从 FilterChain 到 `doDispatch`）；
2. 绑定与转换失败时会走哪类异常解析；
3. 最终错误响应形状由哪些组件共同决定。

---

## 阅读路线（先建立坐标，再沿主线深入）

### 1) 建立坐标：先拿到“全图”

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)
3. [请求调用链速览（先知道断点该打在哪）](part-00-guide/03-webmvc-request-call-chain.md)
4. [知识地图（先看全图）](part-00-guide/05-knowledge-map.md)
5. [断点图（排障优先）](part-00-guide/06-breakpoint-map.md)

### 2) 请求主线（internals）：把链路跑通

- [DispatcherServlet 调用链（含参数解析/绑定）](part-03-web-mvc-internals/01-dispatcherservlet-call-chain.md)
- [消息转换与返回值](part-03-web-mvc-internals/03-message-converters-and-return-values.md)
- [异常收敛与错误流](part-03-web-mvc-internals/04-exception-resolvers-and-error-flow.md)
- [@ControllerAdvice 匹配与顺序](part-03-web-mvc-internals/05-controlleradvice-matching-and-ordering.md)

### 3) 回到应用层：把常见需求接回主线

- [校验与错误塑形](part-01-web-mvc/01-validation-and-error-shaping.md)
- [异常处理](part-01-web-mvc/02-exception-handling.md)
- [绑定与 Converter](part-01-web-mvc/03-binding-and-converters.md)
- [拦截器与过滤器顺序](part-01-web-mvc/04-interceptor-and-filter-ordering.md)
- [异步拦截器生命周期](part-01-web-mvc/05-interceptor-async-lifecycle.md)

### 4) 场景扩展（按需进入）

- View MVC（模板与错误页）：从 [01](part-02-view-mvc/01-thymeleaf-and-view-resolver.md) 开始
- REST 合同（406/415/Jackson）：从 [01](part-04-rest-contract/01-content-negotiation-406-415.md) 开始
- 真实 HTTP 场景（CORS/上传/下载/静态资源）：从 [01](part-05-real-world-http/01-cors-preflight.md) 开始
- 异步与 SSE：从 [01](part-06-async-sse/01-servlet-async-and-testing.md) 开始
- 测试与排障：[01](part-07-testing-debugging/01-webmvc-testing-and-troubleshooting.md)
- 安全与观测：从 [01](part-08-security-observability/01-security-filterchain-and-mvc.md) 开始

---

## 排障入口（从症状回到最短分支）

- 断点地图（排障优先）：[06-breakpoint-map.md](part-00-guide/06-breakpoint-map.md)
- 请求调用链速览（快速定位）：[03-webmvc-request-call-chain.md](part-00-guide/03-webmvc-request-call-chain.md)
- 关键分支矩阵（If/Then 收敛）：[04-branch-decision-matrix.md](part-00-guide/04-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
- Branch Matrix（错误分支矩阵 400/406/415）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-web-mvc -Dtest=*ExerciseSolutionTest test`
- 并发/性能（RequestScope 隔离 / 并发请求边界）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcRequestScopeIsolationLabTest test`

---

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
