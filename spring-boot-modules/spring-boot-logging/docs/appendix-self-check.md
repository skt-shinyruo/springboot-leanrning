# 99 自检：Spring Boot Logging
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（复盘出口）"

    - 主线入口：`BootLoggingBookMatrixLabTest`
    - 分支入口：`BootLoggingBranchMatrixLabTest`
    - 入口：`BootLoggingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[常见坑清单](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](guide-breakpoint-map.md)
- [关键分支矩阵](guide-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[appendix-common-pitfalls.md](appendix-common-pitfalls.md)

## 自检题

1. logger category 是什么？本模块用 `logging.level.com.learning.springboot.bootlogging=...` 命中的 category 到底是哪一层包名？
   - 证据入口：`BootLoggingLabTest#debugLogIsPrintedWhenLevelIsDebug`
2. effective level 是怎么得到的？为什么“设置了 DEBUG”但仍可能看不到 debug 输出？如何把它缩小成一个可断言的最小用例？
   - 证据入口：`BootLoggingLabTest#debugLogIsPrintedWhenLevelIsDebug`
3. 如何证明“日志是可断言的事实”，而不是只能靠肉眼看控制台？（至少说出 2 种方案）
   - 证据入口：`BootLoggingLabTest#debugLogIsPrintedWhenLevelIsDebug`（OutputCapture）/ `BootLoggingExerciseSolutionTest#solution_addMdcAndAssertItAppearsInLogs`（ListAppender）
4. MDC 是什么？如何把 `requestId` 放进 MDC，并验证它真的出现在 log event 的 MDC map 里？
   - 证据入口：`BootLoggingExerciseSolutionTest#solution_addMdcAndAssertItAppearsInLogs`
5. 为什么必须 `MDC.clear()`/`MDC.remove()`？如果线程复用而没清理，会出现什么类型的“跨请求串号”？
   - 证据入口：`BootLoggingConcurrencyLabTest#mdcIsThreadLocal_andDoesNotLeakAcrossThreads_underConcurrentUsage`
6. 如何用断言证明：MDC 本质是 ThreadLocal，不会自动跨线程传播？
   - 证据入口：`BootLoggingConcurrencyLabTest#mdcIsThreadLocal_andDoesNotLeakAcrossThreads_underConcurrentUsage`
7. 当发现“日志不出现”时，如何区分：代码没跑到 / category 不匹配 / level 不够？会按什么顺序收敛？
   - 对照：[`01-common-pitfalls.md`](appendix-common-pitfalls.md)
8. 练习：把 MDC 带入日志并固化断言（让“带 requestId 的日志”成为回归用例）。
   - 入口：`BootLoggingExerciseTest#exercise_addMdcAndAssertItAppearsInLogs`

## 退出条件（完成标准）

- 能用“category + effective level + appender/encoder”解释一条日志为什么出现/为什么不出现，并能写出最小断言用例。
- 能把 MDC 的使用写成规则：设置点、清理点、以及并发下的边界。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootLoggingLabTest`

上一章：[appendix-common-pitfalls.md](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)

<!-- BOOKIFY:END -->
