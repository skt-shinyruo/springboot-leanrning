# Technical Design: tutorials 风格对齐改造（全模块深挖标准化）

## Technical Solution

### Core Technologies
- Java 17
- Maven 多模块工程（聚合 + 分组父 POM + 子模块继承）
- Spring Boot 3.5.x（保持不升级/不降级）
- JUnit 5 + AssertJ（tests-first 教材化）
- MkDocs（`docs-site/`，用于可导航站点）

### Implementation Key Points

1. **命名与映射表先行**
   - 先定义 `springboot-* → spring-boot-*` 的映射（目录名 + artifactId）
   - 统一命令与文档引用均以 `artifactId` 为准（`mvn -q -pl :<artifactId> test`）
2. **POM 分层（workspace → group → module）**
   - 根 `pom.xml`：统一 Spring Boot 版本、通用测试依赖、编码与 Enforcer
   - 分组 `pom.xml`：`spring-boot-modules` / `spring-core-modules` 作为父 POM（packaging=pom）
   - 子模块 `pom.xml`：parent 指向分组 POM，并修正 `relativePath`
3. **全仓库引用统一更新**
   - `scripts/**`：`-pl :<artifactId>`、`spring-boot:run` 等命令保持可用
   - `docs/**`、模块 `README.md`、`helloagents/wiki/**`：更新 artifactId 与路径引用
4. **全模块深挖契约的“最小可落地标准”**
   - 以“章节契约（Chapter Contract）”为规范：call-chain、Debugger Pack、Pitfalls、Self-check、对应 Lab/Test
   - 以“Matrix 测试入口”为规范：Book Matrix（覆盖核心主线）+ Branch Matrix（覆盖关键分支/边界）
   - 并发/性能：每模块至少 1 个稳定可复现实验（线程池饱和、缓存击穿、异步边界、SpEL 并发等）

## Architecture Design

```mermaid
flowchart TD
  ROOT[workspace: root pom.xml] --> BOOT[spring-boot-modules (group parent)]
  ROOT --> CORE[spring-core-modules (group parent)]
  BOOT --> M1[spring-boot-* modules]
  CORE --> M2[spring-core-* modules]
```

## Architecture Decision ADR

### ADR-001: 采用 `spring-boot-*` 命名 + 分组父 POM 分层
**Context:** 需要对齐 `tutorials` 的“模块集合目录 + 统一命名 + 分组组织”，同时保证改造可控、回归稳定。  
**Decision:**  
1) Spring Boot 模块统一命名为 `spring-boot-*`（目录 + artifactId）  
2) 子模块继承分组 POM（`spring-boot-modules` / `spring-core-modules`）而非直接继承根 POM  
**Rationale:**  
- 与 `tutorials` 风格更一致（命名可预期、模块集合目录更清晰）  
- 依赖与组织更容易按领域扩展（Boot/Core 分组）  
- 改动可分阶段推进（先 Maven 结构，再 docs/tests 深挖标准化）  
**Alternatives:**  
- 保持 `springboot-*` 命名 → 拒绝原因：与 `tutorials` 差异仍明显，命名不统一  
- 引入 `parent-boot-*` / `parent-spring-*` 多级父工程 → 拒绝原因：当前模块规模不需要，改造与维护成本更高  
**Impact:**  
- 需要批量更新 artifactId 引用（docs/scripts/wiki）  

## Security and Performance
- **Security:** 不接入生产环境、不引入明文密钥；所有示例配置保持本地可复现与最小权限。  
- **Performance:** 并发/性能实验只验证“机制边界与分支”，避免基于耗时阈值的 flaky 断言；优先可控时钟、latch、失败路径计数。  

## Testing and Deployment
- **Testing:** 每一阶段改造完成后执行 `mvn -q test`；必要时按模块 `-pl :<artifactId>` 快速回归。  
- **Docs Site:** 运行 `docs-site` 构建检查断链与导航一致性。  

