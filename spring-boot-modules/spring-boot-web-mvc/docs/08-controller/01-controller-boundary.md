# 01. Controller：边界、异常与契约的位置
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕「Controller 的边界与职责」展开，目标是回答一个工程上最常见的误判：**controller 并不是“所有问题的发生点”，它只是 MVC 主线里“业务方法执行”的那一段**。在它之前有选路、参数解析、绑定/校验与消息体读取；在它之后有返回值处理、内容协商与异常收敛。

    因此排障时更重要的是：把现象（status/响应体）映射回主线阶段，再决定应该改 controller、还是改 binder/converter/advice。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. Jackson ObjectMapper 可控（严格模式、未知字段、时间）](../07-message-conversion/02-jackson-objectmapper-controls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 传统 MVC 页面渲染入门（@Controller / ViewName / Thymeleaf）](../09-return-value-view/01-thymeleaf-and-view-resolver.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

学习 Web MVC 时，controller 往往是最容易“写出来”的部分，也是最容易被“误当成问题根因”的部分。

本章把 controller 放回主线里定位它的职责边界，并给出两条常用的工程结论：

1. controller 的核心职责是 **声明输入/输出契约**（参数从哪里来、如何校验、返回什么形态）；
2. controller 的工程价值不在于“写业务逻辑”，而在于 **把机制段（绑定/协商/异常）收敛成稳定契约**。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcLabTest`（最小 JSON API 闭环）
    - Lab：`BootWebMvcViewLabTest`（最小页面渲染/表单闭环）
    - Lab：`BootWebMvcBindingDeepDiveLabTest`（方法级校验与 binder 证据链）

## 关键对象（Key Objects）

- `@RestController` / `@Controller`
  - `@RestController`：返回值默认走 body（`@ResponseBody` 语义）
  - `@Controller`：返回值默认走 viewName / `ModelAndView`
- `org.springframework.web.method.HandlerMethod`
  - controller 方法的运行时“元数据载体”（参数、注解、返回值信息都在这里）
- `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter`
  - 将 `HandlerMethod` 变成一次可执行调用（串起 ArgumentResolver/Binder/ReturnValueHandler）

## 扩展点（Extension Points）

controller 本身的“扩展”不等于写更多方法，更常见的是这些边界手段：

1. **声明输入来源与边界**
   - `@RequestParam` / `@PathVariable`：参数从 URL/query 来
   - `@RequestBody`：参数从 body 来（读写依赖 `HttpMessageConverter`）
   - `@ModelAttribute`：从请求参数/表单绑定到对象（绑定依赖 `WebDataBinder`）

2. **声明校验触发点**
   - `@Valid`：对象级校验（常见在 DTO）
   - `@Validated`：方法级校验的触发开关（例如对 `@RequestParam` 上的 `@Min`）

3. **声明错误契约**
   - 不要在 controller 里 `try/catch` “吞掉一切异常”，而是把异常交给 resolver/advice 收敛
   - `@ControllerAdvice/@RestControllerAdvice`：统一错误响应形状（见 C10）

4. **声明返回形态**
   - REST：返回对象，由 `HttpMessageConverter` 序列化
   - View MVC：返回 viewName + Model，交给 `ViewResolver` 渲染

## 常见分支（状态码 / 异常）

### 1) “没进 controller”：前置阶段失败

常见现象：

- 401/403：大概率发生在 FilterChain（Security），controller 与 `@ControllerAdvice` 都不会执行（见 C1）
- 404/405：多发生在 HandlerMapping 阶段（选路失败/方法不支持，见 C3）
- 400：可能来自缺参/类型不匹配、绑定失败、JSON 解析失败、校验失败（需要用 `resolvedException` 分型，见 C14）

### 2) “进了 controller，但返回不符合预期”：返回值处理/内容协商分支

- 同样一个 controller 方法，在不同 `Accept` 下可能走不同 converter 或不同错误页（见 C9/C11）
- 406/415 的根因经常并不在 controller 方法体，而在协商/消息转换或 mapping 约束（见 C7/C9）

### 3) 方法级校验：`@Validated` 与参数约束

典型误区：

- 只在 DTO 上写约束，以为 `@RequestParam` 也会校验；
- 忘记在 controller 上加 `@Validated`，导致方法参数约束不生效。

## 证据链（断点 / 测试）

### 推荐断点

- `org.springframework.web.servlet.DispatcherServlet#doDispatch`（证明“是否进入 MVC”）
- `org.springframework.web.method.support.HandlerMethodArgumentResolverComposite#resolveArgument`（参数从哪来）
- `org.springframework.web.servlet.handler.HandlerExceptionResolverComposite#resolveException`（异常谁来翻译）

### 最小可运行入口（把边界变成断言）

- JSON API：`BootWebMvcLabTest`
- 页面渲染：`BootWebMvcViewLabTest`
- 方法级校验（`@Validated`）：`BootWebMvcBindingDeepDiveLabTest`（请求 `/api/advanced/binding/age-validated`）

延伸阅读（把 controller 放回主线）：

- DispatcherServlet 主链路：[`02-dispatcherservlet/01-dispatcherservlet-call-chain.md`](../02-dispatcherservlet/01-dispatcherservlet-call-chain.md)
- 异常收敛与错误形状：[`10-exception-resolvers/04-exception-resolvers-and-error-flow.md`](../10-exception-resolvers/04-exception-resolvers-and-error-flow.md)

## 小结

- controller 是主线里的“业务方法执行段”，它前后还有一大段机制链路；排障不应只盯 controller。
- controller 的核心价值是声明契约：输入来源、校验触发点、返回形态与错误形状。

