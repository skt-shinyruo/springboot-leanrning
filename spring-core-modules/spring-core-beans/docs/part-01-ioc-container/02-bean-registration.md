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

---

## 3. 注册发生在 refresh 的哪一段？（时机决定能力）

把“注册入口”放回 refresh 时间线（见 [10](../part-00-guide/010-03-mainline-timeline.md)）：

- 定义层注册：必须发生在 **创建单例之前**
- 如果你在 BFPP/BDRPP 之后才注册 BeanDefinition：可能错过一些“定义层改写能力”
- 如果你在 BPP 链完整之前就触发了 getBean：目标 bean 会错过后续 BPP（代理/注解注入等）

排障时你要问的不是“我注册了没有”，而是：

> **我注册的时机，是否保证它能被该看的处理器看见？**

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

---

## 常见坑与边界

1) **把 registerSingleton 当成“注册 bean”**
   - 它注册的是“对象”，不是“定义”；不会 retroactive 走注入/BPP/init。
2) **BeanDefinition 注册了，但候选选择/注入还是失败**
   - 先看是否走到 `findAutowireCandidates`；再看 beanName/Qualifier/Primary 是否匹配（见 14/33 章）。
3) **代理/注解不生效**
   - 优先怀疑时机问题：目标 bean 是否在 BPP 链完整前被创建（见 25/31 章）。

---

## 一句话自检

你应该能用 3 句答题：

1) Bean 注册的“第一性对象”是什么？（提示：BeanDefinition，而不是实例）  
2) `registerBeanDefinition` vs `registerSingleton` 的根本差异是什么？  
3) 为什么“注册时机”会决定 AOP/注解/回调是否生效？

<!-- BOOKIFY:START -->

上一章：[第 13 章：01. `ApplicationContext#refresh` 调用链（主线）](../part-00-guide/013-01-applicationcontext-refresh-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）](08-factorybean.md)

<!-- BOOKIFY:END -->
