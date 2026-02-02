<!-- encoding: UTF-8 -->
# 怎么做（How）

## 版本语境（统一前提）

- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）
- Spring Boot：`3.5.9`

## 下压原则（避免模板化）

- 不生成“统一栏目/固定小标题”，只在“读者会产生疑问的那一段”就地补 1 行对照入口
- 同一章优先选择与段落主题最匹配的 Reference 页面（Beans/Annotation/JavaConfig/Scopes/Factory-Extension/SpEL/Resources/Environment/Conversion/AOT/Boot Auto-Config）
- 避免重复：若段落附近已有对照入口，则跳过

## 落点选择（按章节实际结构）

优先落在这些位置之一（每章不强求同一种位置）：

1) “关键分支/决策树/分流表”小节入口处
2) “排障决策表/异常定位”小节入口处
3) “常见误区与边界”小节中最关键的结论段落处

## 验证与归档

- 回归：`mvn -q -pl :spring-core-beans test`
- 同步：`helloagents/wiki/modules/spring-core-beans.md`、`helloagents/CHANGELOG.md`、`helloagents/history/index.md`
- 迁移：方案包迁移到 `helloagents/history/2026-02/`

