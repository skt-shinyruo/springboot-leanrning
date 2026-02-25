# 逐章内容级再加深建议（appendix 工具章节）

Appendix 的再加深重点：把“工具页”做成可复用的训练与排障中枢——读者能从症状快速定位、能把答案用证据链证明、能用题库训练复述。

### 90. 常见误区清单

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/01-common-pitfalls.md`
- 内容级加深策略：
  - A：为每类误区补“最短证据链入口方法”链接（避免只给结论）。
  - B：为高频误区补“反例对照”：如何一眼区分相似但本质不同的问题（如 depends-on 环 vs 循环依赖）。
  - C：把误区映射到排障 SOP：遇到该误区的典型症状与第一断点入口。
  - D：补断点建议：每类误区给最小断点组与 watch list。
  - E：把误区转成面试追问（追问“为什么/如何证明/反例是什么”）。

### 99. 自测题

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/11-self-check.md`
- 内容级加深策略：
  - A：每题绑定“证据链入口方法 + 推荐 Lab”，让自测变成可证明训练。
  - B：加入“反例题/边界题”，避免只背概念。
  - C：把自测题按“定义层/实例层/代理/值解析/Boot/AOT”分型，形成排障能力训练。
  - D：为高频题给断点闭环建议（断点+watch list+判定标准）。
  - E：与 interview playbook 互链：自测题可直接转为面试复述练习。

### 91. 术语表

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/02-glossary.md`
- 内容级加深策略：
  - A：为关键术语补“对应证据链入口方法”，避免术语解释与源码脱节。
  - B：补易混词反例：BeanDefinition vs bean instance vs exposed object；BFPP vs BPP vs BDRPP 等。
  - C：补“术语误诊”排障提示：遇到某词汇时如何避免错误联想。
  - D：为核心术语补“看见它”的断点/观察点。
  - E：将术语映射到面试题：术语解释必须能给出证据链与反例。

### 92. 知识地图

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/03-knowledge-map.md`
- 内容级加深策略：
  - A：把每条主线补“证据链入口方法”，与章节内部一致。
  - B：为每条症状补“最常见反例/误诊点”，提高定位精度。
  - C：强化“症状→章节→Lab→断点”的完整闭环，作为排障导航主入口之一。
  - D：与 Debugger Pack/断点地图互链，形成可复用断点套件。
  - E：把知识地图与面试题库映射：某题对应哪条地图路径与证明方式。

### 93. 面试复述模板

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/04-interview-playbook.md`
- 内容级加深策略：
  - A：为每道题补“方法级证据链”（最短调用链 + 决策点）。
  - B：为高频题补“反例/边界追问”，避免背诵式答案。
  - C：为题目增加“真实排障对应场景”，让面试题能反哺工程能力。
  - D：为题目给“断点证明路径”，帮助读者用 IDE 复现实证。
  - E：统一答案结构：结论→证据链→反例→追问（保持一致可训练性）。

### 94. 生产排障清单

- 文件：`spring-core-modules/spring-core-beans/docs/appendix/05-production-troubleshooting-checklist.md`
- 内容级加深策略：
  - A：为每类症状补“第一断点入口 + 关键变量”，把清单变成可执行 SOP。
  - B：补“误判对照”：相似症状可能属于不同机制域，如何快速分流。
  - C：把排障清单与 docs/README 症状导航打通（双向链接）。
  - D：与 Debugger Pack 互链：每类症状给推荐断点组。
  - E：把排障题转成面试追问（如何定位/如何证明/如何修复）。

### 95/96. public API 索引与 gap

- 文件：
  - `spring-core-modules/spring-core-beans/docs/appendix/06-spring-beans-public-api-index.md`
  - `spring-core-modules/spring-core-beans/docs/appendix/07-spring-beans-public-api-gap.md`
- 内容级加深策略：
  - A：为每个 API 域补“对应章节与证据链入口”，帮助从 API 反向定位机制。
  - B：为 gap 项补“反例/边界触发条件”，明确为何它是 gap。
  - C：为 API 域补“常见排障场景入口”，让索引服务于排障而不是目录堆叠。
  - D：补建议断点：哪个 API 域对应哪个关键断点入口。
  - E：补面试题映射：某 API 域典型面试题与证明路径。

### 97/98/99. Explore/Debug / Debugger Pack / Team Training

- 文件：
  - `spring-core-modules/spring-core-beans/docs/appendix/08-explore-debug-tests.md`
  - `spring-core-modules/spring-core-beans/docs/appendix/09-debugger-pack.md`
  - `spring-core-modules/spring-core-beans/docs/appendix/10-team-training-kit.md`
- 内容级加深策略：
  - A：为每个用例/断点包补“它在证明什么机制分支”，让工具页更可复用。
  - B：补“反例与踩坑点”：如何避免用例/断点被版本差异误导。
  - C：把工具页与排障清单/知识地图/目录页打通，形成统一导航。
  - D：补 watch list 与判定标准：断点停下后看什么值才算“证据成立”。
  - E：将工具页变成训练脚本：面试复述/团队内训可直接引用其证据链与复现入口。

