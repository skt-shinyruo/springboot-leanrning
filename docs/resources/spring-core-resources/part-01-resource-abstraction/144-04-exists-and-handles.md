# 第 144 章：04. `getResource(...)` 的返回值：为什么它会“返回一个不存在的资源句柄”？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：`getResource(...)` 的返回值：为什么它会“返回一个不存在的资源句柄”？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
    - 原理：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
    - 源码入口：`org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesMechanicsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 143 章：03. `classpath*:` 与 pattern：为什么它能“扫到多个资源”？](143-03-classpath-star-and-pattern.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 145 章：05. 读取资源：InputStream、编码与“可观察性”](145-05-reading-and-encoding.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04. `getResource(...)` 的返回值：为什么它会“返回一个不存在的资源句柄”？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreResourcesMechanicsLabTest`

## 机制主线

很多人第一次看到下面的现象会困惑：

> `resolver.getResource("classpath:data/missing.txt")` 也会返回一个 Resource 对象。

这并不是 bug，这是设计。

- `getResource(...)` 返回一个 handle（句柄）
- 你需要用 `resource.exists()` 判断它是否真实存在

## 为什么要这样设计？

因为 Resource 的目标是统一抽象：

## 学习建议

当你需要更友好的错误处理时：

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreResourcesMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证

看 `SpringCoreResourcesMechanicsLabTest#getResourceReturnsAHandle_evenIfTheResourceDoesNotExist`

- “如何定位资源” 与 “资源是否存在” 是两件事
- 句柄可以携带描述信息，方便 debug（见 [05. reading-and-encoding](145-05-reading-and-encoding.md)）

- 先显式 `exists()` 判断
- 再决定抛出什么异常/提示（Exercise 里会让你练）

## 常见坑与边界

### 坑点 1：拿到 Resource 就以为“资源存在”，忽略了它只是句柄

- Symptom：你拿到 `Resource` 直接读，结果在运行时抛异常；或者把 null/不存在当成路径拼错
- Root Cause：`getResource(...)` 返回的是句柄（handle），资源是否存在需要 `exists()` 或读取时才能确定
- Verification：
  - 句柄存在但资源不存在：`SpringCoreResourcesMechanicsLabTest#getResourceReturnsAHandle_evenIfTheResourceDoesNotExist`
  - 缺失资源读取会抛异常：`SpringCoreResourcesLabTest#missingResourceCausesUncheckedIOException`
- Fix：定位与存在性分开处理：先 `exists()`（或尝试读取并转成更友好的异常），并在错误里输出 `getDescription()` 辅助排障

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesMechanicsLabTest`

上一章：[03-classpath-star-and-pattern](143-03-classpath-star-and-pattern.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05-reading-and-encoding](145-05-reading-and-encoding.md)

<!-- BOOKIFY:END -->
