# Task List: deepen_beans_aop_tx_web（超细化长期路线图）

> Note(2026-01-14): 本方案包剩余未执行任务已在后续方案包中拆分/覆盖，统一标记为 [-]；具体落地请以后续 history 方案包与当前仓库代码为准（见 helloagents/history/index.md）。

Directory: `helloagents/plan/202601091043_deepen_beans_aop_tx_web/`

> 目标：在本仓库内把 **容器（Beans）→ AOP → 事务（Tx）→ Web MVC（含与 Security 交互）** 这条主线做成：
> 1) **可导航**（统一学习路径）  
> 2) **可验证**（关键结论都能用 `*LabTest` 稳定断言）  
> 3) **可练习**（`*ExerciseTest` 提供练习题，默认 `@Disabled`；可选提供 `*ExerciseSolutionTest` 做答案对照）  
> 4) **可排障**（Troubleshooting 清单：现象→根因→如何验证→推荐断点/观察点→修复建议）
>
> 说明：
> - 本文件是“可执行的超细粒度任务清单（Roadmap）”，颗粒度尽量细到“能直接开工写/改一个文件”的级别。
> - 灵感池/额外想法保留在 `task-backlog.md`；但本文件不再依赖 backlog 才能开工。

---

## 0. 全局约定（必须先统一，否则越写越乱）

### 0.1 基线验证（每次开工前/阶段收尾必跑）
- [√] 0.1.2 运行四模块测试基线（确保无回归）：
  - `bash scripts/test-module.sh spring-core-beans`
  - `bash scripts/test-module.sh spring-core-aop`
  - `bash scripts/test-module.sh spring-core-tx`
  - `bash scripts/test-module.sh springboot-web-mvc`
- [-] 0.1.3 记录（无需写入文档）当前失败用例与失败原因（若有）：先定位“环境问题 vs 代码问题”

### 0.2 统一“文档 ↔ 测试入口 ↔ 排障清单”的 SSOT 约定（R0）
- [√] 0.2.1 在 `helloagents/project.md` 明确：`*LabTest` 的定位（可验证 SSOT / 默认启用 / 断言稳定契约）
- [√] 0.2.2 在 `helloagents/project.md` 明确：`*ExerciseTest` 的定位（练习题 / 默认 `@Disabled` / 通过测试提示驱动学习）
- [√] 0.2.3 在 `helloagents/project.md` 明确：`*ExerciseSolutionTest` 的定位（答案对照 / 可选启用 / CI 是否启用的原则）
- [√] 0.2.4 在 `helloagents/project.md` 明确：断言粒度原则（优先断言稳定可观察行为；不要把内部实现细节写死）
- [-] 0.2.5 在 `helloagents/project.md` 增加“文档章节契约模板”（每章至少包含：本章输出/关键分支/验证入口/断点与观察点/常见坑/自检题）
- [√] 0.2.6 在 `helloagents/project.md` 增加“Troubleshooting 条目模板”（现象→根因→如何验证→推荐断点/观察点→修复建议）
- [-] 0.2.7 在 `helloagents/project.md` 增加“跨模块链接与索引规范”（相对链接优先、索引集中、避免漂移）

### 0.3 统一四模块学习路径（导航体验一致）
- [-] 0.3.1 对齐四模块 Deep Dive Guide：每个 guide 都必须提供：
  - 推荐阅读顺序（按主线/按主题）
  - 推荐 Labs 索引（按场景）
  - 推荐断点/观察点索引（按链路节点）
- [-] 0.3.2 对齐四模块 `docs/README.md`：目录可导航（Part 导航 + 关键章节 + 对应测试入口）
- [-] 0.3.3 对齐四模块 module README：把“怎么学/怎么跑/怎么排障”放在最显眼位置（中文）
- [-] 0.3.4 在 `helloagents/wiki/overview.md` 或新增统一页：输出“主线学习路线图（容器→AOP→Tx→Web）”并链接到四模块 guide
- [-] 0.3.5 在 `helloagents/wiki/modules/` 下对齐四模块页面结构：
  - Docs Index（按 Part/关键章节）
  - Labs Index（按场景）
  - Troubleshooting Index（按现象）
  - Self-check Index（按主线）

### 0.4 “测试入口命名与放置”统一（便于 grep/跳转）
- [-] 0.4.1 统一各模块 `part00_guide` 下的 `*LabTest` 索引表（如已有则补齐缺口）
- [-] 0.4.2 统一各模块 `appendix` 下：
  - `90-common-pitfalls.md`（排障清单）
  - `99-self-check.md`（自检题）
  - 以及与之对应的 `*PitfallLabTest/*SelfCheckLabTest`（如采用测试承载自检/排障）
- [-] 0.4.3 统一每个新增测试的“证据链”输出方式（如 InvocationLog / Observations 前缀），避免散乱

---

## 1. 容器（spring-core-beans）：主线深化（R1）

> 目标：围绕容器 refresh 主线与扩展点（BFPP/BPP/生命周期/依赖解析），补齐“顺序 + 边界 + 排障”三条能力线。  
> 覆盖场景：S1-bpp-ordering / S2-circular-dependency-boundary / S3-injection-candidate-selection

### 1.0 Beans 阶段 DoD（验收标准）
- [-] 1.0.1 文档主线可复述：refresh 时间线、PP 介入点、实例化/注入/初始化关键节点
- [-] 1.0.2 每个场景至少 1 个 `*LabTest` 可稳定复现关键分支（并有文档绑定入口）
- [-] 1.0.3 `90-common-pitfalls.md` 至少覆盖：BPP 顺序、循环依赖边界、候选选择报错三类常见坑（每条都指向测试入口）
- [-] 1.0.4 `99-self-check.md` 至少 15 题（每题指向：doc + test）

### 1.1 场景 S1：BPP/BFPP 顺序（“顺序就是机制”）

#### 1.1.1 文档（把顺序写成可断点时间线）
- [-] 1.1.1.1 更新 `docs/beans/spring-core-beans/part-01-ioc-container/` 相关章节：给出 refresh 时间线（以关键方法/关键类为节点）
- [-] 1.1.1.2 增加 BFPP vs BPP 对比表：作用对象（BeanDefinition vs Bean instance）、执行时机、常见用途、典型坑
- [-] 1.1.1.3 增加排序规则说明：`PriorityOrdered` / `Ordered` / 无序，分别在哪一步生效
- [-] 1.1.1.4 增加“为什么你写的 BPP 没生效”的 checklist：注册时机、包扫描、条件装配、BeanFactory 类型等
- [-] 1.1.1.5 每章末尾补齐：对应 Lab/Test + 推荐断点（例如：invokeBeanFactoryPostProcessors/registerBeanPostProcessors/createBean）

#### 1.1.2 Labs（用断言把顺序钉死）
- [-] 1.1.2.1 新增 `SpringCoreBeansPostProcessorOrderingLabTest`（建议放 `part03_container_internals`）
- [-] 1.1.2.2 新增 testsupport：事件记录器（记录“何时注册/何时执行/何时影响 BeanDefinition/Bean instance”）
- [-] 1.1.2.3 用例：`BeanDefinitionRegistryPostProcessor` 的顺序与影响（能改 registry 的证明）
- [-] 1.1.2.4 用例：`BeanFactoryPostProcessor` 的顺序与影响（能改 BeanDefinition 属性/占位符的证明）
- [-] 1.1.2.5 用例：`BeanPostProcessor` 的顺序与影响（能在 before/after init 记录/包装的证明）
- [-] 1.1.2.6 用例：`InstantiationAwareBeanPostProcessor` / `SmartInstantiationAwareBeanPostProcessor` 的介入点（与早期引用/代理的关系）
- [-] 1.1.2.7 用例：对比 PriorityOrdered/Ordered/无序三组，断言事件序列（只断言“相对顺序”，避免绑死实现细节）

#### 1.1.3 Exercises（让你手写一个最小 PP）
- [-] 1.1.3.1 在 `spring-core-beans` 新增 `*ExerciseTest`：要求学员实现一个 PP，使某个 bean 在容器启动后具备特定可观察特征
- [-] 1.1.3.2 为 exercise 给出“失败提示 → 指向 docs 的哪个章节/哪个 Lab”
- [-] 1.1.3.3 （可选）增加 `*ExerciseSolutionTest`：提供参考实现并作为对照

### 1.2 场景 S2：循环依赖边界（能救/不能救）

#### 1.2.1 文档（把边界写成对照矩阵）
- [√] 1.2.1.1 更新 `docs/beans/spring-core-beans/part-01-ioc-container/08-circular-dependencies.md`：
  - constructor vs setter 的根因差异
  - singleton vs prototype 的边界
  - `@Lazy` / `ObjectProvider` 的“打断策略”与代价
- [√] 1.2.1.2 更新 `docs/beans/spring-core-beans/part-03-container-internals/05-early-reference-and-circular.md`：
  - early singleton exposure 的关键节点
  - `getEarlyBeanReference` 与 AOP 代理的关系（只写机制，不绑定具体实现细节）
- [-] 1.2.1.3 在上述两章末尾补齐：对照用例索引（每个分支对应一个 test 方法）

#### 1.2.2 Labs（把“能救/不能救”钉死）
- [√] 1.2.2.1 新增/强化 `SpringCoreBeansCircularDependencyBoundaryLabTest`：
  - 用例：constructor 循环 fail-fast（对照）
  - 用例：constructor + `@Lazy` 打断（对照）
  - 用例：constructor + `ObjectProvider<T>` 打断（对照）
- [-] 1.2.2.2 增加用例：setter 循环（证明 3-level cache 能救的边界）
- [-] 1.2.2.3 增加用例：prototype 循环（证明“救不了”的边界）
- [-] 1.2.2.4 增加用例：引入 AOP 时 early reference 的表现（证明“早期引用拿到的是谁”这件事）

#### 1.2.3 Troubleshooting（把坑写成清单）
- [-] 1.2.3.1 在 `docs/beans/spring-core-beans/appendix/90-common-pitfalls.md` 增加条目：
  - “循环依赖为什么突然报错（升级版本/引入构造注入/引入代理）”
  - “@Lazy 能救但为什么带来 NPE/时序问题”
  - “ObjectProvider 为什么能救但为什么会推迟失败”
- [-] 1.2.3.2 每条都必须：指向对应 LabTest + 推荐断点（例如 DefaultSingletonBeanRegistry/AbstractAutowireCapableBeanFactory）

### 1.3 场景 S3：依赖注入候选选择（@Primary/@Qualifier/泛型）

#### 1.3.1 文档（把候选选择写成可排障流程图）
- [-] 1.3.1.1 增加/深化候选选择章节：候选收集 → 过滤 → 决胜规则（Primary/Qualifier/Name/泛型匹配/Order）
- [-] 1.3.1.2 在文档中明确常见报错的“定位入口”：
  - NoSuchBeanDefinitionException（无候选）
  - NoUniqueBeanDefinitionException（多候选）
  - UnsatisfiedDependencyException（上游包装异常）
- [-] 1.3.1.3 补齐推荐断点：`DefaultListableBeanFactory#doResolveDependency` 等

#### 1.3.2 Labs（用对照实验把规则钉死）
- [-] 1.3.2.1 新增 `SpringCoreBeansInjectionCandidateSelectionLabTest`（建议放 `part01_ioc_container` 或 `part04_wiring_and_boundaries`）
- [-] 1.3.2.2 用例：两个实现 + `@Primary` 决胜
- [-] 1.3.2.3 用例：`@Qualifier` 决胜（含 name qualifier）
- [-] 1.3.2.4 用例：泛型匹配（如 `Repository<Foo>` vs `Repository<Bar>`）
- [-] 1.3.2.5 用例：集合注入（List/Map）与排序（@Order）——强调“排序≠候选决胜”
- [-] 1.3.2.6 用例：`ObjectProvider` 延迟解析（与循环依赖/可选依赖的关系）

#### 1.3.3 Exercises（把排障写成练习题）
- [-] 1.3.3.1 新增 `*ExerciseTest`：给出一个“多候选注入失败”的场景，让学员用三种方式修复（Primary/Qualifier/重构注入点）
- [-] 1.3.3.2 新增 `*ExerciseTest`：给出一个“泛型不匹配导致无候选”的场景，让学员改类型签名/bean 定义

### 1.4 Beans 阶段性回归
- [√] 1.4.1 运行 Beans 模块测试：`bash scripts/test-module.sh spring-core-beans`
- [√] 1.4.3 更新知识库：`helloagents/wiki/modules/spring-core-beans.md`（Docs/Labs/Troubleshooting 索引同步）

---

## 2. AOP（spring-core-aop）：主线深化（R2）

> 目标：围绕代理产生时机、Advisor 链、切点匹配、自调用边界，做到“能解释 + 能断点 + 能排障”。  
> 覆盖场景：S1-self-invocation / S2-auto-proxy-creator-mainline / S3-jdk-vs-cglib-type-diff

### 2.0 AOP 阶段 DoD（验收标准）
- [-] 2.0.1 能给出“为什么这个 bean 会/不会被代理”的证据链（文档 + Lab）
- [-] 2.0.2 能稳定复现：自调用不生效、exposeProxy 生效、拆分 bean 生效 三者对照
- [-] 2.0.3 能稳定断言：JDK vs CGLIB 代理类型差异、final/private 等限制
- [-] 2.0.4 pitfalls 至少 12 条（每条绑定测试入口 + 推荐断点）
- [-] 2.0.5 self-check 至少 15 题（每题指向 doc + test）

### 2.1 场景 S1：自调用导致 AOP 不生效（机制边界）

#### 2.1.1 文档（把“为什么不生效”说清楚）
- [√] 2.1.1.1 更新 `docs/aop/spring-core-aop/appendix/90-common-pitfalls.md`：
  - this 调用绕过代理的根因
  - 为什么“加了 @Transactional/@Async 也不生效”同理（代理边界）
  - 三种修复策略：拆分 bean / 自注入代理 / exposeProxy（说明生产边界）
- [√] 2.1.1.2 更新对应主线章节：把“自调用”放在 proxy fundamentals 的核心坑位（而不是附录角落）

#### 2.1.2 Labs（把对照钉死）
- [√] 2.1.2.1 新增/强化 `SpringCoreAopExposeProxyLabTest`：断言 outer+inner 都命中 advice（证据链：InvocationLog）
- [-] 2.1.2.2 新增 Lab：`SpringCoreAopSelfInvocationPitfallLabTest`（对照 this 调用 vs 代理调用）
- [-] 2.1.2.3 新增 Lab：`SpringCoreAopSelfInjectionWorkaroundLabTest`（自注入/自代理方案）
- [-] 2.1.2.4 对每个 Lab：补齐推荐断点（ProxyFactory/ReflectiveMethodInvocation/AdvisedSupport 等）

#### 2.1.3 Exercises（把“修复 AOP 不生效”变成练习）
- [-] 2.1.3.1 新增 `*ExerciseTest`：给出一个“自调用导致 advice 不生效”的业务样例，让学员用拆分 bean 修复
- [-] 2.1.3.2 新增 `*ExerciseTest`：同样样例，让学员用“自注入代理”修复
- [-] 2.1.3.3 练习题必须提供：提示（指向 doc）+ 最终验证方式（断言 InvocationLog/side effect）

### 2.2 场景 S2：AutoProxyCreator 主线（为什么它能代理你）

#### 2.2.1 文档（把 APC 写成“可断点主线”）
- [-] 2.2.1.1 更新 `docs/aop/spring-core-aop/part-02-autoproxy-and-pointcuts/`：补齐 APC 发生在 BPP after-init 的主线解释
- [-] 2.2.1.2 增加“Advisor 装配与匹配”章节：Advisor 来源、Pointcut 匹配、MethodMatcher
- [-] 2.2.1.3 增加“为什么这个 bean 没被代理”的 checklist：
  - 无 Advisor 命中
  - bean 不可代理（final/classloader）
  - 代理创建时机/循环依赖/early reference
- [-] 2.2.1.4 每章末尾补齐：对应 Lab/Test + 推荐断点（AbstractAutoProxyCreator）

#### 2.2.2 Labs（把“代理判定”钉死）
- [-] 2.2.2.1 新增 `SpringCoreAopAutoProxyCreatorMainlineLabTest`
- [-] 2.2.2.2 用例：Advisor 命中 → 确认 bean 是代理（断言代理类型 + invocation evidence）
- [-] 2.2.2.3 用例：Advisor 不命中 → 确认 bean 不是代理（断言类型/行为）
- [-] 2.2.2.4 用例：同一 bean 多个 advisor 命中 → 断言 advisor 链顺序（@Order + 默认排序）

### 2.3 场景 S3：JDK vs CGLIB（类型差异与限制）

#### 2.3.1 文档（把“类型差异”写成对照表）
- [-] 2.3.1.1 更新 `docs/aop/spring-core-aop/part-01-proxy-fundamentals/`：补齐 JDK/CGLIB 选择条件
- [-] 2.3.1.2 增加“final/private/static/构造器”对 AOP 生效影响说明（强调 CGLIB 限制与可观察症状）
- [-] 2.3.1.3 增加“类型断言与排障入口”（如何在测试里断言代理类型、如何从异常反推）

#### 2.3.2 Labs（把差异钉死）
- [-] 2.3.2.1 新增 `SpringCoreAopProxyTypeDiffLabTest`
- [-] 2.3.2.2 用例：接口代理（JDK）vs 类代理（CGLIB）对照
- [-] 2.3.2.3 用例：final 方法不被拦截（可观察证据：InvocationLog 缺失）
- [-] 2.3.2.4 用例：类型转换边界（JDK 代理不能强转为实现类）

### 2.4 AOP 阶段性回归
- [√] 2.4.1 运行 AOP 模块测试：`bash scripts/test-module.sh spring-core-aop`
- [√] 2.4.3 更新知识库：`helloagents/wiki/modules/spring-core-aop.md`

---

## 3. 事务（spring-core-tx）：主线深化（R3）

> 目标：围绕事务边界、传播行为、回滚规则，形成“可验证 + 可排障”的稳定心智模型。  
> 覆盖场景：S1-tx-boundary-and-proxy / S2-propagation-matrix / S3-rollback-rules

### 3.0 Tx 阶段 DoD（验收标准）
- [-] 3.0.1 能用调用链定位：`@Transactional` → Advisor → TransactionInterceptor → PlatformTransactionManager
- [-] 3.0.2 能稳定断言“事务是否真正开启/挂起/嵌套”（而不是只看日志）
- [-] 3.0.3 覆盖 6+ 传播行为的对照矩阵（至少 REQUIRED/REQUIRES_NEW/NESTED/MANDATORY/NEVER/NOT_SUPPORTED）
- [-] 3.0.4 回滚规则覆盖：Runtime vs Checked、rollbackFor/noRollbackFor、捕获吞异常导致不回滚/UnexpectedRollback
- [-] 3.0.5 pitfalls 至少 15 条；self-check 至少 20 题

### 3.1 场景 S1：事务边界与代理（为什么必须跨 bean）

#### 3.1.1 文档（把边界写成“排障 checklist”）
- [-] 3.1.1.1 更新 `docs/tx/spring-core-tx/part-01-transaction-basics/01-transaction-boundary.md`：补齐“边界”的可验证结论
- [-] 3.1.1.2 更新 `docs/tx/spring-core-tx/part-01-transaction-basics/02-transactional-proxy.md`：
  - `@Transactional` 如何变成 Advisor/Interceptor
  - 推荐断点：TransactionInterceptor/TransactionAspectSupport
- [-] 3.1.1.3 在 `docs/tx/spring-core-tx/appendix/90-common-pitfalls.md` 增补：
  - “事务不生效（自调用/非 public/未被代理）” checklist（绑定 Lab）

#### 3.1.2 Labs（把“事务不生效”钉死）
- [-] 3.1.2.1 新增/强化 `SpringCoreTxSelfInvocationPitfallLabTest`（appendix）
- [-] 3.1.2.2 新增 Lab：`SpringCoreTxBoundaryAcrossBeansLabTest`（对照：跨 bean 调用事务生效）
- [-] 3.1.2.3 每个 Lab 断言点必须可观察：事务 active 标记/连接标识/数据库结果

### 3.2 场景 S2：传播行为矩阵（挂起/新开/嵌套）

#### 3.2.1 文档（把传播写成“矩阵 + 用例索引”）
- [√] 3.2.1.1 更新 `docs/tx/spring-core-tx/part-01-transaction-basics/04-propagation.md`：
  - 常用 vs 进阶分层（REQUIRED/REQUIRES_NEW/NESTED/MANDATORY/NEVER/NOT_SUPPORTED/SUPPORTS）
  - 每种传播的“可观察信号”（谁挂起/谁新开/是否 savepoint）
  - 每个传播绑定到具体 test method
- [-] 3.2.1.2 在文档中明确：NESTED 的前置条件与误区（savepoint/底层事务管理器差异）

#### 3.2.2 Labs（把矩阵钉死）
- [√] 3.2.2.1 新增/强化 `SpringCoreTxPropagationMatrixLabTest`
- [√] 3.2.2.2 用例：MANDATORY（无外层事务时抛异常）
- [√] 3.2.2.3 用例：NEVER（有外层事务时抛异常）
- [-] 3.2.2.4 用例：NOT_SUPPORTED（外层事务被挂起）
- [-] 3.2.2.5 用例：REQUIRES_NEW（外层挂起 + 新事务）
- [√] 3.2.2.6 用例：NESTED（savepoint 回滚内层但外层可提交：需要可观察断言）
- [-] 3.2.2.7 为每个用例补齐推荐断点：TransactionAspectSupport / AbstractPlatformTransactionManager

### 3.3 场景 S3：回滚规则（Runtime vs Checked）

#### 3.3.1 文档（把规则写成“可预测表”）
- [√] 3.3.1.1 新增/深化 `docs/tx/spring-core-tx/part-01-transaction-basics/03-rollback-rules.md`：
  - 默认规则总结表
  - rollbackFor/noRollbackFor 的覆盖关系
  - 捕获吞异常导致“不回滚/UnexpectedRollback”的机制
- [-] 3.3.1.2 在 `docs/tx/spring-core-tx/appendix/90-common-pitfalls.md` 增补回滚相关排障条目（每条绑定 Lab）

#### 3.3.2 Labs（把规则钉死）
- [√] 3.3.2.1 新增 `SpringCoreTxRollbackRulesLabTest`
- [√] 3.3.2.2 用例：RuntimeException 默认回滚
- [√] 3.3.2.3 用例：CheckedException 默认不回滚
- [√] 3.3.2.4 用例：rollbackFor 让 Checked 回滚
- [√] 3.3.2.5 用例：noRollbackFor 阻止 Runtime 回滚
- [-] 3.3.2.6 用例：try/catch 吞异常导致“看似成功但回滚/rollback-only”对照（用数据库结果断言）

### 3.4 Tx 自检与排障（体验对齐）
- [-] 3.4.1 重构 `docs/tx/spring-core-tx/appendix/90-common-pitfalls.md` 为统一模板，并覆盖至少 15 条
- [-] 3.4.2 更新 `docs/tx/spring-core-tx/appendix/99-self-check.md`：新增至少 20 题（传播+回滚+边界）
- [√] 3.4.3 在 `helloagents/wiki/modules/spring-core-tx.md` 汇总：Docs/Labs/排障入口

### 3.5 Tx 阶段性回归
- [√] 3.5.1 运行 Tx 模块测试：`bash scripts/test-module.sh spring-core-tx`

---

## 4. Web MVC（springboot-web-mvc）：主线深化（R4）

> 目标：围绕请求处理链路、参数绑定/校验、异常处理链路，以及与 Security FilterChain 的交互，补齐“可验证 + 可排障”的主线。  
> 覆盖场景：S1-argument-binding-and-validation / S2-exception-resolver-chain / S3-security-filterchain-interaction

### 4.0 Web MVC 阶段 DoD（验收标准）
- [-] 4.0.1 能解释并断点：从请求进入 DispatcherServlet 到 handler adapter / argument resolver / return value handler 的主线
- [-] 4.0.2 能稳定复现并断言：参数绑定失败/校验失败的响应结构与 resolvedException
- [-] 4.0.3 能稳定复现并断言：异常解析链路（@ExceptionHandler/@ControllerAdvice/默认 resolver 的顺序与匹配）
- [-] 4.0.4 能稳定复现并断言：开启 Security 后 401/403 的处理链路在 MVC 之前发生（以及与 MVC 异常处理的边界）
- [-] 4.0.5 pitfalls 至少 15 条；self-check 至少 20 题

### 4.1 场景 S1：参数绑定/类型转换/校验（400 从哪里来）

#### 4.1.1 文档（把绑定写成“链路 + 分支”）
- [-] 4.1.1.1 更新/新增参数绑定章节：覆盖 `@RequestParam/@PathVariable/@RequestBody/@ModelAttribute` 的差异
- [-] 4.1.1.2 增加“类型转换/格式化/校验”的主线解释：
  - Converter/Formatter 介入点
  - Validator 介入点
  - 失败后错误对象如何形成（BindingResult/FieldError）
- [-] 4.1.1.3 给出推荐断点：HandlerMethodArgumentResolverComposite / WebDataBinder / InvocableHandlerMethod
- [-] 4.1.1.4 每章末尾补齐：对应 Lab/Test + 推荐观察点（resolvedException、响应体结构、错误字段）

#### 4.1.2 Labs（把绑定分支钉死）
- [-] 4.1.2.1 新增 `BootWebMvcArgumentBindingAndValidationLabTest`（建议放 `part01_web_mvc` 或 `part03_internals`）
- [-] 4.1.2.2 用例：缺少 request param → 400（断言 resolvedException 类型）
- [-] 4.1.2.3 用例：类型转换失败（如 string→int）→ 400（断言 resolvedException）
- [-] 4.1.2.4 用例：`@Valid` 校验失败 → 400（断言错误字段与 message）
- [-] 4.1.2.5 用例：`@RequestBody` JSON 解析失败 → 400（断言 HttpMessageNotReadableException）

#### 4.1.3 Exercises（把“看懂 400”变成练习）
- [-] 4.1.3.1 新增 `*ExerciseTest`：给出一个绑定/校验失败的 case，让学员写出正确的 controller method 签名与校验注解
- [-] 4.1.3.2 新增 `*ExerciseTest`：给出一个“错误响应结构不符合约定”的 case，让学员补齐 `@ControllerAdvice`

### 4.2 场景 S2：异常处理链路（Resolver/Advice/默认处理）

#### 4.2.1 文档（把 resolver 顺序写清楚）
- [√] 4.2.1.1 更新 `docs/web-mvc/springboot-web-mvc/part-01-web-mvc/02-exception-handling.md`：补齐“异常解析链路概览 + 验证入口”
- [√] 4.2.1.2 更新 `docs/web-mvc/springboot-web-mvc/part-03-web-mvc-internals/04-exception-resolvers-and-error-flow.md`：
  - ExceptionHandlerExceptionResolver / ResponseStatusExceptionResolver / DefaultHandlerExceptionResolver 顺序
  - @ControllerAdvice 匹配规则
  - 与 Boot error controller 的交界
- [√] 4.2.1.3 增加“如何用 resolvedException 快速定位”的排障小节（绑定测试入口）

#### 4.2.2 Labs（把 resolver 链路钉死）
- [√] 4.2.2.1 新增/强化 `BootWebMvcExceptionResolverChainLabTest`
- [√] 4.2.2.2 用例：绑定/校验类异常（BindException/MethodArgumentNotValidException）→ resolvedException
- [√] 4.2.2.3 用例：消息转换异常（HttpMessageNotReadableException）→ resolvedException
- [-] 4.2.2.4 用例：媒体类型异常（415/406）→ resolvedException / handler
- [-] 4.2.2.5 用例：业务异常由 @ExceptionHandler 处理（对照“框架异常 vs 业务异常”）

### 4.3 场景 S3：与 Security 交互（401/403 谁处理）

#### 4.3.1 文档（把 FilterChain 与 MVC 边界写清楚）
- [-] 4.3.1.1 在 Web MVC 文档增加“请求链路总览图”：
  - Security FilterChain（FilterChainProxy）→ DispatcherServlet → Controller
  - 401/403 的典型入口（AuthenticationEntryPoint/AccessDeniedHandler）
- [-] 4.3.1.2 增加“为什么 @ControllerAdvice 处理不了某些 401/403”的解释（发生在 MVC 之前）
- [-] 4.3.1.3 给出推荐断点：FilterChainProxy / ExceptionTranslationFilter

#### 4.3.2 Labs（把边界钉死）
- [-] 4.3.2.1 新增 `BootWebMvcSecurityFilterChainInteractionLabTest`（建议放 `part08_security_observability`）
- [-] 4.3.2.2 用例：未认证访问受保护资源 → 401（断言响应码与入口）
- [-] 4.3.2.3 用例：已认证但无权限 → 403（断言响应码与入口）
- [-] 4.3.2.4 用例：开启/关闭某个 security 配置分支 → 响应差异稳定可复现（形成最小对照）
- [-] 4.3.2.5 用例：同一异常在“FilterChain 阶段” vs “Controller 阶段”抛出时的处理差异

### 4.4 Web MVC 自检与排障
- [-] 4.4.1 重构 `docs/web-mvc/springboot-web-mvc/appendix/90-common-pitfalls.md`（统一模板，至少 15 条）
- [-] 4.4.2 更新 `docs/web-mvc/springboot-web-mvc/appendix/99-self-check.md`（至少 20 题）
- [√] 4.4.3 更新 `helloagents/wiki/modules/springboot-web-mvc.md`

### 4.5 Web MVC 阶段性回归
- [√] 4.5.1 运行 Web MVC 模块测试：`bash scripts/test-module.sh springboot-web-mvc`

---

## 5. 跨模块收尾主线（可选但强烈推荐）：从一次 Web 请求串起 AOP/Tx（R5）

> 目标：作为“收尾大作业”，把真实排障路线固化：MVC → Service（AOP/Tx）→（可选）数据访问。  
> 注意：不追求“最小闭环”，追求“可断点、可复述、可验证”的路线图。

### 5.1 设计一个可复用的 End-to-End 场景（选题）
- [-] 5.1.1 选择 1 个业务主线：鉴权 → 参数校验 → 业务执行（AOP/Tx）→ 结果返回
- [-] 5.1.2 定义 3 类失败分支：绑定/校验失败、权限失败、事务回滚失败（用于排障）
- [-] 5.1.3 明确每类分支的“证据链断言点”（响应码/响应体/数据库结果/InvocationLog）

### 5.2 文档：跨模块 Debug Route Map
- [-] 5.2.1 新增文档：End-to-End Debug Route（列出每一跳的断点与观察点）
- [-] 5.2.2 文档必须绑定：对应 e2e 测试入口（MockMvc 或等价）

### 5.3 Labs：端到端可验证入口
- [-] 5.3.1 新增 `*LabTest`：覆盖主线 + 至少 2 个失败分支
- [-] 5.3.2 对每条链路补齐推荐断点：FilterChainProxy/DispatcherServlet/ReflectiveMethodInvocation/TransactionInterceptor

---

### 6.1 Security Check（必须）
- [√] 6.1.1 确认无明文敏感信息、无外部网络依赖、无生产环境操作（按 G9）

### 6.2 回归与一致性审计
- [√] 6.2.1 跑全量测试：`bash scripts/test-all.sh`
- [-] 6.2.3 README 语言与链接自检：所有 README 中文、链接可达、索引可导航

### 6.3 知识库同步（SSOT）
- [√] 6.3.1 更新 `helloagents/wiki/modules/*`：同步 Docs/Labs/排障索引
- [√] 6.3.2 更新 `helloagents/CHANGELOG.md`：按阶段记录新增 Labs、关键文档变更、排障清单新增

### 6.4 方案包生命周期（仅当执行完某一阶段时）
- [√] 6.4.1 更新本方案包 `task.md` 状态（勾选完成项、补充 Note）
- [√] 6.4.2 迁移方案包到 `helloagents/history/YYYY-MM/202601091043_deepen_beans_aop_tx_web/`
- [√] 6.4.3 更新 `helloagents/history/index.md` 增加索引条目
