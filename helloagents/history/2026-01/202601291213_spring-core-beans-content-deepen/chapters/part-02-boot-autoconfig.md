# 逐章内容级再加深建议（part-02-boot-autoconfig）

本 Part 的再加深重点：把 Boot 的复杂度映射回“定义层/条件/导入/顺序”的可证明链路，并提供可复现反例与排障 SOP。

### 第 19 章：11. 调试与自检：如何“看见”容器正在做什么

- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md`
- 内容级加深策略：
  - A：补“定义层可观测证据链”：如何证明一个 BeanDefinition 是谁注册的、何时注册的、是否被后处理器改写。
  - B：补反例：debug 日志误读、条件评估报告与真实注册行为不一致时的定位方法。
  - C：补排障 SOP：从“容器里没有/有但不是我想要的/被 proxy 了/值不对”四类症状分别如何收敛。
  - D：补观察点：ConditionEvaluationReport、beanDefinition 来源、auto-config import 列表（以及它们的查看方式）。
  - E：补面试追问：Boot 为什么会影响 Bean 图？如何用证据链解释 back-off。

### 09. Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？

- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/020-09-auto-config-ordering.md`
- 内容级加深策略：
  - A：补“顺序影响条件命中”的证据链：导入顺序/条件评估时机/定义是否已存在三者如何交互。
  - B：补反例：@AutoConfigureBefore/After/Order 的边界；同一条件在不同阶段评估导致的“看似偶发”。
  - C：补排障：如何把“偶发失效”归因到顺序、条件、或者定义覆盖/替换。
  - D：补断点：auto-config 导入、条件评估、BeanDefinition 注册关键入口。
  - E：补面试追问：为什么建议把条件写成“可确定性强”的形式？如何解释 matchIfMissing 等三态。

### 第 21 章：10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）

- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md`
- 内容级加深策略：
  - A：补“导入链路证据链”：候选收集→导入→注册 BeanDefinition 的关键链路与最短调用链。
  - B：补反例：用户 bean 顶掉 auto-config / conditionalOnMissingBean 被误判 / FactoryBean+type matching 导致条件误命中。
  - C：补排障：从“bean 没注册/注册了但不是我想要的”到“第一断点入口”的 SOP。
  - D：补 watch list：导入列表、条件上下文、BeanDefinition 注册表的关键对象快照。
  - E：补面试追问：auto-config 的 back-off 与覆盖策略如何解释且可证明。

