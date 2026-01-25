# Change Proposal: 教学体验升级（springboot-web-mvc + spring-core-beans）

## Requirement Background

本仓库的核心定位是“可运行 + 可验证”的 Spring Boot / Spring Core 学习工作区：每个模块都包含 `docs/` 与 `*LabTest/*ExerciseTest`，用测试把关键机制固定下来。

当前的主要教学体验痛点集中在两类：

1. **入口分散、缺少统一导航**：读者需要在根 `README`、模块 `docs/README`、知识库 `helloagents/wiki/*` 之间来回跳转才能形成主线，第一次进入时容易迷路。
2. **Web MVC Part 01 的 Debugger Pack 不够“落地”**：多个章节存在 `Debug 建议`标题，但缺少“从哪个测试类/方法开始、在哪个关键分支打断点、观察哪些对象状态”的具体指引，导致“能跑但不容易看懂”。

同时，`spring-core-beans` 已经有较深的“源码级深潜”体系，但仍需要一条更短的“30 分钟可闭环”快启路线，帮助读者先建立正反馈，再进入深挖。

## Change Content

1. 新增统一学习入口（学习路线图），把“从哪里开始/按什么顺序/先跑哪些 Lab/再做哪些 Exercise”收敛到一个 SSOT 页面。
2. 提升 `springboot-web-mvc` Part 01 的“可调试性”：为核心章节补齐 Debugger Pack（断点入口 + 观察点 + 关键分支证据链）。
3. 为 `spring-core-beans` 增加 “30 分钟快速闭环”快启章节，并强化 `Start Here` 导航。

## Impact Scope

- **Modules:**
  - `springboot-web-mvc`
  - `spring-core-beans`
  - `helloagents/wiki/*`（知识库与导航）
- **Files:**
  - 新增：学习路线图、断点地图、快启章节等文档文件
  - 更新：若干现有章节补齐 Debugger Pack
  - 更新：CI workflow 与脚本入口（不涉及业务代码重构）
- **APIs:** 无（仅教学文档与验证流程增强）
- **Data:** 无

## Core Scenarios

### Requirement: 教学入口收敛（学习路线图）
**Module:** helloagents/wiki
将“学习路线/模块顺序/最小可运行入口/练习开启方式”收敛到单一页面，并在知识库与根 README 提供稳定入口。

#### Scenario: 新读者 3 分钟开跑
- 读者能在 1 次跳转内找到推荐路线与第一个可运行入口（命令 + 对应测试类）
- 路线图能明确区分 “主线（应用闭环）” 与 “机制线（源码深挖）”

### Requirement: Web MVC Part 01 Debugger Pack（可断点、可验证）
**Module:** springboot-web-mvc
为 Web MVC 主线关键章节补齐“从测试出发的源码断点地图”，让读者能从 LabTest 进入调用链并看到关键分支。

#### Scenario: 从 MockMvc 进入主链路并解释关键分支
- 读者能从指定 `*LabTest` 方法开始单步进入框架主线
- 每章至少给出 1 个关键分支的“触发条件 → 分歧 → 观察证据”（例如：是否触发校验、异常如何被 resolver 链处理、binder/converter 命中与失败路径、filter/interceptor 的先后顺序）

### Requirement: spring-core-beans 30 分钟快速闭环
**Module:** spring-core-beans
提供一条“低门槛但可调试”的快启路径：精选 3 个最小实验入口，明确断点与观察点，让读者先看见容器主线与 DI 关键分支，再进入深潜章节。

#### Scenario: 30 分钟完成 3 个最小实验并建立心智模型
- 读者能跑通 3 个精选 Lab，并能用断点观察到关键对象/状态变化
- 快启章节与现有深潜指南形成“先快后深”的阅读闭环

**Module:** root/CI/scripts
把教学质量要求固化为自动检查：避免断链、入口缺失、章节空洞化，保证 PR 合并后仍能“开箱即学”。

#### Scenario: PR 自动拦截断链与低质量章节
- CI 自动执行 docs 检查脚本（相对链接/教学覆盖）
- 对新增章节提供基本结构检查（至少包含：最小实验入口 + 对应 Lab/Test + Debugger Pack）

## Risk Assessment

- **Risk:** CI 耗时增加（docs 检查 + 额外步骤）  
  **Mitigation:** docs job 与 tests job 分离；脚本保持轻量；必要时仅在 PR/主分支触发更重的检查。
- **Risk:** 断点位置随 Spring 小版本变动导致文档失效  
  **Mitigation:** 以“测试入口类/方法”为主锚点；断点给出主入口 + 备选关键词（可用 `rg` 搜索定位）；强调观察点而非死记方法名。
- **Risk:** 文档维护成本上升  
  **Mitigation:** 固化写作结构；用脚本自动检查关键段落与入口块；优先补齐高价值章节（Part 01 + 快启），再逐步推广。

