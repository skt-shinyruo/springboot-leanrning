# 02. ArgumentResolver 与 Binder（参数从哪来、校验在哪触发）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕「ArgumentResolver 与 Binder」展开，目标是把“controller 方法参数到底从哪来”讲成可调试的事实：每一个参数都会被 `HandlerMethodArgumentResolver` 解析；解析后要么走 binder（绑定/转换/校验），要么走 message converter（读 body），最后才进入 controller 方法体。

    读完应当能回答：400 到底是缺参/类型不匹配、绑定失败、JSON 解析失败，还是校验失败？以及每一种分支分别在哪个断点上出现。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. Interceptor 与 Filter：入口在哪里、顺序怎么理解](handleradapter-interceptor-interceptor-and-filter-ordering.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. 请求绑定（Binding）与 Converter/Formatter](binding-validation-binding-and-converters.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

在 Web MVC 主线里，“controller 方法执行”之前最容易误判的阶段就是入参解析：

- 为什么我写了 DTO，却拿到 `null` / 400？
- 为什么同样是 400，有时是 `malformed_json`，有时是 `validation_failed`？
- 为什么加了 `@Valid` 仍然不触发校验？

这些问题在机制上都可以收敛为一句话：**参数先被解析（ArgumentResolver），再决定走哪条通道（binder vs converter），最后才轮到 controller**。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcInternalsLabTest`（自定义参数解析：`@ClientIp`）
    - Lab：`BootWebMvcErrorBranchMatrixLabTest`（400/406/415 分支矩阵）
    - Lab：`BootWebMvcBindingDeepDiveLabTest`（binder/method validation 证据链）

## 关键对象（Key Objects）

### 1) 参数解析总线：ArgumentResolver

- `org.springframework.web.method.support.HandlerMethodArgumentResolver`：解析单个参数的策略接口
- `org.springframework.web.method.support.HandlerMethodArgumentResolverComposite#resolveArgument`：总入口（调试时最值钱的断点）

### 2) 两条最关键的“入参通道”

**通道 A：`@RequestBody`（message converter：读 body）**

- `RequestResponseBodyMethodProcessor#resolveArgument`
- `AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`

**通道 B：`@ModelAttribute` / `@RequestParam`（binder：绑 + 转 +（可选）校验）**

- `ServletModelAttributeMethodProcessor#resolveArgument`（`@ModelAttribute`）
- `org.springframework.web.bind.WebDataBinder#bind`
- `org.springframework.validation.DataBinder#validate`

## 扩展点（Extension Points）

本模块重点演示三类工程里最常用的扩展点：

1. **自定义参数注解 + 自定义 resolver**
   - 适用场景：从 request 中抽取横切信息（例如 client IP / trace id），避免 controller 方法体里散落解析逻辑
   - 本模块示例：`@ClientIp` + `ClientIpArgumentResolver`（对应 Lab：`BootWebMvcInternalsLabTest`）

2. **自定义 Converter/Formatter（把 String 变成你的类型）**
   - 适用场景：让 `@RequestParam`/`@PathVariable`/表单绑定能够直接得到业务类型
   - 对应章节：[`06-binding-validation/03-binding-and-converters.md`](binding-validation-binding-and-converters.md)

3. **`@InitBinder`：限定可绑定字段 + 留证据**
   - 适用场景：防 mass assignment（危险字段不允许从请求写入对象）
   - 关键证据：`BindingResult#getSuppressedFields()`（把“被阻止绑定字段”变成可观察事实）
   - 对应 Lab：`BootWebMvcBindingDeepDiveLabTest`

## 常见分支（状态码 / 异常）

### 1) 400：缺参 / 类型不匹配（resolver/binder 段）

- 缺参：`MissingServletRequestParameterException` / `MissingRequestHeaderException`
- 类型不匹配：`MethodArgumentTypeMismatchException`

这类 400 的共同特点是：**压根还没有进入业务方法体**，且通常可以在 `resolveArgument`/binder 断点上直接看到失败原因。

### 2) 400：校验失败（validation 段）

常见两种形态：

- `@RequestBody + @Valid`：典型异常 `MethodArgumentNotValidException`
- `@ModelAttribute + @Valid`：典型异常 `BindException`

校验失败不是“默认就会发生”，而是取决于：

- 是否写了 `@Valid`（对象校验）
- 是否启用了 `@Validated`（方法参数校验）
- 是否存在 `BindingResult`（有时不会抛异常，而是交给 controller 自己处理回显/塑形）

### 3) 400：malformed JSON（message converter read 段）

典型异常：`HttpMessageNotReadableException`

这类 400 的关键特征是：**body 读不到对象，因此也谈不上对象校验**。

### 4) 415 / 406：内容协商分支（read vs write）

- 415：read（`Content-Type`/`consumes`/converter 不匹配）
- 406：write（`Accept`/`produces`/converter 不匹配）

对应章节：[`07-message-conversion/01-content-negotiation-406-415.md`](message-conversion-content-negotiation-406-415.md)

## 证据链（断点 / 测试）

### 推荐断点（从“总入口”打起）

1. `DispatcherServlet#doDispatch`（证明是否进入 MVC）
2. `HandlerMethodArgumentResolverComposite#resolveArgument`（每一个参数的解析现场）
3. `WebDataBinder#bind` / `DataBinder#validate`（binder/校验现场）
4. `AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`（读 body 现场）

### 最小可运行实验（把分支固化）

- 参数解析阶段的可观察证据：`BootWebMvcInternalsLabTest`
- 400/406/415 分支矩阵：`BootWebMvcErrorBranchMatrixLabTest`
- binder + method validation：`BootWebMvcBindingDeepDiveLabTest`

延伸阅读（把本章放回主线）：

- DispatcherServlet 主链路：[`02-dispatcherservlet/01-dispatcherservlet-call-chain.md`](dispatcherservlet-call-chain.md)
- ExceptionResolvers 与错误流：[`10-exception-resolvers/04-exception-resolvers-and-error-flow.md`](exception-resolvers-and-error-flow.md)

## 小结

- controller 入参解析的总入口是 `HandlerMethodArgumentResolverComposite#resolveArgument`：先解析参数，再决定走 binder 还是 converter。
- 同样是 400，根因可能在不同阶段：缺参/类型不匹配、绑定失败、JSON 解析失败、校验失败；应优先用 `resolvedException` 固定分支再修。
