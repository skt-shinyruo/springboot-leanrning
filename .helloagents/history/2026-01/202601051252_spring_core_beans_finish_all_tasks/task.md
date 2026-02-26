# Task List: spring-core-beans 全任务收尾（Docs 一致性 + 闭环补齐）

Directory: `helloagents/plan/202601051252_spring_core_beans_finish_all_tasks/`

---

## 1. Docs 一致性收尾（导航 + 入口标准化）
- [√] 1.1 为 `docs/beans/spring-core-beans/**.md` 全量补齐“上一章｜目录｜下一章”导航，并确保相对链接正确（0 断链）
  - 交付物：已脚本化生成并写入/替换全部章节的导航行（42/42）
- [√] 1.2 补齐缺少“复现入口/推荐断点/观察点/运行命令”的章节：至少覆盖 Part01/03/04 的章节页与 appendix/90/99
  - 交付物：已为 docs 全量补齐统一的 `## 0. 复现入口（可运行）` 入口块（25/25 缺口全部补齐）
- [√] 1.3 校准新章节 36/37：补齐推荐断点与复现入口，并与已有 Lab/坑点互链
  - 交付物：已在 36/37 章节补齐“复现入口 + 推荐断点/条件断点模板/watch list”

## 2. Labs 补齐（JSR-330）
- [√] 2.1 新增 Lab：JSR-330 `@Inject`/`Provider<T>`（与 `@Autowired`/`ObjectProvider` 对照），并在 DI 文档中落位入口
  - 交付物：
    - 新增：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansJsr330InjectionLabTest.java`
    - 文档落位：`docs/beans/spring-core-beans/part-01-ioc-container/03-dependency-injection-resolution.md`（新增 4.2 小节）
    - 依赖补齐：`spring-core-beans/pom.xml`（test scope 引入 `jakarta.inject:jakarta.inject-api`）

## 3. testsupport 增强（排障可观察性）
- [√] 3.1 增强 `spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/testsupport/BeanGraphDumper.java`：补齐候选集合/依赖边/销毁顺序提示等输出
  - 交付物：补齐 candidates 元信息输出（scope/primary/autowireCandidate/type/origin hint），并输出 dependenciesForBean/dependentBeans/dependsOn 提示
- [√] 3.2 增强 `spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/testsupport/BeanDefinitionOriginDumper.java`：补齐来源追踪输出结构，便于对照 docs/11 的排障路线
  - 交付物：结构化输出 resourceDescription/source/factoryBeanName/factoryMethodName/originatingBeanDefinition 等关键信息

## 4. Security Check
- [√] 4.1 执行安全自检（G9）：确保无敏感信息、无高风险命令、无生产环境操作暗示
  - 交付物：已对 helloagents/ 与 spring-core-beans/ 范围做敏感信息关键字扫描，未发现密钥/私钥/生产环境操作暗示

## 5. Documentation Update（Knowledge Base 同步）
- [√] 5.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：同步本次新增/变更要点与历史索引
  - 交付物：已更新 Highlights/Change History（包含本次方案包索引）
- [√] 5.2 更新 `helloagents/CHANGELOG.md`：记录本次 docs/Labs/tests support 增强
  - 交付物：已在 `[Unreleased]` 补充本次变更摘要（docs 导航/复现入口/JSR-330 Lab/testsupport）

## 6. Testing
- [√] 6.1 运行 `mvn -pl spring-core-beans test`
  - 结果：BUILD SUCCESS
- [√] 6.2 方法级抽查关键 Labs（至少 5 个方法/或按类级），确保文档所指向的实验可复现
  - 结果：已按方法级抽查并通过（候选选择/ordering/early reference/proxying/value placeholder 各 1 个方法）
- [√] 6.3 全量抽查 docs 相对链接：0 断链
  - 结果：已执行 docs 相对链接存在性检查（0 missing targets）

## 7. Migrate Solution Package
- [√] 7.1 迁移方案包到 `helloagents/history/2026-01/202601051252_spring_core_beans_finish_all_tasks/` 并更新 `helloagents/history/index.md`
