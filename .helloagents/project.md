# 项目技术约定（Project Technical Conventions）

## 技术栈

- **Java:** 17
- **构建工具:** Maven（多模块）
- **Spring Boot:** 由父 `pom.xml` 统一管理

## 代码与命名约定

- **模块组织:** 每个学习主题一个独立 Maven module（例如 `spring-core-beans`、`spring-core-events`）。物理目录按领域分组：`spring-core-modules/<module>` 与 `spring-boot-modules/<module>`。
- **包名约定:** `com.learning.springboot.<module>`（例如 `com.learning.springboot.springcorebeans`）。
- **风格优先级:** 以当前仓库代码风格为准（避免为了“更优雅”而过度重构）。

## 测试与验证约定

- **测试框架:** JUnit 5 + AssertJ（由 `spring-boot-starter-test` 提供）
- **测试目标:** “可断言的观察点”优先于日志（能稳定复现实验结论）

### 测试类型约定（Labs / Exercises / Solutions）

> 约定：在本仓库里，“**测试就是教材的一部分**”。  
> 因此测试命名不仅用于回归，更用于表达“你应该验证什么”。

- **`*LabTest`（实验 / 证据链）**
  - 目标：用可断言的方式复现某个机制结论（主线/关键分支/边界条件）。
  - 特点：默认参与回归；断言关注“稳定契约/可观察行为”。
- **`*ExerciseTest`（练习 / 自己补齐）**
  - 目标：留出缺口，让学习者按提示完成实现/修复/补齐断言。
  - 特点：通常默认 `@Disabled`（避免 CI 回归失败）；通过提示把读者指向对应 doc 与推荐断点。
- **`*ExerciseSolutionTest`（参考答案 / 对照）**
  - 目标：提供 Exercise 的可运行参考实现，便于对照与自检。
  - 特点：默认参与回归；尽量复用 Exercise 的场景与断言口径。

### 断言粒度约定（Tests as SSOT）

- 结论以“断言可复现”为准；日志/`System.out.println("OBSERVE: ...")` 只作为辅助观察点。
- 尽量断言稳定契约与可观察信号（响应状态码、异常类型、DB 行数、调用轨迹计数等），避免把内部实现细节写死为强断言。
- 当需要断点观察内部机制时，优先写“推荐断点清单/观察点”，而不是把内部细节当作测试断言。

### 命令与运行约定

- **单模块测试（推荐用 artifactId，避免路径耦合）：** `mvn -q -pl :<artifactId> test`
- **单测试类：** `mvn -q -pl :<artifactId> -Dtest=SomeTest test`
- **单测试方法：** `mvn -q -pl :<artifactId> -Dtest=SomeTest#someMethod test`
- **调试（attach 5005）：** `mvn -q -pl :<artifactId> -Dtest=SomeTest#someMethod -Dmaven.surefire.debug test`
- **脚本约定：**本仓库已移除 `scripts/` 快捷脚本，统一使用上面的 Maven 命令即可。

---

## 文档门禁与批处理（Docs as Code）

> 目标：把“更深入”的文档变成可回归资产：可导航、可验证、可断点、可排障，并能通过脚本稳定维护一致性。

### 1) 章节学习卡片（五问闭环）

（历史上由 `scripts/` 下的批处理脚本统一维护；当前仓库已移除 `scripts/`，因此这些操作以手工维护为准。）

### 2) 正文二次书籍化（Intro/Evidence/Summary 骨架）

（同上：当前以手工维护为准。）

### 3) 章节尾部入口块（测试入口 + 上一章/目录/下一章）

（同上：当前以手工维护为准。）

### 4) 文档质量门禁（建议在批处理后运行）

最小验证建议改为：

- 本地构建文档站：`cd docs-site && mkdocs build -f mkdocs.yml`
  - 目的：确保全站目录 `docs/SUMMARY.md` 可解析、页面可渲染
  - 说明：MkDocs 不保证覆盖所有 Markdown 相对链接的“文件存在性”校验，但能覆盖导航与页面渲染的基本正确性

### 5) 深挖基线审计（docs/tests 入口清单）

（仓库已移除 `scripts/`，该审计脚本入口不再提供。）

### 文档 ↔ 测试入口绑定约定

- `*/docs/**.md` 章节的末尾应包含 `### 对应 Lab/Test` 区块（建议手工维护；历史上由批处理脚本生成）。
- 文档引用一律使用可解析的 Markdown 相对链接（避免 `docs/NN` 这种缩写引用造成断链）。
- 如果文档结论依赖某个可复现分支，必须在文档中显式写出：
  - 推荐测试入口（类名/方法名）
  - 推荐断点/观察点（类/方法）

### Troubleshooting 条目模板（建议在 pitfalls/playbook 中统一采用）

> 目标：让读者能从“现象”快速回到“机制主线”，并落到“可验证入口”。

- **现象（Symptom）：** 你看到了什么？（状态码/异常/行为差异）
- **根因（Root Cause）：** 这通常对应哪个机制分支？（Bean 生命周期/AOP 调用链/事务边界/MVC 链路）
- **如何验证（Verification）：** 跑哪个 Lab/Test？断言看什么？
- **推荐断点（Breakpoints）：** 该从哪里下断点能最快看到关键分支？
- **修复建议（Fix）：** 学习用最小修复是什么？工程上推荐做法是什么？

---

## 文档章节契约模板（Chapter Contract）

> 目标：让每章都能做到“可导航 + 可验证 + 可断点 + 可排障”，避免出现只有标题没有落地内容的空块。

建议每个 `docs/**.md` 章节至少包含以下信息（可按 A–G 结构组织，也可按更适合的方式组织，但契约点要齐）：
（注：当前仓库文档实际位于各模块的 `*/docs/**.md` 下。）

1. **本章输出（What you get）**
   - 读完本章你应该能回答的 2–3 个问题（可复述）
2. **关键分支（Decisive Branch）**
   - 至少 1 个“如果/否则”的分支判断（例如：是否命中某个 resolver / 是否被代理 / 是否进入 async 二次 dispatch）
3. **验证入口（Verification Entry）**
   - 至少 1 个方法级可运行入口：`mvn -q -pl :<artifactId> -Dtest=Class#method test`
   - 说明“你应该看到什么”（断言/输出/可观察信号）
4. **断点与观察点（Debugger Pack）**
   - Entrypoints（2+）：关键入口方法（例如 `DispatcherServlet#doDispatch` / `AbstractApplicationContext#refresh`）
   - Watch List（3+）：建议观察字段/数据结构（例如 `resolvedException` / `singletonObjects`）
5. **常见坑与边界（Pitfalls & Boundaries）**
   - 至少 2 条“现象 → 根因 → 如何验证”的排障分流
6. **一句话自检（Self-check）**
   - 至少 3 个自检问题（概念/边界/排障），并尽量绑定到可跑入口
7. **章节末尾入口块（测试入口 + 导航）**
   - `### 对应 Lab/Test`（测试类路径）
   - `上一章｜目录｜下一章`（稳定导航）

---

## 跨模块链接与索引规范（避免漂移）

> 目标：读者能在 1–2 次跳转内从“现象/问题”到达“对应章节 + 可跑入口”，同时避免链接在维护中不断漂移。

1. **相对链接优先**
   - 文档内部与知识库一律使用 Markdown 相对链接（仓库内可解析）。
2. **索引集中、入口收敛**
   - 模块入口以 `helloagents/wiki/modules/<module>.md` 为“知识库侧索引”。
   - 模块内部入口以 `<module>/docs/README.md` 为“模块侧索引（目录页）”。
   - 全局主线入口以 `helloagents/wiki/learning-path.md` 为“路线图索引”。
3. **避免 `docs/NN` 缩写引用**
   - 章节引用必须写成真实相对路径链接，避免读者误解与断链。
4. **跨模块引用只做必要连接**
   - 原则：只连接主线相关的“下一跳”，不要在正文里发散到大量外链。
   - 例：Web MVC 的 `@Valid` 章只需要链接到 Validation/Beans 的关键章节/模块页即可。
