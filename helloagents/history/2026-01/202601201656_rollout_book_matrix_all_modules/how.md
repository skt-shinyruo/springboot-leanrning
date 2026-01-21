# Technical Design: 推广 Book Matrix（关键分支矩阵入口）到全模块

## Technical Solution

### Core Technologies

- Java 17 + JUnit 5（JUnit Platform）
- JUnit Platform Suite（`@Suite` + `@SelectClasses`）
- Maven Surefire（Spring Boot Parent 统一管理测试基线）

### Implementation Key Points

1. **用 Suite 聚合现有 Labs，避免重复实现**
   - 每个模块新增一个 `*BookMatrixLabTest`，仅负责聚合既有 `*LabTest`。
   - 优点：零业务/最小代码侵入；Matrix 入口稳定；内容仍由既有 Labs 承担。

2. **依赖放在根 `pom.xml`（scope=test）**
   - 增加 JUnit Platform Suite API/Engine 依赖，确保 `@Suite` 可被发现与执行。

3. **每模块只选 2–4 个 Labs**
   - 覆盖：主线（mainline）+ 关键分支/边界（pitfall/override/ordering/async 等）
   - 避免把模块所有 Labs 都塞进 Matrix，保证入口“短小但有信息量”。

## Architecture Decision ADR

### ADR-003: 用 JUnit Platform Suite 作为 Book Matrix 入口
**Context:** 需要给每个模块提供单命令入口，同时不希望为“聚合入口”重复编写大量测试逻辑。
**Decision:** 引入 `junit-platform-suite-api` + `junit-platform-suite-engine`，并为每个模块新增 `*BookMatrixLabTest`（Suite 聚合）。
**Rationale:** 复用既有 Labs，减少维护成本；入口稳定；与 Maven/Surefire/JUnit Platform 机制兼容。
**Alternatives:**
- 手写 MatrixLabTest（复制/重写多个分支场景） → 拒绝原因：重复高、维护成本大、容易与既有 Labs 漂移。
- 在 docs 里列一串命令（没有单入口） → 拒绝原因：体验不一致，Book 主线缺少统一进阶入口。
**Impact:** 增加两项 test scope 依赖；新增少量 Suite 类；Book 与知识库增加统一入口。

## Security and Performance

- **Security:** 无外部网络/密钥/生产环境操作；仅新增测试聚合与文档入口。
- **Performance:** Matrix 入口会运行 2–4 个 Labs；运行时间略增但可控。

## Testing and Deployment

- **Docs Gate:** `bash scripts/check-docs.sh`
- **Docs Site Build:** `bash scripts/docs-site-build.sh`
- **Repo Regression:** `mvn -q test`
- **Spot-check:** 对任意 2–3 个模块运行其 `*BookMatrixLabTest` 单测命令，验证 suite 发现与聚合正确。

