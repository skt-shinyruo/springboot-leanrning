# 05. 关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕04：关键分支矩阵（Branch Decision Matrix）展开，主线可以概括为：分支通常发生在：status→异常映射、reactive operator（timeout/retry）、filter 的“洋葱”顺序。

    把 Web Client 最常见的分支（错误映射/超时/重试/filter 顺序）整理成矩阵表；每行都对应一个最小复现入口。

    对照入口：`BootWebClientBranchMatrixLabTest`。需要下探源码时，可以从 `WebClient#retrieve` / `ExchangeFilterFunction` / Reactor `timeout` / `retry` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Web Client Debugger Pack）](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. RestClient（同步）最小闭环](../part-01-web-client/01-restclient-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootWebClientBranchMatrixLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：分支通常发生在：status→异常映射、reactive operator（timeout/retry）、filter 的“洋葱”顺序。需要下探源码时，可以从 `WebClient#retrieve` / `ExchangeFilterFunction` / Reactor `timeout` / `retry` 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 4xx/5xx 映射 | 下游返回 400/500 | 映射成领域异常并携带 status | `BootWebClientWebClientLabTest` | exception 类型 / status |
| 请求/响应 Filter 顺序 | 链式 filter | request 顺序与 response 顺序相反 | `BootWebClientWebClientFilterOrderLabTest` | trace 列表顺序 |
| 超时 | 下游延迟超过 timeout | 快速失败（异常抛出） | `BootWebClientWebClientLabTest#webClientResponseTimeoutFailsFast` | timeout 异常与耗时 |
| 重试 | 5xx 且配置 retry | 重试后成功（请求数增加） | `BootWebClientWebClientLabTest#webClientRetriesOn5xxAndEventuallySucceeds` | request count / retry 次数 |
| JSON 解码容错 | response 含未知字段 | 默认忽略未知字段 | `BootWebClientWebClientLabTest#webClientIgnoresUnknownJsonFieldsByDefault` | ObjectMapper 配置 |

## 推荐运行命令

- `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

分支通常发生在：status→异常映射、reactive operator（timeout/retry）、filter 的“洋葱”顺序。

下一章见：[第 175 章：01：RestClient 基础：请求构造与错误处理](../part-01-web-client/01-restclient-basics.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootWebClientBranchMatrixLabTest`
- Lab：`BootWebClientWebClientLabTest` / `BootWebClientRestClientLabTest` / `BootWebClientWebClientFilterOrderLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-web-client/01-restclient-basics.md](../part-01-web-client/01-restclient-basics.md)

<!-- BOOKIFY:END -->

