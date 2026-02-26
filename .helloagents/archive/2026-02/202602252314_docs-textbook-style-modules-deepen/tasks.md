# 任务清单: docs-textbook-style-modules-deepen

> **@status:** completed | 2026-02-26 00:57

```yaml
@feature: docs-textbook-style-modules-deepen
@created: 2026-02-25
@status: completed
@mode: R3
```

<!-- LIVE_STATUS_BEGIN -->
状态: completed | 进度: 14/14 (100%) | 更新: 2026-02-26 00:58:30
当前: 已归档到 .helloagents/archive/2026-02/
<!-- LIVE_STATUS_END -->

## 进度概览

| 完成 | 失败 | 跳过 | 总数 |
|------|------|------|------|
| 13 | 0 | 1 | 14 |

---

## 任务列表

### 1. 上下文与基线（入口闭环）

- [√] 1.1 汇总模块清单与可跑入口映射（artifactId / BookMatrix / BranchMatrix）
- [√] 1.2 扫描模块根 README 与 docs 入口的断链/过时路径，并形成修复清单

### 2. 模块根 README（GitHub 入口）教材化

- [√] 2.1 Spring Boot 模块根 README：补齐 Start Here（Book/Branch Matrix）+ docs 阅读顺序（可点击）+ 排坑/自检出口（13 个模块）
- [√] 2.2 Spring Core 模块根 README：补齐 Start Here（Book/Branch Matrix）+ docs 阅读顺序（可点击）+ 排坑/自检出口（9 个模块）
- [√] 2.3 修复已知过时引用（例如 `00-deep-dive-guide.md`、`90-common-pitfalls.md`、`99-self-check.md`）并统一为真实文件名

### 3. 模块 docs 自检页（复盘出口）加厚

- [√] 3.1 Spring Boot 模块：重写/对齐 `docs/appendix/02-self-check.md`（剥离常见坑正文、补齐证据链入口、修复跨模块导航）
- [√] 3.2 Spring Core 模块：重写/对齐 `docs/appendix/02-self-check.md`（Beans 按其既有 appendix 文件命名单独处理）
- [√] 3.3 为“多 BranchMatrix”的模块补齐选择建议与入口表（如 AOP/Tx/Events/Beans/AOP-Weaving）

### 4. 模块 docs 常见坑页（按需对齐）

- [-] 4.1 仅在必要时补齐 `docs/appendix/01-common-pitfalls.md` 的“最短证据链入口”（Book/Branch Matrix 命令 + 断点/分支矩阵链接）

### 5. 构建与链接自检

- [√] 5.1 运行 `mkdocs build`（`docs-site/`）验证站点可构建
- [√] 5.2 抽查/脚本校验入口页相对链接：模块根 README + self-check 页（至少覆盖全模块）

### 6. 记录与归档

- [√] 6.1 更新 `.helloagents/CHANGELOG.md`（新增：docs 教材化入口层加厚；附方案包链接）
- [√] 6.2 迁移方案包到 `.helloagents/archive/2026-02/` 并更新索引（`_index.md`）
- [√] 6.3 遗留方案包扫描与处理建议（如存在）

---

## 执行日志

| 时间 | 任务 | 状态 | 备注 |
|------|------|------|------|
| 2026-02-26 00:57 | 6.2 | [√] | 已迁移到 `.helloagents/archive/2026-02/` 并更新 `.helloagents/archive/_index.md` |

---

## 执行备注

> 记录执行过程中的重要说明、决策变更、风险提示等

- 扫描 `plan/` 发现另一个方案包 `202602031617_beans_part01_docs_retemplate` 仍不完整（0 tasks）。如该方案已不再推进，建议将其补齐任务清单后再归档，或直接标记 `skipped` 归档，避免长期堆积。
