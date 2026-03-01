# 01. HandlerMapping：路由、404/405 与 mapping 约束
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕「HandlerMapping：路由、404/405 与 mapping 约束」展开，目标是把“路由为什么命中/为什么不命中”落成可回归的事实：**在 `DispatcherServlet#doDispatch` 内部，`HandlerMapping` 决定是否能找到 handler；找不到多半是 404，路径命中但方法不支持多半是 405**。

    阅读时建议先用最小入口把现象固化成断言，再带着断点去看 `RequestMappingHandlerMapping#getHandlerInternal` 的分支。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. DispatcherServlet 主链路（把选路/参数解析/返回值/异常串起来）](../02-dispatcherservlet/01-dispatcherservlet-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. Interceptor 与 Filter：入口在哪里、顺序怎么理解](../04-handleradapter-interceptor/04-interceptor-and-filter-ordering.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

同样是“请求没到 controller”，有两类根因经常被混淆：

1. **没进入 MVC**：被 FilterChain（常见是 Security）拦截；
2. **进入了 MVC，但没选到 handler**：路由没有命中（常见就是 404），或者命中路径但方法不支持（常见就是 405）。

本章只聚焦第 2 类：进入 MVC 后，`HandlerMapping` 这一步发生了什么、哪些条件会影响选路、如何用最短证据链定位分支。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcSpringBootLabTest`（未知路由 → 404）
    - Lab：`BootWebMvcViewSpringBootLabTest`（未知页面路由 → 自定义 404 HTML）

## 关键对象（Key Objects）

- `org.springframework.web.servlet.DispatcherServlet#doDispatch`：MVC 总入口（需要先证明“确实进到了这里”）
- `org.springframework.web.servlet.HandlerMapping`：选路策略接口
- `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping`：最常见实现（注解式 `@RequestMapping`）
- `org.springframework.web.servlet.HandlerExecutionChain`：`handler + interceptors` 的组合（后续生命周期依赖它）

## 扩展点（Extension Points）

本章不强调“怎么写 controller”，而强调“哪些写法会改变选路结果”：

1. **`@RequestMapping/@GetMapping/...` 的约束条件**
   - path：路径匹配（最直观的 404 来源）
   - method：HTTP 方法（路径命中但 method 不支持 → 405）
   - `params/headers`：额外条件（常见“以为路径对了但还是不命中”的原因）
   - `consumes/produces`：把“内容协商”提前到选路阶段（很多 406/415 的根因并不在 converter 本身，而在 mapping 约束）

2. **静态资源/错误页并不走 controller**
   - 静态资源与 Boot 错误页是另一套 handler（仍然受 HandlerMapping/HandlerAdapter 体系影响），排障时不要只盯 controller。

## 常见分支（状态码 / 异常）

### 1) 404 Not Found：选路失败（没有 handler）

典型现象：

- 进了 `DispatcherServlet#doDispatch`，但 `mappedHandler == null`
- 最终返回 404（如果是 Spring Boot，通常会回落到 `/error` 的处理逻辑，见 C11）

### 2) 405 Method Not Allowed：路径命中但方法不支持

典型现象：

- 路径匹配到某组 mappings，但 `request.getMethod()` 不满足要求
- 最终通常由默认异常解析器把异常翻译成 405（本模块也演示了如何用 `@ExceptionHandler` 统一错误形状）

### 3) “看起来像 converter 的问题”，但根因是 mapping 约束

`produces/consumes` 写在 mapping 上时，选路阶段就会考虑它们：

- `consumes` 不满足时，可能直接走 415 分支
- `produces` 不满足时，可能直接走 406 分支

因此排障时建议先固定 3 个事实：

1. 请求头：`Accept` / `Content-Type`
2. mapping 约束：`produces` / `consumes`
3. 证据：`resolvedException` 的实际类型（不要只看 status）

## 证据链（断点 / 测试）

### 推荐断点（从“是否命中”开始）

- `org.springframework.web.servlet.DispatcherServlet#doDispatch`
  - 观察 `mappedHandler` 是否为 `null`
- `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#getHandlerInternal`
  - 观察命中的 mapping/候选集合与淘汰原因（方法/consumes/produces/headers/params）

### 最小可运行实验（把 404 固化成事实）

- JSON（未知 API 路由 → 404，且回落到 Boot error JSON）：
  - `BootWebMvcSpringBootLabTest#unknownRouteFallsBackToSpringBootErrorEndpoint`
- HTML（未知页面路由 → 404，且返回自定义 404 HTML）：
  - `BootWebMvcViewSpringBootLabTest#unknownRouteReturnsCustom404HtmlPage`

进一步排障入口（从“异常类型”反推阶段）：

- 断点地图：[`14-testing-observability/06-breakpoint-map.md`](../14-testing-observability/06-breakpoint-map.md)
- 测试与排障：[`14-testing-observability/01-webmvc-testing-and-troubleshooting.md`](../14-testing-observability/01-webmvc-testing-and-troubleshooting.md)

## 小结

- 404/405 的第一定位点不在 controller，而在 `HandlerMapping`：是否能找到 handler、为什么找不到。
- 当看到 406/415 时，不要跳过 mapping 约束（`produces/consumes`）；很多时候分支在“选路阶段”就已经决定。

