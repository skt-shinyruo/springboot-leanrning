# Task List：全模块书籍化重写（去 A–G + 主线叙事）

Directory: `helloagents/plan/202601151647_docs-book-rewrite-all-modules/`

---

## 1. 现状盘点（范围与模式）
- [√] 1.1 统计全仓 A–G 标题与 AG-CONTRACT 标记覆盖范围
- [√] 1.2 抽样 2–3 个模块章节，确认 A–G 的稳定模式与变体

## 2. 自动化迁移（第一层：去契约化）
- [√] 2.1 新增批处理脚本：全仓移除 A–G 字母前缀与 AG-CONTRACT 标记
- [√] 2.2 将“核心结论”转换为 `summary` 提示框（更像书的“本章要点”）
- [√] 2.3 把 `BOOKIFY` 的 Lab/Test 信息提炼为章首 `example` 提示框
- [√] 2.4 更新模块/知识库中对 A–G 的说明（例如 `docs/beans/spring-core-beans/README.md`）

## 3. 校验（自动化迁移后）

## 4. 主线时间线重排（第二层：逐模块分批）
- [ ] 4.1 定义每个模块的“主线时间线”与章节边界（拆/合策略）
- [ ] 4.2 先做主线 5 模块（Basics/Beans/AOP/Tx/Web MVC），其余模块后续分批
