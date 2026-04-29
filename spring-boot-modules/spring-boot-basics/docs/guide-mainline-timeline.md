# 01. 主线时间线：Spring Boot Basics

<!-- CHAPTER-CARD:START -->
!!! summary "章节定位"

    本页保留旧链接入口，并把启动、配置、Profile、绑定四段主线压缩到一页。完整顺序仍以模块 README 为准。

    - 模块目录：[`模块目录`](../README.md)
    - 可跑入口：`BootBasicsOverrideLabTest`
<!-- CHAPTER-CARD:END -->

## 本页定位

Spring Boot Basics 的主线不是“背配置优先级”，而是把一次启动拆成四个可观察问题：

- `SpringApplication#run` 先构造 `Environment`，再创建并刷新 `ApplicationContext`。
- `PropertySources` 决定同一个 key 最终来自哪里。
- Profile 决定哪些配置片段、Bean 定义与条件分支被纳入。
- `@ConfigurationProperties` 把最终环境值绑定成类型化对象，并在绑定失败时暴露清晰错误。

## 主线压缩图

1. 启动入口：先看 [`guide-deep-dive-guide.md`](guide-deep-dive-guide.md)，把 Boot 启动与 Environment 初始化放到一条线上。
2. 配置覆盖：再读 [`boot-basics-property-sources-and-profiles.md`](boot-basics-property-sources-and-profiles.md)，用测试确认“最终值来自哪个来源”。
3. 绑定机制：继续读 [`boot-basics-configuration-properties-binding.md`](boot-basics-configuration-properties-binding.md)，区分“配置存在”与“绑定成功”。
4. 常见误判：最后对照 [`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)，把配置没生效、Profile 没激活、绑定失败拆成不同证据链。

## 可运行入口

- Lab：`BootBasicsOverrideLabTest`
- Maven：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsOverrideLabTest test`

## 下一步

顺读入口仍以模块目录为准：[`模块目录`](../README.md)。本页适合作为“先跑一条配置覆盖实验，再回到目录继续读”的短路径。
