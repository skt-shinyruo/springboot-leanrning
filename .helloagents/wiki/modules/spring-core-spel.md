# spring-core-spel

## Purpose

学习 Spring Expression Language（SpEL）的解析与执行主线：parse → AST → evaluate；并补齐安全边界（不可信输入）与性能观察点（表达式缓存/重复解析成本）。

## Module Overview

- **Responsibility:** 用可运行 Lab 固定 SpEL 解析/求值的关键行为，并给出断点入口与可复现边界案例。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-22

## Start Here（路线图 / 断点地图 / 第一个可运行入口）

- 路线图：`helloagents/wiki/learning-path.md`
- Docs Start Here：`spring-core-modules/spring-core-spel/docs/README.md`
- 调用链（parse → AST → evaluate）：`spring-core-modules/spring-core-spel/docs/part-00-guide/03-spel-call-chain.md`
- 断点地图：`spring-core-modules/spring-core-spel/docs/part-00-guide/04-breakpoint-map.md`
- 第一个可运行入口（3 分钟开跑）：
  - `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelLabTest test`
  - 对应测试类：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelLabTest.java`

## Specifications

### Requirement: SpEL 调用链可被断点与测试验证
**Module:** spring-core-spel
覆盖：表达式解析、类型转换、访问属性/方法的边界行为，并给出“不要对不可信输入 eval”类型的安全提示。

## Docs & 复现入口

- **Docs Index:** `spring-core-modules/spring-core-spel/docs/README.md`
- **Deep Dive Guide:** `spring-core-modules/spring-core-spel/docs/part-00-guide/02-deep-dive-guide.md`
- **Call Chain:** `spring-core-modules/spring-core-spel/docs/part-00-guide/03-spel-call-chain.md`
- **Breakpoint Map:** `spring-core-modules/spring-core-spel/docs/part-00-guide/04-breakpoint-map.md`
- **Branch Decision Matrix:** `spring-core-modules/spring-core-spel/docs/part-00-guide/05-branch-decision-matrix.md`
- **Playbook:** `spring-core-modules/spring-core-spel/docs/appendix/01-common-pitfalls.md`
- **Self-check:** `spring-core-modules/spring-core-spel/docs/appendix/02-self-check.md`
- **Solution（Exercises 对应答案回归）：** `spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelExerciseSolutionTest.java`
- **Lab（并发/性能：可复现范式）：** `spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part02_perf_concurrency/SpringCoreSpelConcurrencyLabTest.java`

## Change History

- [202601221758_tutorials_style_deepen_all](../../history/2026-01/202601221758_tutorials_style_deepen_all/) - ✅ 已执行：新增 `spring-core-spel` 模块（parse/AST/evaluate）+ docs 骨架 + Labs；并纳入 docs/SUMMARY 与 labs-index
- [202601222034_solutions_perf_concurrency_batch01](../../history/2026-01/202601222034_solutions_perf_concurrency_batch01/) - ✅ 已执行：补齐 Exercises 对应 Solution（变量/函数注册）+ 新增并发求值 Lab（复用 parsed expression + per-thread EvaluationContext）
