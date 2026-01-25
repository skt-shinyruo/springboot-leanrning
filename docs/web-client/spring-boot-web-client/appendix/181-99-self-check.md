# 第 181 章：99 - Self Check（springboot-web-client）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（springboot-web-client）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootWebClientRestClientLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 180 章：90：常见坑清单（Web Client）](180-90-common-pitfalls.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 182 章：Testing 主线](../../../book/182-testing-mainline.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读
<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Self Check（springboot-web-client）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientRestClientLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/174-02-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/174-04-branch-decision-matrix.md)

- 本章主题：**99 - Self Check（springboot-web-client）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest` / `BootWebClientWebClientFilterOrderLabTest`

## 机制主线


## 自测题
1. 超时设置应该放在客户端哪一层？（连接/读写/整体调用）
2. 重试策略如何与幂等性、熔断/限流协同？
3. 为什么 MockWebServer 能让客户端测试更稳定？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章未显式引用 LabTest，先注入模块默认 LabTest 作为“合规兜底入口”（后续可逐章细化）。
- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest` / `BootWebClientWebClientFilterOrderLabTest`
- 建议命令：`mvn -pl :spring-boot-web-client test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 对应 Exercise（可运行）

- `BootWebClientExerciseTest`

## 常见坑与边界

### 坑点 1：用真实网络做客户端测试，导致测试不稳定且不可重复

- Symptom：在本地能跑、CI 偶发失败；或者外部服务波动导致你的单元测试“背锅”
- Root Cause：网络与下游服务本身是非确定性的；测试缺少可控证据链
- Verification：本模块所有 timeout/retry/error mapping 都基于 MockWebServer 可复现：
  - `BootWebClientRestClientLabTest#restClientReadTimeoutFailsFast`
  - `BootWebClientWebClientLabTest#webClientRetriesOn5xxAndEventuallySucceeds`
- Fix：优先用 MockWebServer 固定下游行为，把“失败模式”做成可回归实验

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientBookMatrixLabTest` / `BootWebClientBranchMatrixLabTest` / `BootWebClientWebClientLabTest` / `BootWebClientWebClientFilterOrderLabTest`
- Exercise：`BootWebClientExerciseTest`

上一章：[常见坑](180-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
