# Change Proposal: spring-core-beans Boot 自动装配（导入/排序主线 + 来源追踪 + 覆盖矩阵）补齐

## Requirement Background

当前 `spring-core-beans` 已覆盖 Boot 自动装配的条件注解、顺序依赖（after/before）、以及 back-off 时机差异等关键点，但对“框架岗向”的系统学习还缺少三个更贴近真实排障的闭环能力：

1) **导入/排序主线**：自动配置列表如何被排序（after/before）、排序结果如何影响条件评估与最终注册
2) **Bean 来源追踪**：遇到“这个 bean 到底是谁注册的？”如何从 BeanDefinition 的信息快速定位来源
3) **覆盖/back-off 场景矩阵**：把“重复候选 → 注入失败 → 两类修复（确定化选择 vs 让 back-off 生效）”串成一组可断言最小复现

## Change Content

按优先级顺序落地：

1. 新增导入/排序主线 Lab：用最小 auto-config 列表复现 after/before 的排序与条件影响（不依赖 Boot 内置海量 auto-config 清单）
2. 新增 Bean 来源追踪工具（测试辅助类）：输出 BeanDefinition 的 class/factoryMethod/resourceDescription/source/role 等关键信息，用于排障与讲解
3. 新增覆盖/back-off 场景矩阵 Lab：复现重复候选导致注入失败，并给出两条修复路径（@Primary/@Qualifier vs 让 back-off 生效）

## Impact Scope

- **Modules:** `spring-core-beans`
- **Files:** `spring-core-beans/src/test/java/...`、`docs/beans/spring-core-beans/10-*`、`spring-core-beans/README.md`
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: 导入/排序主线可断言
**Module:** spring-core-beans
在不依赖 Boot 内置 auto-config 巨量清单的情况下，复现 auto-config 列表的排序（after/before）与其对条件评估的影响。

#### Scenario: after/before 让行为与“传入列表顺序”解耦
- 输入列表顺序反转时，排序结果依然确定
- dependent auto-config 的条件能因为排序而稳定 match

### Requirement: Bean 来源追踪可复用
**Module:** spring-core-beans
给出一个可复用工具，能用 BeanDefinition 信息快速回答“谁注册的/从哪里来”。

#### Scenario: 能区分 @Bean 工厂方法 vs 直接类定义
- 输出 factoryBeanName/factoryMethodName 与 beanClassName/resourceDescription
- 能给出最小排障闭环入口

### Requirement: 覆盖/back-off 场景矩阵可断言
**Module:** spring-core-beans
把重复候选导致注入失败、以及两条修复路径串成最小复现矩阵。

#### Scenario: 重复候选导致 NoUniqueBeanDefinitionException
- 在没有 qualifier/primary 的情况下，单注入 fail-fast

#### Scenario: 两条修复路径
- 使用 `@Primary/@Qualifier` 让注入“确定化选择”
- 或让 back-off 生效使容器中只有一个候选（更干净）

## Risk Assessment

- **Risk:** 依赖 Boot 内部实现细节导致未来版本变化  
  **Mitigation:** Labs 只断言“排序结果/是否注册/异常类型”，不对具体 message 文本做强断言

