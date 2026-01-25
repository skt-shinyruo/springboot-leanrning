# 第 140 章：02：断点地图（Resources Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Resources Debugger Pack）
    - 怎么使用：先跑 `SpringCoreResourcesBranchMatrixLabTest` 固化“classpath/jar/pattern/encoding”的断言，再用断点观察 Resource 如何被解析、以及读取流的边界条件。
    - 原理：Resource 解析（location/pattern）→ 找到 URL/stream → 读取（encoding）→ jar 与 filesystem 的差异来自底层 URL/URLConnection。
    - 源码入口：`org.springframework.core.io.DefaultResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 140 章：00 - Deep Dive Guide（spring-core-resources）](140-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 140 章：04：关键分支矩阵（Branch Decision Matrix）](140-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

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

- Matrix：`SpringCoreResourcesBranchMatrixLabTest`
- Lab：`SpringCoreResourcesMechanicsLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](140-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](140-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

