# Task List: spring-core-beans 文档与 Labs 深化（证据链 + 边界 Case）

Directory: `helloagents/plan/202601261257_beans_docs_deepen_more/`

---

## 0. 验收标准（Definition of Done）

> 目标：把“能跑的 Lab”变成“能学的教程”。本清单用于把每一章的产出拆到足够可执行，并明确验收口径（深度=粒度 3）。

### 0.1 文档章节（粒度 3）必须包含

- [ ] 0.1.1 章节开头 1~2 个最小问题场景（为什么需要这个机制/解决什么坑）
- [ ] 0.1.2 最小可运行入口（对应 Lab 的类名 + 点跑方式 + 预期现象）
- [ ] 0.1.3 最短源码证据链（至少列出：入口方法 → 关键分支 → 关键副作用/状态变更）
- [ ] 0.1.4 关键分支条件表（“条件是什么/为什么重要/触发后行为变化”）
- [ ] 0.1.5 Watch List（建议在调试器 Watch 面板直接观察的对象/字段/缓存）
- [ ] 0.1.6 边界 Case 与失败形态固定化（至少 2 个；包含典型异常类型 + 报错片段 + 根因）
- [ ] 0.1.7 常见误解澄清（至少 3 条：误解 → 反例 → 证据）
- [ ] 0.1.8 Troubleshooting 分流（“现象 → 可能原因 → 快速验证 → 修复方向”）
- [ ] 0.1.9 章节末尾交叉链接（至少 3 个：相关章节 + 相关 Lab/Playbook）

### 0.2 Lab（可跑闭环）必须包含

- [ ] 0.2.1 Arrange/Act/Assert 结构清晰（每个用例都能读懂）
- [ ] 0.2.2 OBSERVE 输出：必须能看见关键状态（identity/hash、是否代理、容器缓存命中等）
- [ ] 0.2.3 断言稳定：避免依赖打印顺序/反射遍历顺序/随机集合顺序
- [ ] 0.2.4 最小化依赖：优先 AnnotationConfigApplicationContext / GenericApplicationContext，避免引入 Web/Boot 噪音
- [ ] 0.2.5 Debug 指引：给出建议断点方法清单（至少 3 个关键入口）
- [ ] 0.2.6 用例覆盖“成功 + 失败”两条路径（至少各 1 个）

## 1. Labs（边界 Case 可跑闭环）

> 说明：本模块多数主题已存在 Lab，本轮以“加深”为主（补齐边界用例 + 断言 + OBSERVE 输出 + 断点/观测点绑定）。只有当现有 Lab 不足以支撑文档证据链时，才新增测试类。

### 1.1 循环依赖（early reference / 三缓存 / @Lazy 打断环）

- [√] 1.1.1 加深循环依赖边界 Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansCircularDependencyBoundaryLabTest.java`
  - [√] 1.1.1.1 设计最小 Bean 组合（A↔B），分别支持 constructor/setter/field 三种注入形态
  - [√] 1.1.1.2 用例 A：构造器注入环（fail-fast），断言异常类型/核心报错片段/根因可复述
  - [√] 1.1.1.3 用例 B：setter/field 注入环（允许 early reference 时可通过），断言对象引用关系与 early cache 命中证据
  - [√] 1.1.1.4 用例 C：`@Lazy` 注入打断环（注入点 proxy），断言“未触发真实实例化”与“首次调用触发创建”
  - [√] 1.1.1.5 观测输出：打印 A/B 的 identity、实际 class、是否代理、依赖字段是否为同一引用
  - [√] 1.1.1.6 Debug 断点建议：至少覆盖创建入口、early exposure、依赖注入阶段 3 个点
- [√] 1.1.2 加深 early reference Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`
  - [√] 1.1.2.1 用例 A：early reference 暴露发生在何处（触发条件 + 最短调用链），并能在调试器中复现
  - [√] 1.1.2.2 用例 B：early reference 与最终 bean（尤其是代理/包装）的差异，断言“早期引用 != 最终引用”的可观察证据
  - [√] 1.1.2.3 用例 C：加入一个“会导致最终包装变化”的因素（典型：BPP/代理），让差异更明显
  - [√] 1.1.2.4 观测输出：同时打印 earlyRef/finalBean 的 identity、class、equals 行为差异（如有）
- [√] 1.1.3 将循环依赖相关入口挂入 suites：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansBreakpointPackLabTest.java`、`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansTroubleshootingPlaybookLabTest.java`

### 1.2 FactoryBean（`&` / 产品类型推断 / 类型匹配 / 缓存语义）

- [√] 1.2.1 加深 FactoryBean 机制 Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanDeepDiveLabTest.java`
  - [√] 1.2.1.1 用例 A：`&name` 取工厂 vs `name` 取产品（含 `BeanFactory#getBean` / `getType` 对照）
  - [√] 1.2.1.2 用例 B：`getObjectType()` 对“按类型注入/查找”的影响（尤其是泛型/接口）
  - [√] 1.2.1.3 用例 C：`isSingleton()` 与产品缓存语义（同一次容器生命周期内对象 identity 断言）
  - [√] 1.2.1.4 观测输出：打印 `getBean("&x")` / `getBean("x")` 的 class 与 identity，并对照 `getType("x")`
- [√] 1.2.2 加深 FactoryBean 边界 Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`
  - [√] 1.2.2.1 用例 A：`FactoryBean` 本身被注入时的陷阱（按类型/按名）
  - [√] 1.2.2.2 用例 B：类型匹配失败/成功的典型误判（`getType` / `ResolvableType` / 目标类型）
  - [√] 1.2.2.3 用例 C：对照 `ObjectProvider<T>` / `Provider<T>` 等延迟获取方式的差异
  - [√] 1.2.2.4 用例 D：`SmartFactoryBean`（如存在）对 eager type 判断的影响（把“为什么”的差异跑出来）
- [√] 1.2.3 必要时扩充 suites 入口（避免“只在 appendix 才能找到”）：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansBreakpointPackLabTest.java`

### 1.3 泛型/类型匹配（ResolvableType / 泛型擦除 / FactoryBean 交互）

- [√] 1.3.1 加深泛型匹配 Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`
  - [√] 1.3.1.1 用例 A：`List<Foo>` / `Map<String, Foo>` 等集合泛型注入的匹配与失败形态
  - [√] 1.3.1.2 用例 B：`ObjectProvider<Foo>` 与直接注入的差异（“延迟获取”对候选选择的影响）
  - [√] 1.3.1.3 用例 C：泛型匹配与 `FactoryBean` 产品类型推断交互（`getObjectType` 正确与否的后果）
  - [√] 1.3.1.4 用例 D：当候选有多个泛型参数不同的实现时，观察 Spring 的筛选与最终报错（固定异常形态）
- [√] 1.3.2 将泛型匹配纳入排障入口：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansTroubleshootingPlaybookLabTest.java`

### 1.4 `@Lazy` 代理（definition-lazy vs injection-point-lazy / 代理类型差异）

- [√] 1.4.1 加深 `@Lazy` Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansLazyLabTest.java`
  - [√] 1.4.1.1 用例 A：bean definition `lazy-init` 并不会“绕过依赖注入”（依赖注入本身会触发创建）的对照实验
  - [√] 1.4.1.2 用例 B：注入点 `@Lazy` 注入 proxy（首次调用才创建真实对象）的对照实验
  - [√] 1.4.1.3 用例 C：`@Lazy` proxy 的类型/可见性边界（JDK vs CGLIB、按 concrete class 取 bean 的失败形态）
  - [√] 1.4.1.4 观测输出：打印注入点对象的 class、是否为 AOP 代理、首次方法调用前后容器中 target 是否已创建
- [√] 1.4.2 必要时补齐“@Lazy 与代理阶段交互”的辅助 Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`

### 1.5 MergedBeanDefinition（合并发生点 / 合并前后字段对照 / 排障观测）

- [√] 1.5.1 加深 merged BD Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`
  - [√] 1.5.1.1 用例 A：parent/child BD 合并（合并触发时机 + 关键字段变化：scope/lazy/autowire/dependsOn 等）
  - [√] 1.5.1.2 用例 B：合并与 `@Configuration/@Bean` 元信息的关系（“看起来不一致”的来源）
  - [√] 1.5.1.3 用例 C：排障视角：如何在不触发实例化的情况下拿到 merged BD 并定位来源
  - [√] 1.5.1.4 观测输出：合并前/后关键字段对照打印（保证读者不调试也能看懂差异）

### 1.6 占位符严格/非严格（ignoreUnresolvablePlaceholders / 默认值 / 时机）

- [√] 1.6.1 加深占位符解析 Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansValuePlaceholderResolutionLabTest.java`
  - [√] 1.6.1.1 用例 A：严格模式缺失属性直接失败（错误形态固定化）
  - [√] 1.6.1.2 用例 B：非严格模式 + 默认值语法（`${k:default}`）的对照
  - [√] 1.6.1.3 用例 C：解析时机：BeanDefinition 属性值 vs `@Value` 注入点（谁先谁后、为什么）
  - [√] 1.6.1.4 用例 D：`@PropertySource` 与 Environment 属性的覆盖顺序（把“为什么读到的值不同”跑出来）

### 1.7 `@Value` SpEL（表达式求值入口 / 类型转换 / 失败形态）

- [√] 1.7.1 加深 SpEL Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansSpelValueLabTest.java`
  - [√] 1.7.1.1 用例 A：`#{}` 表达式引用 bean / 环境 / 静态方法（`T()`）的最小闭环
  - [√] 1.7.1.2 用例 B：SpEL 返回值的类型转换（Converter/PropertyEditor）发生点与失败形态
  - [√] 1.7.1.3 用例 C：`@Value("${...}")` 与 `@Value("#{...}")` 的先后顺序（占位符 vs SpEL）对照
  - [√] 1.7.1.4 用例 D：失败形态固定化：表达式异常 vs 类型转换异常（区分根因与入口）

### 1.8 作用域/ScopedProxy（prototype 注入陷阱 / scoped proxy 行为 / custom scope 观测）

- [√] 1.8.1 加深 custom scope / scoped proxy Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansCustomScopeLabTest.java`
  - [√] 1.8.1.1 用例 A：prototype 注入到 singleton 的“看似 prototype 实则 singleton”陷阱（identity 断言）
  - [√] 1.8.1.2 用例 B：scoped proxy 如何把每次调用路由到当前 scope target（对照无 proxy 的行为）
  - [√] 1.8.1.3 用例 C：自定义 scope 的排障观测点（目标对象存取位置、销毁回调触发）
  - [√] 1.8.1.4 用例 D：scope 销毁回调（destruction callback）触发时机演示（让读者看见“什么时候会清理”）

### 1.9 SmartLifecycle phase（启动/停止顺序 / phase 分组 / 超时与排障）

- [√] 1.9.1 加深 SmartLifecycle Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`
  - [√] 1.9.1.1 用例 A：不同 phase 的启动/停止顺序（含同 phase 内的顺序）
  - [√] 1.9.1.2 用例 B：`isAutoStartup` 与 `stop(Runnable callback)` 的行为边界（关闭时 callback 触发）
  - [√] 1.9.1.3 用例 C：排障：如何定位“卡在 shutdown”的生命周期组件（最短调用链 + 观测点）
  - [√] 1.9.1.4 用例 D：phase 相同但依赖关系存在时（dependsOn/注入触发）对启动顺序的影响

### 1.10 父子容器（可见性 / 同名屏蔽 / containsLocalBean）

- [√] 1.10.1 加深父子容器 Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansContextHierarchyLabTest.java`
  - [√] 1.10.1.1 用例 A：child 查找 parent（可见性规则）vs parent 查找 child（不可见）的对照
  - [√] 1.10.1.2 用例 B：同名 bean 在 parent/child 的屏蔽与排障（`containsBean` vs `containsLocalBean`）
  - [√] 1.10.1.3 用例 C：层级中的“按类型候选”选择边界（同类型多候选时的失败/选择路径）
  - [√] 1.10.1.4 用例 D：child 覆盖/屏蔽 parent 的同类型候选时，按类型注入选择结果对照

### 1.11 BeanDefinition 覆盖（允许/禁止覆盖 / 覆盖来源定位 / 排障最短路径）

- [√] 1.11.1 加深覆盖 Lab：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`
  - [√] 1.11.1.1 用例 A：允许覆盖 vs 禁止覆盖的行为差异（异常类型 + 报错信息）
  - [√] 1.11.1.2 用例 B：覆盖来源定位：结合 `BeanDefinitionOriginDumper` 输出做“证据链”演示
  - [√] 1.11.1.3 用例 C：与父子容器的组合边界（覆盖 vs 屏蔽：两种不同机制）
  - [√] 1.11.1.4 用例 D：覆盖导致注入候选变化的可观察证据（按类型注入前后对照）

## 2. Docs（证据链级解释 + Lab 映射）

> 说明：文档深度目标为“粒度 3”：每章必须补齐 **最短调用链 + 关键分支条件 + watch list + 最小伪代码/源码片段对照 + 对应 Lab 入口 + 排障分流**。

- [√] 2.1 循环依赖证据链加深：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/16-early-reference-and-circular.md`
  - [√] 2.1.1 改写“问题引入”：为什么构造器环必死、setter/field 可能活（先给结论再给证据）
  - [√] 2.1.2 最短调用链：`getBean` → 创建 → 暴露 early factory → 注入依赖 → 回填（用 10~20 行伪代码把流程跑通）
  - [√] 2.1.3 关键分支条件表：`allowCircularReferences`、`isSingletonCurrentlyInCreation`、early exposure 的触发窗口
  - [√] 2.1.4 三缓存 watch list：`singletonObjects/earlySingletonObjects/singletonFactories` 的读写时机（每一步写清“谁写/谁读/为什么”）
  - [√] 2.1.5 “早期引用 != 最终引用”：解释何时会不一致（代理/BPP/包装）+ 对应可跑证据
  - [√] 2.1.6 边界 Case：构造器环、setter 环、@Lazy 打断环、带代理的环（至少 4 个）
  - [√] 2.1.7 排障分流：遇到 circular reference 报错时，先判定注入类型/是否 AOP/是否允许环，再定位到具体 bean
  - [√] 2.1.8 绑定 Labs：`SpringCoreBeansCircularDependencyBoundaryLabTest` + `SpringCoreBeansEarlyReferenceLabTest`
- [√] 2.2 FactoryBean 证据链加深：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/23-factorybean-deep-dive.md`
  - [√] 2.2.1 改写“问题引入”：为什么很多人把 FactoryBean 当成普通 bean 用，然后注入/按类型查找就崩
  - [√] 2.2.2 最短调用链：`getBean("x")` → `getObjectForBeanInstance` → `FactoryBean#getObject`
  - [√] 2.2.3 `&` 前缀规则：什么时候需要 `&`，什么时候不需要（给 3 个最容易混的例子）
  - [√] 2.2.4 类型匹配证据链：`getType("x")` 如何工作、`getObjectType()` 不准会导致什么错配
  - [√] 2.2.5 缓存语义：FactoryBean 自身 singleton 与产品 singleton 是两套语义（用 identity 断言证明）
  - [√] 2.2.6 边界 Case：按类型注入 FactoryBean 本身、按类型注入产品、泛型产品、SmartFactoryBean（如适用）
  - [√] 2.2.7 绑定 Labs：`SpringCoreBeansFactoryBeanDeepDiveLabTest` + `SpringCoreBeansFactoryBeanEdgeCasesLabTest`
- [√] 2.3 泛型/类型匹配证据链加深：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md`
  - [√] 2.3.1 建立“误解清单”：为什么很多人以为 Spring 能 100% 精准识别所有泛型
  - [√] 2.3.2 用 2~3 个反例把误解打碎（集合泛型/多候选/FactoryBean 产品类型不准）
  - [√] 2.3.3 证据链：`ResolvableType` 在候选筛选中的参与点（给出关键类/方法名单 + 分支条件）
  - [√] 2.3.4 边界 Case：`ObjectProvider<T>` 延迟获取对候选选择的影响（什么时候“注入失败但 provider 能拿到”）
  - [√] 2.3.5 绑定 Labs：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
- [√] 2.4 `@Lazy` 语义证据链加深：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/023-18-lazy-semantics.md`
  - [√] 2.4.1 定义两种 Lazy：definition-lazy（lazy-init）vs injection-point-lazy（注入点代理），先给“结论表”
  - [√] 2.4.2 证据链：为什么“lazy-init 不等于注入不创建”（解释依赖解析触发点）
  - [√] 2.4.3 代理边界：JDK/CGLIB 选择条件、按 concrete class 查找失败的原因与修复方式
  - [√] 2.4.4 边界 Case：@Lazy + 循环依赖、@Lazy + FactoryBean、@Lazy + scoped proxy（至少 3 个）
  - [√] 2.4.5 绑定 Labs：`SpringCoreBeansLazyLabTest`（必要时补充 `SpringCoreBeansProxyingPhaseLabTest`）
- [√] 2.5 MergedBeanDefinition 证据链加深：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/35-merged-bean-definition.md`
  - [√] 2.5.1 改写“问题引入”：为什么你打印的 BD 跟你定义的不一样（根因：merged view）
  - [√] 2.5.2 最短调用链：获取 merged BD 的入口（不触发实例化）+ 合并发生点
  - [√] 2.5.3 watch list：合并前后关键字段对照（scope/lazy/autowire/dependsOn 等）+ 如何验证来源
  - [√] 2.5.4 边界 Case：parent/child、@Bean 方法元信息、BeanDefinitionCustomizer（如存在）叠加效果
  - [√] 2.5.5 绑定 Labs：`SpringCoreBeansMergedBeanDefinitionLabTest`
- [√] 2.6 占位符严格/非严格证据链加深：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md`
  - [√] 2.6.1 结论表：严格/非严格、默认值语法、缺失属性时的行为（含错误形态）
  - [√] 2.6.2 解析时机证据链：BeanDefinition 属性值 vs `@Value` 注入点（分别由谁处理、在什么阶段）
  - [√] 2.6.3 边界 Case：属性源覆盖顺序、@PropertySource、Environment vs PropertyPlaceholderConfigurer
  - [√] 2.6.4 排障分流：遇到 placeholder 未解析/解析为默认值/解析顺序不符合预期时怎么定位
  - [√] 2.6.5 绑定 Labs：`SpringCoreBeansValuePlaceholderResolutionLabTest`
- [√] 2.7 SpEL/`@Value` 证据链加深：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/44-spel-and-value-expression.md`
  - [√] 2.7.1 证据链：表达式解析/求值入口、类型转换入口（在哪个组件里完成）
  - [√] 2.7.2 失败形态固定化：表达式语法错误、引用 bean 不存在、类型转换失败（分别怎么报错）
  - [√] 2.7.3 “占位符 vs SpEL”顺序对照：什么时候先解析 `${}`，什么时候先求值 `#{}`
  - [√] 2.7.4 排障分流：读者只看现象（报错/值不对）也能定位到入口与组件
  - [√] 2.7.5 绑定 Labs：`SpringCoreBeansSpelValueLabTest`
- [√] 2.8 作用域/ScopedProxy 证据链加深：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md`
  - [√] 2.8.1 改写“问题引入”：prototype 注入到 singleton 为何不生效（先给反直觉现象）
  - [√] 2.8.2 证据链：无代理时为什么会变成“只创建一次”（容器解析依赖只发生一次）
  - [√] 2.8.3 scoped proxy 证据链：代理如何把调用路由到当前 scope target（关键类/方法 + 分支条件）
  - [√] 2.8.4 自定义 scope：存取位置、销毁回调、线程绑定/请求绑定（按你实现的 scope 类型写）
  - [√] 2.8.5 绑定 Labs：`SpringCoreBeansCustomScopeLabTest`
- [√] 2.9 SmartLifecycle phase 证据链加深：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/27-smart-lifecycle-phase.md`
  - [√] 2.9.1 最短调用链：refresh/close 时 lifecycle 的调度入口与 phase 分组算法
  - [√] 2.9.2 边界 Case：同 phase 顺序、依赖导致的顺序变化、stop callback 未触发的典型原因
  - [√] 2.9.3 watch list：phase 分组、正在 stop 的组件、回调触发点（能定位“卡在哪个 bean”）
  - [√] 2.9.4 排障分流：shutdown 卡死/超时/顺序不对（怎么快速缩小到某个 lifecycle bean）
  - [√] 2.9.5 绑定 Labs：`SpringCoreBeansSmartLifecycleLabTest`
- [√] 2.10 父子容器证据链加深：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/21-context-hierarchy.md`
  - [√] 2.10.1 结论表：可见性规则、屏蔽规则、containsBean vs containsLocalBean
  - [√] 2.10.2 证据链：按名查找与按类型查找在层级中的搜索路径（分别如何走 parent）
  - [√] 2.10.3 边界 Case：同名屏蔽、同类型多候选、child 覆盖/屏蔽导致的注入变化
  - [√] 2.10.4 排障分流：当你以为拿到的是 parent 的 bean 但实际是 child 的（如何确认与修复）
  - [√] 2.10.5 绑定 Labs：`SpringCoreBeansContextHierarchyLabTest`
- [√] 2.11 BeanDefinition 覆盖证据链加深：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/24-bean-definition-overriding.md`
  - [√] 2.11.1 结论表：允许/禁止覆盖时的行为、异常类型、报错信息结构
  - [√] 2.11.2 证据链：覆盖发生在哪个阶段（注册/解析/刷新）与关键条件
  - [√] 2.11.3 覆盖来源定位手册：如何通过 BD 来源 dump 追到是哪个配置/扫描/导入造成的
  - [√] 2.11.4 边界 Case：覆盖 vs 父子容器屏蔽（明确区分），以及对按类型注入候选的影响
  - [√] 2.11.5 绑定 Labs：`SpringCoreBeansBeanDefinitionOverridingLabTest`
- [√] 2.12 入口/目录同步（让“读文档的人”找得到“可跑入口”）：`spring-core-modules/spring-core-beans/docs/part-00-guide/013-02-breakpoint-map.md`、`spring-core-modules/spring-core-beans/docs/appendix/97-explore-debug-tests.md`、`spring-core-modules/spring-core-beans/docs/README.md`
  - [√] 2.12.1 Breakpoint Map：为每个主题补齐“入口断点 + watch list + 对应 Lab”
  - [√] 2.12.2 Explore Debug Tests：把新增/加深的 explore 用例分组，并明确“为什么需要 explore”
  - [√] 2.12.3 README：把“学完路径”（建议阅读顺序）与“点跑命令”写清楚

## 3. Security Check

- [√] 3.1 执行安全自检（G9）：确认无敏感信息写入、无生产环境操作、无危险命令残留

## 4. Testing

- [√] 4.1 运行测试：`mvn -pl :spring-core-beans test`
- [√] 4.2 构建文档站：`python3 -m mkdocs build -f docs-site/mkdocs.yml`

## 5. Documentation (Knowledge Base)

- [√] 5.1 同步更新 `helloagents/wiki/modules/spring-core-beans.md`（补充本次“证据链 + 边界 Labs 深化”入口）
- [√] 5.2 更新 `helloagents/CHANGELOG.md`（记录本次加深）

## 6. Migration

- [√] 6.1 迁移方案包到 `helloagents/history/2026-01/202601261257_beans_docs_deepen_more/`，并更新 `helloagents/history/index.md`
