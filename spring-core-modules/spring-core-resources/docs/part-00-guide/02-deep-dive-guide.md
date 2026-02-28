# 02. 深挖指南（Spring Core Resources）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕深挖指南（Spring Core Resources）展开，主线可以概括为：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。

    先运行 `SpringCoreResourcesLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。

    需要下探源码时，可以从 `org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：Spring Resources](01-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. `Resource` 抽象：为什么 Spring 不直接使用 `File`？](../part-01-resource-abstraction/01-resource-abstraction.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

## 机制主线

Resources 的“深挖主线”是把“路径字符串”变成可解释的资源句柄：

1. **Resource 抽象**：同一个 API 读取 classpath/file/url 等不同来源
2. **定位语义**：`classpath:` / `classpath*:` / pattern 的差异
3. **存在性与可读性**：`getResource()` 只是拿到 handle，不等于存在
4. **读取与编码**：bytes → text 需要显式指定 charset（避免默认编码坑）

### 1) 时间线：一次资源读取从 location 到 content

1. 给出一个 location（例如 `classpath:data/hello.txt` 或 `classpath*:data/*.txt`）
2. `ResourceLoader/Resolver` 解析 location → 得到 `Resource`（或多个）
3. 判断存在性（可选）：`resource.exists()`
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


建议断点（先把“定位失败”与“读取失败”分开）：

- 定位阶段：
  - `org.springframework.core.io.support.PathMatchingResourcePatternResolver#getResources`
  - `org.springframework.core.io.support.PathMatchingResourcePatternResolver#getResource`
- 读取阶段：
  - `Resource#getInputStream`
- 排障时优先输出：
  - `Resource#getDescription`（确认究竟拿到了哪个资源句柄）

## 最小可运行实验（Lab）

- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

> 验证入口（可跑）：
> - `SpringCoreResourcesLabTest`
> - `SpringCoreResourcesMechanicsLabTest`

配套验证入口：
- Labs/Exercises：见 `src/test/java/com/learning/springboot/springcoreresources/**`

## 常见坑与边界

建议阅读顺序：
1. Resource 抽象是什么、为何存在（Part 01）
2. classpath 定位规则（含 `classpath:`、`classpath*:`、pattern）与“以为找到了，其实没找到”（Part 01）
3. 再进入编码、Jar/文件系统差异与常见坑（Part 01 + Appendix）

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01-resource-abstraction](../part-01-resource-abstraction/01-resource-abstraction.md)

<!-- BOOKIFY:END -->
