# Task List: tutorials_style_reorg（分组聚合 + 目录重排 + Web MVC 深度示范）

Directory: `helloagents/plan/202601201248_tutorials_style_reorg/`

> 本方案包聚焦两件事：  
> 1) **工程结构**对齐 `/home/feng/code/temp/tutorials` 的“分组聚合 + 层级目录”；  
> 2) 以 **Web MVC** 为第一批示范，补齐更深入的“矩阵/证据链/排障入口”。  
> 原则：入口稳定优先（`:artifactId`）→ 分批迁移 → 闸门回归（tests/docs/site）。

---

## 0. 本轮 DoD（验收标准）

- [√] 0.0.1 Maven：根 `pom.xml` 仅聚合分组模块（Boot/Core），verify why.md#requirement-r1-tutorials-style-grouping
- [√] 0.0.2 构建：`mvn -q test` 全绿（迁移收尾验收），verify why.md#requirement-r1-tutorials-style-grouping
- [√] 0.0.3 文档闸门：`bash scripts/check-docs.sh` 通过，verify why.md#requirement-r4-docs-site-and-book-consistency
- [√] 0.0.4 站点：`bash scripts/docs-site-build.sh` 通过，verify why.md#requirement-r4-docs-site-and-book-consistency
- [√] 0.0.5 Web MVC：新增 1 个“矩阵/证据链”入口（Lab + Doc 绑定），verify why.md#requirement-r3-webmvc-deepen-batch01
- [√] 0.0.6 SSOT：同步更新 `helloagents/wiki/**` + `helloagents/CHANGELOG.md` + 方案包迁移到 `helloagents/history/`，verify why.md#requirement-r2-stable-entrypoints

---

## 1. 基线回归（开工前）

- [√] 1.1 运行 `bash scripts/check-docs.sh`（记录基线输出），verify why.md#requirement-r4-docs-site-and-book-consistency
- [√] 1.2 运行 `mvn -q test`（记录基线输出），verify why.md#requirement-r1-tutorials-style-grouping

---

## 2. Maven 分组聚合骨架（对齐 tutorials 风格）

### 2.1 新增分组聚合模块（Boot/Core）

- [√] 2.1.1 新增 `spring-boot-modules/pom.xml`（packaging=pom，聚合 springboot-*），verify why.md#requirement-r1-tutorials-style-grouping-s1-group-poms-aggregate-modules
- [√] 2.1.2 新增 `spring-core-modules/spring-core-modules/pom.xml`（packaging=pom，聚合 spring-core-*），verify why.md#requirement-r1-tutorials-style-grouping-s1-group-poms-aggregate-modules
- [√] 2.1.3 更新根 `pom.xml` 的 `<modules>`：只保留 `spring-boot-modules` + `spring-core-modules`，verify why.md#requirement-r1-tutorials-style-grouping-s1-group-poms-aggregate-modules

> 说明：分组 pom 初期可用 `../<module>` 方式聚合“仍在旧位置”的子模块，确保迁移过程中构建可用。

---

## 3. 子模块迁移（物理目录重排）

> 迁移策略：逐模块迁移，批次闸门回归；每个模块迁移至少包含：目录移动 + pom 适配 + README 链接/命令适配。  
> 目标结构最终为：`spring-boot-modules/<springboot-*>` 与 `spring-core-modules/spring-core-modules/<spring-core-*>`。

### 3.1 springboot-* → spring-boot-modules/

- [√] 3.1.1 迁移 `springboot-web-mvc`（第一批示范）：目录移动 + 更新 `spring-boot-modules/pom.xml` modules 列表，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.1.2 适配 `spring-boot-modules/springboot-web-mvc/pom.xml`（父 pom/relativePath）与模块 README 的命令/链接（`:artifactId` + 相对路径修正），verify why.md#requirement-r2-stable-entrypoints

- [√] 3.1.3 迁移 `springboot-basics`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.1.4 适配 `spring-boot-modules/springboot-basics/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.1.5 迁移 `springboot-data-jpa`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.1.6 适配 `spring-boot-modules/springboot-data-jpa/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.1.7 迁移 `springboot-actuator`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.1.8 适配 `spring-boot-modules/springboot-actuator/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.1.9 迁移 `springboot-testing`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.1.10 适配 `spring-boot-modules/springboot-testing/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.1.11 迁移 `springboot-business-case`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.1.12 适配 `spring-boot-modules/springboot-business-case/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.1.13 迁移 `springboot-security`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.1.14 适配 `spring-boot-modules/springboot-security/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.1.15 迁移 `springboot-web-client`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.1.16 适配 `spring-boot-modules/springboot-web-client/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.1.17 迁移 `springboot-async-scheduling`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.1.18 适配 `spring-boot-modules/springboot-async-scheduling/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.1.19 迁移 `springboot-cache`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.1.20 适配 `spring-boot-modules/springboot-cache/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

### 3.2 spring-core-* → spring-core-modules/spring-core-modules/

- [√] 3.2.1 迁移 `spring-core-beans`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.2.2 适配 `spring-core-modules/spring-core-beans/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.2.3 迁移 `spring-core-aop`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.2.4 适配 `spring-core-modules/spring-core-aop/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.2.5 迁移 `spring-core-aop-weaving`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.2.6 适配 `spring-core-modules/spring-core-aop-weaving/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.2.7 迁移 `spring-core-events`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.2.8 适配 `spring-core-modules/spring-core-events/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.2.9 迁移 `spring-core-validation`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.2.10 适配 `spring-core-modules/spring-core-validation/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.2.11 迁移 `spring-core-resources`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.2.12 适配 `spring-core-modules/spring-core-resources/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.2.13 迁移 `spring-core-tx`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.2.14 适配 `spring-core-modules/spring-core-tx/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

- [√] 3.2.15 迁移 `spring-core-profiles`，verify why.md#requirement-r1-tutorials-style-grouping
- [√] 3.2.16 适配 `spring-core-modules/spring-core-profiles/pom.xml` 与模块 README，verify why.md#requirement-r2-stable-entrypoints

---

## 4. 文档与脚本去路径耦合（迁移后统一修复）

### 4.1 scripts：模块定位统一为 `:artifactId`

- [√] 4.1.1 更新 `scripts/test-module.sh`：支持 `mvn -pl :<artifactId>`（保持老用法可兼容），verify why.md#requirement-r2-stable-entrypoints-s1-mvn-pl-by-artifactid
- [√] 4.1.2 更新 `scripts/run-module.sh`：支持 `mvn -pl :<artifactId> spring-boot:run`，verify why.md#requirement-r2-stable-entrypoints-s1-mvn-pl-by-artifactid
- [√] 4.1.3 如存在其他脚本引用模块路径：统一替换为 `:artifactId`，verify why.md#requirement-r2-stable-entrypoints-s1-mvn-pl-by-artifactid

### 4.2 docs：批量更新源码/测试路径引用

- [√] 4.2.1 批量把 `springboot-*/src/...` 与 `spring-core-*/src/...` 的引用更新到新分组路径（Docs/Book/Module README 全覆盖），verify why.md#requirement-r2-stable-entrypoints
- [√] 4.2.2 Web MVC Book 相关章节（`docs/book/*webmvc*`）同步更新引用与推荐命令（`:springboot-web-mvc`），verify why.md#requirement-r4-docs-site-and-book-consistency

---

## 5. Web MVC 深度示范（第一批新增）

> 目标：新增 1 个“矩阵化证据链”，把 406/415/400 的关键分支用断言固化，并在文档中给出断点/观察点。

- [√] 5.1 新增（或扩展）Web MVC 合同矩阵 LabTest：内容协商分支（406/415/400）对照，verify why.md#requirement-r3-webmvc-deepen-batch01-s1-content-negotiation-matrix
- [√] 5.2 更新对应文档：补齐“关键分支 → 证据链 → 推荐断点/观察点 → 常见坑”，并指向 5.1 的入口，verify why.md#requirement-r3-webmvc-deepen-batch01-s1-content-negotiation-matrix

---

## 6. Security Check（强制）

- [√] 6.1 安全自检（G9）：无生产环境操作、无明文密钥/Token、无破坏性脚本命令

---

## 7. 验证（阶段收尾）

- [√] 7.1 运行 `bash scripts/check-docs.sh`
- [√] 7.2 运行 `bash scripts/docs-site-build.sh`
- [√] 7.3 运行 `mvn -q test`

---

## 8. 知识库同步与归档（强制）

- [√] 8.1 更新 `helloagents/wiki/overview.md` / `helloagents/wiki/learning-path.md`：目录结构变化与入口命令变化（`:artifactId`），verify why.md#requirement-r2-stable-entrypoints
- [√] 8.2 更新 `helloagents/wiki/modules/springboot-web-mvc.md`：新增矩阵入口与迁移后的模块路径索引，verify why.md#requirement-r3-webmvc-deepen-batch01
- [√] 8.3 更新 `helloagents/CHANGELOG.md`：记录本次结构变更与新增 Web MVC 深度内容
- [√] 8.4 迁移方案包：`helloagents/plan/202601201248_tutorials_style_reorg/` → `helloagents/history/2026-01/202601201248_tutorials_style_reorg/`
- [√] 8.5 更新 `helloagents/history/index.md`：新增索引记录（✅Completed）

