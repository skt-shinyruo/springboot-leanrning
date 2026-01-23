# 第 11 章：00. 深挖指南：把“Bean 三层模型”落到源码与断点
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：深挖指南：把“Bean 三层模型”落到源码与断点
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 10 章：主线时间线：Spring Core Beans（IoC 容器）](010-03-mainline-timeline.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）](012-01-quickstart-30min.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**00. 深挖指南：把“Bean 三层模型”落到源码与断点**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansMergedBeanDefinitionLabTest` / `SpringCoreBeansResolvableDependencyLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResolvableDependencyLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

## 机制主线

如果你觉得 [01. Bean 心智模型](../part-01-ioc-container/020-01-bean-mental-model.md) 这类“概念章”看完仍然不过瘾，通常不是因为它讲错了，而是因为你已经进入下一阶段：

> 你需要把概念映射到**时间线（refresh 流程）**、**数据结构（BeanDefinition/缓存）**、以及**关键参与者（哪些类/哪些扩展点在起作用）**。

> 适用版本：本仓库基于 Spring Boot 3.x（对应 Spring Framework 6.x）。类名/方法名可能在小版本中微调，但主干结构非常稳定。

如果你希望用“一章”把 **`refresh()` 时间线** 与 **`doCreateBean()` 创建链路** 串成连续叙事（并落到关键方法/关键分支），建议先读这一篇主线章：

- [从 `refresh()` 到 `doCreateBean()`：把 Spring Bean “变成对象”的主线走通（源码级）](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md)

---

深挖最怕两件事：

- **跑太大（噪音淹没信号）**：直接 `spring-boot:run` 或跑全量测试，断点命中会爆炸，学习效率反而下降  
  - 对策：优先用本仓库的 `*LabTest` 把问题缩成“最小容器 + 最小现象 + 最小断言”
- **不分层（不知道自己在看什么对象）**：把“定义层/解析层/实例层”混在一起看，容易在巨大调用栈里迷路  
  - 对策：先问自己：我是在查 **BeanDefinition**（有没有注册）、还是查 **候选集合**（有哪些候选）、还是查 **最终暴露对象**（为什么是 proxy）？

跑一个测试类：

如果你是第一次进入本模块，建议先快后深：

1) **先快（30 分钟闭环）**：把 3 个最小实验跑通（命令/断点/观察点都给齐）  
   - 见：[01. 30 分钟快速闭环：先快后深（3 个最小实验入口）](012-01-quickstart-30min.md)
2) **再深（选 3 个“入口测试类”当导航锚点）**：每个类都对应一条主线与一个决定性分支  
   - `SpringCoreBeansLabTest`：从“现象”进（Qualifier/Scope/Lifecycle）  
     - 决定性分支：`doResolveDependency` 的候选收敛（Qualifier/Primary/name）  
   - `SpringCoreBeansContainerLabTest`：从“容器主线”进（定义 vs 实例、BFPP/BPP、FactoryBean、循环依赖边界）  
     - 决定性分支：`refresh` 时间线里 BFPP/BPP 的先后与单例创建时机  
   - `SpringCoreBeansBeanCreationTraceLabTest`：从“创建过程可视化”进（哪个阶段替换成 proxy）  
     - 决定性分支：`applyBeanPostProcessorsAfterInitialization` 里 `bean` → `result` 的第一次替换

配合断点地图一起用（把断点从“散点”收敛成“按阶段可复用清单”）：
- [02. 断点地图：容器主线可复用断点/观察点清单](013-02-breakpoint-map.md)

常用条件示例（以 `doCreateBean` 等方法里常见的 `beanName` 为例）：

- `beanName.equals("exampleBean")`
- `beanName.equals("alpha") || beanName.equals("beta")`
- `beanName.startsWith("org.springframework.")`（反向过滤：排除 Spring 自己的）

### 0.3 先跑一遍“First Pass”（不新增章节文件版）

如果你只想快速建立“阶段感”，可以按下面顺序做一次 First Pass（不追求背源码，只追求抓住抓手）：

1) 跑通快启 3 个实验（见 01 章），确保你能用断点证明“候选收敛 / prototype 注入陷阱 / proxy 替换”
2) 在 `AbstractApplicationContext#refresh` 打一个断点，只看 4 个关键节点：  
   - `invokeBeanFactoryPostProcessors`（定义层稳定下来）  
   - `registerBeanPostProcessors`（实例层增强点进入容器）  
   - `preInstantiateSingletons`（非 lazy 单例开始批量创建）  
   - `finishRefresh`（容器就绪）
3) 选一个你最关心的现象（注入歧义/生命周期/代理），把它缩成一个方法级 `-Dtest=Class#method` 入口，再去读对应章节

---

## 1. 深挖时最容易迷路的点（以及正确抓手）

### 1.0 症状驱动导航：遇到现象先选对入口（章节 → 断点 → Lab）

深挖最怕的不是“看不懂源码”，而是**选错入口**：入口错了，你会在巨大调用栈里看一整晚，最后只得到一句“Spring 好复杂”。

这张表的目标是：把“现象”直接映射到**层次（定义/候选/实例）**，再映射到**章节入口/断点抓手/Lab 证据链**。

| 现象（你看到的） | 先判断你卡在哪一层 | 推荐章节入口（最短路径） | 建议断点（抓住分支） | 对应 LabTest（证据链） |
|---|---|---|---|---|
| Bean 根本没注册出来（扫描/导入/`@Bean` 像没生效） | 定义层（BeanDefinition 图没长出来） | [18. refresh→doCreateBean 主线](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md)<br>[12. 注解为何生效（bootstrap）](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md) | `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`<br>`ConfigurationClassPostProcessor#processConfigBeanDefinitions` | `SpringCoreBeansRegistryPostProcessorLabTest`<br>`SpringCoreBeansBootstrapInternalsLabTest` |
| `@Autowired` 为 null / `@PostConstruct` 没跑（尤其是自己 new BeanFactory） | 实例层（规则没装上：BPP 缺失） | [12. 注解为何生效（bootstrap）](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md) | `PostProcessorRegistrationDelegate#registerBeanPostProcessors`<br>`AutowiredAnnotationBeanPostProcessor#postProcessProperties`<br>`InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization` | `SpringCoreBeansBootstrapInternalsLabTest`<br>`SpringCoreBeansBeanFactoryApiLabTest` |
| 启动即报错 vs 第一次 `getBean` 才报错 | refresh 第 9 步 vs on-demand | [18. refresh→doCreateBean 主线](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md) | `DefaultListableBeanFactory#preInstantiateSingletons`<br>`AbstractBeanFactory#doGetBean` | `SpringCoreBeansPreInstantiationLabTest` |
| 单个注入歧义（NoUniqueBeanDefinitionException） | 候选层（候选集合收敛失败） | [33. 候选选择与顺序](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md) | `DefaultListableBeanFactory#doResolveDependency` | `SpringCoreBeansInjectionAmbiguityLabTest`<br>`SpringCoreBeansAutowireCandidateSelectionLabTest` |
| 循环依赖：setter 能救、constructor 就死；或 early reference 形态奇怪 | 实例层（创建窗口 + 缓存/early reference） | [18. refresh→doCreateBean 主线](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md)<br>[16. early reference 与循环依赖](../part-03-container-internals/16-early-reference-and-circular.md) | `DefaultSingletonBeanRegistry#getSingleton/addSingletonFactory`<br>`AbstractAutowireCapableBeanFactory#doCreateBean`（earlySingletonExposure） | `SpringCoreBeansCircularDependencyBoundaryLabTest`<br>`SpringCoreBeansEarlyReferenceLabTest` |
| 代理不生效 / 自调用绕过 / 你以为有 AOP 但没有 | 实例层（BPP 包装阶段） | [31. BPP 如何把 Bean 换成 Proxy](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md) | `BeanPostProcessor#postProcessAfterInitialization` | `SpringCoreBeansProxyingPhaseLabTest` |
| `getBean(\"x\")` 拿到的不是你以为的类型，`&x` 又变了 | 实例层（FactoryBean 语义） | [08. FactoryBean：& 前缀与产品对象](../part-01-ioc-container/08-factorybean.md)<br>[18. refresh→doCreateBean 主线](../part-03-container-internals/18-refresh-to-bean-creation-mainline.md) | `AbstractBeanFactory#getObjectForBeanInstance` | `SpringCoreBeansFactoryBeanDeepDiveLabTest` |

你不需要一次性把表背下来，但建议你每次排障都强制自己走一遍“表的流程”：

1. 我现在看到的是定义层问题、候选层问题，还是实例层问题？
2. 我应该把断点打在 refresh 的哪个阶段？
3. 我能用哪个 `*LabTest` 把现象固化成可回归断言？

### 1.1 不要试图“背源码”，要建立 3 条主线

1) **时间线（发生顺序）**：容器从 `refresh()` 开始，按阶段推进  
2) **定义到实例的映射**：`BeanDefinition` →（合并/增强）→ 实例创建与注入  
3) **扩展点介入点**：BDRPP/BFPP/BPP 分别在“多早、改什么、会影响谁”

---

## 2. 一张“最小源码导航图”（建议贴在脑子里）

把 [01. Bean 心智模型](../part-01-ioc-container/020-01-bean-mental-model.md) 的三层模型落到源码，最小可以这么映射：

### 2.1 定义层：BeanDefinition 进来、存起来、被调整

- **容器与注册表**：`BeanDefinitionRegistry`（接口语义）  
- **最常见实现**：`DefaultListableBeanFactory`（既是 `BeanFactory` 也是 Registry）
- **定义对象**：`BeanDefinition` / `RootBeanDefinition`（实际项目里你常看到的是 Root）

定义层最关键的“会动定义”的入口：

- `BeanDefinitionRegistryPostProcessor`（BDRPP）：能“新增/删除/改名/再注册”定义
- `BeanFactoryPostProcessor`（BFPP）：能“改已有定义的配方/属性”
- `ConfigurationClassPostProcessor`：把 `@Configuration/@Bean/@Import` 解析成一堆定义（属于 BFPP 阵营）

对应章节：

- [02. 注册入口](../part-01-ioc-container/02-bean-registration.md)
- [06. PostProcessor 概览](../part-01-ioc-container/017-06-post-processors.md)
- [12. 注解为什么能工作（基础设施处理器）](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)
- [13. BDRPP](../part-03-container-internals/13-bdrpp-definition-registration.md)

### 2.2 实例层：createBean → 注入 → 初始化 → 可能被代理

- `createBean(...)`：创建入口（会走各种策略/短路）
- `doCreateBean(...)`：核心主流程（实例化 → 属性填充 → 初始化）
- `populateBean(...)`：属性填充（依赖注入最“肉”的地方）
- `initializeBean(...)`：初始化（aware、before-init、init-method、after-init）

实例层最关键的“会动实例/替换实例”的入口：

- `InstantiationAwareBeanPostProcessor`：能影响实例化/属性填充（例如决定构造器、提前返回代理等）
- `BeanPostProcessor`：before/after init 可包装代理（你看到的 AOP/事务代理基本都在这里出现）

对应章节：

- [30. 注入发生在什么时候（field vs constructor）](../part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md)
- [15. 实例化前短路](../part-03-container-internals/15-pre-instantiation-short-circuit.md)
- [31. BPP 如何把 Bean 换成 Proxy](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)

### 2.3 缓存与“循环依赖能不能救”：单例的三层缓存

循环依赖/提前暴露/early reference 这条线，如果你只记“三级缓存”会很虚；建议你把它落到这几个对象上：

- `DefaultSingletonBeanRegistry`：单例缓存与 early reference 的核心所在
- 关键动作：`getSingleton(...)`、提前暴露（singletonFactory）、early reference 生成

对应章节：

- [09. 循环依赖概览](../part-01-ioc-container/09-circular-dependencies.md)
- [16. early reference 与循环依赖：getEarlyBeanReference](../part-03-container-internals/16-early-reference-and-circular.md)

### 2.4 深挖时最容易忽略的三件事（但它们决定了你看到的“真相”）

很多“看起来像魔法”的现象，其实不是藏在 `doCreateBean(...)` 里，而是藏在下面三件事里：

对应章节：

- [11. 调试与自检：先分层再下断点](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)
- [12. 容器启动与基础设施：注解为何生效](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)
- [14. Post-Processor 顺序：PriorityOrdered/Ordered 分段算法](../part-03-container-internals/14-post-processor-ordering.md)
- [35. MergedBeanDefinition：最终生效配方](../part-04-wiring-and-boundaries/35-merged-bean-definition.md)
- [16. early reference 与循环依赖：三层缓存与时机](../part-03-container-internals/16-early-reference-and-circular.md)

---

- refresh 时间线：`beanFactory.getBeanDefinitionCount()`、`beanFactory.getBeanPostProcessorCount()`；以及（展开对象看缓存）`singletonObjects.size()` / `earlySingletonObjects.size()` / `singletonFactories.size()`
- 创建链路：`beanName`、`mbd`（RootBeanDefinition）、`bw.getWrappedInstance()`（当前实例）、`pvs`（属性值集合）
- 依赖解析：`DependencyDescriptor`（注入点类型/泛型/注解）、候选集合（按类型查出来的 beanNames）、最终选中的 beanName
- 单例缓存：`singletonObjects / earlySingletonObjects / singletonFactories` 的大小变化

这些观察点能帮你回答：

- “我现在看到的是 **定义层**（BeanDefinition）还是 **实例层**（对象）？”
- “我现在处在 refresh 的哪个阶段？”
- “为什么它会被提前暴露 / 为什么最后注入的是代理？”

### 3.1 看清“启动时间线”：从 refresh 开始

- `AbstractApplicationContext#refresh`
- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
- `DefaultListableBeanFactory#preInstantiateSingletons`

你会建立一个非常关键的直觉：

- “**先把 PostProcessor 装好**，再批量创建单例”
- 很多“魔法”其实都发生在“创建任何业务 bean 之前”

### 3.2 看清“一个 Bean 的诞生”：从 doCreateBean 切入

观察重点：

- 什么时候创建原始对象
- 什么时候开始做依赖注入
- 什么时候触发 `@PostConstruct` / `InitializingBean`
- 什么时候 BPP 可能把实例换成代理

### 3.3 看清“DI 为什么选它”：从 resolveDependency 切入

- `DefaultListableBeanFactory#doResolveDependency`
- `AutowiredAnnotationBeanPostProcessor#postProcessProperties`

观察重点：

- `DependencyDescriptor` 描述了“注入点是什么”（字段/参数/泛型信息）
- 候选集合是怎么来的（按类型查、再按 qualifier/primary 等规则收敛）

对应章节：

- [03. 依赖注入解析](../part-01-ioc-container/014-03-dependency-injection-resolution.md)
- [33. 候选选择与顺序](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)

### 3.4 看清“最终生效的定义长什么样”：从 merged BeanDefinition 切入

如果你在 `doCreateBean` 里看到一个 `RootBeanDefinition`，但你不确定它从哪来、为什么它和 registry 里的定义不一样：

建议你从 merged definition 入口切：

固定观察点（强烈建议你每次都看同一组）：

- `beanName`
- `mbd`（`RootBeanDefinition`）
- `mbd.getPropertyValues()`（有哪些 property values？来自 parent 还是 child？）
- `mbd.getInitMethodName()` / `mbd.getDestroyMethodName()`（生命周期元数据在哪里合并出来？）
- `mbd.getResolvedTargetType()`（类型在什么时候“变得更具体”？）

### 3.5 看清“能注入但不是 Bean”：从 ResolvableDependency 切入

当你遇到“这个类型能注入，但 `getBean(type)` 拿不到”时，很多时候不是注册阶段出错，而是你命中了另一条解析通道：

- `DefaultListableBeanFactory#registerResolvableDependency`（把类型 -> 值 放进 resolvableDependencies）
- `DefaultListableBeanFactory#doResolveDependency`（依赖解析主流程，会优先命中 resolvableDependencies）
- `AbstractBeanFactory#doGetBean`（`getBean(type)` 的路径，不会命中 resolvableDependencies）

固定观察点：

- `descriptor`（`DependencyDescriptor`：注入点的抽象）
- `descriptor.getDependencyType()`（注入点要的到底是什么类型？）
- `resolvableDependencies`（Map 里有哪些条目？你的类型是否在里面？）
- “没命中后走到哪里”：`findAutowireCandidates(...)`（按 bean 候选集查）

### 3.6 看清“依赖图怎么记录/为什么销毁顺序是那样”：从 dependentBeanMap 切入

你在排障时经常想回答：

- “到底谁依赖谁？”
- “为什么 stop/destroy 先停它再停我？”

建议从依赖关系记录的入口切入：

- `DefaultSingletonBeanRegistry#registerDependentBean`（记录 `A depends on B`）
- `DefaultSingletonBeanRegistry#getDependenciesForBean`（从 beanName 反查依赖）
- `DefaultSingletonBeanRegistry#getDependentBeans`（反向：谁依赖我）

---

## 4. 推荐的“深挖练习”（每个练习都能在 30 分钟内闭环）

### 练习 0：把“容器主线时间线”走通一次（强烈推荐作为第一条）

这个练习跑通后，你再去看 [06. Post Processors](../part-01-ioc-container/017-06-post-processors.md) / [12. 容器启动与基础设施](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md) / [14. Post-Processor 排序](../part-03-container-internals/14-post-processor-ordering.md) / [31. 代理发生的阶段](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md) / [16. 提前引用与循环依赖](../part-03-container-internals/16-early-reference-and-circular.md) / [15. 预实例化短路](../part-03-container-internals/15-pre-instantiation-short-circuit.md)，会非常顺。

### 练习 A：从 BeanDefinition 到实例（最适合刚开始深挖）

目标：你要能用自己的话把“定义层”与“实例层”串起来。

### 练习 B：让注入阶段“可见”（最适合理清阶段感）

目标：建立“构造器阶段 vs 属性填充阶段”的手感，不再把 `@Autowired` 当成“构造时就有”。

### 练习 C：看一次 early reference（最适合理清循环依赖到底怎么救）

按 [16 章](../part-03-container-internals/16-early-reference-and-circular.md) 的思路，在 `DefaultSingletonBeanRegistry#getSingleton(...)` 观察 early reference 的生成与使用。

目标：能解释“为什么 setter 循环有时能成、构造器循环必死”背后的缓存与时机。

### 练习 D：把 merged 看出“手感”（RootBeanDefinition 从哪里来）

目标：你要能解释清楚：

- registry 的 child definition 为什么看不到 parent 的元数据
- merged `RootBeanDefinition` 到底把哪些信息合并进来了
- 为什么要有 `MergedBeanDefinitionPostProcessor` 这种“提前准备/缓存元数据”的 hook

### 练习 E：用 bean graph 输出复盘一次“为什么注入的是它”

目标：你要能用“排障流程语言”复盘一次：

- 候选集合是什么
- 为什么最后选中的是 `@Primary`
- 为什么依赖关系里不会出现 `secondaryWorker`

### 练习 F：看一次 ResolvableDependency 命中（能注入但不是 bean）

目标：你要能解释清楚：

- 为什么它参与 autowiring，但不参与 bean 查找/生命周期/依赖图记录
- 这类机制适合框架内部，不适合放业务对象

---

## 5. 读完本章你应该获得什么

如果本章有效，你应该能做到：

- 能把“Bean 三层模型”落到 **4 个固定断点入口**：`refresh` / `doCreateBean` / `doResolveDependency` / `getMergedBeanDefinition`
- 能把任何“现象描述”翻译成“分层排障问题”：定义层（有没有注册/条件是否满足）→ 解析层（候选集合与收敛）→ 实例层（生命周期/代理替换）
- 能在断点里用固定观察点拿证据，而不是靠日志碰运气（`mbd` / `descriptor` / `matchingBeans` / 三层缓存 / 依赖图）
- 能用 `*LabTest` 把问题缩到最小复现（方法级入口），并给出下一步该读的章节/该下的断点
- 遇到“为什么是 proxy/为什么注解不生效/为什么循环依赖救不回来”这类问题时，至少知道从哪一章与哪个入口开始

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBeanGraphDebugLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test`

本章不新增概念，而是给你一套“深挖路线”，让你能在 IDE 里通过断点和测试，把容器行为看成**可追踪的过程**。

## 0. 准备工作：把“深挖”做成可重复的实验

- **跑太大**：直接 `spring-boot:run` 看全量启动，断点会炸，信息密度反而低
- **看太散**：没有固定入口与观察点，今天看 A、明天看 B，很难形成“可复用的路径”

本模块的设计是：用 **JUnit 测试当最小启动器**，把你要看的机制切成可闭环的实验。

### 0.1 推荐运行方式（命令行也能精确到方法）

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test
```

只跑一个测试方法（建议用于断点深挖）：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest#beanDefinitionIsNotTheBeanInstance test
```

> 你也可以直接在 IDE 里右键运行某个 `@Test` 方法；命令行写法的价值在于“可复制、可分享、可复现”。

### 0.2 一个高收益习惯：所有断点都先加“降噪条件”

容器内部会创建大量基础设施 bean（processors、internal bean、代理相关 bean）。  
如果你不加条件断点，很容易被“无关的命中次数”淹没。

如果你现在的目标是“先把主线跑通”，而不是立刻深挖每个细节，可以按下面 10 个最小实验走一遍。

约束很简单：**每一步只要求你写 1–2 句可复述结论**（定义层/实例层/时机/顺序/断点入口），不要求背源码。

运行方式（推荐精确到方法，噪声最小）：

```bash
mvn -pl :spring-core-beans -Dtest=<TestClass>#<testMethod> test
```

10 个最小实验清单（按“先建立阶段感，再补边界”的顺序）：

1) 定义层 vs 实例层：`BeanDefinition != bean instance`  
   - 入口：`SpringCoreBeansContainerLabTest#beanDefinitionIsNotTheBeanInstance`  
   - 你要写：两句话说明“定义层/实例层”，以及“为什么最终 `getBean()` 可能不是原始实例（proxy）”

2) refresh 6 步粗粒度流程图（1 行/步即可）  
   - 入口：任意能 refresh 的最小 Lab（同上即可）  
   - 你要画：注册定义 → BFPP/BDRPP → 注册 BPP → 创建单例 → 收尾事件（抓住阶段边界即可）

3) BeanDefinition 从哪里来（列 3 条入口 + 各 1 句定位方法）  
   - 入口：阅读 [02](../part-01-ioc-container/02-bean-registration.md) + 跑 `SpringCoreBeansBootstrapInternalsLabTest`  
   - 你要写：扫描 / `@Bean` / `@Import`（selector/registrar）各自如何落到 registry

4) 注入歧义：用 `@Primary` 与 `@Qualifier` 各修一次并解释差异  
   - 入口：`SpringCoreBeansInjectionAmbiguityLabTest`

5) 候选选择 vs 排序/链路：别把 `@Order` 当“选谁注入”  
   - 入口：`SpringCoreBeansAutowireCandidateSelectionLabTest`  
   - 你要写：`@Primary/@Priority/@Order` 哪些影响“单依赖选择”，哪些影响“集合/链路顺序”

6) prototype 注入 singleton 的坑：为什么“看起来像单例”？给出 1 个修复方案  
   - 入口：`SpringCoreBeansLabTest`（prototype 相关用例）

7) 生命周期：`@PostConstruct` 触发时机 + prototype 销毁语义（各 1 句）  
   - 入口：`SpringCoreBeansLifecycleCallbackOrderLabTest`

8) 三类 post-processor：BDRPP / BFPP / BPP 各自“改什么/什么时候改”  
   - 入口：`SpringCoreBeansRegistryPostProcessorLabTest` + `SpringCoreBeansPostProcessorOrderingLabTest`

9) early reference：哪类循环依赖可能被缓解？构造器循环为何通常无解？  
   - 入口：`SpringCoreBeansEarlyReferenceLabTest`

10) 排障闭环：从异常信息找到 1 个“最有效”的断点入口，并解释为什么  
   - 入口：`SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansBeanGraphDebugLabTest`  
   - 你要写：注入失败优先看 `doResolveDependency`；代理/替换优先看 `doCreateBean`/`postProcessAfterInitialization`

如果你在断点里不知道“下一步该去哪”，就回到这三条主线：你当前是在时间线的哪个阶段？你在看定义还是实例？有哪些扩展点可能在改它？

1) **Merged `RootBeanDefinition`**：registry 里保存的是“原始定义”，创建时真正参与计算/缓存的是“合并后的定义”  
   - 你在断点里经常看到的 `RootBeanDefinition`，往往来自 `getMergedLocalBeanDefinition(...)`  
   - 这也解释了：为什么你 `getBeanDefinition(beanName)` 看不到某些元数据，但创建时又“都有了”
2) **ResolvableDependency（可解析但非 bean）**：有些依赖参与 autowiring，但它根本不是 BeanDefinition 注册的 bean  
   - 典型例子：`BeanFactory` / `ApplicationContext` 等“容器对象”为什么能注入？
3) **依赖关系记录（dependentBeanMap）**：容器会记录“谁依赖谁”，它影响：
   - 销毁顺序（谁先 stop / 谁后 destroy）
   - 你能否用 `getDependenciesForBean(beanName)` 复盘“依赖图”

- [35. BeanDefinition 的合并（MergedBeanDefinition）](../part-04-wiring-and-boundaries/35-merged-bean-definition.md)
- [20. registerResolvableDependency：能注入但它不是 Bean](../part-04-wiring-and-boundaries/20-resolvable-dependency.md)
- [19. dependsOn：强制初始化顺序（依赖关系记录）](../part-04-wiring-and-boundaries/19-depends-on.md)
- [11. 调试与自检：异常 → 断点入口 + bean graph](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)

## 3. 断点清单：你想“看见什么”，就打哪一类断点

本仓库已经把很多点做成了可运行的 Labs；建议你用“测试 + 断点”方式学习（比跑 Boot 全量启动更聚焦）。

### 3.0 断点怎么打才不痛苦：位置 + 条件 + 观察点

如果你只记一句话：**断点不是“打在入口”，而是“打在你能拿到关键信息的那一行”**。

建议你给每类断点配一组固定观察点（watch / evaluate）：

建议断点（看整体阶段划分，不建议一步步单步到天荒地老）：

建议断点：

建议断点：

对应章节与可跑实验：

- [35. BeanDefinition 的合并（MergedBeanDefinition）](../part-04-wiring-and-boundaries/35-merged-bean-definition.md)
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`

对应章节与可跑实验：

- [20. registerResolvableDependency：能注入但它不是 Bean](../part-04-wiring-and-boundaries/20-resolvable-dependency.md)
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResolvableDependencyLabTest.java`

对应章节与可跑实验：

- [11. 调试与自检：异常 → 断点入口 + bean graph](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java`
- [19. dependsOn：强制初始化顺序](../part-04-wiring-and-boundaries/19-depends-on.md)

1) 只跑一个测试方法（它把阶段用 events 固化成了断言）：
   - `SpringCoreBeansBeanCreationTraceLabTest.beanCreationTrace_recordsPhases_andExposesProxyReplacement()`
2) 建议断点（够用版）：
   - `AbstractApplicationContext#refresh`（从主线入口开始认路）
   - `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`（BFPP/BDRPP 在哪里发生）
   - `AbstractAutowireCapableBeanFactory#doCreateBean`（实例创建主线）
   - `AbstractAutowireCapableBeanFactory#populateBean`（注入发生点）
   - `AbstractAutowireCapableBeanFactory#initializeBean`（Aware/BPP/init callbacks）
   - `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`（最常见的 proxy/替换发生点）
3) 你应该得到的“阶段感”：
   - instantiate → populate → initialize 的顺序是稳定的
   - BPP 的返回值会成为“最终暴露对象”（因此类型/行为可能变化）

1) 运行 `spring-core-beans` 的测试（只跑一个类也行）
2) 在 `SpringCoreBeansContainerLabTest.beanDefinitionIsNotTheBeanInstance()` 断点  
3) 顺着 `getBean(...)` 看一次 `createBean → doCreateBean`

直接按 [30 章](../part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md) 给的断点走一遍。

1) 只跑一个测试方法：
   - `SpringCoreBeansMergedBeanDefinitionLabTest.mergedBeanDefinition_combinesParentAndChildMetadata_andTriggersMergedDefinitionPostProcessor()`
2) 断点打在：
   - `AbstractBeanFactory#getMergedLocalBeanDefinition`
   - `AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`
3) 条件断点建议：`beanName.equals("childBean")`

1) 运行：`SpringCoreBeansBeanGraphDebugLabTest.dumpBeanGraph_candidatesAndRecordedDependencies_helpTroubleshootWhyItsInjected()`
2) 断点打在：
   - `DefaultListableBeanFactory#doResolveDependency`（看候选集合、看最终选择）
   - `DefaultSingletonBeanRegistry#registerDependentBean`（看依赖边什么时候被记录）
3) 对照输出：`BeanGraphDumper.dumpCandidates(...)` 与 `BeanGraphDumper.dumpDependencies(...)`

1) 运行：`SpringCoreBeansResolvableDependencyLabTest.registerResolvableDependency_enablesAutowiringWithoutRegisteringABean()`
2) 断点打在：
   - `DefaultListableBeanFactory#registerResolvableDependency`（看它进了哪张表）
   - `DefaultListableBeanFactory#doResolveDependency`（看它如何“优先命中”并返回 instance）
   - `AbstractBeanFactory#doGetBean`（看 `getBean(type)` 为什么必然失败）

- 遇到“为什么它被代理/为什么注入的是它/为什么它没注册”，不再只能靠猜
- 能把问题定位到：定义层（注册/条件/顺序）还是实例层（注入/代理/回调）
- 能用断点验证自己的推理，而不是靠日志碰运气

回到概念章继续读也不会“太简单”了：因为你已经知道每个概念在源码里对应哪里、能怎么证明。
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
推荐断点：`AbstractApplicationContext#refresh`、`AbstractAutowireCapableBeanFactory#doCreateBean`、`DefaultListableBeanFactory#doResolveDependency`

## 常见坑与边界

注意：依赖图是 **beanName 级别** 的记录。  
如果你的依赖来自 ResolvableDependency，它不是 beanName，因此不会像普通 bean 一样出现在依赖图里。

## 小结与下一章

实例层的骨架几乎都在 `AbstractAutowireCapableBeanFactory` 一条链路上：

- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `AbstractAutowireCapableBeanFactory#populateBean`
- `AbstractAutowireCapableBeanFactory#initializeBean`

- `AbstractBeanFactory#getMergedLocalBeanDefinition`（合并入口/缓存入口）
- `AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`（merged-definition hook 的触发点）
- `MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition`（谁在“提前准备/缓存”元数据）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansMergedBeanDefinitionLabTest` / `SpringCoreBeansResolvableDependencyLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResolvableDependencyLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. Bean 心智模型：BeanDefinition vs Bean 实例](../part-01-ioc-container/020-01-bean-mental-model.md)

<!-- BOOKIFY:END -->
