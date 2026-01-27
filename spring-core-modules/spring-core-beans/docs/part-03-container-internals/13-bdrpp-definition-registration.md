# 13. BeanDefinitionRegistryPostProcessor：在“注册阶段”动态加定义

## 导读

- 本章主题：**13. BeanDefinitionRegistryPostProcessor：在“注册阶段”动态加定义**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansRegistryPostProcessorLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRegistryPostProcessorLabTest.java`

## 机制主线

这一章聚焦一个比 BFPP 更“早、更强”的扩展点：

- `BeanDefinitionRegistryPostProcessor`（简称 BDRPP）

## 1. 心智模型：先有“定义”，后有“实例”

容器启动阶段的关键流程可以粗略理解为：

BDRPP 的价值在于：它可以在 **第 1 步和第 2 步之间** 动态注册新的 `BeanDefinition`。

## 2. 现象：你没有显式注册 bean，但它依然出现了

- 你只注册了一个 BDRPP
- BDRPP 在 `postProcessBeanDefinitionRegistry(...)` 里注册了 `registeredBean` 的定义
- refresh 之后，你能直接 `getBean(RegisteredBean.class)`

这说明：**BDRPP 能把“定义”塞进容器，从而让 bean 真正成为容器的一部分**。

## 3. 顺序：BDRPP 先于普通 BFPP

- BDRPP 先注册一个 `BeanDefinition`
- 之后 BFPP 再修改这个 `BeanDefinition` 的属性
- 最终实例化出的对象反映了 BFPP 的修改结果

你应该记住：

- **BDRPP 能“新增定义”**
- **BFPP 更常见的用途是“修改定义”**

- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：refresh 早期调用链入口（BDRPP/BFPP 的统一调度点）
- `BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry`：BDRPP 的“注册阶段”入口（新增/改名/批量注册定义）
- `DefaultListableBeanFactory#registerBeanDefinition`：真正把 `BeanDefinition` 放进 registry 的地方（可观察同名冲突/覆盖策略）
- `BeanFactoryPostProcessor#postProcessBeanFactory`：普通 BFPP 的入口（通常用于修改已有定义）
- `DefaultListableBeanFactory#preInstantiateSingletons`：定义阶段结束后，非 lazy 单例通常从这里开始批量创建

入口：

- 最小复现入口（方法级）：
  - `SpringCoreBeansRegistryPostProcessorLabTest.beanDefinitionRegistryPostProcessor_canRegisterNewBeanDefinitions()`
  - `SpringCoreBeansRegistryPostProcessorLabTest.bdrppRunsBeforeRegularBeanFactoryPostProcessor()`
- 推荐断点（闭环版）：
  1) `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：BDRPP/BFPP 的统一调度入口（定义层）
  2) `BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry`：BDRPP 注册定义发生点（看注册了哪些 beanName）
  3) `DefaultListableBeanFactory#registerBeanDefinition`：真正写入 registry（看覆盖/冲突/beanDefinitionNames）
  4) `BeanFactoryPostProcessor#postProcessBeanFactory`：普通 BFPP 修改定义发生点（看它如何改到 BDRPP 注册的定义）
  5) `DefaultListableBeanFactory#preInstantiateSingletons`：定义稳定后才进入实例化（验证“先定义、后实例”）

## 排障分流：这是定义层问题还是实例层问题？

你可以用一句话把本章的适用范围钉死：

> **只要问题是“这个 BeanDefinition 到底从哪里来的/为什么会出现/为什么会被改写”，就优先回到 BDRPP/BFPP（定义层）。**

具体分流：

- **定义层（本章）**：Bean 根本没注册、注册数量不对、beanName 冲突/覆盖、BeanDefinition 元数据不符合预期
  - 典型断点：`PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`、`BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry`、`DefaultListableBeanFactory#registerBeanDefinition`
- **实例层（非本章）**：Bean 有了，但注入不对/变成 proxy/生命周期回调顺序奇怪
  - 典型断点：`PostProcessorRegistrationDelegate#registerBeanPostProcessors`、`AbstractBeanFactory#doGetBean`、`AbstractAutowireCapableBeanFactory#doCreateBean`
  - 对应章节：[14](14-post-processor-ordering.md)、[15](15-pre-instantiation-short-circuit.md)、[31](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)
## 源码最短路径（call chain）

> 目标：当你想回答“这个 bean 为什么会出现（我明明没注册）”或“为什么 BFPP 能改到 BDRPP 注册的定义”时，用最短调用链把问题钉在 refresh 的精确阶段。

从 `refresh()` 进入“注册阶段”的最短主干（只列关键节点）：

把它记成一句话：

- **BDRPP/BFPP 都发生在 refresh 的“前半段”（定义层）**，而 BPP 注册与单例实例化发生在后面。

如果你在调用栈里看到了：

- `invokeBeanFactoryPostProcessors` → `postProcessBeanDefinitionRegistry`
  那你处理的是“定义从哪里来的”问题（本章）
- 如果你要追“最初的定义入口”（扫描/`@Bean`/`@Import`/registrar），先回到 [02](../part-01-ioc-container/02-bean-registration.md)
- `registerBeanPostProcessors` / `preInstantiateSingletons` / `doCreateBean`
  那你处理的是“实例如何被创建/被包装”问题（见 [14](14-post-processor-ordering.md)、[25](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)、[31](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)）

## 固定观察点（watch list）

### 1) 看 BDRPP 是否真的把定义放进了 registry

在 `BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry` 里建议 watch/evaluate：

- `registry.containsBeanDefinition("registeredBean")`（或你的目标 beanName）
- `registry.getBeanDefinition("registeredBean")`（定义的关键字段：class/parentName/propertyValues/scope）

在 `DefaultListableBeanFactory#registerBeanDefinition` 里建议 watch/evaluate：

- `beanName`：当前正在注册的名字
- `this.beanDefinitionMap.containsKey(beanName)`：是否覆盖/冲突
- `this.beanDefinitionNames.size()`：注册前后是否变化（定义是否真的进来了）

### 2) 看 BDRPP/BFPP 的调度顺序（为什么“BDRPP 更早”）

在 `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` 里建议 watch/evaluate：

- `processedBeans`：哪些 post-processor 已处理（避免重复执行）
- `registryPostProcessors` / `regularPostProcessors`（概念上）：BDRPP 分组与 BFPP 分组

### 3) 看 BFPP 是否改到了 BDRPP 注册的定义

在你的 BFPP（`BeanFactoryPostProcessor#postProcessBeanFactory`）里建议 watch/evaluate：

- `beanFactory.containsBeanDefinition("registeredBean")`：确认 BDRPP 注册的定义已经存在
- `beanFactory.getBeanDefinition("registeredBean").getPropertyValues()`：确认 BFPP 对定义的修改是否生效
- （对照）`beanFactory.getBeanDefinitionNames().length`：定义数量是否随 BDRPP 增长

## 反例（counterexample）

**反例：我在 BDRPP/BFPP 阶段调用 `getBean()`，结果某些 BPP/代理/回调“神秘失效”或顺序变得反直觉。**

这类问题的本质是：你把“应该在实例阶段发生的创建”提前到了“处理器阶段”。

- 在 `BeanDefinitionRegistryPostProcessor#postProcessBeanFactory` 里调用 `beanFactory.getBean("earlyTarget")`
  - 此时 `PostProcessorRegistrationDelegate#registerBeanPostProcessors` 还没跑
  - `beanFactory.getBeanPostProcessors()` 里还没有你的 `BeanPostProcessor`
  - **所以 `earlyTarget` 会在“没有 BPP 的世界”里被创建出来**
- refresh 后半段注册了 BPP，但已经太晚：
  - `earlyTarget` 已经在单例缓存里，`preInstantiateSingletons` 不会再重建它
  - 最终你看到：同一个容器里有的 bean 被 BPP 处理了，有的没有（非常反直觉）

这就是为什么很多框架/基础设施会强调：

- BDRPP/BFPP 阶段尽量只处理“定义”，不要拿“实例”（需要实例层行为时，把逻辑放到 BPP/SmartInitializingSingleton 等更合适的阶段）

## 5. 一句话自检

- 常问：BDRPP 和 BFPP 的本质差别是什么？为什么说 BDRPP 更“早、更强”？
  - 答题要点：BDRPP 能在 registry 阶段新增/改名/批量注册 `BeanDefinition`；BFPP 更常用于修改已有定义；二者都发生在 refresh 前半段（定义层）。
- 常见追问：为什么不建议在 BDRPP/BFPP 里 `getBean()`？
  - 答题要点：会触发过早实例化，导致后续 BPP 来不及介入/顺序变得反直觉，最终出现“同一容器里有的 bean 被处理、有的没被处理”。
- 常见追问：你如何用断点证明“某个 bean 是 BDRPP 动态注册出来的”？
  - 答题要点：在 `postProcessBeanDefinitionRegistry` 与 `registerBeanDefinition` 加条件断点（beanName），确认定义进入 registry 的时机与来源。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansRegistryPostProcessorLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRegistryPostProcessorLabTest.java`

1) 收集/注册 `BeanDefinition`
2) 运行 post-processors（可能改定义、加定义）
3) 实例化单例 bean（`preInstantiateSingletons`）

`SpringCoreBeansRegistryPostProcessorLabTest.beanDefinitionRegistryPostProcessor_canRegisterNewBeanDefinitions()` 里：

你应该看到：只注册了 BDRPP，但 refresh 后目标 bean 已可 `getBean`（因为定义在注册阶段被动态加入 registry）。

`SpringCoreBeansRegistryPostProcessorLabTest.bdrppRunsBeforeRegularBeanFactoryPostProcessor()` 里展示：

你应该看到：BDRPP 先注册定义，随后 BFPP 才能拿到并修改该定义（最终实例反映 BFPP 的修改）。

## 源码锚点（建议从这里下断点）

- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`（定义层算法入口：分段执行 + 反复扫描）
- `BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry`（动态注册定义的主入口）
- `DefaultListableBeanFactory#registerBeanDefinition`（registry 写入点：冲突/覆盖/beanDefinitionNames）
- `BeanFactoryPostProcessor#postProcessBeanFactory`（定义修改入口）
- `DefaultListableBeanFactory#preInstantiateSingletons`（定义稳定后批量实例化单例）

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRegistryPostProcessorLabTest.java`
  - `beanDefinitionRegistryPostProcessor_canRegisterNewBeanDefinitions()`
  - `bdrppRunsBeforeRegularBeanFactoryPostProcessor()`

建议断点：

1) 你在 Lab 里实现的 BDRPP：`postProcessBeanDefinitionRegistry(...)`（观察：这里注册了哪些 beanName/定义）
2) `DefaultListableBeanFactory#registerBeanDefinition`（观察：注册时机在 refresh 早期，且会做冲突/覆盖检查）
3) 你在 Lab 里实现的 BFPP：`postProcessBeanFactory(...)`（观察：它能拿到并修改 BDRPP 刚注册的定义）
4) `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`（观察：为什么 BDRPP 能先于普通 BFPP）
5) （可选）`DefaultListableBeanFactory#preInstantiateSingletons`（观察：定义注册/修改完成后才进入实例化阶段）

- “我没显式注册，但某个 bean 却出现了/多了很多 bean” → **优先定义层**：是否有 BDRPP/registrar 在动态注册定义？（本章 Lab）
- “我动态注册的 bean 找不到/没进容器” → **优先定义层**：`postProcessBeanDefinitionRegistry` 是否被调用？是否真的 `registerBeanDefinition` 成功？
- “bean 在，但属性/构造参数不符合预期” → **优先定义层（修改定义）**：BFPP 是否在 BDRPP 之后运行、是否覆盖了定义元数据？（对照本章第 3 节）
- “在 post-processor 阶段 `getBean()` 引发奇怪顺序/代理缺失” → **优先实例层的时机问题**：你可能触发了过早实例化，导致后续 BPP 来不及介入（对照 [14](14-post-processor-ordering.md)、[25](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)）

> 目标：在 debugger 里只看少数几个结构/变量，就能确认“定义到底有没有被注册进去、注册发生在哪、后续有没有被改”。

- `beanFactory.getBeanDefinition("registeredBean").getPropertyValues()`：是否已被修改
- 对照断点：`DefaultListableBeanFactory#preInstantiateSingletons`（确认：修改发生在实例化之前）

最小复现入口（必现）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRegistryPostProcessorLabTest.java`
  - `getBeanDuringPostProcessing_instantiatesTooEarly_andSkipsLaterBeanPostProcessors()`

你在断点里应该看到什么（用于纠错）：

断点建议（把反例看“实”）：

1) 反例 BDRPP 的 `postProcessBeanFactory(...)`：看 `getBean("earlyTarget")` 的调用栈来自 refresh 前半段
2) `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：确认 BPP 是在这一步才进入 `beanFactory.getBeanPostProcessors()` 的
3) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`：对 `earlyTarget/lateTarget` 加条件断点，观察谁命中、谁没命中

- 你能解释清楚：为什么 BFPP 能修改 BDRPP 注册的定义？（提示：因为 BDRPP 更早）
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRegistryPostProcessorLabTest.java`
推荐断点：`PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`、`BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry`、`DefaultListableBeanFactory#registerBeanDefinition`

## 常见坑与边界

### 常见坑

- **坑 1：在 BDRPP/BFPP 里 `getBean()` 触发提前实例化**
  - post-processor 阶段本质是“定义层”的工作。
  - 如果你在这里强行拿实例，可能导致：
    - 某些 BPP 没机会介入
    - 生命周期顺序变得反直觉

- **坑 2：beanName 冲突**
  - BDRPP 动态注册时必须保证名称唯一，否则会覆盖或直接报错（取决于容器配置）。

- **坑 3：以为 `@Order` 会改变“分组”（PriorityOrdered/Ordered/others）**
  - 分组本质上看接口类型，不看注解；`@Order` 只影响“组内排序”（且前提是它确实进入了会被 sort 的列表）。
  - 对应章节：[14](14-post-processor-ordering.md)

- **坑 4：把“注解生效”误认为是 BDRPP 自己完成的**
  - BDRPP 负责把注解世界翻译成 BeanDefinition（图扩张），但 `@Autowired/@PostConstruct/@Resource` 这类行为依赖 BPP 在创建阶段介入。
  - 对应章节：[022-12](022-12-container-bootstrap-and-infrastructure.md)

- `AbstractApplicationContext#refresh`
  - `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
    - （内部）收集并实例化 `BeanDefinitionRegistryPostProcessor` beans
    - `BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry(registry)`
      - **在这里动态注册/修改 `BeanDefinition`**
      - `DefaultListableBeanFactory#registerBeanDefinition(beanName, beanDefinition)`（真正写入 registry）
    - `BeanDefinitionRegistryPostProcessor#postProcessBeanFactory(beanFactory)`
      - **注意：这仍然属于“定义阶段/处理器阶段”，不是实例阶段**
    - `BeanFactoryPostProcessor#postProcessBeanFactory(beanFactory)`（普通 BFPP）
      - **在这里修改 BDRPP 刚注册进去的定义**
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`（BPP 注册在这里发生）
  - `DefaultListableBeanFactory#preInstantiateSingletons`（批量创建非 lazy 单例从这里开始）

## 面试常问（BDRPP：定义注册为什么这么关键）

### Q1：BDRPP 和 BFPP 的本质差异是什么？为什么 BDRPP 能“注册更多定义”？

- 标准答案（可复述）：
  - BDRPP 作用于 registry（定义注册表），能新增/删除/修改 `BeanDefinition`；BFPP 作用于 BeanFactory（已存在定义），通常做“修改定义元数据”。因此 BDRPP 的影响面更大、发生更早。
- 证据链（方法级）：
  - `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
  - `BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry`
  - `DefaultListableBeanFactory#registerBeanDefinition`
- 最小复现：
  - `SpringCoreBeansRegistryPostProcessorLabTest`

### Q2：为什么“在 post-processor 阶段调用 getBean()”会引发时序错乱？

- 标准答案（可复述）：
  - 因为这会触发过早实例化，导致某些 BPP 还没注册就有 bean 先“出生”，后续增强链无法 retroactively 生效，表现为代理/注解处理缺失或顺序反直觉。
- 证据链（方法级）：
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`（BPP 链在这里才完整）
  - `BeanPostProcessorChecker`（典型提示信号）
- 最小复现：
  - `SpringCoreBeansRegistryPostProcessorLabTest#getBeanDuringPostProcessing_instantiatesTooEarly_andSkipsLaterBeanPostProcessors`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansRegistryPostProcessorLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRegistryPostProcessorLabTest.java`

上一章：[12. 容器启动与基础设施处理器：为什么注解能工作](022-12-container-bootstrap-and-infrastructure.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序](14-post-processor-ordering.md)

<!-- BOOKIFY:END -->
