# 01. 主线时间线：Spring Boot Web MVC
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕主线时间线：Spring Boot Web MVC展开，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcInternalsLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[00. 从注解到断点：用一条主线学会 Spring MVC](guide-from-annotations-to-breakpoints.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[深挖导读：Spring Boot Web MVC](guide-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

!!! summary
    - 这一模块关注：一次 HTTP 请求如何在 Spring MVC 中被“选路、绑定、执行、返回、异常处理”，以及如何在关键分支上定位问题。
    - 读完后应能复述：**DispatcherServlet → HandlerMapping → HandlerAdapter → 参数解析/消息转换 → 返回/异常** 这一条主线。
    - 阅读顺序：先读《知识地图/断点图》→ 本章 → 先把 Web MVC Internals 的主线跑通 → 再按场景扩展（REST、文件、异步、测试、安全）。

!!! example "先运行的 Lab（把时间线变成证据）"

    - Lab：`BootWebMvcInternalsLabTest`
## 导读

本章是“主线时间线：Spring Boot Web MVC”的路线图：先给出主线顺序与关键分支，再把每一段落到可运行入口。
先运行 `BootWebMvcInternalsLabTest` 作为主线证据，再回到正文理解“为什么章节按这个顺序组织”。

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Boot Web MVC」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读路径：
- 先看章首的“章节入口/本章要点”，建立预期；
- 先运行本章 Lab 固化现象，再回到正文对照机制。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- Web MVC 处在“应用对外入口”：它把 Servlet 容器的请求事件，翻译成 Controller 方法调用与响应输出。
- 调试 404/400/415/500、参数绑定失败、返回值序列化异常时，本质都在主线的某个分支上。

## 主线时间线（顺读路径：先把请求跑通）

1. 先拿到全图：知道“问题可能出现在主线的哪一段”
   - 知识地图：[01-knowledge-map.md](guide-knowledge-map.md)
   - 断点图：[02-breakpoint-map.md](testing-observability-breakpoint-map.md)
2. 把“入口调用链”跑通：DispatcherServlet#doDispatch 到底做了什么
   - 阅读：[01. DispatcherServlet 主链路](dispatcherservlet-call-chain.md)
3. 把“返回值如何出方法”跑通：MessageConverter / ReturnValueHandler
   - 阅读：[03. MessageConverter 与返回值](return-value-view-message-converters-and-return-values.md)
4. 把“异常如何被收敛”跑通：ExceptionResolver / Error Flow / @ControllerAdvice
   - 阅读：[04. ExceptionResolver 与错误流](exception-resolvers-and-error-flow.md)
   - 阅读：[05. @ControllerAdvice 顺序](exception-resolvers-controlleradvice-matching-and-ordering.md)
5. 再回到应用层：把验证、异常、绑定、拦截器这些最常见需求接回主线
   - 阅读：[校验与错误塑形](binding-validation-validation-and-error-shaping.md)
   - 阅读：[异常处理](exception-resolvers-exception-handling.md)
   - 阅读：[绑定与 Converter](binding-validation-binding-and-converters.md)
   - 阅读：[拦截器与过滤器顺序](handleradapter-interceptor-interceptor-and-filter-ordering.md)
6. 进入真实世界场景：REST 合同、CORS、文件上传下载、异步与测试
   - REST 合同：从 [01](message-conversion-content-negotiation-406-415.md) 开始按目录推进
   - HTTP 场景：从 [01](real-world-http-cors-preflight.md) 开始按目录推进
   - 异步/SSE：从 [01](async-sse-servlet-async-and-testing.md) 开始按目录推进
   - 测试排障：[01](testing-observability-webmvc-testing-and-troubleshooting.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](appendix-common-pitfalls.md)
- 自检：[99-self-check.md](appendix-self-check.md)

## 证据链（如何验证真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章入口后，聚焦「主线时间线：Spring Boot Web MVC」的生效时机/顺序/边界；断点/入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章入口后，聚焦「主线时间线：Spring Boot Web MVC」的生效时机/顺序/边界；断点/入口：`org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章入口后，聚焦「主线时间线：Spring Boot Web MVC」的生效时机/顺序/边界；断点/入口：`org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 动作：跑完 ``BootWebMvcInternalsLabTest`` 后，把上述观察点逐条对照，写出 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Boot Web MVC —— 先运行本章 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。
- 回到主线：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。
- 下一章：按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->
