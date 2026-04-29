# 04. 断点地图（Testing）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（Testing）展开，主线可以概括为：测试注解决定 ContextBootstrapper 与 auto-config 范围；Mock 注入通过 TestExecutionListener 参与容器装配。

    先跑 `BootTestingBranchMatrixLabTest` 固化 “@WebMvcTest vs @SpringBootTest vs @MockBean” 的边界，再用断点定位测试上下文如何装配、mock 如何注入、为什么 bean 不存在。

    需要下探源码时，可以从 `SpringBootTestContextBootstrapper` / `WebMvcTestContextBootstrapper` / `MockitoTestExecutionListener` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Testing](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

- Testing 排障的核心：先确认“测试 slice 边界”（只加载 web 层？还是完整 Boot 上下文？），再谈 mock/bean 是否生效。
- 证据链：失败异常（NoSuchBeanDefinition / UnsatisfiedDependency）→ 断点看谁在创建 context → 观察最终 bean 定义列表。

## 运行入口（先运行）

- Book Matrix：`BootTestingBookMatrixLabTest`
- Branch Matrix：`BootTestingBranchMatrixLabTest`

运行命令：

- `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBranchMatrixLabTest test`

## 断点（上下文创建与 mock 注入）

- `org.springframework.boot.test.context.SpringBootTestContextBootstrapper#buildTestContext`
- `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTestContextBootstrapper#buildTestContext`
- `org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener#postProcessFields`

## 观察点

- 当前测试类上到底是什么注解（决定 slice）
- `ApplicationContext` 里是否存在目标 bean（`containsBeanDefinition` / `getBeanNamesForType`）
- `@MockBean` 是否真的替换了原 bean（观察 beanName 与实例类型）

## 排障入口（Playbook）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

测试注解决定 ContextBootstrapper 与 auto-config 范围；Mock 注入通过 TestExecutionListener 参与容器装配。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootTestingBranchMatrixLabTest`
- Lab：`GreetingControllerWebMvcLabTest` / `GreetingControllerSpringBootLabTest` / `BootTestingMockBeanLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

