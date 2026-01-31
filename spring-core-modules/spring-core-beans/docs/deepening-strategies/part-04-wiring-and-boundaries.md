# 逐章内容级再加深建议（part-04-wiring-and-boundaries）

本 Part 的再加深重点：工程边界与真实误区（Lazy/dependsOn/resolvable dependency/层级/命名/FactoryBean/代理/占位符/转换/泛型匹配等），要求每章都能提供可复现反例与排障 SOP。

## 执行化提示（边界章的“可复现反例”优先）

- 每章至少补 1 个“误诊对照”：现象相似但机制不同（例如 depends-on 环 vs 循环依赖）。
- 每章至少补 1 个“第一断点入口 + watch list”：让读者能在 1 分钟内把问题钉在正确分支。

### 18. Lazy：lazy-init bean vs `@Lazy` 注入点（懒代理）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/023-18-lazy-semantics.md`
- 内容级加深策略：
  - A：补“两类 Lazy 的证据链对照”：lazy-init 的创建时机 vs 注入点 @Lazy 的代理时机。
  - B：补反例：懒代理叠加 AOP/循环依赖时的偏差；final 类/方法限制。
  - C：补排障：为什么读者可能观察到 lazy bean 被提前创建？如何判断是 dependsOn 拉起还是 proxy 触发。
  - D：补断点：代理创建点、首次触发目标创建点、注入解析分支。
  - E：补面试追问：@Lazy 与 ObjectProvider 的选择策略与边界。

### 19. dependsOn：强制初始化顺序

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/19-depends-on.md`
- 内容级加深策略：
  - A：补“doGetBean 内 dependsOn 处理”的关键分支证据链（包含依赖图写入点）。
  - B：补反例：Circular depends-on 与三级缓存循环依赖的误判对照。
  - C：补排障：lazy-init 被拉起、关闭顺序反直觉、写错 beanName 的三类 SOP。
  - D：补 watch list：dependentBeanMap/dependenciesForBeanMap 的判定标准。
  - E：补面试追问：dependsOn 为什么不等于注入依赖？如何证明。

### 20. registerResolvableDependency：能注入但不是 Bean

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/20-resolvable-dependency.md`
- 内容级加深策略：
  - A：补“命中在 doResolveDependency 之前”的证据链，并对比 bean candidates 分支。
  - B：补反例：滥用导致候选收敛被绕过、与 @Qualifier 选择语义冲突。
  - C：补排障：为什么能注入但 getBeansOfType 查不到？如何证明不是 BeanDefinition。
  - D：补断点：resolvableDependencies 命中、AutowireUtils 解包、Aware 回调对照。
  - E：补面试追问：它与 *Aware 的边界与适用场景。

### 21. 父子 ApplicationContext：可见性与覆盖边界

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/21-context-hierarchy.md`
- 内容级加深策略：
  - A：补“可见性规则与搜索顺序”的方法级证据链（child→parent）。
  - B：补反例：同名 bean 覆盖、同 type 不可见、event/环境继承误判。
  - C：补排障：多 context 场景 bean 不可见/注入到错误上下文的 SOP。
  - D：补断点：父子容器查找、registerSingleton/registerBeanDefinition 的覆盖点。
  - E：补面试追问：为什么说 ApplicationContext 在 BeanFactory 之上增加设施？与 hierarchy 如何关联。

### 22. Bean 名称与 alias

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/22-bean-names-and-aliases.md`
- 内容级加深策略：
  - A：补“alias 注册结构与 lookup 路径”的证据链，并对比按类型发现。
  - B：补反例：@Resource 注入错对象、FactoryBean `&` 前缀误判、覆盖策略冲突。
  - C：补排障：按名注入/按名获取行为异常时如何定位 alias 与 canonicalName。
  - D：补 watch list：aliasMap、canonicalName、beanName 解析入口。
  - E：补面试追问：为什么说“@Resource 更像按名称找”？alias 如何影响它。

### 23. FactoryBean 深潜：product vs factory、类型匹配、缓存语义

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/23-factorybean-deep-dive.md`
- 内容级加深策略：
  - A：补“type matching 算法与 getObjectType/isSingleton”的证据链，并给关键分支快照。
  - B：补反例：getObjectType=null 导致条件误判/按类型发现失败；SmartFactoryBean 与 eager init 的边界。
  - C：补排障：为什么按类型注入/条件装配“看起来偶发失效”，如何先判断是否 FactoryBean 语义导致。
  - D：补断点：FactoryBeanRegistrySupport 缓存、getObjectFromFactoryBean 调用链。
  - E：补面试追问：FactoryBean 的价值与高频易错点，如何用证据链解释两个缓存。

### 24. BeanDefinition 覆盖（overriding）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/24-bean-definition-overriding.md`
- 内容级加深策略：
  - A：补“覆盖发生在注册阶段”的证据链与配置入口（Framework/Boot 差异需明确）。
  - B：补反例：覆盖导致注入命中改变但不易察觉；与 auto-config back-off 的交互误判。
  - C：补排障：同名 bean 冲突/覆盖导致行为偏差的 SOP（优先核对注册来源，再核对覆盖策略）。
  - D：补观察点：注册冲突位置、BeanDefinition 源信息（如 resourceDescription）。
  - E：补面试追问：为什么团队通常不建议默认允许覆盖？如何给出工程化理由与证据。

### 25. 手工添加 BeanPostProcessor：顺序与陷阱

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md`
- 内容级加深策略：
  - A：补“绕过默认注册流程导致顺序变化”的证据链与关键列表快照。
  - B：补反例：增强偶发不生效、代理链丢失、@Autowired/@Value 行为偏移。
  - C：补排障：当怀疑 BPP 顺序问题时的第一断点入口与关键观察变量。
  - D：补断点：addBeanPostProcessor、registerBeanPostProcessors、排序位置对照。
  - E：补面试追问：为什么强烈不建议业务侧手工注册 BPP？可证明的副作用有哪些。

### 26. SmartInitializingSingleton：单例创建完之后再做事

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/26-smart-initializing-singleton.md`
- 内容级加深策略：
  - A：补“触发窗口”证据链（preInstantiateSingletons 完成后的回调触发点）。
  - B：补反例：lazy 单例不在其中；过早初始化导致副作用。
  - C：补排障：为什么 hook 没触发/触发顺序不符期望？
  - D：补断点：afterSingletonsInstantiated 的触发点与执行顺序观察。
  - E：补面试追问：它与 ContextRefreshedEvent 的差异与选择策略。

### 27. SmartLifecycle：start/stop 时机与 phase 顺序

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/27-smart-lifecycle-phase.md`
- 内容级加深策略：
  - A：补 phase 排序算法的证据链与关键列表快照。
  - B：补反例：autoStartup 与 isRunning 误判、stop 未执行导致资源泄漏。
  - C：补排障：为什么组件没启动/没停止？如何从 phase/依赖/状态收敛。
  - D：补断点：LifecycleProcessor、start/stop 调度点与观察变量。
  - E：补面试追问：SmartLifecycle 与普通 init/destroy 的边界与适用场景。

### 28. 自定义 Scope + scoped proxy

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md`
- 内容级加深策略：
  - A：补 scope 契约（get/remove/registerDestructionCallback）的证据链与典型实现骨架。
  - B：补反例：thread-local 泄漏、销毁回调不执行、代理导致类型信息丢失。
  - C：补排障：scope 失效/对象“串线程”/销毁不执行如何定位。
  - D：补断点：Scope#get、scoped proxy 创建与目标解析入口。
  - E：补面试追问：何时不该自定义 scope？替代方案怎么选。

### 29. FactoryBean 边界：getObjectType 返回 null

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/29-factorybean-edge-cases.md`
- 内容级加深策略：
  - A：补“type matching 失效”的算法证据链（条件判断/候选收集如何受影响）。
  - B：补反例：按类型注入失效但 getBean(name) 仍可用、条件装配误判。
  - C：补排障 SOP：优先确认是否为 FactoryBean，再核对 getObjectType/isSingleton 与缓存路径。
  - D：补断点：type match 分支、FactoryBean objectType 读取点。
  - E：补面试追问：为什么 getObjectType 这么关键？如何用证据链解释。

### 30. 注入阶段：field vs constructor（postProcessProperties）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md`
- 内容级加深策略：
  - A：补“注入发生在哪一步”的证据链：构造器注入 vs 属性填充 vs @PostConstruct。
  - B：补反例：field injection 在构造器不可用导致 NPE；循环依赖更难排；测试隔离更差。
  - C：补排障：注入时机误判导致的 bug 如何定位（第一断点与变量）。
  - D：补断点：ConstructorResolver、populateBean、postProcessProperties。
  - E：补面试追问：为什么更推荐构造器注入？给出证据链与工程理由。

### 31. 代理产生阶段：BPP 如何换成 Proxy（self-invocation）

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`
- 内容级加深策略：
  - A：补“proxy 替换发生点”的证据链，并对比 pre/early/after-init 三类替换。
  - B：补反例：self-invocation 绕过代理、多个代理叠加导致行为偏移。
  - C：补排障：为什么看起来“代理没生效”？如何用调用栈证明绕过代理。
  - D：补断点：postProcessAfterInitialization、getEarlyBeanReference、AOP auto-proxy 入口。
  - E：补面试追问：如何解释 self-invocation 的根因与常见修复策略。

### 32. `@Resource` 注入：name-first

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/32-resource-injection-name-first.md`
- 内容级加深策略：
  - A：补 name-first 的完整决策链（name 指定/默认字段名/fallback type）的证据链。
  - B：补反例：alias/同名覆盖导致注入错对象；与 @Primary/@Qualifier 的误对比。
  - C：补排障：@Resource 注入错对象时的 SOP（先查 beanName/alias，再查 type）。
  - D：补断点：CommonAnnotationBeanPostProcessor 与依赖解析入口对照。
  - E：补面试追问：@Resource vs @Autowired 的选择策略，如何用证据链说明差异。

### 33. 候选选择 vs 顺序：@Primary/@Priority/@Order/@Qualifier

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md`
- 内容级加深策略：
  - A：补“选择 vs 排序”的证据链：单注入 vs 集合注入两条路径的决策点。
  - B：补反例：@Order 不能解决单注入歧义；by-name fallback 的边界。
  - C：补排障：NoUnique 发生时按哪条路径收敛（优先检查 @Primary/@Qualifier，再检查 @Priority，再检查 by-name）。
  - D：补断点：candidate 决策点、orderedStream/collection injection 排序点。
  - E：补面试追问：@Primary 与 @Priority 谁更强？给出可证明解释。

### 34. `@Value("${...}")` 占位符解析：strict vs non-strict

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md`
- 内容级加深策略：
  - A：补“resolveEmbeddedValue → placeholder resolver”的最短证据链，并区分 ${} 与 #{}。
  - B：补反例：把占位符解析/SpEL 求值/类型转换混为一谈导致误诊。
  - C：补排障 SOP：三连分层（占位符→SpEL→转换），每层第一断点入口与判断标准。
  - D：补 watch list：embeddedValueResolver、propertySources、missing key 处理策略。
  - E：补面试追问：strict 策略是谁决定的？为什么不建议默认 non-strict？

### 35. MergedBeanDefinition：RootBeanDefinition 从哪里来？

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/35-merged-bean-definition.md`
- 内容级加深策略：
  - A：补“合并触发点与缓存语义”的证据链（merged 什么时候生成/什么时候复用）。
  - B：补反例：读者观察到的 BeanDefinition 与最终行为不一致（原因往往在 merged）。
  - C：补排障：注解元信息处理异常/属性不生效时如何先确认 merged BD。
  - D：补断点：getMergedLocalBeanDefinition、applyMergedBeanDefinitionPostProcessors。
  - E：补面试追问：为什么 MBPP（MergedBeanDefinitionPostProcessor）重要？如何证明它的窗口期。

### 36. 类型转换：BeanWrapper / ConversionService / PropertyEditor

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md`
- 内容级加深策略：
  - A：补“属性访问 vs 类型转换”的证据链：populateBean → BeanWrapper → TypeConverterDelegate。
  - B：补反例：占位符没解析导致转换失败、集合/枚举/日期转换链路误判。
  - C：补排障：TypeMismatch/ConversionFailed 的 SOP（先定位 propertyPath，再定位 requiredType 与分支）。
  - D：补断点：setPropertyValues、convertIfNecessary、converter/editor 命中路径。
  - E：补面试追问：PropertyEditor 为什么还存在？与 ConversionService 的边界与迁移建议。

### 37. 泛型匹配陷阱：ResolvableType 与代理导致类型信息丢失

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md`
- 内容级加深策略：
  - A：补“checkGenericTypeMatch 的决策链”与关键变量（ResolvableType 推断结果）。
  - B：补反例：代理/桥接方法/父类擦除导致的泛型信息丢失与匹配失败。
  - C：补排障：为什么 List<Foo> 注入失败？如何在依赖解析入口处证明是泛型不匹配。
  - D：补断点：generic match、candidate resolver、type descriptor 对照。
  - E：补面试追问：泛型匹配在 Spring 里如何实现？为什么代理会影响它？

### 38. Environment/PropertySource：优先级与排障主线

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/38-environment-and-propertysource.md`
- 内容级加深策略：
  - A：补“最终取值→来源”的证据链：PropertySources 顺序如何影响 getProperty。
  - B：补反例：值被覆盖但不自知、profile/条件导致 property source 不同。
  - C：补排障 SOP：从“值不对/找不到/被覆盖”三类症状回推 propertySources 与 resolver。
  - D：补 watch list：MutablePropertySources 顺序、property resolver 命中路径。
  - E：补面试追问：Environment abstraction 与 Boot config data 的关系如何解释。

### 39. BeanFactory API 深入分析：接口族谱与手动 bootstrap 边界

- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/39-beanfactory-api-deep-dive.md`
- 内容级加深策略：
  - A：补“接口能力→可观察行为”的证据链：Listable/Configurable 等接口意味着哪些行为窗口。
  - B：补反例：手工 new BeanFactory 时注解不工作/占位符不解析/代理不出现的误判。
  - C：补排障：当怀疑容器能力缺失时如何快速确认（processor 是否安装、哪条主线缺环）。
  - D：补断点：手工 bootstrap 的装配点、processor 注册点、注入解析入口。
  - E：补面试追问：BeanFactory vs ApplicationContext 的差异如何落到“能力清单 + 证据链”。
