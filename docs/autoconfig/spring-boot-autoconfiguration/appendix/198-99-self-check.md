# 第 198 章：99 - Self Check（springboot-autoconfiguration）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（springboot-autoconfiguration）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootAutoConfigurationLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 197 章：90 - Common Pitfalls（springboot-autoconfiguration）](197-90-common-pitfalls.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[Book TOC](../../../book/index.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Self Check（springboot-autoconfiguration） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Self Check（springboot-autoconfiguration）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/195-02-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/195-04-branch-decision-matrix.md)

## 自检问题（建议写出答案）

1. 当一个 auto-config “完全没生效”时，你第一步会去哪里下断点？为什么？
2. `@ConditionalOnMissingBean` 与 “用户自定义 bean 覆盖默认”之间是什么关系？你能用哪个 Lab 证明？
3. 当容器里有两个同类型 bean 时，你如何解释“最终注入对象是谁”？你会怎么验证？

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Self Check（springboot-autoconfiguration）」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Self Check（springboot-autoconfiguration）」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootAutoConfigurationLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAutoConfigurationLabTest` / `BootAutoConfigurationBookMatrixLabTest` / `BootAutoConfigurationBranchMatrixLabTest`

上一章：[常见坑](197-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
