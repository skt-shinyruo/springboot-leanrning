# 第 29 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
    - 原理：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
    - 源码入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`
    - 推荐 Lab：`SpringCoreAopExposeProxyLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 29 章：02：断点地图（Spring AOP Debugger Pack）](029-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 30 章：01：代理心智模型：你拿到的到底是不是“那个对象”】【From Proxy to Target】](../part-01-proxy-fundamentals/030-01-aop-proxy-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：04：关键分支矩阵（Branch Decision Matrix） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
- 回到主线：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「04：关键分支矩阵（Branch Decision Matrix）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopExposeProxyLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 是否创建代理 | bean 匹配 advisor/pointcut | `AopUtils.isAopProxy==true` | `SpringCoreAopAutoProxyCreatorInternalsLabTest` | `postProcessAfterInitialization` |
| 自调用边界 | 同 bean 内部调用 | advice 不生效（绕过 proxy） | `SpringCoreAopLabTest` | 调用栈不进 `CglibAopProxy` |
| ExposeProxy | 开启 exposeProxy | 允许从 AopContext 获取 proxy 再调用 | `SpringCoreAopExposeProxyLabTest` | `AopContext.currentProxy()` |
| 多层代理顺序 | 多个 advisor/嵌套 proceed | order 影响执行顺序 | `SpringCoreAopRealWorldStackingLabTest` | advisors 顺序 / proceed 链 |

## 推荐运行命令

- `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/040-90-common-pitfalls.md`](../appendix/040-90-common-pitfalls.md)
- 自检：[`../appendix/041-99-self-check.md`](../appendix/041-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.aop.framework.ProxyFactory`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreAopExposeProxyLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopExposeProxyLabTest` / `SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopLabTest` / `SpringCoreAopRealWorldStackingLabTest` / `SpringCoreAopProxyBranchMatrixLabTest`

上一章：[029-01-aop-invocation-call-chain.md](029-01-aop-invocation-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/040-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
