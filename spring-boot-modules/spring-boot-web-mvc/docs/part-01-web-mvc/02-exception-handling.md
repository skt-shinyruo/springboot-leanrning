# 02. 统一异常处理（ControllerAdvice）与“坏输入”
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕02：统一异常处理（ControllerAdvice）与“坏输入”展开，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 校验（Validation）与错误响应形状（Error Shape）](01-validation-and-error-shaping.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. 请求绑定（Binding）与 Converter/Formatter](03-binding-and-converters.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcLabTest`
    - Test file：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part03_internals/BootWebMvcExceptionResolverChainLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part09_advice_order/BootWebMvcAdviceOrderLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part10_advice_matching/BootWebMvcAdviceMatchingLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`

## 机制主线

本章把“坏输入”分成两类，并解释它们为什么看起来都可能是 400，但根因不同：

1) JSON 解析/绑定失败（malformed JSON / 类型不匹配）
2) 校验失败（合法 JSON，但字段不满足约束）

## 应当观察到的现象

- malformed JSON 的失败发生在 **进入 controller 之前**（根本没有 DTO、也没有校验）
- 统一错误响应的关键在于：是否在 `GlobalExceptionHandler` 中覆盖了对应异常

## 机制解释（Why）

Spring MVC 的异常可能来自不同阶段：

- **消息转换阶段**（HTTP message conversion）：例如 JSON 无法解析成对象，会抛出类似 `HttpMessageNotReadableException` 的异常
- **校验阶段**：`@Valid` 触发后，违反约束会抛出校验相关异常（最终被映射成 `ApiError`）

所以“同样是 400”，需要用异常处理把原因显式化，避免客户端只能靠猜。

- 先用测试把两类失败分开固化（状态码 + 错误形状/字段），避免把“绑定失败”误当成“校验失败”。

### 主链路（Call-chain sketch）

以“malformed JSON → 400 → ApiError”为例，一条典型链路是：

1. `DispatcherServlet#doDispatch`
2. `RequestResponseBodyMethodProcessor#resolveArgument`
3. `AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`
4. Jackson/Converter 抛出 `HttpMessageNotReadableException`
5. `DispatcherServlet#processHandlerException`
6. `HandlerExceptionResolverComposite#resolveException`
7. `ExceptionHandlerExceptionResolver` 命中 `GlobalExceptionHandler#handleMalformedJson`

更完整的 resolver 链解释见：
- `docs/web-mvc/spring-boot-web-mvc/part-03-web-mvc-internals/04-exception-resolvers-and-error-flow.md`

## 最小可运行实验（Lab）

- Lab：`BootWebMvcLabTest`
- Lab：`BootWebMvcExceptionResolverChainLabTest`（用 resolvedException 固化：binder/validation/converter 三类 400 的根因差异）
- 建议命令（方法级入口）：
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcLabTest#returnsBadRequestWhenJsonIsMalformed test`
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcExceptionResolverChainLabTest#canDebugHttpMessageNotReadableExceptionFromInvalidJsonViaResolvedException test`


## 实验入口

- 观察现状（malformed JSON 只断言 400）：
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java`
  - `returnsBadRequestWhenJsonIsMalformed`
- 观察 MVC 内部异常类型（用 resolvedException 固化“400 从哪里来”）：
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part03_internals/BootWebMvcExceptionResolverChainLabTest.java`
  - `canDebugBindExceptionFromModelAttributeValidationViaResolvedException`
  - `canDebugMethodArgumentNotValidExceptionFromRequestBodyValidationViaResolvedException`
  - `canDebugHttpMessageNotReadableExceptionFromInvalidJsonViaResolvedException`
- 练习：为 malformed JSON 补齐统一错误响应：
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`
  - `exercise_handleMalformedJson`

## Debug 建议

建议把“异常处理”拆成两步定位（先定根因，再看谁处理）：

### 1) 先定根因：异常来自哪一段（binder / validation / converter）

- 推荐入口（resolvedException 固化根因）：
  - `BootWebMvcExceptionResolverChainLabTest#canDebugHttpMessageNotReadableExceptionFromInvalidJsonViaResolvedException`
  - `BootWebMvcExceptionResolverChainLabTest#canDebugMethodArgumentNotValidExceptionFromRequestBodyValidationViaResolvedException`
  - `BootWebMvcExceptionResolverChainLabTest#canDebugBindExceptionFromModelAttributeValidationViaResolvedException`
  - `MvcResult#getResolvedException()` 的实际类型（这是“400 为什么出现”的最快证据）

### 2) 再看谁处理：resolver 链如何命中 @ExceptionHandler

- 推荐断点：
  - `DispatcherServlet#processHandlerException`
  - `HandlerExceptionResolverComposite#resolveException`
  - `ExceptionHandlerExceptionResolver#doResolveHandlerMethodException`
  - `GlobalExceptionHandler#handleValidation` / `handleBindException` / `handleMalformedJson` / `handleTypeMismatch`
- 决定性分支（最常用的判断）：
  - advice 是否“适用”（matching）：`basePackages/annotations/assignableTypes`
  - advice 是否“优先”（ordering）：多个 advice 都能处理时，`@Order` 决定谁先命中

进一步阅读（带可复现证据链）：
- advice 匹配：`BootWebMvcAdviceMatchingLabTest`（Part 03：05）
- advice 优先级：`BootWebMvcAdviceOrderLabTest`（Part 03：05）

## 常见坑与边界

把“统一异常处理”做成工程闭环时，最容易翻车的不是写不出 `@ExceptionHandler`，而是 **分支没分清/匹配没看懂/测试没锁定**。

### 1) 400 不是一个原因（先分类再处理）

同样是 400，常见根因至少三类（对应不同阶段与断点）：

- **JSON 解析失败**（body 路径）→ `HttpMessageNotReadableException` → `malformed_json`
- **类型不匹配**（binder/param 路径）→ `MethodArgumentTypeMismatchException` → `type_mismatch`
- **校验失败**（validation）→ `MethodArgumentNotValidException`（@RequestBody）或 `BindException`（@ModelAttribute）→ `validation_failed`

建议做法：先用测试把三类分开固化（status + message + fieldErrors），再谈“统一错误体”。

补充一个容易忽略的分支：**BindingResult 会改变错误流**。

- 没有 `BindingResult`：校验失败通常直接抛异常（进入 resolver 链）
- 有 `BindingResult`：controller 可以选择不抛异常，自己手动塑形错误体

本模块给出对照证据链：`BootWebMvcBindingDeepDiveLabTest#bindingResultCanShortCircuitExceptionFlowWhenHandledManually`。

### 2) @WebMvcTest 里“看不到错误体”

如果在 slice 测试里没有显式纳入对应的 `@ControllerAdvice`（例如 `@Import(GlobalExceptionHandler)`），看到的可能是 MVC 默认行为，而不是期望的契约。

建议：在 LabTest 里显式 `@Import` advice，避免“偶然生效”。

### 3) ControllerAdvice 的匹配规则（为什么我的 advice 不生效）

常见原因：

- `@RestControllerAdvice(basePackages = ...)` 的包范围不包含 controller
- `annotations/assignableTypes` 等 selector 没命中（以为“全局”，但实际上只作用于某些 controller 类型）
- 多个 selector 的组合是“并集（OR）”语义：以为自己在“再收敛范围”，实际上可能在“扩大适用范围”
- controller 在另一个 module/package，被 slice 测试排除
- 处理的异常类型不对（异常发生在 converter/binder 阶段，而不是直觉里的业务异常）

建议：第一步永远是拿证据——用 `MvcResult#getResolvedException()` 固定异常类型，再回头调整 `@ExceptionHandler`。

本模块提供了可复现证据链：
- Lab：`BootWebMvcAdviceMatchingLabTest`（覆盖 selector：basePackages / annotations / assignableTypes，并包含与 @Order 叠加的对照）
- 延伸阅读：Part 03 - Internals 的 `05-controlleradvice-matching-and-ordering.md`

### 3.5) 404（无 handler）为什么不会进 ControllerAdvice

现象：
- 访问一个不存在的路由，得到 404，但 `@ControllerAdvice/@ExceptionHandler` 没有生效。

关键原因：
- 404（无 handler）通常发生在 **HandlerMapping 选路阶段**：根本没有命中的 `HandlerMethod`。
- 在 Spring Boot 下，很多 404 会走到 `/error` 兜底（`BasicErrorController + ErrorAttributes`），返回的是 Boot 默认错误 envelope，而不是自定义错误体。

可运行证据链：
- `BootWebMvcSpringBootLabTest#unknownRouteFallsBackToSpringBootErrorEndpoint`（端到端证明：404 返回 body 里包含 `status/path` 等 Boot 默认字段）

### 4) 两个 advice 都能处理时，谁生效（@Order）

当两个 `@ControllerAdvice` 都能处理同一异常时，优先级由 `@Order` 决定（数值越小优先级越高）。

本模块提供了可复现证据链：
- Lab：`BootWebMvcAdviceOrderLabTest`（断言高优先级 advice 生效）

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcLabTest`
- Lab：`BootWebMvcExceptionResolverChainLabTest`
- Lab：`BootWebMvcAdviceOrderLabTest`
- Lab：`BootWebMvcAdviceMatchingLabTest`
- Exercise：`BootWebMvcExerciseTest`
- Test file：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part03_internals/BootWebMvcExceptionResolverChainLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part09_advice_order/BootWebMvcAdviceOrderLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part10_advice_matching/BootWebMvcAdviceMatchingLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`

上一章：[part-01-web-mvc/01-validation-and-error-shaping.md](01-validation-and-error-shaping.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-web-mvc/03-binding-and-converters.md](03-binding-and-converters.md)

<!-- BOOKIFY:END -->
