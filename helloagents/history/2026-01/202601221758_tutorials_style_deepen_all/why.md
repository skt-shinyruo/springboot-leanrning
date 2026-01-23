# Change Proposal: tutorials 风格深度化改造（结构对齐 + 模板统一 + 全模块深挖 + 主题扩展）

## Requirement Background

你希望把本仓库的“格式”对齐 `/home/feng/code/temp/tutorials`，同时把内容做得更深入，最终让它具备两种能力：

1) **像 tutorials 一样“可扩展”**：目录层级清晰、模块分组明确、父子 POM 组织稳定，后续新增主题不会让工程结构失控。  
2) **比 tutorials 更“可验证/可排障”**：每个主题都能落到“可运行入口 + 可断言证据链 + 断点/观察点清单”，并覆盖关键边界/异常路径与性能/并发讨论。

当前仓库已经具备一部分教学化资产（Book 主线、docs-site、Labs/Exercises/Solutions 分层、以及 Boot/Core 分组聚合模块），但仍存在以下可提升点：

- **命名与结构对齐不彻底**：已按 Boot/Core 分组，但模块命名、目录与文档引用仍可能存在风格不统一/路径耦合残留。
- **模板一致性仍可加强**：模块 README 与 docs 的“章节契约/入口契约”需要进一步统一（Start Here、推荐入口、断点地图、分支矩阵、Labs/Exercises 索引等）。
- **深度资产覆盖不均**：Web MVC 已有较多深挖资产，但 AOP/Tx 等模块仍缺少“调用链可视化 + 关键分支矩阵化 + 性能/并发专题化”的统一范式。
- **主题扩展需要工程化支撑**：如果要把主题规模向 tutorials 靠拢，需要“脚手架 + 质量闸门 + 文档索引自动化”避免质量参差与漂移。

<!-- ⚠️ G8 触发：本次属于“重大结构改造/重构”，因此包含 Product Analysis -->
## Product Analysis

### Target Users and Scenarios

- **User Groups:**
  - 学习者：希望“按主线顺读”或“按现象排障”快速定位到可运行入口与关键断点
  - 维护者：希望新增主题模块时有统一模板与闸门，避免 docs/测试/站点漂移
- **Usage Scenarios:**
  - Book 顺读主线（Beans → AOP → Tx → Web MVC → …），每章一屏进入可验证闭环
  - 按主题进入：先跑 `*LabTest` 观察现象，再结合断点地图理解机制
  - 按现象排障：错误码/异常/行为差异 → 分支矩阵 → 最小复现入口 → 断点/观察点收敛
- **Core Pain Points:**
  - 命名与模板不统一导致“入口心智模型”不稳定
  - 深挖内容缺少统一的“证据链表达”（call-chain / branch-matrix / debugger-pack）
  - 扩展主题时缺少脚手架与质量闸门，容易产生“写了但跑不通/不一致/不可回归”的内容

### Value Proposition and Success Metrics

- **Value Proposition:**
  - 结构更像 tutorials：分组清晰、扩展友好
  - 内容更像“可验证的教材”：关键机制都能被测试断言固化，且能通过断点快速定位
  - 维护更像“产品工程”：引入模板、脚本与闸门，让质量可以持续滚动升级
- **Success Metrics:**
  - `mvn -q test` 全绿（全仓库回归基线）
  - 每个主题模块：
    - 至少 1 个推荐入口（`*LabTest`）
    - 至少 1 个可练习入口（`*ExerciseTest`，默认 `@Disabled`）
    - 断点/观察点清单可一跳定位（Breakpoint Map）
    - 关键分支矩阵可一跳定位（Branch Decision Matrix）
    - 至少 1 条“机制调用链”文档（Call Chain）
  - 文档闸门可用：`bash scripts/check-docs.sh` / `bash scripts/docs-site-build.sh` 可通过
  - 新增主题模块可通过脚手架生成并一键纳入 docs/book 与 docs-site

### Humanistic Care

- 默认遵循“入口稳定优先”：命令统一倾向使用 `mvn -pl :artifactId` 降低路径变更带来的迷路成本
- Exercises 默认禁用，避免学习者开启前污染 CI；Solutions 作为可选“对照答案”，不强依赖外部服务
- 不引入任何明文密钥/Token；不做任何生产环境操作

## Change Content

1. **（结构）更强对齐 tutorials 风格**
   - 梳理并固化模块命名与目录约定（Boot/Core 分组 + 主题命名）
   - 在需要时执行目录/模块重命名，并提供迁移映射与 redirect（降低断链）
2. **（模板）统一模块 README 与 docs 的“入口契约”**
   - 每模块 README 统一为“索引/导航”，深度内容放在 docs
   - 明确每个主题的固定资产：Start Here / 推荐入口 / Debugger Pack / Labs & Exercises 索引
3. **（深度）全模块深挖资产滚动补齐**
   - 机制源码调用链（Call Chain）+ 关键类/扩展点索引
   - 关键分支/异常路径矩阵化（Branch Decision Matrix → Lab 证据链）
   - 性能与并发专题：把“慢/竞态/异步边界”落到可复现与可断言入口
4. **（扩展）新增主题模块（向 tutorials 的主题广度靠拢）**
   - 引入一批高价值新主题（优先补齐主线缺口/高频排障点）
   - 新模块从 Day 1 起具备模板化资产与可回归测试
5. **（工程化）脚本与闸门增强**
   - 自动生成索引（Book/Labs/Debugger Pack/Exercises）
   - 增强一致性检查（断链/路径漂移/入口缺失）并作为闸门

## Impact Scope

- **Modules:**
  - 既有：`spring-boot-modules/*`、`spring-core-modules/*`
  - 新增（规划）：`springboot-autoconfiguration`、`springboot-observability`、`springboot-logging`、`spring-core-spel`（可调整）
- **Files:**
  - Maven：根/分组 `pom.xml`、各子模块 `pom.xml`
  - 文档：`docs/**`、`docs/book/**`、`docs-site/**`
  - 脚本：`scripts/**`（索引生成/一致性闸门/新模块脚手架）
  - SSOT：`helloagents/wiki/**`、`helloagents/CHANGELOG.md`、`helloagents/history/index.md`
- **APIs/Data:** 教学用端点/测试为主；不引入生产数据依赖；不做破坏性数据操作

## Core Scenarios

### Requirement: R1-tutorials-style-structure-and-naming
**Module:** root / build / docs
进一步对齐 tutorials 的结构与命名，并提供可迁移与可回滚策略。

#### Scenario: S1-stable-structure-and-entrypoints
完成结构/命名调整后：
- `mvn -q test` 全绿
- 关键入口命令优先通过 `mvn -pl :artifactId` 可运行
- docs-site 可构建，Book 顺读不迷路（必要时保留 redirect）

### Requirement: R2-module-template-contract
**Module:** all modules
统一每个主题模块的 README 与 docs 契约（入口、索引、深挖路径、Debugger Pack）。

#### Scenario: S1-module-readme-and-docs-contract
每个模块 README 都具备统一的“Start Here + 推荐入口 + docs 深挖入口 + 索引/断点包链接”，并能从 README 一跳进入 docs 的断点地图/分支矩阵/调用链。

### Requirement: R3-deepen-mechanics-and-evidence-chain
**Module:** all modules
把“机制”变成“证据链”：调用链文档 + 关键分支矩阵 + 对应 Lab 断言。

#### Scenario: S1-call-chain-branch-matrix-labs
每个模块至少新增/完善 1 条调用链文档，并至少有 1 个 Lab 用矩阵用例固化关键边界/异常路径（例如 400/415/406、代理自调用、事务传播/回滚等）。

### Requirement: R4-performance-and-concurrency-deepen
**Module:** selected modules (Web MVC / Async / Cache / Events / Tx)
把性能与并发的“常见误区”落为可复现与可断言入口，并提供排障观察点。

#### Scenario: S1-reproducible-perf-and-concurrency-cases
为至少 2 个主题提供“可复现性能/并发边界”的 Lab/Doc 组合，并在文档中给出关键观察点（线程、队列、超时、Backpressure、事务边界、锁粒度等）。

### Requirement: R5-expand-topics-with-scaffold-and-gates
**Module:** new modules
新增主题模块必须从第一天起满足模板与闸门要求（不引入“写了但跑不通”的模块）。

#### Scenario: S1-new-module-ready-from-day1
新增模块具备：可运行入口（至少 1 个 LabTest）、可练习入口（ExerciseTest）、docs 结构（part-00-guide + breakpoint-map + branch-matrix + call-chain），并纳入 docs-site/Book 索引。

## Risk Assessment

- **Risk:** 大规模重命名/迁移导致路径断链、IDE 导入混乱、Maven reactor 失败  
  **Mitigation:** 分批迁移 + 每批次闸门回归（`mvn -q test`、`bash scripts/check-docs.sh`、`bash scripts/docs-site-build.sh`）；并提供迁移映射/redirect。
- **Risk:** 深挖内容越写越多，风格与质量不一致  
  **Mitigation:** 固化“模块契约 + 章节契约”，并用脚本检查缺失项（入口/断点地图/分支矩阵/调用链）。
- **Risk:** 性能/并发实验引入不稳定（flaky）测试，破坏 CI  
  **Mitigation:** 性能实验默认以“稳定断言 + 指标观察”为主；必要时将重型实验放入 `@Disabled` 或独立 profile。
