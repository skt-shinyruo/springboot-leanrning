# 02. 深挖导读：把“自动配置导入 + 条件决策”落到源码与断点
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：深挖导读：把“自动配置导入 + 条件决策”落到源码与断点
    - 怎么使用：先跑 `BootAutoConfigurationLabTest` 看见 3 个分支（默认/装饰/用户覆盖），再按调用链定位到 imports 与 condition 的关键入口。
    - 原理：自动配置不是“运行期魔法”，而是启动期的“条件化注册”：imports 决定候选集合，Condition 决定是否注册，backoff 决定是否让位。
    - 源码入口：`AutoConfigurationImportSelector#selectImports` / `ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome`
    - 推荐 Lab：`BootAutoConfigurationLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：springboot-autoconfiguration](01-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）](03-autoconfiguration-import-call-chain.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02. 深挖导读：把“自动配置导入 + 条件决策”落到源码与断点**
- 目标：建立两个“排障先问”的问题：
  1) auto-config 有没有被导入？（imports/selector）
  2) 导入后为什么被跳过？（condition/backoff）

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootAutoConfigurationLabTest`

## 深挖时最容易走偏的点

1. **只看配置文件，不看 imports**
   - 现象：以为配置没生效，但其实 auto-config 根本没被导入（或被 exclude）。
2. **只看 `@ConditionalOnProperty`，忽略 `@ConditionalOnMissingBean`**
   - 现象：以为 property 控制了开关，但实际上是用户自定义 bean 触发了 backoff。
3. **把“顺序问题”当成“条件问题”**
   - 现象：某个 bean 的最终形态不对（被谁包了/没被谁包），但只在看某一个条件注解。

## 推荐抓手（从证据链回到源码）

- **证据链入口：** `BootAutoConfigurationLabTest`
- **导入链入口：** `AutoConfigurationImportSelector#selectImports`
- **条件决策入口：** `ConditionEvaluator#shouldSkip`
- **Bean 条件入口：** `OnBeanCondition#getMatchOutcome`

## 小结与下一章

- 小结：自动配置不是“运行期魔法”，而是启动期的“条件化注册”：imports 决定候选集合，Condition 决定是否注册，backoff 决定是否让位。
- 下一章：[第 195 章：01：AutoConfiguration 调用链](03-autoconfiguration-import-call-chain.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAutoConfigurationLabTest`

上一章：[part-00-guide/03-mainline-timeline.md](01-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/01-autoconfiguration-call-chain.md](03-autoconfiguration-import-call-chain.md)

<!-- BOOKIFY:END -->
