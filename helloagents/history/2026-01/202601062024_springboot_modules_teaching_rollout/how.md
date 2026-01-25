# Technical Design: Spring Boot 模块教学化规范推广（对齐 spring-core 标准）

## Technical Solution

### Core Technologies

- 文档结构：Markdown（以 `docs/README.md` 为章节清单 SSOT）
- 自检脚本：Python（`scripts/check-md-relative-links.py`、`scripts/check-teaching-coverage.py`）
- 可运行入口：JUnit 5 + Spring Boot Test（`src/test/java/**/*LabTest.java` / `*ExerciseTest.java`）

### Implementation Key Points

1. **脚本覆盖范围扩展（从 spring-core → 全模块）**
   - `scripts/check-md-relative-links.py`：默认扫描从“仅 `spring-core-*/docs`”扩展为“`spring-core-*/docs` + `springboot-*/docs`”。
   - `scripts/check-teaching-coverage.py`：从“仅 spring-core”扩展为支持 springboot（建议：扫描所有包含 `docs/README.md` 的模块作为候选集合）。

2. **统一 docs/README.md（章节清单 SSOT）**
   - 将 `springboot-*/docs/README.md` 中的“反引号路径”改为 Markdown 链接（`[title](relative/path.md)`）。
   - 保持 Part 分组与编号稳定（`NN-*.md`），确保脚本可解析并可持续维护。

3. **统一“章节↔可跑入口”闭环**
   - 对 `springboot-*/docs/**/*.md`（含 `part-00-guide` 与 `appendix`）补齐/规范化 `### 对应 Lab/Test（可运行）` 区块。
   - 每章至少 1 个入口，且必须能解析到仓库内真实路径或类名（避免 `...`/`…` 省略号）。

4. **补齐最小 Labs（min-labs=2）**
   - 对当前仅 1 个 Lab 的模块，新增 1 个 `*LabTest.java`（保持现有 `partXX_*` 包结构，不引入外部服务依赖）。
   - 建议新增 Lab 的定位：补齐“当前缺入口的章节”或覆盖“主线关键边界机制”，并在对应章节引用该入口。

## Architecture Decision ADR

### ADR-1: `docs/README.md` 作为章节清单 SSOT（覆盖 spring-core 与 springboot）

**Context：** 教学化验收需要一个稳定、可维护、可审计的“章节清单来源”。若不以 README 为 SSOT，则脚本只能通过扫描目录/文件名推断章节，易产生“新增文件未入目录页”或“顺序不稳定”的漂移。

**Decision：** 统一以每个模块 `docs/README.md` 的 Markdown 链接清单作为“章节清单 SSOT”，脚本仅以此清单为准做后续检查（章节存在性 + 可跑入口存在性）。

**Rationale：**
- 目录页与正文引用同源，能强制维护者在新增/移动章节时同步更新目录；
- 通过断链检查与覆盖检查可做到“变更即验收”，降低知识库漂移风险；
- 与 `spring-core-*` 已采用的模式一致，减少双轨维护成本。

**Alternatives：**
- 方案 A：目录扫描推断章节 → Rejection reason：无法保证“目录页覆盖全部章节”的约束，且排序/分组不可控。
- 方案 B：解析反引号路径（`docs/...`） → Rejection reason：README 可读但不可机读，易漏、且无法被断链脚本覆盖。

**Impact：** `springboot-*/docs/README.md` 需要转换为“Markdown 链接清单”，并为导读/附录等非章节主体同样提供入口块。

## Security and Performance

- **Security：**
  - 不连接生产环境、不引入任何明文密钥/Token；
  - 新增测试只使用内存资源（如 H2、MockMvc、ApplicationContextRunner），不依赖外部 DB/消息队列/第三方网络服务；
  - 若遇到 Maven 依赖下载异常（如 403），仅允许使用标准化的本机缓存修复方式（清理 `.lastUpdated` + `mvn -U`），禁止将任何本机缓存产物提交到仓库。
- **Performance：**
  - 自检脚本应保持 O(文件数) 的扫描复杂度；
  - 通过 `--module` 支持模块级自检，避免全量跑导致迭代慢。

## Testing and Deployment

- **Testing：**
  - 模块级：每完成一个模块执行
    - `python3 scripts/check-md-relative-links.py <module>/docs`
    - `python3 scripts/check-teaching-coverage.py --min-labs 2 --module <module>`
    - `mvn -pl <module> test`
- **Deployment：** 无（教学工程，无部署动作）
