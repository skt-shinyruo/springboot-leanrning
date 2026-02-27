# 99 自检：Spring Resources
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘出口）"

    - 主线入口：`SpringCoreResourcesBookMatrixLabTest`
    - 分支入口：`SpringCoreResourcesBranchMatrixLabTest`
    - 推荐先跑：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（建议反复对照）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](../part-00-guide/04-breakpoint-map.md)
- [关键分支矩阵](../part-00-guide/05-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](01-common-pitfalls.md)

## 自检题（每题都能落到 tests）

1. `getResource(...)` 返回的是“句柄”还是“存在的资源”？如何用断言证明“拿到句柄≠资源存在”？
   - 证据入口：`SpringCoreResourcesMechanicsLabTest#getResourceReturnsAHandle_evenIfTheResourceDoesNotExist`
2. classpath 读取的最小闭环是什么？如何验证“确实读到了 classpath 里的那份内容”？
   - 证据入口：`SpringCoreResourcesLabTest#readsClasspathResourceContent`
3. `classpath:data/x` 与 `classpath:/data/x` 的差异是什么？如何把“带不带 leading slash 都能工作”固定为回归？
   - 证据入口：`SpringCoreResourcesLabTest#supportsLeadingSlashInClasspathLocation`
4. `classpath:` 与 `classpath*:` 的差异是什么？如何用一个 pattern 扫描证明它会返回多个资源？
   - 证据入口：`SpringCoreResourcesLabTest#loadsMultipleResourcesWithPattern` / `SpringCoreResourcesMechanicsLabTest#classpathStarPatternLoadsResourcesFromClasspath`
5. pattern 扫描结果如何“可解释”？如何验证返回集合包含期望的文件名？
   - 证据入口：`SpringCoreResourcesLabTest#patternResultsContainExpectedFilenames`
6. 缺失资源应当如何失败？如何把“缺失即失败”的语义写成断言（避免吞异常）？
   - 证据入口：`SpringCoreResourcesLabTest#missingResourceCausesUncheckedIOException`
7. `Resource` 抽象是否只适用于 classpath？如何证明 file URI 也能被同一套读取逻辑处理？
   - 证据入口：`SpringCoreResourcesLabTest#fileResourcesCanAlsoBeRead_viaResourceAbstraction`
8. 为什么排障时应该优先输出 `Resource#getDescription()`？如何用断言证明 description 能帮助确认“到底拿到了谁”？
   - 证据入口：`SpringCoreResourcesMechanicsLabTest#resourceDescriptionsHelpWithDebugging`
9. 文本读取时编码为什么重要？如何用 bytes→string 的方式显式固定 UTF-8？
   - 证据入口：`SpringCoreResourcesMechanicsLabTest#classpathResourceCanBeReadAsBytes`

## 退出条件（完成标准）

- 能用三段式分流排障：定位（location/pattern）→ 存在性（exists/readable）→ 读取（InputStream/encoding）。
- 能用 description 把“我以为读的是 A”变成“我确定读的是 A”（可回归证据）。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

上一章：[90-common-pitfalls](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
