# task.md - spring-core-beans docs coherence

> 目标：解决 `docs/beans/spring-core-beans` 章节“拼接感/不连贯”，在不丢知识点的前提下，把 01→02→03 读起来像连续教程。

- [√] 重构 `docs/01-bean-mental-model.md`：补齐章节契约（本章定位/输出/实验/下一章），把“可选深挖”与主线阅读分层表达
- [√] 重构 `docs/01-bean-registration.md`：强化与 01 的承接（定义层），补齐到 03 的桥接（实例层依赖解析入口）
- [√] 重构 `docs/03-dependency-injection-resolution.md`：统一章节契约，并把“发生阶段（populateBean/doResolveDependency）”与上一章定义层关联起来
- [√] 同步知识库：更新 `helloagents/wiki/modules/spring-core-beans.md` 记录推荐阅读路径与章节契约
- [√] 更新 `helloagents/CHANGELOG.md`：记录本次文档连贯性重构
- [√] 自检：运行 `mvn -q -pl spring-core-beans test`
