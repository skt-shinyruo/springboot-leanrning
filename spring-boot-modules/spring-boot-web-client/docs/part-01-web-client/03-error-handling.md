# 03. 错误处理：4xx/5xx → 领域异常
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕03：错误处理：4xx/5xx → 领域异常展开，主线可以概括为：构建请求 → exchange/过滤器链 → 处理状态码与异常 → 超时/取消/重试策略 → 测试验证保证可重复。

    阅读时可以先跑 `BootWebClientRestClientLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `RestClient/WebClient` 发起对外 HTTP 调用；用 filter 链统一日志/鉴权/重试/超时；用 mock server 测试把外部依赖固定下来。

    需要下探源码时，可以从 `org.springframework.web.reactive.function.client.WebClient` / `org.springframework.web.reactive.function.client.ExchangeFilterFunction` / `org.springframework.web.reactive.function.client.ExchangeFunction` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. WebClient（响应式）最小闭环](02-webclient-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 超时与重试（确定性实验）](04-timeout-and-retry.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

## 机制主线

本章把“HTTP 状态码”变成“领域异常”，并对比 RestClient 与 WebClient 的写法。

## 应当观察到的现象

- 关键不在于“抛什么异常”，而在于：
  - 能在测试里固定“哪些状态码映射成什么异常”
  - 异常里最好包含 status（用于上层分类处理：重试/降级/告警）

## 最小可运行实验（Lab）

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`
- 建议命令：`mvn -pl :spring-boot-web-client test`（或在 IDE 直接运行上面的测试类）


## 常见坑与边界

### 坑点 1：把底层异常直接抛给业务层，导致“上层无法分流处理”

上层只拿到一个 `RuntimeException/RestClientException`，无法区分 4xx/5xx/超时；重试/告警/降级都做不了

没有把 HTTP 状态码映射成领域异常（并携带 status 作为分类依据）

- RestClient：`BootWebClientRestClientLabTest#restClientMaps400ToDomainException`
- WebClient：`BootWebClientWebClientLabTest#webClientMaps500ToDomainException`

把“状态码 → 领域异常”固定成测试断言，并让异常携带 status（用于上层分流）

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

上一章：[part-01-web-client/02-webclient-basics.md](02-webclient-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-web-client/04-timeout-and-retry.md](04-timeout-and-retry.md)

<!-- BOOKIFY:END -->
