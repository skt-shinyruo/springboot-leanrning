# 02. 99 - Self Check（springboot-logging）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（springboot-logging）
    - 怎么使用：先跑 Book/Branch Matrix，再用本页自检问题检查是否真正理解“level/category/effective level”。
    - 原理：自检的目标是：你能从现象（日志出现/不出现）回到机制（effective level），并能用断点/断言验证。
    - 源码入口：（logback）`Logger#isDebugEnabled`
    - 推荐 Lab：`BootLoggingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 90 - Common Pitfalls（springboot-logging）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02. 99 - Self Check（springboot-logging）**
- 建议入口：优先运行 `BootLoggingLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：自检的目标是：你能从现象（日志出现/不出现）回到机制（effective level），并能用断点/断言验证。
- 源码入口：（logback）`Logger#isDebugEnabled`



## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/04-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/05-branch-decision-matrix.md)

## 自检问题

1. 什么是 logger category？你如何用配置命中它？
2. 什么是 effective level？你会在哪里下断点验证？
3. 为什么“日志要可断言”？你会用什么手段固化？

## 小结与下一章

- 小结：自检的目标是：你能从现象（日志出现/不出现）回到机制（effective level），并能用断点/断言验证。
- 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[appendix/90-common-pitfalls.md](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
