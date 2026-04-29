# 01. 01 - Slice 与 Mock（Testing）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕Slice 与 Mock（Testing） 展开，主线可以概括为：测试注解决定上下文装配范围 → TestContext 缓存与复用 → slice/full context 的权衡 → 断言固化机制结论 → 快速定位失败。

    阅读时可以先跑 `BootTestingMockBeanLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：按目标选择测试切片（如 `@WebMvcTest`）或全量上下文（`@SpringBootTest`）；用 mock/替身把外部依赖固定成可断言证据。

    需要下探源码时，可以从 `org.springframework.boot.test.context.SpringBootTest` / `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest` / `org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Testing](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[常见坑清单](appendix-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章把“测试类型选择”讲清楚：什么时候用 `@WebMvcTest`，什么时候必须用 `@SpringBootTest`，以及 `@MockBean` 的替换边界。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`
    - 测试文件：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java` / `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerSpringBootLabTest.java` / `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingMockBeanLabTest.java`

## 最小可运行实验（Lab）

- Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`
- 运行命令：`mvn -pl :spring-boot-testing test`（或在 IDE 直接运行上面的测试类）


## 最小可复现入口
- WebMvc slice：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java`
- Full Boot：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerSpringBootLabTest.java`
- MockBean：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingMockBeanLabTest.java`

## 常见坑与边界

### 坑点 1：误以为 `@WebMvcTest` 会加载完整业务 bean，导致“启动失败/测试意义跑偏”

- 想测试 controller，却发现测试启动失败（常见是缺少 service/repository bean）
- 或者为了解决启动失败引入了过多配置，最终把 slice 测试写成了“又慢又不稳定的全量测试”

- `@WebMvcTest` 的目标是**只启动 MVC slice**，默认不会把业务依赖（service/repo）全加载进来
- slice 测试里如果 controller 依赖 service，必须显式提供它（通常用 `@MockBean`）

证据链

- WebMvc slice 的正确方式：`GreetingControllerWebMvcLabTest`（通过 `@MockBean GreetingService` 固定 controller 契约）
- `GreetingControllerWebMvcLabTest#returnsGreetingFromMockedService`
- full context 的对照组：`GreetingControllerSpringBootLabTest#returnsGreetingFromRealService`
- `@MockBean` 在 full context 中确实会覆盖真实 bean：`BootTestingMockBeanLabTest#mockBeanOverridesRealBeanInFullContext`

- 只测 controller 契约 → 优先 `@WebMvcTest` + `@MockBean`
- 要验证真实自动装配/配置/集成边界 → 使用 `@SpringBootTest`（并用更少的 mock）

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`
- 测试文件：`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java` / `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerSpringBootLabTest.java` / `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingMockBeanLabTest.java`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[appendix-common-pitfalls.md](appendix-common-pitfalls.md)

<!-- BOOKIFY:END -->
