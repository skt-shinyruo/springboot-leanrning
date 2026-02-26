# Change Proposal: 全模块文档书本化升级（统一章节契约与导航）

## Requirement Background

1. 不同模块/章节的“上一章｜目录｜下一章”导航形式不统一，部分章节缺少可连续阅读的跳转锚点；
2. 各章节的“可跑入口（Lab/Exercise）”在表达形式上不统一：有的散落在正文、有的只在 README，有的缺少统一的章节尾部入口块；
3. 由于缺少统一的章节契约（Chapter Contract），读者在跨模块学习时需要反复适应不同的章节结构与入口习惯，学习成本偏高。

本次变更的目标是：**把所有模块的 docs 按“书本结构”统一到同一套最小契约**，做到“任何一章都可按固定方式读完并找到可跑入口”。

## Change Content

1. 为所有模块的 docs 章节补齐统一的“章节契约”（章节尾部入口块 + 导航）；
2. 以 `docs/README.md` 作为章节清单 SSOT（Single Source of Truth）：根据 README 的章节顺序生成章节间导航；
3. 不重写章节正文知识点，只做结构化增强（导航、入口块、格式一致性），避免引入内容语义风险；

## Impact Scope

- **Modules:** `pom.xml` 中全部模块（共 18 个，包含 `spring-core-*` 与 `springboot-*`）
- **Files:** 以 `*/docs/**/*.md` 为主（章节文件），以及新增/增强 `scripts/` 中的辅助脚本（如需要）
- **APIs:** 无
- **Data:** 无

## Core Scenarios

### Requirement: all-modules-docs-bookify
**Module:** all
将仓库内所有模块 docs 统一升级为“可连续阅读 + 可跑入口可定位”的书本化结构。

#### Scenario: chapter-contract-and-navigation
在任意模块中打开任意章节（非 README）：
- 章节末尾存在统一的“对应 Lab/Test（可运行）”入口块，可直接定位至少 1 个真实测试类；
- 章节末尾存在统一的导航行：上一章 / 目录（Docs TOC）/ 下一章；
- 导航顺序以该模块 `docs/README.md` 的章节链接顺序为准；
- 全量断链检查与教学覆盖检查均通过。

## Risk Assessment

- **Risk:** 批量修改文件数量大，可能出现少量章节的导航顺序或相对路径生成错误
  - **Mitigation:** 用脚本从 README 解析章节顺序并计算相对路径；执行后跑 `scripts/check-md-relative-links.py` 与 `scripts/check-teaching-coverage.py` 全量验收
- **Risk:** 章节内已有“对应 Lab/导航”表达，新增结构可能造成重复信息
  - **Mitigation:** 采用 upsert（存在则替换/归一，不存在则追加）的方式；保持变更可重复运行且不会无限叠加
- **Risk:** 合并冲突风险（多人同时改 docs）
  - **Mitigation:** 变更集中在章节尾部固定区域；尽量避免改动正文段落
