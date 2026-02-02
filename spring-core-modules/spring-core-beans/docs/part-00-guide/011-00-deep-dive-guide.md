# 第 11 章：00. 深入分析指南：将“Bean 三层模型”落实到源码与断点
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问框架）"

    - 知识点：深入分析指南：将“Bean 三层模型”落实到源码与断点
    - 使用方式：建议先运行本章给出的最小实验，以断言固定现象；随后沿“现象 → 分层（定义/候选/实例）→ 首要断点 → 观察点”的路径，形成可复核的证据链。
    - 原理：以 `ApplicationContext#refresh` 为时间线骨架，将 Bean 的行为分解为“定义（BeanDefinition）/实例对象（bean instance）/最终暴露对象（exposed object）”三个层次，并在关键扩展点（BDRPP/BFPP/BPP）处观察定义与实例如何被加工或替换。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 10 章：主线时间线：Spring Core Beans（IoC 容器）](010-03-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）](012-01-quickstart-30min.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

本章不再重复 Spring 的概念性介绍，而侧重给出一套可复用的源码级分析方法：当面对“注入为何选择某个候选”“代理为何出现或缺失”“循环依赖为何仅在特定条件下可闭合”等问题时，能够以最小实验固定现象，并借助断点与观察点形成可复核的结论。

- 本章主题：**00. 深入分析指南：将“Bean 三层模型”落实到源码与断点**
- 阅读建议：先完成一个最小实验（确保入口与断言可复现），再使用“症状驱动导航表”选择章节与断点；必要时对照主线章的时间线，将观察点置于正确阶段理解。

!!! summary "本章要点"

    - 本章的核心内容是“症状驱动导航表”：将现象映射到分层与章节入口，并进一步给出断点与 LabTest 证据链。
    - 本章给出一张“最小源码导航图”：用少量入口方法把 Bean 的定义、创建、注入、代理与缓存机制串为一条主线。
    - 建议至少完成一次方法级最小实验（`-Dtest=Class#method`），并在断点中使用固定的监视列表（watch list）验证结论。

!!! example "本章配套实验（建议先运行）"

    - 核心（建议）：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansBeanCreationTraceLabTest`
    - 扩展（按需选用）：`SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansMergedBeanDefinitionLabTest` / `SpringCoreBeansResolvableDependencyLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansExceptionNavigationLabTest`
    - 入口文件（便于定位）：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansAutowireCandidateSelectionLabTest`，再用 `SpringCoreBeansContainerLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`ApplicationContext#refresh`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“常见误区与边界”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->

## 学习目标与自检标准（10/30/3）

为避免阅读仅停留在主观理解层面，本章给出 10/30/3 的自检标准（不强调背诵，强调可验证与可复现）：

- **10 分钟（可复述）**：能够说明 Bean 的“三层对象”与“最终暴露对象”分别指代什么（Definition / Instance / Exposed），并给出一个反例解释它们可能不一致（例如代理、FactoryBean）。
- **30 分钟（可验证）**：完成一次方法级 Lab/Test 运行，并在断点中验证“对象在哪一层被替换/为何被替换”（而非仅依赖日志推断）。
- **3 个断点（可定位）**：至少能够将断点定位到以下三类方法（方法名不要求逐字一致，但应能定位到方法级）：
  1. `doGetBean(...)`（从“获取对象”切入）
  2. `doCreateBean(...)`（从“创建对象”切入）
  3. `resolveDependency(...)` / `doResolveDependency(...)`（从“为何注入它”切入）

若上述三项目标均可完成，可认为已达到本章学习目标；若尚未完成，建议先按本章实践路线完成最小实验，再进入后续章节，以避免在源码细节中失去主线。

## 0. 机制主线：由概念到可验证结论

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

当读者开始进行源码级分析时，较常见的困难并非“概念错误”，而是缺少可复用的映射关系：概念难以映射到 refresh 时间线、关键数据结构与扩展点的介入位置。为此，本节先给出最小抓手与分析入口。

适用范围：本仓库基于 Spring Boot 3.x（对应 Spring Framework 6.x）。小版本可能调整内部实现细节，但主线阶段划分与核心接口语义通常保持稳定。

如需对 `refresh()` 时间线与 `doCreateBean()` 创建链路获得连续叙事，可先阅读主线章：

- [从 `refresh()` 到 `doCreateBean()`：把 Spring Bean “变成对象”的主线走通（源码级）](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md)

在首次进行断点与源码分析时，较常见的两类困难如下：

- **范围过大（信息噪声较高）**：直接执行 `spring-boot:run` 或运行全量测试，通常会显著增加断点命中次数并放大噪声
  - 建议做法：优先使用本仓库 `*LabTest`，将问题限定为“最小容器 + 最小现象 + 最小断言”
- **缺少分层（对象形态不清晰）**：将“定义层/候选层/实例层”混合观察，容易在长调用栈中失去主线
  - 建议做法：先明确当前关注对象属于哪一层：**BeanDefinition**（是否已注册/已合并）、**候选集合**（候选范围与收敛过程）、**最终暴露对象**（为何呈现为 proxy 等形态）

### 0.1 最小抓手：5 类对象与关键链路（将概念转化为可观察对象）

在最小化观察对象的前提下，可优先关注以下 5 类对象及其关键链路（每一条均可在断点中验证）：

1) **BeanDefinition → MergedBeanDefinition**
   - 入口方法：`AbstractBeanFactory#getMergedBeanDefinition`
   - 观察重点：注解、父子定义、属性覆盖如何合并为最终配方（`RootBeanDefinition`）
2) **DependencyDescriptor → 候选收集/收敛**
   - 入口方法：`DefaultListableBeanFactory#doResolveDependency`
   - 关键分支：`findAutowireCandidates`（收集）→ `determineAutowireCandidate`（收敛）
3) **BeanWrapper → 属性填充**
   - 入口方法：`AbstractAutowireCapableBeanFactory#populateBean`
   - 观察重点：`PropertyValues` 如何转换并写入目标对象
4) **BeanPostProcessor → 对象替换/增强**
   - 入口方法：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`
   - 观察重点：`bean` → `result` 的替换时机（proxy/包装）
5) **单例缓存与 early reference**
   - 入口方法：`DefaultSingletonBeanRegistry#getSingleton` / `addSingletonFactory`
   - 观察重点：三级缓存的变化与 early reference 的介入时机（循环依赖、代理一致性）

### 0.2 首次深入分析的常见障碍与处置路径

以下三类障碍出现频率较高，且往往决定调试效率：

- **断点未命中**
  - 典型原因：目标 bean 尚未进入创建流程（`@Lazy`、未触发 `getBean`、未执行到预实例化）
  - 处理路径：显式触发创建（调用 `getBean` 或取消 `@Lazy`）→ 在 `preInstantiateSingletons` 或 `doGetBean` 处观察是否进入目标分支
- **测试耗时较长或输出干扰较多**
  - 典型原因：启动全量上下文，或 classpath 扫描范围过大
  - 处理路径：使用 `AnnotationConfigApplicationContext` 仅注册最小配置类 → 以方法级方式运行（`-Dtest=Class#method`）
- **调试成本较高**
  - 典型原因：断点设置在高频调用点（如 `doGetBean`、`isTypeMatch`）
  - 处理路径：优先使用条件断点（以 `beanName` 过滤）→ 固定监视列表（watch list）→ 避免以单步方式贯穿全链路

对首次进入本模块的读者，建议先完成可复现的最小实验，再围绕关键入口逐步加深：

1) **首先（约 30 分钟）完成 3 个最小实验**：命令、断点与观察点均已给出
   - 见：[第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）](012-01-quickstart-30min.md)
2) **随后选取 3 个入口测试类作为定位锚点**：每个类分别覆盖一条主线与一个决定性分支
   - `SpringCoreBeansLabTest`：以典型现象为入口（Qualifier/Scope/Lifecycle）
     - 关键分支：`doResolveDependency` 的候选收敛（Qualifier/Primary/by-name fallback）
   - `SpringCoreBeansContainerLabTest`：以容器主线为入口（定义 vs 实例、BFPP/BPP、FactoryBean、循环依赖边界）
     - 关键分支：`refresh` 时间线中 BFPP/BPP 的先后顺序与单例创建时机
   - `SpringCoreBeansBeanCreationTraceLabTest`：以创建过程可视化为入口（何时被替换为 proxy）
     - 关键分支：`applyBeanPostProcessorsAfterInitialization` 中 `bean` → `result` 的首次替换

配合断点地图使用，可进一步降低在章节与源码之间切换的成本：

- [第 13 章：02. 断点地图：容器主线可复用断点/观察点清单](013-02-breakpoint-map.md)

### 0.3 第一轮阅读：以较低成本把握阶段边界
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


第一轮阅读的目标在于把握阶段边界，而非追求完整细节。建议按以下顺序进行：

1) 先完成第 12 章快启实验，确保能够通过断点验证“候选收敛 / prototype 注入边界 / proxy 替换”三类现象
2) 在 `AbstractApplicationContext#refresh` 设置断点，仅观察 4 个关键节点：
   - `invokeBeanFactoryPostProcessors`（定义层稳定）
   - `registerBeanPostProcessors`（实例层增强点进入容器）
   - `preInstantiateSingletons`（非 lazy 单例批量创建）
   - `finishRefresh`（容器就绪）
3) 选择一个现象（注入歧义/生命周期/代理），将其缩减为方法级入口（`-Dtest=Class#method`），再阅读对应章节并完成验证

### 0.4 方法验证：`SpringCoreBeansAutowireCandidateSelectionLabTest` 的 3 条关键结论

完成该 Lab 后，建议至少能够复述并验证以下 3 条结论（每条均可定位到方法级入口）：

1) **`@Order` 影响集合注入顺序，不解决单一依赖歧义**
   - 证据链：`resolveMultipleBeans` → `AnnotationAwareOrderComparator`
   - 对照分支：`determineAutowireCandidate` 仍可能抛出 `NoUniqueBeanDefinitionException`
2) **单一依赖的收敛优先级：Qualifier > Primary > Priority > by-name fallback**
   - 证据链：`QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate` →
     `DefaultListableBeanFactory#determinePrimaryCandidate` →
     `DefaultListableBeanFactory#determineHighestPriorityCandidate` →
     `DefaultListableBeanFactory#determineAutowireCandidate`
3) **泛型信息与 ObjectProvider 会改变候选收敛结果**
   - 证据链：`GenericTypeAwareAutowireCandidateResolver#checkGenericTypeMatch`
   - 对照现象：`ObjectProvider#getIfUnique` 可能返回 null；`orderedStream` 会按 `@Order/@Priority` 排序

## 1. 深入分析的入口选择与主线把握

### 1.0 症状驱动导航：遇到现象先选对入口（章节 → 断点 → Lab）

深入分析的主要风险并非“无法理解源码”，而是入口选择不当。入口不当会导致在庞大调用栈中耗费大量时间，却难以形成可验证的结论。

下表用于将现象映射到**层次（定义/候选/实例）**，再映射到**章节入口/断点抓手/Lab 证据链**。

| 现象（观察到的） | 判定主要障碍所在层次 | 推荐章节入口（最短路径） | 建议断点（抓住分支） | 对应 LabTest（证据链） |
|---|---|---|---|---|
| Bean 未注册（扫描/导入/`@Bean` 未生效） | 定义层（BeanDefinition 图未形成） | [18. refresh→doCreateBean 主线](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md)<br>[12. 注解为何生效（bootstrap）](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md) | `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`<br>`ConfigurationClassPostProcessor#processConfigBeanDefinitions` | `SpringCoreBeansRegistryPostProcessorLabTest`<br>`SpringCoreBeansBootstrapInternalsLabTest` |
| `@Autowired` 为 null / `@PostConstruct` 未执行（常见于手工启动 BeanFactory） | 实例层（规则未装入：BPP 缺失） | [12. 注解为何生效（bootstrap）](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md) | `PostProcessorRegistrationDelegate#registerBeanPostProcessors`<br>`AutowiredAnnotationBeanPostProcessor#postProcessProperties`<br>`InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization` | `SpringCoreBeansBootstrapInternalsLabTest`<br>`SpringCoreBeansBeanFactoryApiLabTest` |
| 启动即异常 vs 第一次 `getBean` 才异常 | refresh 预实例化 vs on-demand | [18. refresh→doCreateBean 主线](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md) | `DefaultListableBeanFactory#preInstantiateSingletons`<br>`AbstractBeanFactory#doGetBean` | `SpringCoreBeansPreInstantiationLabTest` |
| 单个注入歧义（NoUniqueBeanDefinitionException） | 候选层（候选集合收敛失败） | [33. 候选选择与顺序](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md) | `DefaultListableBeanFactory#doResolveDependency` | `SpringCoreBeansInjectionAmbiguityLabTest`<br>`SpringCoreBeansAutowireCandidateSelectionLabTest` |
| 循环依赖：setter 在特定条件下可闭合，constructor 一般无法闭合；或 early reference 形态异常 | 实例层（创建窗口 + 缓存/early reference） | [18. refresh→doCreateBean 主线](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md)<br>[16. early reference 与循环依赖](../part-03-container-internals/16-early-reference-and-circular.md) | `DefaultSingletonBeanRegistry#getSingleton/addSingletonFactory`<br>`AbstractAutowireCapableBeanFactory#doCreateBean`（earlySingletonExposure） | `SpringCoreBeansCircularDependencyBoundaryLabTest`<br>`SpringCoreBeansEarlyReferenceLabTest` |
| 代理不生效 / 自调用绕过 / 误判为“存在 AOP 但未生效” | 实例层（BPP 包装阶段） | [31. BPP 如何把 Bean 换成 Proxy](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md) | `BeanPostProcessor#postProcessAfterInitialization` | `SpringCoreBeansProxyingPhaseLabTest` |
| `getBean("x")` 获取到的类型与预期不一致，`&x` 又呈现不同语义 | 实例层（FactoryBean 语义） | [08. FactoryBean：& 前缀与产品对象](../part-01-ioc-container/08-factorybean.md)<br>[18. refresh→doCreateBean 主线](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md) | `AbstractBeanFactory#getObjectForBeanInstance` | `SpringCoreBeansFactoryBeanDeepDiveLabTest` |

该表无需一次性记忆；在排障时可按以下问题进行定位：

1. 当前现象属于定义层、候选层还是实例层？
2. 断点应设置在 refresh 的哪个阶段？
3. 可用哪个 `*LabTest` 将现象固定为可回归断言？

### 1.1 三条主线：时间线、分层映射、扩展点介入

为避免在细节中丢失主线，建议同时保持以下三条线索：

1) **时间线（发生顺序）**：容器从 `refresh()` 开始，按阶段推进
2) **定义到实例的映射**：`BeanDefinition` →（合并/增强）→ 实例创建与注入 → 最终暴露对象
3) **扩展点介入位置**：BDRPP/BFPP/BPP 分别在“何时介入、改变什么、影响哪些对象”

## 2. 最小源码导航图（定义层 / 实例层 / 缓存层）

将 [01. Bean 运行机制：从 BeanDefinition 到最终暴露对象](../part-01-ioc-container/020-01-bean-mental-model.md) 的三层结构对应到源码，最小可以按以下结构建立映射。

### 2.1 定义层：BeanDefinition 进入、存储、调整

- **容器与注册表**：`BeanDefinitionRegistry`（接口语义）
- **常见实现**：`DefaultListableBeanFactory`（既是 `BeanFactory`，也是 Registry）
- **定义对象**：`BeanDefinition` / `RootBeanDefinition`（多数场景下最终以 Root 形态参与计算）

定义层最关键的入口：

- `BeanDefinitionRegistryPostProcessor`（BDRPP）：新增/修改 BeanDefinition（影响 BeanDefinition 图是否形成）
- `BeanFactoryPostProcessor`（BFPP）：加工已有定义（影响最终参与实例化计算的定义元数据）
- `ConfigurationClassPostProcessor`：把 `@Configuration/@Bean/@Import` 解析为 BeanDefinition（属于 BFPP 体系）

对应章节：

- [02. 注册入口](../part-01-ioc-container/02-bean-registration.md)
- [06. PostProcessor 概览](../part-01-ioc-container/017-06-post-processors.md)
- [12. 注解为何生效（基础设施处理器）](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)
- [13. BDRPP：定义注册](../part-03-container-internals/13-bdrpp-definition-registration.md)

### 2.2 实例层：createBean → 注入 → 初始化 → 可能被替换

- `createBean(...)`：创建入口（包含策略选择与提前返回路径）
- `doCreateBean(...)`：核心主流程（实例化 → 属性填充 → 初始化）
- `populateBean(...)`：属性填充（依赖注入）
- `initializeBean(...)`：初始化（Aware、before-init、init-method、after-init）

实例层最关键的入口：

- `InstantiationAwareBeanPostProcessor`：影响实例化/属性填充（包括提前返回替代对象等）
- `BeanPostProcessor`：before/after init 可替换为代理（AOP/事务代理常在此出现）

对应章节：

- [30. 注入发生在什么时候（field vs constructor）](../part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md)
- [15. 实例化前短路](../part-03-container-internals/15-pre-instantiation-short-circuit.md)
- [31. BPP 如何把 Bean 换成 Proxy](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)

### 2.3 缓存层：单例三层缓存与 early reference

循环依赖、提前暴露与 early reference 相关逻辑主要位于 `DefaultSingletonBeanRegistry`：

- 关键入口：`getSingleton(...)`、`addSingletonFactory(...)`
- 关键现象：`singletonObjects / earlySingletonObjects / singletonFactories` 的大小变化与命中分支

对应章节：

- [09. 循环依赖概览](../part-01-ioc-container/09-circular-dependencies.md)
- [16. early reference 与循环依赖：getEarlyBeanReference](../part-03-container-internals/16-early-reference-and-circular.md)

### 2.4 三项决定性因素：决定断点中可观察到的对象形态

许多看似“隐式”的现象，往往不是由 `doCreateBean(...)` 的单一节点决定，而是由以下三项因素共同作用所致：

1) **refresh 阶段边界**：处理器是否在批量创建单例之前完成注册（BPP 链是否完整）
2) **merged definition 形成时机**：创建时使用的 `RootBeanDefinition` 可能并非 registry 中的原始定义
3) **early reference 与一致性约束**：循环依赖窗口与 early/final 形态一致性检查会改变可观察到的结果

建议在断点中固定监视列表（watch list），以避免在每次调试时重复定位变量：

- refresh 时间线：`beanFactory.getBeanDefinitionCount()`、`beanFactory.getBeanPostProcessorCount()`；以及 `singletonObjects.size()` / `earlySingletonObjects.size()` / `singletonFactories.size()`
- 创建链路：`beanName`、`mbd`（`RootBeanDefinition`）、`bw.getWrappedInstance()`（当前实例）、`pvs`（属性值集合）
- 依赖解析：`DependencyDescriptor`（注入点类型/泛型/注解）、候选集合（按类型查出的 beanNames）、最终选中的 beanName
- 依赖关系：`dependentBeanMap`（依赖关系边的记录与查询）

## 3. 源码入口与观察点（从主线分支切入）

本节将“入口方法—观察点—典型问题”进行对应，以作为断点选择的参考。

### 3.1 启动时间线：从 `refresh` 切入

入口方法（按主线顺序）：

- `AbstractApplicationContext#refresh`
- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
- `DefaultListableBeanFactory#preInstantiateSingletons`

关键结论：**处理器注册应先于业务单例的批量创建**。诸如注解生效、代理生成、生命周期回调执行等行为，在机制上均依赖这条阶段边界。

### 3.2 Bean 创建主线：从 `doCreateBean` 切入

入口方法：

- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `AbstractAutowireCapableBeanFactory#populateBean`
- `AbstractAutowireCapableBeanFactory#initializeBean`

观察重点：

- 原始对象何时创建（实例化）
- 依赖注入何时发生（属性填充）
- `@PostConstruct` / `InitializingBean` 等回调何时触发（初始化阶段）
- BPP 何时可能替换为代理（after-init）

### 3.3 依赖解析：从 `doResolveDependency` 切入

入口方法：

- `DefaultListableBeanFactory#doResolveDependency`
- `AutowiredAnnotationBeanPostProcessor#postProcessProperties`（触发注入）

观察重点：

- `DependencyDescriptor` 描述了注入点的类型、泛型与注解信息
- 候选集合如何产生（按类型查找），以及如何收敛（Qualifier/Primary/Priority/by-name fallback）

对应章节：

- [03. 依赖注入解析](../part-01-ioc-container/014-03-dependency-injection-resolution.md)
- [33. 候选选择与顺序](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)

### 3.4 merged definition：从合并入口切入

当在 `doCreateBean` 中看到 `RootBeanDefinition`，但与 registry 的定义不一致时，可从 merged definition 入口切入：

- `AbstractBeanFactory#getMergedLocalBeanDefinition`
- `AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`

固定观察点建议：

- `beanName`
- `mbd`（`RootBeanDefinition`）
- `mbd.getPropertyValues()`（属性值来源：parent/child/解析后的合并）
- `mbd.getInitMethodName()` / `mbd.getDestroyMethodName()`（生命周期元数据来源）
- `mbd.getResolvedTargetType()`（类型信息何时变得具体）

### 3.5 ResolvableDependency：可注入但非 Bean

当出现“按类型可注入，但 `getBean(type)` 无法获取”时，常见原因是命中了 resolvableDependencies 分支：

- `DefaultListableBeanFactory#registerResolvableDependency`（注册 type → instance）
- `DefaultListableBeanFactory#doResolveDependency`（注入路径：可命中）
- `AbstractBeanFactory#doGetBean`（查找路径：不会命中）

固定观察点：

- `descriptor`（`DependencyDescriptor`）
- `descriptor.getDependencyType()`
- `resolvableDependencies`（Map 条目）

### 3.6 dependentBeanMap：依赖图记录与销毁顺序

若需分析“依赖关系如何记录”“销毁顺序为何与预期不一致”等问题，可从依赖关系记录入口切入：

- `DefaultSingletonBeanRegistry#registerDependentBean`（记录依赖边）
- `DefaultSingletonBeanRegistry#getDependenciesForBean`（从 beanName 反查依赖）
- `DefaultSingletonBeanRegistry#getDependentBeans`（反查：谁依赖当前 bean）

## 4. 实践路线：将分析过程落实为可复现实验

本章建议以最小可复现实验为单位推进。每个实验应能够回答三个问题：现象是什么、断点设置在哪里、证据是什么。

### 4.1 运行方式（精确到测试类/方法）

运行单个测试类：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test
```

运行单个测试方法（更利于断点深入分析）：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest#beanDefinitionIsNotTheBeanInstance test
```

> 亦可在 IDE 中直接运行某个 `@Test` 方法；命令行写法便于复制与共享，更有利于复现。

### 4.2 断点过滤：以 `beanName` 作为第一筛选条件

容器在启动与运行期间会创建大量基础设施 bean（processors、internal bean、代理相关 bean）。若不使用条件断点，断点可能在高频路径上反复命中，从而导致观察点分散。

条件断点示例（以 `beanName` 为例）：

- `beanName.equals("exampleBean")`
- `beanName.equals("alpha") || beanName.equals("beta")`
- `beanName.startsWith("org.springframework.")`（用于识别 Spring 内部对象；排障时亦可用作反向过滤条件）

### 4.3 最小实验清单（建议按顺序）

1) 定义层 vs 实例层：`BeanDefinition != bean instance`
   - 入口：`SpringCoreBeansContainerLabTest#beanDefinitionIsNotTheBeanInstance`
   - 验证要点：说明定义层/实例层区别；解释为何最终 `getBean()` 可能返回 proxy
2) refresh 的粗粒度阶段观察（按阶段而非按调用栈长度）
   - 入口：任意可 refresh 的最小 Lab（同上即可）
   - 验证要点：注册定义 → BFPP/BDRPP → 注册 BPP → 创建单例 → 收尾事件（明确阶段边界）
3) BeanDefinition 从哪里来（列 3 条入口，并说明落点）
   - 入口：阅读 [02](../part-01-ioc-container/02-bean-registration.md) + 运行 `SpringCoreBeansBootstrapInternalsLabTest`
   - 验证要点：扫描 / `@Bean` / `@Import`（selector/registrar）如何注册到 registry
4) 注入歧义：对比 `@Primary` 与 `@Qualifier`
   - 入口：`SpringCoreBeansInjectionAmbiguityLabTest`
5) 候选选择 vs 排序：区分 `@Order/@Priority/@Primary`
   - 入口：`SpringCoreBeansAutowireCandidateSelectionLabTest`
   - 验证要点：哪些影响“单一依赖选择”，哪些影响“集合/链路顺序”
6) prototype 注入 singleton 的语义边界：为何在使用侧呈现单例化效果
   - 入口：`SpringCoreBeansLabTest`（prototype 相关用例）
7) 生命周期：`@PostConstruct` 时机与 prototype 销毁语义
   - 入口：`SpringCoreBeansLifecycleCallbackOrderLabTest`
8) Post-Processor：BDRPP/BFPP/BPP 的介入点与影响面
   - 入口：`SpringCoreBeansRegistryPostProcessorLabTest` + `SpringCoreBeansPostProcessorOrderingLabTest`
9) early reference：哪些循环依赖场景可能被缓解？构造器循环依赖为何通常无法闭合？
   - 入口：`SpringCoreBeansEarlyReferenceLabTest`
10) 排障路径：从异常信息定位断点入口，并说明选择理由
   - 入口：`SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansBeanGraphDebugLabTest`

### 4.4 三条高频但易误判的扩展验证

1) **Merged `RootBeanDefinition`**
   - 入口：`SpringCoreBeansMergedBeanDefinitionLabTest`
   - 要点：registry 里保存的是原始定义；创建时参与计算/缓存的是合并后的定义
2) **ResolvableDependency（可解析但非 bean）**
   - 入口：`SpringCoreBeansResolvableDependencyLabTest`
   - 要点：可参与 autowiring，但不参与 bean 查找/生命周期/依赖图记录
3) **依赖关系记录（dependentBeanMap）**
   - 入口：`SpringCoreBeansBeanGraphDebugLabTest`
   - 要点：容器记录依赖关系；该记录影响销毁顺序与排障路径

## 5. 本章学习收获

完成本章学习后，读者应能够做到：

- 能够将 Bean 三层模型映射到固定断点入口：`refresh` / `doCreateBean` / `doResolveDependency` / merged definition
- 能够将现象描述转化为分层定位：定义层（是否注册/条件是否满足）→ 候选层（候选集合与收敛）→ 实例层（生命周期/代理替换）
- 能够在断点中通过固定监视列表获取证据，而非仅依赖日志推断（`mbd` / `descriptor` / 候选集合 / 三层缓存 / 依赖图）
- 能够通过 `*LabTest` 将问题限定为方法级最小复现，并给出下一步建议（章节入口与断点入口）

## 常见误区与边界

- 依赖图记录是 **beanName 级别** 的；ResolvableDependency 不是 beanName，因此不会以普通 bean 的方式出现在依赖图中。
- 在 BPP 链尚未完整时创建的 bean，不会被后续注册的 BPP 追溯处理；因此，创建时机是排障中的关键变量。
- 当观察到 early/raw 与 final/proxy 不一致时，应优先回到阶段边界与一致性约束进行解释，而不是仅在某个局部方法中寻找原因。

## 小结与下一章

本章给出了“现象 → 分层 → 入口断点 → 观察点 → 最小实验”的分析路径。若需进一步形成连续的源码叙事，可沿以下主线继续阅读：

- refresh 主线与创建主线：[`refresh()` → `doCreateBean()`](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md)
- 断点可复用清单：[`断点地图`](013-02-breakpoint-map.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansMergedBeanDefinitionLabTest` / `SpringCoreBeansResolvableDependencyLabTest`
- 测试入口文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`

上一章：[第 10 章：主线时间线：Spring Core Beans（IoC 容器）](010-03-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）](012-01-quickstart-30min.md)

<!-- BOOKIFY:END -->

## 复述训练（面试）

本章可作为面试复述训练的提纲。其目标并非机械复述概念，而是以可验证的证据链支撑结论：

1) **先给结论**：用一句话说明“它是什么/解决什么问题”。
2) **再给时机**：说明它发生在 refresh 的哪个阶段（definition/creation/after-init）。
3) **最后给证据链**：点名 1–2 个关键方法 + 3 个监视列表（watch list）要点 + 1 个可运行的 Lab。

建议将上述结构与 `appendix/93-interview-playbook.md` 的“标准结构”配合使用，并沿“现象 → 章节 → 断点 → Lab”的路径完成验证。

## 自检要点

应能够用 2–3 句话向他人解释：

1) 为什么本模块建议先运行 Lab，再阅读正文？
2) 当遇到注入失败/代理不生效/循环依赖时，如何沿“现象 → 章节 → 断点 → Lab”的路径完成一次可验证的分析？
3) 下一次学习/排障的入口应从何处进入（Knowledge Map / Debugger Pack / Branch Matrix）？
