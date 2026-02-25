# 04. 断点地图（Resources Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Resources Debugger Pack）
    - 怎么使用：先跑 `SpringCoreResourcesBranchMatrixLabTest` 固化“classpath/jar/pattern/encoding”的断言，再用断点观察 Resource 如何被解析、以及读取流的边界条件。
    - 原理：Resource 解析（location/pattern）→ 找到 URL/stream → 读取（encoding）→ jar 与 filesystem 的差异来自底层 URL/URLConnection。
    - 源码入口：`org.springframework.core.io.DefaultResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Resources）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 关键分支矩阵（Branch Decision Matrix）](05-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04. 断点地图（Resources Debugger Pack）**
- 建议入口：优先运行 `SpringCoreResourcesBranchMatrixLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：Resource 解析（location/pattern）→ 找到 URL/stream → 读取（encoding）→ jar 与 filesystem 的差异来自底层 URL/URLConnection。
- 源码入口：`org.springframework.core.io.DefaultResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`



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

- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

- 小结：Resource 解析（location/pattern）→ 找到 URL/stream → 读取（encoding）→ jar 与 filesystem 的差异来自底层 URL/URLConnection。
- 下一章：[第 140 章：04：关键分支矩阵（Branch Decision Matrix）](05-branch-decision-matrix.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`SpringCoreResourcesBranchMatrixLabTest`
- Lab：`SpringCoreResourcesMechanicsLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](05-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

