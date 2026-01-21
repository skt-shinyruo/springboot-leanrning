# 92. 知识点地图（Concept → Chapter → Lab）

## 导读

- 本章主题：**92. 知识点地图（Concept → Chapter → Lab）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest` / `SpringCoreBeansProxyingPhaseLabTest` / `SpringCoreBeansPreInstantiationLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansGenericTypeMatchingPitfallsLabTest` / `SpringCoreBeansTypeConversionLabTest` / `SpringCoreBeansXmlBeanDefinitionReaderLabTest` / `SpringCoreBeansAutowireCapableBeanFactoryLabTest` / `SpringCoreBeansSpelValueLabTest` / `SpringCoreBeansCustomQualifierLabTest` / `SpringCoreBeansXmlNamespaceExtensionLabTest` / `SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest` / `SpringCoreBeansPropertyEditorLabTest` / `SpringCoreBeansBeanDefinitionValueResolutionLabTest` / `SpringCoreBeansEnvironmentPropertySourceLabTest` / `SpringCoreBeansBeanFactoryApiLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`

## 机制主线

这份“知识点地图”用于两类阅读方式：

---

## 1) 顺读主线（最推荐）

---

### 2.1 注入失败（NoSuch / NoUnique / UnsatisfiedDependency）

- 主入口章节：`03`（依赖解析主线） / `33`（候选收敛边界） / `11`（异常分型与断点入口）
- 主入口 Lab（方法级）：`SpringCoreBeansInjectionAmbiguityLabTest#singleInjectionFailsFast_whenMultipleCandidatesExist_andNoPrimaryOrQualifierIsPresent`
- 推荐断点：`DefaultListableBeanFactory#doResolveDependency`、`DefaultListableBeanFactory#findAutowireCandidates`

### 2.2 prototype 注入 singleton 后“像单例”

- 主入口章节：`04`（scope/prototype 注入陷阱与解法）
- 主入口 Lab（方法级）：`SpringCoreBeansLabTest#demonstratesPrototypeScopeBehavior`
- 推荐断点：`AbstractBeanFactory#doGetBean`（对照 direct vs provider 的调用路径差异）

### 2.3 `@Value` 不严格/值变成 `"${...}"`

- 主入口章节：`34`（embedded value resolver：non-strict vs strict）
- 主入口 Lab（方法级）：`SpringCoreBeansValuePlaceholderResolutionLabTest#defaultEmbeddedValueResolver_resolvesExistingProperty_butLeavesMissingPlaceholderUnresolved`
- 推荐断点：`AbstractBeanFactory#resolveEmbeddedValue`、`PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`

### 2.4 代理导致行为“不符合直觉”（self-invocation、最终对象不是原始实例）

- 主入口章节：`31`（BPP 替换/包装发生点） / `11`（如何锁定是哪一个 BPP 换壳）
- 主入口 Lab（方法级）：`SpringCoreBeansProxyingPhaseLabTest#beanPostProcessorCanReturnAProxyAsTheFinalExposedBean_andSelfInvocationStillBypassesTheProxy`
- 推荐断点：`AbstractAutowireCapableBeanFactory#initializeBean`、`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

### 2.5 循环依赖：为什么 setter 能救、constructor 无解？

- 主入口章节：`09`（循环依赖概览） / `16`（early reference 的关键分支）
- 主入口 Lab（方法级）：`SpringCoreBeansContainerLabTest#circularDependencyWithSettersMaySucceedViaEarlySingletonExposure`
- 推荐断点：`DefaultSingletonBeanRegistry#getSingleton`、`DefaultSingletonBeanRegistry#addSingletonFactory`

### 2.6 “顺序”导致结果不同（post-processors、链路、排序误用）

- 主入口章节：`14`（PriorityOrdered/Ordered 的分段与排序）
- 主入口 Lab（方法级）：`SpringCoreBeansPostProcessorOrderingLabTest#beanPostProcessors_areAppliedInPriorityOrderedThenOrderedThenUnorderedOrder`
- 推荐断点：`PostProcessorRegistrationDelegate#registerBeanPostProcessors`（排序与注册链路）

### 2.7 泛型匹配“看起来有，但按泛型找不到”

- 主入口章节：`37`（ResolvableType 泛型匹配边界）
- 主入口 Lab（方法级）：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest#genericTypeMatching_canFailWhenCandidateLosesGenericInformation_likeJdkProxySingleton`
- 推荐断点：`ResolvableType#forClass`（理解泛型信息从哪来/在哪丢）

### 2.8 类型转换：字符串为何能注入到 int/自定义类型？

- 主入口章节：`36`（BeanWrapper/ConversionService）
- 主入口 Lab（方法级）：`SpringCoreBeansTypeConversionLabTest#stringPropertyValue_canBeConvertedToIntDuringPopulateBean`
- 推荐断点：`BeanWrapperImpl#setPropertyValue`、`TypeConverter#convertIfNecessary`

### 2.9 AOT/Native：RuntimeHints（构建期契约）

- 主入口章节：`41`（RuntimeHints 入门）
- 主入口 Lab（方法级）：`SpringCoreBeansAotRuntimeHintsLabTest#runtimeHintsRegistrar_canRegisterReflectionHints_forAType`
- 推荐断点：`RuntimeHintsRegistrar#registerHints`

### 2.10 定义层失败：BeanDefinitionStoreException（XML/资源/解析）

- 主入口章节：`42`（XML → BeanDefinitionReader） / `11`（异常分型与断点入口）
- 主入口 Lab（方法级）：`SpringCoreBeansExceptionNavigationLabTest#beanDefinitionStoreException_invalidXml`
- 推荐断点：`XmlBeanDefinitionReader#loadBeanDefinitions`、`DefaultListableBeanFactory#registerBeanDefinition`

### 2.12 SpEL：`@Value("#{...}")` 的表达式解析链路

- 主入口章节：`44`（SpEL 与值注入链路）
- 主入口 Lab（方法级）：`SpringCoreBeansSpelValueLabTest#valueWithSpel_canReferenceBeanAndResultIsConvertedToTargetType`
- 推荐断点：`StandardBeanExpressionResolver#evaluate`、`AbstractBeanFactory#resolveEmbeddedValue`

### 2.13 自定义 Qualifier（meta-annotation）与候选收敛

- 主入口章节：`45`（自定义 Qualifier） / `03`（DI 主线）
- 主入口 Lab（方法级）：`SpringCoreBeansCustomQualifierLabTest#customQualifierMetaAnnotation_canNarrowDownCandidates_forSingleInjection`
- 推荐断点：`QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`

### 2.14 XML 自定义 namespace：`<tx:...>` 这类元素怎么注册 BeanDefinition？

- 主入口章节：`46`（XML namespace 扩展）
- 主入口 Lab（方法级）：`SpringCoreBeansXmlNamespaceExtensionLabTest#customNamespaceHandler_isDiscoveredViaSpringHandlers_andCanRegisterBeanDefinitions`
- 推荐断点：`NamespaceHandlerResolver#resolve`（handler 发现）、`BeanDefinitionParserDelegate`（element parse）

### 2.15 product vs factory：为什么 `getBean("x")` 不是你以为的那个对象？

- 主入口章节：`08`（FactoryBean 基础语义） / `49`（内置 FactoryBean 图鉴）
- 主入口 Lab（方法级）：`SpringCoreBeansBuiltInFactoryBeansLabTest#builtInFactoryBeans_methodInvoking_and_serviceLocator_and_factoryDereference`
- 推荐断点：`AbstractBeanFactory#getObjectForBeanInstance`（& 前缀分支）

### 2.16 值解析与类型转换：引用/集合/字符串到底在哪一步“变成对象”？

- 主入口章节：`50`（BeanDefinitionValueResolver/PropertyEditor） / `36`（类型转换）
- 主入口 Lab（方法级）：`SpringCoreBeansBeanDefinitionValueResolutionLabTest#beanDefinitionValueResolver_canResolve_typedString_reference_and_managedCollections`
- 推荐断点：`BeanDefinitionValueResolver#resolveValueIfNecessary`

---

## 3) 一页纸：核心概念到章节的映射

---

- **定义层（BeanDefinition）**：`01`（心智模型）→ `02`（注册）→ `35`（merged）
- **实例层（create/populate/initialize）**：`03`（依赖解析）→ `05`/`17`（生命周期）→ `31`（代理/替换）
- **扩展点与顺序**：`06`（BFPP/BPP）→ `13`（BDRPP）→ `14`（排序）
- **排障入口**：`11`（从异常到断点入口）→ `94`（生产排障清单）→ `99`（自测题）
- **真实世界补齐（AOT/XML/SpEL/Reader/FactoryBean）**：`40–50`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 本章为索引/术语类内容，不直接提供单一 Lab 入口。
- 建议做法：从本页跳转到对应章节后，按章节中的“复现入口（可运行）”运行对应 Test。
- 如果你是“按 API 名称反查章节/Lab”：先看 `95`（Public API 索引），再用 `96`（Gap 清单）决定下一步补齐方向。
- 如果你要做“更深的断点观察（不影响默认回归）”：看 `97`（Explore/Debug 用例）并用显式开关运行。

1) **顺读成体系**：按 Part 00 → 01 → 02 → 03 → 04 → Appendix
2) **按问题跳读**：按“症状/异常/现象”快速定位到章节与 Lab（可直接跑，立刻复现）

如果你希望对 Spring Bean 有“全面透彻的理解”，建议按这个顺序读并跑对应 Labs：

1. `00`：深挖指南（把学习变成可重复实验）
2. `01–09`：IoC 主线（BeanDefinition → 注册 → 注入 → scope → 生命周期 → 扩展点 → 循环依赖）
3. `10–11`：Boot 自动装配与排障（为什么有/为什么没有）
4. `12–17`：容器内部机制（顺序/短路/early reference/回调顺序）
5. `18–37`：边界与高级语义（lazy/dependsOn/resolvable/alias/FactoryBean/代理/value/merged/转换/泛型）
6. `40–50`：AOT/真实世界补齐（RuntimeHints/XML/容器外对象/SpEL/自定义 Qualifier/XML namespace 扩展/其它 Reader/方法注入/内置 FactoryBean/值解析）
7. `90/99`：坑点复盘与自测

## 2) 按问题跳读（症状 → 章节 → Lab）

- 章节：
  - `03` 依赖注入解析（候选收敛）
  - `33` `@Primary/@Priority/@Order` 的边界
  - `11` 调试与可观察性（从异常到断点）
- Labs：
  - `SpringCoreBeansInjectionAmbiguityLabTest`
  - `SpringCoreBeansAutowireCandidateSelectionLabTest`
  - `SpringCoreBeansExceptionNavigationLabTest`

- 章节：`04` Scope 与 prototype 注入陷阱
- Labs/Exercises：
  - `SpringCoreBeansLabTest`（prototype 相关用例）
  - `SpringCoreBeansExerciseTest`（改造题）

- 章节：`34` `@Value("${...}")` 占位符解析
- Lab：`SpringCoreBeansValuePlaceholderResolutionLabTest`

- 章节：
  - `31` 代理产生阶段：BPP 如何把 Bean 换成 Proxy
  - `15/16`（pre/early）代理与短路/early reference
- Labs：
  - `SpringCoreBeansProxyingPhaseLabTest`
  - `SpringCoreBeansPreInstantiationLabTest`
  - `SpringCoreBeansEarlyReferenceLabTest`

- 章节：`09` 循环依赖概览 + `16` early reference 与循环依赖
- Labs：
  - `SpringCoreBeansContainerLabTest`（constructor vs setter）
  - `SpringCoreBeansEarlyReferenceLabTest`

- 章节：`14` 顺序（PriorityOrdered/Ordered/无序）
- Labs：`SpringCoreBeansPostProcessorOrderingLabTest`

- 章节：`37` 泛型匹配与注入坑
- Lab：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest`

- 章节：`36` 类型转换（BeanWrapper/ConversionService）
- Lab：`SpringCoreBeansTypeConversionLabTest`

- 章节：`40` AOT/Native 总览 + `41` RuntimeHints 入门
- Labs：
  - `SpringCoreBeansAotRuntimeHintsLabTest`
  - `SpringCoreBeansAotFactoriesLabTest`（`aot.factories` / `AotServices` / AOT processors 发现入口）

- 章节：
  - `42` XML → BeanDefinitionReader（定义层解析与错误分型）
  - `11` 调试与可观察性（异常分型与断点入口）
- Labs：
  - `SpringCoreBeansXmlBeanDefinitionReaderLabTest`
  - `SpringCoreBeansExceptionNavigationLabTest`（invalid XML 示例）

- 章节：`43` 容器外对象注入：AutowireCapableBeanFactory
- Lab：`SpringCoreBeansAutowireCapableBeanFactoryLabTest`

- 章节：`44` SpEL 与 `@Value("#{...}")`
- Lab：`SpringCoreBeansSpelValueLabTest`

- 章节：`45` 自定义 Qualifier：meta-annotation 与候选收敛
- Lab：`SpringCoreBeansCustomQualifierLabTest`

- 章节：`46` XML namespace 扩展：NamespaceHandler / Parser / spring.handlers
- Labs：
  - `SpringCoreBeansXmlNamespaceExtensionLabTest`

- 章节：
  - `49` 内置 FactoryBean 图鉴（`&beanName` 与典型内置 FactoryBean）
  - `08` FactoryBean：product vs factory（基础语义）
- Lab：`SpringCoreBeansBuiltInFactoryBeansLabTest`
- Lab：`SpringCoreBeansServiceLoaderFactoryBeansLabTest`（ServiceLoader* 系列内置 FactoryBean）

- 章节：
  - `50` PropertyEditor 与 BeanDefinition 值解析
  - `36` 类型转换（BeanWrapper/ConversionService/PropertyEditor 的边界）
- Labs：
  - `SpringCoreBeansPropertyEditorLabTest`
  - `SpringCoreBeansBeanDefinitionValueResolutionLabTest`

### 2.17 配置值“不生效/被覆盖”：Environment/PropertySource 的优先级怎么断点证明？

- 章节：`38` Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线
- Labs：
  - `SpringCoreBeansEnvironmentPropertySourceLabTest`
  - `SpringCoreBeansValuePlaceholderResolutionLabTest`（占位符 strict vs non-strict）

- 章节：
  - `39` BeanFactory API 深挖：接口族谱与手动 bootstrap 的边界
  - `12` 容器启动与基础设施处理器：为什么注解能工作
- Labs：
  - `SpringCoreBeansBeanFactoryApiLabTest`
  - `SpringCoreBeansBootstrapInternalsLabTest`

> 这张映射用于“复述式学习”：你能把概念准确指向章节与断点入口，就意味着你真的掌握了它的边界。

## 常见坑与边界

### 2.18 “注解为什么不生效”：BeanFactory API 的边界与手动 bootstrap

- Bean 三层模型（定义/实例/代理）→ `01`、`15/16/31`
- 注册入口（scan/@Bean/@Import/registrar）→ `02`、`13`
- 候选选择（Qualifier/Primary/Priority/Order）→ `03`、`33`
- 处理器（BFPP/BDRPP/BPP）→ `06`、`12–14`
- 生命周期（回调顺序/销毁语义）→ `05`、`17`
- FactoryBean（product vs factory/边界）→ `08`、`23`、`29`
- 值解析与类型转换（@Value/ConversionService）→ `34`、`36`
- 泛型匹配（ResolvableType/代理丢失信息）→ `37`
- AOT/Native（构建期契约：RuntimeHints）→ `40`、`41`
- 定义层输入（XML → BeanDefinitionReader）→ `42`
- 容器外对象（AutowireCapableBeanFactory）→ `43`
- SpEL（`@Value("#{...}")`）→ `44`
- 自定义限定符（meta-annotation Qualifier）→ `45`
- XML namespace 扩展（spring.handlers/schemas）→ `46`
- BeanDefinitionReader 其它输入源（Properties/Groovy）→ `47`
- 方法注入（replaced-method / MethodOverrides）→ `48`
- 内置 FactoryBean（MethodInvoking/ServiceLocator/& 前缀）→ `49`
- 值解析主线（BeanDefinitionValueResolver/PropertyEditor）→ `50`
- Environment/PropertySource（优先级/@PropertySource/placeholder）→ `38`、`34`
- BeanFactory API（plain vs context / 手动 bootstrap 边界）→ `39`、`12`

## 小结与下一章

### 2.11 容器外对象注入与生命周期托管（AutowireCapableBeanFactory）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest` / `SpringCoreBeansProxyingPhaseLabTest` / `SpringCoreBeansPreInstantiationLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansGenericTypeMatchingPitfallsLabTest` / `SpringCoreBeansTypeConversionLabTest` / `SpringCoreBeansXmlBeanDefinitionReaderLabTest` / `SpringCoreBeansAutowireCapableBeanFactoryLabTest` / `SpringCoreBeansSpelValueLabTest` / `SpringCoreBeansCustomQualifierLabTest` / `SpringCoreBeansXmlNamespaceExtensionLabTest` / `SpringCoreBeansBuiltInFactoryBeansLabTest` / `SpringCoreBeansServiceLoaderFactoryBeansLabTest` / `SpringCoreBeansPropertyEditorLabTest` / `SpringCoreBeansBeanDefinitionValueResolutionLabTest` / `SpringCoreBeansEnvironmentPropertySourceLabTest` / `SpringCoreBeansBeanFactoryApiLabTest` / `SpringCoreBeansBootstrapInternalsLabTest`
- Exercise：`SpringCoreBeansExerciseTest`

上一章：[91. 术语表（Glossary）](91-glossary.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[93. 面试复述模板（决策树 → Lab → 断点入口）](93-interview-playbook.md)

<!-- BOOKIFY:END -->
