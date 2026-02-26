# Technical Design: 全模块文档书籍化重写（Stage 2：主线时间线重排）

## Technical Solution

### Core Technologies

- Markdown（章节正文与目录）
- MkDocs + Material（聚合站点、搜索、侧边栏目录）
- 仓库内脚本：
  - `scripts/docs-site-sync.py`（聚合与目录自动生成）

### Implementation Key Points

1. **每模块新增“主线时间线章节”**
   - 位置建议：`<module>/docs/part-00-guide/03-mainline-timeline.md`
   - 写作目标：用 1 章讲清“这一模块按时间线怎么发生”，并把关键分支映射到现有章节与 Lab/Test。

2. **重排每模块 `docs/README.md` 为“书籍目录”**
   - 第一屏出现：从哪开始读（主线时间线/快速闭环入口）
   - 目录分区：
     - 顺读主线（按时间线）
     - 深挖章节（按 Part/机制域）
     - 排障速查（按症状）

3. **合并/拆章与叙事重写（分批推进）**
   - 优先模块：主线 5 模块（Basics/Beans/AOP/Tx/Web MVC）

4. **链接稳定策略（避免破坏性重排）**
   - 尽量不移动文件路径；必要的合并/拆章用“redirect 页面”保留旧入口
   - redirect 页面形态：保留标题 + 简短说明 + 指向新章节链接

5. **提示框/侧栏使用规范（让“机制/源码/实验/排障”成为插入段）**
   - `!!! summary`：本章要点（章首）
   - `!!! example`：本章配套实验（章首，先跑再读）
   - `!!! note`：源码锚点（关键类/关键方法/关键分支）
   - `!!! info`：Debugger Pack（entrypoints/watchpoints/decisive branch）
   - `!!! warning`：坑点与边界（误区 + 验证/规避）

## Architecture Decision ADR

### ADR-001: 主线时间线先行，重排分批推进（推荐）
**Context:** 全模块同时做合并/拆章/重排风险极高，且读者最需要的是“主线顺读入口”。

**Decision:** 先为每个模块增加主线时间线章节与书籍目录，再按模块分批做合并/拆章与叙事润色。

**Rationale:** 先解决“读者从哪开始读/怎么顺读”的最大痛点；同时把高风险改动拆解为可回滚的小批次。

**Alternatives:**
- 方案 A：一次性重排/改名/移动全部章节 → 拒绝原因：断链与维护风险不可控
- 方案 B：只做站点目录，不改文档正文 → 拒绝原因：无法满足“像书一样顺读”的核心诉求

**Impact:** 需要新增时间线章节与目录重排工作量；合并/拆章的进度会分批推进但整体风险更低。

## Security and Performance

- **Security:** 不涉及生产环境、外部密钥、用户数据；避免在文档/脚本中引入 token/secret；CI 使用 GitHub Actions 的默认权限模型。
- **Performance:** 目录更详细后 MkDocs 构建会变慢，控制策略：
  - 保持 `docs-site-sync.py` 的生成过程可重复且可增量扩展
  - 分批提交，避免一次性引入超大改动导致 CI 排查困难

## Testing and Deployment

- **Deployment**
  - push 到 GitHub 触发 Pages workflow（构建 + 发布）
  - 若 Pages deploy 失败，优先检查：Pages 是否启用、Actions workflow 权限是否为 read/write

