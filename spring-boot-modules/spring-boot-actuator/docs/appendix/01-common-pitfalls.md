# 01. 90 - Common Pitfalls（springboot-actuator）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    Actuator 的排障最容易被一句“访问不到端点”带偏：端点是否存在、是否暴露到 Web、暴露后是否允许访问，其实是三段不同的分流。本章把这些分支写成可对照的排障笔记，让 404/401/403 不再混在一起讨论。

    建议先运行 `BootActuatorExposureOverrideLabTest` 与 `BootActuatorLabTest`，把默认行为与覆盖行为跑成断言，再回到本章逐条对照。需要下探源码时，优先从 `WebEndpointAutoConfiguration` 与 `WebEndpointProperties`（exposure/base-path）这条线切入。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 01 - Actuator 基础与暴露](../part-01-actuator/01-actuator-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-actuator）](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先把三段分流跑成事实（别从“端点是不是没注册”开始猜）

本章后面的每个坑点，最终都会落到三件事之一：端点有没有注册、端点有没有暴露到 HTTP、暴露之后有没有权限访问。把它们先拆开，很多“看起来很玄学”的现象会立刻变得可解释。

最省心的做法是先把主线与分支跑成断言：Book Matrix 只回答“默认行为是什么”，Branch Matrix 则把 exposure/base-path/security 这些常见分支跑全。

- `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`
- `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`

需要下探调用链时，再对照本模块的断点地图与关键分支矩阵去命中入口，避免在日志里猜测行为：[04-breakpoint-map.md](../part-00-guide/04-breakpoint-map.md) / [05-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md)。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`

## 最小可运行实验（Lab）

- Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`
- 建议命令：`mvn -pl :spring-boot-actuator test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

这一模块的排障最怕把现象混在一起：**端点存在 / 端点暴露 / 端点可访问** 是三件不同的事（见 Deep Dive Guide 的“三段式分流”）。

## 坑 1：把 404 当成“端点不存在”，忽略 exposure 的分流

访问 `/actuator/env` 得到 404 时，很容易先怀疑“端点没注册”。但在 Actuator 里，404 更常见的含义是：端点确实存在，只是没有暴露到 Web（exposure 的 include/exclude 没放开，或 base-path 不对）。因此第一步不需要翻 `@Endpoint`，而是回到暴露规则与根路径 `_links`。

下面几条测试刻意把“端点存在”与“端点暴露”拆开，是最短的对照证据：

- 默认不暴露：`BootActuatorLabTest#envEndpointIsNotExposedByDefault`
- 显式 include 后可访问：`BootActuatorExposureOverrideLabTest#envEndpointCanBeExposedViaProperties`
- 根路径 links 只列出暴露端点：`BootActuatorLabTest#actuatorRootListsExposedEndpoints`
- 暴露后 links 才会出现：`BootActuatorExposureOverrideLabTest#actuatorRootIncludesEnvLinkWhenExposed`

先看 `/actuator` 的 `_links`（它只列“暴露端点”），再核对 include/exclude/base-path，而不是上来就怀疑“端点没注册”。

## 坑 2：环境差异把带偏（profile/配置来源）

本地可以、线上不行；或者 IDE 里 OK、命令行不行——这类问题看上去像“端点不稳定”，但更常见的原因是：真正生效的配置来源与直觉不一致。Actuator 的行为高度依赖配置覆盖顺序（profile/环境变量/外部配置）。

先确认“当前生效的配置值是什么、来自哪个 PropertySource”，再讨论“配置写没写对”。

## 坑 3：暴露端点不等于允许匿名访问（401/403）

端点已经暴露，但访问仍然返回 401/403，这通常不是 exposure 的问题，而是安全边界在起作用。exposure 决定“有没有路由”，安全策略决定“能不能访问”。401/403/404 三种状态码要先分流清楚。

先把 401/403/404 分清：404 多半是没暴露/路径不对，401/403 才是安全边界（鉴权/CSRF/网络隔离等）。

## 对应 Lab（可运行）

- `BootActuatorLabTest`
- `BootActuatorExposureOverrideLabTest`

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`

上一章：[part-01-actuator/01-actuator-basics.md](../part-01-actuator/01-actuator-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](02-self-check.md)

<!-- BOOKIFY:END -->
