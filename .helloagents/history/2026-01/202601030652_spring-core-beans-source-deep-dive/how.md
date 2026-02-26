# Technical Design: spring-core-beans 知识点 × 源码解析深挖方案（Deep Dive）

## Technical Solution

### Core Technologies
- Spring Framework IoC（`ApplicationContext` / `BeanFactory` / `BeanDefinition`）
- Spring Framework Core internals（`AbstractApplicationContext#refresh`、`AbstractAutowireCapableBeanFactory#doCreateBean`、`DefaultListableBeanFactory#doResolveDependency`）
- JUnit 5 + AssertJ（以测试作为“行为基线”）
- IDE Debugger（Step Into 外部库源码、条件断点、观察变量/集合、调用栈）

### Documentation Strategy（文档落地策略）

本次实现以“把知识点写进项目 docs”为交付物，遵循以下写法：
1. **不粘贴大段 Spring Framework 原始源码**：用“类/方法名 + 调用链 + 关键状态 + 精简伪代码”表达机制，避免噪声与版权风险。
2. **必须引用仓库 `src/` 的代码作为可运行锚点**：在每个知识点末尾给出对应的本仓库示例（优先 `spring-core-beans/src/test/java/...` 的 Lab；必要时补 `spring-core-beans/src/main/java/...` 的 demo 代码）。
3. **代码引用最小化**：在 docs 中只放“最小必要片段”（5~30 行级别），其余用路径链接/类名定位，保证文档可读、可维护。

### Source Reading Prerequisites（源码阅读前置）

1) **确保 IDE 能 Step Into Spring 源码**
- 推荐在 IDE 里开启：Step into external libraries / Download sources
- Maven 也可下载 sources（任选其一即可）：
  - `mvn -pl spring-core-beans -DskipTests dependency:sources`
  - `mvn -pl spring-core-beans -DskipTests dependency:resolve -Dclassifier=sources`

2) **调试姿势建议**
- 用 “单测试类/单方法” 运行：避免大量无关 Bean 干扰观察
- 优先看：调用栈 + 关键集合（候选集合/缓存）+ 关键状态位（是否 in creation）
- 禁止在第 1 轮就追到最底层：先抓主线，再回填细节

---

## 1. 容器启动主线：`AbstractApplicationContext#refresh`（12 步心智地图）

> 目标：把“概念名词”落到“源码阶段感”，并能在排障时准确定位：问题发生在定义层还是实例层。

建议从以下任一 Lab 入口 Step Into：
- `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`
- `SpringCoreBeansContainerLabTest`（构造 `AnnotationConfigApplicationContext` 的用例）

### 1.1 refresh 的高层步骤（建议你能复述）
1. `prepareRefresh`：刷新前准备（记录启动时间、初始化属性源、校验必需属性等）
2. `obtainFreshBeanFactory` → `refreshBeanFactory`：创建/刷新内部 `BeanFactory`（核心容器实例）
3. `prepareBeanFactory`：把基础设施装进 `BeanFactory`（classloader、resolvers、environment、一些 resolvable dependencies）
4. `postProcessBeanFactory`：给子类留的 hook（常见为空）
5. `invokeBeanFactoryPostProcessors`：执行 BDRPP/BFPP（定义层改写与新增定义）
6. `registerBeanPostProcessors`：注册 BPP（决定后续实例创建/注入/初始化/代理）
7. `initMessageSource`：国际化设施（ApplicationContext 的能力）
8. `initApplicationEventMulticaster`：事件多播器（ApplicationContext 的能力）
9. `onRefresh`：子类 hook（Web 场景会初始化 Web 相关设施）
10. `registerListeners`：注册监听器并发布 early events
11. `finishBeanFactoryInitialization`：
    - 初始化 conversion service
    - 调用 `beanFactory.preInstantiateSingletons()`（关键：非 lazy 单例会在这里批量创建）
12. `finishRefresh`：发布容器就绪事件（如 ContextRefreshedEvent）

### 1.2 refresh 的两条“关键分叉”

**分叉 A：定义层（BDRPP/BFPP）发生在实例化之前**
- 典型主角：`PostProcessorRegistrationDelegate`
- 你要观察的事实：
  - BeanDefinition 的数量/内容在这一步会发生“可观测变化”
  - `@Configuration`、`@Bean`、`@ComponentScan`、`@Import` 的“为什么能工作”，大多是靠这里注册的基础设施

**分叉 B：实例层（BPP）决定最终暴露对象**
- 典型主角：`BeanPostProcessor` / `InstantiationAwareBeanPostProcessor`
- 你要观察的事实：
  - 同一个 beanName，最终 `getBean()` 拿到的对象可能被替换为 proxy
  - 代理替换可能发生在多个阶段（见第 4 节）

---

## 2. 定义层：BeanDefinition 从哪里来（Scan / @Bean / @Import）

### 2.1 三类“输入”对应的典型注册路径

1) **扫描（component scanning）**
- 关键类：
  - `ClassPathBeanDefinitionScanner`
  - `ClassPathScanningCandidateComponentProvider`
- 典型输出：
  - `ScannedGenericBeanDefinition`
- 观察点：
  - `BeanDefinition#getBeanClassName`（类名）
  - `BeanDefinition#getSource` / `getResourceDescription`（可能提示来自哪个资源/元数据）

2) **`@Configuration` + `@Bean`（工厂方法定义）**
- 关键类：
  - `ConfigurationClassPostProcessor`（BDRPP）
  - `ConfigurationClassParser`（解析配置模型）
  - `ConfigurationClassBeanDefinitionReader`（把模型注册为 BeanDefinition）
- 典型输出：
  - `ConfigurationClassBeanDefinition`（内部类型）
  - 或 `RootBeanDefinition`（最终合并形态）
- 观察点：
  - `factoryBeanName` / `factoryMethodName`（标识来源是工厂方法）
  - `resolvedFactoryMethod`（调试时可见）

3) **`@Import` / registrar（程序化注册定义）**
- 关键类：
  - `ImportSelector` / `DeferredImportSelector`
  - `ImportBeanDefinitionRegistrar`
- 观察点：
  - “谁 import 了谁”（可从 parser 的模型里追）
  - registrar 里对 `BeanDefinitionRegistry` 的写入点

### 2.2 你应该如何在调试时回答“这个 bean 谁注册的？”

建议采用“先看定义层元信息，再追注册路径”的顺序：
1. 在 `DefaultListableBeanFactory#getBeanDefinition(beanName)` 拿到 definition
2. 查看以下字段（优先级从高到低）：
   - `factoryBeanName` / `factoryMethodName`：基本可直接确定是 `@Bean` 工厂方法
   - `beanClassName`：如果是扫描/直接类定义，通常是具体类名
   - `resourceDescription`：很多情况下能看见来自哪个配置类/资源
   - `source`：可能是注解元数据/方法元数据（用于追根）

---

## 3. 后处理器主线：BDRPP/BFPP/BPP 的调用顺序与“为什么顺序会改变结果”

> 目标：解决一个高频困惑：为什么我只是换了一个 processor 的顺序，行为就变了（注入/代理/条件装配/循环依赖表现不同）。

### 3.1 定义层：`PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`

核心要点：
- 先处理 `BeanDefinitionRegistryPostProcessor`（因为它可能“新增定义”）
- 再处理普通 `BeanFactoryPostProcessor`（改已有定义）
- 同一类 processor 内部再按：
  1) `PriorityOrdered`
  2) `Ordered`
  3) 无序

你应该观察的事实：
- 执行完 BDRPP/BFPP 后，BeanDefinition 数量可能变化
- `ConfigurationClassPostProcessor` 就在这里发挥关键作用（解析 @Configuration/@Bean/@Import 等）

### 3.2 实例层：`PostProcessorRegistrationDelegate#registerBeanPostProcessors`

核心要点：
- BPP 的“注册顺序”决定了后续每个 bean 的创建链路
- 特别是 `InstantiationAwareBeanPostProcessor` / `SmartInstantiationAwareBeanPostProcessor`：
  - 可能在实例化前短路（直接返回 proxy）
  - 可能参与 early reference（循环依赖）

你应该观察的事实：
- 同一 bean 在多个 BPP 链路里被触发（before/after/init）
- 有的 BPP 只对特定注解或接口生效（例如注入、@Resource、@PostConstruct 等）

---

## 4. 实例创建主线：`AbstractAutowireCapableBeanFactory#doCreateBean`

> 目标：把“生命周期”落到源码：instantiate → populate → initialize，并精确知道 BPP 在哪里插入。

建议对照 Lab：
- `SpringCoreBeansBeanCreationTraceLabTest`（events 序列是现成的“时间线”）

### 4.1 doCreateBean 的关键阶段（高层）
1) `createBeanInstance`：选择构造器/工厂方法创建实例
2) **early singleton exposure（可选）**：
   - 对单例，且允许循环依赖时：提前暴露 `ObjectFactory`（用于后续拿 early reference）
3) `populateBean`：填充属性/依赖注入
   - 关键扩展点：`InstantiationAwareBeanPostProcessor#postProcessProperties`
4) `initializeBean`：Aware → beforeInit BPP → init methods → afterInit BPP
5) `registerDisposableBeanIfNecessary`：注册销毁回调（注意 prototype 默认语义）
6) 返回：可能返回原始实例，也可能返回 proxy（最终暴露对象）

### 4.2 为什么 “按接口类型能拿到 proxy，但按实现类类型拿不到”？

你在 `SpringCoreBeansBeanCreationTraceLabTest` 会看到：
- BPP 在 afterInitialization 返回了 JDK proxy（只实现接口）
- 因此：
  - `getBean(WorkService.class)` 成功（接口匹配）
  - `getBean("service", TraceableService.class)` 失败（类型不匹配）

这个现象对应到源码里的关键点是：
- “最终暴露对象”的类型取决于 BPP 返回值，而不是 beanClass
- 类型匹配发生在 getBean/resolveDependency 的路径里（见第 5 节）

---

## 5. 依赖注入解析：`DefaultListableBeanFactory#doResolveDependency` 的候选选择算法

> 目标：你不仅能说“用 @Qualifier/@Primary”，还要能解释：它们在算法里分别影响哪一步。

建议对照 Lab：
- `SpringCoreBeansInjectionAmbiguityLabTest`
- `SpringCoreBeansAutowireCandidateSelectionLabTest`

### 5.1 一个典型注入点会走到哪里

最常见的注入入口来自：
- `AutowiredAnnotationBeanPostProcessor#postProcessProperties`（字段/方法注入）
- 构造器注入通常在实例化阶段由 `ConstructorResolver` 参与（但最终也会调用到依赖解析）

核心解析入口：
- `DefaultListableBeanFactory#resolveDependency` → `doResolveDependency`

### 5.2 候选集合与确定化（建议你在断点里观察的集合/变量）
你通常会看到以下关键步骤/数据：
1) 根据 `DependencyDescriptor` 推导需要的类型/泛型信息（`ResolvableType`）
2) `findAutowireCandidates` 收集候选 beanName → beanInstance/beanType
3) `determineAutowireCandidate` 做“确定化选择”，常见优先级影响因素：
   - `@Primary`（primary candidate）
   - `@Qualifier`（AutowireCandidateResolver 参与过滤/匹配）
   - `@Priority`（部分场景会影响选择）
   - beanName 匹配（按名称注入时）
4) 如果仍然无法确定，抛出 `NoUniqueBeanDefinitionException`

### 5.3 `@Resource` 为什么是 name-first？
`@Resource` 路径通常由 `CommonAnnotationBeanPostProcessor` 处理；
它倾向先按 name（字段名/注解 name）找，再回退到 type，这与 `@Autowired` 的 type-first 形成对照。

---

## 6. 代理与替换：三个常见发生点（“到底哪里被换成 proxy”）

> 目标：把“最终暴露对象可能是 proxy”的结论，落到三个具体方法入口。

1) **实例化前短路（before instantiation）**
- 入口：`AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation`
- 触发点：`SmartInstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`
- 结果：直接返回 proxy，后续 `doCreateBean` 可能被跳过（典型“短路”）

2) **early reference（循环依赖场景）**
- 入口：`AbstractAutowireCapableBeanFactory#getEarlyBeanReference`
- 触发点：`SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`
- 结果：提前暴露的对象可能已经是 proxy（与三级缓存强相关）

3) **初始化后替换（after initialization）**
- 入口：`BeanPostProcessor#postProcessAfterInitialization`
- 结果：最常见、最直观的包装/替换点

建议对照 Lab：
- `SpringCoreBeansProxyingPhaseLabTest`
- `SpringCoreBeansBeanCreationTraceLabTest`

---

## 7. 循环依赖：`DefaultSingletonBeanRegistry` 的三级缓存

> 目标：能解释“setter 循环依赖为什么可能成功”，并知道代价：可能拿到 early reference（尤其是 proxy 参与时）。

三个缓存（概念级）：
1) `singletonObjects`：完全初始化完成的单例（最终形态）
2) `earlySingletonObjects`：early reference（半成品/早期暴露对象）
3) `singletonFactories`：`ObjectFactory<?>`（用于延迟创建 early reference）

你应该观察的事实：
- setter/field 注入属于 populate 阶段，能在“实例已存在但未完全初始化”时解决依赖指向
- 构造器注入要求在实例化之前就拿到依赖，循环时通常无解，因此 fail-fast

建议对照 Lab：
- `SpringCoreBeansContainerLabTest#circularDependencyWithConstructorsFailsFast`
- `SpringCoreBeansContainerLabTest#circularDependencyWithSettersMaySucceedViaEarlySingletonExposure`

---

## 8. @Configuration(proxyBeanMethods) 增强：为什么能“保住单例语义”

关键类（方向性提示，具体以 IDE 版本源码为准）：
- `ConfigurationClassEnhancer`（生成 CGLIB 子类）
- 内部拦截器（将 `@Bean` 方法调用重定向到容器 getBean）

你应该观察的事实：
- `proxyBeanMethods=true`：在同一个 @Configuration 类里，`configB()` 调用 `configA()` 不会 new 一个新的，而是返回容器已有单例
- `proxyBeanMethods=false`：直接方法调用就是普通 Java 调用，容器无法拦截 → 可能产生“额外实例”

建议对照 Lab：
- `SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsTruePreservesSingletonSemanticsForBeanMethodCalls`
- `SpringCoreBeansContainerLabTest#configurationProxyBeanMethodsFalseAllowsDirectMethodCallToCreateExtraInstance`

---

## 9. FactoryBean：为什么 `&name` 拿到的是 factory，而不是 product

关键概念：
- `FactoryBean<T>` 本身也是一个 bean（factory）
- 同名 `getBean("name")` 默认返回的是 factory 生产的 product（`T`）
- `getBean("&name")` 才返回 factory 自身

你应该观察的事实：
- Spring 内部会在 getBean 路径里判断：目标是否是 FactoryBean，以及用户是否带 `&` 前缀
- `FactoryBean#isSingleton()` 会影响 product 的缓存语义

建议对照 Lab：
- `SpringCoreBeansContainerLabTest#factoryBeanByNameReturnsProductAndAmpersandReturnsFactory`

---

## Testing and Deployment

### Recommended test commands
- 只跑关键类：`mvn -pl spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test`
- 跑源码时间线：`mvn -pl spring-core-beans -Dtest=SpringCoreBeansBeanCreationTraceLabTest test`
- 完整回归：`mvn -pl spring-core-beans test`

## Security and Performance
- **Security:** 不涉及生产环境与外部服务；仅做本地源码阅读与单元测试运行
- **Performance:** 只跑单测/单方法，减少 refresh 次数；调试时避免一次跑全模块
