# 03. `classpath*:` 与 pattern：为什么它能“扫到多个资源”？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：`classpath*:` 与 pattern：为什么它能“扫到多个资源”？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
    - 原理：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
    - 源码入口：`org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. classpath 路径：`classpath:data/x` vs `classpath:/data/x` 有什么区别？](02-classpath-locations.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. `getResource(...)` 的返回值：为什么它会“返回一个不存在的资源句柄”？](04-exists-and-handles.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「03. `classpath*:` 与 pattern：为什么它能“扫到多个资源”？」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `SpringCoreResourcesLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

## 机制主线

当你想一次性加载多个资源时，会用到两件事：

- `ResourcePatternResolver`
- `classpath*:` + 通配符（pattern）

- pattern：`classpath*:data/*.txt`
- 断言能找到 `hello.txt` 与 `info.txt`

- `ResourceReadingService#listResourceLocations(...)` 会返回 `Resource#getDescription()`
- 并排序，保证断言稳定

## 学习建议：避免“顺序不稳定”误判机制

pattern 扫描返回的资源数组顺序不一定稳定（与 classpath 顺序、jar 顺序有关）。

本模块的做法值得复用：

- 把结果映射成可读的 description
- 排序后再断言

`classpath*:` 的价值在于：

> 它面向的是“classpath 上的所有匹配资源”，而不是某一个具体位置。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 在本模块如何验证

看 `SpringCoreResourcesMechanicsLabTest#classpathStarPatternLoadsResourcesFromClasspath`

看 `SpringCoreResourcesLabTest#patternResultsContainExpectedFilenames`：

## 常见坑与边界

### 坑点 1：把 `classpath:` 当成“能扫多个资源”，结果只拿到一个句柄或根本没匹配

- Symptom：你写了通配符但返回为空/只拿到一个资源，于是怀疑“pattern 不工作”
- Root Cause：
  - `classpath:` 是“单资源定位”语义
  - `classpath*:` 才是“扫描所有 classpath 并按 pattern 匹配”的语义
- Verification：
  - `classpath*:` + pattern 能加载多个资源：`SpringCoreResourcesMechanicsLabTest#classpathStarPatternLoadsResourcesFromClasspath`
  - pattern 结果包含预期文件名：`SpringCoreResourcesLabTest#patternResultsContainExpectedFilenames`
- Fix：需要扫描就用 `classpath*:`；并把结果映射成 description 后排序再断言（避免顺序不稳定误判）

## 小结与下一章
<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：`classpath*:` 的关键是“扫全 classpath 再按 pattern 匹配”——别指望 `classpath:` 帮你扫多个资源，且一定要把结果排序后再断言，避免顺序不稳定带来的误判。
- 回到主线：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

上一章：[02-classpath-locations](02-classpath-locations.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04-exists-and-handles](04-exists-and-handles.md)

<!-- BOOKIFY:END -->
