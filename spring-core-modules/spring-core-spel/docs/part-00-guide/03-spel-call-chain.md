# 03. SpEL 调用链（parse → AST → evaluate）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：SpEL 调用链（parse → AST → evaluate）
    - 怎么使用：先跑 `SpringCoreSpelLabTest`，再按本文把 parse/getValue 串成可复述调用链。
    - 原理：parser 把表达式字符串解析为 AST；getValue() 用 evaluation context 提供 root/variables/property access/method resolve 来求值。
    - 源码入口：`SpelExpressionParser#parseExpression` / `SpelExpression#getValue` / `StandardEvaluationContext`
    - 推荐 Lab：`SpringCoreSpelLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖导读：把“表达式求值”落到源码与断点](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 断点地图（SpEL Debugger Pack）](04-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. SpEL 调用链（parse → AST → evaluate）**
- 建议入口：优先运行 `SpringCoreSpelLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：parser 把表达式字符串解析为 AST；getValue() 用 evaluation context 提供 root/variables/property access/method resolve 来求值。
- 源码入口：`SpelExpressionParser#parseExpression` / `SpelExpression#getValue` / `StandardEvaluationContext`



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

- 小结：parser 把表达式字符串解析为 AST；getValue() 用 evaluation context 提供 root/variables/property access/method resolve 来求值。
- 下一章：[第 210 章：02：断点地图](04-breakpoint-map.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreSpelLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md)

<!-- BOOKIFY:END -->
