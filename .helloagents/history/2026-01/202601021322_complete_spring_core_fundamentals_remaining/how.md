# Technical Design: 补齐 Spring Core Fundamentals 剩余任务（AOP / Events / Tx）

## Technical Solution

### Core Technologies
- Java 17
- Spring Boot（当前仓库版本）
- Spring Framework：AOP / Events / Transaction
- JUnit 5（新增 Tx Lab 的稳定断言）

### Implementation Key Points
- DemoRunner 输出统一使用固定前缀（建议：`AOP:` / `EVENTS:` / `TX:`），用 `key=value` 风格输出观察点，降低“日志噪音”带来的误判。
- Events 的异常传播示例采用“特定输入触发 throwing listener”的方式，避免影响日常 happy path。
- Tx 自调用陷阱 Lab 使用最小可断言策略：
  - 自调用路径：同类内部调用 `@Transactional` 方法并抛异常 → 断言数据未回滚
  - 修复路径：拆分为两个 bean，让调用走代理 → 断言数据回滚
  - 同时记录 `TransactionSynchronizationManager.isActualTransactionActive()` 作为“事务是否真的生效”的直接观察点。

## Architecture Design
无架构变更，仅补齐学习型可观察点与实验入口。

## Security and Performance
- **Security:** 不新增端点暴露；不引入敏感信息；不增加外部依赖。
- **Performance:** DemoRunner 仅做少量输出与最小数据库写入（Tx runner 已存在写入/回滚示例），不会改变模块测试性能特征。

## Testing and Deployment
- **Testing:**
  - `mvn -pl spring-core-aop test`
  - `mvn -pl spring-core-events test`
  - `mvn -pl spring-core-tx test`
  - `mvn -q test`
- **Deployment:** 本仓库为学习工程，无部署流程变更。

