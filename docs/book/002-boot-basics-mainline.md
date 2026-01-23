# 第 2 章：Boot 启动与配置主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Boot 启动与配置主线
    - 怎么使用：通过 `application.yml`/环境变量/命令行等配置进入 `Environment`，用 `@ConfigurationProperties` 做类型安全绑定，并将最终值用于条件装配或业务逻辑。
    - 原理：配置源（PropertySource）→ `Environment` 聚合与覆盖 → Binder 绑定 → Profile/优先级分流 → 影响条件装配与运行期行为。
    - 源码入口：`org.springframework.core.env.ConfigurableEnvironment` / `org.springframework.core.env.PropertySource` / `org.springframework.boot.context.properties.bind.Binder` / `org.springframework.boot.context.properties.ConfigurationPropertiesBinder`
    - 推荐 Lab：`BootBasicsOverrideLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 1 章：Start Here（如何运行、如何读、如何调试）](001-start-here.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 3 章：主线时间线：Spring Boot Basics（已迁移到“主线之书”）](../basics/spring-boot-basics/part-00-guide/003-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

这一章解决的问题很朴素：**一个 Spring Boot 应用启动时，“配置”到底从哪里来、最终值是什么、它怎么影响后续的 Bean 装配与运行期行为**。

你不需要先背配置项；你需要先能画出一条可验证的链路：**配置来源 → Environment → 绑定 → 影响条件装配/业务行为**。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Boot 启动与配置主线 —— 通过 `application.yml`/环境变量/命令行等配置进入 `Environment`，用 `@ConfigurationProperties` 做类型安全绑定，并将最终值用于条件装配或业务逻辑。
- 回到主线：配置源（PropertySource）→ `Environment` 聚合与覆盖 → Binder 绑定 → Profile/优先级分流 → 影响条件装配与运行期行为。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Boot 启动与配置主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

把配置当成“事实来源”，把 `Environment` 当成“最终事实数据库”：

1. 多来源配置进入 `Environment`（多份 `PropertySource`）
2. Profile 参与分流与覆盖（既影响“配置文件参与”，也影响“Bean 是否注册”）
3. `@ConfigurationProperties` 把字符串配置绑定成结构化对象（并暴露绑定失败/默认值/覆盖结果）
4. 业务代码读取绑定对象/Environment，行为由最终值决定

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/basics/spring-boot-basics/part-00-guide/004-02-breakpoint-map.md`](../basics/spring-boot-basics/part-00-guide/004-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/basics/spring-boot-basics/part-00-guide/004-04-branch-decision-matrix.md`](../basics/spring-boot-basics/part-00-guide/004-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/basics/spring-boot-basics/appendix/007-90-common-pitfalls.md`](../basics/spring-boot-basics/appendix/007-90-common-pitfalls.md)
- 自检清单：[`docs/basics/spring-boot-basics/appendix/008-99-self-check.md`](../basics/spring-boot-basics/appendix/008-99-self-check.md)

这章在书里给你“主线”，细节与证据链建议下沉到模块文档：

- 模块目录页（建议从这里顺读）：[`docs/basics/spring-boot-basics/README.md`](../basics/spring-boot-basics/README.md)

如果你想把“配置主线”进一步拆细成可回归的阅读顺序（推荐顺读）：

1. 配置从哪里来、谁覆盖谁：[`配置源与 Profiles`](../basics/spring-boot-basics/part-01-boot-basics/005-01-property-sources-and-profiles.md)
2. 配置如何绑定成对象：[`配置绑定（@ConfigurationProperties）`](../basics/spring-boot-basics/part-01-boot-basics/006-02-configuration-properties-binding.md)
3. 最常见的坑一次性清掉：[`常见坑`](../basics/spring-boot-basics/appendix/007-90-common-pitfalls.md) / [`自检`](../basics/spring-boot-basics/appendix/008-99-self-check.md)

如果你只跑一个最小闭环，优先从 Lab 入手：

- `BootBasicsOverrideLabTest`（建议先跑，能最快把“覆盖优先级”变成断言）

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsOverrideLabTest test`（`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`（`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix）：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBranchMatrixLabTest test`（`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseTest.java`

---

## 下一章怎么接

配置的最终值会进入容器与自动配置判断，所以下一章我们把视角切到 **IoC 容器本身**：

- 下一章：[第 9 章：IoC 容器主线（Beans）](009-ioc-container-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Boot 启动与配置主线」的生效时机/顺序/边界；断点/入口：`org.springframework.core.env.ConfigurableEnvironment`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Boot 启动与配置主线」的生效时机/顺序/边界；断点/入口：`org.springframework.core.env.PropertySource`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Boot 启动与配置主线」的生效时机/顺序/边界；断点/入口：`org.springframework.boot.context.properties.bind.Binder`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootBasicsOverrideLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
