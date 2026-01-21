# 第 76 章：01：传统 MVC 页面渲染入门（@Controller / ViewName / Thymeleaf）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：传统 MVC 页面渲染入门（@Controller / ViewName / Thymeleaf）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。
    - 原理：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver`
    - 推荐 Lab：`BootWebMvcViewLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 75 章：05：Interceptor 的生命周期（sync vs async：为什么会“回调少了一截”）](../part-01-web-mvc/075-05-interceptor-async-lifecycle.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 77 章：01：Content Negotiation（406/415：Accept/Content-Type/produces/consumes）](../part-04-rest-contract/077-01-content-negotiation-406-415.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01：传统 MVC 页面渲染入门（@Controller / ViewName / Thymeleaf）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcViewLabTest` / `BootWebMvcViewSpringBootLabTest`
    - Test file：`spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewLabTest.java` / `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewSpringBootLabTest.java`

## 机制主线

本章从“返回 HTML 页面”开始，补齐 Spring MVC 的传统用法：`@Controller` 返回视图名（view name），由 ViewResolver 解析到 Thymeleaf 模板并渲染成 HTML。

## 你应该观察到什么（What to observe）

- 访问 `/pages/ping`：
  - 返回 `Content-Type: text/html`
  - **不是** JSON（对比 `/api/ping`）
  - 响应体来自 Thymeleaf 模板渲染，而不是 Jackson 序列化

## 机制解释（Why）

把两类 Controller 的“返回值语义”分清楚：

- `@RestController`：返回值默认是 **response body**（JSON/字符串等），由 HttpMessageConverter 写入响应体。
- `@Controller`：返回值（String）默认是 **view name**，由 ViewResolver 解析到模板，再渲染为 HTML。

在本模块里可以直接对照：

- 页面 Controller：`spring-boot-modules/springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part02_view_mvc/MvcPingController.java`
- 页面模板：`spring-boot-modules/springboot-web-mvc/src/main/resources/templates/pages/ping.html`
- 静态资源（CSS）：`spring-boot-modules/springboot-web-mvc/src/main/resources/static/css/app.css`

`MvcPingController` 里同时提供两种写法：
- 返回 view name：`GET /pages/ping`
- 返回 `ModelAndView`：`GET /pages/ping-mav`

- 只写了 `@Controller`，但方法上又加了 `@ResponseBody`：会把 view name 当作字符串写回去（看起来像“模板不生效”）。
- 模板放错目录：Thymeleaf 默认从 `classpath:/templates/` 下找模板。
- 静态资源放错目录：默认从 `classpath:/static/`（以及其它约定目录）下提供静态资源。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootWebMvcViewLabTest` / `BootWebMvcViewSpringBootLabTest`
- 建议命令（方法级入口）：
  - `mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcViewLabTest#rendersPingPage test`

### 复现/验证补充说明（来自原文迁移）

## 实验入口（先跑再看）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
实验入口已在章首提示框给出（先跑再读）。建议跑完后回到本章“证据链”逐条验证关键结论。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## 常见坑与边界

### 1) 把 view name 当成 response body（最常见）

症状：
- 你访问 `/pages/ping`，看到的不是 HTML，而是一个纯字符串（例如 `pages/ping`）。

根因：
- `@Controller` 的方法返回 `String` 时，默认语义是 **view name**；
- 但如果方法/类上出现 `@ResponseBody`（或你误用了 `@RestController`），返回值会被当作 **response body** 写回去。

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

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcViewLabTest` / `BootWebMvcViewSpringBootLabTest`
- Test file：`spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewLabTest.java` / `spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewSpringBootLabTest.java`

上一章：[part-01-web-mvc/05-interceptor-async-lifecycle.md](../part-01-web-mvc/075-05-interceptor-async-lifecycle.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-02-view-mvc/02-form-binding-validation-prg.md](02-form-binding-validation-prg.md)

<!-- BOOKIFY:END -->
