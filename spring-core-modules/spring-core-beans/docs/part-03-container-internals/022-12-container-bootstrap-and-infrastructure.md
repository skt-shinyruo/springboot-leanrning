# 第 22 章：12. 容器启动与基础设施处理器：为什么注解能工作？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：容器启动与基础设施处理器：为什么注解能工作？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansBootstrapInternalsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 21 章：10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）](../part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 23 章：18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）](../part-04-wiring-and-boundaries/023-18-lazy-semantics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**12. 容器启动与基础设施处理器：为什么注解能工作？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。
- 主线叙事补充（建议在深挖前先通读一次）：[从 `refresh()` 到 `doCreateBean()`：把 Spring Bean “变成对象”的主线走通（源码级）](18-refresh-to-bean-creation-mainline.md)

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansResourceInjectionLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResourceInjectionLabTest.java`

## 机制主线

这一章回答一个很容易被忽略的问题：

## 1. 现象：同样是 Spring 容器，不同启动方式结果不一样

### 1.1 `GenericApplicationContext` 默认不处理注解

如果你直接用 `GenericApplicationContext` 注册 bean：

这不是 bug，而是：你没有把“注解解析与回调”的那套基础设施装进去。

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`
  - `withoutAnnotationConfigProcessors_autowiredAndPostConstructAreNotApplied()`（证据：字段为 null、`@PostConstruct` 没跑）

### 1.2 注册 annotation processors 后，注解才会被处理

当你调用：

- `AnnotationConfigUtils.registerAnnotationConfigProcessors(context)`

容器会注册一组核心处理器（简化理解）：

- `AutowiredAnnotationBeanPostProcessor`：处理 `@Autowired` / `@Value`
- `CommonAnnotationBeanPostProcessor`：处理 `@Resource` / `@PostConstruct` / `@PreDestroy`
- `ConfigurationClassPostProcessor`：解析 `@Configuration` / `@Bean` / `@Import` 等

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`
  - `registerAnnotationConfigProcessors_enablesAutowiredAndPostConstruct()`（证据：字段被注入、`@PostConstruct` 被触发）

#### 1.2.1 注解能力处理器速查表：功能 → 处理器 → 类型 → 关键方法 → refresh 落点

很多人学 Spring 注解会陷入“背注解名”的状态，但源码级理解的抓手应该是：**是哪一个处理器在什么阶段动了手**。

你可以把“注解为什么能工作”拆成下面这张表（只列最核心的几条，足够覆盖 80% 排障场景）：

| 你看到的能力（现象） | 关键处理器 | 处理器类型 | 关键方法（断点入口） | refresh 落点（阶段） |
|---|---|---|---|---|
| `@Configuration/@Bean/@Import/@ComponentScan` 能扩张定义图 | `ConfigurationClassPostProcessor` | `BeanDefinitionRegistryPostProcessor` + `BeanFactoryPostProcessor` | `postProcessBeanDefinitionRegistry`<br>`processConfigBeanDefinitions` | 第 5 步：`invokeBeanFactoryPostProcessors`（定义阶段） |
| `@Autowired/@Value` 能注入字段/方法/构造器参数 | `AutowiredAnnotationBeanPostProcessor` | `SmartInstantiationAwareBeanPostProcessor`（BPP） | `determineCandidateConstructors`（构造器分支）<br>`postProcessProperties`（属性填充分支） | 第 6 步：`registerBeanPostProcessors`（装规则）<br>创建阶段：`populateBean` / 构造器解析 |
| `@Resource/@PostConstruct/@PreDestroy` 能生效 | `CommonAnnotationBeanPostProcessor` | `InstantiationAwareBeanPostProcessor` + `DestructionAwareBeanPostProcessor`（BPP） | `postProcessProperties`（`@Resource`）<br>`postProcessBeforeInitialization`（`@PostConstruct`）<br>`postProcessBeforeDestruction`（`@PreDestroy`） | 第 6 步：`registerBeanPostProcessors`（装规则）<br>创建/销毁阶段：`initializeBean` / destroy |
| `*Aware`（BeanName/BeanFactory/ApplicationContext/Environment…）能被回调 | `ApplicationContextAwareProcessor`（容器内置） | `BeanPostProcessor`（BPP） | `postProcessBeforeInitialization`（触发各类 `setXxx(...)`） | 第 3 步：`prepareBeanFactory`（先塞基础设施） |
| 某些类型“能注入但不是 Bean”（如 `Environment`） | `registerResolvableDependency`（容器内置） | ——（不是 processor，是解析规则） | `DefaultListableBeanFactory#doResolveDependency`（优先命中 resolvableDependencies） | 第 3 步：`prepareBeanFactory`（先塞基础设施） |

### 1.3 源码解析：为什么 `AnnotationConfigApplicationContext` “默认就有注解能力”？

你前面看到的结论是：

- `GenericApplicationContext` 默认不处理 `@Autowired/@PostConstruct/@Bean/...`
- 手工调用 `AnnotationConfigUtils.registerAnnotationConfigProcessors(context)` 后，注解才“生效”

但真实项目里你经常并没有手工调用这句——因为你用的是 `AnnotationConfigApplicationContext`（以及 Spring Boot 创建的各种 `ApplicationContext` 实现），它们在 bootstrap 阶段就把这套基础设施装配好了。

**关键点：注解能力不是“refresh 时突然出现的”，而是容器在构造/初始化阶段就把一组 internal processors 注册到了 registry。**

#### 1.3.1 关键链路：context 构造器 → reader → registerAnnotationConfigProcessors

精简伪代码（只保留关键动作，不追求逐行一致）：

```text
new AnnotationConfigApplicationContext():
  reader = new AnnotatedBeanDefinitionReader(this.registry)
  scanner = new ClassPathBeanDefinitionScanner(this.registry)

AnnotatedBeanDefinitionReader(registry):
  AnnotationConfigUtils.registerAnnotationConfigProcessors(registry)
```

因此你在 `AnnotationConfigApplicationContext` 里天然就会看到那几个 internal processors 的 BeanDefinition（例如 internalAutowiredAnnotationProcessor 等），而 `GenericApplicationContext` 不会。

#### 1.3.2 重要的时机差异：“注册进 registry” ≠ “已经生效”

更准确的说法是：

1) `registerAnnotationConfigProcessors` 做的是 **定义层动作**：把处理器“作为 BeanDefinition 注册进 registry”
2) 它们真正“生效”需要经历 refresh 的两个关键阶段：
   - `invokeBeanFactoryPostProcessors`：`ConfigurationClassPostProcessor`（BDRPP）在这里解析 `@Configuration/@Bean/@Import`，把 `@Bean` 变成更多 BeanDefinition
   - `registerBeanPostProcessors`：`AutowiredAnnotationBeanPostProcessor` / `CommonAnnotationBeanPostProcessor` 在这里被实例化并加入 BeanFactory 的 BPP 列表
3) 等后续进入 bean 创建主线（`populateBean` / `initializeBean`）时：
   - `@Autowired/@Resource` 才会在属性填充阶段被处理
   - `@PostConstruct` 才会在初始化回调链里被触发

你把这一段与 [06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](../part-01-ioc-container/017-06-post-processors.md) 的 `PostProcessorRegistrationDelegate` 两段算法对起来，就能得到一个稳定排障结论：

- **registry 里有处理器，但 BeanFactory 的 BPP 列表里没有它 ⇒ 注解不会生效**

#### 1.3.3 把“时机差异”画成时间线：定义层注册 → 处理器执行 → 规则装载 → 创建链路命中

如果你把这一章只记成一句话，那就记成：

> 注解能力是一套“处理器 + 时机”的组合：先注册定义，再执行 BFPP/BDRPP 扩张定义，再注册 BPP 装规则，最后在创建链路里真正命中。

把它压缩成时间线会更稳：

```text
T0（bootstrap）：registerAnnotationConfigProcessors
  - 只是注册 BeanDefinition（定义层），处理器还没“生效”

T1（refresh 第 5 步）：invokeBeanFactoryPostProcessors
  - 执行 BDRPP/BFPP
  - ConfigurationClassPostProcessor 在这里把 @Configuration/@Bean/@Import 解析成更多 BeanDefinition

T2（refresh 第 6 步）：registerBeanPostProcessors
  - 实例化各种 BPP，并加入 beanFactory.getBeanPostProcessors()
  - 到这里“注入/回调/代理规则”才算装上

T3（refresh 第 9 步 或首次 getBean）：进入创建链路
  - populateBean / initializeBean 命中 BPP：@Autowired/@PostConstruct/... 才真正发生
```

#### 1.3.4 反例（最常见的坑）：在 BDRPP/BFPP 阶段过早 `getBean()` 会让对象错过 BPP

一个非常“真实世界”的坑是：你在 BFPP/BDRPP 执行阶段就调用了 `getBean()`（例如为了拿一个辅助对象做注册），这会导致：

- bean 在 “BPP 还没注册完” 之前就被创建了
- 于是它会错过注入、生命周期回调、甚至代理包装

本仓库有一个很直观的对照证据链：同一个目标 bean，early vs late 的创建时机不同，最终对象形态/回调就可能不同：

- `SpringCoreBeansRegistryPostProcessorLabTest`

## 2. `@Bean` 为什么能“变成 BeanDefinition”？

很多人以为：

- 写了 `@Configuration` + `@Bean`，容器就自然知道要注册这些 bean

但实际上：

- 如果没有 `ConfigurationClassPostProcessor`（一个 BDRPP，同时也是 BFPP），`@Bean` 方法根本不会被解析成额外的 `BeanDefinition`

对应测试（同一个 config class，两种容器启动方式得到不同结果）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`
  - `configurationClassIsNotParsedWithoutConfigurationClassPostProcessor()`（证据：不装 processors 时 `@Bean` 方法不会变成 BeanDefinition）

- **误解 1：注解是语言特性**
  - 注解只是元数据；让它“生效”的是容器启动阶段注册的处理器。

- **误解 2：以为任何 ApplicationContext 都等价**
  - `AnnotationConfigApplicationContext` 默认就带“注解能力”，而 `GenericApplicationContext` 默认不带。

- **误解 3：把“容器没装处理器”当成业务 bug**
  - 学机制时请优先问：当前容器有没有注册对应的 BFPP/BPP？

- `AbstractApplicationContext#refresh`：容器启动总入口；你能在这里建立“先装处理器、再建实例”的时间线
- `AnnotationConfigUtils#registerAnnotationConfigProcessors`：把让注解生效的一组 BFPP/BPP 注册进容器（本章的关键动作）
- `ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry`：解析 `@Configuration/@Bean/@Import` 并注册额外的 `BeanDefinition`
- `AutowiredAnnotationBeanPostProcessor#postProcessProperties`：属性填充阶段处理 `@Autowired/@Value`（没注册它就不会注入）
- `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`：处理 `@PostConstruct/@PreDestroy`（`CommonAnnotationBeanPostProcessor` 基于它）

入口：

你应该看到：

- 定义层：`registerAnnotationConfigProcessors` 把 internal processors 作为 BeanDefinition 放进 registry
- refresh 前半段：`invokeBeanFactoryPostProcessors` 里 `ConfigurationClassPostProcessor` 才真正解析 `@Configuration/@Bean`
- refresh 中段：`registerBeanPostProcessors` 把 `AutowiredAnnotationBeanPostProcessor/CommonAnnotationBeanPostProcessor` 加入 BeanFactory 的 BPP 列表
- 创建链路：`@Autowired` 在 `postProcessProperties` 命中；`@PostConstruct` 在 before-init 的 BPP 链路命中

推荐断点（按“定义层 → 实例层”的顺序）：

1) `AnnotationConfigUtils#registerAnnotationConfigProcessors`（定义层：处理器 BeanDefinition 进入 registry）
2) `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`（定义层：BDRPP/BFPP 执行时机）
3) `ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry`（`@Bean/@Import` 解析点）
4) `PostProcessorRegistrationDelegate#registerBeanPostProcessors`（实例层：BPP 进入 BeanFactory 列表）
5) `AutowiredAnnotationBeanPostProcessor#postProcessProperties`（`@Autowired/@Value` 注入发生点）
6) `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`（`@PostConstruct` 触发点）

最小复现入口（方法级）：

- `SpringCoreBeansBootstrapInternalsLabTest.withoutAnnotationConfigProcessors_autowiredAndPostConstructAreNotApplied()`
- `SpringCoreBeansBootstrapInternalsLabTest.registerAnnotationConfigProcessors_enablesAutowiredAndPostConstruct()`
- `SpringCoreBeansBootstrapInternalsLabTest.configurationClassIsNotParsedWithoutConfigurationClassPostProcessor()`

## 排障分流：这是定义层问题还是实例层问题？

你遇到的“注解不生效”，几乎都能先用两问把范围缩到 1/2：

1) **是不是“规则根本没装进来”？**（定义层 / registry 层）
   - 症状：`@Configuration/@Bean/@Import/@ComponentScan` 看起来完全没生效；容器里压根没有对应 BeanDefinition
   - 快速判断：看 registry 里是否存在 `internalConfigurationAnnotationProcessor`（`ConfigurationClassPostProcessor`）
   - 典型落点：`AnnotationConfigUtils#registerAnnotationConfigProcessors` / `invokeBeanFactoryPostProcessors`

2) **是不是“规则装进来了，但创建链路没调用到”？**（实例层 / 创建链路）
   - 症状：Bean 有了，但 `@Autowired` 字段是 `null`、`@PostConstruct` 没跑、`@Resource` 没命中
   - 快速判断：看 `beanFactory.getBeanPostProcessors()` 里有没有 `AutowiredAnnotationBeanPostProcessor` / `CommonAnnotationBeanPostProcessor`
   - 典型落点：`PostProcessorRegistrationDelegate#registerBeanPostProcessors` / `populateBean` / `initializeBean`

> 经验法则：**registry 里有 processor 的 BeanDefinition ≠ 注解能用**。  
> 真正决定“注解行为能否发生”的，是这些 BPP 是否进入 BeanFactory 的拦截链，以及目标 bean 是否在它们之后创建。

## 源码最短路径（call chain）

> 目标：当你怀疑“注解没生效”时，用最短调用链回答两个问题：
>
> 1) 相关处理器有没有被**注册**？（定义层）
> 2) 相关处理器有没有在 bean 创建链路里被**调用**？（实例层）

从 `GenericApplicationContext#refresh()` 走到 `@Autowired/@Resource/@PostConstruct` 的最短主干（只列关键节点）：

把这条最短链路走通，你会得到一个很稳的定位策略：

## 固定观察点（watch list）

> 目标：不靠猜，靠固定观察点在 1–2 分钟内确认“注解能力到底装没装、在哪里断了”。

### 1) 先确认：处理器有没有“进 registry”（定义层）

在 `AnnotationConfigUtils#registerAnnotationConfigProcessors` 里 watch/evaluate：

- `registry.containsBeanDefinition("org.springframework.context.annotation.internalConfigurationAnnotationProcessor")`（`ConfigurationClassPostProcessor`）
- `registry.containsBeanDefinition("org.springframework.context.annotation.internalAutowiredAnnotationProcessor")`（`AutowiredAnnotationBeanPostProcessor`）
- `registry.containsBeanDefinition("org.springframework.context.annotation.internalCommonAnnotationProcessor")`（`CommonAnnotationBeanPostProcessor`）

> 说明：这些是 Spring 内部的 “internal*Processor” beanName。你不需要背全，但用它们能非常快地确认“注解能力有没有被装进来”。

### 2) 再确认：处理器有没有“进 BeanFactory”（实例层）

在 `PostProcessorRegistrationDelegate#registerBeanPostProcessors` 里 watch/evaluate：

- `beanFactory.getBeanPostProcessors()`：看最终列表里是否包含 `AutowiredAnnotationBeanPostProcessor` / `CommonAnnotationBeanPostProcessor`

如果你只记一个判断条件，就记这个：

- **`beanFactory.getBeanPostProcessors()` 里没有对应处理器 ⇒ 注解一定不会生效**（后面所有“注入/回调没发生”都能解释）

### 3) 最后确认：目标注解在创建链路里有没有被触发

- 注入相关：`AutowiredAnnotationBeanPostProcessor#postProcessProperties` / `CommonAnnotationBeanPostProcessor#postProcessProperties` 是否命中
- 回调相关：`InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization` 是否命中（`@PostConstruct`）

## 反例（counterexample）

**反例：我用 `GenericApplicationContext` 注册了 bean，容器也能启动，但 `@Autowired/@Resource/@PostConstruct` 全都不生效（字段是 null、回调没跑）。**

- 你没有调用 `AnnotationConfigUtils.registerAnnotationConfigProcessors(...)`
  - 所以 `registerBeanPostProcessors` 阶段看不到 `AutowiredAnnotationBeanPostProcessor` / `CommonAnnotationBeanPostProcessor`
- 因为 BPP 根本没装上：
  - `@Autowired` 的 `postProcessProperties` 不会命中 ⇒ 字段保持 `null`
  - `@Resource` 的 `postProcessProperties` 不会命中 ⇒ 字段保持 `null`
  - `@PostConstruct` 的 `postProcessBeforeInitialization` 不会命中 ⇒ 回调不执行

正确做法（本章成功路径）：

- 要么用 `AnnotationConfigApplicationContext`（默认自带 annotation processors）
- 要么在 `GenericApplicationContext` 中显式调用 `AnnotationConfigUtils.registerAnnotationConfigProcessors(context)` 再 `refresh()`

## 4. 一句话自检

- 常问：为什么 `GenericApplicationContext` 里 `@Autowired/@PostConstruct` 默认不生效？
  - 答题要点：因为 annotation processors（BPP/BFPP）没有注册进容器；注解只是元数据，不是语言魔法。
- 常见追问：你如何用断点证明“处理器已经注册但尚未生效”？
  - 答题要点：`registerAnnotationConfigProcessors` 只注册 BeanDefinition（定义层）；必须经过 `invokeBeanFactoryPostProcessors` 与 `registerBeanPostProcessors` 才会进入创建链路（实例层）。
- 常见追问：遇到“字段为 null / `@PostConstruct` 没跑”，第一步该查什么？
  - 答题要点：先查 `beanFactory.getBeanPostProcessors()` 是否包含 `AutowiredAnnotationBeanPostProcessor/CommonAnnotationBeanPostProcessor`；没有就先补基础设施，而不是先怀疑业务逻辑。

## 面试常问（容器启动与注解为何生效）

2) 为什么 `@Autowired` / `@PostConstruct` / `@Bean` 能工作？如果没有 annotation processors 会怎样？
- 答题要点：注解只是元数据；`ConfigurationClassPostProcessor` 解析 `@Bean/@Import` 注册定义；`AutowiredAnnotationBeanPostProcessor` 做注入；`CommonAnnotationBeanPostProcessor` 处理 `@PostConstruct/@PreDestroy/@Resource`。
- 常见追问：`GenericApplicationContext` vs `AnnotationConfigApplicationContext` 行为差异的根因是什么？Spring Boot 如何“默认装好”这套基础设施？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansResourceInjectionLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

> 你写的 `@Configuration` / `@Bean` / `@Autowired` / `@PostConstruct` 之所以“能工作”，不是因为注解本身有魔法，而是因为 **容器在启动时注册并运行了一组基础设施 BFPP/BPP**。

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResourceInjectionLabTest.java`

- `@Autowired` 不会发生（字段仍然是 `null`）
- `@PostConstruct` 不会运行（回调不会被触发）

- `SpringCoreBeansBootstrapInternalsLabTest.withoutAnnotationConfigProcessors_autowiredAndPostConstructAreNotApplied()`

- `SpringCoreBeansBootstrapInternalsLabTest.registerAnnotationConfigProcessors_enablesAutowiredAndPostConstruct()`

- `SpringCoreBeansBootstrapInternalsLabTest.configurationClassIsNotParsedWithoutConfigurationClassPostProcessor()`

## 源码锚点（建议从这里下断点）

- `AbstractApplicationContext#refresh`：容器启动主入口（定位阶段感）
- `AnnotationConfigUtils#registerAnnotationConfigProcessors`：基础设施 processors 的注册入口（为什么注解能工作）
- `ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry`：`@Configuration/@Bean/@Import` 解析成 BeanDefinition 的核心入口
- `AutowiredAnnotationBeanPostProcessor#postProcessProperties`：`@Autowired/@Value/@Inject` 等注入发生点（populateBean 阶段）
- `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`：`@PostConstruct` 触发点（initializeBean 阶段）

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`
  - `withoutAnnotationConfigProcessors_autowiredAndPostConstructAreNotApplied()`
  - `registerAnnotationConfigProcessors_enablesAutowiredAndPostConstruct()`

建议断点（按从“现象”到“机制”的顺序）：

1) `SpringCoreBeansBootstrapInternalsLabTest` 的两段测试方法内：对照“注册 processors 前后”的行为差异
2) `AnnotationConfigUtils#registerAnnotationConfigProcessors`：确认哪些基础设施处理器被注册进了 `BeanDefinitionRegistry`
3) `ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry`：观察 `@Configuration/@Bean` 是在什么时候被解析成定义的
4) `AutowiredAnnotationBeanPostProcessor#postProcessProperties`：观察 field/method 注入发生在“属性填充阶段”，而不是构造阶段
5) `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`：观察 `@PostConstruct` 触发点（发生在 init 回调链里）

- 不注册 processors：上述 3/4/5 的断点不会命中，注入/回调不会发生
- 注册 processors：断点会命中，`@Autowired` 字段被赋值，`@PostConstruct` 被调用

- “`@Autowired/@Resource/@PostConstruct` 全都不生效/字段一直是 null” → **优先定义层/基础设施问题**：你是否注册了 annotation processors？（回到本章 Lab）
- “`@Bean` 方法写了但容器里没有这个 bean” → **优先定义层问题**：配置类是否被 `ConfigurationClassPostProcessor` 解析？（可对照 [02](../part-01-ioc-container/02-bean-registration.md) 与本章断点）
- “注入生效了但选错候选/候选太多” → **优先实例层（依赖解析）问题**：看 `DefaultListableBeanFactory#doResolveDependency`（见 [03](../part-01-ioc-container/014-03-dependency-injection-resolution.md) / [33](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)）
- “拿到的对象形态不对（proxy/替身）” → **优先实例层（BPP/代理）问题**：看 [31](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md) 与 [00](../part-00-guide/011-00-deep-dive-guide.md)

- 断点 **没命中 `registerBeanPostProcessors`** → 你根本没走到“装处理器”的阶段（容器启动方式/时机问题）
- 断点命中但 **BPP 列表里没有目标处理器** → 定义层没注册/注册被覆盖/没引入 annotation config processors
- 断点命中且 BPP 列表齐全但 **`postProcessProperties` 没命中** → 你看的 bean 不是走这条创建链路（可能是别的容器/别的 BeanFactory/或已提前创建）

最小复现入口（必现）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`
  - `withoutAnnotationConfigProcessors_autowiredAndPostConstructAreNotApplied()`
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResourceInjectionLabTest.java`
  - `withoutAnnotationConfigProcessors_resourceIsIgnored()`

你在断点里应该看到什么（用于纠错）：

- 你能解释清楚：`@Autowired`/`@PostConstruct`/`@Bean` 分别依赖哪些处理器让它们生效吗？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`
推荐断点：`AnnotationConfigUtils#registerAnnotationConfigProcessors`、`ConfigurationClassPostProcessor#processConfigBeanDefinitions`、`AutowiredAnnotationBeanPostProcessor#postProcessProperties`

1) 你能按“refresh 主线”复述 Spring 容器启动时序吗？
- 答题要点：先准备 `BeanFactory`，再运行 BFPP/BDRPP（定义层），再注册 BPP（实例层拦截链基础），最后 `preInstantiateSingletons` 创建非 lazy 单例。
- 常见追问：BFPP/BPP 各自“能改什么/不能改什么”？为什么“代理/替身”通常发生在 BPP 阶段？

3) 面试官让你“给一个断点闭环”，你会怎么打断点证明上面两点？
- 答题要点：从 `AbstractApplicationContext#refresh` 入手，串 `PostProcessorRegistrationDelegate` 的 BFPP/BPP 两段，再落到 `AutowiredAnnotationBeanPostProcessor#postProcessProperties` 与 init 回调处理器。

## 常见坑与边界

这是一个非常高频的误区：很多人看到 registry 里有这些 internal processors，就以为注解已经“能用”。

### 坑 1：只把 processor 注册成了 BeanDefinition，但没把 BPP 装进拦截链

- 你会看到：registry 里能查到 `internalAutowiredAnnotationProcessor`
- 但 `beanFactory.getBeanPostProcessors()` 里没有 `AutowiredAnnotationBeanPostProcessor`
- 结果：`populateBean` 不会触发注入逻辑，字段/参数保持默认值（`null/0/false`）

### 坑 2：过早 `getBean`（在 BPP 注册之前就把 bean 创建出来了）

即使你最终注册了 BPP，如果目标 bean 在它之前就已经被创建（例如在 BFPP 阶段过早触发 `getBean`），你仍然会看到：

- Bean 存在，但注入/回调缺失
- 或者“第一次创建不生效，第二次才生效”（其实是第二次创建的是另一个 bean/另一个 context）

定位策略：回到 refresh 主线，看目标 bean 的创建时机是不是早于 `registerBeanPostProcessors`。

### 坑 3：把“注解不生效”误判为“依赖没引入”

在这个仓库的最小场景里，依赖都在，但只要你跳过 `registerAnnotationConfigProcessors` 这一步，注解同样不会生效。  
因此排障顺序应该是：**先看处理器是否装配/是否执行，再看依赖是否缺失**。


## 小结与下一章

- `AbstractApplicationContext#refresh`
  - `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
    - `ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry`（如果注册了它：`@Configuration/@Bean/@Import` 才会被解析成更多 `BeanDefinition`）
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
    - **在这里把 `AutowiredAnnotationBeanPostProcessor` / `CommonAnnotationBeanPostProcessor` 等 BPP 注册进 BeanFactory**
  - `AbstractApplicationContext#finishBeanFactoryInitialization`
    - `DefaultListableBeanFactory#preInstantiateSingletons`
      - `AbstractBeanFactory#doGetBean`
        - `AbstractAutowireCapableBeanFactory#doCreateBean`
          - `applyMergedBeanDefinitionPostProcessors`  
            - 一些基础设施 BPP 会在这里“基于 merged BD 缓存元数据”（见 [35](../part-04-wiring-and-boundaries/35-merged-bean-definition.md)）
          - `populateBean`
            - `AutowiredAnnotationBeanPostProcessor#postProcessProperties`（`@Autowired/@Value` 注入点解析与赋值，见 [30](../part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md)）
            - `CommonAnnotationBeanPostProcessor#postProcessProperties`（`@Resource` 注入）
          - `initializeBean`
            - `InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`（`@PostConstruct` 触发）

在 `AbstractAutowireCapableBeanFactory#populateBean` / `#initializeBean` 附近 watch/evaluate：

- `beanName`：你关心的目标 bean 是否就在当前栈上
- `this.beanPostProcessors`：注入/生命周期相关 BPP 是否在列表里（没有就不要继续“猜注解为什么没生效”）
- `pvs`（PropertyValues）：在 `populateBean` 里是否被注入处理器补充/改写
- `wrappedBean` / `exposedObject`：`initializeBean` 之后返回的对象是否被包装/替换（代理/替身）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansResourceInjectionLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResourceInjectionLabTest.java`

上一章：[11. 调试与可观察性：从异常到断点入口](../part-02-boot-autoconfig/019-11-debugging-and-observability.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[13. BeanDefinitionRegistryPostProcessor：定义注册再推进](13-bdrpp-definition-registration.md)

<!-- BOOKIFY:END -->
