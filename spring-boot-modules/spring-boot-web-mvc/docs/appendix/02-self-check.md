# 99 自检：Spring Boot Web MVC
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘出口）"

    - 主线入口：`BootWebMvcBookMatrixLabTest`
    - 分支入口：`BootWebMvcErrorBranchMatrixLabTest`（400/406/415）
    - 推荐先跑：`BootWebMvcLabTest` / `BootWebMvcBindingDeepDiveLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（Web MVC）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章是「99 - Self Check（springboot-web-mvc）」的自检与复盘页：不引入新概念，而是把关键分支以问题的形式回放。
建议先运行 `BootWebMvcErrorViewLabTest`（或本章列出的 Matrix/Lab 入口），再按题目逐一回到对应的证据链。

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- 错误分支矩阵 400/406/415：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`
- 配套资料：[`断点地图 `](../part-00-guide/06-breakpoint-map.md) / [` 关键分支矩阵`](../part-00-guide/04-branch-decision-matrix.md)


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcErrorViewLabTest` / `BootWebMvcLabTest`

## 自测题
1. `@Valid` 触发校验发生在 MVC 的哪个阶段？异常如何被塑形为统一响应？
2. `@ControllerAdvice` 与 `@ExceptionHandler` 的匹配规则是什么？
3. Filter 与 Interceptor 的执行顺序与作用域差异是什么？
4. `@RequestBody` 与 `@ModelAttribute` 分别走哪条路径？它们的“校验失败”常见异常类型分别是什么？
5. 406 与 415 的本质差异是什么？会在哪两个断点上分别观察 read/write 的分支？
6. 接入 Spring Security 后，401 与 403 通常发生在 MVC 的哪个位置之前/之后？如何用 `handler/resolvedException` 证明“没进入 DispatcherServlet”？CSRF 缺失导致的 403 如何在测试里稳定复现？
7. ETag/If-None-Match 触发 304 的条件是什么？为什么 304 通常不返回响应体？
8. async（Callable/DeferredResult）为什么会触发两次 dispatch？Interceptor 的回调为什么“少一截”？

## 如何把自测题变成“可验证事实”（证据链指引）

建议对每一题都做到“三段式”：
1) **现象**：先跑 Lab 固定状态码/响应体/headers/asyncStarted
2) **证据**：拿到 `resolvedException` / handler / event sequence
3) **断点**：在关键入口打断点观察分支（不要先改业务代码）

下面给出每题的最小证据链（Lab + 断点建议）：

| 题号 | 最小可运行证据链（Lab/Test） | 建议断点（源码入口） | 观察点 |
| --- | --- | --- | --- |
| 1 | `BootWebMvcLabTest` / `BootWebMvcBindingDeepDiveLabTest` | `DataBinder#validate` / `ExceptionHandlerExceptionResolver` | 400 + message/fieldErrors |
| 2 | `BootWebMvcAdviceMatchingLabTest` / `BootWebMvcAdviceOrderLabTest` | `ControllerAdviceBean#isApplicableToBeanType` / `ExceptionHandlerExceptionResolver#doResolveHandlerMethodException` | 哪个 advice 生效（message）+ 为什么（selector/order） |
| 3 | `BootWebMvcTraceLabTest` | `DispatcherServlet#doDispatch` / `HandlerExecutionChain#applyPreHandle` | events 顺序（REQUEST vs ASYNC） |
| 4 | `BootWebMvcLabTest`（@RequestBody）/ `BootWebMvcBindingDeepDiveLabTest`（@ModelAttribute） | `RequestResponseBodyMethodProcessor#resolveArgument` / `ServletModelAttributeMethodProcessor#resolveArgument` | exception 类型差异 |
| 5 | `BootWebMvcTestingDebuggingLabTest` / `BootWebMvcMessageConverterTraceLabTest` | `AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters` / `AbstractMessageConverterMethodProcessor#writeWithMessageConverters` | 415 vs 406（read vs write）+ selectedConverterType/selectedContentType |
| 6 | `BootWebMvcSecurityLabTest` / `BootWebMvcSecurityVsMvcExceptionBoundaryLabTest` | `FilterChainProxy#doFilterInternal` / `CsrfFilter#doFilterInternal` | 401 vs 403（发生在 MVC 之前）+ handler/resolvedException 证据链 |
| 7 | `BootWebMvcRealWorldHttpLabTest` | `ServletWebRequest#checkNotModified` / `ShallowEtagHeaderFilter` | ETag/Last-Modified/304 |
| 8 | `BootWebMvcAsyncSseLabTest` / `BootWebMvcTraceLabTest` | `WebAsyncManager#startDeferredResultProcessing` / `AsyncHandlerInterceptor#afterConcurrentHandlingStarted` | asyncStarted + 二次 dispatch |

## 最小可运行实验（Lab）

- 本章按“题目 → 证据链”的方式引用 Labs（建议优先跑它们）：
- Lab：`BootWebMvcLabTest`
- Lab：`BootWebMvcBindingDeepDiveLabTest`
- Lab：`BootWebMvcTestingDebuggingLabTest`
- Lab：`BootWebMvcMessageConverterTraceLabTest`
- Lab：`BootWebMvcTraceLabTest`
- Lab：`BootWebMvcAdviceOrderLabTest`
- Lab：`BootWebMvcAdviceMatchingLabTest`
- Lab：`BootWebMvcRealWorldHttpLabTest`
- Lab：`BootWebMvcSecurityLabTest` / `BootWebMvcObservabilityLabTest`
- 建议命令：`mvn -pl :spring-boot-web-mvc test`（或在 IDE 直接运行上面的测试类）


## 对应 Exercise（可运行）

- `BootWebMvcExerciseTest`

## 常见坑索引（本页不重复坑正文）

- 建议对照：[`01-common-pitfalls.md`](01-common-pitfalls.md)

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcErrorViewLabTest` / `BootWebMvcLabTest`
- Lab：`BootWebMvcBindingDeepDiveLabTest`
- Lab：`BootWebMvcTestingDebuggingLabTest`
- Lab：`BootWebMvcMessageConverterTraceLabTest`
- Lab：`BootWebMvcTraceLabTest`
- Lab：`BootWebMvcAdviceOrderLabTest`
- Lab：`BootWebMvcAdviceMatchingLabTest`
- Lab：`BootWebMvcRealWorldHttpLabTest`
- Lab：`BootWebMvcSecurityLabTest` / `BootWebMvcObservabilityLabTest`
- Exercise：`BootWebMvcExerciseTest`

上一章：[appendix/90-common-pitfalls.md](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
