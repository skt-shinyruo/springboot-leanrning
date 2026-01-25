# 第 146 章：06. jar vs filesystem：为什么在 IDE 里 OK，打包后就不行？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：jar vs filesystem：为什么在 IDE 里 OK，打包后就不行？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
    - 原理：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
    - 源码入口：`org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 145 章：05. 读取资源：InputStream、编码与“可观察性”](145-05-reading-and-encoding.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 147 章：90. 常见坑清单（建议反复对照）](../appendix/147-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**06. jar vs filesystem：为什么在 IDE 里 OK，打包后就不行？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

## 机制主线

因此这些做法容易出问题：

- `resource.getFile()`（classpath 资源在 jar 内时通常会失败）
- `new File("classpath:...")`（根本不是文件路径）

## 在本模块的练习入口

## 学习建议

学习阶段建议坚持使用：

- `Resource#getInputStream()`（最通用）
- `ResourcePatternResolver`（pattern 扫描）

等你把机制吃透后，再讨论“什么时候可以用 File 优化”。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章未显式引用 LabTest，先注入模块默认 LabTest 作为“合规兜底入口”（后续可逐章细化）。
- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

- IDE 运行：资源在 `target/classes`，看起来像文件夹
- 打包运行：资源在 jar 内部，不再是“文件系统路径”

看 `SpringCoreResourcesExerciseTest#exercise_jarVsFilesystem`：

- 目标：写一个实验对比 jar 与 filesystem 的差异
- 并把观察结果记录到 README/docs（学习用）

## 常见坑与边界

这是资源读取最经典的学习坑：

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`
- Exercise：`SpringCoreResourcesExerciseTest`

上一章：[05-reading-and-encoding](145-05-reading-and-encoding.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[90-common-pitfalls](../appendix/147-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
