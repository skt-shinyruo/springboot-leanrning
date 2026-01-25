# （Redirect）Web Client 主线（旧入口）

<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：（Redirect）Web Client 主线（旧入口）
    - 怎么使用：用 `RestClient/WebClient` 发起对外 HTTP 调用；用 filter 链统一日志/鉴权/重试/超时；用 mock server 测试把外部依赖固定下来。
    - 原理：构建请求 → exchange/过滤器链 → 处理状态码与异常 → 超时/取消/重试策略 → 测试验证保证可重复。
    - 源码入口：`org.springframework.web.reactive.function.client.WebClient` / `org.springframework.web.reactive.function.client.ExchangeFilterFunction` / `org.springframework.web.reactive.function.client.ExchangeFunction`
    - 推荐 Lab：`BootWebClientWebClientLabTest`
<!-- CHAPTER-CARD:END -->

## 已迁移
本页为旧入口兼容页，正文已迁移到：[新位置](172-web-client-mainline.md)。

## 返回
- [模块目录](../README.md)
- [全书目录](/book/)
