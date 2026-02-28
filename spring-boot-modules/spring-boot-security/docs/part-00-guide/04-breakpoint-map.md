# 04. 断点地图（Security Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕02：断点地图（Security Debugger Pack）展开，主线可以概括为：请求 → `FilterChainProxy` 选择 `SecurityFilterChain` → 逐个 filter 执行 → `Authentication`/`Authorization` 决策 → 可能影响 MVC 异常边界。

    先跑 `BootSecurityBranchMatrixLabTest` 固化“哪条 SecurityFilterChain 被命中”的断言，再用断点沿 `FilterChainProxy` 观察 chain 匹配、过滤器顺序与鉴权失败点。

    需要下探源码时，可以从 `org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 00 - Deep Dive Guide（springboot-security）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 关键分支矩阵（Branch Decision Matrix）](05-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

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

- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

请求 → `FilterChainProxy` 选择 `SecurityFilterChain` → 逐个 filter 执行 → `Authentication`/`Authorization` 决策 → 可能影响 MVC 异常边界。

下一章见：[第 86 章：04：关键分支矩阵（Branch Decision Matrix）](05-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootSecurityBranchMatrixLabTest`
- Lab：`BootSecurityMultiFilterChainOrderLabTest` / `BootSecurityDevProfileLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](05-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

