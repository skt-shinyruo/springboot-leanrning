# 02：SSE（SseEmitter：text/event-stream 最小闭环）

## 导读

- 本章主题：**02：SSE（SseEmitter：text/event-stream 最小闭环）**
- 目标：用“有限事件 + 快速完成”的方式，演示 SSE 的协议形状与最小实现。

!!! summary "本章要点"

    - SSE 是基于 HTTP 的单向推送：响应的 `Content-Type` 通常是 `text/event-stream`，body 由多条 `data:` 事件组成。
    - 教学示例里不要做“无限流”：测试会变得 flaky 或挂死。用 1–2 条事件后 `complete()` 更适合教学与回归。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcAsyncSseLabTest`

## 机制主线

- 本章用 `BootWebMvcAsyncSseLabTest` 固定 SSE：断言 `Content-Type` 与响应体包含 `data: ping-1`。

## 源码与断点

建议断点：
- `org.springframework.web.servlet.mvc.method.annotation.SseEmitter#send`
- `org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler#handleReturnValue`

## 最小可运行实验（Lab）

- Lab：`BootWebMvcAsyncSseLabTest`

## 常见坑与边界

- SSE 与代理/网关/超时设置强相关：本模块只讨论“MVC 侧最小实现与测试”，不扩展到生产部署策略。

## 小结与下一章

- 下一章进入 DeferredResult：回调式异步 + timeout/fallback 的可控分支，并补齐对应测试闭环。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcAsyncSseLabTest`

上一章：[part-06-async-sse/01-servlet-async-and-testing.md](079-01-servlet-async-and-testing.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-06-async-sse/03-deferredresult-and-timeout.md](03-deferredresult-and-timeout.md)

<!-- BOOKIFY:END -->
