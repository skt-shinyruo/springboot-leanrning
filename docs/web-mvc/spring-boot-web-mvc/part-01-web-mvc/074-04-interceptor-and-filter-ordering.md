# 第 74 章：04：Interceptor 与 Filter：入口在哪里、顺序怎么理解
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：Interceptor 与 Filter：入口在哪里、顺序怎么理解
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootWebMvcErrorViewLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 73 章：03：请求绑定（Binding）与 Converter/Formatter](073-03-binding-and-converters.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 75 章：05：Interceptor 的生命周期（sync vs async：为什么会“回调少了一截”）](075-05-interceptor-async-lifecycle.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04：Interceptor 与 Filter：入口在哪里、顺序怎么理解**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcErrorViewLabTest` / `BootWebMvcLabTest`
    - Test file：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`

## 机制主线

本章的目标不是把 Filter/Interceptor 全部讲完，而是建立一个“入口与顺序”的最小心智模型：你知道它们分别在链路的哪里、什么时候该用哪个。

## 你应该观察到什么

- Interceptor 是 MVC 层能力：更贴近 handler 调用（controller 前后）
- Filter 更贴近 Servlet 容器：更接近“请求最外层”

## 机制解释（Why）

- **Filter**：发生在 Servlet 容器层，通常对所有请求都可能生效（除非你按 URL pattern 配置）
- **Interceptor**：发生在 Spring MVC handler 执行链内，便于针对某一类 handler 路由做增强

如果你是“想对 /api/** 生效”，并且增强逻辑与 handler 相关（比如给 response 增 header、记录耗时），Interceptor 往往更直观。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

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
- 建议命令（方法级入口）：
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcTraceLabTest#syncTraceRecordsFilterAndInterceptorOrder test`

### 复现/验证补充说明（来自原文迁移）

## 实验入口

<!-- BOOKLIKE-V2:EVIDENCE:START -->
实验入口已在章首提示框给出（先跑再读）。建议跑完后回到本章“证据链”逐条验证关键结论。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## Debug 建议

- 写测试优先选 `MockMvc`：它能稳定复现 handler 链路并断言结果（比手工 curl 更可控）。

建议断点（按“顺序时间线”）：

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

- 把“顺序问题”当成“业务问题”：很多 401/403/302/304/406 并不在 controller 内发生，先确认你处于 Filter 还是 Interceptor 还是 MessageConverter 阶段。
- 只用 Interceptor 解决跨域/认证：CORS 与认证通常发生在 Filter（尤其是 Security FilterChain）层，Interceptor 更适合做“靠近 handler 的增强”（计时/审计/统一 header）。
- async 场景回调缺失：异步请求第一次 dispatch 可能不触发 `postHandle/afterCompletion`，要结合 `afterConcurrentHandlingStarted` 与二次 dispatch 理解（详见下一章）。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcErrorViewLabTest` / `BootWebMvcLabTest` / `BootWebMvcTraceLabTest`
- Exercise：`BootWebMvcExerciseTest`
- Test file：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`

上一章：[绑定与 Converter](073-03-binding-and-converters.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[异步拦截器生命周期](075-05-interceptor-async-lifecycle.md)

<!-- BOOKIFY:END -->
