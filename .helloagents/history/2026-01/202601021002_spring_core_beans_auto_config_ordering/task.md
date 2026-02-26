# Task List: spring-core-beans 自动装配顺序与条件“可复现”补齐

Directory: `helloagents/plan/202601021002_spring_core_beans_auto_config_ordering/`

---

## 1. spring-core-beans
- [√] 1.1 新增自动配置顺序 Lab：`SpringCoreBeansAutoConfigurationOrderingLabTest`（顺序敏感对照 + after/before 确定化），并输出最小 `OBSERVE:` 观察点
- [√] 1.2 补齐 `matchIfMissing` 的可断言复现：扩展 `SpringCoreBeansConditionEvaluationReportLabTest`（missing/false/true 三态）
- [√] 1.3 更新 `docs/10-spring-boot-auto-configuration.md`：把新增面试点落到正文对应小节（题目 + 追问 + 本仓库 Lab/Test 入口 + 推荐断点）
- [√] 1.4 更新 `spring-core-beans/README.md`：补齐 Labs 索引表入口（包含 ConditionEvaluationReport / Auto-config ordering），并确保 docs 导航一致
- [√] 1.5 更新 `SpringCoreBeansAutoConfigurationExerciseTest`：把练习提示指向新增 Lab/已有最小复现

## 2. Security Check
- [√] 2.1 检查是否引入敏感信息/不安全代码（G9），并确认不需要新增依赖

## 3. Knowledge Base Sync
- [√] 3.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：补充本次变更记录
- [√] 3.2 更新 `helloagents/CHANGELOG.md` 与 `helloagents/history/index.md`
- [√] 3.3 将方案包迁移到 `helloagents/history/2026-01/202601021002_spring_core_beans_auto_config_ordering/`

## 4. Testing
- [√] 4.1 运行并确保通过：`mvn -pl spring-core-beans test`
