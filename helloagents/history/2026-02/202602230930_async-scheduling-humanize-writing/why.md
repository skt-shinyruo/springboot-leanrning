# Change Proposal: spring-boot-async-scheduling “人写化”改造（docs + DemoRunner）

## Requirement Background

当前 `spring-boot-modules/spring-boot-async-scheduling` 的技术深度与证据入口已经较完整，但阅读体验更像“工具输出”而不是“作者写作”：

- 文本结构过度模板化：每章重复同类段落与句式，读者在理解机制前先被结构疲劳消耗
- 口吻偏教练式：反复出现“建议/你应该/先跑再读”等驱动式表述，缺少自然叙事与作者观点的连贯推进
- 表达过度列表化：关键论证被 bullet 替代，像摘要而不像文章
- 抽象口号词密度偏高：例如“闭环/证据链/手册级”等词汇重复出现，会让读者产生“AI 生成感”

你的目标是：在不牺牲技术正确性与可回归性的前提下，把整个模块改写成更像“教材 + 技术博客”的作者稿。

## Change Content

1. **docs 全量人写化改写**：主线章节、导读、附录、矩阵与断点地图都改为“连贯叙事 + 关键分支明确 + 证据入口下移”的写法
2. **弱化骨架感但保留导航能力**：
   - 保留 `CHAPTER-CARD/GLOBAL-BOOK-NAV/BOOKIFY` 等结构性标记（便于站点/导航）
   - 但把“证据入口/Lab/矩阵/断点”从正文强行插入，改为章末“进一步验证/想自己跑一遍”的自然补充
3. **DemoRunner 输出人写化**：将 `spring-boot:run` 的输出从机械 key/value 改为分节讲解式输出（仍保留可观察点）
4. **README 人写化**：从“索引表格”转向“阅读路线 + 运行方式 + 为什么这样组织”

## Impact Scope

- **Modules:** `spring-boot-modules/spring-boot-async-scheduling`
- **Files:**
  - docs：`spring-boot-modules/spring-boot-async-scheduling/docs/**/*.md`
  - module README：`spring-boot-modules/spring-boot-async-scheduling/README.md`
  - DemoRunner：`spring-boot-modules/spring-boot-async-scheduling/src/main/java/**/AsyncSchedulingDemoRunner.java`

## Core Scenarios

### Requirement: 文档改写后更像“人写的”，但仍可定位与可验证
**Module:** springboot-async-scheduling
把“作者叙事感”作为正文主线，把“验证入口”作为读者可选的下沉动作。

#### Scenario: 主线章节读起来像连续文章
- 以真实工程场景切入，而不是模板化“本章要点”
- 关键分支用自然语言解释（为什么会这样），而不是堆表格
- 证据入口在章末以“进一步验证”给出，不抢正文阅读节奏

#### Scenario: Appendix 更像排障短文/习题册
- Pitfalls：像“排障随笔 + 经验总结”，但保留 Proof 指针
- Self-check：像“习题 + 提示 + 参考入口”，而不是机械问卷

#### Scenario: Guide 页减少模板词与教练口吻
- 时间线/导读更像作者写的导言
- 矩阵与断点地图保留功能性，但改写为“读者真的会用”的表达

### Requirement: DemoRunner 输出更像作者讲解
**Module:** springboot-async-scheduling

#### Scenario: 一次运行能读懂核心观察点
- 输出按主题分节（线程/事务/上下文/自动装配）
- 每节提供 1–2 句解释与 2–3 个关键观测值（线程名/是否 active/是否 null）

### Requirement: 技术正确性不变、可回归
**Module:** springboot-async-scheduling

#### Scenario: 文档改写不改变既有语义结论
- 结论仍以现有 `*LabTest#method` 作为事实来源
- `mvn -q -pl :spring-boot-async-scheduling test` 连续 3 次回归全绿

## Risk Assessment

- **Risk:** 文档“人写化”属于主观目标，容易出现“改得多但不对味”的返工。
  - **Mitigation:** 每次改写按章节拆小任务，允许快速 review；优先先改 2–3 篇示范后再批量推进。
- **Risk:** 重写可能造成相对链接、导航、锚点失效。
  - **Mitigation:** 不改文件名/路径；保留 `GLOBAL-BOOK-NAV/BOOKIFY`；改写后做 docs 相对链接自检（必要时补脚本或最小人工检查）。
- **Risk:** DemoRunner 输出改变后，阅读者可能找不到“可复制的观察点”。
  - **Mitigation:** 输出仍保留稳定字段（线程名/active/null），只是把叙述方式改为更自然的分节讲解。
