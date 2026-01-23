# 第 182 章：Testing 主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Testing 主线
    - 怎么使用：按目标选择测试切片（如 `@WebMvcTest`）或全量上下文（`@SpringBootTest`）；用 mock/替身把外部依赖固定成可断言证据。
    - 原理：测试注解决定上下文装配范围 → TestContext 缓存与复用 → slice/full context 的权衡 → 断言固化机制结论 → 快速定位失败。
    - 源码入口：`org.springframework.boot.test.context.SpringBootTest` / `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest` / `org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate`
    - 推荐 Lab：`GreetingControllerWebMvcLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 181 章：99 - Self Check（spring-boot-web-client）](../web-client/spring-boot-web-client/appendix/181-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 183 章：主线时间线：Spring Boot Testing](../testing/spring-boot-testing/part-00-guide/183-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

这一章解决的问题是：**为什么 `@WebMvcTest` 更快、为什么 `@SpringBootTest` 更像集成测试、为什么上下文缓存会让你“以为自己修好了”**。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Testing 主线 —— 按目标选择测试切片（如 `@WebMvcTest`）或全量上下文（`@SpringBootTest`）；用 mock/替身把外部依赖固定成可断言证据。
- 回到主线：测试注解决定上下文装配范围 → TestContext 缓存与复用 → slice/full context 的权衡 → 断言固化机制结论 → 快速定位失败。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Testing 主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

1. 选择测试粒度：
   - slice test（更快、更聚焦）
   - full context（更接近真实，但更慢）
2. 控制上下文：MockBean/测试配置/测试 profile
3. 固化断言：让机制结论可回归（而不是靠日志）
4. 运行与排障：只跑一个 test class/一个方法，快速定位失败
5. 常见坑：上下文污染、并发不稳定、网络/时间相关、测试之间隐式依赖

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/testing/spring-boot-testing/part-00-guide/184-02-breakpoint-map.md`](../testing/spring-boot-testing/part-00-guide/184-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/testing/spring-boot-testing/part-00-guide/184-04-branch-decision-matrix.md`](../testing/spring-boot-testing/part-00-guide/184-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/testing/spring-boot-testing/appendix/186-90-common-pitfalls.md`](../testing/spring-boot-testing/appendix/186-90-common-pitfalls.md)
- 自检清单：[`docs/testing/spring-boot-testing/appendix/187-99-self-check.md`](../testing/spring-boot-testing/appendix/187-99-self-check.md)

- 模块目录页：[`docs/testing/spring-boot-testing/README.md`](../testing/spring-boot-testing/README.md)
- 模块主线时间线（含可跑入口）：[`docs/testing/spring-boot-testing/part-00-guide/03-mainline-timeline.md`](../testing/spring-boot-testing/part-00-guide/183-03-mainline-timeline.md)

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-boot-testing -Dtest=GreetingControllerWebMvcLabTest test`（`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`（`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix）：`mvn -q -pl :spring-boot-testing -Dtest=BootTestingBranchMatrixLabTest test`（`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part00_guide/BootTestingExerciseTest.java`

---

## 下一章怎么接

把这些机制串成一条“真实业务链路”，是验证你是否真正掌握的最快方式。我们用 business case 模块收束整本书。

- 下一章：[第 188 章：Business Case 收束](188-business-case.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Testing 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.boot.test.context.SpringBootTest`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Testing 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Testing 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``GreetingControllerWebMvcLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
