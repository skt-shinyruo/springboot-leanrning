# spring-core-beans Docs Humanize Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:**对 `spring-core-modules/spring-core-beans/docs` 全目录进行“说人话”改写：减少模板化/元叙事，提高第一屏信息密度，同时保留证据链（可运行入口/断点/观察点/下一跳）。

**Architecture:**按主题分批（Guide+Appendix / IoC / Internals / Wiring / Boot+AOT / Deepening）逐批改写并逐批提交；每批尽量只改“导读/解释段落/误判对照/决策表”中最模板化的句子，不改文件名与大纲结构。

**Tech Stack:**Markdown（本仓库的教材式文风约束见 `docs/writing-style-guide.md`）；验证以链接自洽与可运行入口不丢为主（必要时回归 `mvn -pl :spring-core-beans test`）。

---

## 全局改写规则（每批都遵守）

1) **不丢证据链**：方法名、断点入口、watch list、Lab 名称不改；结论必须能回链到“入口方法 + 关键变量”。
2) **不破坏导航**：不改文件名；尽量不改标题层级与锚点；仅在明显病句/重复/空泛时改导读与解释段。
3) **减少元话术**：把“本文将/本章围绕/建议先…”压缩成 1–2 句更具体的“这一章回答什么问题/什么时候用得上/怎么验证”。
4) **句子更短**：优先拆长句；减少“口号式结论”；每一段尽量包含“因此/这意味着/可以观察到”的因果连接。
5) **口吻一致**：第三人称教材腔（读者/建议/应能够），但尽量写成“人在讲”，不是“规范条目”。

---

## Task 0：准备与基线检查

**Files:**
- Read: `docs/writing-style-guide.md`
- Read: `spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md`

**Step 1: 确认改写范围与批次划分**

- 只动 `spring-core-modules/spring-core-beans/docs/*.md`
- 每批一个 commit，commit message 使用 `docs(beans): humanize <batch>`

**Step 2: 预留回滚点**

- 保证每批都是可独立 `git revert` 的提交

---

## Task 1：Batch 1（Guide + Appendix）

**Files (Modify):**
- `spring-core-modules/spring-core-beans/docs/guide-applicationcontext-refresh-call-chain.md`
- `spring-core-modules/spring-core-beans/docs/guide-branch-decision-matrix.md`
- `spring-core-modules/spring-core-beans/docs/guide-breakpoint-map.md`
- `spring-core-modules/spring-core-beans/docs/guide-deep-dive-guide.md`
- `spring-core-modules/spring-core-beans/docs/guide-mainline-timeline.md`
- `spring-core-modules/spring-core-beans/docs/guide-quickstart-30min.md`
- `spring-core-modules/spring-core-beans/docs/guide-why-index.md`
- `spring-core-modules/spring-core-beans/docs/appendix-common-pitfalls.md`
- `spring-core-modules/spring-core-beans/docs/appendix-debugger-pack.md`
- `spring-core-modules/spring-core-beans/docs/appendix-explore-debug-tests.md`
- `spring-core-modules/spring-core-beans/docs/appendix-glossary.md`
- `spring-core-modules/spring-core-beans/docs/appendix-interview-playbook.md`
- `spring-core-modules/spring-core-beans/docs/appendix-knowledge-map.md`
- `spring-core-modules/spring-core-beans/docs/appendix-production-troubleshooting-checklist.md`
- `spring-core-modules/spring-core-beans/docs/appendix-self-check.md`
- `spring-core-modules/spring-core-beans/docs/appendix-spring-beans-public-api-gap.md`
- `spring-core-modules/spring-core-beans/docs/appendix-spring-beans-public-api-index.md`
- `spring-core-modules/spring-core-beans/docs/appendix-team-training-kit.md`

**Step 1: 改写导读第一屏**

- 把“本章围绕…展开/目标是…”改成更具体的“这一页什么时候用得上”
- 保留：配套实验入口、断点入口、下一跳（知识地图/断点地图/Branch Matrix）

**Step 2: 收敛重复信息**

- Guide 页减少重复的“阅读方式建议”句式；把“怎么用这页”写得更可执行（命令/观察点/下一跳）

**Step 3: 自检**

- 快速扫一遍链接是否仍然存在（相对路径不改）

**Step 4: Commit**

- `git add spring-core-modules/spring-core-beans/docs`
- `git commit -m "docs(beans): humanize guide+appendix"`

---

## Task 2：Batch 2（IoC Container）

**Files (Modify):**
- `spring-core-modules/spring-core-beans/docs/ioc-bean-mental-model.md`
- `spring-core-modules/spring-core-beans/docs/ioc-bean-registration.md`
- `spring-core-modules/spring-core-beans/docs/ioc-circular-dependencies.md`
- `spring-core-modules/spring-core-beans/docs/ioc-configuration-enhancement.md`
- `spring-core-modules/spring-core-beans/docs/ioc-dependency-injection-resolution.md`
- `spring-core-modules/spring-core-beans/docs/ioc-factorybean.md`
- `spring-core-modules/spring-core-beans/docs/ioc-lifecycle-and-callbacks.md`
- `spring-core-modules/spring-core-beans/docs/ioc-post-processors.md`
- `spring-core-modules/spring-core-beans/docs/ioc-scope-and-prototype.md`

**Steps:**
- 优先改“导读 + 关键分支解释”的元话术
- 让每章第一屏更快落到“现象→入口→断点→结论”
- Commit：`docs(beans): humanize ioc`

---

## Task 3：Batch 3（Container Internals）

**Files (Modify):**
- `spring-core-modules/spring-core-beans/docs/internals-bdrpp-definition-registration.md`
- `spring-core-modules/spring-core-beans/docs/internals-container-bootstrap-and-infrastructure.md`
- `spring-core-modules/spring-core-beans/docs/internals-early-reference-and-circular.md`
- `spring-core-modules/spring-core-beans/docs/internals-lifecycle-callback-order.md`
- `spring-core-modules/spring-core-beans/docs/internals-post-processor-ordering.md`
- `spring-core-modules/spring-core-beans/docs/internals-pre-instantiation-short-circuit.md`
- `spring-core-modules/spring-core-beans/docs/internals-refresh-to-bean-creation-mainline.md`

**Steps:**
- 重点改“主线叙事的连接句”，减少“列表堆叠”
- 每章至少保留一个“读者应能回答”的问题列表（≤5）
- Commit：`docs(beans): humanize internals`

---

## Task 4：Batch 4（Wiring & Boundaries）

**Files (Modify):**
- `spring-core-modules/spring-core-beans/docs/wiring-autowire-candidate-selection-primary-priority-order.md`
- `spring-core-modules/spring-core-beans/docs/wiring-bean-definition-overriding.md`
- `spring-core-modules/spring-core-beans/docs/wiring-bean-names-and-aliases.md`
- `spring-core-modules/spring-core-beans/docs/wiring-beanfactory-api-deep-dive.md`
- `spring-core-modules/spring-core-beans/docs/wiring-context-hierarchy.md`
- `spring-core-modules/spring-core-beans/docs/wiring-custom-scope-and-scoped-proxy.md`
- `spring-core-modules/spring-core-beans/docs/wiring-depends-on.md`
- `spring-core-modules/spring-core-beans/docs/wiring-environment-and-propertysource.md`
- `spring-core-modules/spring-core-beans/docs/wiring-factorybean-deep-dive.md`
- `spring-core-modules/spring-core-beans/docs/wiring-factorybean-edge-cases.md`
- `spring-core-modules/spring-core-beans/docs/wiring-generic-type-matching-pitfalls.md`
- `spring-core-modules/spring-core-beans/docs/wiring-injection-phase-field-vs-constructor.md`
- `spring-core-modules/spring-core-beans/docs/wiring-lazy-semantics.md`
- `spring-core-modules/spring-core-beans/docs/wiring-merged-bean-definition.md`
- `spring-core-modules/spring-core-beans/docs/wiring-programmatic-bpp-registration.md`
- `spring-core-modules/spring-core-beans/docs/wiring-proxying-phase-bpp-wraps-bean.md`
- `spring-core-modules/spring-core-beans/docs/wiring-resolvable-dependency.md`
- `spring-core-modules/spring-core-beans/docs/wiring-resource-injection-name-first.md`
- `spring-core-modules/spring-core-beans/docs/wiring-smart-initializing-singleton.md`
- `spring-core-modules/spring-core-beans/docs/wiring-smart-lifecycle-phase.md`
- `spring-core-modules/spring-core-beans/docs/wiring-type-conversion-and-beanwrapper.md`
- `spring-core-modules/spring-core-beans/docs/wiring-value-placeholder-resolution-strict-vs-non-strict.md`

**Steps:**
- 重点改“误归因对照/决策表”的解释句，让它更像排障笔记而不是规则列表
- Commit：`docs(beans): humanize wiring`

---

## Task 5：Batch 5（Boot + AOT + Real World）

**Files (Modify):**
- `spring-core-modules/spring-core-beans/docs/boot-auto-config-ordering.md`
- `spring-core-modules/spring-core-beans/docs/boot-debugging-and-observability.md`
- `spring-core-modules/spring-core-beans/docs/boot-spring-boot-auto-configuration.md`
- `spring-core-modules/spring-core-beans/docs/aot-aot-and-native-overview.md`
- `spring-core-modules/spring-core-beans/docs/aot-autowirecapablebeanfactory-external-objects.md`
- `spring-core-modules/spring-core-beans/docs/aot-beandefinitionreader-other-inputs-properties-groovy.md`
- `spring-core-modules/spring-core-beans/docs/aot-built-in-factorybeans-gallery.md`
- `spring-core-modules/spring-core-beans/docs/aot-custom-qualifier-meta-annotation.md`
- `spring-core-modules/spring-core-beans/docs/aot-method-injection-replaced-method.md`
- `spring-core-modules/spring-core-beans/docs/aot-property-editor-and-value-resolution.md`
- `spring-core-modules/spring-core-beans/docs/aot-runtimehints-basics.md`
- `spring-core-modules/spring-core-beans/docs/aot-spel-and-value-expression.md`
- `spring-core-modules/spring-core-beans/docs/aot-xml-bean-definition-reader.md`
- `spring-core-modules/spring-core-beans/docs/aot-xml-namespace-extension.md`

**Steps:**
- 重点把“为什么 JVM 可跑但 native 不可跑/为什么 reader 输入最终落到 BeanDefinition”讲成连续叙事
- Commit：`docs(beans): humanize boot+aot`

---

## Task 6：Batch 6（Deepening）

**Files (Modify):**
- `spring-core-modules/spring-core-beans/docs/deepening-aot-and-real-world.md`
- `spring-core-modules/spring-core-beans/docs/deepening-appendix.md`
- `spring-core-modules/spring-core-beans/docs/deepening-boot-autoconfig.md`
- `spring-core-modules/spring-core-beans/docs/deepening-container-internals.md`
- `spring-core-modules/spring-core-beans/docs/deepening-docs-root.md`
- `spring-core-modules/spring-core-beans/docs/deepening-guide.md`
- `spring-core-modules/spring-core-beans/docs/deepening-ioc-container.md`
- `spring-core-modules/spring-core-beans/docs/deepening-module-readme.md`
- `spring-core-modules/spring-core-beans/docs/deepening-strategies.md`
- `spring-core-modules/spring-core-beans/docs/deepening-wiring-and-boundaries.md`

**Steps:**
- 将“加深建议”从“提示句”改成更可执行的“下一步做什么/跑哪个 Lab/看哪个断点”
- Commit：`docs(beans): humanize deepening`

---

## Task 7：回归验证

**Step 1: 最小回归**

Run: `mvn -q -pl :spring-core-beans test`

Expected: BUILD SUCCESS

**Step 2: 提交前检查**

- `git status` 为空
- commit 历史为 6 个 batch + 设计/计划文档

