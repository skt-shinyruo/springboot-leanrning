# Technical Design: springboot-part-structure-sync

## Technical Solution

### Core Technologies
- Java 17
- Spring Boot（仓库现有版本）
- Maven multi-module（根 `pom.xml`）

### Implementation Key Points

#### 1) docs：统一最小书本结构（默认）
对每个 `springboot-*` 模块，形成统一目录结构：
- `docs/README.md`：目录页（Start Here / 章节索引 / Labs&Exercises 入口索引）
- `docs/part-00-guide/00-deep-dive-guide.md`：导读（快速建立“如何读 + 如何跑实验”的路径）
- `docs/part-01-<module-topic>/`：承载既有编号章节（如 `01-*.md`、`02-*.md`）
- `docs/appendix/`：迁移 `90-common-pitfalls.md`（必要时补 `99-self-check.md` 骨架）

对已有 docs 的模块：执行移动/重命名并同步修复链接。
对缺少 docs 的模块（如 `springboot-actuator`、`springboot-testing`、`springboot-business-case`）：新增 docs 目录与最小骨架，后续可逐步补充正文。

#### 2) src/test：按 Part/topic 分包（对齐 docs 的“可复现入口”）
目标：让 tests 结构不再平铺，并与 docs Part 结构建立映射关系。

命名规则（与 `spring-core-*` 对齐）：
- `part00_guide`：Exercises/入口清单类测试
- `part01_<module_topic>`：该模块核心主题的 Labs（例如 web-mvc 的 exception/binding；security 的 filter chain；cache 的 key/unless）
- `appendix`：坑点/反例/不属于主线但必须掌握的实验

迁移策略：
- 移动测试类文件位置 + 更新 `package` 声明
- 修复 `import`、`@SpringBootTest(classes=...)`（如存在显式指定）
- 同步修复 docs 中引用的测试路径

#### 3) src/main：在不破坏入口包名的前提下做“最小必要分组”
硬约束：每个模块的 `*Application` 入口类包名与路径保持不变。

默认策略：
- 对“纯示例/Runner/Service（用于 Labs）”类，迁移到入口包名的子包 `part01_<module_topic>` 下；
- 对 already 分层良好的模块不强行重排。

特殊策略（`springboot-business-case`）：
- 保持领域分层：`app/api/domain/events/...` 不动；
- 仅对 tests 与 docs 执行 Part 化，以避免“领域模型被 Part 目录结构覆盖”。

#### 4) 引用修复与一致性审计
迁移完成后，执行两类审计：
1) 路径引用审计：全局搜索 `src/main/java`、`src/test/java`、以及 `springboot-*/docs/` 的旧路径引用并修复；
2) docs 入口审计：每个模块 `README.md` → `docs/README.md` → Part 章节路径可达。

#### 5) 分批验证与回滚策略
- 每个模块迁移后立即执行：`mvn -pl <module> test`
- 全部完成后执行聚合回归：`mvn -pl springboot-basics,springboot-web-mvc,springboot-data-jpa,springboot-actuator,springboot-testing,springboot-business-case,springboot-security,springboot-web-client,springboot-async-scheduling,springboot-cache test`
- 回滚策略：以 Git commit 粒度拆分（按模块提交），若某模块迁移引入问题，可单独回退该模块的 commit，不影响其它模块。

## Architecture Design
无架构级变更（结构化重排与文档对齐），不新增运行时组件。

## Architecture Decision ADR

### ADR-001: springboot-business-case 保留领域分层，仅 Part 化 docs/tests
**Context:** business-case 具备清晰的领域分层包结构，强行 Part 化会降低领域表达与边界可读性。  
**Decision:** 保持 `app/api/domain/events/...` 不动；仅对 docs 与 tests 做 Part 化，仍保持 `*Application` 入口包名不变。  
**Rationale:** 兼顾“书本式 docs 与可复现实验入口”与“领域模型表达”的两类可读性目标。  
**Alternatives:** 全量 Part 化 src/main → Rejection reason: 破坏领域结构、收益不成比例。  
**Impact:** 需要在 docs/README 中明确 business-case 的目录逻辑与实验入口定位方式。

## Security and Performance
- **Security:** 本变更为结构重排，不引入外部服务调用；执行中避免误提交敏感信息（token/密钥）；不修改权限/认证逻辑语义。
- **Performance:** 运行时性能不受影响；仅影响源码组织与可读性。

## Testing and Deployment
- **Testing:** 分模块 test + 全量聚合回归（见 Implementation Key Points）。
- **Deployment:** 无需部署；仅代码与文档变更。

