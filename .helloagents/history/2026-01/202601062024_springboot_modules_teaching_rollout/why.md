# Change Proposal: Spring Boot 模块教学化规范推广（对齐 spring-core 标准）

## Requirement Background

当前仓库已经为 `spring-core-*` 模块建立了统一的“教学化规范”：

- `docs/README.md` 作为章节清单（SSOT）：按 Part 分组、编号稳定、链接可解析；
- 每章至少有 1 个“可跑入口”（真实 `src/test/java/.../*Test.java` 路径或 `*LabTest` 类名）；

但 `springboot-*` 模块仍存在未对齐的差异，导致“同一套标准无法对全仓库落地、也无法被脚本持续验收”：

1. 多数 `springboot-*/docs/README.md` 使用反引号展示路径（不是 Markdown 链接），无法作为“章节清单=SSOT”被脚本解析；
2. `part-00-guide/00-deep-dive-guide.md` 与 `appendix/90/99` 普遍缺少可跑入口引用（只能间接指向 README），无法稳定满足“逐章可运行闭环”的要求；
3. 少数 `springboot-*` 模块仅有 1 个 `*LabTest.java`，不满足统一的 `min-labs=2`；

## Change Content

2. 统一 `springboot-*/docs/README.md`：用 Markdown 链接列出章节，作为“章节清单（SSOT）”。
3. 统一章节“可跑入口”块：为导读与附录补齐 `### 对应 Lab/Test（可运行）`，并确保每章至少 1 个入口可解析到真实测试类。
4. 补齐最小 Labs：将 `springboot-*` 每模块 `*LabTest.java` 数量补齐到 `min-labs=2`。
5. 同步 HelloAGENTS 知识库：`wiki/modules/*.md` 变更历史 + `CHANGELOG.md` + `history/index.md` 记录本次推广。

## Impact Scope

- **Modules:**
  - `springboot-basics`
  - `springboot-web-mvc`
  - `springboot-data-jpa`
  - `springboot-actuator`
  - `springboot-testing`
  - `springboot-business-case`
  - `springboot-security`
  - `springboot-web-client`
  - `springboot-async-scheduling`
  - `springboot-cache`
- **Files:**
  - `springboot-*/docs/**`（目录页与章节入口块）
  - `springboot-*/src/test/java/**`（补齐最小 Labs）
  - `helloagents/wiki/**`、`helloagents/CHANGELOG.md`、`helloagents/history/index.md`
- **APIs:** 无（不新增对外 API）
- **Data:** 无（不做不可逆数据变更）

## Core Scenarios

### Requirement: docs-index-and-numbering
**Module:** springboot-*
`docs/README.md` 必须可作为“章节清单（SSOT）”，并且章节编号与文件名一致（`NN-*.md`），排序稳定。

#### Scenario: docs-readme-is-parsable-ssot
当使用自检脚本读取 `springboot-*/docs/README.md`：
- 能解析到所有章节 `.md` 文件的链接清单（包含 Part 与 Appendix）
- 不出现“只写反引号路径导致章节数=0”的情况

### Requirement: chapter-lab-closure
**Module:** springboot-*
每个章节 `.md` 至少包含 1 个“可跑入口”，并能解析到仓库内真实测试类（推荐统一成章节末尾 `### 对应 Lab/Test（可运行）`）。

#### Scenario: each-chapter-has-a-runnable-entry
对 `springboot-*/docs/**/*.md`（含 guide 与 appendix）逐章检查：
- 至少存在 1 个 `src/test/java/.../*.java` 的真实路径引用，或 `*LabTest`/`*ExerciseTest` 类名引用
- 引用目标在仓库中真实存在（不允许 `...`/`…` 省略号）

### Requirement: minimum-labs-per-module
**Module:** springboot-*
每个模块至少拥有 2 个 `*LabTest.java`（`min-labs=2`），用于保证“最小可复现实验入口”数量下限。

#### Scenario: each-module-has-at-least-two-labs
对每个 `springboot-*` 模块：
- `src/test/java/**/**/*LabTest.java` 数量 ≥ 2

### Requirement: docs-link-integrity
**Module:** scripts
自检脚本需要覆盖 `spring-core-*` 与 `springboot-*` 的 docs，相对链接目标必须全部存在（断链为 0）。

#### Scenario: check-md-relative-links-passes-for-all-docs
执行 `python3 scripts/check-md-relative-links.py`：
- 能扫描到 `spring-core-*/docs` 与 `springboot-*/docs`
- 输出断链为 0（missing targets: 0）

### Requirement: teaching-coverage
**Module:** scripts

#### Scenario: check-teaching-coverage-passes-for-all-modules
执行 `python3 scripts/check-teaching-coverage.py --min-labs 2`：
- 以每个模块 `docs/README.md` 的章节链接清单为 SSOT
- 逐章检查“可跑入口”并通过
- 对所有模块输出 PASS

### Requirement: knowledge-base-sync
**Module:** helloagents
本次推广的结果必须同步到知识库，并把方案包从 `plan/` 迁移到 `history/`。

#### Scenario: knowledge-base-updated-and-plan-migrated
- `helloagents/wiki/modules/springboot-*.md` 的 Change History 记录本次推广
- `helloagents/CHANGELOG.md` 与 `helloagents/history/index.md` 增补对应条目
- 方案包迁移至 `helloagents/history/YYYY-MM/YYYYMMDDHHMM_<feature>/`

## Risk Assessment

- **Risk:** Maven 依赖下载偶发 403 导致回归失败（受本机网络/代理/镜像影响）
  - **Mitigation:** 任务中记录标准化处理方式（清理 `~/.m2/**/.lastUpdated`，必要时使用 `mvn -U`），并确保仓库不引入任何本机缓存文件
- **Risk:** 文档重排/改链导致 README 与正文引用不一致
