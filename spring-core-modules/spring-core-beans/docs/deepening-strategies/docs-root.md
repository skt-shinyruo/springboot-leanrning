# 逐章内容级再加深建议（Docs TOC / 目录页）

## 导读

本文属于“加深策略”说明：用于解释本仓库文档与测试在结构上的组织方式，以及如何用最小入口把阅读、调试与验证连成闭环。


## 官方文档对照（版本语境）

- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）
- Spring Boot：`3.5.9`

- Spring Framework Reference（Beans）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（容器扩展点）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html


目标：把 `docs/README.md` 强化为“症状驱动导航中枢”，同时保证它不替代章节正文，而是把读者精准送到“章节 + 证据链 + Lab”。

## 执行化提示（把目录页与章节闭环对齐）

- 章节层已统一补齐：开头“章节学习卡片（五问闭环）”与“上一章/下一章导航”。
- 目录页的最佳增益点：把“症状 → 章节”升级为“症状 → 章节卡片（入口方法/推荐 Lab） → 断点/观察点”，避免目录页堆概念。

### spring-core-beans 文档导航（Docs TOC）

- 关联文件：`spring-core-modules/spring-core-beans/docs/README.md`
- 继续加深建议：
    - 从该章节正文里挑 1 个最短 Lab/Test 入口（或回链到本 Part 的 Quickstart），把现象跑出来。
    - 优先复用该章正文已给出的断点组（不要只列方法名，要写清“看什么/怎么判定”）。
    - 将目录页的价值从“列链接”提升为“给路径”：为关键节点补一句“为什么现在读它”，并在 proxy/事务/自调用等处给出 Beans→AOP 的最短跳转与目的说明。
