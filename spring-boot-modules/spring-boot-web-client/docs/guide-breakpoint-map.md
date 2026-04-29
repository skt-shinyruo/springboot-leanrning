# 04. 断点地图（Web Client）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（Web Client）展开，主线可以概括为：Client 构建（baseUrl/filters）→ 编码/发请求 → 解码/错误映射 → 超时/重试策略影响控制流。

    先跑 `BootWebClientBranchMatrixLabTest` 固化“请求路径/headers/错误映射/超时/重试/Filter 顺序”的断言，再用断点把每个分支对应到 WebClient/RestClient 的关键拦截点。

    需要下探源码时，可以从 `org.springframework.web.reactive.function.client.WebClient` / `ExchangeFilterFunction` / Reactor operators（timeout/retry） 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Web Client](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

- Web Client 排障的第一原则：先确认分支属于哪一类：**编码/请求/响应解码/错误映射/超时/重试/Filter**。
- 证据链：测试断言（status/异常类型/请求数）→ 断点（分支发生点）→ 观察点（请求/响应关键字段）。

## 运行入口（先运行）

- Book Matrix：`BootWebClientBookMatrixLabTest`
- Branch Matrix：`BootWebClientBranchMatrixLabTest`

运行命令：

- `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`

## Filter 顺序断点（最常见误解点）

- 入口：`ExchangeFilterFunction#filter`

调试时重点观察：request 顺序 vs response 顺序（“洋葱模型”）


## 超时/重试断点（从现象回到 operator）

调试时重点观察：timeout 触发时异常类型、重试次数。

- 从测试断言反推：`webClientResponseTimeoutFailsFast` / `webClientRetriesOn5xxAndEventuallySucceeds`

## 观察点

- 请求：path/query/header（尤其是 correlation id）
- 响应：status code（400/500）→ 映射到领域异常
- retry 次数：MockWebServer request count
- filter trace：request/response 的顺序

## 排障入口（Playbook）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

Client 构建（baseUrl/filters）→ 编码/发请求 → 解码/错误映射 → 超时/重试策略影响控制流。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootWebClientBranchMatrixLabTest`
- Lab：`BootWebClientWebClientLabTest` / `BootWebClientRestClientLabTest` / `BootWebClientWebClientFilterOrderLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

