# Task List: spring-core-beans Part 01（IoC Container）逐章内容深度完善（执行清单）

Directory: `helloagents/plan/202601301812_spring_core_beans_part01_ioc_container_deepening_solution/`

---

## 0. 执行前：逐章审阅与问题清单（文档为主，可新增 Labs/Test 用于证据链闭环）

- [√] 0.1 逐章“全文通读 + 标注问题点”：对 Part 01 每个章节列出需要扩写/纠错/重排的小节（重点抓：概念混淆、因果链断裂、术语前后不一致、跨章链接指向错误、调用链/类名/方法名不准确）
- [√] 0.2 逐章“断点/源码锚点核对”：核对章节中出现的关键类/方法名是否与本仓库 LabTest 的入口一致（若发现表述不准确 → 以可跳转/可验证为原则修正文档措辞）
- [√] 0.3 逐章“章节内目录 ↔ 内容对齐”：检查是否存在“目录有标题但内容缺失/内容偏离标题”的段落，必要时拆分/合并/重命名小节以提升可读性
- [√] 0.4 逐章“跨章节桥接补齐”：为每章补齐 3–5 个“下一步去哪读/遇到什么现象跳哪一章”的可点击链接（以 Part 01 内部互链为主；先在审计报告里给出清单，执行阶段落地）
- [√] 0.5 现有测试资产盘点：把 `part01_ioc_container` 现有 LabTest 与 9 个章节建立映射表（每章至少 1 个“可复现入口”），标出缺口
- [√] 0.6 缺口补齐策略确定：对“缺口章节”决定是（A）新增 LabTest，（B）扩展既有 LabTest，（C）仅补互链到 Part 03/04 的对应 LabTest（并明确原因）
- [√] 0.7 Suite 策略：确定新增 LabTest 是否纳入 `SpringCoreBeansBookMatrixLabTest` / `SpringCoreBeansIocBranchMatrixLabTest`（避免矩阵无限膨胀，只收敛入口）
- [√] 0.8 文档结构“冗余/重复段落”扫描：重点检查 `06-configuration-enhancement.md` 是否存在迁移痕迹导致的重复小节，整理成“保留/合并/删除”清单（执行阶段再落地）
- [√] 0.9 文档引用一致性扫描：对每章的 “Lab/Test file” 路径逐条核对真实路径（尤其跨 Part 的 Lab），把“错链/漏链/路径可读性差（太长一行）”标注出来
- [√] 0.10 “同一概念跨章冲突”扫描：聚焦 4 个高风险概念（BeanDefinition vs bean instance、by-name fallback、scoped proxy、early reference/raw vs proxy），若各章结论不一致则记录冲突点与需修正章节
- [√] 0.11 去重/复用决策表：把本方案中“拟新增 LabTest”逐条对照现有 `part03_container_internals` / `part04_wiring_and_boundaries` / `appendix` 是否已有覆盖；若已有则优先“补文档互链 + 追加断言点”，仅在“缺少最小证据链入口”时才新增 Part01 专属 LabTest
- [√] 0.12 章节“证据链模板”统一：为 Part 01 的每章补一个固定模板（入口断点 → watch list → 结论句式），并在执行阶段确保各章使用同一套词汇（Definition/Instance/Exposed/early/proxy）
- [√] 0.13 审计报告归档：输出 `helloagents/plan/202601301812_spring_core_beans_part01_ioc_container_deepening_solution/audit_part01_0x.md`（作为后续 1.* 落地的 SSOT）

## 1. Part 01：逐章内容完善（Docs）

> 说明：以下任务按章节分组。每章优先完成「纠错 + 扩写 + 证据链闭环」，再做「互链 + 排障闭环 + 结构一致性」。

### 1.1 01-bean-registration.md（注册入口：扫描 / @Bean / @Import / registrar / programmatic）

- [√] 1.1 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/01-bean-registration.md`：扩写与纠错（注册入口全景）
- [√] 1.1.1 补强 “BeanDefinition 是什么” 为字段级可观察对象：增加核心字段速查（`beanClassName/factoryBeanName/factoryMethodName/scope/role/lazyInit/dependsOn/autowireCandidate/primary/qualifiers`）并说明“字段 → 后续行为”的因果链
- [√] 1.1.1.1 基于当前正文结构，在 “BeanDefinition 是什么” 小节补一张“不同入口的 BeanDefinition 类型对照表”（scan/@Bean/@Import/registrar/programmatic），并在执行阶段核对实际 `beanDefinition.getClass()`（避免写错类型名）
- [√] 1.1.1.2 增补 “字段→行为”最短映射：为 `scope/lazyInit/dependsOn/autowireCandidate/primary/qualifiers` 各给 1 条“后续会在哪个阶段被读取”的方法级锚点（跨章互链到 14/15/16/17/08/09/20）
- [√] 1.1.2 扩充 “入口对照表/证据链模板”：加入 beanName 生成、alias、命名冲突与 `&` 前缀/`scopedTarget.*` 的命名交叉提示（并补齐跨章节链接）
- [√] 1.1.2.1 增补 “beanName 生成”可解释路径：补齐 `BeanNameGenerator` 的入口提示（扫描/注解注册），并给出读者可断点验证的最短链路（避免停留在“规则背诵”）
- [√] 1.1.2.2 增补 “alias 的创建入口”提示：说明 `@Bean(name={...})` 的“首个 name 为主名、其余为 alias”的规则，并加入到入口对照表（为 14 章 by-name/`@Resource` 做桥接）
- [√] 1.1.3 校对并修正 “@Import 分叉” 的链路描述：把 `ConfigurationClassPostProcessor` 放回 refresh 时间线，明确 selector/registrar 的产物与落点（必要时纠正术语/调用链）
- [√] 1.1.3.1 纠错点核验：逐条核对本章出现的 `processImports/loadBeanDefinitionsFromRegistrars/loadBeanDefinitionsForBeanMethod` 等方法名是否与当前 Spring 版本一致（以“读者能一键跳转”为准）
- [√] 1.1.4 扩写 “programmatic：定义层 vs 实例层” 的工程后果：补齐 `registerSingleton` 导致错过 BPP/生命周期/代理的证据链与排障建议
- [√] 1.1.4.1 增补“补救策略”边界：补一段“如果不得不 registerSingleton，如何显式调用 `autowireBean/initializeBean` 补注入/回调（以及为什么仍可能错过代理/排序）”，并把风险写清楚
- [√] 1.1.5 扩写 “属性绑定入口” 的桥接：把 `populateBean/BeanWrapper` 与值解析/类型转换章节建立可点击路径，并纠正可能的“注册=注入/实例化”的混淆表述
- [√] 1.1.6 纠错与扩写“命名/alias 的真实影响”：补齐 alias 如何影响 `getBean`、注入 by-name fallback、`@Resource(name)`，并增加 1–2 个“误判→证据链→修复”示例
- [√] 1.1.6.1 新增“alias 与 by-name fallback 的边界”小节：明确 by-name 只在特定条件下参与收敛，并把“alias 也算名字匹配”的证据链写清楚（链接到 14 章决策树）
- [√] 1.1.6.2 新增“命名冲突/覆盖”小节补强：结合 `allowBeanDefinitionOverriding` 与 Boot 默认策略（若本项目 docs 处于 Boot 语境）补齐“为什么本地能跑、Boot 工程里直接报错”的解释与互链
- [√] 1.1.7 统一“注册/实例化/注入/初始化”四个词的边界：对全章逐段校对，把容易误导读者的表述改成分层表达（定义层 vs 实例层）
- [√] 1.1.8 校对“断点闭环/排障决策表”：补齐“看见注册结果”的最短路径（查定义/查来源/查 role），并纠正可能过度依赖日志、缺少变量观察点的问题
- [√] 1.1.9 新增/扩展 testsupport：增强 `BeanDefinitionOriginDumper` 输出维度（补齐 `dependsOn/lazyInit/qualifiers` 等），用于支撑文档“字段→行为”的可观察证据
- [√] 1.1.9.1 扩展 `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/testsupport/BeanDefinitionOriginDumper.java`：输出 `lazyInit/dependsOn/abstract/description/initMethod/destroyMethod`（不触发实例化）
- [√] 1.1.9.2 扩展 `.../BeanDefinitionOriginDumper.java`：输出 qualifiers（含 qualifier typeName 与 attribute key-values；输出顺序稳定，便于断言）
- [√] 1.1.9.3 新增 testsupport 单测：`.../testsupport/BeanDefinitionOriginDumperLabTest`（用最小 context 注册一个带 dependsOn/qualifier/lazy 的 BeanDefinition，断言 dumper 输出包含关键字段）
- [√] 1.1.10 新增 LabTest：`part01_ioc_container/SpringCoreBeansBeanDefinitionRegistrationDiffLabTest`（对比 scan/@Bean/@Import/registrar/registerBeanDefinition/registerSingleton 的 BeanDefinition 关键字段差异），并在本章引用为“字段级证据链入口”
- [√] 1.1.10.1 新增测试类文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanDefinitionRegistrationDiffLabTest.java`（最小容器 + 最小配置，先跑通）
- [√] 1.1.10.2 在 `...RegistrationDiffLabTest` 中实现 5 组对照：scan/@Bean/@Import(registrar)/programmatic-registerBeanDefinition/registerSingleton，并用 `BeanDefinitionOriginDumper` 产出证据链文本
- [√] 1.1.10.3 在 `...RegistrationDiffLabTest` 中把“字段差异”固化成断言（至少断言 `beanDefinitionType/source/factoryMethodName/scope/role` 这类稳定字段）
- [√] 1.1.10.4 文档补引用：在 `01-bean-registration.md` 的“BeanDefinition 是什么/入口对照表/断点闭环”小节插入该 LabTest 的定位入口（避免只列类名不说明看什么）
- [√] 1.1.11 命名/alias 证据链去重（优先复用+增量扩展）：优先复用既有 alias/@Resource 相关 Lab，并在本章“命名/alias”小节引用
- [√] 1.1.11.1 复用既有 alias 入口：引用 `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanNameAliasLabTest.java`（alias=同一实例的第二个名字）
- [√] 1.1.11.2 复用既有 @Resource 入口：引用 `.../part04_wiring_and_boundaries/SpringCoreBeansResourceInjectionLabTest.java`（name-first + 依赖 annotation processors）
- [√] 1.1.11.3 补齐“alias 参与 by-name fallback”的缺口（可选）：更推荐在 `.../SpringCoreBeansAutowireCandidateSelectionLabTest.java` 增加 1 个 alias 交叉用例，而不是新增多份重复 Lab
- [-] 1.1.11.4 若 Part01 需要“就近入口”（可选）：新增 `part01_ioc_container/SpringCoreBeansBeanNameAliasBoundaryLabTest`（@Suite 仅聚合真实用例）；本轮通过“复用 Part04 用例 + 本章互链”已满足就近可跑入口，暂不新增 wrapper suite
- [√] 1.1.11.5 文档补引用：在 `01-bean-registration.md` 增加“alias 与注入解析桥接”的可点击互链到 14 章，并引用上述复用入口（写清推荐跑的测试方法名/观察点）
- [√] 1.1.12 文档引用纠错：核对本章引用的 `SpringCoreBeansProgrammaticRegistrationLabTest` 包路径（当前位于 Part 04），若读者易混淆则补一句“为何放在 Part 04 但服务于 Part 01 的定义层/实例层对照”，必要时改链
- [√] 1.1.13 Suite 更新（可选）：若新增 LabTest 属于“入口级”，则更新 `part01_ioc_container/SpringCoreBeansBookMatrixLabTest` 与/或 `SpringCoreBeansIocBranchMatrixLabTest` 纳入（只挑 1–2 个）

### 1.2 02-dependency-injection-resolution.md（注入解析：候选收集 → 收敛 → 最终注入）

- [√] 1.2 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-dependency-injection-resolution.md`：扩写与纠错（注入解析主线）
- [√] 1.2.1 扩写 `DependencyDescriptor` 深挖：补齐字段注入 vs 参数注入（`MethodParameter`）的证据链（泛型保真度、注解集合、参数名可见性对 by-name 的影响）
- [√] 1.2.2 扩充 “候选收集” 的关键角色：写清 `AutowireCandidateResolver` 在 `@Lazy/@Qualifier`（含元注解）与 FactoryBean product 类型上的作用点与观察变量
- [√] 1.2.3 强化 “候选收敛决策树” 的可复述性：校对并补齐 `@Qualifier/@Primary/@Priority/by-name fallback/@Resource(name-first)` 的优先级与边界（明确哪些只影响集合注入）
- [√] 1.2.3.1 纠错“优先级顺序”口径：统一全文中关于 `@Primary/by-name/suggestedName/@Priority` 的先后顺序描述，并明确 Qualifier 属于“候选过滤”而非 `determineAutowireCandidate` 内部排序（避免互相矛盾）
- [√] 1.2.3.2 增补“by-name fallback 触发条件”小节：明确它并非总是发生（例如需要候选>1 且无明确限定信号），并把 alias 参与匹配的边界写清楚（互链到 02 章 alias 小节）
- [√] 1.2.4 增加 “反例集 + 修复策略”：列出本章最易误判的 3–5 个 case（如 `@Order` 不能解单注入歧义、泛型签名缺失导致匹配失败、FactoryBean `getObjectType` 错误污染候选、参数名不可见导致 by-name 不工作等）
- [√] 1.2.5 补齐 “异常 → 断点入口” 的速查闭环：从 NoSuch/NoUnique 直接跳 `doResolveDependency`，固化 watch list（`descriptor/candidates/autowiredBeanNames` 等）
- [√] 1.2.6 纠错 “@Priority vs @Order” 容易被误述的点：明确它们分别影响“单依赖选择/集合顺序”的边界，并补齐“为什么很多人以为 @Order 能解决歧义”的反例解释
- [√] 1.2.7 扩写 “@Qualifier（含元注解）” 的可验证路径：补齐“限定信号如何进入 resolver / descriptor”的证据链入口，并给出一个“自定义 qualifier → 缩窄候选”的最短验证路线（仅文档说明）
- [√] 1.2.8 补齐“@Value/@Lazy 注入点”的交叉提示：解释它们是在依赖解析阶段生效还是在实例化后生效，并与相关章节建立互链（避免读者把它们当成“注册阶段配置”）
- [√] 1.2.9 新增 testsupport：`DependencyDescriptorDumper`（输出 `dependencyType/resolvableType/dependencyName/required/field-vs-methodParameter/annotations`），用于把“注入点到底要什么”变成可观察对象
- [√] 1.2.9.1 新增 `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/testsupport/DependencyDescriptorDumper.java`：提供 `dump(DependencyDescriptor)` 与 `dump(InjectionPoint)` 两类入口（输出稳定、便于断言）
- [√] 1.2.9.2 为 `DependencyDescriptorDumper` 输出补齐“Field vs MethodParameter”差异字段：`dependencyType/resolvableType/dependencyName/annotations/@Qualifier value/required`（输出顺序固定）
- [√] 1.2.9.3 新增 testsupport 单测：`.../testsupport/DependencyDescriptorDumperLabTest`（构造 Field/MethodParameter 两类 descriptor，对照输出差异）
- [√] 1.2.10 新增 LabTest：`part01_ioc_container/SpringCoreBeansDependencyDescriptorMetadataLabTest`（对比 Field 注入点与 `MethodParameter` 注入点的 descriptor 差异，并演示它如何影响候选收敛/调试），并在本章引用
- [√] 1.2.10.1 新增测试类文件：`.../part01_ioc_container/SpringCoreBeansDependencyDescriptorMetadataLabTest.java`（最小容器 + 两类注入点样例）
- [√] 1.2.10.2 在 `...MetadataLabTest` 中对照 2 类注入点：字段注入 vs 构造器参数注入（重点断言 `dependencyName`、`resolvableType`、注解集合）
- [√] 1.2.10.3 在 `...MetadataLabTest` 中增加 1 个“参数名不可见/不可靠”的提示性用例：若当前工程编译参数可获得参数名，则在文档里写明“工程开启/关闭 -parameters 的影响”并给出排障抓手（不强行依赖某一编译配置）
- [√] 1.2.10.4 文档补引用：在 `02-dependency-injection-resolution.md` 的 “DependencyDescriptor 深挖/注入点元数据”处引用本 LabTest（并写清楚建议读者看哪几条断言/输出）
- [√] 1.2.11 by-name fallback 证据链去重（优先复用+增量扩展）：优先复用既有 by-name fallback Lab，并补齐 alias 交叉用例（如确有必要）
- [√] 1.2.11.1 复用既有 by-name fallback 入口：引用 `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansAutowireCandidateSelectionLabTest.java` 中的 by-name fallback 用例（含 Primary 优先级对照）
- [√] 1.2.11.2 增量扩展（可选）：在 `.../SpringCoreBeansAutowireCandidateSelectionLabTest.java` 增加 1 个 alias 参与 by-name fallback 的对照用例（字段名=alias 时的边界），并在本章引用
- [-] 1.2.11.3 若 Part01 需要“就近入口”（可选）：新增 `part01_ioc_container/SpringCoreBeansAutowireCandidateSelectionBoundaryLabTest`（@Suite 仅聚合上述真实用例）；本轮通过“复用 Part04 用例 + 本章互链”已满足就近入口，暂不新增 wrapper suite
- [√] 1.2.11.4 文档补引用：在 `02-dependency-injection-resolution.md` 的 “决策树/反例集/调试闭环”处引用复用入口（明确“看哪个变量能解释结果”）
- [√] 1.2.12 新增/扩展 LabTest：补齐一个“泛型注入成功 vs 失败”的最小反例（可复用 `GenericTypeMatchingPitfallsLabTest` 的思路，但放在 Part 01 语境），并在本章引用为“泛型不是稳定缩窄信号”的证据
- [√] 1.2.12.1 去重决策：对照 `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`，判断是否只需在 14 章补互链与“最小阅读路径”，还是需要新增 Part01 的“更小一条反例”入口
- [-] 1.2.12.2 若需新增：新增 `.../part01_ioc_container/SpringCoreBeansGenericInjectionResolutionBoundaryLabTest.java`（仅做 1 条“看起来应当匹配但失败”的反例 + 1 条修复方案）；本轮已通过互链与 appendix 反例满足“最小证据链”，暂不新增重复入口
- [-] 1.2.13 Suite 更新（可选）：将 `SpringCoreBeansDependencyDescriptorMetadataLabTest` 纳入 `SpringCoreBeansBookMatrixLabTest`（如果它成为推荐入口）；本轮先维持 BookMatrix 小而稳定，暂不纳入

### 1.3 03-scope-and-prototype.md（Scope/Prototype：注入陷阱与代理语义）

- [√] 1.3 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/03-scope-and-prototype.md`：扩写与纠错（Scope/Prototype 边界）
- [√] 1.3.1 扩写 “prototype 注入 singleton 像单例” 的证据链：把现象落到注入时机与缓存语义（而非只给结论），补齐最短调用链与关键变量
- [√] 1.3.2 扩充 “三种方案对照”：在现有对照上补齐适用场景/副作用/排障入口（`ObjectProvider/@Lookup/scoped proxy`），并纠正常见误解（“把 prototype 变单例”等）
- [√] 1.3.3 深化 scoped proxy：补齐 `@Scope(proxyMode=ScopedProxyMode...)`、`beanName vs scopedTarget.*`、`INTERFACES vs TARGET_CLASS` 的差异与 Debug 证据链（判定 proxy/target）
- [√] 1.3.4 补齐 “自定义 scope” 最小实现要点：注册位置、存储/回收策略、线程/请求边界、destroy 回调触发点（以“能写出最小实现”为目标）
- [√] 1.3.5 章节一致性校对：与 `11-custom-scope-and-scoped-proxy.md`、`01-lazy-semantics.md` 的术语/结论对齐，发现冲突即纠正
- [√] 1.3.6 扩写 “scope 与循环依赖/early reference” 的交叉边界：解释为什么 prototype 的环路通常救不了，以及 scoped proxy/延迟获取是否改变这一事实
- [√] 1.3.7 校对本章“销毁语义”的表述：把“容器默认不托管”的边界改写为可操作建议（什么时候手动 destroy、什么时候应当怀疑 scope 实现的问题）
- [√] 1.3.8 scoped proxy 语义去重（优先复用+增量扩展）：优先扩展既有自定义 scope Lab，补齐 `scopedTarget.*` 与 INTERFACES/TARGET_CLASS 的可断言证据链
- [√] 1.3.8.1 扩展既有真实用例：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java` 增加断言：scoped proxy 场景下存在 `scopedTarget.<beanName>` 的 BeanDefinition
- [√] 1.3.8.2 在 `...CustomScopeLabTest.java` 增加 `ScopedProxyMode.INTERFACES` vs `TARGET_CLASS` 对照：接口注入 vs 具体类注入的类型边界（必要时断言按具体类查找失败/返回形态差异）
- [√] 1.3.8.3 文档补引用：在 `03-scope-and-prototype.md` 的 scoped proxy 小节引用上述扩展用例，并补一段“scopedTarget.* 命名不是约定，是容器真实注册的第二个 BeanDefinition”的解释
- [-] 1.3.8.4 若 Part01 需要“就近入口”（可选）：新增 `part01_ioc_container/SpringCoreBeansCustomScopeBoundaryLabTest`（@Suite 仅聚合 `SpringCoreBeansCustomScopeLabTest`）；本轮通过“复用 Part04 用例 + 本章互链”已满足就近入口，暂不新增 wrapper suite
- [√] 1.3.9 自定义 Scope 最小实现（保留为“缺口补齐”）：若扩展 `SimpleThreadScope` 无法覆盖 destruction callback 触发语义，则新增一个最小 `Scope` 实现实验
- [√] 1.3.9.1 新增用例落点建议：更倾向放在 `part04_wiring_and_boundaries`（与 CustomScopeLabTest 同一 Part），避免 Part01 出现重复；若读者入口需要，可再用 Part01 @Suite wrapper 暴露
- [√] 1.3.9.2 断言点要求：同一 scope key 复用、scope 切换产生新实例、scope end/remove 时 destruction callbacks 被触发（避免只靠 println）
- [√] 1.3.9.3 文档补引用：在 `03-scope-and-prototype.md` 的“自定义 scope”小节引用该最小用例，并补一段“destroy 回调是谁触发/何时触发”的证据链提示
- [√] 1.3.10 扩展既有 `SpringCoreBeansContainerLabTest`：若已覆盖 prototype 场景，则把关键观察点升级为断言；若未覆盖则新增一个最小 prototype 反直觉 case 并在文档引用

### 1.4 04-lifecycle-and-callbacks.md（生命周期：回调触发窗口、raw vs proxy）

- [√] 1.4 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/04-lifecycle-and-callbacks.md`：扩写与纠错（生命周期与代理交织）
- [√] 1.4.1 扩写 “生命周期骨架” 到方法级：把 init/destroy 链落到 `initializeBean`/`DisposableBeanAdapter`，并标注每类回调的触发者（BPP/aware/适配器）
- [√] 1.4.2 深化 “@PostConstruct/@PreDestroy 触发者是 BPP” 的证据链：补齐典型类与发生窗口，写清 raw vs proxy 的解释路径（代理替换发生点差异）
- [√] 1.4.3 增补 “容器级生命周期选型” 小节：`SmartInitializingSingleton` / `SmartLifecycle` / 事件监听（`ApplicationListener`）的适用边界与常见误用纠正
- [√] 1.4.4 扩写 “scope 交叉” 与关闭顺序：prototype 与自定义 scope 的销毁边界、`dependsOn`/phase 对初始化与关闭顺序的影响（必要时纠正文中易混点）
- [√] 1.4.5 校对“回调顺序”的易错点：逐段核对 `Aware → BPP(before) → init → BPP(after)` 与 destroy 链的描述，修正可能把“事件/SmartLifecycle/SmartInitializingSingleton”混在同一层的表述
- [√] 1.4.6 扩写“如何证明回调发生在哪个对象上”：补齐 1 条“观察 raw/proxy/target 的最短证据链”，避免只给抽象结论
- [√] 1.4.7 新增 LabTest：`part01_ioc_container/SpringCoreBeansLifecycleRawVsProxyLabTest`（用自定义 BPP 在 after-init 包装成 JDK proxy，证明 `@PostConstruct` 发生在 raw 上、最终注入的是 proxy），并在本章引用
- [√] 1.4.7.1 新增测试类文件：`.../part01_ioc_container/SpringCoreBeansLifecycleRawVsProxyLabTest.java`（最小 context + 1 个带 `@PostConstruct` 的 bean）
- [√] 1.4.7.2 在 `...LifecycleRawVsProxyLabTest` 断言 2 个关键结论：`@PostConstruct` 在 raw 上发生（记录 raw identity）；after-init 之后 `getBean()` 返回 proxy（记录 exposed identity），两者不相同
- [√] 1.4.7.3 文档补引用：在 `04-lifecycle-and-callbacks.md` 的 raw vs exposed 小节引用该 LabTest，并补一个“为什么 @PostConstruct 里事务/异步经常不生效”的可解释路径
- [√] 1.4.8 扩展/对齐既有 `part03_container_internals/SpringCoreBeansLifecycleCallbackOrderLabTest`：把本章“回调顺序”表述与该 LabTest 断言点对齐，发现冲突即纠正文档
- [√] 1.4.9 文档对齐（优先复用）：在 `04-lifecycle-and-callbacks.md` 引用并对齐 `part03_container_internals/SpringCoreBeansPrototypeDestroySemanticsLabTest`（prototype 默认不走 destroy + 手动 destroyBean），仅在“缺少本章语境下的最小入口”时才新增 Part01 wrapper/补充用例

### 1.5 05-post-processors.md（处理器地图：BFPP / BDRPP / BPP）

- [√] 1.5 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/05-post-processors.md`：扩写与纠错（扩展点能力边界）
- [√] 1.5.1 扩写 “介入点地图”：为四类 BPP（实例化前短路/early reference/merged definition/销毁前）补齐关键方法、关键变量、典型误用与跨章节链接
- [√] 1.5.1.1 为 “实例化前短路” 补齐方法级锚点：`InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation` / `#postProcessAfterInstantiation`（写清“返回非 null 会短路什么”）
- [√] 1.5.1.2 为 “属性注入介入点” 补齐方法级锚点：`InstantiationAwareBeanPostProcessor#postProcessProperties`（与 14/30 章互链，避免把它误当成 init 回调）
- [√] 1.5.1.3 为 “early reference” 补齐方法级锚点：`SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`（与 09/16 章互链）
- [√] 1.5.1.4 为 “merged definition” 补齐方法级锚点：`MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition`（说明它是定义层与实例层的桥）
- [√] 1.5.1.5 为 “销毁前” 补齐方法级锚点：`DestructionAwareBeanPostProcessor#postProcessBeforeDestruction`（与 16 章销毁链路互链）
- [√] 1.5.2 校对并补强顺序算法表述：把 `PostProcessorRegistrationDelegate` 的“两段式/循环发现/分组注册”讲成可复述算法，纠正容易误解的地方
- [√] 1.5.3 增加 “错过 BPP” 的排障闭环：补齐 2–3 个典型场景（过早 `getBean`、实例层注册、Bootstrap/最小容器差异）与证据链
- [√] 1.5.3.1 补齐 “错过 BPP” 的识别信号：`BeanPostProcessorChecker` 的典型提示文本，说明它意味着什么、不意味着什么（与本章排障表互链）
- [√] 1.5.4 常见处理器清单校对：核对类名与职责（避免把 Boot 侧机制与 Spring Core 机制混淆），发现不准确描述即修正
- [√] 1.5.5 扩写“如何把扩展点写得可维护”：补齐“该改定义还是改实例”的决策口径，并对常见误用给出替代写法建议（仅文档）
- [√] 1.5.6 扩写“与注册入口的桥接”：补齐“BDRPP/BFPP 为什么直接决定 @Configuration/@ComponentScan/@Import 能否生效”的因果链，并链接回注册章节
- [√] 1.5.7 新增 LabTest：`part01_ioc_container/SpringCoreBeansEarlyGetBeanMissesBppLabTest`（在 BFPP 阶段过早 `getBean` → 目标 bean 错过 BPP），并在本章“错过 BPP”段落引用
- [√] 1.5.7.1 新增测试类文件：`.../part01_ioc_container/SpringCoreBeansEarlyGetBeanMissesBppLabTest.java`（BFPP 内部调用 `getBean` 触发目标提前创建）
- [√] 1.5.7.2 在 `...EarlyGetBeanMissesBppLabTest` 构造一个“后置 BPP”会包装目标 bean 的场景，并断言：若目标在 BFPP 阶段创建则不会被该 BPP 包装（证明错过窗口）
- [√] 1.5.7.3 文档补引用：在 `05-post-processors.md` 的“错过 BPP/过早 getBean”处引用该 LabTest，并补一段“为什么错过不会 retroactive 补上”的解释
- [-] 1.5.8 扩展 testsupport（可选）：提供一个最小 “BPP 命中标记器”（例如在 after-init 打标/包装）以便多个 LabTest 复用，避免每个测试重复写样板代码；本轮用单测内置最小 BPP 已满足证据链，暂不抽取 marker
- [-] 1.5.8.1 新增 testsupport：`.../testsupport/BeanPostProcessorMarker.java`（提供“只对指定 beanName 生效”的 wrapper/marker，供多个 LabTest 复用）；暂不新增
- [-] 1.5.8.2 将 `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest` / 新增 `...EarlyGetBeanMissesBppLabTest` 的“标记逻辑”迁移到 marker（降低重复）；暂不迁移

### 1.6 06-configuration-enhancement.md（配置类增强：full vs lite，参数注入不依赖增强）

- [√] 1.6 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/06-configuration-enhancement.md`：扩写与纠错（配置类增强语义）
- [√] 1.6.1 补齐 lite/full 配置类边界：对照 `@Configuration` 与 “non-@Configuration 但含 @Bean” 的语义差异，补充判定抓手与反例（必要时纠正原文表述）
- [√] 1.6.2 深化 “方法参数声明依赖” 的证明路径：把参数注入点（`MethodParameter`/`DependencyDescriptor`）与依赖解析入口串起来，并与注入解析章节互链
- [√] 1.6.3 扩写 “@Bean 方法互调” 的反例与修复策略：分别覆盖 `proxyBeanMethods=true/false`，给出断点闭环与工程建议
- [√] 1.6.4 补齐与循环依赖/代理交叉：配置类增强本身是代理链的一部分，补充“如何证明影响发生在哪”的证据链入口
- [√] 1.6.5 纠错“proxyBeanMethods=false 只是性能优化”的误导风险：补齐它改变语义的典型场景清单（互调/单例语义/拦截链），并用证据链说明“哪里绕开了容器”
- [√] 1.6.6 校对“配置类解析主线”的类名/方法名：保证读者能从文档直接跳到 IDE 断点位置（发现不准确即修正）
- [√] 1.6.7 文档结构整理：对 `06-configuration-enhancement.md` 做“去重/合并”——把“复现入口/最小实验/源码锚点/排障表”整理成一条主线，移除迁移残留导致的重复段落（不改变结论但提升可读性）
- [√] 1.6.8 扩展既有 `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`：新增 1 个用例证明“@Bean 方法参数注入不依赖配置类增强”（proxyBeanMethods=false + 参数注入仍保持单例语义）
- [-] 1.6.9 若需要独立入口（可选）：新增 `.../part01_ioc_container/SpringCoreBeansConfigurationLiteVsFullLabTest.java`，将 `SpringCoreBeansContainerLabTest` 中与本章强相关的用例收敛为独立类（避免 ContainerLabTest 过度膨胀）（已在 ContainerLabTest 扩展覆盖参数注入证据链，暂不拆分独立入口）
- [√] 1.6.10 文档补引用：在 `06-configuration-enhancement.md` 的“推荐写法：参数注入替代互调”处引用上一步新增/扩展的用例，并互链到 14 章（MethodParameter/DependencyDescriptor）
- [-] 1.6.11 Suite 更新（可选）：若 `SpringCoreBeansConfigurationLiteVsFullLabTest` 成为推荐入口，纳入 `SpringCoreBeansBookMatrixLabTest`（未新增独立入口类，保持 BookMatrix 稳定，暂不纳入）

### 1.7 07-factorybean.md（FactoryBean：类型匹配/缓存/注入/代理交叉）

- [√] 1.7 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/07-factorybean.md`：扩写与纠错（FactoryBean 边界）
- [√] 1.7.1 扩写 `&` 前缀的最短证据链：把 `doGetBean` 的 FactoryBean 分支、`beanName` 解析与返回对象形态讲清楚（factory vs product）
- [√] 1.7.2 校对并扩写 “按类型查找” 的交叉：补齐 `getObjectType` 的约束、错误实现如何污染候选集合，以及排障时如何证明
- [√] 1.7.3 深化缓存语义：解释 `isSingleton` 影响的是 product 缓存而非 factory 本身，补齐与 `FactoryBeanRegistrySupport` 的关系与常见误区纠正
- [√] 1.7.4 与代理/循环依赖交叉补强：补齐 “FactoryBean 产物是 proxy/early reference 介入” 的识别与排障入口，并与深挖章节互链
- [√] 1.7.5 扩写“FactoryBean 与注入解析的交叉提示”：补齐“product 参与按类型查找”的最短解释路径，并链接回注入解析章节（避免读者把问题归因到扫描/注册）
- [√] 1.7.6 校对示例与术语：统一使用“FactoryBean 本体 / product（getObject 返回对象）”的命名，修正可能混用“工厂/产品/代理”的表述
- [√] 1.7.7 扩展既有 `part01_ioc_container/SpringCoreBeansFactoryBeanEdgeCasesLabTest`：增加一例“错误 getObjectType 导致按类型注入/查找误判”的可断言反例，并在本章引用
- [√] 1.7.7.1 定位真实测试文件：`part01_ioc_container/SpringCoreBeansFactoryBeanEdgeCasesLabTest` 为 suite wrapper，实际用例在 `part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`，扩展应落在真实文件
- [√] 1.7.7.2 新增反例用例：在 `...FactoryBeanEdgeCasesLabTest.java` 增加 “getObjectType 返回错误类型” 场景，断言：按类型发现/注入出现误判或失败，并给出修复提示（实现正确的 getObjectType）
- [√] 1.7.7.3 文档补引用：在 `07-factorybean.md` 的“类型匹配/排障决策表”引用新增用例，并补齐“为什么错误 getObjectType 会污染候选集合”的解释
- [-] 1.7.8 新增/扩展 testsupport（可选）：为 FactoryBean 场景提供最小 “product 类型探测/缓存语义” 观测辅助（避免靠 println），并在文档中用作证据链抓手；本轮用现有 Lab 断言已覆盖关键边界，暂不新增 testsupport

### 1.8 08-circular-dependencies.md（循环依赖：能救/不能救、救援窗口与工程规避）

- [√] 1.8 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-circular-dependencies.md`：扩写与纠错（循环依赖能救/不能救）
- [√] 1.8.1 校对并扩写三层缓存语义：明确 final/early/factory 各自代表什么，补齐 `allowCircularReferences/allowRawInjectionDespiteWrapping` 的工程后果与风险提示
- [√] 1.8.2 强化 “救援窗口” 的方法级定位：把 early exposure 放回 `doCreateBean` 的步骤，并补齐 `getSingleton` 的关键变量/缓存命中证据链
- [√] 1.8.3 补齐 “异常 → 断点入口” 速查：从 `Requested bean is currently in creation` 等异常文本直接定位到该看的方法与变量
- [√] 1.8.4 扩写 “工程规避策略矩阵”：`ObjectProvider/@Lazy/事件解耦/重构拆分` 的适用边界与副作用（避免“能跑就行”的误导）
- [√] 1.8.5 章节一致性校对：与 early reference / BPP 代理替换相关章节结论对齐，发现冲突即纠正
- [√] 1.8.6 扩写“二级 vs 三级缓存”的桥接：用最小反例说明“2-level 不够”的原因，并把读者从本章导向 Why Index/early reference 章节（仅文档互链）
- [√] 1.8.7 校对“循环依赖能救≠安全”的风险表述：补齐 early vs final 不一致、raw injection 风险的可解释路径，避免读者得到“能跑=正确”的结论
- [√] 1.8.8 扩展既有 `part01_ioc_container/SpringCoreBeansCircularDependencyBoundaryLabTest`：增加“early vs final 可能不一致”的可复现观察点（必要时复用 `allowRawInjectionDespiteWrapping` 的案例），并在本章引用
- [-] 1.8.9 新增 LabTest（可选）：`part01_ioc_container/SpringCoreBeansCircularDependencyBreakStrategiesLabTest`（对比 ObjectProvider/@Lazy/setter 的打断策略与副作用），并在本章“工程策略矩阵”引用（已有 BoundaryLabTest 覆盖打断策略与副作用对比，暂不新增专属 LabTest）
- [√] 1.8.10 新增反例用例（建议）：在 `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansCircularDependencyBoundaryLabTest.java` 增加 `allowRawInjectionDespiteWrapping=true/false` 对照，证明 raw 注入与最终代理不一致的风险边界
- [√] 1.8.11 文档补引用：在 `08-circular-dependencies.md` 补一段“Boot 默认策略 vs 纯 Spring 默认策略”的对照提示（`spring.main.allow-circular-references` 与 `DefaultListableBeanFactory#setAllowCircularReferences`），避免读者把“本地能救”误判为“工程默认能救”

### 1.9 09-bean-mental-model.md（统一心智模型：Definition / Instance / Exposed）

- [√] 1.9 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-bean-mental-model.md`：扩写与纠错（统一心智模型）
- [√] 1.9.1 扩写“四类对象一张表”：补齐 scoped proxy/FactoryBean/early reference 等导致 “Definition/Instance/Exposed 不一致” 的典型映射
- [√] 1.9.2 扩写“最终对象被替换”的入口：把 early reference、BPP after-init、FactoryBean product、scoped proxy 统一放回主线解释
- [√] 1.9.3 深化 “能注入 ≠ 一定是 Bean”：补齐 ResolvableDependency 与外部对象（`AutowireCapableBeanFactory`）的对照证据链，并与相关章节互链
- [√] 1.9.4 Part 01 全局术语一致性校对：统一 Definition/Instance/Exposed/early/proxy/wrapper 等术语与表述风格，发现前后矛盾即修正
- [√] 1.9.5 扩写“BeanFactory vs ApplicationContext”边界：把差异放到“能力叠加”与“基础设施 bean/事件/资源加载”等维度解释，并给出读者排障时的选用建议（仅文档）
- [√] 1.9.6 校对全章“桥接链接”的准确性：确保从心智模型能跳到 Part 01 各章的关键段落（必要时补齐锚点/重命名小节以提升可导航性）
- [√] 1.9.7 复用现有 LabTest（优先）：在 `09-bean-mental-model.md` 引用 `part04_wiring_and_boundaries/SpringCoreBeansResolvableDependencyLabTest` 作为“能注入但不是 Bean”的可跑证据，并结合 `BeanDefinitionOriginDumper` 给出“如何证明没有 BeanDefinition”的抓手
- [-] 1.9.8 若需要 Part01 内入口（可选）：新增 `part01_ioc_container/SpringCoreBeansResolvableDependencyBoundaryLabTest`（仅 @Suite 引用 `SpringCoreBeansResolvableDependencyLabTest`），让 Part01 的读者不必跨目录寻找入口（本章已引用 Part04 可跑入口，Part01 不再新增 wrapper suite）
- [√] 1.9.9 扩展既有 `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`：增加 1–2 个“能力叠加”断言点（事件/资源/环境等），让本章边界不只停留在概念层
- [-] 1.9.10 Suite 更新（可选）：将本章新增 LabTest 纳入 `SpringCoreBeansBookMatrixLabTest`（如果它成为推荐入口）（保持 BookMatrix 小而稳定，暂不纳入）

## 2. 导航与一致性（可选，但强烈建议）

- [√] 2.1 更新 `spring-core-modules/spring-core-beans/docs/README.md`：补齐 Part 01 章节导航与“现象→章节”的定位入口（若需要）
- [√] 2.2 全目录链接校验：确保 Part 01 内部相对链接可用（避免 broken link）
- [√] 2.3 Markdown 细节一致性校对：清理 trailing whitespace、修正不一致的代码引用格式（类名/方法名/文件名）、统一“术语首次出现”的简短解释方式（仅改 Part 01 文档）
- [√] 2.4 引用一致性校对：核对每章 “对应 Lab/Test” 与本仓库测试类/包名一致；若文档引用已过时 → 修正文档引用（不新增测试）

## 3. Security Check

- [√] 3.1 执行安全检查（按 G9）：确认文档中不包含密钥/PII/生产地址等敏感信息

## 4. Testing

- [√] 4.1 运行 `mvn -pl spring-core-modules/spring-core-beans test`：验证所有 Lab/Test 通过，确保文档绑定的实验入口仍可运行
- [√] 4.2 回归自检：抽查 3 条“异常→断点入口”路线是否能在 IDE 中一键定位（不要求新增代码，仅确保文档指引可用）

## 5. 知识库同步与归档（执行阶段必做）

- [√] 5.1 同步知识库：更新 `helloagents/wiki/modules/spring-core-beans.md`（补充 Part 01 深化场景与新增/扩展 LabTest 的索引入口）
- [√] 5.2 更新变更记录：在 `helloagents/CHANGELOG.md` 记录本次 Part 01 文档与 LabTest 资产的新增/扩写
- [√] 5.3 方案包迁移：执行完成后把 `helloagents/plan/202601301812_spring_core_beans_part01_ioc_container_deepening_solution/` 迁移到 `helloagents/history/YYYY-MM/` 并更新 `helloagents/history/index.md`
