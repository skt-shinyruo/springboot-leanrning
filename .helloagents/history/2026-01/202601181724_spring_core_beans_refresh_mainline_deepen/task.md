[√]（Lightweight Iteration）加深 spring-core-beans “refresh → doCreateBean” 主线叙事

## 目标
[√] 在不改变现有实验/测试结构的前提下，补充一篇“源码主线”叙事文档：从 `AbstractApplicationContext#refresh` 走到 `AbstractAutowireCapableBeanFactory#doCreateBean`，覆盖关键方法与关键分支（Spring Framework 6.2.15 基线）。
[√] 将该主线文档与现有容器内部章节（part-03）以及 guide 索引串联，提升可读性与可验证性。
[√] 同步更新知识库（helloagents/wiki/modules）与 CHANGELOG 记录本次文档增强。

## 任务清单
[√] 新增主线叙事文档（连续叙事、源码级关键方法/分支、结合本仓库 LabTest 验证路径）。
[√] 在容器内部导览页中加入“主线叙事入口”与阅读建议。
[√] 在 spring-core-beans 模块文档 README / deep-dive guide 中加入主线文档入口（不做大范围重排）。
[√] 更新 `helloagents/wiki/modules/spring-core-beans.md`：补充本次新增章节与推荐阅读/验证方式。
[√] 更新 `helloagents/CHANGELOG.md`：记录本次文档增强（不改动代码行为）。
[√] 质量自检：链接可达（本地相对路径）、术语一致（refresh/BeanFactory/BPP/BDRPP）、关键方法名与 Spring 6.2.15 对齐；并运行 `mvn -q -pl spring-core-beans test` 通过。
