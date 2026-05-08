# spring-core-beans Docs Rewrite Phase 1 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rewrite the first container-and-registration batch of `spring-core-beans` docs from template skeletons into knowledge-point-specific tutorial and source-reading documents.

**Architecture:** Keep existing filenames, README ordering, chapter-card markers, and Lab references intact. Rewrite prose in small owner groups: foundation model, registration/bootstrap/mainline, post-processor family, then cross-link and verify. The work is Markdown-first; Java code changes are out of scope unless verification exposes a false documented Lab reference.

**Tech Stack:** Markdown, Spring Boot 3.5.9 / Spring Framework 6.x APIs via the current Maven dependency tree, Maven, JUnit 5, existing `SpringCoreBeans*Test` Labs, `SpringCoreBeansDocumentationContractTest`, `rg`, `git`.

---

## Governing Spec

- Design spec: `docs/superpowers/specs/2026-05-08-spring-core-beans-docs-rewrite-design.md`
- Module root: `spring-core-modules/spring-core-beans`
- Module docs: `spring-core-modules/spring-core-beans/docs`
- Contract test: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansDocumentationContractTest.java`
- Module contract suite: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansModuleContractLabTest.java`

If this plan conflicts with the design spec, pause and update the plan before changing docs.

---

## File Map

### Phase 1 Main Docs

- Modify: `spring-core-modules/spring-core-beans/docs/bean-mental-model.md`
  - Responsibility: relationship between `BeanDefinition`, bean instance, singleton cache, `FactoryBean` product, early reference, and final exposed object.
- Modify: `spring-core-modules/spring-core-beans/docs/bean-definition-registration.md`
  - Responsibility: how definitions enter `BeanDefinitionRegistry` through scanning, `@Bean`, import registrar, and programmatic registration; distinguish `registerSingleton`.
- Modify: `spring-core-modules/spring-core-beans/docs/refresh-mainline.md`
  - Responsibility: `ApplicationContext#refresh()` phase order; do not expand single-bean `doCreateBean()` internals.
- Modify: `spring-core-modules/spring-core-beans/docs/container-bootstrap-and-infrastructure.md`
  - Responsibility: why annotation processors, autowiring, `@PostConstruct`, and configuration parsing work only after infrastructure processors are registered.
- Modify: `spring-core-modules/spring-core-beans/docs/post-processors-overview.md`
  - Responsibility: BFPP, BDRPP, and BPP phase boundaries; route to detailed pages.
- Modify: `spring-core-modules/spring-core-beans/docs/beanfactory-post-processors.md`
  - Responsibility: BFPP window, static `@Bean` BFPP, early configuration instantiation risk.
- Modify: `spring-core-modules/spring-core-beans/docs/bdrpp-definition-registration.md`
  - Responsibility: BDRPP registry callback, repeated discovery, before-regular-BFPP behavior, early `getBean()` risk.
- Modify: `spring-core-modules/spring-core-beans/docs/beanpost-processors.md`
  - Responsibility: BPP before/after initialization windows and final object replacement.
- Modify: `spring-core-modules/spring-core-beans/docs/post-processor-ordering.md`
  - Responsibility: `PriorityOrdered`, `Ordered`, unordered phases, order values, and why `@Order` alone is not enough for BPP grouping.
- Modify: `spring-core-modules/spring-core-beans/docs/bean-creation-mainline.md`
  - Responsibility: `doGetBean()` / `doCreateBean()` mental call chain and single-bean phases from dependency resolution to final exposure.

### Files To Check But Not Normally Modify

- Check: `spring-core-modules/spring-core-beans/README.md`
  - Reason: Phase 1 keeps filenames and directory order, so README should not need changes.
- Check: `spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md`
  - Reason: Phase 1 keeps owner rows, but wording should remain consistent with rewritten docs.
- Check: `spring-core-modules/spring-core-beans/docs/guide-applicationcontext-refresh-call-chain.md`
  - Reason: It routes to `refresh-mainline.md`, `container-bootstrap-and-infrastructure.md`, and `bean-creation-mainline.md`.
- Check: `spring-core-modules/spring-core-beans/docs/guide-breakpoint-map.md`
  - Reason: It routes to `refresh-mainline.md` and `bean-creation-mainline.md`.
- Check: `spring-core-modules/spring-core-beans/docs/deepening-container-internals.md`
  - Reason: It names phase-1 container internals owners.

---

## Shared Writing Rules For Every Doc

- Keep `<!-- CHAPTER-CARD:START -->` and `<!-- CHAPTER-CARD:END -->`.
- Preserve at least one real `SpringCoreBeans*Test` reference already used by the file.
- Remove generic template phrases:
  - `本页负责把这个问题收束到一个可运行证据入口`
  - `相邻主题只在“相邻跳转”中出现`
  - `不用未验证的口头结论替代 Lab`
  - `完成标准是：读者能用上面的 Lab 证明`
- Use a structure that fits the topic. Valid structures include source call chain, phase timeline, comparison table, failure branch, or Lab reading guide.
- Keep support-page behavior out of main docs: do not turn a main doc into a link list.
- Keep adjacent ownership clear by using links for secondary topics.

---

### Task 1: Baseline And Anti-Template Guard

**Files:**
- Read: `docs/superpowers/specs/2026-05-08-spring-core-beans-docs-rewrite-design.md`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansDocumentationContractTest.java`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansModuleContractLabTest.java`
- Modify: none

- [ ] **Step 1: Re-read governing constraints**

Read:

```bash
sed -n '1,220p' docs/superpowers/specs/2026-05-08-spring-core-beans-docs-rewrite-design.md
sed -n '1,240p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansDocumentationContractTest.java
sed -n '1,120p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansModuleContractLabTest.java
```

Expected: confirm README coverage, link resolution, chapter-card markers, and existing test-class references are contractual.

- [ ] **Step 2: Run the existing documentation contract before edits**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected: build exits 0 before the rewrite. If it fails before any doc edit, record the failing assertion in the implementation notes and fix only if the failure blocks Phase 1 verification.

- [ ] **Step 3: Capture the current template phrase count**

Run:

```bash
rg -n "本页负责把这个问题收束|相邻主题只在|不用未验证的口头结论替代 Lab|完成标准是：读者能用上面的 Lab 证明" spring-core-modules/spring-core-beans/docs/bean-mental-model.md spring-core-modules/spring-core-beans/docs/bean-definition-registration.md spring-core-modules/spring-core-beans/docs/refresh-mainline.md spring-core-modules/spring-core-beans/docs/container-bootstrap-and-infrastructure.md spring-core-modules/spring-core-beans/docs/post-processors-overview.md spring-core-modules/spring-core-beans/docs/beanfactory-post-processors.md spring-core-modules/spring-core-beans/docs/bdrpp-definition-registration.md spring-core-modules/spring-core-beans/docs/beanpost-processors.md spring-core-modules/spring-core-beans/docs/post-processor-ordering.md spring-core-modules/spring-core-beans/docs/bean-creation-mainline.md
```

Expected: matches exist before Phase 1 edits. After Task 5, the same command should return no matches for these 10 files.

- [ ] **Step 4: Commit baseline only if files changed**

Run:

```bash
git status --short
```

Expected: no changes from Task 1. Do not commit if the worktree is clean.

---

### Task 2: Rewrite Foundation Model Docs

**Files:**
- Modify: `spring-core-modules/spring-core-beans/docs/bean-mental-model.md`
- Modify: `spring-core-modules/spring-core-beans/docs/bean-definition-registration.md`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanDefinitionRegistrationDiffLabTest.java`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansComponentScanLabTest.java`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/testsupport/BeanDefinitionOriginDumper.java`

- [ ] **Step 1: Read the Lab evidence**

Run:

```bash
sed -n '1,260p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java
sed -n '1,220p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBeanDefinitionRegistrationDiffLabTest.java
sed -n '1,220p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansComponentScanLabTest.java
sed -n '1,220p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/testsupport/BeanDefinitionOriginDumper.java
```

Expected evidence to use:

- `beanDefinitionIsNotTheBeanInstance()`
- `factoryBeanByNameReturnsProductAndAmpersandReturnsFactory()`
- `circularDependencyWithSettersMaySucceedViaEarlySingletonExposure()`
- `beanDefinitionMetadata_differsAcrossRegistrationMechanisms()`
- `BeanDefinitionOriginDumper.dump(...)`

- [ ] **Step 2: Rewrite `bean-mental-model.md`**

Replace the template body after the chapter card with a topic-specific document using these sections:

```markdown
## 先分清四个对象
## BeanDefinition 不是 Bean
## 单例缓存缓存的是什么
## FactoryBean 会让名字和对象分叉
## early reference 不是最终对象的同义词
## 读源码时看哪些入口
## 用本模块怎么验证
## 相邻主题
```

Required content:

- Explain `BeanDefinition` as metadata in `DefaultListableBeanFactory`, not the runtime object.
- Explain singleton cache in `DefaultSingletonBeanRegistry` as a runtime exposure mechanism, not a definition registry.
- Explain that `getBean("sequence")` returns the `FactoryBean` product while `getBean("&sequence")` returns the factory, using the existing Lab name.
- Explain that early reference exists to resolve some setter/field cycles and may interact with proxies, but link detailed mechanics to `early-reference-and-three-level-cache.md`.
- Source-reading anchors must include `DefaultListableBeanFactory#getBeanDefinition`, `AbstractBeanFactory#doGetBean`, `DefaultSingletonBeanRegistry#getSingleton`, and `FactoryBeanRegistrySupport#getObjectFromFactoryBean`.
- Keep links to `bean-definition-registration.md`, `bean-creation-mainline.md`, `factorybean.md`, `early-reference-and-three-level-cache.md`, and `appendix-knowledge-map.md`.

- [ ] **Step 3: Rewrite `bean-definition-registration.md`**

Replace the template body after the chapter card with a topic-specific document using these sections:

```markdown
## 注册解决的是“容器知道什么”
## 四种入口放进去的定义不一样
## registerSingleton 的边界
## refresh 前后的分界线
## 源码阅读顺序
## 用本模块怎么验证
## 相邻主题
```

Required content:

- Compare component scan, `@Bean` factory method, `ImportBeanDefinitionRegistrar`, programmatic `RootBeanDefinition`, and `registerSingleton`.
- State that `registerSingleton` contributes a singleton instance without a normal `BeanDefinition`; use the `singletonBean` assertion from `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest`.
- Source-reading anchors must include `BeanDefinitionRegistry#registerBeanDefinition`, `DefaultListableBeanFactory#registerBeanDefinition`, `ClassPathBeanDefinitionScanner#doScan`, `ConfigurationClassPostProcessor#processConfigBeanDefinitions`, and `DefaultSingletonBeanRegistry#registerSingleton`.
- Link details out to `import-selector-and-registrar.md`, `programmatic-registration.md`, `bean-definition-metadata-and-origin.md`, and `bean-name-and-alias.md`.

- [ ] **Step 4: Verify Task 2 docs**

Run:

```bash
rg -n "本页负责把这个问题收束|相邻主题只在|不用未验证的口头结论替代 Lab|完成标准是：读者能用上面的 Lab 证明" spring-core-modules/spring-core-beans/docs/bean-mental-model.md spring-core-modules/spring-core-beans/docs/bean-definition-registration.md
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected:

- `rg` returns no matches for these two files.
- Maven exits 0.

- [ ] **Step 5: Commit Task 2**

Run:

```bash
git add spring-core-modules/spring-core-beans/docs/bean-mental-model.md spring-core-modules/spring-core-beans/docs/bean-definition-registration.md
git commit -m "docs(beans): rewrite container foundation docs"
```

Expected: commit contains only the two rewritten docs.

---

### Task 3: Rewrite Refresh, Bootstrap, And Creation Mainline Docs

**Files:**
- Modify: `spring-core-modules/spring-core-beans/docs/refresh-mainline.md`
- Modify: `spring-core-modules/spring-core-beans/docs/container-bootstrap-and-infrastructure.md`
- Modify: `spring-core-modules/spring-core-beans/docs/bean-creation-mainline.md`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansMainlineCallChainLabTest.java`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansInfrastructureBeanRoleLabTest.java`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java`

- [ ] **Step 1: Read the Lab evidence**

Run:

```bash
sed -n '1,120p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansMainlineCallChainLabTest.java
sed -n '1,220p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBootstrapInternalsLabTest.java
sed -n '1,180p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansInfrastructureBeanRoleLabTest.java
sed -n '1,260p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java
```

Expected evidence to use:

- Without `AnnotationConfigUtils.registerAnnotationConfigProcessors`, `@Autowired`, `@PostConstruct`, and `@Bean` parsing do not happen in the generic context.
- Infrastructure definitions have `BeanDefinition.ROLE_INFRASTRUCTURE`.
- Bean creation trace is constructor, `InstantiationAwareBeanPostProcessor`, property population, before-init BPP, init callback, after-init proxy replacement.

- [ ] **Step 2: Rewrite `refresh-mainline.md`**

Replace the template body after the chapter card with a source-timeline document using these sections:

```markdown
## refresh 是上下文装配，不是单个 Bean 的创建细节
## 主线时间线
## 哪些阶段还在改 BeanDefinition
## 哪些阶段开始影响 Bean 实例
## 源码阅读顺序
## 用本模块怎么验证
## 相邻主题
```

Required content:

- Describe `refresh()` as the context-level sequence from preparation to `finishBeanFactoryInitialization`.
- Mention definition mutation windows: `invokeBeanFactoryPostProcessors` before `registerBeanPostProcessors`.
- Mention singleton creation window: `finishBeanFactoryInitialization` triggers non-lazy singleton pre-instantiation.
- Source-reading anchors must include `AbstractApplicationContext#refresh`, `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`, `PostProcessorRegistrationDelegate#registerBeanPostProcessors`, `AbstractApplicationContext#finishBeanFactoryInitialization`, and `DefaultListableBeanFactory#preInstantiateSingletons`.
- Link single-bean internals to `bean-creation-mainline.md`.

- [ ] **Step 3: Rewrite `container-bootstrap-and-infrastructure.md`**

Replace the template body after the chapter card with a bootstrap-focused document using these sections:

```markdown
## 基础设施 Bean 先让容器具备能力
## AnnotationConfigUtils 注册了哪些关键处理器
## 没有这些处理器会发生什么
## ROLE_INFRASTRUCTURE 的排障价值
## 源码阅读顺序
## 用本模块怎么验证
## 相邻主题
```

Required content:

- Explain why `@Autowired`, `@PostConstruct`, and `@Configuration` parsing are processor-driven behavior.
- Use the `GenericApplicationContext` contrast from `SpringCoreBeansBootstrapInternalsLabTest`.
- Explain `ROLE_INFRASTRUCTURE` as a source/debugging classifier, not a lifecycle phase.
- Source-reading anchors must include `AnnotationConfigUtils#registerAnnotationConfigProcessors`, `ConfigurationClassPostProcessor`, `AutowiredAnnotationBeanPostProcessor`, `CommonAnnotationBeanPostProcessor`, and `BeanDefinition#ROLE_INFRASTRUCTURE`.

- [ ] **Step 4: Rewrite `bean-creation-mainline.md`**

Replace the template body after the chapter card with a single-bean creation guide using these sections:

```markdown
## 这条线从 getBean 开始
## doGetBean 决定拿缓存还是创建
## doCreateBean 的几个窗口
## populateBean 之前和之后能观察到什么
## initializeBean 之后可能换成另一个对象
## 源码阅读顺序
## 用本模块怎么验证
## 相邻主题
```

Required content:

- Explain the high-level call chain: `getBean -> doGetBean -> createBean -> doCreateBean`.
- Explain phases shown by `SpringCoreBeansBeanCreationTraceLabTest`: constructor, `postProcessAfterInstantiation`, `postProcessProperties`, setter injection, `postProcessBeforeInitialization`, `afterPropertiesSet`, `postProcessAfterInitialization`.
- State that final exposed object can be a proxy and concrete-class lookup can fail after JDK proxy replacement; link deeper proxy behavior to `proxying-phase.md`.
- Source-reading anchors must include `AbstractBeanFactory#doGetBean`, `AbstractAutowireCapableBeanFactory#createBean`, `AbstractAutowireCapableBeanFactory#doCreateBean`, `AbstractAutowireCapableBeanFactory#populateBean`, and `AbstractAutowireCapableBeanFactory#initializeBean`.

- [ ] **Step 5: Verify Task 3 docs**

Run:

```bash
rg -n "本页负责把这个问题收束|相邻主题只在|不用未验证的口头结论替代 Lab|完成标准是：读者能用上面的 Lab 证明" spring-core-modules/spring-core-beans/docs/refresh-mainline.md spring-core-modules/spring-core-beans/docs/container-bootstrap-and-infrastructure.md spring-core-modules/spring-core-beans/docs/bean-creation-mainline.md
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected:

- `rg` returns no matches for these three files.
- Maven exits 0.

- [ ] **Step 6: Commit Task 3**

Run:

```bash
git add spring-core-modules/spring-core-beans/docs/refresh-mainline.md spring-core-modules/spring-core-beans/docs/container-bootstrap-and-infrastructure.md spring-core-modules/spring-core-beans/docs/bean-creation-mainline.md
git commit -m "docs(beans): rewrite refresh and creation mainline docs"
```

Expected: commit contains only the three rewritten docs.

---

### Task 4: Rewrite Post-Processor Family Docs

**Files:**
- Modify: `spring-core-modules/spring-core-beans/docs/post-processors-overview.md`
- Modify: `spring-core-modules/spring-core-beans/docs/beanfactory-post-processors.md`
- Modify: `spring-core-modules/spring-core-beans/docs/bdrpp-definition-registration.md`
- Modify: `spring-core-modules/spring-core-beans/docs/beanpost-processors.md`
- Modify: `spring-core-modules/spring-core-beans/docs/post-processor-ordering.md`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansStaticBeanFactoryPostProcessorLabTest.java`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRegistryPostProcessorLabTest.java`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansLifecycleRawVsProxyLabTest.java`
- Read: `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java`

- [ ] **Step 1: Read the Lab evidence**

Run:

```bash
sed -n '1,240p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansStaticBeanFactoryPostProcessorLabTest.java
sed -n '1,260p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansRegistryPostProcessorLabTest.java
sed -n '1,320p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansPostProcessorOrderingLabTest.java
sed -n '1,180p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansLifecycleRawVsProxyLabTest.java
sed -n '1,260p' spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java
```

Expected evidence to use:

- Non-static `@Bean` BFPP instantiates its configuration class early and misses later BPPs.
- Static `@Bean` BFPP avoids early configuration instantiation.
- BDRPP can register definitions before regular BFPP modifies them.
- Calling `getBean()` during BDRPP/BFPP can create a bean before BPP registration.
- BPP can replace the final exposed object with a JDK or CGLIB proxy.
- Ordering groups are `PriorityOrdered`, `Ordered`, then unordered; smaller order value runs earlier inside ordered groups; `@Order` alone does not move a BPP into the `Ordered` group.

- [ ] **Step 2: Rewrite `post-processors-overview.md`**

Replace the template body after the chapter card with a comparison-and-routing document using these sections:

```markdown
## 先按阶段分，不按名字分
## 三类处理器的能力边界
## 为什么 BDRPP 要单独看
## 为什么 BPP 不应该改 BeanDefinition
## 读源码时先看委派器
## 用本模块怎么验证
## 相邻主题
```

Required content:

- Include a concise comparison of BFPP, BDRPP, and BPP by phase, callback, mutable target, and common failure.
- Source-reading anchors must include `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`, `PostProcessorRegistrationDelegate#registerBeanPostProcessors`, `BeanDefinitionRegistryPostProcessor`, `BeanFactoryPostProcessor`, and `BeanPostProcessor`.
- Link to all four detailed post-processor docs in this task plus `programmatic-bpp-registration.md`.

- [ ] **Step 3: Rewrite `beanfactory-post-processors.md`**

Replace the template body after the chapter card with a BFPP-specific document using these sections:

```markdown
## BFPP 改的是定义，不是普通 Bean 实例
## static @Bean 为什么重要
## 非 static BFPP 的早期实例化问题
## BFPP 阶段不要 getBean
## 源码阅读顺序
## 用本模块怎么验证
## 相邻主题
```

Required content:

- Explain the timing: after definitions are loaded, before ordinary singletons are created and before BPPs are registered.
- Use the exact event contrast from `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest`.
- State that BFPP may mutate `BeanDefinition` property values but should not force ordinary bean creation.
- Source-reading anchors must include `BeanFactoryPostProcessor#postProcessBeanFactory`, `PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`, and `ConfigurationClassEnhancer` only when explaining why the configuration class itself should not be forced too early.

- [ ] **Step 4: Rewrite `bdrpp-definition-registration.md`**

Replace the template body after the chapter card with a BDRPP-specific document using these sections:

```markdown
## BDRPP 多了一个 registry 窗口
## 新定义为什么能被后续 BFPP 看见
## 重复发现窗口是什么
## 早期 getBean 会破坏什么
## 源码阅读顺序
## 用本模块怎么验证
## 相邻主题
```

Required content:

- Explain `postProcessBeanDefinitionRegistry` before regular BFPP.
- Explain why BDRPP-registered `registeredBean` can later be modified by `Modifier`.
- Explain early `getBean()` risk using `earlyTarget` and `lateTarget`.
- Source-reading anchors must include `BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry`, `PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors`, and `DefaultListableBeanFactory#getBeanNamesForType`.

- [ ] **Step 5: Rewrite `beanpost-processors.md`**

Replace the template body after the chapter card with an instance-window document using these sections:

```markdown
## BPP 看到的是实例创建过程
## beforeInitialization 和 afterInitialization 的差异
## InstantiationAwareBeanPostProcessor 介入属性填充
## afterInitialization 可以替换最终暴露对象
## 源码阅读顺序
## 用本模块怎么验证
## 相邻主题
```

Required content:

- Explain that BPP does not own `BeanDefinition` registration.
- Use `LifecycleRawVsProxyLabTest` to show `@PostConstruct` runs on raw object while final exposed bean can be proxy.
- Use `BeanCreationTraceLabTest` as the phase evidence for `InstantiationAwareBeanPostProcessor`.
- Source-reading anchors must include `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`, `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`, `InstantiationAwareBeanPostProcessor#postProcessProperties`, and `AbstractAutowireCapableBeanFactory#initializeBean`.

- [ ] **Step 6: Rewrite `post-processor-ordering.md`**

Replace the template body after the chapter card with an ordering rules document using these sections:

```markdown
## 排序先分组，再看 order 值
## BFPP 的排序
## BPP 的排序
## @Order 和 Ordered 不是同一件事
## 手工注册为什么另算
## 源码阅读顺序
## 用本模块怎么验证
## 相邻主题
```

Required content:

- Explain `PriorityOrdered -> Ordered -> unordered`.
- Explain smaller order values run earlier inside ordered groups.
- Explain `@Order` on a BPP class does not put it into the `Ordered` group unless the processor participates through the relevant ordering mechanism shown by Spring registration logic.
- Link manual registration behavior to `programmatic-bpp-registration.md`.
- Source-reading anchors must include `PostProcessorRegistrationDelegate#sortPostProcessors`, `PriorityOrdered`, `Ordered`, and `AnnotationAwareOrderComparator`.

- [ ] **Step 7: Verify Task 4 docs**

Run:

```bash
rg -n "本页负责把这个问题收束|相邻主题只在|不用未验证的口头结论替代 Lab|完成标准是：读者能用上面的 Lab 证明" spring-core-modules/spring-core-beans/docs/post-processors-overview.md spring-core-modules/spring-core-beans/docs/beanfactory-post-processors.md spring-core-modules/spring-core-beans/docs/bdrpp-definition-registration.md spring-core-modules/spring-core-beans/docs/beanpost-processors.md spring-core-modules/spring-core-beans/docs/post-processor-ordering.md
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected:

- `rg` returns no matches for these five files.
- Maven exits 0.

- [ ] **Step 8: Commit Task 4**

Run:

```bash
git add spring-core-modules/spring-core-beans/docs/post-processors-overview.md spring-core-modules/spring-core-beans/docs/beanfactory-post-processors.md spring-core-modules/spring-core-beans/docs/bdrpp-definition-registration.md spring-core-modules/spring-core-beans/docs/beanpost-processors.md spring-core-modules/spring-core-beans/docs/post-processor-ordering.md
git commit -m "docs(beans): rewrite post processor docs"
```

Expected: commit contains only the five rewritten docs.

---

### Task 5: Cross-Link, Consistency, And Template Sweep

**Files:**
- Check: all 10 Phase 1 main docs
- Check/Modify only if needed: `spring-core-modules/spring-core-beans/README.md`
- Check/Modify only if needed: `spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md`
- Check/Modify only if needed: `spring-core-modules/spring-core-beans/docs/guide-applicationcontext-refresh-call-chain.md`
- Check/Modify only if needed: `spring-core-modules/spring-core-beans/docs/guide-breakpoint-map.md`
- Check/Modify only if needed: `spring-core-modules/spring-core-beans/docs/deepening-container-internals.md`

- [ ] **Step 1: Check all Phase 1 docs for forbidden template phrases**

Run:

```bash
rg -n "本页负责把这个问题收束|相邻主题只在|不用未验证的口头结论替代 Lab|完成标准是：读者能用上面的 Lab 证明" spring-core-modules/spring-core-beans/docs/bean-mental-model.md spring-core-modules/spring-core-beans/docs/bean-definition-registration.md spring-core-modules/spring-core-beans/docs/refresh-mainline.md spring-core-modules/spring-core-beans/docs/container-bootstrap-and-infrastructure.md spring-core-modules/spring-core-beans/docs/post-processors-overview.md spring-core-modules/spring-core-beans/docs/beanfactory-post-processors.md spring-core-modules/spring-core-beans/docs/bdrpp-definition-registration.md spring-core-modules/spring-core-beans/docs/beanpost-processors.md spring-core-modules/spring-core-beans/docs/post-processor-ordering.md spring-core-modules/spring-core-beans/docs/bean-creation-mainline.md
```

Expected: no output. If output appears, rewrite that sentence into topic-specific prose.

- [ ] **Step 2: Check chapter-card markers remain present**

Run:

```bash
for f in spring-core-modules/spring-core-beans/docs/bean-mental-model.md spring-core-modules/spring-core-beans/docs/bean-definition-registration.md spring-core-modules/spring-core-beans/docs/refresh-mainline.md spring-core-modules/spring-core-beans/docs/container-bootstrap-and-infrastructure.md spring-core-modules/spring-core-beans/docs/post-processors-overview.md spring-core-modules/spring-core-beans/docs/beanfactory-post-processors.md spring-core-modules/spring-core-beans/docs/bdrpp-definition-registration.md spring-core-modules/spring-core-beans/docs/beanpost-processors.md spring-core-modules/spring-core-beans/docs/post-processor-ordering.md spring-core-modules/spring-core-beans/docs/bean-creation-mainline.md; do rg -q "<!-- CHAPTER-CARD:START -->" "$f" && rg -q "<!-- CHAPTER-CARD:END -->" "$f" || printf 'missing chapter card: %s\n' "$f"; done
```

Expected: no output.

- [ ] **Step 3: Check local links and support-page consistency**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected: Maven exits 0. If it fails because a local link changed, fix the link in the doc that introduced it. If it fails because a referenced test class is missing, either remove that reference or add the correct existing test class name.

- [ ] **Step 4: Manually inspect support files only for drift**

Run:

```bash
sed -n '1,180p' spring-core-modules/spring-core-beans/README.md
sed -n '1,130p' spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md
sed -n '1,80p' spring-core-modules/spring-core-beans/docs/guide-applicationcontext-refresh-call-chain.md
sed -n '1,80p' spring-core-modules/spring-core-beans/docs/guide-breakpoint-map.md
sed -n '1,80p' spring-core-modules/spring-core-beans/docs/deepening-container-internals.md
```

Expected: no required changes when filenames and owner questions remain stable. If rewritten docs changed a title or owner question materially, update only the affected line in README or `appendix-knowledge-map.md`.

- [ ] **Step 5: Commit Task 5 if support files changed**

Run:

```bash
git status --short
```

If only support consistency edits are present, run:

```bash
git add spring-core-modules/spring-core-beans/README.md spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md spring-core-modules/spring-core-beans/docs/guide-applicationcontext-refresh-call-chain.md spring-core-modules/spring-core-beans/docs/guide-breakpoint-map.md spring-core-modules/spring-core-beans/docs/deepening-container-internals.md
git commit -m "docs(beans): align container doc navigation"
```

Expected: skip the commit when no support files changed.

---

### Task 6: Final Phase 1 Verification

**Files:**
- Verify: `spring-core-modules/spring-core-beans/docs/*.md`
- Verify: `spring-core-modules/spring-core-beans/README.md`
- Verify: existing Lab tests

- [ ] **Step 1: Run the anti-template sweep for Phase 1 files**

Run:

```bash
rg -n "本页负责把这个问题收束|相邻主题只在|不用未验证的口头结论替代 Lab|完成标准是：读者能用上面的 Lab 证明" spring-core-modules/spring-core-beans/docs/bean-mental-model.md spring-core-modules/spring-core-beans/docs/bean-definition-registration.md spring-core-modules/spring-core-beans/docs/refresh-mainline.md spring-core-modules/spring-core-beans/docs/container-bootstrap-and-infrastructure.md spring-core-modules/spring-core-beans/docs/post-processors-overview.md spring-core-modules/spring-core-beans/docs/beanfactory-post-processors.md spring-core-modules/spring-core-beans/docs/bdrpp-definition-registration.md spring-core-modules/spring-core-beans/docs/beanpost-processors.md spring-core-modules/spring-core-beans/docs/post-processor-ordering.md spring-core-modules/spring-core-beans/docs/bean-creation-mainline.md
```

Expected: no output.

- [ ] **Step 2: Run documentation contract**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected: Maven exits 0.

- [ ] **Step 3: Run module contract suite**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansModuleContractLabTest test
```

Expected: Maven exits 0.

- [ ] **Step 4: Run the phase-specific Lab set**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest,SpringCoreBeansBeanDefinitionRegistrationDiffLabTest,SpringCoreBeansComponentScanLabTest,SpringCoreBeansMainlineCallChainLabTest,SpringCoreBeansBootstrapInternalsLabTest,SpringCoreBeansInfrastructureBeanRoleLabTest,SpringCoreBeansStaticBeanFactoryPostProcessorLabTest,SpringCoreBeansRegistryPostProcessorLabTest,SpringCoreBeansPostProcessorOrderingLabTest,SpringCoreBeansLifecycleRawVsProxyLabTest,SpringCoreBeansProxyingPhaseLabTest,SpringCoreBeansBeanCreationTraceLabTest test
```

Expected: Maven exits 0.

- [ ] **Step 5: Review final diff**

Run:

```bash
git diff --stat
git diff -- spring-core-modules/spring-core-beans/docs/bean-mental-model.md spring-core-modules/spring-core-beans/docs/bean-definition-registration.md spring-core-modules/spring-core-beans/docs/refresh-mainline.md spring-core-modules/spring-core-beans/docs/container-bootstrap-and-infrastructure.md spring-core-modules/spring-core-beans/docs/post-processors-overview.md spring-core-modules/spring-core-beans/docs/beanfactory-post-processors.md spring-core-modules/spring-core-beans/docs/bdrpp-definition-registration.md spring-core-modules/spring-core-beans/docs/beanpost-processors.md spring-core-modules/spring-core-beans/docs/post-processor-ordering.md spring-core-modules/spring-core-beans/docs/bean-creation-mainline.md
```

Expected:

- Changes are limited to Phase 1 docs and any explicitly needed support consistency edits.
- Chapter cards still exist.
- Prose is knowledge-specific and does not repeat a shared skeleton.

- [ ] **Step 6: Commit any final verification fixes**

Run:

```bash
git status --short
```

If final verification fixes created uncommitted changes, run:

```bash
git add spring-core-modules/spring-core-beans/docs/bean-mental-model.md spring-core-modules/spring-core-beans/docs/bean-definition-registration.md spring-core-modules/spring-core-beans/docs/refresh-mainline.md spring-core-modules/spring-core-beans/docs/container-bootstrap-and-infrastructure.md spring-core-modules/spring-core-beans/docs/post-processors-overview.md spring-core-modules/spring-core-beans/docs/beanfactory-post-processors.md spring-core-modules/spring-core-beans/docs/bdrpp-definition-registration.md spring-core-modules/spring-core-beans/docs/beanpost-processors.md spring-core-modules/spring-core-beans/docs/post-processor-ordering.md spring-core-modules/spring-core-beans/docs/bean-creation-mainline.md spring-core-modules/spring-core-beans/README.md spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md spring-core-modules/spring-core-beans/docs/guide-applicationcontext-refresh-call-chain.md spring-core-modules/spring-core-beans/docs/guide-breakpoint-map.md spring-core-modules/spring-core-beans/docs/deepening-container-internals.md
git commit -m "docs(beans): verify container rewrite phase"
```

Expected: skip the commit when no final fixes are present.
