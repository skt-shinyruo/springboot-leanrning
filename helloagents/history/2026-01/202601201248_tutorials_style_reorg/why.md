# Change Proposal: 对齐 tutorials 工程结构（分组聚合 + 目录重排）+ Web MVC 深度示范

## Requirement Background

本仓库已具备“教学化”的核心骨架（docs 统一到 `docs/`、Book 顺读目录、Labs/Exercises/Solutions 的测试分层、MkDocs 文档站等），但工程结构仍保持“模块平铺在仓库根目录”的形态：

- **可导航性不足**：模块数量增长后，根目录噪音大；新读者难以建立“Boot vs Core”的分组心智模型。
- **路径耦合偏强**：大量命令/文档引用基于“模块目录名在根目录”的假设；一旦迁移目录层级，引用需要大范围调整。
- **对标参考缺口**：你希望整体“格式”参考 `/home/feng/code/temp/tutorials`（toplevel 分组目录 + 聚合 pom 的层级组织），同时要求内容更深入（调用链/关键分支/边界条件/矩阵测试/断点包/排障 playbook）。

因此需要一次“结构级改造”：把 Maven 工程与仓库目录重组为可扩展的分组结构，并以 `springboot-web-mvc` 作为第一批示范，补齐更深入的“可复述 + 可验证 + 可排障”闭环资产。

<!-- ⚠️ G8 触发：本次属于“重大结构改造/重构”，因此包含 Product Analysis -->
## Product Analysis

### Target Users and Scenarios

- **User Groups:**
  - 学习者：希望“按路线顺读”或“按症状排障”快速定位入口
  - 维护者：希望新增模块时不破坏目录与知识库的一致性
- **Usage Scenarios:**
  - 从 Book 顺读主线（Beans → AOP → Tx → Web MVC → …）
  - 按模块查找：知道主题但不确定入口（“Web MVC 的 406/415 属于哪段链路？”）
  - 按现象排障：错误码/异常/行为差异 → 快速定位关键分支与断点
- **Core Pain Points:**
  - 模块平铺导致“分组心智模型”弱、扩展成本高
  - 文档/脚本对模块路径耦合，迁移容易引入断链与入口漂移
  - 深度内容需要更“证据链化”（尤其是分支矩阵与排障速查）

### Value Proposition and Success Metrics

- **Value Proposition:**
  - 结构对齐 tutorials：更清晰的“按域分组 + 聚合层级”，更适合规模化扩展与维护
  - 入口稳定：命令、文档引用尽量从“路径耦合”升级到“artifactId/证据链入口”
  - 教学更深入：把关键分支做成可复现矩阵与可排障 playbook
- **Success Metrics:**
  - 根目录模块分组清晰：引入 `spring-boot-modules/`、`spring-core-modules/spring-core-modules/`
  - Maven Reactor 仍可一键跑通：`mvn -q test` 全绿
  - 文档站点可构建：`bash scripts/docs-site-build.sh` 可用
  - Web MVC 第一批新增“矩阵/证据链”入口至少 1 个（Lab/Doc 双向绑定）

### Humanistic Care

- 迁移遵循“入口稳定优先”：必要时通过 redirect 页面或稳定命令（`:artifactId`）降低读者迷路成本
- 不引入任何明文密钥/Token；不依赖外部在线服务作为学习必需条件

## Change Content

1. **Maven 分组聚合（对齐 tutorials 风格）**
   - 新增聚合层：`spring-boot-modules`、`spring-core-modules`
   - 根 `pom.xml` 的 `<modules>` 收敛为分组聚合模块
2. **目录重排与模块迁移**
   - 将现有模块迁移到对应分组目录下（物理移动 + pom/README 适配）
   - 保持现有子模块 `artifactId` 不变，最大化兼容与可回滚性
3. **命令/文档去路径耦合**
   - 文档与脚本统一使用 `mvn -pl :artifactId`（减少目录迁移带来的命令漂移）
   - 批量更新文档中的源码/测试路径引用（从旧模块路径迁移到新路径）
4. **Web MVC 第一批“更深入”示范**
   - 新增/强化：调用链讲解、关键分支/边界条件、矩阵测试、断点/观察点清单、排障 playbook
   - 明确绑定到可跑入口（Lab/Exercise/Solution）

## Impact Scope

- **Modules:**
  - `springboot-*`（迁移至 `spring-boot-modules/`）
  - `spring-core-*`（迁移至 `spring-core-modules/spring-core-modules/`）
  - 第一批示范：`springboot-web-mvc`（深度内容 + 入口整理）
- **Files:**
  - Maven：根 `pom.xml`、新分组 `pom.xml`、各子模块 `pom.xml`
  - 文档：`docs/book/*webmvc*`、`docs/web-mvc/springboot-web-mvc/**`（路径与入口同步）
  - 脚本：`scripts/*`（确保 `-pl` 命令与新结构兼容）
  - SSOT：`helloagents/wiki/**`、`helloagents/CHANGELOG.md`、`helloagents/history/index.md`
- **APIs/Data:** 不引入对外兼容性要求；不触达生产数据；教学用端点与测试为主

## Core Scenarios

### Requirement: R1-tutorials-style-grouping
**Module:** root / build
把仓库模块按域分组（Boot vs Core），并通过聚合 pom 形成清晰的层级结构。

#### Scenario: S1-group-poms-aggregate-modules
根 `pom.xml` 只聚合 `spring-boot-modules` 与 `spring-core-modules`；分组 pom 聚合各自子模块，并保持全量测试可运行。

### Requirement: R2-stable-entrypoints
**Module:** docs / scripts
把“入口命令/引用”从“路径耦合”升级为“artifactId/证据链入口”，降低目录迁移的维护成本。

#### Scenario: S1-mvn-pl-by-artifactid
文档与脚本的模块定位优先使用 `mvn -pl :<artifactId>`（例如 `:springboot-web-mvc`），避免路径变化导致命令失效。

### Requirement: R3-webmvc-deepen-batch01
**Module:** springboot-web-mvc
以 Web MVC 为第一批示范：新增更深入的“关键分支/边界条件/矩阵测试 + 调试包 + 排障 playbook”，并绑定到可跑入口。

#### Scenario: S1-content-negotiation-matrix
通过矩阵用例把 406/415/400 的典型分支固化为断言，文档给出“怎么证明走到哪个分支”的观察点与断点。

### Requirement: R4-docs-site-and-book-consistency
**Module:** docs-site / docs/book
迁移后 Book 导航与 docs-site 构建保持一致，关键入口不迷路（必要时保留 redirect）。

#### Scenario: S1-build-and-navigate
`bash scripts/docs-site-build.sh` 可通过；Book 主线章节与 Web MVC 模块深挖入口互相可达。

## Risk Assessment

- **Risk:** 大规模目录迁移导致 Maven reactor 断裂、IDE 导入混乱  
  **Mitigation:** 分批迁移（先骨架、再逐模块），每批次以 `mvn -q test` 与 docs-site 构建作为闸门。
- **Risk:** 文档中的源码/测试路径大量漂移导致“证据链断裂”  
  **Mitigation:** 统一改为 `:artifactId` 命令入口；路径引用使用脚本批量迁移并复跑 `bash scripts/check-docs.sh`。
- **Risk:** 迁移过程中出现“中间态不可用”  
  **Mitigation:** 先引入聚合层并保持构建可用，再进行物理移动；每一步都可回滚。

