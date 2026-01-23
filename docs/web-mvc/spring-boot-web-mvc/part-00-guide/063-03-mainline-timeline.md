# 第 63 章：主线时间线：Spring Boot Web MVC
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：Spring Boot Web MVC
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。
    - 原理：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver`
    - 推荐 Lab：`BootWebMvcInternalsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 62 章：Web MVC 请求主线](../../../book/062-webmvc-mainline.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 64 章：00 - Deep Dive Guide（springboot-web-mvc）](064-00-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：一次 HTTP 请求如何在 Spring MVC 中被“选路、绑定、执行、返回、异常处理”，以及你如何在关键分支上定位问题。
    - 读完你应该能复述：**DispatcherServlet → HandlerMapping → HandlerAdapter → 参数解析/消息转换 → 返回/异常** 这一条主线。
    - 推荐顺序：先读《知识地图/断点图》→ 本章 → 先把 Web MVC Internals 的主线跑通 → 再按场景扩展（REST、文件、异步、测试、安全）。

!!! example "建议先跑的 Lab（把时间线变成证据）"

    - Lab：`BootWebMvcInternalsLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Boot Web MVC —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。
- 回到主线：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Boot Web MVC」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- Web MVC 处在“应用对外入口”：它把 Servlet 容器的请求事件，翻译成 Controller 方法调用与响应输出。
- 你调试 404/400/415/500、参数绑定失败、返回值序列化异常时，本质都在主线的某个分支上。

## 主线时间线（推荐顺读：先把请求跑通）

1. 先拿到全图：知道“问题可能出现在主线的哪一段”
   - 知识地图：[01-knowledge-map.md](065-01-knowledge-map.md)
   - 断点图：[02-breakpoint-map.md](066-02-breakpoint-map.md)
2. 把“入口调用链”跑通：DispatcherServlet#doDispatch 到底做了什么
   - 阅读：[01. DispatcherServlet 主链路](../part-03-web-mvc-internals/067-01-dispatcherservlet-call-chain.md)
3. 把“返回值如何出方法”跑通：MessageConverter / ReturnValueHandler
   - 阅读：[03. MessageConverter 与返回值](../part-03-web-mvc-internals/068-03-message-converters-and-return-values.md)
4. 把“异常如何被收敛”跑通：ExceptionResolver / Error Flow / @ControllerAdvice
   - 阅读：[04. ExceptionResolver 与错误流](../part-03-web-mvc-internals/069-04-exception-resolvers-and-error-flow.md)
   - 阅读：[05. @ControllerAdvice 顺序](../part-03-web-mvc-internals/070-05-controlleradvice-matching-and-ordering.md)
5. 再回到应用层：把验证、异常、绑定、拦截器这些最常见需求接回主线
   - 阅读：[校验与错误塑形](../part-01-web-mvc/071-01-validation-and-error-shaping.md)
   - 阅读：[异常处理](../part-01-web-mvc/072-02-exception-handling.md)
   - 阅读：[绑定与 Converter](../part-01-web-mvc/073-03-binding-and-converters.md)
   - 阅读：[拦截器与过滤器顺序](../part-01-web-mvc/074-04-interceptor-and-filter-ordering.md)
6. 进入真实世界场景：REST 合同、CORS、文件上传下载、异步与测试
   - REST 合同：从 [01](../part-04-rest-contract/077-01-content-negotiation-406-415.md) 开始按目录推进
   - HTTP 场景：从 [01](../part-05-real-world-http/078-01-cors-preflight.md) 开始按目录推进
   - 异步/SSE：从 [01](../part-06-async-sse/079-01-servlet-async-and-testing.md) 开始按目录推进
   - 测试排障：[01](../part-07-testing-debugging/080-01-webmvc-testing-and-troubleshooting.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](../appendix/082-90-common-pitfalls.md)
- 自检：[99-self-check.md](../appendix/083-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Web MVC」的生效时机/顺序/边界；断点/入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Web MVC」的生效时机/顺序/边界；断点/入口：`org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Web MVC」的生效时机/顺序/边界；断点/入口：`org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootWebMvcInternalsLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
