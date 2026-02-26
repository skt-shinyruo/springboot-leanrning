# 99 自检：AOP Weaving（织入：LTW/CTW）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘出口）"

    - 主线入口：`AspectjWeavingBookMatrixLabTest`
    - 分支入口：`AspectjLtwBranchMatrixLabTest`（LTW）/ `AspectjCtwBranchMatrixLabTest`（CTW）
    - 推荐先跑：`AspectjLtwLabTest` / `AspectjCtwLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（LTW/CTW）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`
- Branch Matrix（LTW）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`
- Branch Matrix（CTW）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjCtwBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](../part-00-guide/04-breakpoint-map.md)
- [关键分支矩阵](../part-00-guide/05-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](01-common-pitfalls.md)

## 自检题（每题都能落到 tests）

1. LTW 生效的最小前提是什么？你如何用断言证明“JVM 确实带了 -javaagent”？  
   - 证据入口：`AspectjLtwLabTest#ltw_testJvmIsStartedWithJavaAgent`
2. CTW 的最小前提是什么？你如何用断言证明“JVM 没带 aspectjweaver agent”但 weaving 仍然生效？  
   - 证据入口：`AspectjCtwLabTest#ctw_testJvmIsNotStartedWithAspectjJavaAgent` + `AspectjCtwLabTest#ctw_weavingWorksWithoutJavaAgent_forMethodExecutionAndCall`
3. weaving 与 proxy AOP 的根本差异是什么？你如何用一个“非 Spring 对象”证明 weaving 仍然能拦截？  
   - 证据入口：`AspectjLtwLabTest#ltw_canWeaveExecutionForNonSpringObjects`
4. self-invocation 在 weaving 下会不会绕过拦截？你如何用断言证明“outer/inner 都被织入”？  
   - 证据入口：`AspectjLtwLabTest#ltw_selfInvocationDoesNotBypassWeaving` / `AspectjCtwLabTest#ctw_selfInvocationIsStillIntercepted`
5. `call` 与 `execution` 的差异是什么？你如何用日志/断言证明两类 join point 都触发，并且 kind 不同？  
   - 证据入口：`AspectjLtwLabTest#ltw_callVsExecution_areDifferentJoinPointKinds` / `AspectjCtwLabTest#ctw_weavingWorksWithoutJavaAgent_forMethodExecutionAndCall`
6. weaving 能不能拦截构造器？你如何验证 constructor-call 与 constructor-execution 都会被记录？  
   - 证据入口：`AspectjLtwLabTest#ltw_constructorCallAndExecution_canBeIntercepted` / `AspectjCtwLabTest#ctw_constructorAndFieldJoinPoints_areSupported`
7. weaving 能不能拦截字段读写？你如何验证 field-get/field-set 的 join point？  
   - 证据入口：`AspectjLtwLabTest#ltw_fieldGetAndSet_canBeIntercepted` / `AspectjCtwLabTest#ctw_constructorAndFieldJoinPoints_areSupported`
8. `withincode` 与 `cflow` 各解决什么问题？你如何用对照用例证明“只在指定调用者/控制流下生效”？  
   - 证据入口：`AspectjLtwLabTest#ltw_withincode_limitsJoinPointByCallerMethodBody` + `AspectjLtwLabTest#ltw_cflow_limitsJoinPointByControlFlow` / `AspectjCtwLabTest#ctw_withincodeAndCflow_workAsAdvancedPointcuts`
9. 并发下 weaving 的事件记录是否会串线？你如何用并发实验把它固定成结论？  
   - 证据入口：`AspectjLtwConcurrencyLabTest#ltw_concurrentInvocation_recordsEventsWithCorrectThreadNames`

## 退出条件（完成标准）

- 你能先分流：这是 proxy 世界的问题，还是 weaving 世界的问题（LTW/CTW）？
- 你能用“是否有 agent/是否为织入产物 + join point kind + 调用链位置”三条证据定位“为什么没拦截”。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`

上一章：[90-common-pitfalls](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
