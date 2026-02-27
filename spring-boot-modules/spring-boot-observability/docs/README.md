# Spring Boot Observability：请求链路到观测信号

本模块以一次 HTTP 请求为主线，把“观测信号从哪里来”跑成事实：metrics/observations 在哪一层被创建、标签（tag）在何处决定、以及这些信号如何落到 meter 与 registry。tracing 与 log correlation 属于更复杂的组合，本模块先把基础链路固定下来，再讨论扩展边界。

---

## 10 分钟入口：先跑通 `http.server.requests` 的来源

- `mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBookMatrixLabTest test`

运行后应能回答：一次请求会触发哪些 observation；meter 的创建与命名发生在何处；为何某些 tag 在某些场景下会缺失。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)
3. [Observability 调用链（请求 → observation → meter）](part-00-guide/03-http-observation-call-chain.md)
4. [断点地图（排障优先）](part-00-guide/04-breakpoint-map.md)
5. [关键分支矩阵（If/Then 收敛）](part-00-guide/05-branch-decision-matrix.md)

## 顺读主线

- [HTTP metrics：`http.server.requests` 从哪里来](part-01-observability-basics/01-http-metrics-and-observations.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBranchMatrixLabTest test`
- 并发/性能：`mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
