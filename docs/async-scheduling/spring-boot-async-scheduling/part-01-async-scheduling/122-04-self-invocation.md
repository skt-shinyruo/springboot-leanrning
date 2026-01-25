# 第 122 章：04：self-invocation：为什么异步有时不生效
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：self-invocation：为什么异步有时不生效
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootAsyncSchedulingLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 121 章：03：异常传播：Future vs void](121-03-exceptions.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 123 章：05：`@Scheduled` 基础与可测试性](123-05-scheduling-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04：self-invocation：为什么异步有时不生效**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootAsyncSchedulingLabTest`

## 机制主线


## 你应该观察到什么

- 同类内部 `this.asyncMethod()` 调用会绕过代理 → 不切线程
- 通过另一个 bean 调用（走代理） → 能切线程

## 机制解释（Why）

都要求“调用路径必须经过代理”。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootAsyncSchedulingLabTest`
- 建议命令：`mvn -pl :spring-boot-async-scheduling test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 实验入口

<!-- BOOKLIKE-V2:EVIDENCE:START -->
实验入口已在章首提示框给出（先跑再读）。建议跑完后回到本章“证据链”逐条验证关键结论。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

## 常见坑与边界

这是 Spring 代理体系的通用坑：
- AOP
- `@Transactional`
- method validation
- method security
- `@Async`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAsyncSchedulingLabTest`

上一章：[异常处理](121-03-exceptions.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[@Scheduled 基础](123-05-scheduling-basics.md)

<!-- BOOKIFY:END -->
