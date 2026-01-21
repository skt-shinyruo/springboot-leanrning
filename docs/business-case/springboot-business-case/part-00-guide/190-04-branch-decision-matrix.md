# 第 190 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：把业务案例里的关键分支（成功/失败、回滚/不回滚）写成矩阵表；每行都有复现入口与证据链。
    - 原理：业务逻辑的分支最终会落到“是否抛异常、异常类型、事务边界是否覆盖”。
    - 源码入口：业务 Service + `TransactionInterceptor`
    - 推荐 Lab：`BootBusinessCaseBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 190 章：02：断点地图（Business Case Debugger Pack）](190-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 191 章：01：业务架构与调用链（从入口到事务与持久化）](../part-01-business-case/191-01-architecture-and-flow.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 成功路径 | 正常下单 | 数据落库，事务提交 | `BootBusinessCaseLabTest` | commit 路径 / 行数变化 |
| 失败回滚 | 业务异常抛出（RuntimeException） | 数据回滚不落库 | `BootBusinessCaseServiceLabTest` | rollback 路径 / 行数不变 |
| 证据链收集 | 开启 trace/log | 可从日志/SQL/指标反推分支 | `BootBusinessCaseBranchMatrixLabTest` | 日志关键字段 |

## 推荐运行命令

- `mvn -q -pl :springboot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/192-90-common-pitfalls.md`](../appendix/192-90-common-pitfalls.md)
- 自检：[`../appendix/193-99-self-check.md`](../appendix/193-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootBusinessCaseBranchMatrixLabTest`
- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](190-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-business-case/01-architecture-and-flow.md](../part-01-business-case/191-01-architecture-and-flow.md)

<!-- BOOKIFY:END -->

