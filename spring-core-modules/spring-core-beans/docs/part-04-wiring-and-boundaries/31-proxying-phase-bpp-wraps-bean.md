# 31. 代理/替换阶段：`BeanPostProcessor` 如何把 Bean “换成 Proxy”

## 导读

- 本章主题：**31. 代理/替换阶段：`BeanPostProcessor` 如何把 Bean “换成 Proxy”**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansProxyingPhaseLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java`

## 机制主线

这一章把 AOP/事务里最常见、最折磨人的现象，拉回到容器机制本身：

> 你以为你注入的是目标对象，实际上容器可能把它替换成了另一个对象（proxy / wrapper）。  
> **只有“调用链走代理”，AOP/事务等能力才会生效。**

建议直接跑：

## 1. 现象：容器最终暴露的 Bean 可能不是“原始对象”

- 在 `postProcessAfterInitialization(...)` 里判断：如果 bean 实现了 `WorkService` 接口
- 就返回一个 **JDK 动态代理**，而不是返回原始对象

因此你最终从容器拿到的 bean：

- `Proxy.isProxyClass(bean.getClass()) == true`
- 真实类名会类似 `$Proxy123`

这就是“代理/增强”的最小闭环。

## 2. 现象：self-invocation 仍然绕过代理

- `this.inner(...)`

当你调用 `proxy.outer(...)` 时：

- 代理拦截到了 `outer`
- 但 `outer` 内部的 `this.inner(...)` 是在目标对象内部直接调用  
  ⇒ **不会再走代理**  
  ⇒ `inner` 不会被拦截记录

这和 AOP/事务/方法参数校验里遇到的“自调用不生效”是同一个根因：**调用路径没走代理**。

## 3. 现象：JDK Proxy 会影响“按实现类注入/获取”

JDK 代理的本质是：

- 它只实现接口
- 并不会“变成”目标类的子类

因此：

这解释了很多真实项目里常见的困惑：

- “为什么我 `getBean(实现类.class)` 突然拿不到了？”
- “为什么我按实现类注入会报错，但按接口注入没问题？”

## 4. 你必须掌握的“3 个替换点”（否则你永远讲不清代理什么时候出现）

很多“我以为注入的是 X，结果拿到的是 proxy”的问题，本质都是没分清：**BPP 能在多个阶段把对象“换掉”**。

把这三个点记住，你就能把 AOP/事务/异步等机制串起来：

1. **实例化前短路（pre-instantiation）**
   - 扩展点：`InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`
   - 语义：容器还没创建目标对象，就直接返回一个替代对象（常见是 proxy）
   - 对应章节：[15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行](../part-03-container-internals/15-pre-instantiation-short-circuit.md)（pre-instantiation short circuit）
2. **early reference（解决循环依赖的“提前暴露”）**
   - 扩展点：`SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`
   - 语义：当 bean 正在创建、但需要先暴露引用给别人注入时，给出一个“早期引用”（可能就是早期 proxy）
   - 对应章节：[16. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？](../part-03-container-internals/16-early-reference-and-circular.md)（early reference 与循环依赖）
3. **初始化后替换（最常见的 proxy 时机）**
   - 扩展点：`BeanPostProcessor#postProcessAfterInitialization`
   - 语义：目标对象已经创建并初始化完毕，再被包装/替换为最终暴露的对象（proxy/wrapper）

### 4.1 本模块的可断言闭环（推荐从测试方法开始）

### 4.2 AOP 的对应实现：AutoProxyCreator 如何在这三个点介入

上面讲的是“容器机制的抽象替换点”。AOP（以及事务/缓存/安全等）在 Spring 里对应的典型实现就是 AutoProxyCreator 系列。

你不需要背继承树，但你需要知道它的“角色定位”：

- `AbstractAutoProxyCreator`：核心实现，**本质是一个 BPP**（更准确：实现了 `SmartInstantiationAwareBeanPostProcessor`）
- `AbstractAdvisorAutoProxyCreator`：把“增强来源”统一成 `Advisor` 列表（Advisor=Pointcut+Advice）
- `AnnotationAwareAspectJAutoProxyCreator`：把 `@Aspect` 解析成 Advisors，并参与同一套筛选/代理流程

把它放回“三个替换点”里看，就能解释很多真实项目里的反直觉现象：

> 对应完整版本（AOP 模块）：
>
> - AOP 容器主线（AutoProxyCreator/Advisor/Advice/Pointcut）：[spring-core-aop：AutoProxyCreator 主线](../../../spring-core-aop/docs/part-02-autoproxy-and-pointcuts/036-07-autoproxy-creator-mainline.md)
> - 多切面/多代理叠加与顺序：[spring-core-aop：多代理叠加与顺序](../../../spring-core-aop/docs/part-03-proxy-stacking/038-09-multi-proxy-stacking.md)

- **优先面向接口编程**：按接口注入比按实现类注入更稳
- 遇到 AOP/事务“不生效”，先问一句：
  - “这次调用有没有走到代理对象上？”
- self-invocation 的修复思路（原则层面）：
  - 拆分到另一个 bean（让调用变成跨 bean 调用）
  - 或者用更明确的调用路径让调用经过代理

## 7. 延伸阅读（AOP/Tx 的完整版本）

入口：

## 排障分流：这是定义层问题还是实例层问题？

先把“你在排什么”说清楚：

- **定义层（不在本章主线）**：你怀疑某个代理相关基础设施根本没注册（例如 AutoProxyCreator 没进容器）
  - 快速入口：看 `BeanDefinition` 是否存在、谁注册的（尤其是 `@Enable...` / `@Import` / auto-config）
- **实例层（本章主线）**：bean 已创建，但最终暴露对象被替换成 proxy/wrapper
  - 关键入口：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`
  - 关键判断：`result != bean`（是否发生替换）
- **调用路径问题（AOP/事务“不生效”高频）**：你拿到的是 proxy，但调用没有经过 proxy（self-invocation）
  - 关键判断：断点/日志只命中了外层方法，内层 `this.xxx()` 没有再次经过代理
  - 修复方向：让调用变成跨 bean 调用（或显式从容器拿到代理再调用）
## 源码最短路径（call chain）

> 目标：当你怀疑“这个 bean 被换成了 proxy/wrapper”时，用最短调用链找到“换的那一行”。

一条最短主干（实例创建后半段）：

你只要在 `applyBeanPostProcessorsAfterInitialization` 与你自己的 `postProcessAfterInitialization` 停住，通常 1 次就能确认：到底有没有发生“替换”。

## 固定观察点（watch list）

在 `applyBeanPostProcessorsAfterInitialization(...)` 里建议 watch/evaluate：

- `descriptor.getDependencyType()`：注入点按接口还是按实现类
- `matchingBeans.keySet()`：候选 beanName
  - 如果最终暴露的是 JDK proxy，按实现类注入/查找可能会失败（本章第 3 节）

## 反例（counterexample）

**反例 1：self-invocation 仍然绕过代理（我以为 proxy 会拦截所有调用）。**

- `postProcessAfterInitialization` 确实返回了 proxy（`result != existingBean`）
- 但 `outer()` 内部的 `this.inner()` 调用没有再次经过 proxy  
  ⇒ 拦截记录只出现 `outer`，不出现 `inner`

**反例 2：我按实现类 `getBean(Impl.class)`，突然拿不到了。**

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansProxyingPhaseLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

对应实验（可运行 + 可断言）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`

```bash
mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansProxyingPhaseLabTest test
```

在实验里我们写了一个最小的 `BeanPostProcessor`：

实验里的 `SelfInvocationService#outer(...)` 内部调用：

- 你能按接口拿到 bean（例如 `WorkService`）
- 但你按实现类拿（例如 `SelfInvocationService`）会失败（实验里用断言验证）

- 题目：为什么 JDK 代理下按实现类取 bean 会失败？CGLIB 下又为什么“可能成功”？两者分别对注入点类型有什么要求？
- 追问：
  - 你如何判断一个 bean 是否被代理？如果被代理了，它是 JDK proxy 还是 class-based proxy？
  - 你如何定位“是哪个 `BeanPostProcessor` 把它换掉了”？请给出从 `initializeBean` 到 `applyBeanPostProcessorsAfterInitialization` 的断点闭环。
- 复现入口（可断言 + 可断点）：
  - JDK proxy 导致“按实现类获取失败”：
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`
      - `whenABeanIsWrappedAsJdkProxy_lookupByConcreteClassMayBecomeUnavailable()`
  - class-based proxy（CGLIB）下“按实现类获取仍可能可用”（类型是子类）：
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`
      - `whenABeanIsWrappedAsCglibProxy_lookupByConcreteClassMayStillWork()`
  - JDK proxy 导致“按实现类注入失败”（在循环依赖场景更直观）：
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`
      - `injectingConcreteTypeFailsWhenFinalBeanIsJdkProxy_duringCircularDependency()`
  - “是谁把它换掉了”的最短闭环（记录 BPP before/after 与最终类型差异）：
    - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java`
      - `beanCreationTrace_recordsPhases_andExposesProxyReplacement()`

> 这三个点的最大价值：它能解释“为什么你在一个断点里看到的是原始对象，最终 getBean 却拿到 proxy”，以及“为什么循环依赖里 early 与 final 可能不一致”。

- 初始化后替换（final proxy）：
  - `SpringCoreBeansProxyingPhaseLabTest#beanPostProcessorCanReturnAProxyAsTheFinalExposedBean_andSelfInvocationStillBypassesTheProxy`
  - `SpringCoreBeansProxyingPhaseLabTest#whenABeanIsWrappedAsJdkProxy_lookupByConcreteClassMayBecomeUnavailable`
- early reference（循环依赖中的 early proxy）：
  - `SpringCoreBeansEarlyReferenceLabTest#getEarlyBeanReference_canProvideEarlyProxyDuringCircularDependencyResolution`
  - `SpringCoreBeansEarlyReferenceLabTest#injectingConcreteTypeFailsWhenFinalBeanIsJdkProxy_duringCircularDependency`

## 5. Debug / 观察建议

建议你按这个顺序调试（最快定位）：

1. 打印/观察 bean 的运行时类型：看是不是 `$Proxy...`
2. 如果是 JDK proxy：用 `Proxy.isProxyClass(bean.getClass())` 验证
3. 重点确认调用入口：到底有没有从代理进入？
   - 从容器 `getBean(...)` 拿到的对象调用 → 走代理
   - 同类内部 `this.xxx()` → 不走代理

本章只是“容器视角的最小代理实验”。真正的 AOP/事务会在代理中织入拦截器链：

## 源码锚点（建议从这里下断点）

- `AbstractAutowireCapableBeanFactory#initializeBean`：初始化阶段总入口（BPP(before/after) 都从这里被串起来）
- `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`：最常见的“换成 proxy/wrapper”发生点
- `BeanPostProcessor#postProcessAfterInitialization`：扩展点入口（AOP/事务/本章实验的最小代理都在这里返回替身对象）
- `Proxy#newProxyInstance`：JDK 动态代理的生成点（解释为什么类型会变成 `$Proxy...`）
- `DefaultListableBeanFactory#doResolveDependency`：按类型注入时的候选解析入口（解释“按实现类注入为什么会失败”）

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`

建议断点：

1) 你在 Lab 里实现的 `postProcessAfterInitialization(...)`：观察“原始对象”是在哪里被替换成 proxy 的
2) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`：观察容器是如何遍历 BPP 并采用返回值作为最终暴露对象的
3) `SelfInvocationService#outer(...)` 与 `inner(...)`：观察 self-invocation 为什么绕过代理（`this.inner()` 不会再经过 proxy）
4) `DefaultListableBeanFactory#doResolveDependency`：观察按实现类/按接口注入时，类型匹配链路为什么会分叉

- “AOP/事务/拦截器不生效” → **优先实例层（调用链是否走代理）**：入口调用是否发生在 proxy 上？self-invocation 必然绕过（本章第 2 节）
- “按实现类注入/按实现类 getBean 失败” → **实例层（代理暴露类型）**：JDK proxy 只实现接口（本章第 3 节 + `doResolveDependency`）
- “我以为容器里的 bean 一定是原始对象” → **实例层机制**：BPP 可以替换最终暴露对象（本章第 1 节 + `applyBeanPostProcessorsAfterInitialization`）
- “不知道从哪里下断点” → **回到容器时间线**：从 [00](../part-00-guide/011-00-deep-dive-guide.md) 的 `initializeBean/doCreateBean` 入口开始定位

- `beanName`：给断点加条件只看目标 bean（否则命中会很多）
- `existingBean` / `bean`（入参）：原始对象引用
- `result`（或中间变量）：BPP 链路的返回值（**最终暴露对象**）
- `result == existingBean`：是否发生了对象替换（最关键）
- `Proxy.isProxyClass(result.getClass())`：是否是 JDK proxy（本章实验就是它）

在注入相关断点（`doResolveDependency`）里建议 watch/evaluate：

最小复现入口：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`
  - `beanPostProcessorCanReturnAProxyAsTheFinalExposedBean_andSelfInvocationStillBypassesTheProxy()`

你在断点里应该看到什么（用于纠错）：

最小复现入口：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`
  - `whenABeanIsWrappedAsJdkProxy_lookupByConcreteClassMayBecomeUnavailable()`

对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`
推荐断点：`AbstractAutowireCapableBeanFactory#initializeBean`、`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`、`DefaultListableBeanFactory#doResolveDependency`

## 常见坑与边界

### 面试常问：容器最终暴露对象与类型系统陷阱（JDK vs CGLIB）

1. **pre-instantiation**（少见但关键）
   - AutoProxyCreator 可以在实例化之前就返回 proxy（绕过目标对象的创建）
   - 这也是为什么某些场景你在 `doCreateBean` 早期就看到了代理
2. **early reference**（循环依赖边界的核心）
   - AutoProxyCreator 能在循环依赖注入时给出 early proxy（让依赖方拿到的是 proxy 引用）
   - 但这一步也有边界：并不是所有循环依赖都能靠 early reference 修复（构造器循环依赖仍然是硬失败）
3. **after-init**（最常见的最终替换）
   - 初始化完成后再包一层 proxy，是你最常见的“最终暴露对象不是原始实例”的来源

## 6. 常见坑与实践建议

- AOP 心智模型（入口 + 代理）：[spring-core-aop：AOP Proxy 心智模型](../../../spring-core-aop/docs/part-01-proxy-fundamentals/030-01-aop-proxy-mental-model.md)
- JDK vs CGLIB（为什么有时能按实现类注入）：[spring-core-aop：JDK vs CGLIB](../../../spring-core-aop/docs/part-01-proxy-fundamentals/031-02-jdk-vs-cglib.md)
- 自调用陷阱（AOP 版本）：[spring-core-aop：self-invocation](../../../spring-core-aop/docs/part-01-proxy-fundamentals/032-03-self-invocation.md)
- 事务也是代理（Tx 版本）：[spring-core-tx：`@Transactional` 代理](../../../spring-core-tx/docs/part-01-transaction-basics/055-02-transactional-proxy.md)

## 小结与下一章

- `AbstractAutowireCapableBeanFactory#doCreateBean(...)`
  - `populateBean(...)`（注入发生在这里，注入点解析走 `doResolveDependency`）
  - `initializeBean(...)`
    - `applyBeanPostProcessorsBeforeInitialization(...)`
    - `invokeInitMethods(...)`（`@PostConstruct`/init-method）
    - `applyBeanPostProcessorsAfterInitialization(...)`
      - `BeanPostProcessor#postProcessAfterInitialization`  
        - **这里是“把原始对象替换成 proxy/wrapper”的最常见发生点**

## 一句话自检

- 你能用一句话解释：为什么“容器里那个 bean”有时不是你构造出来的类型，而是 proxy/wrapper 吗？
- 你能指出：最终替换最常见发生在哪个方法里吗？（提示：after-init 的 BPP 链）
- 你能解释清楚：为什么 self-invocation 会绕过代理吗？以及最常见的修复方式是什么？

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansProxyingPhaseLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java` / `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java`

上一章：[30. 注入发生在什么时候：field vs constructor](30-injection-phase-field-vs-constructor.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[32. @Resource 的 name-first：CommonAnnotationBeanPostProcessor](32-resource-injection-name-first.md)

<!-- BOOKIFY:END -->
