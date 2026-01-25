# Spring Resources：目录

> 建议顺读 6 章把资源定位与读取的边界跑通：classpath/jar/filesystem 的差异，是资源问题的高发根因。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/139-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/140-00-deep-dive-guide.md)

## 顺读主线

- [Resource 抽象](part-01-resource-abstraction/141-01-resource-abstraction.md)
- [classpath 定位](part-01-resource-abstraction/142-02-classpath-locations.md)
- [classpath* 与 pattern](part-01-resource-abstraction/143-03-classpath-star-and-pattern.md)
- [exists 与 handles](part-01-resource-abstraction/144-04-exists-and-handles.md)
- [读取与编码](part-01-resource-abstraction/145-05-reading-and-encoding.md)
- [jar vs filesystem](part-01-resource-abstraction/146-06-jar-vs-filesystem.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[140-02-breakpoint-map.md](part-00-guide/140-02-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[140-04-branch-decision-matrix.md](part-00-guide/140-04-branch-decision-matrix.md)
- 排障 playbook：[147-90-common-pitfalls.md](appendix/147-90-common-pitfalls.md)
- 自检清单：[148-99-self-check.md](appendix/148-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-core-resources -Dtest=*ExerciseSolutionTest test`
- 可跑入口（并发/性能 Lab - PathMatchingResourcePatternResolver 并发解析）：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesPatternResolverConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/147-90-common-pitfalls.md)
- [自检](appendix/148-99-self-check.md)
