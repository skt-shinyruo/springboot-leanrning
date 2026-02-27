# Spring Resources：定位、读取与 classpath 边界

资源问题的高频根因在于“同一段路径在不同运行形态下不是同一件事”：classpath、jar 内资源与 filesystem 文件在定位、读取、pattern 扫描、以及 `exists` 语义上都有细微但决定性的差异。本模块按资源抽象逐步展开，目标是把这些边界跑成可验证的事实。

---

## 10 分钟入口：先跑通一次 classpath 定位与读取

- `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`

运行后应能回答：Resource 抽象背后到底是哪一种实现（classpath/jar/file）；`classpath*:` 与 pattern 扫描在何处展开；为何在 IDE 与打包后运行时表现不同。

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

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-core-resources -Dtest=*ExerciseSolutionTest test`
- 并发/性能（PathMatchingResourcePatternResolver 并发解析）：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesPatternResolverConcurrencyLabTest test`

---

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
