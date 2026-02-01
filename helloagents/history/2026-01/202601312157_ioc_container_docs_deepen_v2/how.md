# Technical Design: part-01-ioc-container 文档深度完善（细化版 v2）

## Technical Solution

### Core Technologies

- **载体：** Markdown（必要时使用 Mermaid 图帮助表达时序/分支）
- **可复现实验：** 优先引用现有 `*LabTest.java`（`spring-core-modules/spring-core-beans/src/test/java/.../part01_ioc_container/`）
- **理论锚点：** Spring Framework IoC 容器主线（`refresh` → `getBean` → `doCreateBean`）、以及常见扩展点（BFPP/BDRPP/BPP）

### Implementation Key Points

#### 0. 总体策略（不设“固定补充模块”，按章节画像驱动）

对每个章节先完成一次“章节画像”梳理（读者目标、现有覆盖、断档点），再决定补充方向。补充内容优先围绕：

1. **关键分叉点**：本章最容易产生歧义/误解的决策分支（例如候选选择、scope 边界、BPP 时序）
2. **边界条件**：在哪些情况下规则成立/不成立（例如循环依赖的可解边界）
3. **可验证抓手**：提供能被断点/日志验证的最小复现（优先复用现有 Lab）
4. **跨章链接点**：仅补“最短必需链接”，避免读者在大量链接中迷失

#### 0.1 建议执行节奏（先统一闭环，再做增量增强）

1. **先盘点再动笔：** 对 9 篇文档做一次“章节画像盘点”（读者目标/关键分叉点/现有覆盖/缺口类型/可复现抓手），形成缺口清单。
2. **按章节批次推进：** 建议按“容器主线顺序”推进：心智模型 → 注册 → 依赖解析 → scope → 生命周期 → 后置处理器 → 配置类增强 → FactoryBean → 循环依赖。
3. **每章完成即自检：** 每章更新后立即做一次“链接/锚点 + TODO 占位 + Lab 引用存在性”自检，避免后期集中返工。
4. **最后做全局一致性回收：** 最后统一做跨章跳转梳理与风格一致性回收（避免边写边追风格导致反复）。

#### 0.2 章节画像驱动：按缺口补强（不做固定格式）

本方案不要求每章都长成同一个“模板”，而是先做章节画像，再按缺口选择补强动作。建议的工作方式是：

- 先用 1 句话写清“本章解决什么”，以及读者读完应该能做出什么判断/解释（本章的最小产出）。
- 再去对照本章现有内容，找出最薄弱的那一段（常见在：关键分叉点没讲清 / 边界条件没写全 / 无法复现验证 / 排错路径断档）。
- 按缺口选择补强（只补必要项，不追求“全量补齐”）：
  - **缺源码主线**：补一条“入口 → 关键分支 → 结果”的方法级证据链，并给出对应断点入口；断点数量以覆盖关键分支为准。
  - **缺可复现抓手**：优先绑定到现有 `*LabTest`，补上“跑什么/看什么/如何验证结论”的最短说明；必要时再补一个对照实验覆盖边界。
  - **缺排错闭环**：补一个“现象 → 分层 → 证据 → 修复”的最短诊断路径（或小型决策表），把主观判断转为可验证步骤。
  - **缺跨章链接**：只补 1-2 个“下一跳建议”（按最短路径把读者送到下一章，不堆链接清单）。
  - **有占位**：优先清理 “未完/TODO/FIXME” 占位；要么补齐为可验证解释，要么删去避免读者断档。

#### 0.3 Lab 引用原则（按需要选）

- **主 Lab：** 优先选 1 个能覆盖本章主线的 Lab/Test（用于读者“跑起来 + 打断点 + 看证据链”）。
- **辅助 Lab：** 如本章存在明显边界/误区，再补 1-2 个用于对照（例如 raw vs proxy、early reference、ordering、scope 边界）。
- **写法建议：** 文档中引用 Lab 时，尽量同时给出“类名 + 路径”，降低读者检索成本。

#### 0.4 本方案引用的 Lab/Test 路径速查（按章节主题）

- **part01_ioc_container（本章主线入口类居多）：**
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanFactoryVsApplicationContextLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansIocBranchMatrixLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansComponentScanLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanDefinitionRegistrationDiffLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportExerciseTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportExerciseSolutionTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansDependencyDescriptorMetadataLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansLifecycleRawVsProxyLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansEarlyGetBeanMissesBppLabTest.java`
- **part03_container_internals（容器内部关键分支/窗口期）：**
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansAwareInfrastructureLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansCircularDependencyBoundaryLabTest.java`
- **part04_wiring_and_boundaries（注入/Scope/FactoryBean 的工程边界更集中）：**
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`

---

## Chapter Deepening Strategies

> 说明：下列策略的“新增内容位置/标题命名/是否加图”以每章既有结构为准，不强制统一模板；原则是“补强关键缺口但不推倒重写”。

### 1) 020-01-bean-mental-model.md

**补强目标：** 把 BeanDefinition/BeanFactory/ApplicationContext/扩展点/缓存与主线统一到一个可稳定复用的心智模型里，读者读完能解释“为什么下一章要讲这个”。

**具体策略：**
- 把“主线时序”拆成 2 条并行视角：`refresh`（容器启动）与 `getBean`（按需创建），并明确两者的交汇点（例如 preInstantiateSingletons）。
- 增加“扩展点插槽地图”：把 BDRPP/BFPP/BPP/InstantiationAware* 放到主线节点上（用简图/列表即可），强调“介入点不同 → 影响范围不同”。
- 增加“异常分类与定位路径”：把 NoSuch/NoUnique/CreationException 等归类到“注册阶段/解析阶段/创建阶段”，给出读者下一步应该看的章节或 Lab。
- 复用现有 Lab 作为“可验证抓手”，建议在文末增加“动手验证”小节并链接：
  - `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`
  - `SpringCoreBeansContainerLabTest`
  - `SpringCoreBeansBeanGraphDebugLabTest`
  - `SpringCoreBeansIocBranchMatrixLabTest`

**建议新增小节（按本章现有目录择优落位）：**
- 「容器世界观三件套」：BeanDefinition（描述）/BeanFactory（运行时）/ApplicationContext（编排与基础设施）。
- 「两条主线：启动 vs 按需」：refresh 为什么是“装配舞台”，getBean 为什么是“触发动作”。
- 「把扩展点放回主线」：用“插槽”而不是“清单”描述扩展点的影响范围（修改定义 / 参与创建 / 包装代理）。
- 「遇到异常先问三件事」：找不到、找到了太多、创建失败分别意味着什么（以及下一步断点该落哪里）。

**断点与观察点（建议 Debugger）：**
- `AbstractApplicationContext#refresh`：总入口（建议配合 call stack 观察 refresh 的阶段拆分）。
- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：BDRPP/BFPP 主战场。
- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：BPP 注册时点与排序。
- `DefaultListableBeanFactory#preInstantiateSingletons`：单例预实例化与“为什么启动期会创建 Bean”。
- `AbstractBeanFactory#doGetBean` / `AbstractAutowireCapableBeanFactory#doCreateBean`：按需创建主线与创建阶段边界。

**可运行验证（优先复用现有 Lab）：**
- `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`：对比容器能力边界与“编排能力”差异。
- `SpringCoreBeansBeanGraphDebugLabTest`：把“依赖图”与创建顺序落到断点与对象关系上。

---

### 2) 02-bean-registration.md

**补强目标：** 让读者把所有注册入口统一映射到 BeanDefinitionRegistry，并能解释“注册时点”为什么会影响后续行为（尤其是后置处理器与配置类增强）。

**具体策略：**
- 增加“入口 → 载体 → 落点”的映射表（例如：@ComponentScan → 扫描 → BeanDefinition；@Import → ImportSelector/Registrar → BeanDefinitionRegistry；@Bean → ConfigurationClassPostProcessor → BeanDefinition）。
- 补强“同名覆盖/重复注册/覆盖策略”与调试点：读者能从异常或日志定位到“是谁注册的/何时注册的/为什么覆盖”。
- 增加“与后置处理器的交叉点”说明：哪些注册机制依赖 BFPP/BDRPP 的早期执行（例如配置类解析、Import 解析）。
- 复用/增强现有 Lab 作为章节的“最小复现”：
  - `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest`
  - `SpringCoreBeansImportLabTest`
  - `SpringCoreBeansImportExerciseTest` + `SpringCoreBeansImportExerciseSolutionTest`
  - `SpringCoreBeansComponentScanLabTest`（并关联 `componentscan/*` 作为素材）

**建议新增小节（按本章现有目录择优落位）：**
- 「注册入口全景图」：把 XML/@ComponentScan/@Bean/@Import/编程式注册归为“扫描/解析/显式注册”三类入口。
- 「注册发生的时点」：注册（Definition）与创建（Instance）之间的关系，哪些入口发生在 BFPP/BDRPP 之后会产生差异。
- 「同名冲突与覆盖」：覆盖并不总是坏事，但必须可解释（谁赢、为什么、如何显式化）。
- 「如何回答：这个 Bean 到底从哪里来」：给读者一个最短排查路径（从 BeanDefinitionRegistry 反查入口）。

**断点与观察点（建议 Debugger）：**
- `DefaultListableBeanFactory#registerBeanDefinition`：最终落点（谁把定义塞进来了）。
- `ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry`：@Configuration/@Bean/@Import 的核心入口。
- `ClassPathBeanDefinitionScanner#doScan`：@ComponentScan 的扫描入口。
- `ImportSelector#selectImports` / `ImportBeanDefinitionRegistrar#registerBeanDefinitions`：@Import 的两类分支。

**可运行验证（优先复用现有 Lab）：**
- `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest`：对照不同入口的 BeanDefinition 差异（属性、来源、角色）。
- `SpringCoreBeansImportExerciseTest`：把 @Import 的分支从“会用”提升到“能解释”。

---

### 3) 014-03-dependency-injection-resolution.md

**补强目标：** 把依赖解析从“规则列表”提升为“可调试的决策树”，读者能从注入点出发推导候选集合与过滤路径。

**具体策略：**
- 增加“注入点元数据画像”：讲清 DependencyDescriptor 的关键字段如何影响候选筛选（required、eager、泛型/ResolvableType、字段名/参数名）。
- 把候选选择拆成 3 层：候选收集（findAutowireCandidates）→ 候选过滤（Qualifier/Primary/优先级/默认候选）→ 结果装配（单值/集合/Map/Optional/Provider 等）。
- 增强“歧义场景”的对照：同类型多 Bean 时，分别用 @Primary/@Qualifier/名称匹配/ObjectProvider 延迟获取解决，强调代价与可维护性。
- 加一段“为什么 FactoryBean 会影响按类型注入”的解释：`getObjectType`、`&` 前缀以及产物类型的参与方式。
- 复用现有 Lab，作为读者断点入口：
  - `SpringCoreBeansDependencyDescriptorMetadataLabTest`

**建议新增小节（按本章现有目录择优落位）：**
- 「先画注入点，再谈候选」：字段/参数的名字、类型、泛型、注解共同决定候选筛选路径。
- 「候选集合如何来」：解释 findAutowireCandidates 的“收集范围”（包括父容器/别名/FactoryBean 产物类型参与的边界）。
- 「歧义如何收敛」：按“更强约束优先”的顺序讲：@Qualifier → @Primary → 优先级 → 名称匹配 → 默认候选。
- 「集合/Map/Optional/Provider 的语义差异」：不是语法糖，而是“解析时机/创建时机/异常语义”的差异。

**断点与观察点（建议 Debugger）：**
- `DefaultListableBeanFactory#resolveDependency` / `#doResolveDependency`：决策树入口（最核心断点）。
- `DefaultListableBeanFactory#findAutowireCandidates`：候选集合如何收集（非常适合配合 Watch 观察候选列表变化）。
- `DefaultListableBeanFactory#determineAutowireCandidate`：歧义收敛（@Primary/@Qualifier/名称/优先级的落点）。
- `QualifierAnnotationAutowireCandidateResolver`：Qualifier/Value 等注解如何参与匹配。

**可运行验证（优先复用现有 Lab）：**
- `SpringCoreBeansDependencyDescriptorMetadataLabTest`：从注入点元数据出发，观察解析分支如何变化。
- （推荐补充对照）`SpringCoreBeansAutowireCandidateSelectionLabTest`：更聚焦“候选收敛/歧义消解”的分支对照，适合作为本章的辅助 Lab。

---

### 4) 015-04-scope-and-prototype.md

**补强目标：** 把 scope 讲成“语义边界”，而不是“注解用法”；重点让读者理解 prototype 的“创建/注入/销毁边界”。

**具体策略：**
- 增加“prototype 注入 singleton”的三种常见写法对比：直接注入（一次性）vs ObjectProvider/Provider（按需获取）vs @Lookup（方法注入），并解释各自适用边界。
- 补强 scoped proxy 的本质：代理对象稳定、目标对象按 scope 变化；说明 `scopedTarget.*` 的命名与容器内部表现。
- 强化“销毁回调边界”：prototype 的 destroy 为什么不由容器统一回收，何时需要自管资源（并给出推荐写法）。
- 增加与后续章节的链接点：该章结尾建议显式指向
  - `016-05-lifecycle-and-callbacks.md`（解释 destroy 边界与生命周期插槽）
  - `09-circular-dependencies.md`（scope 与循环依赖/缓存交互的边界）
  - `part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md`（如果需要进一步深入）
- 复用现有 Lab/Test 作为“可验证抓手”（避免新增实验成本）：
  - 主 Lab：`SpringCoreBeansContainerLabTest`（scope 的基本行为与容器主线观察入口）
  - 辅助 Lab：`SpringCoreBeansCustomScopeLabTest`（prototype 注入 singleton / ObjectProvider / scoped proxy / destruction callback 的工程边界）

**建议新增小节（按本章现有目录择优落位）：**
- 「prototype 的 3 个边界」：创建边界（每次 getBean）/注入边界（一次装配）/销毁边界（容器不托管）。
- 「为什么 Provider 才是 ‘按需’」：解释 Provider/ObjectProvider 延迟获取的语义与误用点。
- 「scoped proxy 的可见证据」：scopedTarget 命名、代理类型、以及“看起来是同一个对象”的原因。

**断点与观察点（建议 Debugger）：**
- `AbstractBeanFactory#doGetBean`：观察 prototype 每次请求如何触发创建，以及 singleton 如何走缓存。
- `DefaultSingletonBeanRegistry#getSingleton`：singleton 缓存命中与创建分支。
- `ScopedProxyUtils#createScopedProxy`（或相关 scoped proxy 生成点）：观察 scopedTarget 命名与代理注册。

---

### 5) 016-05-lifecycle-and-callbacks.md

**补强目标：** 让读者把生命周期回调“装入顺序模型”，并能解释代理/后置处理器介入后为什么会出现顺序差异。

**具体策略：**
- 补强“顺序总览图”：实例化 → 属性填充 → aware → BPP before-init → init 回调 → BPP after-init → ready（并标注 prototype/singleton 的差异点）。
- 增加“同一能力多入口”的对照：@PostConstruct vs InitializingBean vs init-method；@PreDestroy vs DisposableBean vs destroy-method，说明冲突与取舍。
- 增强“代理对生命周期的影响”：说明 raw bean vs proxy bean 在回调/依赖注入时机上的差异，以及为什么会出现“看起来没走回调”的错觉。
- 复用现有 Lab 作为验证入口：
  - `SpringCoreBeansLifecycleRawVsProxyLabTest`
  - （推荐主线入口）`SpringCoreBeansAwareInfrastructureLabTest`：更贴近“是谁触发/在何时触发”的基础设施视角，适合作为本章主 Lab。

**建议新增小节（按本章现有目录择优落位）：**
- 「生命周期顺序的最小主线」：用最短主线覆盖 80% 场景（先把读者从细节淹没中救出来）。
- 「同一回调的多入口对照」：给出选择建议（偏框架/偏业务/偏可测性）。
- 「代理介入导致的两类差异」：创建顺序差异（pre-instantiation）与注入对象差异（raw vs proxy）。

**断点与观察点（建议 Debugger）：**
- `AbstractAutowireCapableBeanFactory#populateBean`：属性填充（依赖注入发生的关键阶段）。
- `AbstractAutowireCapableBeanFactory#initializeBean`：aware + init + BPP 的汇合点。
- `CommonAnnotationBeanPostProcessor#postProcessBeforeInitialization`：@PostConstruct 的落点。
- `DisposableBeanAdapter`（销毁回调聚合）：@PreDestroy/destroy-method 的落点。

---

### 6) 017-06-post-processors.md

**补强目标：** 把后置处理器讲成“容器可编程能力的核心”，并补齐未完点，让读者能解释 BFPP/BDRPP/BPP 的时序、排序、以及错过的原因。

**具体策略：**
- 明确分类与时序：BDRPP（注册 BeanDefinition）→ BFPP（修改 BeanFactory/BeanDefinition）→ BPP（参与 bean 创建），并用 refresh 主线节点标注调用点。
- 补齐“排序规则的真实落点”：PriorityOrdered/Ordered/@Order 的作用域分别影响谁（注册顺序 vs 执行顺序）。
- 增加“错过后置处理器”的最小复现：强调“过早 getBean”可能导致 BPP 未参与创建，给出读者可验证路径。
- **清理并补齐未完标记对应的小节**，把占位内容替换为可落地的解释/示例。
- 复用现有 Lab 作为验证入口：
  - `SpringCoreBeansEarlyGetBeanMissesBppLabTest`

**建议新增小节（按本章现有目录择优落位）：**
- 「三类 PostProcessor 的职责边界」：用“能改什么”来分类，而不是仅靠接口名背诵。
- 「排序与注册不是一回事」：读者常把“谁先注册”与“谁先执行”混为一谈，本章要拆开讲。
- 「为什么会 ‘没生效’」：给出最短诊断路径：是否注册成功 → 排序是否如预期 → 是否错过创建阶段（早期 getBean）。

**断点与观察点（建议 Debugger）：**
- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：BDRPP/BFPP 执行主线。
- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：BPP 注册与排序。
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization` / `#applyBeanPostProcessorsAfterInitialization`：BPP 介入的插槽。
- `BeanPostProcessorChecker`（如项目中存在相关日志/行为）：识别“Bean 创建过早”。

---

### 7) 018-07-configuration-enhancement.md

**补强目标：** 让读者理解 @Configuration 增强不是“魔法”，而是为保证 @Bean 方法语义而做的代理；同时讲清 `proxyBeanMethods` 的权衡。

**具体策略：**
- 补强 full/lite 模式对比：哪些注解/条件会触发增强，哪些不会；说明“lite 配置类”常见误区。
- 增加 `proxyBeanMethods=true/false` 的选择指南：何时必须保持方法拦截语义、何时可以换性能（并给出替代写法，例如参数注入替代跨方法调用）。
- 增加与循环依赖的关联说明：配置类增强与 early reference/代理对象的关系，提示读者遇到循环依赖时要关注的额外变量。
- 补充“断点建议”而不是大量源码粘贴：例如增强发生在何处、如何观察增强后的类名/方法拦截。

**建议新增小节（按本章现有目录择优落位）：**
- 「full vs lite：差别不在 ‘能不能用’ 而在 ‘语义保证’」：强调 full 模式保护 @Bean 方法语义。
- 「proxyBeanMethods 的取舍」：用“语义风险清单”辅助决策（跨 @Bean 方法调用、手动 new、循环依赖敏感场景）。
- 「如何识别是否被增强」：从类名（CGLIB Enhancer）与调试视角给出可见证据。

**断点与观察点（建议 Debugger）：**
- `ConfigurationClassPostProcessor`（增强触发入口）与 `ConfigurationClassEnhancer`（增强实现落点）。
- 观察增强后类名：常见会出现 `$$EnhancerBySpringCGLIB$$` 之类后缀（用于读者自检是否进入 full 模式）。

---

### 8) 08-factorybean.md

**补强目标：** 把 FactoryBean 从“用法”提升到“容器语义”：产物缓存、类型推断、提前初始化、与循环依赖/AOP 的交互边界。

**具体策略：**
- 增加“FactoryBean vs 普通 Bean”的对照：容器拿到的是“工厂”还是“产物”，`&` 前缀在调试时怎么用。
- 补强 `isSingleton/getObjectType` 的关键语义：它们如何影响按类型查找、依赖解析与预实例化。
- 增强“边界案例”说明：FactoryBean 产物本身也可能被代理/参与循环依赖；什么时候会触发过早初始化。
- 复用现有 Lab 作为验证入口：
  - 主 Lab：`SpringCoreBeansFactoryBeanDeepDiveLabTest`（FactoryBean 核心语义：工厂/产物、缓存、类型推断）
  - 辅助 Lab：`SpringCoreBeansFactoryBeanEdgeCasesLabTest`（边界案例：提前初始化/代理/与其它机制交叉）

**建议新增小节（按本章现有目录择优落位）：**
- 「两套身份：FactoryBean 与它的产物」：强调名称空间（`&name`）与类型空间（getObjectType）的双重维度。
- 「产物缓存不是可选细节」：解释 singleton 产物缓存如何影响行为与排错（尤其是“看起来不是新对象”的误解）。
- 「FactoryBean 与依赖解析的交叉」：为什么它会改变按类型查找与注入候选。

**断点与观察点（建议 Debugger）：**
- `AbstractBeanFactory#doGetBean`：观察 `&` 前缀分支与正常分支的差异。
- `FactoryBeanRegistrySupport`：观察“产物对象”的创建与缓存。
- 观察 `FactoryBean#getObjectType` 何时被调用，以及返回 null 时对类型匹配的影响。

---

### 9) 09-circular-dependencies.md

**补强目标：** 让读者清晰掌握“可解/不可解”的边界，并能在真实项目中给出可落地的解环方案与权衡。

**具体策略：**
- 用“边界矩阵”方式重述循环依赖：singleton setter 可解、constructor 不可解、prototype 通常不可解、AOP 早期引用会引入额外复杂度。
- 深化三级缓存不是“背概念”：把 singletonObjects/earlySingletonObjects/singletonFactories 的角色放进 doCreateBean 主线，并解释 getEarlyBeanReference 的关键意义。
- 补强配置开关的真实含义与风险：
  - `allowCircularReferences`（Spring Framework）
  - `allowRawInjectionDespiteWrapping`
  - `spring.main.allow-circular-references`（Spring Boot）
- **清理并补齐未完标记对应的小节**，把占位内容替换为“可操作”的解法集合：重构依赖方向、引入 Provider/@Lazy、拆分职责、事件/回调等替代方案，并说明取舍。
- 复用现有 Lab 作为验证入口：
  - `SpringCoreBeansCircularDependencyBoundaryLabTest`
  - （必要时）联动 `SpringCoreBeansLifecycleRawVsProxyLabTest` 解释 raw/proxy 注入差异

**建议新增小节（按本章现有目录择优落位）：**
- 「先画边界，再谈实现」：用“注入方式 × scope × 代理介入”组合快速判断可解性（避免把三级缓存当万能钥匙）。
- 「三级缓存与 early reference 的最短解释」：每个缓存只回答一个问题（最终单例 / 早期引用 / 早期引用工厂），并回到 doCreateBean 主线。
- 「开关不是解法」：把三个开关讲成“风险开关”，明确它们解决的是“启动成功”还是“语义正确”。
- 「可落地解环方案清单」：给出 2-3 条常见改造路径，并补上适用边界（例如 Provider/@Lazy 只改变获取时机，不改变设计耦合本质）。

**断点与观察点（建议 Debugger）：**
- `AbstractAutowireCapableBeanFactory#doCreateBean`：早期暴露发生点（何时把 singletonFactories 塞入）。
- `DefaultSingletonBeanRegistry#getSingleton`（含 ObjectFactory 分支）：三级缓存命中/回退的关键落点。
- `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`：AOP 等场景如何参与早期引用（理解 raw vs proxy 的根源）。

**可运行验证（优先复用现有 Lab）：**
- `SpringCoreBeansCircularDependencyBoundaryLabTest`：边界矩阵的最小复现入口（建议读者逐格切换场景并观察缓存命中与异常类型）。

---

## Security and Performance

- **Security:** 文档补充需避免引入任何敏感信息（token/密钥/内网地址）；引用外部链接时以官方文档与源码仓库为主。
- **Performance:** 主要关注读者的“认知性能”——避免无边界扩写；以“最小可验证链路”替代大段源码搬运。

## Testing and Deployment

- **Testing:** 以“链接/锚点自检 + Lab 可运行”为主（详见 task.md）。  
- **Deployment:** 无部署变更；仅文档更新。

---

## 段落级深化清单（可直接写入章节）

> 说明：
> 1) 下面按章节给出“建议新增段落”的标题与内容要点，写作时可根据章节现有结构把段落放到最自然的位置（概念引入/源码主线/实验验证/排错闭环/小结）。
> 2) 不追求统一模板；每章只补该章最需要的段落，避免无边界扩写。
> 3) 每个“段落”都以“读者能用断点/日志验证”为落点，优先复用现有 `*LabTest.java`。

### A) 020-01-bean-mental-model.md（段落级建议）

落位提示（以当前文档骨架为准）：
- `## 导读`：容器世界观三件套 / 两条主线与交汇点（帮助读者快速建立“本章在主线的坐标”）。
- `## 机制主线` / `## 2. 方法级主线`：扩展点插槽思维（告诉读者“不同插槽能改什么”）。
- `## 可复现闭环`：将断点入口与主 Lab 绑定（让读者能跑起来）。
- `## 4. 排障决策表`：异常分类与最短定位路径（让读者能把现象归因到主线阶段）。
- `## 小结与下一章`：给出跨章“下一跳”建议（注册/依赖解析/循环依赖等）。

1. 段落：容器世界观三件套（描述/运行时/编排）
   - 解释 BeanDefinition 是“描述”，BeanFactory 是“运行时容器”，ApplicationContext 是“编排 + 基础设施整合”。
   - 明确常见误解：ApplicationContext 不是“另一个容器”，而是对 BeanFactory 的组织与增强。

2. 段落：两条主线与交汇点（refresh vs getBean）
   - refresh 更像“搭舞台”（注册定义、注册扩展点、准备创建）；getBean 更像“触发动作”（命中缓存或走创建主线）。
   - 指出两者的交汇：`preInstantiateSingletons` 让“启动期也会创建 Bean”。

3. 段落：把扩展点放回主线（插槽思维）
   - 以插槽描述：BDRPP/BFPP 改定义；BPP 参与创建并可能包装代理；InstantiationAware* 能短路实例化或提供早期引用。
   - 强调“插槽不同 → 能改的东西不同 → 排错路径不同”。

4. 段落：异常分类与最短定位路径
   - NoSuch/NoUnique/CreationException 三类典型异常分别指向：注册缺失/候选歧义/创建失败。
   - 给出“下一跳”：读者应该去依赖解析/后置处理器/循环依赖等章节或对应 Lab 验证。

5. 段落：动手验证（推荐 Lab + 断点入口）
   - 将本章的断点入口与 Lab 显式绑定，给读者“跑起来 → 打断点 → 看对象/缓存/调用栈”的最短路径。

### B) 02-bean-registration.md（段落级建议）

落位提示（以当前文档骨架为准）：
- `## 导读` / `## 章节验收口径`：给出“入口全景图 + 本章闭环口径”（读者读完能解释注册链路）。
- `## 2. 四类常见注册入口`：入口分类与映射表（入口 → 载体 → 落点）。
- `## 3. 注册发生在 refresh 的哪一段？`：强调“时点决定能力”，并链接到后置处理器/配置类增强章节。
- `## 4. 断点闭环`：把 `registerBeanDefinition` / `ConfigurationClassPostProcessor` / scanner 的断点与观察目标写清楚。
- `## 5. 排障决策表`：从“扫不到/被覆盖/定义有了但注入失败”回到证据链。

1. 段落：注册入口全景图（分类而不是罗列）
   - 将入口按“扫描/解析/显式注册”归类：@ComponentScan、@Import、@Bean、XML、编程式注册。
   - 强调它们最终都落到 BeanDefinitionRegistry，但中间链路与时点不同。

2. 段落：入口 → 载体 → 落点（映射表）
   - 用一张映射表把每种入口的“处理器/载体/落点方法”串起来（如 scanner、ConfigurationClassPostProcessor、ImportSelector/Registrar）。
   - 强调：本章重点是“路径可解释”，不是“用法百科”。

3. 段落：注册时点为什么重要（定义阶段 vs 创建阶段）
   - 说明：定义注册发生在 refresh 早期，实例创建发生在后续；如果定义注册太晚，会影响解析/增强/条件判断。
   - 把“时点差异”与后续章节（后置处理器/配置类增强）建立最短链接。

4. 段落：同名冲突/覆盖不是 bug，但必须可解释
   - 引导读者回答三问：谁注册的？什么时候注册的？为什么能覆盖/不能覆盖？
   - 提供调试抓手：从 `registerBeanDefinition` 断点回溯调用栈锁定入口。

5. 段落：动手验证（最小复现路径）
   - 给出一条“对照实验”路径：同一 Bean 用两种入口注册，比较 BeanDefinition 差异并解释来源字段。

### C) 014-03-dependency-injection-resolution.md（段落级建议）

落位提示（以当前文档骨架为准）：
- `## 导读` / `## 机制主线`：先给“可调试决策树”的总览（避免读者迷失在规则列表）。
- `## 2. 候选收集（collect）`：强调收集范围与边界（父容器/别名/FactoryBean 产物类型）。
- `## 3. 候选收敛（narrow down）`：把 @Qualifier/@Primary/名称/优先级写成“候选集合逐步缩小”的证据链。
- `## 4. 可选依赖与延迟解析`：解释 Optional/ObjectProvider 的语义差异。
- `## 6. 调试闭环` / `## 源码与断点`：给出断点观察点（resolveDependency/findAutowireCandidates/...）与预期现象。

1. 段落：先画注入点画像，再谈候选集合
   - 注入点由“类型 + 泛型 + 注解 + 名称（字段名/参数名）”共同定义，DependencyDescriptor 承载这些信息。
   - 点明：很多“注入不符合预期”的根源是注入点画像没画对。

2. 段落：候选集合如何来（收集范围与边界）
   - 解释候选收集的范围：当前容器/父容器、别名参与、FactoryBean 产物类型参与的边界。
   - 提醒读者：候选集合不是“全量 Bean”，而是“能作为候选的 Bean”。

3. 段落：歧义如何收敛（决策树而非规则列表）
   - 给出可调试的决策树：@Qualifier → @Primary → 优先级/默认候选 → 名称匹配 → 最终失败分支。
   - 强调“每一步都能用断点看到候选集合被缩小”。

4. 段落：集合/Map/Optional/Provider 的语义差异
   - 解释差异维度：解析时机、创建时机、异常语义。
   - 提示读者：Provider/ObjectProvider 不是“更高级”，而是“延迟获取”的语义选择。

5. 段落：FactoryBean 为什么会影响按类型注入
   - 解释 `getObjectType` 对类型匹配的影响，以及 `&` 前缀如何取到“工厂本身”。

6. 段落：动手验证（以断点驱动理解）
   - 给出 `resolveDependency/findAutowireCandidates/determineAutowireCandidate` 三个断点的观察目标（候选列表如何变化、为何选中某个 Bean）。

### D) 015-04-scope-and-prototype.md（段落级建议）

落位提示（以当前文档骨架为准）：
- `## 导读` / `## 机制主线`：把 scope 定义为“缓存/注入/生命周期边界”的组合语义，而不是注解用法。
- `## 2.1 prototype 的关键边界` / `## 3. 为什么...`：用“创建/注入/销毁”三条边界解释错觉来源。
- `## 4/5/6 解决方案`：用对照表给出选择建议（直接注入 vs Provider vs @Lookup vs scoped proxy）。
- `## 7. prototype 的销毁语义`：明确容器不托管销毁的原因与工程建议。
- `## 排障决策表`：从“像单例”回到证据链（断点/日志/对象身份）。

1. 段落：scope 不是注解，而是语义边界
   - 强调 scope 影响：缓存策略、注入语义、生命周期/销毁边界。
   - 点明：同一个注入写法，在不同 scope 下语义会改变。

2. 段落：prototype 的三条边界（创建/注入/销毁）
   - 创建：每次 getBean 才可能新建；注入：singleton 只装配一次；销毁：容器不统一回收。
   - 给出常见误区：以为注入 prototype 就“每次都是新对象”。

3. 段落：prototype 注入 singleton 的三种写法对照
   - 直接注入（一次性）、Provider/ObjectProvider（按需）、@Lookup（方法注入）对照：语义、可测性、维护成本。
   - 给出“如何选”的最短规则（按需/可测/可控）。

4. 段落：scoped proxy 的可见证据与命名（scopedTarget）
   - 解释代理稳定、目标对象随 scope 变化；scopedTarget 命名是调试可见证据。

5. 段落：销毁回调边界与资源管理建议
   - prototype 资源释放要自管；给出推荐写法与注意点（避免在 destroy 回调上产生错误期待）。

### E) 016-05-lifecycle-and-callbacks.md（段落级建议）

落位提示（以当前文档骨架为准）：
- `## 机制主线` / `## 1. 源码级生命周期骨架`：先给“最小顺序模型”（实例化→填充→init→ready）。
- `## 补充：@PostConstruct/@PreDestroy...`：强调触发者是 BPP（避免“语法魔法”误解）。
- `## 4. 常见生命周期回调方式`：多入口对照（@PostConstruct vs InitializingBean vs init-method...）。
- `## 5. 生命周期与 Scope 的交互`：把 prototype destroy 边界与 scope 章节形成闭环。
- `## 调试与断点` / `## 可复现闭环`：把断点与主 Lab 绑定（initializeBean / populateBean / DisposableBeanAdapter 等）。

1. 段落：生命周期顺序的最小主线（先把顺序讲清）
   - 实例化 → 属性填充 → aware → BPP before-init → init 回调 → BPP after-init → ready。
   - 明确 singleton 与 prototype 在“预实例化/销毁”上的差异点。

2. 段落：同一能力的多入口对照（选择而非堆叠）
   - init：@PostConstruct vs InitializingBean vs init-method；destroy：@PreDestroy vs DisposableBean vs destroy-method。
   - 给出选择建议：框架侵入性、可测试性、可读性。

3. 段落：BPP 与生命周期的交叉点（为什么顺序会变）
   - 指出“插槽”：before-init/after-init、以及 InstantiationAware* 的 pre-instantiation 短路。
   - 强调：代理/包装会改变“你看到的对象”，但不等于生命周期没发生。

4. 段落：raw vs proxy 的可解释差异（把错觉说清楚）
   - 解释什么时候注入到的是 raw、什么时候是 proxy；以及这会如何影响回调观察结果。

5. 段落：动手验证（用 Lab 验证顺序与对象身份）
   - 建议读者通过 Lab 观察：同一 Bean 在不同阶段的对象身份变化与回调触发点。

### F) 017-06-post-processors.md（段落级建议）

落位提示（以当前文档骨架为准）：
- `## 机制主线`：用 refresh 节点串起 BDRPP/BFPP/BPP 的发生点。
- `## 3. 顺序（Ordering）`：拆开“注册顺序 vs 执行顺序”的常见误区（PriorityOrdered/Ordered/@Order）。
- `## 断点闭环` / `## 可复现闭环`：用“过早 getBean 错过 BPP”的最小复现绑定断点观察。
- `## 常见误区与边界`：补齐“没生效/生效太晚”的诊断三问（注册/排序/是否错过创建阶段）。

1. 段落：三类 PostProcessor 的职责边界（能改什么）
   - BDRPP：注册/补充 BeanDefinition；BFPP：修改 BeanFactory/BeanDefinition；BPP：参与 Bean 创建并可能包装代理。

2. 段落：时序主线（放回 refresh 的节点）
   - 用 refresh 主线串起：invokeBeanFactoryPostProcessors → registerBeanPostProcessors → finishBeanFactoryInitialization。
   - 解释“为什么 BFPP/BDRPP 必须早于 Bean 创建”。

3. 段落：排序与注册不是一回事（拆掉常见误区）
   - PriorityOrdered/Ordered/@Order 分别影响谁、影响的是“注册顺序”还是“执行顺序”。
   - 给出读者可验证的观察点：列表排序结果与执行顺序差异。

4. 段落：为什么某个处理器没生效（最短诊断路径）
   - 诊断三问：是否注册成功？排序是否如预期？是否错过创建阶段（过早 getBean）？
   - 给出“过早 getBean”触发错过的典型路径与防御写法提示。

5. 段落：清理未完占位并补齐关键解释
   - 把“未完”位置替换成可验证的解释（不堆源码），并指向最小复现实验或断点入口。

6. 段落：动手验证（最小复现 + 断点观察）
   - 建议把“错过 BPP”的现象与断点绑定：BPP 注册之前/之后创建的对象差异。

### G) 018-07-configuration-enhancement.md（段落级建议）

落位提示（以当前文档骨架为准）：
- `## 配置类解析主线` / `## 增强机制细节`：把 full/lite 与 proxyBeanMethods 的语义差异落到“方法拦截是否发生”。
- `## 3. 最推荐的写法...`：提供替代写法（参数注入替代跨 @Bean 方法调用）。
- `## 可复现闭环` / `## 源码与断点`：用断点观察增强发生点与增强后类名证据。
- `## 排障决策表`：把“语义不对/性能问题/循环依赖敏感”转为可定位步骤。

1. 段落：full vs lite 不是形式差异，而是语义保证差异
   - full 模式保证 @Bean 方法语义（避免跨方法调用产生多个实例）；lite 更接近“普通 @Bean 方法工厂”。

2. 段落：proxyBeanMethods 的取舍（语义风险清单）
   - 给出风险清单：跨 @Bean 方法调用、手动 new、循环依赖敏感场景、对单例语义强依赖的场景。
   - 给出替代写法方向：参数注入替代跨方法调用、拆分配置等。

3. 段落：如何识别是否被增强（可见证据）
   - 从类名后缀、断点入口、以及方法拦截行为三个角度让读者能“看见增强发生”。

4. 段落：与循环依赖的关联点（早期引用 + 代理）
   - 提示读者：配置类增强/代理会让循环依赖的 raw/proxy 边界更敏感，需要关注 early reference 介入。

5. 段落：动手验证（用断点观察增强与方法拦截）
   - 建议读者用断点观察：增强发生点、增强后类名、以及 @Bean 方法调用路径。

### H) 08-factorybean.md（段落级建议）

落位提示（以当前文档骨架为准）：
- `## 机制主线` / `## 1. FactoryBean 的核心语义`：先讲清“工厂 vs 产物”双重身份与缓存语义。
- `## & 前缀证据链`：把“拿到工厂/拿到产物”变成最短可验证路径。
- `## 可复现闭环`：主 Lab 选 deep dive，边界 Lab 选 edge cases（避免只讲用法）。
- `## 排障决策表`：围绕 name/type/缓存三连，给出最短诊断步骤。

1. 段落：两套身份与两套命名空间（工厂 vs 产物）
   - 解释为什么 `&name` 取的是 FactoryBean 本身，`name` 取的是产物；避免“拿错对象”的排错难题。

2. 段落：产物缓存与 isSingleton 的真实影响
   - 说明产物缓存如何影响“看起来是不是新对象”，以及它对依赖注入/循环依赖观察的副作用。

3. 段落：getObjectType 与按类型查找/注入的交叉
   - 解释 getObjectType 返回值如何参与类型匹配；返回 null 时可能导致的候选收集差异。

4. 段落：FactoryBean 边界案例（提前初始化/代理/循环依赖）
   - 给出读者常见踩坑路径：类型判断触发提前初始化、产物被代理导致的行为差异、与循环依赖交织时的异常形态。

5. 段落：动手验证（用 Lab 观察“工厂/产物/缓存”）
   - 建议读者通过 Lab 验证：什么时候拿到工厂、什么时候拿到产物、缓存命中与创建次数。

### I) 09-circular-dependencies.md（段落级建议）

落位提示（以当前文档骨架为准）：
- `## 机制主线`：先用“constructor 死、setter 有时能活”的窗口期解释引出三级缓存。
- `## 2/3/4`：把三级缓存与 early exposure、early reference 落到 doCreateBean/getSingleton 的关键步骤。
- `## 断点闭环` / `## 可复现闭环`：用边界矩阵逐格验证（setter/singleton/代理介入等）。
- `## 工程处理策略`：给出可落地解环方案并说明代价（避免只给开关）。

1. 段落：先画边界矩阵，再谈三级缓存
   - 以“注入方式（构造器/Setter）× scope（singleton/prototype）× 代理介入”快速判断可解性。

2. 段落：三级缓存的最短解释（每个缓存回答一个问题）
   - singletonObjects：最终单例；earlySingletonObjects：早期引用；singletonFactories：早期引用工厂。
   - 强调：三级缓存的目标是“打破单例 Setter 环”的时序问题，而非万能解决所有环。

3. 段落：early reference 与 getEarlyBeanReference 的关键意义
   - 解释为什么 AOP 等场景会参与 early reference，导致 raw/proxy 分歧；这也是很多“看不懂的现象”的根源。

4. 段落：开关不是解法（风险开关说明）
   - 解释 allowCircularReferences / allowRawInjectionDespiteWrapping / Boot 配置开关解决的是什么问题、带来什么风险。
   - 给出建议：把开关当“临时止血”，不要当长期方案。

5. 段落：可落地解环方案清单（含边界与代价）
   - 方案 A：重构依赖方向/拆分职责；方案 B：Provider/@Lazy 改变获取时机；方案 C：事件/回调/组合替代直接依赖。
   - 每条方案都说明：适用边界、对可测试性/可维护性的影响。

6. 段落：清理未完占位并补齐关键解释
   - 把“未完”位置替换为“可验证”的解释与最小复现实验入口，确保读者读到这里能继续走下去。

7. 段落：动手验证（逐格验证边界矩阵）
   - 建议读者逐格切换场景并观察：命中哪个缓存、在哪一步抛异常、是否发生 early reference、是否出现 raw/proxy 分歧。
