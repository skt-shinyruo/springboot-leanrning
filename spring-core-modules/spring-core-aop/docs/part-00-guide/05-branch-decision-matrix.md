# 05. 关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：把 Spring AOP 的关键分支（代理创建、自调用、ExposeProxy、advisor 顺序）整理成矩阵表，并给出最小复现入口。
    - 原理：分支发生在：是否被 AutoProxyCreator 代理、调用是否经过 proxy、advice 链的顺序与 proceed 嵌套。
    - 源码入口：`AbstractAutoProxyCreator` / `ReflectiveMethodInvocation`
    - 推荐 Lab：`SpringCoreAopProxyBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Spring AOP Debugger Pack）](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. AOP 心智模型：代理（Proxy）+ 入口（Call Path）](../part-01-proxy-fundamentals/01-aop-proxy-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05. 关键分支矩阵（Branch Decision Matrix）**
- 建议入口：优先运行 `SpringCoreAopProxyBranchMatrixLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：分支发生在：是否被 AutoProxyCreator 代理、调用是否经过 proxy、advice 链的顺序与 proceed 嵌套。
- 源码入口：`AbstractAutoProxyCreator` / `ReflectiveMethodInvocation`



## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 是否创建代理 | bean 匹配 advisor/pointcut | `AopUtils.isAopProxy==true` | `SpringCoreAopAutoProxyCreatorInternalsLabTest` | `postProcessAfterInitialization` |
| 自调用边界 | 同 bean 内部调用 | advice 不生效（绕过 proxy） | `SpringCoreAopLabTest` | 调用栈不进 `CglibAopProxy` |
| ExposeProxy | 开启 exposeProxy | 允许从 AopContext 获取 proxy 再调用 | `SpringCoreAopExposeProxyLabTest` | `AopContext.currentProxy()` |
| 多层代理顺序 | 多个 advisor/嵌套 proceed | order 影响执行顺序 | `SpringCoreAopRealWorldStackingLabTest` | advisors 顺序 / proceed 链 |
| this vs target 命中差异 | JDK vs CGLIB 代理类型不同 | JDK 下 `this(实现类)` 不命中、`target(实现类)` 命中 | `SpringCoreAopPointcutExpressionsLabTest` | `AopUtils.isJdkDynamicProxy` / pointcut 表达式 |
| 并发上下文隔离 | 同一 proxy 被多线程并发调用 | ThreadLocal 状态不跨线程串线 | `SpringCoreAopProxyConcurrencyLabTest` | 线程名 / ThreadLocal 值 / `proceed()` |

## 推荐运行命令

- `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

- 小结：分支发生在：是否被 AutoProxyCreator 代理、调用是否经过 proxy、advice 链的顺序与 proceed 嵌套。
- 下一章：[第 30 章：01：代理心智模型：你拿到的到底是不是“那个对象”】【From Proxy to Target】](../part-01-proxy-fundamentals/01-aop-proxy-mental-model.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`SpringCoreAopProxyBranchMatrixLabTest` / `SpringCoreAopAutoProxyBranchMatrixLabTest` / `SpringCoreAopStackingBranchMatrixLabTest`
- Lab：`SpringCoreAopExposeProxyLabTest` / `SpringCoreAopRealWorldStackingLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-proxy-fundamentals/01-aop-proxy-mental-model.md](../part-01-proxy-fundamentals/01-aop-proxy-mental-model.md)

<!-- BOOKIFY:END -->
