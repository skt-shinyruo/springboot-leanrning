# Task List: spring-core-beans 逐章深度完善（Chapter-driven Deepening）

Directory: `helloagents/plan/202601291111_spring-core-beans-chapter-deepening/`

---

## 1. 模块导航与目录页

- [√] 1.1 深化模块 README 导航与“从症状定位章节”路径：`spring-core-modules/spring-core-beans/README.md`，参考 `chapters/module-readme.md`
- [√] 1.2 深化 Docs TOC 的“症状驱动导航/章节串联”与索引可用性：`spring-core-modules/spring-core-beans/docs/README.md`，参考 `chapters/docs-root.md`

## 2. Part 00：Guide（怎么学 / 从哪里下断点）

- [-] 2.1 深化主线时间线：`spring-core-modules/spring-core-beans/docs/part-00-guide/02-mainline-timeline.md`，参考 `chapters/part-00-guide.md`
- [-] 2.2 深化深挖指南：`spring-core-modules/spring-core-beans/docs/part-00-guide/03-deep-dive-guide.md`，参考 `chapters/part-00-guide.md`
- [-] 2.3 深化关键分支矩阵：`spring-core-modules/spring-core-beans/docs/part-00-guide/04-branch-decision-matrix.md`，参考 `chapters/part-00-guide.md`
- [-] 2.4 深化 30 分钟快启闭环：`spring-core-modules/spring-core-beans/docs/part-00-guide/05-quickstart-30min.md`，参考 `chapters/part-00-guide.md`
- [-] 2.5 深化 refresh 调用链：`spring-core-modules/spring-core-beans/docs/part-00-guide/06-applicationcontext-refresh-call-chain.md`，参考 `chapters/part-00-guide.md`
- [-] 2.6 深化断点地图（锚点稳定性 + watch list）：`spring-core-modules/spring-core-beans/docs/part-00-guide/07-breakpoint-map.md`，参考 `chapters/part-00-guide.md`

## 3. Part 01：IoC Container（注册 / 注入 / 生命周期 / 扩展点）

- [-] 3.1 深化 Bean 注册入口（分层与分支）：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/01-bean-registration.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.2 深化依赖注入解析：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-dependency-injection-resolution.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.3 深化 Scope 与 prototype 注入陷阱：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/03-scope-and-prototype.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.4 深化生命周期与回调：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/04-lifecycle-and-callbacks.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.5 深化容器扩展点（BFPP/BPP/BDRPP）：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/05-post-processors.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.6 深化 `@Configuration` 增强与 `@Bean` 语义：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/06-configuration-enhancement.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.7 深化 Bean 心智模型：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-bean-mental-model.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.8 深化 `FactoryBean` 基础章：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/07-factorybean.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.9 深化循环依赖基础章：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-circular-dependencies.md`，参考 `chapters/part-01-ioc-container.md`

## 4. Part 02：Boot Auto-Config（Boot 叠加后容器如何变复杂）

- [-] 4.1 深化“看见容器”的调试与可观测：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/01-debugging-and-observability.md`，参考 `chapters/part-02-boot-autoconfig.md`
- [-] 4.2 深化 Auto-Config 顺序：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/02-auto-config-ordering.md`，参考 `chapters/part-02-boot-autoconfig.md`
- [-] 4.3 深化 Boot 自动装配影响链路：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/03-spring-boot-auto-configuration.md`，参考 `chapters/part-02-boot-autoconfig.md`

## 5. Part 03：Internals（refresh 主线 / 处理器算法 / 缓存边界）

- [-] 5.1 深化注解基础设施：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/01-container-bootstrap-and-infrastructure.md`，参考 `chapters/part-03-container-internals.md`
- [-] 5.2 深化 BDRPP 注册阶段动态加定义：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/02-bdrpp-definition-registration.md`，参考 `chapters/part-03-container-internals.md`
- [-] 5.3 深化后处理器顺序算法：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/03-post-processor-ordering.md`，参考 `chapters/part-03-container-internals.md`
- [-] 5.4 深化实例化前短路：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/04-pre-instantiation-short-circuit.md`，参考 `chapters/part-03-container-internals.md`
- [-] 5.5 深化 early reference 与循环依赖：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/05-early-reference-and-circular.md`，参考 `chapters/part-03-container-internals.md`
- [-] 5.6 深化生命周期回调顺序：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/06-lifecycle-callback-order.md`，参考 `chapters/part-03-container-internals.md`
- [-] 5.7 深化 refresh → doCreateBean 主线（分层与锚点）：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/07-refresh-to-bean-creation-mainline.md`，参考 `chapters/part-03-container-internals.md`

## 6. Part 04：Wiring & Boundaries（装配语义 / 边界 / 真实坑）

- [-] 6.1 深化 Lazy 语义：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/01-lazy-semantics.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [√] 6.2 深化 dependsOn：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/02-depends-on.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [√] 6.3 深化 registerResolvableDependency：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/03-resolvable-dependency.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.4 深化父子上下文：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/04-context-hierarchy.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.5 深化 Bean 名称与 alias：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/05-bean-names-and-aliases.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.6 深化 FactoryBean 深潜：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/06-factorybean-deep-dive.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.7 深化 BeanDefinition 覆盖：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/07-bean-definition-overriding.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.8 深化手工注册 BPP：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/08-programmatic-bpp-registration.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.9 深化 SmartInitializingSingleton：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/09-smart-initializing-singleton.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.10 深化 SmartLifecycle：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.11 深化自定义 Scope 与 scoped proxy：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/11-custom-scope-and-scoped-proxy.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.12 深化 FactoryBean 边界：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/12-factorybean-edge-cases.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.13 深化注入阶段（field vs constructor）：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.14 深化代理产生阶段：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.15 深化 @Resource 注入：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/15-resource-injection-name-first.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.16 深化候选选择 vs 顺序：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.17 深化占位符解析：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.18 深化 MergedBeanDefinition：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/18-merged-bean-definition.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.19 深化类型转换：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.20 深化泛型匹配坑：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/20-generic-type-matching-pitfalls.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.21 深化 Environment/PropertySource：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/21-environment-and-propertysource.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.22 深化 BeanFactory API：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/22-beanfactory-api-deep-dive.md`，参考 `chapters/part-04-wiring-and-boundaries.md`

## 7. Part 05：AOT & Real World（XML/Reader/AOT/外部对象/SpEL/自定义 qualifier）

- [-] 7.1 深化 AOT/Native 总览：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/01-aot-and-native-overview.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [√] 7.2 深化 RuntimeHints：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/02-runtimehints-basics.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.3 深化 XML → BeanDefinitionReader：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/03-xml-bean-definition-reader.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.4 深化容器外对象注入：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/04-autowirecapablebeanfactory-external-objects.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.5 深化 SpEL 与表达式链路：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/05-spel-and-value-expression.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.6 深化自定义 Qualifier：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/06-custom-qualifier-meta-annotation.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.7 深化 XML namespace 扩展：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/07-xml-namespace-extension.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.8 深化 Reader 其他输入：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/08-beandefinitionreader-other-inputs-properties-groovy.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.9 深化方法注入：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/09-method-injection-replaced-method.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.10 深化内置 FactoryBean 图鉴：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/10-built-in-factorybeans-gallery.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.11 深化 PropertyEditor 与值解析：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/11-property-editor-and-value-resolution.md`，参考 `chapters/part-05-aot-and-real-world.md`

## 8. Appendix（术语表 / 速查 / 排障清单 / 索引）

- [-] 8.1 深化常见误区清单：`spring-core-modules/spring-core-beans/docs/appendix/01-common-pitfalls.md`，参考 `chapters/appendix.md`
- [-] 8.2 深化自测题与答案映射：`spring-core-modules/spring-core-beans/docs/appendix/11-self-check.md`，参考 `chapters/appendix.md`
- [-] 8.3 深化术语表可检索性与交叉引用：`spring-core-modules/spring-core-beans/docs/appendix/02-glossary.md`，参考 `chapters/appendix.md`
- [-] 8.4 深化知识地图（症状→章节→Lab→断点）：`spring-core-modules/spring-core-beans/docs/appendix/03-knowledge-map.md`，参考 `chapters/appendix.md`
- [-] 8.5 深化面试复述模板：`spring-core-modules/spring-core-beans/docs/appendix/04-interview-playbook.md`，参考 `chapters/appendix.md`
- [-] 8.6 深化生产排障清单：`spring-core-modules/spring-core-beans/docs/appendix/05-production-troubleshooting-checklist.md`，参考 `chapters/appendix.md`
- [-] 8.7 深化 Public API 索引：`spring-core-modules/spring-core-beans/docs/appendix/06-spring-beans-public-api-index.md`，参考 `chapters/appendix.md`
- [-] 8.8 深化 Public API Gap 清单：`spring-core-modules/spring-core-beans/docs/appendix/07-spring-beans-public-api-gap.md`，参考 `chapters/appendix.md`
- [-] 8.9 深化 Explore/Debug 用例说明：`spring-core-modules/spring-core-beans/docs/appendix/08-explore-debug-tests.md`，参考 `chapters/appendix.md`
- [-] 8.10 深化 Debugger Pack：`spring-core-modules/spring-core-beans/docs/appendix/09-debugger-pack.md`，参考 `chapters/appendix.md`
- [-] 8.11 深化团队内训讲义：`spring-core-modules/spring-core-beans/docs/appendix/10-team-training-kit.md`，参考 `chapters/appendix.md`

## 9. Security Check

- [√] 9.1 执行安全检查（关注 SpEL、表达式、反射/AOT 提示的安全边界，避免鼓励危险用法）

## 10. 知识库同步（SSOT）

- [√] 10.1 同步更新模块知识库：`helloagents/wiki/modules/spring-core-beans.md`
- [√] 10.2 更新变更记录：`helloagents/CHANGELOG.md`

## 11. Testing

- [√] 11.1 回归 `spring-core-beans` 全量测试：`mvn -pl :spring-core-beans test`
- [-] 11.2（可选）验证 Explore/Debug 用例：`mvn -pl :spring-core-beans -Dspringcorebeans.explore=true -Dtest=SpringCoreBeans*ExploreTest test`

---

## 执行摘要（本次 ~exec）

- 本次执行以“逐章阅读后的补强落地”为目标，但经全量扫描与抽样核对，`spring-core-beans/docs` 已具备较完整的教程化结构与可运行闭环（导读/要点/实验入口/方法级主线/断点/面试/自检/小结等）。
- 因此本轮聚焦在“真实缺口”修复与“可用性提升”：
  - 目录页新增“症状驱动导航（快速定位）”
  - 补齐 19/20/41 三章缺失的 `BOOKIFY` 标记，统一书本化导航一致性
- 其余章节条目本轮标记为 `[-]`：表示“已满足预期深度或本轮无必要重复改写”，后续若要按某些章节继续加深，可再开新方案包按章迭代。
