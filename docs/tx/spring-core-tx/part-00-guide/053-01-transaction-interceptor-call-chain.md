# 第 53 章：01：事务拦截器调用链（从 `@Transactional` 到 commit/rollback）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：事务拦截器调用链（从 `@Transactional` 到 commit/rollback）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
    - 原理：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
    - 源码入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction` / `org.springframework.transaction.PlatformTransactionManager`
    - 推荐 Lab：`SpringCoreTxLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 53 章：00. 深挖指南：把“事务边界/传播/回滚规则”落到源码与断点](053-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 53 章：02：断点地图（Tx Debugger Pack）](053-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01：事务拦截器调用链（从 `@Transactional` 到 commit/rollback）**
- 目标：把“事务不生效/回滚不符合预期/传播行为很玄学”这类问题，收敛为：你知道调用链在哪、关键分支在哪、证据入口在哪。
- 基线版本：Spring Framework `6.2.15`（本仓库由 Spring Boot `3.5.9` 管理依赖版本）。

!!! summary "本章要点"

    - 事务有两条链：**代理生成链（容器期）**与**事务边界执行链（运行期）**。先判断你卡在“有没有代理/有没有进入拦截器”，再判断“拦截器里走了哪个分支”。
    - 事务的核心抓手是：`TransactionInterceptor#invoke` → `TransactionAspectSupport#invokeWithinTransaction`。你能在这里把“传播/回滚规则/异常”落到可观察事实。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreTxLabTest`

## 1. 代理生成链：`@Transactional` 为什么会变成拦截器？

从工程视角看，`@Transactional` 不会“魔法般”改变你的方法；它需要在容器里变成：

- 一个 Advisor（决定哪些方法要被拦截）
- 一个拦截器（`TransactionInterceptor`，负责在调用前后开/关事务）

关键点是：事务和 AOP 的关系不是“相似”，而是“同一条基础设施链路”：

- `InfrastructureAdvisorAutoProxyCreator` / `AbstractAutoProxyCreator` 作为 BPP
- `BeanFactoryTransactionAttributeSourceAdvisor` 提供 pointcut（基于 `TransactionAttributeSource`）
- `TransactionInterceptor` 作为 MethodInterceptor 加入 advice 链

因此排障第一问永远是：

> 这个 bean 最终是不是 proxy？（否则根本不可能有事务拦截）

## 2. 执行链：一次 `@Transactional` 方法调用发生了什么？

高层主线（你要能“顺着念出来”）：

1. 调用进入代理（JDK/CGLIB 入口略）
2. 进入 `TransactionInterceptor#invoke`
3. 进入 `TransactionAspectSupport#invokeWithinTransaction`
4. 解析事务属性（传播/隔离/只读/超时/rollback 规则）
5. 根据传播决定：
   - 加入现有事务（`REQUIRED` 等）
   - 或新开事务（`REQUIRES_NEW`）
   - 或无事务执行
6. 执行目标方法 `invocation.proceed()`
7. 根据结果收尾：
   - 正常返回 → commit（或参与外层事务等待外层提交）
   - 抛异常 → 判断是否应回滚 → rollback（或标记 rollback-only）

你可以把它压缩成两段：

- **进入事务边界**：决定“有没有事务/是否新开事务”
- **离开事务边界**：决定“commit 还是 rollback（或 rollback-only）”

## 3. 三个最常见的关键分支（用来解释“为什么不符合预期”）

1. **没进拦截器（事务不生效）**
   - 可能原因：自调用绕过代理、bean 不是 Spring 管理、方法不是 public 等
2. **进了拦截器但没回滚（异常类型/捕获）**
   - 可能原因：checked exception 默认不回滚；异常被 catch 并吞掉；或显式 noRollbackFor
3. **传播导致“看起来很怪”的提交/回滚**
   - 可能原因：`REQUIRES_NEW` 独立边界、外层回滚不影响内层提交，或内层异常只标记外层 rollback-only

这些分支在本模块的“关键分支矩阵”里都有对应最小复现入口：

- [04：关键分支矩阵（Tx）](053-04-branch-decision-matrix.md)

## 小结与下一章

- 本章把 `@Transactional` 的“生成链 + 执行链”串成可复述叙事；下一章把入口收敛为断点地图，方便排障时快速命中关键分支。

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「01：事务拦截器调用链（从 `@Transactional` 到 commit/rollback）」的生效时机/顺序/边界；断点/入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「01：事务拦截器调用链（从 `@Transactional` 到 commit/rollback）」的生效时机/顺序/边界；断点/入口：`org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「01：事务拦截器调用链（从 `@Transactional` 到 commit/rollback）」的生效时机/顺序/边界；断点/入口：`org.springframework.transaction.PlatformTransactionManager`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreTxLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreTxLabTest`

上一章：[053-02-breakpoint-map.md](053-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[053-04-branch-decision-matrix.md](053-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
