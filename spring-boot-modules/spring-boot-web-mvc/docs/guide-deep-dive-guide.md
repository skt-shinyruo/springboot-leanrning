# 深挖导读：Spring Boot Web MVC
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章用于把模块主线、源码入口与断点路径串起来，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：Spring Boot Web MVC](guide-mainline-timeline.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 知识地图（Web MVC 深挖地图）](guide-knowledge-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`

## 机制主线

本模块的“深挖主线”不是背注解，而是能把一次请求分解成阶段，并能解释每个阶段的关键分支：

1. **Filter（Servlet 容器最外层）**
   - 典型分支：401/403（常发生在 MVC 之前，尤其是 Security）

2. **DispatcherServlet 选路**
   - 典型分支：404/405（找不到 handler / 方法不支持）

3. **参数解析 + 绑定 + 校验**
   - resolver：参数从哪来（header/path/query/body…）
   - binder：字符串怎么变成目标类型（ConversionService/Converter/Formatter）
   - validation：对象是否符合约束（@Valid）
   - 典型分支：400（missing/type mismatch/validation failed）

4. **返回值处理 + 内容协商**
   - 典型分支：406/415（write vs read，Accept/Content-Type/produces/consumes）

5. **异常翻译（ExceptionResolvers）**
   - 看到的状态码/错误体往往来自 resolver 链，而不是业务代码的 if/else

6. **真实 HTTP 分支（CORS/multipart/静态资源/条件请求/ETag）**
   - 典型分支：CORS 预检、上传下载 header、静态资源 304

7. **Async（Callable/DeferredResult/SSE）**
   - 典型分支：asyncStarted → asyncDispatch；Interceptor lifecycle 两阶段回调

> 先读知识地图（01），再按“现象 → 证据（测试）→ 断点（源码）”推进。

## 两条课程路径（说的“源码阅读课”与“工程落地课”）

### 路径 1：源码阅读课（以调用链与关键分支为中心）

目标：能把断点打在“分支发生的地方”，并能解释“为什么是这个状态码/这个错误体”。

阅读顺序：
1. 知识地图：`00-guide/05-knowledge-map.md`
2. DispatcherServlet 主链路：`02-dispatcherservlet/01-dispatcherservlet-call-chain.md`
3. resolver/binder：`05-argument-resolver/02-argument-resolver-and-binder.md`
4. converter/return value：`09-return-value-view/03-message-converters-and-return-values.md`
5. exception resolvers：`10-exception-resolvers/04-exception-resolvers-and-error-flow.md`

证据链（先跑再断点）：
- `BootWebMvcInternalsLabTest`（自定义 ArgumentResolver）
- `BootWebMvcTraceLabTest`（Filter/Interceptor + async lifecycle）
- `BootWebMvcTestingDebuggingLabTest`（resolvedException 固定 406/415）

### 路径 2：工程落地课（以契约可控与可回归为中心）

目标：把“常见分支”做成可回归的工程闭环：错误体、契约、缓存、上传下载、async、security、观测。

阅读顺序：
1. 校验与错误塑形：`06-binding-validation/01-validation-and-error-shaping.md`
2. 统一异常处理：`10-exception-resolvers/02-exception-handling.md`
3. 绑定与边界：`06-binding-validation/03-binding-and-converters.md`
4. Contract/Jackson：`07-message-conversion/*`（ProblemDetail 见 `10-exception-resolvers/04-problemdetail-vs-custom-error.md`）
5. Real World HTTP：`13-real-world-http/*`
6. Async/SSE：`12-async-sse/*`
7. Security/Observability：`01-filterchain-security/*` + `14-testing-observability/02-observability-and-metrics.md`
8. View MVC：`09-return-value-view/*` + `11-boot-error/*`

证据链（按顺序运行）：
- `BootWebMvcLabTest` / `BootWebMvcBindingDeepDiveLabTest`
- `BootWebMvcContractJacksonLabTest` / `BootWebMvcProblemDetailLabTest`
- `BootWebMvcRealWorldHttpLabTest`
- `BootWebMvcAsyncSseLabTest`
- `BootWebMvcSecurityLabTest` / `BootWebMvcObservabilityLabTest`
- `BootWebMvcErrorViewLabTest` / `BootWebMvcViewLabTest`

## 最小可运行实验（Lab）

- Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
- 运行命令：`mvn -pl :spring-boot-web-mvc test`（或在 IDE 直接运行上面的测试类）


## 验证目标
1. 能说清一次请求在 MVC 中的关键阶段：handler mapping → argument resolve/binding → validation → exception → response
2. 能写出“可复现”的错误塑形与异常处理策略，并用测试断言锁定行为
3. 能解释 Filter/Interceptor 的顺序与影响范围
4. 能解释 406/415 与 HttpMessageConverter 的关系，并能用 `resolvedException` 快速定位分支
5. 能用测试复现真实 HTTP 场景：CORS 预检、上传下载、静态资源
6. 能写出最小 async/SSE 示例，并避免 flaky（asyncDispatch、有限事件）

## 如何跑实验
- 运行本模块测试：`mvn -pl :spring-boot-web-mvc test`

## 对应 Lab（可运行）

- `BootWebMvcLabTest`
- `BootWebMvcSpringBootLabTest`
- `BootWebMvcInternalsLabTest`
- `BootWebMvcTraceLabTest`
- `BootWebMvcContractJacksonLabTest`
- `BootWebMvcAdviceOrderLabTest`
- `BootWebMvcRealWorldHttpLabTest`
- `BootWebMvcAsyncSseLabTest`
- `BootWebMvcTestingDebuggingLabTest`
- `BootWebMvcExerciseTest`

## 常见坑与边界

直接把坑点当作“清单”执行（先复现、再断点、最后修复）：

- 400 不等于校验失败：先用 `resolvedException` 固定异常类型
- 401/403 多发生在 FilterChain：先怀疑 Security，而不是 controller
- 406/415 先看 header 与 mapping 约束，不要先改业务逻辑
- async 必须 asyncDispatch 才算闭环；Interceptor 回调会出现“两次 dispatch”

配套清单见：`appendix-common-pitfalls.md`

## 阅读顺序
1. [01-validation-and-error-shaping](binding-validation-validation-and-error-shaping.md)
2. [02-exception-handling](exception-resolvers-exception-handling.md)
3. [03-binding-and-converters](binding-validation-binding-and-converters.md)
4. [04-interceptor-and-filter-ordering](handleradapter-interceptor-interceptor-and-filter-ordering.md)
5. [01-knowledge-map](guide-knowledge-map.md)
6. [01-dispatcherservlet-call-chain](dispatcherservlet-call-chain.md)
7. [02-argument-resolver-and-binder](argument-resolver-and-binder.md)
8. [03-message-converters-and-return-values](return-value-view-message-converters-and-return-values.md)
9. [01-content-negotiation-406-415](message-conversion-content-negotiation-406-415.md)
10. [02-jackson-objectmapper-controls](message-conversion-jackson-objectmapper-controls.md)
11. [03-error-contract-hardening](exception-resolvers-error-contract-hardening.md)
12. [01-cors-preflight](real-world-http-cors-preflight.md)
13. [02-multipart-upload](real-world-http-multipart-upload.md)
14. [03-download-and-streaming](real-world-http-download-and-streaming.md)
15. [04-static-resources-and-cache](real-world-http-static-resources-and-cache.md)
16. [01-servlet-async-and-testing](async-sse-servlet-async-and-testing.md)
17. [02-sse-emitter](async-sse-sse-emitter.md)
18. [01-webmvc-testing-and-troubleshooting](testing-observability-webmvc-testing-and-troubleshooting.md)
19. [01-security-filterchain-and-mvc](filterchain-security-security-filterchain-and-mvc.md)
20. [02-observability-and-metrics](testing-observability-observability-and-metrics.md)
21. [01-thymeleaf-and-view-resolver](return-value-view-thymeleaf-and-view-resolver.md)
22. [02-form-binding-validation-prg](return-value-view-form-binding-validation-prg.md)
23. [03-error-pages-and-content-negotiation](boot-error-error-pages-and-content-negotiation.md)
24. [90-common-pitfalls](appendix-common-pitfalls.md)
25. [99-self-check](appendix-self-check.md)

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
- Lab：`BootWebMvcInternalsLabTest`
- Lab：`BootWebMvcTraceLabTest`
- Lab：`BootWebMvcContractJacksonLabTest`
- Lab：`BootWebMvcRealWorldHttpLabTest`
- Lab：`BootWebMvcAsyncSseLabTest`
- Lab：`BootWebMvcSecurityLabTest`
- Lab：`BootWebMvcObservabilityLabTest`
- Exercise：`BootWebMvcExerciseTest`

上一章：[01. 主线时间线：Spring Boot Web MVC](guide-mainline-timeline.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 知识地图（Web MVC 深挖地图）](guide-knowledge-map.md)
<!-- BOOKIFY:END -->
