# 任务清单（Lightweight Iteration）

目标：继续把 `spring-core-beans` 薄弱章节改成“能跑实验 + 能下断点 + 能复述排障”的教程。

## Tasks

- [√] 重写 `docs/part-01-ioc-container/09-circular-dependencies.md`（现象分类 → 窗口期 → 三层缓存语义 → 断点闭环 → 工程策略）
- [√] 重写 `docs/part-03-container-internals/16-early-reference-and-circular.md`（early reference 形态一致性：getEarlyBeanReference / raw vs wrapped）
- [√] 重写 `docs/appendix/97-explore-debug-tests.md`（Explore gate 启用方式 + 观察点/断点 + 用回主线）
- [√] 验证：`mvn -pl :spring-core-beans test` 通过
- [√] 验证：`python3 -m mkdocs build -f docs-site/mkdocs.yml` 通过
- [√] 同步知识库：更新 `helloagents/CHANGELOG.md` / `helloagents/wiki/modules/spring-core-beans.md`
- [√] 迁移方案包到 `helloagents/history/YYYY-MM/` 并更新 `helloagents/history/index.md`
