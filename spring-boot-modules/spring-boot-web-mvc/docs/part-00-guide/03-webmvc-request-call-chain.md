# 03. 请求调用链速览（从 FilterChain 到 DispatcherServlet#doDispatch）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：请求调用链速览（从 FilterChain 到 DispatcherServlet#doDispatch）
    - 怎么使用：把它当作“打断点前的路线图”。先跑一条最小请求（推荐 Lab），用断言固定现象；再按本文的“阶段 → 入口 → 分支”把断点打在分支发生处；最后去 Part 03 的详版调用链把细节读透。
    - 原理：一次 MVC 请求通常经历：FilterChain（含 Security/Observability）→ `DispatcherServlet#doDispatch`（选路）→ `HandlerAdapter#handle`（调用）→ 参数解析/绑定/校验 → 返回值处理/消息转换 → `HandlerExceptionResolver`（异常翻译）→ 写回响应。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#getHandlerInternal` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#handleInternal` / `org.springframework.web.servlet.HandlerExceptionResolver`
    - 推荐 Lab：`BootWebMvcInternalsLabTest` / `BootWebMvcTraceLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 00 - Deep Dive Guide（springboot-web-mvc）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 关键分支矩阵（Web MVC Branch Decision Matrix）](04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「01：请求调用链速览（从 FilterChain 到 DispatcherServlet#doDispatch）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootWebMvcInternalsLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

- 本章主题：**03. 请求调用链速览（从 FilterChain 到 DispatcherServlet#doDispatch）**
- 目标：把“Web MVC 是 DispatcherServlet”升级为“我能按阶段定位：在哪一段决定了 404/405/400/406/415/500，以及 async 为什么会二次 dispatch”。
- 备注：本章是“速览与定位”，详版调用链请读：
  - [DispatcherServlet 主链路（详）](../part-03-web-mvc-internals/01-dispatcherservlet-call-chain.md)
  - [异常收敛与错误流（详）](../part-03-web-mvc-internals/04-exception-resolvers-and-error-flow.md)

!!! summary "你需要能背下来的 1 句话"

    Filter 决定“能不能进 MVC”（401/403/CORS/metrics），`DispatcherServlet` 决定“路由与模型”（404/405/handler 形态），`HandlerAdapter` 决定“怎么调用”（参数解析/绑定/消息转换），Resolver 链决定“错误长什么样”（状态码/错误体）。

## 1. 调用链一眼图（从外到内）

你可以把一次典型请求压缩成下面的阶段序列（先记阶段，再去记入口）：

1. **Servlet 容器**
   - `FilterChain#doFilter`（可见：Security、Observations/Metrics、CORS、CharacterEncoding…）
2. **MVC 入口**
   - `DispatcherServlet#doDispatch`（主入口：拿 handler、选 adapter、执行 interceptor、收尾）
3. **选路**
   - `HandlerMapping#getHandler` → `HandlerExecutionChain`（handler + interceptors）
4. **调用**
   - `HandlerAdapter#handle`（最常见：`RequestMappingHandlerAdapter` 调 `HandlerMethod`）
5. **参数解析 / 绑定 / 校验**
   - `HandlerMethodArgumentResolverComposite` / `WebDataBinder` / Bean Validation
6. **返回值 / 消息转换**
   - `HandlerMethodReturnValueHandlerComposite` / `HttpMessageConverter`
7. **异常翻译**
   - `HandlerExceptionResolverComposite`（含 `ExceptionHandlerExceptionResolver` / `ResponseStatusExceptionResolver` / `DefaultHandlerExceptionResolver`）
8. **写回响应 & 收尾**
   - `HttpServletResponse` / View 渲染 / interceptor `afterCompletion`

## 2. 关键对象：你在断点里“应该盯住什么”

> 这些对象不要求你背字段，但你要知道它们分别属于哪个阶段、能解释哪类分支。

- `HandlerExecutionChain`：决定了 handler 是谁、哪些 interceptor 会执行（以及为什么会短路）
- `HandlerMethod`：把 controller 方法包装成“可被反射调用”的模型（参数/注解/返回值）
- `ModelAndView`：视图渲染路径的输出（REST 场景常常为 `null`）
- `WebAsyncManager`：判断是否进入 async 分支（`isConcurrentHandlingStarted`）
- `Exception` / `resolvedException`（测试里常见）：把“异常路径”变成可断言的证据

## 3. 分支速查：看到某个状态码，你第一反应该去哪段看？

把“现象 → 最可能发生的阶段”固化下来，你排障会快很多：

1. **401/403**
   - 多数发生在 FilterChain（尤其 Security），经常还没到 `DispatcherServlet`
2. **404**
   - MVC 内常见原因：找不到 handler（`HandlerMapping` 未命中）
3. **405**
   - MVC 内常见原因：URL 命中 mapping，但 HTTP method 不匹配（同一路径不同 method）
4. **400**
   - 常见原因：参数缺失 / 类型不匹配 / 绑定失败 / 校验失败（ArgumentResolver/Binder/Validation）
5. **406/415**
   - 常见原因：内容协商（`Accept`）与 converter/produces 不匹配（406），或 `Content-Type` 与 converter/consumes 不匹配（415）
6. **500（或你看到的“某种 JSON 错误体”）**
   - 关键：异常是否被 `HandlerExceptionResolver` 链处理；若被处理，controller 的返回值往往已经不重要
7. **async（请求像是“跑了两遍”）**
   - 关键：`asyncStarted` → `asyncDispatch` 的二阶段流程；Interceptor 回调也会出现“两次 dispatch”行为

> 这些分支的“可跑证据链”与“断点建议”请对照：
>
> - 断点地图：`part-00-guide/06-breakpoint-map.md`
> - 错误分支矩阵（可回归）：`BootWebMvcErrorBranchMatrixLabTest`

## 4. 最小可运行证据链（先把现象固化为断言）

推荐先跑一个最小请求，把“路由 → handler → 返回值”跑通：

```bash
mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcLabTest#pingEndpointReturnsPong test
```

然后跑“内部机制”证据链，把参数解析阶段变成可观测对象：

```bash
mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcInternalsLabTest test
```

最后再用“追踪链路”把 Filter/Interceptor/async 的生命周期看清楚：

```bash
mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcTraceLabTest test
```

## 小结与下一章

- 本章提供了“定位与打断点”的速览地图；下一章把常见状态码与分支收敛成可复用的 If/Then 矩阵。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
- Lab：`BootWebMvcInternalsLabTest`
- Lab：`BootWebMvcTraceLabTest`
- Lab：`BootWebMvcErrorBranchMatrixLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

