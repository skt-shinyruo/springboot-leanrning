# 03. HttpMessageConverter 与返回值处理（序列化发生在哪里）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕03：HttpMessageConverter 与返回值处理（序列化发生在哪里）展开，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcContractJacksonLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 表单提交闭环（@ModelAttribute / BindingResult / 校验回显 / PRG）](return-value-view-form-binding-validation-prg.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[02. 统一异常处理（ControllerAdvice）与“坏输入”](exception-resolvers-exception-handling.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「03：HttpMessageConverter 与返回值处理（序列化发生在哪里）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `BootWebMvcContractJacksonLabTest`（或文末“对应实验/测试”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcContractJacksonLabTest`

## 机制主线

- 把证据链连接到 Part 04：用 `BootWebMvcContractJacksonLabTest` 固定 406/415 的可复现用例，再回到断点看 converter 选择分支。
- 本模块还提供了一个“排障视角”的 Lab：`BootWebMvcTestingDebuggingLabTest`（用 `resolvedException` 固定分支入口）。
- 本章新增一条“可观测证据链”：用 `ResponseBodyAdvice` 把 `selectedConverterType/selectedContentType` 写入响应头，直接回答“到底选了哪个 converter”。

## 源码与断点

断点入口：
- `AbstractMessageConverterMethodProcessor#writeWithMessageConverters`
- `RequestResponseBodyMethodProcessor#resolveArgument`
- `org.springframework.web.accept.ContentNegotiationManager#resolveMediaTypes`
- `org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite#handleReturnValue`（返回值处理链入口）
- `org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor#beforeBodyWrite`（ResponseBodyAdvice 链入口）

## 关键分支：read 与 write 的差异

当看到“406/415”，先判断它属于 read 还是 write：

- **read（入站）**：`@RequestBody` 读取请求体 → 依赖 `AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`
  - 典型失败：415（找不到能读该 `Content-Type` 的 converter）
- **write（出站）**：`@ResponseBody` 写回响应体 → 依赖 `AbstractMessageConverterMethodProcessor#writeWithMessageConverters`
  - 典型失败：406（找不到能写出 `Accept` 的 converter）

这也是为什么排障时应该优先检查 header 与 method mapping 约束，而不是先改业务逻辑。

## 真正要理解的“两个链”

### 1) ReturnValueHandlers（返回值处理链）

在 `@ResponseBody` 场景下，最常见的 handler 是：
- `RequestResponseBodyMethodProcessor`：负责把返回值交给 HttpMessageConverter 写回 body

它通常发生在：
- `HandlerMethodReturnValueHandlerComposite#handleReturnValue`（链入口）
- `AbstractMessageConverterMethodProcessor#writeWithMessageConverters`（真正写回）

### 2) ContentNegotiation（内容协商）

内容协商不是“只看 Accept”：
- `Accept`：客户端希望的响应格式（write）
- `Content-Type`：请求体实际格式（read）
- `produces/consumes`：在 mapping 上写的约束（会直接影响匹配与异常类型）

排障动作：当 406/415 出现时，优先把下面三件事写进证据链：
1. 请求头（Accept/Content-Type）
2. handler mapping 约束（produces/consumes）
3. resolvedException（异常类型就是分支位置）

## 最小可运行实验（Lab）

- Lab：`BootWebMvcContractJacksonLabTest`
- Lab：`BootWebMvcTestingDebuggingLabTest`
- Lab：`BootWebMvcMessageConverterTraceLabTest`（converter 选择可观测：响应头证据链）

## 补充：如何把 “selectedConverterType/selectedContentType” 变成证据

断点能解释“为什么”，但排障往往还需要一条可复现证据链来回答“选的结果是什么”。

Spring MVC 在 `ResponseBodyAdvice#beforeBodyWrite(...)` 提供了两个非常关键的入参：
- `selectedConverterType`：最终选中的 `HttpMessageConverter` 类型
- `selectedContentType`：最终协商出的响应 `Content-Type`

本模块把它落成了可运行实验：
- `MessageConverterTraceAdvice`：仅对 `/api/advanced/message-converters/**` 写入响应头
- endpoints：String/JSON/bytes/strict media type 四种返回值对照
- Lab：`BootWebMvcMessageConverterTraceLabTest` 固定断言（不需要猜、可回归）

## 常见坑与边界

- “只想对某个自定义 media type 严格校验”，不要全局改默认 ObjectMapper；更安全的做法是 **新增一个只支持该 media type 的 converter**。

## 小结与下一章

- 下一章进入 Part 04：专门用 406/415 与 Jackson 严格模式，把“契约可控”做成工程闭环。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootWebMvcContractJacksonLabTest`
- Lab：`BootWebMvcTestingDebuggingLabTest`
- Lab：`BootWebMvcMessageConverterTraceLabTest`

上一章：[02. 表单提交闭环（@ModelAttribute / BindingResult / 校验回显 / PRG）](return-value-view-form-binding-validation-prg.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[02. 统一异常处理（ControllerAdvice）与“坏输入”](exception-resolvers-exception-handling.md)
<!-- BOOKIFY:END -->
