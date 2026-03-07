# 99 自检：Spring Boot Observability
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘出口）"

    - 主线入口：`BootObservabilityBookMatrixLabTest`
    - 分支入口：`BootObservabilityBranchMatrixLabTest`
    - 推荐先跑：`BootObservabilityLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 90 - Common Pitfalls（springboot-observability）](appendix-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](guide-breakpoint-map.md)
- [关键分支矩阵](guide-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](appendix-common-pitfalls.md)

## 自检题（每题都能落到 tests/断点）

1. 如何用一条可回归证据证明：一次 HTTP 请求会产生 `http.server.requests` 指标，并且计数递增？
   - 证据入口：`BootObservabilityLabTest#httpRequestProducesHttpServerRequestsMetrics`
2. 为什么“跑一次请求再看 Meter”比“直接找配置”更可靠？如何把“指标存在”写成断言？
   - 证据入口：`BootObservabilityLabTest#httpRequestProducesHttpServerRequestsMetrics`
3. `ObservationRegistry` 在 Spring Boot 中默认是否可用？如何避免“以为开启了但其实没有”的错觉？
   - 证据入口：`BootObservabilityLabTest#observationRegistryIsAvailableInBoot`
4. 给指标加 tag 的正确姿势是什么？如何验证一个 tag 真的出现在 meter 上？
   - 证据入口：`BootObservabilityExerciseSolutionTest#solution_addCustomTagAndVerifyItAppearsInMeters`
5. 为什么 tag 会带来风险（尤其是高基数）？会用什么手段把 tag 收敛成“可控集合”？
   - 证据入口：`BootObservabilityExerciseSolutionTest#solution_addCustomTagAndVerifyItAppearsInMeters`（对照其中的 `MeterFilter.commonTags(...)`）
6. 会把断点下在什么位置，观察一次请求从 MVC 入口到指标写入的链路？（写出 1 个入口点即可）
   - 证据导航：[`../part-00-guide/04-breakpoint-map.md`](guide-breakpoint-map.md)
7. Observation scope 是否会跨线程泄漏？如何用并发实验把它固定成结论？
   - 证据入口：`BootObservabilityConcurrencyLabTest#observationScope_isThreadLocal_andDoesNotLeak_underConcurrency`
8. 练习：为 `http.server.requests` 增加一个“可解释的维度”（例如 feature），并写断言锁定“不会无限膨胀”。
   - 入口：`BootObservabilityExerciseTest`（对照 Solution：`BootObservabilityExerciseSolutionTest`）

## 退出条件（完成标准）

- 能把“观察（metrics）”落到可回归事实：跑请求 → 指标出现/计数变化 → 写断言。
- 能解释 tag 的收益与风险，并能给出一个“可控 tag 策略”（有断言/过滤器兜底）。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootObservabilityLabTest`

上一章：[appendix/90-common-pitfalls.md](appendix-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
