# Change Proposal: Merge First Pass Content into Existing Docs (00/99)

## Requirement Background
此前为了解决 “First Pass 学习闭环” 的可执行性，新增过一个独立文档文件用于整理 10 个最小实验入口。但该文件被反馈为“没有意义/造成重复编号与噪声”，并已按要求删除。

当前目标是：**不新增独立导航文件**，只把其中确实有价值、且能提升可执行性的内容，以最小改动方式融入现有章节：

- `docs/00-deep-dive-guide.md`（深挖指南：加入可选的 First Pass 清单，帮助先建立阶段感）
- `docs/99-self-check.md`（自测题：补一份对应的 Lab 入口清单，便于自测闭环）

## Change Content
1. 在 `00-deep-dive-guide.md` 增加 “First Pass（10 个最小实验）” 小节：只保留入口与“要写的结论”，不再新增独立文件。
2. 在 `99-self-check.md` 增加 “First Pass 自测入口清单” 小节：把自测题与 Lab 入口对齐。
3. 运行模块测试验证无回归，并同步知识库记录（确保 SSOT 与仓库现状一致）。

## Impact Scope
- **Modules:** `spring-core-beans`
- **Files:**
  - `docs/beans/spring-core-beans/00-deep-dive-guide.md`
  - `docs/beans/spring-core-beans/99-self-check.md`
- **Knowledge Base:**
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/CHANGELOG.md`
  - `helloagents/history/index.md`

## Core Scenarios

### Requirement: First Pass without extra files
**Module:** spring-core-beans

#### Scenario: Keep a minimal executable checklist in existing docs
- 读者只需在 `00` 和 `99` 中就能找到“入口 + 目标结论”
- 不出现重复章节文件、不新增 “00A” 文件名，不制造额外噪声

## Risk Assessment
- **Risk:** 清单内容过长会稀释 00/99 的主线信息密度。
- **Mitigation:** 仅保留“入口 + 结论要求”的最小信息；避免重复解释已存在内容。

