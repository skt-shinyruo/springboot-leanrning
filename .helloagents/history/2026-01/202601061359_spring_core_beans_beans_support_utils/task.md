# Task List: spring-core-beans beans.support（support 工具类）补齐

Directory: `helloagents/plan/202601061359_spring_core_beans_beans_support_utils/`

---

## 1. Docs：补齐 support 工具类小节
- [√] 1.1 扩写 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md`：新增 `org.springframework.beans.support` 小节（含运行入口与定位说明）

## 2. Lab：新增可断言闭环测试
- [√] 2.1 新增 `SpringCoreBeansBeansSupportUtilitiesLabTest`：覆盖 `ArgumentConvertingMethodInvoker`（含 custom editor）、`ResourceEditorRegistrar`、`PropertyComparator`、`MutableSortDefinition`、`PagedListHolder`

## 3. 索引：Appendix 95/96 可审计归零
- [√] 3.1 更新 `scripts/generate-spring-beans-public-api-index.py`：将 `org.springframework.beans.support` 标记为 core，并把主 Lab 指向新增 support utilities Lab
- [√] 3.2 运行 `python3 scripts/generate-spring-beans-public-api-index.py`：重新生成 Appendix 95/96，确认 Gap 为 0

## 4. Knowledge Base：同步变更记录
- [√] 4.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：补充本次补齐点与入口
- [√] 4.2 更新 `helloagents/CHANGELOG.md`：记录 beans.support 闭环补齐与索引归零
- [√] 4.3 迁移方案包到 `helloagents/history/YYYY-MM/` 并更新 `helloagents/history/index.md`

## 5. Quality Verification
- [√] 5.1 运行并通过：`mvn -pl spring-core-beans test`
