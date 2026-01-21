# 第 140 章：深挖指南（Spring Core Resources）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：深挖指南（Spring Core Resources）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
    - 原理：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
    - 源码入口：`org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 139 章：主线时间线：Spring Resources](139-03-mainline-timeline.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 141 章：01. `Resource` 抽象：为什么 Spring 不让你直接用 `File`？](../part-01-resource-abstraction/141-01-resource-abstraction.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**深挖指南（Spring Core Resources）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

## 机制主线

Resources 的“深挖主线”是把“路径字符串”变成可解释的资源句柄：

1. **Resource 抽象**：同一个 API 读取 classpath/file/url 等不同来源
2. **定位语义**：`classpath:` / `classpath*:` / pattern 的差异
3. **存在性与可读性**：`getResource()` 只是拿到 handle，不等于存在
4. **读取与编码**：bytes → text 需要你明确 charset（避免默认编码坑）

### 1) 时间线：一次资源读取从 location 到 content

1. 你给出一个 location（例如 `classpath:data/hello.txt` 或 `classpath*:data/*.txt`）
2. `ResourceLoader/Resolver` 解析 location → 得到 `Resource`（或多个）
3. 你判断存在性（可选）：`resource.exists()`
4. 读取内容：`resource.getInputStream()` → bytes → text（指定 charset）
5. 输出 debug 证据：`resource.getDescription()`（排障时非常有用）

### 2) 关键参与者

- `org.springframework.core.io.Resource`：统一资源抽象（file/classpath/url）
- `PathMatchingResourcePatternResolver`：支持 pattern 与 `classpath*:` 扫描
- `Resource#getDescription()`：排障辅助信息（定位到底读到了哪个资源）

### 3) 本模块的关键分支（2–5 条，默认可回归）

1. **handle vs exists：getResource 会返回句柄，但资源可能不存在**
   - 验证：`SpringCoreResourcesMechanicsLabTest#getResourceReturnsAHandle_evenIfTheResourceDoesNotExist`
2. **classpath* + pattern：可以一次加载多个匹配资源**
   - 验证：`SpringCoreResourcesLabTest#loadsMultipleResourcesWithPattern` / `SpringCoreResourcesMechanicsLabTest#classpathStarPatternLoadsResourcesFromClasspath`
3. **路径细节：classpath location 支持前导 `/`**
   - 验证：`SpringCoreResourcesLabTest#supportsLeadingSlashInClasspathLocation`
4. **读取与编码：以 bytes 读取后显式用 UTF-8 解码**
   - 验证：`SpringCoreResourcesMechanicsLabTest#classpathResourceCanBeReadAsBytes`
5. **描述信息：description 是排障第一现场之一**
   - 验证：`SpringCoreResourcesMechanicsLabTest#resourceDescriptionsHelpWithDebugging`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。
  
建议断点（先把“定位失败”与“读取失败”分开）：

- 定位阶段：
  - `org.springframework.core.io.support.PathMatchingResourcePatternResolver#getResources`
  - `org.springframework.core.io.support.PathMatchingResourcePatternResolver#getResource`
- 读取阶段：
  - `Resource#getInputStream`
- 排障时优先输出：
  - `Resource#getDescription`（确认你到底拿到了哪个资源句柄）

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

> 验证入口（可跑）：
> - `SpringCoreResourcesLabTest`
> - `SpringCoreResourcesMechanicsLabTest`

配套验证入口：
- Labs/Exercises：见 `src/test/java/com/learning/springboot/springcoreresources/**`

## 常见坑与边界

建议阅读顺序：
1. Resource 抽象是什么、为何存在（Part 01）
2. classpath 定位规则（含 `classpath:`、`classpath*:`、pattern）与“你以为你找到，其实没找到”（Part 01）
3. 再进入编码、Jar/文件系统差异与常见坑（Part 01 + Appendix）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01-resource-abstraction](../part-01-resource-abstraction/141-01-resource-abstraction.md)

<!-- BOOKIFY:END -->
