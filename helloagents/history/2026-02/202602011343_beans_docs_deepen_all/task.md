# Task List: spring-core-beans docs 逐章继续深化（按章差异化 + 跨模块互链）

Directory: `helloagents/plan/202602011343_beans_docs_deepen_all/`

---

## 1. 逐章落地（以策略清单为准）

> 执行时先阅读并跟随：`helloagents/plan/202602011343_beans_docs_deepen_all/audit/chapter-strategies.md`。  
> 每章“补什么/怎么补”以该清单为准，不强行套统一小标题或固定骨架。

- [√] 1.1 按 `audit/chapter-strategies.md` 逐章修订 `spring-core-modules/spring-core-beans/docs/**`（不改文件路径；按章差异化补强），verify why.md#requirement-r1-chapter-specific-deepen-scenario-s1-per-chapter-strategies
- [√] 1.2 对 `spring-core-modules/spring-core-beans/docs/deepening-strategies/**` 做降模板化处理：把“维度清单式建议”改为更可执行的阅读/验证路线（仍保持为策略文档，不强行套统一结构），verify why.md#requirement-r1-chapter-specific-deepen-scenario-s1-per-chapter-strategies

## 2. Beans → AOP 跨模块互链（按章需要补齐）

- [√] 2.1 对策略清单中标注的跨模块引用点，补齐“为什么要跳 + 跳过去验证什么”，并确保链接目标与链接文本一致（不改链接目标路径），verify why.md#requirement-r1-chapter-specific-deepen-scenario-s2-beans-to-aop-links

## 3. Security Check

- [√] 3.1 安全自检：确认新增/修改内容不包含密钥/token/内网地址/个人信息（文档示例也不应泄漏），verify why.md#requirement-r2-quality-gates-scenario-s1-self-check-pass

## 4. Quality Verification（全量）

- [√] 4.1 占位清理：扫描 TODO/FIXME/未完/待补/占位 等痕迹，并用可验证解释替换（避免保留“以后再写”），verify why.md#requirement-r2-quality-gates-scenario-s1-self-check-pass
- [√] 4.2 相对链接目标存在性检查（beans docs 全量），verify why.md#requirement-r2-quality-gates-scenario-s1-self-check-pass
- [√] 4.3 Lab/Test 引用存在性检查（beans docs 全量），verify why.md#requirement-r2-quality-gates-scenario-s1-self-check-pass

## 5. Verification（回归）

- [√] 5.1 运行 `mvn -pl spring-core-modules/spring-core-beans test`，verify why.md#requirement-r2-quality-gates-scenario-s1-self-check-pass

## 6. Knowledge Base Sync & Migration

- [√] 6.1 同步知识库与变更记录：
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/CHANGELOG.md`
  - `helloagents/history/index.md`
  verify why.md#requirement-r2-quality-gates-scenario-s1-self-check-pass
- [√] 6.2 迁移方案包到 `helloagents/history/YYYY-MM/202602011343_beans_docs_deepen_all/` 并更新 `helloagents/history/index.md`，verify why.md#requirement-r2-quality-gates-scenario-s1-self-check-pass
