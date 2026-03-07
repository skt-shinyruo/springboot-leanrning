# 05. 关键分支矩阵（Observability）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕04：关键分支矩阵（Observability）展开，主线可以概括为：请求链路存在 → observation/meter 记录 → 指标可查询。

    把“请求后指标是否出现”变成可复现分支：每个分支都有入口（测试）与断点锚点。

    对照入口：`BootObservabilityLabTest`。需要下探源码时，可以从 `DispatcherServlet#doDispatch` / `MeterRegistry` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Observability Debugger Pack）](guide-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. HTTP metrics（`http.server.requests` 从哪里来）](observability-basics-http-metrics-and-observations.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootObservabilityLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：请求链路存在 → observation/meter 记录 → 指标可查询。需要下探源码时，可以从 `DispatcherServlet#doDispatch` / `MeterRegistry` 这些入口切入。


## 分支矩阵（最小闭环）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 断点锚点（Breakpoint） |
|---|---|---|---|---|
| 指标出现 | 请求一次 `/api/ping` | `http.server.requests` timer count 增长 | `BootObservabilityLabTest#httpRequestProducesHttpServerRequestsMetrics` | `DispatcherServlet#doDispatch` |

## 小结与下一章

请求链路存在 → observation/meter 记录 → 指标可查询。

下一章见：[第 206 章：01：HTTP metrics](observability-basics-http-metrics-and-observations.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootObservabilityLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-observability-basics/01-http-metrics.md](observability-basics-http-metrics-and-observations.md)

<!-- BOOKIFY:END -->
