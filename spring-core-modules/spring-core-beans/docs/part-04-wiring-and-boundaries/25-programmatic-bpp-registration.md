# 25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱

## 导读

- 本章主题：**手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱**
- 阅读方式建议：先跑本章两个核心测试，把“为什么 Ordered 不生效 / 为什么手工注册会更早执行”固定成断言；再用断点把它放回 `refresh()` 的注册时机里看清楚。

!!! summary "本章要点"

    - `addBeanPostProcessor(...)` 的语义不是“注册一个可排序的处理器”，而是：**直接修改 `beanFactory.getBeanPostProcessors()` 这个 list**。因此它天然是“按注册顺序”，而不是“按 Ordered 排序”。
    - 手工注册的 BPP 通常发生在 `refresh()` 之前，因此它会早于容器自动发现/排序注册的 BPP 执行 —— **哪怕后者是 PriorityOrdered**。
    - BPP 不是“事后补丁”：**bean 一旦在 BPP 链完整之前被创建出来，就永远错过后续 BPP**。排障时要先分清：你遇到的是“顺序问题”还是“时机问题”。
    - “编程式注册”还有一个更容易误诊的分支：`registerSingleton`（实例层）会绕开创建管线，因此“没注入/没代理/没回调”常常不是顺序问题，而是你根本没走过 `doCreateBean`。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansProgrammaticBeanPostProcessorLabTest` / `SpringCoreBeansProgrammaticRegistrationLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest`
    - Test file：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticRegistrationLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRegistryPostProcessorLabTest.java`

## 机制主线：两条注册路径 + 一个“不可逆”事实

在大多数教程里，BPP 都是这样出现的：

- “声明一个 `BeanPostProcessor` bean，容器会自动发现并注册”

但 Spring 也提供了一个更底层的入口：

- `beanFactory.addBeanPostProcessor(bpp)`（直接把对象塞进链路）

这两条路径的差异，决定了你会遇到的两类典型困惑：

1) “我实现了 Ordered/ PriorityOrdered，为什么顺序不生效？”  
2) “我明明有 BPP，为什么某些 bean 没被它处理（没代理/没增强/没回调）？”

本章用最小可跑实验把这两类困惑拆开并给出断点闭环。

---

## 1. 现象 1：手工添加的 BPP 会比“作为 bean 自动发现”的 BPP 更早执行

对应实验：

- `SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticallyAddedBpp_runsBeforeBeanDefinedBpp_evenIfBeanDefinedIsPriorityOrdered`

实验做了两件事：

1) 在 `refresh()` 之前手工 `addBeanPostProcessor(...)`  
2) 同时注册一个 **作为 bean** 的 `PriorityOrdered` BPP（让容器在 refresh 中发现并排序注册）

你会观察到的稳定结论是：

- **手工注册的 BPP 更早进入最终 list，所以更早执行**

> 这不是“PriorityOrdered 失效”，而是因为 PriorityOrdered 只影响“容器排序那一批候选”，而你绕开了那一批候选。

---

## 2. 现象 2：programmatic BPP 的执行顺序 = 注册顺序（不是 Ordered 顺序）

对应实验：

- `SpringCoreBeansProgrammaticBeanPostProcessorLabTest#programmaticBppExecutionOrder_isRegistrationOrder_notOrderedInterface`

你会看到：即使两个手工注册的 BPP 都实现了 `PriorityOrdered`（并返回不同 order 值），最终执行仍然是：

- **先 add 的先执行**

原因也非常“朴素”：`addBeanPostProcessor` 的语义就是维护一个 list（概念级伪代码足够解释现象）：

```text
addBeanPostProcessor(bpp):
  list.remove(bpp)  // 如果已存在，先移除旧位置
  list.add(bpp)     // 永远追加到末尾
```

因此它天然推导出两个结论：

1) **顺序 = list 顺序**：不会触发排序器，也就谈不上按 Ordered 排  
2) **重复 add 会“挪到最后”**：remove + add 的语义就是重新追加

---

## 3. 两条注册路径对照：你到底走的是哪条？

> 目标：排障时用最短路径确认：当前 BPP 顺序为什么是这样？Ordered 到底有没有机会生效？

### 3.1 手工注册路径（绕过容器排序）

你直接调用：

- `DefaultListableBeanFactory#addBeanPostProcessor`

它的结果是：

- 立刻修改 `beanFactory.getBeanPostProcessors()` 的 list
- **不经过** `PostProcessorRegistrationDelegate#registerBeanPostProcessors` 的收集与排序

### 3.2 容器自动发现 + 排序路径（Ordered 在这里才有意义）

典型栈是：

- `AbstractApplicationContext#refresh`
  - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`
    - 收集 `BeanPostProcessor` 类型的 beanName
    - 创建对应 BPP 实例
    - `AnnotationAwareOrderComparator#sort(...)`（PriorityOrdered → Ordered → others）
    - 再逐个 `addBeanPostProcessor(...)` 进入最终 list

一句话总结：

> **Ordered 影响的是“容器排序那一批候选”，而不是“最终 list 会自动排序”。**

---

## 4. 排障分流：顺序问题 vs 时机问题（先分清楚再下手）

当你遇到“代理没生效 / 增强没生效 / 某些回调没触发”时，先做一次强制分流：

### 4.1 顺序问题（BPP 都生效了，但“包裹顺序”不对）

判断标准：

- 目标 bean 的创建过程中确实执行了多个 BPP
- 只是最终“谁包谁”的顺序不符合预期

此时最有效的观察点只有一个：

- `beanFactory.getBeanPostProcessors()`：**这就是最终执行顺序**

### 4.2 时机问题（目标 bean 在 BPP 链完整之前就被创建了）

判断标准（典型信号）：

- 你在启动日志里看到类似 “not eligible for getting processed by all BeanPostProcessors” 的语义
- 或者你发现某个 bean 在 BFPP/BDRPP 阶段被 `getBean()` 触发创建

对照实验：

- `SpringCoreBeansRegistryPostProcessorLabTest#getBeanDuringPostProcessing_instantiatesTooEarly_andSkipsLaterBeanPostProcessors`

一句话记住这条“不可逆”规律：

> **BPP 不是 retroactive 的。一个 bean 错过了当时存在的 BPP 链，就不会被“事后补上”。**

---

## 5. Debug 断点闭环（用一次就够）

### 5.1 推荐断点（按收益排序）

1) `DefaultListableBeanFactory#addBeanPostProcessor`：观察手工注册对 list 的直接影响
2) `PostProcessorRegistrationDelegate#registerBeanPostProcessors`：观察容器自动发现/排序/注册 BPP 的过程
3) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`：观察最终执行顺序与“谁包谁”
4) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`：观察 before-init BPP 的执行点

### 5.2 固定观察点（watch list）

- `beanFactory.getBeanPostProcessors()`：最终链路（顺序就是语义）
- “你的目标 beanName”：只看你关心的那个 bean，避免噪声
- “你的目标 BPP 类型”：确认它到底有没有进 list，以及在 list 的位置

---

## 6. 扩展：编程式注册的三种入口（定义层 vs 实例层）

这一节是为了避免另一类高频误诊：

> “我把对象塞进容器了，但它没注入/没 init/没代理/没回调，是不是 BPP 顺序问题？”

很多时候根因是：你走了“实例层注册”。

### 6.1 三种入口，你到底注册了什么？

- **定义层（推荐理解）**：把“怎么造对象”交给容器  
  - `registerBeanDefinition` / `registerBean`
  - 会走 `doCreateBean`，因此会有注入、生命周期、BPP
- **实例层（最容易踩坑）**：你已经把对象 new 好了，容器只是“给它一个名字”  
  - `registerSingleton`
  - **不会** retroactive 触发注入、BPP、init 回调

### 6.2 如果你必须把“既有对象”交给 Spring 管，怎么办？

核心原则是：你要显式补齐创建管线的关键步骤，例如：

- `AutowireCapableBeanFactory#autowireBean(...)`
- `AutowireCapableBeanFactory#initializeBean(existing, beanName)`

> 注意：这是一种“强行拼装”的路径，学习理解即可；工程里优先把对象的创建权交回容器（定义层注册）。

---

## 一句话自检

你应该能用 3 句答题：

1) `addBeanPostProcessor` 为什么不会按 Ordered 排序？（它直接改最终 list，绕过 registerBeanPostProcessors 的排序输入）  
2) 怎么判断是“顺序问题”还是“时机问题”？（先看最终 list；再看目标 bean 是否在链完整之前被创建）  
3) `registerSingleton` 为什么容易让人误诊？（它绕开 doCreateBean，因此不会自动注入/BPP/init）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansProgrammaticBeanPostProcessorLabTest` / `SpringCoreBeansProgrammaticRegistrationLabTest` / `SpringCoreBeansRegistryPostProcessorLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticBeanPostProcessorLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticRegistrationLabTest.java`

上一章：[24. BeanDefinition 覆盖：同名定义的冲突策略](24-bean-definition-overriding.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[26. SmartInitializingSingleton：容器就绪后回调](26-smart-initializing-singleton.md)

<!-- BOOKIFY:END -->
