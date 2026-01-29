# 第 25 章：90. 常见误区清单（建议反复对照）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：常见误区清单（建议反复对照）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 24 章：40. AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”](../part-05-aot-and-real-world/024-40-aot-and-native-overview.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 26 章：99. 自测题：是否能够真的理解了？](026-99-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障模板（统一结构）

当遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，建议按下面 6 步收敛（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：观察到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`
   - Branch Matrix - IoC 分支：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansIocBranchMatrixLabTest test`
   - Branch Matrix - 内部机制分支：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansInternalsBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[013-02-breakpoint-map.md](../part-00-guide/013-02-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[011-04-branch-decision-matrix.md](../part-00-guide/011-04-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[026-99-self-check.md](026-99-self-check.md)

- 本章主题：**90. 常见误区清单（建议反复对照）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，应能够用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见误区在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansProxyingPhaseLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest` / `SpringCoreBeansGenericTypeMatchingPitfallsLabTest` / `SpringCoreBeansTypeConversionLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：为每类误区补“最短证据链入口方法”链接（避免只给结论）。
    - B（边界反例）：为高频误区补“反例对照”：如何一眼区分相似但本质不同的问题（如 depends-on 环 vs 循环依赖）。
    - C（排障 SOP）：把误区映射到排障 SOP：遇到该误区的典型症状与第一断点入口。
    - D（断点观察）：断点建议：每类误区给最小断点组与 watch list。
    - E（面试复述）：把误区转成面试追问（追问“为什么/如何证明/反例是什么”）。
<!-- AE-DEEPENING:END -->
## 机制主线

本章把“常见误区”统一归因到 4 类主线：

1) **定义层**：BeanDefinition 注册/覆盖/条件装配
2) **实例层**：createBean → populateBean → initializeBean
3) **代理替换**：BPP 可能替换最终暴露对象
4) **依赖解析**：候选收集 → 收敛 → by-name/Qualifier/Primary

排障时先判层，再下断点，效率最高。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

## 常见误区与边界

> 这一节的目的不是“列口号”，而是把高频误判做成可复现的定位清单：每一条都能在本仓库的某个 Lab 里跑出来，并能下断点看见关键分支。

### 0. 复现入口（可运行）

- 入口测试：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test`

这份清单不是为了“背”，而是为了让在遇到问题时能快速定位：到底是概念没建立，还是机制未厘清。

### 1) 以为“prototype 每次方法调用都是新对象”

典型症状：

- prototype 注入 singleton 后怎么一直不变？

正确理解：

- prototype 是“每次向容器获取都新建”
- 直接注入只获取了一次

解决方案：

- `ObjectProvider`
- `@Lookup`
- scoped proxy（谨慎）

见：[04. Scope 与 prototype 注入陷阱](../part-01-ioc-container/015-04-scope-and-prototype.md)

- 现象：prototype 注入 singleton 后“看起来像单例”
- 证据链：`AbstractBeanFactory#doGetBean` → `AbstractAutowireCapableBeanFactory#populateBean`
- 修复：`ObjectProvider` / `@Lookup` / scoped proxy
- 验证：`SpringCoreBeansCustomScopeLabTest` / `SpringCoreBeansContainerLabTest`

### 2) 以为 `@Order` 能解决“单个依赖注入的歧义”

事实：

- `@Order` 更常用于集合注入的排序
- 单依赖选择优先看 `@Primary`、`@Qualifier` 等

见：[03. 依赖注入解析](../part-01-ioc-container/014-03-dependency-injection-resolution.md)

- 现象：集合注入顺序不稳定
- 证据链：`AnnotationAwareOrderComparator#sort`
- 修复：显式 `@Order`/`Ordered`，或使用 `orderedStream()`
- 验证：`SpringCoreBeansAutowireCandidateSelectionLabTest`

- 现象：写了 `@Qualifier` 但仍注入失败/注入错实现
- 证据链：`QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`
- 修复：确保注入点与候选都标注匹配；避免靠 `@Order`
- 验证：`SpringCoreBeansAutowireCandidateSelectionLabTest`

- 现象：单依赖注入报 `NoUniqueBeanDefinitionException`，误以为 `@Order` 能解决
- 证据链：`DefaultListableBeanFactory#doResolveDependency` → `determineAutowireCandidate`
- 修复：`@Qualifier` / `@Primary` / `@Priority` 收敛
- 验证：`SpringCoreBeansAutowireCandidateSelectionLabTest`

### 3) 在 `@Configuration(proxyBeanMethods=false)` 里互相调用 `@Bean` 方法

典型症状：

- 明明是单例，却出现多个实例（或者行为像多例）

推荐写法：

- 用 `@Bean` 方法参数声明依赖

见：[07. @Configuration 增强](../part-01-ioc-container/018-07-configuration-enhancement.md)

- 现象：`proxyBeanMethods=false` 时出现多实例/方法互调失效
- 证据链：`ConfigurationClassPostProcessor` → `ConfigurationClassEnhancer`
- 修复：改用方法参数注入或开启 `proxyBeanMethods=true`
- 验证：`SpringCoreBeansContainerLabTest`（lite/configuration 互调用例）

### 4) 把 `FactoryBean` 当作“普通 bean”

典型症状：

- `getBean("name")` 拿到的类型不对
- “怎么注入工厂本身？”

核心记忆：

- `"name"` → product
- `"&name"` → factory

见：[08. FactoryBean](../part-01-ioc-container/08-factorybean.md)

- 现象：`getBean("x")` 拿到的不是 FactoryBean 本体
- 证据链：`AbstractBeanFactory#getObjectForBeanInstance`（`&` 分支）
- 修复：需要工厂本体时使用 `&beanName`
- 验证：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`

### 5) 认为“循环依赖能跑起来就没问题”

事实：

- setter 循环能成功不代表设计合理
- 半初始化对象、代理、生命周期都会让问题变复杂
- Boot 环境里可能默认更严格，直接不让读者启动

见：[09. 循环依赖](../part-01-ioc-container/09-circular-dependencies.md)

- 现象：setter 循环勉强可启动，但运行期行为不稳定
- 证据链：`DefaultSingletonBeanRegistry#singletonFactories` / `getEarlyBeanReference`
- 修复：打破循环；避免构造器环；必要时引入代理或拆分职责
- 验证：`SpringCoreBeansCircularDependencyBoundaryLabTest`

### 6) 认为“自动装配就是自动注入”

事实：

- 自动装配主要是在启动时“导入配置并注册 BeanDefinition”
- 依赖注入解析仍遵循 Spring 容器规则

建议：

- 学会看条件报告（`--debug` / `debug=true`）

见：[10. Boot 自动装配](../part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md) 与 [11. 调试](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)

- 现象：以为“自动装配=自动注入”，实际只是定义层导入
- 证据链：`AutoConfigurationImportSelector` / `BeanDefinitionRegistry`
- 修复：区分“导入定义”与“实例化注入”，用条件报告定位
- 验证：`SpringCoreBeansAutoConfigurationLabTest`

### 7) 把 `applicationContext.getBean()` 当成日常依赖注入方式

事实：

- 这是 service locator 风格，会隐藏依赖关系，降低可测试性

建议：

- 默认用构造器注入
- 只有在确实需要“延迟/可选/按需获取”时才用 `ObjectProvider`
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`
推荐断点：`DefaultListableBeanFactory#doResolveDependency`、`AbstractAutowireCapableBeanFactory#populateBean`、`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

- 现象：服务定位导致依赖关系隐式化、测试困难
- 证据链：`DefaultListableBeanFactory#doResolveDependency`
- 修复：优先构造器注入；必要时 `ObjectProvider`
- 验证：`SpringCoreBeansLabTest`

### 8) 以为 `@Qualifier` 是“写了就行”

典型症状：

- 明明写了 `@Qualifier("xxx")`，还是注入失败
- 或者注入到了“看起来不对的那个实现”

事实：

- `@Qualifier` 的作用是 **缩小候选集合**，它不是“让容器更聪明”，而是“让读者把依赖关系写清楚”
- `@Qualifier` 匹配规则取决于 `AutowireCandidateResolver`（一般是 qualifier 元数据/beanName 等）

建议：

- 多实现时优先使用：**`@Qualifier`（精确）** 或 **`@Primary`（默认实现）**
- 不要指望 `@Order` 解决单依赖歧义（见误区 2）

如何验证：

- 对应 Lab/Test：`SpringCoreBeansAutowireCandidateSelectionLabTest#primaryOverridesPriority_forSingleInjection`

见：[03. 依赖注入解析](../part-01-ioc-container/014-03-dependency-injection-resolution.md)

### 9) 以为 `@Primary` 能“覆盖一切”

事实：

- `@Primary` 只是在“没有更强限定条件”时提供默认选择
- 一旦读者引入更强信号（例如 `@Qualifier`、`@Resource` 的 name-first），实际选择会以限定条件为准

如何验证：

- 对应 Lab/Test：`SpringCoreBeansAutowireCandidateSelectionLabTest`（优先级/primary 的对比）

见：[03. 依赖注入解析](../part-01-ioc-container/014-03-dependency-injection-resolution.md) 与 [`@Resource` 注入](../part-04-wiring-and-boundaries/32-resource-injection-name-first.md)

- 现象：`@Primary` 未生效，被 `@Qualifier/@Resource` 覆盖
- 证据链：`DefaultListableBeanFactory#determineAutowireCandidate`
- 修复：显式限定优先级，避免混用策略
- 验证：`SpringCoreBeansAutowireCandidateSelectionLabTest`

### 10) 以为集合注入的顺序“默认就稳定”

典型症状：

- `List<MySPI>` 注入后顺序在不同机器/不同版本下变化

事实：

- 不显式指定顺序时，顺序不应被依赖（读者很容易学到错误结论）
- `@Order`/`Ordered` 才是读者做“确定性顺序”的工具

如何验证：

- 对应 Lab/Test：`SpringCoreBeansAutowireCandidateSelectionLabTest#orderAnnotation_affectsCollectionInjectionOrder`

见：[03. 依赖注入解析](../part-01-ioc-container/014-03-dependency-injection-resolution.md)

### 11) 以为 `@PostConstruct` 发生在“构造器之前”

事实：

- `@PostConstruct` 发生在：实例化完成 + 依赖注入完成之后（属于初始化阶段的一部分）
- 它依赖后处理器触发（不是 Java 语法自带能力），见 [12. 容器启动与基础设施处理器：为什么注解能工作？](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)

如何验证：

- 对应 Lab/Test：`SpringCoreBeansLifecycleCallbackOrderLabTest#singletonLifecycleCallbacks_happenInAStableOrderAroundInitialization`

见：[05. 生命周期：初始化、销毁与回调](../part-01-ioc-container/016-05-lifecycle-and-callbacks.md) 与 [12. 容器启动与基础设施处理器](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)

- 现象：以为 `@PostConstruct` 在构造器前触发
- 证据链：`AbstractAutowireCapableBeanFactory#initializeBean` → `InitDestroyAnnotationBeanPostProcessor`
- 修复：构造器只做轻量初始化，依赖使用放到初始化阶段
- 验证：`SpringCoreBeansLifecycleCallbackOrderLabTest`

### 12) 以为 BPP “只是改属性”，不会把 bean 换成另一个对象

事实：

- `BeanPostProcessor#postProcessAfterInitialization` 可以直接返回另一个对象（最常见就是 proxy）
- 因此容器最终对外暴露的 bean，可能不是编写的那个原始实例

如何验证：

- 对应 Lab/Test：`SpringCoreBeansProxyingPhaseLabTest#beanPostProcessorCanReturnAProxyAsTheFinalExposedBean_andSelfInvocationStillBypassesTheProxy`

见：[31. 代理/替换阶段：BPP 如何把 Bean 换成 Proxy](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)

- 现象：bean 被代理/替换导致类型不匹配或自调用失效
- 证据链：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`
- 修复：确认代理类型（JDK/CGLIB），避免 self-invocation
- 验证：`SpringCoreBeansProxyingPhaseLabTest`

### 13) 以为循环依赖“只要能启动就等于没问题”

事实：

- setter 循环能救，靠的是“提前暴露引用”（early singleton exposure），这意味着读者可能拿到半初始化对象
- 一旦代理介入，early 与 final 不一致会让问题更隐蔽（见 [16. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？](../part-03-container-internals/16-early-reference-and-circular.md)）

如何验证：

- 对应 Lab/Test：`SpringCoreBeansEarlyReferenceLabTest#getEarlyBeanReference_canProvideEarlyProxyDuringCircularDependencyResolution`

见：[09. 循环依赖](../part-01-ioc-container/09-circular-dependencies.md) 与 [16. early reference 与循环依赖](../part-03-container-internals/16-early-reference-and-circular.md)

- 现象：循环依赖启动成功但运行期异常/代理不一致
- 证据链：`DefaultSingletonBeanRegistry#addSingletonFactory` / `getEarlyBeanReference`
- 修复：拆分依赖/引入事件/避免构造器环
- 验证：`SpringCoreBeansEarlyReferenceLabTest`

### 14) 以为 `FactoryBean` 只影响 `getBean("name")` 的返回值

事实：

- `FactoryBean` 还会影响：type matching、缓存语义、按类型发现（尤其 `getObjectType()`）
- `getObjectType=null` + `allowEagerInit=false` 会导致“按类型找不到但按名字能拿到”的边界

如何验证：

- 对应 Lab/Test：`SpringCoreBeansFactoryBeanEdgeCasesLabTest#factoryBeanWithNullObjectType_isNotDiscoverableByTypeWithoutEagerInit_butCanStillBeRetrievedByName`

见：[08. FactoryBean](../part-01-ioc-container/08-factorybean.md)、[23. FactoryBean 深潜](../part-04-wiring-and-boundaries/23-factorybean-deep-dive.md)、[29. FactoryBean 边界](../part-04-wiring-and-boundaries/29-factorybean-edge-cases.md)

- 现象：按类型扫描/条件判断找不到 FactoryBean product
- 证据链：`FactoryBeanRegistrySupport#getTypeForFactoryBean` / `isTypeMatch`
- 修复：保证 `getObjectType()` 稳定；必要时允许 eager init
- 验证：`SpringCoreBeansFactoryBeanEdgeCasesLabTest`

### 15) 以为 `proxyBeanMethods=false` 只是“性能优化”，不会影响语义

事实：

- `proxyBeanMethods=false` 会让配置类内部的 `@Bean` 方法互调变成普通 Java 调用，可能 new 出额外对象
- 推荐写法是用 `@Bean` 方法参数声明依赖（两种模式都正确）

如何验证：

- 对应 Lab/Test：
  - `SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsTruePreservesSingletonSemanticsForBeanMethodCalls`
  - `SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsFalseAllowsDirectMethodCallToCreateExtraInstance`

- 现象：`proxyBeanMethods=false` 导致互调产生新实例
- 证据链：`ConfigurationClassEnhancer` / `@Configuration` 代理是否生效
- 修复：改为参数注入或开启 `proxyBeanMethods=true`
- 验证：`SpringCoreBeansContainerLabTest`

见：[07. @Configuration 增强](../part-01-ioc-container/018-07-configuration-enhancement.md)

### 16) 以为“按泛型找 bean（Handler<String>）一定可靠”

典型症状：

- 按原始类型 `Handler` 能找到候选，但按 `Handler<String>`（带泛型）找不到
- 读者明明觉得“这个实现就是 String 版本”，但容器无法证明

事实：

- Spring 的泛型匹配依赖 `ResolvableType`
- 一旦候选 bean 在运行时丢失了泛型信息（常见原因：JDK 动态代理、手工注册 singleton 实例等），按泛型匹配就可能失配

如何验证：

- 对应 Lab/Test：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest#genericTypeMatching_canFailWhenCandidateLosesGenericInformation_likeJdkProxySingleton`

见：[37. 泛型匹配与注入误区](../part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md)

- 现象：按泛型类型找不到候选（但按原始类型可用）
- 证据链：`GenericTypeAwareAutowireCandidateResolver#checkGenericTypeMatch`
- 修复：避免让候选退化为代理实例；显式提供 target type
- 验证：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest`

### 17) 以为“类型转换只发生在 @Value”，与 BeanDefinition/属性填充无关

典型症状：

- 在 BFPP 里把 property value 写成字符串（例如 `"8080"`），却发现最终注入到 `int` 属性里变成了数字
- 或者自定义值对象注入失败，不知道该在哪注册 Converter

事实：

- 属性填充阶段（populateBean）会通过 `BeanWrapper` 写入属性，并触发类型转换
- `@Value` 的链路是“先解析字符串，再转换为目标类型”

如何验证：

- 对应 Lab/Test：`SpringCoreBeansTypeConversionLabTest`

见：[36. 类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界](../part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md)

- 现象：定义层字符串在属性填充阶段被转换成目标类型
- 证据链：`AbstractAutowireCapableBeanFactory#applyPropertyValues` → `TypeConverterDelegate#convertIfNecessary`
- 修复：注册 ConversionService/PropertyEditor；区分占位符与转换
- 验证：`SpringCoreBeansTypeConversionLabTest`

### 18) 以为 `@Autowired` 永远只按类型，不会按名字回退（by-name fallback）

典型症状：

- 明明容器里有多个同类型候选，读者没写 `@Qualifier/@Primary`，却没有报歧义
- 或者在重构/改字段名后，突然开始报 `NoUniqueBeanDefinitionException` 或注入到了“另一个实现”

事实：

- `@Autowired` 的候选决胜过程中，可能会出现 **by-name fallback**：用“依赖名（dependency name）匹配 beanName”收敛候选
- 它属于“隐式规则”：一旦依赖名变化（字段名/参数名/注入点 name 变化），结果就会变化

如何验证：

- 对应 Lab/Test：
  - `SpringCoreBeansAutowireCandidateSelectionLabTest#byNameFallback_canResolveSingleInjectionAmbiguity_forAutowiredFieldInjection`
  - `SpringCoreBeansAutowireCandidateSelectionLabTest#primaryOverridesByNameFallback_forSingleInjection`

推荐断点：

- `DefaultListableBeanFactory#determineAutowireCandidate`
- `DefaultListableBeanFactory#doResolveDependency`

建议：

- 生产代码里不要“依赖 by-name fallback 的侥幸收敛”，优先显式表达依赖关系：`@Qualifier` / `@Primary`

- 现象：多候选下 `@Autowired` 未报错，重构后行为变化
- 证据链：`DefaultListableBeanFactory#determineAutowireCandidate`（by-name 分支）
- 修复：显式 `@Qualifier` / `@Primary`，避免隐式 by-name
- 验证：`SpringCoreBeansAutowireCandidateSelectionLabTest`

### 19) 混淆 `ObjectProvider#getIfAvailable()` 与 `getIfUnique()`（以及多候选时的行为）

典型症状：

- 容易误以为 `ObjectProvider` “永远不会失败”，结果在多候选时仍然抛异常（或返回不符合预期的对象）
- 容易误以为 `getIfAvailable()` 与 `getIfUnique()` 都是“拿不到就 null”，但它们语义不同

事实：

- `getIfUnique()` 的核心语义是：**只有唯一候选时才返回，否则返回 null**
- `ObjectProvider` 的意义不是“让容器更聪明”，而是让读者把“可选/延迟/多候选”这些语义写清楚

如何验证：

- 对应 Lab/Test：`SpringCoreBeansAutowireCandidateSelectionLabTest#objectProvider_getIfUnique_returnsNull_whenMultipleCandidatesExist`

推荐断点：

- `DefaultListableBeanFactory#doResolveDependency`
- `DefaultListableBeanFactory#resolveDependency`

- 现象：多候选时 `getIfUnique()` 返回 null / `getIfAvailable()` 行为不符合预期
- 证据链：`DefaultListableBeanFactory#resolveDependency`
- 修复：明确语义（唯一性 vs 可用性），必要时显式限定
- 验证：`SpringCoreBeansAutowireCandidateSelectionLabTest`

### 20) 以为 `@Primary` “覆盖一切”，忽略了更强的限定信号（`@Qualifier` / `@Resource`）

典型症状：

- 容器里明明有一个 `@Primary`，但最终注入的却是另一个实现
- 或者读者看见了 `@Primary`，就下意识认为“这就是默认实现”，却忘了注入点可能带了更强限定

事实：

- `@Primary` 只是“默认胜者”，它只在 **没有更强信号** 时才提供默认选择
- 更强的限定信号包括（但不限于）：
  - `@Qualifier`（显式缩小候选集合/指定目标）
  - `@Resource` 的 name-first（按名字优先匹配，见 32 章）

如何验证：

- 对应 Lab/Test：`SpringCoreBeansAutowireCandidateSelectionLabTest#qualifierOverridesPrimary_forSingleInjection`

推荐断点：

- `DefaultListableBeanFactory#doResolveDependency`
- `DefaultListableBeanFactory#determineAutowireCandidate`

- 现象：`@Primary` 未生效，最终注入被更强限定覆盖
- 证据链：`DefaultListableBeanFactory#determineAutowireCandidate`
- 修复：明确限定规则优先级；避免混用导致误判
- 验证：`SpringCoreBeansAutowireCandidateSelectionLabTest`

## 面试常问（把“误区”说成标准答案）

> 目标：读者不是背“误区列表”，而是能把“现象 → 结论 → 证据链（方法级）→ 修复”说成一段可复述答案。
> 建议配合：`appendix/93-interview-playbook.md`（答题模板）与 `appendix/94-production-troubleshooting-checklist.md`（排障分流）。

### Q1：`@Order` / `Ordered` 能解决单依赖注入的多候选歧义吗？

- 标准答案（可复述）：
  - 不能。`@Order` 主要影响“集合注入/链路执行顺序”，单依赖的 winner 选择走的是候选收敛规则（`@Primary` / `@Qualifier` / by-name fallback 等），不是排序规则。
- 证据链（方法级）：
  - 候选收集：`DefaultListableBeanFactory#findAutowireCandidates`
  - winner 收敛：`DefaultListableBeanFactory#determineAutowireCandidate`
  - 集合排序：`DefaultListableBeanFactory#resolveMultipleBeanCollection`（或同类分支）
- 最小复现：
  - `SpringCoreBeansAutowireCandidateSelectionLabTest`（单依赖歧义/候选收敛）

### Q2：为什么 `@Qualifier` 通常“压过” `@Primary`？

- 标准答案（可复述）：
  - `@Primary` 是“默认胜者”，前提是候选集合没有被更强的限定条件缩小；`@Qualifier` 属于“强限定”，会先参与候选过滤/匹配，让不匹配的候选直接出局，后续再在剩余集合里考虑 `@Primary`。
- 证据链（方法级）：
  - 限定过滤：`AutowireCandidateResolver#isAutowireCandidate`（实现通常包含 Qualifier 逻辑）
  - 最终收敛：`DefaultListableBeanFactory#determineAutowireCandidate`
- 最小复现：
  - `SpringCoreBeansAutowireCandidateSelectionLabTest#qualifierOverridesPrimary_forSingleInjection`

### Q3：`@Autowired` 的 by-name fallback 和 `@Resource` 的 name-first 有什么本质差异？

- 标准答案（可复述）：
  - `@Resource` 是“规范定义的 name-first”，先按名字找，再按类型；`@Autowired` 的 by-name fallback 是框架行为且只在特定条件下触发（单依赖歧义/参数名可得性等），两者不是同一个机制。
- 证据链（方法级）：
  - `@Resource` 注入入口：`CommonAnnotationBeanPostProcessor#postProcessProperties`
  - `@Autowired` 注入入口：`AutowiredAnnotationBeanPostProcessor#postProcessProperties`
  - 依赖解析核心：`DefaultListableBeanFactory#doResolveDependency`
- 最小复现：
  - `SpringCoreBeansResourceInjectionLabTest`（`@Resource` name-first）
  - `SpringCoreBeansAutowireCandidateSelectionLabTest`（by-name fallback 边界）

### Q4：循环依赖为什么“构造器死、setter 可能活”？三层缓存解决什么、不解决什么？

- 标准答案（可复述）：
  - setter 循环依赖可能在“提前暴露 early reference”的窗口期被打断；构造器注入没有“先实例化再注入”的窗口，通常 fail-fast。三层缓存的核心是：支持 early reference 的按需生成与区分 early/final，但它不承诺解决所有循环（比如构造器循环、或 raw/wrapped 不一致导致的失败）。
- 证据链（方法级）：
  - 三层缓存入口：`DefaultSingletonBeanRegistry#getSingleton`
  - early exposure：`DefaultSingletonBeanRegistry#addSingletonFactory`
  - early 形态：`AbstractAutowireCapableBeanFactory#getEarlyBeanReference`
- 最小复现：
  - `SpringCoreBeansCircularDependencyBoundaryLabTest`
  - `SpringCoreBeansEarlyReferenceLabTest`

### Q5：为什么“代理导致类型不匹配”在面试里经常出现？如何给出修复建议？

- 标准答案（可复述）：
  - JDK 动态代理只实现接口，不是目标类的子类；若按具体类类型注入/强转，会失败。修复建议通常是：按接口注入、或改用 class-based proxy（CGLIB）、或在设计层避免在容器早期阶段触发代理相关时序问题。
- 证据链（方法级）：
  - 代理替换发生点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`
  - 代理/增强触发者：具体 BPP（如 AOP 相关 post-processor）
- 最小复现：
  - `SpringCoreBeansProxyingPhaseLabTest`

## 自检要点
- 应能够否做到：拿到一个现象（注入失败/拿到 proxy/占位符没解析/启动阶段异常）就能先分层（定义层 vs 实例层），并跳到对应章节与 Lab？
- 应能够否明确区分三件事：**候选选择（谁赢）**、**集合排序（谁先谁后）**、**初始化顺序（谁先创建）**？
- 应能够否把“猜测”变成“证据链”：用一个 LabTest + 断点 + watch list 把结论固定为可复现事实？

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansProxyingPhaseLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest` / `SpringCoreBeansGenericTypeMatchingPitfallsLabTest` / `SpringCoreBeansTypeConversionLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`

上一章：[50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象](../part-05-aot-and-real-world/50-property-editor-and-value-resolution.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[91. 术语表（Glossary）](91-glossary.md)

<!-- BOOKIFY:END -->
