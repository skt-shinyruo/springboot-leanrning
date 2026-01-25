# 93. 面试复述模板：用“可证明的主线”回答 Spring Beans

> 本章目标：把 `spring-core-beans` 变成你的“答题脚本”，但不是靠背诵——而是靠**可运行证据 + 可断点证明**。  
> 面试里你只要能把“现象 → 主线阶段 → 关键分支 → 证据链入口（Lab）”说清楚，就已经超过大多数候选人。

---

## 0. 一句话总纲（通用开场，建议背下来）

> Spring 的 Bean 机制可以用“三层模型”统一：  
> **定义层（BeanDefinition）→ 实例层（create/populate/initialize）→ 最终暴露对象（可能是 proxy/early reference）**。  
> 面试里所有“为什么注入的是它/为什么会 proxy/为什么循环依赖有时能救”，都能落在这三层的某个阶段。

你可以顺手补一句“可证明”：

> 我一般会用一个最小可跑测试把现象复现出来，然后从 `refresh → doCreateBean` 的调用链下断点确认分支条件。

---

## 1. 通用答题结构（30 秒 / 2 分钟 / 深挖加分）

### 1.1 30 秒版本（先把框架立住）

1) 先落到三层模型（你在定义层/实例层/最终对象层）  
2) 给出关键术语（BeanDefinition / BPP / resolveDependency / proxy / early reference）  
3) 给出一个典型坑（让面试官相信你踩过坑）  

### 1.2 2 分钟版本（把主线跑通）

1) 给出主线阶段：refresh 的哪一段？createBean 的哪一段？  
2) 给出关键分支：候选收敛/排序/early reference/是否被 BPP 包装  
3) 给出 2 个断点 + 3 个 watchpoints（“我能证明我说的”）  

### 1.3 深挖加分（你能说源码名，而不是说“源码很复杂”）

- 指出 1 个“算法级入口”：例如 `PostProcessorRegistrationDelegate` 的排序/分段  
- 指出 1 个“数据结构”：例如三级缓存三表  
- 指出 1 个“边界”：例如 `@Order` 解决不了单依赖歧义  

---

## 2. 高频题库（按主题直接套用）

### 2.1 依赖注入（DI）：为什么注入的是它？

**30 秒版本**

- 先按类型收集候选（Map<beanName, candidate>）  
- 再按规则收敛候选：Qualifier → name 匹配 → Primary → Priority/Ordered（部分场景）  
- 收敛成 1 个就注入；收敛不下来就 fail-fast（NoUnique）

**2 分钟版本（源码主线）**

- 入口：`DefaultListableBeanFactory#resolveDependency`  
- 候选收集：`findAutowireCandidates`  
- 收敛规则：`determineAutowireCandidate` + `QualifierAnnotationAutowireCandidateResolver`

**你可以怎么证明（Lab + 断点）**

- 推荐 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest`
- 推荐断点：
  - `DefaultListableBeanFactory#resolveDependency`
  - `DefaultListableBeanFactory#determineAutowireCandidate`
- 推荐观察点：
  - 候选集合（beanName → candidate）
  - 触发收敛的注解（Qualifier/Primary）
  - `DependencyDescriptor`（注入点类型/泛型/required）

对应章节：
- DI 主线：[`014-03-dependency-injection-resolution.md`](../part-01-ioc-container/014-03-dependency-injection-resolution.md)
- 候选收敛边界：[`33-autowire-candidate-selection-primary-priority-order.md`](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)

---

### 2.2 BFPP / BDRPP / BPP：为什么 `@Autowired/@PostConstruct` 能生效？为什么有时不生效？

**30 秒版本**

- **BDRPP/BFPP** 发生在定义层：能改 BeanDefinition（甚至注册新的定义）  
- **BPP** 发生在实例层：能改实例，甚至替换成 proxy（最终暴露对象层）  
- 很多“注解为什么生效”的答案是：**对应 processor 是否被注册、何时注册、是否执行到了**

**2 分钟版本（主线定位）**

- `refresh()` 里先 invoke BFPP/BDRPP，再 register BPP，再开始创建单例  
- 所以“processor 的注册时机”决定了“注解是否生效”

**你可以怎么证明（Lab + 断点）**

- 推荐 Lab：`SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest`
- 推荐断点：
  - `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
- 推荐观察点：
  - PriorityOrdered/Ordered/Unordered 三段列表
  - 哪些 processor 是 infrastructure role

对应章节：
- Post-Processors：[`017-06-post-processors.md`](../part-01-ioc-container/017-06-post-processors.md)
- 排序算法：[`14-post-processor-ordering.md`](../part-03-container-internals/14-post-processor-ordering.md)

---

### 2.3 生命周期：创建/注入/初始化/销毁（以及回调顺序）

**30 秒版本**

- 创建主线：instantiate → populate → initialize  
- `@PostConstruct` 属于 initialize 阶段（依赖 BPP）  
- prototype 默认不走容器销毁（除非你显式 destroy）

**2 分钟版本（主线与边界）**

- populate 里做依赖注入（字段/Setter/`@Value` 等）  
- initialize 里做 Aware/BeforeInit/Init/AfterInit（并且这里可能被 BPP 换壳）

**你可以怎么证明**

- 推荐 Lab：`SpringCoreBeansLifecycleCallbackOrderLabTest`
- 推荐断点：
  - `AbstractAutowireCapableBeanFactory#populateBean`
  - `AbstractAutowireCapableBeanFactory#initializeBean`
- 推荐观察点：
  - init method 选择（接口 vs 注解 vs 自定义）
  - Before/After init BPP 链

对应章节：
- 生命周期与回调：[`016-05-lifecycle-and-callbacks.md`](../part-01-ioc-container/016-05-lifecycle-and-callbacks.md)
- 回调顺序对照：[`17-lifecycle-callback-order.md`](../part-03-container-internals/17-lifecycle-callback-order.md)

---

### 2.4 循环依赖：为什么 constructor 无解、setter 有时能救？

**30 秒版本**

- constructor 循环：需要“先有对象才能注入”，因此无解  
- setter 循环：单例创建时存在 early exposure 窗口（三级缓存），可能救回来  
- 代理介入时 early reference 可能变成 proxy，行为会变

**2 分钟版本（给出缓存三表）**

- singletonObjects：完成品  
- earlySingletonObjects：早期引用  
- singletonFactories：提供早期引用的工厂（是否返回 proxy 是关键边界）

**你可以怎么证明**

- 推荐 Lab：`SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
- 推荐断点：
  - `DefaultSingletonBeanRegistry#getSingleton`
  - `DefaultSingletonBeanRegistry#addSingletonFactory`
- 推荐观察点：
  - 三表内容变化
  - 何时触发 early reference

对应章节：
- 循环依赖概览：[`09-circular-dependencies.md`](../part-01-ioc-container/09-circular-dependencies.md)
- early reference 深挖：[`16-early-reference-and-circular.md`](../part-03-container-internals/16-early-reference-and-circular.md)

---

### 2.5 Spring Boot 自动装配：为什么有/为什么没有/为什么没退让？

**30 秒版本**

- auto-config 本质是：配置导入（`@Import`）+ 条件评估（`@Conditional...`）+ bean 注册  
- 条件评估发生在注册阶段（refresh 前半段），不是看“最终容器状态”  
- back-off 要求“覆盖 bean 在评估时可见”，否则会出现重复候选/注入失败

**你可以怎么证明**

- 推荐 Lab：`SpringCoreBeansAutoConfigurationOrderingLabTest`（顺序）/ `SpringCoreBeansAutoConfigurationBackoffTimingLabTest`（退让时机）
- 推荐断点：
  - `AutoConfigurationImportSelector#getAutoConfigurationEntry`
  - `ConditionEvaluator#shouldSkip`
- 推荐观察点：
  - 候选列表（排序/过滤前后）
  - 条件匹配结果（match/mismatch）

对应章节：
- 自动配置主线：[`021-10-spring-boot-auto-configuration.md`](../part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)
- 顺序：[`020-09-auto-config-ordering.md`](../part-02-boot-autoconfig/020-09-auto-config-ordering.md)

---

### 2.6 AOT/Native：RuntimeHints（构建期契约）

**30 秒版本**

- AOT/Native 的关键是把“运行期反射/代理/资源需求”前移为“构建期契约”  
- Spring 用 RuntimeHints 表达这种契约：reflection/proxy/resource 等  
- 你可以在 JVM 单测里验证 hints 的存在性（不必构建 native image）

**你可以怎么证明**

- 推荐 Lab：`SpringCoreBeansAotRuntimeHintsLabTest`
- 推荐断点：`RuntimeHintsRegistrar#registerHints`

对应章节：
- AOT/Native：[`024-40-aot-and-native-overview.md`](../part-05-aot-and-real-world/024-40-aot-and-native-overview.md)
- RuntimeHints：[`41-runtimehints-basics.md`](../part-05-aot-and-real-world/41-runtimehints-basics.md)

---

### 2.7 真实世界加分：XML / 容器外对象 / SpEL / 自定义 Qualifier

你可以用一句话把它们归类：

> 它们都在“定义层输入”与“实例层托管边界”上：XML/Properties/Groovy 是定义来源；AutowireCapableBeanFactory 是容器外对象托管；SpEL/Qualifier 则直接影响注入解析。

常用断点：

- XML（定义层输入）：`XmlBeanDefinitionReader#loadBeanDefinitions`、`DefaultListableBeanFactory#registerBeanDefinition`
- 容器外对象：`AutowireCapableBeanFactory#autowireBean`、`initializeBean`、`destroyBean`
- SpEL / `@Value("#{...}")`：`StandardBeanExpressionResolver#evaluate`、`AbstractBeanFactory#resolveEmbeddedValue`
- 自定义 Qualifier：`QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`

对应章节：
- XML → BeanDefinitionReader：[`42-xml-bean-definition-reader.md`](../part-05-aot-and-real-world/42-xml-bean-definition-reader.md)
- 容器外对象注入：[`43-autowirecapablebeanfactory-external-objects.md`](../part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md)
- SpEL：[`44-spel-and-value-expression.md`](../part-05-aot-and-real-world/44-spel-and-value-expression.md)
- 自定义 Qualifier：[`45-custom-qualifier-meta-annotation.md`](../part-05-aot-and-real-world/45-custom-qualifier-meta-annotation.md)

---

## 3. 面试常见误区（你说出来就很加分）

1) **误区：`@Order` 能解决单依赖歧义**  
   - `@Order` 主要影响集合注入/链路顺序；单依赖收敛主要看 `@Qualifier/@Primary/name` 等规则。
2) **误区：`@PostConstruct` 是“Java 自带回调”**  
   - `@PostConstruct` 依赖 BPP（例如 CommonAnnotationBeanPostProcessor）；BPP 没注册/没执行时它不会发生。
3) **误区：循环依赖就是“三级缓存技巧”**  
   - 三表承载的是 early reference 的时机与语义；代理介入时 early reference 可能变成 proxy。
4) **误区：条件装配是看“最终容器状态”**  
   - 条件评估发生在注册阶段；back-off 要求“覆盖 bean 在评估时可见”。

---

## 4. 最小断点背诵清单（背 6 个就够）

- `AbstractApplicationContext#refresh`
- `AbstractBeanFactory#doGetBean`
- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `AbstractAutowireCapableBeanFactory#populateBean`
- `AbstractAutowireCapableBeanFactory#initializeBean`
- `DefaultListableBeanFactory#resolveDependency`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAotRuntimeHintsLabTest`
- Lab：`SpringCoreBeansAutoConfigurationBackoffTimingLabTest` / `SpringCoreBeansAutoConfigurationOrderingLabTest`
- Lab：`SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest`
- Lab：`SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest`
- Lab：`SpringCoreBeansProxyingPhaseLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest`
- Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
- Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest` / `SpringCoreBeansAutowireCapableBeanFactoryLabTest`
- Lab：`SpringCoreBeansSpelValueLabTest` / `SpringCoreBeansCustomQualifierLabTest`

上一章：[92. 知识点地图（Concept → Chapter → Lab）](92-knowledge-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[94. 生产排障清单（异常分型 → 入口 → 观察点 → 修复策略）](94-production-troubleshooting-checklist.md)

<!-- BOOKIFY:END -->
