# 04. 关键分支矩阵（Web MVC Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Web MVC Branch Decision Matrix）
    - 怎么使用：把 Web MVC 最常见的错误分支（400/406/415）与对应的异常类型/收敛点写成矩阵表，并提供可复现入口（Branch Matrix Test）。
    - 原理：错误分支多数发生在 controller 前后：argument resolver/binder/message converter/exception resolvers。
    - 源码入口：`DispatcherServlet#doDispatch` / `HandlerMethodArgumentResolverComposite#resolveArgument` / `AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`
    - 推荐 Lab：`BootWebMvcErrorBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 00 - Deep Dive Guide（springboot-web-mvc）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 知识地图（Web MVC Deep Dive Map）](05-knowledge-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本页目标：给你一张“从 status 回到根因”的最小矩阵表——每一行都对应一个：

- 可复现入口（测试方法）
- 可观察证据（resolvedException 类型）
- 可定位断点（分支发生点）

## 关键分支矩阵（最小集合：HTTP 错误分支）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 415 Unsupported Media Type | Content-Type 不支持 | 415 + `HttpMediaTypeNotSupportedException` | `BootWebMvcErrorBranchMatrixLabTest#branch415_whenContentTypeIsNotSupported` | resolvedException 类型 |
| 406 Not Acceptable | Accept 不支持 | 406 + `HttpMediaTypeNotAcceptableException` | `BootWebMvcErrorBranchMatrixLabTest#branch406_whenAcceptIsNotSupported` | selectedMediaType |
| 400 Malformed JSON | JSON 解析失败 | 400 + `HttpMessageNotReadableException` | `BootWebMvcErrorBranchMatrixLabTest#branch400_whenJsonIsMalformed` | converter/readWithMessageConverters |
| 400 Validation | Bean Validation 失败 | 400 + `MethodArgumentNotValidException` | `BootWebMvcErrorBranchMatrixLabTest#branch400_whenValidationFails` | BindingResult errors |
| 400 Type Mismatch | 参数类型不匹配 | 400 + `MethodArgumentTypeMismatchException` | `BootWebMvcErrorBranchMatrixLabTest#branch400_whenRequestParamTypeMismatch` | argument resolver/binder |

## 推荐运行命令

- `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`

## 推荐断点（从错误分支回到机制）

- 入口：`DispatcherServlet#doDispatch`
- body 分支：`AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`
- binder 分支：`WebDataBinder#bind` / `DataBinder#validate`
- 异常收敛：`DispatcherServlet#processHandlerException` / `HandlerExceptionResolverComposite#resolveException`

## 与断点地图/Playbook 的关系

- 断点地图（总入口）：[`06-breakpoint-map.md`](06-breakpoint-map.md)
- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

- 小结：错误分支多数发生在 controller 前后：argument resolver/binder/message converter/exception resolvers。
- 下一章：[第 65 章：01：知识地图（Web MVC Deep Dive Map）](05-knowledge-map.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootWebMvcErrorBranchMatrixLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/01-knowledge-map.md](05-knowledge-map.md)

<!-- BOOKIFY:END -->

