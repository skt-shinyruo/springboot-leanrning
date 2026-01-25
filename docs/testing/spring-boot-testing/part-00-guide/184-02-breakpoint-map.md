# 第 184 章：02：断点地图（Testing Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Testing Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`GreetingControllerWebMvcLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 184 章：00 - Deep Dive Guide（springboot-testing）](184-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 184 章：04：关键分支矩阵（Branch Decision Matrix）](184-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Testing Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

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

- 常见坑：[`../appendix/186-90-common-pitfalls.md`](../appendix/186-90-common-pitfalls.md)
- 自检：[`../appendix/187-99-self-check.md`](../appendix/187-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`GreetingControllerWebMvcLabTest` / `BootTestingBookMatrixLabTest` / `BootTestingBranchMatrixLabTest`

上一章：[slice 与 mocking](../part-01-testing/185-01-slice-and-mocking.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[184-04-branch-decision-matrix.md](184-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
