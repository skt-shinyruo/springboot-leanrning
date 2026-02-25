# 01. 90 - Common Pitfalls（springboot-observability）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Common Pitfalls（springboot-observability）
    - 怎么使用：遇到“指标没出现/标签不对/指标太多”时，用本页把问题收敛到链路节点与配置边界。
    - 原理：观测信号的常见误判来自：请求链路没走到、指标被过滤、或标签基数失控。
    - 源码入口：`MeterRegistry` / `DispatcherServlet#doDispatch`
    - 推荐 Lab：`BootObservabilityLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. HTTP metrics（`http.server.requests` 从哪里来）](../part-01-observability-basics/01-http-metrics-and-observations.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-observability）](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01. 90 - Common Pitfalls（springboot-observability）**
- 建议入口：优先运行 `BootObservabilityLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：观测信号的常见误判来自：请求链路没走到、指标被过滤、或标签基数失控。
- 源码入口：`MeterRegistry` / `DispatcherServlet#doDispatch`



## 坑 1：以为“没指标”就是没引入依赖

- 更常见：请求根本没走到你以为的链路（例如没进 MVC）
- 验证：断点 `DispatcherServlet#doDispatch`

## 坑 2：标签太多导致 metric 爆炸

- 真实工程需要控制 label cardinality（避免把用户输入当 tag）

## 小结与下一章

- 小结：观测信号的常见误判来自：请求链路没走到、指标被过滤、或标签基数失控。
- 下一章：[第 208 章：99 - Self Check（springboot-observability）](02-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootObservabilityLabTest`

上一章：[part-01-observability-basics/01-http-metrics.md](../part-01-observability-basics/01-http-metrics-and-observations.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](02-self-check.md)

<!-- BOOKIFY:END -->
