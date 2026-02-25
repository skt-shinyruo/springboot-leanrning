# 04. 断点地图（Testing Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Testing Debugger Pack）
    - 怎么使用：先跑 `BootTestingBranchMatrixLabTest` 固化 “@WebMvcTest vs @SpringBootTest vs @MockBean” 的边界，再用断点定位测试上下文如何装配、mock 如何注入、为什么 bean 不存在。
    - 原理：测试注解决定 ContextBootstrapper 与 auto-config 范围；Mock 注入通过 TestExecutionListener 参与容器装配。
    - 源码入口：`SpringBootTestContextBootstrapper` / `WebMvcTestContextBootstrapper` / `MockitoTestExecutionListener`
    - 推荐 Lab：`BootTestingBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 00 - Deep Dive Guide（springboot-testing）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 关键分支矩阵（Branch Decision Matrix）](05-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- Testing 排障的核心：先确认“测试 slice 边界”（只加载 web 层？还是完整 Boot 上下文？），再谈 mock/bean 是否生效。
- 推荐证据链：失败异常（NoSuchBeanDefinition / UnsatisfiedDependency）→ 断点看谁在创建 context → 观察最终 bean 定义列表。

## 运行入口（建议先跑）

- Book Matrix：`BootTestingBookMatrixLabTest`
- Branch Matrix：`BootTestingBranchMatrixLabTest`

推荐命令：

- `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBranchMatrixLabTest test`

## 断点（上下文创建与 mock 注入）

- `org.springframework.boot.test.context.SpringBootTestContextBootstrapper#buildTestContext`
- `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTestContextBootstrapper#buildTestContext`
- `org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener#postProcessFields`

## Watchpoints（建议）

- 当前测试类上到底是什么注解（决定 slice）
- `ApplicationContext` 里是否存在目标 bean（`containsBeanDefinition` / `getBeanNamesForType`）
- `@MockBean` 是否真的替换了原 bean（观察 beanName 与实例类型）

## 排障入口（Playbook）

- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

- 小结：测试注解决定 ContextBootstrapper 与 auto-config 范围；Mock 注入通过 TestExecutionListener 参与容器装配。
- 下一章：[第 184 章：04：关键分支矩阵（Branch Decision Matrix）](05-branch-decision-matrix.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootTestingBranchMatrixLabTest`
- Lab：`GreetingControllerWebMvcLabTest` / `GreetingControllerSpringBootLabTest` / `BootTestingMockBeanLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](05-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

