# 99 自检：Spring Core AOP
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（复盘出口）"

    - 主线入口：`SpringCoreAopBookMatrixLabTest`
    - 分支入口：`SpringCoreAopProxyBranchMatrixLabTest`（代理基础）/ `SpringCoreAopAutoProxyBranchMatrixLabTest`（AutoProxy）/ `SpringCoreAopStackingBranchMatrixLabTest`（叠加与顺序）
    - 入口：`SpringCoreAopLabTest` / `SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopPointcutExpressionsLabTest`
    - 专题补齐（本仓库新增覆盖）：`SpringCoreAopAdviceTypesAndBindingLabTest` / `SpringCoreAopIntroductionDeclareParentsLabTest` / `SpringCoreAopTargetSourceLabTest` / `SpringCoreAopProxyObjectSemanticsLabTest`
    - 遗留入口/成本模型：`SpringCoreAopBeanNameAutoProxyCreatorLabTest` / `SpringCoreAopXmlAopConfigLabTest` / `SpringCoreAopRuntimePointcutCostLabTest` / `SpringCoreAopAspectInstantiationModelLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（排查时对照）](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页不是新的主线章节，而是把已读过的机制拿回来验证、排障和自检。读法如下：

1. 先运行 Book Matrix、Branch Matrix 或本页列出的最小 Lab，把现象固定成可重复结果。
2. 再按现象、题目或坑点定位对应章节、断点和关键变量。
3. 最后用对应实验/测试 收束答案；如果答案仍然只停留在概念层面，再回到正文补齐机制。

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`
- Branch Matrix（代理基础）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyBranchMatrixLabTest test`
- Branch Matrix（AutoProxy）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopAutoProxyBranchMatrixLabTest test`
- Branch Matrix（叠加与顺序）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopStackingBranchMatrixLabTest test`

补齐专题（按需挑选）：

- Advice 全家桶语义/绑定：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopAdviceTypesAndBindingLabTest test`
- Introduction / Mixin：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopIntroductionDeclareParentsLabTest test`
- TargetSource：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopTargetSourceLabTest test`
- Proxy 对象语义：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyObjectSemanticsLabTest test`
- 动态切点成本：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopRuntimePointcutCostLabTest test`
- 遗留入口（BeanName/XML）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBeanNameAutoProxyCreatorLabTest,SpringCoreAopXmlAopConfigLabTest test`
- Aspect 实例模型（prototype gate）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopAspectInstantiationModelLabTest test`

配套资料（排障更快）：

- [断点地图](guide-breakpoint-map.md)
- [关键分支矩阵](guide-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](appendix-common-pitfalls.md)

## 自检题

1. Spring AOP 的“增强”发生在容器的哪个阶段？如何在断点里看到 proxy 替换目标对象？
   - 证据入口：`SpringCoreAopAutoProxyCreatorInternalsLabTest#autoProxyCreator_isRegisteredAsBeanPostProcessor_whenEnableAspectJAutoProxyIsUsed`
2. “AOP 生效”的两个前提分别是什么？如何用一条负向用例证明“绕过 proxy 就不会拦截”？
   - 证据入口：`SpringCoreAopLabTest#selfInvocationDoesNotTriggerAdviceForInnerMethod`
3. JDK 动态代理与 CGLIB 代理的差异会如何影响“按类型注入/获取”？如何把差异跑成断言？
   - 证据入口：`SpringCoreAopProxyMechanicsLabTest#jdkDynamicProxyIsUsedForInterfaceBasedBeans_whenProxyTargetClassIsFalse`
4. Advice 全家桶里，success path 与 error path 的执行顺序分别是什么？哪些 advice “一定执行”、哪些“只在成功/失败”执行？
   - 证据入口：`SpringCoreAopAdviceTypesAndBindingLabTest`
5. `returning`/`throwing`/`args`/`@annotation`/`JoinPoint` 分别能绑定到什么？绑定失败最常见的根因是什么？
   - 证据入口：`SpringCoreAopAdviceTypesAndBindingLabTest`
6. Introduction（@DeclareParents）到底改变了谁：proxy 还是 target？为什么 `instanceof NewInterface` 会成立？
   - 证据入口：`SpringCoreAopIntroductionDeclareParentsLabTest`
7. TargetSource 是什么？如何用最小事实证明 “proxy 不变，但 target 可切换/延迟创建”？
   - 证据入口：`SpringCoreAopTargetSourceLabTest`
8. proxy 的对象语义有哪些坑（getClass/instanceof/Map key）？如何在断点里最短自证“当前拿到的是 proxy 还是 target”？
   - 证据入口：`SpringCoreAopProxyObjectSemanticsLabTest`
9. pointcut 的核心语义是什么？如何避免“表达式写对了，但入口没走到代理”的误判？
   - 证据入口：`SpringCoreAopPointcutExpressionsLabTest#this_vs_target_differs_between_JdkProxy_and_CglibProxy`
10. 什么是运行期匹配（runtime matcher）？为什么它会带来 per-invocation 成本？如何写最小用例证明？
    - 证据入口：`SpringCoreAopRuntimePointcutCostLabTest`
11. 没有 `@EnableAspectJAutoProxy` / `@Aspect` 时，AOP 还能从哪来？如何快速识别 BeanNameAutoProxyCreator / XML `<aop:config>`？
    - 证据入口：`SpringCoreAopBeanNameAutoProxyCreatorLabTest` / `SpringCoreAopXmlAopConfigLabTest`
12. 为什么写了 `@Aspect("pertarget(...)")` 但表面上“不生效”？prototype gate 是什么？
    - 证据入口：`SpringCoreAopAspectInstantiationModelLabTest`
13. proxy 为什么可以并发调用？哪些状态会在并发下串线？如何把 ThreadLocal 边界写成可回归用例？
    - 证据入口：`SpringCoreAopProxyConcurrencyLabTest#proxyInvocation_isThreadIsolated_underConcurrentCalls`
14. 多切面时，顺序影响的到底是“advisor/interceptor 链”，还是“容器阶段的 BPP 顺序”？两类顺序分别去哪里观察？
    - 证据入口：`SpringCoreAopMultiProxyStackingLabTest`
15. Weaving vs Proxy：哪些问题 proxy 永远解决不了？遇到这些问题会怎么决策/排障？
    - 证据入口：`appendix-weaving-vs-proxy-decision-matrix.md`
16. 当遇到“不拦截”的问题，稳定的排查顺序是什么？（至少覆盖：入口是否为容器 bean / 是否为 AOP proxy / advisor 是否存在 / 拦截器链是否包含目标 advice）
    - 证据入口：`SpringCoreAopRealWorldStackingLabTest`

## 退出条件（完成标准）

- 能把 AOP 描述为两段事实链：容器阶段（为何生成 proxy）与调用阶段（为何进入拦截器链）。
- 能用断点与断言回答：“有没有 proxy、有哪些 advisors、这次调用挂了哪些拦截器、顺序如何”，而不是依赖日志猜测。

## 动手题（直接做 练习）

- 让自调用也触发 advice：启用 exposeProxy，并完成 `SpringCoreAopExerciseTest#exercise_makeSelfInvocationTriggerAdvice`
- 新增一个 `@Order(0)` 的切面，并证明它会在现有切面之前执行：`SpringCoreAopExerciseTest#exercise_addOrderedAspect`
- 把 pointcut 从 `@annotation` 改成 `execution(...)`，并更新测试：`SpringCoreAopExerciseTest#exercise_changePointcutStyle`

## 常见坑索引（本页不重复坑正文）

- 对照：[`01-common-pitfalls.md`](appendix-common-pitfalls.md)

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreAopLabTest` / `SpringCoreAopProxyMechanicsLabTest` / `SpringCoreAopAdviceTypesAndBindingLabTest` / `SpringCoreAopIntroductionDeclareParentsLabTest` / `SpringCoreAopTargetSourceLabTest` / `SpringCoreAopProxyObjectSemanticsLabTest`
- Lab：`SpringCoreAopAutoProxyCreatorInternalsLabTest` / `SpringCoreAopPointcutExpressionsLabTest` / `SpringCoreAopRuntimePointcutCostLabTest` / `SpringCoreAopBeanNameAutoProxyCreatorLabTest` / `SpringCoreAopXmlAopConfigLabTest` / `SpringCoreAopAspectInstantiationModelLabTest`
- Lab：`SpringCoreAopMultiProxyStackingLabTest` / `SpringCoreAopRealWorldStackingLabTest` / `SpringCoreAopProxyConcurrencyLabTest`
- Exercise：`SpringCoreAopExerciseTest`

上一章：[90-common-pitfalls](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)

<!-- BOOKIFY:END -->
