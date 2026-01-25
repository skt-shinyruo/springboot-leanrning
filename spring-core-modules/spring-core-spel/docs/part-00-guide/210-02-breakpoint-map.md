# 第 210 章：02：断点地图（SpEL Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（SpEL Debugger Pack）
    - 怎么使用：遇到“表达式结果不对/变量取不到/属性访问失败”时，用本页断点把问题收敛到 parser/AST/evaluation context。
    - 原理：先确认 parse 得到的表达式结构，再确认 context 提供的数据（root/variables/accessors）。
    - 源码入口：`SpelExpressionParser#parseExpression` / `SpelExpression#getValue`
    - 推荐 Lab：`SpringCoreSpelLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 210 章：01：SpEL 调用链](210-01-spel-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 210 章：04：关键分支矩阵](210-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## A. parser：表达式字符串怎么变成 AST？

- 建议断点：`SpelExpressionParser#parseExpression`
- 你要看的：解析出来的 expression 与内部 AST（如果你进一步深入）

## B. evaluation：为什么取不到值？

- 建议断点：`SpelExpression#getValue`
- 你要看的：evaluation context 的 root object 与 variables

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreSpelLabTest`

上一章：[part-00-guide/01-spel-call-chain.md](210-01-spel-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](210-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
