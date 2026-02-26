# Change Proposal: spring-core-beans Part 01（IoC Container）逐章内容深度完善（Solution）

## Requirement Background

当前 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/` 已覆盖注册、注入、Scope、生命周期、扩展点、配置类增强、FactoryBean、循环依赖、心智模型等主题，但读者在“进阶理解/排障迁移/源码证明”阶段仍可能出现以下典型断层：

1. **概念能复述，但无法证明**：知道结论却不能用断点、关键变量、最短调用链把结论落地。
2. **章节内理解 OK，章节间迁移失败**：例如“注册→注入→生命周期→代理/替换”之间的因果链断裂，导致排障时走错入口。
3. **高级边界不完整**：注入点元数据（Field vs `MethodParameter`）、scoped proxy（`ScopedProxyMode`）、`@Bean` 方法参数解析、FactoryBean 的类型/缓存语义、循环依赖与代理的交叉等，容易在真实项目里直接触发坑位。

本次目标是：**逐章补齐“补充/完善/深入”的具体策略**，让每章都能更稳地支撑“理解→证明→排障→面试复述”的迁移。

## Change Content

1. 逐章补齐“关键边界/反例/证据链抓手”，把容易误判的点变成可验证结论。
2. 强化章节之间的桥接：把“注册→注入→生命周期→代理替换”的因果链写成可跳转路径。
3. 强化与本仓库 Labs/Test 的对齐：每章明确可复现入口、关键断点、watch list 与典型异常→断点入口。

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`
- **Files:** `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/*.md`
- **APIs:** None（文档与学习资产变更，不改业务 API）
- **Data:** None

## Core Scenarios

### Requirement: Part 01（IoC Container）逐章深度完善
**Module:** spring-core-beans
围绕 IoC 主链路（Definition → Instance → Exposed）对每章做“补充/完善/深入”策略设计。

#### Scenario: 注册层问题能快速归因（扫不到 / 注册时机不对 / 定义被改写）
- 能把“注册入口差异”落到 `BeanDefinition` 类型与关键字段差异
- 能解释“注册时机决定能力”，并给出断点闭环

#### Scenario: 注入歧义/泛型/限定注解能用证据链解释（不靠猜）
- 能从异常与 `DependencyDescriptor` 还原注入点元数据
- 能解释候选收集/收敛的决策路径（含 by-name fallback 边界）

#### Scenario: Scope/prototype 语义不再“凭经验”
- 能解释“prototype 注入 singleton 为什么像单例”
- 能区分 `ObjectProvider` / `@Lookup` / scoped proxy 的语义与边界

#### Scenario: 生命周期回调顺序与代理交织可解释、可断点证明
- 能说明 `@PostConstruct/@PreDestroy` 的触发者与发生窗口
- 能解释“回调发生在 raw 还是 proxy”取决于代理替换时机

#### Scenario: 扩展点（BFPP/BPP/BDRPP）能按“介入点”理解并用于排障
- 能说明每类扩展点的能力边界与典型误用
- 能解释顺序与时机如何影响最终行为（含两段式算法）

#### Scenario: `@Configuration` / `@Bean` 语义差异可复现、可反例
- 能解释 `proxyBeanMethods` 的真实影响（语义 vs 性能）
- 能用断点证明“方法参数注入”不依赖配置类增强

#### Scenario: FactoryBean 与循环依赖的交叉不再误判
- 能区分 product vs factory（含 `&` 前缀）并解释类型匹配与缓存语义
- 能解释循环依赖救援窗口与 early/final 不一致的风险

## Risk Assessment

- **Risk:** 文档策略与源码事实/本仓库实验不一致，导致读者按文档排障走偏。
- **Mitigation:** 每章策略必须绑定可运行 Lab/Test 或可落到方法级断点的证据链；在执行阶段用 `mvn -pl spring-core-modules/spring-core-beans test` 做回归验证。

