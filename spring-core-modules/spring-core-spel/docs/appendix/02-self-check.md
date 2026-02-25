# 02. 99 - Self Check（spring-core-spel）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（spring-core-spel）
    - 怎么使用：先跑 Book/Branch Matrix，再用本页问题自检是否真正理解“parse → context → getValue”。
    - 原理：自检目标：你能从现象回到 context 与求值链，并能用断言/断点验证。
    - 源码入口：`SpelExpressionParser#parseExpression` / `SpelExpression#getValue`
    - 推荐 Lab：`SpringCoreSpelLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 90 - Common Pitfalls（spring-core-spel）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02. 99 - Self Check（spring-core-spel）**
- 建议入口：优先运行 `SpringCoreSpelLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：自检目标：你能从现象回到 context 与求值链，并能用断言/断点验证。
- 源码入口：`SpelExpressionParser#parseExpression` / `SpelExpression#getValue`



## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/04-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/05-branch-decision-matrix.md)

## 自检问题

1. 为什么同一表达式在不同 context 下结果可能不同？
2. 你会在哪个入口下断点观察 parse 与 getValue？
3. 为什么 SpEL 需要安全边界？你会如何限制？

## 小结与下一章

- 小结：自检目标：你能从现象回到 context 与求值链，并能用断言/断点验证。
- 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreSpelLabTest`

上一章：[appendix/90-common-pitfalls.md](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
