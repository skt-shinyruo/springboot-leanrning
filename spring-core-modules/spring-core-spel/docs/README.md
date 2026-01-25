# Spring Core SpEL：目录

> 建议先把“parser → evaluation context → getValue()”跑通，再进入更复杂的函数/安全边界。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/209-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/210-00-deep-dive-guide.md)
3. [SpEL 调用链（parse → AST → evaluate）](part-00-guide/210-01-spel-call-chain.md)
4. [断点地图（排障优先）](part-00-guide/210-02-breakpoint-map.md)
5. [关键分支矩阵（If/Then 收敛）](part-00-guide/210-04-branch-decision-matrix.md)

## 顺读主线

- [SpEL 入门：root/variables/property access](part-01-spel-basics/211-01-spel-basics.md)

## 进阶入口（可跑入口/关键分支）

- 可跑入口（Book Matrix）：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBranchMatrixLabTest test`
- 可跑入口（并发求值 Lab）：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/212-90-common-pitfalls.md)
- [自检](appendix/213-99-self-check.md)
