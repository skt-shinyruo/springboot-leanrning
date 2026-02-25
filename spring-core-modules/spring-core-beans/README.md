# spring-core-beans

本模块通过“可运行的最小示例 + 可验证的测试实验（Labs/Exercises）”系统阐述 Spring Framework 的 **IoC 容器与 Bean**。

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 [docs/README.md](docs/README.md)。

说明：`docs/` 的每一章开头均提供“章节学习卡片（五问闭环）”，并附上一章/下一章导航；建议按“先运行 Lab 以固化现象 → 再阅读机制主线 → 最后通过断点验证”的顺序学习。

## 版本语境（建议先对齐）

> 本仓库依赖版本由父工程的 Spring Boot 管理（见仓库根 `pom.xml`）。
> 文档中提到的类名/方法名/关键分支均以该版本语境为准；当你在其他项目上对照时，若出现行为差异，优先检查版本差异。

- Spring Boot：`3.5.9`
- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）

官方文档对照（Spring 官方 Reference，建议用作“权威定义”与边界核对）：

- Spring Framework Reference（Beans）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（BeanFactory Extension Points / Post-Processors）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html
- Spring Framework Reference（Scopes）：https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html
- Spring Boot Reference（Auto-configuration）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html

## 基础问题索引（Why Index）：优先阐明“为什么”

若读者已顺序阅读 `docs/09`（循环依赖）与 `docs/16`（early reference），但仍难以回答如下问题：

- 为什么 Spring 要用三级缓存（three level cache）？
- 为什么会出现 raw vs wrapped（allowRawInjectionDespiteWrapping）？
- 为什么获取到的 bean 可能是 proxy（exposed object 会变化）？

可先参阅下列“答案先行”索引页（每个问题均绑定最短证据链：Lab + 断点 + watch list）：

- `docs/part-00-guide/01-why-index.md`

最短可运行入口（10 分钟闭环）：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansEarlyReferenceLabTest test
```

## 症状快速定位（从问题直达章节）

若读者以具体问题为导向（异常/现象驱动），可先参阅 `docs/README.md` 的“症状驱动导航（快速定位）”，再回到对应章节按“证据链（方法级）+ 可运行 Lab”完成闭环验证。

## 内容级再加深（按章节策略 A–E）

当读者已能完整运行 Labs 并复述主线，但希望将能力从“理解”提升到“可证明/可排障/可面试复述”，可使用这份全章策略清单进行二次深化：

- `docs/deepening-strategies/README.md`

常见误判提示（高频误区，可对照策略与正文一并阅读）：

- 把 `dependsOn` 当成“注入依赖”（它只管初始化/销毁顺序）
- 误认为 `@Order` 能解决“单候选注入歧义”（单注入优先用 `@Primary/@Qualifier`）
- 把 `FactoryBean` 当成普通 bean（`&` 前缀、product vs factory、type matching 与缓存语义）

## 快速开始（5 分钟闭环）
> 目标：将“初步理解”提升为“可通过断言验证、可解释、可通过断点观察”。

1) 运行一个最小闭环（可作为起始入口）

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test
```

应能够解释清楚：

- 为什么 `@Qualifier` 能解决多实现注入歧义
- 为什么 prototype 注入 singleton 会“看起来像单例”（以及如何修复）
- `@PostConstruct` 为什么会在容器启动时运行

2) 运行一个“容器机制”闭环（将概念放回容器主线）

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test
```

应能够解释清楚：

- `BeanDefinition`（定义）与 bean instance（实例）不是同一个概念对象
- BFPP（改定义）与 BPP（改实例/可能换成 proxy）分别发生在什么阶段

3) 进入深入分析（通过断点建立“阶段意识”，而非依赖记忆性规则）

- 从这里开始读：[`docs/00-deep-dive-guide.md`](docs/part-00-guide/03-deep-dive-guide.md)

## 团队内训（可直接用于授课）

如需在团队内部开展一次“Spring Beans 机制与排障”主题分享，可选用本讲义作为授课材料（含 60/90/120 分钟课时脚本 + Labs/断点/互动题）：

- `docs/appendix/10-team-training-kit.md`

## 学习路线（入门→进阶→深入）

| 层级 | 目标 | 推荐入口（固定） | 应能够解释清楚的要点 |
| --- | --- | --- | --- |
| 入门 | 建立直觉与可断言结论 | `SpringCoreBeansLabTest` | Qualifier/Scope/生命周期的“外部行为”为什么是这样 |
| 进阶 | 把概念放回容器主线 | `SpringCoreBeansContainerLabTest` | 定义层 vs 实例层、BFPP vs BPP、为什么最终暴露对象可能是 proxy |
| 深入 | 断点地图 + 排障闭环 | `docs/00-deep-dive-guide.md` | 能从异常/现象定位到正确断点入口，并用观察点收敛原因 |

## 学习内容
- Bean 的心智模型：`BeanDefinition`（定义） vs Bean instance（实例）
- Bean 如何被“注册”进容器：`@ComponentScan` / `@Bean` / `@Import` / registrar
- 依赖注入（DI）如何解析：类型、名称、`@Qualifier`、`@Primary`
- Scope 的真实语义：`singleton`、`prototype`，以及“prototype 注入 singleton”的常见误区
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

## 核心七件套（从“知识点”到“可断言闭环”）

> 本节所列的 7 个核心主题已拆分到不同章节与 Lab 中。为便于查漏与导航，以下给出对应章节与可运行入口。

1. BeanDefinition 体系（“定义”与“实例”分开讲清楚）

   - `BeanDefinition` 的角色：描述元数据（class、scope、依赖、构造参数、属性注入、init/destroy 等），不是 bean 实例本身
   - 常见 `BeanDefinition` 类型差异：`RootBeanDefinition` / `GenericBeanDefinition` 以及“合并后的定义（merged）”概念
   - `BeanDefinitionRegistry` 与 `BeanFactory` 的分工：注册阶段 vs 创建/获取阶段
   - BeanName / alias 规则：命名、别名、覆盖（override）行为与风险点

2. Bean 创建全链路（可按“源码步骤”拆分为可复述的流程）

   - 典型创建路径：`getBean` → `doGetBean` → `createBean` → `doCreateBean`（宏观链路需能够完整复述）
   - 实例化策略与分支：构造器注入、工厂方法、`Supplier`、`FactoryBean`（尤其 `FactoryBean` 很多人混淆）
   - 属性填充阶段：`populateBean` 里如何解析依赖、如何处理集合/引用/占位符值
   - 初始化阶段：Aware 回调、`InitializingBean`、自定义 `initMethod`、`@PostConstruct`（顺序与触发条件）
   - 销毁阶段：`DisposableBean`、`destroyMethod`、`@PreDestroy`、依赖销毁顺序（dependent beans）

3. 依赖解析与注入细节（比“会用 `@Autowired`”更深入一层）

   - 依赖解析模型：注入点如何被描述（如 `DependencyDescriptor` 这类概念），按类型/按名称/限定符的选择逻辑
   - `@Primary` / `@Qualifier` / 多候选 bean 的决策规则（最好用案例串起来）
   - 延迟获取：`ObjectFactory` / `ObjectProvider` 这种“按需取 bean”的场景与价值
   - 泛型注入与类型匹配：为什么 `List<Foo>` 能注入、类型擦除下如何匹配（至少讲清思路）

4. 容器扩展点（“为什么 Spring 能插拔”这一块最容易缺）

   - `BeanPostProcessor`：bean 初始化前后增强点（AOP/代理很多都靠它串起来）
   - `InstantiationAwareBeanPostProcessor` / `SmartInstantiationAwareBeanPostProcessor`：更早期的“实例化前后”拦截点（解决代理提前暴露等问题）
   - `MergedBeanDefinitionPostProcessor`：合并定义后的增强点（很多注解元信息处理会牵涉）
   - `BeanFactoryPostProcessor` 与 `BeanDefinitionRegistryPostProcessor`：创建 bean 之前“改定义”的能力（非常核心，但经常被略过）
   - 执行顺序体系：`Ordered` / `PriorityOrdered` 以及“为什么顺序会影响结果”

5. 作用域与代理（scope 不只是 singleton/prototype）

   - singleton/prototype 的本质差异：生命周期归属、注入到单例里会发生什么
   - request/session 等 Web scope（若项目会用到）：为何需要 scoped proxy
   - 自定义 scope：什么时候要自定义、关键接口与典型误区

6. 循环依赖（该主题属于 spring-beans 的高频知识缺口）

   - 单例循环依赖为什么“有时能解、有时不能解”
   - 三级缓存的核心思想：提前暴露 early reference、与代理创建时机的关系
   - 典型失败场景：构造器循环依赖、prototype 循环依赖，以及如何拆解（`@Lazy` / 重构依赖 / 抽接口）

7. 类型转换与属性绑定基础（偏底层但很实用）

   - `BeanWrapper` / `PropertyEditor` / `ConversionService` 的定位（至少分清“属性访问”与“类型转换”）
   - 集合、枚举、日期等常见类型的转换链路（从配置到对象的过程）

对应章节与 Lab 入口（可参考：先运行 Lab 固化现象，再回到章节理解主线）：

| 核心点 | 章节入口（Docs） | 推荐 Lab/Test |
| --- | --- | --- |
| 1. BeanDefinition 体系 | [01. Bean 心智模型](docs/part-01-ioc-container/09-bean-mental-model.md)、[22. BeanName 与 alias](docs/part-04-wiring-and-boundaries/05-bean-names-and-aliases.md)、[24. BeanDefinition 覆盖](docs/part-04-wiring-and-boundaries/07-bean-definition-overriding.md)、[35. MergedBeanDefinition](docs/part-04-wiring-and-boundaries/18-merged-bean-definition.md) | `SpringCoreBeansContainerLabTest`、`SpringCoreBeansBeanNameAliasLabTest`、`SpringCoreBeansBeanDefinitionOverridingLabTest`、`SpringCoreBeansMergedBeanDefinitionLabTest` |
| 2. Bean 创建全链路 | [18. refresh→doCreateBean 主线](docs/part-03-container-internals/07-refresh-to-bean-creation-mainline.md)、[30. 注入阶段](docs/part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md)、[05. 生命周期](docs/part-01-ioc-container/04-lifecycle-and-callbacks.md)、[17. 回调顺序](docs/part-03-container-internals/06-lifecycle-callback-order.md) | `SpringCoreBeansBeanCreationTraceLabTest`、`SpringCoreBeansInjectionPhaseLabTest`、`SpringCoreBeansLifecycleCallbackOrderLabTest` |
| 3. 依赖解析与注入细节 | [03. 依赖注入解析](docs/part-01-ioc-container/02-dependency-injection-resolution.md)、[33. 候选选择与优先级](docs/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md)、[37. 泛型匹配注入误区](docs/part-04-wiring-and-boundaries/20-generic-type-matching-pitfalls.md) | `SpringCoreBeansLabTest`、`SpringCoreBeansInjectionAmbiguityLabTest`、`SpringCoreBeansAutowireCandidateSelectionLabTest`、`SpringCoreBeansOptionalInjectionLabTest` |
| 4. 容器扩展点 | [06. PostProcessor 总览](docs/part-01-ioc-container/05-post-processors.md)、[13. BDRPP](docs/part-03-container-internals/02-bdrpp-definition-registration.md)、[14. 顺序（Ordering）](docs/part-03-container-internals/03-post-processor-ordering.md)、[15. 实例化前短路](docs/part-03-container-internals/04-pre-instantiation-short-circuit.md) | `SpringCoreBeansRegistryPostProcessorLabTest`、`SpringCoreBeansPostProcessorOrderingLabTest`、`SpringCoreBeansPreInstantiationLabTest`、`SpringCoreBeansProgrammaticBeanPostProcessorLabTest` |
| 5. 作用域与代理 | [04. Scope 与 prototype](docs/part-01-ioc-container/03-scope-and-prototype.md)、[28. 自定义 Scope + scoped proxy](docs/part-04-wiring-and-boundaries/11-custom-scope-and-scoped-proxy.md)、[18. Lazy 语义](docs/part-04-wiring-and-boundaries/01-lazy-semantics.md) | `SpringCoreBeansLabTest`、`SpringCoreBeansCustomScopeLabTest`、`SpringCoreBeansLazyLabTest` |
| 6. 循环依赖 | [09. 循环依赖](docs/part-01-ioc-container/08-circular-dependencies.md)、[16. early reference 与循环依赖](docs/part-03-container-internals/05-early-reference-and-circular.md) | `SpringCoreBeansCircularDependencyBoundaryLabTest`、`SpringCoreBeansEarlyReferenceLabTest`、`SpringCoreBeansRawInjectionDespiteWrappingLabTest` |
| 7. 类型转换与属性绑定 | [36. 类型转换](docs/part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md)、[50. PropertyEditor 与值解析](docs/part-05-aot-and-real-world/11-property-editor-and-value-resolution.md) | `SpringCoreBeansTypeConversionLabTest`、`SpringCoreBeansValuePlaceholderResolutionLabTest` |

## 前置知识

- 可优先完成 `springboot-basics`（至少能够运行项目并理解配置）
- 了解 Java 注解与反射的基本概念（不要求深入）
- 若希望更快理解“代理相关模块”（AOP/Tx/Validation），可将本模块作为核心基础

## 关键命令

### 运行

```bash
mvn -pl :spring-core-beans spring-boot:run
```

运行时可观察到 `BeansDemoRunner` 的结构化输出（统一前缀 `BEANS:`）。可将“观察到的现象”与“可断言入口”进行对应关联：

- `BEANS:textFormatters` / `BEANS:formattingService.injectedFormatter`：多实现注入如何被确定化  
  - 对照：`docs/03` → `SpringCoreBeansLabTest.usesQualifierToResolveMultipleBeans()`
- `BEANS:prototype.direct.sameId` / `BEANS:prototype.provider.differentId`：prototype 注入 singleton 的误区与修复方式  
  - 对照：`docs/04` → `SpringCoreBeansLabTest.demonstratesPrototypeScopeBehavior()`
- `BEANS:lifecycle.postConstructCalled`：`@PostConstruct` 的时机（init 阶段）  
  - 对照：`docs/05` / `docs/17` → `SpringCoreBeansLabTest.postConstructRunsDuringContextInitialization()`
- `BEANS:beanDefinitionCount`：容器里“定义”的数量（用于建立“定义层”直觉）  
  - 对照：`docs/01` / `docs/12`（把它放回 refresh 主线理解）

### 测试

```bash
mvn -pl :spring-core-beans test
```

仅运行单个测试类 / 方法（便于断点深入分析与调试）：

```bash
# 运行单个测试类
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test

# 运行单个测试方法
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest#beanDefinitionIsNotTheBeanInstance test
```

> 说明：如需“启动后挂起，等待 IDE attach”，可添加 `-Dmaven.surefire.debug`（默认监听 5005）。

Exercises 默认禁用：打开 `*ExerciseTest`，移除/注释 `@Disabled`，按文中说明完成后再运行测试。

## 推荐 docs 阅读顺序（从“能解释清楚”到“理解机制”）

0. [深入指南：把“Bean 三层模型”落实到源码与断点](docs/part-00-guide/03-deep-dive-guide.md)
1. [Bean 心智模型：BeanDefinition vs 实例](docs/part-01-ioc-container/09-bean-mental-model.md)
2. [Bean 注册入口：扫描、@Bean、@Import、registrar](docs/part-01-ioc-container/01-bean-registration.md)
3. [依赖注入解析：类型/名称/@Qualifier/@Primary](docs/part-01-ioc-container/02-dependency-injection-resolution.md)
4. [Scope 与 prototype 注入陷阱](docs/part-01-ioc-container/03-scope-and-prototype.md)
5. [生命周期：初始化、销毁与回调](docs/part-01-ioc-container/04-lifecycle-and-callbacks.md)
6. [容器扩展点：BFPP vs BPP](docs/part-01-ioc-container/05-post-processors.md)
7. [`@Configuration` 增强与 `@Bean` 语义](docs/part-01-ioc-container/06-configuration-enhancement.md)
8. [`FactoryBean`：产品 vs 工厂](docs/part-01-ioc-container/07-factorybean.md)
9. [循环依赖：现象、原因与规避](docs/part-01-ioc-container/08-circular-dependencies.md)
10. [Spring Boot 自动装配如何影响 Bean](docs/part-02-boot-autoconfig/03-spring-boot-auto-configuration.md)
11. [调试与自检：如何观察容器正在执行的工作](docs/part-02-boot-autoconfig/01-debugging-and-observability.md)
12. [容器启动与基础设施处理器：为什么注解能工作？](docs/part-03-container-internals/01-container-bootstrap-and-infrastructure.md)
13. [BDRPP：在“注册阶段”动态加定义](docs/part-03-container-internals/02-bdrpp-definition-registration.md)
14. [顺序：PriorityOrdered / Ordered / 无序](docs/part-03-container-internals/03-post-processor-ordering.md)
15. [实例化前短路：postProcessBeforeInstantiation](docs/part-03-container-internals/04-pre-instantiation-short-circuit.md)
16. [early reference 与循环依赖：getEarlyBeanReference](docs/part-03-container-internals/05-early-reference-and-circular.md)
17. [生命周期回调顺序（含 prototype 不销毁）](docs/part-03-container-internals/06-lifecycle-callback-order.md)
18. [Lazy：lazy-init vs 注入点 `@Lazy`](docs/part-04-wiring-and-boundaries/01-lazy-semantics.md)
19. [dependsOn：强制初始化顺序](docs/part-04-wiring-and-boundaries/02-depends-on.md)
20. [registerResolvableDependency：能注入但不是 Bean](docs/part-04-wiring-and-boundaries/03-resolvable-dependency.md)
21. [父子 ApplicationContext：可见性与覆盖边界](docs/part-04-wiring-and-boundaries/04-context-hierarchy.md)
22. [Bean 名称与 alias](docs/part-04-wiring-and-boundaries/05-bean-names-and-aliases.md)
23. [FactoryBean 深潜：类型匹配与缓存语义](docs/part-04-wiring-and-boundaries/06-factorybean-deep-dive.md)
24. [BeanDefinition 覆盖（overriding）：同名 bean 的冲突策略](docs/part-04-wiring-and-boundaries/07-bean-definition-overriding.md)
25. [手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](docs/part-04-wiring-and-boundaries/08-programmatic-bpp-registration.md)
26. [SmartInitializingSingleton：所有单例都创建完之后再做事](docs/part-04-wiring-and-boundaries/09-smart-initializing-singleton.md)
27. [SmartLifecycle：start/stop 时机与 phase 顺序](docs/part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md)
28. [自定义 Scope + scoped proxy：thread scope 的真实语义](docs/part-04-wiring-and-boundaries/11-custom-scope-and-scoped-proxy.md)
29. [FactoryBean 边界：getObjectType 返回 null](docs/part-04-wiring-and-boundaries/12-factorybean-edge-cases.md)
30. [注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）](docs/part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md)
31. [代理/替换阶段：`BeanPostProcessor` 如何把 Bean “换成 Proxy”](docs/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md)
32. [`@Resource` 注入：为什么它更像“按名称找 Bean”？](docs/part-04-wiring-and-boundaries/15-resource-injection-name-first.md)
33. [候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` 到底各管什么？](docs/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md)
34. [`@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast](docs/part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md)
35. [BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？](docs/part-04-wiring-and-boundaries/18-merged-bean-definition.md)
36. [AOT / Native 总览：为什么“JVM 可运行”不等于“Native 可运行”】【Part 05】](docs/part-05-aot-and-real-world/01-aot-and-native-overview.md)
37. [RuntimeHints 入门：完成构建期契约验证【Part 05】](docs/part-05-aot-and-real-world/02-runtimehints-basics.md)
38. [XML → BeanDefinitionReader：定义层解析与错误分型【Part 05】](docs/part-05-aot-and-real-world/03-xml-bean-definition-reader.md)
39. [容器外对象注入：AutowireCapableBeanFactory【Part 05】](docs/part-05-aot-and-real-world/04-autowirecapablebeanfactory-external-objects.md)
40. [SpEL 与 `@Value("#{...}")`：表达式解析链路【Part 05】](docs/part-05-aot-and-real-world/05-spel-and-value-expression.md)
41. [自定义 Qualifier：meta-annotation 与候选收敛【Part 05】](docs/part-05-aot-and-real-world/06-custom-qualifier-meta-annotation.md)
42. [常见误区清单（可反复对照）](docs/appendix/01-common-pitfalls.md)
43. [术语表（Glossary）](docs/appendix/02-glossary.md)
44. [知识点地图（Concept → Chapter → Lab）](docs/appendix/03-knowledge-map.md)
45. [面试复述模板（决策树 → Lab → 断点入口）](docs/appendix/04-interview-playbook.md)
46. [生产排障清单（异常分型 → 入口 → 观察点 → 修复策略）](docs/appendix/05-production-troubleshooting-checklist.md)
47. [自测题：理解自检](docs/appendix/11-self-check.md)

## 容器主线（refresh call chain）一页纸

> 目标：将 docs 中的概念放回 `AbstractApplicationContext#refresh` 的阶段进行理解，避免停留在“记忆知识点”，并能够说明其位于主线的具体位置。

| 阶段（粗粒度） | 应观察到的现象 | 关键锚点（可设置断点） | 对应 docs | 对应 Lab/Test |
| --- | --- | --- | --- | --- |
| 注册定义 | 配置输入被解析为 BeanDefinition | `ConfigurationClassPostProcessor` / `BeanDefinitionRegistry` | `docs/02`、`docs/12` | `SpringCoreBeansBootstrapInternalsLabTest` |
| 执行 BFPP/BDRPP | “改定义/加定义”发生在实例化之前 | `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` | `docs/06`、`docs/13`、`docs/14` | `SpringCoreBeansContainerLabTest`、`SpringCoreBeansRegistryPostProcessorLabTest` |
| 注册 BPP | BPP 顺序决定后续行为（甚至影响 proxy/短路） | `PostProcessorRegistrationDelegate#registerBeanPostProcessors` | `docs/06`、`docs/14`、`docs/25` | `SpringCoreBeansPostProcessorOrderingLabTest`、`SpringCoreBeansProgrammaticBeanPostProcessorLabTest` |
| 创建/注入/初始化 | instantiate → populate → initialize 的主线与观察点 | `AbstractAutowireCapableBeanFactory#doCreateBean` / `populateBean` / `initializeBean` | `docs/05`、`docs/17`、`docs/30` | `SpringCoreBeansBeanCreationTraceLabTest`、`SpringCoreBeansInjectionPhaseLabTest`、`SpringCoreBeansLifecycleCallbackOrderLabTest` |
| proxy/替换 | 最终暴露对象可能在多个点被替换成 proxy | `postProcessBeforeInstantiation` / `getEarlyBeanReference` / `postProcessAfterInitialization` | `docs/15`、`docs/16`、`docs/31` | `SpringCoreBeansPreInstantiationLabTest`、`SpringCoreBeansEarlyReferenceLabTest`、`SpringCoreBeansProxyingPhaseLabTest` |
| refresh 收尾 | 事件/多语言/资源等“应用层能力”在主线里完成装配 | `AbstractApplicationContext#finishRefresh` | `docs/01`、`docs/12` | `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` |
| 关闭与销毁 | destroy callbacks 的触发时机与顺序 | `DefaultSingletonBeanRegistry#destroySingletons` | `docs/05`、`docs/17` | `SpringCoreBeansLifecycleCallbackOrderLabTest` |

## 概念地图（注入相关：从“选候选”到“值怎么解析”）

- DI 解析：类型/名称/Qualifier/Primary → [docs/03](docs/part-01-ioc-container/02-dependency-injection-resolution.md) → `SpringCoreBeansLabTest`
- 注入歧义（NoUnique）与确定化修复：`@Primary/@Qualifier` → [docs/03](docs/part-01-ioc-container/02-dependency-injection-resolution.md) / [docs/33](docs/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md) → `SpringCoreBeansInjectionAmbiguityLabTest`
- 注入发生在哪个阶段：field vs constructor、`postProcessProperties` → [docs/30](docs/part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md) → `SpringCoreBeansInjectionPhaseLabTest`
- `@Resource`（name-first）与 `CommonAnnotationBeanPostProcessor` → [docs/32](docs/part-04-wiring-and-boundaries/15-resource-injection-name-first.md) → `SpringCoreBeansResourceInjectionLabTest`
- 候选选择 vs 顺序：`@Primary/@Priority/@Order` → [docs/33](docs/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md) → `SpringCoreBeansAutowireCandidateSelectionLabTest`
- `@Value("${...}")` 占位符：embedded value resolver（non-strict）vs placeholder configurer（strict）→ [docs/34](docs/part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md) → `SpringCoreBeansValuePlaceholderResolutionLabTest`

## 概念地图（深入分析/排障：从“异常现象”到“断点入口”）

- BeanDefinition 合并（merged `RootBeanDefinition`）→ [docs/35](docs/part-04-wiring-and-boundaries/18-merged-bean-definition.md) → `SpringCoreBeansMergedBeanDefinitionLabTest`
- 排障：异常 → 断点入口（候选集合/最终注入/依赖关系）→ [docs/11](docs/part-02-boot-autoconfig/01-debugging-and-observability.md) → `SpringCoreBeansBeanGraphDebugLabTest`
- 代理定位闭环：最终暴露对象在何处被替换为 proxy → [docs/11](docs/part-02-boot-autoconfig/01-debugging-and-observability.md) / [docs/31](docs/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md) → `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansProxyingPhaseLabTest`

## 概念地图（真实世界补齐：AOT/XML/外部对象/SpEL/自定义 Qualifier）

- AOT/Native 心智模型 → [docs/40](docs/part-05-aot-and-real-world/01-aot-and-native-overview.md) / [docs/41](docs/part-05-aot-and-real-world/02-runtimehints-basics.md) → `SpringCoreBeansAotRuntimeHintsLabTest`
- XML → BeanDefinitionReader（定义层错误分型）→ [docs/42](docs/part-05-aot-and-real-world/03-xml-bean-definition-reader.md) → `SpringCoreBeansXmlBeanDefinitionReaderLabTest`
- 容器外对象注入与回调 → [docs/43](docs/part-05-aot-and-real-world/04-autowirecapablebeanfactory-external-objects.md) → `SpringCoreBeansAutowireCapableBeanFactoryLabTest`
- SpEL 与 `@Value("#{...}")` → [docs/44](docs/part-05-aot-and-real-world/05-spel-and-value-expression.md) → `SpringCoreBeansSpelValueLabTest`
- 自定义 Qualifier（meta-annotation）→ [docs/45](docs/part-05-aot-and-real-world/06-custom-qualifier-meta-annotation.md) → `SpringCoreBeansCustomQualifierLabTest`

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`。

### 可优先运行以下用例（便于初学者建立基线）

- `SpringCoreBeansLabTest`：Qualifier/Scope/生命周期的最小闭环（可作为入门起点）
- `SpringCoreBeansContainerLabTest`：定义层 vs 实例层（BFPP/BPP/循环依赖等主线现象）
- `SpringCoreBeansBeanCreationTraceLabTest`：实例创建时间线（instantiate → populate → initialize → proxy 替换）
- `SpringCoreBeansBootstrapInternalsLabTest`：为什么注解能工作（基础设施处理器）
- `SpringCoreBeansAutowireCandidateSelectionLabTest`：`@Primary/@Priority/@Order` 的边界（避免将排序规则等同于候选选择）
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
| Lab | `src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java` | 泛型 type matching 误区：ResolvableType 匹配 vs proxy 丢失泛型信息 | ⭐⭐⭐ | `docs/03`、`docs/23`、`docs/29` |
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
| Exercise | `src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java` | 按说明补齐/改造容器行为练习 | ⭐⭐–⭐⭐⭐ | 可在运行相关 Labs 后再开展 |
| Exercise | `src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansContainerInternalsExerciseTest.java` | 进阶练习：custom scope / lifecycle / factorybean | ⭐⭐⭐ | `docs/27` → `docs/29` |
| Exercise | `src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportExerciseTest.java` | 按说明完成 import/registrar 练习 | ⭐⭐⭐ | 可在运行 Import Lab 后再开展 |
| Exercise | `src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationExerciseTest.java` | 按说明完成 auto-configuration 练习 | ⭐⭐⭐ | 可在理解条件评估机制后再进行改造 |

## 概念 → 在本模块中的对应入口

| 需理解的概念 | 对应章节 | 对应测试/代码入口 | 应能够解释清楚 |
| --- | --- | --- | --- |
| 排障：异常 → 断点入口（候选集合/最终注入/依赖关系） | [docs/11](docs/part-02-boot-autoconfig/01-debugging-and-observability.md) | `src/test/java/.../SpringCoreBeansBeanGraphDebugLabTest.java` | 如何从异常信息定位到 `doResolveDependency/getSingleton/preInstantiateSingletons` |
| BeanDefinition 合并（merged `RootBeanDefinition`） | [docs/35](docs/part-04-wiring-and-boundaries/18-merged-bean-definition.md) | `src/test/java/.../SpringCoreBeansMergedBeanDefinitionLabTest.java` | registry 的原始定义如何合并为最终 `RootBeanDefinition`，以及为什么存在 merged-definition hook |
| “注解为什么能工作”（基础设施处理器） | [docs/12](docs/part-03-container-internals/01-container-bootstrap-and-infrastructure.md) | `src/test/java/.../SpringCoreBeansBootstrapInternalsLabTest.java` | `@Autowired/@PostConstruct/@Bean` 并非语言层面的隐式行为，而是 BFPP/BPP 注册与执行的结果 |
| 注入阶段：field vs constructor 的关键差异 | [docs/30](docs/part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md) | `src/test/java/.../SpringCoreBeansInjectionPhaseLabTest.java` | 为什么 field injection 在构造器里一定是 null、而 constructor injection 在构造器里可用 |
| 代理/替换阶段：为什么“必须走代理才生效” | [docs/31](docs/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md) | `src/test/java/.../SpringCoreBeansProxyingPhaseLabTest.java` | BPP 如何把 bean 换成 proxy、为什么自调用绕过、为什么按实现类拿不到 |
| BDRPP：能在注册阶段加定义 | [docs/13](docs/part-03-container-internals/02-bdrpp-definition-registration.md) | `src/test/java/.../SpringCoreBeansRegistryPostProcessorLabTest.java` | 为什么 BDRPP 比 BFPP 更早、能注册定义并被后续 BFPP 修改 |
| post-processor 顺序如何影响结果 | [docs/14](docs/part-03-container-internals/03-post-processor-ordering.md) | `src/test/java/.../SpringCoreBeansPostProcessorOrderingLabTest.java` | `PriorityOrdered`/`Ordered`/无序的相对顺序 |
| 实例化前短路（构造器不执行） | [docs/15](docs/part-03-container-internals/04-pre-instantiation-short-circuit.md) | `src/test/java/.../SpringCoreBeansPreInstantiationLabTest.java` | 为什么一个 bean 可以在构造器抛异常的情况下仍“存在于容器” |
| early reference 与循环依赖里的代理 | [docs/16](docs/part-03-container-internals/05-early-reference-and-circular.md) | `src/test/java/.../SpringCoreBeansEarlyReferenceLabTest.java` | 为什么循环依赖场景里需要 early proxy、如何保证 early 与 final 一致 |
| 生命周期回调顺序（含 prototype 不销毁） | [docs/17](docs/part-03-container-internals/06-lifecycle-callback-order.md) | `src/test/java/.../SpringCoreBeansLifecycleCallbackOrderLabTest.java` | init 回调发生在 BPP(before/after) 的哪里、为什么 prototype 默认不销毁 |
| Lazy：lazy-init vs 注入点 `@Lazy` | [docs/18](docs/part-04-wiring-and-boundaries/01-lazy-semantics.md) | `src/test/java/.../SpringCoreBeansLazyLabTest.java` | 为什么 lazy-init 仍可能在 refresh 时被创建、注入点 `@Lazy` 的本质 |
| dependsOn：强制初始化顺序 | [docs/19](docs/part-04-wiring-and-boundaries/02-depends-on.md) | `src/test/java/.../SpringCoreBeansDependsOnLabTest.java` | dependsOn 解决的是“初始化顺序”而不是“注入” |
| ResolvableDependency：能注入但不是 bean | [docs/20](docs/part-04-wiring-and-boundaries/03-resolvable-dependency.md) | `src/test/java/.../SpringCoreBeansResolvableDependencyLabTest.java` | 为什么能 autowire，但 `getBean(type)` 会失败 |
| 父子 ApplicationContext | [docs/21](docs/part-04-wiring-and-boundaries/04-context-hierarchy.md) | `src/test/java/.../SpringCoreBeansContextHierarchyLabTest.java` | child 可见 parent，parent 不可见 child；覆盖只在 child 生效 |
| beanName 与 alias | [docs/22](docs/part-04-wiring-and-boundaries/05-bean-names-and-aliases.md) | `src/test/java/.../SpringCoreBeansBeanNameAliasLabTest.java` | alias 只是名字映射，不是复制实例 |
| FactoryBean 深潜（`&`、类型匹配、缓存） | [docs/23](docs/part-04-wiring-and-boundaries/06-factorybean-deep-dive.md) | `src/test/java/.../SpringCoreBeansFactoryBeanDeepDiveLabTest.java` + `src/test/java/.../SpringCoreBeansContainerLabTest.java` | product vs factory、`isSingleton()` 的缓存语义 |
| BeanDefinition 覆盖（同名冲突策略） | [docs/24](docs/part-04-wiring-and-boundaries/07-bean-definition-overriding.md) | `src/test/java/.../SpringCoreBeansBeanDefinitionOverridingLabTest.java` | 覆盖开关控制的是同名定义冲突，不是按类型注入选择 |
| Boot 自动装配：主线/定位/覆盖矩阵 | [docs/10](docs/part-02-boot-autoconfig/03-spring-boot-auto-configuration.md) + [docs/11](docs/part-02-boot-autoconfig/01-debugging-and-observability.md) | `src/test/java/.../SpringCoreBeansAutoConfigurationImportOrderingLabTest.java` + `src/test/java/.../SpringCoreBeansConditionEvaluationReportLabTest.java` + `src/test/java/.../SpringCoreBeansBeanDefinitionOriginLabTest.java` + `src/test/java/.../SpringCoreBeansAutoConfigurationOverrideMatrixLabTest.java` | 应能够解释“候选清单如何排序（after/before）”“为什么 match/why skip”“这个 bean 谁注册的”“为什么会有重复候选/NoUnique”，并能给出两类修复（确定化选择 vs 让 back-off 生效） |
| 手工添加 BeanPostProcessor（顺序陷阱） | [docs/25](docs/part-04-wiring-and-boundaries/08-programmatic-bpp-registration.md) | `src/test/java/.../SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java` | 手工注册 BPP 会更早执行，并且不按 Ordered 排序 |
| SmartInitializingSingleton（afterSingletonsInstantiated） | [docs/26](docs/part-04-wiring-and-boundaries/09-smart-initializing-singleton.md) | `src/test/java/.../SpringCoreBeansSmartInitializingSingletonLabTest.java` | 为什么它发生在非 lazy 单例创建完成之后 |
| SmartLifecycle（phase） | [docs/27](docs/part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md) | `src/test/java/.../SpringCoreBeansSmartLifecycleLabTest.java` | start 升序、stop 反序，phase 的意义 |
| 自定义 scope + scoped proxy（thread） | [docs/28](docs/part-04-wiring-and-boundaries/11-custom-scope-and-scoped-proxy.md) | `src/test/java/.../SpringCoreBeansCustomScopeLabTest.java` | 为什么 direct injection 会导致实例冻结，以及如何通过 provider/proxy 进行规避 |
| FactoryBean 边界：getObjectType=null | [docs/29](docs/part-04-wiring-and-boundaries/12-factorybean-edge-cases.md) | `src/test/java/.../SpringCoreBeansFactoryBeanEdgeCasesLabTest.java` | 为什么 type-based 扫描在 allowEagerInit=false 时会错过它 |
| `@Qualifier` 解决多实现注入 | [docs/03](docs/part-01-ioc-container/02-dependency-injection-resolution.md) | `src/main/java/.../FormattingService.java`、`src/main/java/.../*TextFormatter.java`、`src/test/java/.../SpringCoreBeansLabTest.java` | 为什么会歧义、如何指定注入目标、如何验证注入结果 |
| 注入歧义（NoUnique）与确定化修复 | [docs/03](docs/part-01-ioc-container/02-dependency-injection-resolution.md) / [docs/33](docs/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md) | `src/test/java/.../SpringCoreBeansInjectionAmbiguityLabTest.java` | 候选太多为什么 fail-fast、`@Primary`（默认胜者） vs `@Qualifier`（显式选择） |
| prototype 注入 singleton 的“看起来像单例” | [docs/04](docs/part-01-ioc-container/03-scope-and-prototype.md) | `src/main/java/.../DirectPrototypeConsumer.java`、`src/main/java/.../ProviderPrototypeConsumer.java`、`src/main/java/.../PrototypeIdGenerator.java` | prototype 的语义是“每次从容器获取均为新实例”，而不是“每次方法调用均为新实例” |
| `@PostConstruct` 何时运行（基础版） | [docs/05](docs/part-01-ioc-container/04-lifecycle-and-callbacks.md) | `src/main/java/.../LifecycleLogger.java`、`src/test/java/.../SpringCoreBeansLabTest.java` | 容器启动阶段发生了什么、回调在什么时机触发 |
| `@Import` / `ImportSelector` / registrar（高级注册入口） | [docs/02](docs/part-01-ioc-container/01-bean-registration.md) | `src/test/java/.../SpringCoreBeansImportLabTest.java` | 配置类解析阶段到底导入了什么、ImportSelector 如何决定导入列表、registrar 如何直接注册 BeanDefinition |
| `BeanDefinition` vs Bean 实例 | [docs/01](docs/part-01-ioc-container/09-bean-mental-model.md) | `src/test/java/.../SpringCoreBeansContainerLabTest.java` | “定义”是元数据，“实例”是对象；扩展点通常围绕两者分别工作 |
| BFPP 能改定义、BPP 能包/改实例 | [docs/06](docs/part-01-ioc-container/05-post-processors.md) | `src/test/java/.../SpringCoreBeansContainerLabTest.java` | 为什么 BFPP 更早、为什么 BPP 常导致代理/增强、它们各自的边界是什么 |
| `@Configuration` 增强与 `proxyBeanMethods` | [docs/07](docs/part-01-ioc-container/06-configuration-enhancement.md) | `src/test/java/.../SpringCoreBeansContainerLabTest.java` | 为什么 “在 `@Bean` 方法里直接调用另一个 `@Bean` 方法” 会改变实例语义 |
| `FactoryBean` 的 `&` 前缀（基础版） | [docs/08](docs/part-01-ioc-container/07-factorybean.md) | `src/test/java/.../SpringCoreBeansContainerLabTest.java` | 为什么 `getBean("name")` 获取到的是产品而不是工厂本身 |
| 循环依赖：构造器 vs setter（基础版） | [docs/09](docs/part-01-ioc-container/08-circular-dependencies.md) | `src/test/java/.../SpringCoreBeansContainerLabTest.java` | 为什么构造器循环会失败、setter 为什么有时能靠“提前暴露”成功 |
| Boot 自动装配带来的“未显式声明但实际存在的 Bean” | [docs/10](docs/part-02-boot-autoconfig/03-spring-boot-auto-configuration.md) | `src/test/java/.../SpringCoreBeansAutoConfigurationLabTest.java`（基于 `ApplicationContextRunner`） + [docs/11](docs/part-02-boot-autoconfig/01-debugging-and-observability.md) | 自动装配是如何被导入/生效/失效的，以及如何覆盖/禁用它 |

## 常见调试路径

- 首先确认“是否存在该 Bean”：从 `ApplicationContext`/测试断言入手，而非仅依赖日志信息
- 代理/增强问题：区分“定义阶段”（BFPP/BDRPP）与“实例阶段”（BPP），以及它们的触发时机
- 自动装配问题：可优先使用 `ApplicationContextRunner` 缩小场景范围（见 `SpringCoreBeansAutoConfigurationLabTest`）
- 观察容器的运行行为：可优先参阅 [docs/11](docs/part-02-boot-autoconfig/01-debugging-and-observability.md)，并按步骤将“观察点”固化为可断言的验证点

## 进一步学习（相关模块）

- 如需进一步理解代理与切面，可参阅：`spring-core-aop`
- 如需进一步理解事件与监听器，可参阅：`spring-core-events`
- 如需进一步理解事务与传播/回滚，可参阅：`spring-core-tx`
- 如需进一步理解条件装配与环境，可参阅：`spring-core-profiles`

## 参考

- Spring Framework Reference：Core Technologies（IoC Container / Beans / Context）
- Spring Boot Reference：Auto-configuration / Condition Evaluation（理解 Boot 如何“导入配置并注册 Bean”）
