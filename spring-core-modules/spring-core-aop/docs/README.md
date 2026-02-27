# Spring Core AOP：代理、切点与叠加边界

本模块把 AOP 的关键问题放回“代理对象”这一事实：代理何时产生、JDK/CGLIB 的选择如何影响边界、自调用为何绕过切面、以及多个代理叠加时如何定位最终执行的 Advice 链。阅读与排障的核心策略是先跑通代理主线，再进入 AutoProxyCreator 的装配主线，最后处理真实工程中常见的多层代理叠加。

---

## 10 分钟入口：先跑通一次 Advice 链执行

- `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`

运行后应能回答：代理入口在哪里；Advice 链的执行顺序如何形成；自调用为何会绕过代理边界。

## Beans 前置（强烈建议先读一次）

AOP 文档的很多“看起来像 AOP 的问题”，根因其实发生在 **Bean 创建阶段**（什么时候被替换成 proxy / early reference 如何参与循环依赖）。

建议先用 Beans 的两章把“代理替换发生在哪个阶段”建立成稳定心智模型：

- [Beans Why Index（基础问题索引）](../../spring-core-beans/docs/part-00-guide/01-why-index.md)
- [Beans：代理替换发生在哪个阶段](../../spring-core-beans/docs/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md)
- [Beans：early reference 与循环依赖](../../spring-core-beans/docs/part-03-container-internals/05-early-reference-and-circular.md)

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)
3. [AOP 调用链（从代理入口到 Advice 链执行）](part-00-guide/03-aop-invocation-call-chain.md)

## 顺读主线

- [代理心智模型](part-01-proxy-fundamentals/01-aop-proxy-mental-model.md)
- [JDK vs CGLIB](part-01-proxy-fundamentals/02-jdk-vs-cglib.md)
- [self-invocation](part-01-proxy-fundamentals/03-self-invocation.md)
- [代理限制（final 等）](part-01-proxy-fundamentals/04-final-and-proxy-limits.md)
- [exposeProxy](part-01-proxy-fundamentals/05-expose-proxy.md)
- [代理调试](part-01-proxy-fundamentals/06-debugging.md)
- [AutoProxyCreator 主线](part-02-autoproxy-and-pointcuts/01-autoproxy-creator-mainline.md)
- [切点表达式系统](part-02-autoproxy-and-pointcuts/02-pointcut-expression-system.md)
- [并发/性能边界：同一 proxy 并发调用（ThreadLocal 不串线）](part-02-perf-concurrency/01-proxy-concurrency-perf.md)
- [多层代理叠加](part-03-proxy-stacking/01-multi-proxy-stacking.md)
- [叠加排障手册](part-03-proxy-stacking/02-real-world-stacking-playbook.md)

---

## 排障入口（从症状回到最短分支）

- 断点地图（排障优先）：[04-breakpoint-map.md](part-00-guide/04-breakpoint-map.md)
- AOP 调用链（源码主线锚点）：[03-aop-invocation-call-chain.md](part-00-guide/03-aop-invocation-call-chain.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](part-00-guide/05-branch-decision-matrix.md)
- 并发/性能边界（ThreadLocal 不串线）：[01-proxy-concurrency-perf.md](part-02-perf-concurrency/01-proxy-concurrency-perf.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`
- Branch Matrix（Proxy 基础）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyBranchMatrixLabTest test`
- Branch Matrix（AutoProxy）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopAutoProxyBranchMatrixLabTest test`
- Branch Matrix（多代理叠加）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopStackingBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-core-aop -Dtest=*ExerciseSolutionTest test`
- 并发/性能（同一 proxy 并发调用边界）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
