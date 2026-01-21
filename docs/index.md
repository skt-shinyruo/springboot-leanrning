# Spring Boot Learning：主线之书

这个站点把仓库里的多模块 `docs/` 聚合成一个可搜索的静态站点，并且把“跨模块主线”整理成一套可顺读的书籍目录（Book）。

---

## Start Here（推荐入口）

- 书的目录与阅读方法：[主线之书 · 目录](book/index.md)
- 直接开始跑与读：[第 1 章：Start Here](book/001-start-here.md)

---

## 先跑起来（最小闭环）

```bash
mvn -q test
```

只跑单模块（更快）：

```bash
mvn -q -pl :<artifactId> test
```

---

## 模块 docs（素材库/索引）

侧边栏现在同时提供两条入口：

- **模块文档**：按主题 → 模块 → 章节浏览（适合深挖细节/边界条件）
- **主线之书**：跨模块顺读主线（推荐路径）

- 主题索引（模块 docs 入口）：[topics/index.md](topics/index.md)
