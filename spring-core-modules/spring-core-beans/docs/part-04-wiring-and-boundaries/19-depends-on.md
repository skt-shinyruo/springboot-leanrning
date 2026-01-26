# 19. dependsOn：强制初始化顺序（即使没有显式依赖）

## 导读

- 本章主题：**19. dependsOn：强制初始化顺序（即使没有显式依赖）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansDependsOnLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java`

## 机制主线

`dependsOn` 是容器提供的一个“强行排序”能力：

- 它解决的是**初始化顺序**问题
- 不是依赖注入（DI）问题

## 1. 现象：两个互不依赖的 bean，也可以有固定初始化顺序

对应测试：

- `Second` 并不注入 `First`
- 但 `Second` 的 bean definition 声明了 `dependsOn("first")`

因此容器会：

- 先初始化 `first`
- 再初始化 `second`

## 2. 写法入口：`@DependsOn` / `BeanDefinition#setDependsOn(...)` / XML `depends-on`

`dependsOn` 本质是 **BeanDefinition 上的一段元数据**（最终体现在 `RootBeanDefinition#getDependsOn()`）。

因此它可以从多种“写法入口”进入容器，语义一致：

> 结论：你关注的不是“写法”，而是 `BeanDefinition` 上最终有没有这段 `dependsOn` 元数据。

## 3. 常见用法（了解即可，别滥用）

你可能会在一些“基础设施”里看到它：

- 需要确保某个初始化逻辑先跑（例如注册某些全局资源）

但一般业务代码不推荐依赖它，因为它会让依赖关系“隐形”。

## 5. 容器内部结构：`dependentBeanMap` / `dependenciesForBeanMap`

这一章你真正要“写进肌肉记忆”的不是 `dependsOn` 这个 API，而是容器内部那两张表（它们解释了很多“为什么顺序会这样”的问题）：

- `dependentBeanMap`：**谁依赖我**（key 是被依赖者，value 是依赖者集合）
  - 形态：`dependencyBeanName -> Set<dependentBeanName>`
- `dependenciesForBeanMap`：**我依赖谁**（key 是依赖者，value 是被依赖者集合）
  - 形态：`beanName -> Set<dependencyBeanName>`

你可以把它记成一个对称关系（非常好背）：

> 如果 A 依赖 B，那么：  
> `dependentBeanMap[B]` 里会出现 A，`dependenciesForBeanMap[A]` 里会出现 B。

- 依赖边比你手写的 dependsOn 多（这是正常的）
- 关闭顺序也会同时受到“dependsOn + 注入依赖”影响（这也是工程里不建议滥用 dependsOn 的原因之一）

## 6. 销毁顺序：为什么关闭时顺序“反过来”？

初始化阶段，`dependsOn` 看起来是“先 dependency 再 dependent”：

- 创建 `second` 之前先 `getBean("first")`

但销毁阶段，容器必须保证：**不要在依赖者还在用的时候先把依赖销毁掉**，因此会按依赖边做反向销毁：

- **先销毁 dependent（依赖者）**
- **再销毁 dependency（被依赖者）**

也就是说：

- `second dependsOn first`  
  ⇒ 关闭时会先销毁 `second`，再销毁 `first`

这条规则的“落点”就在 `dependentBeanMap` 上：销毁逻辑会先取 `dependentBeanMap["first"]`，先销毁所有依赖 `first` 的 bean。

## 6.1 交互：dependsOn 会强行拉起 lazy-init（`@Lazy` / `lazyInit`）

一个很容易误判的点是：

- 你把 dependency 标成了 `lazy-init`
- 但它仍然在启动（refresh）阶段被创建了

结论写死：

- `lazy-init` 的语义是：“**没人要它的时候**，容器不主动创建它”
- `dependsOn` 的语义是：“**创建当前 bean 之前**，必须先 `getBean(dep)`”

- `AbstractBeanFactory#doGetBean("dependent")` 读取 `dependsOn=["lazyDependency"]`
- 在创建 `dependent` 之前先执行 `getBean("lazyDependency")`（lazy 被强行拉起）
- `DefaultSingletonBeanRegistry#registerDependentBean("lazyDependency","dependent")` 记录依赖边（这条边也会影响销毁顺序）

很多误用来自把“顺序”当成“注入选择”。这两件事发生在 **不同阶段**：

- `dependsOn`：发生在 **创建前置**（`doGetBean` 里处理 `dependsOn`，先 `getBean(dep)` 再创建当前 bean）
- DI 候选选择：发生在 **注入解析**（`doResolveDependency` 里按 `@Qualifier/@Primary/@Priority` 等规则收敛）

所以：

- `dependsOn` 不能帮你“选中哪个候选”
- 它只是在“当前 bean 要开始创建之前”，强制确保某些 bean 已创建

- “我给 A dependsOn B，是不是就会把 B 注入给 A？” → 不是（回到本章开头）

相关章节：

- DI 收敛规则：[03](../part-01-ioc-container/014-03-dependency-injection-resolution.md)、[33](33-autowire-candidate-selection-primary-priority-order.md)

## 6.3 写入时机对照：`dependsOn` 这段元数据是何时写进 `BeanDefinition` 的？

你不需要背内部实现，但建议形成一个定位策略：**先判断当前 bean 的“注册入口”是哪一种**。

- `RootBeanDefinition#getDependsOn()`（定义上是否真的有这段元数据）

- `RootBeanDefinition#getDependsOn`：`dependsOn` 元数据来源（最终挂在 BeanDefinition 上）
- `AbstractBeanFactory#doGetBean`：创建 bean 之前会先处理 `dependsOn`（先 `getBean(dep)` 再创建当前 bean）
- `DefaultSingletonBeanRegistry#registerDependentBean`：记录依赖关系（影响销毁顺序、依赖图可观测性）
- `DefaultListableBeanFactory#preInstantiateSingletons`：非 lazy 单例批量创建期间也会触发 `dependsOn` 的顺序保证
- `DefaultSingletonBeanRegistry#isDependent`：容器用于判断依赖关系/避免重复依赖处理的辅助入口

入口：

1) `Second` / `First` 的构造器或 init 回调：观察最终的创建/初始化顺序
2) `RootBeanDefinition#getDependsOn`：确认 `second` 的定义上确实声明了 dependsOn
3) `AbstractBeanFactory#doGetBean`：观察创建 `second` 前会先触发 `getBean("first")`
4) `DefaultSingletonBeanRegistry#registerDependentBean`：观察容器把这条“隐式依赖”记录进依赖关系表

## 排障分流：这是定义层问题还是实例层问题？

`dependsOn` 的定位非常清晰：它是 **BeanDefinition 元数据（定义层）**，但它影响的是 **创建/销毁顺序（创建链路）**。

按现象快速分流：

- **定义层问题（本章优先）**：你“以为写了 dependsOn”，但顺序没变化
  - 先确认：`RootBeanDefinition#getDependsOn()` 里是否真的有目标 beanName（拼错/别名/覆盖都可能导致你以为生效但其实没写进去）
  - 典型断点：`RootBeanDefinition#getDependsOn` / `AbstractBeanFactory#doGetBean`（读 dependsOn 并触发 `getBean(dep)`）
- **创建链路问题（本章也覆盖）**：`@Lazy` 明明想延迟，但启动时依赖还是被创建了
  - 关键结论：`dependsOn` 会强制 `getBean(dep)`，因此能把 lazy 依赖“拉起”
  - 典型断点：`AbstractBeanFactory#doGetBean`（dependsOn 检查点）/ `DefaultListableBeanFactory#preInstantiateSingletons`
- **实例层问题（非 dependsOn 能解决）**：你想用 dependsOn “解决注入歧义/指定注入对象”
  - 结论：这是概念误用；注入选择看 `@Qualifier/@Primary/@Resource` 等规则（见 [03](../part-01-ioc-container/014-03-dependency-injection-resolution.md)、[33](33-autowire-candidate-selection-primary-priority-order.md)）
## 源码最短路径（call chain）

> 目标：给你“最短可跟栈”，把 dependsOn 如何写进依赖表、依赖表如何影响销毁顺序串起来。

初始化阶段（创建 `second` 时触发 `dependsOn("first")`）：

- `AbstractBeanFactory#doGetBean("second")`
  - `getMergedLocalBeanDefinition("second")`
  - `RootBeanDefinition#getDependsOn`（读出 `["first"]`）
  - `DefaultSingletonBeanRegistry#registerDependentBean("first", "second")`（把依赖边写进两张表）
  - `getBean("first")`（先确保 dependency 已创建）
  - `createBean("second")`（再创建当前 bean）

销毁阶段（关闭 context 时，先销毁 dependent 再销毁 dependency）：

- `DefaultSingletonBeanRegistry#destroySingletons`
  - `destroyBean("first")`
    - 读取 `dependentBeanMap["first"]` 得到 `{ "second", ... }`
    - 递归销毁所有 dependent（先销毁 `second`）
    - 再销毁 `first`

## 固定观察点（watch list）

建议在 `registerDependentBean(...)` 与 `destroySingletons(...)` 里 watch/evaluate：

- `dependentBeanMap`：谁依赖我（销毁顺序的关键依据）
- `dependenciesForBeanMap`：我依赖谁（用于查询/诊断也很直观）
- `dependentBeanMap.get("first")`：当前被依赖者有哪些 dependents（集合里出现谁就意味着“先销毁谁”）
- `dependenciesForBeanMap.get("second")`：当前 bean 依赖哪些 dependencies（集合里出现谁就意味着“创建前要先 getBean 谁”）

> 小技巧：很多依赖边是“创建过程中动态注册”的（不是你在代码里显式写出来的），所以看这两张表往往比“猜依赖”更可靠。

## 反例（counterexample）

**反例：我只写了 dependsOn，为什么依赖图里出现了更多依赖？为什么关闭顺序不像我想的那样“只按 dependsOn”？**

- 除了 `dependsOn`，容器在“解析引用/完成注入”时也会调用 `registerDependentBean(...)` 记录依赖边  
  ⇒ 依赖图里出现更多边，并不代表你写错了 dependsOn，而是容器在记录“真实被注入/被引用”的依赖。
- 因为销毁顺序依赖 `dependentBeanMap`，所以**关闭顺序会同时受 dependsOn 与真实注入依赖影响**  
  ⇒ 工程里滥用 dependsOn 会让拓扑更难解释（这也是本章一开始就强调“别滥用”的原因）。

## 7. 一句话自检

- 常问：`dependsOn` 能解决什么问题？能不能解决注入歧义？
  - 答题要点：`dependsOn` 只控制初始化顺序（谁先创建），不改变 DI 候选选择规则；注入歧义要用 `@Qualifier/@Primary` 或条件装配解决。
- 常见追问：什么时候必须用 `dependsOn`？
  - 答题要点：外部资源/生命周期强依赖（例如必须先初始化某个基础设施再创建业务 bean）时，用它强制顺序更直观。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansDependsOnLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java`

- `SpringCoreBeansDependsOnLabTest.dependsOn_forcesInitializationOrder_evenWithoutDirectDependencies()`

实验里：

- **注解方式：`@DependsOn("first")`**
  - 可用在 `@Component` 类上（扫描注册时写入定义）。
  - 也可用在 `@Bean` 方法上（配置类解析时写入定义）。
- **编程式注册：`BeanDefinition#setDependsOn(...)`**
  - 本仓库的实验用的就是这种（见 `SpringCoreBeansDependsOnLabTest`）。
- **XML：`<bean depends-on="first">`**
  - 现在工程里少见，但底层仍是同一套 `dependsOn` 语义。

- **坑 2：过度使用导致容器拓扑不透明**
  - 学习阶段可以用它做实验；工程里请谨慎。

- **坑 3：人为引入 dependsOn 环（A → B → A）会 fail-fast**
  - 这属于“定义层人为制造拓扑环”，容器一般不会替你“救场”。
  - 最小复现（建议断点跑一遍）：`SpringCoreBeansDependsOnLabTest.dependsOn_cycle_failsFast()`

更关键的一点：**这条依赖边不只来自 `dependsOn`**，还可能来自真实注入依赖（例如 `@Autowired`、`ref` 属性等）。
所以当你用 debugger 看依赖表时，经常会发现：

> 实用补充：除了看内部 map，你也可以直接用 API 做自检：
>
> - `beanFactory.getDependenciesForBean(beanName)`：我依赖谁（更像 `dependenciesForBeanMap` 的视角）
> - `beanFactory.getDependentBeans(beanName)`：谁依赖我（更像 `dependentBeanMap` 的视角）
>
> 如果你想把“依赖边”做成可观测闭环（候选集合 vs 最终依赖边），推荐先看 [11](../part-02-boot-autoconfig/019-11-debugging-and-observability.md) 的固定观察点。

因此：只要 dependent 被创建（refresh 预实例化 / 运行时 `getBean` 触发都算），dependency 就会被强行拉起——即使 dependency 是 lazy。

最小复现（可断言）：

- `SpringCoreBeansDependsOnLabTest.dependsOn_triggersLazyDependencyInstantiation()`

你在断点里应该看到什么（用于纠错）：

- `@DependsOn` 用在 `@Component/@Service` 类上：**扫描注册阶段**写入该类对应的 `BeanDefinition`
- `@DependsOn` 用在 `@Bean` 方法上：**配置类解析阶段**写入该 `@Bean` 方法生成的 `BeanDefinition`
- 编程式注册：`BeanDefinition#setDependsOn(...)`：**注册时就写入**（本仓库 Labs 使用这一种，最直观）

无论入口是哪一种，最终你都应该在 debugger 里用同一个观察点确认：

## 源码锚点（建议从这里下断点）

- `AbstractBeanFactory#doGetBean`：处理 `dependsOn` 的触发点（会先 getBean(dep) 再创建当前 bean）
- `AbstractBeanFactory#getDependsOn` / `RootBeanDefinition#getDependsOn`：依赖声明来源（定义层元数据）
- `DefaultSingletonBeanRegistry#registerDependentBean`：记录依赖关系（dependentBeanMap / dependenciesForBeanMap）
- `DefaultSingletonBeanRegistry#getDependentBeans`：排障时反查“谁依赖我”

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java`
  - `dependsOn_forcesInitializationOrder_evenWithoutDirectDependencies()`
  - `dependsOn_triggersLazyDependencyInstantiation()`（dependsOn × lazy-init）
  - `dependsOn_affectsDestroyOrder_viaDependentBeanMap()`（销毁顺序反序）
  - `dependsOn_cycle_failsFast()`（dependsOn 拓扑环）

建议断点：

- “我设置了 dependsOn，但顺序没变” → **优先定义层**：`dependsOn` 是否真的写进 `BeanDefinition`？beanName 是否写对？（看 `getDependsOn`）
- “我用 dependsOn 想让 `first` 自动注入到 `second`” → **不是注入问题，是概念误用**：dependsOn 只管顺序，不管 DI（回看本章开头）
- “启动/关闭顺序很怪，像是有隐式依赖” → **定义层 + 依赖关系问题**：排查 `dependsOn`/工厂内部注册的依赖（结合 [11](../part-02-boot-autoconfig/019-11-debugging-and-observability.md) 观察 bean 图）
- “出现依赖环（dependsOn A → B → A）” → **定义层问题**：这属于人为引入的拓扑环，建议回避而不是依赖容器“救场”

最小复现入口（建议你对比两个实验的输出/断点）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java`
  - `dependsOn_forcesInitializationOrder_evenWithoutDirectDependencies()`
- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java`
  - `dumpBeanGraph_candidatesAndRecordedDependencies_helpTroubleshootWhyItsInjected()`

你在断点里应该看到什么（用于纠错）：

- 你能解释清楚：dependsOn 解决的是什么问题？（初始化顺序，不是注入）
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java`
推荐断点：`DefaultSingletonBeanRegistry#registerDependentBean`、`DefaultListableBeanFactory#preInstantiateSingletons`、`AbstractBeanFactory#doGetBean`

## 常见坑与边界

### 常见坑

- **坑 1：把 dependsOn 当成 DI 手段**
  - dependsOn 不会把 `first` 注入到 `second` 里。
  - 你得到的只是“创建顺序先后”，不是“注入目标选择”。

### 坑 2：误以为 dependsOn 不会影响 lazy-init

- `@Lazy` 的语义是“没人要就不创建”，但 dependsOn 的语义是“创建当前 bean 前必须先 getBean(dep)”
- 因此 dependsOn 可以强行把 lazy 依赖拉起（本章已给出最小复现）

### 机制边界：为什么 dependsOn 不会影响 DI 候选选择？

把这个边界分清，你就能避免一个经典误判：

## 面试常问（`dependsOn` 的边界）

1) **dependsOn 解决什么问题？为什么说它不是 DI？**
   - 要点：它解决初始化/销毁顺序问题；它不参与候选选择与依赖解析，不会影响注入点拿到谁。

2) **dependsOn 为什么会让 lazy-init “看起来失效”？**
   - 要点：dependsOn 在 `doGetBean` 里被处理，创建当前 bean 前会先 `getBean(dep)`；因此 lazy 依赖会被强制实例化。

3) **关闭时为什么顺序会“反过来”？**
   - 要点：销毁要保证“先销毁依赖者再销毁被依赖者”；容器依据 `dependentBeanMap` 递归销毁 dependents，最后才销毁 dependency。
## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBeanGraphDebugLabTest` / `SpringCoreBeansDependsOnLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansDependsOnLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanGraphDebugLabTest.java`

上一章：[18. @Lazy 的真实语义：延迟的是谁、延迟到哪一步](023-18-lazy-semantics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[20. registerResolvableDependency：能注入但它不是 Bean](20-resolvable-dependency.md)

<!-- BOOKIFY:END -->
