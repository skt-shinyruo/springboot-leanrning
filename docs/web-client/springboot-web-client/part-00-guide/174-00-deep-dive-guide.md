# 第 174 章：00 - Deep Dive Guide（springboot-web-client）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Deep Dive Guide（springboot-web-client）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `RestClient/WebClient` 发起对外 HTTP 调用；用 filter 链统一日志/鉴权/重试/超时；用 mock server 测试把外部依赖固定下来。
    - 原理：构建请求 → exchange/过滤器链 → 处理状态码与异常 → 超时/取消/重试策略 → 测试验证保证可重复。
    - 源码入口：`org.springframework.web.reactive.function.client.WebClient` / `org.springframework.web.reactive.function.client.ExchangeFilterFunction` / `org.springframework.web.reactive.function.client.ExchangeFunction`
    - 推荐 Lab：`BootWebClientRestClientLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 173 章：主线时间线：Spring Boot Web Client](173-03-mainline-timeline.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 175 章：01：RestClient（同步）最小闭环](../part-01-web-client/175-01-restclient-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**00 - Deep Dive Guide（springboot-web-client）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest` / `BootWebClientWebClientFilterOrderLabTest`

## 机制主线

本模块把“调用下游服务”拆成三件可回归的事：

1. **选择客户端与线程模型**：`RestClient`（同步） vs `WebClient`（响应式）
2. **把失败变成稳定语义**：错误码/异常不要“漏到业务层”，要映射成你的领域异常
3. **让不稳定变得可测试**：用 MockWebServer 固定下游行为（超时、5xx、慢响应、错误体）

### 1) 时间线：一次下游调用从发起到返回/失败

1. 构造请求（URL/路径/查询参数/headers）
2. 发起调用（同步阻塞或返回 Mono）
3. 解析响应（JSON → 结构化对象）
4. 失败分流（4xx/5xx/超时/连接失败）
5. 选择性重试（通常只对幂等且可承受的场景）

### 2) 关键参与者（把“行为”与“证据”对齐）

- `RestClientGreetingClient`：同步调用路径（异常类型与超时语义更直观）
- `WebClientGreetingClient`：响应式调用路径（用 `StepVerifier` 做可断言验证）
- `MockWebServer`：固定下游响应（让“超时/重试/错误码”可复现）
- `DownstreamServiceException`：把下游失败映射成领域异常（避免业务层耦合 HTTP 细节）

### 3) 本模块的关键分支（2–5 条，默认可回归）

1. **错误码映射：4xx/5xx → 领域异常（携带 status）**
   - 验证：`BootWebClientRestClientLabTest#restClientMaps400ToDomainException` / `BootWebClientWebClientLabTest#webClientMaps500ToDomainException`
2. **超时失败快：慢响应不会“卡死”调用方**
   - 验证：`BootWebClientRestClientLabTest#restClientReadTimeoutFailsFast` / `BootWebClientWebClientLabTest#webClientResponseTimeoutFailsFast`
3. **5xx 重试可回归：指定次数内最终成功 + 请求次数可断言**
   - 验证：`BootWebClientRestClientLabTest#restClientRetriesOn5xxAndEventuallySucceeds` / `BootWebClientWebClientLabTest#webClientRetriesOn5xxAndEventuallySucceeds`
4. **请求契约固定：路径与关键 header 可断言（避免“悄悄改坏”）**
   - 验证：`BootWebClientRestClientLabTest#restClientSendsExpectedPathAndHeaders` / `BootWebClientWebClientLabTest#webClientSendsExpectedPathAndHeaders`

5. **Filter 顺序：request 顺序 ≠ response 顺序（避免“以为同序”）**
   - 验证：`BootWebClientWebClientFilterOrderLabTest#webClientFilters_requestOrderAndResponseOrder_areDifferent`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。
  
建议断点（先从你自己的 client 代码入手，再下探框架）：

- 请求构造点：确认 path/query/header 是否按预期拼出来
- 错误映射点：确认“哪些异常/状态码”被映射为 `DownstreamServiceException`
- 超时与重试点：确认 timeout 生效位置与 retry 条件（避免把 4xx 也重试）
- 响应式链路（WebClient）：在 `StepVerifier` 的断言处对齐“事件序列”（next/error/complete）

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest` / `BootWebClientWebClientFilterOrderLabTest`
- 建议命令：`mvn -pl :spring-boot-web-client test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 推荐学习目标
1. 能区分 RestClient 与 WebClient 的适用场景与线程模型
2. 能写出可控的错误处理（不要让调用方被底层异常细节污染）
3. 能把超时/重试与幂等性/雪崩风险关联起来思考
4. 能用 MockWebServer 写出“可复现”的客户端测试

## 如何跑实验
- 运行本模块测试：`mvn -pl :spring-boot-web-client test`

## 对应 Lab（可运行）

- `BootWebClientRestClientLabTest`
- `BootWebClientWebClientLabTest`
- `BootWebClientWebClientFilterOrderLabTest`
- `BootWebClientExerciseTest`

## 常见坑与边界

- （本章坑点待补齐：建议先跑一次 E，再回看断言失败场景与边界条件。）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest` / `BootWebClientWebClientFilterOrderLabTest`
- Exercise：`BootWebClientExerciseTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-web-client/01-restclient-basics.md](../part-01-web-client/175-01-restclient-basics.md)

<!-- BOOKIFY:END -->
