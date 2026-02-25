# 任务清单：完善 Spring Core 基础模块学习体验（Beans / AOP / Tx / Events）

Directory: `helloagents/plan/202601020725_enhance_spring_core_fundamentals/`

---

## 1. spring-core-beans
- [√] 1.1 更新 `spring-core-beans/README.md`：新增 “Start Here（5 分钟闭环）” 小节（命令 + 观察点 + 推荐入口测试），验证：why.md「Requirement: 初学者能在每个模块内快速找到…」/「Scenario: 只看 README 就能跑通并验证一个关键结论」
- [√] 1.2 更新 `spring-core-beans/README.md`：新增 “学习路线（入门→进阶→深挖）” 表格，把 **最小入门路线** 固定为 3 个入口（`SpringCoreBeansLabTest` / `SpringCoreBeansContainerLabTest` / `docs/00-deep-dive-guide.md`），并为每一层给出“你应该能解释清楚什么”
- [√] 1.3 更新 `spring-core-beans/README.md`：新增 “容器主线（refresh call chain）一页纸” 小节：以 `AbstractApplicationContext#refresh` 为主线，把 BFPP → BPP → 实例化/注入/初始化/销毁 对应到本模块 docs（`06/12/14/30/31/17`）与 Labs（`ContainerLabTest`/`BeanCreationTraceLabTest`/`LifecycleCallbackOrderLabTest`）
- [√] 1.4 更新 `spring-core-beans/README.md`：新增 “运行态观察点（spring-boot:run）” 小节，把 `BeansDemoRunner` 的关键输出（DI 选择、prototype 行为、生命周期标记等）映射到对应 docs 与 Labs/Exercises（同 1.1 场景验证）
- [√] 1.5 更新 `docs/beans/spring-core-beans/00-deep-dive-guide.md`：补齐 “断点地图（按阶段）” 与 “从一个入口测试走完全链路” 示例（建议入口：`SpringCoreBeansBeanCreationTraceLabTest` 或 `SpringCoreBeansBootstrapInternalsLabTest`）
- [√] 1.6 更新 `docs/beans/spring-core-beans/11-debugging-and-observability.md`：补齐 “IoC/DI 与生命周期 Debug Playbook”（推荐断点：`ConfigurationClassPostProcessor` / `PostProcessorRegistrationDelegate` / `AutowiredAnnotationBeanPostProcessor` / `CommonAnnotationBeanPostProcessor` 等，并给出最小复现入口测试类）
- [√] 1.7 更新 `spring-core-beans/src/main/java/com/learning/springboot/springcorebeans/BeansDemoRunner.java`：增强结构化输出（建议固定 key 前缀，包含：active profiles、beanDefinitionCount、`TextFormatter` beans 列表、FormattingService 实际注入实现、prototype 行为、`@PostConstruct` 标记）
- [√] 1.8 在**现有基础上优先补强**“容器主线时间线”实验：
  - 优先选择并扩展已有 Labs（例如 `SpringCoreBeansBeanCreationTraceLabTest` / `SpringCoreBeansContainerLabTest` / `SpringCoreBeansLifecycleCallbackOrderLabTest`）来覆盖与串联 **BFPP 改定义 → 注入阶段 → init 回调 → BPP 包装/替换**
  - 仅当现有 Labs 无法清晰表达“完整时间线”时，才新增一个新的 Lab（例如 `SpringCoreBeansContainerMainlineTimelineLabTest`），并保持单一主题 + 稳定断言
- [√] 1.9 更新 `spring-core-beans/README.md`：在 Labs/Exercises 索引中新增 “推荐先跑这几个（新手不迷路）” 子区块，并标记高优先级入口（避免 30+ Labs 让初学者无从下手）
- [√] 1.10 补强 “三层心智模型”入口实验（优先复用 `SpringCoreBeansContainerLabTest#beanDefinitionIsNotTheBeanInstance`）：新增断言/说明，强调 `BeanDefinition` / 原始实例 / 最终暴露对象（proxy/替身）三者可能不同，验证：why.md「Requirement: 建立 Spring 容器的“三层心智模型”…」
- [√] 1.11 补强 “注解为何能工作”入口实验（复用 `SpringCoreBeansBootstrapInternalsLabTest`）：在 README 的“Start Here/主线一页纸”中明确推荐该入口，并在 docs/00 给出断点路径（`AnnotationConfigUtils.registerAnnotationConfigProcessors` → `PostProcessorRegistrationDelegate` → `AutowiredAnnotationBeanPostProcessor`）
- [√] 1.12 新增一个“注入歧义最小复现” Lab：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/SpringCoreBeansInjectionAmbiguityLabTest.java`（仅 1 个知识点），复现多候选注入失败并用 `@Qualifier` 或 `@Primary` 修复，验证：why.md「Requirement: 能解释 DI 解析的选择规则…」
- [√] 1.13 统一并补强 README 中 “概念 → 在本模块哪里能看见” 表格：至少覆盖（BeanDefinition vs instance、BFPP vs BPP、注入解析/歧义、生命周期顺序），每行必须能定位到具体 docs 与具体测试类/方法
- [√] 1.14 增强 `spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/SpringCoreBeansExerciseTest.java`：新增 1 个与 1.12 对应的练习题（默认 `@Disabled`），引导学习者用不同手段修复歧义并写断言（`@Qualifier` vs `@Primary` 对比）
- [√] 1.15 模块内验证：运行 `mvn -pl spring-core-beans test`，确保新增/补强的 Labs 稳定；若出现不稳定，优先调整为“事件/对象记录 + 断言”而不是依赖输出时序
- [√] 1.16 把新增面试点“插入到正文对应小节”（禁止新建独立面试题汇总文档）：定义统一格式（题目/追问/复现入口），并在各章节中按主题落位（验证：why.md「Requirement: 面试导向知识点沉淀到正文…」）
- [√] 1.17 在 `docs/beans/spring-core-beans/01-bean-mental-model.md` 的 “BeanFactory vs ApplicationContext：责任边界” 小节**正文中**插入面试块：题目 + 追问（refresh 关联、能力边界）+ 复现入口（优先复用 `SpringCoreBeansBootstrapInternalsLabTest`；若不足则新增最小 Lab 并补链接）
- [√] 1.18 在 `docs/beans/spring-core-beans/03-dependency-injection-resolution.md` 的“候选收集/候选收敛”附近插入面试块：题目 + 追问（`findAutowireCandidates`/`determineAutowireCandidate`/泛型匹配）+ 复现入口（`SpringCoreBeansBeanGraphDebugLabTest`、`SpringCoreBeansAutowireCandidateSelectionLabTest`）
- [√] 1.19 在 `docs/beans/spring-core-beans/16-autowire-candidate-selection-primary-priority-order.md` 的“单注入 vs 集合注入规则差异”附近插入面试块：题目 + 追问（`@Order` 不能解决单注入歧义、集合排序）+ 复现入口（`SpringCoreBeansAutowireCandidateSelectionLabTest`）
- [√] 1.20 在 `docs/beans/spring-core-beans/05-lifecycle-and-callbacks.md` 的 Aware 小节**正文中**插入面试块：题目 + 追问（谁调用 Aware、发生在注入/初始化链路哪个阶段）+ 复现入口（`SpringCoreBeansLifecycleCallbackOrderLabTest`）
- [√] 1.21 在 `docs/beans/spring-core-beans/05-early-reference-and-circular.md` 的 early reference / 三级缓存解释段落中插入面试块：题目 + 追问（构造器 vs setter、三级缓存的“工厂”意义、代理为何让问题更复杂）+ 复现入口（`SpringCoreBeansEarlyReferenceLabTest`、`SpringCoreBeansContainerLabTest`）
- [√] 1.22 在 `docs/beans/spring-core-beans/14-proxying-phase-bpp-wraps-bean.md` 的“替换为 proxy 的阶段/影响”段落中插入面试块：题目 + 追问（JDK vs CGLIB、按实现类获取失败、定位哪个 BPP 替换对象）+ 复现入口（`SpringCoreBeansBeanCreationTraceLabTest`、`SpringCoreBeansProxyingPhaseLabTest`）
- [√] 1.23 若 1.17/1.21/1.22 出现“缺少可复现入口”的情况，新增最小 Lab（单主题、稳定断言）并在对应章节正文中引用该 Lab；新增 Lab 命名必须体现主题（例如 `...BeanFactoryVsApplicationContextLabTest` / `...ThreeLevelCacheLabTest` / `...ProxyTypePitfallsLabTest`）

## 2. spring-core-aop
- [√] 2.1 补齐/强化 `spring-core-aop/README.md` 的“运行态观察点 + Debug 路径”（对齐 why.md#核心场景）
- [√] 2.2 增强 `spring-core-aop/src/main/java/com/learning/springboot/springcoreaop/AopDemoRunner.java` 的结构化输出（展示代理类型、被拦截方法、self-invocation 现象）
- [√] 2.3 新增或补强 1 个 Labs/Exercise（保持断言稳定）：优先围绕“代理类型差异导致的可注入类型差异”或“多切面顺序的可断言验证”

## 3. spring-core-events
- [√] 3.1 补齐/强化 `spring-core-events/README.md` 的“推荐阅读顺序 + Labs/Exercises 索引 + 运行态观察点”
- [√] 3.2 增强 `spring-core-events/src/main/java/com/learning/springboot/springcoreevents/EventsDemoRunner.java` 的结构化输出（展示线程名、同步/异常传播的最小闭环）
- [√] 3.3 新增 2–3 个 Labs（以断言为主，避免时序不稳定）：
  - 事件默认同步（publisher 线程一致）
  - 监听器顺序（`@Order` 的确定性）
  - 异常传播（默认会回炸 publisher）
- [√] 3.4 完善 `spring-core-events/src/test/java/.../SpringCoreEventsExerciseTest.java`：引导学习者新增一个 listener（条件/顺序/异步任选其一）并写断言

## 4. spring-core-tx
- [√] 4.1 补齐/强化 `spring-core-tx/README.md` 的“运行态观察点 + 常见坑排查入口”
- [√] 4.2 增强 `spring-core-tx/src/main/java/com/learning/springboot/springcoretx/TxDemoRunner.java` 的结构化输出（展示事务活跃状态、回滚/提交前后数据变化）
- [√] 4.3 新增 2–3 个 Labs（以断言为主）：
  - checked exception 默认不回滚 + `rollbackFor` 对比
  - 传播行为（`REQUIRES_NEW` 的独立边界）
  - 自调用绕过代理导致事务不生效（最小复现 + 修复对比）
- [√] 4.4 完善 `spring-core-tx/src/test/java/.../SpringCoreTxExerciseTest.java`：引导学习者实现一个“修复自调用陷阱/回滚规则”的练习题

## 5. 全局索引同步
- [√] 5.1 更新根 `README.md`：机制线模块索引与新增/调整的 Labs/Exercises/运行态观察点保持一致
- [√] 5.2 更新 `docs/progress.md`：增加针对四个模块的“最小打卡动作”提示（包含运行态观察的可选项）

## 6. Security Check
- [√] 6.1 执行安全自检（G9）：确认不引入端点暴露、无敏感信息硬编码、无高风险命令/生产环境操作

## 7. Testing
- [√] 7.1 逐模块运行测试并修复失败：`mvn -pl spring-core-beans test` 等
- [√] 7.2 全仓库回归：`mvn -q test`

---

## 执行摘要（2026-01-02）

- 本次仅执行：`spring-core-beans` 的 1.15–1.23（面试点落位到正文对应小节 + 补齐复现入口 + 回归测试）。
- 其余任务在当时标记为 `[-]`，现已在后续变更中补齐并更新为 `[√]`（例如 `helloagents/history/2026-01/202601021322_complete_spring_core_fundamentals_remaining/`）。
