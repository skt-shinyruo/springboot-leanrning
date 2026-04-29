# 深挖导读：Spring Boot Web Client
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章用于把模块主线、源码入口与断点路径串起来，主线可以概括为：构建请求 → exchange/过滤器链 → 处理状态码与异常 → 超时/取消/重试策略 → 测试验证保证可重复。

    阅读时可以先跑 `BootWebClientRestClientLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `RestClient/WebClient` 发起对外 HTTP 调用；用 filter 链统一日志/鉴权/重试/超时；用 mock server 测试把外部依赖固定下来。

    需要下探源码时，可以从 `org.springframework.web.reactive.function.client.WebClient` / `org.springframework.web.reactive.function.client.ExchangeFilterFunction` / `org.springframework.web.reactive.function.client.ExchangeFunction` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：Spring Boot Web Client](guide-mainline-timeline.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. RestClient（同步）最小闭环](web-client-restclient-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

本章用于说明本模块如何阅读、如何验证，以及遇到分支时从哪里下断点。
先运行 `BootWebClientRestClientLabTest` 获得可复现现象，再带着断言/观察点回到正文对照机制。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest` / `BootWebClientWebClientFilterOrderLabTest`

## 机制主线

本模块把“调用下游服务”拆成三件可回归的事：

1. **选择客户端与线程模型**：`RestClient`（同步） vs `WebClient`（响应式）
2. **把失败变成稳定语义**：错误码/异常不要“漏到业务层”，要映射成领域异常
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


断点入口（先从自己的 client 代码入手，再下探框架）：

- 请求构造点：确认 path/query/header 是否按预期拼出来
- 错误映射点：确认“哪些异常/状态码”被映射为 `DownstreamServiceException`
- 超时与重试点：确认 timeout 生效位置与 retry 条件（避免把 4xx 也重试）
- 响应式链路（WebClient）：在 `StepVerifier` 的断言处对齐“事件序列”（next/error/complete）

## 最小可运行实验（Lab）

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest` / `BootWebClientWebClientFilterOrderLabTest`
- 运行命令：`mvn -pl :spring-boot-web-client test`（或在 IDE 直接运行上面的测试类）


## 验证目标
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

如果是带着线上问题来的，先对照本模块 Appendix（common pitfalls/self-check），再回到主线章节逐一核对。

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest` / `BootWebClientWebClientFilterOrderLabTest`
- Exercise：`BootWebClientExerciseTest`

上一章：[模块目录](../README.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[web-client-restclient-basics.md](web-client-restclient-basics.md)

<!-- BOOKIFY:END -->
