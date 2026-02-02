# 02. Bean 注册入口：扫描、@Bean、@Import、registrar（已合并）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Bean 注册入口：扫描、@Bean、@Import、registrar
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`DefaultListableBeanFactory#registerBeanDefinition` / `DefaultSingletonBeanRegistry#registerSingleton` / `ClassPathBeanDefinitionScanner#doScan`
    - 推荐 Lab：`SpringCoreBeansComponentScanLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 13 章：01. `ApplicationContext#refresh` 调用链（主线）](../part-00-guide/013-01-applicationcontext-refresh-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）](08-factorybean.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**Bean 注册入口：扫描、@Bean、@Import、registrar**
- 阅读方式建议：先运行“注册入口对照”的最小 Lab（ComponentScan / Import / Programmatic），再回到正文把“注册发生在 refresh 的哪一段、到底注册了什么”彻底讲清楚。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（Java Config / @Bean，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/beans/java.html


!!! summary "本章要点"

    - Bean 注册不是“new 一个对象放进容器”，而是：先把 **BeanDefinition** 注册进 `BeanDefinitionRegistry`，再在创建阶段按定义生成实例。
    - 必须区分两类入口：**定义层注册（推荐）** vs **实例层注册（容易易错点）**。实例层 `registerSingleton` 会绕开创建管线，因此不会自动注入/不会 retroactive 走 BPP。
    - 真正的分水岭问题是“注册发生在什么时候”：在 BFPP/BDRPP（定义层）阶段之前还是之后，决定了应能够不能被后处理器观察到/改写。

!!! example "本章配套实验（先运行再读）"

    - Lab（字段级证据链入口，强烈可先运行）：
      - `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest`
    - Lab（注册入口对照）：
      - `SpringCoreBeansComponentScanLabTest`
      - `SpringCoreBeansImportLabTest`
      - `SpringCoreBeansProgrammaticRegistrationLabTest`
    - Test file：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanDefinitionRegistrationDiffLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansComponentScanLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticRegistrationLabTest.java`

    - 说明：`SpringCoreBeansProgrammaticRegistrationLabTest` 位于 Part 04，但它同时服务于本章的“定义层 vs 实例层”对照（把边界问题放在 wiring & boundaries 更利于工程化理解）。

## 章节验收口径（10/30/3：教程化闭环）

> 这章内容多，但验收很简单：无需背细节，应能够做到“可运行、可断点验证、可清晰阐释”。

1) **10 分钟最小闭环（可运行）**
   - 至少完成验证 1 个入口 Lab，并在输出/断言里观察到“定义层注册发生了”。
2) **30 分钟断点闭环（断得到）**
   - 用条件断点命中 `registerBeanDefinition`，并能用 `source/factoryMethodName` 反推来源（scan/@Bean/@Import/registrar）。
3) **3 分钟复述闭环（说得清）**
   - 用“结论 → 证据链（方法级）→ 反例/误区”回答本章末尾的面试题（也可对照 `appendix/93-interview-playbook.md`）。

## 机制系统阐述：注册入口的条件 → 分支 → 结果（可断点证明）

- **条件**：读者是“注册定义”还是“注册实例”，以及注册发生的时机
- **分支**：
  - `registerBeanDefinition`（定义层）
  - `registerSingleton`（实例层）
- **结果**：
  - 定义层注册 → 会参与 BFPP/BPP/生命周期（注入/回调/代理可生效）
  - 实例层注册 → 直接入单例缓存，**不会 retroactive 触发注入/BPP**
- **断点建议**：`DefaultListableBeanFactory#registerBeanDefinition` / `DefaultSingletonBeanRegistry#registerSingleton`
- **关键变量**：`beanDefinition.getSource()` / `factoryMethodName` / `allowBeanDefinitionOverriding`

## 关键分支解释（围绕 refresh 的 if/then）

- **注册发生在 BFPP/BDRPP 之前？**
  - 是：定义仍可被加工（注解/占位符/注册表扩张）
  - 否：可能错过定义层加工
- **注册发生在 BPP 之前？**
  - 是：实例创建可被完整 BPP 链处理
  - 否：容易出现过早实例化的 bean（从而错过代理/注解处理）
- **是否允许覆盖**：`allowBeanDefinitionOverriding` 决定同名定义能否被后注册覆盖
  - 补充：纯 Spring 容器里该开关通常默认 **允许**（方便覆盖/重定义），但 Spring Boot 工程里多数场景默认 **禁止**（同名直接异常），需要显式开启 `spring.main.allow-bean-definition-overriding=true`（或自定义 `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(true)`）

## 机制主线：注册 = 先注册定义，再按定义造实例

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

在工程里“把一个东西交给 Spring 管”，本质上有两种完全不同的语义：

1) **定义层（Definition）**：把“怎么造对象”交给容器（容器拥有创建权）
2) **实例层（Instance）**：对象已由调用方创建，容器只是“给它一个名字”

很多“注入未生效 / 代理未生效 / 回调未执行”的误区，往往来自分层误判：易误以为处于定义层，实际处于实例层。

---

## 1. BeanDefinition 是什么？（先把名词变成可观察对象）

一句话定义：

> `BeanDefinition` 描述的是“如何创建一个 bean 实例”的元数据（class、scope、factoryMethod、propertyValues、constructorArgs…）。

在断点里应该能观察到它至少包含（只列“会影响后续行为/排障分层”的关键字段）：

- beanName（注册名；可能还有 alias）
- beanClassName / beanClass（要造的类型；扫描入口通常能直接看到）
- factoryBeanName / factoryMethodName（若来自 `@Bean` 工厂方法，这两个字段是第一线索）
- scope（singleton/prototype/自定义 scope）
- lazyInit（是否延迟创建；影响 `preInstantiateSingletons` 与“什么时候才会创建”）
- dependsOn（强制初始化/销毁顺序；常见于基础设施/资源依赖）
- autowireCandidate / primary / qualifiers（参与候选收集与收敛；决定“能不能注入/注入选谁”）
- role / source / resourceDescription（来源线索：扫描/@Bean/@Import/XML…；排障时用于反推入口）

> 读者不要把这些字段当作“元数据而已”。它们会在后续的「依赖解析」「生命周期」「后处理器」「FactoryBean」「循环依赖」等分支中被读取。

**字段 → 行为（最短读取点）速查：**

| 字段 | 什么时候被读取 | 方法级锚点（建议断点） | 影响的行为 | 推荐跳转 |
| --- | --- | --- | --- | --- |
| `scope` | 获取/创建 bean 时 | `AbstractBeanFactory#doGetBean` | singleton 缓存 vs prototype 每次创建 | [第 15 章：04. Scope 与 prototype 注入陷阱](015-04-scope-and-prototype.md) / [09. 循环依赖](09-circular-dependencies.md) |
| `lazyInit` | 容器预实例化与按需创建 | `DefaultListableBeanFactory#preInstantiateSingletons` | 是否在 refresh 期间创建 | [023. `@Lazy` 语义与边界](../part-04-wiring-and-boundaries/023-18-lazy-semantics.md) |
| `dependsOn` | 创建前强制拉起依赖 | `AbstractBeanFactory#doGetBean`（读取 `mbd.getDependsOn()`） | 初始化/销毁顺序被强制化（并可能拉起 lazy） | [19. dependsOn：强制初始化顺序](../part-04-wiring-and-boundaries/19-depends-on.md) / [第 16 章：05. 生命周期](016-05-lifecycle-and-callbacks.md) |
| `autowireCandidate/primary/qualifiers` | 依赖解析（候选收集/收敛） | `DefaultListableBeanFactory#doResolveDependency` | 候选集合收敛与最终选择 | [第 14 章：03. 依赖注入解析](014-03-dependency-injection-resolution.md) |

对应观察点：

- `DefaultListableBeanFactory#registerBeanDefinition(beanName, beanDefinition)`

### 1.1 不同注册入口的 BeanDefinition “形态”对照（以本仓库 Lab 为准）

本表的目标不是要求记忆实现类名（可能随版本微调），而是用于在断点中通过 **稳定字段组合** 反推出“它来自哪个入口”：

| 入口 | 断点中最稳定可观察到的线索 | 常见误判 | 最短证据入口 |
| --- | --- | --- | --- |
| 扫描（`@ComponentScan`/`scan(...)`） | `beanClassName` 通常不为空；`factoryMethodName` 通常为空；`source/resourceDescription` 常指向 classpath 元数据 | 误以为“扫描=实例化”；实际只注册定义 | `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest` / `SpringCoreBeansComponentScanLabTest` |
| `@Configuration` + `@Bean` | `factoryBeanName/factoryMethodName` 通常不为空（工厂方法定义）；`beanClassName` 可能不是最关键线索 | 将 `proxyBeanMethods` 视为“是否注册”；但其影响的是方法调用语义 | `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest` / `SpringCoreBeansContainerLabTest` |
| `@Import(ImportBeanDefinitionRegistrar)` | registrar 自己 `registry.registerBeanDefinition(...)`；`source/resourceDescription` 往往能被 registrar 写成排障线索 | 误以为 registrar “直接造对象”；实际仍处于定义层 | `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest` / `SpringCoreBeansImportLabTest` |
| 编程式 `registerBeanDefinition` | 手工创建的 `BeanDefinition`；是否可被 BFPP/BDRPP 加工取决于注册时机 | refresh 之后再注册，期待 BFPP 生效 | `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest` / `SpringCoreBeansProgrammaticRegistrationLabTest` |
| 实例层 `registerSingleton` | **没有 BeanDefinition**；只在 `singletonObjects` 有实例 | 误以为“注册完成=注入/BPP/回调都会补上” | `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest` / `SpringCoreBeansProgrammaticRegistrationLabTest` |

---

## 2. 四类常见注册入口（在项目里 99% 会遇到）

### 2.1 Component Scan（扫描注册）

入口理解：

- 交给 Spring 一个“包路径”
- Spring 扫描 classpath，把符合条件的类转换成 BeanDefinition 注册进 registry

关键断点：

- `ClassPathBeanDefinitionScanner#doScan`（扫描入口）
- `DefaultListableBeanFactory#registerBeanDefinition`（最终落点）

Lab：`SpringCoreBeansComponentScanLabTest`

### 2.2 `@Configuration` + `@Bean`（配置类注册）

入口理解：

- 配置类会被解析（ConfigurationClass）
- `@Bean` 方法会被转换成 BeanDefinition（工厂方法方式）

关键断点：

- `ConfigurationClassPostProcessor#processConfigBeanDefinitions`
- `ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass`

### 2.3 `@Import`（导入注册：selector / registrar）

入口理解：

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

这部分最易出错，所以必须单列出来：

- 定义层注册（推荐）：`registerBeanDefinition` / `registerBean`
  - 会走 `doCreateBean`，所以会注入、会生命周期、会 BPP
- 实例层注册（慎用）：`registerSingleton`
  - 只把对象放进单例缓存，不会 retroactive 触发注入/BPP/init

如果确实处在“对象已由外部创建，但仍希望 Spring 补齐注入/回调”的场景，**不应误以为 `registerSingleton` 会自动补齐这些能力**。
更接近该场景所需的能力组合通常为：

- `AutowireCapableBeanFactory#autowireBean`（补注入）
- `AutowireCapableBeanFactory#initializeBean`（触发 init callbacks / BPP 链）

但也要明确边界：这类“补救”依赖调用时机与容器状态，仍可能错过某些排序/代理替换窗口；因此工程上优先级仍应是“能定义层注册就不要实例层塞对象”。

对应深入（外部对象如何接入容器能力）：[43. 容器外对象注入：AutowireCapableBeanFactory](../part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md)

对应章节（深入）：[25. 手工添加 BPP：顺序与时机](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)

Lab：`SpringCoreBeansProgrammaticRegistrationLabTest`

### 2.5 入口对照表（入口 → 注册对象 → 最短调用链）

> 目标：无需记住所有类名，但必须记住每类入口的 **3 个稳定锚点**：入口类、加工类、最终落点（`registerBeanDefinition`）。

| 入口形式（编写的代码/注解） | 注册对象 | 最短调用链（只记锚点） | 最关键落点 | 典型误区（高频易错点） | 推荐 Lab |
| --- | --- | --- | --- | --- | --- |
| `@ComponentScan` / `scan(...)` | 定义层（BeanDefinition） | `ComponentScanAnnotationParser#parse` → `ClassPathBeanDefinitionScanner#doScan` → `BeanDefinitionRegistry#registerBeanDefinition` | `DefaultListableBeanFactory#registerBeanDefinition` | basePackage 不对 / 过滤器排除 / 配置类没被解析 | `SpringCoreBeansComponentScanLabTest` |
| `@Configuration` + `@Bean` | 定义层（工厂方法 BeanDefinition） | `ConfigurationClassPostProcessor#processConfigBeanDefinitions` → `ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass` → `loadBeanDefinitionsForBeanMethod` | 同上 | 把 `proxyBeanMethods` 误当成“是否注册”；过早触发 `getBean` 导致错过 BPP | `SpringCoreBeansContainerLabTest` |
| `@Import(普通配置类)` | 定义层 | `ConfigurationClassParser#processImports` → 作为配置类继续解析 → `registerBeanDefinition` | 同上 | import 顺序与条件组合导致“看起来没生效” | `SpringCoreBeansImportLabTest` |
| `@Import(ImportSelector)` | 定义层（间接产出 className 列表） | `ImportSelector#selectImports` → 返回类名 → 继续按“配置类解析链路”注册 | 同上 | 误以为 selector “直接造对象”；忘了它只返回“要导入的类名” | `SpringCoreBeansImportLabTest` |
| `@Import(ImportBeanDefinitionRegistrar)` | 定义层（直接操作 registry） | `ImportBeanDefinitionRegistrar#registerBeanDefinitions` → `registry.registerBeanDefinition` | 同上 | registrar 没被 `ConfigurationClassPostProcessor` 发现（配置类没被解析） | `SpringCoreBeansImportLabTest` |
| `registerBeanDefinition/registerBean` | 定义层 | 调用的 API → `BeanDefinitionRegistry#registerBeanDefinition` | 同上 | 在 refresh 之后才注册：错过 BFPP/BDRPP 的定义层加工 | `SpringCoreBeansProgrammaticRegistrationLabTest` |
| `registerSingleton` | 实例层（单例缓存） | 调用的 API → `SingletonBeanRegistry#registerSingleton` | `DefaultSingletonBeanRegistry#singletonObjects` | 不会 retroactive 注入/BPP/init；`beanDefinitionMap` 里也没有 | `SpringCoreBeansProgrammaticRegistrationLabTest` |

#### 2.5.1 beanName、alias 与“名字变换”（不要和实例混在一起）

这一节补上“注册阶段经常被忽略、但排障时极关键”的名字层：

1) **beanName 从哪来？（生成入口）**

- 扫描：`@Component` 默认用 `AnnotationBeanNameGenerator`（短类名 decapitalize），也可用 `@Component("explicitName")` 显式指定（更细：见 3.3.3 的“断点闭环”）
- `@Bean`：默认以 **方法名** 作为 beanName；可用 `@Bean(name = "...")` 或 `@Bean(name = {"primary", "alias1", "alias2"})`
- 断点抓手：在 `registerBeanDefinition` 断点里直接看 `beanName`（不要只看类型）

2) **alias 从哪来？（alias 的入口）**

- `@Bean(name = {"primaryName", "aliasName"})`：第一个是主名，其余是 alias
- `ConfigurableBeanFactory#registerAlias(primaryName, aliasName)`：显式注册（常用于兼容旧名/灰度迁移）

3) **alias 影响什么？（影响 lookup 与某些“按名收敛”路径）**

- `getBean("aliasName")` 与 `getBean("primaryName")` 返回同一实例（alias 不会创建第二个对象）
- `@Resource` 是 name-first：字段名/显式 name 会先参与匹配（包含 alias 的情况）；因此重构字段名/alias 时更容易出现隐性回归
- `@Autowired` 的 by-name fallback（当候选>1 且缺少明确限定信号时）也可能命中 alias（它最终会走 “matches bean name” 的路径；详见 [第 14 章：03. 注入解析](014-03-dependency-injection-resolution.md) 的收敛决策树）

4) **交叉：`&` 前缀与 `scopedTarget.*`（名称看似相近，但语义存在分流）**

- `&beanName`：FactoryBean 场景下用于获取 “factory 本体”（见 [08. `FactoryBean`](08-factorybean.md)）
- `scopedTarget.<beanName>`：scoped proxy 会在容器里额外注册一个 target 定义（见 [第 15 章：04. Scope 与 prototype](015-04-scope-and-prototype.md)）

建议将“名字层”单独运行一次（避免后续把注入问题误判为注册问题）：

- 文档：[`22. Bean 名称与 alias：同一个实例，多一个名字`](../part-04-wiring-and-boundaries/22-bean-names-and-aliases.md)
- Lab：`SpringCoreBeansBeanNameAliasLabTest` / `SpringCoreBeansResourceInjectionLabTest`

### 2.6 证据链：3 分钟证明“注册了什么”（建议每次都按此流程）

需要把“注册入口”从概念变成肌肉记忆，关键是：每次都用同一套流程获取到证据链。

#### 2.6.1 证据链模板（通用）

1) 运行一个最小 Lab（噪音最少）
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

#### 2.6.2 用扫描入口运行一次（ComponentScan）

- 入口 Lab：`SpringCoreBeansComponentScanLabTest`
- 额外断点：`ClassPathBeanDefinitionScanner#doScan`
- 需要得到的结论：
  - `registerBeanDefinition` 被命中时，`beanDefinition.getSource()` 显示为“扫描来源”（通常可定位到 classpath/resource）

#### 2.6.3 用 @Import 入口运行一次（selector / registrar）

- 入口 Lab：`SpringCoreBeansImportLabTest`
- 额外断点：
  - `ImportSelector#selectImports`（selector 分支）
  - `ImportBeanDefinitionRegistrar#registerBeanDefinitions`（registrar 分支）
- 需要得到的结论：
  - selector 只是返回 className 列表；真正注册仍会落到 `registerBeanDefinition`
  - registrar 是“直接注册定义”的能力（它本身就是定义层操作）

#### 2.6.4 用 programmatic 对照一遍（定义层 vs 实例层）

- 入口 Lab：`SpringCoreBeansProgrammaticRegistrationLabTest`
- 额外断点：
  - `BeanDefinitionRegistry#registerBeanDefinition`（定义层）
  - `DefaultSingletonBeanRegistry#registerSingleton`（实例层）
- 需要得到的结论：
  - 定义层：后续会走 `doCreateBean → populateBean → initializeBean`（因此注入/回调/BPP 都会发生）
  - 实例层：只是把对象塞进 `singletonObjects`，不会 retroactive 触发注入与 BPP（常见“看起来交给 Spring 了但不生效”）

---

## 可复现闭环（基于 `SpringCoreBeansComponentScanLabTest`）

至少应能够用 3 个断言讲清楚“扫描注册”的关键结论：

1) **扫描注册写入的是 BeanDefinition，不是实例**
   - 断点：`ClassPathBeanDefinitionScanner#doScan` → `registerBeanDefinition`
   - 断言：`beanDefinitionMap` 增长但 `singletonObjects` 仍为空
2) **来源可以通过 source/factoryMethodName 反推**
   - 断点：`registerBeanDefinition`
   - 断言：`beanDefinition.getSource()` 指向扫描来源
3) **时机决定能否被后处理器观察到**
   - 断点：`invokeBeanFactoryPostProcessors`
   - 断言：注册发生在 BFPP/BDRPP 之前 → 定义可被加工

## 3. 注册发生在 refresh 的哪一段？（时机决定能力）

> 这一节是需要的“源码调用链到方法级”。目标不是背源码，而是：**应能够用断点把每条入口链路“走到落点”**。

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
- 因此，在排障时更关键的问题不是“是否已注册”，而是：

> **注册时机是否保证其能被关键处理器观察到？（尤其是 BFPP/BDRPP、BPP）**

方法级主线骨架（建议在 IDE 中按此顺序设置断点运行一次）：

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

可以发现：**scan / @Bean / @Import / registrar 这些“入口”大概率都落在 `processConfigBeanDefinitions` 这条链路里**（除非读者显式调用了 `context.register(...) / context.scan(...) / registry.registerBeanDefinition(...)`）。

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

读者只要把这条链路走通，就能回答两个高价值问题：

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

- 若发现 `ComponentScanAnnotationParser#parse` 从未命中，但 `ClassPathBeanDefinitionScanner#doScan` 命中了：说明读者走的是 `scan(...)` API，而不是 `@ComponentScan` 注解。

#### 3.3.3 beanName 是在哪一步生成的？（BeanNameGenerator）

很多“按名排障 / alias / by-name fallback”的问题，其难点在于：**beanName 到底由谁在什么时间点生成**。

- 扫描场景：`ClassPathBeanDefinitionScanner#doScan` 会对每个候选组件调用 `BeanNameGenerator#generateBeanName(candidate, registry)` 生成 beanName（默认实现是 `AnnotationBeanNameGenerator`）
- 断点闭环：在 `BeanNameGenerator#generateBeanName` 打断点，观察输入的 `BeanDefinition`（常见是 `ScannedGenericBeanDefinition`）与输出 `beanName`，即可解释“为何这个类最终叫这个名字”
- 注解注册场景（`AnnotationConfigApplicationContext#register` / `AnnotatedBeanDefinitionReader`）：同样会走 `BeanNameGenerator`（但 `@Bean` 工厂方法默认是方法名，不走 generator）
- 不要混淆：有些代码（尤其是 registrar / programmatic）会用 `BeanDefinitionReaderUtils.registerWithGeneratedName(...)`，它走的是 `BeanDefinitionReaderUtils#generateBeanName(...)` 这一套（不是 `BeanNameGenerator`）

### 3.4 `@Configuration` + `@Bean`：工厂方法定义是怎么注册的？

核心事实：`@Bean` 不会让 Spring “立刻调用方法造对象”，它首先会把 **工厂方法** 翻译成 BeanDefinition（定义层）。

方法级链路（注册定义）：

```
ConfigurationClassBeanDefinitionReader#loadBeanDefinitions(configClasses)
  -> loadBeanDefinitionsForConfigurationClass(configClass)
       -> loadBeanDefinitionsForBeanMethod(beanMethod)
            -> registry.registerBeanDefinition(beanName, beanDefinition)
```

关键观察点（在断点里应该能观察到）：

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

- 现象：在一个 `@Bean` 方法里直接调用另一个 `@Bean` 方法，却得到了“新对象”而不是容器单例
  - 优先检查：是否处于 “lite mode”（例如仅 `@Component`）或 `proxyBeanMethods=false`
  - 证据链：`ConfigurationClassEnhancer#enhance` 是否命中；配置类是否被增强为 CGLIB 子类（类名通常带 `$$`）
  - 对应章节：[第 18 章：07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）](018-07-configuration-enhancement.md)

### 3.5 `@Import`：selector / registrar 到底在链路上哪里分叉？

可以把 `@Import` 看成“配置类解析链路上的一个分支开关”：

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

一句话：**registrar 的能力上限更高（可动态造定义），但前提仍是：配置类解析链路被触发并执行**。

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
  - 证据链：可以发现 `doCreateBean/populateBean/initializeBean` 根本不命中
- 排障上，“定义存在/实例存在”必须拆开看：`containsBeanDefinition` vs `containsSingleton`

### 3.7 读者说的“属性绑定”：populateBean / BeanWrapper 的方法级入口在哪里？

虽然本章讲“注册”，但读者排障时经常需要回答：**“是否走到属性填充（populateBean）？”**
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

建议读者用这三个断点就能证明“属性填充发生了”：

- `AbstractAutowireCapableBeanFactory#populateBean`
- `AbstractAutowireCapableBeanFactory#applyPropertyValues`
- `AbstractPropertyAccessor#setPropertyValues`

对应深入分析章节：

- [30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）](../part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md)
- [36. 类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界](../part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md)

!!! warning "反例：过早 getBean（或实例层注册）会让读者“绕开管线”"

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
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


先给出一条“强制分层”的总规则（很重要）：

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
| `@Import` 看起来没生效 | 定义层 | `ConfigurationClassParser#processImports`；看是否命中 selector/registrar；最终是否落到 `registerBeanDefinition` | 触发类没被注册为配置类候选 / import 条件分支未命中（Conditional/Profile） | 先证明配置类解析链路已被触发并执行；再查 selector 返回值/registrar 是否被调用 | `SpringCoreBeansImportLabTest` |
| `registerBeanDefinition` 之后 BFPP/BDRPP 不生效 | 定义层（时机） | 看调用发生在 `refresh()` 哪一段；`invokeBeanFactoryPostProcessors` 之后再注册即错过定义层加工 | 在 refresh 之后才动态加定义 | 把注册前移到 refresh 前（或用 BDRPP 动态注册）；避免事后补定义期待 BFPP 生效 | `SpringCoreBeansProgrammaticRegistrationLabTest` |
| 定义有了但实例没创建 | 创建层 | `containsBeanDefinition=true` 且 `containsSingleton=false`；看是否命中 `preInstantiateSingletons/doGetBean` | bean 是 lazy-init / 从未触发 getBean / scope 不是 singleton | 明确触发创建（getBean/依赖触发）；排查 `@Lazy`/scope；需要时在 `preInstantiateSingletons` 断点验证 | 结合章节 13/16/23 |
| 实例存在但“注入/代理/回调不生效” | 注入/代理 | `containsSingleton=true` 但 `doCreateBean/populateBean/initializeBean` 从未命中；或 BPP 链不完整时就创建了 | 使用 `registerSingleton`；或过早 `getBean` 导致错过 BPP | **优先改为定义层注册**；避免在 BFPP/BDRPP 阶段触发目标 bean；必要时手工 `autowireBean/initializeBean`（明确风险） | `SpringCoreBeansProgrammaticRegistrationLabTest` |
| `@Bean` 方法调用返回“新对象” | 运行时语义 | `ConfigurationClassEnhancer#enhance` 是否命中；配置类是否被增强；看 `proxyBeanMethods` | lite mode / `proxyBeanMethods=false` / 直接方法调用绕开容器 | 需要语义时开启 `proxyBeanMethods=true`；或改为参数注入/从容器获取依赖而非直接调用方法 | 见第 18 章（07） |
| 同名 bean 冲突/覆盖（override/Conflicting） | 定义层 | `DefaultListableBeanFactory#registerBeanDefinition`；扫描场景也看 `ClassPathBeanDefinitionScanner#checkCandidate` | beanName 重复；Boot 默认禁止覆盖（多数场景） | 优先改名/限定扫描；确需覆盖再显式开启（谨慎） | 见第 24 章（overriding） |
| `FactoryBean` 注入/获取结果不符合预期 | 获取边界 | `AbstractBeanFactory#doGetBean` → `getObjectForBeanInstance`；看是否为 `FactoryBean` | 忘了 `beanName` 取的是“产品”；`&beanName` 才是工厂 | 需要工厂用 `&`；需要产品按产品类型注入；必要时检查 `getObjectType` 返回值 | 见第 08 章 |
| 候选太多/Qualifier 不生效 | 注入解析 | `DefaultListableBeanFactory#doResolveDependency` → `findAutowireCandidates/isAutowireCandidate`；看 `Qualifier`/`Primary` | 多候选未收敛；Qualifier 不匹配；按名称 fallback 误解 | 用 `@Qualifier/@Primary/@Resource` 明确收敛；必要时打印候选集合（或用 testsupport dumper） | 见第 14/33 章 |
| “观察到 BeanDefinition 了”但来源不明确 | 定义层取证 | 在 `registerBeanDefinition` 看 `beanDefinition.getSource()` / `factoryMethodName` / `role` | 只看了名字/类型，没看来源元数据 | 固化证据链：source + factoryMethodName + 入口断点（scan/@Bean/@Import） | 本章 2.6 |

### 5.3 常见误区与边界（压缩版 checklist）

1) **把 registerSingleton 当成“注册 bean”**
   - 它注册的是“对象”，不是“定义”；不会 retroactive 走注入/BPP/init。
2) **把“定义存在”当成“实例一定存在”**
   - `containsBeanDefinition` 只证明“定义进来了”；实例是否创建取决于 lazy/预实例化/是否触发 getBean。
   - 最短判断：`containsBeanDefinition`（定义层） vs `containsSingleton`（实例层缓存）。
3) **BeanDefinition 注册了，但候选选择/注入还是失败**
   - 优先确认是否进入 `findAutowireCandidates`；再核对 beanName/Qualifier/Primary 是否匹配（见 14/33 章）。
4) **代理/注解不生效**
   - 优先怀疑时机问题：目标 bean 是否在 BPP 链完整前被创建（见 25/31 章）。
5) **扫描看起来没生效**
   - 优先检查：配置类是否被解析（`ConfigurationClassPostProcessor` 是否执行到）、basePackage 是否正确、excludeFilters 是否把目标排除了。
6) **@Import 相关“没生效”**
   - selector/registrar 生效的前提仍是：配置类解析链路完成（`processConfigBeanDefinitions`）。应先证明“配置类解析已发生”，再分析 import 分支。

---

## 6. 面试/内训：标准答案（可复述）

### 6.1 统一答题结构（复述模板）

若想把本章用于面试或团队内训，建议统一用这套答题结构：

1) 一句话定义：Bean 注册的第一性对象是 `BeanDefinition`（定义），不是实例
2) 四类入口：scan / @Bean / @Import（selector+registrar）/ programmatic
3) 时机：它发生在 `refresh` 的定义层阶段，晚了就错过 BFPP/BDRPP 与 BPP 链
4) 反例：`registerSingleton` / 过早 getBean（绕开管线）
5) 证据链：给出一个 Lab + 关键断点 + 3 个观察点（source/beanName/factoryMethodName）

面试高频问法（建议至少能够答出 3 题）：

1) Spring 里“注册一个 Bean”到底注册的是什么？（BeanDefinition vs bean instance）
2) `registerBeanDefinition` vs `registerSingleton` 的根本差异是什么？为什么会影响注入/代理/回调？
3) `@ComponentScan`、`@Bean`、`@Import` 分别是谁在负责解析与注册？（关键处理器/落点）
4) 为什么说“注册时机决定能力”？如何用断点证明一次？
5) 在真实项目里遇到“注入没生效/代理没生效”，如何第一时间判断是不是“绕开了创建管线”？

团队内训可直接复用的课时脚本见：[`appendix/99-team-training-kit.md`](../appendix/99-team-training-kit.md)

### 6.2 面试标准答案（建议至少背熟 8 题）

> 下面每题都按“结论 → 证据链（方法级） → 反例/误区 → 加分项”的结构给出。

#### Q1：Spring 里“注册一个 Bean”到底注册的是什么？

- 结论：**注册的是 `BeanDefinition`（定义），不是“对象实例”。**
- 证据链（方法级）：
  - `DefaultListableBeanFactory#registerBeanDefinition`：定义层最终落点
  - 创建发生在后续：`AbstractBeanFactory#doGetBean` → `AbstractAutowireCapableBeanFactory#doCreateBean`
- 反例/误区：
  - `registerSingleton` 注册的是“对象缓存”，不会 retroactive 触发注入/BPP/init
- 加分项：
  - 讲清“定义层 vs 创建层”的分界线：`invokeBeanFactoryPostProcessors` vs `finishBeanFactoryInitialization`

#### Q2：`registerBeanDefinition` vs `registerSingleton` 根本差异是什么？为什么会影响注入/代理/回调？

- 结论：**前者让容器拥有“创建权”，后者只是把对象塞进单例缓存，绕开创建管线。**
- 证据链（方法级）：
  - 定义层：`registerBeanDefinition` → 后续 `doCreateBean` → `populateBean` → `initializeBean`
  - 实例层：`registerSingleton` → `DefaultSingletonBeanRegistry#addSingleton`（直接入 `singletonObjects`）
- 反例/误区：
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
- 反例/误区：
  - 以为 selector/registrar “自己就能生效”，忽略了前提：配置类解析链路必须被触发并执行
- 加分项：
  - 提到“最终落点统一”：`DefaultListableBeanFactory#registerBeanDefinition`

#### Q4：为什么说“注册时机决定能力”？如何用断点证明一次？

- 结论：**因为 BFPP/BDRPP/BPP 都有严格窗口期：晚了就错过定义层改写或代理注入。**
- 证据链（方法级）：
  - 时间窗：`refresh` → `invokeBeanFactoryPostProcessors`（定义层加工/注册）→ `registerBeanPostProcessors`（BPP 链）→ `preInstantiateSingletons`（创建）
- 反例/误区：
  - 在 BFPP/BDRPP 过程中触发 `getBean`：目标 bean 可能在 BPP 链完整前被创建，表现为“有时生效有时不生效”
- 加分项：
  - 讲清“一次证明”的方法：在 `registerBeanDefinition` 打断点，观察 `source/factoryMethodName`，再回到调用栈定位入口

#### Q5：ImportSelector vs ImportBeanDefinitionRegistrar 有什么区别？各适用什么场景？

- 结论：
  - **ImportSelector：返回“要导入的类名列表”，让它们继续走配置类解析链路。**
  - **Registrar：直接操作 registry，适合动态构建 BeanDefinition。**
- 证据链（方法级）：
  - selector：`ImportSelector#selectImports` → 回到 `loadBeanDefinitions` 注册
  - registrar：`loadBeanDefinitionsFromRegistrars` → `ImportBeanDefinitionRegistrar#registerBeanDefinitions` → `registry.registerBeanDefinition`
- 反例/误区：
  - 把 selector 当成“工厂”，以为它会创建对象（实际只返回类名）
- 加分项：
  - 讲到 DeferredImportSelector：为什么要“延迟”（用于排序/分组，常见于 Boot 自动配置）

#### Q6：为什么 `@Bean` 方法里直接调用另一个 `@Bean` 方法有时会出事？

- 结论：**因为是否“配置类增强”决定了方法调用是否会被拦截并转成容器取 bean。**
- 证据链（方法级）：
  - 增强：`ConfigurationClassPostProcessor#postProcessBeanFactory` → `ConfigurationClassEnhancer#enhance`
- 反例/误区：
  - lite mode / `proxyBeanMethods=false` 下直接方法调用会绕开容器，得到新对象（语义变化）
- 加分项：
  - 给出工程建议：优先用参数注入/构造注入表达依赖，而不是方法体里直接调用另一个 `@Bean` 方法

#### Q7：如何 1 分钟排查“扫不到 Bean”是扫描问题还是注册时机问题？

- 结论：**先判定“定义有没有进 registry”，再判定“配置类解析链路是否触发”。**
- 证据链（最短）：
  - `containsBeanDefinition(beanName)`（没有就是定义层问题）
  - `ConfigurationClassPostProcessor#processConfigBeanDefinitions` 是否命中（没命中多半是配置类没注册/没走 annotation context）
  - `ClassPathBeanDefinitionScanner#doScan` 是否命中（命中但没注册要看过滤器/冲突）
- 反例/误区：
  - 只盯着 `@ComponentScan`，忽略了 basePackage 与 filter 才是高频根因
- 加分项：
  - 能把排障过程落到本章 5.2 决策表的一行

#### Q8：BeanDefinition 有了但注入仍失败，读者下一步看什么？

- 结论：**这通常已经不是“注册问题”，而是“候选解析/Qualifier/Primary/泛型匹配”的注入解析问题。**
- 证据链（方法级）：
  - `DefaultListableBeanFactory#doResolveDependency` → `findAutowireCandidates` → `isAutowireCandidate`
- 反例/误区：
  - 把 `NoUniqueBeanDefinitionException` 当成“没注册”，实际是“注册过多但未收敛”
- 加分项：
  - 能解释 `@Resource` 更像按名称（见 32 章），以及 by-name fallback 的边界

## 面试常问（Bean 注册）

1) **为什么说“注册的第一性对象是 BeanDefinition”，而不是 bean instance？**
   - 要点：定义层注册的产物是“配方”，实例要等到创建阶段按定义产生；定义能被 BFPP/BDRPP 加工，实例能被 BPP 增强/换壳。

2) **定义层注册（registerBeanDefinition）与实例层注册（registerSingleton）有什么本质区别？**
   - 要点：定义层会走完整创建管线（注入/回调/代理）；实例层通常直接进单例缓存，不会 retroactive 补注入/补代理。

3) **`@Import`/Registrar 与 BDRPP 的边界是什么？为什么它们都在“定义层”但时机与能力不同？**
   - 要点：Import 体系发生在配置类解析阶段；BDRPP 是 refresh 早期的统一调度点，能动态加定义并影响后续 BFPP/BPP 链路。

## 自检要点
应能够用 3 句答题：

1) Bean 注册的“第一性对象”是什么？（提示：BeanDefinition，而不是实例）
2) `registerBeanDefinition` vs `registerSingleton` 的根本差异是什么？
3) 为什么“注册时机”会决定 AOP/注解/回调是否生效？
<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansComponentScanLabTest`，再用 `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`DefaultListableBeanFactory#registerBeanDefinition` / `DefaultSingletonBeanRegistry#registerSingleton`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“5. 排障决策表（注册相关：现象 → 分层 → 证据 → 修复）”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBeanDefinitionRegistrationDiffLabTest`
- Lab：`SpringCoreBeansComponentScanLabTest`
- Lab：`SpringCoreBeansImportLabTest`
- Lab：`SpringCoreBeansProgrammaticRegistrationLabTest`

上一章：[第 13 章：01. `ApplicationContext#refresh` 调用链（主线）](../part-00-guide/013-01-applicationcontext-refresh-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）](08-factorybean.md)

<!-- BOOKIFY:END -->
