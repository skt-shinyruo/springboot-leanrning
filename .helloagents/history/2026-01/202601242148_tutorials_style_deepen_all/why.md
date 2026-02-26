# Change Proposal: Tutorials 风格结构重排与深度化改造

## Requirement Background
当前仓库已形成“模块化学习 + 文档站点 + Book 主线”的完整体系，但目录结构与 tutorials 风格存在显著差异，导致模块入口与主题索引的对齐成本高；同时优先模块的深度内容尚未统一到“源码入口/调用链/断点地图/分支矩阵/可跑 Lab”的证据链标准。

## Change Content
1. 目录与模块结构重排：按主题建立顶层分组目录，统一模块命名与入口 README，尽量对齐 tutorials 的“顶层分类 + 子模块”模式。
2. 文档导航对齐：以 `docs/SUMMARY.md`、`docs/README.md`、`docs/topics/index.md` 为三大入口，重排导航与主题索引，保持站点可搜索与目录稳定。
3. 内容深度化：对优先模块（Boot 基础、Web MVC、Beans、AOP、Tx）补齐深挖结构与证据链。
4. 迁移兼容与验证：提供迁移映射与重定向策略，确保构建、链接与阅读入口可用。

## Impact Scope
- **Modules:** `spring-boot-modules/`、`spring-core-modules/`、相关子模块目录
- **Files:** `pom.xml`、聚合 POM、`docs/SUMMARY.md`、`docs/README.md`、`docs/topics/index.md`、优先模块文档
- **APIs:** 无新增对外 API
- **Data:** 无数据模型变更

## Core Scenarios

### Requirement: 目录与模块结构重排
**Module:** 构建与工程结构
在保持模块功能不变的前提下，将模块放置到与主题一致的顶层目录，并同步聚合 POM。

#### Scenario: 构建入口保持可用
迁移后仍可通过根 `pom.xml` 与主题聚合 POM 构建指定模块。

### Requirement: 文档导航对齐与索引统一
**Module:** 文档系统
将文档导航与主题索引统一到固定入口，避免目录漂移与多源维护。

#### Scenario: 站点导航稳定
`docs/SUMMARY.md` 与主题索引保持一致，侧边栏入口可顺读。

### Requirement: 内容深度化规范与首批模块落地
**Module:** 文档内容
为优先模块补齐“源码入口/调用链/断点地图/分支矩阵/实验验证”。

#### Scenario: 证据链完整
每个优先模块至少有 1 个可跑 Lab 作为结论验证入口。

### Requirement: 迁移兼容与验证
**Module:** 迁移策略
提供旧路径到新路径映射与必要的跳转提示。

#### Scenario: 链接不失效
旧链接可定位到新入口或提示迁移路径。

## Risk Assessment
- **Risk:** 目录结构重排导致构建与文档链接失效
- **Mitigation:** 分阶段迁移、提供映射表与重定向、同步更新导航与索引、增量验证
