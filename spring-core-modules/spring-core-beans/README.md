# spring-core-beans：IoC 容器与 Bean（模块导论）

本模块聚焦 Spring Framework 的 IoC 容器与 Bean 机制。它不以“会用某个注解”为终点，而把能力落在三个可验证的层面：

1. **能解释**：把“注册/注入/生命周期/后处理器/代理/循环依赖”等机制放回 `refresh()` 主线，解释清楚它发生在什么时候、为什么会这样。
2. **能调试**：知道关键断点落在“定义层还是创建层”，并能在调试器里观察到决定性变量变化。
3. **能排障**：面对异常与现象，能先分层（定义/创建/最终暴露对象），再用最短证据链收敛到原因。

官方参考（适用 Spring Framework 6.2.x；本仓库基线 6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

补充参考（用于查定义/核对边界）：

- Spring Framework Reference（BeanFactory 扩展点 / Post-Processors）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html
- Spring Framework Reference（Scopes）：https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html
- Spring Boot Reference（Auto-configuration）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html

---


## 本模块读法

本模块入口页承担“定位路线”的职责：先把最小实验跑成事实，再沿主线章节解释机制，最后回到排障与自检材料确认边界。

- **先跑入口**：优先使用本页给出的 Book Matrix、Branch Matrix 或最小 Lab，把现象固定成可重复断言。
- **再读主线**：按“主线时间线 → 深挖导读 → 正文主题”的顺序阅读，避免只按文件名零散跳转。
- **最后排障**：遇到问题先回到断点地图、关键分支矩阵、常见坑和自检清单，把问题收敛到章节、断点与测试入口。

## 10 分钟入口：先跑通一个容器闭环

如果只选择一个入口作为起跑线，可以先运行：

- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test`

读者在这一轮不需要把机制讲全，但应能在断言与调试过程中回答三个事实问题：

- 哪些 BeanDefinition 被注册进容器（定义层发生了什么）？
- bean 实例是在 `refresh()` 的哪一段被创建出来的（创建层发生了什么）？
- 最终暴露对象是否发生过替换（例如被 BPP 换成 proxy）？

---

## 如何运行（保持入口可回归）

- 模块契约回归（文档导航 + testsupport 输出）：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansModuleContractLabTest test`
- 仅运行文档契约：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test`
- 运行本模块全部测试：`mvn -pl :spring-core-beans test`
- 仅运行某个章节对应 Lab：`mvn -pl :spring-core-beans -Dtest=<TestClassName> test`
- Explore/Debug（可选开关，不影响默认回归）：
  `mvn -pl :spring-core-beans -Dspringcorebeans.explore=true -Dtest=SpringCoreBeans*ExploreTest test`

---

## 模块级重写契约

本模块可以重写文档、测试入口和测试支撑层，但重写必须服务同一个目标：把“现象 → 机制 → Lab → 断点 → 验证”这条证据链变短。具体理由见：[模块级重写理由：把文档、Lab 与测试支撑层绑成证据链](docs/deepening-module-rewrite-rationale.md)。

| 层次 | 重写收益 | 可回归入口 |
| --- | --- | --- |
| README / Guide / Appendix | 把入口从链接清单变成路线选择，读者先定位问题再读正文 | `SpringCoreBeansDocumentationContractTest` |
| 正文章节 | 统一为问题、机制、实验、断点、误区、小结，降低逐篇切换成本 | 对应章节 `*LabTest` |
| Matrix / Pack | 把测试套件变成学习路线：最小闭环、关键分支、排障断点 | `SpringCoreBeansBookMatrixLabTest` / `SpringCoreBeansBreakpointPackLabTest` |
| `testsupport` | 复用 BeanDefinition、依赖边、注入点 dump 能力，减少重复样板 | `SpringCoreBeansModuleContractLabTest` |

## 阅读路线（主线 / 断点 / 排障）

本模块的内容跨度较大，最稳定的读法是把“阅读”绑定到可运行入口：

- **主线阅读（建立心智模型）**：先用 Guide 确认 `refresh()` 主线位置，再顺读 IoC Container，最后进入 Internals 与 Wiring & Boundaries。
  - Guide 入口：`docs/guide-deep-dive-guide.md` / `docs/guide-quickstart-30min.md`
- **断点阅读（以可观察为中心）**：每章至少跑一次 Lab，按章节提供的 breakpoints/观察清单 观察关键数据结构。
  - 断点地图：`docs/guide-breakpoint-map.md` / 断点包：`docs/appendix-debugger-pack.md`
- **排障阅读（从现象回到最短证据链）**：先用下文的“症状驱动导航”定位章节，再回到对应 Lab 固化现象与边界。
  - 知识地图：`docs/appendix-knowledge-map.md` / 生产排障清单：`docs/appendix-production-troubleshooting-checklist.md`

---

## 从哪里开始（把入口压到最短）

如果目标是尽快把高频“为什么”变成可验证结论，可以从以下入口切入：

- Why Index：把“三级缓存/early reference/proxy 替换”这类问题做成实验闭环
  - [基础问题索引（Why Index）：把高频“为什么”做成可验证闭环](docs/guide-why-index.md)
- 深入导读：用“症状 → 章节 → 断点 → Lab”确定最短路径
  - `docs/guide-deep-dive-guide.md`

当已经能够“跑得动 Lab / 打得进断点”，想进入更短证据链、更短排错路径时，可以按三种入口继续推进：

- **现象驱动**：先用知识地图把入口压到最短，再跑断点组收敛关键分支
- **断点驱动**：先定位 `refresh()` 处于哪一段，再回到章节把观察到的变量变化收敛为结论
- **排障驱动**：按生产排障清单把问题分型为“定义层/注入解析/代理替换/值解析”，再回到对应章节与 Lab 固化证据链

> 目录页的职责是“给路线与入口”，机制细节在正文中展开。

## 症状驱动导航（快速定位）

> 更系统的“症状 → 章节 → 断点 → Lab”导航见：`docs/guide-deep-dive-guide.md`。定位到章节后，下一步直接用 [知识地图](docs/appendix-knowledge-map.md) 选“断点组 + 对应 Lab”，或用 [断点地图](docs/guide-breakpoint-map.md) 直接命中 C 组（避免把 README 扩写成另一份知识地图）。
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html
> 官方参考（Spring Boot 3.5.9，Spring Boot Auto-configuration）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html

| 现象/异常（读者视角） | 直达章节（最短路径） | 备注（先分层再追栈） |
| --- | --- | --- |
| `NoSuchBeanDefinitionException` / “@Bean/@Component 似乎未生效” | [Bean 注册入口：扫描、@Bean、@Import、registrar（已合并）](docs/ioc-bean-registration.md)、[容器启动与基础设施处理器：为什么注解能工作？](docs/internals-container-bootstrap-and-infrastructure.md)、[Spring Boot 自动装配如何影响 Bean（Auto-configuration）](docs/boot-spring-boot-auto-configuration.md) | 优先判定“定义层有没有注册 BeanDefinition” |
| `NoUniqueBeanDefinitionException` / 多实现注入歧义 | [依赖注入解析：类型/名称/@Qualifier/@Primary](docs/ioc-dependency-injection-resolution.md)、[候选选择与优先级：`@Primary` / `@Priority` / `@Order` / `@Qualifier` 的边界](docs/wiring-autowire-candidate-selection-primary-priority-order.md) | 收敛：`@Primary/@Qualifier/@Priority` |
| “循环依赖”异常 / `BeanCurrentlyInCreationException` | [循环依赖：现象、原因与规避（constructor vs setter）](docs/ioc-circular-dependencies.md)、[early reference 与循环依赖：getEarlyBeanReference 到底解决什么？](docs/internals-early-reference-and-circular.md) | 先区分 constructor vs setter；再看 early reference 参与者 |
| “为什么 Spring 要用三级缓存？” / `three level cache` / `earlySingletonObjects` / `singletonFactories` | [基础问题索引（Why Index）：把高频“为什么”做成可验证闭环](docs/guide-why-index.md)、[循环依赖：现象、原因与规避（constructor vs setter）](docs/ioc-circular-dependencies.md)、[early reference 与循环依赖：getEarlyBeanReference 到底解决什么？](docs/internals-early-reference-and-circular.md) | 优先把握“final/early/factory 三类语义”与“early 形态一致性（raw vs proxy）” |
| lazy bean 启动期被拉起 / “明明 @Lazy 还被提前创建” | [dependsOn：强制初始化顺序（即使没有显式依赖）](docs/wiring-depends-on.md)、[Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）](docs/wiring-lazy-semantics.md) | `dependsOn` 会显式 `getBean(dep)`，可强制拉起 lazy-init |
| “获取到 proxy” / AOP 行为异常 / self-invocation | [代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）](docs/wiring-proxying-phase-bpp-wraps-bean.md)、[实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行](docs/internals-pre-instantiation-short-circuit.md) | 先定位是 pre/early/after-init 哪个窗口替换对象 |
| `@Value("${...}")` 解析失败 / 值不符合预期 | [`@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast](docs/wiring-value-placeholder-resolution-strict-vs-non-strict.md)、[Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线](docs/wiring-environment-and-propertysource.md) | 关注 PropertySource precedence 与 placeholder resolver |
| `@Resource` 注入错对象 / “为什么像按名称找？” | [`@Resource` 注入：为什么其定位更接近“按名称找 Bean”？](docs/wiring-resource-injection-name-first.md)、[Bean 名称与 alias：同一个实例，多一个名字](docs/wiring-bean-names-and-aliases.md) | name-first + alias 会共同影响最终命中 |
| FactoryBean 混淆 `&` / “按类型发现/注入失效” | [`FactoryBean`：产品 vs 工厂（以及 `&` 前缀）](docs/ioc-factorybean.md)、[FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义](docs/wiring-factorybean-deep-dive.md)、[FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效](docs/wiring-factorybean-edge-cases.md) | 关键点：`getObjectType/isSingleton` 对 type matching 的影响 |
| 后处理器顺序导致“偶发不生效”/手工注册 BPP 陷阱 | [顺序（Ordering）：PriorityOrdered / Ordered / 无序](docs/internals-post-processor-ordering.md)、[手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](docs/wiring-programmatic-bpp-registration.md) | 优先核对 `PriorityOrdered/Ordered` 的分组与排序；再确认是否绕过默认注册流程 |
| AOT/Native 运行期缺失反射/代理/资源 | [AOT / Native 总览：为什么“JVM 可运行”不等于“Native 可运行”](docs/aot-aot-and-native-overview.md)、[RuntimeHints 入门：把构建期契约完成验证](docs/aot-runtimehints-basics.md) | 用 registrar + 单测把“构建期契约”固定 |

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。

### Guide（怎么学 / 从哪里设置断点）

- [基础问题索引（Why Index）：把高频“为什么”做成可验证闭环](docs/guide-why-index.md)
- [30 分钟快速闭环：先快后深（3 个最小实验入口）](docs/guide-quickstart-30min.md)
- [深入分析指南：将“Bean 三层模型”落实到源码与断点](docs/guide-deep-dive-guide.md)
- [主线时间线：IoC 容器从 refresh 到创建 Bean](docs/guide-mainline-timeline.md)
- [关键分支矩阵](docs/guide-branch-decision-matrix.md)
- [`refresh()` 调用链（容器从“定义”到“实例”的主线）](docs/guide-applicationcontext-refresh-call-chain.md)
- [断点地图（容器主线：可复用断点/观察点清单）](docs/guide-breakpoint-map.md)

### IoC Container（注册 / 注入 / 生命周期 / 扩展点）

- [Bean 注册入口：扫描、@Bean、@Import、registrar（已合并）](docs/ioc-bean-registration.md)
- [依赖注入解析：类型/名称/@Qualifier/@Primary](docs/ioc-dependency-injection-resolution.md)
- [Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）](docs/ioc-scope-and-prototype.md)
- [生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）](docs/ioc-lifecycle-and-callbacks.md)
- [容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](docs/ioc-post-processors.md)
- [`@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）](docs/ioc-configuration-enhancement.md)
- [`FactoryBean`：产品 vs 工厂（以及 `&` 前缀）](docs/ioc-factorybean.md)
- [循环依赖：现象、原因与规避（constructor vs setter）](docs/ioc-circular-dependencies.md)
- [Bean 运行机制：从 BeanDefinition 到最终暴露对象](docs/ioc-bean-mental-model.md)

### Boot Auto-Config（Boot 叠加后容器如何变复杂）

- [Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？](docs/boot-auto-config-ordering.md)
- [Spring Boot 自动装配如何影响 Bean（Auto-configuration）](docs/boot-spring-boot-auto-configuration.md)
- [调试与自检：如何“观察到”容器正在做什么](docs/boot-debugging-and-observability.md)

### Internals（refresh 主线 / 处理器算法 / 缓存边界）

- [容器启动与基础设施处理器：为什么注解能工作？](docs/internals-container-bootstrap-and-infrastructure.md)
- [BeanDefinitionRegistryPostProcessor：在“注册阶段”动态加定义](docs/internals-bdrpp-definition-registration.md)
- [顺序（Ordering）：PriorityOrdered / Ordered / 无序](docs/internals-post-processor-ordering.md)
- [实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行](docs/internals-pre-instantiation-short-circuit.md)
- [early reference 与循环依赖：getEarlyBeanReference 到底解决什么？](docs/internals-early-reference-and-circular.md)
- [生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）](docs/internals-lifecycle-callback-order.md)
- [从 `refresh()` 到 `doCreateBean()`：把 Spring Bean “变成对象”的主线走通（源码级）](docs/internals-refresh-to-bean-creation-mainline.md)

### Wiring & Boundaries（候选选择 / 代理 / 占位符 / 转换等）

- [Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）](docs/wiring-lazy-semantics.md)
- [dependsOn：强制初始化顺序（即使没有显式依赖）](docs/wiring-depends-on.md)
- [registerResolvableDependency：能注入，但它不是 Bean](docs/wiring-resolvable-dependency.md)
- [父子 ApplicationContext：可见性与覆盖边界](docs/wiring-context-hierarchy.md)
- [Bean 名称与 alias：同一个实例，多一个名字](docs/wiring-bean-names-and-aliases.md)
- [FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义](docs/wiring-factorybean-deep-dive.md)
- [BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？](docs/wiring-bean-definition-overriding.md)
- [手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](docs/wiring-programmatic-bpp-registration.md)
- [SmartInitializingSingleton：所有单例都创建完之后再做事](docs/wiring-smart-initializing-singleton.md)
- [SmartLifecycle：start/stop 时机与 phase 顺序](docs/wiring-smart-lifecycle-phase.md)
- [自定义 Scope + scoped proxy：thread scope 的真实语义](docs/wiring-custom-scope-and-scoped-proxy.md)
- [FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效](docs/wiring-factorybean-edge-cases.md)
- [注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）](docs/wiring-injection-phase-field-vs-constructor.md)
- [代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）](docs/wiring-proxying-phase-bpp-wraps-bean.md)
- [`@Resource` 注入：为什么其定位更接近“按名称找 Bean”？](docs/wiring-resource-injection-name-first.md)
- [候选选择与优先级：`@Primary` / `@Priority` / `@Order` / `@Qualifier` 的边界](docs/wiring-autowire-candidate-selection-primary-priority-order.md)
- [`@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast](docs/wiring-value-placeholder-resolution-strict-vs-non-strict.md)
- [BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？](docs/wiring-merged-bean-definition.md)
- [类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界](docs/wiring-type-conversion-and-beanwrapper.md)
- [泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失](docs/wiring-generic-type-matching-pitfalls.md)
- [Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线](docs/wiring-environment-and-propertysource.md)
- [BeanFactory API 深入分析：接口族谱与手动 bootstrap 的边界](docs/wiring-beanfactory-api-deep-dive.md)

### AOT & Real World（XML/Reader/AOT/外部对象/SpEL/自定义 qualifier）

- [AOT / Native 总览：为什么“JVM 可运行”不等于“Native 可运行”](docs/aot-aot-and-native-overview.md)
- [RuntimeHints 入门：把构建期契约完成验证](docs/aot-runtimehints-basics.md)
- [XML → BeanDefinitionReader：定义层解析与错误分型](docs/aot-xml-bean-definition-reader.md)
- [容器外对象注入：AutowireCapableBeanFactory](docs/aot-autowirecapablebeanfactory-external-objects.md)
- [SpEL 与 `@Value("#{...}")`：表达式解析链路](docs/aot-spel-and-value-expression.md)
- [自定义 Qualifier：meta-annotation 与候选收敛](docs/aot-custom-qualifier-meta-annotation.md)
- [XML namespace 扩展：NamespaceHandler / Parser / spring.handlers](docs/aot-xml-namespace-extension.md)
- [BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy](docs/aot-beandefinitionreader-other-inputs-properties-groovy.md)
- [方法注入（Method Injection）：replaced-method / MethodReplacer](docs/aot-method-injection-replaced-method.md)
- [内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀](docs/aot-built-in-factorybeans-gallery.md)
- [PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象](docs/aot-property-editor-and-value-resolution.md)

### Appendix（术语表 / 速查 / 排障清单 / 索引）

- [常见误区清单（按现象对照）](docs/appendix-common-pitfalls.md)
- [术语表](docs/appendix-glossary.md)
- [知识地图（Knowledge Map）：从现象直达章节/断点/Lab](docs/appendix-knowledge-map.md)
- [面试复述模板（Interview Playbook）：用“证据链”回答 Spring IoC](docs/appendix-interview-playbook.md)
- [生产排障清单（Troubleshooting Checklist）：从症状到证据链](docs/appendix-production-troubleshooting-checklist.md)
- [spring-beans Public API Index](docs/appendix-spring-beans-public-api-index.md)
- [spring-beans Public API Gap 清单（按包/机制域分批深化；含“缺口清单→最小 Labs”）](docs/appendix-spring-beans-public-api-gap.md)
- [Explore/Debug 用例（可选启用，不影响默认回归）](docs/appendix-explore-debug-tests.md)
- [断点包（断点包总入口）](docs/appendix-debugger-pack.md)
- [团队内训讲义（Training Kit）：可直接用于授课的课时脚本](docs/appendix-team-training-kit.md)
- [自检：spring-core-beans 文档导航](docs/appendix-self-check.md)

### 深化策略（可选）

- [spring-core-beans：内容级再加深策略（按章节）](docs/deepening-strategies.md)
- [章节深化路线（模块目录与目录页）](docs/deepening-docs-root.md)
- [章节深化路线（模块 README）](docs/deepening-module-readme.md)
- [模块级重写理由：把文档、Lab 与测试支撑层绑成证据链](docs/deepening-module-rewrite-rationale.md)
- [章节深化路线（Guide）](docs/deepening-guide.md)
- [章节深化路线（IoC Container）](docs/deepening-ioc-container.md)
- [章节深化路线（Boot Auto-Config）](docs/deepening-boot-autoconfig.md)
- [章节深化路线（Internals）](docs/deepening-container-internals.md)
- [章节深化路线（Wiring & Boundaries）](docs/deepening-wiring-and-boundaries.md)
- [章节深化路线（AOT & Real World）](docs/deepening-aot-and-real-world.md)
- [章节深化路线（Appendix 工具章节）](docs/deepening-appendix.md)

## 入口页验收：能否在 1 分钟内找到证据链
- 是否能按“主线 → 分支 → 证据链”的方式学习：先运行 Lab，再结合断点阅读章节？
- 是否能把一个现象先分层：定义阶段（BeanDefinition/processor） vs 创建阶段（getBean/doCreateBean/BPP）？
- 是否能在 1 分钟内从目录定位到：对应章节 + 对应 LabTest + 断点入口？
