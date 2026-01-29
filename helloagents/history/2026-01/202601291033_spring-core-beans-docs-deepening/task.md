# 任务清单（逐章深度完善：执行阶段）

> 说明：本任务清单关注“如何把逐章建议落实到实际文档/实验/索引”。逐章的具体建议已写在 `chapters/*.md`。

> Note：原本按偏好保留在 `helloagents/plan/`；已于 2026-01-29 按确认迁移到 `helloagents/history/2026-01/` 归档。

## A. 入口与导航（优先）

- [√] 执行 `chapters/module-readme.md`：完善模块 README 的“阅读入口/学习路径/版本基线/常见现象索引”
- [√] 执行 `chapters/docs-root.md`：完善 docs/README 的“导航/学习顺序/章节地图/断点入口”

## B. 分 Part 落地（按批次推进）

- [√] 执行 `chapters/part-00-guide.md`：把“主线时间线/断点地图/关键分支矩阵”补到可直接用于源码阅读与讲解
- [√] 执行 `chapters/part-01-ioc-container.md`：把 Bean 注册/DI/Scope/Lifecycle 的边界与反例补齐
- [√] 执行 `chapters/part-02-boot-autoconfig.md`：补强 Boot 介入点、顺序/条件问题的“可复现 + 排障路径”
- [√] 执行 `chapters/part-03-container-internals.md`：把 refresh→createBean 的关键分支、缓存/顺序/边界补成“可画图 + 可追踪”
- [√] 执行 `chapters/part-04-wiring-and-boundaries.md`：把装配规则、代理时机、FactoryBean、类型转换等高频坑补成“能直接排障/能应对追问”
- [√] 执行 `chapters/part-05-aot-and-real-world.md`：补强 AOT/RuntimeHints/XML/Namespace/外部对象接入等真实工程问题链路
- [√] 执行 `chapters/appendix.md`：把“误区/知识地图/排障清单/内训讲义”做成可持续维护的资料库

## C. 横向增强（贯穿所有章节）

- [√] 为关键章节新增可视化图（优先：时序图/状态机/决策树），并在 docs 里形成统一引用方式
- [√] 统一标注版本基线与差异点（Spring Boot 3.5.9 / Spring Framework 6.x 语义），避免读者混用旧资料
- [√] 增加“现象 → 章节 → 断点 → 实验”索引入口（放在 docs/README 与 appendix 知识地图里）
