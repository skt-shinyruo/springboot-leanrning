# 01. `Resource` 抽象：为什么 Spring 不直接使用 `File`？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕 `Resource` 抽象：为什么 Spring 不直接使用 `File`？展开，主线可以概括为：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。

    先运行 `SpringCoreResourcesLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。

    需要下探源码时，可以从 `org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Resources）](../part-00-guide/02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. classpath 路径：`classpath:data/x` vs `classpath:/data/x` 有什么区别？](02-classpath-locations.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreResourcesLabTest`

## 机制主线

> 同样一段读取逻辑，既可能读 classpath 文件，也可能读本地文件，也可能读 URL。

如果只用 `File`：

- 很容易把“资源路径”写死成磁盘路径（例如 `src/main/resources/...`），在 IDE 下看起来能跑，但它并不是 classpath 语义。
- 一旦打成 jar，classpath 资源会在 jar 包里；这时它不再是一个可用的文件路径，`getFile()` 往往直接失败。
- 更糟的是：同一段读取逻辑会在“开发环境 OK、部署后崩溃”，于是开始写各种分环境 if/else——而这正是 `Resource` 抽象要避免的事情。

Spring 的选择是：先提供一个统一的 `Resource` 句柄，再以一致的方式读取它。

- **读取内容**：优先使用 `Resource#getInputStream()`
- **拿到文件路径**：谨慎使用 `getFile()`（对 jar/classpath 资源不友好）

## 本模块的最小闭环

`ResourceReadingService` 做了两件事：

- `readClasspathText(location)`：读取单个资源内容
- `listResourceLocations(pattern)`：用 pattern 扫描多个资源并返回 description 列表

`Resource` 的学习价值在于：

> 所写的是“读取资源”的逻辑，而不是“读取某种存储形态”的逻辑。

## 最小可运行实验（Lab）

- Lab：`SpringCoreResourcesLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

对应 tests：

- `SpringCoreResourcesLabTest#readsClasspathResourceContent`
- `SpringCoreResourcesLabTest#loadsMultipleResourcesWithPattern`

## 常见坑与边界

Spring 的 `Resource` 抽象解决的是一个常见问题：

- classpath 资源在 jar 包里时根本不是“文件路径”
- 会在开发环境 OK、打包后崩溃（典型学习陷阱）

## 小结与下一章
<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：`Resource` 抽象要解决的不是“怎么读文件”，而是“别让存储形态泄漏到业务代码里”——先拿统一句柄，再用 `getInputStream()` 把差异关在框架层。
- 回到主线：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesLabTest`

上一章：[00-deep-dive-guide](../part-00-guide/02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02-classpath-locations](02-classpath-locations.md)

<!-- BOOKIFY:END -->
