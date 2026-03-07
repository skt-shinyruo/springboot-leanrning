# 05. 条件请求（Last-Modified / If-Modified-Since / ETag / ShallowEtagHeaderFilter）

## 导读

本章围绕「05：条件请求（Last-Modified / If-Modified-Since / ETag / ShallowEtagHeaderFilter）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootWebMvcRealWorldHttpLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcRealWorldHttpLabTest`

## 机制主线（对照三条路径）

### 1) 静态资源：Last-Modified 路径

- 第一次请求：拿到 `Last-Modified`
- 第二次请求：带 `If-Modified-Since`，若未变化 → 304

这条路径的关键点是：**它不需要手写 ETag**，但它依赖“时间戳是否能正确代表变化”。

### 2) API 显式 ETag：可控但要自己维护

典型策略：
- body 变化就换 ETag（例如基于内容的 hash 或基于版本号）
- 如果请求头 `If-None-Match` 命中 → 返回 304

优点：契约可控；缺点：必须决定“ETag 的计算与缓存策略”。

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

- 本章完成后：补齐反向代理场景下的“真实请求语义”（Forwarded/X-Forwarded-*），避免 scheme/host/prefix 在生产环境里悄悄变形。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcRealWorldHttpLabTest`

上一章：[04. 静态资源与缓存（Static Resources / Cache-Control）](real-world-http-static-resources-and-cache.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06. 反向代理与 Forwarded Headers（X-Forwarded-*：scheme/host/prefix/ip 的真实边界）](real-world-http-forwarded-headers-and-proxy.md)
<!-- BOOKIFY:END -->
