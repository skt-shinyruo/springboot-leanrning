# spring-boot-web-mvc

本模块用于学习 Spring MVC 的常见入门点，并覆盖两条主线：

- **REST API（JSON）主线**：`@RestController`、参数校验（Validation）、统一错误响应（`@RestControllerAdvice`）
- **传统 MVC（HTML）主线**：`@Controller`、Thymeleaf 页面渲染、表单提交（绑定/校验/回显/PRG）、错误页与内容协商（Accept：HTML vs JSON）


## 本模块读法

本模块入口页承担“定位路线”的职责：先把最小实验跑成事实，再沿主线章节解释机制，最后回到排障与自检材料确认边界。

- **先跑入口**：优先使用本页给出的 Book Matrix、Branch Matrix 或最小 Lab，把现象固定成可重复断言。
- **再读主线**：按“主线时间线 → 深挖导读 → 正文主题”的顺序阅读，避免只按文件名零散跳转。
- **最后排障**：遇到问题先回到断点地图、关键分支矩阵、常见坑和自检清单，把问题收敛到章节、断点与测试入口。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`

文档入口：
- 模块目录：见本 README 的「目录（唯一顺序来源）」
- 总览（先把写法与排障串起来）：[`docs/00-guide/00-from-annotations-to-breakpoints.md`](docs/guide-from-annotations-to-breakpoints.md)
- 常见坑：[`docs/appendix-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix-self-check.md`](docs/appendix-self-check.md)

## 本模块完成后应能解释的内容

- 用 `@RestController` 编写 JSON API
- 用 `@Valid` + `jakarta.validation` 做请求参数校验
- 用 `@RestControllerAdvice` 统一返回错误响应
- 用 `@Controller` 返回 viewName / `ModelAndView` 渲染 Thymeleaf 页面
- 表单提交闭环：`@ModelAttribute` + `BindingResult` + 校验失败回显 + PRG（Post-Redirect-Get）+ Flash Attributes
- 错误页模板（`templates/error/*`）与 Accept 驱动的响应形态（HTML vs JSON）
- 对比 `@WebMvcTest`（切片）与 `@SpringBootTest`（全量上下文）的测试体验

## 前置知识

- 先完成 `spring-boot-basics`（至少理解配置加载与启动过程）
- 了解 HTTP/JSON 的基本概念（状态码、请求体、响应体）
- （可选）了解 Bean Validation 的基本注解（`@NotBlank`、`@Email`）

## 关键命令

### 运行

```bash
mvn -pl :spring-boot-web-mvc spring-boot:run
```

默认端口：`8081`

### 快速验证

- Ping：

```bash
curl http://localhost:8081/api/ping
```

- 创建用户（正常）：

```bash
curl -X POST http://localhost:8081/api/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"Alice","email":"alice@example.com"}'
```

- 创建用户（触发校验失败）：

```bash
curl -X POST http://localhost:8081/api/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"","email":"not-an-email"}'
```

- 访问页面（HTML）：

```bash
curl -H 'Accept: text/html' http://localhost:8081/pages/ping
```

- 表单页（用浏览器打开）：
  - `http://localhost:8081/pages/users/new`

### 测试

```bash
mvn -pl :spring-boot-web-mvc test
```

## docs 阅读顺序

按 “总览（先把写法与排障串起来）→ 入口 → REST 主线 → 页面主线 → 常见坑/自测题” 的顺序学习：

（目录：见本 README 的「目录（唯一顺序来源）」）

0. [从注解到断点：用一条主线学会 Spring MVC](docs/guide-from-annotations-to-breakpoints.md)
1. [校验与错误响应形状](docs/binding-validation-validation-and-error-shaping.md)
2. [统一异常处理与坏输入](docs/exception-resolvers-exception-handling.md)
3. [请求绑定与 Converter/Formatter](docs/binding-validation-binding-and-converters.md)
4. [Interceptor vs Filter：入口与顺序](docs/handleradapter-interceptor-interceptor-and-filter-ordering.md)
5. [传统 MVC 页面渲染入门（Thymeleaf/ViewResolver）](docs/return-value-view-thymeleaf-and-view-resolver.md)
6. [表单提交闭环（绑定/校验/回显/PRG）](docs/return-value-view-form-binding-validation-prg.md)
7. [错误页与内容协商（Accept：HTML vs JSON）](docs/boot-error-error-pages-and-content-negotiation.md)
8. [常见坑清单](docs/appendix-common-pitfalls.md)

对应的可运行实验（先跑后读）：
- `src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java`（`@WebMvcTest` 切片）
- `src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcSpringBootLabTest.java`（`@SpringBootTest` 全量）
- `src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewLabTest.java`（页面渲染 MockMvc）
- `src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcErrorViewLabTest.java`（错误页 + Accept）
- `src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewSpringBootLabTest.java`（页面渲染端到端）

## 概念 → 在本模块哪里能“看见”

| 要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 应能解释清楚 |
| --- | --- | --- | --- |
| 校验在边界触发 | [docs/06-binding-validation/01](docs/binding-validation-validation-and-error-shaping.md) | `BootWebMvcLabTest#returnsValidationErrorWhenRequestIsInvalid` + `CreateUserRequest` | 为什么需要 `@Valid`，失败时异常从哪来 |
| 统一错误响应形状 | [docs/06-binding-validation/01](docs/binding-validation-validation-and-error-shaping.md) | `GlobalExceptionHandler` + `ApiError` | 为什么要自定义错误结构，结构由谁决定 |
| malformed JSON vs 校验失败 | [docs/10-exception-resolvers/02](docs/exception-resolvers-exception-handling.md) | `BootWebMvcLabTest#returnsBadRequestWhenJsonIsMalformed` + `BootWebMvcExerciseTest#exercise_handleMalformedJson` | 两类 400 的根因差异 |
| Converter/Formatter 扩展绑定 | [docs/06-binding-validation/03](docs/binding-validation-binding-and-converters.md) | `BootWebMvcExerciseTest#exercise_converterFormatter` | String 如何变成自定义类型 |
| Interceptor 生效范围与顺序 | [docs/04-handleradapter-interceptor/04](docs/handleradapter-interceptor-interceptor-and-filter-ordering.md) | `BootWebMvcExerciseTest#exercise_interceptor` | 为什么它只对 `/api/**` 生效 |
| `@Controller` 返回 viewName | [docs/09-return-value-view/01](docs/return-value-view-thymeleaf-and-view-resolver.md) | `MvcPingController` + `ping.html` | 为什么返回 String 却渲染了 HTML |
| 表单校验回显（BindingResult） | [docs/09-return-value-view/02](docs/return-value-view-form-binding-validation-prg.md) | `MvcUserController` + `user-form.html` | 为什么校验失败不会抛异常，而是回到表单页 |
| 错误页与 Accept | [docs/11-boot-error/03](docs/boot-error-error-pages-and-content-negotiation.md) | `templates/error/*` + `MvcExceptionHandler` | 为什么同一个错误在浏览器和脚本里长得不一样 |

> 想从机制层理解“校验为什么有时不生效”，可进一步阅读 `spring-core-modules/spring-core-validation/docs/validation-core-method-validation-proxy.md`（方法参数校验与代理）。

## 实验/练习索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶。练习默认 `@Disabled`，逐个开启。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java` | `@WebMvcTest` 切片：Controller/校验/错误结构 | ⭐ | 看 `GlobalExceptionHandler` 的错误结构与字段 |
| Lab | `src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcSpringBootLabTest.java` | `@SpringBootTest`：RANDOM_PORT 下的端到端行为 | ⭐ | 对比切片与全量上下文加载范围 |
| Exercise | `src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java` | 按提示扩展字段/错误响应/测试断言 | ⭐–⭐⭐ | 从“新增字段 + 校验”开始 |
| Lab | `src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewLabTest.java` | 页面渲染：viewName/model/HTML 断言 + 表单回显/redirect | ⭐ | 对照 `MvcUserController` 与 templates 的绑定关系 |
| Lab | `src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcErrorViewLabTest.java` | 错误页：404/5xx HTML 模板 + Accept=JSON 分支 | ⭐–⭐⭐ | 对照 `templates/error/*` 与 `MvcExceptionHandler` |
| Lab | `src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewSpringBootLabTest.java` | 页面渲染：端到端获取 HTML（真实端口） | ⭐ | 对比 MockMvc 与真实运行差异 |

## 常见 Debug 路径

- `400 Bad Request`：先看响应体里 `fieldErrors`，再定位到对应 DTO 的校验注解
- 校验没触发：确认 controller 入参是否带 `@Valid`、是否走到 `GlobalExceptionHandler`
- JSON 解析失败：优先检查请求体是否是合法 JSON（以及字段名是否匹配）
- 页面回显没生效：确认 POST 方法里参数是否为 `@Valid @ModelAttribute` + `BindingResult`（并且 BindingResult 紧跟其后）
- 404：确认路由是否正确（`/api/...` 或 `/pages/...`），并观察自定义错误页是否生效（`templates/error/404.html`）

## 扩展练习（可选）

- 给 `CreateUserRequest` 增加一个字段 `age`，要求 `>= 18`
- 把错误响应结构扩展为：包含 `timestamp`、`path` 等信息

## 参考

- Spring MVC
- Bean Validation (Jakarta Validation)

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Web MVC 源码解析：主链路与关键分支（spring-webmvc 6.2.15）

本目录只写 **Spring Framework / Spring Web MVC（spring-webmvc）源码**：把一次请求从进入到写回的主链路与关键分支，落到可断点、可回归的事实。示例 Controller/DTO 只作为“触发某条分支的证据入口”；正文以框架类与方法为准。

- 基线版本：Spring Boot `3.5.9` → Spring Framework `6.2.15`（`org.springframework:spring-webmvc:6.2.15`），JDK `17`
- 文档约定：每章至少包含 **源码入口（FQCN#method）**、**关键分支（异常/状态码）**、**证据链（可运行的 LabTest）**

---

### 最短入口（源码阅读）：把一次请求链路跑成“可断点事实”
- 运行（把参数解析阶段变成可观察证据）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcInternalsLabTest test`
- 断点（先不加其它）：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod`
- 观察点：handler 是怎么选出来的、参数是在哪个 resolver/binder/converter 里变成对象的、异常最终是由哪个 resolver 翻译成状态码/响应体的

---

### 10 分钟入口（全景）：先把“主线 + 分支”跑通
- `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`

运行后应能回答三个事实问题：

1. 请求进入 MVC 的关键入口在哪里（从 FilterChain 到 `doDispatch`）；
2. 绑定与转换失败时会走哪类异常解析；
3. 最终错误响应形状由哪些组件共同决定。

---

### 源码入口（按调用顺序：在栈上应该看到的“骨架”）
1. Servlet 入口：`org.springframework.web.servlet.FrameworkServlet#processRequest`
2. MVC 总入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch`
3. 选路：`org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#getHandlerInternal`
4. 调用：`org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod`
5. 参数解析：`org.springframework.web.method.support.HandlerMethodArgumentResolverComposite#resolveArgument`
6. binder：`org.springframework.web.bind.WebDataBinder#bind` / `org.springframework.validation.DataBinder#validate`
7. body read/write：`org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters` / `org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor#writeWithMessageConverters`
8. 异常翻译：`org.springframework.web.servlet.HandlerExceptionResolverComposite#resolveException`
9. `/error` 回落（Boot）：`org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController#error`

---

### 阅读路线（按请求从进入到结束的顺序）
#### 0) 先建立坐标：拿到“全图”与断点入口
1. [从注解到断点：用一条主线学会 Spring MVC](docs/guide-from-annotations-to-breakpoints.md)
2. [主线时间线](docs/guide-mainline-timeline.md)
3. [深挖导读](docs/guide-deep-dive-guide.md)
4. [知识地图（先看全图）](docs/guide-knowledge-map.md)
5. [断点图（排障优先）](docs/testing-observability-breakpoint-map.md)
6. [关键分支矩阵（If/Then 收敛）](docs/testing-observability-branch-decision-matrix.md)

#### 1) C1：进入容器（FilterChain / Security）
- 关键对象：`FilterChain` / `jakarta.servlet.Filter`、`DelegatingFilterProxy`、`FilterChainProxy`、`SecurityFilterChain`
- 扩展点：`SecurityFilterChain` 配置（认证/鉴权/CSRF/异常处理）；自定义 Filter（addFilterBefore/After）
- 常见分支：401（未认证）/ 403（无权限或 CSRF）/ 302（登录跳转，取决于安全配置）
- 证据（断点/测试）：`BootWebMvcSecurityLabTest`；断点 `DelegatingFilterProxy#doFilter` / `FilterChainProxy#doFilterInternal`（对照[断点图](docs/testing-observability-breakpoint-map.md)）
- [Security FilterChain 与 Web MVC（401/403/CSRF 在哪发生）](docs/filterchain-security-security-filterchain-and-mvc.md)

#### 2) C2：进入 MVC（DispatcherServlet#doDispatch）
- 关键对象：`DispatcherServlet`、`HandlerMapping`、`HandlerAdapter`、`HandlerExecutionChain`
- 扩展点：`WebMvcConfigurer`（Interceptor/Converter/ArgumentResolver/Async）；`@ControllerAdvice`（异常收敛）
- 常见分支：404/405（选路）；400（解析/绑定/校验）；406/415（协商与 converter）；async 二次 dispatch
- 证据（断点/测试）：`BootWebMvcInternalsLabTest`；断点 `DispatcherServlet#doDispatch` / `processDispatchResult`
- [请求调用链速览（从 FilterChain 到 DispatcherServlet#doDispatch）](docs/dispatcherservlet-webmvc-request-call-chain.md)
- [DispatcherServlet 主链路（把选路/参数解析/返回值/异常串起来）](docs/dispatcherservlet-call-chain.md)

#### 3) C3：选路（HandlerMapping：404/405 的起点）
- 关键对象：`RequestMappingHandlerMapping`、`RequestMappingInfo`、`HandlerMethod`
- 扩展点：`@RequestMapping` 条件（path/method/params/headers/consumes/produces）
- 常见分支：404（无 handler）/ 405（路径命中但方法不支持）
- 证据（断点/测试）：`BootWebMvcSpringBootLabTest`；断点 `RequestMappingHandlerMapping#getHandlerInternal` / `handleNoMatch`
- [HandlerMapping：路由、404/405 与 mapping 约束](docs/handlermapping-routing.md)

#### 4) C4：拦截器（Interceptor）与“入口顺序”
- 关键对象：`HandlerInterceptor` / `AsyncHandlerInterceptor`、`HandlerExecutionChain`、`OncePerRequestFilter`
- 扩展点：`WebMvcConfigurer#addInterceptors`；自定义 Filter（容器层）
- 常见分支：`preHandle` 短路；async 两次 dispatch（REQUEST/ASYNC）；ERROR 分发（错误页）
- 证据（断点/测试）：`BootWebMvcTraceLabTest`；断点 `HandlerExecutionChain#applyPreHandle` / `triggerAfterCompletion`
- [Interceptor 与 Filter：入口在哪里、顺序怎么理解](docs/handleradapter-interceptor-interceptor-and-filter-ordering.md)

#### 5) C5：参数解析（ArgumentResolver）
- 关键对象：`HandlerMethodArgumentResolver`、`HandlerMethodArgumentResolverComposite`、`RequestResponseBodyMethodProcessor`、`WebDataBinder`
- 扩展点：`WebMvcConfigurer#addArgumentResolvers`；自定义注解 + resolver；`@InitBinder`
- 常见分支：缺参/类型不匹配（400）；`@RequestBody` 走 converter；`@ModelAttribute` 走 binder
- 证据（断点/测试）：`BootWebMvcInternalsLabTest` / `BootWebMvcBindingDeepDiveLabTest`；断点 `HandlerMethodArgumentResolverComposite#resolveArgument`
- [ArgumentResolver 与 Binder（解析参数/绑定/校验触发点）](docs/argument-resolver-and-binder.md)

#### 6) C6：绑定/转换/校验（Binder / Conversion / Validation）
- 关键对象：`WebDataBinder`、`ConversionService`、`Converter/Formatter`、`Validator`
- 扩展点：`WebMvcConfigurer#addFormatters`；`@InitBinder#setAllowedFields`；`@Valid/@Validated`
- 常见分支：`BindException` / `MethodArgumentNotValidException` / `HandlerMethodValidationException`
- 证据（断点/测试）：`BootWebMvcBindingDeepDiveLabTest`；断点 `WebDataBinder#bind` / `DataBinder#validate`
- [请求绑定（Binding）与 Converter/Formatter](docs/binding-validation-binding-and-converters.md)
- [校验（Validation）与错误响应形状（Error Shape）](docs/binding-validation-validation-and-error-shaping.md)

#### 7) C7：消息转换与内容协商（HttpMessageConverter / 406 / 415 / Jackson）
- 关键对象：`HttpMessageConverter`、`ContentNegotiationManager`、`ObjectMapper`
- 扩展点：`extendMessageConverters`；`Jackson2ObjectMapperBuilderCustomizer`；`ResponseBodyAdvice`
- 常见分支：415（读不进）/ 406（写不出）/ 400（malformed JSON）
- 证据（断点/测试）：`BootWebMvcErrorBranchMatrixLabTest` / `BootWebMvcContractJacksonLabTest`；断点 `readWithMessageConverters` / `writeWithMessageConverters`
- [Content Negotiation（406/415：Accept/Content-Type/produces/consumes）](docs/message-conversion-content-negotiation-406-415.md)
- [Jackson ObjectMapper 可控（严格模式、未知字段、时间）](docs/message-conversion-jackson-objectmapper-controls.md)

#### 8) C8：Controller（边界与职责）
- 关键对象：`@RestController/@Controller`、`HandlerMethod`、`BindingResult`
- 扩展点：方法签名选择（`ResponseEntity` / `BindingResult`）；`@Validated`；`@InitBinder`
- 常见分支：有无 `BindingResult` 会改变是否抛异常；很多错误（401/403/406/415）并不发生在 controller 内
- 证据（断点/测试）：`BootWebMvcLabTest`；断点 `RequestMappingHandlerAdapter#invokeHandlerMethod`
- [Controller：边界、异常与契约的位置](docs/controller-boundary.md)

#### 9) C9：返回值与写回（ReturnValue / View / Converter(write)）
- 关键对象：`HandlerMethodReturnValueHandler`、`ViewResolver`、`ModelAndView`
- 扩展点：ViewResolver 配置；返回值类型选择（view vs body）；PRG（Post/Redirect/Get）
- 常见分支：渲染/重定向/序列化三条路；写回失败常表现为 406/500
- 证据（断点/测试）：`BootWebMvcErrorViewLabTest`；断点 `HandlerMethodReturnValueHandlerComposite#handleReturnValue` / `writeWithMessageConverters`
- [传统 MVC 页面渲染入门（@Controller / ViewName / Thymeleaf）](docs/return-value-view-thymeleaf-and-view-resolver.md)
- [表单提交闭环（@ModelAttribute / BindingResult / 校验回显 / PRG）](docs/return-value-view-form-binding-validation-prg.md)
- [HttpMessageConverter 与返回值处理（序列化发生在哪里）](docs/return-value-view-message-converters-and-return-values.md)

#### 10) C10：异常收敛（ExceptionResolvers / ControllerAdvice / Error Shape）
- 关键对象：`HandlerExceptionResolverComposite`、`ExceptionHandlerExceptionResolver`、`@ControllerAdvice/@ExceptionHandler`
- 扩展点：自定义 `@ControllerAdvice`；advice matching/ordering；`ProblemDetail` vs 自定义错误体
- 常见分支：400 的三类根因（converter/binder/validation）；advice 不生效/被更高优先级覆盖
- 证据（断点/测试）：`BootWebMvcExceptionResolverChainLabTest` / `BootWebMvcAdviceOrderLabTest` / `BootWebMvcAdviceMatchingLabTest`；断点 `DispatcherServlet#processHandlerException`
- [统一异常处理（ControllerAdvice）与“坏输入”](docs/exception-resolvers-exception-handling.md)
- [错误契约加固（解析失败 vs 校验失败 vs 类型不匹配）](docs/exception-resolvers-error-contract-hardening.md)
- [ExceptionResolvers（异常从哪来、又被谁“翻译”成状态码）](docs/exception-resolvers-and-error-flow.md)
- [ProblemDetail vs 自定义错误体（ApiError：契约的两种路线）](docs/exception-resolvers-problemdetail-vs-custom-error.md)
- [ControllerAdvice 的匹配与优先级（为什么 advice 生效/不生效）](docs/exception-resolvers-controlleradvice-matching-and-ordering.md)

#### 11) C11：Spring Boot /error（ERROR dispatch、错误页与 Accept）
- 关键对象：`BasicErrorController`、`ErrorAttributes`、ERROR dispatch（`DispatcherType.ERROR`）
- 扩展点：自定义 `error/*.html` 错误页；自定义 `ErrorAttributes`；`server.error.*` 配置
- 常见分支：404/500 回落 `/error`；Accept 决定 HTML vs JSON
- 证据（断点/测试）：`BootWebMvcSpringBootLabTest`；断点 `BasicErrorController#error`（结合 `request.getDispatcherType()==ERROR`）
- [错误页（error/*.html）与内容协商（Accept：HTML vs JSON）](docs/boot-error-error-pages-and-content-negotiation.md)

#### 12) C12：Async / SSE（第二次 dispatch：DispatcherType.ASYNC）
- 关键对象：`WebAsyncManager`、`Callable`、`DeferredResult`、`SseEmitter`
- 扩展点：`WebMvcConfigurer#configureAsyncSupport`；timeout/fallback；async interceptor
- 常见分支：REQUEST/ASYNC 二次 dispatch；timeout；客户端断开（SSE）
- 证据（断点/测试）：`BootWebMvcAsyncSseLabTest`；断点 `AsyncHandlerInterceptor#afterConcurrentHandlingStarted` / `WebAsyncManager`
- [Servlet Async（Callable）与测试（asyncDispatch）](docs/async-sse-servlet-async-and-testing.md)
- [SSE（SseEmitter：text/event-stream 最小闭环）](docs/async-sse-sse-emitter.md)
- [DeferredResult（回调式异步）与 timeout/fallback（可控分支）](docs/async-sse-deferredresult-and-timeout.md)
- [Interceptor 的生命周期（sync vs async：为什么会“回调少了一截”）](docs/async-sse-interceptor-async-lifecycle.md)

#### 13) C13：真实 HTTP 场景（CORS / 上传下载 / 静态资源 / 缓存 / 反向代理转发头）
- 关键对象：CORS Processor、`MultipartResolver`、`ResourceHttpRequestHandler`、`ShallowEtagHeaderFilter`、`ForwardedHeaderFilter`
- 扩展点：`addCorsMappings`；上传大小限制；静态资源 handler；缓存控制（ETag/Last-Modified）；`server.forward-headers-strategy`
- 常见分支：预检 OPTIONS；上传解析失败；静态资源 304/404；反向代理下 scheme/host/prefix 语义不一致导致的跳转/回调地址问题
- 证据（断点/测试）：`BootWebMvcRealWorldHttpLabTest`
- 从 [01](docs/real-world-http-cors-preflight.md) 开始按顺序阅读即可（01～06）。

#### 14) C14：测试 / 排障 / 观测（证据链）
- 关键对象：`MockMvc`、`MvcResult`（`handler` / `resolvedException`）、`MeterRegistry`
- 扩展点：`@WebMvcTest` 切片 + `@Import` advice；TimingInterceptor；Actuator exposure
- 常见分支：用 `resolvedException` 分型 400/406/415；指标端点暴露与路径匹配
- 证据（断点/测试）：`BootWebMvcTestingDebuggingLabTest` / `BootWebMvcObservabilityLabTest`；工具页：[分支矩阵](docs/testing-observability-branch-decision-matrix.md) / [断点图](docs/testing-observability-breakpoint-map.md)
- [WebMvc 测试与排障（resolvedException / handler / 断点清单）](docs/testing-observability-webmvc-testing-and-troubleshooting.md)
- [Observability（Interceptor 计时 vs Actuator 指标）](docs/testing-observability-observability-and-metrics.md)
- [关键分支矩阵（Web MVC）](docs/testing-observability-branch-decision-matrix.md)
- [断点地图（Part 01）](docs/testing-observability-breakpoint-map.md)

---

### 排障入口（从症状回到最短分支）
- 断点地图（排障优先）：[06-breakpoint-map.md](docs/testing-observability-breakpoint-map.md)
- 请求调用链速览（快速定位）：[03-webmvc-request-call-chain.md](docs/dispatcherservlet-webmvc-request-call-chain.md)
- 关键分支矩阵（If/Then 收敛）：[04-branch-decision-matrix.md](docs/testing-observability-branch-decision-matrix.md)
- 排障 playbook：[appendix-common-pitfalls.md](docs/appendix-common-pitfalls.md)
- 自检清单：[appendix-self-check.md](docs/appendix-self-check.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
- Branch Matrix（错误分支矩阵 400/406/415）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`
- Solutions（练习 答案回归）：`mvn -q -pl :spring-boot-web-mvc -Dtest=*ExerciseSolutionTest test`
- 并发/性能（RequestScope 隔离 / 并发请求边界）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcRequestScopeIsolationLabTest test`

---

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)
