# 第 29 章：02：断点地图（Spring AOP Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Spring AOP Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
    - 原理：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
    - 源码入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`
    - 推荐 Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 29 章：00 - Deep Dive Guide（spring-core-aop）](029-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 29 章：04：关键分支矩阵（Branch Decision Matrix）](029-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Spring AOP Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
- 回到主线：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 怎么用这页

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「02：断点地图（Spring AOP Debugger Pack）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopAutoProxyCreatorInternalsLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

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

- Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopBookMatrixLabTest` / `SpringCoreAopProxyBranchMatrixLabTest` / `SpringCoreAopAutoProxyBranchMatrixLabTest` / `SpringCoreAopStackingBranchMatrixLabTest`

上一章：[叠加排障手册](../part-03-proxy-stacking/039-10-real-world-stacking-playbook.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[029-01-aop-invocation-call-chain.md](029-01-aop-invocation-call-chain.md)

<!-- BOOKIFY:END -->
