# Change Proposal: spring-core-beans 文档书本化（Bookify）

## Requirement Background

当前 `docs/beans/spring-core-beans/` 的知识点覆盖已经比较全面，但阅读体验更像“分散的章节片段拼接”，主要痛点是：

1. **缺少“像书一样”的目录与分卷结构**：读者不知道应该从哪里开始、按什么顺序推进、每一部分的学习目标是什么。
2. **章节契约不统一**：不同章节的开头/结尾信息密度和风格不一致，导致读者在章节切换时需要重复建立上下文。
3. **承接关系弱**：虽然有大量 cross-link，但“为什么要读下一章 / 下一章会用到本章哪些结论”没有形成稳定的叙事主线。

目标不是新增知识点，而是把现有内容组织成 **官方 Reference Docs 风格的连续教程**（参考 Spring Framework / Spring Boot 官方文档的结构化组织方式）。

## Change Content

1. **重构目录信息架构（IA）**：为 `docs/` 引入“目录页（TOC）+ Part（分卷）”结构，并允许对章节进行移动/重命名/重新编号，以匹配“像书本一样”的阅读体验。
2. **统一章节模板（A–G）**：每章必须包含：
   - A 本章定位（承接上一章 + 本章解决什么）
   - B 核心结论（3–7 条可复述结论）
   - C 机制主线（稳定调用链/阶段图串起来）
   - D 源码解析（类/方法/关键分支 + 伪代码）
   - E 最小可复现实验（Labs/Exercises 入口）
   - F 常见坑/反例
   - G 本章小结 + 下一章预告
3. **书本式导航（验收项 a/b）**：
   - 每章顶部固定导航：上一章｜目录｜下一章
   - 每章末尾固定输出：本章结论 + 下一章预告
4. **全书目录页（验收项 c）**：新增少量“书本基础设施文件”（例如 `docs/README.md` 或 `docs/SUMMARY.md`），作为目录与 Part 概览入口。
5. **链接与引用修复**：章节移动/重命名后，修复 `docs` 内部链接，以及 `spring-core-beans/README.md` 等对 docs 的入口链接，确保读者可以从 TOC 顺畅跳转与顺读。

## Impact Scope

- **Modules:** `spring-core-beans`、`helloagents`（知识库与变更记录）
- **Files:** 主要影响 `docs/beans/spring-core-beans/**`（大量章节将移动/重命名并补齐模板），并新增 `docs/beans/spring-core-beans/README.md`（或等价目录文件）
- **APIs:** 无
- **Data:** 无

## Core Scenarios

### Requirement: 目录页 + Part 结构（像书一样从目录开始）
**Module:** spring-core-beans
将 `docs/` 从“文件列表”升级为“书本目录”。

#### Scenario: 从目录页开始顺读
读者打开 `docs/README.md`（或 `docs/SUMMARY.md`）即可获得：
- 分 Part 的目录结构与学习目标
- 主线阅读顺序（从第 1 章开始可连续阅读）

### Requirement: 章节模板一致（A–G）
**Module:** spring-core-beans
保证任意章节都具备同一套“阅读契约”，降低章节切换的上下文成本。

#### Scenario: 任意章节开头与结尾的信息一致
- 开头包含：本章定位（承接/目标）+ 核心结论
- 结尾包含：本章小结 + 下一章预告（明确承接关系）

### Requirement: 章节导航与链接正确
**Module:** spring-core-beans
章节移动/重命名后链接不失效。

#### Scenario: 上一章/下一章/目录跳转可用
- 每章顶部的“上一章｜目录｜下一章”链接可用
- `spring-core-beans/README.md` 的 docs 入口链接可用

## Risk Assessment

- **Risk:** 大规模移动/重命名导致链接断裂、读者迷路、PR diff 过大难 review  
  **Mitigation:** 先定义“章节映射表（旧→新）+ Part 划分”，再按 Part 分批迁移；迁移后统一执行全局链接修复与自检；提交拆分为“结构迁移/章节模板化/知识库同步”多次小提交。
- **Risk:** “像书一样”的叙事需要统一风格，容易在迁移过程中出现局部不一致  
  **Mitigation:** 固化章节模板（A–G）为硬性检查项，按章节批量对齐，最后做一致性审计（目录、导航、结尾输出、术语一致性）。

