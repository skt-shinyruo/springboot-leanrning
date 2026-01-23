# 03：错误页（error/*.html）与内容协商（Accept：HTML vs JSON）

## 导读

- 本章主题：**03：错误页（error/*.html）与内容协商（Accept：HTML vs JSON）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcErrorViewLabTest` / `BootWebMvcViewSpringBootLabTest`
    - Test file：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcErrorViewLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewSpringBootLabTest.java`

## 机制主线

本章把“错误响应”的学习从 JSON API 扩展到页面场景：同一类错误，在浏览器访问与 API 调用时，往往需要不同的呈现方式。

## 你应该观察到什么（What to observe）

1) 当 Accept 偏向 `text/html`（浏览器）
- 404/4xx/5xx 会返回 HTML 错误页（可读、可导航）

2) 当 Accept 偏向 `application/json`（接口调用/脚本）
- 错误会返回 JSON（便于程序处理与断言）

## 机制解释（Why）

### 1) Spring Boot 的错误页约定

当进入错误处理链路时，Boot 会尝试渲染错误页模板：

- `templates/error/404.html`（最具体）
- `templates/error/4xx.html`（兜底 4xx）
- `templates/error/5xx.html`（兜底 5xx）

因此你只要提供这些模板，就能在“无 handler 的 404”等场景看到自定义页面。

### 2) 内容协商：Accept 决定“渲染 HTML 还是返回 JSON”

同一条错误链路，通常会根据请求的 Accept 选择：
- 返回视图（HTML）
- 或返回 JSON（错误体/ProblemDetail/自定义结构）

在本模块里，我们用一个最小示例演示这种差异：

- `/pages/error-demo` 主动抛出异常
- `MvcExceptionHandler` 基于 Accept 做兜底：HTML → 错误页，JSON → ApiError

## 在本模块里去哪里看

- 错误页模板：
  - `spring-boot-modules/spring-boot-web-mvc/src/main/resources/templates/error/404.html`
  - `spring-boot-modules/spring-boot-web-mvc/src/main/resources/templates/error/4xx.html`
  - `spring-boot-modules/spring-boot-web-mvc/src/main/resources/templates/error/5xx.html`
- 示例 Controller / 异常处理：
  - `spring-boot-modules/spring-boot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part02_view_mvc/MvcErrorDemoController.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part02_view_mvc/MvcExceptionHandler.java`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootWebMvcErrorViewLabTest` / `BootWebMvcViewSpringBootLabTest`
- 建议命令：`mvn -pl :spring-boot-web-mvc test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 实验入口（先跑再看）

- MockMvc（固定行为，最直观）：
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcErrorViewLabTest.java`
    - `returnsCustom404HtmlPageForUnknownRoute`
    - `renders5xxHtmlPageWhenControllerThrows`
    - `returnsJsonWhenAcceptIsJson`
- 端到端（真实端口验证错误页模板生效）：
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewSpringBootLabTest.java`

## 常见坑与边界

### 1) 404 的来源不同：路由 404 vs 业务 404

- **路由 404**：根本没有匹配到 handler（controller 方法不会执行）
  - 这类 404 更依赖 Boot 的错误页模板（`error/404.html`）
  - 对照证据：`BootWebMvcErrorViewLabTest#returnsCustom404HtmlPageForUnknownRoute`
- **业务 404**：handler 执行了，但你主动返回 404（例如找不到资源）
  - 这类 404 更依赖你的业务异常/契约（ApiError/ProblemDetail）

先分清来源，才能知道该打断点在 HandlerMapping 还是在 controller/exception handler。

### 2) 浏览器的 Accept 不是“只有 text/html”

真实浏览器通常会带一个很长的 Accept（包含 `text/html`、`application/xhtml+xml`、`*/*` 等）。
如果你写了过于严格的判断（例如只等于 `text/html`），可能会出现：
- 浏览器访问却返回 JSON
- 或 API 调用却被当成页面渲染

建议做法：
- controller mapping 用 `produces` 明确约束（API vs 页面）
- 错误处理尽量以“可解释的规则”实现，并用测试固化

### 3) @WebMvcTest 与端到端行为差异

错误页模板、静态资源链路等，在 slice 测试与端到端测试中可能存在差异：
- slice：更快、更适合固定 handler 行为与视图名
- 端到端：更适合验证模板解析/错误页是否真的生效

对照证据：
- slice：`BootWebMvcErrorViewLabTest`
- 端到端：`BootWebMvcViewSpringBootLabTest`

### 4) Security 介入后错误页“看起来不对”

如果错误发生在 FilterChain（例如 401/403），它可能不会走到 MVC 的错误页渲染。
排障顺序应优先从 FilterChainProxy/ExceptionTranslationFilter 入手（详见 Part 08）。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcErrorViewLabTest` / `BootWebMvcViewSpringBootLabTest`
- Test file：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcErrorViewLabTest.java` / `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewSpringBootLabTest.java`

上一章：[part-02-view-mvc/02-form-binding-validation-prg.md](02-form-binding-validation-prg.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](../appendix/082-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
