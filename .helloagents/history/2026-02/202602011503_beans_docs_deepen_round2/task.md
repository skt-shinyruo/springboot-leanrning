# Task List: spring-core-beans docs 继续深化（Round 2：从入口页/工具页开始）

Directory: `helloagents/history/2026-02/202602011503_beans_docs_deepen_round2/`

---

## 0. 预备（先把施工图读清楚）

- [√] 0.1 阅读并跟随落地策略清单：`helloagents/history/2026-02/202602011503_beans_docs_deepen_round2/audit/entrypoints-round2.md`（按文件拆解“该改哪里/怎么改”），verify why.md#requirement-r1-next-round-entrypoints-scenario-s1-docs-readme-start-here

## 1. Next-round Entrypoints（入口页）

- [√] 1.1 强化“继续深化从哪里开始”：修订 `spring-core-modules/spring-core-beans/docs/README.md`，新增 Round 2 的最短开始分流（现象驱动/断点驱动/生产排障驱动），并为每条分流补齐下一步可验证动作（对应页面 + 推荐入口 Lab/Test + 断点组提示），verify why.md#requirement-r1-next-round-entrypoints-scenario-s1-docs-readme-start-here
- [√] 1.2 降重复：修订 `spring-core-modules/spring-core-beans/docs/README.md` 的“症状驱动导航”提示语，让它更明确地把读者送到 `appendix/03-knowledge-map.md` 与 `part-00-guide/07-breakpoint-map.md`（避免把 README 扩写成另一份知识地图），verify why.md#requirement-r1-next-round-entrypoints-scenario-s1-docs-readme-start-here
- [√] 1.3 强化策略入口：修订 `spring-core-modules/spring-core-beans/docs/deepening-strategies/README.md`，补齐两种进入方式（从现象进入：知识地图；从断点进入：断点地图），并用最短步骤写清“先跑什么/再看什么/如何收敛结论”，verify why.md#requirement-r1-next-round-entrypoints-scenario-s2-deepening-strategies-entry

## 2. Tool Pages as Hubs（工具页中枢化）

- [√] 2.1 强化“从症状选择断点组”：修订 `spring-core-modules/spring-core-beans/docs/part-00-guide/07-breakpoint-map.md`，新增一个极短的分流（覆盖定义注册/注入歧义/循环依赖/代理替换/@Value），并回链到知识地图入口，verify why.md#requirement-r2-tool-pages-as-hubs-scenario-s1-breakpoint-map-and-knowledge-map-linkage
- [√] 2.2 强化“可稳定跳转”：为 `spring-core-modules/spring-core-beans/docs/part-00-guide/07-breakpoint-map.md` 的 C1–C7（或关键断点组）补充稳定锚点（仅加锚点不改结构），并在 `spring-core-modules/spring-core-beans/docs/appendix/03-knowledge-map.md` 中把高频现象的断点入口链接到对应断点组，verify why.md#requirement-r2-tool-pages-as-hubs-scenario-s1-breakpoint-map-and-knowledge-map-linkage
- [√] 2.3 强化“最短诊断路径”：修订 `spring-core-modules/spring-core-beans/docs/appendix/05-production-troubleshooting-checklist.md`，为最常见 3 类事故写成 3–5 步可验证路径（注入失败/代理不生效/循环依赖），并补齐回链到章节/Lab/断点组，verify why.md#requirement-r2-tool-pages-as-hubs-scenario-s2-production-troubleshooting-shortest-path

## 3. Security Check

- [√] 3.1 安全自检：确认新增/修改内容不包含密钥/token/内网地址/个人信息（文档示例也不应泄漏），verify why.md#requirement-r3-quality-gates-scenario-s1-self-check-pass

## 4. Quality Verification（全量）

- [√] 4.1 相对链接目标存在性检查（beans docs 全量），verify why.md#requirement-r3-quality-gates-scenario-s1-self-check-pass
- [√] 4.2 引用的测试类/文件路径存在性检查（beans docs 全量），verify why.md#requirement-r3-quality-gates-scenario-s1-self-check-pass

## 5. Verification（回归）

- [√] 5.1 运行 `mvn -pl spring-core-modules/spring-core-beans test`，verify why.md#requirement-r3-quality-gates-scenario-s1-self-check-pass

## 6. Knowledge Base Sync & Migration

- [√] 6.1 同步知识库与变更记录：
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/CHANGELOG.md`
  - `helloagents/history/index.md`
  verify why.md#requirement-r3-quality-gates-scenario-s1-self-check-pass
- [√] 6.2 迁移方案包到 `helloagents/history/YYYY-MM/202602011503_beans_docs_deepen_round2/` 并更新 `helloagents/history/index.md`，verify why.md#requirement-r3-quality-gates-scenario-s1-self-check-pass
