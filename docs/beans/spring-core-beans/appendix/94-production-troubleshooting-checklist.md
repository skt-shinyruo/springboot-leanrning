# 94. 生产排障清单（异常分型 → 入口 → 观察点 → 修复策略）

## 导读

- 本章主题：**94. 生产排障清单（异常分型 → 入口 → 观察点 → 修复策略）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`

## 机制主线

本章目标：把 Spring Bean 排障从“靠经验”变成“有固定路线的收敛流程”。

---

## 0. 三步收敛（通用）

当你拿到一个异常栈，先按这三步做：

1) **分型：定义层 vs 实例层 vs 最终暴露对象**
- 定义层：BeanDefinition 读/解析/注册阶段失败（refresh 前半段）
- 实例层：create/populate/initialize 阶段失败（refresh 后半段或 getBean 时）
- 最终暴露对象：proxy/early reference 导致“注入/调用行为”不符合直觉

3) **观察：用固定 watch list 收敛原因**
- `beanName` / `mbd`（merged definition）
- 候选集合：Map<beanName, candidate>
- `descriptor.isRequired()`、注入点类型/注解（Qualifier/Primary/Resource/Value）
- post-processors 列表（顺序问题常见）
- 单例缓存三表（循环依赖/early reference）：`singletonObjects/earlySingletonObjects/singletonFactories`

---

## 1) NoSuchBeanDefinitionException（没有候选）

### 常见根因

- 根本没注册（扫描路径/配置类没生效）
- 条件装配没 match（auto-config 条件失败）
- 类型匹配失败（泛型/FactoryBean product type/代理导致的类型信息）

- `DefaultListableBeanFactory#doResolveDependency`
- `DefaultListableBeanFactory#findAutowireCandidates`

### 修复策略（两类）

1) 让候选出现（注册/导入/开启条件）  
2) 让匹配正确（修正泛型、明确 FactoryBean product type、避免信息丢失）

对应章节：

---

## 2) NoUniqueBeanDefinitionException（候选太多）

### 常见根因

- 多实现同时存在，注入点是单依赖
- auto-config 没退让（覆盖 bean 出现得太晚）

- `DefaultListableBeanFactory#findAutowireCandidates`（候选集合）
- `DefaultListableBeanFactory#determineAutowireCandidate`（收敛规则）
- `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`（Qualifier 匹配）

### 修复策略（两类）

1) **确定化选择**：`@Primary` / `@Qualifier`（候选仍可能多个，但注入点确定）  
2) **让 back-off 生效**：覆盖 bean 必须在条件评估前可见（更干净）

对应章节：

---

## 3) UnsatisfiedDependencyException（注入失败总包装）

### 处理要点

它经常包着真正 root cause：

- NoSuch / NoUnique（最常见）
- 类型转换失败（`@Value` / populateBean）
- 依赖链上游创建失败（构造器异常、init 异常、BPP 包装异常）

- `DefaultListableBeanFactory#doResolveDependency`
- `AutowiredAnnotationBeanPostProcessor#postProcessProperties`

### 观察点

- `descriptor`（required? 注入点注解?）
- 候选集合为空/不唯一的分支
- 值注入分支（suggested value / resolveEmbeddedValue）

对应章节：

---

## 4) BeanCreationException（创建链路失败）

### 常见根因

- 构造器抛异常
- `@PostConstruct` / initMethod 抛异常
- BPP 包装失败/短路异常
- 循环依赖失败（BeanCurrentlyInCreation / early reference 相关）

对应章节：

- 生命周期：`docs/part-01-ioc-container/05-lifecycle-and-callbacks.md`
- early reference：`docs/part-03-container-internals/16-early-reference-and-circular.md`
- 代理阶段：`docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`

---

## 5) BeanDefinitionStoreException（定义层失败：读/解析/注册）

### 典型场景

- XML 非法
- definition 解析失败（占位符/资源缺失）
- 注册冲突（同名覆盖策略/非法定义）

- `XmlBeanDefinitionReader#loadBeanDefinitions`
- `DefaultListableBeanFactory#registerBeanDefinition`
- `AbstractApplicationContext#refresh`

---

## 6) 代理/最终对象问题（行为不符合直觉）

### 典型现象

- 注入进来的对象不是你写的类，而是 proxy
- self-invocation（自调用）导致拦截不生效
- early reference 导致注入到的对象与最终对象不一致

对应章节：

- 代理阶段：`docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`
- early reference：`docs/part-03-container-internals/16-early-reference-and-circular.md`

---

## 7) AOT/Native 相关排障（契约缺失）

当你在 AOT/Native 下遇到“JVM 好好的，Native 失败”的问题，优先问：

- 这是 reflection/proxy/resource 的契约缺失吗？
- 是否需要补 RuntimeHints？

对应章节：

- `docs/part-05-aot-and-real-world/40-aot-and-native-overview.md`
- `docs/part-05-aot-and-real-world/41-runtimehints-basics.md`

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

核心原则：**先分型，再下断点**。

2) **定位：选 1 个主入口断点**
- 定义层：`DefaultListableBeanFactory#registerBeanDefinition` / `XmlBeanDefinitionReader#loadBeanDefinitions`
- 实例层：`AbstractAutowireCapableBeanFactory#doCreateBean` / `populateBean` / `initializeBean`
- 注入相关：`DefaultListableBeanFactory#doResolveDependency`

### 入口断点

### 入口断点

### 入口断点

- 排障主入口：`docs/part-02-boot-autoconfig/11-debugging-and-observability.md`
- 值解析与转换：`docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md`、`docs/part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md`

### 入口断点

### 入口断点

对应章节 + Lab：

- 章节：`docs/part-05-aot-and-real-world/42-xml-bean-definition-reader.md`
- Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`

### 入口断点

## 常见坑与边界

- DI 决策树：`docs/part-01-ioc-container/03-dependency-injection-resolution.md`
- 泛型匹配坑：`docs/part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md`

- 候选选择边界：`docs/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md`
- auto-config 覆盖/back-off：`docs/part-02-boot-autoconfig/10-spring-boot-auto-configuration.md`

## 小结与下一章

- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `AbstractAutowireCapableBeanFactory#createBeanInstance`
- `AbstractAutowireCapableBeanFactory#initializeBean`

- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`
- `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`

上一章：[93. 面试复述模板（决策树 → Lab → 断点入口）](93-interview-playbook.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[95. spring-beans Public API 索引（按类型检索）](95-spring-beans-public-api-index.md)

<!-- BOOKIFY:END -->
