# Task List: spring-core-beans 文档分批重构与测试闭环

Directory: `helloagents/plan/202601240941_beans_docs_refactor/`

---

## 1. 文档入口与目录
- [√] 1.1 重写模块入口 `spring-core-modules/spring-core-beans/README.md`，验证 why.md#requirement-r1-章节结构与命名重构-scenario-s1-稳定入口与目录一致
- [√] 1.2 重写模块目录 `spring-core-modules/spring-core-beans/docs/README.md`，验证 why.md#requirement-r1-章节结构与命名重构-scenario-s1-稳定入口与目录一致

## 2. part-00-guide（主线入口与断点包）
- [√] 2.1 重写 `docs/part-00-guide/02-mainline-timeline.md` 与 `docs/part-00-guide/03-deep-dive-guide.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪
- [√] 2.2 重写 `docs/part-00-guide/04-branch-decision-matrix.md` 与 `docs/part-00-guide/05-quickstart-30min.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 2.3 重写 `docs/part-00-guide/06-applicationcontext-refresh-call-chain.md` 与 `docs/part-00-guide/07-breakpoint-map.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪

## 3. part-01-ioc-container（容器主线）
- [√] 3.1 重写 `docs/part-01-ioc-container/09-bean-mental-model.md` 与 `docs/part-01-ioc-container/01-bean-registration.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪
- [√] 3.2 重写 `docs/part-01-ioc-container/02-dependency-injection-resolution.md` 与 `docs/part-01-ioc-container/03-scope-and-prototype.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 3.3 重写 `docs/part-01-ioc-container/04-lifecycle-and-callbacks.md` 与 `docs/part-01-ioc-container/05-post-processors.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪

## 4. part-02-boot-autoconfig（Boot 与容器）
- [√] 4.1 重写 `docs/part-02-boot-autoconfig/01-debugging-and-observability.md` 与 `docs/part-02-boot-autoconfig/03-spring-boot-auto-configuration.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪
- [√] 4.2 重写 `docs/part-02-boot-autoconfig/02-auto-config-ordering.md` 与 `docs/part-02-boot-autoconfig/03-spring-boot-auto-configuration.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位

## 5. part-03-container-internals（容器内部机制）
- [√] 5.1 重写 `docs/part-03-container-internals/01-container-bootstrap-and-infrastructure.md` 与 `docs/part-03-container-internals/02-bdrpp-definition-registration.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪
- [√] 5.2 重写 `docs/part-03-container-internals/03-post-processor-ordering.md` 与 `docs/part-03-container-internals/04-pre-instantiation-short-circuit.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 5.3 重写 `docs/part-03-container-internals/05-early-reference-and-circular.md` 与 `docs/part-03-container-internals/06-lifecycle-callback-order.md`，验证 why.md#requirement-r4-排障与边界专题完善-scenario-s1-现象到验证
- [√] 5.4 重写 `docs/part-03-container-internals/07-refresh-to-bean-creation-mainline.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪

## 6. part-04-wiring-and-boundaries（装配与边界）
- [√] 6.1 重写 `docs/part-04-wiring-and-boundaries/01-lazy-semantics.md` 与 `docs/part-04-wiring-and-boundaries/02-depends-on.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 6.2 重写 `docs/part-04-wiring-and-boundaries/03-resolvable-dependency.md` 与 `docs/part-04-wiring-and-boundaries/04-context-hierarchy.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 6.3 重写 `docs/part-04-wiring-and-boundaries/05-bean-names-and-aliases.md` 与 `docs/part-04-wiring-and-boundaries/06-factorybean-deep-dive.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位

## 7. part-05-aot-and-real-world（AOT 与真实世界）
- [√] 7.1 重写 `docs/part-05-aot-and-real-world/01-aot-and-native-overview.md` 与 `docs/part-05-aot-and-real-world/02-runtimehints-basics.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪
- [√] 7.2 重写 `docs/part-05-aot-and-real-world/03-xml-bean-definition-reader.md` 与 `docs/part-05-aot-and-real-world/04-autowirecapablebeanfactory-external-objects.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位
- [√] 7.3 重写 `docs/part-05-aot-and-real-world/05-spel-and-value-expression.md` 与 `docs/part-05-aot-and-real-world/06-custom-qualifier-meta-annotation.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位

## 8. appendix（索引、排障与知识地图）
- [√] 8.1 重写 `docs/appendix/01-common-pitfalls.md` 与 `docs/appendix/11-self-check.md`，验证 why.md#requirement-r4-排障与边界专题完善-scenario-s1-现象到验证
- [√] 8.2 重写 `docs/appendix/02-glossary.md` 与 `docs/appendix/03-knowledge-map.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪
- [√] 8.3 重写 `docs/appendix/04-interview-playbook.md` 与 `docs/appendix/05-production-troubleshooting-checklist.md`，验证 why.md#requirement-r4-排障与边界专题完善-scenario-s1-现象到验证
- [√] 8.4 重写 `docs/appendix/06-spring-beans-public-api-index.md` 与 `docs/appendix/07-spring-beans-public-api-gap.md`，验证 why.md#requirement-r2-机制主线与调用链补齐-scenario-s1-主线可追踪
- [√] 8.5 重写 `docs/appendix/08-explore-debug-tests.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位

## 9. 新增 Debugger Pack（统一入口）
- [√] 9.1 新增 `docs/appendix/09-debugger-pack.md`，验证 why.md#requirement-r3-关键分支矩阵与断点包-scenario-s1-分支可定位

## 10. 可跑证据链与测试补齐
- [√] 10.1 新增/完善 `src/test/java/.../part00_guide/SpringCoreBeansMainlineCallChainLabTest.java` 与 `src/test/java/.../part00_guide/SpringCoreBeansBreakpointPackLabTest.java`，验证 why.md#requirement-r6-可跑证据链与测试补齐-scenario-s1-文档到测试闭环
- [√] 10.2 新增/完善 `src/test/java/.../part01_ioc_container/SpringCoreBeansCircularDependencyBoundaryLabTest.java` 与 `src/test/java/.../part01_ioc_container/SpringCoreBeansFactoryBeanEdgeCasesLabTest.java`，验证 why.md#requirement-r6-可跑证据链与测试补齐-scenario-s1-文档到测试闭环
- [√] 10.3 新增/完善 `src/test/java/.../part04_wiring_and_boundaries/SpringCoreBeansInjectionPhaseMatrixLabTest.java` 与 `src/test/java/.../part04_wiring_and_boundaries/SpringCoreBeansResourceResolutionLabTest.java`，验证 why.md#requirement-r6-可跑证据链与测试补齐-scenario-s1-文档到测试闭环
- [√] 10.4 新增/完善 `src/test/java/.../part05_aot_and_real_world/SpringCoreBeansRuntimeHintsBoundaryLabTest.java` 与 `src/test/java/.../part05_aot_and_real_world/SpringCoreBeansPropertyEditorResolutionLabTest.java`，验证 why.md#requirement-r6-可跑证据链与测试补齐-scenario-s1-文档到测试闭环
- [√] 10.5 新增/完善 `src/test/java/.../appendix/SpringCoreBeansPerformanceConcurrencyLabTest.java` 与 `src/test/java/.../appendix/SpringCoreBeansTroubleshootingPlaybookLabTest.java`，验证 why.md#requirement-r6-可跑证据链与测试补齐-scenario-s1-文档到测试闭环

## 11. Security Check
- [√] 11.1 执行安全检查（按 G9：避免敏感信息、避免生产环境操作、禁止危险命令）

## 12. 知识库同步
- [√] 12.1 更新 `helloagents/wiki/modules/spring-core-beans.md`，补齐章节入口与测试闭环映射

## 13. Testing
- [ ] 13.2 按章节运行新增/补齐的 Lab/Exercise 测试集合
