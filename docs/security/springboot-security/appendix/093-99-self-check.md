# 第 93 章：99 - Self Check（springboot-security）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（springboot-security）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。
    - 原理：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。
    - 源码入口：`org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` / `org.springframework.security.web.access.intercept.AuthorizationFilter`
    - 推荐 Lab：`BootSecurityDevProfileLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 92 章：90：常见坑清单（Security）](092-90-common-pitfalls.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 94 章：Data JPA 主线](../../../book/094-data-jpa-mainline.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :springboot-security -Dtest=BootSecurityBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :springboot-security -Dtest=BootSecurityBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/086-02-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/086-04-branch-decision-matrix.md)

- 本章主题：**99 - Self Check（springboot-security）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest` / `BootSecurityMultiFilterChainOrderLabTest`

## 机制主线


## 自测题
1. Filter Chain 的顺序为什么很重要？“同一个请求”会经过哪些 filter？
2. 为什么方法级安全需要代理？自调用会导致什么问题？
3. JWT 无状态方案下，认证信息如何在请求间传递？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章未显式引用 LabTest，先注入模块默认 LabTest 作为“合规兜底入口”（后续可逐章细化）。
- Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest` / `BootSecurityMultiFilterChainOrderLabTest`
- 建议命令：`mvn -pl :springboot-security test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 对应 Exercise（可运行）

- `BootSecurityExerciseTest`

## 常见坑与边界

### 坑点 1：方法级安全“看起来写了注解”，但调用链绕过代理导致不生效

- Symptom：你在 `@PreAuthorize` 等注解上写了规则，但某些调用路径没有触发拦截
- Root Cause：method security 依赖代理；self-invocation 会绕过代理（与 HTTP filter chain 是两条完全不同的线）
- Verification：`BootSecurityLabTest#selfInvocationBypassesMethodSecurityAsAPitfall`
- Fix：把需要拦截的方法调用跨越 bean 边界（让代理参与），并用测试锁住“是否抛 AccessDeniedException”

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootSecurityDevProfileLabTest` / `BootSecurityLabTest` / `BootSecurityMultiFilterChainOrderLabTest`
- Exercise：`BootSecurityExerciseTest`

上一章：[appendix/90-common-pitfalls.md](092-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
