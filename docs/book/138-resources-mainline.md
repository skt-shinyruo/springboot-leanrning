# 第 138 章：Resources 主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Resources 主线
    - 怎么使用：本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
    - 原理：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
    - 源码入口：N/A（本页为索引/工具页）
    - 推荐 Lab：N/A
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 137 章：自测题（Spring Core Events）](../events/spring-core-events/appendix/137-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 139 章：主线时间线：Spring Resources](../resources/spring-core-resources/part-00-guide/139-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

很多“本地没问题、线上就翻车”的问题，最后都会回到资源读取：

- IDE 里 `classpath:` 能读到，打成 jar 后突然读不到；
- `resource.exists()` 看起来是 true，但 `getFile()` 却直接炸；
- pattern 扫描在不同运行方式下漏掉/多出一堆资源。

这一章的目标是把资源当成一个统一抽象来理解：**定位 → 解析 → 校验 → 读取**，并且知道 jar 与 filesystem 的差异到底发生在哪里。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Resources 主线 —— 本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
- 回到主线：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Resources 主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 建议先带着问题顺读一遍正文，再按证据链回到源码/断点验证。
<!-- BOOKLIKE-V2:INTRO:END -->

## 你将学到什么（本章目标）

读完本章，你应该能做到：

1. 用一句话解释 `Resource` 抽象解决了什么问题（以及它没解决什么）
2. 知道哪些操作在 jar 场景下是“天然不可靠”的（典型：`getFile()`）
3. 能正确使用 `classpath:` / `classpath*:` 与 pattern scanning，并对其边界有预期
4. 遇到资源问题时，知道先看哪些可观察信号（description/URL/协议/是否可重复读取）

---

## 主线（按时间线顺读）

把资源读取当成四步走，任何一步错了都会表现为“读不到/读错/乱码/路径不一致”：

1. **定位**：你想找的到底是什么？（路径前缀、相对基准、pattern）
2. **解析**：把“字符串路径”解析成 `Resource`（file/classpath/url/jar 等具体实现）
3. **校验**：`exists()`/`isReadable()` 只能告诉你“可能读”，不能保证“能 getFile()”
4. **读取**：优先按流读取（`getInputStream()`），并显式处理编码与关闭

在此基础上，再理解两个高频扩展能力：

- **pattern 扫描**：`PathMatchingResourcePatternResolver` 支持 `classpath*:` 等语法
- **jar vs filesystem**：能 `getInputStream()` 不代表能 `getFile()`；协议与 classloader 决定了行为差异

---

## 读书式的“证据链”：你该观察什么

资源排障时，最有用的不是“再改一次路径”，而是先把事实看清楚：

- 这个 `Resource` 的 `description`/`URL` 是什么？协议是 `file:` 还是 `jar:`？
- 你是在 IDE/单测/打包 jar/容器里运行？运行方式决定 classpath 形态
- 你到底需要的是“流”还是“文件路径”？如果只是读取内容，优先走流

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/resources/spring-core-resources/part-00-guide/140-02-breakpoint-map.md`](../resources/spring-core-resources/part-00-guide/140-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/resources/spring-core-resources/part-00-guide/140-04-branch-decision-matrix.md`](../resources/spring-core-resources/part-00-guide/140-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/resources/spring-core-resources/appendix/147-90-common-pitfalls.md`](../resources/spring-core-resources/appendix/147-90-common-pitfalls.md)
- 自检清单：[`docs/resources/spring-core-resources/appendix/148-99-self-check.md`](../resources/spring-core-resources/appendix/148-99-self-check.md)

- 模块目录页：[`docs/resources/spring-core-resources/README.md`](../resources/spring-core-resources/README.md)
- 模块主线时间线（含可跑入口）：[`docs/resources/spring-core-resources/part-00-guide/03-mainline-timeline.md`](../resources/spring-core-resources/part-00-guide/139-03-mainline-timeline.md)

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesLabTest test`（`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`（`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix）：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`（`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part00_guide/SpringCoreResourcesExerciseTest.java`

---

## 下一章怎么接

资源与配置经常同时出现：配置文件是否参与、Bean 是否注册，很多时候最后都要回到 “Profile 到底怎么激活”。

- 下一章：[第 149 章：Profiles 主线](149-profiles-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Resources 主线」的生效时机/顺序/边界；断点/入口：N；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Resources 主线」的生效时机/顺序/边界；断点/入口：A（本页为索引；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Resources 主线」的生效时机/顺序/边界；断点/入口：工具页）；断言：你能解释“为什么此处生效/为什么此处不生效”。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
