# 05. 关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕04：关键分支矩阵（Branch Decision Matrix）展开，主线可以概括为：业务逻辑的分支最终会落到“是否抛异常、异常类型、事务边界是否覆盖”。

    把业务案例里的关键分支（成功/失败、回滚/不回滚）写成矩阵表；每行都有复现入口与证据链。

    对照入口：`BootBusinessCaseBranchMatrixLabTest`。需要下探源码时，可以从 业务 Service + `TransactionInterceptor` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Business Case Debugger Pack）](guide-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 01 - 架构与主流程（Business Case）](business-case-architecture-and-flow.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootBusinessCaseBranchMatrixLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：业务逻辑的分支最终会落到“是否抛异常、异常类型、事务边界是否覆盖”。需要下探源码时，可以从 业务 Service + `TransactionInterceptor` 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 成功路径 | 正常下单 | 数据落库，事务提交 | `BootBusinessCaseLabTest` | commit 路径 / 行数变化 |
| 失败回滚 | 业务异常抛出（RuntimeException） | 数据回滚不落库 | `BootBusinessCaseServiceLabTest` | rollback 路径 / 行数不变 |
| 证据链收集 | 开启 trace/log | 可从日志/SQL/指标反推分支 | `BootBusinessCaseBranchMatrixLabTest` | 日志关键字段 |

## 推荐运行命令

- `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/01-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](appendix-self-check.md)

## 小结与下一章

业务逻辑的分支最终会落到“是否抛异常、异常类型、事务边界是否覆盖”。

下一章见：[第 191 章：01：业务架构与调用链（从入口到事务与持久化）](business-case-architecture-and-flow.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootBusinessCaseBranchMatrixLabTest`
- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-business-case/01-architecture-and-flow.md](business-case-architecture-and-flow.md)

<!-- BOOKIFY:END -->

