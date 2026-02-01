# Change Proposal: spring-core-beans docs 继续深化（Round 2：入口页/工具页中枢化）

## Requirement Background

`spring-core-beans` 的正文与 Lab 在上一轮已具备“可跑/可断点/可自证”的基础，但在进入 Round 2 时，读者常见卡点不再是“有没有内容”，而是：

- 已经能跑通实验、也能打进断点，但**不知道下一步该从哪里开始**（入口不够“选择题式”）。
- 工具页（知识地图/断点地图/生产排障清单）已经存在，但互链不够强，导致读者仍需要自己拼“章节 → 断点 → Lab”的路径。
- README 容易膨胀成第二份知识地图：入口页应该只负责“把读者送到下一步可验证动作”，机制细节应留在正文与工具页。

因此，本方案包的目标是：**优先把入口页与工具页强化为可复用中枢**，让读者从“现象/目标”能在 1–2 次跳转内到达：

- 对应章节（最短阅读）
- 对应断点组（最短证据链）
- 对应 Lab/Test（最短可复现）

具体落地策略（按文件）以施工图为准：
- `helloagents/history/2026-02/202602011503_beans_docs_deepen_round2/audit/entrypoints-round2.md`

## Change Content

1) 强化入口页：`docs/README.md`
   - 新增 Round 2 的最短开始分流（现象/断点/排障三条入口）
   - 每条入口都落到“下一步可验证动作”（页面 + 推荐入口 Lab/Test + 断点组提示）
   - 控制 README 体量：避免扩写成另一份知识地图

2) 强化策略入口：`deepening-strategies/README.md`
   - 补齐两种进入方式（从现象进入/从断点进入）
   - 用最短步骤说明“先跑什么/再看什么/如何把观察收敛为结论”

3) 强化工具页互链：知识地图 ↔ 断点地图
   - 断点地图提供“从症状选择断点组”的极短分流，并回链到知识地图
   - 知识地图在高频现象处给出可稳定跳转到断点组的入口（必要时补稳定锚点）

4) 强化生产排障：把高频事故压成最短路径
   - 把最常见事故写成 3–5 步可验证路径，并回链到章节/Lab/断点组

## Non-goals（不做什么）

- 不重排目录、不改文件路径、不改章节编号体系（避免断链与读者习惯被破坏）。
- 不把工具页写成“方法论说明书”，只在确实降低读者成本处补充路径与动作。
- 不把每章都强行套同一套小标题（入口/工具页只负责“指路”，正文再按章节需要补强）。

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`
- **Files (docs):**
  - `spring-core-modules/spring-core-beans/docs/README.md`
  - `spring-core-modules/spring-core-beans/docs/deepening-strategies/README.md`
  - `spring-core-modules/spring-core-beans/docs/appendix/92-knowledge-map.md`
  - `spring-core-modules/spring-core-beans/docs/part-00-guide/013-02-breakpoint-map.md`
  - `spring-core-modules/spring-core-beans/docs/appendix/94-production-troubleshooting-checklist.md`
- **APIs/Data:** None

## Core Scenarios

### Requirement: R1-next-round-entrypoints

让读者在入口页就能快速选路，并立刻知道“下一步可验证动作是什么”。

<a id="requirement-r1-next-round-entrypoints-scenario-s1-docs-readme-start-here"></a>
#### Scenario: S1-docs-readme-start-here

- `docs/README.md` 在“可从此处开始”之后提供 Round 2 的三条最短入口（现象/断点/排障）。
- 每条入口都给出下一步动作：去哪个页面、优先跑哪个 Lab/Test、断点组提示如何选。
- README 不扩写机制细节，避免与知识地图重复。

<a id="requirement-r1-next-round-entrypoints-scenario-s2-deepening-strategies-entry"></a>
#### Scenario: S2-deepening-strategies-entry

- `deepening-strategies/README.md` 明确两种入口（现象/断点），并给出最短步骤：
  - 先跑什么（用例入口）
  - 再看什么（断点组/观察点）
  - 如何把观察收敛为结论/反例/排错路径

### Requirement: R2-tool-pages-as-hubs

工具页应成为“可复用的诊断/学习中枢”，让读者从现象能最短走到证据链。

<a id="requirement-r2-tool-pages-as-hubs-scenario-s1-breakpoint-map-and-knowledge-map-linkage"></a>
#### Scenario: S1-breakpoint-map-and-knowledge-map-linkage

- `013-02-breakpoint-map.md` 提供“从症状选择断点组”的极短分流，并回链到知识地图。
- `013-02-breakpoint-map.md` 的断点组具备稳定锚点（C1–C7），便于互链稳定跳转。
- `92-knowledge-map.md` 的高频现象表格能直接跳到对应断点组（C*），并提供推荐 Lab。

<a id="requirement-r2-tool-pages-as-hubs-scenario-s2-production-troubleshooting-shortest-path"></a>
#### Scenario: S2-production-troubleshooting-shortest-path

- `94-production-troubleshooting-checklist.md` 对 3 类高频事故给出 3–5 步“最短诊断路径”：
  - 注入失败（NoSuch/NoUnique）
  - 代理不生效（时机/顺序）
  - 循环依赖/early reference（constructor vs setter、early vs final）
- 每条路径能回链到：对应章节 / 对应 Lab / 对应断点组。

### Requirement: R3-quality-gates

保证入口页/工具页的继续加深不引入断链、错误引用或敏感信息，并通过模块回归。

<a id="requirement-r3-quality-gates-scenario-s1-self-check-pass"></a>
#### Scenario: S1-self-check-pass

- beans docs 相对链接目标存在性检查 missing targets = 0
- beans docs 引用的测试类/文件路径存在性检查通过
- `mvn -pl spring-core-modules/spring-core-beans test` 通过
- 不引入敏感信息（密钥/token/内网地址/个人信息）

## Risk Assessment

- **Risk:** 入口页/工具页继续加深可能导致“写法变得过度方法论化”，读者反而被形式牵着走。
  - **Mitigation:** 入口页只给路径与动作；正文负责机制细节；以“下一步是否可验证”作为唯一判断。
- **Risk:** 工具页互链调整容易产生断链或重复入口。
  - **Mitigation:** 批次化修改 + 全量相对链接/引用自检 + 模块测试回归。
