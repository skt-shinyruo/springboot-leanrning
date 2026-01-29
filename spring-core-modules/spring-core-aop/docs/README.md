# Spring Core AOP：目录

> 这一模块建议“从代理主线顺读”：先把代理心智模型与边界打牢，再进入 AutoProxyCreator 的主线，最后处理真实世界的多层代理叠加与排障。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/028-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/029-00-deep-dive-guide.md)
3. [AOP 调用链（从代理入口到 Advice 链执行）](part-00-guide/029-01-aop-invocation-call-chain.md)

## 顺读主线

- [代理心智模型](part-01-proxy-fundamentals/030-01-aop-proxy-mental-model.md)
- [JDK vs CGLIB](part-01-proxy-fundamentals/031-02-jdk-vs-cglib.md)
- [self-invocation](part-01-proxy-fundamentals/032-03-self-invocation.md)
- [代理限制（final 等）](part-01-proxy-fundamentals/033-04-final-and-proxy-limits.md)
- [exposeProxy](part-01-proxy-fundamentals/034-05-expose-proxy.md)
- [代理调试](part-01-proxy-fundamentals/035-06-debugging.md)
- [AutoProxyCreator 主线](part-02-autoproxy-and-pointcuts/036-07-autoproxy-creator-mainline.md)
- [切点表达式系统](part-02-autoproxy-and-pointcuts/037-08-pointcut-expression-system.md)
- [并发/性能边界：同一 proxy 并发调用（ThreadLocal 不串线）](part-02-perf-concurrency/042-11-proxy-concurrency-perf.md)
- [多层代理叠加](part-03-proxy-stacking/038-09-multi-proxy-stacking.md)
- [叠加排障手册](part-03-proxy-stacking/039-10-real-world-stacking-playbook.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[029-02-breakpoint-map.md](part-00-guide/029-02-breakpoint-map.md)
- AOP 调用链（源码主线锚点）：[029-01-aop-invocation-call-chain.md](part-00-guide/029-01-aop-invocation-call-chain.md)
- 关键分支矩阵（If/Then 收敛）：[029-04-branch-decision-matrix.md](part-00-guide/029-04-branch-decision-matrix.md)
- 并发/性能边界（ThreadLocal 不串线）：[042-11-proxy-concurrency-perf.md](part-02-perf-concurrency/042-11-proxy-concurrency-perf.md)
- 排障 playbook：[040-90-common-pitfalls.md](appendix/040-90-common-pitfalls.md)
- 自检清单：[041-99-self-check.md](appendix/041-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`
- 可跑入口（Branch Matrix - Proxy 基础）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyBranchMatrixLabTest test`
- 可跑入口（Branch Matrix - AutoProxy）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopAutoProxyBranchMatrixLabTest test`
- 可跑入口（Branch Matrix - 多代理叠加）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopStackingBranchMatrixLabTest test`
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-core-aop -Dtest=*ExerciseSolutionTest test`
- 可跑入口（并发/性能 Lab - 同一 proxy 并发调用边界）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/040-90-common-pitfalls.md)
- [自检](appendix/041-99-self-check.md)
