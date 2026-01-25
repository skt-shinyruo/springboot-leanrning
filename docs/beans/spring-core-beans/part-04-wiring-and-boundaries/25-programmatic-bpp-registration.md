# 25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱

## 导读

- 本章主题：**25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansProgrammaticBeanPostProcessorLabTest` / `SpringCoreBeansProgrammaticRegistrationLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticRegistrationLabTest.java`

## 机制主线

很多学习资料会只讲“把 BPP 声明成 bean，让容器自动发现”。

但容器还支持一种更底层的方式：

- `beanFactory.addBeanPostProcessor(...)`

## 1. 现象：手工添加的 BPP 会比容器自动发现的 BPP 更早执行

对应测试：

- `SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticallyAddedBpp_runsBeforeBeanDefinedBpp_evenIfBeanDefinedIsPriorityOrdered`

我们做了两件事：

1) 在 refresh 之前用 `addBeanPostProcessor` 手工注册一个 BPP
2) 同时再注册一个 `PriorityOrdered` 的 BPP（作为普通 bean）

你会观察到：

- 手工注册的 BPP 仍然先于“作为 bean 自动发现”的 BPP 执行

结论：

- **手工注册的 BPP 优先级非常高**

对应测试：

- `SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticBppExecutionOrder_isRegistrationOrder_notOrderedInterface`

即使你实现了 `PriorityOrdered` 并返回更高优先级：

- 手工注册的 BPP 仍然按“注册顺序”执行

这能解释很多工程里遇到的“我明明写了 order，但为什么不生效？”

## 3. 源码解析：`addBeanPostProcessor` 的语义就是“改 list”，因此不会触发排序

很多人会误以为：既然我实现了 `Ordered/PriorityOrdered`，那 Spring 总会在某个时刻“帮我排一下序”。

但对 programmatic 注册来说，事实恰好相反：

> **`addBeanPostProcessor` 不负责排序，它只负责把对象放进 `beanFactory.getBeanPostProcessors()` 这个 list。**

在 Spring 源码里（`AbstractBeanFactory#addBeanPostProcessor`），它的核心语义非常直接：

```text
addBeanPostProcessor(bpp):
  synchronized(beanPostProcessors):
    beanPostProcessors.remove(bpp)   // 如果已存在，先移除旧位置
    beanPostProcessors.add(bpp)      // 永远追加到末尾
```

你从这个实现立刻能推导出两个稳定结论（也是本章两个测试的根因）：

1) **执行顺序 = list 顺序**：它只会“越早 add 越靠前”，不会“越小 order 越靠前”
2) **重复 add 会把它挪到最后**：因为 remove+add 的语义是“重新追加”

对照容器自动发现路径：

- 容器的排序发生在 `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
- 容器做的是：**先 sort，再按排序结果逐个 add 进 list**

而你手工 add 的 BPP：

- **根本不会进入 `registerBeanPostProcessors` 的排序输入集合**
- 所以它当然也不会被容器排序

这一条在工程里比“Ordered 不生效”更常见，但很多人会把它误诊成“顺序问题”。

核心事实只有一句话：

> **BPP 是“创建时拦截链”，不是“创建后补丁”。**

因此无论你是：

- 手工 `addBeanPostProcessor`
- 还是让容器自动发现并注册 BPP

只要某个 bean 在 BPP 链完整之前就已经被创建出来，它就会错过后续 BPP（包括代理、增强、标记等）。

把这一条理解清楚，你就能在排障时快速分流：

- **顺序问题**：BPP 都注册了，但包裹/增强顺序不对 → 看 `beanFactory.getBeanPostProcessors()` 的 list 顺序
- **时机问题**：bean 在 BPP 链完整前就被创建 → 看“是谁太早触发了 getBean/注入/FactoryMethod”

## 5. 什么时候会用到？

学习阶段你只要知道它存在即可；工程里典型使用场景是：

- 框架需要在 refresh 之前根据条件动态决定是否注册某些 BPP

但要谨记：

- 这是强力扩展点，滥用会让系统难以推理。

入口：

- 入口测试（方法级，建议先跑通再下断点）：
  - `SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticallyAddedBpp_runsBeforeBeanDefinedBpp_evenIfBeanDefinedIsPriorityOrdered`
  - `SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticBppExecutionOrder_isRegistrationOrder_notOrderedInterface`
- 对照用例（时机问题：过早实例化导致“跳过后续 BPP”）：
  - `SpringCoreBeansRegistryPostProcessorLabTest#getBeanDuringPostProcessing_instantiatesTooEarly_andSkipsLaterBeanPostProcessors`

## 排障分流：这是定义层问题还是实例层问题？

## 源码最短路径（call chain）

> 目标：当你遇到“我明明实现了 `Ordered`，为什么顺序不生效 / 代理叠加顺序不对”时，用最短调用链确认：你到底走的是“容器自动发现+排序”还是“手工注册绕过排序”。

### 1) 手工注册路径（绕过容器排序）

你在代码里直接调用：

- `DefaultListableBeanFactory#addBeanPostProcessor(bpp)`

它会把 bpp 直接塞进 `beanFactory.getBeanPostProcessors()` 列表里：

- **这个动作发生在 refresh 之前**
- **不会触发 `AnnotationAwareOrderComparator` 排序**
- **因此顺序只由“注册顺序”决定**

### 2) 容器自动发现+排序路径（走 `registerBeanPostProcessors`）

- `AbstractApplicationContext#refresh`
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
    - 收集 `BeanPostProcessor` 类型的 beanName
    - 创建对应的 BPP 实例
    - `AnnotationAwareOrderComparator#sort(...)`（按 `PriorityOrdered` → `Ordered` → others 排序）
    - `DefaultListableBeanFactory#addBeanPostProcessor(...)`（把排序后的 BPP 注册进 BeanFactory）

### 3) BPP 真正“开始影响结果”的位置（执行点）

当某个目标 bean 被创建时：

一句话记住核心：

- **最终执行顺序 = `beanFactory.getBeanPostProcessors()` 的列表顺序**  
  你只要把这个列表看清楚，大多数“顺序为什么这样”的问题就会瞬间收敛。

## 固定观察点（watch list）

### 1) 先看最终列表（最重要）

在任何位置（尤其是 `registerBeanPostProcessors` 之后）watch/evaluate：

- `beanFactory.getBeanPostProcessors()`：**这就是最终执行顺序**
  - 程序化注册的 BPP 通常在更前面（因为更早加入列表）
  - 容器自动发现的 BPP 在 refresh 中期才加入列表

### 2) 看容器排序的输入/输出（只对“自动发现”生效）

在 `PostProcessorRegistrationDelegate#registerBeanPostProcessors` 里 watch/evaluate：

- `postProcessorNames`：按类型收集到的候选集合
- `AnnotationAwareOrderComparator#sort(...)` 的输入与输出（候选顺序如何变成最终顺序）

### 3) 看执行点（只看你的目标 bean）

## 反例（counterexample）

**反例：我让“手工注册的 BPP”实现了 `PriorityOrdered/Ordered`，但它还是不按 order 排序。**

- 两个 programmatic BPP 都实现了 `PriorityOrdered`，并且 `getOrder()` 值不同
- 但由于它们是通过 `addBeanPostProcessor` 直接进列表：
  - 不会经过 `registerBeanPostProcessors` 的排序
  - 因此执行顺序只按注册顺序：`first` → `second`

- **在 BDRPP/BFPP 阶段 `getBean()` 触发过早实例化**，导致该 bean 在“没有 BPP 的世界”里先被创建出来（见 [13](../part-03-container-internals/13-bdrpp-definition-registration.md) 的反例）

## 6. 补充：三种编程式注册方式对照（定义层 vs 实例层）

这一节解决另一个在工程里更常见、也更容易“误诊成 BPP 顺序问题”的现象：

- “我把对象塞进容器了，但没注入 / 没 init / 没被 BPP 处理”
- “我明明注册了 `@Autowired` 字段，但还是 `null`”

很多时候根因不是 BPP 顺序，而是你走了“实例层注册”路径（`registerSingleton`），本质上绕开了创建管线。

### 6.1 三种入口：你到底注册了什么？

一句话记住：

- 定义层注册 = 把“怎么造对象”交给容器
- 实例层注册 = 你已经把对象造好了，容器只是“帮你挂个名字”

你应该观察到：

- `registerBeanDefinition` 与 `registerBean`：完成依赖注入，并被 `BeanPostProcessor` 处理
- `registerSingleton`：不会自动注入，也不会被 `BeanPostProcessor` retroactive 处理

### 6.3 如果你“就是想把既有对象交给 Spring 管”，该怎么补齐？

原则：把“创建管线”的关键步骤手动调用一遍（否则你得到的只是“有名字的对象”，不是“容器创建的 bean”）。

常见做法（概念级，帮助你建立心智模型）：

### 6.4 源码锚点（看清楚“绕开了哪条管线”）

## 7. 一句话自检

1) `addBeanPostProcessor` 注册的 BPP 为什么不会按 `Ordered/PriorityOrdered` 排序？（提示：它绕过了 `registerBeanPostProcessors` 的排序输入）
2) 你怎么用 `beanFactory.getBeanPostProcessors()` 这一个观察点，判断“顺序问题”还是“时机问题”？
3) 如果某个 bean 没被代理/没被增强，你会如何判断它是否“在 BPP 注册之前就被创建了”？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansProgrammaticBeanPostProcessorLabTest` / `SpringCoreBeansProgrammaticRegistrationLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java`

补充对照实验（编程式注册 Bean 的三种入口差异）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticRegistrationLabTest.java`

- `SpringCoreBeansProgrammaticBeanPostProcessorLabTest.programmaticallyAddedBpp_runsBeforeBeanDefinedBpp_evenIfBeanDefinedIsPriorityOrdered()`

- `SpringCoreBeansProgrammaticBeanPostProcessorLabTest.programmaticBppExecutionOrder_isRegistrationOrder_notOrderedInterface()`

本仓库的可运行复现（不是 programmatic add，但解释的是同一条“时机规律”）：

- `SpringCoreBeansRegistryPostProcessorLabTest.getBeanDuringPostProcessing_instantiatesTooEarly_andSkipsLaterBeanPostProcessors()`
  - 在 BDRPP/BFPP 阶段 `getBean()` 触发“过早实例化”
  - 结果：early bean 不是 eligible for getting processed by all BPPs（后续 BPP 不会补上）

## 源码锚点（建议从这里下断点）

- `DefaultListableBeanFactory#addBeanPostProcessor`：手工注册入口（直接改 list，不会触发排序）
- `AbstractBeanFactory#getBeanPostProcessors`：观察当前 BPP 链（顺序就是语义）
- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：容器自动发现 BPP 的注册入口（会排序）
- `AbstractAutowireCapableBeanFactory#initializeBean`：BPP before/after-init 的介入点（代理/增强常发生在 after-init）

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java`
  - `programmaticallyAddedBpp_runsBeforeBeanDefinedBpp_evenIfBeanDefinedIsPriorityOrdered()`
  - `programmaticBppExecutionOrder_isRegistrationOrder_notOrderedInterface()`

建议断点：

- “我实现了 `Ordered/@Order`，但某个 BPP 顺序不生效” → **实例层 + 注册方式问题**：它是不是被手工 `addBeanPostProcessor` 进去的？（本章第 2 节）
- “某个 BPP 完全没生效” → **实例层 + 时机问题**：目标 bean 是否在 BPP 注册之前就被提前实例化了？（结合 [14](../part-03-container-internals/14-post-processor-ordering.md) 与本章断点）
- “系统里出现很难追踪的代理/增强行为” → **实例层可观测性问题**：优先从 BPP 列表与注册方式入手（也可对照 [31](31-proxying-phase-bpp-wraps-bean.md)）
- “把这类手工注册当成常规手段到处用” → **设计风险**：它会绕开容器默认排序与可观测性，建议仅用于框架/基础设施层

> 目标：在 debugger 里用固定观察点回答：谁先注册、谁先执行、顺序由谁决定。

- 给断点加条件：`beanName.equals("target")`（或你的目标 beanName）
- watch `result`（或等价变量）：是否被替换为 proxy/wrapper（见 [31](31-proxying-phase-bpp-wraps-bean.md)）

最小复现入口（必现）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java`
  - `programmaticBppExecutionOrder_isRegistrationOrder_notOrderedInterface()`

你在断点里应该看到什么（用于纠错）：

### 6.2 最小复现入口（可断言 + 可断点）

- 入口测试：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticRegistrationLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansProgrammaticRegistrationLabTest test`

- 你能解释清楚：为什么手工注册的 BPP 不受 Ordered 影响吗？（提示：容器不会再对它排序，只按注册顺序调用）
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java`
推荐断点：`AbstractBeanFactory#addBeanPostProcessor`、`PostProcessorRegistrationDelegate#registerBeanPostProcessors`、`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

## 常见坑与边界

## 2. 更隐蔽的坑：手工注册的 BPP 不会按 Ordered 排序

## 4. 时机陷阱：BPP 不会 retroactive，只影响“之后创建”的 bean

如果你碰到的是“某个 bean 完全没被 BPP 处理”，也常见于另一个更隐蔽的坑：

- `registerBeanDefinition` / `registerBean`：注册的是 **定义（BeanDefinition 语义）**
  - 结果：对象会在之后的创建过程中被容器创建，并完整参与 “注入 → 初始化 → BPP” 管线
- `registerSingleton`：注册的是 **既有实例（instance 语义）**
  - 结果：容器只是把你提供的对象放进 singleton cache，以后 `getBean` 直接返回它
  - 注意：不会 retroactive 触发注入/初始化/BPP；也不会替你“补上代理/包装”

## 小结与下一章

- `ConfigurableListableBeanFactory#addBeanPostProcessor`：手工注册 BPP 的入口（绕过容器的自动发现与排序）
- `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：容器自动发现并排序 BPP 的入口（与手工注册形成对照）
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`：BPP(before) 的调用点（观察“谁先执行”）
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`：BPP(after) 的调用点（观察“谁后执行/谁包谁”）
- `AbstractApplicationContext#refresh`：把“BPP 注册时机”放回到容器时间线（手工注册通常发生在 refresh 前）

1) 测试里 `addBeanPostProcessor(...)` 的调用行：确认手工注册发生在 refresh 之前
2) `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：观察“作为 bean 自动发现”的 BPP 是何时被注册的
3) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`：观察手工注册的 BPP 会先于自动发现的 BPP 执行
4) 对照第二个测试：观察手工注册的多个 BPP 为什么只按“注册顺序”执行，而不是按 `Ordered` 排序

- `AbstractAutowireCapableBeanFactory#doCreateBean`
  - `populateBean`（注入发生在这里）
  - `initializeBean`
    - `applyBeanPostProcessorsBeforeInitialization`
    - `applyBeanPostProcessorsAfterInitialization`（很多代理/包装发生在这里）

在 `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` 里建议：

- `AutowireCapableBeanFactory#autowireBean(existing)`：补齐依赖注入
- `AutowireCapableBeanFactory#initializeBean(existing, beanName)`：补齐初始化与 BPP 链路
- 销毁阶段：用 `destroyBean` 显式触发销毁回调（与 prototype 销毁语义一起理解，见 [05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）](../part-01-ioc-container/016-05-lifecycle-and-callbacks.md)）

- 定义层注册入口：`DefaultListableBeanFactory#registerBeanDefinition`、`GenericApplicationContext#registerBean`
- 实例层注册入口：`DefaultSingletonBeanRegistry#registerSingleton`
- 定义层创建主线：`AbstractAutowireCapableBeanFactory#doCreateBean`
- 初始化主线（BPP/init callbacks）：`AbstractAutowireCapableBeanFactory#initializeBean`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansProgrammaticBeanPostProcessorLabTest` / `SpringCoreBeansProgrammaticRegistrationLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticRegistrationLabTest.java`

上一章：[24. BeanDefinition 覆盖：同名定义的冲突策略](24-bean-definition-overriding.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[26. SmartInitializingSingleton：容器就绪后回调](26-smart-initializing-singleton.md)

<!-- BOOKIFY:END -->
