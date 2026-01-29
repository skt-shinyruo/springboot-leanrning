# Task List: spring-core-beans 将 A–E 策略写入各章节正文

Directory: `helloagents/plan/202601291256_spring-core-beans-ae-into-chapters/`

---

任务状态：
- `[ ]` Pending
- `[√]` Completed
- `[X]` Failed
- `[-]` Skipped
- `[?]` To be confirmed

## 1. 全章正文插入 A–E 深化提示块

- [√] 1.1 Part-00：为 `spring-core-modules/spring-core-beans/docs/part-00-guide/*.md` 插入章节级 `AE-DEEPENING` 块，并确保内容与 `docs/deepening-strategies/part-00-guide.md` 一致
- [√] 1.2 Part-01：为 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/*.md` 插入章节级 `AE-DEEPENING` 块，并确保内容与 `docs/deepening-strategies/part-01-ioc-container.md` 一致
- [√] 1.3 Part-02：为 `spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/*.md` 插入章节级 `AE-DEEPENING` 块，并确保内容与 `docs/deepening-strategies/part-02-boot-autoconfig.md` 一致
- [√] 1.4 Part-03：为 `spring-core-modules/spring-core-beans/docs/part-03-container-internals/*.md` 插入章节级 `AE-DEEPENING` 块，并确保内容与 `docs/deepening-strategies/part-03-container-internals.md` 一致
- [√] 1.5 Part-04：为 `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/*.md` 插入章节级 `AE-DEEPENING` 块，并确保内容与 `docs/deepening-strategies/part-04-wiring-and-boundaries.md` 一致
- [√] 1.6 Part-05：为 `spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/*.md` 插入章节级 `AE-DEEPENING` 块，并确保内容与 `docs/deepening-strategies/part-05-aot-and-real-world.md` 一致
- [√] 1.7 Appendix：为 `spring-core-modules/spring-core-beans/docs/appendix/*.md` 插入章节级 `AE-DEEPENING` 块，并确保内容与 `docs/deepening-strategies/appendix.md` 一致

## 2. 安全检查

- [√] 2.1 安全检查：重点审阅涉及 SpEL/表达式/反射/AOT 的新增提示块措辞，避免引导高风险用法

## 3. SSOT 同步

- [√] 3.1 同步模块知识库：`helloagents/wiki/modules/spring-core-beans.md`
- [√] 3.2 更新变更记录：`helloagents/CHANGELOG.md`

## 4. Testing

- [√] 4.1 全量回归：`mvn -pl :spring-core-beans test`
