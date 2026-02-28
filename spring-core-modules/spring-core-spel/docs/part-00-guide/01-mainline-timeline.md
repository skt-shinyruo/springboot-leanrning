# 01. 主线时间线：spring-core-spel
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕主线时间线：spring-core-spel展开，主线可以概括为：SpEL 的核心是：parser 把字符串变成表达式对象（AST），evaluation context 决定如何读取属性/变量/函数，最后 getValue() 求值。

    本页是导航页。先运行 `SpringCoreSpelLabTest`，把“表达式解析与求值”固化成断言，再按调用链定位到 parser 与 evaluation。

    需要下探源码时，可以从 `SpelExpressionParser#parseExpression` / `Expression#getValue` / `StandardEvaluationContext` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 深挖导读：把“表达式求值”落到源码与断点](02-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `SpringCoreSpelLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：SpEL 的核心是：parser 把字符串变成表达式对象（AST），evaluation context 决定如何读取属性/变量/函数，最后 getValue() 求值。需要下探源码时，可以从 `SpelExpressionParser#parseExpression` / `Expression#getValue` / `StandardEvaluationContext` 这些入口切入。


## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`

## 小结与下一章

SpEL 的核心是：parser 把字符串变成表达式对象（AST），evaluation context 决定如何读取属性/变量/函数，最后 getValue() 求值。

下一章见：[第 210 章：00. 深挖导读](02-deep-dive-guide.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreSpelLabTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md)

<!-- BOOKIFY:END -->
