# 第 117 章：主线时间线：Spring Boot Async & Scheduling
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：Spring Boot Async & Scheduling
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootAsyncSchedulingLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 116 章：Async/Scheduling 主线](../../../book/116-async-scheduling-mainline.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 118 章：00 - Deep Dive Guide（springboot-async-scheduling）](118-00-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：@Async/@Scheduled 如何通过代理与调度器把“并发执行”织入方法调用，以及线程池/异常/自调用的边界。
    - 读完你应该能复述：**方法调用 → 代理拦截 → 提交到 Executor → 执行/异常处理** 这一条主线（以及 scheduling 的触发链）。
    - 推荐顺序：先读《深挖导读》→ 本章 → Part 01 顺读 → 附录排坑。

!!! example "建议先跑的 Lab（把时间线变成证据）"

    - Lab：`BootAsyncSchedulingLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Boot Async & Scheduling —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Boot Async & Scheduling」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- 异步能力依赖 AOP 代理：很多“不生效”的问题，本质是“没代理上”或“自调用绕过代理”。
- 调度是“按时间触发的入口”：问题常见于线程池饱和、异常吞掉、并发重入。

## 主线时间线（建议顺读）

1. 先建立心智模型：@Async 到底在代理链里做了什么
   - 阅读：[01. @Async 心智模型](../part-01-async-scheduling/119-01-async-proxy-mental-model.md)
2. 线程模型：Executor 选择、线程命名、上下文传递
   - 阅读：[02. Executor 与线程模型](../part-01-async-scheduling/120-02-executor-and-threading.md)
3. 异常处理：为什么你“看不到异常”，应该如何验证
   - 阅读：[03. 异常处理](../part-01-async-scheduling/121-03-exceptions.md)
4. 最常见坑：self-invocation（自调用）导致 @Async 不生效
   - 阅读：[04. self-invocation](../part-01-async-scheduling/122-04-self-invocation.md)
5. 调度主线：@Scheduled 的触发与执行边界
   - 阅读：[05. @Scheduled 基础](../part-01-async-scheduling/123-05-scheduling-basics.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](../appendix/124-90-common-pitfalls.md)
- 自检：[99-self-check.md](../appendix/125-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Async & Scheduling」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Boot Async & Scheduling」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootAsyncSchedulingLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingLabTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[深挖导读](118-00-deep-dive-guide.md)

<!-- BOOKIFY:END -->
