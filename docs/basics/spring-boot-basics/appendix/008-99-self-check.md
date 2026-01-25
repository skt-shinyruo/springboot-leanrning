# 第 8 章：99 - Self Check（springboot-basics）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（springboot-basics）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `application.yml`/环境变量/命令行等配置进入 `Environment`，用 `@ConfigurationProperties` 做类型安全绑定，并将最终值用于条件装配或业务逻辑。
    - 原理：配置源（PropertySource）→ `Environment` 聚合与覆盖 → Binder 绑定 → Profile/优先级分流 → 影响条件装配与运行期行为。
    - 源码入口：`org.springframework.core.env.ConfigurableEnvironment` / `org.springframework.core.env.PropertySource` / `org.springframework.boot.context.properties.bind.Binder` / `org.springframework.boot.context.properties.ConfigurationPropertiesBinder`
    - 推荐 Lab：`BootBasicsDefaultLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 7 章：90：常见坑清单（建议反复对照）](007-90-common-pitfalls.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 9 章：IoC 容器主线（Beans）](../../../book/009-ioc-container-mainline.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/004-02-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/004-04-branch-decision-matrix.md)

- 本章主题：**99 - Self Check（springboot-basics）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest`

## 机制主线

这一章是“复盘用的自测入口”，不是新增概念章。你应该用它检验三件事：

1. **你能否用证据链讲清“谁覆盖谁”**  
   - 先跑 `BootBasicsOverrideLabTest#testPropertiesOverrideApplicationProperties`，再用 `Environment`/绑定对象断言解释原因
2. **你能否用证据链讲清“Profile 到底影响了什么”**  
   - 对照 `BootBasicsDefaultLabTest#activeProfilesDoNotContainDevByDefault` 与 `BootBasicsDevLabTest#activeProfilesContainDev`
3. **你能否把“现象 → 机制分支 → 修复策略”说成可操作步骤**  
   - 目标：遇到配置问题时，不靠“猜”，而是先把最终值与生效分支固定下来

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章未显式引用 LabTest，先注入模块默认 LabTest 作为“合规兜底入口”（后续可逐章细化）。
- Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest`
- 建议命令：`mvn -pl :spring-boot-basics test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 自测题
1. `PropertySources` 的优先级如何决定最终属性值？
2. `spring.profiles.active` 与 `spring.profiles.include` 的差异是什么？
3. `@ConfigurationProperties` 绑定失败时，你会优先看哪些线索（日志/断点/条件报告）？

## 对应 Exercise（可运行）

- `BootBasicsExerciseTest`

## 常见坑与边界

### 坑点 1：把“属性覆盖”误当成“Bean 一定会切换”

- Symptom：你修改了 `app.greeting`，但注入点的实现类没变；或实现类变了但属性值没变
- Root Cause：属性覆盖与 Bean 注册是两条线：属性走 `Environment`，Bean 切换走条件注册（profile/条件装配）
- Verification：对照 `BootBasicsDevLabTest#loadsDevProfileConfigurationAndBean` 与 `BootBasicsOverrideLabTest#beansSeeOverriddenProperties`
- Fix：先用断言固定“active profiles 与最终属性值”，再排查实现类是否受 `@Profile`/条件影响

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest`
- Exercise：`BootBasicsExerciseTest`

上一章：[appendix/90-common-pitfalls.md](007-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
