# Task List: spring-boot-async-scheduling 手册级内容加深（Async + Scheduling）

Directory: `helloagents/plan/202602221600_async-scheduling-handbook-deepen/`

---

## 1. spring-boot-async-scheduling（示例代码 + 可断言实验）
- [√] 1.1 新增 executor 选择矩阵 LabTest，并覆盖 `@Async("...")` / 默认选择规则，verify why.md#requirement-async-的机制证据链可复现#scenario-executor-选择优先级可断言
- [√] 1.2 新增 proxy 类型/可见性边界 LabTest，verify why.md#requirement-async-的机制证据链可复现
- [√] 1.3 新增 void async handler method+args 证据链 LabTest，verify why.md#requirement-async-的机制证据链可复现#scenario-void-异步异常可观测
- [√] 1.4 新增 `@Scheduled` 注册断言 LabTest（FixedRate/Delay/Cron），verify why.md#requirement-scheduled-的注册触发异常语义讲清楚#scenario-任务注册结果可确定性验证
- [√] 1.5 新增 `@Scheduled` 异常语义 LabTest（异常后是否继续调度/由谁处理），verify why.md#requirement-scheduled-的注册触发异常语义讲清楚#scenario-任务执行异常的后果可验证
- [√] 1.6 新增 `@Scheduled + @Async` 组合实验 LabTest（执行线程可观测），verify why.md#requirement-scheduled-的注册触发异常语义讲清楚#scenario-scheduled--async-组合的执行线程可观测
- [√] 1.7 调整 BookMatrix/BranchMatrix 聚合内容，纳入饱和拒绝等关键分支，verify why.md#change-content

## 2. spring-boot-async-scheduling（文档加深）
- [√] 2.1 加深 120 章 executor 选择与线程模型（含证据入口/源码锚点），verify why.md#requirement-async-的机制证据链可复现
- [√] 2.2 加深 121 章异常语义（Future.get vs join、void handler 观测），verify why.md#requirement-async-的机制证据链可复现
- [√] 2.3 加深 122 章 self-invocation（三种修法对比 + 对应实验），verify why.md#requirement-async-的机制证据链可复现
- [√] 2.4 加深 123 章 scheduling 注册/默认 scheduler/异常语义/组合注解，verify why.md#requirement-scheduled-的注册触发异常语义讲清楚
- [√] 2.5 重写 124 常见坑清单为“可复现排障手册”（每条坑带 Proof 入口），verify why.md#change-content
- [√] 2.6 更新 125 自检题为“看代码能回答 + 有证据入口”，verify why.md#change-content

## 3. 质量与回归
- [√] 3.1 连续运行模块测试至少 3 次确保无 flaky：`mvn -q -pl :spring-boot-async-scheduling test`，verify why.md#risk-assessment
- [√] 3.2 更新知识库（如存在对应模块页）并记录变更，verify why.md#impact-scope
