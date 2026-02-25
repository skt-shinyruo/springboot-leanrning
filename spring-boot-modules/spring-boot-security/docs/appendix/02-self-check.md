# 02. 99 - Self Check（springboot-security）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（springboot-security）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。
    - 原理：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。
    - 源码入口：`org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` / `org.springframework.security.web.access.intercept.AuthorizationFilter`
    - 推荐 Lab：`BootSecurityDevProfileLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（Security）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 94 章：Data JPA 主线](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/04-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/05-branch-decision-matrix.md)

- 本章主题：**02. 99 - Self Check（springboot-security）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest` / `BootSecurityMultiFilterChainOrderLabTest`

## 机制主线

Security 这一模块的主线可以用一句话概括：**同一个请求进来，先走 FilterChain，再谈认证/授权；方法级安全是另一条“代理拦截器链”**。

自检时建议先把下面三个分流说清楚（并能指出一个可跑的验证入口）：

1. 401/403/404 分别意味着什么？哪些是“路由/暴露问题”，哪些是“安全边界问题”？
2. 多条 `SecurityFilterChain` 时，为什么“更宽的 matcher + 更靠前的顺序”会吃掉后续规则？
3. method security 为什么像 AOP/Tx 一样绕不过代理与入口？（尤其是 self-invocation）

## 自测题
1. Filter Chain 的顺序为什么很重要？“同一个请求”会经过哪些 filter？
2. 为什么方法级安全需要代理？自调用会导致什么问题？
3. JWT 无状态方案下，认证信息如何在请求间传递？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章主要作为补充说明/索引页使用：推荐直接从模块的 Matrix/Lab 入口进入，再回到这里对照。
- Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest` / `BootSecurityMultiFilterChainOrderLabTest`
- 建议命令：`mvn -pl :spring-boot-security test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 对应 Exercise（可运行）

- `BootSecurityExerciseTest`

## 常见坑与边界

### 坑点 1：方法级安全“看起来写了注解”，但调用链绕过代理导致不生效

- Symptom：你在 `@PreAuthorize` 等注解上写了规则，但某些调用路径没有触发拦截
- Root Cause：method security 依赖代理；self-invocation 会绕过代理（与 HTTP filter chain 是两条完全不同的线）
- Verification：`BootSecurityLabTest#selfInvocationBypassesMethodSecurityAsAPitfall`
- Fix：把需要拦截的方法调用跨越 bean 边界（让代理参与），并用测试锁定“是否抛 AccessDeniedException”

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest` / `BootSecurityMultiFilterChainOrderLabTest`
- Exercise：`BootSecurityExerciseTest`

上一章：[appendix/90-common-pitfalls.md](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
