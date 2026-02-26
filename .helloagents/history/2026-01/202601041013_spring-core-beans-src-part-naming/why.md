# Change Proposal: spring-core-beans 源码分组命名语义化（去掉 part01/part02）

## Requirement Background
目前 `spring-core-beans` 已将 `src/main` 与 `src/test` 按 docs 的 Part 分组（解决了“平铺难以定位”的问题），但分组目录使用了 `part00/part01/part02/...` 这类“序号式”命名，仍存在两点不足：

1. **可读性不够**：仅看目录名无法立即知道该分组对应 docs 的哪个 Part（需要读者额外记忆映射关系）。
2. **书本一致性不足**：docs 使用的是“具名 Part”（例如 `part-01-ioc-container`），源码使用“序号 Part”，视觉与语义不一致。

本次目标是在不改变既有内容与可复现实验的前提下，把源码分组目录改为“序号 + 语义”的命名，使其更像书本目录，减少认知转换成本。

## Change Content
1. 将 `src/main/java` 与 `src/test/java` 下的分组目录由 `part00/part01/...` 改为 `part00_guide/part01_ioc_container/...` 等具名形式。
2. 同步修改 Java package 声明与引用 import，保证编译与测试均正常。
3. 同步修复 `docs/beans/spring-core-beans/**`、`spring-core-beans/README.md`、`helloagents/wiki/modules/spring-core-beans.md` 中所有源码路径引用。
4. **约束保持不变**：`com.learning.springboot.springcorebeans.SpringCoreBeansApplication` 包名与路径保持不变。

## Impact Scope
- **Modules:** `spring-core-beans`
- **Files:** `src/main/java/**`, `src/test/java/**`, `docs/beans/spring-core-beans/**`, `spring-core-beans/README.md`, `helloagents/wiki/modules/spring-core-beans.md`
- **APIs:** 无（工程结构调整）
- **Data:** 无

## Core Scenarios

### Requirement: 目录命名语义化（与 docs Part 一致）
**Module:** spring-core-beans
源码目录应能“看名知意”，并能稳定对应到 docs 的 Part。

#### Scenario: Rename `part01` to `part01_ioc_container` etc.
把 `part01/part02/...` 改为 `part01_ioc_container/part02_boot_autoconfig/...` 等，提升可读性。
- 期望结果：目录结构更直观；读者看到目录名即可知道对应章节域。

### Requirement: Docs 路径引用一致性
**Module:** spring-core-beans
文档中的源码路径引用必须与真实路径一致。

#### Scenario: Update All Docs Source Paths
完成目录重命名/改包后，docs/README/wiki/模块 README 中的路径不应指向旧目录。
- 期望结果：所有引用路径均可定位到真实文件位置。

### Requirement: Keep Application Package Stable
**Module:** spring-core-beans
必须保留 `com.learning.springboot.springcorebeans.SpringCoreBeansApplication` 包名不变。

#### Scenario: Preserve Boot Configuration Lookup
测试类迁移到 `com.learning.springboot.springcorebeans.*` 子包后，Spring Boot 仍能向上找到 `@SpringBootConfiguration`。
- 期望结果：测试可正常启动，不需要新增第二启动类。

## Risk Assessment
- **Risk:** 大量文件改包/移动，容易遗漏 import 或 docs 引用。
  - **Mitigation:** 批量替换后全局搜索残留（旧包名/旧路径前缀），并运行 `mvn -pl spring-core-beans test` 验证。
