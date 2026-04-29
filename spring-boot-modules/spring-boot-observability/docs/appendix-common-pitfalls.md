# 常见坑清单
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕常见坑清单（springboot-observability）展开，主线可以概括为：观测信号的常见误判来自：请求链路没走到、指标被过滤、或标签基数失控。

    遇到“指标没出现/标签不对/指标太多”时，用本页把问题收敛到链路节点与配置边界。

    对照入口：`BootObservabilityLabTest`。需要下探源码时，可以从 `MeterRegistry` / `DispatcherServlet#doDispatch` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. HTTP metrics（`http.server.requests` 从哪里来）](observability-basics-http-metrics-and-observations.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[自检题](appendix-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootObservabilityLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：观测信号的常见误判来自：请求链路没走到、指标被过滤、或标签基数失控。需要下探源码时，可以从 `MeterRegistry` / `DispatcherServlet#doDispatch` 这些入口切入。


## 坑 1：以为“没指标”就是没引入依赖

- 更常见：请求根本没走到预期中的链路（例如没进 MVC）
- 验证：断点 `DispatcherServlet#doDispatch`

## 坑 2：标签太多导致 metric 爆炸

- 真实工程需要控制 label cardinality（避免把用户输入当 tag）

## 小结与下一章

观测信号的常见误判来自：请求链路没走到、指标被过滤、或标签基数失控。

下一章见：[自检题](appendix-self-check.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootObservabilityLabTest`

上一章：[observability-basics-http-metrics-and-observations.md](observability-basics-http-metrics-and-observations.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[appendix-self-check.md](appendix-self-check.md)

<!-- BOOKIFY:END -->
