# Task List: spring-core-beans docs 书面化改写（去口语化）

Directory: `helloagents/plan/202601281326_spring_core_beans_docs_formalize/`

---

## 1. Documentation（书面化改写）

- [√] 1.1 扫描 docs 口语化标记（第二人称/俚语/课堂化措辞），in `spring-core-modules/spring-core-beans/docs/**/*.md`, verify why.md#文档书面化去口语化-阅读与复述
- [√] 1.2 对 `spring-core-modules/spring-core-beans/docs/**/*.md` 全量执行书面化改写（跳过 fenced code block，保持技术语义不变）, verify why.md#文档书面化去口语化-阅读与复述
- [√] 1.3 重点抽查并校正高敏感章节（调用链/决策表/面试答案/排障清单），避免语义偏移, verify why.md#文档书面化去口语化-排障复盘

## 2. Security Check

- [√] 2.1 安全自检（G9）：确认无敏感信息、无危险命令示例、无生产环境操作暗示

## 3. Verification

- [√] 3.1 关键短语回归扫描：确认“你/如果你/踩坑/翻车”等口语标记显著减少（docs 全量扫描）
- [√] 3.2 可选：运行模块测试 `mvn -pl :spring-core-beans test`（如耗时可跳过并记录原因）

## 4. Knowledge Base Sync（SSOT）

- [√] 4.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：记录本轮“书面化改写”变更与索引入口
- [√] 4.2 更新 `helloagents/CHANGELOG.md`：记录本轮 docs 风格改写

## 5. Migration

- [√] 5.1 更新本 task.md 状态：逐条把已完成任务标记为 `[√]`，跳过标记为 `[-]`，失败标记为 `[X]` 并写明原因
- [√] 5.2 迁移 `helloagents/plan/202601281326_spring_core_beans_docs_formalize/` → `helloagents/history/2026-01/202601281326_spring_core_beans_docs_formalize/`
- [√] 5.3 更新 `helloagents/history/index.md`：新增索引记录（✅Completed）
