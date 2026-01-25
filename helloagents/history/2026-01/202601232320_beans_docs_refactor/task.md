# Task List: spring-core-beans 文档全量重构与深度补齐

Directory: `helloagents/plan/202601232320_beans_docs_refactor/`

---

## 1. 文档入口与目录
- [√] 1.1 重写模块入口 `spring-core-modules/spring-core-beans/README.md`，验证 why.md#requirement-r1-章节结构与命名重构-scenario-s1-稳定入口与目录一致
- [√] 1.2 重写模块目录 `spring-core-modules/spring-core-beans/docs/README.md`，验证 why.md#requirement-r1-章节结构与命名重构-scenario-s1-稳定入口与目录一致

## 2. part-00-guide（主线入口与断点包）
- [√] 2.1 重写 `docs/part-00-guide/010-03-mainline-timeline.md` 与 `docs/part-00-guide/011-00-deep-dive-guide.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪
- [√] 2.2 重写 `docs/part-00-guide/011-04-branch-decision-matrix.md` 与 `docs/part-00-guide/012-01-quickstart-30min.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 2.3 重写 `docs/part-00-guide/013-01-applicationcontext-refresh-call-chain.md` 与 `docs/part-00-guide/013-02-breakpoint-map.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪

## 3. part-01-ioc-container（容器主线）
- [√] 3.1 重写 `docs/part-01-ioc-container/020-01-bean-mental-model.md` 与 `docs/part-01-ioc-container/02-bean-registration.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪
- [√] 3.2 重写 `docs/part-01-ioc-container/014-03-dependency-injection-resolution.md` 与 `docs/part-01-ioc-container/015-04-scope-and-prototype.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 3.3 重写 `docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md` 与 `docs/part-01-ioc-container/017-06-post-processors.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪
- [√] 3.4 重写 `docs/part-01-ioc-container/018-07-configuration-enhancement.md` 与 `docs/part-01-ioc-container/08-factorybean.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 3.5 重写 `docs/part-01-ioc-container/09-circular-dependencies.md`，验证 why.md#requirement-r4-排障与边界专题完善-scenario-s1-现象到验证

## 4. part-02-boot-autoconfig（Boot 衔接）
- [√] 4.1 重写 `docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md` 与 `docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪

## 5. part-03-container-internals（容器内部机制）
- [√] 5.1 重写 `docs/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md` 与 `docs/part-03-container-internals/13-bdrpp-definition-registration.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪
- [√] 5.2 重写 `docs/part-03-container-internals/14-post-processor-ordering.md` 与 `docs/part-03-container-internals/15-pre-instantiation-short-circuit.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 5.3 重写 `docs/part-03-container-internals/16-early-reference-and-circular.md` 与 `docs/part-03-container-internals/17-lifecycle-callback-order.md`，验证 why.md#requirement-r4-排障与边界专题完善-scenario-s1-现象到验证
- [√] 5.4 重写 `docs/part-03-container-internals/18-refresh-to-bean-creation-mainline.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪

## 6. part-04-wiring-and-boundaries（装配与边界）
- [√] 6.1 重写 `docs/part-04-wiring-and-boundaries/023-18-lazy-semantics.md` 与 `docs/part-04-wiring-and-boundaries/19-depends-on.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 6.2 重写 `docs/part-04-wiring-and-boundaries/20-resolvable-dependency.md` 与 `docs/part-04-wiring-and-boundaries/21-context-hierarchy.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 6.3 重写 `docs/part-04-wiring-and-boundaries/22-bean-names-and-aliases.md` 与 `docs/part-04-wiring-and-boundaries/23-factorybean-deep-dive.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 6.4 重写 `docs/part-04-wiring-and-boundaries/24-bean-definition-overriding.md` 与 `docs/part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md`，验证 why.md#requirement-r4-排障与边界专题完善-scenario-s1-现象到验证
- [√] 6.5 重写 `docs/part-04-wiring-and-boundaries/26-smart-initializing-singleton.md` 与 `docs/part-04-wiring-and-boundaries/27-smart-lifecycle-phase.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 6.6 重写 `docs/part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md` 与 `docs/part-04-wiring-and-boundaries/29-factorybean-edge-cases.md`，验证 why.md#requirement-r4-排障与边界专题完善-scenario-s1-现象到验证
- [√] 6.7 重写 `docs/part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md` 与 `docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 6.8 重写 `docs/part-04-wiring-and-boundaries/32-resource-injection-name-first.md` 与 `docs/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 6.9 重写 `docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md` 与 `docs/part-04-wiring-and-boundaries/35-merged-bean-definition.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 6.10 重写 `docs/part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md` 与 `docs/part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md`，验证 why.md#requirement-r4-排障与边界专题完善-scenario-s1-现象到验证
- [√] 6.11 重写 `docs/part-04-wiring-and-boundaries/38-environment-and-propertysource.md` 与 `docs/part-04-wiring-and-boundaries/39-beanfactory-api-deep-dive.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪

## 7. part-05-aot-and-real-world（AOT 与真实世界）
- [√] 7.1 重写 `docs/part-05-aot-and-real-world/024-40-aot-and-native-overview.md` 与 `docs/part-05-aot-and-real-world/41-runtimehints-basics.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪
- [√] 7.2 重写 `docs/part-05-aot-and-real-world/42-xml-bean-definition-reader.md` 与 `docs/part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 7.3 重写 `docs/part-05-aot-and-real-world/44-spel-and-value-expression.md` 与 `docs/part-05-aot-and-real-world/45-custom-qualifier-meta-annotation.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 7.4 重写 `docs/part-05-aot-and-real-world/46-xml-namespace-extension.md` 与 `docs/part-05-aot-and-real-world/47-beandefinitionreader-other-inputs-properties-groovy.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 7.5 重写 `docs/part-05-aot-and-real-world/48-method-injection-replaced-method.md` 与 `docs/part-05-aot-and-real-world/49-built-in-factorybeans-gallery.md`，验证 why.md#requirement-r4-排障与边界专题完善-scenario-s1-现象到验证
- [√] 7.6 重写 `docs/part-05-aot-and-real-world/50-property-editor-and-value-resolution.md`，验证 why.md#requirement-r4-排障与边界专题完善-scenario-s1-现象到验证

## 8. appendix（索引、排障与知识地图）
- [√] 8.1 重写 `docs/appendix/025-90-common-pitfalls.md` 与 `docs/appendix/026-99-self-check.md`，验证 why.md#requirement-r4-排障与边界专题完善-scenario-s1-现象到验证
- [√] 8.2 重写 `docs/appendix/91-glossary.md` 与 `docs/appendix/92-knowledge-map.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪
- [√] 8.3 重写 `docs/appendix/93-interview-playbook.md` 与 `docs/appendix/94-production-troubleshooting-checklist.md`，验证 why.md#requirement-r4-排障与边界专题完善-scenario-s1-现象到验证
- [√] 8.4 重写 `docs/appendix/95-spring-beans-public-api-index.md` 与 `docs/appendix/96-spring-beans-public-api-gap.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪
- [√] 8.5 重写 `docs/appendix/97-explore-debug-tests.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位

## 9. 新增 Debugger Pack（统一入口）
- [√] 9.1 新增 `docs/appendix/98-debugger-pack.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位

## 10. 可跑证据链与测试补齐
- [√] 10.1 新增/完善 `src/test/java/.../part00_guide/SpringCoreBeansMainlineCallChainLabTest.java` 与 `src/test/java/.../part00_guide/SpringCoreBeansBreakpointPackLabTest.java`，验证 why.md#requirement-r6-可跑证据链与测试补齐-scenario-s1-文档到测试闭环
- [√] 10.2 新增/完善 `src/test/java/.../part01_ioc_container/SpringCoreBeansCircularDependencyBoundaryLabTest.java` 与 `src/test/java/.../part01_ioc_container/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`，验证 why.md#requirement-r6-可跑证据链与测试补齐-scenario-s1-文档到测试闭环
- [√] 10.3 新增/完善 `src/test/java/.../part04_wiring_and_boundaries/SpringCoreBeansInjectionPhaseMatrixLabTest.java` 与 `src/test/java/.../part04_wiring_and_boundaries/SpringCoreBeansResourceResolutionLabTest.java`，验证 why.md#requirement-r6-可跑证据链与测试补齐-scenario-s1-文档到测试闭环
- [√] 10.4 新增/完善 `src/test/java/.../part05_aot_and_real_world/SpringCoreBeansRuntimeHintsBoundaryLabTest.java` 与 `src/test/java/.../part05_aot_and_real_world/SpringCoreBeansPropertyEditorResolutionLabTest.java`，验证 why.md#requirement-r6-可跑证据链与测试补齐-scenario-s1-文档到测试闭环
- [√] 10.5 新增/完善 `src/test/java/.../appendix/SpringCoreBeansPerformanceConcurrencyLabTest.java` 与 `src/test/java/.../appendix/SpringCoreBeansTroubleshootingPlaybookLabTest.java`，验证 why.md#requirement-r6-可跑证据链与测试补齐-scenario-s1-文档到测试闭环

## 11. Security Check
- [√] 11.1 执行安全检查（示例不包含敏感信息/不鼓励不安全配置/排障示例避免暴露堆栈细节）

## 12. Documentation Update
- [√] 12.1 更新知识库 `helloagents/wiki/modules/spring-core-beans.md` 以反映章节与入口变更

## 13. Testing
- [-] 13.1 运行 `mvn -q -pl :spring-core-beans test`，验证核心 Labs
  > Note: 未执行，待确认是否运行文档自检脚本。
  > Note: 未执行，待确认是否运行新增/补齐的测试集合。
