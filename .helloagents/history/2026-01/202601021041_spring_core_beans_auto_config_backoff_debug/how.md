# Technical Design: spring-core-beans 自动装配 back-off/覆盖排障闭环

## Technical Solution

### Core Technologies
- Spring Boot `ApplicationContextRunner` + `AutoConfigurations`
- `@ConditionalOnMissingBean`
- `BeanDefinitionRegistryPostProcessor`（用早/晚执行对照复现“判断时机差异”）
- `ConditionEvaluationReport`（作为可查询定位手段）

### Implementation Key Points

1) Lab 设计（最小可控）
- 一个 auto-config：提供 `DemoGreeting`（`@ConditionalOnMissingBean`）
- 一个 registrar：通过注册 BeanDefinition 的方式提供同类型 `DemoGreeting`
- 通过 registrar 是否实现 `PriorityOrdered` 来控制它在 `ConfigurationClassPostProcessor` 前/后执行：
  - 早执行（PriorityOrdered, order < MAX_VALUE）：覆盖先出现 → auto-config back-off
  - 晚执行（无序/Ordered）：auto-config 先评估 → 默认 bean 注册 → 后续覆盖再出现 → 产生重复

2) 断点闭环建议（写入 docs）
- 入口：`ConditionEvaluator#shouldSkip`
- 条件细节：`OnBeanCondition#getMatchOutcome`
- refresh 主线定位：`AbstractApplicationContext#refresh` → `invokeBeanFactoryPostProcessors`

3) 文档落位与入口同步
- `docs/10` 增加一个小节：解释“为什么 override 没生效”的时机差异
- `spring-core-beans/README.md` Labs 索引表补齐该 Lab 入口

## Security and Performance
- **Security:** 仅测试/文档变更，无外部服务调用
- **Performance:** Lab 使用 runner 小上下文；测试快速稳定

## Testing and Deployment
- **Testing:** `mvn -pl spring-core-beans test`
- **Deployment:** None

