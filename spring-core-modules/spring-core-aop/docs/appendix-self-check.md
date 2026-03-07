# 99 自检：Spring Core AOP
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘出口）"

    - 主线入口：`SpringCoreAopBookMatrixLabTest`
    - 分支入口：`SpringCoreAopProxyBranchMatrixLabTest`（代理基础）/ `SpringCoreAopAutoProxyBranchMatrixLabTest`（AutoProxy）/ `SpringCoreAopStackingBranchMatrixLabTest`（叠加与顺序）
    - 推荐先跑：`SpringCoreAopLabTest` / `SpringCoreAopPointcutExpressionsLabTest` / `SpringCoreAopAutoProxyCreatorInternalsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（建议反复对照）](appendix-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`
- Branch Matrix（代理基础）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyBranchMatrixLabTest test`
- Branch Matrix（AutoProxy）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopAutoProxyBranchMatrixLabTest test`
- Branch Matrix（叠加与顺序）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopStackingBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](guide-breakpoint-map.md)
- [关键分支矩阵](guide-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](appendix-common-pitfalls.md)

## 自检题（每题都能落到 tests）

1. Spring AOP 的“增强”发生在容器的哪个阶段？如何在断点里看到 proxy 替换目标对象？
   - 证据入口：`SpringCoreAopAutoProxyCreatorInternalsLabTest#autoProxyCreator_isRegisteredAsBeanPostProcessor_whenEnableAspectJAutoProxyIsUsed`
2. “AOP 生效”的两个前提分别是什么？如何用一条负向用例证明“绕过 proxy 就不会拦截”？
   - 证据入口：`SpringCoreAopLabTest#selfInvocationDoesNotTriggerAdviceForInnerMethod`
3. JDK 动态代理与 CGLIB 代理的差异会如何影响“按类型注入/获取”？如何把差异跑成断言？
   - 证据入口：`SpringCoreAopProxyMechanicsLabTest#proxyType_differsBetweenJdkAndCglib`
4. pointcut 的核心语义是什么？如何避免“表达式写对了，但入口没走到代理”的误判？
   - 证据入口：`SpringCoreAopPointcutExpressionsLabTest#pointcut_matches_and_invocation_goes_through_proxy`
5. `execution/within/this/target` 各自控制的“范围”是什么？如何用对照用例证明差异（尤其在 JDK proxy 下）？
   - 证据入口：`SpringCoreAopPointcutExpressionsLabTest#this_vs_target_differs_between_JdkProxy_and_CglibProxy`
6. `@annotation/@within/@target` 的差异是什么？如何在项目内提供一个可复现入口，而不是靠记忆对照表？
   - 证据入口：`SpringCoreAopPointcutExpressionsLabTest`
7. 多切面时，顺序影响的到底是“advisor/interceptor 链”，还是“容器阶段的 BPP 顺序”？两类顺序分别去哪里观察？
   - 证据入口：`SpringCoreAopMultiProxyStackingLabTest`
8. 为什么“构造器/初始化阶段内部调用”容易造成增强误判？如何用断点证明“调用发生在 proxy 生成之前”？
   - 证据入口：`SpringCoreAopAutoProxyCreatorInternalsLabTest`
9. proxy 为什么可以并发调用？哪些状态会在并发下串线？如何把 ThreadLocal 边界写成可回归用例？
   - 证据入口：`SpringCoreAopProxyConcurrencyLabTest#proxyInvocation_isThreadIsolated_underConcurrentCalls`
10. 当遇到“不拦截”的问题，稳定的排查顺序是什么？（至少覆盖：入口是否为容器 bean / 是否为 AOP proxy / advisor 是否存在 / 拦截器链是否包含目标 advice）
    - 证据入口：`SpringCoreAopRealWorldStackingLabTest`

## 退出条件（完成标准）

- 能把 AOP 描述为两段事实链：容器阶段（为何生成 proxy）与调用阶段（为何进入拦截器链）。
- 能用断点与断言回答：“有没有 proxy、有哪些 advisors、这次调用挂了哪些拦截器、顺序如何”，而不是依赖日志猜测。

## 动手题（建议直接做 Exercises）

- 让自调用也触发 advice：启用 exposeProxy，并完成 `SpringCoreAopExerciseTest#exercise_makeSelfInvocationTriggerAdvice`
- 新增一个 `@Order(0)` 的切面，并证明它会在现有切面之前执行：`SpringCoreAopExerciseTest#exercise_addOrderedAspect`
- 把 pointcut 从 `@annotation` 改成 `execution(...)`，并更新测试：`SpringCoreAopExerciseTest#exercise_changePointcutStyle`

## 常见坑索引（本页不重复坑正文）

- 建议对照：[`01-common-pitfalls.md`](appendix-common-pitfalls.md)

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreAopLabTest` / `SpringCoreAopProxyMechanicsLabTest` / `SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopPointcutExpressionsLabTest` / `SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopRealWorldStackingLabTest` / `SpringCoreAopProxyConcurrencyLabTest`
- Exercise：`SpringCoreAopExerciseTest`

上一章：[90-common-pitfalls](appendix-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
