# 第 29 章：02：断点地图（Spring AOP Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Spring AOP Debugger Pack）
    - 怎么使用：先跑 3 个 Branch Matrix（Proxy/AutoProxy/Stacking）固化关键分支，再用断点观察代理创建点、拦截点与 advice 链路。
    - 原理：bean 初始化后由 AutoProxyCreator 决定是否创建代理；调用时由 `MethodInvocation#proceed` 驱动 advice 链；自调用/最终方法等是典型边界。
    - 源码入口：`AbstractAutoProxyCreator` / `ProxyFactory` / `CglibAopProxy` / `ReflectiveMethodInvocation`
    - 推荐 Lab：`SpringCoreAopProxyBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 29 章：00 - Deep Dive Guide（spring-core-aop）](029-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 29 章：04：关键分支矩阵（Branch Decision Matrix）](029-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 运行入口（建议先跑）

- Book Matrix：`SpringCoreAopBookMatrixLabTest`
- Branch Matrix（Proxy）：`SpringCoreAopProxyBranchMatrixLabTest`
- Branch Matrix（AutoProxy）：`SpringCoreAopAutoProxyBranchMatrixLabTest`
- Branch Matrix（Stacking）：`SpringCoreAopStackingBranchMatrixLabTest`

## 断点：代理创建（决定“有没有 AOP”）

- `org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization`
- `org.springframework.aop.framework.ProxyFactory#getProxy`

## 断点：调用拦截（决定“执行了哪些 advice”）

- `org.springframework.aop.framework.CglibAopProxy.DynamicAdvisedInterceptor#intercept`
- `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`

## Watchpoints（建议）

- `AopUtils.isAopProxy(bean)` / `AopUtils.isCglibProxy(bean)`
- `Advised#getAdvisors()`（advisor 列表与顺序）
- 调用栈是否进入 `ReflectiveMethodInvocation#proceed`（否则没有走 AOP）

## 排障入口（Playbook）

- 常见坑：[`../appendix/040-90-common-pitfalls.md`](../appendix/040-90-common-pitfalls.md)
- 自检：[`../appendix/041-99-self-check.md`](../appendix/041-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`SpringCoreAopProxyBranchMatrixLabTest` / `SpringCoreAopAutoProxyBranchMatrixLabTest` / `SpringCoreAopStackingBranchMatrixLabTest`
- Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopRealWorldStackingLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](029-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](029-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

