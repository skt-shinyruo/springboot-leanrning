# spring-core-beans 文档导航（Docs TOC）

## 导读

- 这份文档是 `spring-core-beans` 模块的学习入口与目录页：把“读什么 / 怎么跑 / 从哪里下断点”串成一条线。
- 本模块目标：把 IoC 容器从“会用 API”提升到“能解释机制、能下断点、能排障定位”。
- 推荐学习方式：**先跑对应 Lab 固化现象，再回到章节读机制主线，最后按断点闭环把证据链走通**。

## 四条阅读路线（按读者分层：源码进阶 + 面试）

- A（能用为主）：按目录顺读每章的“本章要点 + 最小实验”，遇到问题回看“常见坑”。
- B（能断点为主）：每章至少跑一次对应 Lab，并按章节给出的 breakpoints/watch list 在调试器里看见关键数据结构变化。
- C（能排障/能解释为主）：把每章的“一句话自检”当成面试/复盘模板；遇到真实问题时按章节的“排障分流表”定位到最短调用链。
- D（面试冲刺为主）：先刷 `appendix/93-interview-playbook.md` 的题库，再回到对应章节用“证据链（方法级）+ 可运行 Lab”把答案证明出来（而不是背书）。

## 章节契约（教程化验收口径：10/30/3）

你可以把每一章都当成一个“可验收交付物”，按 10/30/3 三段闭环学习：

1) **10 分钟最小闭环**：跑通本章 Lab/Test，看到预期现象（或断言）。
2) **30 分钟深挖闭环**：命中关键断点（3–5 个稳定锚点）并通过 watch list 看见决定性变量。
3) **3 分钟复述闭环**：用“结论 → 证据链（关键方法）→ 反例/坑”复述本章核心机制（对标 `appendix/93` 的标准结构）。

## 怎么跑（最小闭环）

- 运行本模块全部测试：
  - `mvn -pl :spring-core-beans test`
- 只跑某个章节对应 Lab：
  - `mvn -pl :spring-core-beans -Dtest=<TestClassName> test`
- Explore/Debug（可选启用，不影响默认回归）：
  - `mvn -pl :spring-core-beans -Dspringcorebeans.explore=true -Dtest=SpringCoreBeans*ExploreTest test`

## 推荐从这里开始

- 30 分钟快启：`part-00-guide/012-01-quickstart-30min.md`
- 深挖导读（症状驱动导航）：`part-00-guide/011-00-deep-dive-guide.md`
- 核心七件套（检查表 + 对应章节/Lab）：`appendix/92-knowledge-map.md`（第 0 节）
- Debugger Pack（断点包总入口）：`appendix/98-debugger-pack.md`
- 团队内训讲义（可直接开讲的课时脚本）：`appendix/99-team-training-kit.md`

## 目录

### Part 00：Guide（怎么学 / 从哪里下断点）

- [第 10 章：主线时间线：Spring Core Beans（IoC 容器）](part-00-guide/010-03-mainline-timeline.md)
- [第 11 章：00. 深挖指南：把“Bean 三层模型”落到源码与断点](part-00-guide/011-00-deep-dive-guide.md)
- [第 11 章：04：关键分支矩阵（Beans Branch Decision Matrix）](part-00-guide/011-04-branch-decision-matrix.md)
- [第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）](part-00-guide/012-01-quickstart-30min.md)
- [第 13 章：01：`refresh()` 调用链（容器从“定义”到“实例”的主线）](part-00-guide/013-01-applicationcontext-refresh-call-chain.md)
- [第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）](part-00-guide/013-02-breakpoint-map.md)

### Part 01：IoC Container（注册 / 注入 / 生命周期 / 扩展点）

- [第 14 章：03. 依赖注入解析：类型/名称/@Qualifier/@Primary](part-01-ioc-container/014-03-dependency-injection-resolution.md)
- [第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）](part-01-ioc-container/015-04-scope-and-prototype.md)
- [第 16 章：05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）](part-01-ioc-container/016-05-lifecycle-and-callbacks.md)
- [第 17 章：06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](part-01-ioc-container/017-06-post-processors.md)
- [第 18 章：07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）](part-01-ioc-container/018-07-configuration-enhancement.md)
- [02. Bean 注册入口：扫描、`@Bean`、`@Import`、Registrar](part-01-ioc-container/02-bean-registration.md)
- [第 20 章：01. Bean 心智模型与注册入口：从 BeanDefinition 到 Bean 实例](part-01-ioc-container/020-01-bean-mental-model.md)
- [08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）](part-01-ioc-container/08-factorybean.md)
- [09. 循环依赖：现象、原因与规避（constructor vs setter）](part-01-ioc-container/09-circular-dependencies.md)

### Part 02：Boot Auto-Config（Boot 叠加后容器如何变复杂）

- [第 19 章：11. 调试与自检：如何“看见”容器正在做什么](part-02-boot-autoconfig/019-11-debugging-and-observability.md)
- [020-09 Auto-Config Ordering（自动配置顺序）](part-02-boot-autoconfig/020-09-auto-config-ordering.md)
- [第 21 章：10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）](part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)

### Part 03：Internals（refresh 主线 / 处理器算法 / 缓存边界）

- [第 22 章：12. 容器启动与基础设施处理器：为什么注解能工作？](part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)
- [13. BeanDefinitionRegistryPostProcessor：在“注册阶段”动态加定义](part-03-container-internals/13-bdrpp-definition-registration.md)
- [14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序](part-03-container-internals/14-post-processor-ordering.md)
- [15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行](part-03-container-internals/15-pre-instantiation-short-circuit.md)
- [16. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？](part-03-container-internals/16-early-reference-and-circular.md)
- [17. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）](part-03-container-internals/17-lifecycle-callback-order.md)
- [从 `refresh()` 到 `doCreateBean()`：把 Spring Bean “变成对象”的主线走通（源码级）](part-03-container-internals/18-refresh-to-bean-creation-mainline.md)

### Part 04：Wiring & Boundaries（候选选择 / 代理 / 占位符 / 转换等）

- [第 23 章：18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）](part-04-wiring-and-boundaries/023-18-lazy-semantics.md)
- [19. dependsOn：强制初始化顺序（即使没有显式依赖）](part-04-wiring-and-boundaries/19-depends-on.md)
- [20. registerResolvableDependency：能注入，但它不是 Bean](part-04-wiring-and-boundaries/20-resolvable-dependency.md)
- [21. 父子 ApplicationContext：可见性与覆盖边界](part-04-wiring-and-boundaries/21-context-hierarchy.md)
- [22. Bean 名称与 alias：同一个实例，多一个名字](part-04-wiring-and-boundaries/22-bean-names-and-aliases.md)
- [23. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义](part-04-wiring-and-boundaries/23-factorybean-deep-dive.md)
- [24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？](part-04-wiring-and-boundaries/24-bean-definition-overriding.md)
- [25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)
- [26. SmartInitializingSingleton：所有单例都创建完之后再做事](part-04-wiring-and-boundaries/26-smart-initializing-singleton.md)
- [27. SmartLifecycle：start/stop 时机与 phase 顺序](part-04-wiring-and-boundaries/27-smart-lifecycle-phase.md)
- [28. 自定义 Scope + scoped proxy：thread scope 的真实语义](part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md)
- [29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效](part-04-wiring-and-boundaries/29-factorybean-edge-cases.md)
- [30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）](part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md)
- [31. 代理/替换阶段：`BeanPostProcessor` 如何把 Bean “换成 Proxy”](part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)
- [32. `@Resource` 注入：为什么它更像“按名称找 Bean”？](part-04-wiring-and-boundaries/32-resource-injection-name-first.md)
- [33. 候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` 到底各管什么？](part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)
- [34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast](part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md)
- [35. BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？](part-04-wiring-and-boundaries/35-merged-bean-definition.md)
- [36. 类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界](part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md)
- [37. 泛型匹配与注入坑：ResolvableType 与代理导致的类型信息丢失](part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md)
- [38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线](part-04-wiring-and-boundaries/38-environment-and-propertysource.md)
- [39. BeanFactory API 深挖：接口族谱与手动 bootstrap 的边界](part-04-wiring-and-boundaries/39-beanfactory-api-deep-dive.md)

### Part 05：AOT & Real World（XML/Reader/AOT/外部对象/SpEL/自定义 qualifier）

- [第 24 章：40. AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”](part-05-aot-and-real-world/024-40-aot-and-native-overview.md)
- [41. RuntimeHints 入门：把构建期契约跑通](part-05-aot-and-real-world/41-runtimehints-basics.md)
- [42. XML → BeanDefinitionReader：定义层解析与错误分型](part-05-aot-and-real-world/42-xml-bean-definition-reader.md)
- [43. 容器外对象注入：AutowireCapableBeanFactory](part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md)
- [44. SpEL 与 `@Value("#{...}")`：表达式解析链路](part-05-aot-and-real-world/44-spel-and-value-expression.md)
- [45. 自定义 Qualifier：meta-annotation 与候选收敛](part-05-aot-and-real-world/45-custom-qualifier-meta-annotation.md)
- [46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers](part-05-aot-and-real-world/46-xml-namespace-extension.md)
- [47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy](part-05-aot-and-real-world/47-beandefinitionreader-other-inputs-properties-groovy.md)
- [48. 方法注入（Method Injection）：replaced-method / MethodReplacer](part-05-aot-and-real-world/48-method-injection-replaced-method.md)
- [49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀](part-05-aot-and-real-world/49-built-in-factorybeans-gallery.md)
- [50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象](part-05-aot-and-real-world/50-property-editor-and-value-resolution.md)

### Appendix（术语表 / 速查 / 排障清单 / 索引）

- [第 25 章：90. 常见坑清单（建议反复对照）](appendix/025-90-common-pitfalls.md)
- [第 26 章：99. 自测题：你是否真的理解了？](appendix/026-99-self-check.md)
- [91. 术语表（Glossary）](appendix/91-glossary.md)
- [92. 知识地图（Concept → Chapter → Lab）](appendix/92-knowledge-map.md)
- [93. 面试复述模板：用“可证明的主线”回答 Spring Beans](appendix/93-interview-playbook.md)
- [94. 生产排障清单（异常分型 → 入口 → 观察点 → 修复策略）](appendix/94-production-troubleshooting-checklist.md)
- [95. spring-beans Public API 索引（Spring Framework 6.2.15）](appendix/95-spring-beans-public-api-index.md)
- [96. spring-beans Public API 覆盖差距（Gap）清单（Spring Framework 6.2.15）](appendix/96-spring-beans-public-api-gap.md)
- [97. Explore/Debug 用例（可选启用，不影响默认回归）](appendix/97-explore-debug-tests.md)
- [Debugger Pack（断点包总入口）](appendix/98-debugger-pack.md)
- [99. 团队内训讲义（Training Kit）：可直接开讲的课时脚本](appendix/99-team-training-kit.md)

## 一句话自检

- 你是否已经能按“主线 → 分支 → 证据链”的方式学习：先跑 Lab，再带着断点读章节？
- 你是否能把一个现象先分层：定义阶段（BeanDefinition/processor） vs 创建阶段（getBean/doCreateBean/BPP）？
- 你是否能在 1 分钟内从目录定位到：对应章节 + 对应 LabTest + 建议断点入口？

<!-- BOOKIFY:START -->

上一章：[00. 模块导读与路径建议（Start Here）](part-00-guide/012-01-quickstart-30min.md) ｜ 目录：[Docs TOC](README.md) ｜ 下一章：[01. 深挖指南（症状驱动导航）](part-00-guide/011-00-deep-dive-guide.md)

<!-- BOOKIFY:END -->
