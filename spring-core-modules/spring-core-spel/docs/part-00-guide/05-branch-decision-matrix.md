# 05. 关键分支矩阵（SpEL）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（SpEL）
    - 怎么使用：把“表达式求值”变成可复现分支：每个分支都有入口（测试）与断点锚点。
    - 原理：root/variables/context 决定求值结果；同一表达式在不同 context 下可能不同。
    - 源码入口：`SpelExpression#getValue` / `StandardEvaluationContext`
    - 推荐 Lab：`SpringCoreSpelLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（SpEL Debugger Pack）](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. SpEL 入门（root/variables/property access）](../part-01-spel-basics/01-spel-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05. 关键分支矩阵（SpEL）**
- 建议入口：优先运行 `SpringCoreSpelLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：root/variables/context 决定求值结果；同一表达式在不同 context 下可能不同。
- 源码入口：`SpelExpression#getValue` / `StandardEvaluationContext`



## 分支矩阵（最小闭环）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 断点锚点（Breakpoint） |
|---|---|---|---|---|
| 简单运算 | 表达式 `"1 + 2"` | 返回 3 | `SpringCoreSpelLabTest#parsesAndEvaluatesSimpleExpression` | `SpelExpressionParser#parseExpression` |
| root + variable | root=User + `#minAge` | boolean 为 true | `SpringCoreSpelLabTest#evaluatesAgainstRootObjectAndVariables` | `SpelExpression#getValue` |

## 小结与下一章

- 小结：root/variables/context 决定求值结果；同一表达式在不同 context 下可能不同。
- 下一章：[第 211 章：01：SpEL 入门](../part-01-spel-basics/01-spel-basics.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreSpelLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-spel-basics/01-spel-basics.md](../part-01-spel-basics/01-spel-basics.md)

<!-- BOOKIFY:END -->
