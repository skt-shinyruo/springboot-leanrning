# 第 185 章：01 - Slice 与 Mock（Testing）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Slice 与 Mock（Testing）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：按目标选择测试切片（如 `@WebMvcTest`）或全量上下文（`@SpringBootTest`）；用 mock/替身把外部依赖固定成可断言证据。
    - 原理：测试注解决定上下文装配范围 → TestContext 缓存与复用 → slice/full context 的权衡 → 断言固化机制结论 → 快速定位失败。
    - 源码入口：`org.springframework.boot.test.context.SpringBootTest` / `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest` / `org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate`
    - 推荐 Lab：`BootTestingMockBeanLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 184 章：00 - Deep Dive Guide（springboot-testing）](../part-00-guide/184-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 186 章：90 - Common Pitfalls（springboot-testing）](../appendix/186-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01 - Slice 与 Mock（Testing）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`
    - Test file：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java` / `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerSpringBootLabTest.java` / `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingMockBeanLabTest.java`

## 机制主线

这页不展开完整机制主线；它更像一张“决策表 + 入口清单”：帮你在 slice test 与 mock 策略之间快速收敛分支，并把结论落到可回归的入口上。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`
- 建议命令：`mvn -pl :spring-boot-testing test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 导读
本章把“测试类型选择”讲清楚：什么时候用 `@WebMvcTest`，什么时候必须用 `@SpringBootTest`，以及 `@MockBean` 的替换边界。

## 最小可复现入口
- WebMvc slice：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java`
- Full Boot：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerSpringBootLabTest.java`
- MockBean：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingMockBeanLabTest.java`

## 常见坑与边界

### 坑点 1：误以为 `@WebMvcTest` 会加载完整业务 bean，导致“启动失败/测试意义跑偏”

- Symptom：
  - 你想测试 controller，却发现测试启动失败（常见是缺少 service/repository bean）
  - 或者你为了解决启动失败引入了过多配置，最终把 slice 测试写成了“又慢又不稳定的全量测试”
- Root Cause：
  - `@WebMvcTest` 的目标是**只启动 MVC slice**，默认不会把你的业务依赖（service/repo）全加载进来
  - slice 测试里如果 controller 依赖 service，你必须显式提供它（通常用 `@MockBean`）
- Verification（证据链）：
  - WebMvc slice 的正确姿势：`GreetingControllerWebMvcLabTest`（通过 `@MockBean GreetingService` 固定 controller 契约）
    - `GreetingControllerWebMvcLabTest#returnsGreetingFromMockedService`
  - full context 的对照组：`GreetingControllerSpringBootLabTest#returnsGreetingFromRealService`
  - `@MockBean` 在 full context 中确实会覆盖真实 bean：`BootTestingMockBeanLabTest#mockBeanOverridesRealBeanInFullContext`
- Fix：
  - 只测 controller 契约 → 优先 `@WebMvcTest` + `@MockBean`
  - 要验证真实自动装配/配置/集成边界 → 使用 `@SpringBootTest`（并用更少的 mock）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`
- Test file：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java` / `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerSpringBootLabTest.java` / `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingMockBeanLabTest.java`

上一章：[part-00-guide/00-deep-dive-guide.md](../part-00-guide/184-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](../appendix/186-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
