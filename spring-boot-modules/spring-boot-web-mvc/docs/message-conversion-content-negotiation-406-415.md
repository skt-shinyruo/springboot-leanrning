# 01. Content Negotiation（406/415：Accept/Content-Type/produces/consumes）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕01：Content Negotiation（406/415：Accept/Content-Type/produces/consumes）展开，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcErrorBranchMatrixLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 校验（Validation）与错误响应形状（Error Shape）](binding-validation-validation-and-error-shaping.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. Jackson ObjectMapper 可控（严格模式、未知字段、时间）](message-conversion-jackson-objectmapper-controls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 目标：让 406/415 变成“可预期、可复现、可排障”的问题，而不是靠猜。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcErrorBranchMatrixLabTest`（同一视角对照 400/406/415 分支）
    - Lab：`BootWebMvcContractJacksonLabTest`（聚焦 406/415 + strict JSON）

## 机制主线

- 本章推荐先跑 `BootWebMvcErrorBranchMatrixLabTest` 固定三类分支（同一视角对照 400/406/415）：
  - `/api/users`：malformed JSON → 400（`HttpMessageNotReadableException`）
  - `/api/advanced/contract/echo`：错误 `Content-Type` → 415
  - `/api/advanced/contract/ping`：错误 `Accept` → 406
- 然后用 `BootWebMvcContractJacksonLabTest` 把“契约可控”做成可回归证据（strict media type / unknown fields）。
- 排障时如果想“把猜测变成证据”，可以对照 `BootWebMvcTestingDebuggingLabTest` 的写法：
  - 直接拿到 `resolvedException`，最快锁定“到底走的是 406 还是 415（或 400）”的分支入口。

## 源码与断点

建议断点：
- `RequestMappingHandlerMapping#handleMatch`
- `org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping#handleNoMatch`（映射阶段就不匹配时）
- `AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`
- `AbstractMessageConverterMethodProcessor#writeWithMessageConverters`

## 应当怎么定位 406/415（按优先级）

> 目标：别在 controller 里盲改代码，而是沿着链路确认“到底在哪一段失败”。

1. **先确认映射约束**：controller 方法的 `produces/consumes` 是否与 `Accept/Content-Type` 对齐？
2. **再确认 converter**：
   - 415（read 失败）：能否找到“能读该 Content-Type”的 converter？
   - 406（write 失败）：能否找到“能写出 Accept 的格式”的 converter？
3. **用证据锁定分支**：MockMvc 的 `resolvedException`（见 `BootWebMvcErrorBranchMatrixLabTest` / `BootWebMvcTestingDebuggingLabTest`）是最快的“分支定位器”。

## 最小可运行实验（Lab）

- Lab：`BootWebMvcErrorBranchMatrixLabTest`
- Lab：`BootWebMvcContractJacksonLabTest`
- Lab：`BootWebMvcTestingDebuggingLabTest`

## 常见坑与边界

- 在 Postman/curl 里不小心带了 `Accept: */*` 或 `Content-Type` 缺失/错误，容易误以为“后端逻辑坏了”，但实际上是契约不匹配。

## 小结与下一章

- 下一章进入 Jackson 可控：如何“只对某类请求严格”，避免全局影响。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcErrorBranchMatrixLabTest`
- Lab：`BootWebMvcContractJacksonLabTest`
- Lab：`BootWebMvcTestingDebuggingLabTest`

上一章：[01. 校验（Validation）与错误响应形状（Error Shape）](binding-validation-validation-and-error-shaping.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. Jackson ObjectMapper 可控（严格模式、未知字段、时间）](message-conversion-jackson-objectmapper-controls.md)
<!-- BOOKIFY:END -->
