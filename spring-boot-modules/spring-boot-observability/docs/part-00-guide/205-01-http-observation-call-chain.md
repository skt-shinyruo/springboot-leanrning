# 第 205 章：01：Observability 调用链（请求 → observation → meter）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Observability 调用链（请求 → observation → meter）
    - 怎么使用：先跑 `BootObservabilityLabTest`，再按本文把“请求发生一次 → 指标 count 增长”串成可复述调用链。
    - 原理：一次请求会触发 observation 的开始/结束；结束时会把耗时等数据写入 meter（timer/counter）。
    - 源码入口：`MeterRegistry` / `Timer` / `Observation` /（MVC）`DispatcherServlet#doDispatch`
    - 推荐 Lab：`BootObservabilityLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 205 章：00. 深挖导读](205-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 205 章：02：断点地图](205-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 你要能复述的“最短链路”

1. 发起请求（本模块：`/api/ping`）
2. 请求进入 Web 链路（FilterChain → MVC）
3. 链路中创建 observation（开始计时/记录标签）
4. 链路结束时结束 observation，并把数据写入 `MeterRegistry`
5. 你在 `MeterRegistry` 中能找到 `http.server.requests`，其 count 增长

本模块的证据链入口：

- `BootObservabilityLabTest#httpRequestProducesHttpServerRequestsMetrics`

## 小结与下一章

- 下一章给出“断点/观察点清单”，把链路落到可调试入口。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootObservabilityLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](205-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](205-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
