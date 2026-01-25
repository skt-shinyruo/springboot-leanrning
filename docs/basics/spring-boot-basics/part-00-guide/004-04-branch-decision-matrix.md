# 第 4 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootBasicsDevLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 4 章：02：断点地图（Boot Basics Debugger Pack）](004-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 5 章：01：配置来源（PropertySources）与 Profile 覆盖](../part-01-boot-basics/005-01-property-sources-and-profiles.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：04：关键分支矩阵（Branch Decision Matrix） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

本页把“配置到底怎么生效”拆成 3 条最常见、最容易踩坑的分支；建议做法：

1. 先跑 `BootBasicsBranchMatrixLabTest`
2. 断点停在 `AppProperties#setGreeting`，观察最终绑定值
3. 回到框架断点确认来源（命中哪个 PropertySource）

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| Default Profile | 未显式激活 `dev` | greeting 来自默认配置，使用 default bean | `BootBasicsBookMatrixLabTest` / `BootBasicsDefaultLabTest` | `activeProfiles` / `app.greeting` / `DefaultGreetingProvider` |
| Dev Profile 生效 | 激活 `dev` profile | greeting 来自 dev 配置，使用 dev bean | `BootBasicsBranchMatrixLabTest` / `BootBasicsDevLabTest` | `getActiveProfiles()` / `DevGreetingProvider` |
| 测试覆盖优先级 | 测试通过 properties 覆盖 `app.greeting` | greeting 以测试覆盖为准（优先级最高） | `BootBasicsBranchMatrixLabTest` / `BootBasicsOverrideLabTest` | `Environment#getProperty("app.greeting")` 命中哪个 source |

## 推荐运行命令

- `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBranchMatrixLabTest test`

## 调试路线（建议）

- 第 1 站：`AppProperties#setGreeting`（确认绑定结果）
- 第 2 站：`ConfigurationPropertiesBindingPostProcessor#postProcessBeforeInitialization`（确认绑定发生点）
- 第 3 站：`PropertySourcesPropertyResolver#getProperty`（确认命中的 PropertySource）

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/007-90-common-pitfalls.md`](../appendix/007-90-common-pitfalls.md)
- 自检：[`../appendix/008-99-self-check.md`](../appendix/008-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootBasicsDevLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBasicsDevLabTest` / `BootBasicsBranchMatrixLabTest` / `BootBasicsBookMatrixLabTest` / `BootBasicsDefaultLabTest` / `BootBasicsOverrideLabTest`

上一章：[004-02-breakpoint-map.md](004-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/007-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
