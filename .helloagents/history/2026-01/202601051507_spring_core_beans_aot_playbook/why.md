# Change Proposal: spring-core-beans AOT/生产排障/面试体系补齐

## Requirement Background

当前仓库的 `spring-core-beans` 已经覆盖了 IoC/Bean 生命周期/扩展点/循环依赖/FactoryBean/装配边界/Boot 自动装配与可观察性等主线，但仍存在一个“学习闭环缺口”：

1) **AOT/Native 时代的容器约束**缺少可运行的最小实验（RuntimeHints、代理/反射/动态注册的限制与替代方案）。
2) **生产排障**缺少“可复述的排障 SOP”（从异常 → 断点入口 → 观察点 → 修复策略），虽然已有章节，但可固化成模板式清单。
3) **面试体系化**缺少“可背诵但不靠背”的决策树（候选收敛、后处理器边界、循环依赖窗口期、自动装配 back-off 时机等）。
4) 若要让以上三条线落地，需要补齐一些“真实项目里经常遇到，但学习资料常跳过”的机制：XML/BeanDefinitionReader、容器外对象注入（AutowireCapableBeanFactory）、SpEL/表达式解析、自定义限定符（meta-annotation）。

## Change Content

1. 新增一组“Part 05：真实世界与 AOT”章节（以可断言实验为主线）。
2. 新增对应 Labs（可运行、可断言、可下断点）。
3. 新增 2 份 Appendix 文档：
   - 面试复述模板（按决策树组织）
   - 生产排障清单（按异常 → 入口 → 观察点组织）
4. 更新 `spring-core-beans` 的 README/Docs TOC/知识点地图，使新增内容可导航、可跳读。

## Impact Scope

- **Modules:**
  - `spring-core-beans`
  - `helloagents`（知识库同步）
- **Files:**
  - `docs/beans/spring-core-beans/**`
  - `spring-core-beans/src/test/java/**`
  - `spring-core-beans/README.md`
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/CHANGELOG.md`

## Core Scenarios

### Requirement: AOT/Native 心智模型与 RuntimeHints
**Module:** spring-core-beans
把“为什么 JVM 里能跑、Native 里会挂”变成可验证的结论，并给出 RuntimeHints 的最小落地方式。

#### Scenario: 通过 AOT 处理流程观察/断言 RuntimeHints 的存在性
- 在 JVM 测试中运行 AOT 生成流程（不要求构建 native image）
- 能断言：未注册 hints 时缺失；注册 hints 后出现
- 能给出断点入口（AOT 处理、hints 写入点）

### Requirement: 生产排障 SOP（从异常到根因）
**Module:** spring-core-beans
把常见异常（NoSuch/NoUnique/UnsatisfiedDependency/BeanCreation/BeanDefinitionStore）固化为 3 步收敛流程。

#### Scenario: 从异常信息定位到固定断点入口与观察点
- 给出每类异常的首选断点入口
- 给出关键观察点（候选集合、最终选中、依赖边、post-processors 列表、缓存三表等）

### Requirement: 面试体系化复述模板（可背诵但可证明）
**Module:** spring-core-beans
把容器关键决策树变成可复述结构，并能用本仓库 Lab/Test 证明。

#### Scenario: 用“决策树 + 对应测试方法 + 断点入口”回答高频题
- 候选收集/收敛/注入
- BFPP/BDRPP/BPP 能做什么/不能做什么
- 循环依赖为什么 constructor 无解、setter 有时能救
- Boot 自动装配条件评估时机与 back-off

### Requirement: 真实项目里常见的补齐机制（XML/外部对象/SpEL/自定义限定符）
**Module:** spring-core-beans
补齐那些“平时用得到、但不在默认主线里”的机制，让排障与面试回答更完整。

#### Scenario: 每个机制都有最小可运行实验与对应章节
- XML → BeanDefinitionReader → BeanDefinition 元信息
- 容器外对象 → AutowireCapableBeanFactory 托管注入/回调
- SpEL → 表达式解析与 `@Value("#{...}")`
- 自定义 qualifier → meta-annotation 与匹配规则

## Risk Assessment

- **Risk:** 新增章节与 Labs 数量较多，可能导致目录/索引不一致或导航断链。
  - **Mitigation:** 同步更新 Docs TOC、模块 README、知识点地图，并用 `mvn -pl spring-core-beans test` 做回归验证。
- **Risk:** AOT 相关 API 在不同 Spring Boot 版本存在差异。
  - **Mitigation:** 以 Spring Boot 3.5.x / Spring Framework 6.2.x 的公共 API 为准；测试优先用官方提供的 predicates/生成上下文实现。

