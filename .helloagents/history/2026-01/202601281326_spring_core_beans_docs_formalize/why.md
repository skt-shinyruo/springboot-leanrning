# Change Proposal: spring-core-beans 文档书面化改写（去口语化）

## Requirement Background

`spring-core-modules/spring-core-beans/docs` 以及模块入口文档 `spring-core-modules/spring-core-beans/README.md` 的技术深度已覆盖主要机制域，但正文中仍存在较多“口语化/对话式”的表达（例如大量第二人称叙述、俚语化表述与课堂化措辞）。这类表达在“源码进阶/面试复述/团队内训讲义化”场景下，会带来两个问题：

1. **可复述性下降：** 读者难以将内容直接迁移为书面答案（面试/文档/评审口径），需要额外“转述”成本。
2. **严谨性观感受损：** 同样的机制描述，在口语语境下容易被误读为“经验性建议”，不利于建立“基于源码分支/证据链可验证”的论证风格。

因此需要对 `spring-core-beans` 模块文档进行一次“书面化处理”，在不降低信息密度与机制深度的前提下，统一去除口语化表达，形成更适合教学交付与复盘的文本风格。

## Change Content

1. 对 `spring-core-modules/spring-core-beans/docs/**/*.md` 与 `spring-core-modules/spring-core-beans/README.md` 全量执行“书面化改写”：减少第二人称叙述、去除俚语与口语化提示语，将表达改为“陈述式/规范式/可复述式”。
2. 保持章节知识点、调用链锚点、排障决策表与面试答案的技术内容不变，仅调整措辞与叙事语气，避免引入新的理解偏差。
3. 对“自检/复述/常见坑”类段落，统一改写为“目标要求/检查点/结论化描述”，使其可直接用于书面复盘材料。

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`
- **Files:** `spring-core-modules/spring-core-beans/docs/**/*.md`、`spring-core-modules/spring-core-beans/README.md`
- **APIs:** 无
- **Data:** 无

## Core Scenarios

### Requirement: 文档书面化（去口语化）
**Module:** spring-core-beans
将 docs 全量改写为更严谨的书面表述。

#### Scenario: 阅读与复述
读者以“源码进阶/面试复述/团队内训讲义”方式阅读同一章节：
- 预期结果：关键结论可直接复述为书面答案，不依赖“对话式解释”。
- 预期结果：文本更接近“机制说明书 + 可验证证据链”的语体，而非口语化讲解。

#### Scenario: 排障复盘
读者按章节给出的证据链/断点入口进行复盘：
- 预期结果：排障段落呈现为“现象 → 证据链 → 修复策略 → 验证”的规范叙述，不夹杂口语化建议。

## Risk Assessment

- **Risk:** 大规模措辞改写可能造成少量语义偏移或误改代码片段/命令片段。
- **Mitigation:** 改写时跳过代码块；改写后以关键短语扫描（第二人称/俚语）与局部抽查回读，确保机制结论与关键方法名不被改写。
