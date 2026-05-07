# spring-core-beans Knowledge-Point Docs Rewrite Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rewrite `spring-core-beans` documentation into one-owner-per-knowledge-point documents, with README/guide/appendix/deepening pages reduced to navigation, evidence, and maintenance roles.

**Architecture:** The rewrite is file-boundary first, prose second. Start by locking the target knowledge map and filename migration, then migrate or split existing content into canonical main documents, then shrink support documents, and only then run contract tests and patch missing evidence. Production code is out of scope unless a documented Lab or observation point cannot be made true otherwise.

**Tech Stack:** Markdown, Maven, JUnit 5, existing `SpringCoreBeans*Test` Labs, `SpringCoreBeansDocumentationContractTest`, `SpringCoreBeansModuleContractLabTest`, `git mv`, `rg`.

---

## Governing Spec

- Spec: `docs/specs/2026-05-07-spring-core-beans-docs-knowledge-point-rewrite-spec.md`
- Module root: `spring-core-modules/spring-core-beans`
- Module docs: `spring-core-modules/spring-core-beans/docs`
- Contract tests:
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansDocumentationContractTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansModuleContractLabTest.java`

This plan must not override the spec. If a task here conflicts with the spec, fix the plan before implementing the task.

---

## File Map

### Entry And Support Files

- Modify: `spring-core-modules/spring-core-beans/README.md`
  - Responsibility: module entry, shortest commands, reading routes, symptom navigation, complete docs order.
- Modify: `spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md`
  - Responsibility: full knowledge-point index; one row per main document; no mechanism prose.
- Modify: `spring-core-modules/spring-core-beans/docs/appendix-common-pitfalls.md`
  - Responsibility: misconception-to-main-doc pointer table only.
- Modify: `spring-core-modules/spring-core-beans/docs/appendix-glossary.md`
  - Responsibility: terms and one-line pointers only.
- Modify: `spring-core-modules/spring-core-beans/docs/guide-*.md`
  - Responsibility: reading route, breakpoint route, Lab route; no repeated mechanism explanation.
- Modify: `spring-core-modules/spring-core-beans/docs/deepening-*.md`
  - Responsibility: maintainer documentation; no tutorial body.
- Modify: `docs/book/03-spring-core-beans.md`
  - Responsibility: book-level route into the module; link to README and selected support docs only.

### Test Files

- Modify only if needed: `SpringCoreBeansDocumentationContractTest.java`
  - Responsibility: local Markdown links resolve; README lists docs files; documented `SpringCoreBeans*Test` classes exist.
- Modify only if needed: `SpringCoreBeansModuleContractLabTest.java`
  - Responsibility: suite includes documentation contract and testsupport output contracts.
- Do not modify production code unless a documented evidence chain is impossible to make true with docs/tests only.

### Final Main Documents

The final `spring-core-modules/spring-core-beans/docs` directory must contain these main documents, plus support documents:

```text
bean-mental-model.md
beanfactory-vs-applicationcontext.md
bean-definition-registration.md
bean-definition-metadata-and-origin.md
bean-name-and-alias.md
bean-definition-overriding.md
merged-bean-definition.md
configuration-and-bean-method.md
import-selector-and-registrar.md
programmatic-registration.md
refresh-mainline.md
container-bootstrap-and-infrastructure.md
post-processors-overview.md
beanfactory-post-processors.md
bdrpp-definition-registration.md
beanpost-processors.md
post-processor-ordering.md
programmatic-bpp-registration.md
pre-instantiation-short-circuit.md
bean-creation-mainline.md
dependency-injection-resolution.md
dependency-descriptor-and-injection-point.md
autowire-candidate-selection.md
qualifier-primary-priority-order.md
resource-vs-autowired.md
optional-and-provider-injection.md
resolvable-dependency.md
generic-type-matching.md
injection-phase.md
scope-and-prototype.md
custom-scope-and-scoped-proxy.md
lazy-semantics.md
depends-on.md
lifecycle-callbacks.md
smart-initializing-singleton.md
smart-lifecycle.md
circular-dependency.md
early-reference-and-three-level-cache.md
proxying-phase.md
factorybean.md
factorybean-type-matching.md
context-hierarchy.md
beanfactory-api-and-autowirecapablebeanfactory.md
environment-and-propertysource.md
value-placeholder-resolution.md
spel-and-value-expression.md
type-conversion-and-beanwrapper.md
xml-bean-definition-reader.md
properties-and-groovy-reader.md
xml-namespace-extension.md
method-injection.md
built-in-factorybeans.md
boot-auto-configuration-ordering.md
boot-auto-configuration-beans.md
aot-runtimehints.md
aot-xml-bean-definition-reader.md
aot-autowirecapablebeanfactory-external-objects.md
aot-spel-and-value-expression.md
aot-custom-qualifier.md
aot-xml-namespace-extension.md
aot-beandefinitionreader-other-inputs.md
aot-method-injection.md
aot-built-in-factorybeans.md
aot-property-editor-and-value-resolution.md
aot-native-overview.md
```

---

## Migration Map

### Direct Renames

Use `git mv` for these moves so history follows the new canonical names:

| Current file | Target file |
| --- | --- |
| `docs/ioc-bean-mental-model.md` | `docs/bean-mental-model.md` |
| `docs/wiring-beanfactory-api-deep-dive.md` | `docs/beanfactory-api-and-autowirecapablebeanfactory.md` |
| `docs/wiring-bean-definition-overriding.md` | `docs/bean-definition-overriding.md` |
| `docs/wiring-merged-bean-definition.md` | `docs/merged-bean-definition.md` |
| `docs/ioc-configuration-enhancement.md` | `docs/configuration-and-bean-method.md` |
| `docs/internals-container-bootstrap-and-infrastructure.md` | `docs/container-bootstrap-and-infrastructure.md` |
| `docs/internals-bdrpp-definition-registration.md` | `docs/bdrpp-definition-registration.md` |
| `docs/internals-post-processor-ordering.md` | `docs/post-processor-ordering.md` |
| `docs/wiring-programmatic-bpp-registration.md` | `docs/programmatic-bpp-registration.md` |
| `docs/internals-pre-instantiation-short-circuit.md` | `docs/pre-instantiation-short-circuit.md` |
| `docs/ioc-dependency-injection-resolution.md` | `docs/dependency-injection-resolution.md` |
| `docs/wiring-resource-injection-name-first.md` | `docs/resource-vs-autowired.md` |
| `docs/wiring-resolvable-dependency.md` | `docs/resolvable-dependency.md` |
| `docs/wiring-generic-type-matching-pitfalls.md` | `docs/generic-type-matching.md` |
| `docs/wiring-injection-phase-field-vs-constructor.md` | `docs/injection-phase.md` |
| `docs/ioc-scope-and-prototype.md` | `docs/scope-and-prototype.md` |
| `docs/wiring-custom-scope-and-scoped-proxy.md` | `docs/custom-scope-and-scoped-proxy.md` |
| `docs/wiring-lazy-semantics.md` | `docs/lazy-semantics.md` |
| `docs/wiring-depends-on.md` | `docs/depends-on.md` |
| `docs/ioc-lifecycle-and-callbacks.md` | `docs/lifecycle-callbacks.md` |
| `docs/wiring-smart-initializing-singleton.md` | `docs/smart-initializing-singleton.md` |
| `docs/wiring-smart-lifecycle-phase.md` | `docs/smart-lifecycle.md` |
| `docs/ioc-circular-dependencies.md` | `docs/circular-dependency.md` |
| `docs/internals-early-reference-and-circular.md` | `docs/early-reference-and-three-level-cache.md` |
| `docs/wiring-proxying-phase-bpp-wraps-bean.md` | `docs/proxying-phase.md` |
| `docs/ioc-factorybean.md` | `docs/factorybean.md` |
| `docs/wiring-context-hierarchy.md` | `docs/context-hierarchy.md` |
| `docs/wiring-environment-and-propertysource.md` | `docs/environment-and-propertysource.md` |
| `docs/wiring-value-placeholder-resolution-strict-vs-non-strict.md` | `docs/value-placeholder-resolution.md` |
| `docs/wiring-type-conversion-and-beanwrapper.md` | `docs/type-conversion-and-beanwrapper.md` |
| `docs/boot-auto-config-ordering.md` | `docs/boot-auto-configuration-ordering.md` |
| `docs/boot-spring-boot-auto-configuration.md` | `docs/boot-auto-configuration-beans.md` |
| `docs/aot-runtimehints-basics.md` | `docs/aot-runtimehints.md` |
| `docs/aot-beandefinitionreader-other-inputs-properties-groovy.md` | `docs/aot-beandefinitionreader-other-inputs.md` |
| `docs/aot-built-in-factorybeans-gallery.md` | `docs/aot-built-in-factorybeans.md` |
| `docs/aot-custom-qualifier-meta-annotation.md` | `docs/aot-custom-qualifier.md` |
| `docs/aot-method-injection-replaced-method.md` | `docs/aot-method-injection.md` |
| `docs/aot-aot-and-native-overview.md` | `docs/aot-native-overview.md` |

### Split Or Merge Sources

These files currently own more than one knowledge point. Split them during the relevant task; do not keep duplicate mechanism prose in the old source.

| Source | Final owners |
| --- | --- |
| `docs/ioc-bean-registration.md` | `bean-definition-registration.md`, `import-selector-and-registrar.md`, `programmatic-registration.md` |
| `docs/ioc-post-processors.md` | `post-processors-overview.md`, `beanfactory-post-processors.md`, `beanpost-processors.md` |
| `docs/internals-refresh-to-bean-creation-mainline.md` | `refresh-mainline.md`, `bean-creation-mainline.md` |
| `docs/wiring-autowire-candidate-selection-primary-priority-order.md` | `autowire-candidate-selection.md`, `qualifier-primary-priority-order.md` |
| `docs/wiring-factorybean-deep-dive.md` and `docs/wiring-factorybean-edge-cases.md` | `factorybean-type-matching.md`; product-vs-factory mechanism stays in `factorybean.md` |
| `docs/aot-spel-and-value-expression.md` | `spel-and-value-expression.md`, `aot-spel-and-value-expression.md` |
| `docs/aot-xml-bean-definition-reader.md` | `xml-bean-definition-reader.md`, `aot-xml-bean-definition-reader.md` |
| `docs/aot-xml-namespace-extension.md` | `xml-namespace-extension.md`, `aot-xml-namespace-extension.md` |
| `docs/aot-property-editor-and-value-resolution.md` | `type-conversion-and-beanwrapper.md`, `aot-property-editor-and-value-resolution.md` |

### New Main Documents

Create these documents when no existing page owns the knowledge point cleanly:

| New document | Minimum evidence source |
| --- | --- |
| `beanfactory-vs-applicationcontext.md` | `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` |
| `bean-definition-metadata-and-origin.md` | `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest`, `SpringCoreBeansBeanDefinitionOriginLabTest` |
| `bean-name-and-alias.md` | `SpringCoreBeansBeanNameAliasLabTest` |
| `dependency-descriptor-and-injection-point.md` | `SpringCoreBeansDependencyDescriptorMetadataLabTest`, `SpringCoreBeansProgrammaticResolveDependencyLabTest` |
| `optional-and-provider-injection.md` | `SpringCoreBeansOptionalInjectionLabTest`, `SpringCoreBeansJsr330InjectionLabTest` |
| `xml-bean-definition-reader.md` | `SpringCoreBeansXmlBeanDefinitionReaderLabTest` |
| `properties-and-groovy-reader.md` | `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`, `SpringCoreBeansGroovyBeanDefinitionReaderLabTest` |
| `method-injection.md` | `SpringCoreBeansReplacedMethodLabTest` |
| `built-in-factorybeans.md` | `SpringCoreBeansBuiltInFactoryBeansLabTest`, `SpringCoreBeansServiceLoaderFactoryBeansLabTest` |

---

## Task 0: Baseline And Safety Checks

**Files:**
- Read: `docs/specs/2026-05-07-spring-core-beans-docs-knowledge-point-rewrite-spec.md`
- Read: `spring-core-modules/spring-core-beans/README.md`
- Read: `spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md`
- Read: `SpringCoreBeansDocumentationContractTest.java`

- [ ] **Step 1: Confirm worktree state**

Run:

```bash
git status --short
```

Expected: unrelated user changes, if any, are noted and left untouched. Do not revert unrelated files.

- [ ] **Step 2: Capture current docs inventory**

Run:

```bash
find spring-core-modules/spring-core-beans/docs -maxdepth 1 -type f -name '*.md' | sort
```

Expected: output includes current `ioc-*`, `wiring-*`, `internals-*`, `boot-*`, `aot-*`, `guide-*`, `appendix-*`, and `deepening-*` files.

- [ ] **Step 3: Run current documentation contract**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected: PASS before rewrite starts. If it fails, record each failure in the implementation notes and fix the contract break before renaming files.

- [ ] **Step 4: Run current module contract**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansModuleContractLabTest test
```

Expected: PASS before rewrite starts. If it fails, fix missing Lab/testsupport evidence before rewriting prose.

---

## Task 1: Create The Knowledge Ownership Index

**Files:**
- Modify: `spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md`
- Modify: `spring-core-modules/spring-core-beans/README.md`

- [ ] **Step 1: Rewrite `appendix-knowledge-map.md` as the ownership table**

Replace mechanism prose with a table using these columns:

```markdown
| 层级 | 症状或问题 | 主文档 | 最短证据入口 |
| --- | --- | --- | --- |
```

Expected: every canonical main document from the spec appears exactly once in the `主文档` column. The table may be long; that is acceptable because this page is an index, not a tutorial.

- [ ] **Step 2: Add all current support documents to a separate support table**

Add a second table:

```markdown
| 支持文档 | 职责 | 禁止承担的内容 |
| --- | --- | --- |
```

Expected: all `guide-*`, `appendix-*`, `deepening-*`, and `boot-debugging-and-observability.md` support pages appear once. Their `禁止承担的内容` cells explicitly say they must not repeat main-document mechanism prose.

- [ ] **Step 3: Update README to point to the ownership index**

In `spring-core-modules/spring-core-beans/README.md`, keep only a short paragraph under the knowledge-map route:

```markdown
完整知识点归属见：[知识地图](docs/appendix-knowledge-map.md)。README 只负责把读者导向主文档，不在这里复述机制。
```

Expected: README no longer explains the same mechanism in multiple symptom rows.

- [ ] **Step 4: Verify the index has no duplicate main-document owner**

Run:

```bash
rg -n "\\| [^|]+\\.md \\|" spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md
```

Expected: manual inspection confirms each canonical main document appears once as owner. Support documents can appear in the support table.

- [ ] **Step 5: Commit**

Run:

```bash
git add spring-core-modules/spring-core-beans/README.md spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md
git commit -m "docs(beans): establish knowledge ownership index"
```

Expected: one commit containing only README and knowledge-map changes.

---

## Task 2: Rename Direct Main Documents

**Files:**
- Move: files listed in `Migration Map / Direct Renames`
- Modify: `spring-core-modules/spring-core-beans/README.md`
- Modify: `spring-core-modules/spring-core-beans/docs/*.md`
- Modify: `docs/book/03-spring-core-beans.md`

- [ ] **Step 1: Run direct `git mv` commands**

Run one `git mv` per row in `Migration Map / Direct Renames`. Example:

```bash
git mv spring-core-modules/spring-core-beans/docs/ioc-bean-mental-model.md spring-core-modules/spring-core-beans/docs/bean-mental-model.md
```

Expected: old direct-rename filenames no longer exist; target filenames exist.

- [ ] **Step 2: Update local links to renamed files**

Use `rg` to find old filenames:

```bash
rg -n "ioc-bean-mental-model|wiring-beanfactory-api-deep-dive|wiring-bean-definition-overriding|wiring-merged-bean-definition|ioc-configuration-enhancement|internals-container-bootstrap-and-infrastructure|internals-bdrpp-definition-registration|internals-post-processor-ordering|wiring-programmatic-bpp-registration|internals-pre-instantiation-short-circuit|ioc-dependency-injection-resolution|wiring-resource-injection-name-first|wiring-resolvable-dependency|wiring-generic-type-matching-pitfalls|wiring-injection-phase-field-vs-constructor|ioc-scope-and-prototype|wiring-custom-scope-and-scoped-proxy|wiring-lazy-semantics|wiring-depends-on|ioc-lifecycle-and-callbacks|wiring-smart-initializing-singleton|wiring-smart-lifecycle-phase|ioc-circular-dependencies|internals-early-reference-and-circular|wiring-proxying-phase-bpp-wraps-bean|ioc-factorybean|wiring-context-hierarchy|wiring-environment-and-propertysource|wiring-value-placeholder-resolution-strict-vs-non-strict|wiring-type-conversion-and-beanwrapper|boot-auto-config-ordering|boot-spring-boot-auto-configuration|aot-runtimehints-basics|aot-beandefinitionreader-other-inputs-properties-groovy|aot-built-in-factorybeans-gallery|aot-custom-qualifier-meta-annotation|aot-method-injection-replaced-method|aot-aot-and-native-overview" spring-core-modules/spring-core-beans docs/book/03-spring-core-beans.md
```

Expected: every hit is replaced with its target filename from the direct rename map.

- [ ] **Step 3: Run documentation contract**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected: PASS. If README no longer lists all docs after renames, update the README directory section before proceeding.

- [ ] **Step 4: Commit**

Run:

```bash
git add spring-core-modules/spring-core-beans docs/book/03-spring-core-beans.md
git commit -m "docs(beans): rename direct knowledge point documents"
```

Expected: one commit containing only direct renames and link updates.

---

## Task 3: Split Container And Registration Knowledge Points

**Files:**
- Modify/Create: `spring-core-modules/spring-core-beans/docs/bean-definition-registration.md`
- Create: `spring-core-modules/spring-core-beans/docs/import-selector-and-registrar.md`
- Create: `spring-core-modules/spring-core-beans/docs/programmatic-registration.md`
- Create: `spring-core-modules/spring-core-beans/docs/beanfactory-vs-applicationcontext.md`
- Create: `spring-core-modules/spring-core-beans/docs/bean-definition-metadata-and-origin.md`
- Modify/Create: `spring-core-modules/spring-core-beans/docs/bean-name-and-alias.md`
- Remove after migration: `spring-core-modules/spring-core-beans/docs/ioc-bean-registration.md`

- [ ] **Step 1: Create `bean-definition-registration.md` owner**

Scope this document to: how a `BeanDefinition` enters the registry. Use only registration evidence, not import, metadata, or programmatic API differences.

Required evidence:

```markdown
- `SpringCoreBeansBeanDefinitionRegistrationDiffLabTest`
- `SpringCoreBeansComponentScanLabTest`
```

Expected: reader can answer “who registered this BeanDefinition?” without learning `@Import` or `registerSingleton`.

- [ ] **Step 2: Create `import-selector-and-registrar.md` owner**

Scope this document to: `@Import`, `ImportSelector`, and `ImportBeanDefinitionRegistrar` boundary.

Required evidence:

```markdown
- `SpringCoreBeansImportLabTest`
- `SpringCoreBeansImportExerciseTest`
- `SpringCoreBeansImportExerciseSolutionTest`
```

Expected: `bean-definition-registration.md` only points here for `@Import` details.

- [ ] **Step 3: Create `programmatic-registration.md` owner**

Scope this document to: `registerBeanDefinition`, `registerBean`, and `registerSingleton` differences.

Required evidence:

```markdown
- `SpringCoreBeansProgrammaticRegistrationLabTest`
```

Expected: document explains the three APIs as different contracts, not as interchangeable registration helpers.

- [ ] **Step 4: Create `beanfactory-vs-applicationcontext.md` owner**

Scope this document to: capability differences between `BeanFactory` and `ApplicationContext`.

Required evidence:

```markdown
- `SpringCoreBeansBeanFactoryVsApplicationContextLabTest`
```

Expected: `beanfactory-api-and-autowirecapablebeanfactory.md` only links here for the broader container comparison.

- [ ] **Step 5: Create `bean-definition-metadata-and-origin.md` owner**

Scope this document to: `primary`, `autowireCandidate`, `source`, `factoryMethod`, and origin troubleshooting metadata.

Required evidence:

```markdown
- `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest`
- `SpringCoreBeansBeanDefinitionOriginLabTest`
```

Expected: candidate selection docs link here for metadata definitions instead of redefining them.

- [ ] **Step 6: Remove duplicated source content**

After all five owners exist, delete or empty the mechanism body in `ioc-bean-registration.md`, then remove the old file once all links point to new owners.

Run:

```bash
rg -n "ioc-bean-registration\\.md" spring-core-modules/spring-core-beans docs/book/03-spring-core-beans.md
```

Expected: no hits before deleting `ioc-bean-registration.md`.

- [ ] **Step 7: Verify**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected: PASS.

- [ ] **Step 8: Commit**

Run:

```bash
git add spring-core-modules/spring-core-beans docs/book/03-spring-core-beans.md
git commit -m "docs(beans): split registration ownership"
```

Expected: one commit with new registration/container owner documents and old-link removal.

---

## Task 4: Split Post-Processor And Creation Mainline Documents

**Files:**
- Modify/Create: `spring-core-modules/spring-core-beans/docs/post-processors-overview.md`
- Create: `spring-core-modules/spring-core-beans/docs/beanfactory-post-processors.md`
- Create: `spring-core-modules/spring-core-beans/docs/beanpost-processors.md`
- Create: `spring-core-modules/spring-core-beans/docs/refresh-mainline.md`
- Create: `spring-core-modules/spring-core-beans/docs/bean-creation-mainline.md`
- Remove after migration: `spring-core-modules/spring-core-beans/docs/ioc-post-processors.md`
- Remove after migration: `spring-core-modules/spring-core-beans/docs/internals-refresh-to-bean-creation-mainline.md`

- [ ] **Step 1: Create `post-processors-overview.md`**

Scope this document to: BFPP / BDRPP / BPP responsibility boundaries and definition-vs-instance phase.

Required evidence:

```markdown
- `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest`
- `SpringCoreBeansRegistryPostProcessorLabTest`
- `SpringCoreBeansPostProcessorOrderingLabTest`
```

Expected: overview points to detailed BFPP, BDRPP, BPP, ordering, and programmatic-BPP docs.

- [ ] **Step 2: Create `beanfactory-post-processors.md`**

Scope this document to: when BFPP modifies existing BeanDefinitions and what it must not do.

Required evidence:

```markdown
- `SpringCoreBeansStaticBeanFactoryPostProcessorLabTest`
```

Expected: does not explain BPP proxy replacement.

- [ ] **Step 3: Create `beanpost-processors.md`**

Scope this document to: BPP participation in bean instance creation and proxy replacement windows.

Required evidence:

```markdown
- `SpringCoreBeansLifecycleRawVsProxyLabTest`
- `SpringCoreBeansProxyingPhaseLabTest`
```

Expected: points to `proxying-phase.md` for self-invocation and proxy-window detail.

- [ ] **Step 4: Create `refresh-mainline.md`**

Scope this document to: `refresh()` order, from context preparation to singleton pre-instantiation.

Required evidence:

```markdown
- `SpringCoreBeansMainlineCallChainLabTest`
- `SpringCoreBeansBootstrapInternalsLabTest`
```

Expected: does not walk through `doCreateBean()` internals.

- [ ] **Step 5: Create `bean-creation-mainline.md`**

Scope this document to: `doGetBean()` / `doCreateBean()` path, dependency resolution, instantiation, property population, initialization, final exposure.

Required evidence:

```markdown
- `SpringCoreBeansBeanCreationTraceLabTest`
```

Expected: links to `refresh-mainline.md` for the context-level caller and to `proxying-phase.md` for BPP proxy replacement detail.

- [ ] **Step 6: Remove old combined files**

Run:

```bash
rg -n "ioc-post-processors\\.md|internals-refresh-to-bean-creation-mainline\\.md" spring-core-modules/spring-core-beans docs/book/03-spring-core-beans.md
```

Expected: no hits before deleting the old combined files.

- [ ] **Step 7: Verify and commit**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
git add spring-core-modules/spring-core-beans docs/book/03-spring-core-beans.md
git commit -m "docs(beans): split processor and creation mainlines"
```

Expected: contract PASS before commit.

---

## Task 5: Split Dependency Resolution Documents

**Files:**
- Modify: `spring-core-modules/spring-core-beans/docs/dependency-injection-resolution.md`
- Create: `spring-core-modules/spring-core-beans/docs/dependency-descriptor-and-injection-point.md`
- Create: `spring-core-modules/spring-core-beans/docs/autowire-candidate-selection.md`
- Create: `spring-core-modules/spring-core-beans/docs/qualifier-primary-priority-order.md`
- Create: `spring-core-modules/spring-core-beans/docs/optional-and-provider-injection.md`
- Remove after migration: `spring-core-modules/spring-core-beans/docs/wiring-autowire-candidate-selection-primary-priority-order.md`

- [ ] **Step 1: Keep `dependency-injection-resolution.md` focused**

Scope this document to: what demand an injection point submits to the container.

Required evidence:

```markdown
- `SpringCoreBeansInjectionAmbiguityLabTest`
- `SpringCoreBeansAutowireCandidateSelectionLabTest`
```

Expected: it links to candidate selection and qualifier docs for selection steps.

- [ ] **Step 2: Create `dependency-descriptor-and-injection-point.md`**

Scope this document to: metadata carried by `DependencyDescriptor` / `InjectionPoint`.

Required evidence:

```markdown
- `SpringCoreBeansDependencyDescriptorMetadataLabTest`
- `SpringCoreBeansProgrammaticResolveDependencyLabTest`
```

Expected: debugging metadata lives here and is only pointed to elsewhere.

- [ ] **Step 3: Create `autowire-candidate-selection.md`**

Scope this document to: candidate collection, filtering, and convergence.

Required evidence:

```markdown
- `SpringCoreBeansAutowireCandidateSelectionLabTest`
- `SpringCoreBeansAutowireCandidateSelectionExerciseTest`
- `SpringCoreBeansAutowireCandidateSelectionExerciseSolutionTest`
```

Expected: `@Primary`, `@Priority`, `@Order`, and `@Qualifier` are named but not fully explained.

- [ ] **Step 4: Create `qualifier-primary-priority-order.md`**

Scope this document to: which step each annotation affects.

Required evidence:

```markdown
- `SpringCoreBeansAutowireCandidateSelectionLabTest`
- `SpringCoreBeansBeanDefinitionMetadataFlagsLabTest`
```

Expected: explains annotation boundaries without repeating candidate collection details.

- [ ] **Step 5: Create `optional-and-provider-injection.md`**

Scope this document to: optional and lazy dependency expression via `Optional`, `required=false`, `ObjectProvider`, and `Provider`.

Required evidence:

```markdown
- `SpringCoreBeansOptionalInjectionLabTest`
- `SpringCoreBeansJsr330InjectionLabTest`
```

Expected: lazy semantics for `@Lazy` still belongs to `lazy-semantics.md`.

- [ ] **Step 6: Remove old combined candidate doc**

Run:

```bash
rg -n "wiring-autowire-candidate-selection-primary-priority-order\\.md" spring-core-modules/spring-core-beans docs/book/03-spring-core-beans.md
```

Expected: no hits before deleting the old combined file.

- [ ] **Step 7: Verify and commit**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
git add spring-core-modules/spring-core-beans docs/book/03-spring-core-beans.md
git commit -m "docs(beans): split dependency resolution topics"
```

Expected: contract PASS before commit.

---

## Task 6: Finish Lifecycle, Scope, Proxy, And FactoryBean Boundaries

**Files:**
- Modify: `scope-and-prototype.md`
- Modify: `custom-scope-and-scoped-proxy.md`
- Modify: `lazy-semantics.md`
- Modify: `depends-on.md`
- Modify: `lifecycle-callbacks.md`
- Modify: `smart-initializing-singleton.md`
- Modify: `smart-lifecycle.md`
- Modify: `circular-dependency.md`
- Modify: `early-reference-and-three-level-cache.md`
- Modify: `proxying-phase.md`
- Modify: `factorybean.md`
- Create: `factorybean-type-matching.md`
- Remove after migration: `wiring-factorybean-deep-dive.md`
- Remove after migration: `wiring-factorybean-edge-cases.md`

- [ ] **Step 1: Give each existing lifecycle/scope page one ownership statement**

At the top of each modified file, add a short ownership paragraph:

```markdown
这一页只回答一个问题：...
```

Expected: each page names exactly one problem and points elsewhere for adjacent mechanisms.

- [ ] **Step 2: Create `factorybean-type-matching.md`**

Scope this document to: FactoryBean type matching boundaries, `getObjectType()`, `isSingleton()`, and type discovery.

Required evidence:

```markdown
- `SpringCoreBeansFactoryBeanDeepDiveLabTest`
- `SpringCoreBeansFactoryBeanEdgeCasesLabTest`
```

Expected: `factorybean.md` owns product-vs-factory and `&` prefix; this page owns type matching.

- [ ] **Step 3: Remove duplicated FactoryBean source docs**

Run:

```bash
rg -n "wiring-factorybean-deep-dive\\.md|wiring-factorybean-edge-cases\\.md" spring-core-modules/spring-core-beans docs/book/03-spring-core-beans.md
```

Expected: no hits before deleting the old files.

- [ ] **Step 4: Verify lifecycle/scope evidence**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansCustomScopeLabTest,SpringCoreBeansLazyLabTest,SpringCoreBeansDependsOnLabTest,SpringCoreBeansLifecycleCallbackOrderLabTest,SpringCoreBeansSmartInitializingSingletonLabTest,SpringCoreBeansSmartLifecycleLabTest,SpringCoreBeansCircularDependencyBoundaryLabTest,SpringCoreBeansEarlyReferenceLabTest,SpringCoreBeansProxyingPhaseLabTest,SpringCoreBeansFactoryBeanDeepDiveLabTest,SpringCoreBeansFactoryBeanEdgeCasesLabTest test
```

Expected: PASS.

- [ ] **Step 5: Commit**

Run:

```bash
git add spring-core-modules/spring-core-beans docs/book/03-spring-core-beans.md
git commit -m "docs(beans): tighten lifecycle scope proxy and factorybean topics"
```

Expected: one commit after evidence tests pass.

---

## Task 7: Split Value Resolution And External Input Documents

**Files:**
- Modify: `environment-and-propertysource.md`
- Modify: `value-placeholder-resolution.md`
- Create: `spel-and-value-expression.md`
- Modify: `type-conversion-and-beanwrapper.md`
- Create: `xml-bean-definition-reader.md`
- Create: `properties-and-groovy-reader.md`
- Create: `xml-namespace-extension.md`
- Create: `method-injection.md`
- Create: `built-in-factorybeans.md`

- [ ] **Step 1: Keep value and environment docs separate**

Expected ownership:

```text
environment-and-propertysource.md owns where values come from.
value-placeholder-resolution.md owns ${...} strict vs non-strict placeholder behavior.
spel-and-value-expression.md owns #{...} and ${...} ordering.
type-conversion-and-beanwrapper.md owns value-to-property conversion.
```

Required evidence:

```markdown
- `SpringCoreBeansEnvironmentPropertySourceLabTest`
- `SpringCoreBeansValuePlaceholderResolutionLabTest`
- `SpringCoreBeansSpelValueLabTest`
- `SpringCoreBeansTypeConversionLabTest`
- `SpringCoreBeansPropertyEditorLabTest`
- `SpringCoreBeansPropertyEditorResolutionLabTest`
```

- [ ] **Step 2: Create input-reader main docs**

Create these non-AOT owners:

```text
xml-bean-definition-reader.md
properties-and-groovy-reader.md
xml-namespace-extension.md
method-injection.md
built-in-factorybeans.md
```

Required evidence:

```markdown
- `SpringCoreBeansXmlBeanDefinitionReaderLabTest`
- `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`
- `SpringCoreBeansGroovyBeanDefinitionReaderLabTest`
- `SpringCoreBeansXmlNamespaceExtensionLabTest`
- `SpringCoreBeansReplacedMethodLabTest`
- `SpringCoreBeansBuiltInFactoryBeansLabTest`
- `SpringCoreBeansServiceLoaderFactoryBeansLabTest`
```

Expected: AOT docs link to these owners and only add native/build-time constraints.

- [ ] **Step 3: Verify input/value evidence**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansEnvironmentPropertySourceLabTest,SpringCoreBeansValuePlaceholderResolutionLabTest,SpringCoreBeansSpelValueLabTest,SpringCoreBeansTypeConversionLabTest,SpringCoreBeansPropertyEditorLabTest,SpringCoreBeansPropertyEditorResolutionLabTest,SpringCoreBeansXmlBeanDefinitionReaderLabTest,SpringCoreBeansPropertiesBeanDefinitionReaderLabTest,SpringCoreBeansGroovyBeanDefinitionReaderLabTest,SpringCoreBeansXmlNamespaceExtensionLabTest,SpringCoreBeansReplacedMethodLabTest,SpringCoreBeansBuiltInFactoryBeansLabTest,SpringCoreBeansServiceLoaderFactoryBeansLabTest test
```

Expected: PASS.

- [ ] **Step 4: Commit**

Run:

```bash
git add spring-core-modules/spring-core-beans docs/book/03-spring-core-beans.md
git commit -m "docs(beans): split value and external input topics"
```

Expected: one commit after evidence tests pass.

---

## Task 8: Tighten Boot And AOT Documents

**Files:**
- Modify: `boot-auto-configuration-ordering.md`
- Modify: `boot-auto-configuration-beans.md`
- Modify: `boot-debugging-and-observability.md`
- Modify: `aot-runtimehints.md`
- Modify: `aot-xml-bean-definition-reader.md`
- Modify: `aot-autowirecapablebeanfactory-external-objects.md`
- Modify: `aot-spel-and-value-expression.md`
- Modify: `aot-custom-qualifier.md`
- Modify: `aot-xml-namespace-extension.md`
- Modify: `aot-beandefinitionreader-other-inputs.md`
- Modify: `aot-method-injection.md`
- Modify: `aot-built-in-factorybeans.md`
- Modify: `aot-property-editor-and-value-resolution.md`
- Modify: `aot-native-overview.md`

- [ ] **Step 1: Separate Boot owners from Boot support**

Expected ownership:

```text
boot-auto-configuration-ordering.md owns ordering and condition timing.
boot-auto-configuration-beans.md owns bean appearance/backoff.
boot-debugging-and-observability.md is support only: Actuator, ConditionEvaluationReport, logs, breakpoints.
```

Required evidence:

```markdown
- `SpringCoreBeansAutoConfigurationOrderingLabTest`
- `SpringCoreBeansAutoConfigurationImportOrderingLabTest`
- `SpringCoreBeansAutoConfigurationBackoffTimingLabTest`
- `SpringCoreBeansAutoConfigurationOverrideMatrixLabTest`
- `SpringCoreBeansConditionEvaluationReportLabTest`
```

- [ ] **Step 2: Make each AOT page an AOT-only addendum**

For each `aot-*.md`, keep only:

```markdown
1. JVM behavior owner link
2. AOT/native-specific constraint
3. RuntimeHints/resource/proxy/reflection contract if applicable
4. shortest AOT Lab evidence
```

Expected: AOT docs do not re-explain XML reader, SpEL, method injection, FactoryBean, or PropertyEditor baseline mechanisms.

- [ ] **Step 3: Verify Boot and AOT evidence**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansAutoConfigurationOrderingLabTest,SpringCoreBeansAutoConfigurationImportOrderingLabTest,SpringCoreBeansAutoConfigurationBackoffTimingLabTest,SpringCoreBeansAutoConfigurationOverrideMatrixLabTest,SpringCoreBeansConditionEvaluationReportLabTest,SpringCoreBeansAotRuntimeHintsLabTest,SpringCoreBeansRuntimeHintsBoundaryLabTest,SpringCoreBeansAotFactoriesLabTest,SpringCoreBeansXmlBeanDefinitionReaderLabTest,SpringCoreBeansAutowireCapableBeanFactoryLabTest,SpringCoreBeansSpelValueLabTest,SpringCoreBeansCustomQualifierLabTest,SpringCoreBeansXmlNamespaceExtensionLabTest,SpringCoreBeansPropertiesBeanDefinitionReaderLabTest,SpringCoreBeansGroovyBeanDefinitionReaderLabTest,SpringCoreBeansReplacedMethodLabTest,SpringCoreBeansBuiltInFactoryBeansLabTest,SpringCoreBeansBeanDefinitionValueResolutionLabTest test
```

Expected: PASS.

- [ ] **Step 4: Commit**

Run:

```bash
git add spring-core-modules/spring-core-beans docs/book/03-spring-core-beans.md
git commit -m "docs(beans): tighten boot and aot ownership"
```

Expected: one commit after evidence tests pass.

---

## Task 9: Shrink Support Documents

**Files:**
- Modify: all `spring-core-modules/spring-core-beans/docs/guide-*.md`
- Modify: all `spring-core-modules/spring-core-beans/docs/appendix-*.md`
- Modify: all `spring-core-modules/spring-core-beans/docs/deepening-*.md`
- Modify: `spring-core-modules/spring-core-beans/docs/boot-debugging-and-observability.md`

- [ ] **Step 1: Shrink Guide pages**

Each `guide-*.md` must contain only:

```text
route purpose
when to use it
main-doc links
breakpoint or Lab links
```

Expected: no guide page has a full mechanism explanation that belongs to a main document.

- [ ] **Step 2: Shrink Appendix pages**

Each `appendix-*.md` must contain only:

```text
lookup table
term definition
pitfall pointer
test/Lab index
troubleshooting checklist
```

Expected: `appendix-common-pitfalls.md` maps misconception to owner document; it does not explain the mechanism itself.

- [ ] **Step 3: Rewrite deepening pages as maintainer docs**

Each `deepening-*.md` must answer one maintenance question:

```text
what this document set controls
which pages are main owners
which pages are support only
which links are risky
how to validate the maintenance surface
```

Expected: no `deepening-*` page reads like a tutorial chapter.

- [ ] **Step 4: Scan support docs for repeated tutorial phrasing**

Run:

```bash
rg -n "机制是|完整链路|主线如下|详细过程|本章会解释|这一机制" spring-core-modules/spring-core-beans/docs/guide-*.md spring-core-modules/spring-core-beans/docs/appendix-*.md spring-core-modules/spring-core-beans/docs/deepening-*.md spring-core-modules/spring-core-beans/docs/boot-debugging-and-observability.md
```

Expected: every hit is either a one-line pointer or is rewritten into a link to the owner document.

- [ ] **Step 5: Commit**

Run:

```bash
git add spring-core-modules/spring-core-beans/docs
git commit -m "docs(beans): shrink support documents to pointers"
```

Expected: one commit containing only support-document reductions.

---

## Task 10: Rebuild README And Book Entry

**Files:**
- Modify: `spring-core-modules/spring-core-beans/README.md`
- Modify: `docs/book/03-spring-core-beans.md`

- [ ] **Step 1: Rewrite README first screen**

README first screen must contain only:

```text
module boundary
three-layer diagnostic model
shortest command
where to go next
```

Expected: no long mechanism body appears before the first route section.

- [ ] **Step 2: Rebuild README directory as the only docs-order source**

README directory must list every `spring-core-modules/spring-core-beans/docs/*.md` file exactly once.

Run:

```bash
find spring-core-modules/spring-core-beans/docs -maxdepth 1 -type f -name '*.md' -printf '%f\n' | sort
```

Expected: each output filename appears in README as a local Markdown link.

- [ ] **Step 3: Keep book entry as a route only**

`docs/book/03-spring-core-beans.md` should link to:

```text
spring-core-modules/spring-core-beans/README.md
appendix-knowledge-map.md
guide-breakpoint-map.md
selected first-run Lab
```

Expected: it does not duplicate module README symptom tables or main-document mechanisms.

- [ ] **Step 4: Verify README contract**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test
```

Expected: PASS.

- [ ] **Step 5: Commit**

Run:

```bash
git add spring-core-modules/spring-core-beans/README.md docs/book/03-spring-core-beans.md
git commit -m "docs(beans): rebuild module and book entry routes"
```

Expected: one commit after documentation contract passes.

---

## Task 11: Final Contract And Duplicate-Ownership Review

**Files:**
- Modify if needed: `SpringCoreBeansDocumentationContractTest.java`
- Modify if needed: `SpringCoreBeansModuleContractLabTest.java`
- Modify if needed: docs with broken links or missing Lab references

- [ ] **Step 1: Run full module tests**

Run:

```bash
mvn -pl :spring-core-beans test
```

Expected: PASS.

- [ ] **Step 2: Run focused contract tests**

Run:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest,SpringCoreBeansModuleContractLabTest test
```

Expected: PASS.

- [ ] **Step 3: Scan for old filenames**

Run:

```bash
rg -n "ioc-|wiring-|internals-|aot-aot-and-native-overview|aot-runtimehints-basics|boot-spring-boot-auto-configuration|boot-auto-config-ordering" spring-core-modules/spring-core-beans/README.md spring-core-modules/spring-core-beans/docs docs/book/03-spring-core-beans.md
```

Expected: hits are limited to support-document prefixes intentionally retained (`guide-*`, `appendix-*`, `deepening-*`) and explanatory text about old names if needed. No link target should point to a removed old main-document filename.

- [ ] **Step 4: Scan for duplicate owner language**

Run:

```bash
rg -n "完整解释|详细机制|这页完整讲清楚|本页负责解释" spring-core-modules/spring-core-beans/docs/guide-*.md spring-core-modules/spring-core-beans/docs/appendix-*.md spring-core-modules/spring-core-beans/docs/deepening-*.md spring-core-modules/spring-core-beans/docs/boot-debugging-and-observability.md
```

Expected: no support document claims ownership of a Bean mechanism.

- [ ] **Step 5: Commit final fixes**

Run:

```bash
git add spring-core-modules/spring-core-beans docs/book/03-spring-core-beans.md
git commit -m "docs(beans): verify knowledge point rewrite contracts"
```

Expected: one final commit only if there are fixes after Task 10.

---

## Completion Checklist

Before marking the rewrite complete, verify every item:

- [ ] Every canonical main document exists.
- [ ] Every main document owns one knowledge point or one tightly coupled problem group.
- [ ] No old combined main-document filename remains linked.
- [ ] `README.md` lists every `docs/*.md` file.
- [ ] `appendix-knowledge-map.md` maps every knowledge point to one owner.
- [ ] Guide, Appendix, and deepening pages are support-only.
- [ ] Every referenced `SpringCoreBeans*Test` exists.
- [ ] `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansDocumentationContractTest test` passes.
- [ ] `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansModuleContractLabTest test` passes.
- [ ] `mvn -pl :spring-core-beans test` passes.

---

## Execution Notes

- Do not rewrite all prose in one commit. The review unit is the task.
- Do not keep transitional stub files for old main-document names. They hide broken ownership and make README noisy.
- Do not use guide/appendix/deepening pages as overflow storage for mechanisms that do not fit a main document.
- If a Lab referenced in a main document is missing, first search existing tests with:

```bash
find spring-core-modules/spring-core-beans/src/test/java -name 'SpringCoreBeans*Test.java' | sort
```

If no existing Lab proves the claim, either remove the claim or add the smallest test that proves it.
