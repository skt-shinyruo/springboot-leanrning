# 逐章补强建议（part-04-wiring-and-boundaries 装配语义与边界）

本 Part 的补强重点是“真实工程边界”：Lazy/dependsOn/resolvable dependency/context hierarchy/FactoryBean/代理/占位符/泛型匹配等，目标是让读者能从症状快速定位到第一断点入口。

### 第 23 章：18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/01-lazy-semantics.md`
- 补强策略：
  - 明确区分两类 Lazy：bean 定义层的 lazy-init vs 注入点的 `@Lazy`（懒代理），并分别给出“何时创建对象”的证据链入口。
  - 补充“懒代理的形态与限制”：JDK vs CGLIB、final 类/方法限制、调试器如何识别懒代理是否已触发初始化。
  - 串联循环依赖与代理产生阶段：解释 Lazy 为什么能缓解某些循环依赖，以及它可能引入的新坑。

### 19. dependsOn：强制初始化顺序（即使没有显式依赖）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/02-depends-on.md`
- 补强策略：
  - 增加“dependsOn 生效的窗口”：它改变的是创建顺序还是依赖解析？给出具体入口方法与观测变量。
  - 补充与 Lazy/SmartInitializingSingleton 的交互：强制提前初始化可能改变代理时机与回调顺序。
  - 增加排障视角：为什么“看起来无依赖也被初始化了”，如何从日志/断点证明 dependsOn 触发链路。

### 20. registerResolvableDependency：能注入，但它不是 Bean

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/03-resolvable-dependency.md`
- 补强策略：
  - 深化“它不是 Bean”的含义：不会出现在 BeanDefinition/beanName 集合里，但会参与依赖解析；给出最短证据链。
  - 补齐常见内置 resolvable dependencies 的清单与来源（例如 Environment、ResourceLoader 等），并说明为何这样设计。
  - 增加误区与边界：为什么不能用它做候选收敛、为什么不会触发完整生命周期。

### 21. 父子 ApplicationContext：可见性与覆盖边界

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/04-context-hierarchy.md`
- 补强策略：
  - 增补“可见性规则表”：lookup/注入时 parent/child 的搜索顺序、覆盖与隔离边界（并落到具体入口方法）。
  - 补充事件传播与环境继承：Context hierarchy 下常见误判点，以及如何用断点证明事件/资源查找路径。
  - 串联 Boot：在多 context（父/子/management）场景下如何排障 bean 不可见/重复定义。

### 22. Bean 名称与 alias：同一个实例，多一个名字

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/05-bean-names-and-aliases.md`
- 补强策略：
  - 增加“命名/别名/覆盖”的统一模型：alias 与 beanName 的注册结构、lookup 的路径与优先级。
  - 补齐与 `@Qualifier`、`@Resource`（按名优先）、FactoryBean `&` 的串联，帮助读者把“按名”理解成一条主线。
  - 增加排障案例：为什么同一个对象能被多个名字拿到，如何确认 alias 是否导致误注入。

### 23. FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/06-factorybean-deep-dive.md`
- 补强策略：
  - 强化 type matching 的关键点：`getObjectType`/`SmartFactoryBean`/eager init 如何影响按类型发现与注入。
  - 补齐“两个缓存”的精确语义：FactoryBean 实例缓存与 product 缓存分别在哪，如何在调试器中验证缓存命中。
  - 串联 AOT/Native：FactoryBean 产生的代理/反射需求如何落到 RuntimeHints（链接 Part 05）。

### 24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/07-bean-definition-overriding.md`
- 补强策略：
  - 增补“版本与开关差异”说明：Spring/Boot 在覆盖策略上的默认值与配置入口（避免读者用错结论）。
  - 补齐“覆盖发生在哪个阶段”：定义注册阶段冲突 vs 实例创建阶段冲突的区别与排障入口。
  - 增加工程建议：何时允许覆盖、如何在团队层面避免隐式覆盖带来的不可预期。

### 25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/08-programmatic-bpp-registration.md`
- 补强策略：
  - 讲透“为什么顺序会变”：手工添加绕过默认注册流程后，PriorityOrdered/Ordered 的排序可能失效；给出证据链入口。
  - 增加“副作用列表”：提前创建 bean、影响 AOP 代理、影响 @Autowired/@Value 等注解处理器执行次序。
  - 增补排障：出现“某些增强不生效/增强顺序错乱”的第一断点入口与观察变量。

### 26. SmartInitializingSingleton：所有单例都创建完之后再做事

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/09-smart-initializing-singleton.md`
- 补强策略：
  - 补齐与 `ContextRefreshedEvent` 的对比：两者触发点不同，适用场景不同，避免误用。
  - 增加与 Lazy 的交互说明：lazy 单例是否会影响 afterSingletonsInstantiated 的语义与期望。
  - 增补工程用例：warm-up、缓存预热、校验配置等，并给出“不会引入早期初始化副作用”的写法建议。

### 27. SmartLifecycle：start/stop 时机与 phase 顺序

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md`
- 补强策略：
  - 深化 phase 排序与依赖：如何保证多个组件按顺序 start/stop，如何在调试器中验证排序结果。
  - 补齐与 graceful shutdown 的连接：stop 的触发窗口、常见误区（资源未释放、线程未停）。
  - 增加排障入口：为什么组件没启动/没停止、autoStartup 与 isRunning 状态不一致时怎么定位。

### 28. 自定义 Scope + scoped proxy：thread scope 的真实语义

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/11-custom-scope-and-scoped-proxy.md`
- 补强策略：
  - 补齐 Scope 接口的关键契约：get/remove/registerDestructionCallback 的语义与典型误用（尤其是销毁回调）。
  - 强化 scoped proxy 的“注入语义”：注入的其实是代理而非目标对象，如何调试、如何避免 thread-local 泄漏。
  - 增加“何时不该自定义 scope”的反例：用更简单的手段（ObjectProvider/显式工厂）替代时的判断依据。

### 29. FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/12-factorybean-edge-cases.md`
- 补强策略：
  - 深化“为什么返回 null 会坏掉”：把按类型发现的算法与缓存路径讲透，并给出具体入口方法。
  - 补齐修复方案对比：实现 getObjectType、实现 SmartFactoryBean、提供显式 @Bean 类型声明等各自的成本与适用场景。
  - 增加“误区清单”：例如以为 getObjectType 只影响工具/IDE，其实会影响注入与条件判断。

### 30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md`
- 补强策略：
  - 把“注入发生在哪”讲成可断点证明：构造器注入 vs 属性注入分别在哪个窗口完成。
  - 增加“为何构造器注入更利于排障/重构”的工程论证：与循环依赖、不可变性、测试隔离的关系。
  - 补齐 `InstantiationAwareBeanPostProcessor#postProcessProperties` 的典型应用：如 @Autowired/@Value 的属性注入处理链路。

### 31. 代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md`
- 补强策略：
  - 增加“代理链分解”：AOP 代理、lazy 代理、configuration 代理之间的区别、叠加顺序与调试识别方法。
  - 深化 self-invocation 的根因与可复现证明：为什么同类内部调用绕过代理，如何用断点/调用栈证明。
  - 串联 early reference：在循环依赖窗口期产生代理时会发生什么，与最终对象不一致的风险在哪里。

### 32. `@Resource` 注入：为什么它更像“按名称找 Bean”？

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/15-resource-injection-name-first.md`
- 补强策略：
  - 增补“按名优先”的完整决策过程：name 指定、默认字段名、fallback 到类型的边界，给出证据链入口。
  - 串联 alias 与 beanName：解释为什么 alias 会影响 @Resource 行为，以及如何排障“看起来注入错了对象”。
  - 增加与 @Autowired/@Qualifier 的对照：让读者能在真实项目中选择合适注入方式。

### 33. 候选选择 vs 顺序：`@Primary` / `@Priority` / `@Order` / `@Qualifier` 的边界

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md`
- 补强策略：
  - 强化“选择 vs 排序”的边界：`@Primary/@Qualifier` 影响候选收敛，`@Order` 多用于集合注入排序；把典型误区讲透。
  - 增补“同名/同类型多候选”的决策树：按类型、按名称、限定符、优先级、默认候选的完整顺序。
  - 串联 Boot back-off：解释 auto-config 与用户 bean 并存时，候选如何被收敛到期望对象。

### 34. `@Value("${...}")` 占位符解析：默认 non-strict vs strict fail-fast

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md`
- 补强策略：
  - 增加“解析链路证据链”：PropertySource → placeholder resolver → 注入点赋值的关键窗口与入口方法。
  - 串联 SpEL：明确 `${...}` 与 `#{...}` 的链路差异，帮助读者在报错时快速分型（链接 Part 05 SpEL 章）。
  - 增补“strict vs non-strict”在工程上的取舍：什么时候应该 fail-fast，如何做环境/测试隔离。

### 35. BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/18-merged-bean-definition.md`
- 补强策略：
  - 深化“合并的触发点”：在哪个窗口从 Generic/Child 合并成 Root，哪些元数据最终生效，给出证据链入口。
  - 串联 MBPP（MergedBeanDefinitionPostProcessor）：哪些注解元信息处理依赖 merged BD，为什么顺序与缓存会影响结果。
  - 增加排障案例：为什么你看到的 BeanDefinition 与最终行为不一致（原因往往在 merged）。

### 36. 类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md`
- 补强策略：
  - 把“属性访问”与“类型转换”分层：BeanWrapper 负责属性路径与访问，ConversionService/PropertyEditor 负责转换，避免概念混淆。
  - 增补“值从定义层到对象”的链路：与 Part 05 的值解析章建立更强串联（TypedStringValue/ValueResolver 等）。
  - 增加工程建议：什么时候用 Converter、什么时候还会遇到 PropertyEditor（legacy），以及常见坑（集合/枚举/日期）。

### 37. 泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/20-generic-type-matching-pitfalls.md`
- 补强策略：
  - 强化“类型信息丢失”的根因：代理、桥接方法、父类泛型擦除等导致的 ResolvableType 判定差异。
  - 补齐“如何证明匹配失败”：在依赖解析入口处观察 ResolvableType 的实际推断结果，避免只凭感觉。
  - 增加“修复手段对比”：显式限定符、接口抽象、避免过度泛型、通过 @Primary 收敛等。

### 38. Environment Abstraction：PropertySource / @PropertySource / 优先级与排障主线

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/21-environment-and-propertysource.md`
- 补强策略：
  - 增补“优先级规则可视化”：把不同 PropertySource 的 precedence 用可执行的观察点证明（Environment 中 propertySources 的顺序）。
  - 串联 @Value 章节：从“属性找不到/被覆盖/值不符合预期”的症状出发，给出最短排障链路。
  - 补齐与 Boot config data 的区别点：哪些是 Spring Framework 的抽象，哪些是 Boot 的扩展与默认加载。

### 39. BeanFactory API 深挖：接口族谱与手动 bootstrap 的边界

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/22-beanfactory-api-deep-dive.md`
- 补强策略：
  - 增加“接口族谱 → 能力清单”映射：Listable/Hierarchical/Configurable 等接口分别意味着哪些能力，避免只背继承图。
  - 增补“手动 bootstrap 的最小组件集”：在不使用 ApplicationContext 的情况下，要让注解工作需要哪些基础设施（串联 Part 03 的基础设施章）。
  - 增加排障视角：当你怀疑是容器能力缺失导致功能不生效时，如何快速确认当前容器实现与能力边界。

