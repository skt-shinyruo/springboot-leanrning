# 03. SpEL 调用链（parse → AST → evaluate）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：SpEL 调用链（parse → AST → evaluate）展开，主线可以概括为：parser 把表达式字符串解析为 AST；getValue() 用 evaluation context 提供 root/variables/property access/method resolve 来求值。

    先跑 `SpringCoreSpelLabTest`，再按本章把 parse/getValue 串成可复述调用链。

    需要下探源码时，可以从 `SpelExpressionParser#parseExpression` / `SpelExpression#getValue` / `StandardEvaluationContext` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖导读：把“表达式求值”落到源码与断点](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. 断点地图（SpEL）](guide-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `SpringCoreSpelLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：parser 把表达式字符串解析为 AST；getValue() 用 evaluation context 提供 root/variables/property access/method resolve 来求值。需要下探源码时，可以从 `SpelExpressionParser#parseExpression` / `SpelExpression#getValue` / `StandardEvaluationContext` 这些入口切入。


## 最短调用链

1. `SpelExpressionParser#parseExpression(String)`：把字符串解析成 `SpelExpression`
2. `SpelExpression#getValue(...)`：开始求值
3. AST 节点逐层求值（属性访问/运算符/比较等）
4. evaluation context 提供：
   - root object（`#root`/默认对象）
   - variables（`#var`）
   - property accessor / type locator / method resolver

证据链入口：

- `SpringCoreSpelLabTest`

## 小结与下一章

parser 把表达式字符串解析为 AST；getValue() 用 evaluation context 提供 root/variables/property access/method resolve 来求值。

下一章见：[02：断点地图](guide-breakpoint-map.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreSpelLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-breakpoint-map.md](guide-breakpoint-map.md)

<!-- BOOKIFY:END -->
