# 01. 程序化事务：为什么 `TransactionTemplate` 在学习阶段很有价值？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：程序化事务：为什么 `TransactionTemplate` 在学习阶段很有价值？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
    - 原理：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
    - 源码入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction` / `org.springframework.transaction.PlatformTransactionManager`
    - 推荐 Lab：`SpringCoreTxLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 传播行为（Propagation）：`REQUIRED` vs `REQUIRES_NEW` 到底差在哪？](../part-01-transaction-basics/04-propagation.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. Debug / 观察：如何判断“当前是否真的有事务”？](02-debugging.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「05. 程序化事务：为什么 `TransactionTemplate` 在学习阶段很有价值？」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `SpringCoreTxLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreTxLabTest`

## 机制主线

声明式事务（`@Transactional`）的优点是“用起来很爽”，但学习阶段往往会隐藏机制。

`TransactionTemplate` 的优点是把 commit/rollback 的控制权暴露出来：

它演示了两次执行：

1. 正常执行：插入成功 → 提交 → 能查到数据
2. `status.setRollbackOnly()`：显式标记回滚 → 最终查不到数据

## 学习建议

- 学完 `@Transactional` 后，用 `TransactionTemplate` 复写一遍同样的场景  
  你会更清楚“事务管理器到底在干什么”。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreTxLabTest`
- 建议命令：`mvn -pl :spring-core-tx test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证

看测试：`SpringCoreTxLabTest#transactionTemplateAllowsProgrammaticCommitOrRollback`

## 常见坑与边界

- 你能清楚看到：什么时候提交、什么时候标记回滚
- 你能把“事务边界”写成显式代码（帮助建立直觉）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreTxLabTest`

上一章：[04-propagation](../part-01-transaction-basics/04-propagation.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06-debugging](02-debugging.md)

<!-- BOOKIFY:END -->
