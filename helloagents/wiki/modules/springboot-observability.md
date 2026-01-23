# springboot-observability

## Purpose

学习 Spring Boot 的可观测性主线：一次 HTTP 请求如何产出 observation/metrics（以 `http.server.requests` 为代表），以及如何用测试把观测行为锁住（避免仅靠肉眼看 Actuator）。

## Module Overview

- **Responsibility:** 提供最小 Web + Actuator 示例，并用测试断言 metrics/observations 行为与边界条件。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-23

## Start Here（路线图 / 断点地图 / 第一个可运行入口）

- 路线图：`helloagents/wiki/learning-path.md`
- Docs Start Here：`docs/observability/springboot-observability/README.md`
- 调用链（请求 → observation → meter）：`docs/observability/springboot-observability/part-00-guide/205-01-http-observation-call-chain.md`
- 断点地图：`docs/observability/springboot-observability/part-00-guide/205-02-breakpoint-map.md`
- 第一个可运行入口（3 分钟开跑）：
  - `mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityLabTest.java`

## Specifications

### Requirement: HTTP 指标可被测试验证
**Module:** springboot-observability
覆盖：请求触发 observation、meter 注册与指标读取，确保“观测链路”有稳定证据链。

#### Scenario: http.server.requests 指标存在且可读取
- 发起一次 HTTP 请求后，断言 meter registry 中存在相应指标

## Docs & 复现入口

- **Docs Index:** `docs/observability/springboot-observability/README.md`
- **Deep Dive Guide:** `docs/observability/springboot-observability/part-00-guide/205-00-deep-dive-guide.md`
- **Call Chain:** `docs/observability/springboot-observability/part-00-guide/205-01-http-observation-call-chain.md`
- **Breakpoint Map:** `docs/observability/springboot-observability/part-00-guide/205-02-breakpoint-map.md`
- **Branch Decision Matrix:** `docs/observability/springboot-observability/part-00-guide/205-04-branch-decision-matrix.md`
- **Playbook:** `docs/observability/springboot-observability/appendix/207-90-common-pitfalls.md`
- **Self-check:** `docs/observability/springboot-observability/appendix/208-99-self-check.md`
- **Lab（并发/性能：Observation scope ThreadLocal 隔离可断言复现）：** `spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part02_perf_concurrency/BootObservabilityConcurrencyLabTest.java`
- **Solution（Exercises 对应答案回归）：** `spring-boot-modules/spring-boot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityExerciseSolutionTest.java`

## Change History

- [202601221758_tutorials_style_deepen_all](../../history/2026-01/202601221758_tutorials_style_deepen_all/) - ✅ 已执行：新增 `springboot-observability` 模块（web+actuator+metrics）+ docs 骨架 + Labs；并纳入 docs/SUMMARY 与 labs-index
- [202601222034_solutions_perf_concurrency_batch01](../../history/2026-01/202601222034_solutions_perf_concurrency_batch01/) - ✅ 已执行：新增 Exercises 对应 Solution（commonTags/MeterFilter 固化 tag 断言），默认参与回归验证
