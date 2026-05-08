# spring-core-beans 文档重写设计

日期：2026-05-08

## 背景

`spring-core-modules/spring-core-beans/docs` 当前有 95 个 Markdown 文档。README 是 docs 目录的顺序来源，`SpringCoreBeansDocumentationContractTest` 负责校验：

- README 必须覆盖 docs 下所有 Markdown。
- README 与 docs 内的本地 Markdown 链接必须可解析。
- 面向读者的正文页必须保留 `CHAPTER-CARD` 章节入口；`deepening-*` 维护文档除外。
- 文档中引用的 `SpringCoreBeans*Test` 必须真实存在。

现有文档已经建立了知识点 owner、Lab 入口和链接骨架，但大量正文是固定模板化表达：归属边界、观察口径、相邻跳转、小结反复出现，缺少对知识点本身的解释、源码路径和可验证推理。用户明确要求：文档要根据知识点自然写作，不使用固定模板；同时兼顾教程型学习和源码导读。

## 目标

重写后的文档应满足三个目标：

1. 按知识点讲清楚机制：读者能理解 Spring Beans / IoC 容器中每个主题解决什么问题、发生在哪个阶段、边界在哪里。
2. 能进入源码：关键主题给出 Spring 调用链、核心类或方法、断点位置和阅读顺序。
3. 能被本模块验证：每篇主文档绑定已有 Lab 或测试入口，结论优先来自可运行用例，而不是口头断言。

## 非目标

- 不改模块 Java 代码和测试代码，除非后续发现现有 Lab 无法支撑必要文档结论。
- 不一次性删除现有文件名或打乱 README 目录结构。
- 不把支持文档写成重复教程。
- 不追求每篇相同长度、相同标题层级或相同段落结构。

## 文档类型

### 主文档

主文档承载真实知识点。每篇围绕自己的问题自然组织，不套统一模板。例如：

- `bean-creation-mainline.md` 应围绕 `getBean -> doGetBean -> createBean -> doCreateBean -> populateBean -> initializeBean` 的主线写。
- `autowire-candidate-selection.md` 应围绕候选收集、`DependencyDescriptor`、`@Primary`、`@Qualifier`、`@Priority` 和名称收敛写。
- `early-reference-and-three-level-cache.md` 应围绕三级缓存、early reference、代理早期暴露和失败边界写。
- `factorybean.md` 应围绕工厂对象、产品对象、`&beanName`、类型预测和缓存语义写。

每篇主文档必须保留章节入口卡片和真实 Lab 引用，但正文结构由知识点决定。可以使用时间线、源码链路、对照表、排障分支、代码片段或案例复盘，选择最能解释该知识点的写法。

### 支持文档

`guide-*`、`appendix-*` 和 `boot-debugging-and-observability.md` 主要负责导航、索引、断点组、排障路线、自检和训练编排。它们不重复主文档机制，只把读者送到正确 owner 文档和 Lab。

### 维护文档

`deepening-*` 文档只写维护准则：owner 边界如何判断、哪些文件需要同步、哪些测试必须运行、常见漂移如何发现。它们不写成教程正文。

## 重写批次

按知识域分批处理，避免全量一次性改写造成链接和知识归属漂移。

1. 容器与注册：Bean 心智模型、BeanDefinition 注册、refresh、post processor、创建主线等。
2. 依赖解析与注入：`DependencyDescriptor`、候选选择、限定符、`@Resource`、可选依赖、泛型匹配等。
3. 生命周期、Scope 与代理边界：scope、lazy、dependsOn、生命周期、循环依赖、early reference、FactoryBean、父子容器等。
4. 值解析、转换与外部输入：Environment、占位符、SpEL、类型转换、XML、Properties、Groovy、namespace、方法注入、内置 FactoryBean 等。
5. Boot 叠加层：自动装配顺序、backoff、ConditionEvaluationReport 与可观察性。
6. AOT / Native：RuntimeHints、XML、外部对象注入、SpEL、Qualifier、namespace、reader、方法注入、FactoryBean、PropertyEditor 等边界。
7. Guide / Appendix / Deepening：在主文档稳定后收敛支持页和维护页，删除模板味重复表述。

每个批次完成后运行文档契约测试。批次之间保留可提交状态。

## 写作准则

- 先写读者会遇到的问题，再写机制，不从术语堆叠开始。
- 结论必须能落到 Spring 类、方法、容器阶段或本模块 Lab。
- 一篇文档只拥有一个清晰知识点；相邻主题用链接跳转，不在正文里复制另一篇的解释。
- 允许每篇采用不同结构。结构服务于知识点，不服务于统一外观。
- 源码导读要给阅读顺序和判断点，不只列类名。
- 表格只用于比较和分支判断，不用于填充篇幅。
- 小结只总结关键判断，不重复标题。
- 支持文档只做导航和检查清单，不承担机制解释。

## 验收标准

单篇文档的完成标准：

- 章节入口卡片仍在，引用的测试类存在。
- 文档能回答该文件名和 README 标题承诺的问题。
- 至少包含一个可运行 Lab、关键源码入口或可调试观察点。
- 没有模板化占位句，例如“本页负责把这个问题收束到一个可运行证据入口”这类通用填充。
- 相邻链接存在且职责不重叠。

批次完成标准：

- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test` 通过。
- 如果批次涉及 testsupport 或模块契约入口，同步运行 `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansModuleContractLabTest test`。
- README 目录仍覆盖所有 docs 文件。
- 新增、删除或重命名文档时同步 `appendix-knowledge-map.md` 和 README。

## 风险与控制

- 风险：主文档扩写后知识点重复。控制：每篇只拥有一个 owner 问题，相邻内容只链接。
- 风险：源码导读引用 Spring 内部方法随版本漂移。控制：以当前依赖版本和本模块测试为准，避免写无法验证的版本泛化结论。
- 风险：批量修改导致链接断裂。控制：每批运行文档契约测试。
- 风险：支持文档继续膨胀。控制：支持页只保留路线、索引、断点、Lab 和 checklist。

## 实施策略

先从“容器与注册”批次开始，因为它是后续依赖解析、生命周期、Boot 和 AOT 的共同主线。第一批建议优先重写：

- `bean-mental-model.md`
- `bean-definition-registration.md`
- `refresh-mainline.md`
- `container-bootstrap-and-infrastructure.md`
- `post-processors-overview.md`
- `beanfactory-post-processors.md`
- `bdrpp-definition-registration.md`
- `beanpost-processors.md`
- `post-processor-ordering.md`
- `bean-creation-mainline.md`

第一批完成后再根据实际篇幅和测试结果决定是否继续同一轮扩展到注册细节页，如 `bean-name-and-alias.md`、`bean-definition-overriding.md`、`merged-bean-definition.md`、`configuration-and-bean-method.md`、`import-selector-and-registrar.md`、`programmatic-registration.md`。
