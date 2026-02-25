# 05. 关键分支矩阵（Observability）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Observability）
    - 怎么使用：把“请求后指标是否出现”变成可复现分支：每个分支都有入口（测试）与断点锚点。
    - 原理：请求链路存在 → observation/meter 记录 → 指标可查询。
    - 源码入口：`DispatcherServlet#doDispatch` / `MeterRegistry`
    - 推荐 Lab：`BootObservabilityLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Observability Debugger Pack）](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. HTTP metrics（`http.server.requests` 从哪里来）](../part-01-observability-basics/01-http-metrics-and-observations.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05. 关键分支矩阵（Observability）**
- 建议入口：优先运行 `BootObservabilityLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：请求链路存在 → observation/meter 记录 → 指标可查询。
- 源码入口：`DispatcherServlet#doDispatch` / `MeterRegistry`



## 分支矩阵（最小闭环）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 断点锚点（Breakpoint） |
|---|---|---|---|---|
| 指标出现 | 请求一次 `/api/ping` | `http.server.requests` timer count 增长 | `BootObservabilityLabTest#httpRequestProducesHttpServerRequestsMetrics` | `DispatcherServlet#doDispatch` |

## 小结与下一章

- 小结：请求链路存在 → observation/meter 记录 → 指标可查询。
- 下一章：[第 206 章：01：HTTP metrics](../part-01-observability-basics/01-http-metrics-and-observations.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootObservabilityLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-observability-basics/01-http-metrics.md](../part-01-observability-basics/01-http-metrics-and-observations.md)

<!-- BOOKIFY:END -->
