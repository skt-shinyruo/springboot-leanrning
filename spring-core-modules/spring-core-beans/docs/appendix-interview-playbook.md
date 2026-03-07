# 面试复述模板（Interview Playbook）：用“证据链”回答 Spring IoC
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    - 使用方式：建议先用本章的“清单/索引/分流”把问题分型，再回到对应章节用断点与 Lab 把结论证明出来；团队内训/复盘时可直接按本章结构复用。

    本章围绕面试复述模板：用“证据链”回答 Spring IoC展开，主线可以概括为：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansIocBranchMatrixLabTest`。需要下探源码时，可以从 `ApplicationContext#refresh` / `AbstractApplicationContext#refresh` / `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 导读

- 这页可以当作“可复习题库”。每道题都给出：一句话结论 → 关键证据链（方法/数据结构）→ 对应 Lab。读者不依赖背诵，而以“可运行 + 可断点验证”作为得分依据。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab（建议作为复习入口总集合）：`SpringCoreBeansIocBranchMatrixLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest` / `SpringCoreBeansBreakpointPackLabTest`

## 机制主线：面试答题的“标准结构”

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

推荐读者用一个固定结构回答（不管题目问什么，都能套）：

1) **一句话结论（What）**：读者主张的结论是什么？
2) **关键约束（When/Where）**：它发生在 refresh 的哪一段？为什么这个时机决定了行为？
3) **证据链（Evidence）**：关键方法/关键分支/关键数据结构是什么？
4) **可复现入口（Repro）**：本仓库哪个 Lab 可复现该现象？

下面按主题给出高频题模板。

---

## 面试常见误归因对照（先纠错再答题）

- **误归因**：`@Order` 能解决单依赖歧义
  **纠正**：单依赖收敛看 `@Qualifier/@Primary/@Priority`（见 Q2）

- **误归因**：`@PostConstruct` 是 Java 语法自带
  **纠正**：它依赖 BPP 触发（见 Q4）

- **误归因**：循环依赖能启动就安全
  **纠正**：early reference 可能是半成品/代理不一致（见 Q6/Q7）

## 容器主线：refresh 到底干了什么？

### Q1：`ApplicationContext#refresh` 的主线应能够讲清楚吗？

- 一句话结论：refresh = 准备 BeanFactory → 定义层处理（BFPP/BDRPP）→ 注册 BPP 链 → 创建单例（doCreateBean）→ 完成与回调。
- 证据链：
  - `AbstractApplicationContext#refresh`
  - `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`（定义层）
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`（实例层）
  - `finishBeanFactoryInitialization` → `doCreateBean`（创建单例）
- Lab：`SpringCoreBeansMainlineCallChainLabTest`

对照章节：

- `internals-refresh-to-bean-creation-mainline.md`
- `guide-mainline-timeline.md`

---

## 注入解析：为什么会 NoSuch / NoUnique？

### Q2：按类型注入到底怎么选候选？

- 一句话结论：单依赖注入不是“按类型拿一个”，而是：先收集候选（by type）→ 再按规则收敛（primary/qualifier/name/priority...）→ 最终注入。
- 证据链：
  - `doResolveDependency` → `findAutowireCandidates` → `determineAutowireCandidate`
  
  调试时建议重点盯：`matchingBeans.keySet()`、`dependencyName`、`primaryCandidate`。
  
- Lab：`SpringCoreBeansAutowireCandidateSelectionLabTest`

对照章节：

- `ioc-dependency-injection-resolution.md`
- `wiring-autowire-candidate-selection-primary-priority-order.md`

### Q3：`@Resource` 和 `@Autowired` 的核心差异？

- 一句话结论：`@Resource` 更偏 name-first（字段名/显式 name），由 `CommonAnnotationBeanPostProcessor` 处理；`@Autowired` 更偏 type-first，由 `AutowiredAnnotationBeanPostProcessor` 处理。
- 证据链：
  - `CommonAnnotationBeanPostProcessor#postProcessProperties`
  
  调试时建议重点盯：`resourceName`（默认字段名）是否命中 beanName。
  
- Lab：`SpringCoreBeansResourceInjectionLabTest`

对照章节：

- `wiring-resource-injection-name-first.md`

---

## 生命周期：初始化回调顺序应能够讲到证据吗？

### Q4：Aware/@PostConstruct/afterPropertiesSet/initMethod 的顺序？

- 一句话结论：初始化发生在 `initializeBean`，包含 Aware 回调、before-init BPP、初始化方法、after-init BPP（可能返回 proxy）。
- 证据链：
  - `AbstractAutowireCapableBeanFactory#initializeBean`
  - `invokeAwareMethods` / `invokeInitMethods` / `applyBeanPostProcessorsBefore/AfterInitialization`
- Lab：生命周期相关 Lab（本模块有多条）

对照章节：

- `ioc-lifecycle-and-callbacks.md`
- `internals-lifecycle-callback-order.md`

---

## Post-Processor：BFPP vs BPP 到底差在哪？

### Q5：为什么 BFPP 很“早”，BPP 很“后”？

- 一句话结论：BFPP/BDRPP 发生在实例化之前，改的是 BeanDefinition；BPP 发生在 bean 创建过程中，改的是实例（甚至替换成 proxy）。
- 证据链：
  - `invokeBeanFactoryPostProcessors` vs `registerBeanPostProcessors`
  
  调试时建议重点盯：BPP 链是否完整、目标 bean 是否创建过早错过 BPP。
  
- Lab：processor/ordering 相关 Lab（本模块有多条）

对照章节：

- `ioc-post-processors.md`
- `internals-post-processor-ordering.md`
- `wiring-programmatic-bpp-registration.md`

---

## 循环依赖：三级缓存到底解决了什么？

### Q6：为什么 constructor cycle 基本 fail-fast，而 setter 有时能救？

- 一句话结论：constructor 依赖发生在实例化之前，没有 early exposure 窗口；setter/field 依赖发生在实例已创建但未初始化完的窗口期，singleton 可以提前暴露引用，使依赖环得以闭合。
- 证据链：
  - `doCreateBean` 的 early exposure（`addSingletonFactory`）
  - `getSingleton(beanName, allowEarlyReference)` 三层命中（final/early/factory）
- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest`

对照章节：

- `ioc-circular-dependencies.md`

### Q7：`getEarlyBeanReference` 解决的是什么问题？

- 一句话结论：它解决的是“early 引用是否等于最终暴露形态（proxy/wrapper）”，避免 raw 注入绕过代理与 raw/wrapped 不一致 fail-fast。
- 证据链：
  - `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`
  - `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`
  - `doCreateBean` 尾部 raw vs wrapped 一致性检查
- Lab：`SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansRawInjectionDespiteWrappingLabTest`

对照章节：

- `internals-early-reference-and-circular.md`

---

## FactoryBean：为什么 getBean("name") 获取到的不是工厂？

### Q8：`FactoryBean` 的 product vs factory 怎么区分？

- 一句话结论：`getBean("name")` 默认拿 product；`getBean("&name")` 才拿 factory 本身。
- 证据链：
  - `FactoryBeanRegistrySupport` 相关路径
  
  调试时建议重点盯：`&` 前缀分流。
  
- Lab：`SpringCoreBeansFactoryBeanDeepDiveLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`

对照章节：

- `ioc-factorybean.md`
- `wiring-factorybean-deep-dive.md`
- `wiring-factorybean-edge-cases.md`

---

## 值注入三连：占位符 / SpEL / 类型转换

### Q9：`@Value("${missing}")` 为什么可能不失败？

- 一句话结论：取决于 embedded value resolver 是否 strict；默认可能 non-strict 原样保留 `${...}`；注册 `PropertySourcesPlaceholderConfigurer` 可使缺失占位符 fail-fast。
- 证据链：`resolveEmbeddedValue` / `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`
- Lab：`SpringCoreBeansValuePlaceholderResolutionLabTest`

对照章节：

- `wiring-value-placeholder-resolution-strict-vs-non-strict.md`

### Q10：字符串怎么变成 int/Duration/自定义值对象？

- 一句话结论：转换发生在注入/属性填充阶段的 `convertIfNecessary`；决策点通常在 `TypeConverterDelegate`。
- 证据链：`applyPropertyValues` / `BeanWrapperImpl#setPropertyValue` / `TypeConverterDelegate#convertIfNecessary`
- Lab：`SpringCoreBeansTypeConversionLabTest`

对照章节：

- `wiring-type-conversion-and-beanwrapper.md`
- `aot-spel-and-value-expression.md`

---

## 注册入口：扫描 / `@Bean` / `@Import` / 编程式注册

### Q11：Spring 里“注册一个 Bean”到底注册的是什么？入口有哪些？

- 一句话结论：注册的第一性对象是 **BeanDefinition（定义）**；入口常见四类：scan / `@Bean` / `@Import`（selector+registrar）/ programmatic（定义层 vs 实例层）。
- 证据链：
  - 注册落点：`DefaultListableBeanFactory#registerBeanDefinition`
  - 定义层主入口：`ConfigurationClassPostProcessor#processConfigBeanDefinitions`
  - 实例层反例：`DefaultSingletonBeanRegistry#registerSingleton`（绕开创建管线）
- Lab：`SpringCoreBeansComponentScanLabTest` / `SpringCoreBeansImportLabTest` / `SpringCoreBeansProgrammaticRegistrationLabTest`

对照章节：

- `ioc-bean-registration.md`

---

## Spring Boot Auto-Config：顺序 / 条件 / 报告

### Q12：为什么跨 auto-config 的 `@ConditionalOnBean` 会“偶发不匹配”？如何让它确定化？

- 一句话结论：顺序未定义时，条件评估可能发生在依赖 bean 注册之前；用 `@AutoConfiguration(after=...)` 把隐式依赖变成显式排序规则。
- 证据链：
  - 导入入口：`AutoConfigurationImportSelector#selectImports`
  - 排序入口：`AutoConfigurationSorter`（具体方法名随版本可能微调）
  - 条件评估：`ConditionEvaluator#shouldSkip`
  - 回到定义层：`ConfigurationClassPostProcessor#processConfigBeanDefinitions`
- Lab：`SpringCoreBeansAutoConfigurationOrderingLabTest` / `SpringCoreBeansConditionEvaluationReportLabTest`

对照章节：

- `boot-auto-config-ordering.md`
- `boot-spring-boot-auto-configuration.md`

---

## Environment：PropertySource / 占位符 / 时机

### Q13：`Environment` 的属性优先级是什么？为什么“后改环境”不一定影响已创建的 bean？

- 一句话结论：Environment 通过 PropertySources 的顺序解析属性；**已创建的 bean 不会因为读者后续修改 Environment 而自动重注入**，除非读者让它延迟创建/重新创建。
- 证据链：
  - `ConfigurableEnvironment#getPropertySources`（优先级来源）
  - 值解析入口：`AbstractBeanFactory#resolveEmbeddedValue`
  - 注入/属性填充窗口：`AbstractAutowireCapableBeanFactory#populateBean`
- Lab：`SpringCoreBeansEnvironmentPropertySourceLabTest` / `SpringCoreBeansValuePlaceholderResolutionLabTest`

对照章节：

- `wiring-environment-and-propertysource.md`
- `wiring-value-placeholder-resolution-strict-vs-non-strict.md`

---

## BeanFactory API：容器外对象 / 手动 bootstrap 的边界
> 官方参考（Spring Boot 3.5.9，Spring Boot Auto-configuration）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html


### Q14：`BeanFactory` 和 `ApplicationContext` 的关键差异是什么？为什么 plain BeanFactory 下“注解不生效”？

- 一句话结论：`ApplicationContext` 会自动完成 BFPP/BDRPP/BPP 的 bootstrap；plain `DefaultListableBeanFactory` 只是内核，不会自动“让注解生效”，除非读者手动注册/执行相关处理器。
- 证据链：
  - `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
  
  调试时建议重点盯：`beanFactory.getBeanPostProcessors()` 是否为空/是否包含注解处理器。
  
- Lab：`SpringCoreBeansBeanFactoryApiLabTest` / `SpringCoreBeansAutowireCapableBeanFactoryLabTest`

对照章节：

- `wiring-beanfactory-api-deep-dive.md`
- `aot-autowirecapablebeanfactory-external-objects.md`

---

## AOT / RuntimeHints：为什么“JVM 可运行”不等于“Native 可运行”？

### Q15：RuntimeHints 解决什么问题？如何用证据链证明“没注册就不会命中”？

- 一句话结论：AOT/Native 需要把“运行期反射/资源访问等动态行为”变成构建期可知契约；RuntimeHints 就是这份契约的一部分。
- 证据链：
  - `RuntimeHintsRegistrar#registerHints`
  - `RuntimeHintsPredicates`（断言某类 hints 是否存在）
  - `AotServices.factories().load(...)`（从 `aot.factories` 加载注册器）
- Lab：`SpringCoreBeansAotRuntimeHintsLabTest` / `SpringCoreBeansAotFactoriesLabTest` / `SpringCoreBeansRuntimeHintsBoundaryLabTest`

对照章节：

- `aot-aot-and-native-overview.md`
- `aot-runtimehints-basics.md`

---

## XML / Reader / Namespace：定义层输入不止注解

### Q16：XML 是如何变成 BeanDefinition 并注册进容器的？

- 一句话结论：XML 不是“直接造对象”，而是被 Reader 解析成 BeanDefinition 再注册；本质仍是“定义层输入”。
- 证据链：
  - `XmlBeanDefinitionReader#loadBeanDefinitions`
  - `DefaultListableBeanFactory#registerBeanDefinition`
- Lab：`SpringCoreBeansXmlBeanDefinitionReaderLabTest`

对照章节：

- `aot-xml-bean-definition-reader.md`
- `ioc-bean-registration.md`

### Q17：自定义 XML namespace 扩展到底扩展的是什么？（Handler/Parser 的职责）

- 一句话结论：namespace 扩展扩展的是“定义层解析器”：把自定义标签翻译成 BeanDefinition（或一组定义）注册进 registry。
- 证据链：
  - `NamespaceHandlerResolver` / `NamespaceHandler#init`
  - `BeanDefinitionParser#parse`
  - 最终落点：`registerBeanDefinition`
- Lab：`SpringCoreBeansXmlNamespaceExtensionLabTest`

对照章节：

- `aot-xml-namespace-extension.md`

---

## 自检要点
应能够做到：

1) 任意挑一题，说出 1 个关键方法名 + 3 个 watch list + 1 个对应 Lab。
2) 把“名词”翻译成“时机”：BFPP/BDRPP/BPP 分别发生在哪一段？
3) 把“主观判断”替换为“可观察事实”：能在调试器里描述三层缓存/early reference/代理替换发生的瞬间。

## 证据链 ≈ 调用链：面试里如何落到“方法级”

这份 Playbook 里每题都写了“证据链”，但在面试里输出时，建议把它明确说成“调用链”（因为更具象）：

- 最小调用链写法（推荐 3 行以内）：
  1) 入口方法：从哪个入口开始看（通常就是 LabTest 里最先命中的方法）。
  2) 关键分支：在哪个方法里做决定（候选收敛/early reference/代理替换/值解析）。
  3) 观察点：观察哪个变量/集合以证明结论。

若答题时能把这三行说出来，再补一个反例/误区，答案就会非常“像做过源码排障的人”。
<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    建议 先跑 `SpringCoreBeansIocBranchMatrixLabTest`，再用 `SpringCoreBeansInternalsBranchMatrixLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`ApplicationContext#refresh`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：面试复述页为每个高频题补“可验证证据”：明确可以用哪个测试 + 哪个断点证明，而不是只给口头答案。
    - 下一跳：若是从现象进入，优先回到 [知识地图](appendix-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](guide-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->

## 小结

`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

