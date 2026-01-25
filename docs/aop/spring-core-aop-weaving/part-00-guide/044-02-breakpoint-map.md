# 第 44 章：02：断点地图（AspectJ Weaving Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（AspectJ Weaving Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。
    - 原理：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。
    - 源码入口：`org.springframework.context.weaving.AspectJWeavingEnabler` / `org.springframework.instrument.classloading.LoadTimeWeaver` / `org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter`
    - 推荐 Lab：`AspectjLtwLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 44 章：00 - Deep Dive Guide（spring-core-aop-weaving）](044-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 44 章：04：关键分支矩阵（Branch Decision Matrix）](044-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（AspectJ Weaving Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。
- 回到主线：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

- 本模块最重要的“分支”不是某个 if，而是 **运行方式**：LTW（带 agent） vs CTW（不带 agent）。
- 推荐证据链：
  - LTW：运行时 inputArgs 包含 `-javaagent:.../aspectjweaver.jar`，并且 advice 记录被写入日志
  - CTW：inputArgs 不包含 agent，但 advice 依然生效（因为类已被织入）

## 运行入口（建议先跑）

- Book Matrix（手动运行）：`AspectjWeavingBookMatrixLabTest`
- Branch Matrix（LTW）：`AspectjLtwBranchMatrixLabTest`
- Branch Matrix（CTW）：`AspectjCtwBranchMatrixLabTest`

推荐命令（建议直接跑模块测试，让 surefire 的 includes 分流 LTW/CTW）：

- `mvn -q -pl :spring-core-aop-weaving test`

## 断点（最有效：直接下在 advice 上）

- LTW advice：`com.learning.springboot.springcoreaopweaving.part02_ltw_fundamentals.LtwWeavingAspect`
- CTW advice：`com.learning.springboot.springcoreaopweaving.part03_ctw_fundamentals.CtwWeavingAspect`

## Watchpoints（建议）

- `ManagementFactory.getRuntimeMXBean().getInputArguments()`：是否带 agent（LTW/CTW 决策证据）
- invocation log（本模块的 InvocationLog）：记录哪些 join point 被拦截

## 排障入口（Playbook）

- 常见坑：[`../appendix/049-90-common-pitfalls.md`](../appendix/049-90-common-pitfalls.md)
- 自检：[`../appendix/050-99-self-check.md`](../appendix/050-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`AspectjLtwLabTest` / `AspectjWeavingBookMatrixLabTest` / `AspectjLtwBranchMatrixLabTest` / `AspectjCtwBranchMatrixLabTest`

上一章：[Join Point 菜谱](../part-04-join-points/048-04-join-point-cookbook.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[044-04-branch-decision-matrix.md](044-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
