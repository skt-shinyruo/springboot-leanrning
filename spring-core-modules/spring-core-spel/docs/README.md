# Spring Core SpEL：目录

## 导读

本页是「Spring Core SpEL：目录」的目录页，建议以“先跑后读”的方式使用：先选一个可运行入口把现象跑通，再按主线章节顺读，把每个结论落到可回归的断言。


> 建议先把“parser → evaluation context → getValue()”跑通，再进入更复杂的函数/安全边界。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)
3. [SpEL 调用链（parse → AST → evaluate）](part-00-guide/03-spel-call-chain.md)
4. [断点地图（排障优先）](part-00-guide/04-breakpoint-map.md)
5. [关键分支矩阵（If/Then 收敛）](part-00-guide/05-branch-decision-matrix.md)

## 顺读主线

- [SpEL 入门：root/variables/property access](part-01-spel-basics/01-spel-basics.md)

## 进阶入口（可跑入口/关键分支）

- 可跑入口（Book Matrix）：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBranchMatrixLabTest test`
- 可跑入口（并发求值 Lab）：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
