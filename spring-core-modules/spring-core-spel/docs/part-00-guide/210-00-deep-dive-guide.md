# 第 210 章：00. 深挖导读：把“表达式求值”落到源码与断点
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：深挖导读：把“表达式求值”落到源码与断点
    - 怎么使用：先跑 `SpringCoreSpelLabTest`，把 parse/getValue 固化成断言；再下断点回答：字符串如何变成 AST？AST 如何用 evaluation context 取值？
    - 原理：SpEL 的可解释性来自 AST 与 context：同一表达式在不同 context 下可能得到不同结果。
    - 源码入口：`SpelExpressionParser#parseExpression` /（AST）`SpelNodeImpl` / `Expression#getValue`
    - 推荐 Lab：`SpringCoreSpelLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 209 章：03：主线时间线](209-03-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 210 章：01：SpEL 调用链](210-01-spel-call-chain.md)
<!-- GLOBAL-BOOK-NAV:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreSpelLabTest`

上一章：[part-00-guide/03-mainline-timeline.md](209-03-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/01-spel-call-chain.md](210-01-spel-call-chain.md)

<!-- BOOKIFY:END -->
