# 05：条件请求（Last-Modified / If-Modified-Since / ETag / ShallowEtagHeaderFilter）

## 导读

- 本章主题：**05：条件请求（Last-Modified / If-Modified-Since / ETag / ShallowEtagHeaderFilter）**
- 目标：把“为什么我明明点了刷新却拿到 304”讲清楚：条件请求不是 bug，而是 HTTP 协议的正常分支；并用测试把静态资源与 API 的两条路径固定下来。

!!! summary "本章要点"

    - 304 的本质：**资源未变化** → 允许服务端不返回 body（节省带宽与时间），但仍然是一次成功的交互。
    - 两条常见路径：
      1. **Last-Modified / If-Modified-Since**：常用于静态资源（基于修改时间）
      2. **ETag / If-None-Match**：常用于内容型资源（基于内容指纹），也可以通过 Filter 自动计算
    - 本模块提供三种对照，帮助你“工程落地时知道选哪种”：
      - 静态资源：`Last-Modified → If-Modified-Since → 304`
      - API 显式 ETag：controller 自己计算并返回 `ETag`，自己判断是否 304
      - Filter 计算 ETag：controller 只返回 body，`ShallowEtagHeaderFilter` 负责 ETag/304


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcRealWorldHttpLabTest`

## 机制主线（对照三条路径）

### 1) 静态资源：Last-Modified 路径

- 第一次请求：拿到 `Last-Modified`
- 第二次请求：带 `If-Modified-Since`，若未变化 → 304

这条路径的关键点是：**它不需要你手写 ETag**，但它依赖“时间戳是否能正确代表变化”。

### 2) API 显式 ETag：可控但要自己维护

典型策略：
- body 变化就换 ETag（例如基于内容的 hash 或基于版本号）
- 如果请求头 `If-None-Match` 命中 → 返回 304

优点：契约可控；缺点：你必须决定“ETag 的计算与缓存策略”。

### 3) ShallowEtagHeaderFilter：框架级 ETag/304

Filter 会在响应写回时：
- 缓存一份 body
- 计算 ETag
- 与请求头 `If-None-Match` 对比
- 命中则直接把响应改为 304

工程落地边界：
- 它会缓存响应体，因此不适合大流量/大 body/流式响应场景
- 更适合教学与小响应体 API 的对照理解

## 源码与断点

建议断点：
- 静态资源：`org.springframework.web.servlet.resource.ResourceHttpRequestHandler#handleRequest`
- 条件判断：`org.springframework.web.context.request.ServletWebRequest#checkNotModified`
- ETag Filter：`org.springframework.web.filter.ShallowEtagHeaderFilter#doFilterInternal`（或其内部更新 ETag 的分支）

## 最小可运行实验（Lab）

- Lab：`BootWebMvcRealWorldHttpLabTest`
  - `staticResourceSupportsIfModifiedSince304`
  - `apiEtagSupportsConditionalGet304`
  - `shallowEtagHeaderFilterSupportsConditionalGet304`

## 常见坑与边界

- **坑 1：把 304 当成错误**
  - 304 是正常的成功分支；排障要看的是“为什么命中”而不是“为什么不是 200”。

- **坑 2：全局开启 ETag Filter**
  - ETag Filter 会缓存响应体；不做 scope 容易引入不可预期的性能与内存开销。
  - 本模块示例用“限定路径”的方式控制影响面（只对 `/api/advanced/cache/filter-etag` 生效）。

- **坑 3：流式/大响应体上使用 Shallow ETag**
  - 这类响应更适合用协议/缓存头做策略，而不是强行缓存 body 计算 ETag。

## 小结与下一章

- 本章完成后：进入 Async & SSE，理解“为什么某些响应不是同步完成的”（而条件请求是同步完成但返回 304）。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcRealWorldHttpLabTest`

上一章：[part-05-real-world-http/04-static-resources-and-cache.md](04-static-resources-and-cache.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-06-async-sse/01-servlet-async-and-testing.md](../part-06-async-sse/079-01-servlet-async-and-testing.md)

<!-- BOOKIFY:END -->

