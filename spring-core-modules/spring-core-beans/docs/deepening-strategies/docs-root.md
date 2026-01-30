# 逐章内容级再加深建议（Docs TOC / 目录页）

目标：把 `docs/README.md` 强化为“症状驱动导航中枢”，同时保证它不替代章节正文，而是把读者精准送到“章节 + 证据链 + Lab”。

## 执行化提示（把目录页与章节闭环对齐）

- 章节层已统一补齐：开头“章节学习卡片（五问闭环）”与“上一章/下一章导航”。
- 目录页的最佳增益点：把“症状 → 章节”升级为“症状 → 章节卡片（入口方法/推荐 Lab） → 断点/观察点”，避免目录页堆概念。

### spring-core-beans 文档导航（Docs TOC）

- 关联文件：`spring-core-modules/spring-core-beans/docs/README.md`
- 本轮内容级加深策略（A–E）：
  - A：为症状导航表补“证据链入口方法提示”（例如依赖解析从 `doResolveDependency` 进，代理替换从 `applyBeanPostProcessorsAfterInitialization` 进）。
  - B：为每个症状补 1 个“最常见误诊反例”（例如把 `Circular depends-on relationship` 当三级缓存循环依赖）。
  - C：把常见异常按“定义层失败/实例层失败/运行期行为异常”分型，并提供第一断点入口。
  - D：与 Debugger Pack/断点地图互链：目录页告诉读者何时用“断点地图（主线）”，何时用“Debugger Pack（专题）”。
  - E：把面试题库与章节绑定：提供“面试题 → 章节 → Lab”的跳转入口，帮助读者用证据链回答。
