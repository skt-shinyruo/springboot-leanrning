# 第 191 章：01 - 架构与主流程（Business Case）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：架构与主流程（Business Case）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootBusinessCaseLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 190 章：00 - Deep Dive Guide（springboot-business-case）](../part-00-guide/190-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 192 章：90 - Common Pitfalls（springboot-business-case）](../appendix/192-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01 - 架构与主流程（Business Case）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootBusinessCaseLabTest`
    - Test file：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseLabTest.java`

## 机制主线

把业务主线按“边界”拆开，你会更容易排障：

1. **Controller（输入边界）**：参数绑定 + `@Valid` 校验 → 决定 200 还是 400
2. **Service（事务边界）**：`@Transactional` → 决定能否回滚、回滚能否阻止落库
3. **Repository（持久化边界）**：写入是否真的发生（以 count/查询为证据）
4. **Events（副作用边界）**：
   - 同步 listener：发布即执行（回滚也可能已经执行）
   - afterCommit listener：只在 commit 后执行（更适合“最终落库后的副作用”）
5. **AOP（横切边界）**：调用链记录/审计 → 需要代理参与
6. **Exception shaping（对外契约边界）**：内部异常 → 统一响应结构（避免泄漏细节）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootBusinessCaseLabTest`
- 建议命令：`mvn -pl :spring-boot-business-case test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 导读
本章把业务主线串起来，并明确每个边界点“应该观察什么、如何用测试复现”。

## 最小可复现入口
- `BootBusinessCaseLabTest`：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseLabTest.java`

## 常见坑与边界

### 坑点 1：把“事件副作用”当成“事务的一部分”，导致回滚也产生副作用

- Symptom：事务回滚后数据库没有落库，但你仍看到审计/通知等副作用发生
- Root Cause：同步事件监听器与事务生命周期无关；它在 publish 时立即执行
- Verification：
  - 回滚时同步 listener 仍执行：`BootBusinessCaseLabTest#syncListenerRunsEvenWhenTransactionRollsBack_butAfterCommitDoesNot`
  - 成功时 afterCommit listener 才执行：`BootBusinessCaseLabTest#afterCommitListenerRunsOnSuccess`
- Fix：需要“只在成功提交后才执行”的副作用，用 `@TransactionalEventListener(phase = AFTER_COMMIT)`；同步 listener 只放“允许回滚也执行”的逻辑（或仅做记录/打点）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBusinessCaseLabTest`
- Test file：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseLabTest.java`

上一章：[深挖导读](../part-00-guide/190-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[190-02-breakpoint-map.md](../part-00-guide/190-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
