# Spring Core Beans Docs Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Generate the first production-quality Chinese documentation set for `spring-core-modules/spring-core-beans/docs/` according to `DOCUMENTATION_SPEC.md`.

**Architecture:** Create reader-facing Markdown documents as independent owner articles, not as fixed-template pages. Keep the module README as the ordered entry and docs directory index required by `SpringCoreBeansDocumentationContractTest`. Each batch must leave the module in a contract-testable state.

**Tech Stack:** Markdown, Maven/JUnit 5, existing SpringCoreBeans Lab tests.

---

## File Structure

- Modify: `spring-core-modules/spring-core-beans/README.md`
  - Module entry, reading order, commands, and complete docs directory.
- Read: `spring-core-modules/spring-core-beans/DOCUMENTATION_SPEC.md`
  - Writing rules and per-document authoring guidance.
- Read: `spring-core-modules/spring-core-beans/KNOWLEDGE.md`
  - Knowledge-point source only; do not copy it as body text.
- Create: `spring-core-modules/spring-core-beans/docs/`
  - Flat docs directory for reader-facing and support Markdown files.
- Create in definition/container batch:
  - `spring-core-modules/spring-core-beans/docs/bean-mental-model.md`
  - `spring-core-modules/spring-core-beans/docs/bean-definition-registration.md`
  - `spring-core-modules/spring-core-beans/docs/bean-definition-metadata-and-origin.md`
  - `spring-core-modules/spring-core-beans/docs/beanfactory-vs-applicationcontext.md`
  - `spring-core-modules/spring-core-beans/docs/refresh-mainline.md`
  - `spring-core-modules/spring-core-beans/docs/container-bootstrap-and-infrastructure.md`
  - `spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md`
- Create in creation/injection batch:
  - `spring-core-modules/spring-core-beans/docs/bean-creation-mainline.md`
  - `spring-core-modules/spring-core-beans/docs/pre-instantiation-short-circuit.md`
  - `spring-core-modules/spring-core-beans/docs/dependency-injection-resolution.md`
  - `spring-core-modules/spring-core-beans/docs/dependency-descriptor-and-injection-point.md`
  - `spring-core-modules/spring-core-beans/docs/autowire-candidate-selection.md`
  - `spring-core-modules/spring-core-beans/docs/optional-and-provider-injection.md`
  - `spring-core-modules/spring-core-beans/docs/resource-vs-autowired.md`
- Create in lifecycle/proxy batch:
  - `spring-core-modules/spring-core-beans/docs/scope-and-prototype.md`
  - `spring-core-modules/spring-core-beans/docs/custom-scope-and-scoped-proxy.md`
  - `spring-core-modules/spring-core-beans/docs/lazy-semantics.md`
  - `spring-core-modules/spring-core-beans/docs/lifecycle-callbacks.md`
  - `spring-core-modules/spring-core-beans/docs/smart-initializing-singleton.md`
  - `spring-core-modules/spring-core-beans/docs/smart-lifecycle.md`
  - `spring-core-modules/spring-core-beans/docs/early-reference-and-three-level-cache.md`
  - `spring-core-modules/spring-core-beans/docs/proxying-phase.md`
- Create in extension/input batch:
  - `spring-core-modules/spring-core-beans/docs/post-processors-overview.md`
  - `spring-core-modules/spring-core-beans/docs/beanpost-processors.md`
  - `spring-core-modules/spring-core-beans/docs/factorybean.md`
  - `spring-core-modules/spring-core-beans/docs/factorybean-type-matching.md`
  - `spring-core-modules/spring-core-beans/docs/xml-bean-definition-reader.md`
  - `spring-core-modules/spring-core-beans/docs/properties-and-groovy-reader.md`
  - `spring-core-modules/spring-core-beans/docs/xml-namespace-extension.md`
- Create in Boot/guide/appendix batch:
  - `spring-core-modules/spring-core-beans/docs/boot-auto-configuration-beans.md`
  - `spring-core-modules/spring-core-beans/docs/aot-native-overview.md`
  - `spring-core-modules/spring-core-beans/docs/guide-quickstart-30min.md`
  - `spring-core-modules/spring-core-beans/docs/guide-mainline-timeline.md`
  - `spring-core-modules/spring-core-beans/docs/guide-breakpoint-map.md`
  - `spring-core-modules/spring-core-beans/docs/guide-deep-dive-guide.md`
  - `spring-core-modules/spring-core-beans/docs/appendix-common-pitfalls.md`
  - `spring-core-modules/spring-core-beans/docs/appendix-production-troubleshooting-checklist.md`

## Implementation Rules

- Every reader-facing `docs/*.md` file must include `<!-- CHAPTER-CARD:START -->` and `<!-- CHAPTER-CARD:END -->`.
- Do not use one fixed body template. Use the natural structure specified for each document in `DOCUMENTATION_SPEC.md`.
- Do not add generic "相邻主题" sections. Add links only when a local explanation would cross a real owner boundary.
- Every referenced `SpringCoreBeans*Test` must exist under `spring-core-modules/spring-core-beans/src/test/java`.
- After each batch, update `spring-core-modules/spring-core-beans/README.md` so every created docs Markdown file is linked from README.
- After each batch, run the documentation contract test.

## Task 1: Establish Docs Root, README, and First Owner Map

**Files:**
- Create: `spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md`
- Modify: `spring-core-modules/spring-core-beans/README.md`

- [ ] **Step 1: Create docs directory**

Run:

```bash
mkdir -p spring-core-modules/spring-core-beans/docs
```

Expected: command exits 0 and the directory exists.

- [ ] **Step 2: Write `appendix-knowledge-map.md`**

Create a Chinese owner map that maps the first planned documentation set to questions and Lab evidence.

Required content:

```markdown
# 知识地图：Spring Bean 文档归属
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 这页用于定位 Spring Bean 问题应该看哪篇 owner 文档。
    - 它不解释完整机制，只给问题、主文档和最短验证入口。
    - 新增或改名文档时，先同步这张归属表，再同步 README。

    观察对象：Spring Bean 文档归属、Lab 证据和排障入口。
    主线位置：定义、容器、创建、注入、生命周期、扩展、暴露、Boot、AOT。
    对照入口：`SpringCoreBeansModuleContractLabTest`。
<!-- CHAPTER-CARD:END -->
```

Include a table with all files listed in this plan. Do not add links to files that have not been created in the current batch unless they will also be created before the next contract test run. For future files, use plain code formatting instead of Markdown links until created.

- [ ] **Step 3: Write initial README**

Replace the empty module README with:

````markdown
# spring-core-beans

本模块用可运行 Lab 和中文文档讲清 Spring IoC 容器与 Bean 机制。

## 最短验证

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest,SpringCoreBeansModuleContractLabTest test
```

## 文档入口

- [知识地图：Spring Bean 文档归属](docs/appendix-knowledge-map.md)

## 写作规格

- [Spring Core Beans 详细文档写作规格](DOCUMENTATION_SPEC.md)
````

- [ ] **Step 4: Run documentation contract**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected: build exits 0.

## Task 2: Write Definition and Container Mainline Documents

**Files:**
- Create: `spring-core-modules/spring-core-beans/docs/bean-mental-model.md`
- Create: `spring-core-modules/spring-core-beans/docs/bean-definition-registration.md`
- Create: `spring-core-modules/spring-core-beans/docs/bean-definition-metadata-and-origin.md`
- Create: `spring-core-modules/spring-core-beans/docs/beanfactory-vs-applicationcontext.md`
- Create: `spring-core-modules/spring-core-beans/docs/refresh-mainline.md`
- Create: `spring-core-modules/spring-core-beans/docs/container-bootstrap-and-infrastructure.md`
- Modify: `spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md`
- Modify: `spring-core-modules/spring-core-beans/README.md`

- [ ] **Step 1: Write `bean-mental-model.md`**

Follow `DOCUMENTATION_SPEC.md`. The article must be self-contained and explain Java class, BeanDefinition, merged definition, raw instance, early reference, exposed object, proxy, and FactoryBean product.

Use existing Lab references only:

```text
SpringCoreBeansContainerLabTest
SpringCoreBeansBeanGraphDebugLabTest
SpringCoreBeansLifecycleRawVsProxyLabTest
SpringCoreBeansFactoryBeanDeepDiveLabTest
```

- [ ] **Step 2: Write `bean-definition-registration.md`**

Explain XML, component scan, `@Bean`, `@Import`, conditional registration, programmatic registration, and Boot auto-configuration as BeanDefinition sources. Focus on registration timing and diagnostics.

Use existing Lab references only:

```text
SpringCoreBeansBeanDefinitionRegistrationDiffLabTest
SpringCoreBeansComponentScanLabTest
SpringCoreBeansImportLabTest
SpringCoreBeansProgrammaticRegistrationLabTest
SpringCoreBeansAutoConfigurationLabTest
```

- [ ] **Step 3: Write `bean-definition-metadata-and-origin.md`**

Explain metadata as container decision input: scope, lazy, primary, fallback/default candidate, autowire candidate, role, source/origin, factory method, init/destroy, depends-on.

Use existing Lab references only:

```text
SpringCoreBeansBeanDefinitionMetadataFlagsLabTest
SpringCoreBeansBeanDefinitionOriginLabTest
SpringCoreBeansDependsOnLabTest
```

- [ ] **Step 4: Write `beanfactory-vs-applicationcontext.md`**

Explain BeanFactory and ApplicationContext differences by behavior: refresh, resources, environment, events, infrastructure, and singleton pre-instantiation.

Use existing Lab references only:

```text
SpringCoreBeansBeanFactoryVsApplicationContextLabTest
SpringCoreBeansContainerLabTest
```

- [ ] **Step 5: Write `refresh-mainline.md`**

Explain refresh as a timeline and focus on container-state changes. Avoid full single-bean creation details.

Use existing Lab references only:

```text
SpringCoreBeansMainlineCallChainLabTest
SpringCoreBeansBootstrapInternalsLabTest
SpringCoreBeansContainerLabTest
```

- [ ] **Step 6: Write `container-bootstrap-and-infrastructure.md`**

Explain why annotation handling, dependency injection, lifecycle annotations, events, conversion, and AOP are infrastructure-backed container capabilities.

Use existing Lab references only:

```text
SpringCoreBeansBootstrapInternalsLabTest
SpringCoreBeansInfrastructureBeanRoleLabTest
SpringCoreBeansAwareInfrastructureLabTest
```

- [ ] **Step 7: Update README and knowledge map**

Add Markdown links for all six new docs to README and `appendix-knowledge-map.md`.

- [ ] **Step 8: Run documentation contract**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected: build exits 0.

## Task 3: Write Creation and Injection Documents

**Files:**
- Create: `spring-core-modules/spring-core-beans/docs/bean-creation-mainline.md`
- Create: `spring-core-modules/spring-core-beans/docs/pre-instantiation-short-circuit.md`
- Create: `spring-core-modules/spring-core-beans/docs/dependency-injection-resolution.md`
- Create: `spring-core-modules/spring-core-beans/docs/dependency-descriptor-and-injection-point.md`
- Create: `spring-core-modules/spring-core-beans/docs/autowire-candidate-selection.md`
- Create: `spring-core-modules/spring-core-beans/docs/optional-and-provider-injection.md`
- Create: `spring-core-modules/spring-core-beans/docs/resource-vs-autowired.md`
- Modify: `spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md`
- Modify: `spring-core-modules/spring-core-beans/README.md`

- [ ] **Step 1: Write `bean-creation-mainline.md`**

Explain `getBean -> doGetBean -> createBean -> doCreateBean -> populateBean -> initializeBean -> exposed object`.

Use:

```text
SpringCoreBeansBeanCreationTraceLabTest
SpringCoreBeansMainlineCallChainLabTest
```

- [ ] **Step 2: Write `pre-instantiation-short-circuit.md`**

Explain `postProcessBeforeInstantiation` and why construction can be skipped.

Use:

```text
SpringCoreBeansPreInstantiationLabTest
```

- [ ] **Step 3: Write `dependency-injection-resolution.md`**

Explain injection resolution from descriptor to candidate collection, filtering, single-value resolution, and failure.

Use:

```text
SpringCoreBeansInjectionAmbiguityLabTest
SpringCoreBeansAutowireCandidateSelectionLabTest
SpringCoreBeansInjectionPhaseLabTest
```

- [ ] **Step 4: Write `dependency-descriptor-and-injection-point.md`**

Explain what injection-point metadata contains and how to inspect it.

Use:

```text
SpringCoreBeansDependencyDescriptorMetadataLabTest
SpringCoreBeansProgrammaticResolveDependencyLabTest
DependencyDescriptorDumperLabTest
```

- [ ] **Step 5: Write `autowire-candidate-selection.md`**

Explain candidate rules: type, autowire candidate, qualifier, primary, priority, fallback/default candidate, name, collection ordering.

Use:

```text
SpringCoreBeansAutowireCandidateSelectionLabTest
SpringCoreBeansAutowireCandidateSelectionExerciseTest
SpringCoreBeansAutowireCandidateSelectionExerciseSolutionTest
SpringCoreBeansBeanDefinitionMetadataFlagsLabTest
```

- [ ] **Step 6: Write `optional-and-provider-injection.md`**

Explain optional and lazy dependency APIs.

Use:

```text
SpringCoreBeansOptionalInjectionLabTest
SpringCoreBeansJsr330InjectionLabTest
SpringCoreBeansLazyLabTest
```

- [ ] **Step 7: Write `resource-vs-autowired.md`**

Explain name-first vs type-first dependency resolution.

Use:

```text
SpringCoreBeansResourceInjectionLabTest
SpringCoreBeansResourceResolutionLabTest
```

- [ ] **Step 8: Update README and knowledge map**

Add Markdown links for all new docs.

- [ ] **Step 9: Run documentation contract**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected: build exits 0.

## Task 4: Write Lifecycle, Scope, Circular Dependency, and Proxy Documents

**Files:**
- Create: `spring-core-modules/spring-core-beans/docs/scope-and-prototype.md`
- Create: `spring-core-modules/spring-core-beans/docs/custom-scope-and-scoped-proxy.md`
- Create: `spring-core-modules/spring-core-beans/docs/lazy-semantics.md`
- Create: `spring-core-modules/spring-core-beans/docs/lifecycle-callbacks.md`
- Create: `spring-core-modules/spring-core-beans/docs/smart-initializing-singleton.md`
- Create: `spring-core-modules/spring-core-beans/docs/smart-lifecycle.md`
- Create: `spring-core-modules/spring-core-beans/docs/early-reference-and-three-level-cache.md`
- Create: `spring-core-modules/spring-core-beans/docs/proxying-phase.md`
- Modify: `spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md`
- Modify: `spring-core-modules/spring-core-beans/README.md`

- [ ] **Step 1: Write scope documents**

Create `scope-and-prototype.md`, `custom-scope-and-scoped-proxy.md`, and `lazy-semantics.md` according to `DOCUMENTATION_SPEC.md`.

Use:

```text
SpringCoreBeansLabTest
SpringCoreBeansPrototypeDestroySemanticsLabTest
SpringCoreBeansCustomScopeLabTest
SpringCoreBeansLazyLabTest
```

- [ ] **Step 2: Write lifecycle documents**

Create `lifecycle-callbacks.md`, `smart-initializing-singleton.md`, and `smart-lifecycle.md`.

Use:

```text
SpringCoreBeansLifecycleCallbackOrderLabTest
SpringCoreBeansLifecycleRawVsProxyLabTest
SpringCoreBeansSmartInitializingSingletonLabTest
SpringCoreBeansSmartLifecycleLabTest
```

- [ ] **Step 3: Write circular dependency and proxy documents**

Create `early-reference-and-three-level-cache.md` and `proxying-phase.md`.

Use:

```text
SpringCoreBeansCircularDependencyBoundaryLabTest
SpringCoreBeansEarlyReferenceLabTest
SpringCoreBeansRawInjectionDespiteWrappingLabTest
SpringCoreBeansProxyingPhaseLabTest
```

- [ ] **Step 4: Update README and knowledge map**

Add Markdown links for all new docs.

- [ ] **Step 5: Run documentation contract**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected: build exits 0.

## Task 5: Write Extension, FactoryBean, and External Input Documents

**Files:**
- Create: `spring-core-modules/spring-core-beans/docs/post-processors-overview.md`
- Create: `spring-core-modules/spring-core-beans/docs/beanpost-processors.md`
- Create: `spring-core-modules/spring-core-beans/docs/factorybean.md`
- Create: `spring-core-modules/spring-core-beans/docs/factorybean-type-matching.md`
- Create: `spring-core-modules/spring-core-beans/docs/xml-bean-definition-reader.md`
- Create: `spring-core-modules/spring-core-beans/docs/properties-and-groovy-reader.md`
- Create: `spring-core-modules/spring-core-beans/docs/xml-namespace-extension.md`
- Modify: `spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md`
- Modify: `spring-core-modules/spring-core-beans/README.md`

- [ ] **Step 1: Write post-processor documents**

Create `post-processors-overview.md` and `beanpost-processors.md`.

Use:

```text
SpringCoreBeansStaticBeanFactoryPostProcessorLabTest
SpringCoreBeansRegistryPostProcessorLabTest
SpringCoreBeansPostProcessorOrderingLabTest
SpringCoreBeansLifecycleRawVsProxyLabTest
SpringCoreBeansProgrammaticBeanPostProcessorLabTest
```

- [ ] **Step 2: Write FactoryBean documents**

Create `factorybean.md` and `factorybean-type-matching.md`.

Use:

```text
SpringCoreBeansFactoryBeanDeepDiveLabTest
SpringCoreBeansFactoryBeanEdgeCasesLabTest
SpringCoreBeansServiceLoaderFactoryBeansLabTest
```

- [ ] **Step 3: Write external input documents**

Create `xml-bean-definition-reader.md`, `properties-and-groovy-reader.md`, and `xml-namespace-extension.md`.

Use:

```text
SpringCoreBeansXmlBeanDefinitionReaderLabTest
SpringCoreBeansPropertiesBeanDefinitionReaderLabTest
SpringCoreBeansGroovyBeanDefinitionReaderLabTest
SpringCoreBeansXmlNamespaceExtensionLabTest
```

- [ ] **Step 4: Update README and knowledge map**

Add Markdown links for all new docs.

- [ ] **Step 5: Run documentation contract**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected: build exits 0.

## Task 6: Write Boot, AOT, Guide, and Appendix Documents

**Files:**
- Create: `spring-core-modules/spring-core-beans/docs/boot-auto-configuration-beans.md`
- Create: `spring-core-modules/spring-core-beans/docs/aot-native-overview.md`
- Create: `spring-core-modules/spring-core-beans/docs/guide-quickstart-30min.md`
- Create: `spring-core-modules/spring-core-beans/docs/guide-mainline-timeline.md`
- Create: `spring-core-modules/spring-core-beans/docs/guide-breakpoint-map.md`
- Create: `spring-core-modules/spring-core-beans/docs/guide-deep-dive-guide.md`
- Create: `spring-core-modules/spring-core-beans/docs/appendix-common-pitfalls.md`
- Create: `spring-core-modules/spring-core-beans/docs/appendix-production-troubleshooting-checklist.md`
- Modify: `spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md`
- Modify: `spring-core-modules/spring-core-beans/README.md`

- [ ] **Step 1: Write Boot and AOT documents**

Create `boot-auto-configuration-beans.md` and `aot-native-overview.md`.

Use:

```text
SpringCoreBeansAutoConfigurationLabTest
SpringCoreBeansAutoConfigurationBackoffTimingLabTest
SpringCoreBeansAutoConfigurationOverrideMatrixLabTest
SpringCoreBeansConditionEvaluationReportLabTest
SpringCoreBeansAotRuntimeHintsLabTest
SpringCoreBeansRuntimeHintsBoundaryLabTest
SpringCoreBeansAotFactoriesLabTest
```

- [ ] **Step 2: Write guide documents**

Create `guide-quickstart-30min.md`, `guide-mainline-timeline.md`, `guide-breakpoint-map.md`, and `guide-deep-dive-guide.md`.

Use:

```text
SpringCoreBeansLabTest
SpringCoreBeansMainlineCallChainLabTest
SpringCoreBeansBreakpointPackLabTest
SpringCoreBeansModuleContractLabTest
```

- [ ] **Step 3: Write appendix documents**

Create `appendix-common-pitfalls.md` and `appendix-production-troubleshooting-checklist.md`.

Use:

```text
SpringCoreBeansTroubleshootingPlaybookLabTest
SpringCoreBeansExceptionNavigationLabTest
SpringCoreBeansModuleContractLabTest
```

- [ ] **Step 4: Update README and knowledge map**

Add Markdown links for all new docs.

- [ ] **Step 5: Run documentation contract and module contract**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest,SpringCoreBeansModuleContractLabTest test
```

Expected: build exits 0.

## Task 7: Self-Review the Documentation Set

**Files:**
- Review: `spring-core-modules/spring-core-beans/docs/*.md`
- Review: `spring-core-modules/spring-core-beans/README.md`
- Review: `spring-core-modules/spring-core-beans/DOCUMENTATION_SPEC.md`

- [ ] **Step 1: Check fixed-template drift**

Run:

```bash
rg -n "相邻主题|上一页|下一页|固定模板|待补|占位" spring-core-modules/spring-core-beans/docs spring-core-modules/spring-core-beans/README.md
```

Expected: no placeholder wording; any `相邻主题` occurrence must be intentional and justified by a real owner boundary.

- [ ] **Step 2: Check chapter-card markers**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected: build exits 0.

- [ ] **Step 3: Check full module contract**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest,SpringCoreBeansModuleContractLabTest test
```

Expected: build exits 0.

- [ ] **Step 4: Review git diff**

Run:

```bash
git diff -- spring-core-modules/spring-core-beans README.md docs/superpowers/plans/2026-05-16-spring-core-beans-docs.md
```

Expected: only intended documentation changes appear.
