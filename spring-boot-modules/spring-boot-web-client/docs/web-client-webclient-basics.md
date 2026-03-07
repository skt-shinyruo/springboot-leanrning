# 02. WebClient（响应式）最小闭环
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕02：WebClient（响应式）最小闭环展开，主线可以概括为：构建请求 → exchange/过滤器链 → 处理状态码与异常 → 超时/取消/重试策略 → 测试验证保证可重复。

    阅读时可以先跑 `BootWebClientWebClientLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `RestClient/WebClient` 发起对外 HTTP 调用；用 filter 链统一日志/鉴权/重试/超时；用 mock server 测试把外部依赖固定下来。

    需要下探源码时，可以从 `org.springframework.web.reactive.function.client.WebClient` / `org.springframework.web.reactive.function.client.ExchangeFilterFunction` / `org.springframework.web.reactive.function.client.ExchangeFunction` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. RestClient（同步）最小闭环](web-client-restclient-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. 错误处理：4xx/5xx → 领域异常](web-client-error-handling.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebClientWebClientLabTest`
    - Test file：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientWebClientLabTest.java`

## 机制主线

本章用最小示例跑通 WebClient：发请求、解析 JSON、用 StepVerifier 断言响应。

## 应当观察到的现象

- WebClient 返回的是 `Mono<T>`：成功/失败都在 reactive 流里表达（不是“抛异常/返回值”二选一）
- `StepVerifier` 能把“next/error/complete”写成确定性断言，比随手 `.block()` 更稳定

## 最小可运行实验（Lab）

- Lab：`BootWebClientWebClientLabTest`
- 建议命令：`mvn -pl :spring-boot-web-client test`（或在 IDE 直接运行上面的测试类）


## 常见坑与边界

### 坑点 1：用 `.block()` 代替 StepVerifier，导致“错误路径没测到/测试挂死”

测试看起来能跑通成功路径，但错误路径（4xx/5xx/timeout）没有任何断言；或者 `.block()` 没有超时导致卡住

- reactive 流的错误是信号（error signal），需要显式断言
- `.block()` 更接近“临时把响应式当同步用”，容易漏掉语义与边界

- StepVerifier 固定成功路径：`BootWebClientWebClientLabTest#webClientGetsGreeting`
- timeout 会失败（需要明确超时边界）：`BootWebClientWebClientLabTest#webClientResponseTimeoutFailsFast`

成功/失败都优先用 StepVerifier 写断言；不得不 block 时也要明确 timeout（并把超时作为可回归证据）

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientWebClientLabTest`
- Test file：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientWebClientLabTest.java`

上一章：[part-01-web-client/01-restclient-basics.md](web-client-restclient-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-web-client/03-error-handling.md](web-client-error-handling.md)

<!-- BOOKIFY:END -->
