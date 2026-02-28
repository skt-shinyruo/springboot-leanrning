# 02. classpath 路径：`classpath:data/x` vs `classpath:/data/x` 有什么区别？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕classpath 路径：`classpath:data/x` vs `classpath:/data/x` 有什么区别？展开，主线可以概括为：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。

    先运行 `SpringCoreResourcesLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。

    需要下探源码时，可以从 `org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. `Resource` 抽象：为什么 Spring 不直接使用 `File`？](01-resource-abstraction.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. `classpath*:` 与 pattern：为什么它能“扫到多个资源”？](03-classpath-star-and-pattern.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreResourcesLabTest`

## 机制主线

- 有的人写 `classpath:data/hello.txt`
- 有的人写 `classpath:/data/hello.txt`

- `readsClasspathResourceContent`：`classpath:data/hello.txt`
- `supportsLeadingSlashInClasspathLocation`：`classpath:/data/hello.txt`

两者都能读到 `Hello from classpath`。

## 应当得到的结论

- 学习阶段可以把它们当作等价写法（在常见 classpath 场景下）
- 更重要的是：别把 classpath 资源当作 `File` 路径使用（尤其是打包成 jar 后）

## 最小可运行实验（Lab）

- Lab：`SpringCoreResourcesLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

## 在本模块如何验证

看 `SpringCoreResourcesLabTest`：

## 常见坑与边界

学习阶段最容易踩的坑之一是 classpath 路径写法不一致：

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesLabTest`

上一章：[01-resource-abstraction](01-resource-abstraction.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03-classpath-star-and-pattern](03-classpath-star-and-pattern.md)

<!-- BOOKIFY:END -->
