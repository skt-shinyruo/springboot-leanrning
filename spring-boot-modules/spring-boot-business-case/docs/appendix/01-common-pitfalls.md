# 01. 90 - Common Pitfalls（springboot-business-case）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    Business Case 这条链路把 Web、Validation、Security、AOP、Tx、JPA、Events 串在一起，因此“常见坑”也更像边界判断题：错误到底发生在请求入口、权限、事务、持久化，还是事件时机？如果一开始就盯着某个实现细节，很容易在错误的层里修半天。

    建议从 `BootBusinessCaseLabTest` 跑起，尤其是那些故意失败的用例：它们把“哪条边界先拦截、哪条边界负责回滚/副作用”的差异写成了断言。需要下探时，入口通常沿 `DispatcherServlet#doDispatch` → Security FilterChain → `TransactionInterceptor#invoke` → `SimpleJpaRepository` 这条主线展开。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 01 - 架构与主流程（Business Case）](../part-01-business-case/01-architecture-and-flow.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-business-case）](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先把问题归类到边界（这条链路很少只“坏在一个点”）

这一个模块最容易“看起来哪都不对”，本质原因是它覆盖的边界太多：同一个请求既可能被 Validation 拦下来，也可能被 Security 拦下来，还可能在事务回滚、事件时机上出现副作用差异。排障时如果不先做分类，就会在错误的层里做无效修复。

建议先跑两组矩阵测试，把主线与关键分支跑成断言，再回到本章逐条对照：

- `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`
- `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`

需要下探时，对照本模块的断点地图与关键分支矩阵会更省时间：[04-breakpoint-map.md](../part-00-guide/04-breakpoint-map.md) / [05-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md)。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`

## 最小可运行实验（Lab）

- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`
- 建议命令：`mvn -pl :spring-boot-business-case test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 验证入口（可跑）：`BootBusinessCaseLabTest`（建议从失败用例开始跑，因为坑往往都在那里）

## 坑 1：把“请求校验失败”当成业务失败，却没看清它发生在哪个边界

- 会看到：400 + `validation_failed`；但仍然在 service/事务/事件里找原因。
- `BootBusinessCaseLabTest#returnsValidationErrorWhenRequestIsInvalid`
- `BootBusinessCaseLabTest#validationRejectsNegativeQuantity`
- `BootBusinessCaseLabTest#validationRejectsMissingFields`

先把“校验失败”当成 MVC/Validation 边界问题：字段错误应该在 controller 入参阶段就被拦截；并且不应写库、不应发事件（本模块的 `auditLog` 会帮助验证）。

## 坑 2：以为“抛异常就会回滚”，但没有确认事务边界是否真的生效

- 会看到：失败接口 `/api/orders/fail` 返回 500，但不确定数据到底有没有落库。
`BootBusinessCaseLabTest#rollbackPreventsPersistenceOnFailure`

用 `repository.count()` + 断言确认回滚，再回到 Tx 模块定位为什么事务没有生效（代理/入口/self-invocation）。

## 坑 3：把事件当成 after-commit，结果回滚时仍有副作用

- 会看到：回滚用例里仍然出现 `sync:` 审计，但 `afterCommit:` 没有。
- `BootBusinessCaseLabTest#syncListenerRunsEvenWhenTransactionRollsBack_butAfterCommitDoesNot`
- `BootBusinessCaseLabTest#afterCommitListenerRunsOnSuccess`

副作用如果要跟着事务命运走，就用 `@TransactionalEventListener(AFTER_COMMIT)`；否则默认 `@EventListener` 会立刻执行。

## 坑 4：觉得“有 AOP/Tracing”但其实没走代理，或者没打到直觉里的入口

- `BootBusinessCaseLabTest#serviceBeanIsAnAopProxy`
- `BootBusinessCaseLabTest#aspectRecordsInvocationForTracedOperation`

先确认 bean 是 proxy，再确认 aspect 的 pointcut 命中关心的方法（看 InvocationLog 的“最后一次命中方法”）。

## 坑 5：误以为业务接口天然幂等（重试/重复请求导致重复下单）

- 会看到：同样请求发两次，库里会有两条订单。
`BootBusinessCaseLabTest#createOrderIsIdempotentAtDatabaseLevel_perRequestOnly`

幂等要显式设计（幂等键/唯一约束/去重）；不要把“测试里跑两次都 200”当成幂等证明。

## 对应 Lab（可运行）

- `BootBusinessCaseLabTest`
- `BootBusinessCaseServiceLabTest`

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`

上一章：[part-01-business-case/01-architecture-and-flow.md](../part-01-business-case/01-architecture-and-flow.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](02-self-check.md)

<!-- BOOKIFY:END -->
