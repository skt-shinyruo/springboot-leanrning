# spring-core-beans

本模块用“可运行的最小示例 + 可验证的测试实验（Labs/Exercises）”讲透 Spring Framework 的 **IoC 容器与 Bean**。

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 [docs/README.md](docs/README.md)。

## Start Here（5 分钟闭环）

> 目标：把“我大概懂了”变成“我能用断言证明 + 能解释 + 能下断点”。

1) 跑一个最小闭环（推荐从这里开始）

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test
```

你应该能解释清楚：

- 为什么 `@Qualifier` 能解决多实现注入歧义
- 为什么 prototype 注入 singleton 会“看起来像单例”（以及如何修复）
- `@PostConstruct` 为什么会在容器启动时运行

2) 跑一个“容器机制”闭环（把概念放回容器主线）

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test
```

你应该能解释清楚：

- `BeanDefinition`（定义）与 bean instance（实例）不是同一个概念对象
- BFPP（改定义）与 BPP（改实例/可能换成 proxy）分别发生在什么阶段

3) 开始深挖（用断点建立“阶段感”，而不是背规则）

- 从这里开始读：[`docs/00-deep-dive-guide.md`](docs/part-00-guide/011-00-deep-dive-guide.md)

## 学习路线（入门→进阶→深挖）

| 层级 | 目标 | 推荐入口（固定） | 你应该能解释清楚什么 |
| --- | --- | --- | --- |
| 入门 | 建立直觉与可断言结论 | `SpringCoreBeansLabTest` | Qualifier/Scope/生命周期的“外部行为”为什么是这样 |
| 进阶 | 把概念放回容器主线 | `SpringCoreBeansContainerLabTest` | 定义层 vs 实例层、BFPP vs BPP、为什么最终暴露对象可能是 proxy |
| 深挖 | 断点地图 + 排障闭环 | `docs/00-deep-dive-guide.md` | 能从异常/现象定位到正确断点入口，并用观察点收敛原因 |

## 你将学到什么

- Bean 的心智模型：`BeanDefinition`（定义） vs Bean instance（实例）
- Bean 如何被“注册”进容器：`@ComponentScan` / `@Bean` / `@Import` / registrar
- 依赖注入（DI）如何解析：类型、名称、`@Qualifier`、`@Primary`
- Scope 的真实语义：`singleton`、`prototype`，以及“prototype 注入 singleton”的坑
- 生命周期：创建、初始化、销毁；回调顺序与 scope 的交互
- 容器扩展点：BFPP/BPP/BDRPP（改定义/改实例/注册定义）
- 容器启动基础设施（annotation processors）：为什么 `@Bean/@Autowired/@PostConstruct` 能工作
- `@Configuration(proxyBeanMethods=...)` 对 `@Bean` 语义的影响
- `FactoryBean`：product vs factory，`&` 前缀与缓存语义
- 循环依赖：构造器为什么失败？setter 为什么有时能成功？early reference 在哪里起作用？
- `@Lazy` / `dependsOn` 等“装配语义”：到底影响什么
- BeanDefinition 覆盖（overriding）：同名 bean 的冲突策略
- 手工添加 `BeanPostProcessor`：强制顺序与 Ordered 的陷阱
- 容器启动后/关闭前的钩子：`SmartInitializingSingleton` 与 `SmartLifecycle`
- 自定义 scope 与 scoped proxy：thread scope 的语义与注入陷阱
- 父子 `ApplicationContext`：可见性与覆盖边界
- Spring Boot 自动装配如何影响最终的 Bean 图（bean graph）

## 前置知识

- 建议先完成 `springboot-basics`（至少能跑通项目、理解配置）
- 了解 Java 注解与反射的基本概念（不要求深入）
- 想更快理解“代理相关模块”（AOP/Tx/Validation），建议把本模块作为核心底座

## 关键命令

### 运行

```bash
mvn -pl :spring-core-beans spring-boot:run
```

运行时你可以观察到 `BeansDemoRunner` 的结构化输出（统一前缀 `BEANS:`）。建议把“看到的现象”与“可断言入口”绑定起来：

- `BEANS:textFormatters` / `BEANS:formattingService.injectedFormatter`：多实现注入如何被确定化  
  - 对照：`docs/03` → `SpringCoreBeansLabTest.usesQualifierToResolveMultipleBeans()`
- `BEANS:prototype.direct.sameId` / `BEANS:prototype.provider.differentId`：prototype 注入 singleton 的坑与修复方式  
  - 对照：`docs/04` → `SpringCoreBeansLabTest.demonstratesPrototypeScopeBehavior()`
- `BEANS:lifecycle.postConstructCalled`：`@PostConstruct` 的时机（init 阶段）  
  - 对照：`docs/05` / `docs/17` → `SpringCoreBeansLabTest.postConstructRunsDuringContextInitialization()`
- `BEANS:beanDefinitionCount`：容器里“定义”的数量（帮助你建立“定义层”直觉）  
  - 对照：`docs/01` / `docs/12`（把它放回 refresh 主线理解）

### 测试

```bash
mvn -pl :spring-core-beans test
```

只跑一个测试类 / 方法（用于断点深挖更舒服）：

```bash
# 跑一个测试类
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test

# 跑一个测试方法
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest#beanDefinitionIsNotTheBeanInstance test
```

> 提示：如果你想“启动后挂起，等待 IDE attach”，可以加 `-Dmaven.surefire.debug`（默认监听 5005）。

Exercises 默认禁用：打开 `*ExerciseTest`，移除/注释 `@Disabled`，按提示完成后再运行测试。

## 推荐 docs 阅读顺序（从“能解释清楚”到“理解机制”）

0. [深挖指南：把“Bean 三层模型”落到源码与断点](docs/part-00-guide/011-00-deep-dive-guide.md)
1. [Bean 心智模型：BeanDefinition vs 实例](docs/part-01-ioc-container/020-01-bean-mental-model.md)
2. [Bean 注册入口：扫描、@Bean、@Import、registrar](docs/part-01-ioc-container/02-bean-registration.md)
3. [依赖注入解析：类型/名称/@Qualifier/@Primary](docs/part-01-ioc-container/014-03-dependency-injection-resolution.md)
4. [Scope 与 prototype 注入陷阱](docs/part-01-ioc-container/015-04-scope-and-prototype.md)
5. [生命周期：初始化、销毁与回调](docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md)
6. [容器扩展点：BFPP vs BPP](docs/part-01-ioc-container/017-06-post-processors.md)
7. [`@Configuration` 增强与 `@Bean` 语义](docs/part-01-ioc-container/018-07-configuration-enhancement.md)
8. [`FactoryBean`：产品 vs 工厂](docs/part-01-ioc-container/08-factorybean.md)
9. [循环依赖：现象、原因与规避](docs/part-01-ioc-container/09-circular-dependencies.md)
10. [Spring Boot 自动装配如何影响 Bean](docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)
11. [调试与自检：如何“看见”容器正在做什么](docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md)
12. [容器启动与基础设施处理器：为什么注解能工作？](docs/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)
13. [BDRPP：在“注册阶段”动态加定义](docs/part-03-container-internals/13-bdrpp-definition-registration.md)
14. [顺序：PriorityOrdered / Ordered / 无序](docs/part-03-container-internals/14-post-processor-ordering.md)
15. [实例化前短路：postProcessBeforeInstantiation](docs/part-03-container-internals/15-pre-instantiation-short-circuit.md)
16. [early reference 与循环依赖：getEarlyBeanReference](docs/part-03-container-internals/16-early-reference-and-circular.md)
17. [生命周期回调顺序（含 prototype 不销毁）](docs/part-03-container-internals/17-lifecycle-callback-order.md)
18. [Lazy：lazy-init vs 注入点 `@Lazy`](docs/part-04-wiring-and-boundaries/023-18-lazy-semantics.md)
19. [dependsOn：强制初始化顺序](docs/part-04-wiring-and-boundaries/19-depends-on.md)
20. [registerResolvableDependency：能注入但不是 Bean](docs/part-04-wiring-and-boundaries/20-resolvable-dependency.md)
21. [父子 ApplicationContext：可见性与覆盖边界](docs/part-04-wiring-and-boundaries/21-context-hierarchy.md)
22. [Bean 名称与 alias](docs/part-04-wiring-and-boundaries/22-bean-names-and-aliases.md)
23. [FactoryBean 深潜：类型匹配与缓存语义](docs/part-04-wiring-and-boundaries/23-factorybean-deep-dive.md)
24. [BeanDefinition 覆盖（overriding）：同名 bean 的冲突策略](docs/part-04-wiring-and-boundaries/24-bean-definition-overriding.md)
25. [手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](docs/part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)
26. [SmartInitializingSingleton：所有单例都创建完之后再做事](docs/part-04-wiring-and-boundaries/26-smart-initializing-singleton.md)
27. [SmartLifecycle：start/stop 时机与 phase 顺序](docs/part-04-wiring-and-boundaries/27-smart-lifecycle-phase.md)
28. [自定义 Scope + scoped proxy：thread scope 的真实语义](docs/part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md)
29. [FactoryBean 边界：getObjectType 返回 null](docs/part-04-wiring-and-boundaries/29-factorybean-edge-cases.md)
30. [注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）](docs/part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md)
31. [代理/替换阶段：`BeanPostProcessor` 如何把 Bean “换成 Proxy”](docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)
32. [`@Resource` 注入：为什么它更像“按名称找 Bean”？](docs/part-04-wiring-and-boundaries/32-resource-injection-name-first.md)
33. [候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` 到底各管什么？](docs/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)
34. [`@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast](docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md)
35. [BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？](docs/part-04-wiring-and-boundaries/35-merged-bean-definition.md)
36. [AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”】【Part 05】](docs/part-05-aot-and-real-world/024-40-aot-and-native-overview.md)
37. [RuntimeHints 入门：把构建期契约跑通【Part 05】](docs/part-05-aot-and-real-world/41-runtimehints-basics.md)
38. [XML → BeanDefinitionReader：定义层解析与错误分型【Part 05】](docs/part-05-aot-and-real-world/42-xml-bean-definition-reader.md)
39. [容器外对象注入：AutowireCapableBeanFactory【Part 05】](docs/part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md)
40. [SpEL 与 `@Value("#{...}")`：表达式解析链路【Part 05】](docs/part-05-aot-and-real-world/44-spel-and-value-expression.md)
41. [自定义 Qualifier：meta-annotation 与候选收敛【Part 05】](docs/part-05-aot-and-real-world/45-custom-qualifier-meta-annotation.md)
42. [常见坑清单（建议反复对照）](docs/appendix/025-90-common-pitfalls.md)
43. [术语表（Glossary）](docs/appendix/91-glossary.md)
44. [知识点地图（Concept → Chapter → Lab）](docs/appendix/92-knowledge-map.md)
45. [面试复述模板（决策树 → Lab → 断点入口）](docs/appendix/93-interview-playbook.md)
46. [生产排障清单（异常分型 → 入口 → 观察点 → 修复策略）](docs/appendix/94-production-troubleshooting-checklist.md)
47. [自测题：你是否真的理解了？](docs/appendix/026-99-self-check.md)

## 容器主线（refresh call chain）一页纸

> 目标：把你在 docs 里读到的概念，放回 `AbstractApplicationContext#refresh` 的阶段里理解；否则你永远只能“背知识点”，讲不清“它插在主线的哪里”。

| 阶段（粗粒度） | 你应该看见什么 | 关键锚点（建议断点） | 对应 docs | 对应 Lab/Test |
| --- | --- | --- | --- | --- |
| 注册定义 | 配置输入被解析为 BeanDefinition | `ConfigurationClassPostProcessor` / `BeanDefinitionRegistry` | `docs/02`、`docs/12` | `SpringCoreBeansBootstrapInternalsLabTest` |
| 执行 BFPP/BDRPP | “改定义/加定义”发生在实例化之前 | `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` | `docs/06`、`docs/13`、`docs/14` | `SpringCoreBeansContainerLabTest`、`SpringCoreBeansRegistryPostProcessorLabTest` |
| 注册 BPP | BPP 顺序决定后续行为（甚至影响 proxy/短路） | `PostProcessorRegistrationDelegate#registerBeanPostProcessors` | `docs/06`、`docs/14`、`docs/25` | `SpringCoreBeansPostProcessorOrderingLabTest`、`SpringCoreBeansProgrammaticBeanPostProcessorLabTest` |
| 创建/注入/初始化 | instantiate → populate → initialize 的主线与观察点 | `AbstractAutowireCapableBeanFactory#doCreateBean` / `populateBean` / `initializeBean` | `docs/05`、`docs/17`、`docs/30` | `SpringCoreBeansBeanCreationTraceLabTest`、`SpringCoreBeansInjectionPhaseLabTest`、`SpringCoreBeansLifecycleCallbackOrderLabTest` |
| proxy/替换 | 最终暴露对象可能在多个点被替换成 proxy | `postProcessBeforeInstantiation` / `getEarlyBeanReference` / `postProcessAfterInitialization` | `docs/15`、`docs/16`、`docs/31` | `SpringCoreBeansPreInstantiationLabTest`、`SpringCoreBeansEarlyReferenceLabTest`、`SpringCoreBeansProxyingPhaseLabTest` |
| refresh 收尾 | 事件/多语言/资源等“应用层能力”在主线里完成装配 | `AbstractApplicationContext#finishRefresh` | `docs/01`、`docs/12` | `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` |
| 关闭与销毁 | destroy callbacks 的触发时机与顺序 | `DefaultSingletonBeanRegistry#destroySingletons` | `docs/05`、`docs/17` | `SpringCoreBeansLifecycleCallbackOrderLabTest` |

## 概念地图（注入相关：从“选候选”到“值怎么解析”）

- DI 解析：类型/名称/Qualifier/Primary → [docs/03](docs/part-01-ioc-container/014-03-dependency-injection-resolution.md) → `SpringCoreBeansLabTest`
- 注入歧义（NoUnique）与确定化修复：`@Primary/@Qualifier` → [docs/03](docs/part-01-ioc-container/014-03-dependency-injection-resolution.md) / [docs/33](docs/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md) → `SpringCoreBeansInjectionAmbiguityLabTest`
- 注入发生在哪个阶段：field vs constructor、`postProcessProperties` → [docs/30](docs/part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md) → `SpringCoreBeansInjectionPhaseLabTest`
- `@Resource`（name-first）与 `CommonAnnotationBeanPostProcessor` → [docs/32](docs/part-04-wiring-and-boundaries/32-resource-injection-name-first.md) → `SpringCoreBeansResourceInjectionLabTest`
- 候选选择 vs 顺序：`@Primary/@Priority/@Order` → [docs/33](docs/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md) → `SpringCoreBeansAutowireCandidateSelectionLabTest`
- `@Value("${...}")` 占位符：embedded value resolver（non-strict）vs placeholder configurer（strict）→ [docs/34](docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md) → `SpringCoreBeansValuePlaceholderResolutionLabTest`

## 概念地图（深挖/排障：从“报错”到“断点入口”）

- BeanDefinition 合并（merged `RootBeanDefinition`）→ [docs/35](docs/part-04-wiring-and-boundaries/35-merged-bean-definition.md) → `SpringCoreBeansMergedBeanDefinitionLabTest`
- 排障：异常 → 断点入口（候选集合/最终注入/依赖关系）→ [docs/11](docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md) → `SpringCoreBeansBeanGraphDebugLabTest`
- 代理定位闭环：最终暴露对象是哪里被换成 proxy 的？→ [docs/11](docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md) / [docs/31](docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md) → `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansProxyingPhaseLabTest`

## 概念地图（真实世界补齐：AOT/XML/外部对象/SpEL/自定义 Qualifier）

- AOT/Native 心智模型 → [docs/40](docs/part-05-aot-and-real-world/024-40-aot-and-native-overview.md) / [docs/41](docs/part-05-aot-and-real-world/41-runtimehints-basics.md) → `SpringCoreBeansAotRuntimeHintsLabTest`
- XML → BeanDefinitionReader（定义层错误分型）→ [docs/42](docs/part-05-aot-and-real-world/42-xml-bean-definition-reader.md) → `SpringCoreBeansXmlBeanDefinitionReaderLabTest`
- 容器外对象注入与回调 → [docs/43](docs/part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md) → `SpringCoreBeansAutowireCapableBeanFactoryLabTest`
- SpEL 与 `@Value("#{...}")` → [docs/44](docs/part-05-aot-and-real-world/44-spel-and-value-expression.md) → `SpringCoreBeansSpelValueLabTest`
- 自定义 Qualifier（meta-annotation）→ [docs/45](docs/part-05-aot-and-real-world/45-custom-qualifier-meta-annotation.md) → `SpringCoreBeansCustomQualifierLabTest`

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`。

### 推荐先跑这几个（新手不迷路）

- `SpringCoreBeansLabTest`：Qualifier/Scope/生命周期的最小闭环（入门第一站）
- `SpringCoreBeansContainerLabTest`：定义层 vs 实例层（BFPP/BPP/循环依赖等主线现象）
- `SpringCoreBeansBeanCreationTraceLabTest`：实例创建时间线（instantiate → populate → initialize → proxy 替换）
- `SpringCoreBeansBootstrapInternalsLabTest`：为什么注解能工作（基础设施处理器）
- `SpringCoreBeansAutowireCandidateSelectionLabTest`：`@Primary/@Priority/@Order` 的边界（别把排序当成选择）
- `SpringCoreBeansLifecycleCallbackOrderLabTest`：生命周期回调顺序 + prototype 不销毁

| 类型 | 入口 | 知识点 | 难度 | 推荐阅读 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java` | Qualifier/Scope/生命周期等“外部行为”验证 | ⭐⭐ | `docs/03`、`docs/04`、`docs/05` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java` | 实例创建时间线：instantiate → populate → initialize → proxy 替换 | ⭐⭐⭐ | `docs/05`、`docs/30`、`docs/31` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java` | 容器启动基础设施：为什么注解能工作 | ⭐⭐⭐ | `docs/12` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanFactoryVsApplicationContextLabTest.java` | BeanFactory vs ApplicationContext：事件/多语言/资源加载等能力边界 | ⭐⭐ | `docs/01`、`docs/12` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansAwareInfrastructureLabTest.java` | Aware 的基础设施：哪些是容器直接调，哪些依赖处理器（BPP） | ⭐⭐ | `docs/05`、`docs/12` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionPhaseLabTest.java` | 注入阶段：field vs constructor、`postProcessProperties`、`@PostConstruct` 时机 | ⭐⭐⭐ | `docs/30`、`docs/12` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResourceInjectionLabTest.java` | `@Resource`：name-first 注入 + 为什么需要 processors | ⭐⭐ | `docs/32`、`docs/12` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java` | 单依赖候选选择 vs 集合顺序：`@Primary/@Priority/@Order` | ⭐⭐ | `docs/33`、`docs/03` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionAmbiguityLabTest.java` | 注入歧义最小复现：NoUnique fail-fast + `@Primary/@Qualifier` 修复对照 | ⭐⭐ | `docs/03`、`docs/33` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java` | 泛型 type matching 坑：ResolvableType 匹配 vs proxy 丢失泛型信息 | ⭐⭐⭐ | `docs/03`、`docs/23`、`docs/29` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java` | `@Value("${...}")` 占位符解析：non-strict vs strict fail-fast | ⭐⭐⭐ | `docs/34`、`docs/06`、`docs/12` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java` | 代理/替换阶段：BPP 返回 proxy、自调用绕过、按接口 vs 实现类获取 | ⭐⭐⭐ | `docs/31` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRegistryPostProcessorLabTest.java` | BDRPP 动态注册定义 + 与 BFPP 的关系 | ⭐⭐⭐ | `docs/13` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java` | BFPP/BPP 的顺序（PriorityOrdered/Ordered） | ⭐⭐⭐ | `docs/14` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java` | 手工添加 BPP：强制顺序与 Ordered 陷阱 | ⭐⭐⭐ | `docs/25` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPreInstantiationLabTest.java` | 实例化前短路（before-instantiation replacement） | ⭐⭐⭐ | `docs/15` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java` | early reference：循环依赖场景下的 early proxy | ⭐⭐⭐ | `docs/16` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest.java` | 生命周期回调顺序 + prototype 不销毁 | ⭐⭐–⭐⭐⭐ | `docs/17` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java` | lazy-init 与注入点 `@Lazy` 的差异 | ⭐⭐ | `docs/18` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java` | dependsOn：强制初始化顺序 | ⭐⭐ | `docs/19` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansResolvableDependencyLabTest.java` | ResolvableDependency：能注入但不是 bean | ⭐⭐ | `docs/20` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java` | parent/child context 可见性与覆盖边界 | ⭐⭐ | `docs/21` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java` | beanName 与 alias 解析 | ⭐⭐ | `docs/22` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java` | 排障：候选集合 + 最终注入 + 依赖关系（bean graph） | ⭐⭐ | `docs/11`、`docs/03` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java` | BeanDefinition 覆盖（同名冲突策略） | ⭐⭐ | `docs/24` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartInitializingSingletonLabTest.java` | `afterSingletonsInstantiated` 的时机（lazy 与非 lazy） | ⭐⭐⭐ | `docs/26` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java` | `SmartLifecycle`：start/stop 与 phase 顺序 | ⭐⭐⭐ | `docs/27` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java` | 自定义 scope + scoped proxy（thread scope） | ⭐⭐⭐ | `docs/28` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java` | FactoryBean 深潜：类型匹配与缓存语义 | ⭐⭐⭐ | `docs/23` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java` | FactoryBean 边界：getObjectType 返回 null | ⭐⭐⭐ | `docs/29` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java` | BFPP/BPP、`@Configuration`、`FactoryBean`、循环依赖等“容器机制” | ⭐⭐⭐ | `docs/06` → `docs/09` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java` | BeanDefinition 合并：merged `RootBeanDefinition` + `MergedBeanDefinitionPostProcessor` 时机 | ⭐⭐⭐ | `docs/35`、`docs/01`、`docs/00` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportLabTest.java` | `@Import` / `ImportSelector` / registrar（高级注册入口） | ⭐⭐⭐ | `docs/02` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationLabTest.java` | Boot 自动装配（条件生效/失效、覆盖策略） | ⭐⭐⭐ | `docs/10`、`docs/11` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansConditionEvaluationReportLabTest.java` | 条件报告：ConditionEvaluationReport 可查询 + `matchIfMissing` 缺省语义 | ⭐⭐–⭐⭐⭐ | `docs/11`、`docs/10` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOrderingLabTest.java` | 自动配置顺序依赖：`@ConditionalOnBean` 时机差异 + `after/before` 确定化 | ⭐⭐–⭐⭐⭐ | `docs/10` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationBackoffTimingLabTest.java` | back-off 判断时机：为什么“写了覆盖 Bean”却没退让（early/late registrar 对照） | ⭐⭐⭐ | `docs/10` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationImportOrderingLabTest.java` | auto-config 导入/排序主线：after/before 排序结果 + 条件影响（不依赖内置清单） | ⭐⭐⭐ | `docs/10` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansBeanDefinitionOriginLabTest.java` | Bean 来源追踪：用 BeanDefinition 回答“谁注册的/从哪来” | ⭐⭐–⭐⭐⭐ | `docs/10`、`docs/11` |
| Lab | `src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOverrideMatrixLabTest.java` | 覆盖/back-off 场景矩阵：重复候选 → NoUnique → 两类修复（primary/qualifier vs back-off） | ⭐⭐⭐ | `docs/10` |
| Exercise | `src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java` | 按提示补齐/改造容器行为练习 | ⭐⭐–⭐⭐⭐ | 先跑完相关 Labs 再做 |
| Exercise | `src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansContainerInternalsExerciseTest.java` | 深水区练习：custom scope / lifecycle / factorybean | ⭐⭐⭐ | `docs/27` → `docs/29` |
| Exercise | `src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportExerciseTest.java` | 按提示做 import/registrar 练习 | ⭐⭐⭐ | 先跑 Import Lab 再做练习 |
| Exercise | `src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationExerciseTest.java` | 按提示做 auto-configuration 练习 | ⭐⭐⭐ | 先能读懂条件评估再改造 |

## 概念 → 在本模块哪里能“看见”

| 你要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 你应该能解释清楚 |
| --- | --- | --- | --- |
| 排障：异常 → 断点入口（候选集合/最终注入/依赖关系） | [docs/11](docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md) | `src/test/java/.../SpringCoreBeansBeanGraphDebugLabTest.java` | 如何从报错快速跳到 `doResolveDependency/getSingleton/preInstantiateSingletons` |
| BeanDefinition 合并（merged `RootBeanDefinition`） | [docs/35](docs/part-04-wiring-and-boundaries/35-merged-bean-definition.md) | `src/test/java/.../SpringCoreBeansMergedBeanDefinitionLabTest.java` | registry 的原始定义如何合并为最终 `RootBeanDefinition`，以及为什么存在 merged-definition hook |
| “注解为什么能工作”（基础设施处理器） | [docs/12](docs/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md) | `src/test/java/.../SpringCoreBeansBootstrapInternalsLabTest.java` | `@Autowired/@PostConstruct/@Bean` 不是魔法，而是 BFPP/BPP 的产物 |
| 注入阶段：field vs constructor 的关键差异 | [docs/30](docs/part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md) | `src/test/java/.../SpringCoreBeansInjectionPhaseLabTest.java` | 为什么 field injection 在构造器里一定是 null、而 constructor injection 在构造器里可用 |
| 代理/替换阶段：为什么“必须走代理才生效” | [docs/31](docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md) | `src/test/java/.../SpringCoreBeansProxyingPhaseLabTest.java` | BPP 如何把 bean 换成 proxy、为什么自调用绕过、为什么按实现类拿不到 |
| BDRPP：能在注册阶段加定义 | [docs/13](docs/part-03-container-internals/13-bdrpp-definition-registration.md) | `src/test/java/.../SpringCoreBeansRegistryPostProcessorLabTest.java` | 为什么 BDRPP 比 BFPP 更早、能注册定义并被后续 BFPP 修改 |
| post-processor 顺序如何影响结果 | [docs/14](docs/part-03-container-internals/14-post-processor-ordering.md) | `src/test/java/.../SpringCoreBeansPostProcessorOrderingLabTest.java` | `PriorityOrdered`/`Ordered`/无序的相对顺序 |
| 实例化前短路（构造器不执行） | [docs/15](docs/part-03-container-internals/15-pre-instantiation-short-circuit.md) | `src/test/java/.../SpringCoreBeansPreInstantiationLabTest.java` | 为什么一个 bean 可以在构造器抛异常的情况下仍“存在于容器” |
| early reference 与循环依赖里的代理 | [docs/16](docs/part-03-container-internals/16-early-reference-and-circular.md) | `src/test/java/.../SpringCoreBeansEarlyReferenceLabTest.java` | 为什么循环依赖场景里需要 early proxy、如何保证 early 与 final 一致 |
| 生命周期回调顺序（含 prototype 不销毁） | [docs/17](docs/part-03-container-internals/17-lifecycle-callback-order.md) | `src/test/java/.../SpringCoreBeansLifecycleCallbackOrderLabTest.java` | init 回调发生在 BPP(before/after) 的哪里、为什么 prototype 默认不销毁 |
| Lazy：lazy-init vs 注入点 `@Lazy` | [docs/18](docs/part-04-wiring-and-boundaries/023-18-lazy-semantics.md) | `src/test/java/.../SpringCoreBeansLazyLabTest.java` | 为什么 lazy-init 仍可能在 refresh 时被创建、注入点 `@Lazy` 的本质 |
| dependsOn：强制初始化顺序 | [docs/19](docs/part-04-wiring-and-boundaries/19-depends-on.md) | `src/test/java/.../SpringCoreBeansDependsOnLabTest.java` | dependsOn 解决的是“初始化顺序”而不是“注入” |
| ResolvableDependency：能注入但不是 bean | [docs/20](docs/part-04-wiring-and-boundaries/20-resolvable-dependency.md) | `src/test/java/.../SpringCoreBeansResolvableDependencyLabTest.java` | 为什么能 autowire，但 `getBean(type)` 会失败 |
| 父子 ApplicationContext | [docs/21](docs/part-04-wiring-and-boundaries/21-context-hierarchy.md) | `src/test/java/.../SpringCoreBeansContextHierarchyLabTest.java` | child 可见 parent，parent 不可见 child；覆盖只在 child 生效 |
| beanName 与 alias | [docs/22](docs/part-04-wiring-and-boundaries/22-bean-names-and-aliases.md) | `src/test/java/.../SpringCoreBeansBeanNameAliasLabTest.java` | alias 只是名字映射，不是复制实例 |
| FactoryBean 深潜（`&`、类型匹配、缓存） | [docs/23](docs/part-04-wiring-and-boundaries/23-factorybean-deep-dive.md) | `src/test/java/.../SpringCoreBeansFactoryBeanDeepDiveLabTest.java` + `src/test/java/.../SpringCoreBeansContainerLabTest.java` | product vs factory、`isSingleton()` 的缓存语义 |
| BeanDefinition 覆盖（同名冲突策略） | [docs/24](docs/part-04-wiring-and-boundaries/24-bean-definition-overriding.md) | `src/test/java/.../SpringCoreBeansBeanDefinitionOverridingLabTest.java` | 覆盖开关控制的是同名定义冲突，不是按类型注入选择 |
| Boot 自动装配：主线/定位/覆盖矩阵 | [docs/10](docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md) + [docs/11](docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md) | `src/test/java/.../SpringCoreBeansAutoConfigurationImportOrderingLabTest.java` + `src/test/java/.../SpringCoreBeansConditionEvaluationReportLabTest.java` + `src/test/java/.../SpringCoreBeansBeanDefinitionOriginLabTest.java` + `src/test/java/.../SpringCoreBeansAutoConfigurationOverrideMatrixLabTest.java` | 你能解释“候选清单如何排序（after/before）”“为什么 match/why skip”“这个 bean 谁注册的”“为什么会有重复候选/NoUnique”，并能给出两类修复（确定化选择 vs 让 back-off 生效） |
| 手工添加 BeanPostProcessor（顺序陷阱） | [docs/25](docs/part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md) | `src/test/java/.../SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java` | 手工注册 BPP 会更早执行，并且不按 Ordered 排序 |
| SmartInitializingSingleton（afterSingletonsInstantiated） | [docs/26](docs/part-04-wiring-and-boundaries/26-smart-initializing-singleton.md) | `src/test/java/.../SpringCoreBeansSmartInitializingSingletonLabTest.java` | 为什么它发生在非 lazy 单例创建完成之后 |
| SmartLifecycle（phase） | [docs/27](docs/part-04-wiring-and-boundaries/27-smart-lifecycle-phase.md) | `src/test/java/.../SpringCoreBeansSmartLifecycleLabTest.java` | start 升序、stop 反序，phase 的意义 |
| 自定义 scope + scoped proxy（thread） | [docs/28](docs/part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md) | `src/test/java/.../SpringCoreBeansCustomScopeLabTest.java` | 为什么 direct injection 会冻结实例、如何用 provider/proxy 修复 |
| FactoryBean 边界：getObjectType=null | [docs/29](docs/part-04-wiring-and-boundaries/29-factorybean-edge-cases.md) | `src/test/java/.../SpringCoreBeansFactoryBeanEdgeCasesLabTest.java` | 为什么 type-based 扫描在 allowEagerInit=false 时会错过它 |
| `@Qualifier` 解决多实现注入 | [docs/03](docs/part-01-ioc-container/014-03-dependency-injection-resolution.md) | `src/main/java/.../FormattingService.java`、`src/main/java/.../*TextFormatter.java`、`src/test/java/.../SpringCoreBeansLabTest.java` | 为什么会歧义、如何指定注入目标、如何验证注入结果 |
| 注入歧义（NoUnique）与确定化修复 | [docs/03](docs/part-01-ioc-container/014-03-dependency-injection-resolution.md) / [docs/33](docs/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md) | `src/test/java/.../SpringCoreBeansInjectionAmbiguityLabTest.java` | 候选太多为什么 fail-fast、`@Primary`（默认胜者） vs `@Qualifier`（显式选择） |
| prototype 注入 singleton 的“看起来像单例” | [docs/04](docs/part-01-ioc-container/015-04-scope-and-prototype.md) | `src/main/java/.../DirectPrototypeConsumer.java`、`src/main/java/.../ProviderPrototypeConsumer.java`、`src/main/java/.../PrototypeIdGenerator.java` | prototype 的语义是“每次向容器要都是新的”，而不是“每次方法调用都是新的” |
| `@PostConstruct` 何时运行（基础版） | [docs/05](docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md) | `src/main/java/.../LifecycleLogger.java`、`src/test/java/.../SpringCoreBeansLabTest.java` | 容器启动阶段发生了什么、回调在什么时机触发 |
| `@Import` / `ImportSelector` / registrar（高级注册入口） | [docs/02](docs/part-01-ioc-container/02-bean-registration.md) | `src/test/java/.../SpringCoreBeansImportLabTest.java` | 配置类解析阶段到底导入了什么、ImportSelector 如何决定导入列表、registrar 如何直接注册 BeanDefinition |
| `BeanDefinition` vs Bean 实例 | [docs/01](docs/part-01-ioc-container/020-01-bean-mental-model.md) | `src/test/java/.../SpringCoreBeansContainerLabTest.java` | “定义”是元数据，“实例”是对象；扩展点通常围绕两者分别工作 |
| BFPP 能改定义、BPP 能包/改实例 | [docs/06](docs/part-01-ioc-container/017-06-post-processors.md) | `src/test/java/.../SpringCoreBeansContainerLabTest.java` | 为什么 BFPP 更早、为什么 BPP 常导致代理/增强、它们各自的边界是什么 |
| `@Configuration` 增强与 `proxyBeanMethods` | [docs/07](docs/part-01-ioc-container/018-07-configuration-enhancement.md) | `src/test/java/.../SpringCoreBeansContainerLabTest.java` | 为什么 “在 `@Bean` 方法里直接调用另一个 `@Bean` 方法” 会改变实例语义 |
| `FactoryBean` 的 `&` 前缀（基础版） | [docs/08](docs/part-01-ioc-container/08-factorybean.md) | `src/test/java/.../SpringCoreBeansContainerLabTest.java` | 为什么 `getBean("name")` 拿到的是产品而不是工厂本身 |
| 循环依赖：构造器 vs setter（基础版） | [docs/09](docs/part-01-ioc-container/09-circular-dependencies.md) | `src/test/java/.../SpringCoreBeansContainerLabTest.java` | 为什么构造器循环会失败、setter 为什么有时能靠“提前暴露”成功 |
| Boot 自动装配带来的“你没写但它存在的 Bean” | [docs/10](docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md) | `src/test/java/.../SpringCoreBeansAutoConfigurationLabTest.java`（基于 `ApplicationContextRunner`） + [docs/11](docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md) | 自动装配是如何被导入/生效/失效的，以及你如何覆盖/禁用它 |

## 常见 Debug 路径

- 先确认“有没有这个 Bean”：从 `ApplicationContext`/测试断言入手，而不是只看日志
- 代理/增强问题：区分“定义阶段”（BFPP/BDRPP）与“实例阶段”（BPP），以及它们的触发时机
- 自动装配问题：优先用 `ApplicationContextRunner` 把场景做小（见 `SpringCoreBeansAutoConfigurationLabTest`）
- 看清容器在做什么：优先读 [docs/11](docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md)，按里面的步骤把“观察点”做成可断言

## 下一步（学完这里去哪里）

- 想理解代理与切面：`spring-core-aop`
- 想理解事件与监听器：`spring-core-events`
- 想理解事务与传播/回滚：`spring-core-tx`
- 想理解条件装配/环境：`spring-core-profiles`

## 参考

- Spring Framework Reference：Core Technologies（IoC Container / Beans / Context）
- Spring Boot Reference：Auto-configuration / Condition Evaluation（理解 Boot 如何“导入配置并注册 Bean”）
