# 文档教材化改写进度（2026-02-26）

本文件用于记录“同一文风（中性教材口吻）”的迁移进度，避免在全仓库范围内推进时失去阶段性边界。

## 已完成（入口层）

已将以下两类入口页统一为教材式写法（模块边界 → 10 分钟入口 → 阅读路线/排障入口 → 可运行回归命令）：

- 各模块的 `*/README.md`（站点入口）
- 各模块的 `*/README.md`（GitHub/仓库入口，已去除第二人称与“Start Here/学习产出”这类模板腔）

覆盖模块：

- Spring Boot（应用层）：`spring-boot-actuator`、`spring-boot-async-scheduling`、`spring-boot-autoconfiguration`、`spring-boot-basics`、`spring-boot-business-case`、`spring-boot-cache`、`spring-boot-data-jpa`、`spring-boot-logging`、`spring-boot-observability`、`spring-boot-security`、`spring-boot-testing`、`spring-boot-web-client`、`spring-boot-web-mvc`
- Spring Core（基础设施）：`spring-core-aop`、`spring-core-aop-weaving`、`spring-core-beans`、`spring-core-events`、`spring-core-profiles`、`spring-core-resources`、`spring-core-spel`、`spring-core-tx`、`spring-core-validation`

## 已完成（2026-04-28：全模块入口与导航统一）

- 22 个模块 `README.md` 已补齐“本模块读法”，统一说明“先跑入口 → 再读主线 → 最后排障”的阅读顺序。
- 长篇 `guide-*` / `appendix-*` 导航页已补齐“本页路线图”，把实验入口、源码/断点、常见坑与自检闭环放在页首。
- 全模块 Markdown 已统一“观察点/观察清单”术语，并把行首 `1)` 风格列表改为标准 Markdown 编号；代码块内的伪代码编号保持原样。

## 已完成（2026-04-28：正文精读与全模块复查）

- `docs/book/*.md` 已把“学习目标/概念框架/练习”等模板标题改为更自然的教材式标题，并修正了主线目录中“默认路径/实验路径”的表达。
- `spring-core-modules/*` 与 `spring-boot-modules/*` 的正文页已清理残留的 `Start Here`、`Watch List/Watchpoints`、`推荐先跑/建议先跑` 等混杂表达，统一为“本模块读法/观察清单/先运行入口”。
- 全模块 `guide-mainline-timeline.md` 已把早置的“小结与下一章”移到正文尾部，恢复“导读 → 主线 → 证据链 → 小结”的阅读顺序。
- `spring-boot-basics` 的旧迁移页已改为可直接阅读的主线压缩页；`spring-core-aop-weaving` 的重复“本模块验证点”已改为可执行的验证重点。
- 主动保留 `docs/plans/*` 中的历史描述与 `docs/writing-style-guide.md` 中的反例句，因为它们用于说明迁移背景或坏例，不属于正文口吻残留。

## 已完成（2026-04-28：第二轮逐字复查）

- 正文范围再次扫描并清理第一/第二人称、`本文`、`学习目标`、`建议/推荐` 等口吻残留；正文中这些规则词已无命中。
- `spring-boot-web-mvc/docs/guide-from-annotations-to-breakpoints.md` 这类长文已从对话式“你/你的”改为中性教材表达。
- 修复批量改写后的空标题与空括号，保持标题可读、段落顺序可跟随。

## 已完成（样章层）

- 全书主线样章：`docs/book/README.md`、`docs/book/01-getting-started.md`、`docs/book/02-spring-boot-basics.md`
- 模块正文样章：
  - `spring-boot-basics`：`docs/part-01-boot-basics/01-property-sources-and-profiles.md`
  - `spring-core-beans`：`docs/part-01-ioc-container/09-bean-mental-model.md`

## 后续维护

后续新增正文页时，按本文件与 [`docs/writing-style-guide.md`](../writing-style-guide.md) 的规则维护：

1. 新增/移动模块内 `docs/*.md` 时，先更新对应模块 `README.md` 的目录顺序。
2. 新增长篇 guide/appendix 页时，保留“本页路线图 + 可运行入口 + 证据链”的闭环。
3. 不再新增 `Start Here`、`Watch List`、空泛“学习目标/练习”标题；结论必须回链到可运行入口或断点观察点。
