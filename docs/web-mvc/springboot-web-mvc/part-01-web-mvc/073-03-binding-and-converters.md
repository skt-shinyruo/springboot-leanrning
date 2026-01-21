# 第 73 章：03：请求绑定（Binding）与 Converter/Formatter
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：03：请求绑定（Binding）与 Converter/Formatter
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。
    - 原理：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver`
    - 推荐 Lab：`BootWebMvcLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 72 章：02：统一异常处理（ControllerAdvice）与“坏输入”](072-02-exception-handling.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 74 章：04：Interceptor 与 Filter：入口在哪里、顺序怎么理解](074-04-interceptor-and-filter-ordering.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03：请求绑定（Binding）与 Converter/Formatter**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcLabTest`
    - Test file：`spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java` / `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcBindingDeepDiveLabTest.java` / `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`

## 机制主线

本章聚焦“请求如何变成 Java 入参”，以及当你希望引入自定义类型（例如 `UserId`）时应该怎么做。

## 你应该观察到什么

同样是“请求不合法”，但它们发生在不同阶段、对应不同异常（这决定了你应该从哪里排查）：

- **JSON 解析失败**（body → Java 失败）：典型是 `HttpMessageNotReadableException`
- **校验失败**（`@Valid`）：典型是 `MethodArgumentNotValidException`（`@RequestBody`）或 `BindException`（`@ModelAttribute`）
- **类型不匹配**（String → 目标类型失败）：典型是 `MethodArgumentTypeMismatchException`

本模块的目标是：把这些分支都“塑形”为可控的响应（例如 `ApiError`），并用测试锁住它。

## 机制解释（Why）

你可以把“请求绑定”理解为两条路径（这也是排障时最重要的第一步：**先判断走哪条路**）：

1) **请求体（body）**：由 message converter 完成（JSON → Java）
2) **路径/查询参数（path/query）**：由 conversion service 完成（String → Java）

Converter/Formatter 属于第二条路径：它让 Spring MVC 知道怎么把字符串转换成你的领域类型。

## @RequestBody vs @ModelAttribute：它们到底差在哪

| 维度 | `@RequestBody`（body 路径） | `@ModelAttribute`（binder 路径） |
| --- | --- | --- |
| 输入来源 | 请求体（JSON/XML 等） | path/query/form 参数（默认） |
| 关键组件 | `HttpMessageConverter` | `DataBinder` + `ConversionService` |
| 常见异常 | `HttpMessageNotReadableException` | `BindException` / `MethodArgumentTypeMismatchException` |
| 校验异常（无 BindingResult 时） | `MethodArgumentNotValidException` | `BindException`（常见） |
| 常见误区 | 把 415/400 当成 controller 问题 | 以为加了约束注解就一定触发校验 |

如果你遇到 400：不要直接去 controller 里打印日志。先用测试或断点确认它属于哪条路径。

## @InitBinder：它的价值不是“花活”，而是边界

`@InitBinder` 的常见用途（面向真实工程）：
- **输入收敛**：例如 trim 空白、统一日期格式、注册局部 converter/formatter
- **绑定边界**：例如设置 allowed/disallowed fields，降低“批量绑定（mass assignment）”风险
- **错误信息可控**：让错误更可读、更可定位（结合统一错误响应）

本模块提供了一个可复现证据链：在 binder 路径提交表单时，即使携带了 `admin=true`，也不会被绑定（因为 controller 的 `@InitBinder` 设置了 allowedFields 白名单）。

- 证据链：`BootWebMvcBindingDeepDiveLabTest#preventsMassAssignmentViaInitBinderAllowedFields`

进一步（更适合排障）：Spring 6.2+ 的 `BindingResult#getSuppressedFields()` 可以把“被阻止绑定字段”变成可观测证据。本模块提供了对应的 debug endpoint 与断言：

- endpoint：`POST /api/advanced/binding/mass-assignment-debug`
- 证据链：`BootWebMvcBindingDeepDiveLabTest#exposesSuppressedFieldsAsEvidenceWhenBindingBlockedFields`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

### 主链路（Call-chain sketch）

本章最重要的不是背类名，而是把“两个通道”区分清楚：

1) body 通道（`@RequestBody`）：
- `DispatcherServlet#doDispatch`
- `RequestResponseBodyMethodProcessor#resolveArgument`
- `AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`
- 校验失败：`MethodArgumentNotValidException`

2) binder 通道（`@ModelAttribute/@RequestParam`）：
- `HandlerMethodArgumentResolverComposite#resolveArgument`
- `ServletModelAttributeMethodProcessor#resolveArgument`
- `WebDataBinder#bind` → `DataBinder#validate`
- 校验失败：`BindException`（无 BindingResult 时）

机制内核解释见：
- `docs/web-mvc/springboot-web-mvc/part-03-web-mvc-internals/02-argument-resolver-and-binder.md`

推荐断点（按路径）：

- body 路径（`@RequestBody`）：
  - `RequestResponseBodyMethodProcessor#resolveArgument`
  - `AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`
- binder 路径（`@ModelAttribute` / `@RequestParam` / `@PathVariable`）：
  - `ServletModelAttributeMethodProcessor#resolveArgument`
  - `WebDataBinder#bind`
  - `DataBinder#validate`
- 统一异常塑形：
  - `ExceptionHandlerExceptionResolver`

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootWebMvcLabTest`
- Lab：`BootWebMvcBindingDeepDiveLabTest`
- 建议命令（方法级入口）：
  - `mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcBindingDeepDiveLabTest#returnsTypeMismatchWhenRequestParamCannotConvert test`
  - `mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcBindingDeepDiveLabTest#returnsValidationFailedWhenModelAttributeIsInvalid test`

### 复现/验证补充说明（来自原文迁移）

## 实验入口

- 绑定基础（JSON → DTO）：
  - `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java`
    - `createsUserWhenRequestIsValid`
    - `ignoresUnknownJsonFieldsByDefault`
- 练习：path variable + 自定义类型绑定：
  - `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`
    - `exercise_pathVariables`
    - `exercise_converterFormatter`

- 默认情况下，JSON 多余字段不会导致失败（本模块当前实验断言“unknown 字段被忽略”）
- 当 controller 的入参不是 String/Long 等简单类型时，需要通过 Converter/Formatter 扩展绑定能力

## Debug 建议

- 绑定失败时先确认“走的是哪条路径”（body 还是 binder），不要在错误的地方加断点。
- 建议优先用测试把分支固化出来：400 并不等于校验失败，也可能是解析失败或类型不匹配。

建议从本模块的“对照用例”入手（同一测试类内对比更直观）：

- type mismatch（String → int 失败）：`BootWebMvcBindingDeepDiveLabTest#returnsTypeMismatchWhenRequestParamCannotConvert`
- 方法级校验（`@Validated` + 参数约束）：`BootWebMvcBindingDeepDiveLabTest#returnsMethodValidationFailedWhenRequestParamViolatesConstraint`
- BindingResult 改变错误流（不抛异常，controller 手动塑形）：`BootWebMvcBindingDeepDiveLabTest#bindingResultCanShortCircuitExceptionFlowWhenHandledManually`

配合断点地图一起用更省时间：`docs/web-mvc/springboot-web-mvc/part-00-guide/02-breakpoint-map.md`

进一步阅读（只做必要连接）：

- 校验机制（Bean Validation）：`helloagents/wiki/modules/spring-core-validation.md`
- 类型转换（容器视角，ConversionService/TypeConverter/BeanWrapper）：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md`

## 常见坑与边界

- DTO 上写了约束注解，但 controller 入参没加 `@Valid`：校验不会触发。
- `@ModelAttribute` 的校验失败常见是 `BindException`，不要只处理 `MethodArgumentNotValidException`。
- 400 不是一个原因：建议把 **解析失败 / 类型不匹配 / 校验失败** 三类错误响应区分开（至少 message 不同）。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcLabTest`
- Lab：`BootWebMvcBindingDeepDiveLabTest`
- Exercise：`BootWebMvcExerciseTest`
- Test file：`spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java` / `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcBindingDeepDiveLabTest.java` / `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`

上一章：[part-01-web-mvc/02-exception-handling.md](072-02-exception-handling.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-web-mvc/04-interceptor-and-filter-ordering.md](074-04-interceptor-and-filter-ordering.md)

<!-- BOOKIFY:END -->
