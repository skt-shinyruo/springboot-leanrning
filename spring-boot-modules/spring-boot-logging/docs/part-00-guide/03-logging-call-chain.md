# 03. Logging 调用链（LoggingSystem 初始化与级别决策）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Logging 调用链（LoggingSystem 初始化与级别决策）
    - 怎么使用：先跑 `BootLoggingLabTest`，再用本文把“配置 → effective level → 输出”串成一条链。
    - 原理：启动期由 Boot 初始化 logging system；运行期每条日志的输出由 `logger.isXEnabled()` 决策。
    - 源码入口：`LoggingSystem` / `LoggerFactory` /（logback）`Logger#isDebugEnabled`
    - 推荐 Lab：`BootLoggingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖导读：把“日志级别生效”落到源码与断点](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 断点地图（Logging Debugger Pack）](04-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. Logging 调用链（LoggingSystem 初始化与级别决策）**
- 建议入口：优先运行 `BootLoggingLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：启动期由 Boot 初始化 logging system；运行期每条日志的输出由 `logger.isXEnabled()` 决策。
- 源码入口：`LoggingSystem` / `LoggerFactory` /（logback）`Logger#isDebugEnabled`



## 1. 启动期：LoggingSystem 什么时候初始化？

无需背所有细节，但要知道：

- logging 初始化发生得很早（早于大多数业务 bean）
- 因此很多“日志形态/级别”问题，根源来自启动期的配置加载

## 2. 运行期：为什么 debug 会/不会输出？

大多数 logger 会先做级别判断：

- `logger.isDebugEnabled()` 为 false → debug 日志直接短路（不会组装字符串）
- `logger.isDebugEnabled()` 为 true → 才会进入 appender 输出

因此“debug 没输出”的排障路径应当是：

1) category 是什么？（通常是类名）
2) effective level 是什么？
3) 输出端（appender/console）有没有被禁用/过滤？

## 3. 把日志变成证据链

- OutputCapture：适合教学闭环（本仓库的 `BootLoggingLabTest`）
- ListAppender：适合需要验证 MDC/结构化字段的场景（更精确）

## 小结与下一章

- 小结：启动期由 Boot 初始化 logging system；运行期每条日志的输出由 `logger.isXEnabled()` 决策。
- 下一章：[第 200 章：02：断点地图](04-breakpoint-map.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md)

<!-- BOOKIFY:END -->
