# Technical Design: tutorials_style_deepen_all

## Technical Solution

### Core Technologies

- Java 17 / Maven（多模块聚合）
- Spring Boot / Spring Framework（教学用最小可复现工程）
- JUnit 5 + AssertJ（Labs/Exercises/Solutions 的断言体系）
- MkDocs（`docs-site/`）与 Book（`docs/book/`）

### Implementation Key Points

1. **结构与命名策略（优先入口稳定）**
   - 原则：入口稳定优先于“完全改名”  
   - 默认策略：保持 Maven `artifactId` 稳定（命令统一用 `mvn -pl :artifactId`），目录命名/文档标题允许滚动升级  

2. **模块契约（Module Contract）作为 SSOT**
   - 定义每个模块必须具备的最小资产集合：
     - README：只做索引/导航（Start Here、推荐入口、关键命令、docs 深挖入口）
     - docs：`part-00-guide/`（深挖指南、Breakpoint Map、Branch Matrix）+ 至少 1 条 Call Chain
     - tests：至少 1 个 `*LabTest`（默认启用）+ 至少 1 个 `*ExerciseTest`（默认 `@Disabled`）
     - （可选）`*ExerciseSolutionTest`：作为对照答案，可默认启用，但需避免引入外部依赖与不稳定行为

3. **“证据链”写作范式**
   - 从现象到机制：
     1) 用 Lab 先把现象固化成断言（可回归）
     2) 在文档中给出最短调用链（Call Chain），定位关键类/方法
     3) 用 Breakpoint Map 给出“断点/观察点清单”
     4) 用 Branch Decision Matrix 统一列出关键分支与最小复现入口
   - 每个“深挖点”都必须落到一个可跑入口（测试类/测试方法），避免纯文字漂移

4. **性能与并发：两层策略避免 flaky**
   - 第一层（默认启用）：稳定断言（例如超时边界、线程切换、传播/回滚等“可判定”行为）

   - 脚手架：新增模块时自动生成“pom/README/docs 骨架/test 骨架”
     - `mvn -q test`（代码回归）
   - 增强一致性检查：模块契约缺失项（无推荐入口/无断点地图/无分支矩阵/无调用链）应被脚本检测出来

## Architecture Design

### Target Repository Layout（概念目标）

```text
springboot-learning/
  spring-boot-modules/
    <topic-modules...>
  spring-core-modules/
    <topic-modules...>
  docs/
    <topic>/
      <module>/
        part-00-guide/
        part-xx-.../
  docs/book/
  docs-site/
  scripts/
  helloagents/   (SSOT)
```

> 注：具体模块命名可逐步演进，但必须保证“入口稳定”（`:artifactId`）与“文档不迷路”（redirect/映射表）。

## Architecture Decision ADR

### ADR-001: 以 `:artifactId` 作为长期稳定入口（优先于路径/目录名）
**Context:** 目录与命名会随 tutorials 风格对齐与主题扩展发生多次迁移，如果入口依赖路径，文档与脚本维护成本会指数级上升。  
**Decision:** 文档与脚本的模块定位优先使用 `mvn -pl :<artifactId>`；推荐入口以测试类/方法为主（Lab/Exercise），避免路径耦合。  
**Rationale:** `artifactId` 稳定性强、与 Maven reactor 绑定；适合作为长期入口契约。  
**Alternatives:** 以目录名作为入口 → 拒绝原因：迁移即断链，维护成本过高。  
**Impact:** 迁移过程中必须维护 `artifactId` 不轻易变更；如确需变更，需要提供映射与过渡期策略。

**Decision:** 固化模块契约，并以脚本进行自动化检查；缺失项视为“未完成”。  
**Rationale:** 把“质量要求”工程化，避免依赖人工记忆与审阅。  
**Alternatives:** 仅靠 README 约定 → 拒绝原因：难以持续与规模化。  
**Impact:** 需要维护脚本与模板，但能显著降低长期维护成本。

## Security and Performance

- **Security:**
  - 不引入明文密钥/Token；不接入生产环境；不添加破坏性脚本命令（如 `rm -rf`、`DROP/TRUNCATE`）
  - 新增模块如涉及外部依赖（DB/消息队列/第三方服务），必须提供本地可运行替代（Testcontainers 或 in-memory），且默认不影响 `mvn test`
- **Performance:**
  - CI 默认只跑稳定、快速的 Labs
  - 长耗时/压力类实验放入 `@Disabled` 或独立 profile，避免 flaky

## Testing and Deployment

  - 全仓库：`mvn -q test`
  - 单模块：`mvn -q -pl :<artifactId> test`
- **Deployment**
  - 本仓库以本地学习与 GitHub Pages/静态站点为主；发布流程以 docs-site 构建产物为准（若启用）
