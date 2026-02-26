# Task List: spring-core-beans AOT/生产排障/面试体系补齐

Directory: `helloagents/history/2026-01/202601051507_spring_core_beans_aot_playbook/`

---

## 1. spring-core-beans（文档：Part 05 + Appendix + 索引一致性）

> 目标：新增内容必须“像书一样可连续阅读”，并能从 TOC/知识点地图 1–2 次跳转定位到对应 Lab。

- [√] 1.1 新增 Part 05 目录：`docs/beans/spring-core-beans/part-05-aot-and-real-world/`（目录结构），verify why.md#requirement-aotnative-心智模型与-runtimehints
- [√] 1.2 新增章节：`docs/beans/spring-core-beans/part-05-aot-and-real-world/40-aot-and-native-overview.md`（AOT/Native 约束总览 + 断点入口 + 复现入口），verify why.md#requirement-aotnative-心智模型与-runtimehints
- [√] 1.3 新增章节：`docs/beans/spring-core-beans/part-05-aot-and-real-world/02-runtimehints-basics.md`（RuntimeHints 心智模型 + 如何声明反射/代理需求），verify why.md#scenario-通过-aot-处理流程观察断言-runtimehints-的存在性
- [√] 1.4 新增章节：`docs/beans/spring-core-beans/part-05-aot-and-real-world/03-xml-bean-definition-reader.md`（XML → BeanDefinitionReader → BeanDefinition 元信息/错误分型），verify why.md#scenario-每个机制都有最小可运行实验与对应章节
- [√] 1.5 新增章节：`docs/beans/spring-core-beans/part-05-aot-and-real-world/04-autowirecapablebeanfactory-external-objects.md`（容器外对象注入与生命周期托管边界），verify why.md#scenario-每个机制都有最小可运行实验与对应章节
- [√] 1.6 新增章节：`docs/beans/spring-core-beans/part-05-aot-and-real-world/05-spel-and-value-expression.md`（SpEL 与 `@Value("#{...}")` 的解析链路与断点），verify why.md#scenario-每个机制都有最小可运行实验与对应章节
- [√] 1.7 新增章节：`docs/beans/spring-core-beans/part-05-aot-and-real-world/06-custom-qualifier-meta-annotation.md`（自定义 Qualifier/meta-annotation 与候选收敛），verify why.md#scenario-每个机制都有最小可运行实验与对应章节
- [√] 1.8 更新 Docs TOC：`docs/beans/spring-core-beans/README.md`（新增 Part 05 条目 + 新增 Appendix 条目链接），depends on 1.2–1.7
- [√] 1.9 新增 Appendix：`docs/beans/spring-core-beans/appendix/04-interview-playbook.md`（面试复述模板：决策树 → 测试 → 断点入口），verify why.md#requirement-面试体系化复述模板可背诵但可证明
- [√] 1.10 新增 Appendix：`docs/beans/spring-core-beans/appendix/05-production-troubleshooting-checklist.md`（生产排障清单：异常分型 → 入口 → 观察点 → 修复策略），verify why.md#requirement-生产排障-sop从异常到根因
- [√] 1.11 更新知识点地图：`docs/beans/spring-core-beans/appendix/03-knowledge-map.md`（补齐 Part 05 与两份 Appendix 的索引映射），depends on 1.2–1.10
- [√] 1.12 更新术语表：`docs/beans/spring-core-beans/appendix/02-glossary.md`（补齐 AOT/RuntimeHints/SpEL/AutowireCapableBeanFactory 相关条目），depends on 1.2–1.7
- [√] 1.13 更新模块 README 导航：`spring-core-beans/README.md`（推荐阅读顺序增加 Part 05；新增 Labs 索引入口），depends on 1.2–1.11

## 2. spring-core-beans（Labs：AOT/RuntimeHints）

> 目标：用 JVM 单测验证 AOT 的“构建期契约”（hints 的存在性/声明方式），不在 CI 中构建 native image。

- [√] 2.1 新增 AOT/RuntimeHints Lab 测试类：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAotRuntimeHintsLabTest.java`（仅骨架/最小用例），verify why.md#scenario-通过-aot-处理流程观察断言-runtimehints-的存在性
- [√] 2.2 在 Lab 中实现 “无 hints vs 有 hints” 对照断言：以 `RuntimeHintsPredicates`（或同等能力）验证 reflection/proxy/资源等 hints 的命中情况，depends on 2.1
- [√] 2.3 在 AOT/RuntimeHints 章节中补齐“断点入口/观察点/常见误区”：`docs/beans/spring-core-beans/part-05-aot-and-real-world/40-aot-and-native-overview.md`、`docs/beans/spring-core-beans/part-05-aot-and-real-world/02-runtimehints-basics.md`，depends on 2.1–2.2
- [√] 2.4 测试验证：`mvn -pl spring-core-beans -Dtest=SpringCoreBeansAotRuntimeHintsLabTest test`

## 3. spring-core-beans（Labs：XML / 外部对象 / SpEL / 自定义 Qualifier）

> 目标：每个机制都有“最小可运行 + 可断言 + 可下断点”的 Lab，并在 Part 05 对应章节给出复现入口与断点。

- [√] 3.1 新增 XML Lab 测试类：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansXmlBeanDefinitionReaderLabTest.java`
  - 断言点：XML 解析成功时 BeanDefinition 元信息可读；XML 非法时抛出 `BeanDefinitionStoreException`（错误分型）
  - verify why.md#scenario-每个机制都有最小可运行实验与对应章节
- [√] 3.2 文档补齐 XML 章节复现入口与断点：`docs/beans/spring-core-beans/part-05-aot-and-real-world/03-xml-bean-definition-reader.md`，depends on 3.1
- [√] 3.3 新增容器外对象 Lab 测试类：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansAutowireCapableBeanFactoryLabTest.java`
  - 断言点：容器外对象可被注入/执行初始化回调；对“不是容器管理的对象”哪些语义不会自动发生
  - verify why.md#scenario-每个机制都有最小可运行实验与对应章节
- [√] 3.4 文档补齐容器外对象章节复现入口与断点：`docs/beans/spring-core-beans/part-05-aot-and-real-world/04-autowirecapablebeanfactory-external-objects.md`，depends on 3.3
- [√] 3.5 新增 SpEL `@Value("#{...}")` Lab 测试类：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansSpelValueLabTest.java`
  - 断言点：SpEL 表达式解析发生在何处；与 `${...}` 占位符解析的链路差异
  - verify why.md#scenario-每个机制都有最小可运行实验与对应章节
- [√] 3.6 文档补齐 SpEL 章节复现入口与断点：`docs/beans/spring-core-beans/part-05-aot-and-real-world/05-spel-and-value-expression.md`，depends on 3.5
- [√] 3.7 新增自定义 Qualifier（meta-annotation）Lab 测试类：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part05_aot_and_real_world/SpringCoreBeansCustomQualifierLabTest.java`
  - 断言点：自定义限定符如何参与候选收敛；与 `@Qualifier("name")` 的匹配规则差异
  - verify why.md#scenario-每个机制都有最小可运行实验与对应章节
- [√] 3.8 文档补齐自定义 Qualifier 章节复现入口与断点：`docs/beans/spring-core-beans/part-05-aot-and-real-world/06-custom-qualifier-meta-annotation.md`，depends on 3.7
- [√] 3.9 分别单测验证：
  - `mvn -pl spring-core-beans -Dtest=SpringCoreBeansXmlBeanDefinitionReaderLabTest test`
  - `mvn -pl spring-core-beans -Dtest=SpringCoreBeansAutowireCapableBeanFactoryLabTest test`
  - `mvn -pl spring-core-beans -Dtest=SpringCoreBeansSpelValueLabTest test`
  - `mvn -pl spring-core-beans -Dtest=SpringCoreBeansCustomQualifierLabTest test`

## 4. Security Check

- [√] 4.1 执行安全检查（按 G9：避免敏感信息、避免危险命令示例、避免外部服务依赖）
  - [√] 4.1.1 检查新增示例/文档是否包含敏感信息（token/密码/生产域名）
  - [√] 4.1.2 检查新增示例是否引入高风险命令/不可逆操作（如 DROP/TRUNCATE/rm -rf）

## 5. Documentation Sync（Knowledge Base）

- [√] 5.1 同步更新知识库模块文档：`helloagents/wiki/modules/spring-core-beans.md`
- [√] 5.2 更新知识库变更记录：`helloagents/CHANGELOG.md`

## 6. Testing

- [√] 6.1 运行模块测试：`mvn -pl spring-core-beans test`
- [√] 6.2 冒烟检查：从 `docs/beans/spring-core-beans/README.md` 导航到新增章节/附录，确保链接不破（目录/上一章/下一章一致）
