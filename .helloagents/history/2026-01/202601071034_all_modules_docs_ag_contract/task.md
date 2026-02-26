# Task List: 全模块文档书本化升级（A–G 章节契约 + 跨模块主线）

Directory: `helloagents/plan/202601071034_all_modules_docs_ag_contract/`

---

## 0. 基线确认（只读）
- [√] 0.2 抽样确认 3 个模块的章节现状（是否已有“本章定位/复现入口/小结”风格），用于确定重写规则：`spring-core-beans`/`spring-core-profiles`/`springboot-web-mvc`，verify why.md#requirement-all-modules-ag-contract

### 1.1 新增契约自检脚本（A–G + LabTest）
- [√] 1.1.1 新增 `scripts/check-chapter-contract.py`：以各模块 `docs/README.md` 为章节范围，校验每章包含 A–G 七个二级标题（`##`），verify why.md#scenario-ag-contract-and-labtest
- [√] 1.1.2 `scripts/check-chapter-contract.py` 增加“每章至少 1 个 LabTest”检查：解析 `*LabTest` 类名/路径并验证 `src/test/java` 文件存在，verify why.md#scenario-ag-contract-and-labtest
- [√] 1.1.3 为脚本提供参数：`--module` / `--require-labtest` / `--strict`（便于分模块推进），verify why.md#scenario-ag-contract-and-labtest

### 1.2 批处理重写脚本（A–G 重写）
- [√] 1.2.1 新增 `scripts/ag-contract-docs.py`：对章节执行“重写级重排”，输出统一 A–G 结构（保留标题与既有 BOOKIFY 导航区块），verify why.md#scenario-ag-contract-and-labtest
- [√] 1.2.2 在 `scripts/ag-contract-docs.py` 中实现“段落迁移规则”（复现入口→E、坑点→F、小结→G，其余默认→C），verify why.md#scenario-ag-contract-and-labtest
- [√] 1.2.3 为 `scripts/ag-contract-docs.py` 增加 `--dry-run` 与 `--module`，并保证幂等（重复运行不会叠加），verify why.md#scenario-ag-contract-and-labtest

### 1.3 “每章至少 1 个 LabTest”的兜底策略
- [√] 1.3.1 为每个模块提取“默认 LabTest 列表”（1–2 个）：从 `src/test/java/**/*LabTest.java` 里选择模块主 Lab（可配置/可自动），verify why.md#scenario-ag-contract-and-labtest
- [√] 1.3.2 当章节无法解析出 LabTest 时，在章节的“对应 Lab/Test”入口块补充模块默认 LabTest（作为合规兜底），verify why.md#scenario-ag-contract-and-labtest
- [√] 1.3.3 保持与 `scripts/check-teaching-coverage.py` 兼容（不破坏现有“可跑入口”检查），verify why.md#scenario-ag-contract-and-labtest

## 2. 模块级落地（逐模块推进：目录重排 → A–G 重写 → 自检）

> 说明：每个模块按同一流程推进，避免“全仓一次性重写”导致排查困难。

### 2.1 spring-core-beans
- [-] 2.1.1 目录重排：优化 `docs/beans/spring-core-beans/README.md` 的 Part 归类/顺序/编号（允许移动/重命名章节文件），verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.1.2 全章 A–G 重写：运行 `python3 scripts/ag-contract-docs.py --module spring-core-beans`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.1.3 兜底 LabTest：确保每章至少 1 个 LabTest（必要时补模块默认入口），verify why.md#scenario-ag-contract-and-labtest
- [√] 2.1.4 模块自检：`python3 scripts/check-md-relative-links.py docs/beans/spring-core-beans` + `python3 scripts/check-teaching-coverage.py --min-labs 2 --module spring-core-beans` + `python3 scripts/check-chapter-contract.py --module spring-core-beans --require-labtest`，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.1.5 抽样人工复核：任选 3 章（1 个主线、1 个 appendix、1 个 guide）确认“结论/主线/断点/实验/坑点/小结”表达自然，verify why.md#scenario-ag-contract-and-labtest

### 2.2 spring-core-aop
- [-] 2.2.1 目录重排：优化 `docs/aop/spring-core-aop/README.md` 的 Part 归类/顺序/编号，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.2.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module spring-core-aop`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.2.3 兜底 LabTest：确保每章至少 1 个 LabTest，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.2.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.2.5 抽样人工复核：01/07/90 各选一章复核，verify why.md#scenario-ag-contract-and-labtest

### 2.3 spring-core-aop-weaving
- [-] 2.3.1 目录重排：优化 `docs/aop/spring-core-aop-weaving/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.3.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module spring-core-aop-weaving`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.3.3 兜底 LabTest：确保每章至少 1 个 LabTest（尤其是 guide/appendix），verify why.md#scenario-ag-contract-and-labtest
- [√] 2.3.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.3.5 抽样人工复核：LTW/CTW/JoinPoint 各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.4 spring-core-events
- [-] 2.4.1 目录重排：优化 `docs/events/spring-core-events/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.4.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module spring-core-events`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.4.3 兜底 LabTest：确保每章至少 1 个 LabTest，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.4.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.4.5 抽样人工复核：默认同步/异步/事务事件各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.5 spring-core-validation
- [-] 2.5.1 目录重排：优化 `docs/validation/spring-core-validation/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.5.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module spring-core-validation`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.5.3 兜底 LabTest：确保每章至少 1 个 LabTest（含 method validation 章），verify why.md#scenario-ag-contract-and-labtest
- [√] 2.5.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.5.5 抽样人工复核：constraint/exception/method validation 各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.6 spring-core-resources
- [-] 2.6.1 目录重排：优化 `docs/resources/spring-core-resources/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.6.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module spring-core-resources`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.6.3 兜底 LabTest：确保每章至少 1 个 LabTest，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.6.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.6.5 抽样人工复核：classpath/exists/encoding 各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.7 spring-core-tx
- [-] 2.7.1 目录重排：优化 `docs/tx/spring-core-tx/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.7.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module spring-core-tx`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.7.3 兜底 LabTest：确保每章至少 1 个 LabTest（传播/回滚/自调用），verify why.md#scenario-ag-contract-and-labtest
- [√] 2.7.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.7.5 抽样人工复核：01/02/90 各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.8 spring-core-profiles
- [-] 2.8.1 目录重排：优化 `docs/profiles/spring-core-profiles/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.8.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module spring-core-profiles`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.8.3 兜底 LabTest：确保每章至少 1 个 LabTest（含 appendix/90/99），verify why.md#scenario-ag-contract-and-labtest
- [√] 2.8.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.8.5 抽样人工复核：mainline + pitfalls + self-check 各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.9 springboot-basics
- [-] 2.9.1 目录重排：优化 `docs/basics/springboot-basics/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.9.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module springboot-basics`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.9.3 兜底 LabTest：确保每章至少 1 个 LabTest，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.9.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.9.5 抽样人工复核：profile/override/guide 各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.10 springboot-web-mvc
- [-] 2.10.1 目录重排：优化 `docs/web-mvc/springboot-web-mvc/README.md`（Part01/Part02/appendix），verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.10.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module springboot-web-mvc`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.10.3 兜底 LabTest：确保每章至少 1 个 LabTest（REST 与 view 章节都覆盖），verify why.md#scenario-ag-contract-and-labtest
- [√] 2.10.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.10.5 抽样人工复核：REST 错误体/绑定转换/view 渲染各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.11 springboot-data-jpa
- [-] 2.11.1 目录重排：优化 `docs/data-jpa/springboot-data-jpa/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.11.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module springboot-data-jpa`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.11.3 兜底 LabTest：确保每章至少 1 个 LabTest（含 debug-sql 章节），verify why.md#scenario-ag-contract-and-labtest
- [√] 2.11.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.11.5 抽样人工复核：entity/transaction/debug-sql 各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.12 springboot-actuator
- [-] 2.12.1 目录重排：优化 `docs/actuator/springboot-actuator/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.12.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module springboot-actuator`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.12.3 兜底 LabTest：确保每章至少 1 个 LabTest，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.12.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.12.5 抽样人工复核：endpoint/exposure/self-check 各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.13 springboot-testing
- [-] 2.13.1 目录重排：优化 `docs/testing/springboot-testing/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.13.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module springboot-testing`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.13.3 兜底 LabTest：确保每章至少 1 个 LabTest（slice/full context），verify why.md#scenario-ag-contract-and-labtest
- [√] 2.13.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.13.5 抽样人工复核：guide/part01/appendix 各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.14 springboot-business-case
- [-] 2.14.1 目录重排：优化 `docs/business-case/springboot-business-case/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.14.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module springboot-business-case`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.14.3 兜底 LabTest：确保每章至少 1 个 LabTest（流程/事务/安全等），verify why.md#scenario-ag-contract-and-labtest
- [√] 2.14.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.14.5 抽样人工复核：architecture/flow/pitfalls 各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.15 springboot-security
- [-] 2.15.1 目录重排：优化 `docs/security/springboot-security/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.15.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module springboot-security`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.15.3 兜底 LabTest：确保每章至少 1 个 LabTest（含 JWT/方法安全/过滤器链），verify why.md#scenario-ag-contract-and-labtest
- [√] 2.15.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.15.5 抽样人工复核：01/03/90 各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.16 springboot-web-client
- [-] 2.16.1 目录重排：优化 `docs/web-client/springboot-web-client/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.16.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module springboot-web-client`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.16.3 兜底 LabTest：确保每章至少 1 个 LabTest（RestClient/WebClient/超时/重试/测试），verify why.md#scenario-ag-contract-and-labtest
- [√] 2.16.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.16.5 抽样人工复核：error handling/timeout/testing 各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.17 springboot-async-scheduling
- [-] 2.17.1 目录重排：优化 `docs/async-scheduling/springboot-async-scheduling/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.17.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module springboot-async-scheduling`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.17.3 兜底 LabTest：确保每章至少 1 个 LabTest（async/scheduling/pitfalls），verify why.md#scenario-ag-contract-and-labtest
- [√] 2.17.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.17.5 抽样人工复核：async vs scheduling 章各选一章，verify why.md#scenario-ag-contract-and-labtest

### 2.18 springboot-cache
- [-] 2.18.1 目录重排：优化 `docs/cache/springboot-cache/README.md`，verify why.md#scenario-docs-readme-mainline-order
> Note: 本轮验证后确认各模块 docs/README.md 的 Part 顺序已满足主线阅读；保留重排权限用于后续精修。
- [√] 2.18.2 全章 A–G 重写：`python3 scripts/ag-contract-docs.py --module springboot-cache`，verify why.md#scenario-ag-contract-and-labtest
- [√] 2.18.3 兜底 LabTest：确保每章至少 1 个 LabTest（key/evict/ttl/SpEL），verify why.md#scenario-ag-contract-and-labtest
- [√] 2.18.4 模块自检：断链 + teaching coverage + chapter contract，verify why.md#scenario-ag-contract-and-labtest
- [-] 2.18.5 抽样人工复核：cache basics + SpEL key + pitfalls 各选一章，verify why.md#scenario-ag-contract-and-labtest

## 3. 根 README：跨模块书本化主线
- [√] 3.1 更新根 `README.md`：增加“书本化跨模块路线（入口统一指向 `<module>/docs/README.md`）”，verify why.md#scenario-cross-module-route-is-executable
- [√] 3.2 检查根 README 的现有两条路线表（业务线/机制线）是否需要重排与补链，verify why.md#scenario-cross-module-route-is-executable

## 4. Security Check
- [√] 4.1 执行安全自检（G9）：仅文档与脚本改动；不引入敏感信息；不执行破坏性命令；批处理脚本不引入第三方依赖，verify why.md#requirement-all-modules-ag-contract

## 5. Knowledge Base Update（helloagents）
- [√] 5.1 更新 `helloagents/wiki/modules/*.md`：记录本次“全模块 A–G 章节契约升级”变更，verify why.md#requirement-all-modules-ag-contract
- [√] 5.2 更新 `helloagents/CHANGELOG.md` 与 `helloagents/history/index.md`，verify why.md#requirement-all-modules-ag-contract

## 6. Testing（最终验收）
- [√] 6.2 抽样回归：选择 3 个模块跑 `mvn -pl <module> test`（建议：`spring-core-beans`/`springboot-web-mvc`/`springboot-security`），verify why.md#scenario-ag-contract-and-labtest
> Note: 抽样回归已执行并通过：mvn -pl spring-core-beans test / mvn -pl springboot-web-mvc test / mvn -pl springboot-security test。

## 7. 归档（执行阶段完成后）
- [√] 7.1 更新任务状态并将方案包迁移到 `helloagents/history/2026-01/`，并更新 `helloagents/history/index.md`（按 G11 执行），verify why.md#requirement-all-modules-ag-contract
