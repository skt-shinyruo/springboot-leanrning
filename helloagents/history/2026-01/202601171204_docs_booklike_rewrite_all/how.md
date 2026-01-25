# Technical Design: 全站文档深度书籍化改写（全部章节）

## Technical Solution

### Core Technologies

- Markdown（现有文档载体）
- Python 3（批处理脚本）
- MkDocs（站点构建与 `--strict` 校验）

### Implementation Key Points

1. **章节语义 SSOT：章节学习卡片（五问闭环）**
   - 章节卡片里的 5 个字段被视为“章节目标/入口/证据链”的事实来源。
   - 批处理只做“结构收束与表达补齐”，尽量不凭空添加机制细节，避免技术不准确。

2. **章节类型分流（避免一刀切）**
   - 普通章节：需要补齐导读、主线推进、证据链提示、收束承接，清理冗余与空块。
   - 工具页/索引页：允许 `源码入口：N/A`、`推荐 Lab：N/A`，正文侧重“怎么用这页”。
   - redirect 页：保持最小文本（说明 + 新位置链接），不做过度扩写，避免噪音。

3. **幂等改写（可重复执行）**
   - 使用 marker/heading 检测：存在则更新，不存在则插入。
   - 保留关键 marker：`<!-- CHAPTER-CARD:* -->`、`<!-- GLOBAL-BOOK-NAV:* -->`、`<!-- BOOKIFY:* -->`。

4. **去冗余与补空块**
   - 合并重复的实验入口（example callout / “最小可运行实验”），保留一个清晰的“先跑再读”入口。
   - 自动填充空的“一句话总结/小结与下一章”片段：使用卡片字段生成“可复述的一句话”，并明确承接下一章（不猜测不存在的章节关系，优先复用已有导航）。

5. **章节清单与执行范围**
   - 模块章节：以各模块 `docs/README.md` 的 Markdown 链接清单为 SSOT。
   - Book：`docs/book/**/*.md` 全量纳入（含工具页与 redirect 页）。

---

## Architecture Decision ADR

### ADR-001: 以“章节学习卡片”作为批处理改写的事实来源

**Context:** 全量章节改写规模大，若直接“自由生成”正文，很容易引入不一致或技术错误。  
**Decision:** 以卡片字段作为章节改写的唯一事实来源，批处理只做表达与结构整理。  
**Alternatives:** “逐章自由重写全部正文” → 拒绝原因：成本与风险过高，且难以保证一致性与准确性。  
**Impact:** 章节会获得更稳定的书籍化结构；对少量关键章节仍可人工精修提升深度。

---

## Security and Performance

- **Security:** 仅文档与脚本改造；不引入生产环境操作、不写入密钥/PII；脚本只做文件内容重写与链接校验。
- **Performance:** 批处理为离线执行（CI/本地）；对运行性能无影响。脚本应避免全仓多次重复扫描，按 SSOT 清单处理。

---

## Testing and Deployment

- **Deployment**
  - 文档站点保持现有 workflow，不更改发布流程；以 `mkdocs --strict` 作为验收基线。

