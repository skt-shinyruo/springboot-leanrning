# Why: 完成遗留未勾选任务（Docs + Labs + 知识库）

## 背景

仓库 `helloagents/history/2026-01/` 下存在多份已归档的方案包，其 `task.md` 中仍保留大量未完成（`[ ]`）条目，导致：

- 读者路径不够收敛（路线图/Start Here/可运行入口分散）
- 文档与可运行证据链（`*LabTest` 方法级入口）存在空块或缺口
- 模块间主线（Beans → AOP → Tx → Web MVC）难以形成一致的导航与排障体验

本次目标是把这些未完成项真正落地为“可导航 + 可验证 + 可排障”的教学闭环，并把遗留的未勾选任务回填为可审计状态。

## 本次范围（以三份历史 task 为 SSOT）

1. `helloagents/history/2026-01/202601131039_teaching-experience-webmvc-beans/task.md`
2. `helloagents/history/2026-01/202601091043_deepen_beans_aop_tx_web/task.md`
3. `helloagents/history/2026-01/202601051050_spring_core_beans_deepen/task.md`

## 验收标准（Definition of Done）

- 三份 `task.md` 中所有未完成（`[ ]`）条目被处理为：
  - `[√]` 已完成（有代码/文档/测试证据链）
  - `[-]` 合理跳过（明确原因：不再适用/与现有实现重复/超出本次范围）
- 新读者路径可在 1 次跳转内到达“第一个可运行入口”（命令 + 测试类 + 测试方法）
- 文档中的“入口/断点/观察点/关键分支”不再出现空块
- 关键模块回归通过（至少覆盖：`spring-core-beans`、`spring-core-aop`、`spring-core-tx`、`springboot-web-mvc`）

