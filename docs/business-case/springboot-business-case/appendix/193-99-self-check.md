# 第 193 章：99 - Self Check（springboot-business-case）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（springboot-business-case）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用端到端链路把 Web/Validation/Security/AOP/Tx/JPA/Events 串起来：遇到红测/异常时，先定位“哪个边界没生效”，再回到对应模块主线。
    - 原理：一次业务请求贯穿：MVC 入参→安全边界→事务边界→持久化上下文→事件时机→可观测信号；排障的关键是把问题归类到具体边界。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.data.jpa.repository.support.SimpleJpaRepository`
    - 推荐 Lab：`BootBusinessCaseLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 192 章：90 - Common Pitfalls（springboot-business-case）](192-90-common-pitfalls.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[Book TOC](../../../book/index.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :springboot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :springboot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/190-02-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/190-04-branch-decision-matrix.md)

- 本章主题：**99 - Self Check（springboot-business-case）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`

## 机制主线

- （本章主线内容暂以契约骨架兜底；建议结合源码与测试用例补齐主线解释。）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章未显式引用 LabTest，先注入模块默认 LabTest 作为“合规兜底入口”（后续可逐章细化）。
- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`
- 建议命令：`mvn -pl :springboot-business-case test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 对应 Exercise（可运行）

- `BootBusinessCaseExerciseTest`

## 常见坑与边界


## 自测题
1. 业务流里有哪些天然的边界（输入校验/领域状态变更/事件发布/横切）？
2. 如果事件监听器抛异常，应该在什么位置处理最合理？为什么？

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`
- Exercise：`BootBusinessCaseExerciseTest`

上一章：[appendix/90-common-pitfalls.md](192-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
