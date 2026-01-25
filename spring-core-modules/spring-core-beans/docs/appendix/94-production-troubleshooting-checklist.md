# 94. 生产排障清单（异常分型 → 入口 → 观察点 → 修复策略）

> 目标：把 Spring Bean 排障从“靠经验”变成“固定 3 步收敛”。  
> 关键词：**分型（在哪一层）→ 复现（可跑证据）→ 断点（看变量证伪/证实）**。

## 本章怎么用（建议顺序）

1) 先过一遍 **0. 三步收敛**（这是本章真正的“工具”）  
2) 按异常类型跳到对应小节（NoSuch/NoUnique/BeanCreation/DefinitionStore…）  
3) 用“最小断点集合 + watch list”把问题收敛到 1–2 个关键分支  

---

## 0. 三步收敛（通用）

### Step 1：分型（Definition Layer vs Instance Layer vs Final Exposed Object）

你拿到异常栈后，第一件事不是搜日志、不是改配置，而是判断它发生在哪一层：

- **定义层（Definition Layer）**：读/解析/注册 `BeanDefinition` 失败  
  - 典型异常：`BeanDefinitionStoreException` / XML 解析异常 / 资源不存在  
  - 常见根因：输入（XML/资源/配置类）不对、占位符解析失败、同名覆盖策略冲突
- **实例层（Instance Layer）**：create → populate → initialize 过程中失败  
  - 典型异常：`BeanCreationException` / `UnsatisfiedDependencyException` / `BeanCurrentlyInCreationException`
- **最终暴露对象（Final Exposed Object）**：你拿到的对象不是“你写的那个类”，而是 proxy/early reference  
  - 典型现象：self-invocation 不生效、注入对象与最终对象不一致、类型匹配/泛型匹配表现反直觉

> 你不分型，就会在错误层面修 bug：例如把“没注册”当成“注入问题”，永远修不完。

### Step 2：选一个最小可跑入口（用 Lab 做证据）

排障最怕“我只能在生产复现”。在这个仓库里，你应该优先把问题映射到某个 Lab：

- 注入歧义：`SpringCoreBeansInjectionAmbiguityLabTest`
- 候选者收敛：`SpringCoreBeansAutowireCandidateSelectionLabTest`
- 生命周期/回调顺序：`SpringCoreBeansLifecycleCallbackOrderLabTest`
- 循环依赖/early reference：`SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
- 定义层（XML）：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`

### Step 3：用固定 watch list 收敛原因（看变量，不靠猜）

建议你每次排障都固定盯这组变量（它们分别回答一个关键问题）：

- `beanName`：当前正在处理哪个 bean（很多栈很深，先抓住“是谁”）
- `mbd`（merged bean definition）：这个 bean “最终定义”长什么样（很多扩展点会在这里写入信息）
- 候选集合：`Map<String, Object>` / `Map<String, BeanDefinition>`（为什么候选为空/太多）
- 注入点描述：`DependencyDescriptor`（required? 泛型? 注解? 注入方式?）
- post-processors 列表（顺序问题常见）：PriorityOrdered/Ordered/Unordered
- 单例缓存三表（循环依赖/early reference）：`singletonObjects` / `earlySingletonObjects` / `singletonFactories`

---

## 1) NoSuchBeanDefinitionException（没有候选）

### 常见根因（按概率排序）

1) **根本没注册（定义层没发生）**：扫描路径不对、配置类没进解析集合、`@Import` 没触发  
2) **条件没 match**：auto-config 条件失败（不是 bug，是条件不满足）  
3) **类型匹配失败**：泛型/FactoryBean product type/代理导致类型信息丢失

### 推荐断点

- `DefaultListableBeanFactory#findAutowireCandidates`（候选集合在哪里变成空）
- `ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry`（配置类解析/注册入口）

### 修复策略（两类）

1) **让候选出现**：修正扫描/导入/条件，使定义进入 registry  
2) **让匹配正确**：修正泛型、明确 FactoryBean product type、避免代理丢信息

对应章节（深入理解而不是“背异常名”）：
- 注册入口：[`02-bean-registration.md`](../part-01-ioc-container/02-bean-registration.md)
- 依赖解析主线：[`014-03-dependency-injection-resolution.md`](../part-01-ioc-container/014-03-dependency-injection-resolution.md)
- 泛型匹配坑：[`37-generic-type-matching-pitfalls.md`](../part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md)

---

## 2) NoUniqueBeanDefinitionException（候选太多）

### 常见根因

- 多实现同时存在，但注入点是“单依赖”（并且没有 `@Primary/@Qualifier`）  
- Boot 的 back-off 没生效（覆盖 bean 出现得太晚，条件评估时看不见它）

### 推荐断点

- `DefaultListableBeanFactory#findAutowireCandidates`（候选集合）
- `DefaultListableBeanFactory#determineAutowireCandidate`（收敛规则）
- `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`（Qualifier 匹配）

### 修复策略（两类）

1) **确定化选择**：`@Primary` / `@Qualifier`（让注入点确定）  
2) **让 back-off 生效**：覆盖 bean 必须在条件评估前可见（更干净）

对应章节：
- 候选者收敛规则：[`33-autowire-candidate-selection-primary-priority-order.md`](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)
- Boot 自动配置主线：[`021-10-spring-boot-auto-configuration.md`](../part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)

---

## 3) UnsatisfiedDependencyException（注入失败总包装）

> 它常常只是“外壳异常”，真正 root cause 在 cause 链更里面。

常见真实根因：

- NoSuch / NoUnique（最常见）
- 类型转换失败（`@Value` / populateBean）
- 依赖链上游创建失败（构造器异常、init 异常、BPP 包装异常）

推荐断点：
- `DefaultListableBeanFactory#doResolveDependency`
- `AutowiredAnnotationBeanPostProcessor#postProcessProperties`

观察点：
- `DependencyDescriptor`（required? 注解? 泛型信息?）
- 候选集合分支（空/不唯一）
- 值注入分支（suggested value / resolveEmbeddedValue）

对应章节：
- 值解析：[`34-value-placeholder-resolution-strict-vs-non-strict.md`](../part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md)
- 类型转换：[`36-type-conversion-and-beanwrapper.md`](../part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md)

---

## 4) BeanCreationException（创建链路失败）

### 常见根因（按阶段）

- **instantiate**：构造器抛异常 / FactoryMethod 抛异常  
- **populate**：依赖注入失败 / 类型转换失败 / `@Value` 解析失败  
- **initialize**：`@PostConstruct` / initMethod 抛异常 / BPP 包装抛异常  
- **final exposed object**：代理替换失败/短路导致的异常

推荐断点：
- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `AbstractAutowireCapableBeanFactory#populateBean`
- `AbstractAutowireCapableBeanFactory#initializeBean`

对应章节：
- 生命周期与回调：[`016-05-lifecycle-and-callbacks.md`](../part-01-ioc-container/016-05-lifecycle-and-callbacks.md)
- 代理/包装阶段：[`31-proxying-phase-bpp-wraps-bean.md`](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)

---

## 5) BeanDefinitionStoreException（定义层失败：读/解析/注册）

典型场景：

- XML 非法 / schema 不匹配
- 资源缺失（classpath 路径不对）
- 注册冲突（同名覆盖策略/非法定义）
- 占位符解析失败（定义阶段失败或延迟到实例阶段失败）

推荐断点：
- `XmlBeanDefinitionReader#loadBeanDefinitions`
- `DefaultListableBeanFactory#registerBeanDefinition`
- `AbstractApplicationContext#refresh`（看它停在 refresh 的哪一段）

对应章节：
- XML → BeanDefinitionReader：[`42-xml-bean-definition-reader.md`](../part-05-aot-and-real-world/42-xml-bean-definition-reader.md)
- 注册入口：[`02-bean-registration.md`](../part-01-ioc-container/02-bean-registration.md)

---

## 6) 代理/最终对象问题（行为不符合直觉）

典型现象：

- 注入进来的对象不是你写的类，而是 proxy
- self-invocation（自调用）导致拦截不生效
- early reference 导致注入到的对象与最终对象不一致（尤其是循环依赖 + 代理）

推荐断点：
- `AbstractAutowireCapableBeanFactory#initializeBean`
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`
- `DefaultSingletonBeanRegistry#getSingleton`（early reference/三级缓存）

对应章节：
- 代理阶段：[`31-proxying-phase-bpp-wraps-bean.md`](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)
- early reference：[`16-early-reference-and-circular.md`](../part-03-container-internals/16-early-reference-and-circular.md)

---

## 7) Boot 自动装配相关排障（为什么有/为什么没有/为什么不退让）

最常见的三问：

1) **为什么有这个 Bean？**（哪个 auto-config 导入的？条件为什么成立？）  
2) **为什么没有这个 Bean？**（被过滤了？条件没 match？还是压根没导入？）  
3) **为什么不退让（back-off）？**（覆盖 bean 出现得太晚，条件评估时不可见）

推荐断点：
- `AutoConfigurationImportSelector#getAutoConfigurationEntry`（候选收集/排序/过滤）
- `ConfigurationClassParser#processImports`（导入到容器）
- `ConditionEvaluator#shouldSkip`（条件评估）

对应章节：
- 自动配置主线：[`021-10-spring-boot-auto-configuration.md`](../part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)
- 顺序问题：[`020-09-auto-config-ordering.md`](../part-02-boot-autoconfig/020-09-auto-config-ordering.md)
- 调试与观测：[`019-11-debugging-and-observability.md`](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)

---

## 8) AOT/Native 相关排障（契约缺失）

当你在 AOT/Native 下遇到“JVM 好好的，Native 失败”的问题，优先问：

- 这是 reflection/proxy/resource 的契约缺失吗？
- 是否需要补 `RuntimeHints`？

对应章节：
- AOT/Native 概览：[`024-40-aot-and-native-overview.md`](../part-05-aot-and-real-world/024-40-aot-and-native-overview.md)
- RuntimeHints 入门：[`41-runtimehints-basics.md`](../part-05-aot-and-real-world/41-runtimehints-basics.md)

---

## 最小断点集合（建议你背下来）

如果你只允许自己背 6 个断点，就背这 6 个：

- `AbstractApplicationContext#refresh`
- `AbstractBeanFactory#doGetBean`
- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `AbstractAutowireCapableBeanFactory#populateBean`
- `AbstractAutowireCapableBeanFactory#initializeBean`
- `DefaultListableBeanFactory#resolveDependency`

---

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab（定义层/XML）：[`SpringCoreBeansXmlBeanDefinitionReaderLabTest`](../../src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlBeanDefinitionReaderLabTest.java)  
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansXmlBeanDefinitionReaderLabTest test`
- Lab（注入歧义）：`SpringCoreBeansInjectionAmbiguityLabTest`
- Lab（候选收敛）：`SpringCoreBeansAutowireCandidateSelectionLabTest`
- Lab（循环依赖/early ref）：`SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
- Lab（代理阶段）：`SpringCoreBeansProxyingPhaseLabTest`

上一章：[93. 面试复述模板（决策树 → Lab → 断点入口）](93-interview-playbook.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[95. spring-beans Public API 索引（按类型检索）](95-spring-beans-public-api-index.md)

<!-- BOOKIFY:END -->
