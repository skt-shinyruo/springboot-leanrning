# 31. 代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization` / `AbstractAutowireCapableBeanFactory#doCreateBean` / `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`
    - 推荐 Lab：`SpringCoreBeansBeanCreationTraceLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）](30-injection-phase-field-vs-constructor.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[32. `@Resource` 注入：为什么它更像“按名称找 Bean”？](32-resource-injection-name-first.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy（以及 self-invocation）**
- 本章目标：把 “AOP/事务/异步不生效” 这类问题，从“背概念”变成“方法级证据链 + 可复现最小实验”。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html
- 官方文档对照（容器扩展点，Spring Framework 6.2.x）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html


!!! summary "本章要点"

    - 容器对外返回的是 **最终暴露对象（exposed object）**，它可能在创建流程中被 BPP 替换成 proxy/wrapper。
    - 最常见的代理替换点是：`postProcessAfterInitialization`（after-init BPP）。
    - self-invocation 之所以不生效，不是“事务没开”，而是 **调用链没有走到代理对象**（`this.xxx()` 永远绕过 proxy）。
    - 代理类型边界必须会排障：JDK proxy 只实现接口，class-based proxy 才是子类（类型可用性差异巨大）。

!!! example "本章配套实验（先运行再读）"

    - Lab：
      - `SpringCoreBeansProxyingPhaseLabTest`
      - `SpringCoreBeansBeanCreationTraceLabTest`
      - `SpringCoreBeansEarlyReferenceLabTest`
    - Test file：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`

## 为什么最终暴露对象会变化？（统一解释：缓存解决“时机”，BPP 决定“形态”）

> 若读者的困惑为“已观察到 bean 实例被创建，为何最终注入/获取时却变为 proxy？”  
> 建议先把这个索引页看完（答案先行）：[`00. Why Index（基础问题索引）`](../part-00-guide/009-00-why-index.md)。

这一章是 Beans ↔ AOP 的关键桥接点：它把“代理”放回 IoC 容器视角解释清楚。

请先记住一句话（后续所有排障都围绕它展开）：

> **容器对外返回的是最终暴露对象（exposed object），而不是原始实例；BPP 允许在创建过程中返回替身对象（proxy/wrapper）。**

把它与循环依赖放在一起看，会更清晰：

- **三级缓存**解决的是：循环依赖窗口期“什么时候可以交付引用”（final/early/factory 三类语义）
- **BPP/`getEarlyBeanReference`**解决的是：窗口期“交付出去的引用到底是什么形态”（raw 还是 proxy），并尽量做到 early == final

跨模块前置（建议只读 1 次，之后就能在两边自由切换）：

- AOP 前置理解（call path/self-invocation）：[01. AOP：代理（Proxy）+ 入口（Call Path）](../../../spring-core-aop/docs/part-01-proxy-fundamentals/030-01-aop-proxy-mental-model.md)（为什么要跳：本章解释“容器允许换对象”，AOP 侧补齐“代理到底是什么 + 调用从哪进”；验证什么：在 AOP 章跑一个最小 proxy 用例，确认只有“经过代理的调用”才会触发增强）
- AOP 容器主线（为什么 AutoProxyCreator 是 BPP）：[07. AOP 的容器主线：AutoProxyCreator 作为 BPP](../../../spring-core-aop/docs/part-02-autoproxy-and-pointcuts/036-07-autoproxy-creator-mainline.md)（为什么要跳：本章看到的是“BPP 把 bean 换成 proxy”，AOP 侧补齐“是谁在 after-init 阶段 wrapIfNecessary”；验证什么：在 `AbstractAutoProxyCreator#postProcessAfterInitialization` 附近观察 proxy 的产生条件与目标对象）

## 机制主线：容器允许“换对象”

> 官方参考（Spring Framework 6.2.x，容器扩展点：Post-Processor 体系）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

Spring 的一个关键能力是：在 bean 创建过程中，容器允许扩展点返回“另一个对象”作为最终结果。

把它落到一句话：

> **BPP 是创建时拦截链，不是创建后补丁。**
> 一个 bean 如果在 BPP 链完整之前就创建了，后续 BPP 不会 retroactive 生效。

### 机制系统阐述：条件 → 分支 → 结果

**条件**：BPP 返回的 `result` 与原始 `bean` 不同  
**分支**：`initializeBean` 在 after-init 阶段“用 result 替换 bean”  
**结果**：容器暴露的是 **proxy/wrapper**，原对象只作为内部目标存在  
**断点建议**：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

## 1. 方法级主线：代理替换发生在 initializeBean 的哪一步？

最常见的“换壳点”发生在初始化链路的末尾：

1) `AbstractAutowireCapableBeanFactory#doCreateBean`
2) `populateBean`（注入发生点）
3) `initializeBean`
   - `applyBeanPostProcessorsBeforeInitialization`
   - `invokeInitMethods`
   - `applyBeanPostProcessorsAfterInitialization`  ← **最常见代理替换点**

在断点里只要盯住这一句，就能把“代理是否发生”变成可观测事实：

- `result != bean` ⇒ 发生了替换（最终暴露对象已不是原对象）

## 2. proxy 的两种形态与类型边界（必须会排障）
> 官方参考（Spring Framework 6.2.x，容器扩展点：Post-Processor 体系）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html


### 2.1 JDK dynamic proxy（接口代理）

特征：

- `Proxy.isProxyClass(bean.getClass()) == true`
- 只实现接口，不是目标类子类

后果（高频误区）：

- 按接口注入/获取通常没问题
- 按实现类注入/获取可能失败（`BeanNotOfRequiredTypeException` / `NoSuchBeanDefinitionException`）

### 2.2 class-based proxy（CGLIB/子类代理）

特征：

- `ClassUtils.isCglibProxyClass(bean.getClass()) == true`（或类名带 `$$`）
- 本质是目标类子类（但会受 final 限制）

后果：

- 按实现类注入/获取通常仍可用（因为是子类）
- 但 final 类/方法会让代理能力受限

## 3. self-invocation：为什么“看起来像配置问题”，本质是调用路径问题？

当从容器获取到的是 proxy：

- 外部调用：`proxy.outer()` ⇒ 走代理 ⇒ 拦截器链生效
- 内部自调用：`this.inner()` ⇒ 直接调用目标对象方法 ⇒ **不走代理** ⇒ 拦截器链不生效

这解释了大量真实项目的“事务不生效/切面不生效”：

- 不是“切面没注册”
- 是“这一次调用根本没经过 proxy”

修复思路（原则层面）：

- 把被拦截的方法挪到另一个 bean，通过容器注入再调用（让调用从 proxy 进入）
- 或者使用更明确的设计表达依赖关系（而不是在同类内部 `this.xxx()`）

## 4. 必须知道的“三个替换点”（pre / early / after-init）

排障时常受制于这一误判：

> “在某个断点中观察到原对象，因此认为最终不可能是 proxy。”

错。容器存在三个常见替换点：

1) **pre-instantiation short-circuit（实例化前短路）**
   - `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`
   - 对应章节：[15](../part-03-container-internals/15-pre-instantiation-short-circuit.md)
2) **early reference（循环依赖窗口）**
   - `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`
   - 对应章节：[16](../part-03-container-internals/16-early-reference-and-circular.md)
3) **after-init（最常见 final proxy）**
   - `BeanPostProcessor#postProcessAfterInitialization`
   - 本章重点（以及 AOP/事务常见落点）

## 5. 排障决策表（代理/增强：从“没生效”到“证据链”）

| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| AOP/事务“不生效” | 调用没走 proxy（常见 self-invocation） | 断点 `applyBeanPostProcessorsAfterInitialization` 看是否替换；对照外部调用 vs `this.xxx()` | 让调用从容器注入的 proxy 进入；拆分 bean | `SpringCoreBeansProxyingPhaseLabTest` |
| 按实现类 `getBean`/注入失败 | JDK proxy 只实现接口 | `Proxy.isProxyClass(...)`；注入点类型是实现类 | 按接口注入；或改 class-based proxy（注意 final） | `SpringCoreBeansProxyingPhaseLabTest` / `SpringCoreBeansEarlyReferenceLabTest` |
| 有时是原对象，有时是 proxy | 创建时机不同导致错过/命中 BPP | 对照 `registerBeanPostProcessors` 与目标 bean 创建时机；看是否过早 `getBean` | 避免在 BFPP/BDRPP 阶段过早创建；保证 BPP 链完整 | 结合 [14](../part-03-container-internals/14-post-processor-ordering.md)、[25](25-programmatic-bpp-registration.md) |
| 循环依赖中“类型突然不对” | early reference 形态与 final 形态不一致 | 断点 `getEarlyBeanReference`；看 raw vs wrapped 一致性检查 | 理解 early reference 边界；避免 constructor cycle；必要时调整注入类型 | `SpringCoreBeansEarlyReferenceLabTest` |

## 6. 断点闭环（建议照做一次）

### 6.1 推荐断点（按收益排序）

1) `AbstractAutowireCapableBeanFactory#initializeBean`（串联点：before/after-init BPP 都从这里过）
2) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`（最常见替换点：`result != bean`）
3) 目标 BPP 的 `postProcessAfterInitialization`（定位“是谁换的”）
4) （循环依赖相关）`AbstractAutowireCapableBeanFactory#getEarlyBeanReference`

### 6.2 固定观察点（watch list）

- `beanName`（建议条件断点只看目标 bean）
- `bean` vs `result`（是否发生替换）
- `result.getClass()`（proxy 类型判断）
- `beanFactory.getBeanPostProcessors()`（链路里有哪些 BPP）

## 7. 面试常问（proxy 与 self-invocation）

### Q1：为什么 `getBean()` 获取到的可能不是原始实例？最常见的替换点在哪？

- 标准答案（可复述）：
  - 容器返回最终暴露对象；after-init BPP（`postProcessAfterInitialization`）是最常见的替换点，AOP/事务通常在这里返回 proxy。
- 证据链（方法级）：
  - `initializeBean` → `applyBeanPostProcessorsAfterInitialization`

### Q2：self-invocation 为什么会让事务/AOP不生效？

- 标准答案（可复述）：
  - 因为自调用发生在目标对象内部，走的是 `this.xxx()`，不会经过容器返回的 proxy，因此拦截器链不会触发。

### Q3：JDK proxy 与 CGLIB proxy 的核心差别是什么？为什么会影响“按实现类注入”？

- 标准答案（可复述）：
  - JDK proxy 只实现接口，不是目标类子类；CGLIB proxy 是子类（但受 final 限制）。所以按实现类注入在 JDK proxy 下更容易失败。

## 自检要点
应能够用 3 句回答：

1) 代理最常见在哪个方法级替换点产生？（提示：after-init BPP）
2) 为什么 self-invocation 一定绕过代理？
3) 如何用断点证明“是谁把对象换成了 proxy”？
<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    - 建议入口：先跑 `SpringCoreBeansBeanCreationTraceLabTest`，再用 `SpringCoreBeansProxyingPhaseLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“2. proxy 的两种形态与类型边界（必须会排障）”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](../appendix/92-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](../part-00-guide/013-02-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansProxyingPhaseLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`

上一章：[30. 注入阶段：field injection vs constructor injection（以及 `postProcessProperties`）](30-injection-phase-field-vs-constructor.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[32. `@Resource` 注入：为什么它更像“按名称找 Bean”？](32-resource-injection-name-first.md)

<!-- BOOKIFY:END -->
