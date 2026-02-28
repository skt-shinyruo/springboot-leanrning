# 02. 深挖导读：把“表达式求值”落到源码与断点
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕深挖导读：把“表达式求值”落到源码与断点展开，主线可以概括为：SpEL 的可解释性来自 AST 与 context：同一表达式在不同 context 下可能得到不同结果。

    先跑 `SpringCoreSpelLabTest`，把 parse/getValue 固化成断言；再下断点回答：字符串如何变成 AST？AST 如何用 evaluation context 取值？

    需要下探源码时，可以从 `SpelExpressionParser#parseExpression` /（AST）`SpelNodeImpl` / `Expression#getValue` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：spring-core-spel](01-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. SpEL 调用链（parse → AST → evaluate）](03-spel-call-chain.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `SpringCoreSpelLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：SpEL 的可解释性来自 AST 与 context：同一表达式在不同 context 下可能得到不同结果。需要下探源码时，可以从 `SpelExpressionParser#parseExpression` /（AST）`SpelNodeImpl` / `Expression#getValue` 这些入口切入。


## 小结与下一章

SpEL 的可解释性来自 AST 与 context：同一表达式在不同 context 下可能得到不同结果。

下一章见：[第 210 章：01：SpEL 调用链](03-spel-call-chain.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreSpelLabTest`

上一章：[part-00-guide/03-mainline-timeline.md](01-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/01-spel-call-chain.md](03-spel-call-chain.md)

<!-- BOOKIFY:END -->
