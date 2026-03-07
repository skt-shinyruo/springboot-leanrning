# 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。

    本章围绕30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）展开，主线可以概括为：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansInjectionPhaseLabTest`。需要下探源码时，可以从 `DependencyDescriptor#required` / `DependencyDescriptor#annotations` / `DependencyDescriptor#resolvableType` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 导读

本章围绕「30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreBeansInjectionPhaseLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansInjectionPhaseLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionPhaseLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    建议 先跑 `SpringCoreBeansInjectionPhaseLabTest` 把现象跑出来；跑完后回到正文，把“现象 → 调用链/分支 → 结论”对齐到源码。
    - 第一断点：`ApplicationContext#refresh`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“排障分流：这是定义层问题还是实例层问题？”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](appendix-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](guide-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，注解驱动与依赖注入语义）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html

这一章解决两个“非常折磨人但非常核心”的问题：

1. 为什么在构造器里访问 `@Autowired` 字段时，它永远是 `null`？
2. 为什么构造器注入（constructor injection）却能在构造器里获取到依赖？

关键结论先给出：

> **field injection 发生在“实例化之后”的属性填充阶段**，而 **constructor injection 发生在“实例化之前”的构造器解析阶段**。

建议直接运行：

## 现象：field injection 在构造器里拿不到依赖

- 构造器里记录“依赖是否为 null” → **会是 null**
- 容器完成注入后（在初始化阶段之前）依赖变为非 null
- `@PostConstruct` 里再次检查 → **依赖已经可用**

可以把它记成一条时间线：

1) **构造器执行**（对象刚被 `new` 出来）
2) **属性填充 / 注入阶段**（field/method injection 发生在这里）
3) **初始化回调**（例如 `@PostConstruct`）

## 现象：constructor injection 在构造器里就能获取到依赖

- 一个无参构造器（`no-arg`）
- 一个带参数的构造器，并用 `@Autowired` 标记为注入构造器（`autowired`）

当容器具备注解处理能力时（见 [容器启动与基础设施处理器：为什么注解能工作？](internals-container-bootstrap-and-infrastructure.md)），它会：

- 在实例化之前先决定“用哪个构造器”
- 再解析构造器参数依赖
- 然后创建对象

因此：constructor injection 的依赖在对象构造完成时就已经存在。

## 2.1 DependencyDescriptor 深入分析：解析“注入点语义”的核心对象

无论是构造器参数还是字段，最终都会被包装成 `DependencyDescriptor`：

- `DependencyDescriptor#required`：是否必需（决定是否允许 `null` / Optional）
- `DependencyDescriptor#annotations`：注入点上的注解集合（`@Qualifier/@Lazy/@Value` 等都会影响分支）
- `DependencyDescriptor#resolvableType`：泛型信息（决定按类型匹配是否精确）
- `DependencyDescriptor#getDependencyName`：按名称回退时的候选名（`@Resource` 尤其依赖它）

这就是“注入点语义”的单一入口；在排障过程中，建议优先从此处入手。

## 2.2 依赖解析分支树（简化版）

可以把 `doResolveDependency` 的决策流程记成一棵树：

1) **快捷路径**：Optional/Provider/@Lazy/@Value → 有条件地短路
2) **resolvableDependencies**：`registerResolvableDependency` 的直接命中
3) **候选收集**：`findAutowireCandidates`（按类型收集）
4) **候选收敛**：`determineAutowireCandidate`（@Primary/@Priority/@Qualifier/beanName）
5) **集合解析**：`Collection/Map/Stream/Array` 类型走“多候选路径”
6) **fallback**：可选依赖或容器默认值

每个分支都可能改变“读者到底获取到哪个对象”的结论。

## 2.3 关键变量解释（调试时只看这几项）

- `candidates`：收集到的候选集合（数量决定是否进入“歧义”分支）
- `primary` / `priority`：收敛时的优先级判定依据
- `dependencyName`：按名称回退的关键输入（字段名/参数名/Qualifier value）
- `resolvedCandidate`：最终被选中的 beanName（这是需要“证明”的结论）

## `postProcessProperties(...)` 在哪里起作用？

这一点是把“注解不是隐式行为”落地成可解释机制的关键：

- **field injection 并不是语言层做的**，而是容器在属性填充阶段调用了一组处理器完成的
- 其中一个关键扩展点就是：
  - `InstantiationAwareBeanPostProcessor#postProcessProperties(...)`
- Spring 的 `AutowiredAnnotationBeanPostProcessor`（一个基础设施 BPP）就是靠这条路径处理 `@Autowired` 字段/方法注入

若想把“注解能力从哪来”也串起来，请回看：

- [容器启动与基础设施处理器：为什么注解能工作？](internals-container-bootstrap-and-infrastructure.md)

- **不要在构造器里依赖 field injection 的字段**：那一定是 `null`（这是机制决定的，不是偶然）
- **必填依赖优先用 constructor injection**：更早失败、更容易测试、也更符合不可变设计
- 当类存在多个构造器时：
  - 用 `@Autowired` 明确指定注入构造器，避免“选择规则误判”

## 延伸阅读（把点连成线）

- AOP（代理与调用路径）：[spring-core-aop：代理（Proxy）+ 入口（Call Path）](../../spring-core-aop/docs/proxy-fundamentals-aop-proxy-mental-model.md)（为什么要跳：本章讲“注入发生在哪个阶段”，一旦引入代理，很多现象会变成“对象形态 + 调用路径”的问题；验证什么：在 AOP 章跑一个最小 proxy 用例，确认“字段注入/构造器注入”与“代理是否生效”是两条不同维度）
- 事务也是代理：[spring-core-tx：`@Transactional` 代理](../../spring-core-tx/docs/transaction-basics-transactional-proxy.md)

入口：

## 排障分流：这是定义层问题还是实例层问题？
> 官方参考（Spring Framework 6.2.x，注解驱动与依赖注入语义）：https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html


- “构造器里访问 field injection 字段为 null” → **这是实例层阶段差异（预期）**：field injection 在实例化之后才发生（本章第 1 节）
- “constructor injection 没走到带参构造器/选错构造器” → **实例层（构造器解析）**：看 `determineCandidateConstructors` 与 `autowireConstructor`（本章源码锚点）
- “`@Autowired/@Value` 完全不生效” → **优先定义层/基础设施问题**：注解处理器是否注册？（见 [12](internals-container-bootstrap-and-infrastructure.md)）
- “注入发生了但候选选择不符合预期” → **实例层（依赖解析）**：转到 [03](ioc-dependency-injection-resolution.md)/[33](wiring-autowire-candidate-selection-primary-priority-order.md)

对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionPhaseLabTest.java`

推荐断点（按创建链路顺序）：

- `AutowiredAnnotationBeanPostProcessor#determineCandidateConstructors` / `AbstractAutowireCapableBeanFactory#autowireConstructor`（constructor injection）
- `AbstractAutowireCapableBeanFactory#populateBean` / `AutowiredAnnotationBeanPostProcessor#postProcessProperties`（field/property injection）
## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（优先运行它们）：
- Lab：`SpringCoreBeansInjectionPhaseLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 验证补充（从实验现象出发）

## 复现入口（可运行）

- 入口测试（推荐先运行通再设置断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionPhaseLabTest.java`
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansInjectionPhaseLabTest test`

对应实验（可运行 + 可断言）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionPhaseLabTest.java`

```bash
mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansInjectionPhaseLabTest test
```

在实验里 `FieldInjectedTarget` 使用 field injection：

实验里 `ConstructorInjectedTarget` 同时提供：

本章的 Lab 额外加了一个“探针 BPP”，在 `postProcessProperties(...)` 里记录快照，帮助在断点里“观察到注入发生在这一段”。

## Debug / 观察建议

建议读者用断点把“阶段感”建立起来：

1. 在 `FieldInjectedTarget` 的构造器里设置断点：可以观察到依赖为 `null`
2. 在 `InjectionPhaseProbePostProcessor#postProcessProperties(...)` 设置断点：这是属性填充阶段的入口之一
3. 在 `FieldInjectedTarget#init(@PostConstruct)` 设置断点：可以观察到依赖已可用
4. 对照 `ConstructorInjectedTarget`：依赖在构造器内就已可用，并且会选择 `@Autowired` 构造器

## 源码锚点（建议从这里设置断点）

- `AutowiredAnnotationBeanPostProcessor#determineCandidateConstructors`：决定“用哪个构造器做 constructor injection”的关键入口
- `AbstractAutowireCapableBeanFactory#autowireConstructor`：构造器注入的核心路径（解析参数依赖并实例化）
- `AbstractAutowireCapableBeanFactory#populateBean`：属性填充阶段入口（field/method injection 的舞台）
- `InstantiationAwareBeanPostProcessor#postProcessProperties`：属性填充阶段的扩展点（`AutowiredAnnotationBeanPostProcessor` 正是靠它处理 field injection）
- `AutowiredAnnotationBeanPostProcessor#postProcessProperties`：`@Autowired/@Value` 等注解注入的直接入口（最适合设置断点）

## 断点闭环（用本仓库 Lab/Test 运行一次）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionPhaseLabTest.java`

建议断点（把“阶段感”走一遍即可）：

## 常见误区与边界

> 注意：**多个 BPP 的顺序会影响在 `postProcessProperties(...)` 里看到的 bean 状态**。
> 需要把重点放在“注入发生在属性填充阶段”这个结论上，而不是纠结某一个 BPP 是先还是后（顺序规则见第 14/25 章）。

## 常见误区与实践建议

本章本质是在讲：**容器通过 BPP 让注解“生效”**。这条线能直接解释 AOP/事务为何会出现“入口必须走代理”的误区：

## 面试常问（注入阶段：constructor vs field）

### Q1：构造器注入与字段注入分别发生在创建链路的哪一步？

- 标准答案（可复述）：
  - 构造器注入发生在实例化阶段（选构造器 → 解析参数依赖 → 创建实例）；字段/属性注入主要发生在 `populateBean` 阶段，由注解处理器（如 `AutowiredAnnotationBeanPostProcessor`）介入填充依赖。
- 证据链（方法级）：
  - `AbstractAutowireCapableBeanFactory#doCreateBean`
  - `AbstractAutowireCapableBeanFactory#autowireConstructor` / `#createBeanInstance`
  - `AbstractAutowireCapableBeanFactory#populateBean`
  - `AutowiredAnnotationBeanPostProcessor#postProcessProperties`
- 最小复现：
  - `SpringCoreBeansInjectionPhaseLabTest`

### Q2：为什么“在构造器里拿不到字段注入的值”不是 bug？

- 标准答案（可复述）：
  - 因为字段注入发生在实例创建之后（populate 阶段）；构造器执行时对象还没进入属性填充/注解处理器链路，字段自然还是默认值。
- 工程建议：
  - 必填依赖优先构造器注入（更早失败、可测试、不可变）；可选/延迟语义用 `ObjectProvider` 明确表达。

## 自检要点
- 应能够解释清楚：为什么 field injection 在构造器里一定是 `null` 吗？（提示：注入发生在 `populateBean` 阶段，不会倒流到构造器）
- 应能够解释清楚：constructor injection 为什么更适合“必填依赖”吗？（提示：更早失败 + 可测试 + 不可变）
- 应能够指出：`@Autowired` 的源码触发点在哪里吗？（提示：`AutowiredAnnotationBeanPostProcessor#postProcessProperties`）

## 小结

1) `FieldInjectedTarget` 构造器：观察此时 `@Autowired` 字段必然还是 `null`
2) `AutowiredAnnotationBeanPostProcessor#postProcessProperties`：观察容器在属性填充阶段为字段赋值
3) `FieldInjectedTarget#init(@PostConstruct)`：观察 init 阶段依赖已可用
4) `AutowiredAnnotationBeanPostProcessor#determineCandidateConstructors`：观察 constructor injection 为什么能在构造器内获取到依赖（先选构造器再解析参数）
5) `AbstractAutowireCapableBeanFactory#autowireConstructor`：观察构造器参数依赖的解析与实例化路径

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansInjectionPhaseLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansInjectionPhaseLabTest.java`

<!-- BOOKIFY:END -->
