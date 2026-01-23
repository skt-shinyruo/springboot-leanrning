# 第 209 章：03：主线时间线：spring-core-spel
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：spring-core-spel
    - 怎么使用：本页是导航页。建议先跑 `SpringCoreSpelLabTest`，把“表达式解析与求值”固化成断言，再按调用链定位到 parser 与 evaluation。
    - 原理：SpEL 的核心是：parser 把字符串变成表达式对象（AST），evaluation context 决定如何读取属性/变量/函数，最后 getValue() 求值。
    - 源码入口：`SpelExpressionParser#parseExpression` / `Expression#getValue` / `StandardEvaluationContext`
    - 推荐 Lab：`SpringCoreSpelLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[Book TOC](../../../book/index.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 210 章：00. 深挖导读](210-00-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreSpelLabTest`

上一章：[Book TOC](../../../book/index.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/00-deep-dive-guide.md](210-00-deep-dive-guide.md)

<!-- BOOKIFY:END -->
