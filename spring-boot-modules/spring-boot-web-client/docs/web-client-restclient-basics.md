# 01. RestClient（同步）最小闭环
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕01：RestClient（同步）最小闭环展开，主线可以概括为：构建请求 → exchange/过滤器链 → 处理状态码与异常 → 超时/取消/重试策略 → 测试验证保证可重复。

    阅读时可以先跑 `BootWebClientRestClientLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `RestClient/WebClient` 发起对外 HTTP 调用；用 filter 链统一日志/鉴权/重试/超时；用 mock server 测试把外部依赖固定下来。

    需要下探源码时，可以从 `org.springframework.web.reactive.function.client.WebClient` / `org.springframework.web.reactive.function.client.ExchangeFilterFunction` / `org.springframework.web.reactive.function.client.ExchangeFunction` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 00 - Deep Dive Guide（springboot-web-client）](guide-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. WebClient（响应式）最小闭环](web-client-webclient-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebClientRestClientLabTest`
    - Test file：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientRestClientLabTest.java`

## 机制主线

本章用最小示例跑通 RestClient：发请求、解析 JSON、断言请求路径/headers。

## 应当观察到的现象

- RestClient 是阻塞式（blocking）调用：直接返回 `GreetingResponse`
- MockWebServer 能让人断言“请求到底发了什么”（path/header/body），比手工抓包更可控

## 最小可运行实验（Lab）

- Lab：`BootWebClientRestClientLabTest`
- 建议命令：`mvn -pl :spring-boot-web-client test`（或在 IDE 直接运行上面的测试类）


## 常见坑与边界

### 坑点 1：只断言响应，不断言请求契约，导致“悄悄把下游调用改坏”

测试只验证返回值，后来有人改了 path/header/query 参数，线上集成才暴雷

下游调用的“契约”不仅是响应结构，还包括请求路径、查询参数与 headers

`BootWebClientRestClientLabTest#restClientSendsExpectedPathAndHeaders`

用 MockWebServer 固定下游，并把 path/header/body 写成断言（把契约变成回归断言）

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientRestClientLabTest`
- Test file：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientRestClientLabTest.java`

上一章：[part-00-guide/00-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-web-client/02-webclient-basics.md](web-client-webclient-basics.md)

<!-- BOOKIFY:END -->
