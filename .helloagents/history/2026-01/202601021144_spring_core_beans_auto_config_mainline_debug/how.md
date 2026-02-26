# Technical Design: spring-core-beans Boot 自动装配（主线 + 排障工具 + 矩阵）

## Technical Solution

### Core Technologies
- Spring Boot `ApplicationContextRunner` + `AutoConfigurations`
- `@AutoConfiguration(after/before)`
- `@ConditionalOnBean` / `@ConditionalOnMissingBean`
- `ConditionEvaluationReport`（定位 why match/why skip）
- `BeanDefinition` 元信息（来源追踪）

### Implementation Key Points

1) 导入/排序主线 Lab
- 使用 `AutoConfigurations.of(...)` 构造“受控 auto-config 列表”
- 用 `Configurations.getClasses(...)` 观测排序后的 class 序列（不依赖 Boot 内置清单）
- 在 runner 里用条件（@ConditionalOnBean）验证排序对条件评估的影响

2) Bean 来源追踪工具
- 新增测试辅助类（类似 `BeanGraphDumper`）：从 `BeanDefinition` 提取：
  - beanClassName / factoryBeanName / factoryMethodName
  - resourceDescription / role / scope / primary
  - source 的类型信息（用于定位“配置类方法/扫描来源”）
- 在 Lab 中输出少量 `OBSERVE:` 行与格式化 dump

3) 覆盖/back-off 场景矩阵
- 基于 runner 构造重复候选场景
- 断言启动失败 root cause 是 `NoUniqueBeanDefinitionException`
- 用两种方式修复并断言结果：
  - primary/qualifier 确定化选择（候选仍可能有多个）
  - back-off 生效（候选只有一个）

## Security and Performance
- **Security:** 无外部服务调用，不新增依赖
- **Performance:** runner 小上下文，避免启动完整应用；Exercises 默认禁用不影响默认测试

## Testing and Deployment
- **Testing:** `mvn -pl spring-core-beans test`
- **Deployment:** None

