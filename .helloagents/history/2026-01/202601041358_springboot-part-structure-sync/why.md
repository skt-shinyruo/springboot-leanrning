# Change Proposal: springboot-part-structure-sync

## Requirement Background

当前仓库中 `spring-core-*` 模块已完成“书本式 Part 结构”的落地：docs 具备目录页与 Part 章节分组，源码与测试也按 Part/topic 分组，文档中的源码路径引用能够真实可点且与代码一致。

相比之下，多数 `springboot-*` 模块仍处于：
1. `docs/` 章节平铺，缺少统一的 Part 结构与目录页；
2. `src/main/java` 与 `src/test/java` 的示例/Lab/Exercise 多为平铺或弱分组，难以与 docs 的章节结构一一对应；
3. docs/README/根 README 中存在对源码路径的直接引用，一旦移动改包，链接容易断裂。

本变更旨在把已验证有效的“Part 结构”推广到全部 `springboot-*` 模块，使 docs 与代码组织方式对齐，阅读与复现路径一致，并保持 Spring Boot 的入口类（`*Application`）包名不变，避免破坏自动扫描与默认配置推断。

## Change Content
1. 为所有 `springboot-*` 模块建立最小“书本基础设施”：
   - `docs/README.md`：目录页（Start Here + Part 索引）
   - `docs/part-00-guide/`：导读（00）
   - `docs/part-01-<module-topic>/`：承载现有 `01-xx.md/02-xx.md/...` 章节（默认先集中到一个 Part，保证连贯性）
   - `docs/appendix/`：迁移 `90-common-pitfalls.md`（必要时补 `99-self-check.md` 的骨架）
2. 对齐源码分组（默认策略）：
   - 除 `springboot-business-case` 外，其它 `springboot-*` 模块：将“可复现实验入口（Labs/Exercises）相关代码”按 Part/topic 分包，避免全部平铺。
   - `springboot-business-case`：保留既有领域分层包结构（`app/api/domain/events/...`），仅对 docs 与 tests 做 Part 化分组。
3. 保持每个模块 `*Application` 入口类包名不变（硬约束）。
4. 在移动/改包后，同步修复 docs/README/根 README/跨模块引用中的所有源码路径引用，保证“可点且真实存在”。

## Impact Scope
- **Modules:**
  - `springboot-basics`
  - `springboot-web-mvc`
  - `springboot-data-jpa`
  - `springboot-actuator`
  - `springboot-testing`
  - `springboot-business-case`
  - `springboot-security`
  - `springboot-web-client`
  - `springboot-async-scheduling`
  - `springboot-cache`
- **Files:**
  - 各模块 `docs/**`（新增/移动/重命名）
  - 各模块 `README.md`（更新入口与链接）
  - 各模块 `src/main/java/**`（除入口类外的 demo/lab 代码可能改包）
  - 各模块 `src/test/java/**`（按 Part/topic 改包分组）
  - 根 `README.md`（跨模块入口与链接修复）
  - `helloagents/wiki/modules/springboot-*.md`（知识库同步）
  - `helloagents/CHANGELOG.md`、`helloagents/history/index.md`、`helloagents/history/YYYY-MM/...`（变更记录与归档）
- **APIs:** 无新增/变更对外 API（仅结构重排与文档对齐）
- **Data:** 无数据结构/数据库变更

## Core Scenarios

### Requirement: docs Part 结构推广
**Module:** springboot-*
将各模块既有章节从平铺 `docs/*.md` 迁移到 `docs/part-*` 与 `docs/appendix/`，并补齐 `docs/README.md` 目录页。

#### Scenario: 阅读路径一致
当读者从模块 `README.md` 进入 docs 时：
- 能从 `docs/README.md` 找到完整章节目录
- “上一章｜目录｜下一章”类的导航不会断链（至少目录入口可用）

### Requirement: 源码与 tests 分组对齐 docs
**Module:** springboot-*
按 Part/topic 对测试（Labs/Exercises）进行 package 分组，使 docs 的“最小可复现入口”能定位到具体测试类。

#### Scenario: 复现入口可定位
当读者在 docs 中点击/复制源码路径时：
- 路径真实存在
- 对应测试可运行（`mvn -pl <module> test`）

### Requirement: 保持入口类包名不变
**Module:** springboot-*
任何 `*Application`（例如 `BootWebMvcApplication`）保持原包名与路径不变。

#### Scenario: Spring Boot 默认扫描不受影响
即使 tests 被移动到子 package：
- `@SpringBootTest` 仍能通过包层级找到 `@SpringBootConfiguration`
- 应用上下文能正常启动

### Requirement: business-case 保持领域分层
**Module:** springboot-business-case
不将 `domain/api/app/events/...` 强行改为 Part 分组，避免损害领域表达与边界。

#### Scenario: 领域结构不被破坏
迁移完成后：
- `springboot-business-case/src/main/java/com/learning/springboot/bootbusinesscase/**` 下既有分层保持
- 仅 tests 与 docs 的入口/路径完成对齐

### Requirement: 全量修复引用
**Module:** root + springboot-*
修复根 README、模块 README、docs 内的源码路径/相对链接引用。

#### Scenario: 链接不 404
以 `rg` 搜索旧路径后逐一修复：
- 文档中的 `src/main/java/...` / `src/test/java/...` 引用不指向已不存在的位置

## Risk Assessment
- **Risk:** 大规模移动/改包导致编译失败或测试失效  
  **Mitigation:** 按模块分批执行与验证；保持入口类包名不变；每个模块迁移后立即 `mvn -pl <module> test`。
- **Risk:** docs/README/跨模块引用遗漏导致链接断裂  
  **Mitigation:** 在迁移后执行全局引用审计（`rg "springboot-.*?/docs/"`、`rg "src/(main|test)/java"`），逐一修复并再跑聚合测试。

