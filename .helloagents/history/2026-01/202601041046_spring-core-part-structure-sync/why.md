# Change Proposal: 同步 spring-core-* 模块的 Part 化结构（对齐 spring-core-beans）

## Requirement Background
目前 `spring-core-beans` 已完成“像书本一样”的结构化整理：  
- `docs/` 按 Part（`part-00-guide` / `part-01-*` / `appendix`）组织  
- `src/main` 与 `src/test` 按 Part（`part01_*` / `appendix` / `testsupport`）组织  
- 文档中的源码路径引用与真实路径保持一致

但从仓库全局看，其他 `spring-core-*` 模块仍处于：
- `docs/` 平铺（或甚至缺失 `docs/`）
- `src/main` 与 `src/test` 代码平铺在根包下

这会导致跨模块学习体验不一致：读者在不同模块间切换时，需要重新适应目录结构与“文档 ↔ 代码 ↔ 实验”对应关系。

## Change Content
1. 将所有 `spring-core-*` 模块对齐为与 `spring-core-beans` 一致的“书本化”结构：
   - `docs/part-00-guide/00-deep-dive-guide.md`
   - `docs/part-01-*/...`
   - `docs/appendix/90-common-pitfalls.md`、`docs/appendix/99-self-check.md`
2. 将 `src/main/java` 与 `src/test/java` 按 docs 的 Part 结构进行分组：
   - Java package 采用 `part01_<topic>`、`part02_<topic>`（下划线，避免 `-`）
   - `appendix`、`testsupport` 用于跨 Part 的附录与复用工具
3. **保持各模块 `*Application.java` 的包名与路径不变**（避免 Spring Boot 扫描/启动入口变化）
4. 在移动/改包后，同步修复 docs 中所有源码路径/包名引用（硬性要求：文档必须与真实路径一致）
5. 逐模块运行测试，保证重构后仍可复现与验证

## Impact Scope
- **Modules:**
  - `spring-core-aop`
  - `spring-core-events`
  - `spring-core-profiles`
  - `spring-core-resources`
  - `spring-core-tx`
  - `spring-core-validation`
- **Files:** 多文件移动/重命名/改包（以每个模块的 docs + src(main/test) 为主）
- **APIs:** 无（不新增对外接口）
- **Data:** 无（不涉及数据结构变更）

## Core Scenarios

### Requirement: 统一 spring-core-* 的 docs “Part 目录骨架”
**Module:** spring-core-*
将各模块 docs 对齐为与 `spring-core-beans` 一致的 Part/Appendix 结构。

#### Scenario: 阅读体验一致且可发现
- 文档以 Part 组织，章节之间承接清晰
- 附录（常见坑/自检）稳定落在 `docs/appendix/`
- 文档内部链接不失效

### Requirement: src(main/test) 按 Part 语义化分组
**Module:** spring-core-*
将 demo/Labs/Exercises 的源码按 Part 结构分组，减少“平铺 + 难定位”。

#### Scenario: 代码定位与文档章节对应
- 文档中提到的实验类/示例类能在对应 Part package 下快速找到
- `testsupport` 作为可复用工具，不散落在各 Part 包中

### Requirement: 保持 Application 入口稳定 + 文档引用真实有效
**Module:** spring-core-*
不改变 `*Application.java` 的包名与路径；移动后修复所有 doc 引用；测试通过。

#### Scenario: 重构后仍可运行与可复现
- `mvn -pl <module> test` 可通过（每个模块）
- docs 引用的源码路径/包名与真实文件一致

## Risk Assessment
- **Risk:** 大规模移动/改包导致 import 断裂、文档路径引用失效、测试失败  
  **Mitigation:** 逐模块迁移→逐模块修复引用→逐模块 `mvn -pl <module> test` 回归；使用 `rg` 全局检索确认无残留旧路径
- **Risk:** 某些示例依赖默认包可见性，分包后出现访问失败  
  **Mitigation:** 将跨包复用工具收敛到 `testsupport`，必要时调整可见性为 `public`

