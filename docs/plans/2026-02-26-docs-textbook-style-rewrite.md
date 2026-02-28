# 文档教材化改写 Implementation Plan

> 执行说明：按任务逐步推进，每个批次完成后做一次 `mkdocs build` 校验，避免在全仓库范围内一次性改动导致回归困难。

**Goal:** 将本仓库文档从“模板化清单 + AI 腔”迁移为更像书籍/教材的连续叙述（中性讲授口吻），同时保留“可运行实验入口/证据链/导航（SUMMARY）”的可用性。

**Architecture:** 先制定写作规范与样板（`docs/book/` + 2 个样板模块），再按模块分批迁移；优先原地改写以保持 `docs/SUMMARY.md` 的链接稳定；必要时只做小幅导航重排（不追求一次性重构所有 1132 篇 Markdown）。

**Tech Stack:** Markdown、MkDocs Material、`mkdocs-literate-nav`（读取 `docs/SUMMARY.md`）、Maven 测试入口（`*LabTest/*BookMatrixLabTest`）作为可验证证据链。

---

## 写作规范（统一口吻与章法）

### 约束 1：中性教材口吻（不聊天、不元叙事）

- 避免第一/第二人称与“推荐/先把结论放前面”等口吻；用“本章/本节/读者/可以观察到/因此”推进。
- 观点必须能回指到：实验入口、断言、断点观察点（而不是抽象建议）。

### 约束 2：章节的默认骨架（像书，不像清单）

对正文页（`part-xx/*.md`）优先使用以下结构（允许因主题增删）：

1. 问题（开篇 1–2 段，点名困惑/现象）
2. 实验（最短可跑入口 + 期望观察点）
3. 解释（机制主线，段落推进为主）
4. 边界（反例/误区：现象→原因→如何验证）
5. 小结（≤3 条可复述句）
6. 延伸阅读（少量且说明“为何读它”）

对目录页（`*/docs/README.md`）优先使用：

- 导论（本模块边界/读完能做什么）
- 入口（10 分钟最短闭环 + 症状/主题索引）
- 阅读路线（主线 → internals → appendix）

### 约束 3：对现有标记的处理策略

- 保留现有 `<!-- CHAPTER-CARD:START -->`、`<!-- GLOBAL-BOOK-NAV:START -->`、`<!-- BOOKIFY:START -->` 注释块（它们目前不影响 MkDocs 渲染，且未来可能被脚本消费）。
- 允许调整注释块内的文字；不强制全仓库删除这些标记（避免一次性大迁移风险）。

---

## Task 1: 建立“教材化写作规范”样板页

**Files:**
- Create: `docs/writing-style-guide.md`
- Modify: `docs/SUMMARY.md`

**Step 1: 写作规范页（教材口吻 + 例子）**

- 内容包含：标题层级、段落推进、实验块写法、反例写法、引用/链接策略、常见“AI 腔”替换表。

**Step 2: 更新 `docs/SUMMARY.md` 把规范页放到“维护者可见”位置**

- 放置建议：`封面 ` 之后、` 全书主线` 之前或附录中新增“写作规范（维护者）”条目。

**Step 3: 构建文档站验证无断链**

Run: `cd docs-site && mkdocs build -f mkdocs.yml`
Expected: build 成功（site_dir 输出到 `/.site-springboot-learning`）。

---

## Task 2: 重写全书主线的“序言与章法说明”

**Files:**
- Modify: `docs/book/README.md`
- (Optional) Modify: `docs/book/01-getting-started.md`
- (Optional) Modify: `docs/book/02-spring-boot-basics.md`

**Step 1: 将 `docs/book/README.md` 从“分节模板”改为教材序言**

- 目标：读者能理解“主线章节 vs 模块正文”的分工，且能自然走进第 1 章。

**Step 2: 选 1～2 章做成样板章（建议 01、02）**

- 目标：把“实验入口/观察点/边界/小结”写成连贯叙述，不堆清单。

**Step 3: mkdocs build 验证**

Run: `cd docs-site && mkdocs build -f mkdocs.yml`
Expected: build 成功。

---

## Task 3: 样板模块一（spring-boot-basics）

**Files:**
- Modify: `spring-boot-modules/spring-boot-basics/docs/README.md`
- Modify: `spring-boot-modules/spring-boot-basics/docs/part-01-boot-basics/01-property-sources-and-profiles.md`

**Step 1: 目录页改为“导论 + 路线 + 索引”**

- 保留可跑入口（Book Matrix / Branch Matrix / Solutions），但从“列表”改为“实验”叙述。

**Step 2: 重写正文样板章**

- 把“Environment 是最终事实”写成问题推进：何时会误判、如何用最短观察点收敛、为何 Profile 会影响 Bean 注册。
- 将“常见坑”写成 2～3 个反例段落（现象→原因→验证）。

**Step 3: mkdocs build 验证**

Run: `cd docs-site && mkdocs build -f mkdocs.yml`
Expected: build 成功。

---

## Task 4: 样板模块二（spring-core-beans）

**Files:**
- Modify: `spring-core-modules/spring-core-beans/docs/README.md`
- Modify: `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-bean-mental-model.md`

**Step 1: 目录页从“导航清单”改成“课程导论”**

- 强化“读完能做什么”（解释机制/断点/排障），用段落组织阅读路线。

**Step 2: 重写一篇正文样板章（Bean 心智模型）**

- 目标：把“定义/实例/最终暴露对象”的分层写成可讲授的连续叙述。
- 保留实验入口与关键断点，但让它们服务于段落叙述。

**Step 3: mkdocs build 验证**

Run: `cd docs-site && mkdocs build -f mkdocs.yml`
Expected: build 成功。

---

## Task 5: 扩展到全仓库的迁移策略（迭代）

**Files:**
- Modify: `docs/plans/2026-02-26-docs-textbook-style-rewrite.md`（记录迁移进度与下一批模块清单）

**Step 1: 按模块制定迁移批次**

- 每批次 1～2 个模块：先改目录页，再改 1～2 篇代表正文，最后再覆盖 appendix。

**Step 2: 每批次完成后做一次 mkdocs build**

Run: `cd docs-site && mkdocs build -f mkdocs.yml`
Expected: build 成功。

> 说明：在受限沙箱环境中，`docs-site/mkdocs.yml` 的默认 `site_dir` 可能不可写；可改用：
> `cd docs-site && mkdocs build -f mkdocs.yml --site-dir /tmp/site-springboot-learning`

---

## Task 6: 批量迁移“所有模块”的入口页（目录页 + 模块 README）

**Goal:** 让读者无论从 GitHub 模块页（`*/README.md`）还是从站点侧边栏（`*/docs/README.md`）进入，都能看到一致的教材式入口：问题边界、10 分钟实验、阅读路线、排障入口与可运行回归命令。

**Files (per module):**
- Modify: `spring-boot-modules/<module>/README.md`
- Modify: `spring-boot-modules/<module>/docs/README.md`
- Modify: `spring-core-modules/<module>/README.md`
- Modify: `spring-core-modules/<module>/docs/README.md`

**Step 1: 统一入口结构**

- 将 “目录页/读者导言/Start Here/学习产出” 等模板化小节改写为：
  - 模块边界（1–2 段）
  - 10 分钟入口（Book Matrix）
  - 阅读路线（主线→排障）
  - 可运行入口（Book/Branch/Solutions/Perf）
  - 排坑与自检（pitfalls/self-check）

**Step 2: 全站构建校验**

Run: `cd docs-site && mkdocs build -f mkdocs.yml --site-dir /tmp/site-springboot-learning`
Expected: build 成功（允许存在历史遗留 WARNING，但不应出现 ERROR）。
