# 第 126 章：Events 主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Events 主线
    - 怎么使用：本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
    - 原理：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
    - 源码入口：N/A（本页为索引/工具页）
    - 推荐 Lab：N/A
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 125 章：99 - Self Check（spring-boot-async-scheduling）](../async-scheduling/spring-boot-async-scheduling/appendix/125-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 127 章：主线时间线：Spring Events](../events/spring-core-events/part-00-guide/127-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

这一章解决的问题是：**为什么发布一个事件就能触发多个监听器、异常怎么传播、事务提交后再发事件该怎么写**。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Events 主线 —— 本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
- 回到主线：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Events 主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 建议先带着问题顺读一遍正文，再按证据链回到源码/断点验证。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

1. 发布：`ApplicationEventPublisher` 发出事件
2. 分发：`ApplicationEventMulticaster` 找到匹配的监听器并调用
3. 同步 vs 异步：默认同步；引入 executor 后变成异步
4. 事务事件：`@TransactionalEventListener` 在 AFTER_COMMIT 等时机触发
5. 常见坑：监听器顺序、异常传播、异步测试不稳定、事务回滚时事件是否触发

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/events/spring-core-events/part-00-guide/128-02-breakpoint-map.md`](../events/spring-core-events/part-00-guide/128-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/events/spring-core-events/part-00-guide/128-04-branch-decision-matrix.md`](../events/spring-core-events/part-00-guide/128-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/events/spring-core-events/appendix/136-90-common-pitfalls.md`](../events/spring-core-events/appendix/136-90-common-pitfalls.md)
- 自检清单：[`docs/events/spring-core-events/appendix/137-99-self-check.md`](../events/spring-core-events/appendix/137-99-self-check.md)

- 模块目录页：[`docs/events/spring-core-events/README.md`](../events/spring-core-events/README.md)
- 模块主线时间线（含可跑入口）：[`docs/events/spring-core-events/part-00-guide/03-mainline-timeline.md`](../events/spring-core-events/part-00-guide/127-03-mainline-timeline.md)

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsLabTest test`（`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBookMatrixLabTest test`（`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix - 基础事件）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsBasicsBranchMatrixLabTest test`（`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsBasicsBranchMatrixLabTest.java`）
- Lab（进阶：Branch Matrix - 异步/事务事件）：`mvn -q -pl :spring-core-events -Dtest=SpringCoreEventsAsyncTransactionalBranchMatrixLabTest test`（`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/SpringCoreEventsAsyncTransactionalBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part00_guide/SpringCoreEventsExerciseTest.java`

---

## 下一章怎么接

把事件跑通后，很多问题会落到“资源加载/扫描”：例如 classpath、jar、pattern matching。我们进入 Resources 主线。

- 下一章：[第 138 章：Resources 主线](138-resources-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Events 主线」的生效时机/顺序/边界；断点/入口：N；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Events 主线」的生效时机/顺序/边界；断点/入口：A（本页为索引；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Events 主线」的生效时机/顺序/边界；断点/入口：工具页）；断言：你能解释“为什么此处生效/为什么此处不生效”。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
