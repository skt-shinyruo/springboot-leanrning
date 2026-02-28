# 04. 断点地图（Observability Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕02：断点地图（Observability Debugger Pack）展开，主线可以概括为：先确认请求是否真的走到了 MVC，再确认观测信号是否被记录到 registry。

    遇到“指标没出现/标签不对/观测不到位”时，用本页断点把问题收敛到链路节点。

    对照入口：`BootObservabilityLabTest`。需要下探源码时，可以从 `MeterRegistry` / `Timer` / `DispatcherServlet#doDispatch` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. Observability 调用链（请求 → observation → meter）](03-http-observation-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 关键分支矩阵（Observability）](05-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootObservabilityLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：先确认请求是否真的走到了 MVC，再确认观测信号是否被记录到 registry。需要下探源码时，可以从 `MeterRegistry` / `Timer` / `DispatcherServlet#doDispatch` 这些入口切入。


## A. 先确认请求链路（MVC 主线）

断点可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` 这些位置开始。调试时建议重点盯：请求是否进入 `DispatcherServlet`，以及是否走到了 handler。


## B. 再确认指标是否写入

- 断点/观察建议：在 `BootObservabilityLabTest` 里直接观察 `MeterRegistry`

调试时建议重点盯：`http.server.requests` timer 是否存在、count 是否变化。


## Watch List

- timer 名称：`http.server.requests`
- timer tags（如果需要扩展）：method/status/uri 等

## 小结与下一章

先确认请求是否真的走到了 MVC，再确认观测信号是否被记录到 registry。

下一章见：[第 205 章：04：关键分支矩阵](05-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootObservabilityLabTest`

上一章：[part-00-guide/01-call-chain.md](03-http-observation-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](05-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
