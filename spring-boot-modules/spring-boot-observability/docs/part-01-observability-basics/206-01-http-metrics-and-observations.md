# 第 206 章：01：HTTP metrics（`http.server.requests` 从哪里来）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：HTTP metrics（`http.server.requests` 从哪里来）
    - 怎么使用：先跑 `BootObservabilityLabTest`，再按本文把“timer 出现”映射到链路节点与标签来源。
    - 原理：HTTP server metrics 是对请求链路的度量：耗时/状态码/URI 等信号被聚合进 meter。
    - 源码入口：`MeterRegistry` / `Timer` / `DispatcherServlet#doDispatch`
    - 推荐 Lab：`BootObservabilityLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 205 章：04：关键分支矩阵](../part-00-guide/205-04-branch-decision-matrix.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 207 章：90 - Common Pitfalls（springboot-observability）](../appendix/207-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 1. 先把“存在性”证明出来

本模块先不追求所有标签与采集细节，只追求可验证闭环：

- 请求一次 `/api/ping`
- `MeterRegistry` 中 `http.server.requests` timer count 增长

证据链入口：

- `BootObservabilityLabTest#httpRequestProducesHttpServerRequestsMetrics`

## 2. 再谈标签（tags）

当你进入真实工程排障，你需要理解：

- 这些指标通常带有 method/status/uri 等标签
- 标签过多会导致 metric cardinality 爆炸（需要取舍）

本模块的 Exercise 预留了“自定义 tag”的练习入口：

- `BootObservabilityExerciseTest`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootObservabilityLabTest`

上一章：[part-00-guide/04-branch-decision-matrix.md](../part-00-guide/205-04-branch-decision-matrix.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](../appendix/207-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
