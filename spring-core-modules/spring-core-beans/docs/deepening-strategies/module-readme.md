# 逐章内容级再加深建议（模块 README）

目标：把模块入口从“导航”提升为“问题驱动入口 + 证据链入口”，并确保与 docs/README 的症状导航形成一致闭环。

## 执行化提示（本轮落地的默认结构）

- 章节正文已统一补齐：`CHAPTER-CARD`（开头五问闭环）+ `GLOBAL-BOOK-NAV`（上一章/下一章导航）。
- 推荐落地顺序：先把“入口/证据链/断点/Lab”写进章节学习卡片，再在正文补反例/排障 SOP，最后用“面试常问/自检要点”固化表达。

### spring-core-beans（模块 README）

- 关联文件：`spring-core-modules/spring-core-beans/README.md`
- 本轮内容级加深策略（A–E）：
  - A：在 README 中明确“证据链最短路径”——读者遇到问题时先跳到哪个章节、对应的关键入口方法是什么。
  - B：补充 3–5 个最常见误用反例（例如把 dependsOn 当注入、把 @Order 当单候选选择、把 FactoryBean 当普通 bean）。
  - C：补充“症状入口”索引（与 docs/README 的表保持一致或互为补充），确保从 README 能直接走到排障主线。
  - D：补充 Debugger Pack/断点地图的入口解释：什么时候看断点地图、什么时候看 Debugger Pack、如何组合使用。
  - E：补充“面试复述路径”入口：从 interview playbook 反向回到章节与 Lab 的证明方式。
