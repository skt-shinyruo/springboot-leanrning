# spring-core-beans 文档课程化重构设计

日期：2026-05-15

## 背景

`spring-core-modules/spring-core-beans/docs` 目前是扁平知识点目录，文件数量多，阅读顺序依赖模块 `README.md`。上一轮重写已经让部分主题页从模板化说明变成机制解释，但整体仍然更像知识点索引，而不是一套可以顺读、跳读、训练和排障的课程材料。

这次重构允许大幅调整 `docs/*.md`：可以重命名、合并、删除旧文件，不保留旧路径跳转页。目标不是维护旧文档网络，而是把内容重写成一组更少、更完整、更容易阅读的课程章节。

用户已经明确几项写作约束：

- 不使用用户明确排除的泛化说法，标题和正文直接使用 Spring 对象、接口、流程、阶段和问题描述。
- 文档文件名和标题不带顺序编号；排序只在模块 `README.md` 中表达。
- 正文页不套固定栏目，不为了覆盖固定模板而写“本章回答什么”“源码入口”“常见误区”等统一段落。
- 正文页不维护互相链接的导航网络；`README.md` 负责链接所有正文文件。
- 删除 `CHAPTER-CARD` 相关契约，不在新正文页里保留这类标记。

## 目标

重构后的 `spring-core-beans` 文档应同时服务三类使用方式：

- 入门顺读：读者能从 `README.md` 按顺序读完，逐步理解 Spring Beans / IoC 容器的关键流程。
- 源码深挖：有经验的读者能围绕 `ApplicationContext#refresh()`、`BeanFactory`、后处理器、依赖解析、单例创建、三级缓存、AOT 等主题进入源码。
- 团队训练和排障：团队可以用同一套材料组织内训、面试复述、自检和生产问题定位。

这三类需求通过整体课程结构覆盖，不要求每个正文页都有相同栏目。

## 非目标

- 不维护旧 `docs/*.md` 文件名兼容性。
- 不保留旧文件跳转页。
- 不在正文页之间建立完整互链。
- 不在文档中保留 `CHAPTER-CARD` 标记。
- 不继续保留“每篇文档必须有章节入口卡片”的测试约束。
- 不改业务示例和生产代码。
- 不为了文档路径提示去批量更新测试里的普通文本，除非它影响编译、契约测试或 README 导航。

## 新文档组织

`docs/` 目录保留扁平结构，但文件名不表达顺序。课程顺序只写在 `spring-core-modules/spring-core-beans/README.md`。

建议的新正文文件：

- `course-guide.md`
- `beandefinition-and-singleton-cache.md`
- `beandefinition-registration.md`
- `applicationcontext-refresh.md`
- `post-processors.md`
- `singleton-bean-creation.md`
- `dependency-resolution.md`
- `scope-lazy-and-lifecycle.md`
- `circular-reference-and-proxy.md`
- `factorybean-and-beanfactory-api.md`
- `property-resolution-and-readers.md`
- `boot-autoconfiguration-beans.md`
- `aot-native.md`
- `troubleshooting.md`
- `training-review.md`

最终实施时可以根据内容合并或调整文件名，但必须遵守两个原则：文件名不带排序编号，排序只出现在 README。

## README 职责

模块 `README.md` 是唯一导航入口，负责：

- 给出模块定位和最短运行命令。
- 按课程顺序列出 `docs/*.md`。
- 给出不同读法：顺读、源码深挖、排障、团队训练。
- 列出关键 Maven 命令，例如文档契约、模块契约、Book Matrix、Branch Matrix、Exercise Solution。

README 必须覆盖最终保留的所有 `docs/*.md` 文件。正文页不需要反向链接 README，也不需要上一页/下一页导航。

## 正文写作原则

每个正文页按知识点自然组织，不使用统一模板。可选写法包括：

- 从一个可复现现象开始，例如“为什么 `getBean("&x")` 和 `getBean("x")` 不是同一个对象”。
- 按源码流程展开，例如 `refresh()` 的阶段顺序或单例 Bean 的创建过程。
- 按对照关系展开，例如 `@Autowired`、`@Resource`、`ObjectProvider`、`ResolvableDependency` 的差异。
- 按故障分支展开，例如“找不到 Bean”“拿到的不是代理”“Native 下运行失败”。
- 按训练材料展开，例如复述题、排障演练、代码阅读任务。

正文应尽量使用具体 Spring 类型、方法、阶段和测试入口支撑结论。可以引用类名、方法名、测试类名和 Maven 命令，但不要求把这些内容做成固定栏目。

## 内容归并方向

`course-guide.md` 承担读法说明、读者路径、命令入口和维护约定。旧 `deepening-*` 文档的有效内容可以并入这里或删除。

`beandefinition-and-singleton-cache.md` 合并旧的 Bean 基础对象关系内容，覆盖 `BeanDefinition`、运行时实例、单例缓存、别名、最终暴露对象等基础关系。

`beandefinition-registration.md` 合并注册入口相关内容，覆盖组件扫描、`@Bean`、`@Import`、`ImportSelector`、`ImportBeanDefinitionRegistrar`、编程式注册、覆盖规则和来源排查。

`applicationcontext-refresh.md` 聚焦 `ApplicationContext#refresh()`，覆盖容器启动阶段、基础设施 Bean 注册、定义阶段和实例阶段的分界。

`post-processors.md` 合并 BDRPP、BFPP、BPP、排序、手工注册、实例化前短路等内容。

`singleton-bean-creation.md` 聚焦 `getBean`、`doGetBean`、`createBean`、`doCreateBean`、属性填充、初始化、销毁和最终暴露。

`dependency-resolution.md` 合并依赖解析和候选选择内容，覆盖 `DependencyDescriptor`、`InjectionPoint`、`@Primary`、`@Qualifier`、`@Priority`、`@Order`、泛型、可选依赖、Provider、`@Resource`、可解析依赖。

`scope-lazy-and-lifecycle.md` 合并 scope、prototype、custom scope、scoped proxy、lazy、dependsOn、生命周期回调、`SmartInitializingSingleton`、`SmartLifecycle`。

`circular-reference-and-proxy.md` 合并循环依赖、early reference、三级缓存、代理发生阶段、raw injection 等内容。

`factorybean-and-beanfactory-api.md` 合并 `FactoryBean`、类型预测、内置 FactoryBean、`BeanFactory`、`AutowireCapableBeanFactory`、外部对象注入等内容。

`property-resolution-and-readers.md` 合并 Environment、PropertySource、占位符、SpEL、类型转换、BeanWrapper、XML、Properties、Groovy、XML namespace、方法注入。

`boot-autoconfiguration-beans.md` 合并 Boot 自动配置顺序、backoff、ConditionEvaluationReport、Bean 来源观察和调试内容。

`aot-native.md` 合并 AOT / Native 相关主题，覆盖 RuntimeHints、XML reader、namespace、SpEL、Qualifier、FactoryBean、PropertyEditor、方法注入和外部对象注入限制。

`troubleshooting.md` 汇总常见问题定位路线，不重复讲完整机制，重点是现象、可能原因、验证命令和下一步阅读位置。

`training-review.md` 汇总内训脚本、自检题、面试复述、Lab 索引、公共 API 索引和团队训练安排。

## 契约测试调整

`SpringCoreBeansDocumentationContractTest` 需要从旧规则调整为更轻的导航检查：

- README 中链接到的本地 Markdown 文件必须存在。
- README 必须覆盖 `docs/` 下所有 Markdown 文件。
- 文档中引用的 `SpringCoreBeans*Test` 类名如果存在于 Markdown 中，仍应校验测试类是否真实存在。
- 删除面向读者正文页必须包含 `CHAPTER-CARD` 的检查。
- 不要求扫描所有正文页里的本地 Markdown 链接；正文页不再承担导航网络职责。

`SpringCoreBeansModuleContractLabTest` 可以继续包含文档契约测试和测试支撑层输出契约。

## 实施策略

实施应以内容重构为主，不做逐文件机械迁移。

建议步骤：

- 先改文档契约测试，删除 `CHAPTER-CARD` 要求，收窄链接检查范围到 README。
- 新建目标正文文件，按课程结构重写核心内容。
- 删除旧碎片文档和维护文档。
- 重写模块 README，把它作为唯一顺序来源。
- 运行文档契约测试，修正 README 覆盖和测试类引用问题。
- 运行模块契约测试，确认 testsupport 输出契约仍然稳定。

## 验收标准

重构完成后应满足：

- `spring-core-modules/spring-core-beans/docs` 中不再出现旧的碎片化正文集合。
- `docs/*.md` 文件名不带排序编号。
- 正文页不包含 `CHAPTER-CARD` 标记。
- README 覆盖所有保留的 Markdown 文档，并负责唯一阅读顺序。
- 正文页不套固定栏目，表达方式跟随知识点本身。
- 不出现用户明确排除的泛化表达。
- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test` 通过。
- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansModuleContractLabTest test` 通过。

## 风险与控制

大幅删除和重命名会让旧路径失效。用户已明确允许不保留旧链接，因此控制重点不是兼容旧路径，而是保证 README 的新入口完整、清晰。

正文页取消互链后，读者主要通过 README 跳转。README 必须写得足够清楚，避免读者在正文页之间迷路。

合并过度可能让单篇文档过长。实施时应按知识点自然拆分，不追求固定篇数；如果某个主题明显过长，可以拆成两个无序号文件，并在 README 中安排顺序。

测试文本里可能仍有旧文档路径提示。除非它影响编译或契约测试，本轮不把这些普通文本作为必须维护的链接。
