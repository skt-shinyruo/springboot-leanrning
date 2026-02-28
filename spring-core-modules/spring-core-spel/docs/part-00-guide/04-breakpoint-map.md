# 04. 断点地图（SpEL Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕02：断点地图（SpEL Debugger Pack）展开，主线可以概括为：先确认 parse 得到的表达式结构，再确认 context 提供的数据（root/variables/accessors）。

    遇到“表达式结果不对/变量取不到/属性访问失败”时，用本页断点把问题收敛到 parser/AST/evaluation context。

    对照入口：`SpringCoreSpelLabTest`。需要下探源码时，可以从 `SpelExpressionParser#parseExpression` / `SpelExpression#getValue` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. SpEL 调用链（parse → AST → evaluate）](03-spel-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 关键分支矩阵（SpEL）](05-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `SpringCoreSpelLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：先确认 parse 得到的表达式结构，再确认 context 提供的数据（root/variables/accessors）。需要下探源码时，可以从 `SpelExpressionParser#parseExpression` / `SpelExpression#getValue` 这些入口切入。


## A. parser：表达式字符串怎么变成 AST？

- 建议断点：`SpelExpressionParser#parseExpression`

调试时建议重点盯：解析出来的 expression 与内部 AST（如果进一步深入）


## B. evaluation：为什么取不到值？

- 建议断点：`SpelExpression#getValue`

调试时建议重点盯：evaluation context 的 root object 与 variables。


## 小结与下一章

先确认 parse 得到的表达式结构，再确认 context 提供的数据（root/variables/accessors）。

下一章见：[第 210 章：04：关键分支矩阵](05-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreSpelLabTest`

上一章：[part-00-guide/01-spel-call-chain.md](03-spel-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](05-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
