# 全书主线：Spring Boot / Spring Core（教材式顺读）

本目录是一条“可顺读的教材主线”。它不尝试把 Spring 的机制讲完，而是把本仓库里分散在各模块中的**可运行证据链**按章节串成路线：每章先给出一个可以运行的实验入口，再把读者送到对应的模块正文与断点地图。

这本“书”的基本假设很简单：只有当现象能被复现、结论能被断言、边界能被反例验证时，理解才算落地。否则再漂亮的解释也容易在真实排障里失效。

---

## 如何使用这条主线

第一次打开时，不需要先读目录，也不需要先背概念。更稳妥的顺序是：

1. 运行本章的 `*BookMatrixLabTest`，让关键现象先变成“能复现的事实”；
2. 打开失败/关键断言所在的测试方法，明确“这一章要回答的问题”；
3. 回到对应模块的根 `README.md`（模块目录页），按“目录（唯一顺序来源）/断点地图/分支矩阵”继续深入。

主线章节承担“引导与聚合”的工作；机制正文、分支矩阵、断点清单在各模块的 `*/docs/` 中维护。

---

## 实验入口（从 10 分钟闭环开始）

用一个最小入口验证环境与阅读闭环（默认从 `spring-boot-basics` 起步）：

- `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`

运行成功后，读者应能回答两个事实问题：当前激活了哪些 profile？某个配置 key 的最终值是什么？（不要求立刻解释原因，先把事实钉住。）

---

## 当遇到问题时，如何快速回到“最短证据链”

顺读是一种方式；另一种更常见的阅读姿势是“问题驱动”。当出现具体症状时，不必从第 1 章重读，可以直接进入索引：

- 现象分型与入口选择：[`90 Troubleshooting Index`](90-troubleshooting-index.md)
- 术语对照（避免同词异义）：[`91 Glossary`](91-glossary.md)
- 参考资料与延伸阅读：[`92 References`](92-references.md)

---

## 维护约定（给贡献者）

主线章节的“合格标准”是：可顺读、可跳转、可运行、可回归。为避免维护成本失控：

- 主线章节只写**最小解释 + 可运行入口 + 导航**，不复制模块正文。
- 侧边栏导航以 `docs/SUMMARY.md` 为唯一事实来源（SSOT）。
- 写作风格与章节章法统一参考：[`docs/writing-style-guide.md`](../writing-style-guide.md)。

更多站点预览与构建方式见：[`docs-site/README.md`](../../docs-site/README.md)

---

## 两条阅读路径

### 路径 A：主线顺读（推荐）

按 01 → 14 顺序阅读，每章先跑本章的 Book Matrix，再进入该模块的目录页顺读主线章节。

### 路径 B：实验入口驱动（适合“先解决一个问题”）

遇到具体问题时，先从最接近症状的章节进入，运行该章的 Book Matrix，把现象/分支固定下来，然后回到模块文档的“断点地图/关键分支矩阵/常见坑”收敛定位路径。

- 400/406/415、绑定与错误响应形状 → [06 Web MVC](06-spring-boot-web-mvc.md) → `BootWebMvcBookMatrixLabTest`
- “为什么没走代理/切面/事务不生效” → [04 AOP](04-spring-core-aop.md) / [05 Tx](05-spring-core-tx.md) → `SpringCoreAopBookMatrixLabTest` / `SpringCoreTxBookMatrixLabTest`
- 观测信号缺失、日志级别不生效、Actuator 端点暴露问题 → [13 Observability & Actuator](13-observability-and-actuator.md)
- 校验没触发、Group/方法校验表现异常 → [07 Validation](07-spring-core-validation.md)

---

## 全书目录（主线章节）

- [01 Getting Started](01-getting-started.md)
- [02 Spring Boot Basics](02-spring-boot-basics.md)
- [03 Spring Core Beans](03-spring-core-beans.md)
- [04 Spring Core AOP](04-spring-core-aop.md)
- [05 Spring Core Tx](05-spring-core-tx.md)
- [06 Spring Boot Web MVC](06-spring-boot-web-mvc.md)
- [07 Spring Core Validation](07-spring-core-validation.md)
- [08 Spring Boot Testing](08-spring-boot-testing.md)
- [09 Spring Boot Data JPA](09-spring-boot-data-jpa.md)
- [10 Spring Boot Web Client](10-spring-boot-web-client.md)
- [11 Spring Boot Async & Scheduling](11-spring-boot-async-scheduling.md)
- [12 Spring Boot Cache](12-spring-boot-cache.md)
- [13 Observability & Actuator](13-observability-and-actuator.md)
- [14 Spring Boot Security](14-spring-boot-security.md)

---

## 索引与附录

- [90 Troubleshooting Index](90-troubleshooting-index.md)
- [91 Glossary](91-glossary.md)
- [92 References](92-references.md)

---

## 如何维护（写作与链接规范）

### 主线章节的“必含要素”（不强制同名标题）

主线章节的目标是“把读者送到下一步可验证动作”，因此每章至少应让读者获得以下信息（标题是否一致不作强制）：

- 这章要回答什么问题（或解释什么现象）
- 最短可跑入口（`*BookMatrixLabTest`）与预期观察点
- 机制解释的主线（只写够用的最小解释，不复写模块正文）
- 关键边界/反例（避免读者把结论套错场景）
- 小结（≤3 条可复述句）
- 下一跳（模块目录页 / 断点地图 / 分支矩阵的最短跳转）

更完整的写作约束与示例见：[`docs/writing-style-guide.md`](../writing-style-guide.md)。

### 链接策略（只聚合，不复写）

- 主线章节只写**最小解释 + 入口链接**，不复制模块 `docs/` 的正文内容。
- 优先链接模块目录页：`../../<module>/README.md`。
- “如何调试/怎么打断点”的内容，优先链接模块 `docs/` 下的 `guide-*.md`（调用链/断点图/分支矩阵）。

### 实验入口（统一命令）

- 章节必须至少给出一个 `*BookMatrixLabTest` 入口，并同时给出：
  - Maven 命令：`mvn -q -pl :<module> -Dtest=<TestClass> test`
  - 测试类源码链接：`../../<module>/src/test/java/.../<TestClass>.java`

### 章节导航（相对路径）

每章末尾固定追加：

`[← 上一章](...) | [目录](README.md) | [下一章 →](...)`

---

下一章：[`01-getting-started.md`](01-getting-started.md)
