# 第 174 章：02：断点地图（Web Client Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Web Client Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootWebClientWebClientLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 174 章：00 - Deep Dive Guide（springboot-web-client）](174-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 174 章：04：关键分支矩阵（Branch Decision Matrix）](174-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Web Client Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

- Web Client 排障的第一原则：先确认分支属于哪一类：**编码/请求/响应解码/错误映射/超时/重试/Filter**。
- 推荐证据链：测试断言（status/异常类型/请求数）→ 断点（分支发生点）→ Watchpoints（请求/响应关键字段）。

## 运行入口（建议先跑）

- Book Matrix：`BootWebClientBookMatrixLabTest`
- Branch Matrix：`BootWebClientBranchMatrixLabTest`

推荐命令：

- `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`

## Filter 顺序断点（最常见误解点）

- 入口：`ExchangeFilterFunction#filter`
- 观察点：request 顺序 vs response 顺序（“洋葱模型”）

## 超时/重试断点（从现象回到 operator）

- 观察点：timeout 触发时异常类型、重试次数
- 建议从测试断言反推：`webClientResponseTimeoutFailsFast` / `webClientRetriesOn5xxAndEventuallySucceeds`

## Watchpoints（建议）

- 请求：path/query/header（尤其是 correlation id）
- 响应：status code（400/500）→ 映射到领域异常
- retry 次数：MockWebServer request count
- filter trace：request/response 的顺序

## 排障入口（Playbook）

- 常见坑：[`../appendix/180-90-common-pitfalls.md`](../appendix/180-90-common-pitfalls.md)
- 自检：[`../appendix/181-99-self-check.md`](../appendix/181-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientWebClientLabTest` / `BootWebClientBookMatrixLabTest` / `BootWebClientBranchMatrixLabTest`

上一章：[MockWebServer 测试](../part-01-web-client/179-05-testing-with-mockwebserver.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[174-04-branch-decision-matrix.md](174-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
