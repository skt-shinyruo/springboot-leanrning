# 第 192 章：90 - Common Pitfalls（springboot-business-case）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Common Pitfalls（springboot-business-case）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用端到端链路把 Web/Validation/Security/AOP/Tx/JPA/Events 串起来：遇到红测/异常时，先定位“哪个边界没生效”，再回到对应模块主线。
    - 原理：一次业务请求贯穿：MVC 入参→安全边界→事务边界→持久化上下文→事件时机→可观测信号；排障的关键是把问题归类到具体边界。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.data.jpa.repository.support.SimpleJpaRepository`
    - 推荐 Lab：`BootBusinessCaseLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 191 章：01 - 架构与主流程（Business Case）](../part-01-business-case/191-01-architecture-and-flow.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 193 章：99 - Self Check（springboot-business-case）](193-99-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障模板（统一结构）

当你遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，建议按下面 6 步收敛（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：你看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :springboot-business-case -Dtest=BootBusinessCaseBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :springboot-business-case -Dtest=BootBusinessCaseBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[190-02-breakpoint-map.md](../part-00-guide/190-02-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[190-04-branch-decision-matrix.md](../part-00-guide/190-04-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[193-99-self-check.md](193-99-self-check.md)

- 本章主题：**90 - Common Pitfalls（springboot-business-case）**
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

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`
- 建议命令：`mvn -pl :springboot-business-case test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界


## 常见坑
1. 误把业务案例当成“只跑得起来的 demo”：缺乏断言与边界说明，容易失真
2. 异常传播路径不清晰：controller/service/listener/aspect 的责任边界混乱
3. 日志可观察性不足：看不到“谁触发了谁”，难以排障

## 对应 Lab（可运行）

- `BootBusinessCaseLabTest`
- `BootBusinessCaseServiceLabTest`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`

上一章：[part-01-business-case/01-architecture-and-flow.md](../part-01-business-case/191-01-architecture-and-flow.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](193-99-self-check.md)

<!-- BOOKIFY:END -->
