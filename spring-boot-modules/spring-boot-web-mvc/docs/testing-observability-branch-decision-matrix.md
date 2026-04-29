# 04. 关键分支矩阵（Web MVC）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵（Web MVC）展开，主线可以概括为：错误分支多数发生在 controller 前后：argument resolver/binder/message converter/exception resolvers。

    把 Web MVC 最常见的错误分支（400/406/415）与对应的异常类型/收敛点写成矩阵表，并提供可复现入口（Branch Matrix Test）。

    对照入口：`BootWebMvcErrorBranchMatrixLabTest`。需要下探源码时，可以从 `DispatcherServlet#doDispatch` / `HandlerMethodArgumentResolverComposite#resolveArgument` / `AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[06. 断点地图（Part 01）](testing-observability-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. Security FilterChain 与 Web MVC（401/403/CSRF 在哪发生）](filterchain-security-security-filterchain-and-mvc.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本页目标：给一张“从 status 回到根因”的最小矩阵表——每一行都对应一个：

- 可复现入口（测试方法）
- 可观察证据（resolvedException 类型）
- 可定位断点（分支发生点）

## 关键分支矩阵（最小集合：HTTP 错误分支）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点 |
|---|---|---|---|---|
| 415 Unsupported Media Type | Content-Type 不支持 | 415 + `HttpMediaTypeNotSupportedException` | `BootWebMvcErrorBranchMatrixLabTest#branch415_whenContentTypeIsNotSupported` | resolvedException 类型 |
| 406 Not Acceptable | Accept 不支持 | 406 + `HttpMediaTypeNotAcceptableException` | `BootWebMvcErrorBranchMatrixLabTest#branch406_whenAcceptIsNotSupported` | selectedMediaType |
| 400 Malformed JSON | JSON 解析失败 | 400 + `HttpMessageNotReadableException` | `BootWebMvcErrorBranchMatrixLabTest#branch400_whenJsonIsMalformed` | converter/readWithMessageConverters |
| 400 Validation | Bean Validation 失败 | 400 + `MethodArgumentNotValidException` | `BootWebMvcErrorBranchMatrixLabTest#branch400_whenValidationFails` | BindingResult errors |
| 400 Type Mismatch | 参数类型不匹配 | 400 + `MethodArgumentTypeMismatchException` | `BootWebMvcErrorBranchMatrixLabTest#branch400_whenRequestParamTypeMismatch` | argument resolver/binder |

## 运行命令

- `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`

## 断点入口（从错误分支回到机制）

- 入口：`DispatcherServlet#doDispatch`
- body 分支：`AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`
- binder 分支：`WebDataBinder#bind` / `DataBinder#validate`
- 异常收敛：`DispatcherServlet#processHandlerException` / `HandlerExceptionResolverComposite#resolveException`

## 与断点地图/Playbook 的关系

- 断点地图（总入口）：[`06-breakpoint-map.md`](testing-observability-breakpoint-map.md)
- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

错误分支多数发生在 controller 前后：argument resolver/binder/message converter/exception resolvers。

下一章见：[01：知识地图（Web MVC 深挖地图）](guide-knowledge-map.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootWebMvcErrorBranchMatrixLabTest`

上一章：[06. 断点地图（Part 01）](testing-observability-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. Security FilterChain 与 Web MVC（401/403/CSRF 在哪发生）](filterchain-security-security-filterchain-and-mvc.md)
<!-- BOOKIFY:END -->
