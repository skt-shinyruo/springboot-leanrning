# springboot-autoconfiguration

## Purpose

学习 Spring Boot Auto-Configuration 的“导入 → 条件决策 → 产出 bean → 回退（backoff）”主线，并把关键行为固化为可断言的 Labs。

## Module Overview

- **Responsibility:** 用最小可运行实验复现 `AutoConfiguration.imports`、`@Conditional*`、条件评估报告与 backoff 的核心分支。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-23

## Start Here（路线图 / 断点地图 / 第一个可运行入口）

- 路线图：`helloagents/wiki/learning-path.md`
- Docs Start Here：`docs/autoconfig/springboot-autoconfiguration/README.md`
- 调用链（导入/条件/产出 bean）：`docs/autoconfig/springboot-autoconfiguration/part-00-guide/195-01-autoconfiguration-import-call-chain.md`
- 断点地图：`docs/autoconfig/springboot-autoconfiguration/part-00-guide/195-02-breakpoint-map.md`
- 第一个可运行入口（3 分钟开跑）：
  - `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationLabTest#autoConfigCreatesDefaultBeanWhenEnabled test`
  - 对应测试类：`spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationLabTest.java`

## Specifications

### Requirement: AutoConfiguration 导入与条件决策可观察
**Module:** springboot-autoconfiguration
覆盖 `AutoConfiguration.imports` 的导入链路、条件决策的关键分支，以及“为什么有时生效、有时不生效”的可复现证据链。

#### Scenario: 用户自定义 Bean 触发 backoff
- 用户提供 `GreetingService` → 默认 auto-config bean 必须 back off
- `demo.greeting.decorate=true` 时，装饰器只在存在默认 bean 时生效（避免误装饰用户实现）

## Docs & 复现入口

- **Docs Index:** `docs/autoconfig/springboot-autoconfiguration/README.md`
- **Deep Dive Guide:** `docs/autoconfig/springboot-autoconfiguration/part-00-guide/195-00-deep-dive-guide.md`
- **Call Chain:** `docs/autoconfig/springboot-autoconfiguration/part-00-guide/195-01-autoconfiguration-import-call-chain.md`
- **Breakpoint Map:** `docs/autoconfig/springboot-autoconfiguration/part-00-guide/195-02-breakpoint-map.md`
- **Branch Decision Matrix:** `docs/autoconfig/springboot-autoconfiguration/part-00-guide/195-04-branch-decision-matrix.md`
- **Playbook:** `docs/autoconfig/springboot-autoconfiguration/appendix/197-90-common-pitfalls.md`
- **Self-check:** `docs/autoconfig/springboot-autoconfiguration/appendix/198-99-self-check.md`
- **Lab（并发/性能：容器产物一致性/隔离性可断言复现）：** `spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part02_perf_concurrency/BootAutoConfigurationConcurrencyLabTest.java`
- **Solution（Exercises 对应答案回归）：** `spring-boot-modules/spring-boot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationExerciseSolutionTest.java`

## Change History

- [202601221758_tutorials_style_deepen_all](../../history/2026-01/202601221758_tutorials_style_deepen_all/) - ✅ 已执行：新增 `springboot-autoconfiguration` 模块（imports/条件/backoff）+ docs 骨架 + Labs；并纳入 docs/SUMMARY 与 labs-index
- [202601222034_solutions_perf_concurrency_batch01](../../history/2026-01/202601222034_solutions_perf_concurrency_batch01/) - ✅ 已执行：新增 Exercises 对应 Solution（独立 auto-config 分支 + backoff/顺序断言），默认参与回归验证
