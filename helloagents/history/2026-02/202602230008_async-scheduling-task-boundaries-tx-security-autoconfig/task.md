# Task List: spring-boot-async-scheduling 加深（三）——任务边界（事务 / SecurityContext / RequestContext）与 Boot `spring.task.*` 自动装配

Directory: `helloagents/plan/202602230008_async-scheduling-task-boundaries-tx-security-autoconfig/`

---

## 1. Evidence-first Labs（tests-first）
- [√] 1.1 新增 `@Async × @Transactional` 边界 LabTest（默认不传播 / `@Async @Transactional` 事务在异步线程），verify why.md#requirement-async--transactional-事务边界可断言
- [√] 1.2 新增 SecurityContext 传播 LabTest（默认丢失 / Delegating* 修复 / 无泄漏断言），verify why.md#requirement-securitycontext--requestcontext-上下文传播与泄漏可复现
- [√] 1.3 新增 RequestContext 传播 LabTest（默认丢失 / TaskDecorator 修复 / 泄漏反例），verify why.md#requirement-securitycontext--requestcontext-上下文传播与泄漏可复现
- [√] 1.4 新增 Boot `spring.task.execution.*` / `spring.task.scheduling.*` 自动装配 LabTests（属性映射到 executor/scheduler + 与 `@EnableAsync/@EnableScheduling` 的交互），verify why.md#requirement-spring-boot-springtask-自动装配可解释可断言
- [√] 1.5 将新增 Labs 纳入 Branch Matrix（必要时扩展 Book Matrix），verify why.md#change-content

## 2. `src/main` 可运行示例（DemoRunner）
- [√] 2.1 新增 `BootAsyncSchedulingApplication` + `AsyncSchedulingDemoRunner`：输出 executor/scheduler 信息与三条边界对比结果，verify why.md#requirement-demrunner-可运行示例非-web
- [√] 2.2 视需要新增最小示例 Service（async/tx/security/request 观测），verify why.md#requirement-demrunner-可运行示例非-web

## 3. Docs 加深（内容，不做表面格式化）
- [√] 3.1 新增主线章节：`@Async × @Transactional`（事务边界与 AOP 顺序），并引用 1.1 证据入口
- [√] 3.2 新增主线章节：SecurityContext / RequestContext（默认丢失、修复策略、泄漏反例），并引用 1.2/1.3 证据入口
- [√] 3.3 新增主线章节：Boot `spring.task.*` 自动装配（默认 bean、属性映射、如何断言“@Async/@Scheduled 用的哪个”），并引用 1.4 证据入口
- [√] 3.4 更新 Pitfalls / Self-check / Branch Decision Matrix / Breakpoint Map：新增事务/安全/请求/自动装配分支与排障入口，verify why.md#change-content
- [√] 3.5 更新模块 README 与 docs/README（阅读顺序 + Labs 索引 + 可运行 demo 命令），verify why.md#change-content

## 4. Dependencies & Safety
- [√] 4.1 补齐最小依赖（spring-tx / spring-security-core / spring-web（test）），并确认不引入 web server，verify why.md#impact-scope
- [√] 4.2 安全检查：不引入敏感信息处理；确保 demo 输出不泄漏凭证，verify why.md#risk-assessment

## 5. Verification + Knowledge Base
- [√] 5.1 连续运行模块测试至少 3 次：`mvn -q -pl :spring-boot-async-scheduling test`
- [√] 5.2 同步更新知识库与变更记录，并迁移方案包到 history，verify why.md#impact-scope
