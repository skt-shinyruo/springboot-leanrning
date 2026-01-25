# 第 4 章：01：`SpringApplication#run` 调用链（启动 → 环境 → 容器）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：`SpringApplication#run` 调用链（启动 → 环境 → 容器）
    - 怎么使用：先跑 `BootBasicsDefaultLabTest`/`BootBasicsDevLabTest`，把“profile/配置覆盖”固化为断言，再按本文把 `run()` 的关键阶段串起来。
    - 原理：Boot 启动是“先环境、后容器”：先构建 Environment（含 profile/property sources），再创建 ApplicationContext 并 refresh。
    - 源码入口：`org.springframework.boot.SpringApplication#run` / `ConfigDataEnvironmentPostProcessor` / `org.springframework.context.support.AbstractApplicationContext#refresh`
    - 推荐 Lab：`BootBasicsDefaultLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 4 章：00. 深挖导读](004-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 4 章：02：断点地图](004-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 最短调用链（你要能复述）

1. `SpringApplication#run`
2. 准备 Environment（加载配置、激活 profiles、合并 property sources）
3. 创建 ApplicationContext（选择 context 类型）
4. `refresh()`：容器初始化（定义层 → 实例层）
5. 发布启动完成事件（应用可用）

证据链入口：

- `BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBasicsDefaultLabTest`
- Lab：`BootBasicsDevLabTest`
- Lab：`BootBasicsOverrideLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](004-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](004-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
