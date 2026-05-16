# spring-core-beans

本模块用可运行 Lab 和中文文档讲清 Spring IoC 容器与 Bean 机制。

## 最短验证

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest,SpringCoreBeansModuleContractLabTest test
```

## 文档入口

- [知识地图：Spring Bean 文档归属](docs/appendix-knowledge-map.md)
- [Bean 对象模型：一个名字背后的多层对象](docs/bean-mental-model.md)
- [BeanDefinition 注册：Bean 如何进入容器](docs/bean-definition-registration.md)
- [BeanDefinition 元数据与来源：容器决策的输入](docs/bean-definition-metadata-and-origin.md)
- [BeanFactory 与 ApplicationContext：按行为看容器边界](docs/beanfactory-vs-applicationcontext.md)
- [ApplicationContext refresh 主线：容器状态如何一步步就绪](docs/refresh-mainline.md)
- [容器启动与基础设施：注解能力为什么会生效](docs/container-bootstrap-and-infrastructure.md)
- [Bean 创建主线：从 getBean 到最终暴露对象](docs/bean-creation-mainline.md)
- [实例化前短路：构造器为什么可以不执行](docs/pre-instantiation-short-circuit.md)
- [依赖注入解析主线：从注入点需求到候选收敛](docs/dependency-injection-resolution.md)
- [DependencyDescriptor 与 InjectionPoint：注入需求的元数据](docs/dependency-descriptor-and-injection-point.md)
- [自动装配候选选择：从类型匹配到唯一候选](docs/autowire-candidate-selection.md)
- [可选依赖与 Provider：缺失、延迟和重复获取](docs/optional-and-provider-injection.md)
- [Resource 与 Autowired：name-first 和 type-first 的差异](docs/resource-vs-autowired.md)
- [Scope 与 prototype：谁复用、谁创建、谁销毁](docs/scope-and-prototype.md)
- [自定义 Scope 与 scoped proxy：调用方拿到的为什么不是目标对象](docs/custom-scope-and-scoped-proxy.md)
- [Lazy 语义：延迟的到底是什么](docs/lazy-semantics.md)
- [生命周期回调：从构造到销毁，哪个窗口能看到什么](docs/lifecycle-callbacks.md)
- [SmartInitializingSingleton：所有非懒 singleton 结束后的一次统一回调](docs/smart-initializing-singleton.md)
- [SmartLifecycle：容器启动与停止阶段的编排规则](docs/smart-lifecycle.md)
- [Early reference 与三级缓存：循环依赖为什么有时能过、有时会炸](docs/early-reference-and-three-level-cache.md)
- [Proxying phase：代理什么时候出现，谁会先拿到它](docs/proxying-phase.md)
- [后处理器总览：BFPP、BDRPP、BPP、IABPP、MBDPP、DABPP](docs/post-processors-overview.md)
- [BeanPostProcessor：初始化窗口、包装和代理](docs/beanpost-processors.md)
- [FactoryBean：工厂对象与产品对象的双重身份](docs/factorybean.md)
- [FactoryBean 类型匹配：`getObjectType()` 如何影响发现与推断](docs/factorybean-type-matching.md)
- [XML BeanDefinition 读取：XML 如何落成定义](docs/xml-bean-definition-reader.md)
- [Properties 与 Groovy 读取器：外部定义输入的两种风格](docs/properties-and-groovy-reader.md)
- [XML namespace 扩展：自定义标签如何变成 BeanDefinition](docs/xml-namespace-extension.md)
- [Boot 自动配置 Bean：出现、排序与 backoff](docs/boot-auto-configuration-beans.md)
- [AOT / Native 总览：JVM 可运行不等于 Native 可运行](docs/aot-native-overview.md)
- [30 分钟快速上手：先跑主线，再补关键分支](docs/guide-quickstart-30min.md)
- [主线时间线：refresh、创建、注入、初始化、暴露](docs/guide-mainline-timeline.md)
- [断点地图：问题出现时先停哪里](docs/guide-breakpoint-map.md)
- [深入阅读路线：先学什么、每阶段验收什么](docs/guide-deep-dive-guide.md)
- [Appendix：常见误区与失败症状](docs/appendix-common-pitfalls.md)
- [Appendix：生产排障检查清单](docs/appendix-production-troubleshooting-checklist.md)

## 写作规格

- [Spring Core Beans 详细文档写作规格](DOCUMENTATION_SPEC.md)
