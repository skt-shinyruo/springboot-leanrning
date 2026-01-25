# 任务清单（Lightweight Iteration）

目标：继续把 `spring-core-beans` 的薄弱章节改成“能跑实验 + 能下断点 + 能复述排障”的教程结构，优先修复 Part-04 wiring&boundaries 中空节/重复最明显的章节，并修复全站目录链接。

## Tasks

- [√] 重写 `docs/part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md`（两条转换链路 + 决策点 + 断点闭环 + 排障分流）
- [√] 重写 `docs/part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md`（programmatic BPP 的顺序/时机/定义层 vs 实例层注册）
- [√] 重写 `docs/part-04-wiring-and-boundaries/32-resource-injection-name-first.md`（`@Resource` name-first + 处理器依赖 + 断点闭环）
- [√] 重写 `docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md`（strict/non-strict 的决定因素 + 分流到 SpEL/转换）
- [√] 修复 `docs/SUMMARY.md` 链接：从 `docs/` 出发统一使用 `../` 前缀（GitHub/MkDocs 均可正确跳转）
- [√] 验证：`mvn -pl :spring-core-beans test` 通过
- [√] 验证：`python3 -m mkdocs build -f docs-site/mkdocs.yml` 通过（修复 SUMMARY 链接相关 warning）
- [√] 同步知识库：更新 `helloagents/CHANGELOG.md` / `helloagents/wiki/modules/spring-core-beans.md`
- [√] 迁移方案包到 `helloagents/history/YYYY-MM/` 并更新 `helloagents/history/index.md`
