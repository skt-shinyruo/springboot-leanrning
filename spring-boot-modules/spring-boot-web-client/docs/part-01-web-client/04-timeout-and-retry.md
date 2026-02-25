# 04. 超时与重试（确定性实验）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：超时与重试（确定性实验）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `RestClient/WebClient` 发起对外 HTTP 调用；用 filter 链统一日志/鉴权/重试/超时；用 mock server 测试把外部依赖固定下来。
    - 原理：构建请求 → exchange/过滤器链 → 处理状态码与异常 → 超时/取消/重试策略 → 测试验证保证可重复。
    - 源码入口：`org.springframework.web.reactive.function.client.WebClient` / `org.springframework.web.reactive.function.client.ExchangeFilterFunction` / `org.springframework.web.reactive.function.client.ExchangeFunction`
    - 推荐 Lab：`BootWebClientRestClientLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. 错误处理：4xx/5xx → 领域异常](03-error-handling.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 测试策略：为什么用 MockWebServer？](05-testing-with-mockwebserver.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04. 超时与重试（确定性实验）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

## 机制主线


## 应当观察到的现象

- timeout 不是“线上偶现参数”，应该能在测试里用确定性方式复现
- retry 不是“救命稻草”，它会放大下游压力，必须有明确边界（哪些错误、最大次数、是否 backoff）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`
- 建议命令：`mvn -pl :spring-boot-web-client test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

本章重点是：把 timeout/retry 做成 **可测** 的实验，避免“线上偶现、测试不稳定”。

## 实验入口

- RestClient 超时：
  - `BootWebClientRestClientLabTest#restClientReadTimeoutFailsFast`
- WebClient 超时：
  - `BootWebClientWebClientLabTest#webClientResponseTimeoutFailsFast`
- 重试：
  - `BootWebClientRestClientLabTest#restClientRetriesOn5xxAndEventuallySucceeds`
  - `BootWebClientWebClientLabTest#webClientRetriesOn5xxAndEventuallySucceeds`

- MockWebServer 通过延迟响应复现 timeout（比连不可达地址更稳定）
- 重试需要明确边界：
  - 对哪些错误重试（通常是 5xx 或网络错误）
  - 最大次数
  - 是否有 backoff

## 常见坑与边界

### 坑点 1：把重试当成默认策略，导致放大故障（甚至把 4xx 也重试）

- Symptom：下游 5xx 时请求数量暴涨；或对 4xx（逻辑错误）仍然重试，浪费资源
- Root Cause：重试条件与上限不清晰；没有用测试锁定“重试只发生在该发生的时候”
- Verification：
  - timeout 可确定性复现：`BootWebClientRestClientLabTest#restClientReadTimeoutFailsFast` / `BootWebClientWebClientLabTest#webClientResponseTimeoutFailsFast`
  - 5xx 重试可回归 + 请求次数可断言：`BootWebClientRestClientLabTest#restClientRetriesOn5xxAndEventuallySucceeds` / `BootWebClientWebClientLabTest#webClientRetriesOn5xxAndEventuallySucceeds`
- Fix：只对“你确认可重试且幂等”的失败重试（通常是网络错误/5xx），并把最大次数与行为写成断言（比如 request count）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

上一章：[part-01-web-client/03-error-handling.md](03-error-handling.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-web-client/05-testing-with-mockwebserver.md](05-testing-with-mockwebserver.md)

<!-- BOOKIFY:END -->
