# 01. 主线时间线：springboot-logging
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕主线时间线：springboot-logging展开，主线可以概括为：日志系统在启动早期初始化；之后每条日志是否输出取决于：logger category 的有效级别 + appender/encoder 输出形态。

    本页是导航页。建议先跑 `BootLoggingLabTest` 固化“debug 级别是否生效”，再按“LoggingSystem 初始化 → logger level 决策”顺读。

    需要下探源码时，可以从 `org.springframework.boot.logging.LoggingSystem` / `org.slf4j.Logger` /（logback）`ch.qos.logback.classic.Logger` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 深挖导读：把“日志级别生效”落到源码与断点](02-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `BootLoggingLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：日志系统在启动早期初始化；之后每条日志是否输出取决于：logger category 的有效级别 + appender/encoder 输出形态。需要下探源码时，可以从 `org.springframework.boot.logging.LoggingSystem` / `org.slf4j.Logger` /（logback）`ch.qos.logback.classic.Logger` 这些入口切入。


## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBookMatrixLabTest test`

## 机制主线（需要建立的叙事）

1. **日志系统何时初始化？**（LoggingSystem + logging config）
2. **日志级别如何决策？**（category → effective level）
3. **如何把日志变成可断言信号？**（capture/appender）

## 小结与下一章

日志系统在启动早期初始化；之后每条日志是否输出取决于：logger category 的有效级别 + appender/encoder 输出形态。

下一章见：[第 200 章：00. 深挖导读](02-deep-dive-guide.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootLoggingLabTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md)

<!-- BOOKIFY:END -->
