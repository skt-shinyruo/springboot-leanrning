# 第 211 章：01：SpEL 入门（root/variables/property access）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：SpEL 入门（root/variables/property access）
    - 怎么使用：先跑 `SpringCoreSpelLabTest`，再对照本文把“root/variables”映射到 evaluation context。
    - 原理：SpEL 的求值依赖 context；root/variables 决定表达式读取的数据来源。
    - 源码入口：`StandardEvaluationContext` / `SpelExpression#getValue`
    - 推荐 Lab：`SpringCoreSpelLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 210 章：04：关键分支矩阵](../part-00-guide/210-04-branch-decision-matrix.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 212 章：90 - Common Pitfalls（spring-core-spel）](../appendix/212-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 1. root object

`StandardEvaluationContext(root)` 中的 root 影响：

- 直接写 `name`/`age` 这样的 property access

## 2. variables

`ctx.setVariable("x", ...)` 后，你可以用：

- `#x` 读取变量

## 3. 安全边界（提示）

SpEL 非常强大，但也意味着：

- 不要在“用户可控输入”上直接执行表达式
- 在真实工程需要限制 type/method access（本仓库先把机制跑通）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreSpelLabTest`

上一章：[part-00-guide/04-branch-decision-matrix.md](../part-00-guide/210-04-branch-decision-matrix.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](../appendix/212-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
