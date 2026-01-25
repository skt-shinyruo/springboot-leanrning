# How：实现方案（入口对齐 + 章节可跑闭环）

## 方案概览

1) **README 作为封面/导读**

- 在根 `README.md` 增加“阅读方式（顺读主线 / 目标驱动）”
- 增加“主线之书 Book-only”目录入口，并提供 0–18 章的跳转链接
- 给出文档站点入口 `docs-site/README.md`（便于本地预览/构建/发布）

2) **Book-only 章节补齐“可跑入口”**

- 在 `docs/book/00-18` 各章尾部新增统一区块：`## 本章可跑入口（最小闭环）`
- 每章固定提供两条信息（用 inline code 展示，避免站内断链风险）：
  - Lab：`mvn -q -pl <module> -Dtest=<LabTest> test` + 对应测试文件路径
  - Exercise：对应 `*ExerciseTest.java` 文件路径（提示默认 `@Disabled`）

## 验证

## 风险与规避

- 风险：在书章节中新增指向源码/测试的 Markdown 链接，可能造成站内断链或构建失败。
  - 规避：只使用 inline code 展示路径与命令，不新增源码链接。
- 风险：README 新增链接目标不存在。
