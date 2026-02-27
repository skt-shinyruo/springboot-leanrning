# 文档教材化改写进度（2026-02-26）

本文件用于记录“同一文风（中性教材口吻）”的迁移进度，避免在全仓库范围内推进时失去阶段性边界。

## 已完成（入口层）

已将以下两类入口页统一为教材式写法（模块边界 → 10 分钟入口 → 阅读路线/排障入口 → 可运行回归命令）：

- 各模块的 `*/docs/README.md`（站点入口）
- 各模块的 `*/README.md`（GitHub/仓库入口，已去除第二人称与“Start Here/学习产出”这类模板腔）

覆盖模块：

- Spring Boot（应用层）：`spring-boot-actuator`、`spring-boot-async-scheduling`、`spring-boot-autoconfiguration`、`spring-boot-basics`、`spring-boot-business-case`、`spring-boot-cache`、`spring-boot-data-jpa`、`spring-boot-logging`、`spring-boot-observability`、`spring-boot-security`、`spring-boot-testing`、`spring-boot-web-client`、`spring-boot-web-mvc`
- Spring Core（基础设施）：`spring-core-aop`、`spring-core-aop-weaving`、`spring-core-beans`、`spring-core-events`、`spring-core-profiles`、`spring-core-resources`、`spring-core-spel`、`spring-core-tx`、`spring-core-validation`

## 已完成（样章层）

- 全书主线样章：`docs/book/README.md`、`docs/book/01-getting-started.md`、`docs/book/02-spring-boot-basics.md`
- 模块正文样章：
  - `spring-boot-basics`：`docs/part-01-boot-basics/01-property-sources-and-profiles.md`
  - `spring-core-beans`：`docs/part-01-ioc-container/09-bean-mental-model.md`

## 下一步（正文批量迁移建议）

为了让“同一文风”真正覆盖到阅读体验中，建议按模块逐步迁移正文页（`part-xx/*.md`）：

1. 每个模块先迁移 1 篇“主线第一章”（作为该模块正文样板）。
2. 再迁移 `appendix/01-common-pitfalls.md`（把“坑”从清单改为“反例段落”）。
3. 最后迁移 `appendix/02-self-check.md`（把自检题的描述改为更教材式的“问题—验证点”）。
