# 第 190 章：00 - Deep Dive Guide（springboot-business-case）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Deep Dive Guide（springboot-business-case）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用端到端链路把 Web/Validation/Security/AOP/Tx/JPA/Events 串起来：遇到红测/异常时，先定位“哪个边界没生效”，再回到对应模块主线。
    - 原理：一次业务请求贯穿：MVC 入参→安全边界→事务边界→持久化上下文→事件时机→可观测信号；排障的关键是把问题归类到具体边界。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.data.jpa.repository.support.SimpleJpaRepository`
    - 推荐 Lab：`BootBusinessCaseLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 189 章：主线时间线：Business Case（综合案例）](189-03-mainline-timeline.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 191 章：01 - 架构与主流程（Business Case）](../part-01-business-case/191-01-architecture-and-flow.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**00 - Deep Dive Guide（springboot-business-case）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`

## 机制主线

本模块把“真实业务流”拆成一条可验证主线：**请求 → 校验 → 事务 → 事件 → AOP → 异常塑形 → 回滚边界**。

你要学到的不是某个注解的用法，而是遇到问题时能用分流判断：

- 这是 **校验没挡住**？还是 **事务没生效**？还是 **事件时机不对**？
- 这是 **回滚失败导致落库**？还是 **异常被转换导致看不到原始原因**？

### 1) 时间线：一次下单请求从进来到落库/回滚

把“创建订单”主线按时间线拆开：

1. **HTTP 请求进入**
   - Controller 负责做参数绑定与触发校验（`@Valid`）。
2. **校验阶段（Validation）**
   - 校验失败：直接 400 返回，**不进入事务、不落库**。
3. **业务阶段（Service）**
   - Service 方法是“业务边界”，通常在这里标注 `@Transactional`。
4. **持久化阶段（Repository）**
   - 保存订单，进入同一个事务。
5. **事件阶段（Events）**
   - 同步 listener：发布时立即执行（不等 commit）
   - 事务事件 listener：在 commit/rollback 的特定阶段触发（如 afterCommit）
6. **切面阶段（AOP）**
   - 记录调用链/打点等横切逻辑，验证“代理是否存在 + advice 是否执行”。
7. **异常塑形（API Error Shaping）**
   - 对外返回统一错误结构（避免把内部异常细节直接透出）。
8. **回滚边界（Rollback）**
   - 发生异常：事务回滚 → 不落库；但同步事件可能已经执行

### 2) 关键参与者（你应该能把它们连成一条链）

- Web/MVC：Controller、请求 DTO、`@Valid`（触发 bean validation）
- Tx：`@Transactional`（事务边界），TransactionInterceptor（代理执行入口）
- Data：Repository（持久化边界）
- Events：
  - 同步：`ApplicationEventPublisher` / `ApplicationEventMulticaster`
  - 事务：`@TransactionalEventListener`（afterCommit/afterRollback 等阶段）
- AOP：`@Aspect` + AOP proxy（调用链/审计记录）
- Exception：全局异常处理（把异常转换为稳定响应）

### 3) 本模块的关键分支（2–5 条，默认可回归）

1. **校验失败时：400 + 不落库 + 不产生审计事件**
   - 验证：`BootBusinessCaseLabTest#returnsValidationErrorWhenRequestIsInvalid`
2. **事务回滚：异常发生时不落库**
   - 验证：`BootBusinessCaseLabTest#rollbackPreventsPersistenceOnFailure`
3. **事件时机：回滚时同步 listener 会执行，但 afterCommit 不会**
   - 验证：`BootBusinessCaseLabTest#syncListenerRunsEvenWhenTransactionRollsBack_butAfterCommitDoesNot`
4. **AOP 证据链：service 是代理 + aspect 记录调用链**
   - 验证：`BootBusinessCaseLabTest#serviceBeanIsAnAopProxy` / `BootBusinessCaseLabTest#aspectRecordsInvocationForTracedOperation`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。
  
建议断点（从“请求现象”反推到“机制分支”）：

- 校验是否真的发生：
  - 你的 Controller 入参绑定点（DTO 生成处）与异常处理入口（400 返回点）
- 事务是否真的生效（代理是否参与）：
  - `org.springframework.transaction.interceptor.TransactionInterceptor#invoke`
- 事件发布与监听触发：
  - `org.springframework.context.event.SimpleApplicationEventMulticaster#multicastEvent`
  - `org.springframework.transaction.event.TransactionalApplicationListenerMethodAdapter#onApplicationEvent`
- AOP 调用链：
  - 你的 `@Aspect` advice 方法（观察入参与返回/异常）

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`
- 建议命令：`mvn -pl :springboot-business-case test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 推荐学习目标
1. 通过“一个真实业务流”理解基础设施如何协作（controller → service → event → aspect）
2. 学会用 tests 做端到端断言，避免只看日志“感觉对了”
3. 学会定位：异常是在哪个边界被转换/传播/吞掉的

## 如何跑实验
- 运行本模块测试：`mvn -pl :springboot-business-case test`

## 对应 Lab（可运行）

- `BootBusinessCaseLabTest`
- `BootBusinessCaseServiceLabTest`
- `BootBusinessCaseExerciseTest`

## 常见坑与边界

- （本章坑点待补齐：建议先跑一次 E，再回看断言失败场景与边界条件。）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`
- Exercise：`BootBusinessCaseExerciseTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-business-case/01-architecture-and-flow.md](../part-01-business-case/191-01-architecture-and-flow.md)

<!-- BOOKIFY:END -->
