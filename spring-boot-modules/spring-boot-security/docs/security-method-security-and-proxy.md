# 03. Method Security 与代理：self-invocation 陷阱
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕03：Method Security 与代理：self-invocation 陷阱展开，主线可以概括为：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。

    阅读时可以先跑 `BootSecurityLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。

    需要下探源码时，可以从 `org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` / `org.springframework.security.web.access.intercept.AuthorizationFilter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. CSRF：为什么 GET 没事但 POST 会 403？](security-csrf.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. FilterChain：多链路 + 顺序 + 自定义 Filter](security-filter-chain-and-order.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootSecurityLabTest`
    - 测试文件：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

## 机制主线

本章解释：为什么在方法上写了 `@PreAuthorize`，但某些调用路径却“没生效”。

对应代码：

- `spring-boot-modules/spring-boot-security/src/main/java/com/learning/springboot/bootsecurity/part01_security/AdminOnlyService.java`
- `spring-boot-modules/spring-boot-security/src/main/java/com/learning/springboot/bootsecurity/part01_security/SelfInvocationPitfallService.java`

对应验证入口（可跑）：

- `BootSecurityLabTest#methodSecurityDeniesAdminOnlyMethodForNonAdmin`
- `BootSecurityLabTest#methodSecurityAllowsAdminOnlyMethodForAdmin`
- `BootSecurityLabTest#selfInvocationBypassesMethodSecurityAsAPitfall`

## 应当观察到的现象

1. 外部调用受保护方法（跨 bean 边界）时，method security 能拦住（抛 `AccessDeniedException`）。
2. 同一个类内部的 `this.xxx()` 调用会绕过代理：即使目标方法上有 `@PreAuthorize`，也可能“表面上没生效”。

## 机制解释（Why）

Method Security 的本质仍然是 **代理**：

- 只有当调用路径经过代理对象时，`@PreAuthorize` 才会触发安全拦截器。
- 类内部的 `this.xxx()` 属于 self-invocation，会直接调用目标对象方法，绕过代理。

这也是为什么：
- AOP
- `@Transactional`
- method validation
- method security

## 处理方式

- 尽量避免在同一类里用 `this.xxx()` 调用带安全注解的方法。
- 或者把需要安全保护的方法拆到另一个 bean（通过依赖注入调用），确保走代理。

## 最小可运行实验（Lab）

- Lab：`BootSecurityLabTest`
- 运行命令：`mvn -pl :spring-boot-security test`（或在 IDE 直接运行上面的测试类）


## 常见坑与边界

### 坑点 1：self-invocation 绕过代理，导致 `@PreAuthorize` 表面上“没生效”

在方法上写了 `@PreAuthorize`，但某条调用路径没有触发拦截

method security 依赖代理；同类内部 `this.xxx()` 属于 self-invocation，会直接调用目标方法，绕过代理

`BootSecurityLabTest#selfInvocationBypassesMethodSecurityAsAPitfall`

- `SelfInvocationPitfallService#outerCallsAdminOnly`（自调用入口）
- `SelfInvocationPitfallService#adminOnly`（被绕过的注解方法）

把受保护方法拆到另一个 bean，通过依赖注入跨 bean 调用，确保走代理；并用默认 Lab 把“是否抛 AccessDeniedException”锁成回归断言

### 坑点 2：roles vs authorities 的前缀差异，导致规则误判（ROLE_ 边界）

给了 `ADMIN` authority，但 `@PreAuthorize("hasRole('ADMIN')")` 仍然拒绝

`hasRole('ADMIN')` 的语义是检查 `ROLE_ADMIN`；只有 `ADMIN` 并不等价于 `ROLE_ADMIN`

`BootSecurityLabTest#methodSecurityDeniesAdminOnlyMethodWhenRolePrefixMissing_asPitfall`

`AdminOnlyService#adminOnlyAction`

统一权限命名（role 语义使用 `ROLE_` 前缀），或把规则改为 `hasAuthority('ADMIN')` 并保证配置/测试一致

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootSecurityLabTest`
- 测试文件：`spring-boot-modules/spring-boot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`

上一章：[security-csrf.md](security-csrf.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[security-filter-chain-and-order.md](security-filter-chain-and-order.md)

<!-- BOOKIFY:END -->
