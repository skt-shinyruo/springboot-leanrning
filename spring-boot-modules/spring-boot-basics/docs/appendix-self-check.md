# 99 自检：Spring Boot Basics
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（复盘题）"

    这章没有新知识点，其定位更接近读完主线后的复盘纸：每个问题都对应一个可复现入口。能答出来，说明已经能在项目里独立排“配置没生效”。

    - 主线入口：`BootBasicsBookMatrixLabTest`
    - 分支入口：`BootBasicsBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（排查时对照）](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootBasicsBookMatrixLabTest`（主线）或 `BootBasicsBranchMatrixLabTest`（分支）（见文末“对应实验/测试”），再按题目回到对应复现入口逐一验证。


## 先跑入口（不要只做“纸面题”）

- Book Matrix：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](guide-breakpoint-map.md)
- [关键分支矩阵](guide-branch-decision-matrix.md)

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
6. “属性覆盖”和“Bean 切换”为什么不是一回事？如何用本模块的证据入口说明它们是两条线？
   - 入口：`BootBasicsDevLabTest#loadsDevProfileConfigurationAndBean` + `BootBasicsOverrideLabTest#beansSeeOverriddenProperties`
7. `featureEnabled` 对应的配置 key 应该怎么写？为什么很多人会写错？
   - 入口：`AppProperties`（字段名） + `application.properties`
8. 如果新增一个字段（例如 `app.color`），会怎么证明它真的绑定成功？
   - 练习：`BootBasicsExerciseTest#exercise_addNewPropertyField`
9. 如果故意写一个类型错误（例如 `app.feature-enabled=not-a-boolean`），期望看到什么错误？如何写断言避免版本漂移？
   - 练习：`BootBasicsExerciseTest#exercise_invalidPropertyType`
10. 遇到“配置没生效”，优先采用的排障顺序是什么？（用 3 步描述即可）
    - 对照：`01-common-pitfalls.md`

## 如果卡住了

这模块最容易卡人的点，不是“不会写配置”，而是“没把最终事实先固定下来”。如果发现自己又开始盯着某个文件猜原因了，停一下：先断言 `Environment#getProperty(...)` 的最终值，再回过头解释为什么。

## 小结与下一章

下一章见：[模块目录](../README.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest`
- Exercise：`BootBasicsExerciseTest`

上一章：[appendix-common-pitfalls.md](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)

<!-- BOOKIFY:END -->
