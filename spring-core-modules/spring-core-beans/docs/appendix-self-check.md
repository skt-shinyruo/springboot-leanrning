# 自检：spring-core-beans 文档导航
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，将现象固化为断言，再结合正文理解机制；在真实项目中，常见路径包括：通过配置类/扫描/导入注册 Bean；通过注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。

    观察对象：自检题：能否复述并验证。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansLabTest`。需要下探源码时，可以从 `org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate` 这些入口切入。

<!-- CHAPTER-CARD:END -->

## 本页路线图

本页不是新的主线章节，而是把已读过的机制拿回来验证、排障和自检。读法如下：

1. 先运行 Book Matrix、Branch Matrix 或本页列出的最小 Lab，把现象固定成可重复结果。
2. 再按现象、题目或坑点定位对应章节、断点和关键变量。
3. 最后用对应实验/测试 收束答案；如果答案仍然只停留在概念层面，再回到正文补齐机制。

## 自检结论：能复述才算跑通

- 收束：自检题不是背名词，而是把现象还原为章节、入口方法、断点变量和验证用例。
- 主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

## 自检入口：用题目反推章节、Lab 和断点

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html

### 统一答题结构（每题至少包含 4 个要素）

- **现象**：读者观察到的行为/异常（最好能复现）
- **证据链**：1 个入口方法 + 2–3 个关键分支/变量
- **修复**：最小可行动作（配置/代码/调用方式）
- **验证**：对应实验/测试（方法级更佳）

> 本页所有问题都要求读者按“现象→证据链→修复→验证”给出可复述答案。

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- IoC 分支：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansIocBranchMatrixLabTest test`
- 内部机制分支：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansInternalsBranchMatrixLabTest test`
- 配套资料：[断点地图](guide-breakpoint-map.md) / [关键分支矩阵](guide-branch-decision-matrix.md)

本页把「自检题：能否复述并验证」拆成可检查的问题：先定边界，再看分支，最后用可运行入口验证结论。

阅读路径：
- 先阅读章首的“章节入口/本章要点”，建立预期；
- 先运行一遍本章 Lab，再带着问题回到正文。

## 复现入口（可运行）

- 入口测试：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java`
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansExerciseTest test`

学习方式：

- 先不看代码，尝试回答问题
- 再去对应章节/实验里验证
- 最后再启用 练习 把理解落实成可运行的结论

## 基础抓手（对应 01/06）

验证入口：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansInfrastructureBeanRoleLabTest`

- 需要用一句话区分：`BeanDefinition`（定义元数据） vs bean instance（运行时对象）？
- BFPP/BPP/BDRPP 分别“改的是定义还是实例”？它们在哪个阶段发生？
- 需要复述 `ApplicationContext#refresh()` 的关键阶段（至少说清：什么时候执行 BFPP，什么时候注册/执行 BPP，什么时候开始创建非 lazy singleton）？

1. 用一句话解释：什么是 `BeanDefinition`？它与 Bean 实例的关系是什么？
2. BFPP 与 BPP 的差别是什么？它们分别作用在“定义层”还是“实例层”？
3. 为什么说“自动装配本质上也是在注册 BeanDefinition”？

!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansBeanGraphDebugLabTest`
    - 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`

## 依赖注入（对应 03）

> 官方参考（Spring Framework 6.2.x，注解驱动与依赖注入语义）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html

验证入口：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansProgrammaticResolveDependencyLabTest` / `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest`

- 当一个接口有两个实现时，`@Autowired` 单注入会发生什么？可以优先用 `@Qualifier` 还是 `@Primary`，为什么？
- `@Order` 能不能解决单注入歧义？它主要解决什么问题？
- `@Priority` 能不能作为“默认实现”方案？它与 `@Primary` 的优先级如何？（用 Lab 验证，不要靠猜）
- 需要说出注入解析的“源码级决策树”：先收集候选，再缩小候选？关键断点打在哪里？

7. 同类型多个候选时，`@Qualifier` 与 `@Primary` 各自适合什么场景？
8. `ObjectProvider` 解决的是什么问题？它为什么有助于 prototype 注入？
9. 遇到 `NoUniqueBeanDefinitionException` 时，相应的排查顺序是什么？

## Scope 与生命周期（对应 04/05）

验证入口：`SpringCoreBeansLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest`

- `singleton` 与 `prototype` 的真实语义分别是什么？它们的“创建时机/销毁时机”有什么根本区别？
- prototype 注入 singleton 后为什么“表面上像单例”？需要能给出 2 种正确的解决方式吗？
- 需要能写出（或复述）初始化阶段的回调顺序：BPP before-init / `@PostConstruct` / `afterPropertiesSet` / initMethod / BPP after-init？

10. prototype 的语义是什么？为什么“prototype 注入 singleton”会像单例？
11. `@PostConstruct` 在 bean 创建流程的哪个阶段触发？
12. 为什么 prototype 的 `@PreDestroy` 常常不会触发？

## 机制题（对应 07/08/09）

验证入口：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest` / `SpringCoreBeansEarlyReferenceLabTest`

- 为什么 `@Configuration(proxyBeanMethods=false)` 下，配置类内部 `@Bean` 方法互调可能 new 出额外对象？最稳妥的写法是什么？
- `FactoryBean` 的两条硬规则是什么？（提示：`name` vs `&name`）
- `FactoryBean#isSingleton()` 决定缓存的是什么？（提示：缓存的是 product）
- `getObjectType()` 返回 `null` 会导致什么边界问题？为什么 `allowEagerInit=false` 会放大它？
- 循环依赖为什么构造器基本救不了、setter 有时能救？early reference 的意义是什么？代理介入后为什么更复杂？

13. `proxyBeanMethods=false` 下，为什么在 `@Bean` 方法体里互相调用可能会 new 出额外实例？
14. 为什么 `getBean("sequence")` 获取到的是 Long 而不是 `SequenceFactoryBean`？
15. 构造器循环依赖为什么必然失败？setter 循环为什么有时能成功？

## H. 值解析与类型转换（对应 34/36）

验证入口：`SpringCoreBeansValuePlaceholderResolutionLabTest` / `SpringCoreBeansTypeConversionLabTest`

- 需要能说清 `@Value` 的链路：先做 `${...}`/SpEL 解析，再做类型转换？核心断点打在哪里？
- 需要解释“为什么在 BFPP 里把 property value 写成字符串，最后能注入到 `int` 属性里”？这属于 bean 创建的哪个阶段？
- 需要让字符串能注入为自定义值对象（例如 `UserId`），可以把 Converter 注册在哪里？它怎么被安装进 BeanFactory？

16. `@Value("${demo.port}") int port` 这行代码背后至少经历了哪两步（解析与转换）？
17. `BeanDefinition#getPropertyValues()` 里的 `"8080"` 最终写入 `int port` 的关键入口方法是什么？

## I. 泛型匹配与注入误区（对应 37/29）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


验证入口：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest` / `SpringCoreBeansFactoryBeanEdgeCasesLabTest`

- 需要用一句话解释：为什么按 `Handler` 能找到，但按 `Handler<String>` 找不到？
- `ResolvableType` 在这个问题里扮演什么角色？
- 为什么“候选是运行时 proxy 实例”时，泛型信息更容易丢失？

18. 为什么 `DefaultListableBeanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(Handler.class, String.class))` 可能返回空？
19. 可以用什么工程策略避免“靠泛型匹配做关键依赖注入”带来的不确定性？

## J. 候选收敛决策树（对应 33/32/37）

验证入口：`SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansResourceInjectionLabTest`

> 这一组题专门练“注入失败/注错”的排障能力：需要把现象落到候选收敛的具体分支上。

20. by-name fallback 什么时候会触发？它依赖什么信息（field name vs constructor parameter name、是否需要 `-parameters`）？
21. 为什么 `@Qualifier` 可以“压过” `@Primary`？可以用哪一个 Lab 用例证明？
22. `@Resource` 的 name-first 与 `@Autowired` 的 by-name fallback 有什么本质差异？可以用哪个章节/断点证明？
23. 多候选时，`ObjectProvider#getIfUnique()` 与 `getObject()` 各自会怎样表现？为什么这会影响“可选依赖”的写法？
24. `ObjectProvider#orderedStream()` 的排序规则来自哪里？它与 `List<T>` 注入排序是同一套机制吗？
25. 泛型参与候选收敛时，哪些场景可靠、哪些场景不可靠？（提示：class metadata vs 运行时 proxy 实例）
26. 如何把 `UnsatisfiedDependencyException` 拆成“外层包装异常 + root cause（NoUnique/NoSuch）”？排查顺序是什么？
27. 在断点里可以观察哪些字段来确认“dependency 的名字/类型/是否 required”？（提示：DependencyDescriptor）
28. alias 会不会影响候选选择？它主要影响哪些匹配路径（按名匹配、qualifier 匹配）？
29. 什么时候应该避免 relying on by-name fallback？可以如何把依赖关系改写得更明确？

## K. 注入点元数据（对应 03/07）

1. 字段注入点与构造器参数注入点在 Spring 内部对应哪些元数据对象？在断点中可通过哪些方式区分它们？（提示：`DependencyDescriptor#getField()` / `#getMethodParameter()`）
2. 为什么“表面上同样是 `T`”，但泛型注入（例如 `Handler<String>`）在某些场景会出现误判？可用哪个对象证明这是“注入点元数据/ResolvableType”的问题，而不是“候选未注册”的问题？
3. `@Bean` 工厂方法参数解析的入口在哪里？可如何证明它并不依赖 `@Configuration` 的增强（`proxyBeanMethods`）？

## 动手题（直接做 练习）

这些题都已经在本模块的 练习 里给出（默认 `@Disabled`）：

- 让 `FormattingService` 切换为 lower formatter（体会 `@Qualifier`）
- 去掉 `@Qualifier`，改用 `@Primary` 解决歧义
- 让 `DirectPrototypeConsumer` 每次都获取到新 id（体会 prototype 注入陷阱与解决方式）

对应文件：

- `src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java`
对应实验/测试：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`
断点入口：`DefaultListableBeanFactory#doResolveDependency`、`AbstractAutowireCapableBeanFactory#doCreateBean`、`AbstractAutowireCapableBeanFactory#initializeBean`

## First Pass（10 个最小闭环入口，按 Lab 自测）

若不想一次性把整套章节都读完，想先把“主线 + 常见误区”完成验证一遍，可以按下面 10 个入口做自测：每个入口只要求读者写 1–2 句结论（定义层/实例层/时机/顺序/断点入口）。

1. 定义层 vs 实例层：`SpringCoreBeansContainerLabTest#beanDefinitionIsNotTheBeanInstance`
2. refresh 阶段感：从 `AbstractApplicationContext#refresh` 走一遍 BFPP/BPP 的关键阶段（同上测试即可）
3. 注册入口：`SpringCoreBeansBootstrapInternalsLabTest`（配合 [02](ioc-bean-registration.md)）
4. 注入歧义：`SpringCoreBeansInjectionAmbiguityLabTest`
5. 候选选择边界：`SpringCoreBeansAutowireCandidateSelectionLabTest`
6. prototype 注入陷阱：`SpringCoreBeansLabTest`（prototype 相关用例）
7. 生命周期回调顺序：`SpringCoreBeansLifecycleCallbackOrderLabTest`
8. post-processor 职责边界：`SpringCoreBeansRegistryPostProcessorLabTest` + `SpringCoreBeansPostProcessorOrderingLabTest`
9. early reference：`SpringCoreBeansEarlyReferenceLabTest`
10. 排障断点：`SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansBeanGraphDebugLabTest`

## 证据链（调用链 + 断点 + 断言）

自检题不要求背完整源码，但每个答案都要能回到具体观察点：

- `AbstractApplicationContext#refresh`：判断问题停在定义阶段、注册 BPP 阶段，还是进入了单例创建阶段。
- `DefaultListableBeanFactory#doResolveDependency`：回答候选集合如何形成、如何被 `@Qualifier` / `@Primary` / by-name 等规则收敛。
- `AbstractAutowireCapableBeanFactory#doCreateBean`：确认实例化、属性填充、初始化与 BPP 替换分别发生在哪一步。

运行完成 `SpringCoreBeansLabTest` 后，用上面三个入口各写 1 句结论；如果只能说“断点命中了”，说明还没有形成可复述证据。

## 面试使用方式（把自测题“收敛成可复述答案”）

把这页当成“题库”，但答题时不要停留在名词解释。按固定结构输出：

1. **结论（1 句）**：先把边界说清楚（能/不能/发生在哪个阶段）。
2. **证据链（方法级调用链）**：给出 1 个入口方法名 + 2–3 个关键分支/数据结构。
3. **最小复现**：指向 1 个可运行的实验/测试（最好能到方法级）。
4. **反例/误区**：给出 1 个“常见误归因”，说明为什么错。

模板对照：`appendix-interview-playbook.md`

## 验证标准：题目必须能回到证据链
- 是否能在不看文档的情况下，用 2–3 句话复述 `refresh()` 的两条主线（定义阶段/创建阶段），并指出各自的关键断点入口？
- 是否能把“现象”稳定复现为一个可回归的实验/测试，并写出 1–2 句结论（而不是抄结论）？
- 是否能从任意一条自测题出发，给出：对应章节 + 对应 Lab + 关键观察点 + 修复方向？


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansBeanGraphDebugLabTest`
- Exercise：`SpringCoreBeansExerciseTest`
- 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`

<!-- BOOKIFY:END -->
