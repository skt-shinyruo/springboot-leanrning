# 04. 断点地图（SpEL Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（SpEL Debugger Pack）
    - 怎么使用：遇到“表达式结果不对/变量取不到/属性访问失败”时，用本页断点把问题收敛到 parser/AST/evaluation context。
    - 原理：先确认 parse 得到的表达式结构，再确认 context 提供的数据（root/variables/accessors）。
    - 源码入口：`SpelExpressionParser#parseExpression` / `SpelExpression#getValue`
    - 推荐 Lab：`SpringCoreSpelLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. SpEL 调用链（parse → AST → evaluate）](03-spel-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 关键分支矩阵（SpEL）](05-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04. 断点地图（SpEL Debugger Pack）**
- 建议入口：优先运行 `SpringCoreSpelLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：先确认 parse 得到的表达式结构，再确认 context 提供的数据（root/variables/accessors）。
- 源码入口：`SpelExpressionParser#parseExpression` / `SpelExpression#getValue`



## A. parser：表达式字符串怎么变成 AST？

- 建议断点：`SpelExpressionParser#parseExpression`
- 观察点：解析出来的 expression 与内部 AST（如果进一步深入）

## B. evaluation：为什么取不到值？

- 建议断点：`SpelExpression#getValue`
- 观察点：evaluation context 的 root object 与 variables

## 小结与下一章

- 小结：先确认 parse 得到的表达式结构，再确认 context 提供的数据（root/variables/accessors）。
- 下一章：[第 210 章：04：关键分支矩阵](05-branch-decision-matrix.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreSpelLabTest`

上一章：[part-00-guide/01-spel-call-chain.md](03-spel-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](05-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
