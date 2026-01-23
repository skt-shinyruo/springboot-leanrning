# Change Proposal: tutorials 风格对齐改造（全模块深挖标准化）

## Requirement Background

当前仓库已经具备“多模块 + docs-site + tests-first”的学习形态，但整体仓库外观与 Maven 层级组织方式仍与 `tutorials`（模块集合目录 + 分组聚合 + 清晰命名）存在差异：

1. Spring Boot 相关模块命名采用 `springboot-*`（缺少 `spring-boot-*` 的一致性）。
2. 子模块 `pom.xml` 直接继承根 POM，缺少按“模块集合目录（Boot/Core）”分组的父子层级。
3. 读者入口虽然有 Book 主线，但缺少“像 tutorials 一样先看到一组模块集合目录，再进入具体模块”的一致体验。

同时，我们希望在保持 Java 17、Spring Boot 3.5.x 不变的前提下，进一步把每个模块的“深挖内容”标准化到同一套可验证契约（call-chain/断点清单/分支矩阵/练习/排障/并发实验），并确保 `mvn -q test` 全绿。

## Change Content

1. **模块命名规则统一**
   - Spring Boot 模块：统一改为 `spring-boot-*`（目录名 + artifactId）
   - Spring Core 模块：保持 `spring-core-*`（如需补齐一致性，仅做最小调整）
2. **父子 POM 组织方式分层**
   - 根 POM 作为 workspace parent（统一版本与共用依赖）
   - `spring-boot-modules` / `spring-core-modules` 作为分组 parent + aggregator
   - 具体模块继承各自分组的 parent（减少耦合、增强组织一致性）
3. **模块集合目录入口对齐**
   - 从根 README / docs / wiki 入口可以按“Boot Modules / Core Modules”导航到模块集合，再进入模块
4. **全模块深挖内容标准化（A–E）**
   - A 原理 call-chain + 断点清单（Debugger Pack）
   - B 关键分支/边界矩阵 Lab（Branch/Book Matrix）
   - C Exercise 练习题 + Solution 回归
   - D Troubleshooting playbook（现象→根因→验证→断点→修复建议）
   - E 性能/并发可复现实验（避免阈值断言，偏 latch/边界/失败路径）

## Impact Scope

- **Modules:** `spring-boot-modules/*`、`spring-core-modules/*`（仅现有模块，不新增模块）
- **Files:** Maven `pom.xml`（根/分组/模块）、`docs/**`、模块 `README.md`、`scripts/**`、`helloagents/wiki/**`
- **APIs:** 无对外 API 约束变化（教学模块内部示例可能会调整，但不引入新服务端契约）
- **Data:** 无生产数据/外部依赖改动

## Core Scenarios

### Requirement: tutorials 风格的模块集合目录
**Module:** workspace
读者应能像使用 `tutorials` 一样：先从“模块集合目录”定位到需要的领域，再进入具体模块学习与运行。

#### Scenario: 从根入口进入 Boot/Core 模块集合
- 能从根入口快速识别 `spring-boot-modules` 与 `spring-core-modules`
- 能在集合目录中找到模块清单与运行命令（以 `mvn -q -pl :<artifactId> test` 为准）

### Requirement: 模块命名规则与父子 POM 分层
**Module:** build
仓库模块命名与 POM 组织方式应统一、可维护，并且不破坏现有测试回归。

#### Scenario: `mvn -q test` 全绿
- 全仓库 `mvn -q test` 通过
- 单模块 `mvn -q -pl :<artifactId> test` 通过

### Requirement: 全模块深挖契约标准化（A–E）
**Module:** docs/tests
每个模块都应具备“可导航 + 可验证 + 可断点 + 可排障 + 可并发复现”的最小闭环。

#### Scenario: 任一模块都能一键进入深挖闭环
- docs 中存在 call-chain 入口与 Debugger Pack（断点/观察点）
- 存在 Branch/Book Matrix 的可跑入口
- Exercise 默认禁用、Solution 可回归
- 至少 1 个可复现的并发/性能边界实验入口

## Risk Assessment

- **Risk:** 模块目录与 artifactId 重命名导致构建/文档/脚本引用失效  
  **Mitigation:** 先建立映射表与批量替换策略；分两步执行（先 Maven 再 docs）；每步后跑 `mvn -q test` 作为闸门。
- **Risk:** 文档链接/站点导航出现断链  
  **Mitigation:** 批量校验 `docs-site` 构建；对 `docs/**` 相对链接做自动化检查与修复。
- **Risk:** 并发/性能测试变得 flaky  
  **Mitigation:** 禁止时间阈值断言；优先 latch、可控时钟/虚拟时间、失败路径计数等稳定信号。

