# 01 入门：把“学习路径”落到可运行的证据链

本章不讲 Spring 机制。它只回答一个更现实的问题：面对一个多模块仓库、上千页文档与大量测试入口时，如何在最短时间内把“阅读—运行—调试”变成闭环。

本仓库的组织方式与常见教程不同：目录不是起点，**测试才是起点**。原因在于——机制类知识最容易“听懂但用不出来”，而测试能把概念压成事实：能复现它、断言它、也能在它坏掉时把问题定位出来。

---

## 问题：从哪里开始，才不会陷入“读了很多但没抓住主线”

读者最常见的卡点通常不是“看不懂某段源码”，而是：

- 入口太多，不知道哪一个才是稳定的起跑线；
- 读完一页结论，却不知道它对应哪个现象、哪个断点；
- 看到异常后开始到处搜答案，最后无法判断“是版本差异，还是理解错了边界”。

本章的目标，是让“入口选择”变成一件可重复的事：先跑一个最短实验，把事实钉住；再回到文档，沿着证据链顺着读下去。

---

## 实验：跑通第一条主线入口（10 分钟闭环）

从 `spring-boot-basics` 开始，用一个 Book Matrix 把环境与路径跑通：

- 运行：
  - `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- 阅读入口（测试类）：
  - [`BootBasicsBookMatrixLabTest.java`](../../spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBookMatrixLabTest.java)
- 下一跳（模块目录页）：
  - [`spring-boot-basics/README.md`](../../spring-boot-modules/spring-boot-basics/README.md)

运行成功后，读者不需要立刻解释原因，但应该能说清两件事实：

1. 当前激活了哪些 profile；
2. 某个配置 key 的最终值是什么（最终事实在 `Environment` 中，而不是在某个配置文件中）。

如果希望更早把“容器/代理”的边界跑起来，可以追加一个更底层的入口（这会在后续章节展开）：

- `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`
- 测试类：[`SpringCoreBeansBookMatrixLabTest.java`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBookMatrixLabTest.java)

---

## 解释：这个仓库的“学习单元”是什么

为了避免阅读与回归互相冲突，本仓库把内容拆成三个层次：

- **模块（module）**：主题边界。每个模块都能独立运行与测试。
- **模块文档（`*/docs/`）**：机制正文、调用链、断点地图、分支矩阵与自检清单都在这里。
- **测试入口（实验/练习）**：
  - `*LabTest`：复现现象，并把现象固化为断言（默认启用，作为回归基线）；
  - `*ExerciseTest`：动手改写题（通常 `@Disabled`，用于练习与自证）。

主线章节（`docs/book/`）只承担一个职责：把读者送到“下一步可验证动作”。机制正文与细节不在主线重复维护。

---

## 边界：三个高频误区（以及如何在本仓库里自证）

**误区一：只读目录与结论，不跑测试。**
读完的“理解”没有锚点，很难判断是“概念理解”还是“恰好说对”。解决方式是先固定事实：跑 Book Matrix，然后只围绕失败/关键断言展开阅读。

**误区二：把模块根 `README.md` 当成正文。**
模块 `README.md` 的职责是导航与路线，正文在模块 `docs/*.md` 章节里（`docs/` 目录保持扁平、顺序只在 `README.md` 维护）。判断一个页面是否是“正文”，看它是否提供了实验入口与机制解释，而不仅仅是链接列表。

**误区三：看到异常直接外部搜答案。**
外部资料无法替对齐本仓库的版本语境与实验入口。更可控的路径是：先用本仓库的“常见坑/自检/分支矩阵”把问题归类成可验证分支，再去对照官方 Reference 或外部资料。

---

## 小结

- 在这个仓库里，阅读入口不是目录，而是测试：先把现象跑成事实，再回到文档解释原因与边界。
- 主线章节负责“导航与起跑线”，机制正文在模块 `*/docs/` 中维护。

---

[← 上一章](README.md) | [目录](README.md) | [下一章 →](02-spring-boot-basics.md)
