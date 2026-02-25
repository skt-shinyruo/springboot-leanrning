# Task List: spring-core-beans docs 全量“教程化 v2”（源码进阶 + 面试标准答案）

Directory: `helloagents/plan/202601272227_spring_core_beans_docs_tutorial_v2_all/`

---

## 1. Global（目录/索引/SSOT）

- [√] 1.1 强化 `docs/README.md`：新增“双轨阅读路线”（源码进阶 / 面试冲刺），并与 Appendix/93/94/98 互链，in `spring-core-modules/spring-core-beans/docs/README.md`, verify `why.md#requirement-r1-docs-tutorial-contract`
- [√] 1.2 升级面试题库：补齐对 Boot/AutoConfig/AOT/XML 等章节的映射与标准答案入口，in `spring-core-modules/spring-core-beans/docs/appendix/04-interview-playbook.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 1.3 升级全局排障清单：补齐“按现象分流 → 对应章节/Lab/断点”的映射规则，in `spring-core-modules/spring-core-beans/docs/appendix/05-production-troubleshooting-checklist.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`
- [√] 1.4 升级 Debugger Pack：补齐“10/30/3 章契约”与“方法级证据链卡片”索引入口，in `spring-core-modules/spring-core-beans/docs/appendix/09-debugger-pack.md`, verify `why.md#requirement-r2-method-level-evidence-chain`

## 2. Part 00（Guide）- 6 files

- [√] 2.1 教程化升级（章契约/面试映射/排障分流），in `spring-core-modules/spring-core-beans/docs/part-00-guide/02-mainline-timeline.md`, verify `why.md#requirement-r1-docs-tutorial-contract`
- [√] 2.2 教程化升级（章契约/面试映射/排障分流），in `spring-core-modules/spring-core-beans/docs/part-00-guide/03-deep-dive-guide.md`, verify `why.md#requirement-r1-docs-tutorial-contract`
- [√] 2.3 教程化升级（章契约/面试映射/排障分流），in `spring-core-modules/spring-core-beans/docs/part-00-guide/04-branch-decision-matrix.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`
- [√] 2.4 教程化升级（章契约/面试映射/排障分流），in `spring-core-modules/spring-core-beans/docs/part-00-guide/05-quickstart-30min.md`, verify `why.md#requirement-r1-docs-tutorial-contract`
- [√] 2.5 教程化升级（章契约/面试映射/排障分流），in `spring-core-modules/spring-core-beans/docs/part-00-guide/06-applicationcontext-refresh-call-chain.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 2.6 教程化升级（章契约/面试映射/排障分流），in `spring-core-modules/spring-core-beans/docs/part-00-guide/07-breakpoint-map.md`, verify `why.md#requirement-r2-method-level-evidence-chain`

## 3. Part 01（IoC Container）- 9 files

- [√] 3.1 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/01-bean-registration.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 3.2 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-dependency-injection-resolution.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 3.3 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/03-scope-and-prototype.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`
- [√] 3.4 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/04-lifecycle-and-callbacks.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 3.5 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/05-post-processors.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 3.6 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/06-configuration-enhancement.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 3.7 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-bean-mental-model.md`, verify `why.md#requirement-r1-docs-tutorial-contract`
- [√] 3.8 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/07-factorybean.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 3.9 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-circular-dependencies.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`

## 4. Part 02（Boot Auto-Config）- 3 files

- [√] 4.1 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/01-debugging-and-observability.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`
- [√] 4.2 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/02-auto-config-ordering.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 4.3 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/03-spring-boot-auto-configuration.md`, verify `why.md#requirement-r3-interview-standard-answers`

## 5. Part 03（Container Internals）- 7 files

- [√] 5.1 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-03-container-internals/01-container-bootstrap-and-infrastructure.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 5.2 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-03-container-internals/02-bdrpp-definition-registration.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 5.3 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-03-container-internals/03-post-processor-ordering.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 5.4 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-03-container-internals/04-pre-instantiation-short-circuit.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`
- [√] 5.5 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-03-container-internals/05-early-reference-and-circular.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 5.6 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-03-container-internals/06-lifecycle-callback-order.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 5.7 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-03-container-internals/07-refresh-to-bean-creation-mainline.md`, verify `why.md#requirement-r2-method-level-evidence-chain`

## 6. Part 04（Wiring & Boundaries）- 22 files

- [√] 6.1 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/02-depends-on.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`
- [√] 6.2 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/03-resolvable-dependency.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 6.3 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/04-context-hierarchy.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`
- [√] 6.4 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/05-bean-names-and-aliases.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`
- [√] 6.5 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/06-factorybean-deep-dive.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 6.6 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/07-bean-definition-overriding.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`
- [√] 6.7 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/08-programmatic-bpp-registration.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 6.8 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/09-smart-initializing-singleton.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 6.9 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/10-smart-lifecycle-phase.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 6.10 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/11-custom-scope-and-scoped-proxy.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`
- [√] 6.11 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/12-factorybean-edge-cases.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`
- [√] 6.12 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/13-injection-phase-field-vs-constructor.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 6.13 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/14-proxying-phase-bpp-wraps-bean.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 6.14 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/15-resource-injection-name-first.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 6.15 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/16-autowire-candidate-selection-primary-priority-order.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 6.16 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/17-value-placeholder-resolution-strict-vs-non-strict.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 6.17 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/18-merged-bean-definition.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 6.18 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 6.19 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/20-generic-type-matching-pitfalls.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 6.20 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/21-environment-and-propertysource.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 6.21 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/22-beanfactory-api-deep-dive.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 6.22 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/01-lazy-semantics.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`

## 7. Part 05（AOT & Real World）- 11 files

- [√] 7.1 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/01-aot-and-native-overview.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 7.2 教程化升级（章契约/排障决策表/面试映射），in `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/02-runtimehints-basics.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 7.3 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/03-xml-bean-definition-reader.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 7.4 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/04-autowirecapablebeanfactory-external-objects.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`
- [√] 7.5 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/05-spel-and-value-expression.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 7.6 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/06-custom-qualifier-meta-annotation.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 7.7 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/07-xml-namespace-extension.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 7.8 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/08-beandefinitionreader-other-inputs-properties-groovy.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 7.9 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/09-method-injection-replaced-method.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 7.10 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/10-built-in-factorybeans-gallery.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 7.11 教程化升级（章契约/排障决策表/面试标准答案），in `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/11-property-editor-and-value-resolution.md`, verify `why.md#requirement-r2-method-level-evidence-chain`

## 8. Appendix（索引/清单/工具页）- 11 files

- [√] 8.1 知识地图升级：补齐“章节 → 面试题库”映射入口，in `spring-core-modules/spring-core-beans/docs/appendix/03-knowledge-map.md`, verify `why.md#requirement-r3-interview-standard-answers`
- [√] 8.2 坑点清单升级：按“现象→证据链→修复→验证”结构统一表达，in `spring-core-modules/spring-core-beans/docs/appendix/01-common-pitfalls.md`, verify `why.md#requirement-r4-troubleshooting-decision-table`
- [√] 8.3 自测题升级：将关键题型对齐面试题库与章节证据链入口，in `spring-core-modules/spring-core-beans/docs/appendix/11-self-check.md`, verify `why.md#requirement-r1-docs-tutorial-contract`
- [√] 8.4 术语表升级：统一“核心术语 ↔ 对应章节 ↔ 对应断点”引用，in `spring-core-modules/spring-core-beans/docs/appendix/02-glossary.md`, verify `why.md#requirement-r2-method-level-evidence-chain`
- [√] 8.5 API 索引/Gap/Explore/Training：按“教程化 v2”补齐互链与定位入口，in `spring-core-modules/spring-core-beans/docs/appendix/06-spring-beans-public-api-index.md`, verify `why.md#requirement-r1-docs-tutorial-contract`
- [√] 8.6 API 索引/Gap/Explore/Training：按“教程化 v2”补齐互链与定位入口，in `spring-core-modules/spring-core-beans/docs/appendix/07-spring-beans-public-api-gap.md`, verify `why.md#requirement-r1-docs-tutorial-contract`
- [√] 8.7 API 索引/Gap/Explore/Training：按“教程化 v2”补齐互链与定位入口，in `spring-core-modules/spring-core-beans/docs/appendix/08-explore-debug-tests.md`, verify `why.md#requirement-r1-docs-tutorial-contract`
- [√] 8.8 API 索引/Gap/Explore/Training：按“教程化 v2”补齐互链与定位入口，in `spring-core-modules/spring-core-beans/docs/appendix/10-team-training-kit.md`, verify `why.md#requirement-r1-docs-tutorial-contract`

## 9. Knowledge Base Sync（SSOT）

- [√] 9.1 同步知识库模块页：追加本次“docs 教程化 v2”变更记录与入口链接，in `helloagents/wiki/modules/spring-core-beans.md`
- [√] 9.2 更新变更日志：记录本次 docs 全量优化，in `helloagents/CHANGELOG.md`
- [√] 9.3 归档方案包与索引更新：迁移到 `helloagents/history/YYYY-MM/` 并更新 `helloagents/history/index.md`

## 10. Security Check

- [√] 10.1 安全自检（G9）：不引入密钥/生产地址；不引入外部网络依赖

## 11. Testing

- [√] 11.1 分批最小回归（每个 Part 至少 1 个相关 LabTest）
- [√] 11.2 全量回归：`mvn -pl :spring-core-beans test`

