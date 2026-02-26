# Technical Design: spring-core-beans Auto-configuration/Conditions Exercises 深挖

## Technical Solution

### Core Technologies
- Spring Boot test utilities: `ApplicationContextRunner`、`AutoConfigurations`
- `ConditionEvaluationReport`（将报告当成可查询数据结构）
- JUnit 5 + AssertJ

### Implementation Key Points

1) Exercises 以“可执行练习”形式补齐
- 继续沿用仓库现有模式：`@Test` + `@Disabled("练习...")` + 失败断言提示块
- 每个练习必须给出：
  - 题目（要实现什么）
  - 追问（要能解释什么）
  - 复现入口（本仓库对应 Lab/Test 文件路径）
  - 推荐断点（1-3 个即可）

2) 文档落位到正文对应小节
- 在 `docs/10` 的条件章节（4.x）补齐 `@ConditionalOnBean` 的顺序/时机差异说明
- “面试常问”只作为总结，不做独立面试题文档

## Security and Performance
- **Security:** 无外部服务调用，不新增依赖
- **Performance:** Exercises 默认禁用，不影响默认 `mvn test`；Labs 仍以 runner 小场景为主

## Testing and Deployment
- **Testing:** `mvn -pl spring-core-beans test`
- **Deployment:** None

