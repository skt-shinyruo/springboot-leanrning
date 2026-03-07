# 01. 主线时间线：AOP Weaving（织入：LTW/CTW）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕主线时间线：AOP Weaving（织入：LTW/CTW）展开，主线可以概括为：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。

    先运行 `AspectjLtwLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。

    需要下探源码时，可以从 `org.springframework.context.weaving.AspectJWeavingEnabler` / `org.springframework.instrument.classloading.LoadTimeWeaver` / `org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 42 章：织入主线（LTW/CTW）](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 深挖指南：把 weaving 的“结论 → 实验 → 排障路径”跑通](guide-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：当“代理”无法覆盖需求时，织入（weaving）如何在类加载期/编译期把横切逻辑写进字节码。
    - 读完应当能复述：**代理 vs 织入 → 选择 LTW/CTW → 选 Join Point → 验证生效** 这一条主线。
    - 推荐顺序：先读《深挖导读》→ 本章 → 依次顺读 4 章 → 附录排坑。

!!! example "先运行的 Lab（把时间线变成证据）"

    - Lab：`AspectjLtwLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：AOP Weaving（织入：LTW/CTW） —— 先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。
- 回到主线：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

本章是「第 43 章：主线时间线：AOP Weaving（织入：LTW/CTW）」的路线图：先给出主线顺序与关键分支，再把每一段落到可运行入口。
先运行 `AspectjLtwLabTest` 作为主线证据，再回到正文理解“为什么章节按这个顺序组织”。

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：AOP Weaving（织入：LTW/CTW）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- 织入更贴近“字节码层”：它绕开了部分代理的边界（例如 final/构造期等），但引入了工程化复杂度（agent、编译、类加载）。
- 在大多数业务场景，代理足够；只有在需要更强覆盖面时才考虑 weaving。

## 主线时间线（建议顺读）

1. 先明确：代理解决什么、织入解决什么（以及成本）
   - 阅读：[01. 代理 vs 织入](mental-model-proxy-vs-weaving.md)
2. 选择 LTW（Load-Time Weaving）：类加载期织入怎么落地
   - 阅读：[02. LTW 基础](ltw-basics.md)
3. 选择 CTW（Compile-Time Weaving）：编译期织入怎么落地
   - 阅读：[03. CTW 基础](ctw-basics.md)
4. 把 Join Point 这本“菜谱”掌握住：知道能织到哪里
   - 阅读：[04. Join Point 菜谱](join-points-join-point-cookbook.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](appendix-common-pitfalls.md)
- 自检：[99-self-check.md](appendix-self-check.md)

## 证据链（如何验证理解成立）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：AOP Weaving（织入：LTW/CTW）」的生效时机/顺序/边界；断点/入口：`org.springframework.context.weaving.AspectJWeavingEnabler`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：AOP Weaving（织入：LTW/CTW）」的生效时机/顺序/边界；断点/入口：`org.springframework.instrument.classloading.LoadTimeWeaver`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「主线时间线：AOP Weaving（织入：LTW/CTW）」的生效时机/顺序/边界；断点/入口：`org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``AspectjLtwLabTest`` 后，把上述观察点逐条对照，写出 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
