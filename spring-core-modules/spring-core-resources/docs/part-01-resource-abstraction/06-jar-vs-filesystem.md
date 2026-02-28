# 06. jar vs filesystem：为什么在 IDE 里 OK，打包后就不行？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕jar vs filesystem：为什么在 IDE 里 OK，打包后就不行？展开，主线可以概括为：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。

    先运行 `SpringCoreResourcesLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。

    需要下探源码时，可以从 `org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. 读取资源：InputStream、编码与“可观察性”](05-reading-and-encoding.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 常见坑清单（建议反复对照）](../appendix/01-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

## 机制主线

这一章解决的其实是一个**误会**：把 `Resource` 当成了 `File`。

在 IDE 里，这个误会经常“侥幸成立”——因为 `src/main/resources` 会被复制到 `target/classes`，看起来就像普通文件夹；
但一旦把应用打成 jar（尤其是 Spring Boot 的可执行 jar），资源就被塞进 jar 里，**不再是文件系统路径**，于是问题就暴露出来。

可以用一句话记住它：

> `Resource` 的底层可能是 file，也可能是 jar 里的 entry；**能稳定依赖的只有 `getInputStream()`**。

### 1) 典型误用：把 classpath 资源“强行落到 File”

下面这种写法在 IDE 下可能 OK，但打包后很容易炸：

```java
Resource resource = resolver.getResource("classpath:data/hello.txt");
File file = resource.getFile(); // IDE 可能 OK；jar 里通常不可靠
```

为什么？关键在于拿到的“资源 URL”是什么协议：

- IDE / `mvn test`：经常是 `file:/.../target/classes/data/hello.txt`（确实是磁盘文件）
- jar 运行：常见是 `jar:file:/.../app.jar!/BOOT-INF/classes!/data/hello.txt`（jar 里的 entry，不是文件）

当它不是 `file:` 协议时，`getFile()` 本质上是在要求框架“把一个不是文件的东西变成文件路径”，这当然做不到。

### 2) 正确心智模型：Resource 是 handle，读取走 stream

在本模块的实现里（见 `ResourceReadingService`），读取统一走：

- `ResourcePatternResolver#getResource(...)` 得到 `Resource`（注意：这只是一个 handle）
- `Resource#getInputStream()` 读取内容（这是跨 jar/filesystem 最稳定的方式）
- 需要调试时看 `resource.getDescription()`（它会把“我到底拿到了什么”说得更清楚）

### 3) pattern 扫描时：`classpath:` 与 `classpath*:` 的差异

如果在做“扫描多个资源文件”，建议形成肌肉记忆：

- `classpath:` 更接近“取一个”
- `classpath*:` 才是“从整个 classpath 里扫一遍”（见本模块的 mechanics/lab）

## 在本模块的练习入口

先用可运行的断言把“我以为”变成“我证明了”（建议按这个顺序跑）：

- Resource 只是 handle（不代表存在）：`SpringCoreResourcesMechanicsLabTest#getResourceReturnsAHandle_evenIfTheResourceDoesNotExist`
- 读取 classpath 的正确方式（stream）：`SpringCoreResourcesMechanicsLabTest#classpathResourceCanBeReadAsBytes` / `SpringCoreResourcesLabTest#readsClasspathResourceContent`
- 扫描多个资源（pattern）：`SpringCoreResourcesMechanicsLabTest#classpathStarPatternLoadsResourcesFromClasspath` / `SpringCoreResourcesLabTest#loadsMultipleResourcesWithPattern`
- 路径细节（leading slash）：`SpringCoreResourcesLabTest#supportsLeadingSlashInClasspathLocation`
- “file 资源”也能走同一套抽象：`SpringCoreResourcesLabTest#fileResourcesCanAlsoBeRead_viaResourceAbstraction`

如果想把 jar vs filesystem 变成“亲手复现过的结论”：

- 练习题入口（默认禁用，避免影响 CI）：`SpringCoreResourcesExerciseTest#exercise_jarVsFilesystem`
- 建议把观察记录成两条结论：`getInputStream()` 稳定；`getFile()` 取决于资源是否真的是文件

## 学习建议

学习阶段建议坚持使用：

- `Resource#getInputStream()`（最通用）
- `ResourcePatternResolver`（pattern 扫描）

待把机制吃透后，再讨论“什么时候可以用 File 优化”。

## 最小可运行实验（Lab）

- 先运行一遍 Lab，再回到本章对照机制；如果正在排障，可直接从“常见坑与边界”进入。
- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

不需要背结论，做一次对比就够了：

- IDE 运行：资源在 `target/classes`，通常是 `file:` URL → 很多“把资源当 File 用”的写法会蒙混过关
- 打包运行：资源在 jar 内部，通常是 `jar:` URL → `getFile()` 不再可靠

建议用“可观察性”做实验记录：

- 在断点/日志里看 `Resource#getDescription()`（它往往比自行猜路径更可靠）
- 观察 `Resource#getURL()` 的协议（`file:` vs `jar:`），再决定能不能用 `getFile()`

动手题见：`SpringCoreResourcesExerciseTest#exercise_jarVsFilesystem`（练习：对比两种运行方式，并把观察写成笔记）。

## 常见坑与边界

这是资源读取最经典的学习坑：

### 坑 1：在 IDE 里能跑 ≠ 打包后也能跑

IDE 里 `resource.getFile()` 正常，jar 一运行就抛异常（或读不到文件）

资源 URL 协议变了（`file:` → `jar:`），资源不再是文件系统路径

读取优先走 `getInputStream()`；如果确实需要 `File`，把流落到临时文件再处理（并明确这是“复制后的文件”）

### 坑 2：以为 `getResource(...)` 拿到对象就代表资源存在

`resolver.getResource("classpath:xxx")` 不为 null，但读取时失败

`getResource` 返回的是 handle；存在性要靠 `exists()`/读取来验证

`SpringCoreResourcesMechanicsLabTest#getResourceReturnsAHandle_evenIfTheResourceDoesNotExist`

### 坑 3：pattern 扫描写成 `classpath:`，结果只拿到“一个”

以为会扫到多个 `*.txt`，实际只返回一个/甚至为空

`classpath:` 与 `classpath*:` 语义不同

`SpringCoreResourcesMechanicsLabTest#classpathStarPatternLoadsResourcesFromClasspath`

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`
- Exercise：`SpringCoreResourcesExerciseTest`

上一章：[05-reading-and-encoding](05-reading-and-encoding.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[90-common-pitfalls](../appendix/01-common-pitfalls.md)

<!-- BOOKIFY:END -->
