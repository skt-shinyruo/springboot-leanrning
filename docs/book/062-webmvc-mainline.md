# 第 62 章：Web MVC 请求主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Web MVC 请求主线
    - 怎么使用：编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。
    - 原理：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping` / `org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod` / `org.springframework.web.servlet.HandlerExceptionResolver`
    - 推荐 Lab：`BootWebMvcInternalsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 61 章：自测题（Spring Core Tx）](../tx/spring-core-tx/appendix/061-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 63 章：主线时间线：Spring Boot Web MVC](../web-mvc/spring-boot-web-mvc/part-00-guide/063-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

这一章解决的问题是：**一个 HTTP 请求进入 Spring Boot 后，是怎么被路由到你的 Controller、怎么完成参数绑定/校验、怎么写回响应、出错时怎么被统一处理**。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Web MVC 请求主线 —— 编写 `@Controller/@RestController` 作为入口，配合参数绑定（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）、校验（Bean Validation）与统一异常处理（`@ControllerAdvice`）。
- 回到主线：HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析与校验 → 视图/消息转换写回 → ExceptionResolvers 收敛错误。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Web MVC 请求主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

把 Web MVC 看成一条“请求流水线”：

1. 请求进入 `DispatcherServlet`
2. `HandlerMapping` 找到 handler（Controller 方法）
3. `HandlerAdapter` 调用 handler：
   - ArgumentResolver 解析参数（`@RequestParam/@PathVariable/@RequestBody/@ModelAttribute`）
   - Validator/BindingResult 参与校验与错误收集
4. 写回响应：
   - View 渲染（HTML）
   - 或 MessageConverter 序列化（JSON）
5. 出错时进入异常解析链：ExceptionResolvers（400/404/415/500 等）
6. 常见边界：媒体类型协商（406/415）、绑定失败分支（BindException vs MethodArgumentNotValid）、Filter/Interceptor 顺序与 async 生命周期

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/web-mvc/spring-boot-web-mvc/part-00-guide/066-02-breakpoint-map.md`](../web-mvc/spring-boot-web-mvc/part-00-guide/066-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/web-mvc/spring-boot-web-mvc/part-00-guide/064-04-branch-decision-matrix.md`](../web-mvc/spring-boot-web-mvc/part-00-guide/064-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/web-mvc/spring-boot-web-mvc/appendix/082-90-common-pitfalls.md`](../web-mvc/spring-boot-web-mvc/appendix/082-90-common-pitfalls.md)
- 自检清单：[`docs/web-mvc/spring-boot-web-mvc/appendix/083-99-self-check.md`](../web-mvc/spring-boot-web-mvc/appendix/083-99-self-check.md)

- 模块目录页：[`docs/web-mvc/spring-boot-web-mvc/README.md`](../web-mvc/spring-boot-web-mvc/README.md)
- 模块主线时间线（含可跑入口）：[`docs/web-mvc/spring-boot-web-mvc/part-00-guide/03-mainline-timeline.md`](../web-mvc/spring-boot-web-mvc/part-00-guide/063-03-mainline-timeline.md)

建议先跑的最小闭环：

- `BootWebMvcLabTest`（`@WebMvcTest` 切片）

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcLabTest test`（`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`（`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcBookMatrixLabTest.java`）
- Lab（进阶：错误分支矩阵 400/406/415）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`（`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part07_testing/BootWebMvcErrorBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`

---

## 下一章怎么接

Web MVC 把“请求处理”跑通之后，下一层经常就会碰到“认证/授权”：我们进入 Security 主线。

- 下一章：[第 84 章：Security 主线](084-security-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Web MVC 请求主线」的生效时机/顺序/边界；断点/入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Web MVC 请求主线」的生效时机/顺序/边界；断点/入口：`org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Web MVC 请求主线」的生效时机/顺序/边界；断点/入口：`org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootWebMvcInternalsLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
