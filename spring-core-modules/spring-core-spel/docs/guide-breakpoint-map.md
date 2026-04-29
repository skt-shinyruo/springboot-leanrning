# 04. 断点地图（SpEL）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（SpEL）展开，主线可以概括为：先确认 parse 得到的表达式结构，再确认 context 提供的数据（root/variables/accessors）。

    遇到“表达式结果不对/变量取不到/属性访问失败”时，用本页断点把问题收敛到 parser/AST/evaluation context。

    对照入口：`SpringCoreSpelLabTest`。需要下探源码时，可以从 `SpelExpressionParser#parseExpression` / `SpelExpression#getValue` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. SpEL 调用链（parse → AST → evaluate）](guide-spel-call-chain.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵（SpEL）](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `SpringCoreSpelLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：先确认 parse 得到的表达式结构，再确认 context 提供的数据（root/variables/accessors）。需要下探源码时，可以从 `SpelExpressionParser#parseExpression` / `SpelExpression#getValue` 这些入口切入。


## A. parser：表达式字符串怎么变成 AST？

- 断点入口：`SpelExpressionParser#parseExpression`

调试时重点观察：解析出来的 expression 与内部 AST（如果进一步深入）


## B. evaluation：为什么取不到值？

- 断点入口：`SpelExpression#getValue`

调试时重点观察：evaluation context 的 root object 与 variables。


## 小结与下一章

先确认 parse 得到的表达式结构，再确认 context 提供的数据（root/variables/accessors）。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreSpelLabTest`

上一章：[guide-spel-call-chain.md](guide-spel-call-chain.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
