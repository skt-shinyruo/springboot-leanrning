# 05. 测试策略：为什么用 MockWebServer？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕05：测试策略：为什么用 MockWebServer？展开，主线可以概括为：构建请求 → exchange/过滤器链 → 处理状态码与异常 → 超时/取消/重试策略 → 测试验证保证可重复。

    阅读时可以先跑 `BootWebClientRestClientLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `RestClient/WebClient` 发起对外 HTTP 调用；用 filter 链统一日志/鉴权/重试/超时；用 mock server 测试把外部依赖固定下来。

    需要下探源码时，可以从 `org.springframework.web.reactive.function.client.WebClient` / `org.springframework.web.reactive.function.client.ExchangeFilterFunction` / `org.springframework.web.reactive.function.client.ExchangeFunction` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 超时与重试（确定性实验）](web-client-timeout-and-retry.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 常见坑清单（Web Client）](appendix-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「05：测试策略：为什么用 MockWebServer？」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootWebClientRestClientLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

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

## 最小可运行实验（Lab）

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`
- 建议命令：`mvn -pl :spring-boot-web-client test`（或在 IDE 直接运行上面的测试类）


## 对应实验入口

- `BootWebClientRestClientLabTest#restClientSendsExpectedPathAndHeaders`
- `BootWebClientWebClientLabTest#webClientSendsExpectedPathAndHeaders`

## 常见坑与边界

### 坑点 1：为了验证“客户端内部行为”也上 MockWebServer，导致测试变慢/变脆

只是想验证 `WebClient` 的 filter 顺序、Header 组装、错误映射等“纯客户端逻辑”，却引入了 MockWebServer；测试需要开端口、写 enqueue、还可能出现 `InterruptedException` 或偶发超时。

MockWebServer 本质上是一个真实的 HTTP server（socket + 线程 + I/O）；当目标只是验证 **ExchangeFilterFunction 链路** 时，引入网络层会增加不确定性与成本。

`BootWebClientWebClientFilterOrderLabTest#webClientFilters_requestOrderAndResponseOrder_areDifferent`

`org.springframework.web.reactive.function.client.ExchangeFunctions$DefaultExchangeFunction#exchange`、`ExchangeFilterFunction` 链路（filter 的 request/response 包裹顺序）

把测试分层：

- 只测客户端链路（filters/错误映射）→ 用 `ExchangeFunction` stub
- 需要验证真实 HTTP 行为（path/query/body/headers/序列化）→ 再用 MockWebServer

学习 HTTP client 的最大坑之一是：不知道“请求到底发了什么”，以及 client 行为是否稳定（比如是否重试、header 是否注入、body 是否正确）。

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

上一章：[part-01-web-client/04-timeout-and-retry.md](web-client-timeout-and-retry.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](appendix-common-pitfalls.md)

<!-- BOOKIFY:END -->
