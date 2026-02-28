# 02. 00 - Deep Dive Guide（springboot-basics）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（怎么用这模块）"

    这份模块不想让人背“配置优先级表”。它只做一件更实用的事：把“配置从哪来、谁覆盖谁、Profile 到底影响什么”变成可以回归的结论——跑完几组 Lab，就能在项目里用同一套顺序排“配置没生效”的问题。

    - 主线入口：`BootBasicsBookMatrixLabTest`
    - 排障入口：`BootBasicsBranchMatrixLabTest` + [断点地图](04-breakpoint-map.md)
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：Spring Boot Basics（已迁移）](01-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 配置来源（PropertySources）与 Profile 覆盖](../part-01-boot-basics/01-property-sources-and-profiles.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootBasicsBookMatrixLabTest`（见文末“对应 Lab/Test”），先把“最终值来自哪里”跑成事实，再按章节把排障顺序补齐。


## 在这里要解决的“真实问题”

在项目里，配置相关的坑通常长这样：

- 我明明改了 `application.properties`，为什么没生效？
- dev 环境和本地行为不一致，究竟是哪份配置在生效？
- 测试里为什么又变了？我是不是被 `@SpringBootTest(properties = ...)` 覆盖了？

如果能把上面三句抱怨翻译成三句事实（active profiles 是什么、最终属性值是什么、Bean 实现是哪一个），这类问题基本就结束了。

## 两条阅读路线（按心情来）

### 路线 A：顺读主线（更接近读一篇短书）

1. 先读：[配置来源与 Profiles](../part-01-boot-basics/01-property-sources-and-profiles.md)（把“谁覆盖谁”说清楚）
2. 再读：[`@ConfigurationProperties`](../part-01-boot-basics/02-configuration-properties-binding.md)（把“字符串配置”收敛成类型安全对象）
3. 最后对照：常见坑与自检（附录两章）

### 路线 B：排障/复现（更接近带着问题翻手册）

- 先跑：`BootBasicsBranchMatrixLabTest`
- 再看：[断点地图](04-breakpoint-map.md)（用断点确认“最终值来自哪里”）
- 再对照：[关键分支矩阵](05-branch-decision-matrix.md)（把分支收敛成 If/Then）

## 机制主线（只记 4 件事就够用）

### 1) 最终值不在“某个文件”里，而在 `Environment` 里

写的 `application.properties`、dev profile 的配置、测试覆盖……最后都会汇总成同一个事实来源：`Environment`。

如果只做一件事：请在断点或断言里直接看它：

- `environment.getActiveProfiles()`
- `environment.getProperty("app.greeting")`

### 2) “覆盖”不是魔法，而是优先级

同一个 key 的最终值来自哪个来源，本质是 `PropertySources` 的优先级竞赛。无需记住全表，只需要用测试把关心的那几条覆盖关系钉住。

### 3) Profile 同时影响两条线：配置文件 + Bean 注册

Profile 很容易被误解成“只影响配置文件”。实际上它同时影响：

1. 哪些 profile 文件参与（例如 `application-dev.properties`）
2. 哪些 Bean 会被注册（例如 `@Profile("dev")` 的实现是否存在）

所以排障时必须分清：是在查“属性没覆盖”，还是在查“Bean 没切换”。

### 4) `@ConfigurationProperties` 是收敛点

当配置开始变多，散落的 `@Value` 会让人很难判断“最终值是什么”。`@ConfigurationProperties` 把配置集中到一个对象上，让能在测试里直接断言它。

## 本模块能稳定复现的 3 条分支（建议先跑一遍）

- 默认 profile（没有 dev）：`BootBasicsDefaultLabTest`
- dev profile：`BootBasicsDevLabTest`
- 测试覆盖优先级：`BootBasicsOverrideLabTest`

推荐入口（少而全）：

- `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`

## 断点建议（够用版）

- 确认 profile：`org.springframework.core.env.AbstractEnvironment#getActiveProfiles`
- 确认最终取值：`org.springframework.core.env.PropertySourcesPropertyResolver#getProperty`
- 深挖绑定（可选）：`org.springframework.boot.context.properties.bind.Binder#bind`

## 小结与下一章

- 下一章进入“谁覆盖谁”：把 `Environment/PropertySources/Profile` 这三件事串成一条可复用的排障路径。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`
- Exercise：`BootBasicsExerciseTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-boot-basics/01-property-sources-and-profiles.md](../part-01-boot-basics/01-property-sources-and-profiles.md)

<!-- BOOKIFY:END -->
