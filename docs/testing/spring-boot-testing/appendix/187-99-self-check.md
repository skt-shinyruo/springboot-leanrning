# 第 187 章：99 - Self Check（springboot-testing）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（springboot-testing）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：按目标选择测试切片（如 `@WebMvcTest`）或全量上下文（`@SpringBootTest`）；用 mock/替身把外部依赖固定成可断言证据。
    - 原理：测试注解决定上下文装配范围 → TestContext 缓存与复用 → slice/full context 的权衡 → 断言固化机制结论 → 快速定位失败。
    - 源码入口：`org.springframework.boot.test.context.SpringBootTest` / `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest` / `org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate`
    - 推荐 Lab：`BootTestingMockBeanLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 186 章：90 - Common Pitfalls（springboot-testing）](186-90-common-pitfalls.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 188 章：Business Case 收束](../../../book/188-business-case.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/184-02-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/184-04-branch-decision-matrix.md)

- 本章主题：**99 - Self Check（springboot-testing）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest`

## 机制主线

这一章用“对照 + 断言”复盘三件事：

1. **slice vs full 的 bean 图边界**：你到底启动了什么（决定你能断言什么）
2. **mock 的替换边界**：`@MockBean` 是“替换 Spring 容器里的 bean”，不是 Mockito 的普通字段 mock
3. **排障分流**：启动失败/bean 缺失/行为不一致时，先确认测试类型与上下文范围

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章未显式引用 LabTest，先注入模块默认 LabTest 作为“合规兜底入口”（后续可逐章细化）。
- Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest`
- 建议命令：`mvn -pl :spring-boot-testing test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 自测题
1. `@WebMvcTest` 会加载哪些 bean？不会加载哪些 bean？
2. `@MockBean` 与 Mockito 的 `@Mock` 有何差异？替换发生在哪个阶段？

## 对应 Exercise（可运行）

- `BootTestingExerciseTest`

## 常见坑与边界

### 坑点 1：把 `@Mock` 当成 `@MockBean`，导致“mock 了但并未生效”

- Symptom：你以为某个依赖已经被 mock，但实际请求仍走真实实现（或直接 NPE/启动失败）
- Root Cause：
  - `@Mock` 只是 Mockito 字段 mock，不会自动替换 Spring 容器里的 bean
  - `@MockBean` 才会把容器中的 bean 替换掉，进而影响注入与调用链
- Verification：
  - full context + `@MockBean` 覆盖真实 bean：`BootTestingMockBeanLabTest#mockBeanOverridesRealBeanInFullContext`
  - slice（WebMvcTest）里用 `@MockBean` 兜底 controller 依赖：`GreetingControllerWebMvcLabTest#returnsGreetingFromMockedService`
- Fix：需要影响 Spring 注入链就用 `@MockBean`；需要测试真实集成边界就减少 mock 并用 `@SpringBootTest`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest`
- Exercise：`BootTestingExerciseTest`

上一章：[appendix/90-common-pitfalls.md](186-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
