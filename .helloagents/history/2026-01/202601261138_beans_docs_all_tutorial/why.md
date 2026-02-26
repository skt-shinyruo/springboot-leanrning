# Change Proposal: spring-core-beans 全章文档“教程化”补齐与一致性修复

## Requirement Background

当前 `spring-core-modules/spring-core-beans/docs/` 中仍存在以下问题，导致它“像笔记/索引”，而不是可复用的学习教程：

1. 部分章节缺少统一的教程闭环结构（导读/要点/实验入口/机制主线/排障分流/自检/书本导航）。
2. 多个章节存在空段落或重复标题（例如“排障分流/常见坑/面试常问”仅有标题没有内容），阅读体验断裂。
3. `refresh() → doCreateBean()` 主线章内容质量较高，但未纳入统一“书本化章节契约”，难以融入全章阅读路径。

目标是把 beans 模块文档提升为：**能学、能跑、能下断点、能复述、能排障** 的教程体系，并兼顾读者 A/B/C（重点 B/C）。

## Change Content

1. 统一并补齐 beans 文档“教程级章节契约”
   - 每章至少提供：导读 + 要点 + 推荐实验入口 + 机制主线 + 排障分流 + 常见坑/边界 + 一句话自检 + BOOKIFY 导航。
2. 清理空段落/重复标题，并把“坑/面试/排障”内容补齐成可复用的问答与定位套路。
3. 重构 `refresh() → doCreateBean()` 主线章为“书本化章节”
   - 保留原有高质量主线与分支决策表，但补齐统一章节结构与上下章导航。
4. 保持 docs-site 构建与全站导航稳定
   - 不改动链接路径语义，必要时同步修复内部引用与目录页入口说明。

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`
- **Files:**
  - `spring-core-modules/spring-core-beans/docs/**`
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/CHANGELOG.md`

## Core Scenarios

### Requirement: 教程化（B/C 优先）
**Module:** spring-core-beans

在不引入新的技术栈、不改变现有 Lab 行为的前提下，把文档升级为教程：

#### Scenario: B（有经验工程师）需要“源码定位 + 分支决策 + 可复现证据链”
- 预期：每章明确断点入口/必看变量/对应 LabTest；能用一句话解释“为什么会这样”。

#### Scenario: C（需要排障/复盘）需要“现象 → 分层 → 断点 → 修复 → 复验”的套路
- 预期：每章提供“排障分流”与“常见坑/边界”，并能从 docs 直达对应章节与测试入口。

#### Scenario: A（初学者）需要“先跑再读”的最小闭环
- 预期：每章的实验入口与阅读路径清晰，不会因为空标题/缺入口而卡住。

## Risk Assessment

- **Risk:** 大量文档编辑可能引入死链或导航不一致。
  - **Mitigation:** 每批改动后执行 `python3 -m mkdocs build -f docs-site/mkdocs.yml`，并保持 `docs/SUMMARY.md` 作为全站 SSOT 不变。
- **Risk:** 文档调整误写源码路径导致读者误导。
  - **Mitigation:** 优先引用仓库内现有 LabTest/类名/方法名，避免编造；必要时用 grep/IDE 对照确认路径。

