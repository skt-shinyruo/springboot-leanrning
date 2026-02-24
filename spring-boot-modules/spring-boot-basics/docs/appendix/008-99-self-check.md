# 第 8 章：99 - Self Check（springboot-basics）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘题）"

    这章没有新知识点，它更像你读完主线后的复盘纸：每个问题都对应一个可复现入口。你能答出来，说明你已经能在项目里独立排“配置没生效”。

    - 主线入口：`BootBasicsBookMatrixLabTest`
    - 分支入口：`BootBasicsBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 7 章：90：常见坑清单（建议反复对照）](007-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先跑入口（不要只做“纸面题”）

- Book Matrix：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](../part-00-guide/004-02-breakpoint-map.md)
- [关键分支矩阵](../part-00-guide/004-04-branch-decision-matrix.md)

## 自测题（每题都能落到 tests）

1. 默认情况下，`environment.getActiveProfiles()` 为什么不包含 `dev`？它的 default profile 又是什么？  
   - 入口：`BootBasicsDefaultLabTest#activeProfilesDoNotContainDevByDefault`
2. dev profile 激活后，哪些东西变了？（至少说出：一个属性值 + 一个 Bean 实现类）  
   - 入口：`BootBasicsDevLabTest#loadsDevProfileConfigurationAndBean`
3. 同一个 key（例如 `app.greeting`）在 default/dev/test override 三种来源下，最终值分别来自哪里？  
   - 入口：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`
4. 为什么 `Environment#getProperty("app.feature-enabled")` 是字符串 `"true/false"`，而 `AppProperties#isFeatureEnabled()` 是 boolean？  
   - 入口：`BootBasicsDefaultLabTest#canReadRawPropertyValuesFromEnvironment`
5. `@SpringBootTest(properties = ...)` 为什么能覆盖配置文件里的同名 key？它覆盖后哪些 Bean 会“看到”新值？  
   - 入口：`BootBasicsOverrideLabTest#beansSeeOverriddenProperties`
6. “属性覆盖”和“Bean 切换”为什么不是一回事？你如何用本模块的证据入口说明它们是两条线？  
   - 入口：`BootBasicsDevLabTest#loadsDevProfileConfigurationAndBean` + `BootBasicsOverrideLabTest#beansSeeOverriddenProperties`
7. `featureEnabled` 对应的配置 key 应该怎么写？为什么很多人会写错？  
   - 入口：`AppProperties`（字段名） + `application.properties`
8. 如果你新增一个字段（例如 `app.color`），你会怎么证明它真的绑定成功？  
   - 练习：`BootBasicsExerciseTest#exercise_addNewPropertyField`
9. 如果你故意写一个类型错误（例如 `app.feature-enabled=not-a-boolean`），你期望看到什么错误？你会如何写断言避免版本漂移？  
   - 练习：`BootBasicsExerciseTest#exercise_invalidPropertyType`
10. 遇到“配置没生效”，你最推荐的排障顺序是什么？（用 3 步描述即可）  
    - 对照：`007-90-common-pitfalls.md`

## 如果你卡住了

这模块最容易卡人的点，不是“不会写配置”，而是“没把最终事实先固定下来”。如果你发现自己又开始盯着某个文件猜原因了，停一下：先断言 `Environment#getProperty(...)` 的最终值，再回过头解释为什么。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest`
- Exercise：`BootBasicsExerciseTest`

上一章：[appendix/90-common-pitfalls.md](007-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
