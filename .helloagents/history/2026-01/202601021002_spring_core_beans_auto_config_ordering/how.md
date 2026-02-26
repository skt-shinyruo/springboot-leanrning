# Technical Design: spring-core-beans 自动装配顺序与条件“可复现”补齐

## Technical Solution

### Core Technologies
- Java 17
- Spring Boot（依赖版本由父 pom 管理）
- `spring-boot-starter-test`
- `ApplicationContextRunner` + `AutoConfigurations`
- `ConditionEvaluationReport`

### Implementation Key Points

1) `matchIfMissing` 的最小复现
- 使用 `ApplicationContextRunner` 构造三个场景：property 缺失 / false / true
- 断言“bean 是否存在”，并用 `OBSERVE:` 输出提示学习者观察点
- 该 Lab 与 `docs/10` 的“条件装配”章节绑定

2) 自动配置顺序依赖的最小复现
- 设计两个 `@AutoConfiguration`：
  - `MarkerAutoConfiguration`：提供 `Marker` bean
  - `DependentAutoConfiguration`：`@ConditionalOnBean(Marker.class)`，只有当 Marker 已存在时才注册 dependent bean
- 先做对照：导入顺序不同 → 行为不同（order-sensitive）
- 再做修复：在 dependent auto-config 上声明 `@AutoConfiguration(after=MarkerAutoConfiguration.class)` → 变成确定性行为

3) 文档与入口同步
- `docs/10-spring-boot-auto-configuration.md` 新增小节：把“顺序敏感/如何确定化”写成“题目 + 追问 + 复现入口”
- `spring-core-beans/README.md` 的 Lab 索引表新增对应入口，确保学习路径可发现

## Security and Performance
- **Security:** 仅测试与文档变更，无外部服务调用，无敏感信息引入
- **Performance:** Lab 采用 `ApplicationContextRunner`，避免启动完整 Spring Boot 应用，保证测试快速稳定

## Testing and Deployment
- **Testing:** `mvn -pl spring-core-beans test`
- **Deployment:** None（学习型仓库模块）

