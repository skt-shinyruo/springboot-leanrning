# 01. 90 - Common Pitfalls（spring-core-spel）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕Common Pitfalls（spring-core-spel）展开，主线可以概括为：SpEL 的行为高度依赖 evaluation context；大多数误判来自 root/variables 没设对或访问策略不对。

    遇到“表达式取不到值/结果不对/安全风险”时，用本页把问题收敛到 context/访问器/输入边界。

    对照入口：`SpringCoreSpelLabTest`。需要下探源码时，可以从 `SpelExpression#getValue` / `StandardEvaluationContext` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. SpEL 入门（root/variables/property access）](../part-01-spel-basics/01-spel-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（spring-core-spel）](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `SpringCoreSpelLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：SpEL 的行为高度依赖 evaluation context；大多数误判来自 root/variables 没设对或访问策略不对。需要下探源码时，可以从 `SpelExpression#getValue` / `StandardEvaluationContext` 这些入口切入。


## 坑 1：以为表达式“读的是变量”，实际读的是 root

- 验证：分别用 `name` 与 `#name` 对比

## 坑 2：把用户输入当作表达式执行

- 真实工程需要安全边界：限制 type/method access 或彻底禁止

## 小结与下一章

SpEL 的行为高度依赖 evaluation context；大多数误判来自 root/variables 没设对或访问策略不对。

下一章见：[第 213 章：99 - Self Check（spring-core-spel）](02-self-check.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreSpelLabTest`

上一章：[part-01-spel-basics/01-spel-basics.md](../part-01-spel-basics/01-spel-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](02-self-check.md)

<!-- BOOKIFY:END -->
