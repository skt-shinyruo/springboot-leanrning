# 01. 90 - Common Pitfalls（springboot-business-case）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Common Pitfalls（springboot-business-case）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用端到端链路把 Web/Validation/Security/AOP/Tx/JPA/Events 串起来：遇到红测/异常时，先定位“哪个边界没生效”，再回到对应模块主线。
    - 原理：一次业务请求贯穿：MVC 入参→安全边界→事务边界→持久化上下文→事件时机→可观测信号；排障的关键是把问题归类到具体边界。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.data.jpa.repository.support.SimpleJpaRepository`
    - 推荐 Lab：`BootBusinessCaseLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 01 - 架构与主流程（Business Case）](../part-01-business-case/01-architecture-and-flow.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-business-case）](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障模板（统一结构）

当遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，建议按下面 6 步收敛（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[04-breakpoint-map.md](../part-00-guide/04-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[05-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[02-self-check.md](02-self-check.md)

- 本章主题：**01. 90 - Common Pitfalls（springboot-business-case）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，应当能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`

## 机制主线

这页不展开完整机制主线；其定位更接近排障备忘录：把常见分支与可复现入口列出来，便于回到 tests 验证。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`
- 建议命令：`mvn -pl :spring-boot-business-case test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 验证入口（可跑）：`BootBusinessCaseLabTest`（建议从失败用例开始跑，因为坑往往都在那里）

## 坑 1：把“请求校验失败”当成业务失败，却没看清它发生在哪个边界

- 会看到：400 + `validation_failed`；但仍然在 service/事务/事件里找原因。
- Verification：
  - `BootBusinessCaseLabTest#returnsValidationErrorWhenRequestIsInvalid`
  - `BootBusinessCaseLabTest#validationRejectsNegativeQuantity`
  - `BootBusinessCaseLabTest#validationRejectsMissingFields`
- Fix：先把“校验失败”当成 MVC/Validation 边界问题：字段错误应该在 controller 入参阶段就被拦截；并且不应写库、不应发事件（本模块的 `auditLog` 会帮助验证）。

## 坑 2：以为“抛异常就会回滚”，但没有确认事务边界是否真的生效

- 会看到：失败接口 `/api/orders/fail` 返回 500，但不确定数据到底有没有落库。
- Verification：`BootBusinessCaseLabTest#rollbackPreventsPersistenceOnFailure`
- Fix：用 `repository.count()` + 断言确认回滚，再回到 Tx 模块定位为什么事务没有生效（代理/入口/self-invocation）。

## 坑 3：把事件当成 after-commit，结果回滚时仍有副作用

- 会看到：回滚用例里仍然出现 `sync:` 审计，但 `afterCommit:` 没有。
- Verification：
  - `BootBusinessCaseLabTest#syncListenerRunsEvenWhenTransactionRollsBack_butAfterCommitDoesNot`
  - `BootBusinessCaseLabTest#afterCommitListenerRunsOnSuccess`
- Fix：副作用如果要跟着事务命运走，就用 `@TransactionalEventListener(AFTER_COMMIT)`；否则默认 `@EventListener` 会立刻执行。

## 坑 4：觉得“有 AOP/Tracing”但其实没走代理，或者没打到直觉里的入口

- Verification：
  - `BootBusinessCaseLabTest#serviceBeanIsAnAopProxy`
  - `BootBusinessCaseLabTest#aspectRecordsInvocationForTracedOperation`
- Fix：先确认 bean 是 proxy，再确认 aspect 的 pointcut 命中关心的方法（看 InvocationLog 的“最后一次命中方法”）。

## 坑 5：误以为业务接口天然幂等（重试/重复请求导致重复下单）

- 会看到：同样请求发两次，库里会有两条订单。
- Verification：`BootBusinessCaseLabTest#createOrderIsIdempotentAtDatabaseLevel_perRequestOnly`
- Fix：幂等要显式设计（幂等键/唯一约束/去重）；不要把“测试里跑两次都 200”当成幂等证明。

## 对应 Lab（可运行）

- `BootBusinessCaseLabTest`
- `BootBusinessCaseServiceLabTest`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`

上一章：[part-01-business-case/01-architecture-and-flow.md](../part-01-business-case/01-architecture-and-flow.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](02-self-check.md)

<!-- BOOKIFY:END -->
