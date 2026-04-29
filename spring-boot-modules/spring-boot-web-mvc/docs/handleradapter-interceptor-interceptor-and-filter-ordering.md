# 04. Interceptor 与 Filter：入口在哪里、顺序怎么理解
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：Interceptor 与 Filter：入口在哪里、顺序怎么理解展开，主线可以概括为：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。

    阅读时可以先跑 `BootWebMvcErrorViewLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. HandlerMapping：路由、404/405 与 mapping 约束](handlermapping-routing.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[02. ArgumentResolver 与 Binder（参数从哪来、校验在哪触发）](argument-resolver-and-binder.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcErrorViewLabTest` / `BootWebMvcLabTest`
    - 测试文件：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`

## 机制主线

本章的目标不是把 Filter/Interceptor 全部讲完，而是建立一个“入口与顺序”的最小心智模型：知道它们分别在链路的哪里、什么时候该用哪个。

## 应当观察到的现象

- Interceptor 是 MVC 层能力：更贴近 handler 调用（controller 前后）
- Filter 更贴近 Servlet 容器：更接近“请求最外层”

## 机制解释（Why）

- **Filter**：发生在 Servlet 容器层，通常对所有请求都可能生效（除非按 URL pattern 配置）
- **Interceptor**：发生在 Spring MVC handler 执行链内，便于针对某一类 handler 路由做增强

如果是“想对 /api/** 生效”，并且增强逻辑与 handler 相关（比如给 response 增 header、记录耗时），Interceptor 往往更直观。

### 主链路（Call-chain sketch）

把一次 sync 请求粗略分成三段，会更容易定位“顺序问题”：

1. FilterChain（Servlet 容器外层）
2. DispatcherServlet#doDispatch（进入 MVC）
3. HandlerExecutionChain（Interceptor 链）→ handler method → 返回值写回

当请求进入 async，链路会变成“两次 dispatch”（下一章详述）：

- 第一次 REQUEST：会进入 `afterConcurrentHandlingStarted`
- 第二次 ASYNC：才会进入 `postHandle/afterCompletion`

## 最小可运行实验（Lab）

- 本章用“事件序列”把顺序变成可断言证据：
- Lab：`BootWebMvcTraceLabTest`（Filter vs Interceptor 的相对位置 + async lifecycle 对照）
- 运行命令（方法级入口）：
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcTraceLabTest#syncTraceRecordsFilterAndInterceptorOrder test`


## Debug 路径

- 写测试优先选 `MockMvc`：它能稳定复现 handler 链路并断言结果（比手工 curl 更可控）。

断点入口（按“顺序时间线”）：

- Filter 层（最外层）：`OncePerRequestFilter#doFilter`
- MVC 层入口：`DispatcherServlet#doDispatch`
- Interceptor 链：
  - `HandlerExecutionChain#applyPreHandle`
  - `HandlerExecutionChain#applyPostHandle`
  - `HandlerExecutionChain#triggerAfterCompletion`
- async 特殊回调：`AsyncHandlerInterceptor#afterConcurrentHandlingStarted`

关键观察点（决定性分支）：

- `request.getDispatcherType()`：
  - REQUEST：第一次 dispatch
  - ASYNC：二次 dispatch（asyncDispatch）
  - ERROR：错误页/错误分发（很多“为什么进不到 controller”的问题在这里暴露）

## 常见坑与边界

- 把“顺序问题”当成“业务问题”：很多 401/403/302/304/406 并不在 controller 内发生，先确认处于 Filter 还是 Interceptor 还是 MessageConverter 阶段。
- 只用 Interceptor 解决跨域/认证：CORS 与认证通常发生在 Filter（尤其是 Security FilterChain）层，Interceptor 更适合做“靠近 handler 的增强”（计时/审计/统一 header）。
- async 场景回调缺失：异步请求第一次 dispatch 可能不触发 `postHandle/afterCompletion`，要结合 `afterConcurrentHandlingStarted` 与二次 dispatch 理解（详见下一章）。

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootWebMvcErrorViewLabTest` / `BootWebMvcLabTest`
- Lab：`BootWebMvcTraceLabTest`
- Exercise：`BootWebMvcExerciseTest`
- 测试文件：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`

上一章：[01. HandlerMapping：路由、404/405 与 mapping 约束](handlermapping-routing.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[02. ArgumentResolver 与 Binder（参数从哪来、校验在哪触发）](argument-resolver-and-binder.md)
<!-- BOOKIFY:END -->
