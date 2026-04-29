# 全量人工精读重写 Spec

> 本 spec 是本轮文档改造的唯一总 spec。后续可以按模块创建独立 plan，但每个 plan 必须引用本文档，并继承本文档的范围、验收口径与失控防护规则。

**Goal:** 对本仓库全部读者可见文档做逐字人工精读与重写，把“劣质 AI 生成感、模板化清单、空泛元叙事”改造成中文教材式项目学习手册，同时保留可运行实验、断点观察点、链接导航与回归验证能力。

**Architecture:** 以现有 Markdown 与 MkDocs 结构为基础，原地重写正文，默认不改文件名、不拆目录、不重排站点导航。执行上采用“唯一总 spec + 模块级 plan + 单篇验收记录”的方式控制范围，保证每批可审阅、可构建、可回滚。

**Tech Stack:** Markdown、MkDocs Material、`mkdocs-literate-nav`、`docs/SUMMARY.md`、各模块 `README.md`、Maven 测试入口（`*LabTest` / `*BookMatrixLabTest` / `*BranchMatrixLabTest`）。

---

## 1. 背景

仓库已经有教材化写作规范与历史改写计划：

- [`docs/writing-style-guide.md`](../writing-style-guide.md)：当前写作规范。
- [`docs/plans/2026-02-26-docs-textbook-style-rewrite.md`](2026-02-26-docs-textbook-style-rewrite.md)：历史教材化计划。
- [`docs/plans/2026-02-26-docs-textbook-style-rewrite-progress.md`](2026-02-26-docs-textbook-style-rewrite-progress.md)：历史进度记录。
- [`docs/plans/2026-03-07-spring-core-beans-docs-humanize-design.md`](2026-03-07-spring-core-beans-docs-humanize-design.md)：`spring-core-beans` 的局部“说人话”设计。

这些文件说明项目已经意识到“AI 腔”和模板化问题，但现有正文仍存在结构性残留：章首同质化、证据入口被埋没、标题像模板、列表多于解释、实验与结论之间缺少因果连接。本轮不再做局部润色，而是按“逐篇人工精读”重建文档质量。

---

## 2. 改造原则

### 2.1 人工精读优先

每篇正文必须先完整阅读原文，再判断主题、证据链、读者困惑和下一跳。不得只根据标题重写，不得用批量生成替代人工判断。

### 2.2 一个 spec，允许模块 plan

本文档是唯一总 spec，用来固定全量人工重写的目标、范围、验收标准与失控防护。模块推进可以创建独立 plan，例如 `docs/plans/YYYY-MM-DD-<module>-docs-rewrite-plan.md`，但每个模块 plan 必须满足：

- 明确引用本文档。
- 不改变本文档定义的必改范围与非目标。
- 只细化该模块的文件顺序、人工精读步骤、验证命令与提交边界。
- 不在模块 plan 中重新定义一套冲突的写作标准或验收标准。

### 2.3 原地重写，降低导航风险

默认保持：

- 文件路径不变。
- 模块内 `README.md` 作为目录顺序来源。
- `docs/SUMMARY.md` 作为站点导航 SSOT。
- `<!-- CHAPTER-CARD:START -->`、`<!-- GLOBAL-BOOK-NAV:START -->`、`<!-- BOOKIFY:START -->` 等注释块不删除；内容可重写。

只有当文件本身重复、过时或链接错误会持续伤害阅读体验时，才提出合并、删除或重命名，并在同一批次内完成链接修复与构建验证。

### 2.4 证据链不可丢

每篇技术正文至少保留一个可验证入口：

- Maven 命令。
- 测试类或测试方法。
- 源码入口或断点观察点。
- 期望观察到的事实。

如果原文没有证据入口，应补为“待补证据”的明确问题并在同批次内查找现有测试；查无现有测试时，正文不能伪造命令或断点。

### 2.5 中文教材式项目手册

目标不是写成营销页，也不是写成百科全书。正文应像一本可顺读的技术教材，同时服务项目实践：

- 先说明问题和现象。
- 再给最短可运行入口。
- 然后解释机制。
- 最后给边界、排障入口和下一跳。

---

## 3. 范围

### 3.1 必改范围

以下 Markdown 属于本轮必改范围：

- 根入口：`README.md`。
- 文档站入口：`docs/SUMMARY.md`、`docs-site/README.md`。
- 全书主线：`docs/book/*.md`。
- 写作规范：`docs/writing-style-guide.md`。
- Spring Boot 模块文档：`spring-boot-modules/*/README.md` 与 `spring-boot-modules/*/docs/*.md`。
- Spring Core 模块文档：`spring-core-modules/*/README.md` 与 `spring-core-modules/*/docs/*.md`。

### 3.2 审慎范围

以下文件只在影响读者体验或后续执行时修改：

- `docs/plans/*.md`：历史计划保留上下文，不按正文风格强制重写；本文档作为后续唯一总 spec，模块级 plan 可以在此目录下创建。
- `reference/**/*.md`：作为参考资料保留原貌，除非被主线或模块文档直接引用且内容错误。
- `AGENTS.md`：保留托管块与项目指令，不作为普通文档重写对象。

### 3.3 非目标

- 不重写 Java 代码。
- 不为了文风调整测试语义。
- 不一次性重命名目录或文件。
- 不把所有页面压成同一套固定标题模板。
- 不把外部官方资料改写进正文，官方资料只作为校验与延伸链接。

---

## 4. 合格正文标准

每篇重写后的正文必须通过以下检查。

### 4.1 30 秒入口检查

读者打开第一屏，应能回答：

1. 这篇在解释哪个具体现象或问题？
2. 最短验证入口是什么？
3. 读完之后下一步去哪一页、哪条调用链或哪个测试？

### 4.2 证据链检查

技术结论必须能回到至少一种证据：

- 运行命令：`mvn -q -pl :<module> -Dtest=<TestClass> test`。
- 测试类路径。
- 断点方法或关键变量。
- 可观察的状态码、异常类型、返回值、Bean 名称、线程名、SQL、日志类别等。

### 4.3 文风检查

必须清理或重写以下低质量模式：

- “本章围绕……展开，目标是……”
- “主线可以概括为……”
- “先把结论放前面……”
- “本文将介绍……”
- 大段无证据的建议、推荐、强烈建议。
- 机械标题：`Key Objects`、`Extension Points`、`学习目标`、孤立的“常见误区”清单。
- 只有列表，没有解释原因、验证方式和边界。

### 4.4 结构检查

正文页推荐结构：

1. 问题或现象。
2. 最短实验。
3. 机制解释。
4. 边界与反例。
5. 排障或下一跳。
6. 小结。

目录页推荐结构：

1. 模块边界。
2. 10 分钟入口。
3. 阅读路线。
4. 症状驱动索引。
5. 可运行命令。
6. 目录顺序。

这不是强制模板。每篇应按主题自然组织，但不得丢失上述信息。

---

## 5. 单篇人工精读流程

每篇文档按同一流程处理。

### Step 1：完整阅读

从标题读到末尾，标出：

- 真实主题。
- 读者困惑。
- 原文可保留的技术事实。
- 原文空泛、重复、错误或过时的位置。
- 需要核验的测试、链接、断点、术语。

### Step 2：查证证据

在修改前核对：

- 文中测试类是否存在。
- Maven 模块名是否正确。
- 链接目标是否存在。
- 断点方法是否属于当前 Spring / Boot 基线可识别入口。
- 同一主题是否已有更权威的模块页或 guide 页。

### Step 3：重建开头

用 1-3 段回答“这篇为什么存在”。开头不写空泛导语，不堆标题，不先列概念清单。

### Step 4：重写正文

保留正确事实，重写表达顺序：

- 把清单改成因果段落。
- 把“建议”改成“现象 -> 原因 -> 验证”。
- 把术语堆叠改成可观察对象。
- 把多个重复入口合并成一个最短入口。

### Step 5：重写边界

每篇至少说明一个容易误判的边界。边界必须包含：

- 表面现象。
- 根因或机制分支。
- 如何验证。

### Step 6：收尾验收

检查：

- 标题层级连续。
- 代码块语言标注正确。
- 相对链接可解析。
- README 使用中文。
- 没有遗留模板腔。
- 小结不超过 3 条，且能回指到正文证据。

---

## 6. 批次设计

批次按“入口优先、核心机制优先、读者路径优先”排序。每个 Phase 可以拆成多个 Batch；本文档维护批次总览与验收口径，模块级 plan 维护具体执行步骤。

### Phase 0：基线与样板

**目标：**建立可执行质量线，防止后续模块越改越散。

**文件：**

- `docs/writing-style-guide.md`
- `README.md`
- `docs/book/README.md`
- `spring-core-modules/spring-core-beans/README.md`
- `spring-core-modules/spring-core-beans/docs/ioc-bean-mental-model.md`
- `spring-boot-modules/spring-boot-web-mvc/README.md`
- `spring-boot-modules/spring-boot-web-mvc/docs/controller-boundary.md`

**验收：**

- 形成 README、模块 README、普通正文、长 guide 的样板。
- 跑通 `cd docs-site && mkdocs build -f mkdocs.yml --site-dir /tmp/site-springboot-learning`。
- 样板页无模板腔，且证据入口真实存在。

### Phase 1：全站入口层

**目标：**让读者从任意入口都进入同一套阅读逻辑。

**文件：**

- `README.md`
- `docs/SUMMARY.md`
- `docs-site/README.md`
- `docs/book/README.md`
- 所有 `spring-boot-modules/*/README.md`
- 所有 `spring-core-modules/*/README.md`

**验收：**

- 所有 README 为中文。
- 每个模块 README 有模块边界、最短入口、阅读路线、目录顺序、排障入口。
- 不重复维护模块内 `docs/*.md` 的顺序到 `docs/SUMMARY.md`。

### Phase 2：全书主线

**目标：**把 `docs/book` 改成可顺读的教材主线，而不是模块索引。

**文件：**

- `docs/book/01-getting-started.md`
- `docs/book/02-spring-boot-basics.md`
- `docs/book/03-spring-core-beans.md`
- `docs/book/04-spring-core-aop.md`
- `docs/book/05-spring-core-tx.md`
- `docs/book/06-spring-boot-web-mvc.md`
- `docs/book/07-spring-core-validation.md`
- `docs/book/08-spring-boot-testing.md`
- `docs/book/09-spring-boot-data-jpa.md`
- `docs/book/10-spring-boot-web-client.md`
- `docs/book/11-spring-boot-async-scheduling.md`
- `docs/book/12-spring-boot-cache.md`
- `docs/book/13-observability-and-actuator.md`
- `docs/book/14-spring-boot-security.md`
- `docs/book/90-troubleshooting-index.md`
- `docs/book/91-glossary.md`
- `docs/book/92-references.md`

**验收：**

- 每章只做“最小解释 + 实验入口 + 下一跳”，不复制模块正文。
- 每章有可运行 `*BookMatrixLabTest` 或等价实验入口；没有现成入口时明确使用模块主线 Lab。
- 排障索引能从症状跳到模块章节和断点地图。

### Phase 3：Spring Core 核心机制模块

**目标：**先处理术语重、AI 味最容易放大的底层机制文档。

**模块顺序：**

1. `spring-core-modules/spring-core-beans`
2. `spring-core-modules/spring-core-aop`
3. `spring-core-modules/spring-core-tx`
4. `spring-core-modules/spring-core-validation`
5. `spring-core-modules/spring-core-events`
6. `spring-core-modules/spring-core-resources`
7. `spring-core-modules/spring-core-profiles`
8. `spring-core-modules/spring-core-spel`
9. `spring-core-modules/spring-core-aop-weaving`

**单模块顺序：**

1. `README.md`
2. `docs/guide-*.md`
3. 主线正文。
4. 边界、排障、性能或真实场景正文。
5. `docs/appendix-*.md`

**验收：**

- 每个模块至少一次模块测试：`mvn -q -pl :<module> test`。
- 每个模块至少一次文档站构建。
- 每个模块完成后记录已改文件数、跳过原因、遗留风险。

### Phase 4：Spring Boot 业务路径模块

**目标：**把读者最容易直接使用的 Boot 模块改成“问题驱动 + 可运行入口”的学习手册。

**模块顺序：**

1. `spring-boot-modules/spring-boot-basics`
2. `spring-boot-modules/spring-boot-web-mvc`
3. `spring-boot-modules/spring-boot-data-jpa`
4. `spring-boot-modules/spring-boot-testing`
5. `spring-boot-modules/spring-boot-business-case`
6. `spring-boot-modules/spring-boot-security`
7. `spring-boot-modules/spring-boot-cache`
8. `spring-boot-modules/spring-boot-async-scheduling`
9. `spring-boot-modules/spring-boot-web-client`
10. `spring-boot-modules/spring-boot-actuator`
11. `spring-boot-modules/spring-boot-observability`
12. `spring-boot-modules/spring-boot-autoconfiguration`
13. `spring-boot-modules/spring-boot-logging`

**单模块顺序：**

1. `README.md`
2. 请求、配置、数据、测试等主线正文。
3. guide 与 branch matrix。
4. appendix 与 self-check。

**验收：**

- 每篇正文能把表面现象映射到具体 Spring Boot 机制。
- HTTP、JPA、Security、Cache 等模块的状态码、异常、SQL、线程、端点暴露等观察对象写清楚。
- 不把“业务建议”写成无证据口号。

### Phase 5：全仓复查

**目标：**发现跨模块不一致、断链、术语漂移和批次之间的风格偏差。

**检查项：**

- 全仓 Markdown 链接。
- `README.md` 中文规则。
- 模板腔残留。
- 重复章节、重复解释、过时导航。
- `docs/SUMMARY.md` 与模块 README 的职责边界。

**验收：**

- `cd docs-site && mkdocs build -f mkdocs.yml --site-dir /tmp/site-springboot-learning` 成功。
- `mvn -q test` 在可接受时间内成功；若全量测试时间过长，至少跑完所有改动模块测试并记录未跑全量的原因。
- 最终正文不出现大面积同质化章首。

---

## 7. 失控防护

### 7.1 批次上限

单个 Batch 最多包含：

- 1 个大模块，或
- 2 个小模块，或
- 1 个全站入口层子集。

超过上限时必须拆成下一批；批次总览记录在本文档，具体步骤可以写入对应模块 plan。

### 7.2 每批固定出口

每批完成前必须满足：

- 本批所有文件已逐篇精读。
- 已运行文档构建。
- 已运行相关模块测试或说明跳过原因。
- 已记录遗留问题。

### 7.3 禁止中途扩范围

遇到代码问题、测试缺失、文档结构缺陷时，只能做三种处理：

1. 同批次内修复小的文档引用或链接错误。
2. 在本文档“遗留问题记录”里登记。
3. 单独请求用户确认是否把代码或测试补强纳入后续工作。

不得在文档重写批次里顺手重构 Java 代码。

### 7.4 回滚边界

每个 Batch 应独立提交。建议 commit message：

- `docs: rewrite docs entry layer`
- `docs(core-beans): manual rewrite`
- `docs(web-mvc): manual rewrite`
- `docs(book): rewrite mainline chapters`

如果某批质量不符合预期，应能单独 `git revert <commit>`，不影响其他模块。

---

## 8. 验证命令

### 文档站构建

```bash
cd docs-site && mkdocs build -f mkdocs.yml --site-dir /tmp/site-springboot-learning
```

预期：构建成功。允许历史 warning，但不得出现新增 error。

### 单模块测试

```bash
mvn -q -pl :<module> test
```

预期：当前批次涉及模块测试通过。

### 全量测试

```bash
mvn -q test
```

预期：全量测试通过。若执行成本过高，必须记录已执行的替代测试范围。

### 模板腔扫描

```bash
rg -n "本章围绕|主线可以概括为|目标是把|本文将|先把结论|Key Objects|Extension Points|学习目标" README.md docs spring-core-modules spring-boot-modules -g '*.md'
```

预期：正文范围无新增命中。历史计划或写作规范中的反例命中可保留。

### README 中文规则

```bash
rg --files -g 'README.md'
```

预期：所有 README 内容为中文；英文术语、类名、命令、官方名称可保留。

---

## 9. 单篇验收记录格式

每篇重写完成后，在当批工作记录中用以下格式记录。工作记录可以放在对应模块 plan、本文档的“执行记录”章节，或作为提交说明的一部分；无论放在哪里，都必须能回链到本文档的最终验收标准。

```markdown
- `path/to/file.md`
  - 类型：模块 README / 普通正文 / guide / appendix / book chapter
  - 处理：重写 / 小修 / 保留
  - 证据入口：测试类、命令或断点
  - 链接核验：已核验 / 无外链 / 有遗留
  - 遗留问题：无 / 具体问题
```

---

## 10. 执行记录

### Batch 0：spec 建立

- 日期：2026-04-29
- 范围：建立唯一总 spec。
- 结果：本文档定义全量人工精读重写的目标、范围、批次、验收与失控防护。
- 下一批：Phase 0 基线与样板。

后续批次可以在这里追加总览记录；具体模块执行可以创建模块级 plan，但模块级 plan 必须引用本文档。

### Batch 1：Phase 0 基线与样板

- 日期：2026-04-29
- 范围：`docs/writing-style-guide.md`、根 README、全书主线入口、`spring-core-beans` 样板、`spring-boot-web-mvc` 样板。
- 结果：形成后续模块计划可引用的入口页、模块 README、机制正文与轻量正文样板。
- 验证：模板腔扫描无正文命中；`mkdocs build -f mkdocs.yml --site-dir /tmp/site-springboot-learning` 成功（保留历史 SUMMARY 链接 warning）；`mvn -q -pl :spring-core-beans test` 成功；`mvn -q -pl :spring-boot-web-mvc test` 成功。

---

## 11. 最终验收

全量人工重写完成时，必须满足：

1. 必改范围内每篇文档都有人工精读记录。
2. 所有模块 README 都能在第一屏给出模块边界和最短入口。
3. 所有技术正文都有证据入口或明确说明为什么没有现成入口。
4. `docs/book` 能作为顺读主线，不再只是链接聚合。
5. `docs/SUMMARY.md` 与模块 README 的职责不冲突。
6. 文档站构建成功。
7. 已运行全量测试，或记录不可运行全量测试的具体原因和替代验证范围。
8. 模板腔扫描没有正文范围内的新增问题。
