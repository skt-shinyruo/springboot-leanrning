# 04. 断点地图（Resources）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（Resources）展开，主线可以概括为：Resource 解析（location/pattern）→ 找到 URL/stream → 读取（encoding）→ jar 与 filesystem 的差异来自底层 URL/URLConnection。

    先跑 `SpringCoreResourcesBranchMatrixLabTest` 固化“classpath/jar/pattern/encoding”的断言，再用断点观察 Resource 如何被解析、以及读取流的边界条件。

    需要下探源码时，可以从 `org.springframework.core.io.DefaultResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Resources）](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `SpringCoreResourcesBranchMatrixLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：Resource 解析（location/pattern）→ 找到 URL/stream → 读取（encoding）→ jar 与 filesystem 的差异来自底层 URL/URLConnection。需要下探源码时，可以从 `org.springframework.core.io.DefaultResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver` 这些入口切入。


## 运行入口（先运行）

- Book Matrix：`SpringCoreResourcesBookMatrixLabTest`
- Branch Matrix：`SpringCoreResourcesBranchMatrixLabTest`

## 断点（解析与读取）

- `org.springframework.core.io.DefaultResourceLoader#getResource`
- `org.springframework.core.io.support.PathMatchingResourcePatternResolver#getResources`
- `org.springframework.core.io.ClassPathResource#getInputStream`

## 观察点

- `resource.getDescription()` / `resource.getURL()`
- `resource.exists()` / `resource.isReadable()`
- “解析出的 resources 数量”（pattern 分支证据）

## 排障入口（Playbook）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

Resource 解析（location/pattern）→ 找到 URL/stream → 读取（encoding）→ jar 与 filesystem 的差异来自底层 URL/URLConnection。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`SpringCoreResourcesBranchMatrixLabTest`
- Lab：`SpringCoreResourcesMechanicsLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

