# Change Proposal: spring-core-beans 源码按 docs Part 分组

## Requirement Background
当前 `spring-core-beans` 模块的文档已经按 Part（书本结构）进行了分组，但 `src/main/java` 与 `src/test/java` 仍是“平铺式”组织方式：类与测试散落在同一个 package 下，导致：

1. 读文档时难以快速定位到对应的实验/源码位置（路径不稳定、认知跳跃大）。
2. Labs / Exercises 缺少“章节级/Part 级”的结构化索引（可发现性差）。
3. 代码组织与知识结构不一致，后续继续补充源码解析或新增实验时，容易越写越乱。

目标是让“文档 Part 结构”与“源码/测试 Part 结构”对齐，从而让阅读体验更像一本书：按 Part 逐步深入，代码与实验跟随章节推进。

## Change Content
1. 将 `spring-core-beans/src/main/java`（除 `com.learning.springboot.springcorebeans.SpringCoreBeansApplication` 外）按 docs Part 结构拆分到子包。
2. 将 `spring-core-beans/src/test/java` 按 docs Part 结构拆分到子包（Labs/Exercises 归类到对应 Part）。
3. 引入 `testsupport`（测试支撑包）承载跨 Part 复用的测试工具/marker，避免 package-private 可见性问题。
4. 在移动/改包后，全量修复 `docs/beans/spring-core-beans/**` 中对源码路径的引用（含 `src/main/java` 与 `src/test/java`）。

## Impact Scope
- **Modules:** `spring-core-beans`
- **Files:** `src/main/java/**`, `src/test/java/**`, `docs/beans/spring-core-beans/**`（仅路径引用修复，不减少知识点）
- **APIs:** 无对外 API 变更（仅内部 demo/labs 包结构调整）
- **Data:** 无

## Core Scenarios

### Requirement: Part-Based Source Grouping
**Module:** spring-core-beans
源码与测试应与 docs 的 Part 分组保持一致，读者可以“按 Part”理解与验证机制。

#### Scenario: Group `src/main` Classes by Part
在不改变 `SpringCoreBeansApplication` 包名的前提下，将 demo/业务示例类迁移到 `part-xx` 对应的子包。
- 期望结果：`SpringCoreBeansApplication` 仍在 `com.learning.springboot.springcorebeans`；其他 main 类按 Part 归类，命名/语义更清晰。

#### Scenario: Group `src/test` Labs/Exercises by Part
将现有 49 个测试/实验类按 Part 分组，避免“测试目录平铺导致不可发现”的问题。
- 期望结果：阅读某个 Part 的文档时，可以在对应 Part 的测试包中找到“可复现实验入口”。

### Requirement: Docs Source Path Consistency
**Module:** spring-core-beans
文档中的源码路径引用必须与改包后的真实路径一致，避免“文档能读但链接失效”。

#### Scenario: Fix Docs References After Refactor
移动/改包后，修复 docs 内所有对 `src/main/java` 与 `src/test/java` 的路径引用（链接/文本路径）。
- 期望结果：docs 中的路径引用可直接定位到正确文件；不会因为改包而留下过期路径。

### Requirement: Keep Application Package Stable
**Module:** spring-core-beans
必须保留 `com.learning.springboot.springcorebeans.SpringCoreBeansApplication` 的包名不变。

#### Scenario: Preserve Boot Configuration Lookup
测试类迁移到 `com.learning.springboot.springcorebeans.*` 的子包后，Spring Boot 仍能向上找到 `@SpringBootConfiguration`。
- 期望结果：测试可正常启动/定位配置；不引入额外的启动类或扫描配置成本。

## Risk Assessment
- **Risk:** 测试目录中存在 package-private 的工具类/marker（同包可见）；拆分到子包后可能导致编译失败。
  - **Mitigation:** 将跨 Part 复用的工具/marker 迁移到 `com.learning.springboot.springcorebeans.testsupport`，并将必要类型调整为 `public`；统一修复引用并运行 `mvn -pl spring-core-beans test` 验证。
- **Risk:** docs 中存在大量写死路径引用，迁移后容易遗漏。
  - **Mitigation:** 对旧路径进行全局搜索替换 + 逐章抽样校验；以“类名 → 新路径”的映射为准进行修复。
