# 第 190 章：02：断点地图（Business Case Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Business Case Debugger Pack）
    - 怎么使用：先跑 `BootBusinessCaseBranchMatrixLabTest` 固化“成功/失败路径、事务回滚、观测证据”的断言，再沿 Service/Tx 断点把业务流与基础设施（Tx/JPA/日志）串起来。
    - 原理：业务入口（Controller/Service）→ 事务边界 → Repository/JPA → 异常触发回滚 → 观测/日志作为证据链。
    - 源码入口：`TransactionInterceptor` / `JpaTransactionManager` / 业务 Service 方法
    - 推荐 Lab：`BootBusinessCaseBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 190 章：00 - Deep Dive Guide（springboot-business-case）](190-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 190 章：04：关键分支矩阵（Branch Decision Matrix）](190-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

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

- Matrix：`BootBusinessCaseBranchMatrixLabTest`
- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](190-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](190-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

