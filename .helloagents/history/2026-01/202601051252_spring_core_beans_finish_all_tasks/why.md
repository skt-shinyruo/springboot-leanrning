# Change Proposal: spring-core-beans 全任务收尾（Docs 一致性 + 闭环补齐）

## Requirement Background

`spring-core-beans` 的目标是让读者“读完文档 + 跑完 Labs，就能对 Spring Bean 有全面、透彻、能落到断点的理解”。此前已经完成了大量章节与实验增强，但仍存在：

1. 文档层面的“一致性缺口”：部分章节缺少稳定的导航（上一章｜目录｜下一章），导致阅读链路断裂；
2. 闭环层面的“入口缺口”：部分章节没有明确写出可运行的复现入口（Test/Lab）与推荐断点/观察点的标准化呈现；
3. 少量实验缺口：例如 JSR-330 `@Inject` / `Provider<T>` 的对照 Lab 仍未落地；
4. task 清单需要“可验收收口”：把剩余任务按“已覆盖/已补齐/已合并”方式收敛，避免任务长期悬空。

本次变更目标：在不引入架构级改动的前提下，把剩余任务收尾到“可顺读、可跳读、可复现、可验收”的状态。

## Change Content

1. 为 `docs/beans/spring-core-beans/**` 全量补齐导航（上一章｜目录｜下一章），并确保相对链接正确
2. 对“缺少复现入口/断点闭环呈现”的章节补齐统一的“复现入口/推荐断点/条件断点模板/观察点/运行命令”结构
3. 新增 JSR-330 `@Inject` / `Provider<T>` 对照 Lab，并在文档中落位入口与差异说明
4. 增强 testsupport 的 dump 工具，提升排障可观察性（候选集合/依赖边/来源定位）
5. 更新知识库与变更日志，并通过测试验证

## Impact Scope

- **Modules:** `spring-core-beans`、`helloagents`
- **Files:**
  - `docs/beans/spring-core-beans/**`
  - `spring-core-beans/src/test/java/**`
  - `helloagents/wiki/**`
  - `helloagents/CHANGELOG.md`
  - `helloagents/history/index.md`
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: Docs 可顺读 + 可跳读
**Module:** spring-core-beans
把 docs 的阅读体验从“靠搜索/靠运气”提升为“稳定导航 + 统一入口”，支持顺读与按问题跳读。

#### Scenario: 顺读不迷路
- 每章都能通过“上一章｜目录｜下一章”稳定跳转
- 目录页与 appendix 索引可作为“返回入口”

#### Scenario: 跳读能定位到可运行入口
- 读者从问题出发（例如“为什么注入类型不匹配？”）能定位到对应章节，并立刻跑起一个 Lab

### Requirement: 缺失实验补齐（JSR-330）
**Module:** spring-core-beans
补齐 `@Inject`/`Provider<T>` 与 `@Autowired`/`ObjectProvider<T>` 的对照，让读者能把“规范差异”落到容器行为与断点观察点。

#### Scenario: 用一个 Lab 解释两条注入体系的异同
- 同一个容器里同时存在 Spring 注入与 JSR-330 注入
- 通过断言与断点观察 `DependencyDescriptor` / provider 延迟解析的差异

## Risk Assessment

- **Risk:** 批量改动 docs 可能引入链接错误或阅读顺序混乱
- **Mitigation:** 使用脚本化检查（相对链接存在性检查）+ 运行模块测试，确保 0 断链与 BUILD SUCCESS

