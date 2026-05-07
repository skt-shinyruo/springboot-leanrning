# spring-core-beans 文档按知识点重写 Spec

> 本 spec 是 `spring-core-beans` 模块后续文档重写的唯一总纲。它继承 [`docs/plans/2026-04-29-docs-full-manual-rewrite-spec.md`](2026-04-29-docs-full-manual-rewrite-spec.md) 与 [`docs/writing-style-guide.md`](../writing-style-guide.md) 的写作底线，但把本模块的边界进一步收紧为“按知识点建文档”，而不是按文件族套模板。

**目标：**把 `spring-core-beans` 现有文档重写成一组一组独立的 Bean 知识点文档。每个知识点只在一个文档里完整讲清楚，其他文档只做指针或前置说明，不再重复解释同一机制。

**核心约束：**

1. 一个文档只拥有一个知识点，或一组紧密耦合、必须一起理解的问题。
2. 同一个知识点不能散在多个文件里。
3. 目录页、索引页、维护页只负责导航和归档，不再承担机制解释。
4. 文档结构不强制统一模板，按主题自然组织。
5. 如果文档内容与代码、测试、断点入口不一致，优先修正文档或补最小代码/测试，让证据链成立。

---

## 1. 背景

`spring-core-beans` 目前已经有大量正文、Guide、Appendix、AOT、Boot 和 deepening 文档，但它们之间仍存在三个问题：

1. 同一个知识点会在多个文件中反复出现。
2. 一些导航页同时承担了“索引”和“解释”两种职责。
3. 读者很难判断某一页到底在负责哪个知识点。

这次重写不是“修辞润色”，而是把模块文档重构成知识点目录。目标不是更像模板，而是更像一套边界清晰的学习手册。

---

## 2. 定义

### 2.1 什么叫“知识点”

这里的“知识点”不是一个关键词，而是一个读者在调试或学习时会问出的核心问题。判断标准是：

- 这个问题能不能用一个文档讲完。
- 讲完后，读者能不能用一个最短证据入口验证它。
- 这个问题是否与另一个问题共享同一条机制主线。

如果两个问题需要完全不同的证据链，就应该拆成两个文档；如果它们只是同一机制的不同视角，就应该合并到同一个文档里。

### 2.2 什么叫“唯一归属”

每个知识点只能有一个主文档。主文档负责：

- 解释机制。
- 给出最短证据入口。
- 说明边界与反例。
- 收束到一个可复述结论。

其他页面如果需要提到这个知识点，只能：

- 给一句定位。
- 链到主文档。
- 不重复展开机制。

### 2.3 什么叫“支持文档”

支持文档不拥有新的知识点，只做以下事情：

- 目录与路由。
- 症状到知识点的映射。
- 术语表。
- 断点入口。
- 测试与 Lab 索引。
- 维护说明。

支持文档允许有简短解释，但不能把主文档已经讲清楚的机制再讲一遍。

---

## 3. 目标

### 3.1 结构目标

- 每个 Bean 相关知识点有且只有一个主文档。
- `README.md` 只保留模块总入口、阅读路线、运行命令、症状导航。
- `appendix-*`、`guide-*`、`deepening-*` 中凡是承担解释职责的内容，都要迁回对应主文档。
- `deepening-*` 改写为维护者文档，不再是教程正文。

### 3.2 内容目标

- 不再按固定模板写每篇正文。
- 每篇文档按自己的知识点决定结构。
- 该用问题驱动就用问题驱动，该用流程驱动就用流程驱动，该用对照表就用对照表。
- 允许一篇文档很短，也允许一篇文档较长，前提是它只讲一个知识点。

### 3.3 去重目标

- 同一概念只出现一次完整解释。
- 其他页面只保留指向性表述。
- 禁止把同一条机制拆成“总览一遍 + 细节一遍 + 误区一遍”这种散落结构。

---

## 4. 非目标

- 不追求全模块统一版式模板。
- 不为了拆分而拆分。
- 不把所有内容压成一套标题目录。
- 不重写与 Bean 无关的模块文档。
- 不做大规模生产代码重构，除非文档证据链必须修正。

---

## 5. Canonical 知识点清单

下面这份清单是后续重写的归属表。每一项都应该最终落到一个主文档里。文件名可以根据实际内容调整，但知识点归属不能分散。

### 5.1 容器与注册

| 建议主文档 | 核心问题 |
| --- | --- |
| `bean-mental-model.md` | Bean、BeanDefinition、单例缓存、最终暴露对象分别是什么关系？ |
| `beanfactory-vs-applicationcontext.md` | BeanFactory 与 ApplicationContext 的能力差异是什么？ |
| `bean-definition-registration.md` | 一个 BeanDefinition 是如何被注册进容器的？ |
| `bean-definition-metadata-and-origin.md` | BeanDefinition 的 primary/autowireCandidate/source/factoryMethod 等元数据如何支撑候选选择和来源排查？ |
| `bean-name-and-alias.md` | beanName 和 alias 如何影响定位、注入和排障？ |
| `bean-definition-overriding.md` | 同名 BeanDefinition 冲突时，谁生效、谁失败、什么时候失败？ |
| `merged-bean-definition.md` | MergedBeanDefinition / RootBeanDefinition 在什么阶段形成，解决什么问题？ |
| `configuration-and-bean-method.md` | `@Configuration`、`@Bean`、`proxyBeanMethods` 各自改变了什么？ |
| `import-selector-and-registrar.md` | `@Import`、ImportSelector、ImportBeanDefinitionRegistrar 的边界在哪里？ |
| `programmatic-registration.md` | `registerBeanDefinition`、`registerBean`、`registerSingleton` 的根本差异是什么？ |
| `refresh-mainline.md` | `refresh()` 这条主线到底先做什么、后做什么？ |
| `container-bootstrap-and-infrastructure.md` | 为什么注解处理器、自动装配和基础设施能够在容器里生效？ |
| `post-processors-overview.md` | BFPP / BDRPP / BPP 的职责边界是什么，分别属于定义阶段还是实例阶段？ |
| `beanfactory-post-processors.md` | BFPP 在什么时候修改已有 BeanDefinition，不能做什么？ |
| `bdrpp-definition-registration.md` | BDRPP 为什么能在普通 BFPP 之前新增或改写 BeanDefinition？ |
| `beanpost-processors.md` | BPP 如何介入实例创建，什么时候会把 bean 换成 proxy？ |
| `post-processor-ordering.md` | PriorityOrdered、Ordered、无序处理器的排序规则如何影响行为？ |
| `programmatic-bpp-registration.md` | 手工添加 BeanPostProcessor 为什么会绕过容器排序？ |
| `pre-instantiation-short-circuit.md` | `postProcessBeforeInstantiation` 为什么能让构造器根本不执行？ |
| `bean-creation-mainline.md` | `doGetBean()` / `doCreateBean()` 的主线是什么？ |

### 5.2 依赖解析与注入

| 建议主文档 | 核心问题 |
| --- | --- |
| `dependency-injection-resolution.md` | 注入点到底向容器提出了什么需求？ |
| `dependency-descriptor-and-injection-point.md` | DependencyDescriptor / InjectionPoint 里有哪些元数据可用于排障？ |
| `autowire-candidate-selection.md` | 候选 bean 是如何被收集、筛选、收敛的？ |
| `qualifier-primary-priority-order.md` | `@Qualifier`、`@Primary`、`@Priority`、`@Order` 各自管哪一步？ |
| `resource-vs-autowired.md` | `@Resource` 的 name-first 与 `@Autowired` 的 by-type 有何本质差异？ |
| `optional-and-provider-injection.md` | Optional、`required=false`、`ObjectProvider`、`Provider` 怎么表达可选与延迟？ |
| `resolvable-dependency.md` | 为什么有些对象能注入，但它们不是 Bean？ |
| `generic-type-matching.md` | 泛型信息如何参与注入匹配，代理为什么会让它失真？ |
| `injection-phase.md` | field injection 与 constructor injection 处在什么阶段，观察点有什么不同？ |

### 5.3 生命周期、Scope 与代理边界

| 建议主文档 | 核心问题 |
| --- | --- |
| `scope-and-prototype.md` | singleton、prototype、其他 scope 的行为边界是什么？ |
| `custom-scope-and-scoped-proxy.md` | 自定义 Scope 与 scoped proxy 如何改变注入对象和目标对象的关系？ |
| `lazy-semantics.md` | lazy-init 与注入点 `@Lazy` 分别延迟了什么？ |
| `depends-on.md` | `dependsOn` 如何强制初始化顺序，为什么它不是依赖注入规则？ |
| `lifecycle-callbacks.md` | Aware、init、destroy、`@PostConstruct` 的顺序如何理解？ |
| `smart-initializing-singleton.md` | `SmartInitializingSingleton` 为什么要等所有单例都创建完？ |
| `smart-lifecycle.md` | `SmartLifecycle` 的 start/stop 与 phase 顺序如何工作？ |
| `circular-dependency.md` | 循环依赖究竟解决了什么，解决不了什么？ |
| `early-reference-and-three-level-cache.md` | early reference 与三级缓存如何协作？ |
| `proxying-phase.md` | BPP 在哪个窗口把 bean 包装成 proxy，自调用为什么绕过它？ |
| `factorybean.md` | FactoryBean 的产品对象和工厂对象如何区分？ |
| `factorybean-type-matching.md` | FactoryBean 的类型匹配边界在哪里，`getObjectType()` 为什么关键？ |
| `context-hierarchy.md` | 父子 ApplicationContext 的可见性和覆盖边界是什么？ |
| `beanfactory-api-and-autowirecapablebeanfactory.md` | BeanFactory API 与 AutowireCapableBeanFactory 的边界是什么？ |

### 5.4 值解析、转换与外部输入

| 建议主文档 | 核心问题 |
| --- | --- |
| `environment-and-propertysource.md` | Environment / PropertySource 如何决定值从哪里来？ |
| `value-placeholder-resolution.md` | `${...}` 占位符何时 strict，何时 non-strict？ |
| `spel-and-value-expression.md` | `#{...}` SpEL 与 `${...}` 占位符的解析顺序是什么？ |
| `type-conversion-and-beanwrapper.md` | BeanWrapper、ConversionService、PropertyEditor 各负责哪一段？ |
| `xml-bean-definition-reader.md` | XML 如何变成 BeanDefinition？ |
| `properties-and-groovy-reader.md` | Properties / Groovy 这类输入如何变成 BeanDefinition？ |
| `xml-namespace-extension.md` | XML namespace 扩展如何把自定义标签变成定义？ |
| `method-injection.md` | lookup-method / replaced-method 解决的是什么动态取对象问题？ |
| `built-in-factorybeans.md` | Spring 内置 FactoryBean 的常见形态有哪些？ |

### 5.5 Boot 叠加后的变化

| 建议主文档 | 核心问题 |
| --- | --- |
| `boot-auto-configuration-ordering.md` | Auto-configuration 的顺序为什么会影响条件命中？ |
| `boot-auto-configuration-beans.md` | Boot 自动装配如何决定一个 Bean 出现还是退回 backoff？ |

### 5.6 AOT / Native 场景

| 建议主文档 | 核心问题 |
| --- | --- |
| `aot-runtimehints.md` | RuntimeHints 为什么是构建期契约？ |
| `aot-xml-bean-definition-reader.md` | AOT 语境下 XML BeanDefinitionReader 的边界是什么？ |
| `aot-autowirecapablebeanfactory-external-objects.md` | 容器外对象注入在 AOT 下怎样成立？ |
| `aot-spel-and-value-expression.md` | SpEL / Value 在 AOT 下会遇到什么约束？ |
| `aot-custom-qualifier.md` | 自定义 Qualifier 在 AOT 下要补什么契约？ |
| `aot-xml-namespace-extension.md` | XML namespace 扩展在 AOT 下为什么需要额外约束？ |
| `aot-beandefinitionreader-other-inputs.md` | Properties / Groovy 等输入在 AOT 下有哪些边界？ |
| `aot-method-injection.md` | 方法注入在 AOT 下为什么需要单独验证？ |
| `aot-built-in-factorybeans.md` | 内置 FactoryBean 在 AOT 下有哪些特殊要求？ |
| `aot-property-editor-and-value-resolution.md` | PropertyEditor / 值解析在 AOT 下如何落地？ |
| `aot-native-overview.md` | 为什么 JVM 可运行不等于 Native 可运行？ |

---

## 6. 支持文档边界

支持文档保留，但职责要收紧。

### 6.1 README

`spring-core-beans/README.md` 只保留：

- 模块总入口。
- 阅读路线。
- 运行命令。
- 症状导航。

它不负责解释某个知识点的机制细节。

### 6.2 Guide

`guide-*` 只负责学习路线、断点入口和阅读顺序，不重复机制细节。它们可以告诉读者“该去哪个主文档”，但不能把主文档的内容再讲一遍。

`boot-debugging-and-observability.md` 这类观察/排障入口也归入支持文档：它可以聚合 Actuator、ConditionEvaluationReport、日志和断点入口，但不拥有新的 Bean 机制知识点。

### 6.3 Appendix

`appendix-*` 只做速查、术语、排障、索引、测试入口。`appendix-common-pitfalls.md` 只保留误区对照和跳转，不再展开机制长文。

`appendix-knowledge-map.md` 会成为全量知识点索引：一行一个主文档，只负责把症状、知识点和主文档连起来，不承担机制解释。

### 6.4 deepening

`deepening-*` 改写为维护者文档，每篇只负责一个维护面，例如：

- 这套文档为什么要重写。
- 哪些页面是知识点主文档。
- 哪些页面是索引页。
- 哪些页面是维护页。
- 哪些交叉链接最容易失控。

它们不能再写成教程正文，也不能复述主文档机制。

---

## 7. 去重规则

### 7.1 单点归属

一个知识点只能有一个主文档。若某个主题同时出现在多个文件中，必须选出主文档，其余文件只保留一句指向性描述。

### 7.2 不允许拆散同一条机制

同一条机制不得按“总览 / 细节 / 误区 / 维护说明”拆到多个文件里。可以在一个文档里分小节，但不能在多个文档里重复解释。

### 7.3 允许主题合并

如果两个文档只是同一问题的不同视角，应该合并成一个主文档，而不是保留两份半重复内容。

### 7.4 允许主题拆分

如果一个文档同时讲了两个独立机制，就必须拆分。拆分后的两个文档要能各自成立，不能互相依赖才能读懂。

### 7.5 支持文档只做指针

索引页、术语页、排障页可以列很多链接，但不能用大段机制解释去占据主文档的职责。

---

## 8. 代码与测试联动

这次重写的默认姿态是“先改文档，必要时改最小的代码或测试来让文档成立”。

### 8.1 必须对齐的对象

- `SpringCoreBeansDocumentationContractTest`
- `SpringCoreBeansModuleContractLabTest`
- 所有被文档引用的 `SpringCoreBeans*Test`
- 所有文档中出现的本地 Markdown 链接

### 8.2 允许的代码调整

仅在以下场景允许同步改代码或测试：

- 文档引用的 Lab / helper 不存在。
- 文档描述的观察点与实际实现不一致。
- testsupport 输出不能稳定支撑新的知识点切分。

### 8.3 不允许的代码调整

- 不为“让文档好看”而改生产逻辑。
- 不为“统一表达”而改无关行为。
- 不为“减少文档改动”而伪造测试入口。

---

## 9. 验证标准

### 9.1 文档层

- 每个主文档只拥有一个清晰知识点。
- 主文档之间不重复解释同一机制。
- 支持文档不再承担机制正文职责。
- `README.md` 保持简洁，不回到超长目录清单。

### 9.2 链接层

- 所有本地 Markdown 链接可解析。
- 所有文档中引用的 `SpringCoreBeans*Test` 类存在。
- 目录、索引、主文档之间的跳转是闭环的。

### 9.3 体验层

- 读者可以从症状导航进入一个主文档。
- 读者可以在一个主文档里读完一个知识点。
- 读者不会在三个地方看到同一条机制的重复解释。

---

## 10. 交付顺序

后续修改建议按这个顺序推进：

1. 先定主文档清单。
2. 再收紧 `README.md` 和知识地图。
3. 再重写主文档。
4. 再把 `guide-*`、`appendix-*`、`deepening-*` 收缩成支持文档。
5. 最后跑文档契约测试，补齐缺失的测试引用或最小代码。

---

## 11. 完成标准

这份 spec 对应的工作完成时，应该满足以下结果：

- `spring-core-beans` 的知识点边界清楚。
- 一个知识点只在一个主文档里讲。
- 目录页和维护页不再重复正文内容。
- README 只做入口。
- 文档与测试、断点和 Lab 保持一致。
