# 第 174 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootWebClientWebClientLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 174 章：02：断点地图（Web Client Debugger Pack）](174-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 175 章：01：RestClient 基础：请求构造与错误处理](../part-01-web-client/175-01-restclient-basics.md)
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
mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientWebClientLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 4xx/5xx 映射 | 下游返回 400/500 | 映射成领域异常并携带 status | `BootWebClientWebClientLabTest` | exception 类型 / status |
| 请求/响应 Filter 顺序 | 链式 filter | request 顺序与 response 顺序相反 | `BootWebClientWebClientFilterOrderLabTest` | trace 列表顺序 |
| 超时 | 下游延迟超过 timeout | 快速失败（异常抛出） | `BootWebClientWebClientLabTest#webClientResponseTimeoutFailsFast` | timeout 异常与耗时 |
| 重试 | 5xx 且配置 retry | 重试后成功（请求数增加） | `BootWebClientWebClientLabTest#webClientRetriesOn5xxAndEventuallySucceeds` | request count / retry 次数 |
| JSON 解码容错 | response 含未知字段 | 默认忽略未知字段 | `BootWebClientWebClientLabTest#webClientIgnoresUnknownJsonFieldsByDefault` | ObjectMapper 配置 |

## 推荐运行命令

- `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/180-90-common-pitfalls.md`](../appendix/180-90-common-pitfalls.md)
- 自检：[`../appendix/181-99-self-check.md`](../appendix/181-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootWebClientWebClientLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientWebClientLabTest` / `BootWebClientWebClientFilterOrderLabTest` / `BootWebClientBranchMatrixLabTest`

上一章：[174-02-breakpoint-map.md](174-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/180-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
