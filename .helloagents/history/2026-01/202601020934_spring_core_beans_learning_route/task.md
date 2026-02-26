# 任务清单：spring-core-beans 学习路线与最小闭环（README + Labs/Exercises）

Directory: `helloagents/plan/202601020934_spring_core_beans_learning_route/`

---

## 1. spring-core-beans
- [√] 1.1 更新 `spring-core-beans/README.md`：新增 “Start Here（5 分钟闭环）” 小节（命令 + 观察点 + 推荐入口测试），验证：why.md#requirement-5-分钟完成一次运行--断言--解释的闭环
- [√] 1.2 更新 `spring-core-beans/README.md`：新增 “学习路线（入门→进阶→深挖）” 表格，把最小入门路线固定为 3 个入口（`SpringCoreBeansLabTest` / `SpringCoreBeansContainerLabTest` / `docs/00-deep-dive-guide.md`），并为每一层给出“你应该能解释清楚什么”，验证：why.md#requirement-学习路线清晰入门进阶深挖且每一层都有你应该能解释什么
- [√] 1.3 更新 `spring-core-beans/README.md`：新增 “容器主线（refresh call chain）一页纸” 小节：以 `AbstractApplicationContext#refresh` 为主线，把 BFPP → BPP → 实例化/注入/初始化/销毁 对应到本模块 docs 与 Labs，验证：why.md#requirement-refresh-主线一页纸--断点地图按阶段
- [√] 1.4 更新 `spring-core-beans/README.md`：新增 “运行态观察点（spring-boot:run）” 小节，把 `BeansDemoRunner` 输出字段映射到对应 docs 与 Labs/Exercises，验证：why.md#requirement-5-分钟完成一次运行--断言--解释的闭环
- [√] 1.5 更新 `docs/beans/spring-core-beans/00-deep-dive-guide.md`：补齐 “断点地图（按阶段）” 与 “从一个入口测试走完全链路” 示例（建议入口：`SpringCoreBeansBeanCreationTraceLabTest`），验证：why.md#requirement-refresh-主线一页纸--断点地图按阶段
- [√] 1.6 更新 `docs/beans/spring-core-beans/11-debugging-and-observability.md`：补齐 “IoC/DI 与生命周期 Debug Playbook”（每条 playbook 必须绑定一个最小复现入口测试类/方法），验证：why.md#requirement-refresh-主线一页纸--断点地图按阶段
- [√] 1.7 更新 `spring-core-beans/src/main/java/com/learning/springboot/springcorebeans/BeansDemoRunner.java`：增强结构化输出（固定 key 前缀，包含：active profiles、beanDefinitionCount、TextFormatter beans 列表、FormattingService 实际注入实现、prototype 行为、`@PostConstruct` 标记），验证：why.md#requirement-5-分钟完成一次运行--断言--解释的闭环
- [√] 1.8 在现有基础上补强“容器主线时间线”实验：优先通过 README 映射串联已有 Labs（`ContainerLabTest`/`BeanCreationTraceLabTest`/`LifecycleCallbackOrderLabTest`/`BootstrapInternalsLabTest`），仅当无法表达时才新增 Lab，验证：why.md#requirement-refresh-主线一页纸--断点地图按阶段
- [√] 1.9 新增 `spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/SpringCoreBeansInjectionAmbiguityLabTest.java`：复现多候选注入失败并用 `@Qualifier` 或 `@Primary` 修复（单主题、稳定断言），验证：why.md#requirement-di-注入歧义可复现可修复可对照
- [√] 1.10 更新 `spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/SpringCoreBeansExerciseTest.java`：新增与 1.9 对应的练习题（默认 `@Disabled`），引导学习者修复歧义并写断言，验证：why.md#requirement-di-注入歧义可复现可修复可对照
- [√] 1.11 更新 `spring-core-beans/README.md`：在 Labs/Exercises 索引中新增 “推荐先跑这几个（新手不迷路）” 子区块，并标记高优先级入口（≤6 个），验证：why.md#requirement-学习路线清晰入门进阶深挖且每一层都有你应该能解释什么
- [√] 1.12 更新 `spring-core-beans/README.md`：统一并补强 “概念 → 在本模块哪里能看见” 表格：至少覆盖（BeanDefinition vs instance vs proxy、BFPP vs BPP、注入解析/歧义、生命周期顺序），每行必须定位到具体 docs 与具体测试类/方法，验证：why.md#requirement-学习路线清晰入门进阶深挖且每一层都有你应该能解释什么
- [√] 1.13 模块内验证：运行 `mvn -pl spring-core-beans test`，确保 Labs/Exercises 稳定；若出现不稳定，优先调整为“事件/对象记录 + 断言”而不是依赖输出时序

## 2. 全局索引同步
- [√] 2.1 更新根 `README.md`：同步 spring-core-beans 的学习路线入口（至少 Start Here 的 3 个入口与命令），验证：why.md#requirement-学习路线清晰入门进阶深挖且每一层都有你应该能解释什么
- [√] 2.2 更新 `docs/progress.md`：同步 spring-core-beans 的最小打卡动作（建议以“跑哪些测试/读哪些 docs”为主），验证：why.md#requirement-5-分钟完成一次运行--断言--解释的闭环

## 3. Knowledge Base Sync
- [√] 3.1 同步更新 `helloagents/CHANGELOG.md`、`helloagents/wiki/modules/spring-core-beans.md`、`helloagents/history/index.md`
