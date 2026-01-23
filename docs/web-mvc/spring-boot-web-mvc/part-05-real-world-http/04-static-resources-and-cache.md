# 04：静态资源与缓存（Static Resources / Cache-Control）

## 导读

- 本章主题：**04：静态资源与缓存（Static Resources / Cache-Control）**
- 目标：把“静态资源 404/样式不生效”的问题变成可复现：从 URL 到 classpath，再到响应头。

!!! summary "本章要点"

    - 默认情况下，Spring Boot 会从 `classpath:/static/` 提供静态资源（如 `/css/app.css`）。
    - 静态资源一旦纳入教学示例，就要用测试断言它确实能被访问到，否则前端页面示例会“偶尔坏”。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcRealWorldHttpLabTest`

## 机制主线

- 本章用 `BootWebMvcRealWorldHttpLabTest` 固定访问 `/css/app.css` 的可用性（状态码 + content-type + 内容片段）。
- 同一个 Lab 还包含一个“API 侧的最小 ETag/304”对照：
  - 访问 `/api/advanced/cache/etag` 拿到 `ETag`
  - 再带 `If-None-Match` 访问同一 URL 得到 304（不返回 body）

## 源码与断点

建议断点：
- `org.springframework.web.servlet.resource.ResourceHttpRequestHandler#handleRequest`
- 条件请求（ETag/304）的入口可从 `ServletWebRequest#checkNotModified` 的调用链反推（本模块示例为显式实现）。

## 最小可运行实验（Lab）

- Lab：`BootWebMvcRealWorldHttpLabTest`

## 常见坑与边界

- 使用 `@WebMvcTest` 时，静态资源链路可能与端到端上下文略有差异；如果你发现 slice 不稳定，优先在 `@SpringBootTest` 里再补一条端到端断言。

## 小结与下一章

- 下一章进入条件请求：把静态资源与 API 的 304 分支对照起来（Last-Modified/ETag/ETag Filter）。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcRealWorldHttpLabTest`

上一章：[part-05-real-world-http/03-download-and-streaming.md](03-download-and-streaming.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-05-real-world-http/05-conditional-requests-last-modified-etag-filter.md](05-conditional-requests-last-modified-etag-filter.md)

<!-- BOOKIFY:END -->
