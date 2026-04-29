# 04. 断点地图（Spring AOP）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（Spring AOP）展开，主线可以概括为：bean 初始化后由 AutoProxyCreator 决定是否创建代理；调用时由 `MethodInvocation#proceed` 驱动 advice 链；自调用/最终方法等是典型边界。

    先跑 3 个 Branch Matrix（Proxy/AutoProxy/Stacking）固化关键分支，再用断点观察代理创建点、拦截点与 advice 链路。

    对照入口：`SpringCoreAopProxyBranchMatrixLabTest`。需要下探源码时，可以从 `AbstractAutoProxyCreator` / `ProxyFactory` / `CglibAopProxy` / `ReflectiveMethodInvocation` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南：把“代理是怎么来的、advice 链怎么跑”落到源码与断点](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `SpringCoreAopProxyBranchMatrixLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：bean 初始化后由 AutoProxyCreator 决定是否创建代理；调用时由 `MethodInvocation#proceed` 驱动 advice 链；自调用/最终方法等是典型边界。需要下探源码时，可以从 `AbstractAutoProxyCreator` / `ProxyFactory` / `CglibAopProxy` / `ReflectiveMethodInvocation` 这些入口切入。


## 运行入口（先运行）

- Book Matrix：`SpringCoreAopBookMatrixLabTest`
- Branch Matrix（Proxy）：`SpringCoreAopProxyBranchMatrixLabTest`
- Branch Matrix（AutoProxy）：`SpringCoreAopAutoProxyBranchMatrixLabTest`
- Branch Matrix（Stacking）：`SpringCoreAopStackingBranchMatrixLabTest`
- proceed 嵌套（调用链顺序可断言）：`SpringCoreAopProceedNestingLabTest`
- 真实基础设施叠加（Tx/Cache/Security）：`SpringCoreAopRealWorldStackingLabTest`
- 并发/性能边界（ThreadLocal 不串线）：`SpringCoreAopProxyConcurrencyLabTest`

## 断点：代理创建（决定“有没有 AOP”）

- `org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization`
- `org.springframework.aop.framework.ProxyFactory#getProxy`

## 断点：调用拦截（决定“执行了哪些 advice”）

- `org.springframework.aop.framework.CglibAopProxy.DynamicAdvisedInterceptor#intercept`
- `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`

## 断点：链条组装（决定“这次调用挂了哪些拦截器”）

- `org.springframework.aop.framework.DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice`

## 观察点

- `AopUtils.isAopProxy(bean)` / `AopUtils.isCglibProxy(bean)`
- `Advised#getAdvisors()`（advisor 列表与顺序）
- 调用栈是否进入 `ReflectiveMethodInvocation#proceed`（否则没有走 AOP）
- `interceptorsAndDynamicMethodMatchers` / `currentInterceptorIndex`（链条顺序与执行推进）

## 排障入口（Playbook）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

bean 初始化后由 AutoProxyCreator 决定是否创建代理；调用时由 `MethodInvocation#proceed` 驱动 advice 链；自调用/最终方法等是典型边界。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`SpringCoreAopProxyBranchMatrixLabTest` / `SpringCoreAopAutoProxyBranchMatrixLabTest` / `SpringCoreAopStackingBranchMatrixLabTest`
- Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopRealWorldStackingLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
