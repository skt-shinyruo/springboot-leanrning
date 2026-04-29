# Spring Boot 学习项目（多模块）

本仓库是一个面向学习与验证的 Spring Boot 多模块工作区：以模块为主题边界，以可运行测试为主要入口，以模块文档为阅读主线。

- **模块边界**：一个模块对应一个主题；各模块可以独立运行与测试。
- **测试入口**：测试用例分为两类：  
  - `*LabTest.java`：提供可复现的现象与断言证据链（默认启用，要求全绿）。  
  - `*ExerciseTest.java`：提供动手改写题（默认 `@Disabled`，需学习者手动开启）。  
- **文档组织**：模块文档位于各自的 `<module>/docs/`；仓库根 `docs/` 提供：[`docs/SUMMARY.md`](docs/SUMMARY.md)（全站目录 SSOT）与 [`docs/book/README.md`](docs/book/README.md)（全书主线，教材式顺读）。

更稳定的入口是：先跑通一个模块的最小闭环，再阅读机制细节，而不是从索引开始逐条记忆。

## 从哪里开始（三条阅读路径）

- **路径 A（全书主线，教材式顺读）**：从 [`docs/book/README.md`](docs/book/README.md) 开始按章推进；侧边栏目录与完整索引以 [`docs/SUMMARY.md`](docs/SUMMARY.md) 为准。
- **路径 B（按目录阅读）**：从 [`docs/SUMMARY.md`](docs/SUMMARY.md) 进入全站目录，按模块 → 章节阅读。
- **路径 C（以可运行入口驱动）**：直接进入某个模块的根 `README.md`（模块目录页，例如 `spring-boot-modules/spring-boot-basics/README.md`）→ 先运行一个 `*LabTest.java` 形成可验证的证据 → 再开启 `*ExerciseTest.java` 完成改写练习。

> 想要可搜索、按“全书主线→模块→章节”侧边栏顺读的站点体验：看 [`docs-site/README.md`](docs-site/README.md)（本地预览/构建/Pages 发布）。

## 环境要求

- JDK：`17`
- Maven：`3.8+` 或更高版本

> 本仓库不提供 Maven Wrapper（`mvnw`），请直接使用系统的 `mvn`。

## 快速开始（先跑起来）

首次使用时，先在本地完整运行一次测试，以确认 JDK/Maven 环境与仓库依赖可用。此步骤的目标是验证环境与入口可运行，而非理解全部细节。

- 构建并运行所有模块测试（默认只跑实验）：

```bash
mvn -q test
```

> 如首次执行出现失败，优先参考下方 `Debug 工具箱` 的定位流程。

- 只构建/测试某个模块（示例：`spring-boot-web-mvc`）：

```bash
mvn -q -pl :spring-boot-web-mvc test
```

- 运行某个模块（示例：`spring-boot-basics`）：

```bash
mvn -pl :spring-boot-basics spring-boot:run
```

## 学习模式：实验 vs 练习（从现象到可验证的理解）

可以将每个模块的测试入口理解为两层：

- **实验**：`src/test/java/.../*LabTest.java`（默认启用，`mvn -q test` 必须全绿）
  目的：提供可复现的现象，并给出将现象固化为断言的证据链入口。
- **练习**：`src/test/java/.../*ExerciseTest.java`（默认 `@Disabled`，学习者手动开启）
  目的：要求学习者通过改写实现行为变化，并以测试结果验证理解，而非停留在阅读层面。

开启练习的方法：打开对应的 `*ExerciseTest`，移除/注释 `@Disabled`，根据测试提示完成实现（或改造），再运行模块测试验证。

## 快速上手（约 10 分钟）：跑通一个模块（默认从 `spring-boot-basics` 开始）

按以下顺序完成一次最小闭环：

- 先跑一遍全仓库 `mvn -q test`，确认环境没问题。
- 进入模块目录页（例如 [`spring-boot-basics` 模块目录](spring-boot-modules/spring-boot-basics/README.md)），找一个主线入口 `*LabTest.java` 跑起来。
- 当能够通过断点观察到一次“调用链/容器边界/代理边界”，该模块主线即可进入；剩余内容可按目录逐步补齐。

## Debug 工具箱（遇到红测/异常时）

- 常用命令：
  - 全仓库：`mvn -q test`
  - 单模块：`mvn -q -pl <module> test`
  - 运行模块：`mvn -pl <module> spring-boot:run`
- 常用定位套路：
  - 先看**失败断言**（测试描述通常就是“现象”），再回到对应的 `*LabTest` 代码
  - 优先用断点“看见容器/代理/事务边界”而不是只看日志
  - Boot 条件装配排查：必要时加 `--debug` 看 Condition Evaluation（或对照 `spring-core-beans` 的调试章节）

## 学习路线（含预计耗时与完成标准）

### 每个模块的通用完成标准（退出条件）

- 能解释：用自己的话讲清“机制 + 典型坑 + 为什么这样设计”（不背 API）
- 能改写：完成一个实验或练习的“改造题”，并保持模块测试全绿
- 能调试：遇到红测/异常，能用断点/日志/断言定位根因并验证修复

### 主线（先跑通业务闭环）

| 顺序 | 模块 | 参考耗时 | 完成标准（能解释/能改写/能调试） |
| --- | --- | --- | --- |
| 1 | [`spring-boot-basics`](spring-boot-modules/spring-boot-basics/README.md) | 1–2h | 启动、配置、Profile 与绑定；能定位配置最终值来自哪里 |
| 2 | [`spring-boot-web-mvc`](spring-boot-modules/spring-boot-web-mvc/README.md) | 2–3h | Controller/校验/错误处理；扩展请求字段；能定位 400 的字段错误 |
| 3 | [`spring-boot-data-jpa`](spring-boot-modules/spring-boot-data-jpa/README.md) | 2–3h | persistence context/flush/脏检查；加查询；能解释“一级缓存假象” |
| 4 | [`spring-boot-business-case`](spring-boot-modules/spring-boot-business-case/README.md) | 2–4h | 串联 Web/JPA/Tx/AOP/Events；改一条链路；能定位代理/事务边界问题 |

### 机制线（把底层原理补齐）

| 顺序 | 模块 | 参考耗时 | 完成标准（能解释/能改写/能调试） |
| --- | --- | --- | --- |
| 1 | [`spring-core-beans`](spring-core-modules/spring-core-beans/README.md) | 4–6h | Bean 注册/注入/生命周期/扩展点；能解释 BFPP vs BPP；能用调试手段“看见容器” |
| 2 | [`spring-core-profiles`](spring-core-modules/spring-core-profiles/README.md) | 1–2h | `@Profile`/`@ConditionalOnProperty`；避免注入歧义；能定位条件为何不生效 |
| 3 | [`spring-core-validation`](spring-core-modules/spring-core-validation/README.md) | 1–2h | 约束/violation/method validation；能解释“为何依赖代理”；能定位 `@Validated` 缺失 |
| 4 | [`spring-core-aop`](spring-core-modules/spring-core-aop/README.md) | 2–3h | 代理/切点/自调用/final 限制；能判断 JDK vs CGLIB；能解释“入口没走代理” |
| 5 | [`spring-core-aop-weaving`](spring-core-modules/spring-core-aop-weaving/README.md) | 2–4h | AspectJ weaving（LTW/CTW）；`call` vs `execution`；constructor/get/set；withincode/cflow；能排障“代理做不到/织入没生效” |
| 6 | [`spring-core-tx`](spring-core-modules/spring-core-tx/README.md) | 2–3h | 事务边界/回滚规则/传播；能解释 checked exception；能定位“自调用导致事务不生效” |
| 7 | [`spring-core-events`](spring-core-modules/spring-core-events/README.md) | 1–2h | 同步/异常传播/@Async/after-commit；能解释回滚时机；能稳定断言异步 |
| 8 | [`spring-core-resources`](spring-core-modules/spring-core-resources/README.md) | 1–2h | `Resource` 抽象/pattern/jar 差异；能区分 handle vs exists；能定位 classpath 路径问题 |

## 实验/练习 总索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战/综合。练习默认 `@Disabled`，按模块 README 的索引逐个开启。
>
> 索引用于按主题检索与查阅。完成一次模块闭环后，再按主题使用索引进行系统补齐。

<details>
<summary>展开：实验/练习索引</summary>

### 配置 / Profiles / 条件装配

- ⭐ `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDefaultLabTest.java`：配置绑定 + 默认 profile
- ⭐ `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsDevLabTest.java`：dev profile 覆盖与 Bean 切换
- ⭐⭐ `spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java`：测试级 property override 优先级
- ⭐⭐ `spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/SpringCoreProfilesLabTest.java`：`@Profile`/`@ConditionalOnProperty` + `ApplicationContextRunner`
- （练习）`spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part00_guide/BootBasicsExerciseTest.java`
- （练习）`spring-core-modules/spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/SpringCoreProfilesExerciseTest.java`

### Web MVC / 错误处理

- ⭐ `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java`：`@WebMvcTest` 切片（更快、更聚焦）
- ⭐ `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcSpringBootLabTest.java`：`@SpringBootTest` 全量上下文（更接近集成）
- ⭐⭐⭐ `spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseLabTest.java`：综合链路（含 Validation/JPA/Tx/Events/AOP）
- （练习）`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`
- （练习）`spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part00_guide/BootBusinessCaseExerciseTest.java`

### 测试切片（Testing）

- ⭐ `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java`：`@WebMvcTest` + `@MockBean`
- ⭐ `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerSpringBootLabTest.java`：`@SpringBootTest(webEnvironment=RANDOM_PORT)`
- ⭐⭐ `spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingMockBeanLabTest.java`：full context 里用 `@MockBean` 覆盖真实 Bean
- （练习）`spring-boot-modules/spring-boot-testing/src/test/java/com/learning/springboot/boottesting/part00_guide/BootTestingExerciseTest.java`

### JPA（Spring Data JPA）

- ⭐⭐ `spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaLabTest.java`：persistence context / flush / dirty checking
- （练习）`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part00_guide/BootDataJpaExerciseTest.java`

### Bean / IoC 容器

- ⭐⭐⭐ `spring-core-modules/spring-core-beans/README.md`：IoC 容器与 Bean 机制总入口（含学习路线、起步入口、完整实验/练习与 container internals 深潜）
- ⭐⭐ `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/SpringCoreBeansLabTest.java`：起步入口（最小闭环，命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLabTest test`）
- ⭐⭐ `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/SpringCoreBeansContainerLabTest.java`：容器机制闭环（定义层/实例层/BFPP/BPP/循环依赖，命令：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test`）
- ⭐⭐ `spring-core-modules/spring-core-beans/docs/guide-deep-dive-guide.md`：断点地图与深挖练习（从一个入口测试走完整主线）
- ⭐⭐ `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/SpringCoreBeansAutoConfigurationImportOrderingLabTest.java`：Boot 自动装配导入/排序主线（after/before 顺序 + 条件可见性）
- ⭐⭐ `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/SpringCoreBeansAutoConfigurationOverrideMatrixLabTest.java`：覆盖/back-off 场景矩阵（NoUnique → 两类修复路径）
- ⭐ `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/SpringCoreBeansBeanDefinitionOriginLabTest.java`：定位 Bean 来源（谁注册的/从哪来/工厂方法 vs 直接类）

### AOP / 代理

- ⭐⭐ `spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/SpringCoreAopLabTest.java`：最小 advice 闭环 + 自调用陷阱
- ⭐⭐⭐ `spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/SpringCoreAopProxyMechanicsLabTest.java`：JDK vs CGLIB、final 限制、`@Order`
- （练习）`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/SpringCoreAopExerciseTest.java`

### 事务（Tx）

- ⭐⭐ `spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/SpringCoreTxLabTest.java`：边界/回滚/传播/模板
- ⭐⭐ `spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/SpringCoreTxSelfInvocationPitfallLabTest.java`：自调用绕过事务最小复现 + 修复对比
- （练习）`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/SpringCoreTxExerciseTest.java`

### 事件（Events）

- ⭐⭐ `spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/SpringCoreEventsLabTest.java`：多监听器/顺序/condition/payload
- ⭐⭐ `spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/SpringCoreEventsMechanicsLabTest.java`：异常传播 + `@Async`
- （练习）`spring-core-modules/spring-core-events/src/test/java/com/learning/springboot/springcoreevents/SpringCoreEventsExerciseTest.java`

### Validation（校验）

- ⭐⭐ `spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/SpringCoreValidationLabTest.java`：程序化校验 + Spring 集成
- ⭐⭐ `spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/SpringCoreValidationMechanicsLabTest.java`：代理差异 + groups + 自定义约束
- （练习）`spring-core-modules/spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/SpringCoreValidationExerciseTest.java`

### Resources（资源）

- ⭐⭐ `spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/SpringCoreResourcesLabTest.java`：读取/模式扫描
- ⭐⭐ `spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/SpringCoreResourcesMechanicsLabTest.java`：handle/exists/description
- （练习）`spring-core-modules/spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/SpringCoreResourcesExerciseTest.java`

### Actuator（可观测性）

- ⭐ `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorLabTest.java`：health/info + 自定义健康检查
- ⭐⭐ `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorExposureOverrideLabTest.java`：端点暴露配置与验证
- （练习）`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part00_guide/BootActuatorExerciseTest.java`

</details>

## 模块目录（Catalog）

该表用于按主题快速定位模块与对应的 `模块目录` 入口。

<details>
<summary>展开：模块目录</summary>

| 模块 | 主题 | 入口说明 |
|---|---|---|
| `spring-boot-basics` | 启动、配置属性、Profiles | [模块目录](spring-boot-modules/spring-boot-basics/README.md) |
| `spring-boot-web-mvc` | REST Controller、Validation、错误处理 | [模块目录](spring-boot-modules/spring-boot-web-mvc/README.md) |
| `spring-boot-security` | Security：认证/授权/CSRF/method security/JWT | [模块目录](spring-boot-modules/spring-boot-security/README.md) |
| `spring-boot-data-jpa` | Spring Data JPA + H2、Entity、Repository、CRUD | [模块目录](spring-boot-modules/spring-boot-data-jpa/README.md) |
| `spring-boot-cache` | 缓存：Spring Cache + Caffeine（key/evict/过期） | [模块目录](spring-boot-modules/spring-boot-cache/README.md) |
| `spring-boot-async-scheduling` | 异步与调度：`@Async`、线程池、`@Scheduled` | [模块目录](spring-boot-modules/spring-boot-async-scheduling/README.md) |
| `spring-boot-actuator` | Actuator 端点 + 自定义健康检查 | [模块目录](spring-boot-modules/spring-boot-actuator/README.md) |
| `spring-boot-web-client` | Web Client：RestClient vs WebClient、超时/重试/错误处理 | [模块目录](spring-boot-modules/spring-boot-web-client/README.md) |
| `spring-boot-testing` | 测试基础：`@SpringBootTest`、`@WebMvcTest` 等 | [模块目录](spring-boot-modules/spring-boot-testing/README.md) |
| `spring-boot-business-case` | Capstone：MVC + Validation + JPA + Tx + Events + AOP | [模块目录](spring-boot-modules/spring-boot-business-case/README.md) |
| `spring-boot-autoconfiguration` | 自动装配：`@AutoConfiguration`/`@ImportAutoConfiguration`/条件装配 | [模块目录](spring-boot-modules/spring-boot-autoconfiguration/README.md) |
| `spring-boot-logging` | 日志：logback 配置、MDC、日志级别与输出 | [模块目录](spring-boot-modules/spring-boot-logging/README.md) |
| `spring-boot-observability` | 可观测性：micrometer/observation/trace/metrics | [模块目录](spring-boot-modules/spring-boot-observability/README.md) |
| `spring-core-beans` | Spring Core：Bean、DI、`@Qualifier`、Scope、生命周期 | [模块目录](spring-core-modules/spring-core-beans/README.md) |
| `spring-core-aop` | Spring AOP：代理、Advice、Pointcut、自调用陷阱 | [模块目录](spring-core-modules/spring-core-aop/README.md) |
| `spring-core-aop-weaving` | AspectJ weaving：LTW/CTW、更多 join point（call/get/set/constructor） | [模块目录](spring-core-modules/spring-core-aop-weaving/README.md) |
| `spring-core-tx` | 事务：`@Transactional` 提交/回滚（JDBC + H2） | [模块目录](spring-core-modules/spring-core-tx/README.md) |
| `spring-core-events` | Spring 事件：发布事件、`@EventListener`、默认同步 | [模块目录](spring-core-modules/spring-core-events/README.md) |
| `spring-core-resources` | 资源：`Resource` 抽象 + classpath pattern | [模块目录](spring-core-modules/spring-core-resources/README.md) |
| `spring-core-profiles` | Profiles/条件装配：`@Profile` + `@ConditionalOnProperty` | [模块目录](spring-core-modules/spring-core-profiles/README.md) |
| `spring-core-validation` | 校验：Bean Validation + Spring 集成 | [模块目录](spring-core-modules/spring-core-validation/README.md) |
| `spring-core-spel` | SpEL：表达式解析/求值、类型转换、并发安全 | [模块目录](spring-core-modules/spring-core-spel/README.md) |

</details>

> 端口说明：`spring-boot-web-mvc` 默认 `8081`，`spring-boot-actuator` 默认 `8082`，`spring-boot-testing` 默认 `8083`，`spring-boot-business-case` 默认 `8084`，`spring-boot-security` 默认 `8085`。非 Web 模块不监听端口。

## 学习路径提示

- 先从 `spring-boot-basics` 开始：理解应用启动、配置加载、Profile 切换。
- 每个模块都尽量保持“可运行、可观察、有测试”，按模块 README 的步骤操作即可。

## 跨模块概念地图（把点连成网）

下面这些“连接线”往往比单个知识点更重要：它们用于解释不同模块中反复出现的同类现象及其共同根因。

- **代理（Proxy）是共同底座**：AOP、事务（`@Transactional`）、方法参数校验（method validation）都依赖代理机制  
  - 从机制角度优先阅读：[AOP 心智模型](spring-core-modules/spring-core-aop/docs/proxy-fundamentals-aop-proxy-mental-model.md)、[`@Transactional` 也是代理](spring-core-modules/spring-core-tx/docs/transaction-basics-transactional-proxy.md)、[方法参数校验也需要代理](spring-core-modules/spring-core-validation/docs/validation-core-method-validation-proxy.md)
- **织入（Weaving）是代理的能力上限之外**：当需要 `call/constructor/get/set/withincode/cflow` 这类 join point，或需要在 Spring Bean 之外进行拦截时，参照 `spring-core-aop-weaving`
  - 对照：[`spring-core-aop-weaving` 深挖指南](spring-core-modules/spring-core-aop-weaving/docs/guide-deep-dive-guide.md)、[Proxy vs Weaving 心智模型](spring-core-modules/spring-core-aop-weaving/docs/mental-model-proxy-vs-weaving.md)
- **自调用（self-invocation）是代理世界第一大坑**：同一个类里用 `this.xxx()` 调用会绕过代理，因此 AOP/Tx 都可能“不生效”  
  - 对照：[AOP 自调用陷阱](spring-core-modules/spring-core-aop/docs/proxy-fundamentals-self-invocation.md)、[Tx 常见坑清单](spring-core-modules/spring-core-tx/docs/appendix-common-pitfalls.md)
- **事务边界决定持久化行为**：JPA 的 flush / dirty checking 不是隐式自动能力，它强依赖事务与 persistence context
  - 对照：[JPA Persistence Context](spring-boot-modules/spring-boot-data-jpa/docs/data-jpa-persistence-context.md)、[事务边界](spring-core-modules/spring-core-tx/docs/transaction-basics-transaction-boundary.md)
- **事件（Events）与事务（Tx）的时机关系**：同步事件 vs after-commit 事件（`@TransactionalEventListener`）能解释“回滚了但监听器为什么执行了/没执行”  
  - 从现象入手：[spring-core-events](spring-core-modules/spring-core-events/README.md)、[`@TransactionalEventListener`（after-commit）](spring-core-modules/spring-core-events/docs/async-and-transactional-transactional-event-listener.md)、[spring-boot-business-case](spring-boot-modules/spring-boot-business-case/README.md)
- **资源（Resources）是很多能力的地基**：配置文件、classpath 扫描、jar/本地差异，最终都会回到 `Resource` 抽象  
  - 对照：[Resource 抽象](spring-core-modules/spring-core-resources/docs/resource-abstraction.md)、[jar vs filesystem](spring-core-modules/spring-core-resources/docs/resource-abstraction-jar-vs-filesystem.md)
- **测试切片 vs 全量上下文**：`@DataJpaTest` / `@WebMvcTest` 用来快速验证机制，`@SpringBootTest` 更接近集成链路  
  - 对照：[spring-boot-testing](spring-boot-modules/spring-boot-testing/README.md)、[`@DataJpaTest` 切片](spring-boot-modules/spring-boot-data-jpa/docs/data-jpa-datajpatest-slice.md)

## 常见问题

- `mvn` 不存在：确认已安装 Maven，并确保 `mvn -v` 可用
- 端口占用：修改模块的 `application.properties` 里的 `server.port`，或运行时追加 `--server.port=9090`
- JDK 版本不对：确认 `java -version` 为 17（或更高）
