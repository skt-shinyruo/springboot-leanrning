# 第 84 章：Security 主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Security 主线
    - 怎么使用：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。
    - 原理：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。
    - 源码入口：`org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` / `org.springframework.security.web.access.intercept.AuthorizationFilter`
    - 推荐 Lab：`BootSecurityLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 83 章：99 - Self Check（springboot-web-mvc）](../web-mvc/springboot-web-mvc/appendix/083-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 85 章：主线时间线：Spring Boot Security](../security/springboot-security/part-00-guide/085-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

这一章解决的问题是：**你没写安全框架代码，为什么请求就会被拦住、为什么会出现 401/403、为什么 CSRF 会影响表单提交**。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Security 主线 —— 将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。
- 回到主线：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Security 主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

把 Security 当成“在 Web 请求前后加一串过滤器”：

1. 请求进入 Security Filter Chain（Filter）
2. 认证（Authentication）：是谁（identity）？
3. 授权（Authorization）：能做什么（authority）？
4. 异常处理与重定向：401/403、登录页、错误页
5. 常见坑：Filter 顺序、匿名用户、Session/RememberMe、CSRF 与非幂等请求

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/security/springboot-security/part-00-guide/086-02-breakpoint-map.md`](../security/springboot-security/part-00-guide/086-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/security/springboot-security/part-00-guide/086-04-branch-decision-matrix.md`](../security/springboot-security/part-00-guide/086-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/security/springboot-security/appendix/092-90-common-pitfalls.md`](../security/springboot-security/appendix/092-90-common-pitfalls.md)
- 自检清单：[`docs/security/springboot-security/appendix/093-99-self-check.md`](../security/springboot-security/appendix/093-99-self-check.md)

- 模块目录页：[`docs/security/springboot-security/README.md`](../security/springboot-security/README.md)
- 模块主线时间线（含可跑入口）：[`docs/security/springboot-security/part-00-guide/03-mainline-timeline.md`](../security/springboot-security/part-00-guide/085-03-mainline-timeline.md)

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :springboot-security -Dtest=BootSecurityLabTest test`（`spring-boot-modules/springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :springboot-security -Dtest=BootSecurityBookMatrixLabTest test`（`spring-boot-modules/springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix）：`mvn -q -pl :springboot-security -Dtest=BootSecurityBranchMatrixLabTest test`（`spring-boot-modules/springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-boot-modules/springboot-security/src/test/java/com/learning/springboot/bootsecurity/part00_guide/BootSecurityExerciseTest.java`

---

## 下一章怎么接

请求通过安全边界后，最常见的落点是“读写数据”：我们进入 Data JPA 主线。

- 下一章：[第 94 章：Data JPA 主线](094-data-jpa-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Security 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.security.web.FilterChainProxy`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Security 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.security.web.SecurityFilterChain`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Security 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.security.web.access.intercept.AuthorizationFilter`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootSecurityLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
