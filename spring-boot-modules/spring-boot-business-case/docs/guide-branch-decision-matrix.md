# 05. 关键分支矩阵
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵展开，主线可以概括为：业务逻辑的分支最终会落到“是否抛异常、异常类型、事务边界是否覆盖”。

    把业务案例里的关键分支（成功/失败、回滚/不回滚）写成矩阵表；每行都有复现入口与证据链。

    对照入口：`BootBusinessCaseBranchMatrixLabTest`。需要下探源码时，可以从 业务 Service + `TransactionInterceptor` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Business Case）](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 01 - 架构与主流程（Business Case）](business-case-architecture-and-flow.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootBusinessCaseBranchMatrixLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：业务逻辑的分支最终会落到“是否抛异常、异常类型、事务边界是否覆盖”。需要下探源码时，可以从 业务 Service + `TransactionInterceptor` 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点 |
|---|---|---|---|---|
| 成功路径 | 正常下单 | 数据落库，事务提交 | `BootBusinessCaseLabTest` | commit 路径 / 行数变化 |
| 失败回滚 | 业务异常抛出（RuntimeException） | 数据回滚不落库 | `BootBusinessCaseServiceLabTest` | rollback 路径 / 行数不变 |
| 证据链收集 | 开启 trace/log | 可从日志/SQL/指标反推分支 | `BootBusinessCaseBranchMatrixLabTest` | 日志关键字段 |

## 运行命令

- `mvn -q -pl :spring-boot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

业务逻辑的分支最终会落到“是否抛异常、异常类型、事务边界是否覆盖”。

下一章见：[01：业务架构与调用链（从入口到事务与持久化）](business-case-architecture-and-flow.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootBusinessCaseBranchMatrixLabTest`
- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`

上一章：[guide-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[business-case-architecture-and-flow.md](business-case-architecture-and-flow.md)

<!-- BOOKIFY:END -->

