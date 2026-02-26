# Technical Design: 全模块文档书本化升级（统一章节契约与导航）

## Technical Solution

### Core Technologies
- Python 3（用于批量处理 Markdown 文件）
- Markdown（文档结构规范）

### Implementation Key Points

1. **模块发现（Module Discovery）**
   - 以仓库根目录下 `*/docs/README.md` 为判断条件发现模块；
   - 章节清单以每个模块的 `docs/README.md` Markdown 链接为 SSOT。

2. **章节顺序（Chapter Ordering）**
   - 解析 `docs/README.md` 中指向 `.md` 的相对链接；
   - 去重并保持顺序，得到该模块的“可连续阅读顺序”。

3. **章节契约 upsert（Idempotent Upsert）**
   - 对每个章节文件（README 除外）执行：
     - 计算上一章/下一章路径（基于章节清单顺序）
     - 生成统一导航行（相对路径从当前章节所在目录出发）
     - 提取该章节文本中已有的可跑入口引用（优先类名：`*LabTest/*ExerciseTest/*ExerciseSolutionTest`；必要时兼容 `src/test/java/...` 真实路径）
     - upsert “对应 Lab/Test（可运行）”入口块（存在则更新列表；不存在则追加）
     - upsert 导航行（存在则替换为统一格式；不存在则追加）

4. **一致性与回滚策略**
   - 脚本以“可重复运行”为目标；重复执行不会造成入口块/导航行重复叠加；
   - 若出现单章异常，可手动回滚对应文件或重新运行脚本修复（以 README 为 SSOT，脚本可再生成）。

## Security and Performance

- **Security:** 仅操作本地仓库文件；不连接生产环境；不引入密钥/凭据；不写入本机缓存到仓库
- **Performance:** 脚本扫描范围约 200+ Markdown 文件，预计秒级完成

## Testing and Deployment

- **Verification (Docs):**
  - `python3 scripts/check-md-relative-links.py`
  - `python3 scripts/check-teaching-coverage.py --min-labs 2`
- **Optional Regression (Code):**
  - 由于本次主要为 docs 改动，回归可抽样跑关键模块 `mvn -pl <module> test`

## Notes

本次变更的“书本化”定义为**结构契约统一**（章节尾部入口块 + 导航），不强制对正文内容做重写与重排，避免引入知识点语义偏差。
