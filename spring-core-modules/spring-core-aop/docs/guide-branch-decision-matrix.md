# 05. 关键分支矩阵
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵展开，主线可以概括为：分支发生在：是否被 AutoProxyCreator 代理、调用是否经过 proxy、advice 链的顺序与 proceed 嵌套。

    把 Spring AOP 的关键分支（代理创建、自调用、ExposeProxy、advisor 顺序）整理成矩阵表，并给出最小复现入口。

    对照入口：`SpringCoreAopProxyBranchMatrixLabTest`。需要下探源码时，可以从 `AbstractAutoProxyCreator` / `ReflectiveMethodInvocation` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Spring AOP）](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. AOP 心智模型：代理（Proxy）+ 入口（Call Path）](proxy-fundamentals-aop-proxy-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `SpringCoreAopProxyBranchMatrixLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：分支发生在：是否被 AutoProxyCreator 代理、调用是否经过 proxy、advice 链的顺序与 proceed 嵌套。需要下探源码时，可以从 `AbstractAutoProxyCreator` / `ReflectiveMethodInvocation` 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点 |
|---|---|---|---|---|
| 是否创建代理 | bean 匹配 advisor/pointcut | `AopUtils.isAopProxy==true` | `SpringCoreAopAutoProxyCreatorInternalsLabTest` | `postProcessAfterInitialization` |
| 自调用边界 | 同 bean 内部调用 | advice 不生效（绕过 proxy） | `SpringCoreAopLabTest` | 调用栈不进 `CglibAopProxy` |
| ExposeProxy | 开启 exposeProxy | 允许从 AopContext 获取 proxy 再调用 | `SpringCoreAopExposeProxyLabTest` | `AopContext.currentProxy()` |
| 多层代理顺序 | 多个 advisor/嵌套 proceed | order 影响执行顺序 | `SpringCoreAopRealWorldStackingLabTest` | advisors 顺序 / proceed 链 |
| this vs target 命中差异 | JDK vs CGLIB 代理类型不同 | JDK 下 `this(实现类)` 不命中、`target(实现类)` 命中 | `SpringCoreAopPointcutExpressionsLabTest` | `AopUtils.isJdkDynamicProxy` / pointcut 表达式 |
| 并发上下文隔离 | 同一 proxy 被多线程并发调用 | ThreadLocal 状态不跨线程串线 | `SpringCoreAopProxyConcurrencyLabTest` | 线程名 / ThreadLocal 值 / `proceed()` |

## 运行命令

- `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

分支发生在：是否被 AutoProxyCreator 代理、调用是否经过 proxy、advice 链的顺序与 proceed 嵌套。

下一章见：[01：代理心智模型：拿到的到底是不是“那个对象”】【From Proxy to Target】](proxy-fundamentals-aop-proxy-mental-model.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`SpringCoreAopProxyBranchMatrixLabTest` / `SpringCoreAopAutoProxyBranchMatrixLabTest` / `SpringCoreAopStackingBranchMatrixLabTest`
- Lab：`SpringCoreAopExposeProxyLabTest` / `SpringCoreAopRealWorldStackingLabTest`

上一章：[guide-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[proxy-fundamentals-aop-proxy-mental-model.md](proxy-fundamentals-aop-proxy-mental-model.md)

<!-- BOOKIFY:END -->
