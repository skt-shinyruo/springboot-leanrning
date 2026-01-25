# 第 96 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootDataJpaMergeAndDetachLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 96 章：02：断点地图（Data JPA Debugger Pack）](096-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 97 章：01. Entity 状态机：transient / managed / detached / removed](../part-01-data-jpa/097-01-entity-states.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：04：关键分支矩阵（Branch Decision Matrix） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

JPA 的大多数“怪现象”都能用两条线解释：

- **对象现在是什么状态（managed/detached）**
- **flush 什么时候发生（以及 flush 后谁能看到）**

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| managed 脏检查 | 事务内修改 managed entity | 提交/flush 后变化落库 | `BootDataJpaBranchMatrixLabTest` / `BootDataJpaLabTest` | `entityManager.contains` / flush 时机 / SQL |
| clear → detach 边界 | 调用 `EntityManager#clear` | 后续修改不再自动落库 | `BootDataJpaBranchMatrixLabTest` / `BootDataJpaLabTest` | `contains(entity)==false` |
| merge 语义 | detached entity 调用 `merge` | detached 变化被合并到 managed copy | `BootDataJpaBranchMatrixLabTest` / `BootDataJpaMergeAndDetachLabTest` | merge 返回值引用是否变化 |
| SQL 证据链 | 打开 show-sql/format_sql | 可以直接看见“什么时候发 SQL” | `BootDataJpaBranchMatrixLabTest` / `BootDataJpaDebugSqlLabTest` | 控制台 SQL 输出与断点对应 |

## 推荐运行命令

- `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`

## 调试路线（建议）

- 第 1 站：`EntityManager#clear` / `#merge` / `#flush`（状态机与 flush）
- 第 2 站：`SimpleJpaRepository#save`（Repository 到 EM 的桥）
- 第 3 站：回到断言处，确认“你以为发生的分支”与“真实发生的分支”一致

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/104-90-common-pitfalls.md`](../appendix/104-90-common-pitfalls.md)
- 自检：[`../appendix/105-99-self-check.md`](../appendix/105-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootDataJpaMergeAndDetachLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootDataJpaMergeAndDetachLabTest` / `BootDataJpaBranchMatrixLabTest` / `BootDataJpaLabTest` / `BootDataJpaDebugSqlLabTest`

上一章：[096-02-breakpoint-map.md](096-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/104-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
