# 第 86 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootSecurityMultiFilterChainOrderLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 86 章：02：断点地图（Security Debugger Pack）](086-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 87 章：01：Basic Auth 与授权（最小可跑主线）](../part-01-security/087-01-basic-auth-and-authorization.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：04：关键分支矩阵（Branch Decision Matrix） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「04：关键分支矩阵（Branch Decision Matrix）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-boot-security -Dtest=BootSecurityMultiFilterChainOrderLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 多 chain 选择 | 多个 `SecurityFilterChain` 同时存在 | 命中顺序正确（更具体的先匹配） | `BootSecurityMultiFilterChainOrderLabTest` | `FilterChainProxy#getFilters` |
| Profile 差异 | `dev` profile 激活 | dev 配置生效（允许/放宽策略） | `BootSecurityDevProfileLabTest` | activeProfiles / bean 注册 |
| 默认主线 | 默认 profile | 默认授权策略生效 | `BootSecurityLabTest` | authentication / status |

## 推荐运行命令

- `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/092-90-common-pitfalls.md`](../appendix/092-90-common-pitfalls.md)
- 自检：[`../appendix/093-99-self-check.md`](../appendix/093-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootSecurityMultiFilterChainOrderLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootSecurityMultiFilterChainOrderLabTest` / `BootSecurityDevProfileLabTest` / `BootSecurityLabTest` / `BootSecurityBranchMatrixLabTest`

上一章：[086-02-breakpoint-map.md](086-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/092-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
