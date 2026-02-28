# 04. `getResource(...)` 的返回值：为什么它会“返回一个不存在的资源句柄”？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕 `getResource(...)` 的返回值：为什么它会“返回一个不存在的资源句柄”？展开，主线可以概括为：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。

    先运行 `SpringCoreResourcesMechanicsLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。

    需要下探源码时，可以从 `org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[03. `classpath*:` 与 pattern：为什么它能“扫到多个资源”？](03-classpath-star-and-pattern.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 读取资源：InputStream、编码与“可观察性”](05-reading-and-encoding.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「04. `getResource(...)` 的返回值：为什么它会“返回一个不存在的资源句柄”？」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `SpringCoreResourcesMechanicsLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreResourcesMechanicsLabTest`

## 机制主线

很多人第一次看到下面的现象会困惑：

> `resolver.getResource("classpath:data/missing.txt")` 也会返回一个 Resource 对象。

这并不是 bug，这是设计。

- `getResource(...)` 返回一个 handle（句柄）
- 需要用 `resource.exists()` 判断它是否真实存在

## 为什么要这样设计？

因为 Resource 的目标是统一抽象：

## 学习建议

当需要更友好的错误处理时：

## 最小可运行实验（Lab）

- Lab：`SpringCoreResourcesMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

这一章的关键结论可以浓缩成一句话：`getResource(...)` 返回的是句柄，不是“存在性证明”。因此验证应当总是包含 `exists()`（或一次可控的读取）。

## 在本模块如何验证

看 `SpringCoreResourcesMechanicsLabTest#getResourceReturnsAHandle_evenIfTheResourceDoesNotExist`

- “如何定位资源” 与 “资源是否存在” 是两件事
- 句柄可以携带描述信息，方便 debug（见 [05. reading-and-encoding](05-reading-and-encoding.md)）

- 先显式 `exists()` 判断
- 再决定抛出什么异常/提示（Exercise 会引导练习）

## 常见坑与边界

### 坑点 1：拿到 Resource 就以为“资源存在”，忽略了它只是句柄

拿到 `Resource` 直接读，结果在运行时抛异常；或者把 null/不存在当成路径拼错

`getResource(...)` 返回的是句柄（handle），资源是否存在需要 `exists()` 或读取时才能确定

- 句柄存在但资源不存在：`SpringCoreResourcesMechanicsLabTest#getResourceReturnsAHandle_evenIfTheResourceDoesNotExist`
- 缺失资源读取会抛异常：`SpringCoreResourcesLabTest#missingResourceCausesUncheckedIOException`

定位与存在性分开处理：先 `exists()`（或尝试读取并转成更友好的异常），并在错误里输出 `getDescription()` 辅助排障

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesMechanicsLabTest`

上一章：[03-classpath-star-and-pattern](03-classpath-star-and-pattern.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05-reading-and-encoding](05-reading-and-encoding.md)

<!-- BOOKIFY:END -->
