# 第 26 章：99. 自测题：你是否真的理解了？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：自测题：你是否真的理解了？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 25 章：90. 常见坑清单（建议反复对照）](025-90-common-pitfalls.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 27 章：AOP/代理主线](../../../book/027-aop-proxy-mainline.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：自测题：你是否真的理解了？ —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
- 回到主线：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- IoC 分支：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansIocBranchMatrixLabTest test`
- 内部机制分支：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansInternalsBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/013-02-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/011-04-branch-decision-matrix.md)

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「自测题：你是否真的理解了？」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 0. 复现入口（可运行）

- 入口测试：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansExerciseTest test`

建议学习方式：

- 先不看代码，尝试回答问题
- 再去对应章节/实验里验证
- 最后再启用 Exercises 把理解落实成可运行的结论

## 心智模型（对应 01/06）

- 你能不能用一句话区分：`BeanDefinition`（定义元数据） vs bean instance（运行时对象）？
- BFPP/BPP/BDRPP 分别“改的是定义还是实例”？它们在哪个阶段发生？
- 你能不能复述 `ApplicationContext#refresh()` 的关键阶段（至少说清：什么时候跑 BFPP，什么时候注册/执行 BPP，什么时候开始创建非 lazy singleton）？

1) 用一句话解释：什么是 `BeanDefinition`？它与 Bean 实例的关系是什么？
2) BFPP 与 BPP 的差别是什么？它们分别作用在“定义层”还是“实例层”？
3) 为什么说“自动装配本质上也是在注册 BeanDefinition”？

!!! summary "本章要点"

    - 你能列出 4 条常见注册入口，并说明它们“注册的是谁/发生在什么时候”吗？
      - `@ComponentScan`
      - `@Configuration + @Bean`
      - `@Import`（含 `ImportSelector`）
      - `ImportBeanDefinitionRegistrar`
    - Spring Boot 自动装配对“Bean 注册”的本质影响是什么？（提示：它更像“按条件批量 @Import”）

    4) `@ComponentScan`、`@Bean`、`@Import` 这三种入口分别解决什么问题？
    5) `ImportSelector` 与 `ImportBeanDefinitionRegistrar` 的角色差异是什么？
    6) 你如何解释 Spring Boot 自动装配“从哪里拿到要导入的配置类列表”？

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansBeanGraphDebugLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`

## 依赖注入（对应 03）

- 当一个接口有两个实现时，`@Autowired` 单注入会发生什么？你会优先用 `@Qualifier` 还是 `@Primary`，为什么？
- `@Order` 能不能解决单注入歧义？它主要解决什么问题？
- `@Priority` 能不能作为“默认实现”方案？它与 `@Primary` 的优先级如何？（建议用 Lab 验证，不要靠猜）
- 你能不能说出注入解析的“源码级决策树”：先收集候选，再缩小候选？关键断点打在哪里？

7) 同类型多个候选时，`@Qualifier` 与 `@Primary` 各自适合什么场景？
8) `ObjectProvider` 解决的是什么问题？它为什么有助于 prototype 注入？
9) 遇到 `NoUniqueBeanDefinitionException` 时，你的排查顺序是什么？

## Scope 与生命周期（对应 04/05）

- `singleton` 与 `prototype` 的真实语义分别是什么？它们的“创建时机/销毁时机”有什么根本区别？
- prototype 注入 singleton 后为什么“看起来像单例”？你能给出 2 种正确的解决方式吗？
- 你能不能写出（或复述）初始化阶段的回调顺序：BPP before-init / `@PostConstruct` / `afterPropertiesSet` / initMethod / BPP after-init？

10) prototype 的语义是什么？为什么“prototype 注入 singleton”会像单例？
11) `@PostConstruct` 在 bean 创建流程的哪个阶段触发？
12) 为什么 prototype 的 `@PreDestroy` 常常不会触发？

## 机制题（对应 07/08/09）

- 为什么 `@Configuration(proxyBeanMethods=false)` 下，配置类内部 `@Bean` 方法互调可能 new 出额外对象？最推荐的写法是什么？
- `FactoryBean` 的两条硬规则是什么？（提示：`name` vs `&name`）
- `FactoryBean#isSingleton()` 决定缓存的是什么？（提示：缓存的是 product）
- `getObjectType()` 返回 `null` 会导致什么边界问题？为什么 `allowEagerInit=false` 会放大它？
- 循环依赖为什么构造器基本救不了、setter 有时能救？early reference 的意义是什么？代理介入后为什么更复杂？

13) `proxyBeanMethods=false` 下，为什么在 `@Bean` 方法体里互相调用可能会 new 出额外实例？
14) 为什么 `getBean("sequence")` 拿到的是 Long 而不是 `SequenceFactoryBean`？
15) 构造器循环依赖为什么必然失败？setter 循环为什么有时能成功？

## H. 值解析与类型转换（对应 34/36）

- 你能不能说清 `@Value` 的链路：先做 `${...}`/SpEL 解析，再做类型转换？核心断点打在哪里？
- 你能不能解释“为什么在 BFPP 里把 property value 写成字符串，最后能注入到 `int` 属性里”？这属于 bean 创建的哪个阶段？
- 你要让字符串能注入为自定义值对象（例如 `UserId`），你会把 Converter 注册在哪里？它怎么被安装进 BeanFactory？

16) `@Value("${demo.port}") int port` 这行代码背后至少经历了哪两步（解析与转换）？
17) `BeanDefinition#getPropertyValues()` 里的 `"8080"` 最终写入 `int port` 的关键入口方法是什么？

## I. 泛型匹配与注入坑（对应 37/29）

- 你能不能用一句话解释：为什么按 `Handler` 能找到，但按 `Handler<String>` 找不到？
- `ResolvableType` 在这个问题里扮演什么角色？
- 为什么“候选是运行时 proxy 实例”时，泛型信息更容易丢失？

18) 为什么 `DefaultListableBeanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(Handler.class, String.class))` 可能返回空？
19) 你会用什么工程策略避免“靠泛型匹配做关键依赖注入”带来的不确定性？

## J. 候选收敛决策树（对应 33/32/37）

> 这一组题专门练“注入失败/注错”的排障能力：你要能把现象落到候选收敛的具体分支上。

20) by-name fallback 什么时候会触发？它依赖什么信息（field name vs constructor parameter name、是否需要 `-parameters`）？
21) 为什么 `@Qualifier` 可以“压过” `@Primary`？你会用哪一个 Lab 用例证明？
22) `@Resource` 的 name-first 与 `@Autowired` 的 by-name fallback 有什么本质差异？你会用哪个章节/断点证明？
23) 多候选时，`ObjectProvider#getIfUnique()` 与 `getObject()` 各自会怎样表现？为什么这会影响“可选依赖”的写法？
24) `ObjectProvider#orderedStream()` 的排序规则来自哪里？它与 `List<T>` 注入排序是同一套机制吗？
25) 泛型参与候选收敛时，哪些场景可靠、哪些场景不可靠？（提示：class metadata vs 运行时 proxy 实例）
26) 你如何把 `UnsatisfiedDependencyException` 拆成“外层包装异常 + root cause（NoUnique/NoSuch）”？排查顺序是什么？
27) 在断点里你会观察哪些字段来确认“dependency 的名字/类型/是否 required”？（提示：DependencyDescriptor）
28) alias 会不会影响候选选择？它主要影响哪些匹配路径（按名匹配、qualifier 匹配）？
29) 什么时候应该避免 relying on by-name fallback？你会如何把依赖关系改写得更明确？

## 动手题（建议直接做 Exercises）

这些题都已经在本模块的 Exercises 里给出（默认 `@Disabled`）：

- 让 `FormattingService` 切换为 lower formatter（体会 `@Qualifier`）
- 去掉 `@Qualifier`，改用 `@Primary` 解决歧义
- 让 `DirectPrototypeConsumer` 每次都拿到新 id（体会 prototype 注入陷阱与解决方式）

对应文件：

- `src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java`
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`
推荐断点：`DefaultListableBeanFactory#doResolveDependency`、`AbstractAutowireCapableBeanFactory#doCreateBean`、`AbstractAutowireCapableBeanFactory#initializeBean`

## First Pass（10 个最小闭环入口，按 Lab 自测）

如果你不想一次性把整套章节都读完，想先把“主线 + 常见坑点”跑通一遍，可以按下面 10 个入口做自测：每个入口只要求你写 1–2 句结论（定义层/实例层/时机/顺序/断点入口）。

1) 定义层 vs 实例层：`SpringCoreBeansContainerLabTest#beanDefinitionIsNotTheBeanInstance`
2) refresh 阶段感：从 `AbstractApplicationContext#refresh` 走一遍 BFPP/BPP 的关键阶段（同上测试即可）
3) 注册入口：`SpringCoreBeansBootstrapInternalsLabTest`（配合 [02](../part-01-ioc-container/02-bean-registration.md)）
4) 注入歧义：`SpringCoreBeansInjectionAmbiguityLabTest`
5) 候选选择边界：`SpringCoreBeansAutowireCandidateSelectionLabTest`
6) prototype 注入陷阱：`SpringCoreBeansLabTest`（prototype 相关用例）
7) 生命周期回调顺序：`SpringCoreBeansLifecycleCallbackOrderLabTest`
8) post-processor 职责边界：`SpringCoreBeansRegistryPostProcessorLabTest` + `SpringCoreBeansPostProcessorOrderingLabTest`
9) early reference：`SpringCoreBeansEarlyReferenceLabTest`
10) 排障断点入口：`SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansBeanGraphDebugLabTest`

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「自测题：你是否真的理解了？」的生效时机/顺序/边界；断点/入口：`org.springframework.context.support.AbstractApplicationContext#refresh`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「自测题：你是否真的理解了？」的生效时机/顺序/边界；断点/入口：`org.springframework.beans.factory.support.DefaultListableBeanFactory`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「自测题：你是否真的理解了？」的生效时机/顺序/边界；断点/入口：`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreBeansLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansBootstrapInternalsLabTest` / `SpringCoreBeansInjectionAmbiguityLabTest` / `SpringCoreBeansAutowireCandidateSelectionLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansBeanGraphDebugLabTest`
- Exercise：`SpringCoreBeansExerciseTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`

上一章：[97. Explore/Debug 用例（可选启用，不影响默认回归）](97-explore-debug-tests.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
