# 第 168 章：02：断点地图（Actuator Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Actuator Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootActuatorExposureOverrideLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 168 章：00 - Deep Dive Guide（springboot-actuator）](168-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 168 章：04：关键分支矩阵（Branch Decision Matrix）](168-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Actuator Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

- Actuator 排障核心问题：**endpoint 是否注册**、**是否暴露**、**是否被安全/网关拦截**。
- 推荐证据链：先用测试证明“是否可访问（HTTP 200/404/401）”，再用断点定位分支发生点。

## 运行入口（建议先跑）

- Book Matrix：`BootActuatorBookMatrixLabTest`
- Branch Matrix：`BootActuatorBranchMatrixLabTest`

推荐命令：

- `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`

## 断点（Endpoint 注册与暴露）

- `org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer#discoverEndpoints`
- `org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver#resolveLinks`
- `org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping#getHandlerInternal`

## Watchpoints

- `management.endpoints.web.exposure.include`（最终值）
- `/actuator` root 的 `_links` 是否包含目标 endpoint
- 请求是否被安全链拦截（若启用 security，优先确认 FilterChain 分支）

## 排障入口（Playbook）

- 常见坑：[`../appendix/170-90-common-pitfalls.md`](../appendix/170-90-common-pitfalls.md)
- 自检：[`../appendix/171-99-self-check.md`](../appendix/171-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorBookMatrixLabTest` / `BootActuatorBranchMatrixLabTest`

上一章：[Actuator 基础](../part-01-actuator/169-01-actuator-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[168-04-branch-decision-matrix.md](168-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
