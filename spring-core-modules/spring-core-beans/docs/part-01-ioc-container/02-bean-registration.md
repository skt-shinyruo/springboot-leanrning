# 02. Bean 注册入口：扫描、@Bean、@Import、registrar（已合并）

## 导读

- 本章主题：**Bean 注册入口：扫描、@Bean、@Import、registrar**
- 阅读方式建议：先跑“注册入口对照”的最小 Lab（ComponentScan / Import / Programmatic），再回到正文把“注册发生在 refresh 的哪一段、到底注册了什么”彻底讲清楚。

!!! summary "本章要点"

    - Bean 注册不是“new 一个对象放进容器”，而是：先把 **BeanDefinition** 注册进 `BeanDefinitionRegistry`，再在创建阶段按定义生成实例。
    - 你必须区分两类入口：**定义层注册（推荐）** vs **实例层注册（容易踩坑）**。实例层 `registerSingleton` 会绕开创建管线，因此不会自动注入/不会 retroactive 走 BPP。
    - 真正的分水岭问题是“注册发生在什么时候”：在 BFPP/BDRPP（定义层）阶段之前还是之后，决定了你能不能被后处理器看见/改写。

!!! example "本章配套实验（先跑再读）"

    - Lab（注册入口对照）：
      - `SpringCoreBeansComponentScanLabTest`
      - `SpringCoreBeansImportLabTest`
      - `SpringCoreBeansProgrammaticRegistrationLabTest`
    - Test file：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansComponentScanLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticRegistrationLabTest.java`

## 章节验收口径（10/30/3：教程化闭环）

> 这章内容多，但验收很简单：你不需要背细节，你要能“跑得出 + 断得到 + 说得清”。

1) **10 分钟最小闭环（跑得出）**
   - 至少跑通 1 个入口 Lab，并在输出/断言里看见“定义层注册发生了”。
2) **30 分钟断点闭环（断得到）**
   - 用条件断点命中 `registerBeanDefinition`，并能用 `source/factoryMethodName` 反推来源（scan/@Bean/@Import/registrar）。
3) **3 分钟复述闭环（说得清）**
   - 用“结论 → 证据链（方法级）→ 反例/坑”回答本章末尾的面试题（也可对照 `appendix/93-interview-playbook.md`）。

## 机制主线：注册 = 先注册定义，再按定义造实例

你在工程里“把一个东西交给 Spring 管”，本质上有两种完全不同的语义：

1) **定义层（Definition）**：把“怎么造对象”交给容器（容器拥有创建权）
2) **实例层（Instance）**：你已经造好了对象，容器只是“给它一个名字”

很多“注入没生效 / 代理没生效 / 回调没执行”的坑，都来自：你以为自己走的是定义层，其实走的是实例层。

---

## 1. BeanDefinition 是什么？（先把名词变成可观察对象）

一句话定义：

> `BeanDefinition` 描述的是“如何创建一个 bean 实例”的元数据（class、scope、factoryMethod、propertyValues、constructorArgs…）。

你在断点里应该能看见它至少包含：

- beanName（注册名）
- beanClass / beanClassName（要造的类型）
- scope（singleton/prototype/自定义 scope）
- role / source（来源：扫描/@Bean/@Import/XML…）

对应观察点：

- `DefaultListableBeanFactory#registerBeanDefinition(beanName, beanDefinition)`

---

## 2. 四类常见注册入口（你在项目里 99% 会遇到）

### 2.1 Component Scan（扫描注册）

入口心智模型：

- 你交给 Spring 一个“包路径”
- Spring 扫描 classpath，把符合条件的类转换成 BeanDefinition 注册进 registry

关键断点：

- `ClassPathBeanDefinitionScanner#doScan`（扫描入口）
- `DefaultListableBeanFactory#registerBeanDefinition`（最终落点）

Lab：`SpringCoreBeansComponentScanLabTest`

### 2.2 `@Configuration` + `@Bean`（配置类注册）

入口心智模型：

- 配置类会被解析（ConfigurationClass）
- `@Bean` 方法会被转换成 BeanDefinition（工厂方法方式）

关键断点：

- `ConfigurationClassPostProcessor#processConfigBeanDefinitions`
- `ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass`

### 2.3 `@Import`（导入注册：selector / registrar）

入口心智模型：

- `@Import` 不是“引入一个类那么简单”，它可以导入：
  - 普通配置类
  - `ImportSelector`（根据条件返回要导入的类名）
  - `ImportBeanDefinitionRegistrar`（直接操作 registry 注册 BeanDefinition）

关键断点：

- `ConfigurationClassPostProcessor#processConfigBeanDefinitions`（导入主入口也在这里）
- `ImportSelector#selectImports`
- `ImportBeanDefinitionRegistrar#registerBeanDefinitions`

Lab：`SpringCoreBeansImportLabTest`

### 2.4 Programmatic（编程式注册：Definition vs Instance）

这部分最容易踩坑，所以必须单列出来：

- 定义层注册（推荐）：`registerBeanDefinition` / `registerBean`
  - 会走 `doCreateBean`，所以会注入、会生命周期、会 BPP
- 实例层注册（慎用）：`registerSingleton`
  - 只把对象放进单例缓存，不会 retroactive 触发注入/BPP/init

对应章节（深入）：[25. 手工添加 BPP：顺序与时机](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)

Lab：`SpringCoreBeansProgrammaticRegistrationLabTest`

### 2.5 入口对照表（入口 → 注册对象 → 最短调用链）

> 目标：你不需要记住所有类名，但必须记住每类入口的 **3 个稳定锚点**：入口类、加工类、最终落点（`registerBeanDefinition`）。

| 入口形式（你写的代码/注解） | 注册对象 | 最短调用链（只记锚点） | 最关键落点 | 典型坑（高频翻车点） | 推荐 Lab |
| --- | --- | --- | --- | --- | --- |
| `@ComponentScan` / `scan(...)` | 定义层（BeanDefinition） | `ComponentScanAnnotationParser#parse` → `ClassPathBeanDefinitionScanner#doScan` → `BeanDefinitionRegistry#registerBeanDefinition` | `DefaultListableBeanFactory#registerBeanDefinition` | basePackage 不对 / 过滤器排除 / 配置类没被解析 | `SpringCoreBeansComponentScanLabTest` |
| `@Configuration` + `@Bean` | 定义层（工厂方法 BeanDefinition） | `ConfigurationClassPostProcessor#processConfigBeanDefinitions` → `ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass` → `loadBeanDefinitionsForBeanMethod` | 同上 | 把 `proxyBeanMethods` 误当成“是否注册”；过早触发 `getBean` 导致错过 BPP | `SpringCoreBeansContainerLabTest` |
| `@Import(普通配置类)` | 定义层 | `ConfigurationClassParser#processImports` → 作为配置类继续解析 → `registerBeanDefinition` | 同上 | import 顺序与条件组合导致“看起来没生效” | `SpringCoreBeansImportLabTest` |
| `@Import(ImportSelector)` | 定义层（间接产出 className 列表） | `ImportSelector#selectImports` → 返回类名 → 继续按“配置类解析链路”注册 | 同上 | 误以为 selector “直接造对象”；忘了它只返回“要导入的类名” | `SpringCoreBeansImportLabTest` |
| `@Import(ImportBeanDefinitionRegistrar)` | 定义层（直接操作 registry） | `ImportBeanDefinitionRegistrar#registerBeanDefinitions` → `registry.registerBeanDefinition` | 同上 | registrar 没被 `ConfigurationClassPostProcessor` 发现（配置类没被解析） | `SpringCoreBeansImportLabTest` |
| `registerBeanDefinition/registerBean` | 定义层 | 你调用的 API → `BeanDefinitionRegistry#registerBeanDefinition` | 同上 | 在 refresh 之后才注册：错过 BFPP/BDRPP 的定义层加工 | `SpringCoreBeansProgrammaticRegistrationLabTest` |
| `registerSingleton` | 实例层（单例缓存） | 你调用的 API → `SingletonBeanRegistry#registerSingleton` | `DefaultSingletonBeanRegistry#singletonObjects` | 不会 retroactive 注入/BPP/init；`beanDefinitionMap` 里也没有 | `SpringCoreBeansProgrammaticRegistrationLabTest` |

### 2.6 证据链：3 分钟证明“注册了什么”（建议每次都按这个套路）

你要把“注册入口”从概念变成肌肉记忆，关键是：每次都用同一套流程拿到证据链。

#### 2.6.1 证据链模板（通用）

1) 跑一个最小 Lab（噪音最少）
2) 在 `registerBeanDefinition` 处打断点（定义层落点）
3) 只看固定 watch list（不要在调用栈里漫游）
4) 用 `source/factoryMethodName/beanClassName` 判断来源（scan / @Bean / @Import / registrar）

推荐固定断点：

- `DefaultListableBeanFactory#registerBeanDefinition`（所有定义层注册最终都会落在这里）
- `AbstractApplicationContext#refresh`（把注册放回时间线）

推荐固定 watch list（最小够用版）：

- `beanName`
- `beanDefinition.getBeanClassName()`（或看实际类型是否为 `RootBeanDefinition`）
- `beanDefinition.getSource()`（来源线索：扫描/@Bean 方法元数据/@Import 等）
- `beanDefinition.getRole()`（基础设施 vs 应用 bean 的一个线索）

> Tip：建议用 **条件断点** 过滤 `beanName`（否则扫描场景会命中非常多次）。

#### 2.6.2 用扫描入口跑一遍（ComponentScan）

- 入口 Lab：`SpringCoreBeansComponentScanLabTest`
- 额外断点：`ClassPathBeanDefinitionScanner#doScan`
- 你要得到的结论：
  - `registerBeanDefinition` 被命中时，`beanDefinition.getSource()` 显示为“扫描来源”（通常可定位到 classpath/resource）

#### 2.6.3 用 @Import 入口跑一遍（selector / registrar）

- 入口 Lab：`SpringCoreBeansImportLabTest`
- 额外断点：
  - `ImportSelector#selectImports`（selector 分支）
  - `ImportBeanDefinitionRegistrar#registerBeanDefinitions`（registrar 分支）
- 你要得到的结论：
  - selector 只是返回 className 列表；真正注册仍会落到 `registerBeanDefinition`
  - registrar 是“直接注册定义”的能力（它本身就是定义层操作）

#### 2.6.4 用 programmatic 对照一遍（定义层 vs 实例层）

- 入口 Lab：`SpringCoreBeansProgrammaticRegistrationLabTest`
- 额外断点：
  - `BeanDefinitionRegistry#registerBeanDefinition`（定义层）
  - `DefaultSingletonBeanRegistry#registerSingleton`（实例层）
- 你要得到的结论：
  - 定义层：后续会走 `doCreateBean → populateBean → initializeBean`（因此注入/回调/BPP 都会发生）
  - 实例层：只是把对象塞进 `singletonObjects`，不会 retroactive 触发注入与 BPP（常见“看起来交给 Spring 了但不生效”）

---

## 3. 注册发生在 refresh 的哪一段？（时机决定能力）

> 这一节是你要的“源码调用链到方法级”。目标不是背源码，而是：**你能用断点把每条入口链路“走到落点”**。

!!! note "版本说明（很重要）"

    - 下面链路以 **Spring Framework 6.2.x（Spring Boot 3.5.x）** 为准。
    - 少数内部方法/类名在不同小版本可能会微调，但以下锚点基本稳定：
      - `AbstractApplicationContext#refresh`
      - `PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors`
      - `ConfigurationClassPostProcessor#processConfigBeanDefinitions`
      - `DefaultListableBeanFactory#registerBeanDefinition`

### 3.1 refresh 主线骨架：定义层注册发生在哪个时间窗？

把“注册入口”放回 refresh 时间线（见 [10](../part-00-guide/010-03-mainline-timeline.md)）：

- **定义层注册（BeanDefinition）**：发生在 `refresh()` 的 **BFPP/BDRPP 阶段**，也就是 `invokeBeanFactoryPostProcessors` 这一段。
- **创建单例**：发生在后面的 `finishBeanFactoryInitialization`（`preInstantiateSingletons`）这一段。
- 所以你排障时问的不是“我注册了没有”，而是：

> **我注册的时机，是否保证它能被该看的处理器看见？（尤其是 BFPP/BDRPP、BPP）**

方法级主线骨架（建议你在 IDE 里按这个顺序下断点跑一遍）：

```
AbstractApplicationContext#refresh
  -> invokeBeanFactoryPostProcessors(beanFactory)
       -> PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(...)
            -> invokeBeanDefinitionRegistryPostProcessors(...)
                 -> BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry(...)
                      -> ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry(registry)
                           -> processConfigBeanDefinitions(registry)
                                -> ConfigurationClassParser#parse(...)
                                -> ConfigurationClassBeanDefinitionReader#loadBeanDefinitions(...)

  -> registerBeanPostProcessors(beanFactory)
  -> finishBeanFactoryInitialization(beanFactory)
       -> DefaultListableBeanFactory#preInstantiateSingletons
            -> AbstractBeanFactory#doGetBean(...)
            -> AbstractAutowireCapableBeanFactory#doCreateBean(...)
```

你会发现：**scan / @Bean / @Import / registrar 这些“入口”大概率都落在 `processConfigBeanDefinitions` 这条链路里**（除非你显式调用了 `context.register(...) / context.scan(...) / registry.registerBeanDefinition(...)`）。

### 3.2 注解入口的“总闸门”：ConfigurationClassPostProcessor 在做什么？

一句话：**它是把“注解配置”翻译成 BeanDefinition 的核心 BDRPP**。

核心链路（方法级锚点）：

```
ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry
  -> processConfigBeanDefinitions(registry)
       -> new ConfigurationClassParser(...).parse(candidates)
       -> parser.validate()
       -> new ConfigurationClassBeanDefinitionReader(...).loadBeanDefinitions(configClasses)
            -> registry.registerBeanDefinition(...)
```

你只要把这条链路走通，就能回答两个高价值问题：

1) `@ComponentScan/@Bean/@Import` 这类注解“谁在解析”？（答：配置类解析链路）
2) 解析出来的结果是什么？（答：BeanDefinition 注册进 registry）

### 3.3 ComponentScan：@ComponentScan vs scan(...) 的方法级链路

#### 3.3.1 通过注解触发（`@ComponentScan`）

这条链路发生在“配置类解析”过程中：

```
ConfigurationClassParser#doProcessConfigurationClass(...)
  -> ComponentScanAnnotationParser#parse(componentScan, declaringClass)
       -> ClassPathBeanDefinitionScanner#doScan(basePackages)
            -> findCandidateComponents(basePackage)
            -> BeanDefinitionReaderUtils#registerBeanDefinition(holder, registry)
                 -> BeanDefinitionRegistry#registerBeanDefinition(beanName, beanDefinition)
```

稳定落点仍是：`DefaultListableBeanFactory#registerBeanDefinition`

建议断点（最短闭环）：

- `ComponentScanAnnotationParser#parse`
- `ClassPathBeanDefinitionScanner#doScan`
- `DefaultListableBeanFactory#registerBeanDefinition`

#### 3.3.2 通过 API 触发（`AnnotationConfigApplicationContext#scan`）

这条链路绕开“@ComponentScan 注解解析”，但注册落点相同：

```
AnnotationConfigApplicationContext#scan(basePackages)
  -> ClassPathBeanDefinitionScanner#scan(basePackages)
       -> doScan(basePackages)
            -> registerBeanDefinition(...)
```

排障提示：

- 如果你发现 `ComponentScanAnnotationParser#parse` 从未命中，但 `ClassPathBeanDefinitionScanner#doScan` 命中了：说明你走的是 `scan(...)` API，而不是 `@ComponentScan` 注解。

### 3.4 `@Configuration` + `@Bean`：工厂方法定义是怎么注册的？

核心事实：`@Bean` 不会让 Spring “立刻调用方法造对象”，它首先会把 **工厂方法** 翻译成 BeanDefinition（定义层）。

方法级链路（注册定义）：

```
ConfigurationClassBeanDefinitionReader#loadBeanDefinitions(configClasses)
  -> loadBeanDefinitionsForConfigurationClass(configClass)
       -> loadBeanDefinitionsForBeanMethod(beanMethod)
            -> registry.registerBeanDefinition(beanName, beanDefinition)
```

关键观察点（你在断点里应该能看见）：

- `RootBeanDefinition` 上的 `factoryBeanName` / `factoryMethodName`（说明它来自 `@Bean` 工厂方法，而不是扫描 class）
- `beanDefinition.getSource()`（通常能指向 `@Bean` 方法的元数据来源）

#### 3.4.1 配置类增强（proxyBeanMethods）发生在哪？为什么会影响“看起来像注册”但行为不对？

配置类增强不是“注册定义”的一部分，但它决定了 `@Bean` 的运行时语义（尤其是 **方法间调用是否走容器**）。

典型链路（增强发生在 `postProcessBeanFactory` 阶段）：

```
ConfigurationClassPostProcessor#postProcessBeanFactory(beanFactory)
  -> enhanceConfigurationClasses(beanFactory)
       -> ConfigurationClassEnhancer#enhance(configClass, classLoader)
```

排障提示：

- 现象：你在一个 `@Bean` 方法里直接调用另一个 `@Bean` 方法，却得到了“新对象”而不是容器单例
  - 优先检查：是否处于 “lite mode”（例如仅 `@Component`）或 `proxyBeanMethods=false`
  - 证据链：`ConfigurationClassEnhancer#enhance` 是否命中；配置类是否被增强为 CGLIB 子类（类名通常带 `$$`）
  - 对应章节：[第 18 章：07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）](018-07-configuration-enhancement.md)

### 3.5 `@Import`：selector / registrar 到底在链路上哪里分叉？

你可以把 `@Import` 看成“配置类解析链路上的一个分支开关”：

```
ConfigurationClassParser#doProcessConfigurationClass(...)
  -> processImports(configClass, sourceClass, importCandidates, ...)
```

#### 3.5.1 ImportSelector：返回“类名列表”，不是“注册对象”

```
processImports(...)
  -> ImportSelector#selectImports(importingClassMetadata)
       -> (return String[] classNames)
  -> 把 classNames 继续当作“配置类候选”去解析
       -> ConfigurationClassBeanDefinitionReader#loadBeanDefinitions(...)
            -> registerBeanDefinition(...)
```

一句话：**selector 决定“导入哪些类”，注册仍由配置类解析链路完成**。

#### 3.5.2 ImportBeanDefinitionRegistrar：直接操作 registry 注册定义

registrar 是“定义层的手术刀”，它最终一定会回到 `registry.registerBeanDefinition(...)`：

```
ConfigurationClassBeanDefinitionReader#loadBeanDefinitions(...)
  -> loadBeanDefinitionsFromRegistrars(configClass.getImportBeanDefinitionRegistrars())
       -> ImportBeanDefinitionRegistrar#registerBeanDefinitions(...)
            -> registry.registerBeanDefinition(beanName, beanDefinition)
```

一句话：**registrar 的能力上限更高（可动态造定义），但前提仍是：配置类解析链路跑起来**。

### 3.6 Programmatic：定义层注册 vs 实例层注册（方法级对照）

#### 3.6.1 定义层（推荐）：registerBeanDefinition / register / registerBean

注册落点与后果：

```
BeanDefinitionRegistry#registerBeanDefinition(beanName, beanDefinition)
  -> DefaultListableBeanFactory#registerBeanDefinition(...)

后续创建时（当 getBean 或 preInstantiateSingletons 触发）：
  -> AbstractAutowireCapableBeanFactory#doCreateBean
       -> populateBean
       -> initializeBean
```

#### 3.6.2 实例层（慎用）：registerSingleton

注册落点与后果：

```
SingletonBeanRegistry#registerSingleton(beanName, singletonObject)
  -> DefaultSingletonBeanRegistry#registerSingleton(...)
       -> addSingleton(beanName, singletonObject)
            -> singletonObjects.put(beanName, singletonObject)
```

关键结论：

- **实例层注册不会 retroactive 触发注入/BPP/init**
  - 证据链：你会发现 `doCreateBean/populateBean/initializeBean` 根本不命中
- 排障上，“定义存在/实例存在”必须拆开看：`containsBeanDefinition` vs `containsSingleton`

### 3.7 你说的“属性绑定”：populateBean / BeanWrapper 的方法级入口在哪里？

虽然本章讲“注册”，但你排障时经常要回答：**“我到底有没有走到属性填充（populateBean）？”**
因为它直接决定“注入/值绑定/类型转换”是否发生。

方法级最短链路（创建阶段）：

```
AbstractBeanFactory#doGetBean(beanName)
  -> AbstractAutowireCapableBeanFactory#createBean(beanName, mbd, args)
       -> doCreateBean(beanName, mbd, args)
            -> populateBean(beanName, mbd, instanceWrapper)
                 -> applyPropertyValues(beanName, mbd, bw, pvs)
                      -> BeanDefinitionValueResolver#resolveValueIfNecessary(...)
                      -> BeanWrapper#setPropertyValues(...)（实现类通常是 `BeanWrapperImpl`）
                           -> AbstractPropertyAccessor#setPropertyValues(...)
```

建议你用这三个断点就能证明“属性填充发生了”：

- `AbstractAutowireCapableBeanFactory#populateBean`
- `AbstractAutowireCapableBeanFactory#applyPropertyValues`
- `AbstractPropertyAccessor#setPropertyValues`

对应深挖章节：

- [30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）](../part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md)
- [36. 类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界](../part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md)

!!! warning "反例：过早 getBean（或实例层注册）会让你“绕开管线”"

    - 场景 1：在 BFPP/BDRPP 执行过程中（定义层加工阶段）直接触发 `getBean(...)`
      - 后果：目标 bean 可能在 BPP 链注册完成前被创建，导致注解注入/代理/回调行为异常（表现为“有时生效、有时不生效”）。
    - 场景 2：用 `registerSingleton` 把对象塞进容器
      - 后果：对象不会 retroactive 走注入/BPP/init（表面看“在容器里”，实际上绕开了创建管线）。

---

## 4. 断点闭环（最小可复用）

### 4.1 推荐断点（按收益排序）

1) `DefaultListableBeanFactory#registerBeanDefinition`（所有定义层注册最终都会到这里）
2) `ConfigurationClassPostProcessor#processConfigBeanDefinitions`（@Configuration/@Bean/@Import 主入口）
3) `ClassPathBeanDefinitionScanner#doScan`（扫描入口）
4) `ImportSelector#selectImports` / `ImportBeanDefinitionRegistrar#registerBeanDefinitions`（@Import 深水区）
5) `AbstractApplicationContext#refresh`（把注册放回时间线）

### 4.2 固定观察点（watch list）

- `beanName` / `beanDefinition.getBeanClassName()`
- `beanDefinition.getSource()`（来源线索）
- `beanFactory.containsBeanDefinition(beanName)`（定义层是否已注册）
- `beanFactory.containsSingleton(beanName)`（实例层是否已存在对象）

补充观察点（用于“@Bean/@Import/FactoryBean”类问题）：

- `beanDefinition` 的实际类型（是否为 `RootBeanDefinition`）
  - 若是 `RootBeanDefinition`：关注 `factoryBeanName` / `factoryMethodName`（能判断是否来自 `@Bean` 工厂方法）
- `beanFactory.getBeanDefinition(beanName)` 的 `role/source`（基础设施 bean vs 业务 bean 的一个线索）

---

## 5. 排障决策表（注册相关：现象 → 分层 → 证据 → 修复）

先给你一条“强制分层”的总规则（很重要）：

- **定义层问题**：`BeanDefinition` 根本没进 registry（扫描没扫到 / @Import 没执行 / @Bean 没被解析）
- **创建层问题**：定义进来了，但实例没创建（lazy/没触发 getBean/不是单例预实例化范围）
- **注入/代理问题**：实例创建了，但注入/代理/回调看起来没生效（常见原因：实例层注册或过早创建）

### 5.1 最短判定：三件事先问清楚

1) `containsBeanDefinition(beanName)` 是否为 true？（定义层是否存在）
2) `containsSingleton(beanName)` 是否为 true？（实例层是否已有对象）
3) `doCreateBean/populateBean` 是否命中过？（是否走过创建与属性填充）

### 5.2 排障决策表（建议收藏）

| 现象（Symptoms） | 优先分层 | 最短证据链（断点/变量） | 最可能根因（高频） | 修复策略（优先级） | 推荐 Lab |
| --- | --- | --- | --- | --- | --- |
| 扫不到 `@Component`（NoSuchBeanDefinition） | 定义层 | `ComponentScanAnnotationParser#parse` / `ClassPathBeanDefinitionScanner#doScan` / `registerBeanDefinition`；看 basePackage、过滤器、`beanName` | basePackage 写错 / excludeFilters 误伤 / 配置类没被解析 | 修正 basePackage；先证明 `processConfigBeanDefinitions` 命中；必要时用 `context.scan(...)` 对照 | `SpringCoreBeansComponentScanLabTest` |
| `@Import` 看起来没生效 | 定义层 | `ConfigurationClassParser#processImports`；看是否命中 selector/registrar；最终是否落到 `registerBeanDefinition` | 触发类没被注册为配置类候选 / import 条件分支未命中（Conditional/Profile） | 先证明配置类解析链路跑起来；再查 selector 返回值/registrar 是否被调用 | `SpringCoreBeansImportLabTest` |
| `registerBeanDefinition` 之后 BFPP/BDRPP 不生效 | 定义层（时机） | 看调用发生在 `refresh()` 哪一段；`invokeBeanFactoryPostProcessors` 之后再注册即错过定义层加工 | 你在 refresh 之后才动态加定义 | 把注册前移到 refresh 前（或用 BDRPP 动态注册）；避免事后补定义期待 BFPP 生效 | `SpringCoreBeansProgrammaticRegistrationLabTest` |
| 定义有了但实例没创建 | 创建层 | `containsBeanDefinition=true` 且 `containsSingleton=false`；看是否命中 `preInstantiateSingletons/doGetBean` | bean 是 lazy-init / 从未触发 getBean / scope 不是 singleton | 明确触发创建（getBean/依赖触发）；排查 `@Lazy`/scope；需要时在 `preInstantiateSingletons` 断点验证 | 结合章节 13/16/23 |
| 实例存在但“注入/代理/回调不生效” | 注入/代理 | `containsSingleton=true` 但 `doCreateBean/populateBean/initializeBean` 从未命中；或 BPP 链不完整时就创建了 | 使用 `registerSingleton`；或过早 `getBean` 导致错过 BPP | **优先改为定义层注册**；避免在 BFPP/BDRPP 阶段触发目标 bean；必要时手工 `autowireBean/initializeBean`（明确风险） | `SpringCoreBeansProgrammaticRegistrationLabTest` |
| `@Bean` 方法调用返回“新对象” | 运行时语义 | `ConfigurationClassEnhancer#enhance` 是否命中；配置类是否被增强；看 `proxyBeanMethods` | lite mode / `proxyBeanMethods=false` / 直接方法调用绕开容器 | 需要语义时开启 `proxyBeanMethods=true`；或改为参数注入/从容器获取依赖而非直接调用方法 | 见第 18 章（07） |
| 同名 bean 冲突/覆盖（override/Conflicting） | 定义层 | `DefaultListableBeanFactory#registerBeanDefinition`；扫描场景也看 `ClassPathBeanDefinitionScanner#checkCandidate` | beanName 重复；Boot 默认禁止覆盖（多数场景） | 优先改名/限定扫描；确需覆盖再显式开启（谨慎） | 见第 24 章（overriding） |
| `FactoryBean` 注入/获取结果不符合预期 | 获取边界 | `AbstractBeanFactory#doGetBean` → `getObjectForBeanInstance`；看是否为 `FactoryBean` | 忘了 `beanName` 取的是“产品”；`&beanName` 才是工厂 | 需要工厂用 `&`；需要产品按产品类型注入；必要时检查 `getObjectType` 返回值 | 见第 08 章 |
| 候选太多/Qualifier 不生效 | 注入解析 | `DefaultListableBeanFactory#doResolveDependency` → `findAutowireCandidates/isAutowireCandidate`；看 `Qualifier`/`Primary` | 多候选未收敛；Qualifier 不匹配；按名称 fallback 误解 | 用 `@Qualifier/@Primary/@Resource` 明确收敛；必要时打印候选集合（或用 testsupport dumper） | 见第 14/33 章 |
| “看见 BeanDefinition 了”但来源不明确 | 定义层取证 | 在 `registerBeanDefinition` 看 `beanDefinition.getSource()` / `factoryMethodName` / `role` | 只看了名字/类型，没看来源元数据 | 固化证据链：source + factoryMethodName + 入口断点（scan/@Bean/@Import） | 本章 2.6 |

### 5.3 常见坑与边界（压缩版 checklist）

1) **把 registerSingleton 当成“注册 bean”**
   - 它注册的是“对象”，不是“定义”；不会 retroactive 走注入/BPP/init。
2) **把“定义存在”当成“实例一定存在”**
   - `containsBeanDefinition` 只证明“定义进来了”；实例是否创建取决于 lazy/预实例化/是否触发 getBean。
   - 最短判断：`containsBeanDefinition`（定义层） vs `containsSingleton`（实例层缓存）。
3) **BeanDefinition 注册了，但候选选择/注入还是失败**
   - 先看是否走到 `findAutowireCandidates`；再看 beanName/Qualifier/Primary 是否匹配（见 14/33 章）。
4) **代理/注解不生效**
   - 优先怀疑时机问题：目标 bean 是否在 BPP 链完整前被创建（见 25/31 章）。
5) **扫描看起来没生效**
   - 优先检查：配置类是否被解析（`ConfigurationClassPostProcessor` 是否执行到）、basePackage 是否正确、excludeFilters 是否把目标排除了。
6) **@Import 相关“没生效”**
   - selector/registrar 生效的前提仍是：配置类解析链路跑起来（`processConfigBeanDefinitions`）。先证明“配置类解析发生了”，再看 import 分支。

---

## 6. 面试/内训：标准答案（可复述）

### 6.1 统一答题结构（复述模板）

如果你想把本章用于面试或团队内训，建议统一用这套答题结构：

1) 一句话定义：Bean 注册的第一性对象是 `BeanDefinition`（定义），不是实例
2) 四类入口：scan / @Bean / @Import（selector+registrar）/ programmatic
3) 时机：它发生在 `refresh` 的定义层阶段，晚了就错过 BFPP/BDRPP 与 BPP 链
4) 反例：`registerSingleton` / 过早 getBean（绕开管线）
5) 证据链：给出一个 Lab + 关键断点 + 3 个观察点（source/beanName/factoryMethodName）

面试高频问法（建议你至少能答出 3 题）：

1) Spring 里“注册一个 Bean”到底注册的是什么？（BeanDefinition vs bean instance）
2) `registerBeanDefinition` vs `registerSingleton` 的根本差异是什么？为什么会影响注入/代理/回调？
3) `@ComponentScan`、`@Bean`、`@Import` 分别是谁在负责解析与注册？（关键处理器/落点）
4) 为什么说“注册时机决定能力”？你如何用断点证明一次？
5) 你在真实项目里遇到“注入没生效/代理没生效”，如何第一时间判断是不是“绕开了创建管线”？

团队内训可直接复用的课时脚本见：[`appendix/99-team-training-kit.md`](../appendix/99-team-training-kit.md)

### 6.2 面试标准答案（建议至少背熟 8 题）

> 下面每题都按“结论 → 证据链（方法级） → 反例/坑 → 加分项”的结构给出。

#### Q1：Spring 里“注册一个 Bean”到底注册的是什么？

- 结论：**注册的是 `BeanDefinition`（定义），不是“对象实例”。**
- 证据链（方法级）：
  - `DefaultListableBeanFactory#registerBeanDefinition`：定义层最终落点
  - 创建发生在后续：`AbstractBeanFactory#doGetBean` → `AbstractAutowireCapableBeanFactory#doCreateBean`
- 反例/坑：
  - `registerSingleton` 注册的是“对象缓存”，不会 retroactive 触发注入/BPP/init
- 加分项：
  - 讲清“定义层 vs 创建层”的分界线：`invokeBeanFactoryPostProcessors` vs `finishBeanFactoryInitialization`

#### Q2：`registerBeanDefinition` vs `registerSingleton` 根本差异是什么？为什么会影响注入/代理/回调？

- 结论：**前者让容器拥有“创建权”，后者只是把对象塞进单例缓存，绕开创建管线。**
- 证据链（方法级）：
  - 定义层：`registerBeanDefinition` → 后续 `doCreateBean` → `populateBean` → `initializeBean`
  - 实例层：`registerSingleton` → `DefaultSingletonBeanRegistry#addSingleton`（直接入 `singletonObjects`）
- 反例/坑：
  - “看起来在容器里”但 `@Autowired/@Value/AOP` 不生效，多半就是实例层注册或过早创建
- 加分项：
  - 提到“可手工补救但不推荐”：`AutowireCapableBeanFactory#autowireBean/initializeBean`（并强调风险与边界）

#### Q3：`@ComponentScan`、`@Bean`、`@Import` 分别是谁在负责解析与注册？

- 结论：**绝大多数注解入口都由 `ConfigurationClassPostProcessor` 触发的“配置类解析链路”负责翻译为 BeanDefinition。**
- 证据链（方法级）：
  - `ConfigurationClassPostProcessor#processConfigBeanDefinitions`
  - 扫描：`ComponentScanAnnotationParser#parse` → `ClassPathBeanDefinitionScanner#doScan`
  - @Bean：`ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForBeanMethod`
  - @Import：`ConfigurationClassParser#processImports`
- 反例/坑：
  - 以为 selector/registrar “自己就能生效”，忽略了前提：配置类解析链路必须跑起来
- 加分项：
  - 提到“最终落点统一”：`DefaultListableBeanFactory#registerBeanDefinition`

#### Q4：为什么说“注册时机决定能力”？你如何用断点证明一次？

- 结论：**因为 BFPP/BDRPP/BPP 都有严格窗口期：晚了就错过定义层改写或代理注入。**
- 证据链（方法级）：
  - 时间窗：`refresh` → `invokeBeanFactoryPostProcessors`（定义层加工/注册）→ `registerBeanPostProcessors`（BPP 链）→ `preInstantiateSingletons`（创建）
- 反例/坑：
  - 在 BFPP/BDRPP 过程中触发 `getBean`：目标 bean 可能在 BPP 链完整前被创建，表现为“有时生效有时不生效”
- 加分项：
  - 讲清“一次证明”的套路：在 `registerBeanDefinition` 打断点，看 `source/factoryMethodName` 再回到调用栈定位入口

#### Q5：ImportSelector vs ImportBeanDefinitionRegistrar 有什么区别？各适用什么场景？

- 结论：
  - **ImportSelector：返回“要导入的类名列表”，让它们继续走配置类解析链路。**
  - **Registrar：直接操作 registry，适合动态构建 BeanDefinition。**
- 证据链（方法级）：
  - selector：`ImportSelector#selectImports` → 回到 `loadBeanDefinitions` 注册
  - registrar：`loadBeanDefinitionsFromRegistrars` → `ImportBeanDefinitionRegistrar#registerBeanDefinitions` → `registry.registerBeanDefinition`
- 反例/坑：
  - 把 selector 当成“工厂”，以为它会创建对象（其实只返回类名）
- 加分项：
  - 讲到 DeferredImportSelector：为什么要“延迟”（用于排序/分组，常见于 Boot 自动配置）

#### Q6：为什么 `@Bean` 方法里直接调用另一个 `@Bean` 方法有时会出事？

- 结论：**因为是否“配置类增强”决定了方法调用是否会被拦截并转成容器取 bean。**
- 证据链（方法级）：
  - 增强：`ConfigurationClassPostProcessor#postProcessBeanFactory` → `ConfigurationClassEnhancer#enhance`
- 反例/坑：
  - lite mode / `proxyBeanMethods=false` 下直接方法调用会绕开容器，得到新对象（语义变化）
- 加分项：
  - 给出工程建议：优先用参数注入/构造注入表达依赖，而不是方法体里直接调用另一个 `@Bean` 方法

#### Q7：如何 1 分钟排查“扫不到 Bean”是扫描问题还是注册时机问题？

- 结论：**先判定“定义有没有进 registry”，再判定“配置类解析链路是否触发”。**
- 证据链（最短）：
  - `containsBeanDefinition(beanName)`（没有就是定义层问题）
  - `ConfigurationClassPostProcessor#processConfigBeanDefinitions` 是否命中（没命中多半是配置类没注册/没走 annotation context）
  - `ClassPathBeanDefinitionScanner#doScan` 是否命中（命中但没注册要看过滤器/冲突）
- 反例/坑：
  - 只盯着 `@ComponentScan`，忽略了 basePackage 与 filter 才是高频根因
- 加分项：
  - 能把排障过程落到本章 5.2 决策表的一行

#### Q8：BeanDefinition 有了但注入仍失败，你下一步看什么？

- 结论：**这通常已经不是“注册问题”，而是“候选解析/Qualifier/Primary/泛型匹配”的注入解析问题。**
- 证据链（方法级）：
  - `DefaultListableBeanFactory#doResolveDependency` → `findAutowireCandidates` → `isAutowireCandidate`
- 反例/坑：
  - 把 `NoUniqueBeanDefinitionException` 当成“没注册”，其实是“注册太多没收敛”
- 加分项：
  - 能解释 `@Resource` 更像按名称（见 32 章），以及 by-name fallback 的边界

## 一句话自检

你应该能用 3 句答题：

1) Bean 注册的“第一性对象”是什么？（提示：BeanDefinition，而不是实例）
2) `registerBeanDefinition` vs `registerSingleton` 的根本差异是什么？
3) 为什么“注册时机”会决定 AOP/注解/回调是否生效？

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansComponentScanLabTest`
- Lab：`SpringCoreBeansImportLabTest`
- Lab：`SpringCoreBeansProgrammaticRegistrationLabTest`

上一章：[第 13 章：01. `ApplicationContext#refresh` 调用链（主线）](../part-00-guide/013-01-applicationcontext-refresh-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）](08-factorybean.md)

<!-- BOOKIFY:END -->
