# Spring Core SpEL：解析、求值与边界

本模块以一条最短求值链路为主线，把 SpEL 的三个关键对象跑成事实：parser 如何把表达式解析成 AST，evaluation context 如何提供 root/variables/property access，最终 `getValue()` 在何处完成求值与类型转换。函数扩展与安全边界属于更复杂的分支，本模块先把基础链路固定下来，再进入扩展点。

---

## 10 分钟入口：先跑通 parse → evaluate

- `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`

运行后应能回答：表达式在何处被解析；属性访问与类型转换在何处发生；不同 evaluation context 下为何会产生不同的求值结果。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)
3. [SpEL 调用链（parse → AST → evaluate）](part-00-guide/03-spel-call-chain.md)
4. [断点地图（排障优先）](part-00-guide/04-breakpoint-map.md)
5. [关键分支矩阵（If/Then 收敛）](part-00-guide/05-branch-decision-matrix.md)

## 顺读主线

- [SpEL 入门：root/variables/property access](part-01-spel-basics/01-spel-basics.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBranchMatrixLabTest test`
- 并发求值：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
