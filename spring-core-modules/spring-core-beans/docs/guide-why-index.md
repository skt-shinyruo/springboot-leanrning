# 基础问题索引（Why Index）：把高频“为什么”做成可验证闭环


## 官方文档对照（版本语境）

- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）
- Spring Boot：`3.5.9`

- Spring Framework Reference（Beans）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（容器扩展点）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    - 使用方式：可先运行本章推荐 Lab，建立主线/断点闭环；随后回到正文，按“时间线/分支矩阵/证据链”定位机制窗口；最后通过自检题将表述固化为可复述答案。

    本章围绕009-00-why-index展开，主线可以概括为：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansCircularDependencyBoundaryLabTest`。需要下探源码时，可以从 `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton` / `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#addSingletonFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#getEarlyBeanReference` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 导读

这页是一个“高频为什么”的索引页：每个 Why 都试图把一个常见困惑压缩成三样东西：

1) 一句话结论（能复述）
2) 10 分钟证据链（能验证：Lab + 断点 + watch list）
3) 常见误区对照（能避免误归因）

建议读者先跑 `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansEarlyReferenceLabTest`，把“三级缓存/early reference”这类高频现象跑成事实，再回到本页逐条对照。


## 这页解决什么问题

很多“基础问题”（例如：**为什么 Spring 要用三级缓存**）之所以让读者读完仍然困惑，通常不是因为正文里没有提到名词，而是因为：

- 读者缺少稳定的前置结论：**容器对外返回的是最终暴露对象（exposed object），它可能被 proxy/wrapper 替换**；
- 论证链分散在多个章节（循环依赖 / early reference / 代理替换 / AOP call path），需要读者自行整合；
- 缺少“最短证据链”（对应 Lab、断点入口与关键变量）导致无法将概念转化为可验证事实。

因此本页采取“答案先行”的结构：**一句话结论 → 为什么重要 → 10 分钟证据链 → 误区对照 → 下一步去哪读**。

> 设计意图：本页不宜视为“知识点章节”，而应作为“索引 + 最短闭环入口（SSOT）”使用。

## 使用方式（30 秒定位）

当读者阅读某个章节但仍难以复述关键结论时，可先按检索关键词匹配下列条目：

- 搜索：`三级缓存 ` / `three level cache` / `earlySingletonObjects` / `singletonFactories` → 看 Why-01/02
- 搜索：`getEarlyBeanReference` / `early reference` / `raw vs wrapped` / `allowRawInjectionDespiteWrapping` → 看 Why-03
- 搜索：`proxy` / `exposedObject` / `BeanPostProcessor` / `postProcessAfterInitialization` → 看 Why-04
- 搜索：`self invocation` / `self-invocation` / `call path` / “事务不生效” → 看 Why-05

---

## Why-01：为什么 Spring 使用三级缓存（three level cache）？

### 一句话结论（Answer）

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

Spring 的“三级缓存”并不是为了“让循环依赖都能启动”，而是为了在 **singleton 创建窗口期** 支持 **按需提前暴露引用（early reference）**，并把“早期引用的形态（raw 还是 proxy）”的决定权交给 `getEarlyBeanReference`（BPP/AOP 可介入），从而尽量保证 **early == final（最终暴露形态一致）**。

### 为什么重要（Why it matters）

这个问题一旦没讲清，会直接导致读者在以下场景里误判：

- 循环依赖到底“能不能救”，以及 constructor vs setter 的边界；
- 为什么加了 AOP/事务之后循环依赖问题更容易暴露（本质是**对象形态一致性**问题）；
- 为什么同名 bean 会出现 raw/proxy 两种形态并存时，Spring 倾向于 fail-fast。

### 10 分钟证据链（Proof in 10 minutes）

**1) 运行最小实验（可先运行，再结合断点分析）**

- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansCircularDependencyBoundaryLabTest test`
- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansEarlyReferenceLabTest test`

**2) 设置 3 个断点（稳定锚点）**

1. `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton`
2. `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#addSingletonFactory`
3. `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#getEarlyBeanReference`

**3) watch list（仅关注以下变量）**

- `singletonObjects` / `earlySingletonObjects` / `singletonFactories`（三层命中情况）
- `allowEarlyReference`（决定是否允许走 early 分支）
- `isSingletonCurrentlyInCreation(beanName)`（是否处于创建窗口）

**4) 可观察到的现象（验证点）**

- `addSingletonFactory` 发生在 **populateBean 之前、initializeBean 之前**（它是“窗口期固定点”）
- `getSingleton(..., allowEarlyReference=true)` 命中顺序是 **final → early → factory**
- 只有当确实需要 early 引用时（出现依赖注入“需要获取对方引用”），`singletonFactories.get(beanName).getObject()` 才会被调用，从而触发 early reference 的创建

### 常见误区对照（Misconceptions）

1) “三级缓存 = Spring 解决循环依赖”
更准确：三级缓存只为 **特定窗口期** 提供机制支撑，工程上仍应优先消环。

2) “constructor 循环依赖也是靠三级缓存救”
更准确：constructor 依赖发生在实例化前，通常没有 early exposure 窗口；能“救”的通常是通过改变时机（`@Lazy`/`ObjectProvider`）而不是缓存本身。

3) “earlySingletonObjects 里放的就是原始对象”
更准确：early 引用可能是 raw，也可能已经是 proxy；关键取决于 `getEarlyBeanReference`（见 Why-03）。

### 下一步去哪读（Next reading）

- Beans：[`09. 循环依赖（constructor vs setter）`](ioc-circular-dependencies.md)
- Beans：[`16. early reference 与循环依赖：getEarlyBeanReference`](internals-early-reference-and-circular.md)
- Beans：[`31. 代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy`](wiring-proxying-phase-bpp-wraps-bean.md)
- AOP（前置理解）：[01. AOP：代理（Proxy）+ 入口（Call Path）](../../spring-core-aop/docs/proxy-fundamentals-aop-proxy-mental-model.md)（为什么要跳：本章的“early 形态 = raw/proxy”离不开对“代理是什么、调用从哪进”的直觉；验证什么：在 AOP 章先跑通一个最小 proxy 用例，确认“调用路径经过代理”才会触发增强）

---

## Why-02：为什么不是“二级缓存”就够？（2-level vs 3-level）

### 一句话结论（Answer）

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

**二级缓存只能缓存“对象”，而三级缓存额外缓存了“按需创建 early reference 的能力（ObjectFactory）”。**
这让容器能同时满足两个目标：

1) **只在真的需要 early reference 时才创建它**（避免为所有 bean 都提前生成 early proxy/early wrapper）
2) **让 BPP/AOP 有机会决定 early 的形态，并且只创建一次**（尽量保证 early == final，避免 raw 注入绕过代理）

### 10 分钟证据链（Proof in 10 minutes）

- 断点：`addSingletonFactory` → `getSingleton(..., allowEarlyReference=true)`
  观察：factory 先被注册，但并不会立刻 `getObject()`；只有出现循环注入“确实需要 A 的引用”时才会调用。

### 常见误区对照（Misconceptions）

- “多一层缓存只是历史包袱/拍脑袋设计”
更准确：第三层解决的是“**延迟决策 + 延迟创建**”，并且把代理介入点固定在可控窗口内。

### 下一步去哪读（Next reading）

- Beans：[`16. early reference 与循环依赖`](internals-early-reference-and-circular.md)（其中对“形态一致性”进行系统阐述）

---

## Why-03：为什么需要 `getEarlyBeanReference`？（early reference 的形态：raw vs proxy）

### 一句话结论（Answer）

> 官方参考（Spring Framework 6.2.x，容器扩展点：Post-Processor 体系）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

三级缓存解决的是“**什么时候可以交付引用**”，而 `getEarlyBeanReference` 解决的是“**交付出去的引用应该是什么形态**”。
当 AOP/代理介入时，若 dependent bean 获取到的是 raw，而容器最终对外暴露的是 proxy（wrapped），则可能出现：

- 行为绕过（事务/安全/缓存等拦截失效）
- 或被 Spring 直接 fail-fast（raw vs wrapped 一致性保护）

### 10 分钟证据链（Proof in 10 minutes）

- 运行实验：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansRawInjectionDespiteWrappingLabTest test`
- 设置断点：
  1) `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`
  2) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`
  3) `AbstractAutowireCapableBeanFactory#doCreateBean`（尾部一致性检查附近）

### 下一步去哪读（Next reading）

- Beans：[`16. early reference 与循环依赖`](internals-early-reference-and-circular.md)
- Beans：[`31. 代理替换阶段（BPP after-init）`](wiring-proxying-phase-bpp-wraps-bean.md)

---

## Why-04：为什么最终暴露对象（exposed object）可能变成 proxy/wrapper？

### 一句话结论（Answer）

> 官方参考（Spring Framework 6.2.x，容器扩展点：Post-Processor 体系）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

Spring 容器返回的是 **exposed object**，而不是“原始实例”；在 bean 创建过程中，`BeanPostProcessor` 允许返回替代对象（proxy/wrapper），因此最终 `getBean()` 获取到的对象可能并非原始实例，而是经过替换后的 proxy/wrapper。

### 10 分钟证据链（Proof in 10 minutes）

- 运行实验：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansProxyingPhaseLabTest test`
- 设置断点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`
  观察：`result != bean`（是否发生替换）

如需进一步将“代理由谁生成（AutoProxyCreator）”纳入可运行的证据链（AOP 模块），可参阅：

- `mvn -pl :spring-core-aop -Dtest=SpringCoreAopAutoProxyCreatorInternalsLabTest test`
- 断点锚点：`AbstractAutoProxyCreator#postProcessAfterInitialization` / `#wrapIfNecessary`

### 下一步去哪读（Next reading）

- Beans：[`31. 代理产生阶段：BPP 如何把 Bean 换成 Proxy`](wiring-proxying-phase-bpp-wraps-bean.md)
- AOP：[07. AOP 的容器主线：AutoProxyCreator 作为 BPP](../../spring-core-aop/docs/autoproxy-and-pointcuts-autoproxy-creator-mainline.md)（为什么要跳：Beans 侧能解释“BPP 允许换对象”，但“是谁、在什么时候 wrapIfNecessary”要靠 AOP 容器主线补齐；验证什么：按本节给出的 AOP 测试 + 断点锚点跑一遍，观察代理生成条件与目标对象）

---

## Why-05：为什么 self-invocation 会让 AOP/事务“不生效”？（call path）

### 一句话结论（Answer）

> 官方参考（Spring Framework 6.2.x，容器扩展点：Post-Processor 体系）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

Spring AOP 默认基于代理实现：只有“通过代理对象发起的调用”才会被 advice 包起来。
同类内部 `this.xxx()` 调用不会经过代理（call path 绕过 proxy），因此不会触发拦截器链。

### 10 分钟证据链（Proof in 10 minutes）

可通过 2 个断言完成“代理存在 + 自调用绕过”的闭环验证：

1) 首先验证“获取到的对象为 proxy”：

```bash
mvn -pl :spring-core-aop -Dtest=SpringCoreAopLabTest#tracedBusinessServiceIsAnAopProxy test
```

2) 再证明“call path 决定是否生效（self-invocation 绕过 proxy）”：

```bash
mvn -pl :spring-core-aop -Dtest=SpringCoreAopLabTest#selfInvocationDoesNotTriggerAdviceForInnerMethod test
```

可选对照（进阶）：可进一步验证 “exposeProxy 可以使内部调用也经过 proxy”：

```bash
mvn -pl :spring-core-aop -Dtest=SpringCoreAopExposeProxyLabTest#exposeProxyAllowsSelfInvocationToTriggerAdvice test
```

### 下一步去哪读（Next reading）

- AOP：[03. self-invocation：为什么 `this.inner()` 不会被拦截](../../spring-core-aop/docs/proxy-fundamentals-self-invocation.md)（为什么要跳：本章说的是“call path 绕过 proxy”，AOP 章把“绕过发生在哪个调用点”讲得更细；验证什么：用本节的 self-invocation / exposeProxy 对照用例，观察“同类内部调用”是否经过代理）
- Beans（补齐容器视角）：[`31. 代理替换发生在哪个阶段`](wiring-proxying-phase-bpp-wraps-bean.md)

## 面试常问（Why Index）

1) **为什么说“三级缓存不是为了让所有循环依赖都能启动”？它真正解决的是什么？**
   - 结论：三级缓存解决的是“singleton 创建窗口期的 early reference 交付能力”，并把 early 形态的决定权交给 `getEarlyBeanReference`，尽量保证 early == final。
   - 证据链（方法级）：`DefaultSingletonBeanRegistry#addSingletonFactory` → `#getSingleton(allowEarlyReference=true)` → `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`。

2) **为什么 `getEarlyBeanReference` 是“形态一致性”的关键？raw vs wrapped 的风险是什么？**
   - 结论：若 dependent bean 获取到 raw，而最终对外暴露为 wrapped/proxy，则可能出现拦截失效（事务/安全/缓存）或被 fail-fast。
   - 证据链：`getEarlyBeanReference`（early window） vs `applyBeanPostProcessorsAfterInitialization`（after-init window）的对照。

3) **为什么 self-invocation 会绕过 AOP？如何用证据链证明“绕过的是 call path”而不是“没有代理”？**
   - 结论：代理只拦截“通过代理对象发起的调用”；`this.inner()` 不经 proxy，自然无拦截器链。
   - 证据链：先验证 `getBean()` 获取到的是 proxy（isAopProxy），再验证内部调用走的是 `this`（调用栈/断点）。

## 自检要点
应能够做到：

1) 用 3 句复述 Why-01/03：结论是什么、证据链入口方法是什么、最常见误区是什么。
2) 在 IDE 中设置 3 个稳定锚点断点，并用 watch list 解释“何时命中 final/early/factory”“何时触发 early reference”。
3) 遇到真实问题时，能把症状先分层（定义/创建/代理/值解析），再回到本页选择最短闭环入口（章节 + Lab + 断点）。


<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    建议 先跑 `SpringCoreBeansCircularDependencyBoundaryLabTest`，再用 `SpringCoreBeansEarlyReferenceLabTest` 做对照；把两次差异对齐到正文的关键分支解释。
    - 第一断点：`ApplicationContext#refresh`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：对跨模块链接补“跳转目的”：在链接附近用 1–2 句说明为什么此处需要 AOP/TX 视角，以及跳过去应验证的关键点（例如代理创建点/自调用行为/拦截器链顺序）。
    - 下一跳：若是从现象进入，优先回到 [知识地图](appendix-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](guide-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->

## 小结

`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

