# 01. 校验（Validation）与错误响应形状（Error Shape）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：校验（Validation）与错误响应形状（Error Shape）展开，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. 请求绑定（Binding）与 Converter/Formatter](binding-validation-binding-and-converters.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. Content Negotiation（406/415：Accept/Content-Type/produces/consumes）](message-conversion-content-negotiation-406-415.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
    - 测试文件：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcSpringBootLabTest.java`

## 机制主线

把本章问题收敛成一句话：

- **校验发生在“参数解析之后、进入业务逻辑之前”；错误形状发生在“异常被 resolver 链收敛之后”。**

因此排障时不要只盯着“400”这一个信号，而是先问：**异常是什么、发生在哪个阶段、最终是谁把它翻译成了 `ApiError`**。

在本模块里可以按 3 段主线理解（也是调试顺序）：

1. **输入 → 对象**（message conversion / data binding）
2. **对象 → 约束检查**（validation：`@Valid` / `@Validated` 决定是否触发）
3. **异常 → 错误体**（ExceptionResolvers 命中 `GlobalExceptionHandler`，返回统一 `ApiError`）

关键分支（看到的异常类型与 `message` 不同）：

- `@RequestBody + @Valid`：失败抛 `MethodArgumentNotValidException` → `ApiError.message = "validation_failed"`
- `@ModelAttribute(+@Valid)`（表单/QueryString 走 binder）：失败抛 `BindException` → `ApiError.message = "validation_failed"`
- `@Validated + 参数约束（@RequestParam/@PathVariable/...）`：失败抛 `HandlerMethodValidationException`（或 `ConstraintViolationException`）→ `ApiError.message = "method_validation_failed"`

## 应当观察到的现象（What to observe）

把现象先钉在可断言入口上（直接运行这些方法级用例）：

- `BootWebMvcLabTest#returnsValidationErrorWhenRequestIsInvalid`（`POST /api/users`）：
  - 400
  - `message = "validation_failed"`
  - `fieldErrors.name` / `fieldErrors.email` 存在
- `BootWebMvcLabTest#createUserSucceedsWhenControllerOmitsValidAnnotation`（`POST /api/users/no-valid`）：
  - 同样的无效字段也会 200（证明：没有 `@Valid` 时约束不会自动触发）
- `BootWebMvcBindingDeepDiveLabTest#returnsValidationFailedWhenModelAttributeIsInvalid`（`POST /api/advanced/binding/form`）：
  - 400
  - `resolvedException` 是 `BindException`（binder 路径）
  - `message = "validation_failed"`
- `BootWebMvcBindingDeepDiveLabTest#returnsMethodValidationFailedWhenRequestParamViolatesConstraint`（`GET /api/advanced/binding/age-validated?age=-1`）：
  - 400
  - `message = "method_validation_failed"`
  - `fieldErrors.age` 存在

## 机制解释（Why）

在 Web MVC 里，“写了约束注解”并不等于“校验一定发生”，原因是校验是一个**可选分支**：

- 只有当参数解析器在解析参数时识别到 `@Valid` / `@Validated`，才会调用 `Validator`。
- 校验失败后，抛出的异常类型取决于走的是 body 路径还是 binder 路径（见上面的分支表）。

而“错误响应形状”则发生在另一个阶段：异常被 `DispatcherServlet#processHandlerException` 交给 resolver 链处理，最终由某个 resolver 产出响应。

在本模块中，这个“翻译器”是 `GlobalExceptionHandler`（`@RestControllerAdvice`）：

- DTO/表单校验失败 → `validation_failed`
- 方法参数校验失败 → `method_validation_failed`

所以目标应落在：遇到 400 时，先用测试/断点把异常类型固定下来，再谈“响应体怎么设计”，避免把不同根因揉成同一个错误体。

## 方法级校验（Method Validation）在 Controller 边界

除了“对象校验”（`@Valid` 校验 DTO），Web MVC 还可能在 controller 方法参数上触发 **方法级校验**：

- 典型写法：controller 上 `@Validated`，参数上写 `@Min/@NotBlank/...`
- 失败时常见异常（随 Spring 版本不同）：`HandlerMethodValidationException` 或 `ConstraintViolationException`
- 本模块把该分支也塑形成统一错误体：`ApiError(message = "method_validation_failed")`

可运行证据链（先运行再断点）：

- endpoint：`GET /api/advanced/binding/age-validated?age=-1`
- Lab：`BootWebMvcBindingDeepDiveLabTest#returnsMethodValidationFailedWhenRequestParamViolatesConstraint`

### 主链路（Call-chain sketch）

以 `@WebMvcTest + MockMvc` 为例，一条“@RequestBody + @Valid 失败 → 统一错误体”的主链路可以粗略理解为：

1. `MockMvc` 发起请求（测试入口）
2. `DispatcherServlet#doDispatch`（主入口，见 Part 03：DispatcherServlet call-chain）
3. `RequestMappingHandlerMapping#getHandlerInternal`（选路：找到 handler method）
4. `RequestMappingHandlerAdapter#handleInternal`（调用 handler）
5. `HandlerMethodArgumentResolverComposite#resolveArgument`（解析参数）
6. `RequestResponseBodyMethodProcessor#resolveArgument`（解析 `@RequestBody`）
7. `AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`（JSON → DTO）
8. `SpringValidatorAdapter#validate`（触发 Bean Validation）
9. 校验失败抛出 `MethodArgumentNotValidException`
10. `DispatcherServlet#processHandlerException` → `ExceptionHandlerExceptionResolver` 命中 `GlobalExceptionHandler`

对应的机制内核解释见（按顺序）：
- [DispatcherServlet 主链路（把选路/参数解析/异常串起来）](dispatcherservlet-call-chain.md)
- [ArgumentResolver 与 Binder（解析参数/绑定/校验触发点）](argument-resolver-and-binder.md)
- [ExceptionResolvers（异常从哪来、又被谁“翻译”成状态码）](exception-resolvers-and-error-flow.md)

## 最小可运行实验（Lab）

- Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
- 运行命令（方法级入口）：
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcLabTest#returnsValidationErrorWhenRequestIsInvalid test`


## Debug 路径

把本章当作 3 个“可验证的分支”来调试，会更快：

1. **校验是否触发（@Valid 分支）**
- 入口测试：`BootWebMvcLabTest#returnsValidationErrorWhenRequestIsInvalid`
- 运行命令：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcLabTest#returnsValidationErrorWhenRequestIsInvalid test`
- 对照用例（故意省略 `@Valid`，证明“写了注解但没触发”）：`BootWebMvcLabTest#createUserSucceedsWhenControllerOmitsValidAnnotation`
  - `RequestResponseBodyMethodProcessor#resolveArgument`
  - `SpringValidatorAdapter#validate`
  - DTO 上是否有约束注解（`CreateUserRequest`）
  - controller 入参是否带 `@Valid`
- 决定性分支：
  - **没有 `@Valid`**：约束注解不会自动生效（这是最常见的“注解已写但没有触发校验”的原因）

2. **异常如何被塑形成 ApiError（错误体分支）**
  - `ExceptionHandlerExceptionResolver#doResolveHandlerMethodException`
  - `GlobalExceptionHandler#handleValidation`
  - `ex.getBindingResult().getFieldErrors()`（字段错误列表）
  - 最终响应体的 `message/fieldErrors`

3. **同样是 400：到底是校验失败还是 JSON 解析失败（根因分支）**
- 配套跑：`BootWebMvcExceptionResolverChainLabTest`（用 `resolvedException` 固定根因）

进一步阅读（只做必要连接，不扩散篇幅）：

- Validation 模块（Bean Validation 机制本身）：[`spring-core-validation/README.md`](../../../spring-core-modules/spring-core-validation/README.md)
- Beans 模块（类型转换/值解析等底层支撑）：[`spring-core-beans/README.md`](../../../spring-core-modules/spring-core-beans/README.md)

## 常见坑与边界

本章聚焦 Web MVC 的“边界校验”：请求从 HTTP/JSON 进入 Controller 时，校验在哪里触发、失败后如何形成可控的错误响应。

1. **数据绑定（binding）**：JSON → DTO（例如 `CreateUserRequest`）
2. **边界校验（validation）**：`@Valid` 触发 Bean Validation，对 DTO 执行约束检查
3. **错误映射（error mapping）**：异常被 `@RestControllerAdvice` 捕获，转换成 `ApiError` 形状

最常见的三类误判：

- DTO 上有约束注解，但 controller 参数缺 `@Valid`：不会触发校验（见对照用例 `BootWebMvcLabTest#createUserSucceedsWhenControllerOmitsValidAnnotation`）。
- 只处理 `MethodArgumentNotValidException`：会漏掉 binder 路径的 `BindException`（表单 / `@ModelAttribute`）。
- 把方法级校验当成 DTO 校验：区分 `validation_failed` 与 `method_validation_failed`，否则客户端只能靠猜根因。

## 小结与下一章

本章把“校验”与“错误响应形状”两件事钉在可验证入口上：

- **校验触发点**：`@Valid` / `@Validated` 决定是否进入校验分支；缺一不可。
- **异常类型**：`@RequestBody` 校验失败是 `MethodArgumentNotValidException`；binder（表单/`@ModelAttribute`）校验失败是 `BindException`；方法级参数约束是 `HandlerMethodValidationException`（或 `ConstraintViolationException`）。
- **错误形状来源**：响应体之所以是 `ApiError`，是因为 resolver 链命中了 `GlobalExceptionHandler`（而不是回落到默认错误页或其它 envelope）。

下一章会把“坏输入”系统分型：同样是 400，如何用 resolver 链把根因显式化（malformed JSON / type mismatch / validation ...），并把这些分支固化成可回归契约。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
- 测试文件：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcSpringBootLabTest.java`

上一章：[03. 请求绑定（Binding）与 Converter/Formatter](binding-validation-binding-and-converters.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. Content Negotiation（406/415：Accept/Content-Type/produces/consumes）](message-conversion-content-negotiation-406-415.md)
<!-- BOOKIFY:END -->
