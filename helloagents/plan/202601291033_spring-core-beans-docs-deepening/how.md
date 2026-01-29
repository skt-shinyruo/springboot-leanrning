# 如何执行逐章“补充 + 完善 + 深入”

## 执行原则（不搞统一硬标准，但要可落地）

你明确要求“不设固定标准”，所以本方案不强制每一章都必须补齐某个固定栏目。

但为了可执行与可验收，我们仍然会用一些 **通用切入点** 来驱动深入（它们不是“模板”，而是“加深阅读与复现的杠杆”）：

- **可视化**：把复杂链路/状态变化画出来（时序图/状态机/决策树）。
- **可复现**：对照实验（成功/失败/边界）让读者“跑得出来”。
- **可定位**：明确源码入口、最短调用链、关键分支与变量观察点。
- **可迁移**：把结论写成“判断条件 + 适用范围 + 反例边界”，能迁移到真实项目。
- **可排障**：把常见异常/日志与章节机制域建立映射。
- **版本语义**：标注 Spring Framework / Spring Boot 版本基线与差异点（本仓库父 POM：Spring Boot 3.5.9，Java 17）。

## 本次阅读输出的位置

逐章建议已按 docs 的目录结构拆分存放在：

- `helloagents/plan/202601291033_spring-core-beans-docs-deepening/chapters/module-readme.md`
- `helloagents/plan/202601291033_spring-core-beans-docs-deepening/chapters/docs-root.md`
- `helloagents/plan/202601291033_spring-core-beans-docs-deepening/chapters/part-00-guide.md`
- `helloagents/plan/202601291033_spring-core-beans-docs-deepening/chapters/part-01-ioc-container.md`
- `helloagents/plan/202601291033_spring-core-beans-docs-deepening/chapters/part-02-boot-autoconfig.md`
- `helloagents/plan/202601291033_spring-core-beans-docs-deepening/chapters/part-03-container-internals.md`
- `helloagents/plan/202601291033_spring-core-beans-docs-deepening/chapters/part-04-wiring-and-boundaries.md`
- `helloagents/plan/202601291033_spring-core-beans-docs-deepening/chapters/part-05-aot-and-real-world.md`
- `helloagents/plan/202601291033_spring-core-beans-docs-deepening/chapters/appendix.md`

每个章节条目都包含：

- 本章已有素材提示（入口方法/关键类型/对应实验等）；
- 针对本章主题的补充与深入策略（按性价比排序）。

## 执行方式（进入 ~exec 阶段时）

建议按“分批迭代”推进，避免一次性改动过大难以回归：

1. 先执行 `part-00-guide` 与 `part-03-container-internals`（收益最高、牵引全局理解）
2. 再执行 `part-04-wiring-and-boundaries`（日常开发与排障高频）
3. 最后补齐 `part-02/part-05/appendix` 的“可迁移/版本语义/生产案例”密度

每次落地时同时做：

- 文档补强（内容/图/索引/反例）
- 对应 Lab/Test 的补充或对照用例（保证可复现）
- 知识库同步（`helloagents/wiki/modules/spring-core-beans.md` 记录增量与入口映射）

