# 03. Observability 调用链（请求 → observation → meter）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：Observability 调用链（请求 → observation → meter）展开，主线可以概括为：一次请求会触发 observation 的开始/结束；结束时会把耗时等数据写入 meter（timer/counter）。

    先跑 `BootObservabilityLabTest`，再按本章把“请求发生一次 → 指标 count 增长”串成可复述调用链。

    需要下探源码时，可以从 `MeterRegistry` / `Timer` / `Observation` /（MVC）`DispatcherServlet#doDispatch` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖导读：把“HTTP → metrics/observations”落到源码与断点](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. 断点地图（Observability）](guide-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootObservabilityLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：一次请求会触发 observation 的开始/结束；结束时会把耗时等数据写入 meter（timer/counter）。需要下探源码时，可以从 `MeterRegistry` / `Timer` / `Observation` /（MVC）`DispatcherServlet#doDispatch` 这些入口切入。


## 应能复述的“最短链路”

1. 发起请求（本模块：`/api/ping`）
2. 请求进入 Web 链路（FilterChain → MVC）
3. 链路中创建 observation（开始计时/记录标签）
4. 链路结束时结束 observation，并把数据写入 `MeterRegistry`
5. 在 `MeterRegistry` 中能找到 `http.server.requests`，其 count 增长

本模块的证据链入口：

- `BootObservabilityLabTest#httpRequestProducesHttpServerRequestsMetrics`

## 小结与下一章

- 下一章给出“断点/观察点清单”，把链路落到可调试入口。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootObservabilityLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-breakpoint-map.md](guide-breakpoint-map.md)

<!-- BOOKIFY:END -->
