# 99 自检：Business Case（综合案例）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘出口）"

    - 主线入口：`BootBusinessCaseBookMatrixLabTest`
    - 分支入口：`BootBusinessCaseBranchMatrixLabTest`
    - 推荐先跑：`BootBusinessCaseLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 90 - Common Pitfalls（springboot-business-case）](appendix-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](guide-breakpoint-map.md)
- [关键分支矩阵](guide-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](appendix-common-pitfalls.md)

## 自检题（每题都能落到 tests）

1. 输入校验失败时，哪些东西应该发生、哪些不该发生？如何用一条用例证明“不会落库、不会产生副作用事件”？
   - 证据入口：`BootBusinessCaseLabTest#returnsValidationErrorWhenRequestIsInvalid`
2. 请求成功时，如何用断言同时固定三件事：HTTP 返回正确、数据落库、事件副作用发生在正确时机？
   - 证据入口：`BootBusinessCaseLabTest#createsOrderWhenRequestIsValid`
3. 如何证明 `OrderService` 真的走了 AOP 代理（不是直接 new 出来的对象）？
   - 证据入口：`BootBusinessCaseLabTest#serviceBeanIsAnAopProxy`
4. tracing aspect 在哪里记录调用？如何在一次请求里观察它命中了关心的方法？
   - 证据入口：`BootBusinessCaseLabTest#aspectRecordsInvocationForTracedOperation`
5. 事务回滚时，“数据库状态”和“事件副作用”分别会怎样？为什么 sync listener 与 afterCommit listener 会出现分流？
   - 证据入口：`BootBusinessCaseLabTest#rollbackPreventsPersistenceOnFailure` + `BootBusinessCaseLabTest#syncListenerRunsEvenWhenTransactionRollsBack_butAfterCommitDoesNot`
6. 成功提交时 afterCommit listener 为什么会触发？如何把“只在提交后发生”写成断言？
   - 证据入口：`BootBusinessCaseLabTest#afterCommitListenerRunsOnSuccess`
7. 这个模块的“幂等”是什么语义？为什么同样请求两次会有两条订单？
   - 证据入口：`BootBusinessCaseLabTest#createOrderIsIdempotentAtDatabaseLevel_perRequestOnly`
8. 如何定义“边界排障顺序”（MVC → Security → Tx → JPA → Events → Observability）？遇到红测/异常时，第一步会选哪个入口把它跑成事实？
   - 对照：`BootBusinessCaseBookMatrixLabTest` / `BootBusinessCaseBranchMatrixLabTest`
9. 练习：把对某个边界的理解固化成一条“可回归”的服务级用例（不要停留在名词解释）。
   - 入口：`BootBusinessCaseExerciseTest`

## 退出条件（完成标准）

- 能把一次业务请求拆成“边界链路”，并能指出每个边界的证据入口（至少 1 条测试用例）。
- 能用“数据是否落库 + 事件时机”作为最终证据，避免只看日志/异常做判断。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`
- Exercise：`BootBusinessCaseExerciseTest`

上一章：[appendix/90-common-pitfalls.md](appendix-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
