# Change Proposal: spring-core-beans docs Round 2（全量逐章差异化继续深化）

## Requirement Background

2026-02-01 已完成一轮 `spring-core-modules/spring-core-beans/docs/**` 的全量深化并归档（包含逐章“怎么继续加深”的基线提取与当时的落地结果）：

- `helloagents/history/2026-02/202602011343_beans_docs_deepen_all/`

你当前要的 Round 2，不是再补一套“统一格式/统一小标题/统一验收口径”的模板，而是继续把每一章**读者最容易卡住的那一块**补上：

- 有的章读完后，读者不知道“下一步怎么证”，需要把证据链写得更短、更贴近现象。
- 有的章缺“边界/对照”，需要用一个反例或对照实验把误解钉死。
- 有的章缺“排错闭环”，需要把主观判断压成可验证的步骤（能在调试器里看到分支/对象变化）。
- 也有章节已经很强，只需要把“可复用入口”写得更利于复盘与跳转（而不是为统一而重写）。

**写作约束（本轮一票否决项）**

- 不套统一小标题/固定检查表去重写每一章（避免“格式更统一、内容更空”）。
- 不改路径、不重排目录、不改章节编号体系（避免断链与读者习惯被破坏）。
- 不保留 “TODO/FIXME/未完/待补/占位” 这类占位；若历史上有占位语气，必须替换为“可验证解释”（指向章节/Lab/断点/排错路径）。
- 不引入敏感信息（密钥/token/内网地址/个人信息），示例命令不包含敏感参数。

## Change Content

1. 产出 Round 2 的逐章“继续加深”施工图（不套模板，按章差异化）：
   - `helloagents/history/2026-02/202602011541_beans_docs_deepen_round2_allchapters/audit/chapter-strategies.md`
2. 执行阶段按施工图逐章落地到 `spring-core-modules/spring-core-beans/docs/**`：
   - 每章只补它真正缺的拼图；对已强的章节，只做必要的“可复用性增强”（更短路径/更清晰承接/更少误判）。
3. 必要时做跨章节承接（按章需要补齐，避免堆链接列表）：
   - 当某章天然需要跳到 AOP/TX/Boot 等视角时，补齐“为什么跳 / 用什么入口验证”。
4. 质量门禁（执行阶段必须守住）：
   - 相对链接目标存在性检查（beans docs 全量）
   - 引用的测试类/文件路径存在性检查（beans docs 全量）
   - `mvn -pl spring-core-modules/spring-core-beans test` 回归通过

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`
- **Files:** `spring-core-modules/spring-core-beans/docs/**`（81 篇 Markdown）
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: R1-per-chapter-deepen-round2
**Module:** spring-core-beans docs
对 `spring-core-modules/spring-core-beans/docs/**` 做第二轮逐章差异化加深：每章补它最缺的那块，并确保读者能“按最短路径走到可验证结论”。

#### Scenario: S1-generate-per-chapter-strategies
输出 Round 2 逐章施工图：
- 覆盖 81 篇文档，每篇给出“继续加深”的具体策略（不套统一骨架）
- 策略应能落到：该章已有的推荐 Lab/源码入口/断点建议/排错路线（如该章已有卡片/工具段落，优先复用）

#### Scenario: S2-execute-per-chapter-changes
执行阶段逐章落地：
- 章节结构保持稳定，不做无差别重写
- 需要加深时，优先补“读者下一步动作”（复现/观察/对照/排错/承接）
- 不留下任何占位语气（TODO/FIXME/未完/待补）

### Requirement: R2-quality-gates
**Module:** spring-core-beans docs
保证 Round 2 的修改不引入断链、错误引用或敏感信息，并保持模块测试回归通过。

#### Scenario: S1-self-check-pass
- beans docs 相对链接目标存在性检查 missing targets = 0
- beans docs 引用的测试类/文件路径存在性检查通过
- `mvn -pl spring-core-modules/spring-core-beans test` 通过
- 不引入敏感信息（密钥/token/内网地址/个人信息）

## Risk Assessment

- **Risk:** 逐章继续加深容易变成“到处加料”，导致信息密度上升但路径变长。
  - **Mitigation:** 每章只做“缺的那一块”；优先补“下一步动作/可验证证据链/排错最短路径”，避免堆概念与堆链接。
- **Risk:** 跨章节承接与锚点链接调整容易产生断链或重复入口。
  - **Mitigation:** 执行时批次化修改 + 全量相对链接检查；用“最短跳转建议”替代“链接列表”。
- **Risk:** 章节风格被统一化写法牵引，读者感觉“更像模板作文”。
  - **Mitigation:** 不强制统一小标题；按章差异化补强，允许每章采用最合适的表达方式。
