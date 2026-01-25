# 第 71 章：01：校验（Validation）与错误响应形状（Error Shape）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：校验（Validation）与错误响应形状（Error Shape）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。
    - 原理：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver`
    - 推荐 Lab：`BootWebMvcLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 70 章：05：ControllerAdvice 的匹配与优先级（为什么 advice 生效/不生效）](../part-03-web-mvc-internals/070-05-controlleradvice-matching-and-ordering.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 72 章：02：统一异常处理（ControllerAdvice）与“坏输入”](072-02-exception-handling.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01：校验（Validation）与错误响应形状（Error Shape）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
    - Test file：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcSpringBootLabTest.java`

## 机制主线

## 你应该观察到什么（What to observe）

- 当请求体字段不满足约束（`@NotBlank`、`@Email` 等）时：
  - HTTP 状态码为 `400 Bad Request`
  - 响应体是统一形状（`ApiError`），包含：
    - `message = "validation_failed"`
    - `fieldErrors` 中包含对应字段（`name`、`email`）

## 机制解释（Why）

可以把 Web MVC 的请求处理分成三段：

在本模块里，错误形状由：
- `spring-boot-modules/spring-boot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part01_web_mvc/GlobalExceptionHandler.java`
控制。

- 看到 400：先看响应体的 `fieldErrors`，定位是哪一个字段失败，再回到 DTO 的注解。
- 校验没触发：确认 controller 入参是否带 `@Valid`；如果只写了约束注解但没写 `@Valid`，通常不会触发。

## 方法级校验（Method Validation）在 Controller 边界

除了“对象校验”（`@Valid` 校验 DTO），Web MVC 还可能在 controller 方法参数上触发 **方法级校验**：

- 典型写法：controller 上 `@Validated`，参数上写 `@Min/@NotBlank/...`
- 失败时常见异常（随 Spring 版本不同）：`HandlerMethodValidationException` 或 `ConstraintViolationException`
- 本模块把该分支也塑形成统一错误体：`ApiError(message = "method_validation_failed")`

可运行证据链（推荐先跑再断点）：

- endpoint：`GET /api/advanced/binding/age-validated?age=-1`
- Lab：`BootWebMvcBindingDeepDiveLabTest#returnsMethodValidationFailedWhenRequestParamViolatesConstraint`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

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

对应的机制内核解释见：
- `docs/web-mvc/spring-boot-web-mvc/part-03-web-mvc-internals/01-dispatcherservlet-call-chain.md`
- `docs/web-mvc/spring-boot-web-mvc/part-03-web-mvc-internals/02-argument-resolver-and-binder.md`
- `docs/web-mvc/spring-boot-web-mvc/part-03-web-mvc-internals/04-exception-resolvers-and-error-flow.md`

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
- 建议命令（方法级入口）：
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcLabTest#returnsValidationErrorWhenRequestIsInvalid test`

### 复现/验证补充说明（来自原文迁移）

## 实验入口（先跑再看）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
实验入口已在章首提示框给出（先跑再读）。建议跑完后回到本章“证据链”逐条验证关键结论。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## Debug 建议

把本章当作 3 个“可验证的分支”来调试，会更快：

1) **校验是否触发（@Valid 分支）**
- 入口测试：`BootWebMvcLabTest#returnsValidationErrorWhenRequestIsInvalid`
- 建议命令：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcLabTest#returnsValidationErrorWhenRequestIsInvalid test`
- 对照用例（故意省略 `@Valid`，证明“写了注解但没触发”）：`BootWebMvcLabTest#createUserSucceedsWhenControllerOmitsValidAnnotation`
- 断点建议：
  - `RequestResponseBodyMethodProcessor#resolveArgument`
  - `SpringValidatorAdapter#validate`
- 观察点：
  - DTO 上是否有约束注解（`CreateUserRequest`）
  - controller 入参是否带 `@Valid`
- 决定性分支：
  - **没有 `@Valid`**：约束注解不会自动生效（这是最常见的“我写了注解但没校验”的原因）

2) **异常如何被塑形成 ApiError（错误体分支）**
- 断点建议：
  - `ExceptionHandlerExceptionResolver#doResolveHandlerMethodException`
  - `GlobalExceptionHandler#handleValidation`
- 观察点：
  - `ex.getBindingResult().getFieldErrors()`（字段错误列表）
  - 最终响应体的 `message/fieldErrors`

3) **同样是 400：到底是校验失败还是 JSON 解析失败（根因分支）**
- 建议配套跑：`BootWebMvcExceptionResolverChainLabTest`（用 `resolvedException` 固定根因）

进一步阅读（只做必要连接，不扩散篇幅）：

- Validation 模块（Bean Validation 机制本身）：`helloagents/wiki/modules/spring-core-validation.md`
- Beans 模块（类型转换/值解析等底层支撑）：`helloagents/wiki/modules/spring-core-beans.md`

## 常见坑与边界

本章聚焦 Web MVC 的“边界校验”：请求从 HTTP/JSON 进入 Controller 时，校验在哪里触发、失败后如何形成可控的错误响应。

1) **数据绑定（binding）**：JSON → DTO（例如 `CreateUserRequest`）
2) **边界校验（validation）**：`@Valid` 触发 Bean Validation，对 DTO 执行约束检查
3) **错误映射（error mapping）**：异常被 `@RestControllerAdvice` 捕获，转换成你的 `ApiError` 形状

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
- Test file：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcSpringBootLabTest.java`

上一章：[part-00-guide/00-deep-dive-guide.md](../part-00-guide/064-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-web-mvc/02-exception-handling.md](072-02-exception-handling.md)

<!-- BOOKIFY:END -->
