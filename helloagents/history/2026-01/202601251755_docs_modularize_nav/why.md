# Change Proposal: 文档归并（以模块 */docs 为 SSOT，docs 仅保留总目录）

## Requirement Background

当前仓库同时存在两套“模块文档”：

- 仓库根 `docs/`（按 topic 分组：`docs/<topic>/<module>/...`）
- 各代码模块内的 `*/docs/`（按模块收敛：`spring-boot-modules/<module>/docs/...`、`spring-core-modules/<module>/docs/...`）

两套内容长期并行会产生漂移（同名章节内容不一致、链接容易断），且 `docs/` 目录体量过大，不利于“模块即边界”的维护方式。

用户期望：

1. 以各模块 `*/docs/` 作为最终唯一事实来源（SSOT）。
2. 仓库根 `docs/` 只保留一个“总目录文件”，用它把所有模块文档串起来（用于文档站侧边栏导航）。
3. `docs/book/` 目录不再需要，直接移除。

## Change Content

1. 文档 SSOT 回归模块：所有模块文档以 `spring-boot-modules/**/docs/` 与 `spring-core-modules/**/docs/` 为准。
2. 根 `docs/` 目录收敛为“全站目录（导航）”：
   - 保留 `docs/SUMMARY.md` 作为站点导航 SSOT（目录文件本身就是文档）
   - 目录内容覆盖所有模块 `*/docs/` 下的文档页面
3. 移除 `docs/book/` 及其入口，并清理仓库内对 Book 的引用。
4. 同步调整文档站（MkDocs）与脚本工具链，使其以“模块 docs + 全站目录 SUMMARY”的新结构工作。
5. 同步更新知识库（`helloagents/wiki/*`）与根 `README.md` 的入口链接，避免断链与误导。

## Impact Scope

- **Modules:**
  - `spring-boot-modules/*`
  - `spring-core-modules/*`
  - `docs-site`
  - `scripts`
  - `helloagents`（知识库/变更记录）
- **Files:**
  - `docs/`（将大幅收敛，仅保留 `docs/SUMMARY.md`）
  - `spring-boot-modules/**/docs/**.md`
  - `spring-core-modules/**/docs/**.md`
  - `docs-site/mkdocs.yml`
  - `scripts/*`（涉及 docs SSOT 的脚本）
  - `README.md`
  - `helloagents/wiki/*`、`helloagents/CHANGELOG.md`
- **APIs:** 无
- **Data:** 无

## Core Scenarios

### Requirement: 全站目录以 SUMMARY 为唯一导航
**Module:** docs-site / docs
根 `docs/SUMMARY.md` 作为站点导航唯一事实来源，目录内链接指向各模块 `*/docs/` 的页面。

#### Scenario: 文档站侧边栏可完整访问模块文档
- 目录文件覆盖所有模块文档页面（包含 README、guide、appendix 等）
- MkDocs 构建/预览可正常解析目录并渲染页面

### Requirement: 模块 docs 成为唯一事实来源
**Module:** spring-boot-modules / spring-core-modules

#### Scenario: 新文档只写在模块内，不再双份维护
- 不再在根 `docs/` 下保留模块内容副本
- 相关脚本默认以模块 `*/docs/README.md` 作为章节清单 SSOT

### Requirement: 移除 docs/book
**Module:** docs

#### Scenario: Book 入口不再出现在导航与 README/知识库中
- 删除 `docs/book/`
- 清理 `docs/book` 的引用（README/知识库/目录文件/章节导航）

## Risk Assessment

- **Risk:** 大规模删除/迁移文档导致断链或文档站无法构建
  - **Mitigation:** 迁移前后执行断链检查、MkDocs 构建验证；在删除前保留可回退路径（Git 版本可追溯）。
- **Risk:** 章节中遗留 Book 导航（`Book TOC`/`book/*.md`）导致错误跳转
  - **Mitigation:** 批量清理/替换为 `Docs TOC`（模块 README）或站点目录入口（SUMMARY）。

