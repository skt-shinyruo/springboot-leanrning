# 14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序

## 导读

- 本章主题：**14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java`

## 机制主线

### 默认基础设施处理器（为什么它们的顺序很重要）

| 处理器 | 作用 | 层级 |
| --- | --- | --- |
| `ConfigurationClassPostProcessor` | 解析配置类、扩张定义 | 定义层（BDRPP/BFPP） |
| `AutowiredAnnotationBeanPostProcessor` | `@Autowired/@Value` 注入 | 实例层（BPP） |
| `CommonAnnotationBeanPostProcessor` | `@PostConstruct/@PreDestroy` | 实例层（BPP/DestructionAware） |
| `ApplicationContextAwareProcessor` | Aware 回调 | 实例层（BPP） |

## 1. 规则总览（记住这三层就够）

Spring 在同一类 post-processor 内，常用的排序规则是：

1) `PriorityOrdered`（最优先）
2) `Ordered`
3) 没实现顺序接口（最后）

> 这套规则适用于 BFPP 与 BPP（以及很多“插件式扩展点”）。

### 1.2 一个具体例子：顺序改变最终对象形态

当多个 BPP 都可能“包裹/替换”对象时（如多层代理），顺序决定最终暴露形态。  
对应用例：`SpringCoreAopMultiProxyStackingLabTest`（观察代理栈叠加顺序）。

## 1.1 源码解析：真正参与排序的“不是接口名”，而是 comparator 的比较规则

你在脑子里需要同时放下 2 个概念（后面会反复用到）：

1) **分组**：`PriorityOrdered` / `Ordered` / others（三段分组是“宏观规则”）
2) **组内排序**：比较 `order` 值（`getOrder()` / `@Order` / `@Priority`）（这是“微观规则”）

`PriorityOrdered` 与 `Ordered` 的意义不只是“标签”，而是会直接改变执行/注册阶段的分段流程；而在同一段内部，最终顺序取决于 comparator 计算出来的 order 值。

### 1.1.1 `AnnotationAwareOrderComparator`：order 值解析规则（精简伪代码）

Spring 里最常见的 comparator 是 `AnnotationAwareOrderComparator`，它是 `OrderComparator` 的增强版：在 `Ordered` 之外，还会读取 `@Order` 与 `@Priority`。

精简伪代码（只保留最稳定的规则）：

你只要记住两个结论就够：

- **`Ordered#getOrder()` 比注解更强**：实现了接口就以接口为准
- **order 值越小越靠前**：`HIGHEST_PRECEDENCE` 最靠前，`LOWEST_PRECEDENCE` 最靠后

### 1.1.2 Spring 到底用哪个 comparator 排序？

在 `PostProcessorRegistrationDelegate#sortPostProcessors` 里，Spring 的策略是：

1) 如果 `beanFactory` 是 `DefaultListableBeanFactory` 且设置了 `dependencyComparator` → 用它
2) 否则回退到 `OrderComparator.INSTANCE`（只认 `Ordered/PriorityOrdered`，**不认 `@Order`**）

因此如果你在一个“只用 BeanFactory、不走 ApplicationContext”的极简场景里发现 `@Order` 不生效，通常不是你记错了规则，而是你根本没用到 `AnnotationAwareOrderComparator`。

- 对 BFPP/BDRPP/BPP 来说，`PostProcessorRegistrationDelegate` 的“分段”判断是按 **接口类型**（`PriorityOrdered/Ordered`）做的
- **`@Order` 本身不会把你归到 Ordered 段**：如果你既没实现 `Ordered`，也没进入任何会被 sort 的列表，那么 `@Order` 再小也不会影响执行顺序

## 2. BFPP 的顺序：先改谁的定义？

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java`
  - `beanFactoryPostProcessors_areInvokedInPriorityOrderedThenOrderedThenUnorderedOrder()`（分段：PriorityOrdered → Ordered → others）
  - `beanFactoryPostProcessors_withDifferentOrderValues_areSortedAscendingWithinOrderedGroup()`（组内：order 值越小越靠前）

它只断言我们自己注册的三个 BFPP 的相对顺序：

- `bfpp:priority` → `bfpp:ordered` → `bfpp:unordered`

这样做的原因是：

- 容器内部也可能有自己的处理器
- 断言内部处理器的完整顺序容易随版本变化而变得不稳定

学习重点：**你能控制你自己的扩展点顺序**。

### 2.1 源码解析：`invokeBeanFactoryPostProcessors` 的分段执行算法（精简伪代码）

本章不是让你去背源码，而是让你能回答一个“工程上最关键”的问题：

> **为什么 Spring 要用多轮扫描 + 多段列表，而不是“一次性收集→一次性排序→一次性执行”？**

答案就在源码注释里（原话大意）：为了严格遵守 `PriorityOrdered/Ordered` 的契约，**不能在错误的时机 `getBean()` 把 processor 实例化出来**，也不能把它们以错误顺序注册进容器。

精简伪代码（突出“分段 + 防重复 + BDRPP 循环发现”的结构）：

```text
invokeBeanFactoryPostProcessors(beanFactory, externalBfpps):
  processed = set() // 记录 beanName，避免重复执行

  if beanFactory is BeanDefinitionRegistry:
    // A) 先处理 external processors（手工 add 进来的）
    externalRegistryProcessors = []
    externalRegularProcessors = []
    for pp in externalBfpps:
      if pp is BDRPP:
        pp.postProcessBeanDefinitionRegistry(registry)
        externalRegistryProcessors.add(pp)
      else:
        externalRegularProcessors.add(pp)

    // B) 再处理作为 bean 定义存在的 BDRPP：PriorityOrdered -> Ordered -> others
    //    关键点：每一段都可能“注册新的 BDRPP”，所以需要循环扫描直到稳定
    repeat:
      current = find BDRPP names not in processed and is PriorityOrdered
      instantiate(current); sort(current); invoke postProcessBeanDefinitionRegistry
      processed.addAll(current)
    until stable
    repeat Ordered...
    repeat others...

    // C) registry 阶段结束后，统一调用所有 BDRPP 的 postProcessBeanFactory
    invoke postProcessBeanFactory on all BDRPPs invoked so far
    invoke postProcessBeanFactory on externalRegularProcessors

  else:
    invoke postProcessBeanFactory on externalBfpps

  // D) 最后处理普通 BFPP：PriorityOrdered -> Ordered -> others
  names = getBeanNamesForType(BFPP)
  split names into (PriorityOrdered, Ordered, others), skip processed
  instantiate + sort each group, then invoke postProcessBeanFactory
```

你从这段伪代码应该得到两个“顺序的本质”：

- **顺序的第一性来源是“分段执行”**：`PriorityOrdered` 的那一段一定比 `Ordered` 更早
- **第二性来源是“组内排序”**：组内由 comparator 决定（order 越小越靠前；是否认 `@Order` 取决于 comparator）

## 3. BPP 的顺序：谁先“动手”改实例？

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java`
  - `beanPostProcessors_areAppliedInPriorityOrderedThenOrderedThenUnorderedOrder()`（分段：PriorityOrdered → Ordered → others）
  - `beanPostProcessors_withDifferentOrderValues_areSortedAscendingWithinOrderedGroup()`（组内：`getOrder()` 升序）
  - `beanPostProcessors_annotatedWithOrderButNotOrdered_areNotSorted_andFollowRegistrationOrder()`（误解：`@Order` 不会把你“变成 Ordered”）

同样只断言我们自己注册的三个 BPP 的相对顺序。

学习重点：

- 多个 BPP 对同一个 bean 做增强时，“顺序”是结果的一部分。

### 3.1 源码解析：`registerBeanPostProcessors` 先排序再注册，但最后还会“挪动 internal BPP”

`registerBeanPostProcessors` 的关键不是“执行 BPP”（执行发生在 bean 创建时），而是：

1) **决定 BPP 列表的最终顺序**（`beanFactory.getBeanPostProcessors()` 的 list 顺序）
2) **在 refresh 中前段把它们注册好**，确保后续 bean 创建能走完整链路

精简伪代码（突出“分组注册 + internal BPP 重新注册到最后”）：

这段逻辑直接解释两个常见现象：

- 为什么“顺序”会影响代理/包装的叠加结果：因为 **BPP list 的顺序就是调用顺序**
- 为什么有些 BPP 看起来“明明 PriorityOrdered 但又在后面”：因为它可能属于 internal BPP，被最后重新注册挪到了尾部（它仍然会在 internal 组内按 order 排序）

## 4. 常见误解

- **误解：`@Order` 能影响单依赖注入的选择**
  - 单个依赖（注入一个 `T`）的选择通常看：`@Primary`、`@Qualifier`、beanName 等。
  - `@Order` 更常见的影响是：集合注入（`List<T>`）、拦截链、处理器链。

- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：BFPP/BDRPP 的执行入口（排序与分组都在这一段完成）
- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：BPP 的注册入口（这里决定“谁先作用于实例”）
- `AnnotationAwareOrderComparator#sort`：Spring 常用的排序器（会综合 `PriorityOrdered/Ordered/@Order/@Priority` 等信息）
- `Ordered#getOrder`：顺序接口的关键语义点（数字越小通常越靠前）
- `DefaultListableBeanFactory#addBeanPostProcessor`：BPP 最终进入 BeanFactory 的地方（注册顺序会影响调用顺序）

入口：

- 最小复现入口（方法级）：
  - `SpringCoreBeansPostProcessorOrderingLabTest.beanFactoryPostProcessors_areInvokedInPriorityOrderedThenOrderedThenUnorderedOrder()`
  - `SpringCoreBeansPostProcessorOrderingLabTest.beanPostProcessors_areAppliedInPriorityOrderedThenOrderedThenUnorderedOrder()`
  - （误区对照）`SpringCoreBeansPostProcessorOrderingLabTest.beanPostProcessors_annotatedWithOrderButNotOrdered_areNotSorted_andFollowRegistrationOrder()`
- 推荐断点（闭环版）：
  1) `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`（定义层：BFPP/BDRPP 的分段执行与排序）
  2) `PostProcessorRegistrationDelegate#sortPostProcessors`（排序入口：看使用哪个 comparator）
3) `PostProcessorRegistrationDelegate#registerBeanPostProcessors`（实例层：BPP 的分段注册与最终顺序）
4) `DefaultListableBeanFactory#addBeanPostProcessor`（BPP 进入 `beanFactory.getBeanPostProcessors()` 的最终写入点）
5) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`（对照：BPP 的“注册顺序”如何变成“执行顺序”）

## 可复现闭环（基于 `SpringCoreAopMultiProxyStackingLabTest`）

跑完该 Lab，你至少要能复述 3 条结论：

1) **分段规则决定“谁先执行”**  
   - 断点：`sortPostProcessors`  
   - 断言：`PriorityOrdered` 永远早于 `Ordered`
2) **组内排序决定“代理叠加顺序”**  
   - 断点：`addBeanPostProcessor`  
   - 断言：顺序不同导致代理链叠加顺序不同
3) **`@Order` 不等于 `Ordered`**  
   - 断点：`sortPostProcessors`（观察 comparator）  
   - 断言：未实现 `Ordered` 的 BPP 仍可能按注册顺序执行

## 排障分流：这是定义层问题还是实例层问题？

你可以先用“这到底影响什么”来判断要追哪条链路：

- **定义层（BFPP/BDRPP）顺序问题**：你看到的是“BeanDefinition 元数据/占位符/条件/扫描结果”不符合预期
  - 典型落点：`invokeBeanFactoryPostProcessors`（先分段再排序再执行）
- **实例层（BPP）顺序问题**：你看到的是“代理叠加顺序/回调顺序/注入增强”不符合预期
  - 典型落点：`registerBeanPostProcessors`（决定 BPP 列表顺序）+ `applyBeanPostProcessors*`（把顺序变成最终对象形态）
- **手工注册导致的“顺序失效”**：你在代码里 `addBeanPostProcessor`，但期待 `Ordered/@Order` 生效
  - 典型落点：不经过 `registerBeanPostProcessors` 的排序流程，执行顺序只看“谁先 add”
  - 对应章节：[25](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)
## 源码最短路径（call chain）

> 目标：当你怀疑“顺序导致结果反直觉”时，用最短调用链把问题归位：到底是 **BFPP（定义层）** 的顺序，还是 **BPP（实例层）** 的顺序？

容器启动主链路（只列最关键节点）：

- 你要看“谁先改定义” → 去 `invokeBeanFactoryPostProcessors`
- 你要看“谁先包/谁后包（代理叠加顺序）” → 去 `registerBeanPostProcessors` + `applyBeanPostProcessorsAfterInitialization`

## 固定观察点（watch list）

在 `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` 里建议 watch/evaluate：

- `processedBeans`（或同等含义的集合）：哪些 processor 已经处理过（避免重复执行）
- “三组 processor 集合”（概念上）：`PriorityOrdered` / `Ordered` / others 的分组结果
  - 你不必强记变量名，但要确认：同一类 processor 是否被按三段执行

在 `PostProcessorRegistrationDelegate#registerBeanPostProcessors` 里建议 watch/evaluate：

- `beanFactory.getBeanPostProcessors()`：**最终 BPP 列表（顺序就是执行顺序）**
- `internalPostProcessors`（概念上）：容器会把一些 internal BPP 放到最后重新注册（这会影响“包裹顺序”）

> 小技巧：你只要把 `beanFactory.getBeanPostProcessors()` 的顺序看清楚，很多“为什么代理是这样叠加的”就不再神秘了。

## 反例（counterexample）

**反例：我明明让 BPP 实现了 `PriorityOrdered/Ordered`，为什么顺序还是不生效？**

- 手工 `beanFactory.addBeanPostProcessor(...)` 注册的 BPP
  - **不会**走 `PostProcessorRegistrationDelegate#registerBeanPostProcessors` 的排序流程
  - 执行顺序只按“注册顺序”，不是按 `Ordered`
- 因此你会看到：`beanFactory.getBeanPostProcessors()` 里手工注册的 BPP 在更前面
  ⇒ 最终包裹/增强顺序也跟着变（很多“反直觉”就是从这里来的）

把这个反例看懂，你就能把两个顺序体系彻底分开：

- “容器自动发现 + 排序”体系：见本章（`registerBeanPostProcessors`）
- “手工注册绕过排序”体系：见 [25](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)

## 5. 一句话自检

- 常问：`PriorityOrdered/Ordered/@Order` 三者谁更“强”？为什么？
  - 答题要点：分段规则按接口（PriorityOrdered/Ordered/others）决定；组内才按 order 值排序；`Ordered#getOrder()` 通常强于注解；`@Order` 是否生效取决于 comparator。
- 常见追问：为什么我写了 `@Order`，但 post-processor 顺序没变？
  - 答题要点：`@Order` 不是“接口”，不会把你放进 Ordered 段；并且如果容器没使用 `AnnotationAwareOrderComparator`，也可能不会读注解。
- 常见追问：为什么手工 `addBeanPostProcessor(...)` 的顺序看起来“不听 Ordered”？
  - 答题要点：手工注册绕过 `PostProcessorRegistrationDelegate#registerBeanPostProcessors` 的排序流程；最终顺序就是注册顺序（见 [25](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)）。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

当容器里存在多个 BFPP/BPP 时，“谁先运行”会直接决定最终结果。

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java`

本模块提供了一个“反直觉但很重要”的可复现反例（只用 `@Order`，不实现 `Ordered`）：
`SpringCoreBeansPostProcessorOrderingLabTest.beanPostProcessors_annotatedWithOrderButNotOrdered_areNotSorted_andFollowRegistrationOrder()`

- `SpringCoreBeansPostProcessorOrderingLabTest.beanFactoryPostProcessors_areInvokedInPriorityOrderedThenOrderedThenUnorderedOrder()`
- `SpringCoreBeansPostProcessorOrderingLabTest.beanFactoryPostProcessors_withDifferentOrderValues_areSortedAscendingWithinOrderedGroup()`

- `SpringCoreBeansPostProcessorOrderingLabTest.beanPostProcessors_areAppliedInPriorityOrderedThenOrderedThenUnorderedOrder()`
- `SpringCoreBeansPostProcessorOrderingLabTest.beanPostProcessors_withDifferentOrderValues_areSortedAscendingWithinOrderedGroup()`

## 源码锚点（建议从这里下断点）

- `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：BFPP/BDRPP 的分组 + 排序 + 多轮扫描算法
- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：BPP 的排序与注册时机（影响后续所有 bean 的创建）
- `AnnotationAwareOrderComparator#sort`：排序器入口（`PriorityOrdered` / `Ordered` / `@Order` 的差异在这里体现）
- `DefaultListableBeanFactory#addBeanPostProcessor`：手工注册 BPP 的路径（绕过排序，顺序只看注册先后）

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java`
  - `beanFactoryPostProcessors_areInvokedInPriorityOrderedThenOrderedThenUnorderedOrder()`
  - `beanPostProcessors_areAppliedInPriorityOrderedThenOrderedThenUnorderedOrder()`

建议断点：

1) `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`：观察 BFPP 分组与排序（PriorityOrdered → Ordered → others）
2) `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：观察 BPP 的同样排序逻辑以及注册到 BeanFactory 的时机
3) `AnnotationAwareOrderComparator#sort`：观察排序输入（候选集合）与排序输出（最终顺序）
4) 你在 Lab 中定义的三个 processor（priority/ordered/unordered）入口方法：观察断言里记录的执行顺序是怎么来的

- “某个 BFPP 改定义没生效/被覆盖了” → **定义层 + 顺序问题**：先确认它是否实现了 `PriorityOrdered/Ordered`，再看它是否比其他 BFPP 更早执行（本章 Lab）
- “某个 BPP 的代理/增强消失了或包裹顺序不对” → **实例层 + 顺序问题**：看 `registerBeanPostProcessors` 的排序与注册时机（对照 [31](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)）
- “我手工 `addBeanPostProcessor` 后，`Ordered` 反而不生效” → **实例层 + 注册方式问题**：手工注册的 BPP 不会走容器的排序流程（见 [25](../part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md)）
- “我以为 `@Order` 能解决单依赖注入歧义” → **不是顺序问题，是候选选择问题**：转到 [33](../part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)

一个非常实用的“断点分流口诀”：

> 注意：这里的“先包/后包”说的是 **容器阶段的 BPP 包裹顺序**。
> 它会影响是否出现“多层 proxy（套娃）”以及外层/内层 proxy 的归属。
>
> AOP/事务/缓存/安全这类能力内部还有另一套“链条顺序”（advisor/interceptor 顺序），不要混在一起：
>
> - advisor 顺序与 `proceed()` 嵌套：见 [spring-core-aop：debugging](../../../spring-core-aop/docs/part-01-proxy-fundamentals/035-06-debugging.md)
> - 多切面/多代理叠加与顺序（两套顺序分流）：见 [spring-core-aop：multi-proxy stacking](../../../spring-core-aop/docs/part-03-proxy-stacking/038-09-multi-proxy-stacking.md)
>
> 对应可运行闭环：
>
> - beans（BPP 注册顺序）：`SpringCoreBeansPostProcessorOrderingLabTest`
> - aop（多 advisor vs 套娃 proxy）：`SpringCoreAopMultiProxyStackingLabTest`

> 目标：不靠猜，直接用 debugger 的固定观察点回答“顺序到底怎么来的、最终谁先执行”。

- `beanName`：给断点加条件只看目标 bean（否则会非常吵）
- `result`（或等价变量）：BPP 链路的中间/最终返回值
- `result == bean`：是否发生了“替换/包装”

最小复现入口（必现）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java`
  - `programmaticBppExecutionOrder_isRegistrationOrder_notOrderedInterface()`

你在断点里应该看到什么（用于纠错）：

- 你能解释清楚：为什么我们只断言“相对顺序”，而不去断言“容器内所有处理器的全序列”？
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java`
推荐断点：`PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`、`PostProcessorRegistrationDelegate#registerBeanPostProcessors`、`AnnotationAwareOrderComparator#sort`

## 常见坑与边界

```text
findOrder(obj):
  if obj implements Ordered:
    return obj.getOrder()            // 注意：接口优先级最高，会覆盖注解值

  if obj (class/type) has @Order:
    return @Order.value

  if obj (class/type) has @Priority:
    return @Priority.value

  return null

compare(o1, o2):
  if o1 is PriorityOrdered and o2 is not: return -1
  if o2 is PriorityOrdered and o1 is not: return  1

  order1 = findOrder(o1) ?? LOWEST_PRECEDENCE
  order2 = findOrder(o2) ?? LOWEST_PRECEDENCE
  return Integer.compare(order1, order2)  // 数字越小越靠前
```

另外还有一个经常被忽略的点（很容易“学会了 comparator，却还是踩坑”）：

```text
registerBeanPostProcessors(beanFactory):
  names = getBeanNamesForType(BeanPostProcessor)

  // A) 先塞一个 checker：提示“有 bean 在 BPP 链还没完整时被创建了”
  addBeanPostProcessor(new BeanPostProcessorChecker(...))

  // B) 三段分组：PriorityOrdered -> Ordered -> others
  //    注意：这里会 getBean() 实例化 BPP（BPP 本身也是 bean）
  priority = []
  orderedNames = []
  nonOrderedNames = []
  internal = [] // MergedBeanDefinitionPostProcessor

  for name in names:
    if typeMatch(name, PriorityOrdered): instantiate; priority.add(pp); if pp is internal: internal.add(pp)
    else if typeMatch(name, Ordered): orderedNames.add(name)
    else: nonOrderedNames.add(name)

  sort(priority); addAll(priority)

  ordered = instantiate(orderedNames); sort(ordered); addAll(ordered)
  nonOrdered = instantiate(nonOrderedNames); addAll(nonOrdered)

  // C) 最后：把 internal BPP 再注册一次（借助 addBeanPostProcessor 的 “remove then add to end” 语义）
  sort(internal); addAll(internal) // internal 统一被挪到 list 尾部
```

## 面试常问（顺序：为什么“先包谁/后包谁”会改变行为）

### Q1：`PriorityOrdered` / `Ordered` / `@Order` 的优先级关系是什么？

- 标准答案（可复述）：
  - 分组优先看接口：`PriorityOrdered` 最高、`Ordered` 次之、其余最后；组内再用 comparator（接口 `getOrder()` 优先于注解值），数字越小越靠前。
- 证据链（方法级）：
  - 注册入口：`PostProcessorRegistrationDelegate#registerBeanPostProcessors`
  - 排序入口：`AnnotationAwareOrderComparator#sort`

### Q2：为什么 “手工 addBeanPostProcessor” 可能让 `Ordered` 不生效？

- 标准答案（可复述）：
  - 手工注册绕过了容器的分组+排序装配算法，本质按“注册顺序”生效；这会直接改变包裹顺序。
- 最小复现：
  - `SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticBppExecutionOrder_isRegistrationOrder_notOrderedInterface`

## 小结与下一章

- `AbstractApplicationContext#refresh`
  - `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
    - **BFPP/BDRPP 在这里执行**（定义层：改 `BeanDefinition`）
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
    - **BPP 在这里注册进 BeanFactory**（实例层：决定“谁先作用于实例”）
  - `AbstractApplicationContext#finishBeanFactoryInitialization`
    - `DefaultListableBeanFactory#preInstantiateSingletons`（批量创建非 lazy 单例）
      - `AbstractAutowireCapableBeanFactory#doCreateBean`
        - `initializeBean`
          - `applyBeanPostProcessorsBeforeInitialization`
          - `applyBeanPostProcessorsAfterInitialization`

在 `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` 里建议 watch/evaluate：

- `beanName`：确认当前被处理的目标 bean
- `result`（或等价变量）：每个 BPP 处理后返回的对象引用（是否在某一步被替换成 proxy）
- `this.beanPostProcessors`：最终 BPP 列表顺序（顺序就是“包裹/增强顺序”）
- `result.getClass()`：最终暴露对象的实际类型（经常能一眼看出“谁先包、谁后包”）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java`

上一章：[13. BeanDefinitionRegistryPostProcessor：定义注册再推进](13-bdrpp-definition-registration.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[15. 实例化前短路：还没 new 就拿到对象了？](15-pre-instantiation-short-circuit.md)

<!-- BOOKIFY:END -->
