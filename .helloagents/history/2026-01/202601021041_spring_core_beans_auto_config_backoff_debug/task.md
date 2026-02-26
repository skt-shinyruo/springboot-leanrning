# Task List: spring-core-beans 自动装配 back-off/覆盖排障闭环

Directory: `helloagents/plan/202601021041_spring_core_beans_auto_config_backoff_debug/`

---

## 1. spring-core-beans
- [√] 1.1 新增 Lab：`SpringCoreBeansAutoConfigurationBackoffTimingLabTest`（late registrar 复现重复 bean、early registrar 触发 back-off）
- [√] 1.2 更新 `docs/10-spring-boot-auto-configuration.md`：在“覆盖”章节补齐面试点（题目 + 追问 + 入口 + 推荐断点）
- [√] 1.3 更新 `spring-core-beans/README.md`：Labs 索引表补齐入口

## 2. Security Check
- [√] 2.1 检查是否引入敏感信息/不安全代码（G9），并确认不需要新增依赖

## 3. Testing
- [√] 3.1 运行并确保通过：`mvn -pl spring-core-beans test`

## 4. Knowledge Base Sync
- [√] 4.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：补充本次变更记录
- [√] 4.2 更新 `helloagents/CHANGELOG.md` 与 `helloagents/history/index.md`
- [√] 4.3 将方案包迁移到 `helloagents/history/2026-01/202601021041_spring_core_beans_auto_config_backoff_debug/`
