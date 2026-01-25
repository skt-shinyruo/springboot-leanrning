# 第 210 章：01：SpEL 调用链（parse → AST → evaluate）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：SpEL 调用链（parse → AST → evaluate）
    - 怎么使用：先跑 `SpringCoreSpelLabTest`，再按本文把 parse/getValue 串成可复述调用链。
    - 原理：parser 把表达式字符串解析为 AST；getValue() 用 evaluation context 提供 root/variables/property access/method resolve 来求值。
    - 源码入口：`SpelExpressionParser#parseExpression` / `SpelExpression#getValue` / `StandardEvaluationContext`
    - 推荐 Lab：`SpringCoreSpelLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 210 章：00. 深挖导读](210-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 210 章：02：断点地图](210-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

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

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreSpelLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](210-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](210-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
