# 03. 错误处理：4xx/5xx → 领域异常
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：03：错误处理：4xx/5xx → 领域异常
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `RestClient/WebClient` 发起对外 HTTP 调用；用 filter 链统一日志/鉴权/重试/超时；用 mock server 测试把外部依赖固定下来。
    - 原理：构建请求 → exchange/过滤器链 → 处理状态码与异常 → 超时/取消/重试策略 → 测试验证保证可重复。
    - 源码入口：`org.springframework.web.reactive.function.client.WebClient` / `org.springframework.web.reactive.function.client.ExchangeFilterFunction` / `org.springframework.web.reactive.function.client.ExchangeFunction`
    - 推荐 Lab：`BootWebClientRestClientLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. WebClient（响应式）最小闭环](02-webclient-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 超时与重试（确定性实验）](04-timeout-and-retry.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. 错误处理：4xx/5xx → 领域异常**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，应当能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

## 机制主线

本章把“HTTP 状态码”变成“领域异常”，并对比 RestClient 与 WebClient 的写法。

## 应当观察到的现象

- 关键不在于“抛什么异常”，而在于：
  - 能在测试里固定“哪些状态码映射成什么异常”
  - 异常里最好包含 status（用于上层分类处理：重试/降级/告警）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`
- 建议命令：`mvn -pl :spring-boot-web-client test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 实验入口

<!-- BOOKLIKE-V2:EVIDENCE:START -->
实验入口已在章首提示框给出（先跑再读）。建议跑完后回到本章“证据链”逐条验证关键结论。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## 常见坑与边界

### 坑点 1：把底层异常直接抛给业务层，导致“上层无法分流处理”

- Symptom：上层只拿到一个 `RuntimeException/RestClientException`，无法区分 4xx/5xx/超时；重试/告警/降级都做不了
- Root Cause：没有把 HTTP 状态码映射成领域异常（并携带 status 作为分类依据）
- Verification：
  - RestClient：`BootWebClientRestClientLabTest#restClientMaps400ToDomainException`
  - WebClient：`BootWebClientWebClientLabTest#webClientMaps500ToDomainException`
- Fix：把“状态码 → 领域异常”固定成测试断言，并让异常携带 status（用于上层分流）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

上一章：[part-01-web-client/02-webclient-basics.md](02-webclient-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-web-client/04-timeout-and-retry.md](04-timeout-and-retry.md)

<!-- BOOKIFY:END -->
