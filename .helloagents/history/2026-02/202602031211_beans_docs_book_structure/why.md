# Change Proposal: spring-core-beans 文档书籍化目录重整

## Requirement Background
`spring-core-modules/spring-core-beans/docs` 当前已按 Part 分目录组织，但入口 `README.md` 同时承担“导读 + 运行方式 + 路线选择 + 症状导航 + 全量目录”多重职责，导致：

1. 入口页信息密度过高，难以把握“书籍阅读顺序”
2. 目录与导读混杂，目录维护成本高（链接多、易失序）
3. 同一套文档在 `docs-site/.generated` 中存在镜像路径，需要保持一致

本变更希望把“书籍结构”显式化：入口页只做“封面/导读”，目录单独沉淀为 `SUMMARY.md`，并按阅读顺序编排。

## Change Content
1. 新增模块级 `SUMMARY.md`，作为 **书籍目录 SSOT**
2. 精简 `README.md` 的“目录”段落：保留 Part 级导航与阅读指引，详细目录迁移到 `SUMMARY.md`
3. 同步 `docs-site/.generated` 中对应文件，保持站点渲染来源一致
4. 同步知识库模块说明，明确入口与目录位置

## Impact Scope
- **Modules:**
  - spring-core-modules/spring-core-beans
  - docs-site（.generated 镜像）
  - helloagents/wiki（模块说明）
- **Files:**
  - `spring-core-modules/spring-core-beans/docs/README.md`
  - `spring-core-modules/spring-core-beans/docs/SUMMARY.md` (new)
  - `docs-site/.generated/docs/spring-core-beans/docs/README.md`
  - `docs-site/.generated/docs/spring-core-beans/docs/SUMMARY.md` (new)
  - `helloagents/wiki/modules/spring-core-beans.md`

## Core Scenarios

### Requirement: 读者入口清晰
**Module:** spring-core-beans docs
读者打开模块文档后能快速理解“怎么读”和“从哪里开始”，并能一键跳转到全量目录。

#### Scenario: 从 README 进入并按书籍顺序阅读
打开 `docs/README.md` 后：
- 能看到推荐起点与阅读路线
- 能通过 `SUMMARY.md` 获取按顺序编排的完整目录

### Requirement: 目录可维护
**Module:** spring-core-beans docs
目录应当成为单独文件，减少 README 的维护压力，并能稳定扩展章节。

#### Scenario: 新增章节时只需更新 SUMMARY
- README 无需堆叠大量链接
- 目录顺序由 SUMMARY 统一控制

## Risk Assessment
- **Risk:** 目录重整导致旧链接失效或站点镜像不一致
- **Mitigation:** 不改动现有章节文件路径；仅新增 SUMMARY 并重构 README 的目录段落；同步更新 `docs-site/.generated` 镜像路径；增加链接存在性校验

