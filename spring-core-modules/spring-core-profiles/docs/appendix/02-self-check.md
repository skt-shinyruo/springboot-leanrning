# 99 自检：Spring Profiles
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘出口）"

    - 主线入口：`SpringCoreProfilesBookMatrixLabTest`
    - 分支入口：`SpringCoreProfilesBranchMatrixLabTest`
    - 推荐先跑：`SpringCoreProfilesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑（Spring Core Profiles）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-core-profiles -Dtest=SpringCoreProfilesBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](../part-00-guide/04-breakpoint-map.md)
- [关键分支矩阵](../part-00-guide/05-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](01-common-pitfalls.md)

## 自检题（每题都能落到 tests）

1. 当你没有配置 active profiles 时，`Environment#getActiveProfiles()` 与 `getDefaultProfiles()` 分别是什么？  
   - 证据入口：`SpringCoreProfilesProfilePrecedenceLabTest#defaultProfilesContainDefault_whenNoActiveProfilesConfigured`
2. “不设 profile、不设 property”时，最终会选择哪个实现？你如何把它写成可回归断言？  
   - 证据入口：`SpringCoreProfilesLabTest#defaultsToDefaultProviderWhenNoProfileAndNoProperty`
3. `@ConditionalOnProperty` 的最小闭环是什么？你如何用一个 property 把实现切换跑成事实？  
   - 证据入口：`SpringCoreProfilesLabTest#usesFancyProviderWhenPropertyEnabled`
4. 激活 `dev` profile 后，为什么会切换到 dev 实现？你如何把“profile 选择”固定为断言？  
   - 证据入口：`SpringCoreProfilesLabTest#usesDevProviderWhenDevProfileActive`
5. 当 `dev` profile 与某个 property 条件同时成立时，谁“赢”？你如何用一条用例证明优先级？  
   - 证据入口：`SpringCoreProfilesLabTest#devProfileWinsOverNonDevConditionals`
6. profile negation（例如 `!dev`）的语义是什么？`dev` 不激活时它为什么会生效？  
   - 证据入口：`SpringCoreProfilesLabTest#profileNegationActivatesNonDevConfigurationWhenDevIsNotActive`
7. 当 `dev` 激活时，negation 条件为什么应该失效？你如何把它跑成事实？  
   - 证据入口：`SpringCoreProfilesLabTest#profileNegationDeactivatesNonDevConfigurationWhenDevIsActive`
8. `spring.profiles.active` 与 `spring.profiles.default` 谁优先？你如何用对照用例证明“default 不会混进 active”？  
   - 证据入口：`SpringCoreProfilesProfilePrecedenceLabTest#springProfilesActiveOverridesSpringProfilesDefault`
9. 多个 active profiles 时，行为是否会叠加？你如何证明 `dev,prod` 仍会激活 dev，并影响 negation 分支？  
   - 证据入口：`SpringCoreProfilesProfilePrecedenceLabTest#multipleActiveProfilesStillActivateDev_andDisableNegationProfile`
10. 并发下读取 Environment/property/profile 是否稳定？你如何用并发实验把“并发读一致”固定为结论？  
    - 证据入口：`SpringCoreProfilesEnvironmentConcurrencyLabTest#environmentReads_areConsistent_underConcurrentAccess`

## 退出条件（完成标准）

- 你能用“active/default/negation/property 条件”四个抓手解释最终装配结果，并能提供对应的断言入口。
- 你能用 ApplicationContextRunner 做最小复现：不依赖 IDE/外部环境，只用 property/profiles 固化事实。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreProfilesLabTest`

上一章：[90-common-pitfalls](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
