# 技术设计：全站书籍化 Stage 3（Book-only + 教学体验增强）

## 技术方案

### 核心技术

- Markdown（书章节与索引页）
- MkDocs + Material（站点生成、搜索、侧边栏导航）
- Python（导航/索引生成脚本）
  - `scripts/docs-site-sync.py`
  - `scripts/check-teaching-coverage.py`

### 关键实现点

1. **建立 Book 目录作为“唯一目录”的 SSOT**
   - 新增 `docs/book/`，作为“主线之书”章节树的承载目录（书的章节与附录页都在这里）
   - MkDocs 侧边栏只展示 Book 章节树；模块文档作为“素材库/引用目标”保留，但不在侧边栏出现

2. **调整 `docs-site-sync.py` 的 nav 生成逻辑**
   - 现状：自动生成模块详细目录并注入 `mkdocs.yml`
   - Stage3：改为自动生成 Book 章节树并注入 `mkdocs.yml`（Book-only）
   - 模块目录页（`modules/index.md` 与 `<module>/docs/README.md`）保留为“书内引用/搜索命中/附录页入口”

3. **教学体验增强（A/B/C/D）以“书内索引页”承载**
   - A：新增 Book 的 Labs 索引页（按模块/按主题/按命令），必要时用脚本自动生成
   - B：新增 Book 的 Debugger Pack 索引页（入口断点/观察点/关键分支），以“可跳转的索引页”为主，正文以插入段为辅
   - C：新增 Exercises/Solutions 使用说明页（如何启用、如何运行、如何避免破坏 CI）
   - D：优化站点首页与 Start Here，使读者第一屏知道“先跑什么、从哪开始读”

4. **迁移策略（允许改名/移动，但仍建议少量 redirect）**
   - 用户偏好允许断链：允许把章节从模块目录迁移到 Book 目录并重排
   - 仍建议对“关键入口”保留少量 redirect（例如旧章改成“已迁移”页，指向新章），避免站内导航、搜索结果与历史链接完全失效

## 架构决策 ADR

### ADR-001：Book-only 导航，但模块文档先保留为素材库（推荐）
**Context:** 一次性把 18 模块全部章节物理迁移到 Book 目录，风险极高且不利于渐进式推进。

**Decision:** 先实现“Book-only 导航 + 书章节树”，书章节以叙事与链接承接模块章节；模块 docs 暂时保留为素材库与引用目标，后续再分阶段做物理迁移。

**Rationale:** 先解决“阅读入口”最大痛点；同时把高风险的物理迁移拆成可回滚的小批次。

**Alternatives:**
- 方案：一次性物理迁移全部章节 → 拒绝原因：断链与回归成本不可控

**Impact:** 前期会同时存在“书章节 + 模块章节”，但侧边栏只有书，读者路径更明确。

### ADR-002：允许章节改名/移动，但关键入口建议保留 redirect（折中）
**Context:** 用户接受断链，但站内导航与搜索命中仍需要可控的入口稳定性。

**Decision:** 迁移/合并时对关键入口保留少量 redirect；非关键页面允许断链与清理。

**Rationale:** 用很低成本换取显著的读者体验稳定性。

**Impact:** 会存在少量“redirect 页面”，但不会要求全量维护历史路径。

## 安全与性能

- **Security:** 不引入 secrets/token；不连接生产环境；脚本只做本地文件生成与校验。
- **Performance:** Book-only 导航会让目录更集中，MkDocs 构建时间可能增加：
  - 控制策略：索引页尽量由脚本生成；分阶段迁移、分批提交，避免一次性大改导致 CI 排障困难。

## 测试与发布

- **Deployment**
  - push 到 GitHub 后由 Pages workflow 构建并发布
  - 如失败，优先检查 Pages Source 配置与 Actions 权限配置

