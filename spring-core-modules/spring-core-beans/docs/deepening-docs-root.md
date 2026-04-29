# 章节深化路线（模块目录与目录页）

## 定位：模块目录页的深化方式

模块目录页的职责不是讲完整机制，而是把读者送到正确章节、正确实验和正确断点。深化时应把目录从“链接清单”改成“路线入口”：读者看到一个症状后，能迅速找到对应章节和证据链。


## 官方文档对照（版本语境）

- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）
- Spring Boot：`3.5.9`

- Spring Framework Reference（Beans）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（容器扩展点）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html


落点是把模块根 `README.md` 强化为症状驱动导航中枢，同时保证它不替代章节正文，而是把读者精准送到“章节 + 证据链 + Lab”。

## 执行化提示（把目录页与章节闭环对齐）

- 章节层已经提供章节入口、对应 Lab 和断点锚点，目录页应复用这些入口，不重复展开机制细节。
- 目录页的最佳增益点，是把“症状 → 章节”升级为“症状 → 章节卡片（入口方法/对应 Lab） → 断点/观察点”，避免目录页堆概念。

### spring-core-beans 文档导航（模块目录）

- 关联文件：`spring-core-modules/spring-core-beans/README.md`
- 深化落点：
    - 从该章节正文里挑 1 个最短实验/测试入口（或回链到本部分的 Quickstart），把现象跑出来。
    - 优先复用该章正文已给出的断点组（不要只列方法名，要写清“看什么/怎么判定”）。
    - 将目录页的价值从“列链接”提升为“给路径”：为关键节点补一句“为什么现在读它”，并在 proxy/事务/自调用等处给出 Beans→AOP 的最短跳转与目的说明。
