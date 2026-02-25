# Task List: spring-core-beans 内容级再加深（全章 A–E 维度）

Directory: `helloagents/plan/202601291213_spring-core-beans-content-deepen/`

---

任务状态：
- `[ ]` Pending
- `[√]` Completed
- `[X]` Failed
- `[-]` Skipped
- `[?]` To be confirmed

## 0. 输出“逐章内容级再加深策略”（对外可读）

- [√] 0.1 发布策略目录：`spring-core-modules/spring-core-beans/docs/deepening-strategies/README.md`
- [√] 0.2 发布各 Part/Appendix 策略清单：`spring-core-modules/spring-core-beans/docs/deepening-strategies/*.md`

## 1. 目录页与导航中枢（症状 → 章节 → 证据链）

- [√] 1.1 深化模块入口：`spring-core-modules/spring-core-beans/README.md`（症状入口/快速定位/推荐最短路径），参考 `chapters/module-readme.md`
- [√] 1.2 深化 Docs TOC：`spring-core-modules/spring-core-beans/docs/README.md`（症状驱动导航/跨章串联/工具型章节入口），参考 `chapters/docs-root.md`

## 2. Part 00：Guide（主线/分支/断点体系再下沉）

> Note: 本轮优先“产出逐章深化策略清单”（见 `spring-core-beans/docs/deepening-strategies/part-00-guide.md`），不重复堆叠改写已具备深度的正文；如需按策略进一步落地到章节正文，可另起 ~exec 执行。

- [-] 2.1 深化时间线：`spring-core-modules/spring-core-beans/docs/part-00-guide/02-mainline-timeline.md`，参考 `chapters/part-00-guide.md`
- [-] 2.2 深化深挖指南：`spring-core-modules/spring-core-beans/docs/part-00-guide/03-deep-dive-guide.md`，参考 `chapters/part-00-guide.md`
- [-] 2.3 深化分支矩阵：`spring-core-modules/spring-core-beans/docs/part-00-guide/04-branch-decision-matrix.md`，参考 `chapters/part-00-guide.md`
- [-] 2.4 深化 30min 闭环：`spring-core-modules/spring-core-beans/docs/part-00-guide/05-quickstart-30min.md`，参考 `chapters/part-00-guide.md`
- [-] 2.5 深化 refresh 调用链：`spring-core-modules/spring-core-beans/docs/part-00-guide/06-applicationcontext-refresh-call-chain.md`，参考 `chapters/part-00-guide.md`
- [-] 2.6 深化断点地图：`spring-core-modules/spring-core-beans/docs/part-00-guide/07-breakpoint-map.md`，参考 `chapters/part-00-guide.md`

## 3. Part 01：IoC Container（注册/注入/生命周期/扩展点：内容再下沉）

> Note: 本轮优先“产出逐章深化策略清单”（见 `spring-core-beans/docs/deepening-strategies/part-01-ioc-container.md`），不重复堆叠改写已具备深度的正文；如需按策略进一步落地到章节正文，可另起 ~exec 执行。

- [-] 3.1 深化注册入口：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/01-bean-registration.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.2 深化依赖注入解析：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-dependency-injection-resolution.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.3 深化 scope/prototype：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/03-scope-and-prototype.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.4 深化生命周期：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/04-lifecycle-and-callbacks.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.5 深化容器扩展点：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/05-post-processors.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.6 深化配置类增强：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/06-configuration-enhancement.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.7 深化心智模型：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-bean-mental-model.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.8 深化 FactoryBean 基础：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/07-factorybean.md`，参考 `chapters/part-01-ioc-container.md`
- [-] 3.9 深化循环依赖基础：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-circular-dependencies.md`，参考 `chapters/part-01-ioc-container.md`

## 4. Part 02：Boot Auto-Config（定义层复杂度提升：证据链/反例/排障）

> Note: 本轮优先“产出逐章深化策略清单”（见 `spring-core-beans/docs/deepening-strategies/part-02-boot-autoconfig.md`），不重复堆叠改写已具备深度的正文；如需按策略进一步落地到章节正文，可另起 ~exec 执行。

- [-] 4.1 深化“看见容器”：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/01-debugging-and-observability.md`，参考 `chapters/part-02-boot-autoconfig.md`
- [-] 4.2 深化 auto-config 顺序：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/02-auto-config-ordering.md`，参考 `chapters/part-02-boot-autoconfig.md`
- [-] 4.3 深化 auto-config 影响链：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/03-spring-boot-auto-configuration.md`，参考 `chapters/part-02-boot-autoconfig.md`

## 5. Part 03：Internals（refresh→doCreateBean：算法级再下沉）

> Note: 本轮优先“产出逐章深化策略清单”（见 `spring-core-beans/docs/deepening-strategies/part-03-container-internals.md`），不重复堆叠改写已具备深度的正文；如需按策略进一步落地到章节正文，可另起 ~exec 执行。

- [-] 5.1 深化注解基础设施：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/01-container-bootstrap-and-infrastructure.md`，参考 `chapters/part-03-container-internals.md`
- [-] 5.2 深化 BDRPP：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/02-bdrpp-definition-registration.md`，参考 `chapters/part-03-container-internals.md`
- [-] 5.3 深化 ordering 算法：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/03-post-processor-ordering.md`，参考 `chapters/part-03-container-internals.md`
- [-] 5.4 深化实例化前短路：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/04-pre-instantiation-short-circuit.md`，参考 `chapters/part-03-container-internals.md`
- [-] 5.5 深化 early reference：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/05-early-reference-and-circular.md`，参考 `chapters/part-03-container-internals.md`
- [-] 5.6 深化回调顺序：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/06-lifecycle-callback-order.md`，参考 `chapters/part-03-container-internals.md`
- [-] 5.7 深化主线叙事：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/07-refresh-to-bean-creation-mainline.md`，参考 `chapters/part-03-container-internals.md`

## 6. Part 04：Wiring & Boundaries（真实工程边界：可复现 + 可排障）

> Note: 本轮优先“产出逐章深化策略清单”（见 `spring-core-beans/docs/deepening-strategies/part-04-wiring-and-boundaries.md`），不重复堆叠改写已具备深度的正文；如需按策略进一步落地到章节正文，可另起 ~exec 执行。

- [-] 6.1 深化 Lazy：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/01-lazy-semantics.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.2 深化 dependsOn：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/02-depends-on.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.3 深化 resolvable dependency：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/03-resolvable-dependency.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.4 深化父子容器：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/04-context-hierarchy.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.5 深化 beanName/alias：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/05-bean-names-and-aliases.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.6 深化 FactoryBean 深潜：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/06-factorybean-deep-dive.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.7 深化 overriding：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/07-bean-definition-overriding.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.8 深化 programmatic BPP：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/08-programmatic-bpp-registration.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.9 深化 SmartInitializingSingleton：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/09-smart-initializing-singleton.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.10 深化 SmartLifecycle：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.11 深化自定义 scope：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/11-custom-scope-and-scoped-proxy.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.12 深化 FactoryBean 边界：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/12-factorybean-edge-cases.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.13 深化注入阶段：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.14 深化代理替换阶段：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.15 深化 @Resource：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/15-resource-injection-name-first.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.16 深化候选选择/顺序：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.17 深化占位符解析：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.18 深化 merged BD：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/18-merged-bean-definition.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.19 深化类型转换：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.20 深化泛型匹配坑：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/20-generic-type-matching-pitfalls.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.21 深化 Environment/PropertySource：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/21-environment-and-propertysource.md`，参考 `chapters/part-04-wiring-and-boundaries.md`
- [-] 6.22 深化 BeanFactory API：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/22-beanfactory-api-deep-dive.md`，参考 `chapters/part-04-wiring-and-boundaries.md`

## 7. Part 05：AOT & Real World（输入层 + AOT：可证明深化）

> Note: 本轮优先“产出逐章深化策略清单”（见 `spring-core-beans/docs/deepening-strategies/part-05-aot-and-real-world.md`），不重复堆叠改写已具备深度的正文；如需按策略进一步落地到章节正文，可另起 ~exec 执行。

- [-] 7.1 深化 AOT 总览：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/01-aot-and-native-overview.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.2 深化 RuntimeHints：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/02-runtimehints-basics.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.3 深化 XML reader：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/03-xml-bean-definition-reader.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.4 深化容器外注入：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/04-autowirecapablebeanfactory-external-objects.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.5 深化 SpEL：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/05-spel-and-value-expression.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.6 深化自定义 Qualifier：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/06-custom-qualifier-meta-annotation.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.7 深化 XML namespace：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/07-xml-namespace-extension.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.8 深化 Reader 其他输入：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/08-beandefinitionreader-other-inputs-properties-groovy.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.9 深化方法注入：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/09-method-injection-replaced-method.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.10 深化内置 FactoryBean：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/10-built-in-factorybeans-gallery.md`，参考 `chapters/part-05-aot-and-real-world.md`
- [-] 7.11 深化值解析/PropertyEditor：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/11-property-editor-and-value-resolution.md`，参考 `chapters/part-05-aot-and-real-world.md`

## 8. Appendix（工具型章节：索引/训练/排障）

> Note: 本轮优先“产出逐章深化策略清单”（见 `spring-core-beans/docs/deepening-strategies/appendix.md`），不重复堆叠改写已具备深度的正文；如需按策略进一步落地到章节正文，可另起 ~exec 执行。

- [-] 8.1 深化常见误区：`spring-core-modules/spring-core-beans/docs/appendix/01-common-pitfalls.md`，参考 `chapters/appendix.md`
- [-] 8.2 深化自测题：`spring-core-modules/spring-core-beans/docs/appendix/11-self-check.md`，参考 `chapters/appendix.md`
- [-] 8.3 深化术语表：`spring-core-modules/spring-core-beans/docs/appendix/02-glossary.md`，参考 `chapters/appendix.md`
- [-] 8.4 深化知识地图：`spring-core-modules/spring-core-beans/docs/appendix/03-knowledge-map.md`，参考 `chapters/appendix.md`
- [-] 8.5 深化面试题库：`spring-core-modules/spring-core-beans/docs/appendix/04-interview-playbook.md`，参考 `chapters/appendix.md`
- [-] 8.6 深化生产排障清单：`spring-core-modules/spring-core-beans/docs/appendix/05-production-troubleshooting-checklist.md`，参考 `chapters/appendix.md`
- [-] 8.7 深化 public API 索引：`spring-core-modules/spring-core-beans/docs/appendix/06-spring-beans-public-api-index.md`，参考 `chapters/appendix.md`
- [-] 8.8 深化 public API gap：`spring-core-modules/spring-core-beans/docs/appendix/07-spring-beans-public-api-gap.md`，参考 `chapters/appendix.md`
- [-] 8.9 深化 Explore/Debug：`spring-core-modules/spring-core-beans/docs/appendix/08-explore-debug-tests.md`，参考 `chapters/appendix.md`
- [-] 8.10 深化 Debugger Pack：`spring-core-modules/spring-core-beans/docs/appendix/09-debugger-pack.md`，参考 `chapters/appendix.md`
- [-] 8.11 深化团队内训讲义：`spring-core-modules/spring-core-beans/docs/appendix/10-team-training-kit.md`，参考 `chapters/appendix.md`

## 9. Security Check

- [√] 9.1 安全检查：重点审阅 SpEL/表达式/反射/AOT 相关章节的安全边界描述，避免引导高风险用法

## 10. SSOT 同步

- [√] 10.1 同步模块知识库：`helloagents/wiki/modules/spring-core-beans.md`
- [√] 10.2 更新变更记录：`helloagents/CHANGELOG.md`

## 11. Testing

- [√] 11.1 全量回归：`mvn -pl :spring-core-beans test`
- [-] 11.2（可选）Explore/Debug：`mvn -pl :spring-core-beans -Dspringcorebeans.explore=true -Dtest=SpringCoreBeans*ExploreTest test`
