# Spring Resources：目录

## 导读

本页是「Spring Resources：目录」的目录页，建议以“先跑后读”的方式使用：先选一个可运行入口把现象跑通，再按主线章节顺读，把每个结论落到可回归的断言。


> 建议顺读 6 章把资源定位与读取的边界跑通：classpath/jar/filesystem 的差异，是资源问题的高发根因。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)

## 顺读主线

- [Resource 抽象](part-01-resource-abstraction/01-resource-abstraction.md)
- [classpath 定位](part-01-resource-abstraction/02-classpath-locations.md)
- [classpath* 与 pattern](part-01-resource-abstraction/03-classpath-star-and-pattern.md)
- [exists 与 handles](part-01-resource-abstraction/04-exists-and-handles.md)
- [读取与编码](part-01-resource-abstraction/05-reading-and-encoding.md)
- [jar vs filesystem](part-01-resource-abstraction/06-jar-vs-filesystem.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[04-breakpoint-map.md](part-00-guide/04-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](part-00-guide/05-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-core-resources -Dtest=*ExerciseSolutionTest test`
- 可跑入口（并发/性能 Lab - PathMatchingResourcePatternResolver 并发解析）：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesPatternResolverConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
