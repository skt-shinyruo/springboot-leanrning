# 第 29 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：把 Spring AOP 的关键分支（代理创建、自调用、ExposeProxy、advisor 顺序）整理成矩阵表，并给出最小复现入口。
    - 原理：分支发生在：是否被 AutoProxyCreator 代理、调用是否经过 proxy、advice 链的顺序与 proceed 嵌套。
    - 源码入口：`AbstractAutoProxyCreator` / `ReflectiveMethodInvocation`
    - 推荐 Lab：`SpringCoreAopProxyBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 29 章：02：断点地图（Spring AOP Debugger Pack）](029-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 30 章：01：代理心智模型：你拿到的到底是不是“那个对象”】【From Proxy to Target】](../part-01-proxy-fundamentals/030-01-aop-proxy-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

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

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`SpringCoreAopProxyBranchMatrixLabTest` / `SpringCoreAopAutoProxyBranchMatrixLabTest` / `SpringCoreAopStackingBranchMatrixLabTest`
- Lab：`SpringCoreAopExposeProxyLabTest` / `SpringCoreAopRealWorldStackingLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](029-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-proxy-fundamentals/01-aop-proxy-mental-model.md](../part-01-proxy-fundamentals/030-01-aop-proxy-mental-model.md)

<!-- BOOKIFY:END -->

