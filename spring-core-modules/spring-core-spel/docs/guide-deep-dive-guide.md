# 02. 深挖导读：把“表达式求值”落到源码与断点
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕把“表达式求值”落到源码与断点展开，主线可以概括为：SpEL 的可解释性来自 AST 与 context：同一表达式在不同 context 下可能得到不同结果。

    先跑 `SpringCoreSpelLabTest`，把 parse/getValue 固化成断言；再下断点回答：字符串如何变成 AST？AST 如何用 evaluation context 取值？

    需要下探源码时，可以从 `SpelExpressionParser#parseExpression` /（AST）`SpelNodeImpl` / `Expression#getValue` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：spring-core-spel](guide-mainline-timeline.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[03. SpEL 调用链（parse → AST → evaluate）](guide-spel-call-chain.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `SpringCoreSpelLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：SpEL 的可解释性来自 AST 与 context：同一表达式在不同 context 下可能得到不同结果。需要下探源码时，可以从 `SpelExpressionParser#parseExpression` /（AST）`SpelNodeImpl` / `Expression#getValue` 这些入口切入。


## 小结与下一章

SpEL 的可解释性来自 AST 与 context：同一表达式在不同 context 下可能得到不同结果。

下一章见：[01：SpEL 调用链](guide-spel-call-chain.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreSpelLabTest`

上一章：[guide-mainline-timeline.md](guide-mainline-timeline.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-spel-call-chain.md](guide-spel-call-chain.md)

<!-- BOOKIFY:END -->
