# Technical Design: Spring Core 系列模块教学化规范全量推广

## Technical Solution

### Core Technologies
- Markdown（`docs/**/*.md`）
- Python（`scripts/*.py` 自检）
- Maven + JUnit（`src/test/java/**/*Test.java`）
- Spring 测试工具：`ApplicationContextRunner`（优先用于轻量、可断言的 Labs）

### Implementation Key Points

1. **以目录页为 SSOT（章节清单）**
   - 以每个模块 `docs/README.md` 中列出的章节链接作为“章节集合”
   - 校验目录页列出的章节文件必须存在

2. **链接规范化：真实链接替代缩写**
   - 替换 `docs/07` 等缩写引用为真实 Markdown 链接
   - 统一跨 Part/跨模块引用的相对路径写法，使其在仓库中可解析

3. **章节 ↔ 实验入口闭环**
   - 目标：每个章节至少提供一个可跑入口（优先 `*LabTest.java`）
   - 策略：
     - 若该章节已有对应 Lab：补齐“对应 Lab/Test”区块（并确保路径真实存在）
     - 若缺失 Lab：新增最小 LabTest（尽量轻量、断言明确、可复现）

4. **自检脚本升级**
   - `scripts/check-md-relative-links.py`：
     - 支持一次性覆盖全部 `spring-core-*` 模块 docs（作为默认或提供显式参数）
   - 新增 `scripts/check-teaching-coverage.py`：
     - 扫描 `spring-core-*/docs/README.md` → 获取章节集合
     - 对每个章节，校验至少出现一个指向 `src/test/java/**/**LabTest.java` 的路径引用（或被允许的 Exercise/Solution 入口）
     - 校验引用的文件路径真实存在
     - 校验每个模块 `*LabTest.java` 数量 ≥ N（本次默认 N=2）

## Architecture Design

本次变更不引入新的运行时模块或服务，仅对“文档与测试的组织方式”做一致性规范化；因此不涉及新增运行时架构图。

## Architecture Decision ADR

### ADR-1: 以“章节内对照块”为章节↔实验映射的 SSOT
**Context:** 需要让“读完章节后跑哪个实验验证”具备稳定入口，并可被脚本校验。  
**Decision:** 每个章节在正文中提供“对应 Lab/Test（或 Exercise/Solution）”区块；脚本以此作为 SSOT 做一致性校验。  
**Rationale:** 对读者最友好（阅读即获得可运行入口），且不需要维护额外的映射文件来避免漂移。  
**Alternatives:**
- 方案 A：在 `docs/README.md` 维护章节↔Lab 表格 → 拒绝原因：目录页易膨胀，且难以承载章节内的上下文说明
- 方案 B：依赖命名约定（如 `NN-*.md` ↔ `*NN*LabTest.java`）→ 拒绝原因：约定脆弱且不适配复杂主题
**Impact:** 文档会多一个标准区块；需要脚本与模板一起约束，避免漏补。

## Security and Performance
- **Security:** 不涉及生产环境、外部服务、密钥；脚本仅做文件扫描与存在性检查
- **Performance:** 自检脚本应保持线性扫描与快速失败；每个模块单独输出失败条目，便于定位

## Testing and Deployment
  - `python3 scripts/check-md-relative-links.py`（覆盖全部 `spring-core-*` docs）
  - `python3 scripts/check-teaching-coverage.py --min-labs 2`（新增）
- 回归测试（按模块逐个验证）：
  - `mvn -pl spring-core-aop test`
  - `mvn -pl spring-core-aop-weaving test`
  - `mvn -pl spring-core-events test`
  - `mvn -pl spring-core-profiles test`
  - `mvn -pl spring-core-resources test`
  - `mvn -pl spring-core-tx test`
  - `mvn -pl spring-core-validation test`
