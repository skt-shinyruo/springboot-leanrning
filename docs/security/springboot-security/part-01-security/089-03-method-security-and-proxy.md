# 第 89 章：03：Method Security 与代理：self-invocation 陷阱
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：03：Method Security 与代理：self-invocation 陷阱
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。
    - 原理：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。
    - 源码入口：`org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` / `org.springframework.security.web.access.intercept.AuthorizationFilter`
    - 推荐 Lab：`BootSecurityLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 88 章：02：CSRF：为什么 GET 没事但 POST 会 403？](088-02-csrf.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 90 章：04：FilterChain：多链路 + 顺序 + 自定义 Filter](090-04-filter-chain-and-order.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03：Method Security 与代理：self-invocation 陷阱**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootSecurityLabTest`
    - Test file：`spring-boot-modules/springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

## 机制主线

本章解释：为什么你在方法上写了 `@PreAuthorize`，但某些调用路径却“没生效”。

对应代码：

- `spring-boot-modules/springboot-security/src/main/java/com/learning/springboot/bootsecurity/part01_security/AdminOnlyService.java`
- `spring-boot-modules/springboot-security/src/main/java/com/learning/springboot/bootsecurity/part01_security/SelfInvocationPitfallService.java`

对应验证入口（可跑）：

- `BootSecurityLabTest#methodSecurityDeniesAdminOnlyMethodForNonAdmin`
- `BootSecurityLabTest#methodSecurityAllowsAdminOnlyMethodForAdmin`
- `BootSecurityLabTest#selfInvocationBypassesMethodSecurityAsAPitfall`

## 你应该观察到什么

1. 外部调用受保护方法（跨 bean 边界）时，method security 能拦住（抛 `AccessDeniedException`）。
2. 同一个类内部的 `this.xxx()` 调用会绕过代理：即使目标方法上有 `@PreAuthorize`，也可能“看起来没生效”。

## 机制解释（Why）

Method Security 的本质仍然是 **代理**：

- 只有当调用路径经过代理对象时，`@PreAuthorize` 才会触发安全拦截器。
- 类内部的 `this.xxx()` 属于 self-invocation，会直接调用目标对象方法，绕过代理。

这也是为什么：
- AOP
- `@Transactional`
- method validation
- method security

## 建议

- 尽量避免在同一类里用 `this.xxx()` 调用带安全注解的方法。
- 或者把需要安全保护的方法拆到另一个 bean（通过依赖注入调用），确保走代理。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootSecurityLabTest`
- 建议命令：`mvn -pl :springboot-security test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 实验入口

<!-- BOOKLIKE-V2:EVIDENCE:START -->
实验入口已在章首提示框给出（先跑再读）。建议跑完后回到本章“证据链”逐条验证关键结论。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## 常见坑与边界

### 坑点 1：self-invocation 绕过代理，导致 `@PreAuthorize` 看起来“没生效”

- Symptom：你在方法上写了 `@PreAuthorize`，但某条调用路径没有触发拦截
- Root Cause：method security 依赖代理；同类内部 `this.xxx()` 属于 self-invocation，会直接调用目标方法，绕过代理
- Verification：`BootSecurityLabTest#selfInvocationBypassesMethodSecurityAsAPitfall`
- Breakpoints：
  - `SelfInvocationPitfallService#outerCallsAdminOnly`（自调用入口）
  - `SelfInvocationPitfallService#adminOnly`（被绕过的注解方法）
- Fix：把受保护方法拆到另一个 bean，通过依赖注入跨 bean 调用，确保走代理；并用默认 Lab 把“是否抛 AccessDeniedException”锁成回归门禁

### 坑点 2：roles vs authorities 的前缀差异，导致规则误判（ROLE_ 边界）

- Symptom：你给了 `ADMIN` authority，但 `@PreAuthorize("hasRole('ADMIN')")` 仍然拒绝
- Root Cause：`hasRole('ADMIN')` 的语义是检查 `ROLE_ADMIN`；只有 `ADMIN` 并不等价于 `ROLE_ADMIN`
- Verification：`BootSecurityLabTest#methodSecurityDeniesAdminOnlyMethodWhenRolePrefixMissing_asPitfall`
- Breakpoints：`AdminOnlyService#adminOnlyAction`
- Fix：统一权限命名（role 语义使用 `ROLE_` 前缀），或把规则改为 `hasAuthority('ADMIN')` 并保证配置/测试一致

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootSecurityLabTest`
- Test file：`spring-boot-modules/springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

上一章：[part-01-security/02-csrf.md](088-02-csrf.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-security/04-filter-chain-and-order.md](090-04-filter-chain-and-order.md)

<!-- BOOKIFY:END -->
