# Spring Boot Auto-Configuration：目录

## 导读

本页是「Spring Boot Auto-Configuration：目录」的目录页，建议以“先跑后读”的方式使用：先选一个可运行入口把现象跑通，再按主线章节顺读，把每个结论落到可回归的断言。


> 建议先把“AutoConfiguration 是怎么被导入的（imports）”与“条件装配如何决策（Condition）”跑通，再进入 backoff/顺序等细节。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)
3. [AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）](part-00-guide/03-autoconfiguration-import-call-chain.md)
4. [断点地图（排障优先）](part-00-guide/04-breakpoint-map.md)
5. [关键分支矩阵（If/Then 收敛）](part-00-guide/05-branch-decision-matrix.md)

## 顺读主线

- [条件装配与 backoff：为什么它“有时生效、有时不生效”](part-01-autoconfig-basics/01-conditional-and-backoff.md)

## 进阶入口（可跑入口/关键分支）

- 可跑入口（Book Matrix）：`mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBranchMatrixLabTest test`
- 可跑入口（Perf/Concurrency Lab）：`mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
