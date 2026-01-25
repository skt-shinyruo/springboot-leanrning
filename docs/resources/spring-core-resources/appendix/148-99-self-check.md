# 第 148 章：自测题（Spring Core Resources）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：自测题（Spring Core Resources）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
    - 原理：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
    - 源码入口：`org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 147 章：90. 常见坑清单（建议反复对照）](147-90-common-pitfalls.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 149 章：Profiles 主线](../../../book/149-profiles-mainline.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/140-02-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/140-04-branch-decision-matrix.md)

- 本章主题：**自测题（Spring Core Resources）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

## 机制主线

这一章用“最小实验 + 可断言证据链”复盘三段式分流：

1. 定位：location → Resource（classpath/classpath*）
2. 存在性：handle vs exists（不要把句柄当存在）
3. 读取：InputStream/encoding/description（把问题变得可观察）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

> 验证入口（可跑）：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

1. `Resource` 与 `ResourceLoader` 的职责分别是什么？
2. `classpath:` 与 `classpath*:` 的差异是什么？你会如何最小化复现？
3. 为什么同一个路径在 IDE 运行与打成 jar 后的行为可能不同？
4. 读取文本资源时，最容易踩的编码坑是什么？如何验证？

## 常见坑与边界

### 坑点 1：排障只看路径字符串，不看 `Resource#getDescription()`，导致定位到错误的资源

- Symptom：你以为读的是某个文件，实际读到的是 classpath 里另一个同名资源（或 jar 内资源），排查越走越偏
- Root Cause：location 字符串不等于最终解析到的资源；description 才是“你到底拿到了谁”的证据
- Verification：`SpringCoreResourcesMechanicsLabTest#resourceDescriptionsHelpWithDebugging`
- Fix：排障时优先输出 description，并结合 classpath* 的扫描结果做确认（避免“我以为”的陷阱）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

上一章：[90-common-pitfalls](147-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
