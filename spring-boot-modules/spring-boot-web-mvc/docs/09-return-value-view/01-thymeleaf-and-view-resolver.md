# 01. 传统 MVC 页面渲染入门（@Controller / ViewName / Thymeleaf）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕01：传统 MVC 页面渲染入门（@Controller / ViewName / Thymeleaf）展开，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcViewLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. Controller：边界、异常与契约的位置](../08-controller/01-controller-boundary.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 表单提交闭环（@ModelAttribute / BindingResult / 校验回显 / PRG）](02-form-binding-validation-prg.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcViewLabTest` / `BootWebMvcViewSpringBootLabTest`
    - Test file：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewSpringBootLabTest.java`

## 机制主线

本章从“返回 HTML 页面”开始，补齐 Spring MVC 的传统用法：`@Controller` 返回视图名（view name），由 ViewResolver 解析到 Thymeleaf 模板并渲染成 HTML。

## 应当观察到的现象（What to observe）

- 访问 `/pages/ping`：
  - 返回 `Content-Type: text/html`
  - **不是** JSON（对比 `/api/ping`）
  - 响应体来自 Thymeleaf 模板渲染，而不是 Jackson 序列化

## 机制解释（Why）

把两类 Controller 的“返回值语义”分清楚：

- `@RestController`：返回值默认是 **response body**（JSON/字符串等），由 HttpMessageConverter 写入响应体。
- `@Controller`：返回值（String）默认是 **view name**，由 ViewResolver 解析到模板，再渲染为 HTML。

在本模块里可以直接对照：

- 页面 Controller：`spring-boot-modules/spring-boot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part02_view_mvc/MvcPingController.java`
- 页面模板：`spring-boot-modules/spring-boot-web-mvc/src/main/resources/templates/pages/ping.html`
- 静态资源（CSS）：`spring-boot-modules/spring-boot-web-mvc/src/main/resources/static/css/app.css`

`MvcPingController` 里同时提供两种写法：
- 返回 view name：`GET /pages/ping`
- 返回 `ModelAndView`：`GET /pages/ping-mav`

- 只写了 `@Controller`，但方法上又加了 `@ResponseBody`：会把 view name 当作字符串写回去（看起来像“模板不生效”）。
- 模板放错目录：Thymeleaf 默认从 `classpath:/templates/` 下找模板。
- 静态资源放错目录：默认从 `classpath:/static/`（以及其它约定目录）下提供静态资源。

## 最小可运行实验（Lab）

- Lab：`BootWebMvcViewLabTest` / `BootWebMvcViewSpringBootLabTest`
- 建议命令（方法级入口）：
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcViewLabTest#rendersPingPage test`


## 常见坑与边界

### 1) 把 view name 当成 response body（最常见）

症状：
- 访问 `/pages/ping`，看到的不是 HTML，而是一个纯字符串（例如 `pages/ping`）。

根因：
- `@Controller` 的方法返回 `String` 时，默认语义是 **view name**；
- 但如果方法/类上出现 `@ResponseBody`（或误用了 `@RestController`），返回值会被当作 **response body** 写回去。

怎么验证：
- 在 `DispatcherServlet#doDispatch` 后，跟进 `HandlerMethodReturnValueHandlerComposite#handleReturnValue`，看命中的是 view 相关 handler 还是 message converter 相关 handler。

### 2) 模板找不到（TemplateInputException / 视图解析失败）

症状：
- 500，日志里出现 “template not found / cannot resolve template”。

根因：
- Thymeleaf 默认从 `classpath:/templates/` 下找模板；路径或文件名不一致会失败。

怎么验证：
- 断点：`org.thymeleaf.spring6.view.ThymeleafViewResolver#resolveViewName`
- 观察：最终选中的 viewName、模板资源路径。

### 3) 静态资源 404（CSS/JS 不生效）

症状：
- 页面能渲染，但 CSS/JS 404。

根因：
- Spring Boot 静态资源默认目录是 `classpath:/static/` 等约定目录；或者页面里引用路径写错（相对路径/前缀）。

怎么验证：
- 用浏览器 Network/或 MockMvc 对静态资源路径发起 GET；
- 或断点：`ResourceHttpRequestHandler#handleRequest`（资源是否命中）。

### 4) redirect/forward 语义没分清（PRG 链路会用到）

症状：
- 表单提交后刷新页面重复提交、URL 不变化、或者跳转逻辑不符合预期。

根因：
- `redirect:` 会返回 302 并让浏览器发起新请求；`forward:` 仍在服务端内部转发。

怎么验证：
- 在 controller 返回值处观察返回的 viewName 是否带 `redirect:` 前缀；
- 对照下一章的 PRG 闭环章节与对应 Lab。

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcViewLabTest` / `BootWebMvcViewSpringBootLabTest`
- Test file：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewSpringBootLabTest.java`

上一章：[01. Controller：边界、异常与契约的位置](../08-controller/01-controller-boundary.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 表单提交闭环（@ModelAttribute / BindingResult / 校验回显 / PRG）](02-form-binding-validation-prg.md)
<!-- BOOKIFY:END -->
