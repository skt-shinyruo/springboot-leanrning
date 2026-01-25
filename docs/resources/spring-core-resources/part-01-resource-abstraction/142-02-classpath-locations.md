# 第 142 章：02. classpath 路径：`classpath:data/x` vs `classpath:/data/x` 有什么区别？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：classpath 路径：`classpath:data/x` vs `classpath:/data/x` 有什么区别？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
    - 原理：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
    - 源码入口：`org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 141 章：01. `Resource` 抽象：为什么 Spring 不让你直接用 `File`？](141-01-resource-abstraction.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 143 章：03. `classpath*:` 与 pattern：为什么它能“扫到多个资源”？](143-03-classpath-star-and-pattern.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02. classpath 路径：`classpath:data/x` vs `classpath:/data/x` 有什么区别？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreResourcesLabTest`

## 机制主线

- 有的人写 `classpath:data/hello.txt`
- 有的人写 `classpath:/data/hello.txt`

- `readsClasspathResourceContent`：`classpath:data/hello.txt`
- `supportsLeadingSlashInClasspathLocation`：`classpath:/data/hello.txt`

两者都能读到 `Hello from classpath`。

## 你应该得到的结论

- 学习阶段你可以把它们当作等价写法（在常见 classpath 场景下）
- 更重要的是：别把 classpath 资源当作 `File` 路径使用（尤其是打包成 jar 后）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreResourcesLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证

看 `SpringCoreResourcesLabTest`：

## 常见坑与边界

学习阶段最容易踩的坑之一是 classpath 路径写法不一致：

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesLabTest`

上一章：[01-resource-abstraction](141-01-resource-abstraction.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03-classpath-star-and-pattern](143-03-classpath-star-and-pattern.md)

<!-- BOOKIFY:END -->
