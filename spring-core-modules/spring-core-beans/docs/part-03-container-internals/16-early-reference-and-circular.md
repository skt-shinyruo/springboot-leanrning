# 16. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？

## 导读

- 本章主题：**early reference 与循环依赖：getEarlyBeanReference 到底解决什么？**
- 阅读方式建议：先跑一遍“early proxy”与“raw injection despite wrapping”两个实验，把“对象形态不一致”的误区变成可复现断言，再回到主线对照源码把证据链走通。

!!! summary "本章要点"

    - `getEarlyBeanReference` 解决的不是“能不能拿到引用”，而是：**循环依赖窗口期拿到的 early reference 是否等于最终暴露形态（proxy/wrapper）**。
    - 只懂“三级缓存救 setter 环”还不够：一旦 AOP/代理介入，如果 early 是 raw、final 是 proxy，需要么 **fail-fast**，要么 **带着隐患启动（绕过代理）**。
    - 两个典型失败形态必须见过一次：
      - **按实现类注入 + JDK proxy** → 类型直接对不上（`BeanNotOfRequiredTypeException` 相关）
      - **raw 注入但最终 wrapping** → Spring 默认 fail-fast（raw vs wrapped 一致性保护）
    - 断点主线就三处：`getSingleton`（三层命中）→ `getEarlyBeanReference`（early 形态决定）→ `doCreateBean` 尾部一致性检查（raw vs wrapped）。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansRawInjectionDespiteWrappingLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansContainerLabTest`
    - Test file：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRawInjectionDespiteWrappingLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansCircularDependencyBoundaryLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

## 机制主线：early reference 的“时机”与“形态”

上一章（[09. 循环依赖](../part-01-ioc-container/09-circular-dependencies.md)）读者已经建立了一个关键事实：

- setter/field 循环依赖之所以可能成功，是因为 singleton 创建过程里存在 **early exposure 窗口期**。

这一章要解决的是更难、也更工程化的问题：

> 当容器不得不在“还没初始化完”的窗口期把一个引用交出去时，这个引用到底应该是 **raw object**，还是应该已经是 **proxy/wrapper（最终形态）**？

若忽略“形态一致性”，就会遇到两类灾难：

- **功能绕过**：依赖方拿到 raw，调用链绕过事务/安全/缓存等代理增强（见 [31. 代理/替换阶段：`BeanPostProcessor` 如何把 Bean “换成 Proxy”](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)）
- **类型爆炸**：final 是 JDK proxy，但读者按实现类注入/获取，直接类型不匹配

### 机制讲透：时机 → 形态 → 结果（可断点验证）

**条件**：是否进入 early exposure 窗口  
**分支**：`getSingleton(..., allowEarlyReference=true)` → `getEarlyBeanReference`  
**结果**：  
- early == final：循环依赖可被救活且不绕过代理  
- early ≠ final：触发一致性保护 fail-fast  
**断点建议**：`AbstractAutowireCapableBeanFactory#getEarlyBeanReference`

---

## 1. 先跑实验：让问题变成“可见”的

推荐读者按下面顺序跑（从“成功路径”到“失败边界”）：

1) early proxy 成功路径：`SpringCoreBeansEarlyReferenceLabTest#getEarlyBeanReference_canProvideEarlyProxyDuringCircularDependencyResolution`
2) 类型边界：`SpringCoreBeansEarlyReferenceLabTest#injectingConcreteTypeFailsWhenFinalBeanIsJdkProxy_duringCircularDependency`
3) raw vs wrapped fail-fast：`SpringCoreBeansRawInjectionDespiteWrappingLabTest`（该类内部包含“开关语义”的对照）

命令示例（读者也可以在 IDE 直接运行测试类）：

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansEarlyReferenceLabTest test
```

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansRawInjectionDespiteWrappingLabTest test
```

---

## 2. 需要同时记住的三件事（别拆开背）

### 2.1 三层缓存解决“时机问题”

循环依赖窗口期，容器在 `DefaultSingletonBeanRegistry#getSingleton(beanName, allowEarlyReference)` 里会出现三层命中：

- `singletonObjects`：final（已初始化完成）
- `earlySingletonObjects`：early（已产生 early 引用）
- `singletonFactories`：factory（存在一个 `ObjectFactory`，允许“现在才生成 early 引用”）

这部分回答的是：**什么时候可以把引用交出去？**

补充一个经常被忽略的前提：

- 若将 `allowCircularReferences=false`（更安全的工程策略之一），那么 setter/field 循环依赖也会直接 fail-fast
- early reference / getEarlyBeanReference 这些机制就没有“发挥空间”（因为循环依赖被更早阻断）

对照实验（本仓库已补齐）：

- `SpringCoreBeansCircularDependencyBoundaryLabTest#setterCycleMaySucceedViaEarlySingletonExposure_whenAllowCircularReferencesIsEnabled`
- `SpringCoreBeansCircularDependencyBoundaryLabTest#setterCycleFailsFast_whenAllowCircularReferencesIsDisabled`

### 2.1.1 可救/不可救分类速查

| 环类型 | 是否可能救活 | 关键原因 |
| --- | --- | --- |
| constructor ↔ constructor | 基本不行 | 没有 early exposure 窗口 |
| setter/field ↔ setter/field（singleton） | 可能 | early reference 可介入 |
| prototype ↔ prototype | 不行 | prototype 不进单例缓存 |
| dependsOn 形成的环 | 不行 | 强制初始化顺序直接 fail-fast |

### 2.2 `getEarlyBeanReference` 解决“形态问题”

如果 final 阶段会被代理（典型是 AOP），那读者就不能让依赖方先注入 raw，否则注入方会绕过代理。

因此 Spring 提供了一个提前介入的扩展点：

- `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`

它回答的是：**交出去的引用到底是什么形态？raw 还是 proxy？**

### 2.3 doCreateBean 尾部的一致性检查解决“风险控制”

如果 early 阶段交出去的是 raw，但 final 阶段又变成 proxy，系统会出现“同名 bean 两种形态并存”。

Spring 默认倾向 **fail-fast**，并通过 `DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping(boolean)` 这类开关表达态度：

- 默认：宁可失败，也不要让系统带着“绕过代理”的隐患继续跑
- 可以强行允许，但那是读者接受风险，不是 Spring 兜底

---

## 3. 源码最短路径：把三件事串成一条证据链

这一段的目标不是逐行复现源码，而是给读者一个“能在调试器里跑通”的最短路径（读者跑一次，就能把这章彻底记牢）。

以 setter 循环依赖为例，关键链路可以压缩成：

1) `doCreateBean("alpha")`：实例已创建，但尚未初始化完
2) early exposure：`addSingletonFactory("alpha", ...)`（把 factory 放进三级缓存）
3) `populateBean("alpha")` → 触发创建 `beta`
4) `populateBean("beta")` 需要注入 `alpha` → `getSingleton("alpha", allowEarlyReference=true)`
5) `getSingleton` 命中 factory → `factory.getObject()` → `getEarlyBeanReference(...)`（决定 early 是 raw 还是 proxy）
6) `initializeBean("alpha")`：after-init BPP 可能再次 wrapping
7) `doCreateBean` 尾部检查：如果 dependent bean 已注入 raw，但 final 又 wrapping，可能 fail-fast（raw vs wrapped 不一致）

可以发现：**三级缓存并没有“无条件解决所有环”**，它只是把“有机会把引用交出去”的窗口期暴露出来；至于交出去什么形态，要靠 `getEarlyBeanReference` 与一致性保护来兜住。

---

## 4. 断点闭环（推荐照着做一次）

### 4.1 推荐断点（按收益排序）

1) `DefaultSingletonBeanRegistry#getSingleton`：观察三层命中与 allowEarlyReference 分支
2) `DefaultSingletonBeanRegistry#addSingletonFactory`：观察 early exposure 的起点
3) `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`：观察容器向 BPP 请求 early reference 的时机
4) 相应的 `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`（在 Lab 里实现的那个）：观察 early proxy 的产生与缓存
5) `AbstractAutowireCapableBeanFactory#doCreateBean` 尾部：观察 raw vs wrapped 一致性检查触发条件

### 4.2 固定观察点（watch list）

在 `getSingleton` 里建议盯：

- `isSingletonCurrentlyInCreation(beanName)`
- `singletonObjects` / `earlySingletonObjects` / `singletonFactories` 的 key 是否包含 beanName
- `allowEarlyReference`（这一个值经常决定读者有没有机会拿到 early）

在 `doCreateBean` 里建议盯：

- `earlySingletonExposure`
- `earlySingletonReference`
- `exposedObject`（final 暴露对象，可能被 after-init BPP 替换）
- `dependentBeans` / `hasDependentBean(beanName)`（决定一致性检查是否有足够“证据” fail-fast）

---

## 可复现闭环（基于 `SpringCoreBeansEarlyReferenceLabTest`）

跑完该 Lab，至少应能够复述 3 条结论：

1) **early proxy 可在循环依赖窗口期提供一致形态**  
   - 断点：`getEarlyBeanReference`  
   - 断言：early 与 final 形态一致
2) **按实现类注入 + JDK proxy 会失败**  
   - 断点：`isTypeMatch`  
   - 断言：实现类注入不通过
3) **raw vs wrapped 不一致会 fail-fast**  
   - 断点：`doCreateBean` 尾部一致性检查  
   - 断言：抛出 raw/wrapped 相关异常

## 5. 两个必须掌握的边界：类型与一致性

### 5.1 边界 1：按实现类注入 vs JDK proxy

当 final 是 JDK proxy（只实现接口）时：

- 读者按接口注入：OK（proxy 满足接口类型）
- 读者按实现类注入：fail（proxy 不是实现类实例）

这个失败在循环依赖里会更“早暴露”，因为 early 阶段读者可能就已经拿到了 proxy 形态。

对应实验：`SpringCoreBeansEarlyReferenceLabTest#injectingConcreteTypeFailsWhenFinalBeanIsJdkProxy_duringCircularDependency`

工程建议（按优先级）：

1) 优先按接口注入（这是 Spring 世界里最省心的选择）
2) 如果必须按实现类注入：需要 class-based proxy（或避免会改变暴露类型的代理策略）

### 5.2 边界 2：raw injection despite wrapping（为什么 Spring 默认 fail-fast）

当 early 阶段把 raw 交给了依赖方，但 final 阶段被 wrapping/proxy 替换：

- dependent bean 持有 raw（绕过代理）
- 容器对外暴露 proxy（行为不同）

这会造成“同名 bean 两种形态并存”，Spring 默认宁可启动失败也不要放行。

对应实验：`SpringCoreBeansRawInjectionDespiteWrappingLabTest`（其中会对照开关语义）

需要把结论记成一句话：

> `getEarlyBeanReference` 解决的是“尽量让 early == final”；`allowRawInjectionDespiteWrapping` 解决的是“如果做不到一致，要不要带着隐患硬跑”。

---

## 排障配方：定位环路边并选择打断手段

1) **先锁定 root cause**：`BeanCurrentlyInCreationException`  
2) **找环路边**：  
   - 断点：`DefaultSingletonBeanRegistry#beforeSingletonCreation`  
   - 观察：`dependentBeanMap` / `dependenciesForBeanMap`  
3) **判断类型**：constructor / setter / prototype / dependsOn  
4) **选择手段**：  
   - `@Lazy`：引入代理延迟依赖  
   - `ObjectProvider`：显式按需获取（更可控）  
   - **重构**：拆环（长期最优）

## 常见误区与排障提示

1) **看到异常里包含 “raw version / wrapped”**
   - 先去 `doCreateBean` 尾部看一致性检查条件；再回到 `getEarlyBeanReference` 看 early 形态是否能提前变成 proxy。
2) **循环依赖里事务/安全/缓存突然失效**
   - 高概率是 dependent bean 持有 raw 引用绕过 proxy；优先让 early 与 final 一致，或者消除循环依赖。
3) **“我只是加了一个 AOP，就开始报循环依赖相关异常”**
   - 代理改变了 final 形态，使 early/raw 与 final/proxy 的不一致暴露出来；这不是 AOP “制造了 bug”，而是它让读者看见了之前已经存在的边界。

## 面试常问（early reference：边界比结论更重要）

### Q1：为什么有 early reference 仍然不能“保证循环依赖都能解”？

- 标准答案（可复述）：
  - early reference 只覆盖“能先实例化再注入”的窗口期（典型 setter 依赖）；构造器循环没有这个窗口。并且 early/raw 与 final/proxy 形态不一致时，Spring 可能 fail-fast 以保证一致性。
- 证据链（方法级）：
  - `DefaultSingletonBeanRegistry#getSingleton`（early 分支）
  - `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`
  - `AbstractAutowireCapableBeanFactory#doCreateBean`（尾部一致性检查）
- 最小复现：
  - `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansRawInjectionDespiteWrappingLabTest`

---

## 自检要点
应能够用 3 句答题：

1) `getEarlyBeanReference` 解决什么？（循环依赖窗口期让 early reference 尽量等于最终 proxy/wrapper 形态）
2) 为什么需要它？（避免 raw 注入绕过代理，避免 early 与 final 形态不一致）
3) `allowRawInjectionDespiteWrapping` 是什么态度？（做不到一致时的风险开关：默认 fail-fast，打开就是读者接受“绕过代理”的隐患）
<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：三级缓存的“写入/读出时间点”与对象类型（raw/early/final）的证据链对照。
    - B（边界反例）：反例：early 与 final 不一致的真实后果（事务/AOP/懒代理叠加），以及 allowRawInjectionDespiteWrapping 的边界。
    - C（排障 SOP）：排障：如何从异常/行为差异定位到“early window 参与者是谁”。
    - D（断点观察）：断点：addSingletonFactory/getEarlyBeanReference/after-init proxy 的对照断点组。
    - E（面试复述）：面试追问：为什么说“能解不等于安全”？用哪条证据链回答。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansRawInjectionDespiteWrappingLabTest` / `SpringCoreBeansContainerLabTest`
- Test file：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRawInjectionDespiteWrappingLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`

上一章：[15. 实例化前短路：还没 new 就拿到对象了？](15-pre-instantiation-short-circuit.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[17. 生命周期回调顺序：Aware/@PostConstruct/afterPropertiesSet/initMethod](17-lifecycle-callback-order.md)

<!-- BOOKIFY:END -->
