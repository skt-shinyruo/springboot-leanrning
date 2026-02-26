# Task List: spring-boot-async-scheduling 加深（二）：线程上下文传播（ThreadLocal/MDC）证据链

Directory: `helloagents/plan/202602222318_async-scheduling-context-propagation/`

---

## 1. Evidence-first Labs
- [√] 1.1 新增 context propagation LabTest：默认不传播 + TaskDecorator 修复 + 无泄漏断言，verify why.md#requirement-threadlocalmdc-跨线程的边界可复现#scenario-taskdecorator-修复--无泄漏证据链
- [√] 1.2 将新 Lab 纳入 Branch Matrix 聚合入口，verify why.md#change-content

## 2. Docs 加深（内容）
- [√] 2.1 在第 120 章补齐 ThreadLocal/MDC 机制边界与 TaskDecorator 策略，并引用 1.1 的证据入口，verify why.md#change-content
- [√] 2.2 在 Pitfalls 增加“上下文丢失/泄漏”坑位条目（含 Proof），verify why.md#change-content
- [√] 2.3 在 Self-check 增加“上下文传播”自测题与证据入口映射，verify why.md#change-content
- [√] 2.4 在 Branch Decision Matrix 增加分支行（上下文不传播/TaskDecorator 修复/泄漏风险），verify why.md#change-content
- [√] 2.5 在 Breakpoint Map 增加 TaskDecorator/执行器边界断点建议，verify why.md#change-content

## 3. Verification + Knowledge Base
- [√] 3.1 连续运行模块测试至少 3 次：`mvn -q -pl :spring-boot-async-scheduling test`
- [√] 3.2 更新知识库与变更记录，并迁移方案包到 history，verify why.md#impact-scope
