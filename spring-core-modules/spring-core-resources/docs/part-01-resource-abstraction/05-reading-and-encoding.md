# 05. 读取资源：InputStream、编码与“可观察性”
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕读取资源：InputStream、编码与“可观察性”展开，主线可以概括为：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。

    先运行 `SpringCoreResourcesMechanicsLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。

    需要下探源码时，可以从 `org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. `getResource(...)` 的返回值：为什么它会“返回一个不存在的资源句柄”？](04-exists-and-handles.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06. jar vs filesystem：为什么在 IDE 里 OK，打包后就不行？](06-jar-vs-filesystem.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「05. 读取资源：InputStream、编码与“可观察性”」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreResourcesMechanicsLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreResourcesMechanicsLabTest`

## 机制主线

资源读取看起来简单，但学习阶段可以建立两个习惯：

1) 始终明确编码（尤其文本）
2) 把错误转换成“更好理解的异常/提示”

- `resource.getInputStream()`
- 读 bytes
- 用 `StandardCharsets.UTF_8` 构建字符串

看 `ResourceReadingService#readClasspathText`：

`Resource#getDescription()` 很有用：

- 它能说明这个 resource 是从哪里来的
- 在 classpath/jar 相关问题里，description 往往比 path 更可信

## 最小可运行实验（Lab）

- Lab：`SpringCoreResourcesMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

## 在本模块如何验证“读取方式”

看 `SpringCoreResourcesMechanicsLabTest#classpathResourceCanBeReadAsBytes`：

- 把 `IOException` 包成 `UncheckedIOException`（学习阶段更容易写 tests）

## Debug/观察建议

验证入口：`SpringCoreResourcesMechanicsLabTest#resourceDescriptionsHelpWithDebugging`

## 常见坑与边界

### 坑点 1：依赖平台默认编码读取文本，导致“本地正常、线上乱码”

在某台机器上中文/特殊字符乱码，换环境又正常

平台默认编码不可控；文本读取必须显式指定 charset

`SpringCoreResourcesMechanicsLabTest#classpathResourceCanBeReadAsBytes`（bytes → UTF-8 text）

读取文本时始终显式指定 `StandardCharsets.UTF_8`；并把 `Resource#getDescription()` 打进异常或日志，提升可观察性

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesMechanicsLabTest`

上一章：[04-exists-and-handles](04-exists-and-handles.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[06-jar-vs-filesystem](06-jar-vs-filesystem.md)

<!-- BOOKIFY:END -->
