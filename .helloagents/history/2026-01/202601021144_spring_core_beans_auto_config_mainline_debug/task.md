# Task List: spring-core-beans Boot 自动装配主线与排障闭环补齐

Directory: `helloagents/history/2026-01/202601021144_spring_core_beans_auto_config_mainline_debug/`

---

## 1. spring-core-beans
- [√] 1.1 新增导入/排序主线 Lab：`SpringCoreBeansAutoConfigurationImportOrderingLabTest`（after/before 排序 + 条件影响可断言）
- [√] 1.2 新增 Bean 来源追踪工具：`BeanDefinitionOriginDumper`（测试辅助类）+ 新增 Lab 演示用法
- [√] 1.3 新增覆盖/back-off 场景矩阵 Lab：`SpringCoreBeansAutoConfigurationOverrideMatrixLabTest`（重复候选→注入失败→两类修复）
- [√] 1.4 更新 `docs/10-spring-boot-auto-configuration.md`：补齐三块新增入口（题目 + 追问 + 复现入口 + 推荐断点）
- [√] 1.5 更新 `spring-core-beans/README.md`：Labs 索引与“概念→哪里能看见”表同步入口

## 2. Security Check
- [√] 2.1 检查是否引入敏感信息/不安全代码（G9），并确认不需要新增依赖

## 3. Testing
- [√] 3.1 运行并确保通过：`mvn -pl spring-core-beans test`

## 4. Knowledge Base Sync
- [√] 4.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：补充本次变更记录
- [√] 4.2 更新 `helloagents/CHANGELOG.md` 与 `helloagents/history/index.md`
- [√] 4.3 将方案包迁移到 `helloagents/history/2026-01/202601021144_spring_core_beans_auto_config_mainline_debug/`
