# Change Proposal: 推广 Book Matrix（关键分支矩阵入口）到全模块

## Requirement Background

当前仓库已经完成：

- Maven 模块按 `tutorials` 风格分组（`spring-boot-modules/` + `spring-core-modules/`）
- 文档体系（`docs/` + `docs/book/` + docs-site）与模块 Labs/Exercises 的闭环
- 以 `springboot-web-mvc` 为首批示范：新增“错误分支矩阵”Lab，并绑定到 Book 主线章节

但在“可执行入口”的一致性上仍有一个明显缺口：**每个模块缺少一个统一、可一键运行的“关键分支矩阵入口”**（尤其在 Book 主线中更需要一个稳定的“进阶入口”）。

因此需要把 Web MVC 的示范模式推广到所有模块：为每个模块提供一个“Book Matrix”入口（一个命令即可跑完该模块主线 + 关键分支的最小集合），并把入口写进 Book 与知识库索引中。

## Change Content

1. 为每个模块新增一个 `*BookMatrixLabTest`（基于 JUnit Platform Suite 聚合现有 Labs，避免重复实现）。
2. 在 Book 主线章节中统一补充“进阶：Book Matrix（关键分支矩阵入口）”的可运行命令。
3. 在知识库模块页（`helloagents/wiki/modules/*.md`）中补充统一入口（便于从索引进入）。

## Impact Scope

- **Modules:** 全部（`spring-boot-modules/*` + `spring-core-modules/*`）
- **Files:**
  - 根 `pom.xml`（增加 test 依赖：JUnit Platform Suite）
  - 各模块 `src/test/java/**/part00_guide/*BookMatrixLabTest.java`（新增）
  - `docs/book/*-mainline.md`（更新）
  - `helloagents/wiki/modules/*.md`（更新）
- **APIs:** 无
- **Data:** 无

## Core Scenarios

### Requirement: 为每个模块提供一键“关键分支矩阵入口”
**Module:** 全部模块
新增 `*BookMatrixLabTest`，聚合该模块的核心 Labs（主线 + 关键分支/边界），让读者可以通过一个命令跑通“最小但覆盖关键分支”的证据链。

#### Scenario: 读者从 Book/知识库进入并一键验证
在每个模块中提供一个命令：
- `mvn -q -pl :<artifactId> -Dtest=<Module>BookMatrixLabTest test`

预期：
- 测试可稳定通过（默认回归）
- 能覆盖至少 1 条主线 + 1 个关键分支/边界（由被聚合的 Labs 提供）

## Risk Assessment

- **Risk:** 引入 JUnit Platform Suite 依赖后，若版本不匹配可能影响测试发现/执行。
  - **Mitigation:** 依赖使用 Spring Boot Parent 的版本管理；在全仓 `mvn -q test` 回归验证。
- **Risk:** Book Matrix 聚合过多 Labs 导致运行时间变长。
  - **Mitigation:** 每模块只挑“最小集合”（2–4 个），并把其定位为“进阶入口”，不替代默认推荐 Lab。
