# 91 Glossary：术语与边界（用于复盘与排障分型）

## 学习目标

- 能用一致的术语描述问题，减少“同词不同义”导致的误判。
- 能把术语落到可验证入口：对应章节 + 对应模块的 Book Matrix。
- 能在排障时快速区分相近概念（例如 flush vs commit、401 vs 403、Advice vs Aspect）。

## 概念框架

术语按“问题分型”分组（建议结合 [90 Troubleshooting Index](90-troubleshooting-index.md) 使用）：

- 配置与启动：Environment、PropertySource、Profile、Binding
- 容器与装配：BeanDefinition、BFPP/BPP、生命周期、依赖解析、代理替换窗口
- 代理与拦截：Proxy、Pointcut、Advice、Interceptor、self-invocation、Order
- 数据与事务：Persistence Context、flush、dirty checking、Propagation、rollback rule
- Web：DispatcherServlet、HandlerMapping、MessageConverter、406/415
- 测试：slice、TestContext cache、`@MockBean`
- 观测与安全：Actuator exposure、Observation/Meter、MDC、FilterChain、401/403、CSRF

## 实验入口

术语要落地，优先使用以下入口把概念跑成事实：

- Beans：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`
- AOP：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`
- Tx：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`
- Web MVC：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`

## 常见误区

- 用一个词解释所有问题：例如把“没生效”统一归因到“注解没写对”。应先分型：是否经过代理、是否匹配范围、是否顺序问题。
- 把“看到的结果”当成“数据库事实”或“线程事实”。应先确认上下文边界（持久化上下文/线程池/代理）。
- 混淆 401 与 403，导致在错误层面排查配置。应先分型：认证 vs 授权 vs CSRF。

## 练习

- 练习 1（术语对齐）：
  - 从下方术语表任取 10 个词，为每个词写一句“可验证定义”（避免引用外部百科式表述）。
- 练习 2（术语 → 入口）：
  - 为每个词补一条入口链接：对应章节（本书）+ 对应 Book Matrix（模块）。

## 小结

- 术语的价值是减少歧义：先用一致语言描述边界，再用测试与断点证明。
- 排障时优先用术语做分型，再回到对应章节与模块文档收敛证据链入口。

## 延伸阅读

- Troubleshooting Index：[`90-troubleshooting-index.md`](90-troubleshooting-index.md)
- 全书目录：[`README.md`](README.md)
- 参考资料：[`92-references.md`](92-references.md)

---

## 术语表（按主题分组）

### 配置与启动

- **Environment**：运行时配置的统一访问入口；最终值来自多个 PropertySource 的优先级决策（见 [02](02-spring-boot-basics.md)）。
- **PropertySource**：配置来源的抽象（文件/环境变量/命令行等）；多个来源参与覆盖规则（见 [02](02-spring-boot-basics.md)）。
- **Profile**：控制配置片段与 Bean 条件生效的开关；常与覆盖规则一起出现（见 [02](02-spring-boot-basics.md)）。
- **`@ConfigurationProperties` Binding**：将字符串配置绑定为类型安全对象，包含转换与默认值边界（见 [02](02-spring-boot-basics.md)）。

### 容器与装配

- **BeanDefinition**：Bean 的定义层元数据；决定创建方式、作用域、依赖等（见 [03](03-spring-core-beans.md)）。
- **BeanFactoryPostProcessor（BFPP）**：在实例创建前增强/修改定义层（见 [03](03-spring-core-beans.md)）。
- **BeanPostProcessor（BPP）**：在实例创建过程中增强/替换对象（代理替换通常在这一链路出现）（见 [03](03-spring-core-beans.md)）。
- **生命周期回调**：初始化/销毁等回调点；常与顺序问题耦合（见 [03](03-spring-core-beans.md)）。
- **循环依赖**：依赖图存在环；不同注入方式与代理会改变可解性边界（见 [03](03-spring-core-beans.md)）。

### 代理与拦截（AOP 家族）

- **Proxy（代理）**：运行时替换真实对象的包装层；多数拦截型能力基于代理实现（见 [04](04-spring-core-aop.md)）。
- **Pointcut（切点）**：决定“选谁”的匹配规则（见 [04](04-spring-core-aop.md)）。
- **Advice**：决定“做什么”的拦截逻辑（见 [04](04-spring-core-aop.md)）。
- **Interceptor 链**：多个 Advice 的组合执行链；顺序决定行为差异（见 [04](04-spring-core-aop.md)）。
- **self-invocation**：同一类内部调用绕过代理的典型场景，是“注解不生效”的高频根因（见 [04](04-spring-core-aop.md)、[05](05-spring-core-tx.md)、[11](11-spring-boot-async-scheduling.md)）。

### 事务与数据

- **事务边界**：开启/提交/回滚发生的位置（通常由拦截器织入）（见 [05](05-spring-core-tx.md)）。
- **Propagation（传播行为）**：嵌套调用时边界如何组合（见 [05](05-spring-core-tx.md)）。
- **rollback rule（回滚规则）**：异常类型与规则共同决定回滚行为（见 [05](05-spring-core-tx.md)）。
- **Persistence Context（持久化上下文）**：JPA 的一级缓存与变更跟踪边界，影响可见性与写入时机（见 [09](09-spring-boot-data-jpa.md)）。
- **flush**：SQL 发送时机；不等于 commit（见 [09](09-spring-boot-data-jpa.md)）。
- **dirty checking**：变更检测并生成更新 SQL 的机制（见 [09](09-spring-boot-data-jpa.md)）。
- **N+1**：关联加载导致的额外查询问题，需要结合 fetching 与访问路径复现（见 [09](09-spring-boot-data-jpa.md)）。

### Web

- **DispatcherServlet**：Spring MVC 请求主线入口（见 [06](06-spring-boot-web-mvc.md)）。
- **HandlerMapping/HandlerAdapter**：选路与执行的核心组件；常与 404/方法匹配分支相关（见 [06](06-spring-boot-web-mvc.md)）。
- **MessageConverter**：请求体/响应体转换；常与 406/415 分支相关（见 [06](06-spring-boot-web-mvc.md)）。

### 测试

- **slice（切片）**：只加载目标相关上下文以固定边界（见 [08](08-spring-boot-testing.md)）。
- **TestContext cache**：上下文复用机制；可能引入状态污染与偶发问题（见 [08](08-spring-boot-testing.md)）。
- **`@MockBean`**：在上下文中替换真实 Bean 的测试手段；需明确替换边界与顺序（见 [08](08-spring-boot-testing.md)）。

### 观测与安全

- **Actuator exposure（端点暴露）**：端点是否对外可见由配置/安全/路径组合决定（见 [13](13-observability-and-actuator.md)）。
- **Observation/Meter（观测/指标）**：请求主线产生观测信号的抽象；用于趋势与告警（见 [13](13-observability-and-actuator.md)）。
- **MDC**：日志上下文关联；跨线程需要显式传播（见 [13](13-observability-and-actuator.md)、[11](11-spring-boot-async-scheduling.md)）。
- **SecurityFilterChain**：安全过滤器链；匹配范围与顺序决定行为（见 [14](14-spring-boot-security.md)）。
- **401 vs 403**：未认证 vs 已认证但无权限（或被策略拒绝）；先分型再排障（见 [14](14-spring-boot-security.md)）。
- **CSRF**：跨站请求伪造防护策略；对写请求引入额外分支（见 [14](14-spring-boot-security.md)）。

---

[← 上一章](90-troubleshooting-index.md) | [目录](README.md) | [下一章 →](92-references.md)

