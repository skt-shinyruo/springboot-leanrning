# 逐章内容级再加深建议（part-02-boot-autoconfig）

## 官方文档对照（版本语境）

- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）
- Spring Boot：`3.5.9`

- Spring Boot Reference（自动装配）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html
- Spring Boot Reference（总览）：https://docs.spring.io/spring-boot/reference/
- Spring Framework Reference（Beans）：https://docs.spring.io/spring-framework/reference/core/beans.html


本 Part 的再加深重点：把 Boot 的复杂度映射回“定义层/条件/导入/顺序”的可证明链路，并提供可复现反例与排障 SOP。

## 执行化提示（把“看不见的条件”变成“可证明事实”）

- 每个结论都要落到“导入列表 + 条件上下文 + BeanDefinition 注册表”三件套：能在断点里观察到，且能用 Lab 固化。
- 反例优先：把“看似偶发”写成“顺序/条件/覆盖”的可复现分型，避免停留在日志解释。

### 第 19 章：11. 调试与自检：如何“观察到”容器正在做什么

- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md`
- 继续加深建议：
    - `SpringCoreBeansAutoConfigurationLabTest`（再对照 `SpringCoreBeansAutoConfigurationOrderingLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“机制主线”时，建议把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。

### 09. Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？

- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/020-09-auto-config-ordering.md`
- 继续加深建议：
    - `SpringCoreBeansAutoConfigurationOrderingLabTest`（再对照 `SpringCoreBeansAutoConfigurationBackoffTimingLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `AutoConfigurationImportSelector#selectImports` 进，到 `ConditionEvaluator#shouldSkip` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“4. 常见误区（工程里最容易误诊的点）”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。

### 第 21 章：10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）

- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md`
- 继续加深建议：
    - `SpringCoreBeansAutoConfigurationBackoffTimingLabTest`（再对照 `SpringCoreBeansAutoConfigurationImportOrderingLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
    - 针对“常见误区与边界”时，建议把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
