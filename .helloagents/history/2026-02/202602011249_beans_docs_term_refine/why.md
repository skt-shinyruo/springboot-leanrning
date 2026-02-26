# Change Proposal: spring-core-beans 文档术语降噪（抽象标签 → 更直白表述）

## Requirement Background

`spring-core-modules/spring-core-beans/docs/` 以“可验证 + 可排障”为核心风格，但当前在少数章节与导航中仍存在一些容易口号化的抽象表述。

在实际阅读/交流场景里，该词容易产生两类摩擦：

1. **语义误解**：读者可能把抽象标签误读为“口号/玄学”，或与机器学习语境的 model 混淆。
2. **叙述不够直白**：同样的内容用“运行机制/解释框架/入口理解”等更直白的表述，通常更利于快速建立证据链与调试路径。

本次变更的目标不是删减机制深度，而是**降低术语噪音**：在不破坏已有“方法级主线 + 可复现实验 + 排障闭环”的前提下，把抽象标签替换为更贴近读者直觉的说法。

## Change Content

1. **统一术语**：在 `spring-core-beans` 文档中把抽象标签相关表述替换为“理解框架/运行机制/入口理解”等更直白表述（按上下文选择，避免机械替换）。
2. **同步导航与索引**：同步更新 docs README、知识地图、章节跳转导航中的章节名描述，保持阅读路径一致。
3. **跨模块引用降噪**：对 `spring-core-beans` 文档中引用的 AOP 章节链接文本做降噪处理（保留链接目标，优化表述）。

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`（docs）
- **Files:** 以 `spring-core-modules/spring-core-beans/docs/**` 为主（具体见 task.md）
- **APIs:** None
- **Data:** None

## Success Criteria

- `spring-core-beans/docs` 内不再出现口号化抽象标签（仍保留必要的机制表达与证据链）。
- 章节导航与知识地图对第 20 章/相关引用的描述保持一致，避免读者“跳转后标题不一致”的困惑。
- 文档相对链接目标存在性自检通过；引用的 Lab/Test 类名仍真实存在。

## Risk Assessment

- **Risk:** 只改“文字”可能导致跨章节引用名不一致或造成“标题与链接文本不匹配”的阅读落差。
- **Mitigation:** 统一在 beans docs 范围内同步修订导航/索引条目，并执行链接/引用自检；不更改文件路径与目录结构，避免断链。
