# 全书主线：Spring Boot / Spring Core 教材式顺读

本目录提供一条“教材式主线”：按章聚合与指引，把读者送到**下一步可验证动作**（模块文档 + 可运行测试入口），不重复抄写各模块正文。

## 学习目标

- 明确全书主线与模块文档的分工：主线负责聚合与指引，模块负责机制正文与断点细节。
- 能用统一命令运行每章的稳定入口（`*BookMatrixLabTest`），把现象跑成事实再阅读。
- 知道如何维护主线章节：只聚合、只链接、只提供可执行入口与导航。

## 概念框架

- **主线章节（`docs/book/`）**：跨模块聚合，强调“下一步动作”，不复制正文。
- **模块文档（`*/docs/`）**：机制正文、调用链、断点图、分支矩阵与自检清单。
- **测试入口（`*BookMatrixLabTest`）**：可运行的事实基线；用于复现、排障、回归。

## 实验入口

首次进入本书时，建议用一个主线入口验证环境与阅读闭环：

- `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`

## 常见误区

- 把主线章节写成“第二份模块正文”，导致重复与维护成本失控。
- 只堆链接不提供可执行入口，读者无法把结论落地为事实。
- 不提供上一章/下一章/目录导航，导致章节无法连续顺读与复盘。

## 练习

- 练习 1：从目录进入 [02 Boot Basics](02-spring-boot-basics.md)，先跑 Book Matrix，再回到模块目录页顺读主线时间线。
- 练习 2：遇到任意红测/异常时，用 [90 Troubleshooting Index](90-troubleshooting-index.md) 分型并选择入口。

## 小结

- 主线章节的验收口径是：可顺读、可跳转、可运行、可回归。

## 延伸阅读

- 仓库根导读：[`../../README.md`](../../README.md)
- 全站导航（SSOT）：[`../../docs/SUMMARY.md`](../../docs/SUMMARY.md)
- 本地文档站与发布：[`../../docs-site/README.md`](../../docs-site/README.md)

本书约定：

- **先跑后读**：先运行 `*BookMatrixLabTest` 把现象跑成事实，再回到文档补齐机制与边界。
- **入口稳定**：主线默认只引用模块目录页（`*/docs/README.md`）与 `*BookMatrixLabTest`（以及少量调用链/断点图等“导航型”文档）。
- **命令格式统一**：所有“实验入口”使用同一种格式（模块 + 测试类）：
  - `mvn -q -pl :<module> -Dtest=<TestClass> test`
- **文档-代码一致性**：结论以代码与测试为准；文档只负责把证据链入口组织起来。

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

### 章节契约（每章必含）

每个主线章节必须包含以下小节（标题保持一致，便于检索与对齐）：

- 学习目标
- 概念框架
- 实验入口
- 常见误区
- 练习
- 小结
- 延伸阅读

### 链接策略（只聚合，不复写）

- 主线章节只写**最小解释 + 入口链接**，不复制模块 `docs/` 的正文内容。
- 优先链接模块目录页：`../../<module>/docs/README.md`。
- “如何调试/怎么打断点”的内容，优先链接模块的 `part-00-guide/`（调用链/断点图/分支矩阵）。

### 实验入口（统一命令）

- 章节必须至少给出一个 `*BookMatrixLabTest` 入口，并同时给出：
  - Maven 命令：`mvn -q -pl :<module> -Dtest=<TestClass> test`
  - 测试类源码链接：`../../<module>/src/test/java/.../<TestClass>.java`

### 章节导航（相对路径）

每章末尾固定追加：

`[← 上一章](...) | [目录](README.md) | [下一章 →](...)`

---

下一章：[`01-getting-started.md`](01-getting-started.md)
