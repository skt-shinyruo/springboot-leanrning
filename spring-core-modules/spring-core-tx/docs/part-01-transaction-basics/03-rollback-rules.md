# 03. 回滚规则：为什么 checked exception 默认不回滚？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：回滚规则：为什么 checked exception 默认不回滚？
    - 怎么使用：先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
    - 原理：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
    - 源码入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction` / `org.springframework.transaction.PlatformTransactionManager`
    - 推荐 Lab：`SpringCoreTxLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. `@Transactional` 如何生效：它也是 AOP（也是代理）](02-transactional-proxy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 传播行为（Propagation）：`REQUIRED` vs `REQUIRES_NEW` 到底差在哪？](04-propagation.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. 回滚规则：为什么 checked exception 默认不回滚？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 本章结束后，应能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 速读路径：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreTxLabTest`

## 机制主线

Spring 事务默认回滚规则经常让人困惑：

## 在本模块如何“看见”差异


## 为什么 Spring 默认这样做？

历史原因 + 语义取舍：

- checked exception 在 Java 语义里往往表示“可预期的业务分支”
- Spring 默认认为：这类异常不一定等价于“系统失败”，因此不默认回滚

学习仓库里更重要的是形成“可预测规则”：

> **想让 checked exception 回滚，就显式写 `rollbackFor`。**

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreTxLabTest`
- Lab：`SpringCoreTxRollbackRulesLabTest`（Runtime vs Checked + rollbackFor/noRollbackFor）
- 建议命令：`mvn -pl :spring-core-tx test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

- **运行时异常（RuntimeException / Error）**：默认回滚
- **受检异常（checked exception）**：默认不回滚

看 `SpringCoreTxLabTest` 里的 `TxPlaygroundService`（是 test 内部类，方便做机制实验）：

- `insertThenThrowChecked()`：抛 checked exception，但默认 **不回滚**
  - 对应断言：`SpringCoreTxLabTest#checkedExceptionsDoNotRollbackByDefault`
- `insertThenThrowCheckedWithRollback()`：加了 `@Transactional(rollbackFor = ...)` 后 **会回滚**
  - 对应断言：`SpringCoreTxLabTest#rollbackForCheckedExceptionsCanBeConfigured`

如果想把“规则矩阵”固化成更直观的对照（避免只看单个方法），建议再跑：

- `SpringCoreTxRollbackRulesLabTest`：
  - `runtimeExceptionRollsBackByDefault`：RuntimeException 默认回滚
  - `checkedExceptionCommitsByDefault`：CheckedException 默认提交
  - `checkedExceptionRollsBackWhenRollbackForIsSpecified`：`rollbackFor` 覆盖默认规则
  - `runtimeExceptionCommitsWhenNoRollbackForIsSpecified`：`noRollbackFor` 覆盖默认规则

## 常见坑与边界

### 坑点 1：以为“抛异常就一定回滚”，结果 checked exception 仍然提交

- Symptom：抛了业务异常（checked），却发现数据仍然落库，误以为事务没生效
- Root Cause：Spring 默认回滚规则：RuntimeException/Error 回滚；checked exception 默认不回滚
- Verification：
  - checked 默认不回滚：`SpringCoreTxLabTest#checkedExceptionsDoNotRollbackByDefault`
  - 显式 rollbackFor 才回滚：`SpringCoreTxLabTest#rollbackForCheckedExceptionsCanBeConfigured`
- Fix：把回滚规则当成显式契约写出来（rollbackFor/noRollbackFor），并用测试锁定“哪些异常会导致哪些数据落库结果”

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreTxLabTest`
- Lab：`SpringCoreTxRollbackRulesLabTest`

上一章：[02-transactional-proxy](02-transactional-proxy.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04-propagation](04-propagation.md)

<!-- BOOKIFY:END -->
