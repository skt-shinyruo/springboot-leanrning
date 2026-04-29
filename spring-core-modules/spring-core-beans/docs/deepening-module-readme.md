# 章节深化路线（模块 README）

## 起点：章节深化路线（模块 README）

这类页面用于校准文档结构：把章节、最小实验、断点入口和验证口径放到同一张路线图里。


## 官方文档对照（版本语境）

- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）
- Spring Boot：`3.5.9`

- Spring Framework Reference（Beans）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（容器扩展点）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html


落点：把模块入口从“导航”提升为“问题驱动入口 + 证据链入口”，并确保与 docs/README 的症状导航形成一致闭环。

## 执行化提示（本轮落地的默认结构）

- 章节正文已经提供入口提示与前后章节导航。
- 落地顺序：先把“入口/证据链/断点/Lab”写进章节入口，再在正文补反例/排障 SOP，最后用“面试常问/自检要点”固化表达。

### spring-core-beans（模块 README）

- 关联文件：`spring-core-modules/spring-core-beans/README.md`
- 深化落点：
    - 从该章节正文里挑 1 个最短实验/测试入口（或回链到本部分的 Quickstart），把现象跑出来。
    - 优先复用该章正文已给出的断点组（不要只列方法名，要写清“看什么/怎么判定”）。
    - 把这一章最容易误判的 1 个点，写成“现象 → 第一入口 → 关键分支 → 结论”的最短诊断路径（读者可复用）。
