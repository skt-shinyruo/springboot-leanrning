# 93. 面试复述模板（决策树 → Lab → 断点入口）

## 导读

- 本章主题：**93. 面试复述模板（决策树 → Lab → 断点入口）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansProxyingPhaseLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansConditionEvaluationReportLabTest` / `SpringCoreBeansAutoConfigurationOverrideMatrixLabTest` / `SpringCoreBeansXmlBeanDefinitionReaderLabTest` / `SpringCoreBeansAutowireCapableBeanFactoryLabTest` / `SpringCoreBeansSpelValueLabTest` / `SpringCoreBeansCustomQualifierLabTest`

## 机制主线

本章目标：把 `spring-core-beans` 变成你的“面试答题脚本”。

---

## 0. 一句话总纲（通用开场）

> Spring 的 Bean 机制可以用“三层模型”统一：**定义层（BeanDefinition）→ 实例层（create/populate/initialize）→ 最终暴露对象（可能是 proxy/early reference）**。  
> 面试里所有“为什么注入的是它/为什么会 proxy/为什么循环依赖有时能救”，都能落到这三层的某个阶段。

对应入口：

- 建议先跑：`docs/part-00-guide/01-quickstart-30min.md`（3 个最小闭环，把“可复述”变成“可证明”）
- definition vs instance（定义层 vs 实例层）：`SpringCoreBeansContainerLabTest#beanDefinitionIsNotTheBeanInstance`
- final exposed object（最终暴露对象可能被替换）：`SpringCoreBeansBeanCreationTraceLabTest#beanCreationTrace_recordsPhases_andExposesProxyReplacement`

---

## 1) 依赖解析（DI）：候选收集 → 候选收敛 → 最终注入

### 复述模板（可背）

1. 先按类型收集候选（Map<beanName, candidate>）  
2. 再按规则收敛候选：Qualifier → name 匹配 → Primary → Priority/Ordered（部分场景）  
3. 收敛成 1 个就注入；收敛不下来就 fail-fast（NoUnique）  

- `DefaultListableBeanFactory#doResolveDependency`
- `DefaultListableBeanFactory#findAutowireCandidates`
- `DefaultListableBeanFactory#determineAutowireCandidate`
- `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`

---

## 2) BFPP / BDRPP / BPP：改定义 vs 改实例（以及它们能/不能做什么）

### 复述模板（可背）

- **BDRPP**：可以“注册/修改 BeanDefinition”（定义层入口更早）  
- **BFPP**：可以“修改 BeanDefinition”（仍在定义层）  
- **BPP**：可以“处理实例”甚至“替换成 proxy”（实例层/最终暴露对象）  

---

## 3) 生命周期：创建/注入/初始化/销毁（以及回调顺序）

### 复述模板（可背）

- 创建主线：instantiate → populate → initialize  
- 注入发生在 populate（而不是构造器之后“自然发生”）  
- `@PostConstruct` 属于初始化阶段（依赖 BPP）  
- prototype 默认不走容器销毁（除非你显式 destroy）  

---

## 4) 循环依赖：为什么 constructor 无解、setter 有时能救？

### 复述模板（可背）

- constructor 循环：需要“先有对象才能注入”，因此无解  
- setter 循环：单例创建时存在 early exposure 窗口（三级缓存），可能救回来  
- 一旦代理/包装介入，early reference 可能变成 proxy，行为会变化  

---

## 5) Spring Boot 自动装配：为什么有/为什么没有/为什么没退让？

### 复述模板（可背）

- auto-config 本质是：配置导入（@Import）+ 条件评估（@Conditional...）+ bean 注册  
- 条件评估发生在注册阶段（refresh 前半段），不是看“最终容器状态”  
- 覆盖/back-off 要求“覆盖 bean 在条件评估时可见”，否则会出现重复候选/注入失败  

- `ConditionEvaluator#shouldSkip`
- `OnBeanCondition#getMatchOutcome`
- `AbstractApplicationContext#refresh`

---

## 6) AOT/Native：RuntimeHints（构建期契约）

### 复述模板（可背）

- `RuntimeHintsRegistrar#registerHints`

---

## 7) 真实世界补齐（面试加分：你能解释“容器外对象/SpEL/自定义 Qualifier”）

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

约束：**不靠背概念**，而是靠“可复述的决策树 + 可运行的 Lab + 可下断点证明”。

- 深挖指南：`docs/part-00-guide/00-deep-dive-guide.md`
- 排障主入口：`docs/part-02-boot-autoconfig/11-debugging-and-observability.md`

### 对应 Lab（可证明）

- `SpringCoreBeansInjectionAmbiguityLabTest`
- `SpringCoreBeansAutowireCandidateSelectionLabTest`

### 推荐断点（够用版）

推荐断点（按“候选收集 → 候选收敛 → 最终注入”顺序）：

1) `DefaultListableBeanFactory#doResolveDependency`（依赖解析总入口）
2) `DefaultListableBeanFactory#findAutowireCandidates`（候选集合：Map<beanName, candidate>）
3) `DefaultListableBeanFactory#determineAutowireCandidate`（候选收敛：最终 winner）
4) `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`（Qualifier/meta-annotation 过滤）

固定观察点（watch list）：

- `descriptor`（注入点抽象：类型/required/注解/名称）
- `matchingBeans` / `candidates`（候选集合）
- `autowiredBeanName` / `candidateName`（最终命中者）

### 对应 Lab（可证明）

- `SpringCoreBeansRegistryPostProcessorLabTest`
- `SpringCoreBeansPostProcessorOrderingLabTest`
- `SpringCoreBeansProxyingPhaseLabTest`

### 推荐断点

推荐断点（把“改定义 vs 改实例”落到 refresh 主线）：

1) `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`（BDRPP/BFPP 执行：定义层）
2) `PostProcessorRegistrationDelegate#registerBeanPostProcessors`（BPP 注册：实例层拦截链）
3) `AbstractBeanFactory#addBeanPostProcessor`（最终进入 bpp list 的时机与顺序）

固定观察点（watch list）：

- `postProcessorNames`（候选集合）
- `beanFactory.getBeanPostProcessors()`（最终执行顺序）

### 对应 Lab（可证明）

- `SpringCoreBeansLifecycleCallbackOrderLabTest`
- `SpringCoreBeansBeanCreationTraceLabTest`

### 推荐断点

推荐断点（把回调顺序跑成“断点证据链”）：

1) `AbstractAutowireCapableBeanFactory#doCreateBean`（创建主线）
2) `AbstractAutowireCapableBeanFactory#populateBean`（注入发生点）
3) `AbstractAutowireCapableBeanFactory#initializeBean`（Aware + init callbacks 串联点）
4) `CommonAnnotationBeanPostProcessor#postProcessBeforeInitialization`（`@PostConstruct` 触发点之一）
5) `DisposableBeanAdapter#destroy`（销毁回调入口）

### 对应 Lab（可证明）

- `SpringCoreBeansContainerLabTest`
- `SpringCoreBeansEarlyReferenceLabTest`

### 推荐断点

推荐断点（把“constructor 无解 / setter 可救”落到三层缓存语义）：

1) `DefaultSingletonBeanRegistry#getSingleton`（三层缓存命中路径）
2) `DefaultSingletonBeanRegistry#addSingletonFactory`（early exposure：注册 ObjectFactory）
3) `AbstractAutowireCapableBeanFactory#doCreateBean`（创建窗口期：什么时候允许 early reference）
4) `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`（early reference 是否变成 proxy 的分支）

### 对应 Lab（可证明）

- `SpringCoreBeansConditionEvaluationReportLabTest`
- `SpringCoreBeansAutoConfigurationOrderingLabTest`
- `SpringCoreBeansAutoConfigurationBackoffTimingLabTest`
- `SpringCoreBeansAutoConfigurationOverrideMatrixLabTest`

### 推荐断点

推荐断点（把“为什么有/为什么没有/为什么没退让”落到条件评估时机）：

1) `AutoConfigurationImportSelector#selectImports`（自动配置导入入口之一）
2) `ConditionEvaluator#shouldSkip`（条件评估总入口）
3) `OnBeanCondition#getMatchOutcome`（`@ConditionalOnBean/@ConditionalOnMissingBean` 等匹配）
4) `ConditionEvaluationReport#get`（把条件报告当成数据结构查询，而不是日志）

- AOT/Native 的关键是把“运行期反射/代理/资源需求”前移为“构建期契约”  
- Spring 用 RuntimeHints 表达这种契约：reflection/proxy/resource 等  
- 你可以在 JVM 单测里验证 hints 的存在性（不必构建 native image）  

### 对应章节 + Lab（可证明）

- 章节：`docs/part-05-aot-and-real-world/40-aot-and-native-overview.md`
- 章节：`docs/part-05-aot-and-real-world/41-runtimehints-basics.md`
- Lab：`SpringCoreBeansAotRuntimeHintsLabTest`

### 推荐断点

真实世界补齐（XML / 容器外对象 / SpEL / 自定义 Qualifier）常用断点：

- XML（定义层输入）：`XmlBeanDefinitionReader#loadBeanDefinitions`、`DefaultListableBeanFactory#registerBeanDefinition`
- 容器外对象：`AutowireCapableBeanFactory#autowireBean`、`AutowireCapableBeanFactory#initializeBean`、`AutowireCapableBeanFactory#destroyBean`
- SpEL / `@Value("#{...}")`：`StandardBeanExpressionResolver#evaluate`、`AbstractBeanFactory#resolveEmbeddedValue`
- 自定义 Qualifier：`QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`

- XML → BeanDefinitionReader：`docs/part-05-aot-and-real-world/42-xml-bean-definition-reader.md` + `SpringCoreBeansXmlBeanDefinitionReaderLabTest`
- 容器外对象注入：`docs/part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md` + `SpringCoreBeansAutowireCapableBeanFactoryLabTest`
- SpEL：`docs/part-05-aot-and-real-world/44-spel-and-value-expression.md` + `SpringCoreBeansSpelValueLabTest`
- 自定义 Qualifier：`docs/part-05-aot-and-real-world/45-custom-qualifier-meta-annotation.md` + `SpringCoreBeansCustomQualifierLabTest`

## 常见坑与边界

1) **误区：`@Order` 能解决单依赖歧义**
   - `@Order` 主要影响集合注入/链路顺序；单依赖收敛主要看 `@Qualifier/@Primary/name` 等规则（见 `03/33`）。
2) **误区：`@PostConstruct` 是“Java 自带回调”**
   - `@PostConstruct` 依赖 BPP（例如 CommonAnnotationBeanPostProcessor）；BPP 没注册/没执行时它不会发生（见 `12/17`）。
3) **误区：循环依赖就是“三级缓存技巧”**
   - 三级缓存承载的是 early reference 的时机与语义；代理介入时 early reference 可能变成 proxy（见 `09/16`）。
4) **误区：条件装配是看“最终容器状态”**
   - 条件评估发生在注册阶段（refresh 前半段）；back-off 要求“覆盖 bean 在评估时可见”（见 `10/11`）。

## 小结与下一章

- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `AbstractAutowireCapableBeanFactory#populateBean`
- `AbstractAutowireCapableBeanFactory#initializeBean`
- `DisposableBeanAdapter#destroy`

- `DefaultSingletonBeanRegistry#getSingleton`
- `DefaultSingletonBeanRegistry#addSingletonFactory`
- `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansProxyingPhaseLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansConditionEvaluationReportLabTest` / `SpringCoreBeansAutoConfigurationOverrideMatrixLabTest` / `SpringCoreBeansXmlBeanDefinitionReaderLabTest` / `SpringCoreBeansAutowireCapableBeanFactoryLabTest` / `SpringCoreBeansSpelValueLabTest` / `SpringCoreBeansCustomQualifierLabTest`

上一章：[92. 知识点地图（Concept → Chapter → Lab）](92-knowledge-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[94. 生产排障清单（异常分型 → 入口 → 观察点 → 修复策略）](94-production-troubleshooting-checklist.md)

<!-- BOOKIFY:END -->
