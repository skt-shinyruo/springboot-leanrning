# 第 149 章：Profiles 主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Profiles 主线
    - 怎么使用：用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。
    - 原理：激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。
    - 源码入口：`org.springframework.context.annotation.Profile` / `org.springframework.context.annotation.ConditionEvaluator#shouldSkip` / `org.springframework.core.env.ConfigurableEnvironment#getActiveProfiles`
    - 推荐 Lab：`SpringCoreProfilesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 148 章：自测题（Spring Core Resources）](../resources/spring-core-resources/appendix/148-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 150 章：主线时间线：Spring Profiles](../profiles/spring-core-profiles/part-00-guide/150-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

这一章解决的问题是：**为什么同一个应用在 dev/prod 行为不一样、为什么某个 Bean 在某环境“消失了”、为什么条件装配没生效**。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Profiles 主线 —— 用 `@Profile`/`@ConditionalOnProperty` 在不同环境选择 Bean 实现；排障时先确认 profiles 激活方式与条件匹配结果。
- 回到主线：激活 profiles → 条件评估（shouldSkip）→ Bean 是否注册；profiles 同时影响配置参与与装配选择。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Profiles 主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

Profile 影响两条线：

1. **配置文件参与与否**：哪些配置源进入 `Environment`
2. **Bean 注册与否**：哪些配置类/Bean 被注册到容器

因此排障时你要能先分流判断：

- 这是“最终属性值不对”的问题？
- 还是“Bean 根本没注册/注册错了实现”的问题？

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/profiles/spring-core-profiles/part-00-guide/151-02-breakpoint-map.md`](../profiles/spring-core-profiles/part-00-guide/151-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/profiles/spring-core-profiles/part-00-guide/151-04-branch-decision-matrix.md`](../profiles/spring-core-profiles/part-00-guide/151-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/profiles/spring-core-profiles/appendix/153-90-common-pitfalls.md`](../profiles/spring-core-profiles/appendix/153-90-common-pitfalls.md)
- 自检清单：[`docs/profiles/spring-core-profiles/appendix/154-99-self-check.md`](../profiles/spring-core-profiles/appendix/154-99-self-check.md)

- 模块目录页：[`docs/profiles/spring-core-profiles/README.md`](../profiles/spring-core-profiles/README.md)
- 模块主线时间线（含可跑入口）：[`docs/profiles/spring-core-profiles/part-00-guide/03-mainline-timeline.md`](../profiles/spring-core-profiles/part-00-guide/150-03-mainline-timeline.md)

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesLabTest test`（`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`（`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix）：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`（`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part00_guide/SpringCoreProfilesExerciseTest.java`

---

## 下一章怎么接

配置与 Bean 都到位后，下一类“必须把分支讲清楚”的机制是校验：Validation 的错误是怎么产生、怎么回传、为什么依赖代理。

- 下一章：[第 155 章：Validation 主线](155-validation-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Profiles 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.context.annotation.Profile`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Profiles 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.context.annotation.ConditionEvaluator#shouldSkip`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Profiles 主线」的生效时机/顺序/边界；断点/入口：`org.springframework.core.env.ConfigurableEnvironment#getActiveProfiles`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreProfilesLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
