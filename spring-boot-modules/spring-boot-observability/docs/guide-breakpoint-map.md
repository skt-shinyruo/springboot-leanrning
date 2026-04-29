# 04. 断点地图（Observability）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（Observability）展开，主线可以概括为：先确认请求是否真的走到了 MVC，再确认观测信号是否被记录到 registry。

    遇到“指标没出现/标签不对/观测不到位”时，用本页断点把问题收敛到链路节点。

    对照入口：`BootObservabilityLabTest`。需要下探源码时，可以从 `MeterRegistry` / `Timer` / `DispatcherServlet#doDispatch` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. Observability 调用链（请求 → observation → meter）](guide-http-observation-call-chain.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵（Observability）](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootObservabilityLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：先确认请求是否真的走到了 MVC，再确认观测信号是否被记录到 registry。需要下探源码时，可以从 `MeterRegistry` / `Timer` / `DispatcherServlet#doDispatch` 这些入口切入。


## A. 先确认请求链路（MVC 主线）

断点可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` 这些位置开始。调试时重点观察：请求是否进入 `DispatcherServlet`，以及是否走到了 handler。


## B. 再确认指标是否写入

- 断点/观察动作：在 `BootObservabilityLabTest` 里直接观察 `MeterRegistry`

调试时重点观察：`http.server.requests` timer 是否存在、count 是否变化。


## 观察清单

- timer 名称：`http.server.requests`
- timer tags（如果需要扩展）：method/status/uri 等

## 小结与下一章

先确认请求是否真的走到了 MVC，再确认观测信号是否被记录到 registry。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootObservabilityLabTest`

上一章：[guide-http-observation-call-chain.md](guide-http-observation-call-chain.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
