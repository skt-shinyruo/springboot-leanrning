# 第 75 章：05：Interceptor 的生命周期（sync vs async：为什么会“回调少了一截”）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：05：Interceptor 的生命周期（sync vs async：为什么会“回调少了一截”）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootWebMvcTraceLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 74 章：04：Interceptor 与 Filter：入口在哪里、顺序怎么理解](074-04-interceptor-and-filter-ordering.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 76 章：01：传统 MVC 页面渲染入门（@Controller / ViewName / Thymeleaf）](../part-02-view-mvc/076-01-thymeleaf-and-view-resolver.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05：Interceptor 的生命周期（sync vs async：为什么会“回调少了一截”）**
- 目标：把 Interceptor 在 **同步请求** 与 **异步请求**（Callable/DeferredResult/SSE）下的回调差异讲清，并用可运行 Lab 把“顺序/分支”固定成证据链。

!!! summary "本章要点"

    - 同步（sync）请求：Interceptor 的典型回调顺序是 `preHandle → postHandle → afterCompletion`。
    - 异步（async）请求的**第一次 dispatch**：通常只有 `preHandle`，并且会触发 `afterConcurrentHandlingStarted`，而 **不会触发** `postHandle/afterCompletion`。
    - 异步（async）请求的**第二次 dispatch（asyncDispatch）**：会再次进入 `preHandle → postHandle → afterCompletion`，但 handler 方法通常不会再次执行（结果已由 async 线程产生）。
    - 这也是为什么：你把“计时/埋点/日志”写在 `postHandle`，在 async 场景下可能会“少打一半日志”——你需要同时理解 `afterConcurrentHandlingStarted` 与二次 dispatch。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcTraceLabTest`

## 机制主线（把 async 视为“两次 dispatch”）

把 async 看成 **两阶段**，你会更容易理解“为什么回调少了”：

1. **第一次 dispatch（REQUEST）**
   - 进入 DispatcherServlet，选路与参数解析照常发生
   - handler 返回 `Callable` / `DeferredResult` / `SseEmitter` → WebAsyncManager 启动并发处理
   - 这时 Interceptor 会触发 `afterConcurrentHandlingStarted`

2. **第二次 dispatch（ASYNC）**
   - async 线程把结果写回 WebAsyncManager
   - 容器触发一次新的 dispatch（在测试里体现为 `asyncDispatch`）
   - DispatcherServlet 再跑一遍处理流程（但 handler 通常不会再执行）
   - Interceptor 这一次会完整触发 `preHandle/postHandle/afterCompletion`

## 源码与断点（建议从 Lab 反推）

建议断点（配合本章 Lab）：
- `org.springframework.web.servlet.DispatcherServlet#doDispatch`
- `org.springframework.web.servlet.HandlerExecutionChain#applyPreHandle`
- `org.springframework.web.servlet.HandlerExecutionChain#applyPostHandle`
- `org.springframework.web.servlet.HandlerExecutionChain#triggerAfterCompletion`
- `org.springframework.web.servlet.AsyncHandlerInterceptor#afterConcurrentHandlingStarted`

关键观察点（决定性分支）：

- `request.getDispatcherType()`：第一次通常是 REQUEST，二次 asyncDispatch 是 ASYNC
- 是否进入 `afterConcurrentHandlingStarted`：它是“第一次 async dispatch”最典型的证据
- handler 方法是否二次执行：通常不会（第二次 dispatch 主要是把 async 结果写回）

## 最小可运行实验（Lab）

本章使用“事件序列”把 lifecycle 变成可断言结果：

- Lab：`BootWebMvcTraceLabTest`
- 对应端点：
  - sync：`GET /api/advanced/trace/sync`
  - async：`GET /api/advanced/trace/async`

建议命令（方法级入口）：

```bash
mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcTraceLabTest#asyncTraceRecordsAfterConcurrentHandlingStartedAndAsyncDispatchCallbacks test
```

你在测试里会看到两类证据：

1. sync：完整回调链路（含 Filter finally 的 after）
2. async：第一次 dispatch 出现 `afterConcurrentHandlingStarted`，第二次 dispatch 出现 `postHandle/afterCompletion`

## 常见坑与边界

- **坑 1：只在 postHandle 做观测**
  - async 第一次 dispatch 不会进 postHandle，你会“漏掉一半日志/埋点”。
  - 处理策略：观测逻辑要么放在 Filter（更外层），要么同时覆盖 async lifecycle（`afterConcurrentHandlingStarted` + 二次 dispatch）。

- **坑 2：把 async 当作“只是换了线程”**
  - 实际上它改变了 DispatcherServlet 的回调时机：你看到的行为差异通常来自“两次 dispatch”的结构。

- **坑 3：测试只写一次 perform，不做 asyncDispatch**
  - 你只能看到“asyncStarted”，看不到最终响应；这会让排障停留在“感觉”而不是“证据”。

## 小结与下一章

- 本章完成后：建议回到 Part 06 对照 Callable/DeferredResult/SSE 的实现与测试方式，形成“生命周期解释 → 可跑证据链”的闭环。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcTraceLabTest`

上一章：[拦截器与过滤器顺序](074-04-interceptor-and-filter-ordering.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01](../part-02-view-mvc/076-01-thymeleaf-and-view-resolver.md)

<!-- BOOKIFY:END -->
