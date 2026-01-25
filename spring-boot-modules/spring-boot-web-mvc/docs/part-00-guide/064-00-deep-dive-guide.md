# 第 64 章：00 - Deep Dive Guide（springboot-web-mvc）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Deep Dive Guide（springboot-web-mvc）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。
    - 原理：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver`
    - 推荐 Lab：`BootWebMvcLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 63 章：主线时间线：Spring Boot Web MVC](063-03-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 65 章：01：知识地图（Web MVC Deep Dive Map）](065-01-knowledge-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**00 - Deep Dive Guide（springboot-web-mvc）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

    ### 先跑哪个 Lab（3 分钟开跑）

    先跑通一个最小入口，保证环境与依赖没问题：

    ```bash
    mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcLabTest#pingEndpointReturnsPong test
    ```

    然后把断点放在“分支发生处”（而不是满屏日志）：
    - 断点地图（Part 01 Debugger Pack）：`part-00-guide/066-02-breakpoint-map.md`


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
   - 你看到的状态码/错误体往往来自 resolver 链，而不是业务代码的 if/else

6. **真实 HTTP 分支（CORS/multipart/静态资源/条件请求/ETag）**
   - 典型分支：CORS 预检、上传下载 header、静态资源 304

7. **Async（Callable/DeferredResult/SSE）**
   - 典型分支：asyncStarted → asyncDispatch；Interceptor lifecycle 两阶段回调

> 推荐先读知识地图（01），再按“现象 → 证据（测试）→ 断点（源码）”推进。

## 两条课程路径（你说的“源码阅读课”与“工程落地课”）

### 路径 1：源码阅读课（以调用链与关键分支为中心）

目标：能把断点打在“分支发生的地方”，并能解释“为什么是这个状态码/这个错误体”。

推荐顺序：
1. 知识地图：`part-00-guide/065-01-knowledge-map.md`
2. DispatcherServlet 主链路：`part-03-web-mvc-internals/067-01-dispatcherservlet-call-chain.md`
3. resolver/binder：`part-03-web-mvc-internals/02-argument-resolver-and-binder.md`
4. converter/return value：`part-03-web-mvc-internals/068-03-message-converters-and-return-values.md`
5. exception resolvers：`part-03-web-mvc-internals/069-04-exception-resolvers-and-error-flow.md`

证据链（先跑再断点）：
- `BootWebMvcInternalsLabTest`（自定义 ArgumentResolver）
- `BootWebMvcTraceLabTest`（Filter/Interceptor + async lifecycle）
- `BootWebMvcTestingDebuggingLabTest`（resolvedException 固定 406/415）

### 路径 2：工程落地课（以契约可控与可回归为中心）

目标：把“常见分支”做成可回归的工程闭环：错误体、契约、缓存、上传下载、async、security、观测。

推荐顺序：
1. 校验与错误塑形：`part-01-web-mvc/01-validation-and-error-shaping.md`
2. 统一异常处理：`part-01-web-mvc/02-exception-handling.md`
3. 绑定与边界：`part-01-web-mvc/03-binding-and-converters.md`
4. Contract/Jackson/ProblemDetail：`part-04-rest-contract/*`
5. Real World HTTP：`part-05-real-world-http/*`
6. Async/SSE：`part-06-async-sse/*`
7. Security/Observability：`part-08-security-observability/*`
8. View MVC：`part-02-view-mvc/*`

证据链（建议按顺序跑）：
- `BootWebMvcLabTest` / `BootWebMvcBindingDeepDiveLabTest`
- `BootWebMvcContractJacksonLabTest` / `BootWebMvcProblemDetailLabTest`
- `BootWebMvcRealWorldHttpLabTest`
- `BootWebMvcAsyncSseLabTest`
- `BootWebMvcSecurityLabTest` / `BootWebMvcObservabilityLabTest`
- `BootWebMvcErrorViewLabTest` / `BootWebMvcViewLabTest`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
- 建议命令：`mvn -pl :spring-boot-web-mvc test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 推荐学习目标
1. 能说清一次请求在 MVC 中的关键阶段：handler mapping → argument resolve/binding → validation → exception → response
2. 能写出“可复现”的错误塑形与异常处理策略，并用测试断言锁住行为
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

建议直接把坑点当作“清单”执行（先复现、再断点、最后修复）：

- 400 不等于校验失败：先用 `resolvedException` 固定异常类型
- 401/403 多发生在 FilterChain：先怀疑 Security，而不是 controller
- 406/415 先看 header 与 mapping 约束，不要先改业务逻辑
- async 必须 asyncDispatch 才算闭环；Interceptor 回调会出现“两次 dispatch”

配套清单见：`appendix/90-common-pitfalls.md`

## 推荐阅读顺序
1. [01-validation-and-error-shaping](../part-01-web-mvc/071-01-validation-and-error-shaping.md)
2. [02-exception-handling](../part-01-web-mvc/072-02-exception-handling.md)
3. [03-binding-and-converters](../part-01-web-mvc/073-03-binding-and-converters.md)
4. [04-interceptor-and-filter-ordering](../part-01-web-mvc/074-04-interceptor-and-filter-ordering.md)
5. [01-knowledge-map](065-01-knowledge-map.md)
6. [01-dispatcherservlet-call-chain](../part-03-web-mvc-internals/067-01-dispatcherservlet-call-chain.md)
7. [02-argument-resolver-and-binder](../part-03-web-mvc-internals/02-argument-resolver-and-binder.md)
8. [03-message-converters-and-return-values](../part-03-web-mvc-internals/068-03-message-converters-and-return-values.md)
9. [01-content-negotiation-406-415](../part-04-rest-contract/077-01-content-negotiation-406-415.md)
10. [02-jackson-objectmapper-controls](../part-04-rest-contract/02-jackson-objectmapper-controls.md)
11. [03-error-contract-hardening](../part-04-rest-contract/03-error-contract-hardening.md)
12. [01-cors-preflight](../part-05-real-world-http/078-01-cors-preflight.md)
13. [02-multipart-upload](../part-05-real-world-http/02-multipart-upload.md)
14. [03-download-and-streaming](../part-05-real-world-http/03-download-and-streaming.md)
15. [04-static-resources-and-cache](../part-05-real-world-http/04-static-resources-and-cache.md)
16. [01-servlet-async-and-testing](../part-06-async-sse/079-01-servlet-async-and-testing.md)
17. [02-sse-emitter](../part-06-async-sse/02-sse-emitter.md)
18. [01-webmvc-testing-and-troubleshooting](../part-07-testing-debugging/080-01-webmvc-testing-and-troubleshooting.md)
19. [01-security-filterchain-and-mvc](../part-08-security-observability/081-01-security-filterchain-and-mvc.md)
20. [02-observability-and-metrics](../part-08-security-observability/02-observability-and-metrics.md)
21. [01-thymeleaf-and-view-resolver](../part-02-view-mvc/076-01-thymeleaf-and-view-resolver.md)
22. [02-form-binding-validation-prg](../part-02-view-mvc/02-form-binding-validation-prg.md)
23. [03-error-pages-and-content-negotiation](../part-02-view-mvc/03-error-pages-and-content-negotiation.md)
24. [90-common-pitfalls](../appendix/082-90-common-pitfalls.md)
25. [99-self-check](../appendix/083-99-self-check.md)

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
- Lab：`BootWebMvcInternalsLabTest`
- Lab：`BootWebMvcTraceLabTest`
- Lab：`BootWebMvcContractJacksonLabTest`
- Lab：`BootWebMvcRealWorldHttpLabTest`
- Lab：`BootWebMvcAsyncSseLabTest`
- Lab：`BootWebMvcSecurityLabTest`
- Lab：`BootWebMvcObservabilityLabTest`
- Exercise：`BootWebMvcExerciseTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/065-01-knowledge-map.md](065-01-knowledge-map.md)

<!-- BOOKIFY:END -->
