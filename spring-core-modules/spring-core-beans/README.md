# spring-core-beans：IoC 容器与 Bean

本模块只覆盖 Spring Framework Beans / IoC 容器边界：BeanDefinition 如何进入容器，Bean 如何被创建、注入、初始化、代理并最终暴露给调用方。

排障时先分三层：

1. **定义层**：有没有 `BeanDefinition`，是谁注册的，元数据是否允许它成为候选。
2. **创建层**：何时实例化，如何解析依赖，哪些后处理器介入。
3. **暴露层**：调用方拿到的是原始对象、FactoryBean product、early reference 还是 proxy。

最短命令：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test
```

下一步：先看知识地图，再按断点地图跑对应 Lab。README 只负责入口和目录，不复述主文档机制。

## 路线入口

- 主文档归属：知识地图
- 30 分钟快启：30 分钟快启
- 断点地图：断点地图
- 常见误区：常见误区
- 模块契约：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest,SpringCoreBeansModuleContractLabTest test`

## 文档目录

### 容器与注册

- [Bean 心智模型：定义、实例、缓存与最终暴露对象](docs/bean-mental-model.md)
- [BeanFactory vs ApplicationContext：能力边界](docs/beanfactory-vs-applicationcontext.md)
- [BeanDefinition 注册：谁把定义放进容器](docs/bean-definition-registration.md)
- [BeanDefinition 元数据与来源排查](docs/bean-definition-metadata-and-origin.md)
- [Bean 名称与 alias：定位和注入的名字边界](docs/bean-name-and-alias.md)
- [BeanDefinition 覆盖：同名定义谁生效](docs/bean-definition-overriding.md)
- [MergedBeanDefinition：创建前的最终定义视图](docs/merged-bean-definition.md)
- [@Configuration 与 @Bean：工厂方法语义](docs/configuration-and-bean-method.md)
- [@Import、ImportSelector 与 Registrar](docs/import-selector-and-registrar.md)
- [编程式注册：定义层 API 与实例层 API](docs/programmatic-registration.md)
- [refresh 主线：上下文刷新顺序](docs/refresh-mainline.md)
- [容器启动与基础设施 Bean](docs/container-bootstrap-and-infrastructure.md)
- [Post-Processor 总览：定义阶段与实例阶段](docs/post-processors-overview.md)
- [BeanFactoryPostProcessor：修改已有定义](docs/beanfactory-post-processors.md)
- [BDRPP：后处理器阶段新增定义](docs/bdrpp-definition-registration.md)
- [BeanPostProcessor：实例创建中的介入窗口](docs/beanpost-processors.md)
- [Post-Processor Ordering：处理器排序规则](docs/post-processor-ordering.md)
- [手工注册 BPP：绕过排序的边界](docs/programmatic-bpp-registration.md)
- [实例化前短路：构造器为何没执行](docs/pre-instantiation-short-circuit.md)
- [Bean 创建主线：doGetBean 到 doCreateBean](docs/bean-creation-mainline.md)

### 依赖解析与注入

- [依赖注入解析：注入点提出的需求](docs/dependency-injection-resolution.md)
- [DependencyDescriptor 与 InjectionPoint 元数据](docs/dependency-descriptor-and-injection-point.md)
- [候选 Bean 选择：收集、筛选、收敛](docs/autowire-candidate-selection.md)
- [Qualifier、Primary、Priority、Order 的边界](docs/qualifier-primary-priority-order.md)
- [@Resource 与 @Autowired：name-first vs by-type](docs/resource-vs-autowired.md)
- [Optional 与 Provider：可选依赖和延迟获取](docs/optional-and-provider-injection.md)
- [Resolvable Dependency：可注入但不是 Bean](docs/resolvable-dependency.md)
- [泛型类型匹配：ResolvableType 与代理失真](docs/generic-type-matching.md)
- [注入阶段：字段、构造器与属性填充窗口](docs/injection-phase.md)

### 生命周期、Scope 与代理边界

- [Scope 与 prototype：对象复用和销毁边界](docs/scope-and-prototype.md)
- [自定义 Scope 与 scoped proxy](docs/custom-scope-and-scoped-proxy.md)
- [Lazy 语义：定义延迟与注入点代理](docs/lazy-semantics.md)
- [dependsOn：初始化顺序而非注入规则](docs/depends-on.md)
- [生命周期回调：Aware、init、destroy 顺序](docs/lifecycle-callbacks.md)
- [SmartInitializingSingleton：所有单例之后](docs/smart-initializing-singleton.md)
- [SmartLifecycle：start/stop 与 phase](docs/smart-lifecycle.md)
- [循环依赖：能解决什么，解决不了什么](docs/circular-dependency.md)
- [Early Reference 与三级缓存](docs/early-reference-and-three-level-cache.md)
- [代理发生阶段：BPP 包装最终对象](docs/proxying-phase.md)
- [FactoryBean：产品对象与工厂对象](docs/factorybean.md)
- [FactoryBean 类型匹配：getObjectType 的边界](docs/factorybean-type-matching.md)
- [Context Hierarchy：父子容器可见性](docs/context-hierarchy.md)
- [BeanFactory API 与 AutowireCapableBeanFactory](docs/beanfactory-api-and-autowirecapablebeanfactory.md)

### 值解析、转换与外部输入

- [Environment 与 PropertySource：值从哪里来](docs/environment-and-propertysource.md)
- [占位符解析：strict vs non-strict](docs/value-placeholder-resolution.md)
- [SpEL 与 @Value：表达式和占位符顺序](docs/spel-and-value-expression.md)
- [类型转换与 BeanWrapper](docs/type-conversion-and-beanwrapper.md)
- [XML BeanDefinitionReader：XML 变成定义](docs/xml-bean-definition-reader.md)
- [Properties 与 Groovy Reader：其他定义输入](docs/properties-and-groovy-reader.md)
- [XML Namespace 扩展：自定义标签到定义](docs/xml-namespace-extension.md)
- [方法注入：lookup-method 与 replaced-method](docs/method-injection.md)
- [内置 FactoryBean：常见工厂形态](docs/built-in-factorybeans.md)

### Boot 叠加后的变化

- [Boot Auto-Configuration 顺序](docs/boot-auto-configuration-ordering.md)
- [Boot Auto-Configuration Bean：出现与 backoff](docs/boot-auto-configuration-beans.md)

### AOT / Native

- [AOT RuntimeHints：构建期契约](docs/aot-runtimehints.md)
- [AOT XML Reader 边界](docs/aot-xml-bean-definition-reader.md)
- [AOT 外部对象注入](docs/aot-autowirecapablebeanfactory-external-objects.md)
- [AOT SpEL 与 Value 约束](docs/aot-spel-and-value-expression.md)
- [AOT 自定义 Qualifier](docs/aot-custom-qualifier.md)
- [AOT XML Namespace 扩展](docs/aot-xml-namespace-extension.md)
- [AOT 其他 BeanDefinitionReader 输入](docs/aot-beandefinitionreader-other-inputs.md)
- [AOT 方法注入](docs/aot-method-injection.md)
- [AOT 内置 FactoryBean](docs/aot-built-in-factorybeans.md)
- [AOT PropertyEditor 与值解析](docs/aot-property-editor-and-value-resolution.md)
- [AOT / Native 总览：JVM 可运行不等于 Native 可运行](docs/aot-native-overview.md)

### 支持文档

- [Guide：ApplicationContext refresh 调用链](docs/guide-applicationcontext-refresh-call-chain.md)
- [Guide：分支决策矩阵](docs/guide-branch-decision-matrix.md)
- [Guide：断点地图](docs/guide-breakpoint-map.md)
- [Guide：深入阅读顺序](docs/guide-deep-dive-guide.md)
- [Guide：主线时间线](docs/guide-mainline-timeline.md)
- [Guide：30 分钟快启](docs/guide-quickstart-30min.md)
- [Guide：Why Index](docs/guide-why-index.md)
- [Appendix：知识地图](docs/appendix-knowledge-map.md)
- [Appendix：常见误区对照](docs/appendix-common-pitfalls.md)
- [Appendix：断点包](docs/appendix-debugger-pack.md)
- [Appendix：Explore/Debug 用例索引](docs/appendix-explore-debug-tests.md)
- [Appendix：术语表](docs/appendix-glossary.md)
- [Appendix：面试复述手册](docs/appendix-interview-playbook.md)
- [Appendix：生产排障清单](docs/appendix-production-troubleshooting-checklist.md)
- [Appendix：自检清单](docs/appendix-self-check.md)
- [Appendix：Spring Beans Public API Gap](docs/appendix-spring-beans-public-api-gap.md)
- [Appendix：Spring Beans Public API Index](docs/appendix-spring-beans-public-api-index.md)
- [Appendix：团队内训脚本](docs/appendix-team-training-kit.md)
- [Boot Debugging And Observability](docs/boot-debugging-and-observability.md)

### 维护文档

- [Deepening：AOT 与真实项目维护面](docs/deepening-aot-and-real-world.md)
- [Deepening：Appendix 维护边界](docs/deepening-appendix.md)
- [Deepening：Boot Auto-Config 维护边界](docs/deepening-boot-autoconfig.md)
- [Deepening：容器内部主线维护边界](docs/deepening-container-internals.md)
- [Deepening：docs 根目录维护说明](docs/deepening-docs-root.md)
- [Deepening：Guide 维护边界](docs/deepening-guide.md)
- [Deepening：IoC Container 维护边界](docs/deepening-ioc-container.md)
- [Deepening：模块 README 维护边界](docs/deepening-module-readme.md)
- [Deepening：模块重写理由](docs/deepening-module-rewrite-rationale.md)
- [Deepening：维护策略总览](docs/deepening-strategies.md)
- [Deepening：Wiring 与边界维护说明](docs/deepening-wiring-and-boundaries.md)
