# 第 141 章：01. `Resource` 抽象：为什么 Spring 不让你直接用 `File`？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：`Resource` 抽象：为什么 Spring 不让你直接用 `File`？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
    - 原理：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
    - 源码入口：`org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 140 章：深挖指南（Spring Core Resources）](../part-00-guide/140-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 142 章：02. classpath 路径：`classpath:data/x` vs `classpath:/data/x` 有什么区别？](142-02-classpath-locations.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01. `Resource` 抽象：为什么 Spring 不让你直接用 `File`？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreResourcesLabTest`

## 机制主线

> 同样一段读取逻辑，既可能读 classpath 文件，也可能读本地文件，也可能读 URL。

如果你只用 `File`：

- 你很容易把“资源路径”写死成磁盘路径（例如 `src/main/resources/...`），在 IDE 下看起来能跑，但它并不是 classpath 语义。
- 一旦打成 jar，classpath 资源会在 jar 包里；这时它不再是一个可用的文件路径，`getFile()` 往往直接失败。
- 更糟的是：同一段读取逻辑会在“开发环境 OK、部署后崩溃”，于是你开始写各种分环境 if/else——而这正是 `Resource` 抽象要替你避免的事情。

Spring 的选择是：先给你一个统一的 `Resource` 句柄，再让你用一致的方式读取它。

- **读取内容**：优先使用 `Resource#getInputStream()`
- **拿到文件路径**：谨慎使用 `getFile()`（对 jar/classpath 资源不友好）

## 本模块的最小闭环

`ResourceReadingService` 做了两件事：

- `readClasspathText(location)`：读取单个资源内容
- `listResourceLocations(pattern)`：用 pattern 扫描多个资源并返回 description 列表

`Resource` 的学习价值在于：

> 你写的是“读取资源”的逻辑，而不是“读取某种存储形态”的逻辑。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreResourcesLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

对应 tests：

- `SpringCoreResourcesLabTest#readsClasspathResourceContent`
- `SpringCoreResourcesLabTest#loadsMultipleResourcesWithPattern`

## 常见坑与边界

Spring 的 `Resource` 抽象解决的是一个常见问题：

- classpath 资源在 jar 包里时根本不是“文件路径”
- 你会在开发环境 OK、打包后崩溃（典型学习陷阱）

## 小结与下一章
<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：`Resource` 抽象要解决的不是“怎么读文件”，而是“别让存储形态泄漏到业务代码里”——先拿统一句柄，再用 `getInputStream()` 把差异关在框架层。
- 回到主线：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesLabTest`

上一章：[00-deep-dive-guide](../part-00-guide/140-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02-classpath-locations](142-02-classpath-locations.md)

<!-- BOOKIFY:END -->
