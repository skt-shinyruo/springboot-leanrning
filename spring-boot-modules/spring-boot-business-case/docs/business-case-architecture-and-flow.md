# 01. 01 - 架构与主流程（Business Case）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕架构与主流程（Business Case）展开，主线可以概括为：一次业务请求贯穿：MVC 入参→安全边界→事务边界→持久化上下文→事件时机→可观测信号；排障的关键是把问题归类到具体边界。

    阅读时可以先跑 `BootBusinessCaseLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用端到端链路把 Web/Validation/Security/AOP/Tx/JPA/Events 串起来：遇到红测/异常时，先定位“哪个边界没生效”，再回到对应模块主线。

    需要下探源码时，可以从 `org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.data.jpa.repository.support.SimpleJpaRepository` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Business Case](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[常见坑清单](appendix-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「01 - 架构与主流程（Business Case）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `BootBusinessCaseLabTest`（或文末“对应实验/测试”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootBusinessCaseLabTest`
    - 测试文件：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseLabTest.java`

## 机制主线

把业务主线按“边界”拆开，会更容易排障：

1. **Controller（输入边界）**：参数绑定 + `@Valid` 校验 → 决定 200 还是 400
2. **Service（事务边界）**：`@Transactional` → 决定能否回滚、回滚能否阻止落库
3. **Repository（持久化边界）**：写入是否真的发生（以 count/查询为证据）
4. **Events（副作用边界）**：
   - 同步 listener：发布即执行（回滚也可能已经执行）
   - afterCommit listener：只在 commit 后执行（更适合“最终落库后的副作用”）
5. **AOP（横切边界）**：调用链记录/审计 → 需要代理参与
6. **Exception shaping（对外契约边界）**：内部异常 → 统一响应结构（避免泄漏细节）

## 最小可运行实验（Lab）

- Lab：`BootBusinessCaseLabTest`
- 运行命令：`mvn -pl :spring-boot-business-case test`（或在 IDE 直接运行上面的测试类）

## 最小可复现入口
- `BootBusinessCaseLabTest`：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseLabTest.java`

## 常见坑与边界

### 坑点 1：把“事件副作用”当成“事务的一部分”，导致回滚也产生副作用

事务回滚后数据库没有落库，但仍看到审计/通知等副作用发生

同步事件监听器与事务生命周期无关；它在 publish 时立即执行

- 回滚时同步 listener 仍执行：`BootBusinessCaseLabTest#syncListenerRunsEvenWhenTransactionRollsBack_butAfterCommitDoesNot`
- 成功时 afterCommit listener 才执行：`BootBusinessCaseLabTest#afterCommitListenerRunsOnSuccess`

需要“只在成功提交后才执行”的副作用，用 `@TransactionalEventListener(phase = AFTER_COMMIT)`；同步 listener 只放“允许回滚也执行”的逻辑（或仅做记录/打点）

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootBusinessCaseLabTest`
- 测试文件：`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseLabTest.java`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[appendix-common-pitfalls.md](appendix-common-pitfalls.md)

<!-- BOOKIFY:END -->
