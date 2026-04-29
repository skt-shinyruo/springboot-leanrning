# 04. 断点地图（Business Case）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（Business Case）展开，主线可以概括为：业务入口（Controller/Service）→ 事务边界 → Repository/JPA → 异常触发回滚 → 观测/日志作为证据链。

    先跑 `BootBusinessCaseBranchMatrixLabTest` 固化“成功/失败路径、事务回滚、观测证据”的断言，再沿 Service/Tx 断点把业务流与基础设施（Tx/JPA/日志）串起来。

    需要下探源码时，可以从 `TransactionInterceptor` / `JpaTransactionManager` / 业务 Service 方法 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Business Case](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

- 本章收束点：提供“真实业务链路”的调试入口：从订单/库存等业务方法开始，下沉到事务与持久化，再回到可观测性证据。
- 证据链：测试断言（DB 行数/状态变化）→ Tx 断点（commit/rollback）→ SQL/日志（最终证据）。

## 运行入口（先运行）

- Book Matrix：`BootBusinessCaseBookMatrixLabTest`
- Branch Matrix：`BootBusinessCaseBranchMatrixLabTest`

运行命令：

- `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`

## 入口断点（业务与事务）

- 业务入口：`com.learning.springboot.bootbusinesscase.part01_business_case` 下的 Service 方法（以测试用例调用点为准）
- 事务入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke`
- 事务管理器：`org.springframework.transaction.support.AbstractPlatformTransactionManager#commit` / `#rollback`

## 观察点

- 事务状态：`TransactionSynchronizationManager.isActualTransactionActive()`
- 数据库证据：关键表行数（测试里通常用 repository/jdbcTemplate 验证）
- 异常：是否被吞掉（吞掉会导致“不回滚”错觉）

## 排障入口（Playbook）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

业务入口（Controller/Service）→ 事务边界 → Repository/JPA → 异常触发回滚 → 观测/日志作为证据链。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootBusinessCaseBranchMatrixLabTest`
- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

