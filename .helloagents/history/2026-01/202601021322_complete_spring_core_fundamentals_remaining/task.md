# Task List: 补齐 Spring Core Fundamentals 剩余任务（AOP / Events / Tx）

Directory: `helloagents/plan/202601021322_complete_spring_core_fundamentals_remaining/`

---

## 1. spring-core-aop
- [√] 1.1 增强 `spring-core-aop/src/main/java/com/learning/springboot/springcoreaop/AopDemoRunner.java`：输出代理类型、被拦截方法、自调用现象（固定 `AOP:` 前缀），验证 why.md#requirement-运行态输出可作为稳定观察点-scenario-aop-代理与自调用现象可被观察

## 2. spring-core-events
- [√] 2.1 增强 `spring-core-events/src/main/java/com/learning/springboot/springcoreevents/EventsDemoRunner.java`：输出线程名 + 异常传播最小闭环（固定 `EVENTS:` 前缀），验证 why.md#requirement-运行态输出可作为稳定观察点-scenario-events-的同步线程与异常传播可被观察
- [√] 2.2 增补一个“按特定输入触发的 throwing listener”（不影响正常注册场景），验证 why.md#requirement-运行态输出可作为稳定观察点-scenario-events-的同步线程与异常传播可被观察

## 3. spring-core-tx
- [√] 3.1 增强 `spring-core-tx/src/main/java/com/learning/springboot/springcoretx/TxDemoRunner.java`：输出事务活跃状态 + 回滚/提交数据变化（固定 `TX:` 前缀），验证 why.md#requirement-运行态输出可作为稳定观察点-scenario-tx-的事务活跃状态与回滚提交差异可被观察
- [√] 3.2 新增 Lab：`spring-core-tx/src/test/java/com/learning/springboot/springcoretx/SpringCoreTxSelfInvocationPitfallLabTest.java`（自调用绕过事务 + 拆分 bean 修复对比），验证 why.md#requirement-tx-自调用陷阱可稳定复现并对比修复-scenario-自调用导致以为有事务实际没有事务
- [√] 3.3 同步更新 `spring-core-tx/README.md` 与根 `README.md` 的 Labs 索引（如新增 Lab 需要出现在索引中），验证 why.md#requirement-progress-清单覆盖四模块最小闭环-scenario-学习者按清单可完成跑通-断言-可选运行态观察

## 4. Documentation Update
- [√] 4.1 更新 `docs/progress.md`：补齐 `spring-core-aop` / `spring-core-events` / `spring-core-tx` 的“最小打卡动作”（含可选 `spring-boot:run` 观察点），验证 why.md#requirement-progress-清单覆盖四模块最小闭环-scenario-学习者按清单可完成跑通-断言-可选运行态观察
- [√] 4.2 同步更新 helloagents 知识库：`helloagents/wiki/modules/spring-core-aop.md`、`helloagents/wiki/modules/spring-core-events.md`、`helloagents/wiki/modules/spring-core-tx.md`

## 5. Security Check
- [√] 5.1 执行安全自检（G9）：确认不引入端点暴露、无敏感信息硬编码、无高风险命令/生产环境操作

## 6. Testing
- [√] 6.1 逐模块运行测试：`mvn -pl spring-core-aop test`、`mvn -pl spring-core-events test`、`mvn -pl spring-core-tx test`
- [√] 6.2 全仓库回归：`mvn -q test`
