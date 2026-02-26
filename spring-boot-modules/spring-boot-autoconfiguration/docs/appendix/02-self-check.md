# 99 自检：Spring Boot Auto-Configuration
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘出口）"

    - 主线入口：`BootAutoConfigurationBookMatrixLabTest`
    - 分支入口：`BootAutoConfigurationBranchMatrixLabTest`
    - 推荐先跑：`BootAutoConfigurationLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 90 - Common Pitfalls（springboot-autoconfiguration）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](../part-00-guide/04-breakpoint-map.md)
- [关键分支矩阵](../part-00-guide/05-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](01-common-pitfalls.md)

## 自检题（每题都能落到 tests/断点）

1. auto-config 在什么条件下会创建“默认实现”？你如何用一个断言证明它真的被注册并可用？  
   - 证据入口：`BootAutoConfigurationLabTest#autoConfigCreatesDefaultBeanWhenEnabled`
2. `demo.greeting.decorate=true` 打开后，为什么会出现多个相关 bean，但最终注入的是“装饰后的实现”？你如何证明“取到的是哪个”？  
   - 证据入口：`BootAutoConfigurationLabTest#decoratorCreatesPrimaryBeanWhenEnabled`
3. 用户自定义一个 `GreetingService` 后，为什么 auto-config 会 backoff？你如何证明 backoff 发生在“定义层”（bean 是否注册），而不是“调用层”（运行时才选择）？  
   - 证据入口：`BootAutoConfigurationLabTest#userBeanOverridesAutoConfig_backoffOccurs`
4. 你如何解释 `@Primary` 的作用边界：它解决的是“候选选择”，还是“禁止注册”？  
   - 对照：`BootAutoConfigurationLabTest#decoratorCreatesPrimaryBeanWhenEnabled` vs `BootAutoConfigurationLabTest#userBeanOverridesAutoConfig_backoffOccurs`
5. 如果你怀疑某个条件没命中，你会把断点下在什么位置来观察“为什么 shouldSkip”？（写出至少 1 个入口方法名）  
   - 证据导航：[`../part-00-guide/04-breakpoint-map.md`](../part-00-guide/04-breakpoint-map.md)
6. “装配顺序”在这个模块里如何体现？你如何从测试断言反推：哪些 bean 先出现、哪些后出现？  
   - 证据入口：`BootAutoConfigurationLabTest#decoratorCreatesPrimaryBeanWhenEnabled`
7. 并发条件下，多线程反复 `getBean(GreetingService.class)` 是否会拿到不同实例？你用什么证据把它写成可回归结论？  
   - 证据入口：`BootAutoConfigurationConcurrencyLabTest#retrievingPrimaryBeanAndCallingService_isConsistent_underConcurrency`
8. 你能用 2–3 句话给出这个模块的“最短排障路径”吗？（从 imports → condition → backoff）  
   - 对照：[`01-common-pitfalls.md`](01-common-pitfalls.md)
9. 练习：新增一个条件分支并把 backoff 固化成断言（不要靠日志）。  
   - 入口：`BootAutoConfigurationExerciseTest#exercise_addAConditionalBeanAndVerifyBackoff`

## 退出条件（完成标准）

- 你能把“为什么生效/为什么不生效”落到两件事：条件是否命中 + backoff 是否发生，并能指回一个测试断言。
- 你能在断点里观察到：`shouldSkip` 的结论如何影响最终 bean 定义是否注册。
