# 第 205 章：01：Observability 调用链（请求 → observation → meter）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Observability 调用链（请求 → observation → meter）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootObservabilityLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 205 章：00. 深挖导读](205-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 205 章：02：断点地图](205-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「01：Observability 调用链（请求 → observation → meter）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 你要能复述的“最短链路”

1. 发起请求（本模块：`/api/ping`）
2. 请求进入 Web 链路（FilterChain → MVC）
3. 链路中创建 observation（开始计时/记录标签）
4. 链路结束时结束 observation，并把数据写入 `MeterRegistry`
5. 你在 `MeterRegistry` 中能找到 `http.server.requests`，其 count 增长

本模块的证据链入口：

- `BootObservabilityLabTest#httpRequestProducesHttpServerRequestsMetrics`

## 小结与下一章

- 下一章给出“断点/观察点清单”，把链路落到可调试入口。

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「01：Observability 调用链（请求 → observation → meter）」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「01：Observability 调用链（请求 → observation → meter）」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootObservabilityLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootObservabilityLabTest`

上一章：[深挖导读](205-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[断点地图（排障优先）](205-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
