# 变更提案：全站书籍化 Stage 3（Book-only + 教学体验增强）

## 需求背景

当前仓库已经具备：

- 多模块 `docs/`（每模块有自己的目录页与章节结构）
- `docs-site`（MkDocs 聚合站点，支持搜索与侧边栏目录）
- Stage2：每模块新增“主线时间线”章节，并将模块 `docs/README.md` 重排为更书籍化的目录入口

但仍存在一个核心体验问题：**读者无法“像读书一样”从头到尾顺读跨模块主线**。

你希望进一步完善为：

- **全站以“书”为唯一目录**（侧边栏只展示书的章节树）
- 覆盖 **18 个模块**，把它们放进一条“主线时间线”
- 教学体验增强 **A/B/C/D 全部要**（实验索引、Debugger Pack、Exercises/Solutions、站内阅读体验）
- 允许在必要时移动/改名章节文件（可接受断链风险）
- 不要求先把 Stage2 的变更 commit/push 再开始 Stage3

## 变更内容

1. 建立“主线之书”（跨模块连续目录）
2. 将 `docs-site` 的导航切换为 **Book-only**（仅展示书的章节树）
3. 教学体验增强（覆盖 A/B/C/D）
   - A：可跑实验（Labs）索引与一键命令
   - B：Debugger Pack 索引（Entrypoints/Watchpoints/Decisive Branch）
   - C：Exercises/Solutions 的启用方式与组织说明
   - D：站内阅读体验优化（入口、跳转、减少噪声）
4. 迁移策略：允许合并/拆章与改名；必要时保留少量 redirect 入口（用于关键入口或站内导航）

## 影响范围

- **模块：**
  - `docs-site`（站点结构、导航、书章节内容）
  - `scripts`（生成/校验脚本，可能引入 book 级 SSOT 与校验）
  - `spring-core-*` / `springboot-*`（章节重排、必要时迁移/redirect）
  - `helloagents`（知识库说明、变更记录、方案包）
- **文件：**
  - `docs-site/mkdocs.yml`
  - `docs/book/**.md`（新增）
  - `scripts/docs-site-sync.py`（导航生成逻辑调整）
  - 各模块 `docs/README.md` 与部分章节（当进入“物理迁移/合并拆章”阶段）

## 核心场景

### Requirement: 全站以书为唯一目录（Book-only）
**Module:** `docs-site`
读者打开站点后应当把它当成一本书来读，而不是在模块目录间跳转。

#### Scenario: 侧边栏只有“主线之书”
- 侧边栏只展示“主线之书”的章节树
- 模块级目录不作为侧边栏主入口（可通过书中链接、搜索或附录页访问）

### Requirement: 覆盖 18 模块并形成主线时间线
**Module:** `docs-site` + 全模块 docs

#### Scenario: 主线章节能顺读覆盖所有模块
- 书的章节树覆盖 18 模块的关键机制点
- 每个模块至少在书中出现一次“主线落点”（章节或明确的承接链接）

### Requirement: 教学体验增强（A/B/C/D）
**Module:** `docs-site` + `scripts`

#### Scenario: 读者能“按目标”找到可跑入口与调试入口
- 能按模块/主题找到 Labs（并给出可运行命令）
- 能快速找到 Debugger Pack（entrypoints/watchpoints/decisive branch）
- Exercises/Solutions 有清晰启用方式（默认不破坏 CI）
- 站内入口更像书（Start Here、阅读路线、减少噪声链接）

### Requirement: 允许合并/拆章与改名
**Module:** 全模块 docs

#### Scenario: 章节结构能持续演进
- 允许把“机制/源码/实验/排障”融入叙事，必要时合并/拆章
- 允许移动/改名文件以适配书的章节结构（可接受断链风险）

## 风险评估

  - **缓解：** 以“书目录 SSOT + 分阶段迁移”为主；关键入口保留少量 redirect；每阶段完成后进行文档与站点构建的人工验证。
- **风险：** 未先 commit Stage2，可能导致后续回滚与对比困难。
