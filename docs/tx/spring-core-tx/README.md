# Spring Core Tx（事务）：目录

> 建议先把“事务边界 + 代理主线”跑通，再看回滚/传播，最后用模板与调试手段把行为固定下来。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/052-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/053-00-deep-dive-guide.md)

## 顺读主线

- [事务边界](part-01-transaction-basics/054-01-transaction-boundary.md)
- [@Transactional 代理](part-01-transaction-basics/055-02-transactional-proxy.md)
- [回滚规则](part-01-transaction-basics/056-03-rollback-rules.md)
- [传播行为](part-01-transaction-basics/057-04-propagation.md)
- [TransactionTemplate](part-02-template-and-debugging/058-05-transaction-template.md)
- [事务调试](part-02-template-and-debugging/059-06-debugging.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[053-02-breakpoint-map.md](part-00-guide/053-02-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[053-04-branch-decision-matrix.md](part-00-guide/053-04-branch-decision-matrix.md)
- 排障 playbook：[060-90-common-pitfalls.md](appendix/060-90-common-pitfalls.md)
- 自检清单：[061-99-self-check.md](appendix/061-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`
- 可跑入口（Branch Matrix - 事务主分支）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`
- 可跑入口（Branch Matrix - 常见坑聚合）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxPitfallsBranchMatrixLabTest test`

## 排坑与自检

- [常见坑](appendix/060-90-common-pitfalls.md)
- [自检](appendix/061-99-self-check.md)
