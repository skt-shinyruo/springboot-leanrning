# 第 118 章：02：断点地图（Async & Scheduling Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Async & Scheduling Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootAsyncSchedulingLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 118 章：00 - Deep Dive Guide（springboot-async-scheduling）](118-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 118 章：04：关键分支矩阵（Branch Decision Matrix）](118-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Async & Scheduling Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

- 本章目标：把异步/调度最关键的分支（Enable、代理、自调用、异常）收敛为断点清单与 Watchpoints。
- 常见误区：看到 `@Async` 就以为会线程切换；看到 `@Scheduled` 就以为会触发（实际需要 enable + 代理链路）。

## 运行入口（建议先跑）

- Book Matrix：`BootAsyncSchedulingBookMatrixLabTest`
- Branch Matrix：`BootAsyncSchedulingBranchMatrixLabTest`

推荐命令：

- `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBranchMatrixLabTest test`

## 入口断点（先证明“是不是代理”）

- `org.springframework.aop.support.AopUtils#isAopProxy`（在测试断言处观察）
- `org.springframework.aop.framework.CglibAopProxy#intercept`（确认调用是否经过代理）

## @Async 断点（线程切换的决定点）

- `org.springframework.aop.interceptor.AsyncExecutionInterceptor#invoke`
- `org.springframework.aop.interceptor.AsyncExecutionAspectSupport#doSubmit`

## @Scheduled 断点（是否注册/是否触发）

- `org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor#processScheduled`
- `org.springframework.scheduling.support.ScheduledMethodRunnable#run`

## Watchpoints（建议）

- 当前线程名：`Thread.currentThread().getName()`
- 是否代理：`AopUtils.isAopProxy(bean)`
- `@EnableAsync/@EnableScheduling` 是否存在（从配置类/BeanDefinition 反推）
- 自调用场景：同一个 bean 内部调用是否绕过 proxy（观察调用栈是否进入 CglibAopProxy）

## 排障入口（Playbook）

- 常见坑：[`../appendix/124-90-common-pitfalls.md`](../appendix/124-90-common-pitfalls.md)
- 自检：[`../appendix/125-99-self-check.md`](../appendix/125-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingLabTest` / `BootAsyncSchedulingBookMatrixLabTest` / `BootAsyncSchedulingBranchMatrixLabTest`

上一章：[@Scheduled 基础](../part-01-async-scheduling/123-05-scheduling-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[118-04-branch-decision-matrix.md](118-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
