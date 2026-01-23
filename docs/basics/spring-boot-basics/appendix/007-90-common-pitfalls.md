# 第 7 章：90：常见坑清单（建议反复对照）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：90：常见坑清单（建议反复对照）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `application.yml`/环境变量/命令行等配置进入 `Environment`，用 `@ConfigurationProperties` 做类型安全绑定，并将最终值用于条件装配或业务逻辑。
    - 原理：配置源（PropertySource）→ `Environment` 聚合与覆盖 → Binder 绑定 → Profile/优先级分流 → 影响条件装配与运行期行为。
    - 源码入口：`org.springframework.core.env.ConfigurableEnvironment` / `org.springframework.core.env.PropertySource` / `org.springframework.boot.context.properties.bind.Binder` / `org.springframework.boot.context.properties.ConfigurationPropertiesBinder`
    - 推荐 Lab：`BootBasicsDefaultLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 6 章：02：`@ConfigurationProperties` 绑定与类型转换](../part-01-boot-basics/006-02-configuration-properties-binding.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 8 章：99 - Self Check（springboot-basics）](008-99-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障模板（统一结构）

当你遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，建议按下面 6 步收敛（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：你看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[004-02-breakpoint-map.md](../part-00-guide/004-02-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[004-04-branch-decision-matrix.md](../part-00-guide/004-04-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[008-99-self-check.md](008-99-self-check.md)

- 本章主题：**90：常见坑清单（建议反复对照）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`

## 机制主线

- （本章主线内容暂以契约骨架兜底；建议结合源码与测试用例补齐主线解释。）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`
- 建议命令：`mvn -pl :spring-boot-basics test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界


## 配置没生效

- 先看 `Environment#getActiveProfiles()`：你以为的 profile 是否真的激活？
- 再看 `Environment#getProperty("app.xxx")`：最终值到底是什么？
- 如果是测试覆盖：检查 `@SpringBootTest(properties=...)` 或 `@TestPropertySource` 等来源是否在覆盖你。

## `@ConfigurationProperties` 没绑定

- prefix 写错（`app` vs `apps`）
- 字段名映射误判（`featureEnabled` ↔ `feature-enabled`）
- 忘了启用扫描/启用绑定（本模块靠 `@ConfigurationPropertiesScan`）

## Bean 没切换 / 条件不生效

- 配置覆盖与 Bean 注册是两条线：配置变了不代表 Bean 一定会变。
- profile 条件：确认类上 `@Profile` 是否匹配当前激活 profile。

## 建议的排查顺序

1. 用断言确认最终值（不要猜）
2. 缩小上下文（优先 tests）
3. 再用日志/断点解释“为什么”

## 对应 Lab（可运行）

- `BootBasicsDefaultLabTest`
- `BootBasicsDevLabTest`
- `BootBasicsOverrideLabTest`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`

上一章：[part-01-boot-basics/02-configuration-properties-binding.md](../part-01-boot-basics/006-02-configuration-properties-binding.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](008-99-self-check.md)

<!-- BOOKIFY:END -->
