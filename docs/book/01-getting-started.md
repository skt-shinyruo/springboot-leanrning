# 01 Getting Started：如何在本仓库学习（入口与证据链）

## 学习目标

- 知道本仓库的学习单元是什么（模块、文档、测试入口），以及主线章节的职责边界。
- 能在 10 分钟内跑通一个稳定入口（Book Matrix），并回到模块文档继续顺读。
- 知道遇到红测/异常时，应该先去哪里查（常见坑、自检、断点图、分支矩阵）。

## 概念框架

- **模块（module）**：主题边界。每个模块都有自己的 `docs/` 与可运行测试入口。
- **文档（docs）**：用于组织阅读顺序与断点入口，不替代代码事实。
- **Labs / Exercises**：
  - `*LabTest`：可复现现象 + 断言证据链（默认启用，应保持全绿）。
  - `*ExerciseTest`：动手改写题（通常 `@Disabled`，由学习者手动开启）。
- **Book Matrix**：把多个关键 Lab 聚合为一个“主线入口”，适合用作每章的起跑线。

## 实验入口

建议用一个“可跑入口”验证环境与学习路径是否闭环。默认从 `spring-boot-basics` 起步：

- Book Matrix（入门推荐）：
  - `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
  - 测试类：[`BootBasicsBookMatrixLabTest.java`](../../spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBookMatrixLabTest.java)
- 模块目录页（下一步阅读入口）：
  - [`spring-boot-basics/docs/README.md`](../../spring-boot-modules/spring-boot-basics/docs/README.md)

如果希望尽早建立“容器/代理”的可调试心智模型，可追加跑一个底层入口：

- `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`
- 测试类：[`SpringCoreBeansBookMatrixLabTest.java`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBookMatrixLabTest.java)

## 常见误区

- 只读目录与结论，不跑测试。结果：无法区分“概念理解”与“该项目的真实行为”。
- 把“模块 README”当成正文读完。模块 README 的职责是导航；正文在各 `part-*` 章节里。
- 看到异常直接搜博客。优先用本仓库的 **常见坑 / 自检 / 分支矩阵**，把问题归类成可验证分支。

## 练习

- 练习 1（最小闭环）：
  1) 运行 `BootBasicsBookMatrixLabTest`；
  2) 打开失败/关键断言所在方法；
  3) 沿着断言提示回到模块文档的“主线时间线”，确认该现象对应哪一章。
- 练习 2（建立调试入口）：
  - 打开 `spring-boot-basics` 的断点地图（模块文档的 `part-00-guide/04-breakpoint-map.md`），选择 3 个稳定断点，验证能命中并观察到关键变量变化。

## 小结

- 主线章节负责“把读者送到下一步可验证动作”，不承担“讲完全部细节”。
- 每章都用 Book Matrix 起跑；读不动时回到附录的“常见坑/自检/分支矩阵”。

## 延伸阅读

- 仓库根导读：[`../../README.md`](../../README.md)
- 全站导航（SSOT）：[`../../docs/SUMMARY.md`](../../docs/SUMMARY.md)
- 常见问题索引：[`90-troubleshooting-index.md`](90-troubleshooting-index.md)
- 术语对照表：[`91-glossary.md`](91-glossary.md)

---

[← 上一章](README.md) | [目录](README.md) | [下一章 →](02-spring-boot-basics.md)
