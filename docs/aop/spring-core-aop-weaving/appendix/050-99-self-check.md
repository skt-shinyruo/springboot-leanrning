# 第 50 章：99. 自测题：你是否真的理解了 weaving？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：自测题：你是否真的理解了 weaving？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。
    - 原理：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。
    - 源码入口：`org.springframework.context.weaving.AspectJWeavingEnabler` / `org.springframework.instrument.classloading.LoadTimeWeaver` / `org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter`
    - 推荐 Lab：`AspectjCtwLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 49 章：90. 常见坑清单（LTW/CTW）](049-90-common-pitfalls.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 51 章：事务主线（Tx）](../../../book/051-tx-mainline.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读
<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「自测题：你是否真的理解了 weaving？」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjCtwLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- LTW/CTW：建议直接跑模块 `mvn -q -pl :spring-core-aop-weaving test`（让 Surefire 自动区分 execution）；或分别：
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`
  - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjCtwBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/044-02-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/044-04-branch-decision-matrix.md)

- 本章主题：**99. 自测题：你是否真的理解了 weaving？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

## 机制主线


## 心智模型

- 你能用一句话解释：proxy AOP 与 weaving 的根本差异是什么？
- 你能说清：为什么 weaving 不依赖“走代理的 call path”？

!!! example "本章配套实验（先跑再读）"

    - （未提取到实验入口）

## CTW

- 为什么 CTW 不需要 `-javaagent`？
- CTW 的主要代价是什么？你会如何控制织入范围？

## join point / pointcut


## 排障

- 当你遇到“拦截没发生”时，你的分流排查顺序是什么？（至少 4 步）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`
- 建议命令：`mvn -pl :spring-core-aop-weaving test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

> 验证入口（可跑）：`AspectjLtwLabTest` / `AspectjCtwLabTest`

- LTW 生效的三个前提是什么？
- 为什么把 `aop.xml` 放在 `src/test/resources` 是一个“学习友好”的选择？它的代价是什么？

- `call` 与 `execution` 的差异是什么？你会如何在测试里验证它们触发的 kind？
- `withincode` 与 `cflow` 各自解决什么问题？为什么说它们“很强但也更难维护”？

## 常见坑与边界

### 坑点 1：把 proxy AOP 的排障套路直接套到 weaving 上，导致方向跑偏

- Symptom：你用“是否走代理/是否注入到 Spring bean”排查 weaving，结果越查越乱
- Root Cause：weaving 不依赖 Spring 容器与 call path，它依赖的是：
  - LTW：JVM agent + aop.xml + include 范围
  - CTW：构建期织入产物
- Verification：
  - LTW 有 agent：`AspectjLtwLabTest#ltw_testJvmIsStartedWithJavaAgent`
  - CTW 无 agent：`AspectjCtwLabTest#ctw_testJvmIsNotStartedWithAspectjJavaAgent`
- Fix：先分流你处于 LTW 还是 CTW，再按对应的“生效前提”排障；不要把 weaving 当成 proxy 世界的问题

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`AspectjCtwLabTest` / `AspectjWeavingBookMatrixLabTest` / `AspectjLtwBranchMatrixLabTest` / `AspectjCtwBranchMatrixLabTest` / `AspectjLtwLabTest`

上一章：[常见坑](049-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
