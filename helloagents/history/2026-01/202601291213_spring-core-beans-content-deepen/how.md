# Technical Design: spring-core-beans 内容级再加深（全章 A–E 维度）

## Technical Solution

### Core Technologies
- **Docs:** Markdown（`spring-core-modules/spring-core-beans/docs/**.md`）
- **Verification:** JUnit 5 + Maven Surefire（模块 Lab/Test）
- **Debugging:** IntelliJ IDEA Debugger（断点组 + watch list）

### Implementation Key Points

#### 1) 逐章“差异化增量”策略（避免模板化填空）

- 每章先做“现状取证”：当前章节已经覆盖哪些机制/链路/反例/排障入口（以章节内已有的入口方法、决策表、Lab/Test 为准）。
- 再做“增量补强”：只补“缺口最大的 2–4 个点”，确保新增内容带来可观测/可证明收益，而不是重复堆叠。

#### 2) A–E 五维度的落地方式（按章选取最合适表现形态）

为避免形成“统一标准”，本次不强制每章都用同样小节结构，但确保每章都能在内容层面覆盖 A–E：

- A（证据链）：用“最短调用链 + 关键分支条件 + 必看变量”呈现  
  形式可选：调用链列表 / 分支决策表 / 关键方法注释块
- B（边界反例）：用“反例/失败分型 + 规避策略 + 可复现入口”呈现  
  形式可选：反例小节 / 失败分型表 / 典型坑与修复
- C（排障 SOP）：用“症状 → 分层（定义/实例）→ 第一断点 → 观察点 → 修复”呈现  
  形式可选：排障决策表 / 3 步 SOP / 快速定位卡片
- D（断点与观察）：用“断点组 + watch list + 判定标准”呈现  
  形式可选：断点闭环小节 / Debugger Pack 交叉引用
- E（面试复述）：用“结论→证据链→反例→追问”呈现  
  形式可选：面试常问/追问块 / 复述模板链接

#### 3) Lab/Test 的增量增强原则

- 优先复用既有 Lab/Test：用更强的断言、更多边界用例、以及更清晰的可观察输出固化结论。
- 必要时新增 Lab/Test：只为“新增内容里最关键且最容易误判的结论”新增最小可复现用例，避免测试膨胀。
- 回归基线：每轮修改后跑 `mvn -pl :spring-core-beans test`，保证模块全量回归通过。

#### 4) 文档一致性与导航策略

- 目录页（`docs/README.md`）作为“症状导航中枢”，持续增强“从现象到章节”的跳转体验。
- Chapter 之间的横向串联：把“同一机制在不同章节的出现窗口”串起来（例如 `FactoryBean` 与 type matching、`@Lazy` 与 proxy、AOT 与 reflection/proxy hints）。
- 对版本敏感内容：显式标注适用条件，避免读者跨版本误用。

## Security and Performance

- **Security:** 对 SpEL/表达式/反射相关章节补充“安全边界与风险提示”，避免鼓励高风险用法（如不受控表达式求值）。
- **Performance:** 新增测试以“最小集”为原则；避免引入长耗时或依赖外部环境的用例；必要时放入 Explore/Debug 可选用例而非默认回归。

## Testing and Deployment

- **Testing:**
  - 全量回归：`mvn -pl :spring-core-beans test`
  - 单章定向：`mvn -pl :spring-core-beans -Dtest=<TestClassName> test`
  - Explore/Debug（可选）：`mvn -pl :spring-core-beans -Dspringcorebeans.explore=true -Dtest=SpringCoreBeans*ExploreTest test`
- **Deployment:** 无（学习型模块文档与测试变更）

