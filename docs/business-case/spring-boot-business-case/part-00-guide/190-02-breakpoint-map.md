# 第 190 章：02：断点地图（Business Case Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Business Case Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootBusinessCaseLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 190 章：00 - Deep Dive Guide（springboot-business-case）](190-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 190 章：04：关键分支矩阵（Branch Decision Matrix）](190-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Business Case Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

- 本章目标：提供“真实业务链路”的调试入口：从订单/库存等业务方法开始，下沉到事务与持久化，再回到可观测性证据。
- 推荐证据链：测试断言（DB 行数/状态变化）→ Tx 断点（commit/rollback）→ SQL/日志（最终证据）。

## 运行入口（建议先跑）

- Book Matrix：`BootBusinessCaseBookMatrixLabTest`
- Branch Matrix：`BootBusinessCaseBranchMatrixLabTest`

推荐命令：

- `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`

## 入口断点（业务与事务）

- 业务入口：`com.learning.springboot.bootbusinesscase.part01_business_case` 下的 Service 方法（以测试用例调用点为准）
- 事务入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke`
- 事务管理器：`org.springframework.transaction.support.AbstractPlatformTransactionManager#commit` / `#rollback`

## Watchpoints（建议）

- 事务状态：`TransactionSynchronizationManager.isActualTransactionActive()`
- 数据库证据：关键表行数（测试里通常用 repository/jdbcTemplate 验证）
- 异常：是否被吞掉（吞掉会导致“不回滚”错觉）

## 排障入口（Playbook）

- 常见坑：[`../appendix/192-90-common-pitfalls.md`](../appendix/192-90-common-pitfalls.md)
- 自检：[`../appendix/193-99-self-check.md`](../appendix/193-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseBookMatrixLabTest` / `BootBusinessCaseBranchMatrixLabTest`

上一章：[架构与端到端流转](../part-01-business-case/191-01-architecture-and-flow.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[190-04-branch-decision-matrix.md](190-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
