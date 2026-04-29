# 章节深化路线（模块 README）

## 定位：模块 README 的深化方式

模块 README 是读者进入 `spring-core-beans` 的第一层路由。深化时要让它同时承担三个任务：说明模块边界，给出最短可运行入口，并把常见症状导向对应章节和证据链。


## 官方文档对照（版本语境）

- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）
- Spring Boot：`3.5.9`

- Spring Framework Reference（Beans）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（容器扩展点）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html


落点是把模块入口从导航提升为问题驱动入口 + 证据链入口，并确保它与 docs 目录下各正文页的症状导航形成一致闭环。

## 执行化提示（本轮落地的默认结构）

- 章节正文已经提供入口提示、对应 Lab 和断点锚点，README 应负责把读者送到正确入口。
- 落地顺序：先写清入口/证据链/断点/Lab，再在正文补反例和排障 SOP，最后用面试常问与自检要点固化表达。

### spring-core-beans（模块 README）

- 关联文件：`spring-core-modules/spring-core-beans/README.md`
- 深化落点：
    - 从该章节正文里挑 1 个最短实验/测试入口（或回链到本部分的 Quickstart），把现象跑出来。
    - 优先复用该章正文已给出的断点组（不要只列方法名，要写清“看什么/怎么判定”）。
    - 把这一章最容易误判的 1 个点，写成“现象 → 第一入口 → 关键分支 → 结论”的最短诊断路径（读者可复用）。
