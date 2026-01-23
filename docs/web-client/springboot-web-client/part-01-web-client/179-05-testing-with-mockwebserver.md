# 第 179 章：05：测试策略：为什么用 MockWebServer？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：05：测试策略：为什么用 MockWebServer？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `RestClient/WebClient` 发起对外 HTTP 调用；用 filter 链统一日志/鉴权/重试/超时；用 mock server 测试把外部依赖固定下来。
    - 原理：构建请求 → exchange/过滤器链 → 处理状态码与异常 → 超时/取消/重试策略 → 测试验证保证可重复。
    - 源码入口：`org.springframework.web.reactive.function.client.WebClient` / `org.springframework.web.reactive.function.client.ExchangeFilterFunction` / `org.springframework.web.reactive.function.client.ExchangeFunction`
    - 推荐 Lab：`BootWebClientRestClientLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 178 章：04：超时与重试（确定性实验）](178-04-timeout-and-retry.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 180 章：90：常见坑清单（Web Client）](../appendix/180-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05：测试策略：为什么用 MockWebServer？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

## 机制主线


## 目的

MockWebServer 的优势：

- 在进程内启动一个可控的 HTTP server（不依赖外部网络）
- 能断言请求的：
  - method/path
  - headers
  - body
- 能精确控制响应：
  - status code
  - body
  - 延迟（用于 timeout）
  - 多次响应（用于 retry）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`
- 建议命令：`mvn -pl :spring-boot-web-client test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 对应实验入口

- `BootWebClientRestClientLabTest#restClientSendsExpectedPathAndHeaders`
- `BootWebClientWebClientLabTest#webClientSendsExpectedPathAndHeaders`

## 常见坑与边界

### 坑点 1：为了验证“客户端内部行为”也上 MockWebServer，导致测试变慢/变脆

- Symptom：你只是想验证 `WebClient` 的 filter 顺序、Header 组装、错误映射等“纯客户端逻辑”，却引入了 MockWebServer；测试需要开端口、写 enqueue、还可能出现 `InterruptedException` 或偶发超时。
- Root Cause：MockWebServer 本质上是一个真实的 HTTP server（socket + 线程 + I/O）；当目标只是验证 **ExchangeFilterFunction 链路** 时，引入网络层会增加不确定性与成本。
- Verification：`BootWebClientWebClientFilterOrderLabTest#webClientFilters_requestOrderAndResponseOrder_areDifferent`
- Breakpoints：`org.springframework.web.reactive.function.client.ExchangeFunctions$DefaultExchangeFunction#exchange`、`ExchangeFilterFunction` 链路（filter 的 request/response 包裹顺序）
- Fix：把测试分层：
  - 只测客户端链路（filters/错误映射）→ 用 `ExchangeFunction` stub
  - 需要验证真实 HTTP 行为（path/query/body/headers/序列化）→ 再用 MockWebServer

学习 HTTP client 的最大坑之一是：你不知道“请求到底发了什么”，以及你的 client 行为是否稳定（比如是否重试、header 是否注入、body 是否正确）。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

上一章：[part-01-web-client/04-timeout-and-retry.md](178-04-timeout-and-retry.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](../appendix/180-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
