# 第 140 章：02：断点地图（Resources Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Resources Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
    - 原理：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
    - 源码入口：`org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesMechanicsLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 140 章：00 - Deep Dive Guide（spring-core-resources）](140-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 140 章：04：关键分支矩阵（Branch Decision Matrix）](140-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Resources Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
- 回到主线：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 怎么用这页

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「02：断点地图（Resources Debugger Pack）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesMechanicsLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 运行入口（建议先跑）

- Book Matrix：`SpringCoreResourcesBookMatrixLabTest`
- Branch Matrix：`SpringCoreResourcesBranchMatrixLabTest`

## 断点（解析与读取）

- `org.springframework.core.io.DefaultResourceLoader#getResource`
- `org.springframework.core.io.support.PathMatchingResourcePatternResolver#getResources`
- `org.springframework.core.io.ClassPathResource#getInputStream`

## Watchpoints（建议）

- `resource.getDescription()` / `resource.getURL()`
- `resource.exists()` / `resource.isReadable()`
- “解析出的 resources 数量”（pattern 分支证据）

## 排障入口（Playbook）

- 常见坑：[`../appendix/147-90-common-pitfalls.md`](../appendix/147-90-common-pitfalls.md)
- 自检：[`../appendix/148-99-self-check.md`](../appendix/148-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesMechanicsLabTest` / `SpringCoreResourcesBookMatrixLabTest` / `SpringCoreResourcesBranchMatrixLabTest`

上一章：[jar vs filesystem](../part-01-resource-abstraction/146-06-jar-vs-filesystem.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[140-04-branch-decision-matrix.md](140-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
