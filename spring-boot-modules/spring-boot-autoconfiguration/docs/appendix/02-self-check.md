# 02. 99 - Self Check（springboot-autoconfiguration）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（springboot-autoconfiguration）
    - 怎么使用：先跑 Book/Branch Matrix，再用本页问题自检；每个问题都能落到一个断点入口或一个 Lab 断言。
    - 原理：自检的目标是：你能从“现象”回到 imports/condition/backoff/顺序，并能用证据链证明。
    - 源码入口：`ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome`
    - 推荐 Lab：`BootAutoConfigurationLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 90 - Common Pitfalls（springboot-autoconfiguration）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02. 99 - Self Check（springboot-autoconfiguration）**
- 建议入口：优先运行 `BootAutoConfigurationLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：自检的目标是：你能从“现象”回到 imports/condition/backoff/顺序，并能用证据链证明。
- 源码入口：`ConditionEvaluator#shouldSkip` / `OnBeanCondition#getMatchOutcome`



## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/04-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/05-branch-decision-matrix.md)

## 自检问题（建议写出答案）

1. 当一个 auto-config “完全没生效”时，你第一步会去哪里下断点？为什么？
2. `@ConditionalOnMissingBean` 与 “用户自定义 bean 覆盖默认”之间是什么关系？你能用哪个 Lab 证明？
3. 当容器里有两个同类型 bean 时，你如何解释“最终注入对象是谁”？你会怎么验证？

## 小结与下一章

- 小结：自检的目标是：你能从“现象”回到 imports/condition/backoff/顺序，并能用证据链证明。
- 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAutoConfigurationLabTest`

上一章：[appendix/90-common-pitfalls.md](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
