# 04. `@Aspect` 实例模型：singleton vs perthis/pertarget/pertypewithin（Spring AOP 语境）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕`@Aspect` 实例模型：singleton vs perthis/pertarget/pertypewithin（Spring AOP 语境）展开，主线可以概括为：Spring AOP（proxy-based）支持 AspectJ 的部分实例模型语法，但语义与“真正的 weaving”不同；最关键的工程结论是：非 singleton per-clause 要求 aspect bean 是 prototype，否则会被忽略/不生效。

    先运行 `SpringCoreAopAspectInstantiationModelLabTest`，用一正一反两套配置把“为什么 pertarget/perthis 表面上不生效”固化成断言：singleton aspect + 非 singleton per-clause → 不代理；prototype aspect → 生效且按需实例化。

    需要下探源码时，可以从 `org.springframework.aop.aspectj.annotation.BeanFactoryAspectJAdvisorsBuilder` / `org.springframework.aop.aspectj.annotation.AspectMetadata` / `org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory` 这些入口切入。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. 除 `@EnableAspectJAutoProxy` 之外：BeanNameAutoProxyCreator / ProxyFactoryBean / XML](autoproxy-and-pointcuts-other-configuration-entries.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 并发 / 性能：同一 proxy 并发调用边界（ThreadLocal 不串线）](perf-concurrency-proxy-concurrency-perf.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章解决一个“高级但很实用”的问题：

> 在 `@Aspect` 上写了 `@Aspect(\"pertarget(...)\" )`（或 perthis/pertypewithin），为什么在 Spring 里表面上没效果？

结论先行（本章会用 Lab 验证）：

- Spring AOP 支持 `singleton / perthis / pertarget / pertypewithin`（不支持 `percflow/percflowbelow`）
- **非 singleton per-clause 要求 aspect bean 是 prototype**
  - 如果把它当成普通 singleton bean，Spring 会认为配置不一致，并忽略该 aspect（表现为：目标 bean 没有被代理）

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreAopAspectInstantiationModelLabTest`

## 1) 支持范围（Spring AOP 语境）

从 Spring AOP 的实现约束出发，本章只强调“对排障有用的那一部分”：

- ✅ `singleton`：默认
- ✅ `pertarget(...)` / `perthis(...)` / `pertypewithin(...)`：可用，但别误以为等价于 weaving
- ❌ `percflow` / `percflowbelow`：Spring AOP 明确不支持（启动期会被判定为不兼容）

## 2) 最关键的工程结论：prototype gate

当 per-clause 不是 singleton 时：

- aspect bean 必须是 prototype
- 否则它会被当成“不兼容 aspect”，不会参与 advisors 构建

**这就是“写了但不生效”的最高频根因。**

## 3) Spring AOP 与 weaving 的语义差异（避免误用）

Spring AOP 是 proxy-based：

- 只拦截“通过 proxy 发起的 method execution”
- 不具备 weaving 那种“全 join point 模型”

因此：per-clause 这类语法在 Spring AOP 中更适合被理解为：

- 一种“延迟 materialize aspect 的条件/约束”
- 而不是“完全等价的 AspectJ 实例化语义”

真正需要 weaving 的场景见附录：[Weaving vs Proxy 决策表](appendix-weaving-vs-proxy-decision-matrix.md)。

## 小结与下一章

- perthis/pertarget/pertypewithin 能用，但要先过 prototype gate。
- 下一章进入并发/性能边界：proxy 可并发调用，但 advice 的状态必须隔离/可清理。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreAopAspectInstantiationModelLabTest`

上一章：[03-other-config-entries](autoproxy-and-pointcuts-other-configuration-entries.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[11-proxy-concurrency-perf](perf-concurrency-proxy-concurrency-perf.md)

<!-- BOOKIFY:END -->
