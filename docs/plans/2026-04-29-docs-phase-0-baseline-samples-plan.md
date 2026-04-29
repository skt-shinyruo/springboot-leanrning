# Phase 0 Docs Baseline Samples Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** Establish the first executable quality baseline for the full manual documentation rewrite by rewriting the Phase 0 sample set defined in `docs/plans/2026-04-29-docs-full-manual-rewrite-spec.md`.

**Architecture:** This plan is a Phase 0 implementation plan under the single full-rewrite spec, not a competing spec. It rewrites seven sample files in place: one style guide, two global entry pages, two module entry pages, and two normal content pages. The result should become the model used by later module-level plans.

**Tech Stack:** Markdown, MkDocs Material, `docs/SUMMARY.md` as navigation SSOT, module `README.md` files as module-order SSOT, Maven test entries (`*LabTest`, `*BookMatrixLabTest`, `*BranchMatrixLabTest`) as evidence.

---

## Governing Spec

All tasks in this plan inherit:

- Scope and non-goals from `docs/plans/2026-04-29-docs-full-manual-rewrite-spec.md`.
- Writing rules from `docs/writing-style-guide.md`.
- README language rule from `AGENTS.md`: every `README.md` must be written in Chinese.

This plan may create checkpoints and commits, but it must not redefine the full-rewrite standard. If a task conflicts with the full spec, the full spec wins.

---

## File Map

- Modify: `docs/writing-style-guide.md`
  - Responsibility: define the writing standard used by every later rewrite plan.
- Modify: `README.md`
  - Responsibility: root entry for repository readers; must explain project purpose, fastest start, learning routes, and evidence model.
- Modify: `docs/book/README.md`
  - Responsibility: entry for the sequential book-style route; must explain the role of book chapters versus module docs.
- Modify: `spring-core-modules/spring-core-beans/README.md`
  - Responsibility: sample for a deep Spring Core module entry page.
- Modify: `spring-core-modules/spring-core-beans/docs/ioc-bean-mental-model.md`
  - Responsibility: sample for a dense mechanism chapter with source-level evidence.
- Modify: `spring-boot-modules/spring-boot-web-mvc/README.md`
  - Responsibility: sample for a Spring Boot user-facing module entry page.
- Modify: `spring-boot-modules/spring-boot-web-mvc/docs/controller-boundary.md`
  - Responsibility: sample for a concise user-facing mechanism chapter.

---

## Task 0: Baseline Inventory

**Files:**
- Read: `docs/plans/2026-04-29-docs-full-manual-rewrite-spec.md`
- Read: `docs/writing-style-guide.md`
- Read: `README.md`
- Read: `docs/book/README.md`
- Read: `spring-core-modules/spring-core-beans/README.md`
- Read: `spring-core-modules/spring-core-beans/docs/ioc-bean-mental-model.md`
- Read: `spring-boot-modules/spring-boot-web-mvc/README.md`
- Read: `spring-boot-modules/spring-boot-web-mvc/docs/controller-boundary.md`

- [x] **Step 1: Capture the current Phase 0 file list**

Run:

```bash
printf '%s\n' \
  docs/writing-style-guide.md \
  README.md \
  docs/book/README.md \
  spring-core-modules/spring-core-beans/README.md \
  spring-core-modules/spring-core-beans/docs/ioc-bean-mental-model.md \
  spring-boot-modules/spring-boot-web-mvc/README.md \
  spring-boot-modules/spring-boot-web-mvc/docs/controller-boundary.md
```

Expected: the seven paths above print exactly once.

- [x] **Step 2: Record existing template-language findings**

Run:

```bash
rg -n "本章围绕|主线可以概括为|目标是把|本文将|先把结论|Key Objects|Extension Points|学习目标" \
  docs/writing-style-guide.md \
  README.md \
  docs/book/README.md \
  spring-core-modules/spring-core-beans/README.md \
  spring-core-modules/spring-core-beans/docs/ioc-bean-mental-model.md \
  spring-boot-modules/spring-boot-web-mvc/README.md \
  spring-boot-modules/spring-boot-web-mvc/docs/controller-boundary.md
```

Expected: any hits in `docs/writing-style-guide.md` are allowed only where the guide is showing bad examples; hits in sample content pages must be removed or justified by the rewrite.

- [x] **Step 3: Record evidence-entry names that must not be lost**

Run:

```bash
rg -n "BookMatrix|BranchMatrix|LabTest|SpringCoreBeans|BootWebMvc|DispatcherServlet|doDispatch|doCreateBean|BeanDefinition" \
  README.md \
  docs/book/README.md \
  spring-core-modules/spring-core-beans/README.md \
  spring-core-modules/spring-core-beans/docs/ioc-bean-mental-model.md \
  spring-boot-modules/spring-boot-web-mvc/README.md \
  spring-boot-modules/spring-boot-web-mvc/docs/controller-boundary.md
```

Expected: existing evidence names are visible before rewriting and can be checked again after rewriting.

---

## Task 1: Rewrite the Style Guide as the Baseline Contract

**Files:**
- Modify: `docs/writing-style-guide.md`

- [x] **Step 1: Rewrite the opening contract**

Replace the opening body with a concise contract that states:

```markdown
本规范服务于全量人工精读重写：每篇正文先读懂，再改写；每个结论必须能回到实验、断言、断点或链接。它不是排版模板，而是质量底线。

本仓库文档的目标形态是“中文教材式项目学习手册”：读者既能顺读，也能在遇到具体症状时快速回到最短证据链。
```

Expected: the guide frames itself as the standard for manual rewriting, not only a historical migration note.

- [x] **Step 2: Add a single-page acceptance checklist**

Add a section named `## 单篇验收清单` with these exact checks:

```markdown
一篇正文合格前，必须逐项确认：

1. 第一屏能说明本页解释的现象或问题。
2. 至少有一个真实证据入口：命令、测试类、断点方法或可观察状态。
3. 机制解释使用“现象 -> 原因 -> 验证”的顺序推进。
4. 边界讨论写出表面现象、机制分支和验证方式。
5. 小结不超过 3 条，并且每条能回指到正文证据。
6. 相对链接能解析，README 全文为中文。
```

Expected: later module plans can cite this checklist without restating it.

- [x] **Step 3: Keep bad-phrase examples only as examples**

Ensure bad examples stay inside the writing guide as examples, while the guide itself does not use those phrases as live prose.

Run:

```bash
rg -n "先把结论放前面|本文将介绍|学习目标|Key Objects|Extension Points" docs/writing-style-guide.md
```

Expected: hits appear only in sections that explicitly describe avoided wording or examples.

---

## Task 2: Rewrite the Global Entry Pages

**Files:**
- Modify: `README.md`
- Modify: `docs/book/README.md`

- [x] **Step 1: Rewrite `README.md` first screen**

Rewrite the root README opening so the first screen states:

```markdown
本仓库是一套 Spring Boot / Spring Core 学习工作区。它用多模块代码、可运行测试和中文文档，把常见机制问题固定成可复现的证据链。

读者不需要从索引开始背概念。更稳定的读法是：先跑一个模块的最小 Lab，把现象变成断言；再回到模块 README 和正文解释机制；最后用断点地图或分支矩阵处理边界问题。
```

Preserve the existing module catalog and experiment index unless a specific link is wrong.

Expected: the opening no longer reads like a generic project summary; it clearly explains the evidence-chain learning model.

- [x] **Step 2: Reshape `README.md` quick start**

Keep these commands and explain their purpose in prose:

```bash
mvn -q test
mvn -q -pl :spring-boot-web-mvc test
mvn -pl :spring-boot-basics spring-boot:run
```

Expected: commands remain copyable; surrounding text tells the reader what fact each command establishes.

- [x] **Step 3: Rewrite `docs/book/README.md` as the sequential-route sample**

Keep the current role separation:

```markdown
主线章节承担“引导与聚合”的工作；机制正文、分支矩阵、断点清单在各模块的 `*/docs/` 中维护。
```

Strengthen the first screen around three facts:

1. Book chapters are not full module documentation.
2. Each chapter starts from a runnable evidence entry.
3. When troubleshooting, the reader can enter through `90-troubleshooting-index.md`.

Expected: `docs/book/README.md` becomes the sample for a book-level entry page.

- [x] **Step 4: Verify global entry links**

Run:

```bash
rg -n "docs/SUMMARY.md|docs/book/README.md|docs-site/README.md|90-troubleshooting-index.md" README.md docs/book/README.md
```

Expected: the root README links to summary/book/site docs; the book README links to troubleshooting/glossary/references.

---

## Task 3: Rewrite the `spring-core-beans` Module Entry Sample

**Files:**
- Modify: `spring-core-modules/spring-core-beans/README.md`

- [x] **Step 1: Rewrite the first screen around the three-layer diagnostic model**

The first screen must explain that this module teaches readers to separate:

```markdown
定义层：有没有 BeanDefinition、是谁注册的、条件是否满足。
创建层：何时实例化、如何注入、哪些后处理器参与。
最终暴露对象：`getBean()` 或注入点拿到的对象是否已经被代理或包装。
```

Expected: the page reads like a module guide, not a long link index.

- [x] **Step 2: Preserve the shortest command entry**

Keep and contextualize:

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test
mvn -pl :spring-core-beans test
```

Expected: the reader knows what fact each command proves.

- [x] **Step 3: Tighten symptoms table introduction**

Keep the symptoms table, but rewrite its introduction to state:

```markdown
症状表不是第二份目录。它只用于把异常或现象先分层，再跳到最短章节和断点入口。
```

Expected: the table has a clear purpose and does not duplicate the directory prose.

- [x] **Step 4: Verify key links still exist**

Run:

```bash
test -f spring-core-modules/spring-core-beans/docs/ioc-bean-registration.md
test -f spring-core-modules/spring-core-beans/docs/ioc-bean-mental-model.md
test -f spring-core-modules/spring-core-beans/docs/guide-breakpoint-map.md
test -f spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md
```

Expected: all commands exit 0.

---

## Task 4: Rewrite the Bean Mental Model Chapter Sample

**Files:**
- Modify: `spring-core-modules/spring-core-beans/docs/ioc-bean-mental-model.md`

- [x] **Step 1: Fix the chapter-card evidence block**

Rewrite the `CHAPTER-CARD` content so it names:

```markdown
问题：为什么同一个 bean 会在调试器里出现 BeanDefinition、merged RootBeanDefinition、raw instance、proxy 等多种形态？
最短入口：`SpringCoreBeansContainerLabTest`。
深入入口：`SpringCoreBeansBeanCreationTraceLabTest` 与 `SpringCoreBeansProxyingPhaseLabTest`。
断点：`AbstractApplicationContext#refresh`、`AbstractAutowireCapableBeanFactory#doCreateBean`、`AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`。
```

Expected: first screen gives problem, test entries, and breakpoint entries without malformed nested lists.

- [x] **Step 2: Replace the malformed experiment list**

Rewrite the experiment block as:

```markdown
## 实验：把“定义、实例、最终暴露对象”分开观察

运行：

- `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test`
- `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBeanCreationTraceLabTest test`
- `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansProxyingPhaseLabTest test`

观察：

- `BeanDefinition` 与真实对象不是同一层东西。
- `populateBean` 负责把依赖填入 raw instance。
- `applyBeanPostProcessorsAfterInitialization` 可能把最终暴露对象换成 proxy。
```

Expected: the existing broken indentation under `!!! example` is gone.

- [x] **Step 3: Turn the four-object model into the sample mechanism section**

Preserve the four objects:

```markdown
BeanDefinition
merged RootBeanDefinition
raw instance
exposed object
```

Rewrite the explanation so each object answers one diagnostic question: existence, final recipe, construction/injection, and external identity.

Expected: this chapter becomes the sample for dense mechanism writing.

- [x] **Step 4: Verify evidence file paths**

Run:

```bash
test -f spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java
test -f spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansBeanCreationTraceLabTest.java
test -f spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProxyingPhaseLabTest.java
```

Expected: all commands exit 0.

---

## Task 5: Rewrite the `spring-boot-web-mvc` Module Entry Sample

**Files:**
- Modify: `spring-boot-modules/spring-boot-web-mvc/README.md`

- [x] **Step 1: Rewrite first screen around request-stage diagnosis**

The first screen must establish that Web MVC issues should be located by request stage:

```markdown
进入容器：FilterChain / Security。
进入 MVC：DispatcherServlet。
选路：HandlerMapping。
调用：HandlerAdapter。
参数：ArgumentResolver / Binder / Converter / Validator。
返回：ReturnValueHandler / MessageConverter / ViewResolver。
异常：HandlerExceptionResolver / Boot error fallback。
```

Expected: the module entry explains how to locate a symptom before listing docs.

- [x] **Step 2: Preserve runnable entries and HTTP probes**

Keep these commands:

```bash
mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test
mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test
mvn -pl :spring-boot-web-mvc spring-boot:run
curl http://localhost:8081/api/ping
```

Expected: the page still supports both test-driven and local HTTP exploration.

- [x] **Step 3: Split the overly long README into clear bands without moving files**

Keep all existing links, but make the entry page bands explicit:

```markdown
## 5 分钟入口
## 本模块读法
## 症状驱动导航
## 可运行入口
## 目录（唯一顺序来源）
```

Expected: the README becomes the sample for a large Boot module entry page.

- [x] **Step 4: Verify key links still exist**

Run:

```bash
test -f spring-boot-modules/spring-boot-web-mvc/docs/guide-from-annotations-to-breakpoints.md
test -f spring-boot-modules/spring-boot-web-mvc/docs/dispatcherservlet-call-chain.md
test -f spring-boot-modules/spring-boot-web-mvc/docs/controller-boundary.md
test -f spring-boot-modules/spring-boot-web-mvc/docs/exception-resolvers-and-error-flow.md
```

Expected: all commands exit 0.

---

## Task 6: Rewrite the Controller Boundary Chapter Sample

**Files:**
- Modify: `spring-boot-modules/spring-boot-web-mvc/docs/controller-boundary.md`

- [x] **Step 1: Remove template-language from the chapter card**

Replace the chapter-card prose with:

```markdown
Controller 是 MVC 主线里的“业务方法执行段”。如果请求在选路、参数解析、绑定、校验、消息转换或异常解析阶段失败，现象会表现为 controller 没执行、执行前失败、或执行后响应形状不符合预期。

本章的任务不是扩展 controller 写法，而是把 controller 放回请求主线，判断一个问题应当改 controller、binder/converter，还是 advice/resolver。
```

Expected: no `本章围绕` or `目标是` remains in the content page.

- [x] **Step 2: Replace English template headings**

Rename:

```markdown
## 关键对象（Key Objects）
## 扩展点（Extension Points）
```

to:

```markdown
## Controller 在请求主线中的位置
## Controller 能声明哪些契约
```

Expected: the headings read as chapter prose, not template fields.

- [x] **Step 3: Make evidence entries copyable**

Under the evidence section, include:

```bash
mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcLabTest test
mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcViewLabTest test
mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBindingDeepDiveLabTest test
```

Expected: the reader can run the listed evidence entries directly.

- [x] **Step 4: Verify no template-language remains in this chapter**

Run:

```bash
rg -n "本章围绕|目标是|Key Objects|Extension Points|学习目标" spring-boot-modules/spring-boot-web-mvc/docs/controller-boundary.md
```

Expected: no output.

---

## Task 7: Phase 0 Verification

**Files:**
- Verify: all Phase 0 files

- [x] **Step 1: Run template-language scan on Phase 0 sample files**

Run:

```bash
rg -n "本章围绕|主线可以概括为|目标是把|本文将|先把结论|Key Objects|Extension Points|学习目标" \
  README.md \
  docs/book/README.md \
  spring-core-modules/spring-core-beans/README.md \
  spring-core-modules/spring-core-beans/docs/ioc-bean-mental-model.md \
  spring-boot-modules/spring-boot-web-mvc/README.md \
  spring-boot-modules/spring-boot-web-mvc/docs/controller-boundary.md
```

Expected: no output.

- [x] **Step 2: Run documentation build**

Run:

```bash
cd docs-site && mkdocs build -f mkdocs.yml --site-dir /tmp/site-springboot-learning
```

Expected: build exits 0. Existing warnings are acceptable only if they are unrelated to Phase 0 changes.

- [x] **Step 3: Run the two sample module test suites**

Run:

```bash
mvn -q -pl :spring-core-beans test
mvn -q -pl :spring-boot-web-mvc test
```

Expected: both commands exit 0.

- [x] **Step 4: Record Phase 0 completion in the full spec**

Append to `docs/plans/2026-04-29-docs-full-manual-rewrite-spec.md` under `## 10. 执行记录`:

```markdown
### Batch 1：Phase 0 基线与样板

- 日期：2026-04-29
- 范围：`docs/writing-style-guide.md`、根 README、全书主线入口、`spring-core-beans` 样板、`spring-boot-web-mvc` 样板。
- 结果：形成后续模块计划可引用的入口页、模块 README、机制正文与轻量正文样板。
- 验证：记录 mkdocs build 与两个模块测试结果。
```

Expected: the full spec keeps a high-level execution log while this plan keeps the detailed task list.

- [x] **Step 5: Commit Phase 0**

Run:

```bash
git add docs/plans/2026-04-29-docs-full-manual-rewrite-spec.md \
  docs/plans/2026-04-29-docs-phase-0-baseline-samples-plan.md \
  docs/writing-style-guide.md \
  README.md \
  docs/book/README.md \
  spring-core-modules/spring-core-beans/README.md \
  spring-core-modules/spring-core-beans/docs/ioc-bean-mental-model.md \
  spring-boot-modules/spring-boot-web-mvc/README.md \
  spring-boot-modules/spring-boot-web-mvc/docs/controller-boundary.md
git commit -m "docs: establish full rewrite baseline samples"
```

Expected: commit succeeds after verification.
