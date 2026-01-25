# 第 145 章：05. 读取资源：InputStream、编码与“可观察性”
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：读取资源：InputStream、编码与“可观察性”
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
    - 原理：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
    - 源码入口：`org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesMechanicsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 144 章：04. `getResource(...)` 的返回值：为什么它会“返回一个不存在的资源句柄”？](144-04-exists-and-handles.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 146 章：06. jar vs filesystem：为什么在 IDE 里 OK，打包后就不行？](146-06-jar-vs-filesystem.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05. 读取资源：InputStream、编码与“可观察性”**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreResourcesMechanicsLabTest`

## 机制主线

资源读取看起来简单，但学习阶段建议你建立两个习惯：

1) 始终明确编码（尤其文本）  
2) 把错误转换成“更好理解的异常/提示”

- `resource.getInputStream()`
- 读 bytes
- 用 `StandardCharsets.UTF_8` 构建字符串

看 `ResourceReadingService#readClasspathText`：

`Resource#getDescription()` 很有用：

- 它能告诉你这个 resource 是从哪里来的
- 在 classpath/jar 相关问题里，description 往往比 path 更可信

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreResourcesMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证“读取方式”

看 `SpringCoreResourcesMechanicsLabTest#classpathResourceCanBeReadAsBytes`：

- 把 `IOException` 包成 `UncheckedIOException`（学习阶段更容易写 tests）

## Debug/观察建议

验证入口：`SpringCoreResourcesMechanicsLabTest#resourceDescriptionsHelpWithDebugging`

## 常见坑与边界

### 坑点 1：依赖平台默认编码读取文本，导致“本地正常、线上乱码”

- Symptom：在某台机器上中文/特殊字符乱码，换环境又正常
- Root Cause：平台默认编码不可控；文本读取必须显式指定 charset
- Verification：`SpringCoreResourcesMechanicsLabTest#classpathResourceCanBeReadAsBytes`（bytes → UTF-8 text）
- Fix：读取文本时始终显式指定 `StandardCharsets.UTF_8`；并把 `Resource#getDescription()` 打进异常或日志，提升可观察性

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesMechanicsLabTest`

上一章：[04-exists-and-handles](144-04-exists-and-handles.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06-jar-vs-filesystem](146-06-jar-vs-filesystem.md)

<!-- BOOKIFY:END -->
