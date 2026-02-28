# 04. 断点地图（Resources Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕02：断点地图（Resources Debugger Pack）展开，主线可以概括为：Resource 解析（location/pattern）→ 找到 URL/stream → 读取（encoding）→ jar 与 filesystem 的差异来自底层 URL/URLConnection。

    先跑 `SpringCoreResourcesBranchMatrixLabTest` 固化“classpath/jar/pattern/encoding”的断言，再用断点观察 Resource 如何被解析、以及读取流的边界条件。

    需要下探源码时，可以从 `org.springframework.core.io.DefaultResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Resources）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 关键分支矩阵（Branch Decision Matrix）](05-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `SpringCoreResourcesBranchMatrixLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：Resource 解析（location/pattern）→ 找到 URL/stream → 读取（encoding）→ jar 与 filesystem 的差异来自底层 URL/URLConnection。需要下探源码时，可以从 `org.springframework.core.io.DefaultResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver` 这些入口切入。


## 运行入口（先运行）

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

- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

Resource 解析（location/pattern）→ 找到 URL/stream → 读取（encoding）→ jar 与 filesystem 的差异来自底层 URL/URLConnection。

下一章见：[第 140 章：04：关键分支矩阵（Branch Decision Matrix）](05-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`SpringCoreResourcesBranchMatrixLabTest`
- Lab：`SpringCoreResourcesMechanicsLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](05-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

