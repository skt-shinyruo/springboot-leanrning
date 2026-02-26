# Technical Design: 对齐 tutorials 工程结构（分组聚合 + 目录重排）+ Web MVC 深度示范

## Technical Solution

本轮采用“**先搭骨架、再批量迁移、最后补齐深度示范与入口稳定**”的策略：

1. **结构对齐 tutorials：** 引入分组聚合层（Boot/Core），让 Maven Reactor 形成层级组织。
2. **入口去耦合：** 命令优先按 `:artifactId` 定位模块，降低目录变化带来的维护成本。
3. **Web MVC 先行示范：** 在结构迁移过程中同步强化 Web MVC 的“矩阵/证据链/排障入口”，形成可复制模板。

### Core Technologies

- Java 17
- Maven 多模块（Reactor）
- Spring Boot 3.5.9（依赖与版本由父 pom 管理）
- JUnit 5 / AssertJ
- Spring Test / MockMvc
- MkDocs（Material 主题，`docs-site/`，源文档 `docs/`）

### Implementation Key Points

1. **引入分组聚合 pom（层级组织）**
   - 新增：
     - `spring-boot-modules/pom.xml`
     - `spring-core-modules/spring-core-modules/pom.xml`
   - 根 `pom.xml` 的 `<modules>` 收敛为上述两个分组模块。
   - 分组 pom 初期可先用相对路径聚合“仍在旧位置”的模块，保证迁移过程中构建可用；随后逐模块完成物理移动并更新 modules 列表。

2. **子模块父子关系与可独立构建**
   - 目标结构（最终态）：
     - Root `springboot-learning`（packaging=pom）
       - `spring-boot-modules`（packaging=pom）
         - `springboot-web-mvc`、`springboot-basics`、…
       - `spring-core-modules`（packaging=pom）
         - `spring-core-beans`、`spring-core-aop`、…
   - 原则：
     - 子模块 `artifactId` 保持不变（兼容历史命令与认知）
     - 子模块可在模块目录内直接执行 `mvn test`（父 pom 通过相对路径可被解析）

3. **命令入口稳定化（减少路径耦合）**
   - 文档/脚本统一偏向使用：
     - `mvn -pl :springboot-web-mvc test`
     - `mvn -pl :springboot-web-mvc spring-boot:run`
   - 说明：
     - `:artifactId` 方式适合从仓库根执行（对目录迁移天然稳定）
     - 模块目录内执行仍保留 `mvn test` 的“本地闭环”体验

4. **文档与引用迁移策略**
   - `docs/` 作为 SSOT 不移动（站点源目录保持稳定）
   - 需要迁移的引用：
     - 文档中指向源码/测试的路径：从旧模块路径更新到新分组路径
     - 模块根目录 `README.md` 内部的相对链接：随模块物理位置变化更新
   - 执行方式：
     - 通过脚本批量替换常见前缀（例如 `spring-boot-modules/springboot-web-mvc/src/...` → `spring-boot-modules/springboot-web-mvc/src/...`）

5. **Web MVC 第一批深度示范（可复制模板）**
   - 新增/强化重点：
     - 内容协商（406/415）与错误落点的“矩阵化证据链”
     - 对应文档：补齐“关键分支 → 证据链 → 断点/观察点 → 常见坑”
     - 对应测试：新增矩阵 LabTest（或在现有 LabTest 基础上扩展）

## Architecture Decision ADR

### ADR-1: 采用“分组聚合层”而不是继续根目录平铺
**Context:** 根目录模块平铺不利于规模化与心智模型建立；参考 tutorials 的组织方式更清晰。  
**Decision:** 引入 `spring-boot-modules` 与 `spring-core-modules` 两级聚合层，根 pom 收敛为分组模块。  
**Rationale:** 目录更清晰、扩展更自然、对齐参考项目。  
**Alternatives:** 仅在根目录做命名约定但不分组 → 可读性提升有限。  
**Impact:** 需要一次性迁移目录与引用，但长期维护成本降低。

### ADR-2: 文档与脚本的模块定位优先使用 `:artifactId`
**Context:** 模块目录迁移会导致 `mvn -pl <path>` 以及文档命令失效。  
**Decision:** 命令入口统一为 `mvn -pl :<artifactId>`（从根目录执行）。  
**Rationale:** 对目录变化天然稳定，且 Maven 版本要求已满足。  
**Alternatives:** 继续使用相对路径 → 每次目录变化都需全局修订。  
**Impact:** 文档与脚本需要一次性统一；读者学习成本可接受且收益长期。

## Security and Performance

- **Security**
  - 不接入生产环境服务；不引入明文密钥/Token
  - 迁移脚本仅做仓库内文件变更，不执行破坏性命令
- **Performance**
  - Web MVC 测试尽量使用 `@WebMvcTest` 维持 slice 速度
  - 只有在验证 FilterChain/async/端到端行为时使用 `@SpringBootTest`

## Testing and Deployment

  - `mvn -q test`
- 站点构建验证：

