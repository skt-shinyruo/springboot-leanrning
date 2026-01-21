# 第 1 章：Start Here（如何运行、如何读、如何调试）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Start Here（如何运行、如何读、如何调试）
    - 怎么使用：先跑 `mvn -q test` 确认环境；再用 `mvn -q -pl :<artifactId> -Dtest=<LabTest> test` 跑一个最小闭环；阅读时优先按章节“上一章/下一章”顺读，遇到概念就用推荐的断点/源码入口验证。
    - 原理：本仓库采用“证据链驱动学习”：每章先给五问闭环（学什么/怎么用/为什么/看哪段源码/跑哪个 Lab），再把结论固化为可回归的 `*LabTest`，最后用断点把关键分支变成可观察事实。
    - 源码入口：`org.springframework.boot.SpringApplication#run`（启动入口）/ `org.springframework.context.support.AbstractApplicationContext#refresh`（容器刷新主线）/ 本仓库 `*LabTest.java`（可跑证据链入口）
    - 推荐 Lab：`SpringCoreBeansLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[Book TOC](/) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 2 章：Boot 启动与配置主线](002-boot-basics-mainline.md)
<!-- GLOBAL-BOOK-NAV:END -->

这本“主线之书”希望解决的是：**你能顺着一条时间线把 Spring Boot / Spring Core 的关键机制跑通**，并且每一步都能落到可验证的证据（测试断言、断点、源码）。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Start Here（如何运行、如何读、如何调试） —— 先跑 `mvn -q test` 确认环境；再用 `mvn -q -pl :<artifactId> -Dtest=<LabTest> test` 跑一个最小闭环；阅读时优先按章节“上一章/下一章”顺读，遇到概念就用推荐的断点/源码入口验证。
- 回到主线：本仓库采用“证据链驱动学习”：每章先给五问闭环（学什么/怎么用/为什么/看哪段源码/跑哪个 Lab），再把结论固化为可回归的 `*LabTest`，最后用断点把关键分支变成可观察事实。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Start Here（如何运行、如何读、如何调试）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 先把项目跑起来（5 分钟）

### 1) 全仓库跑一遍（推荐先做一次）

```bash
mvn -q test
```

### 2) 只跑某个模块（更快、更聚焦）

```bash
mvn -q -pl :<artifactId> test
```

例如：

```bash
mvn -q -pl :springboot-web-mvc test
```

### 3) 运行某个模块（需要启动应用时）

```bash
mvn -pl :<artifactId> spring-boot:run
```

---

## Labs / Exercises：这套仓库是怎么“教你”机制的

你会在 `src/test/java` 里看到两类测试：

- **Labs**：`*LabTest.java`（默认启用，`mvn -q test` 必须全绿）  
  用来把机制的关键结论“固化成断言”，你可以边读边跑、边打断点边看数据结构如何变化。
- **Exercises**：`*ExerciseTest.java`（默认 `@Disabled`）  
  用来让你“动手改写/补齐”，把理解从“看懂”变成“能写出来”。

对应的索引页：

- [Labs 索引（可跑入口）](labs-index.md)
- [Exercises & Solutions（练习与答案）](exercises-and-solutions.md)

---

## 如何读主线（建议顺序）

如果你打算顺读，推荐从“启动 → 容器 → AOP/事务 → Web → 安全/数据 → 可观测性/测试 → 业务收束”读下去：

- 下一章入口：[第 1 章：Boot 启动与配置主线](002-boot-basics-mainline.md)

如果你是“带着问题来”的，建议：

1. 先在 [Debugger Pack](debugger-pack.md) 里把断点装好（入口/观察点/关键分支）
2. 再到模块 docs 的主线时间线章节找“完整机制图”
3. 最后回到对应 LabTest，用断言把结论固定下来

---

## 如何使用站点（导航说明）

侧边栏提供两条入口：

- **主线之书（Book）**：跨模块顺读主线（推荐入口）
- **模块文档**：按主题 → 模块 → 章节浏览（素材库/边界条件/排障）
- 主题索引（模块 docs 入口）：[`docs/topics/index.md`](../topics/index.md)

如果你想本地预览文档站：

```bash
bash scripts/docs-site-serve.sh
```

严格构建（用于 CI/验收）：

```bash
bash scripts/docs-site-build.sh
```

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test`（`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java`

---

## 下一步

- 继续顺读：[第 2 章：Boot 启动与配置主线](002-boot-basics-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Start Here（如何运行、如何读、如何调试）」的生效时机/顺序/边界；断点/入口：`org.springframework.boot.SpringApplication#run`（启动入口）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Start Here（如何运行、如何读、如何调试）」的生效时机/顺序/边界；断点/入口：`org.springframework.context.support.AbstractApplicationContext#refresh`（容器刷新主线）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Start Here（如何运行、如何读、如何调试）」的生效时机/顺序/边界；断点/入口：本仓库 `*LabTest.java`（可跑证据链入口）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreBeansLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
