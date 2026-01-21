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

