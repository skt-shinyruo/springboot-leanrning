# 02. 深挖导读：把“HTTP → metrics/observations”落到源码与断点
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：深挖导读：把“HTTP → metrics/observations”落到源码与断点
    - 怎么使用：先跑 `BootObservabilityLabTest`，确认请求后 timer count 增长；再用断点回答：这个指标是谁记录的？在哪一层记录的？
    - 原理：观测信号的价值在于“可解释与可聚合”。从最小闭环开始：先证明 `http.server.requests` 存在，再理解它的来源与标签。
    - 源码入口：`MeterRegistry` / `Timer` /（MVC）`DispatcherServlet` /（Observation）`Observation`
    - 推荐 Lab：`BootObservabilityLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：springboot-observability](01-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. Observability 调用链（请求 → observation → meter）](03-http-observation-call-chain.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02. 深挖导读：把“HTTP → metrics/observations”落到源码与断点**
- 目标：你能从一个指标（`http.server.requests`）追溯到“它在哪一段链路被记录”，并能用断点验证。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootObservabilityLabTest`

## 小结与下一章

- 小结：观测信号的价值在于“可解释与可聚合”。从最小闭环开始：先证明 `http.server.requests` 存在，再理解它的来源与标签。
- 下一章：[第 205 章：01：调用链](03-http-observation-call-chain.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootObservabilityLabTest`

上一章：[part-00-guide/03-mainline-timeline.md](01-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/01-call-chain.md](03-http-observation-call-chain.md)

<!-- BOOKIFY:END -->
