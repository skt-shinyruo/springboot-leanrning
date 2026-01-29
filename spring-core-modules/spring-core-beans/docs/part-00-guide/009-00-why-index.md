# 第 09 章：00. 基础问题索引（Why Index）：把高频“为什么”做成可验证闭环

## 这页解决什么问题

很多“基础问题”（例如：**为什么 Spring 要用三级缓存**）之所以让读者读完仍然困惑，通常不是因为正文里没有提到名词，而是因为：

- 读者缺少稳定的前置心智模型：**容器对外返回的是最终暴露对象（exposed object），它可能被 proxy/wrapper 替换**；
- 论证链分散在多个章节（循环依赖 / early reference / 代理替换 / AOP call path），需要读者自己“拼图”；
- 缺少“最短证据链”（跑哪个 Lab、下哪几个断点、看哪几个变量）导致无法把概念变成可验证事实。

因此本页采取“答案先行”的结构：**一句话结论 → 为什么重要 → 10 分钟证据链 → 误区对照 → 下一步去哪读**。

> 设计意图：你不必把这页当成“知识点章节”，而应把它当成“索引 + 最短闭环入口（SSOT）”。

## 怎么用（30 秒定位）

当你读完某个章节但仍然“解释不出来”，请先按你的搜索词命中下面的条目：

- 搜索：`三级缓存` / `three level cache` / `earlySingletonObjects` / `singletonFactories` → 看 Why-01/02  
- 搜索：`getEarlyBeanReference` / `early reference` / `raw vs wrapped` / `allowRawInjectionDespiteWrapping` → 看 Why-03  
- 搜索：`proxy` / `exposedObject` / `BeanPostProcessor` / `postProcessAfterInitialization` → 看 Why-04  
- 搜索：`self invocation` / `self-invocation` / `call path` / “事务不生效” → 看 Why-05

---

## Why-01：为什么 Spring 使用三级缓存（three level cache）？

### 一句话结论（Answer）

Spring 的“三级缓存”并不是为了“让循环依赖都能启动”，而是为了在 **singleton 创建窗口期** 支持 **按需提前暴露引用（early reference）**，并把“早期引用的形态（raw 还是 proxy）”的决定权交给 `getEarlyBeanReference`（BPP/AOP 可介入），从而尽量保证 **early == final（最终暴露形态一致）**。

### 为什么重要（Why it matters）

这个问题一旦没讲清，会直接导致读者在以下场景里误判：

- 循环依赖到底“能不能救”，以及 constructor vs setter 的边界；
- 为什么加了 AOP/事务之后循环依赖问题更容易暴露（本质是**对象形态一致性**问题）；
- 为什么同名 bean 会出现 raw/proxy 两种形态并存时，Spring 倾向于 fail-fast。

### 10 分钟证据链（Proof in 10 minutes）

**1) 跑最小实验（建议先跑再读断点）**

- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansCircularDependencyBoundaryLabTest test`
- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansEarlyReferenceLabTest test`

**2) 下 3 个断点（稳定锚点）**

1. `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton`
2. `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#addSingletonFactory`
3. `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#getEarlyBeanReference`

**3) watch list（只盯这些就够）**

- `singletonObjects` / `earlySingletonObjects` / `singletonFactories`（三层命中情况）
- `allowEarlyReference`（决定是否允许走 early 分支）
- `isSingletonCurrentlyInCreation(beanName)`（是否处于创建窗口）

**4) 你应该看见什么（验证点）**

- `addSingletonFactory` 发生在 **populateBean 之前、initializeBean 之前**（它是“窗口期钉死点”）
- `getSingleton(..., allowEarlyReference=true)` 命中顺序是 **final → early → factory**
- 只有当确实需要 early 引用时（出现依赖注入“要拿到对方”），`singletonFactories.get(beanName).getObject()` 才会被调用，从而触发 early reference 的创建

### 常见误区对照（Misconceptions）

1) “三级缓存 = Spring 解决循环依赖”  
更准确：三级缓存只为 **特定窗口期** 提供机制支撑，工程上仍应优先消环。

2) “constructor 循环依赖也是靠三级缓存救”  
更准确：constructor 依赖发生在实例化前，通常没有 early exposure 窗口；能“救”的通常是通过改变时机（`@Lazy`/`ObjectProvider`）而不是缓存本身。

3) “earlySingletonObjects 里放的就是原始对象”  
更准确：early 引用可能是 raw，也可能已经是 proxy；关键取决于 `getEarlyBeanReference`（见 Why-03）。

### 下一步去哪读（Next reading）

- Beans：[`09. 循环依赖（constructor vs setter）`](../part-01-ioc-container/09-circular-dependencies.md)
- Beans：[`16. early reference 与循环依赖：getEarlyBeanReference`](../part-03-container-internals/16-early-reference-and-circular.md)
- Beans：[`31. 代理产生在哪个阶段：BPP 如何把 Bean 换成 Proxy`](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)
- AOP（前置心智模型）：[01. AOP 心智模型：代理（Proxy）+ 入口（Call Path）](../../../spring-core-aop/docs/part-01-proxy-fundamentals/030-01-aop-proxy-mental-model.md)

---

## Why-02：为什么不是“二级缓存”就够？（2-level vs 3-level）

### 一句话结论（Answer）

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

- Beans：[`16. early reference 与循环依赖`](../part-03-container-internals/16-early-reference-and-circular.md)（里面把“形态一致性”讲透）

---

## Why-03：为什么需要 `getEarlyBeanReference`？（early reference 的形态：raw vs proxy）

### 一句话结论（Answer）

三级缓存解决的是“**什么时候可以交付引用**”，而 `getEarlyBeanReference` 解决的是“**交付出去的引用应该是什么形态**”。  
当 AOP/代理介入时，如果 dependent bean 拿到的是 raw，而容器最终对外暴露的是 proxy（wrapped），就会出现：

- 行为绕过（事务/安全/缓存等拦截失效）
- 或被 Spring 直接 fail-fast（raw vs wrapped 一致性保护）

### 10 分钟证据链（Proof in 10 minutes）

- 跑实验：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansRawInjectionDespiteWrappingLabTest test`
- 下断点：
  1) `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`  
  2) `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`  
  3) `AbstractAutowireCapableBeanFactory#doCreateBean`（尾部一致性检查附近）

### 下一步去哪读（Next reading）

- Beans：[`16. early reference 与循环依赖`](../part-03-container-internals/16-early-reference-and-circular.md)
- Beans：[`31. 代理替换阶段（BPP after-init）`](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)

---

## Why-04：为什么最终暴露对象（exposed object）可能变成 proxy/wrapper？

### 一句话结论（Answer）

Spring 容器返回的是 **exposed object**，而不是“原始实例”；在 bean 创建过程中，`BeanPostProcessor` 允许返回“替身对象”（proxy/wrapper），因此最终 `getBean()` 拿到的对象可能被换过壳。

### 10 分钟证据链（Proof in 10 minutes）

- 跑实验：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansProxyingPhaseLabTest test`
- 下断点：`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`  
  观察：`result != bean`（是否发生替换）

如果你希望把“代理是谁生成的（AutoProxyCreator）”也落到可运行证据链（AOP 模块）：

- `mvn -pl :spring-core-aop -Dtest=SpringCoreAopAutoProxyCreatorInternalsLabTest test`
- 断点锚点：`AbstractAutoProxyCreator#postProcessAfterInitialization` / `#wrapIfNecessary`

### 下一步去哪读（Next reading）

- Beans：[`31. 代理产生阶段：BPP 如何把 Bean 换成 Proxy`](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)
- AOP：[07. AOP 的容器主线：AutoProxyCreator 作为 BPP](../../../spring-core-aop/docs/part-02-autoproxy-and-pointcuts/036-07-autoproxy-creator-mainline.md)（AutoProxyCreator 作为 BPP 的主线）

---

## Why-05：为什么 self-invocation 会让 AOP/事务“不生效”？（call path）

### 一句话结论（Answer）

Spring AOP 默认基于代理实现：只有“通过代理对象发起的调用”才会被 advice 包起来。  
同类内部 `this.xxx()` 调用不会经过代理（call path 绕过 proxy），因此不会触发拦截器链。

### 10 分钟证据链（Proof in 10 minutes）

建议用 2 个断言把“代理存在 + 自调用绕过”一次闭环跑通：

1) 先证明“你拿到的是 proxy”：

```bash
mvn -pl :spring-core-aop -Dtest=SpringCoreAopLabTest#tracedBusinessServiceIsAnAopProxy test
```

2) 再证明“call path 决定是否生效（self-invocation 绕过 proxy）”：

```bash
mvn -pl :spring-core-aop -Dtest=SpringCoreAopLabTest#selfInvocationDoesNotTriggerAdviceForInnerMethod test
```

可选对照（进阶）：如果你想验证 “exposeProxy 可以让内部调用也走 proxy”：

```bash
mvn -pl :spring-core-aop -Dtest=SpringCoreAopExposeProxyLabTest#exposeProxyAllowsSelfInvocationToTriggerAdvice test
```

### 下一步去哪读（Next reading）

- AOP：[03. self-invocation：为什么 `this.inner()` 不会被拦截](../../../spring-core-aop/docs/part-01-proxy-fundamentals/032-03-self-invocation.md)
- Beans（补齐容器视角）：[`31. 代理替换发生在哪个阶段`](../part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md)
