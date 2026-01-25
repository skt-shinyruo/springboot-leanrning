# 第 86 章：02：断点地图（Security Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Security Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootSecurityMultiFilterChainOrderLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 86 章：00 - Deep Dive Guide（springboot-security）](086-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 86 章：04：关键分支矩阵（Branch Decision Matrix）](086-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Security Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

- Security 排障最关键问题：**命中哪条 FilterChain**、**在哪个 filter 失败**、**失败如何映射成响应**。
- 推荐证据链：先用 MockMvc/TestRestTemplate 固定 status，再用断点定位 chain 与 filter。

## 运行入口（建议先跑）

- Book Matrix：`BootSecurityBookMatrixLabTest`
- Branch Matrix：`BootSecurityBranchMatrixLabTest`

推荐命令：

- `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBranchMatrixLabTest test`

## 入口断点（链路分段）

- `org.springframework.security.web.FilterChainProxy#doFilterInternal`
- `org.springframework.security.web.FilterChainProxy#getFilters`

## Watchpoints（建议）

- request path / method（决定 chain 匹配）
- 命中的 `SecurityFilterChain`（以及 filters 列表）
- `SecurityContextHolder.getContext().getAuthentication()`（认证结果）

## 常见分支定位（与矩阵表配合）

- 多 chain 顺序不对：优先看 `FilterChainProxy#getFilters` 返回的 chain。
- Profile 差异：先看 activeProfiles，再看对应 bean/配置是否注册。

## 排障入口（Playbook）

- 常见坑：[`../appendix/092-90-common-pitfalls.md`](../appendix/092-90-common-pitfalls.md)
- 自检：[`../appendix/093-99-self-check.md`](../appendix/093-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootSecurityMultiFilterChainOrderLabTest` / `BootSecurityBookMatrixLabTest` / `BootSecurityBranchMatrixLabTest`

上一章：[JWT 无状态](../part-01-security/091-05-jwt-stateless.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[086-04-branch-decision-matrix.md](086-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
