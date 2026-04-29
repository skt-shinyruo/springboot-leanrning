# 01. SpEL 入门（root/variables/property access）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：SpEL 入门（root/variables/property access）展开，主线可以概括为：SpEL 的求值依赖 context；root/variables 决定表达式读取的数据来源。

    先跑 `SpringCoreSpelLabTest`，再对照本章把“root/variables”映射到 evaluation context。

    需要下探源码时，可以从 `StandardEvaluationContext` / `SpelExpression#getValue` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. 关键分支矩阵（SpEL）](guide-branch-decision-matrix.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[常见坑清单](appendix-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `SpringCoreSpelLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：SpEL 的求值依赖 context；root/variables 决定表达式读取的数据来源。需要下探源码时，可以从 `StandardEvaluationContext` / `SpelExpression#getValue` 这些入口切入。


## 1. root object

`StandardEvaluationContext(root)` 中的 root 影响：

- 直接写 `name`/`age` 这样的 property access

## 2. variables

`ctx.setVariable("x", ...)` 后，可以用：

- `#x` 读取变量

## 3. 安全边界（提示）

SpEL 非常强大，但也意味着：

- 不要在“用户可控输入”上直接执行表达式
- 在真实工程需要限制 type/method access（本仓库先把机制跑通）

## 小结与下一章

SpEL 的求值依赖 context；root/variables 决定表达式读取的数据来源。

下一章见：[常见坑清单](appendix-common-pitfalls.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreSpelLabTest`

上一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[appendix-common-pitfalls.md](appendix-common-pitfalls.md)

<!-- BOOKIFY:END -->
