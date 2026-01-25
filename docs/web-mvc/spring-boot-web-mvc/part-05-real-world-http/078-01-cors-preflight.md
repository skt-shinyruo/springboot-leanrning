# 第 78 章：01：CORS 与预检（OPTIONS：浏览器为什么要先问一句）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：CORS 与预检（OPTIONS：浏览器为什么要先问一句）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootWebMvcRealWorldHttpLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 77 章：01：Content Negotiation（406/415：Accept/Content-Type/produces/consumes）](../part-04-rest-contract/077-01-content-negotiation-406-415.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 79 章：01：Servlet Async（Callable）与测试（asyncDispatch）](../part-06-async-sse/079-01-servlet-async-and-testing.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01：CORS 与预检（OPTIONS：浏览器为什么要先问一句）**
- 目标：用测试稳定复现“预检请求”和响应头，让你能解释：为什么前端明明没发 GET，却先发了 OPTIONS。

!!! summary "本章要点"

    - CORS 是浏览器约束：服务端必须用 `Access-Control-*` 响应头“授权”跨域访问。
    - 预检（preflight）是浏览器在某些请求条件下先发的 OPTIONS，用来确认方法/headers 是否被允许。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcRealWorldHttpLabTest`

## 机制主线

- 本章用 `BootWebMvcRealWorldHttpLabTest` 固定预检请求与响应头断言。

## 源码与断点

建议断点：
- `org.springframework.web.cors.DefaultCorsProcessor#processRequest`
- `org.springframework.web.servlet.handler.AbstractHandlerMapping#getHandler`

## 最小可运行实验（Lab）

- Lab：`BootWebMvcRealWorldHttpLabTest`

## 常见坑与边界

- 你只在后端放开 GET，但忘了允许前端带的自定义 header（例如 `X-Request-Id`），预检会失败。

## 小结与下一章

- 下一章进入 multipart 上传：为什么 `@RequestBody` 解析不了 `multipart/form-data`。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcRealWorldHttpLabTest`

上一章：[01](../part-04-rest-contract/077-01-content-negotiation-406-415.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01](../part-06-async-sse/079-01-servlet-async-and-testing.md)

<!-- BOOKIFY:END -->
