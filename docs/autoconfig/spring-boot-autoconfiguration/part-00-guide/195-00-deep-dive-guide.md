# 第 195 章：00. 深挖导读：把“自动配置导入 + 条件决策”落到源码与断点
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：深挖导读：把“自动配置导入 + 条件决策”落到源码与断点
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootAutoConfigurationLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 194 章：03：主线时间线](194-03-mainline-timeline.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 195 章：01：AutoConfiguration 调用链](195-01-autoconfiguration-import-call-chain.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：深挖导读：把“自动配置导入 + 条件决策”落到源码与断点 —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

- 本章主题：**00. 深挖导读：把“自动配置导入 + 条件决策”落到源码与断点**
- 目标：建立两个“排障先问”的问题：
  1) auto-config 有没有被导入？（imports/selector）
  2) 导入后为什么被跳过？（condition/backoff）

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootAutoConfigurationLabTest`

## 深挖时最容易走偏的点

1. **只看配置文件，不看 imports**
   - 现象：你以为配置没生效，但其实 auto-config 根本没被导入（或被 exclude）。
2. **只看 `@ConditionalOnProperty`，忽略 `@ConditionalOnMissingBean`**
   - 现象：你以为 property 控制了开关，但实际上是用户自定义 bean 触发了 backoff。
3. **把“顺序问题”当成“条件问题”**
   - 现象：某个 bean 的最终形态不对（被谁包了/没被谁包），但你只在看某一个条件注解。

## 推荐抓手（从证据链回到源码）

- **证据链入口：** `BootAutoConfigurationLabTest`
- **导入链入口：** `AutoConfigurationImportSelector#selectImports`
- **条件决策入口：** `ConditionEvaluator#shouldSkip`
- **Bean 条件入口：** `OnBeanCondition#getMatchOutcome`

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「深挖导读：把“自动配置导入 + 条件决策”落到源码与断点」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「深挖导读：把“自动配置导入 + 条件决策”落到源码与断点」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootAutoConfigurationLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAutoConfigurationLabTest`

上一章：[主线时间线](194-03-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）](195-01-autoconfiguration-import-call-chain.md)

<!-- BOOKIFY:END -->
