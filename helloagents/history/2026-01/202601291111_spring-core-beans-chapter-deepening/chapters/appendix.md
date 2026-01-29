# 逐章补强建议（appendix 工具章节）

Appendix 的补强目标：把“工具型章节”真正做成可复用的索引与训练器，强化可检索性、跨章节映射、以及从症状到证据链的快速定位。

### 第 25 章：90. 常见误区清单（建议反复对照）

- 关联文件：`spring-core-modules/spring-core-beans/docs/appendix/025-90-common-pitfalls.md`
- 补强策略：
  - 增加“按机制域/阶段”分类索引：定义层/实例层/代理/占位符/FactoryBean/Boot/AOT，减少读者在长清单里搜索成本。
  - 为每个误区补“最短证据链入口”：至少给出 1 个断点入口或章节链接，避免只给结论。
  - 把误区映射到 Knowledge Map：让误区能反向导航到章节与实验。

### 第 26 章：99. 自测题：是否能够真的理解了？

- 关联文件：`spring-core-modules/spring-core-beans/docs/appendix/026-99-self-check.md`
- 补强策略：
  - 将题目与章节/实验/断点显式绑定：每道题给出“去哪章找证据/跑哪个 Lab”。
  - 增加“追问题”与“反例题”：避免自测只停留在背概念，推动到“边界条件与排障”。
  - 可选：补充答案结构模板（结论→证据链→反例）并链接到 interview playbook。

### 91. 术语表（Glossary）

- 关联文件：`spring-core-modules/spring-core-beans/docs/appendix/91-glossary.md`
- 补强策略：
  - 增加“同义词/易混词对照”：例如 BeanDefinition vs bean instance vs exposed object、BFPP vs BPP vs BDRPP、proxy vs target。
  - 为关键术语补章节反向链接：术语不是孤立解释，而能一键跳到具体章节与实验。
  - 统一术语用词：与各章节标题与正文用词对齐，减少“同一概念多叫法”导致的学习负担。

### 92. 知识地图（Knowledge Map）：从现象直达章节/断点/Lab

- 关联文件：`spring-core-modules/spring-core-beans/docs/appendix/92-knowledge-map.md`
- 补强策略：
  - 将地图从“概念维度”加强到“症状维度”：把常见异常/现象作为第一入口，映射到章节与 Lab。
  - 增加“断点包入口”引用：对每条主线给出 Debugger Pack 对应断点组，形成闭环。
  - 定期校验覆盖率：对新增章节/新增 Lab/新增误区及时更新地图，避免地图过时。

### 93. 面试复述模板（Interview Playbook）：用“证据链”回答 Spring IoC

- 关联文件：`spring-core-modules/spring-core-beans/docs/appendix/93-interview-playbook.md`
- 补强策略：
  - 增加“题目→章节→证据链→Lab”的闭环：每个题目给出至少一个可运行证明入口，避免背书。
  - 增补“高频追问与陷阱”：例如循环依赖的边界、FactoryBean 的 type matching、@Value/SpEL 的差异、代理的阶段等。
  - 加强答案结构一致性：保持“结论→方法级证据链→反例/误区”的格式，让读者可以直接照此训练。

### 94. 生产排障清单（Troubleshooting Checklist）：从症状到证据链

- 关联文件：`spring-core-modules/spring-core-beans/docs/appendix/94-production-troubleshooting-checklist.md`
- 补强策略：
  - 增强“异常分型→第一入口”的决策树：例如注入异常、代理异常、占位符异常、生命周期异常各自的第一断点入口。
  - 补充“观察点清单”：每类问题给出 3–5 个必须观察的变量/对象，帮助快速收敛。
  - 与 Part 00/断点地图强绑定：把排障清单与断点包链接，减少“知道要排障但不知道怎么下手”。

### 95. spring-beans Public API 索引（Spring Framework 6.2.15）

- 关联文件：`spring-core-modules/spring-core-beans/docs/appendix/95-spring-beans-public-api-index.md`
- 补强策略：
  - 将 API 索引与章节对齐：标注每个包/接口族主要覆盖在哪些章节，帮助读者从 API 反向定位知识点。
  - 增加“常用入口 API”突出区：把排障时最常用的 API/入口方法单列，并给出对应观察目的。
  - 版本提示：说明索引对应的 Spring Framework 版本，避免跨版本误用。

### 96. spring-beans Public API Gap 清单（按包/机制域分批深化）

- 关联文件：`spring-core-modules/spring-core-beans/docs/appendix/96-spring-beans-public-api-gap.md`
- 补强策略：
  - 把 gap 按“机制域”再细分：定义层/实例层/后处理器/类型转换/FactoryBean/AOT 等，并给出优先级建议。
  - 为每个 gap 增加“补强落点”：对应章节是否已有覆盖，若无，规划新增小节或新增 Lab。
  - 作为长期路线图：与 plan/ 的任务形成映射，避免 gap 清单长期停留在“未执行”。

### 97. Explore/Debug 用例（可选启用，不影响默认回归）

- 关联文件：`spring-core-modules/spring-core-beans/docs/appendix/97-explore-debug-tests.md`
- 补强策略：
  - 为每个 explore 用例标注“它在证明什么”：主线/分支/缓存/代理哪个机制点。
  - 增补“如何调试更高效”的提示：条件断点、断点组、watch list 的建议。
  - 校验与章节的一致性：确保章节引用的 explore 用例真实存在且命名可追踪。

### 98. Debugger Pack（断点包总入口）

- 关联文件：`spring-core-modules/spring-core-beans/docs/appendix/98-debugger-pack.md`
- 补强策略：
  - 将断点包按“问题类型”组织：注册/注入/代理/循环依赖/占位符/FactoryBean/Boot/AOT，并提供最小断点组。
  - 为每个断点补“看什么变量”：断点本身不够，要明确 watch list 与判断标准。
  - 增加“断点稳定性”注记：哪些断点跨版本稳定，哪些可能漂移，帮助读者维护断点包。

### 99. 团队内训讲义（Training Kit）：可直接开讲的课时脚本

- 关联文件：`spring-core-modules/spring-core-beans/docs/appendix/99-team-training-kit.md`
- 补强策略：
  - 按课时目标补“可跑实验 + 互动题 + 讲师提示”：让讲义更像可直接执行的培训脚本。
  - 增加“课后作业/自测”映射：把讲义与 self-check / interview playbook 串起来，形成训练闭环。
  - 加强团队落地建议：如何把断点包/知识地图融入日常排障与 code review 讨论。

