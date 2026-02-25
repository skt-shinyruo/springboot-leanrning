# 目录（spring-core-beans）

> 本目录按「书籍阅读顺序」编排。入口与阅读路线请先看 `README.md`。

## Part 00：Guide（怎么学 / 从哪里设置断点）
- [01. 基础问题索引（Why Index）：把高频“为什么”做成可验证闭环](part-00-guide/01-why-index.md)
- [05. 30 分钟快速闭环：先快后深（3 个最小实验入口）](part-00-guide/05-quickstart-30min.md)
- [03. 深入分析指南：将“Bean 三层模型”落实到源码与断点](part-00-guide/03-deep-dive-guide.md)
- [02. 主线时间线：IoC 容器从 refresh 到创建 Bean](part-00-guide/02-mainline-timeline.md)
- [04. 关键分支矩阵（Branch Decision Matrix）](part-00-guide/04-branch-decision-matrix.md)
- [06. `refresh()` 调用链（容器从“定义”到“实例”的主线）](part-00-guide/06-applicationcontext-refresh-call-chain.md)
- [07. 断点地图（容器主线：可复用断点/观察点清单）](part-00-guide/07-breakpoint-map.md)

## Part 01：IoC Container（注册 / 注入 / 生命周期 / 扩展点）
- [01. Bean 注册入口：扫描、@Bean、@Import、registrar（已合并）](part-01-ioc-container/01-bean-registration.md)
- [02. 依赖注入解析：类型/名称/@Qualifier/@Primary](part-01-ioc-container/02-dependency-injection-resolution.md)
- [03. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）](part-01-ioc-container/03-scope-and-prototype.md)
- [04. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）](part-01-ioc-container/04-lifecycle-and-callbacks.md)
- [05. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）](part-01-ioc-container/05-post-processors.md)
- [06. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）](part-01-ioc-container/06-configuration-enhancement.md)
- [07. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）](part-01-ioc-container/07-factorybean.md)
- [08. 循环依赖：现象、原因与规避（constructor vs setter）](part-01-ioc-container/08-circular-dependencies.md)
- [09. Bean 运行机制：从 BeanDefinition 到最终暴露对象](part-01-ioc-container/09-bean-mental-model.md)

## Part 02：Boot Auto-Config（Boot 叠加后容器如何变复杂）
- [02. Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？](part-02-boot-autoconfig/02-auto-config-ordering.md)
- [03. Spring Boot 自动装配如何影响 Bean（Auto-configuration）](part-02-boot-autoconfig/03-spring-boot-auto-configuration.md)
- [01. 调试与自检：如何“观察到”容器正在做什么](part-02-boot-autoconfig/01-debugging-and-observability.md)

## Part 03：Internals（refresh 主线 / 处理器算法 / 缓存边界）
- [01. 容器启动与基础设施处理器：为什么注解能工作？](part-03-container-internals/01-container-bootstrap-and-infrastructure.md)
- [02. BeanDefinitionRegistryPostProcessor：在“注册阶段”动态加定义](part-03-container-internals/02-bdrpp-definition-registration.md)
- [03. 顺序（Ordering）：PriorityOrdered / Ordered / 无序](part-03-container-internals/03-post-processor-ordering.md)
- [04. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行](part-03-container-internals/04-pre-instantiation-short-circuit.md)
- [05. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？](part-03-container-internals/05-early-reference-and-circular.md)
- [06. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）](part-03-container-internals/06-lifecycle-callback-order.md)
- [07. 从 `refresh()` 到 `doCreateBean()`：把 Spring Bean “变成对象”的主线走通（源码级）](part-03-container-internals/07-refresh-to-bean-creation-mainline.md)

## Part 04：Wiring & Boundaries（候选选择 / 代理 / 占位符 / 转换等）
- [01. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）](part-04-wiring-and-boundaries/01-lazy-semantics.md)
- [02. dependsOn：强制初始化顺序（即使没有显式依赖）](part-04-wiring-and-boundaries/02-depends-on.md)
- [03. registerResolvableDependency：能注入，但它不是 Bean](part-04-wiring-and-boundaries/03-resolvable-dependency.md)
- [04. 父子 ApplicationContext：可见性与覆盖边界](part-04-wiring-and-boundaries/04-context-hierarchy.md)
- [05. Bean 名称与 alias：同一个实例，多一个名字](part-04-wiring-and-boundaries/05-bean-names-and-aliases.md)
- [06. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义](part-04-wiring-and-boundaries/06-factorybean-deep-dive.md)
- [07. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？](part-04-wiring-and-boundaries/07-bean-definition-overriding.md)
- [08. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](part-04-wiring-and-boundaries/08-programmatic-bpp-registration.md)
- [09. SmartInitializingSingleton：所有单例都创建完之后再做事](part-04-wiring-and-boundaries/09-smart-initializing-singleton.md)
- [10. SmartLifecycle：start/stop 时机与 phase 顺序](part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md)
- [11. 自定义 Scope + scoped proxy：thread scope 的真实语义](part-04-wiring-and-boundaries/11-custom-scope-and-scoped-proxy.md)
- [12. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效](part-04-wiring-and-boundaries/12-factorybean-edge-cases.md)
- [13. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）](part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md)
- [14. 代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）](part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md)
- [15. `@Resource` 注入：为什么其定位更接近“按名称找 Bean”？](part-04-wiring-and-boundaries/15-resource-injection-name-first.md)
- [16. 候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` / `@Qualifier` 的边界](part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md)
- [17. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast](part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md)
- [18. BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？](part-04-wiring-and-boundaries/18-merged-bean-definition.md)
- [19. 类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界](part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md)
- [20. 泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失](part-04-wiring-and-boundaries/20-generic-type-matching-pitfalls.md)
- [21. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线](part-04-wiring-and-boundaries/21-environment-and-propertysource.md)
- [22. BeanFactory API 深入分析：接口族谱与手动 bootstrap 的边界](part-04-wiring-and-boundaries/22-beanfactory-api-deep-dive.md)

## Part 05：AOT & Real World（XML/Reader/AOT/外部对象/SpEL/自定义 qualifier）
- [01. AOT / Native 总览：为什么“JVM 可运行”不等于“Native 可运行”](part-05-aot-and-real-world/01-aot-and-native-overview.md)
- [02. RuntimeHints 入门：把构建期契约完成验证](part-05-aot-and-real-world/02-runtimehints-basics.md)
- [03. XML → BeanDefinitionReader：定义层解析与错误分型](part-05-aot-and-real-world/03-xml-bean-definition-reader.md)
- [04. 容器外对象注入：AutowireCapableBeanFactory](part-05-aot-and-real-world/04-autowirecapablebeanfactory-external-objects.md)
- [05. SpEL 与 `@Value("#{...}")`：表达式解析链路](part-05-aot-and-real-world/05-spel-and-value-expression.md)
- [06. 自定义 Qualifier：meta-annotation 与候选收敛](part-05-aot-and-real-world/06-custom-qualifier-meta-annotation.md)
- [07. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers](part-05-aot-and-real-world/07-xml-namespace-extension.md)
- [08. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy](part-05-aot-and-real-world/08-beandefinitionreader-other-inputs-properties-groovy.md)
- [09. 方法注入（Method Injection）：replaced-method / MethodReplacer](part-05-aot-and-real-world/09-method-injection-replaced-method.md)
- [10. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀](part-05-aot-and-real-world/10-built-in-factorybeans-gallery.md)
- [11. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象](part-05-aot-and-real-world/11-property-editor-and-value-resolution.md)

## Appendix（术语表 / 速查 / 排障清单 / 索引）
- [01. 常见误区清单（建议反复对照）](appendix/01-common-pitfalls.md)
- [02. 术语表（Glossary）](appendix/02-glossary.md)
- [03. 知识地图（Knowledge Map）：从现象直达章节/断点/Lab](appendix/03-knowledge-map.md)
- [04. 面试复述模板（Interview Playbook）：用“证据链”回答 Spring IoC](appendix/04-interview-playbook.md)
- [05. 生产排障清单（Troubleshooting Checklist）：从症状到证据链](appendix/05-production-troubleshooting-checklist.md)
- [06. <!--](appendix/06-spring-beans-public-api-index.md)
- [07. spring-beans Public API Gap 清单（按包/机制域分批深化）](appendix/07-spring-beans-public-api-gap.md)
- [08. Explore/Debug 用例（可选启用，不影响默认回归）](appendix/08-explore-debug-tests.md)
- [09. Debugger Pack（断点包总入口）](appendix/09-debugger-pack.md)
- [10. 团队内训讲义（Training Kit）：可直接用于授课的课时脚本](appendix/10-team-training-kit.md)
- [11. 自测题：是否能够真的理解了？](appendix/11-self-check.md)

## 深化策略（可选）
- [spring-core-beans：内容级再加深策略（按章节）](deepening-strategies/README.md)
- [逐章内容级再加深建议（Docs TOC / 目录页）](deepening-strategies/docs-root.md)
- [逐章内容级再加深建议（模块 README）](deepening-strategies/module-readme.md)
- [逐章内容级再加深建议（part-00-guide 指南）](deepening-strategies/part-00-guide.md)
- [逐章内容级再加深建议（part-01-ioc-container）](deepening-strategies/part-01-ioc-container.md)
- [逐章内容级再加深建议（part-02-boot-autoconfig）](deepening-strategies/part-02-boot-autoconfig.md)
- [逐章内容级再加深建议（part-03-container-internals）](deepening-strategies/part-03-container-internals.md)
- [逐章内容级再加深建议（part-04-wiring-and-boundaries）](deepening-strategies/part-04-wiring-and-boundaries.md)
- [逐章内容级再加深建议（part-05-aot-and-real-world）](deepening-strategies/part-05-aot-and-real-world.md)
- [逐章内容级再加深建议（appendix 工具章节）](deepening-strategies/appendix.md)
