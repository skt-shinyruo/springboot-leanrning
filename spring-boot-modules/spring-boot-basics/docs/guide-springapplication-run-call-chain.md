# 03. `SpringApplication#run` 调用链（启动 → 环境 → 容器）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：`SpringApplication#run` 调用链（启动 → 环境 → 容器）展开，主线可以概括为：Boot 启动是“先环境、后容器”：先构建 Environment（含 profile/property sources），再创建 ApplicationContext 并 refresh。

    先跑 `BootBasicsDefaultLabTest`/`BootBasicsDevLabTest`，把“profile/配置覆盖”固化为断言，再按本章把 `run()` 的关键阶段串起来。

    需要下探源码时，可以从 `org.springframework.boot.SpringApplication#run` / `ConfigDataEnvironmentPostProcessor` / `org.springframework.context.support.AbstractApplicationContext#refresh` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Basics](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. 断点地图（Boot Basics）](guide-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootBasicsDefaultLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：Boot 启动是“先环境、后容器”：先构建 Environment（含 profile/property sources），再创建 ApplicationContext 并 refresh。需要下探源码时，可以从 `org.springframework.boot.SpringApplication#run` / `ConfigDataEnvironmentPostProcessor` / `org.springframework.context.support.AbstractApplicationContext#refresh` 这些入口切入。


## 最短调用链（应能复述）

1. `SpringApplication#run`
2. 准备 Environment（加载配置、激活 profiles、合并 property sources）
3. 创建 ApplicationContext（选择 context 类型）
4. `refresh()`：容器初始化（定义层 → 实例层）
5. 发布启动完成事件（应用可用）

证据链入口：

- `BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`

## 小结与下一章

Boot 启动是“先环境、后容器”：先构建 Environment（含 profile/property sources），再创建 ApplicationContext 并 refresh。

下一章见：[02：断点地图](guide-breakpoint-map.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootBasicsDefaultLabTest`
- Lab：`BootBasicsDevLabTest`
- Lab：`BootBasicsOverrideLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-breakpoint-map.md](guide-breakpoint-map.md)

<!-- BOOKIFY:END -->
