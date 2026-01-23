# 学习路线图（主线 / 机制线）

目标：把“看文档”变成“先跑起来 → 再读懂 → 再做练习 → 再能排障”的闭环。

---

## 0. Start Here（3 分钟开跑）

如果你只做一件事：先跑通 **主线的第一个 Lab**，确保你本地环境没问题（Java/Maven/依赖下载）。

```bash
mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest#usesQualifierToResolveMultipleBeans test
```

你接下来应该做：

1. 先读：`docs/beans/spring-core-beans/part-00-guide/01-quickstart-30min.md`
2. 再做：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java`

---

## 1. 主线学习路径（Beans → AOP → Tx → Web MVC）

这条线解决“我到底先学什么、为什么这样顺序、每一步怎么验证”。

### 1.1 Beans（IoC 容器与依赖注入）

- Start Here（文档）：`docs/beans/spring-core-beans/part-00-guide/01-quickstart-30min.md`
- 第一个可运行入口：
  - 命令：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest#usesQualifierToResolveMultipleBeans test`
  - 测试类：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansLabTest.java`
- 推荐下一步：
  1. 读完 Part 00 guide（`docs/beans/spring-core-beans/part-00-guide/README.md`）
  2. 进入 Part 01：注册 → 注入解析 → 生命周期（`docs/beans/spring-core-beans/part-01-ioc-container/README.md`）
  3. 做练习：`SpringCoreBeansExerciseTest`（默认存在 Solution 对照）

### 1.2 AOP（代理与切面）

- Start Here（文档）：`docs/aop/spring-core-aop/README.md`
- 第一个可运行入口：
  - 命令：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopLabTest#adviceIsAppliedToTracedMethod test`
  - 测试类：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopLabTest.java`
- 推荐下一步：
  1. 先把“为什么自调用不生效”跑明白（同一个测试类里就有对照用例）
  2. 再做练习：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part00_guide/SpringCoreAopExerciseTest.java`

### 1.3 Tx（事务：边界/传播/回滚）

- Start Here（文档）：`docs/tx/spring-core-tx/README.md`
- 第一个可运行入口：
  - 命令：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxLabTest#transactionsAreActiveInsideTransactionalMethods test`
  - 测试类：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxLabTest.java`
- 推荐下一步：
  1. 读：`docs/tx/spring-core-tx/part-01-transaction-basics/04-propagation.md`
  2. 跑矩阵：`SpringCoreTxPropagationMatrixLabTest`
  3. 做练习：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part00_guide/SpringCoreTxExerciseTest.java`

### 1.4 Web MVC（请求处理链路：绑定/校验/异常/拦截器）

- Start Here（文档）：`docs/web-mvc/springboot-web-mvc/README.md`
- 第一个可运行入口（MockMvc 版本）：
  - 命令：`mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcLabTest#pingEndpointReturnsPong test`
  - 测试类：`spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java`
- 推荐下一步：
  1. 读 Part 01：校验/异常/绑定（`docs/web-mvc/springboot-web-mvc/part-01-web-mvc/README.md`）
  2. 再跑链路追踪：`BootWebMvcTraceLabTest#syncTraceRecordsFilterAndInterceptorOrder`
  3. 做练习：`spring-boot-modules/springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`

---

## 2. 机制线（把“关键机制”按链路节点学）

这条线解决“我想按机制拆开学习，并能用断点确认发生了什么”。

### 2.1 机制线第一个可运行入口（推荐）

```bash
mvn -q -pl :springboot-web-mvc -Dtest=BootWebMvcTraceLabTest#syncTraceRecordsFilterAndInterceptorOrder test
```

### 2.2 机制线节点导航（建议顺序）

1. 容器启动主线（refresh / PP 顺序）：`spring-core-beans`
2. 代理链路（AOP）：`spring-core-aop`
3. 事务拦截器链路：`spring-core-tx`
4. 请求分发链路（DispatcherServlet / HandlerMapping / HandlerAdapter）：`springboot-web-mvc`

---

## 3. 专题扩展（可选：把“机制节点”补成体系）

> 这一组主题不强制跟主线顺序绑定：按你当前在做的事情选一个最小入口跑通即可。

### 3.1 AutoConfiguration（imports/条件/backoff）

- Start Here（文档）：`docs/autoconfig/springboot-autoconfiguration/README.md`
- 第一个可运行入口：
  - 命令：`mvn -q -pl :springboot-autoconfiguration -Dtest=BootAutoConfigurationLabTest#autoConfigCreatesDefaultBeanWhenEnabled test`
  - 测试类：`spring-boot-modules/springboot-autoconfiguration/src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationLabTest.java`

### 3.2 Logging（LoggingSystem/级别/输出）

- Start Here（文档）：`docs/logging/springboot-logging/README.md`
- 第一个可运行入口：
  - 命令：`mvn -q -pl :springboot-logging -Dtest=BootLoggingLabTest test`
  - 测试类：`spring-boot-modules/springboot-logging/src/test/java/com/learning/springboot/bootlogging/part00_guide/BootLoggingLabTest.java`

### 3.3 Observability（HTTP 指标/观测）

- Start Here（文档）：`docs/observability/springboot-observability/README.md`
- 第一个可运行入口：
  - 命令：`mvn -q -pl :springboot-observability -Dtest=BootObservabilityLabTest test`
  - 测试类：`spring-boot-modules/springboot-observability/src/test/java/com/learning/springboot/bootobservability/part00_guide/BootObservabilityLabTest.java`

### 3.4 SpEL（parse → AST → evaluate）

- Start Here（文档）：`docs/spel/spring-core-spel/README.md`
- 第一个可运行入口：
  - 命令：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelLabTest test`
  - 测试类：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part00_guide/SpringCoreSpelLabTest.java`

### 3.5 性能与并发（可复现实验范式）

> 建议先把“可复现范式”建立成默认习惯：不要靠耗时阈值断言；用 latch/失败路径/线程边界做证据链。

- Book 专题页：`docs/book/performance-and-concurrency.md`
- 推荐第一个可运行入口（线程池饱和/拒绝策略）：
  - 命令：`mvn -q -pl :springboot-async-scheduling -Dtest=BootAsyncSchedulingExecutorSaturationLabTest test`
  - 测试类：`spring-boot-modules/springboot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part02_perf_concurrency/BootAsyncSchedulingExecutorSaturationLabTest.java`
- 第二个可运行入口（SpEL 并发求值）：
  - 命令：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelConcurrencyLabTest test`
  - 测试类：`spring-core-modules/spring-core-spel/src/test/java/com/learning/springboot/springcorespel/part02_perf_concurrency/SpringCoreSpelConcurrencyLabTest.java`
- 全量模块入口（按模块选一个即可）：
  - 优先从各模块目录页 `docs/<topic>/<module>/README.md` 的“进阶入口”复制命令
  - 或直接从 Book 专题页索引进入：`docs/book/performance-and-concurrency.md`
