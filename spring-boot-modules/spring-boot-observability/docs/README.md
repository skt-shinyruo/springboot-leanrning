# Spring Boot Observability：目录

> 建议先把“HTTP 请求 → 观测信号（metrics/observations）”跑通，再进入更复杂的 tracing/log correlation。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/204-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/205-00-deep-dive-guide.md)
3. [Observability 调用链（请求 → observation → meter）](part-00-guide/205-01-http-observation-call-chain.md)
4. [断点地图（排障优先）](part-00-guide/205-02-breakpoint-map.md)
5. [关键分支矩阵（If/Then 收敛）](part-00-guide/205-04-branch-decision-matrix.md)

## 顺读主线

- [HTTP metrics：`http.server.requests` 从哪里来](part-01-observability-basics/206-01-http-metrics-and-observations.md)

## 进阶入口（可跑入口/关键分支）

- 可跑入口（Book Matrix）：`mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBranchMatrixLabTest test`
- 可跑入口（Perf/Concurrency Lab）：`mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/207-90-common-pitfalls.md)
- [自检](appendix/208-99-self-check.md)
