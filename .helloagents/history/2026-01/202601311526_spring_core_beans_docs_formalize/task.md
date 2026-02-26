# Task List: spring-core-beans 文档书面化改写（去口语化）

Directory: `helloagents/history/2026-01/202601311526_spring_core_beans_docs_formalize/`

---

## 1. Scope & Baseline
- [√] 1.1 确认改写范围：仅 Markdown（默认）或包含 `src/test/**` 的练习说明文本（可选），并记录最终范围到 `helloagents/history/2026-01/202601311526_spring_core_beans_docs_formalize/how.md`
- [√] 1.2 建立“口语化短语清单 + 替换原则”，用于执行期扫描与人工复核（记录到 `helloagents/history/2026-01/202601311526_spring_core_beans_docs_formalize/how.md`）

## 2. Entry Docs (Priority)
- [√] 2.1 改写 `spring-core-modules/spring-core-beans/README.md`，统一为书面语体，保留命令与链接
- [√] 2.2 改写 `spring-core-modules/spring-core-beans/docs/README.md`，统一为书面语体，保留目录结构与相对链接

## 3. Chapter Docs (Batch Rewrite)
- [√] 3.1 改写 `spring-core-modules/spring-core-beans/docs/part-00-guide/**/*.md`（导读/断点地图/主线时间轴等）
- [√] 3.2 改写 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/**/*.md`（注册/注入/生命周期/扩展点）
- [√] 3.3 改写 `spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/**/*.md`（Boot 自动装配叠加语义）
- [√] 3.4 改写 `spring-core-modules/spring-core-beans/docs/part-03-container-internals/**/*.md`（refresh 主线与处理器算法）
- [√] 3.5 改写 `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/**/*.md`（候选选择/代理/占位符/转换等）
- [√] 3.6 改写 `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/**/*.md`（AOT、XML、FactoryBean 等）
- [√] 3.7 改写 `spring-core-modules/spring-core-beans/docs/deepening-strategies/**/*.md` 与 `spring-core-modules/spring-core-beans/docs/deepening-strategies/README.md`
- [√] 3.8 改写 `spring-core-modules/spring-core-beans/docs/appendix/**/*.md`（术语表/面试题库/断点包/内训讲义等）

## 4. Optional Extension (If Selected in 1.1)
- [-] 4.1 书面化改写 `spring-core-modules/spring-core-beans/src/test/**` 中面向读者的练习/提示文本（仅字符串与注释，不更改测试逻辑）

## 5. Security Check
- [√] 5.1 执行安全自检（按 G9：不引入敏感信息、命令示例最小权限、避免误导性操作步骤）

## 6. Verification
- [√] 6.1 执行“口语化短语清单”扫描，确保目标范围内无残留（允许出现在代码块中的情形需人工确认并记录保留理由）
- [√] 6.2 抽检关键入口文档相对链接可用：`spring-core-modules/spring-core-beans/README.md` 与 `spring-core-modules/spring-core-beans/docs/README.md`
- [√] 6.3 运行回归测试：`mvn -pl :spring-core-beans test`

## 7. Knowledge Base Sync
- [√] 7.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：补充本次“文档语体规范化”的约定与入口链接（如有调整）
- [√] 7.2 更新 `helloagents/CHANGELOG.md` 记录本次变更

## 8. Solution Package Lifecycle
- [√] 8.1 开发实现完成后，将本 solution package 迁移到 `helloagents/history/2026-01/202601311526_spring_core_beans_docs_formalize/`，并更新 `helloagents/history/index.md`
