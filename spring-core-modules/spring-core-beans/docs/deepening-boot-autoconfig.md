# 章节深化路线（Boot Auto-Config）

## 定位：Boot Auto-Config 章节的深化方式

Boot Auto-Config 章节的难点在于“看不见”：导入顺序、条件上下文、BeanDefinition 注册和 back-off 时机都发生在启动阶段。深化时要把这些隐含决策改成可观察事实。


## 官方文档对照（版本语境）

- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）
- Spring Boot：`3.5.9`

- Spring Boot Reference（自动装配）：https://docs.spring.io/spring-boot/reference/using/auto-configuration.html
- Spring Boot Reference（总览）：https://docs.spring.io/spring-boot/reference/
- Spring Framework Reference（Beans）：https://docs.spring.io/spring-framework/reference/core/beans.html


本部分的再加深重点，是把 Boot 的复杂度映射回“定义层、条件、导入、顺序”的可证明链路，并提供可复现反例与排障 SOP。

## 执行化提示（把“看不见的条件”变成“可证明事实”）

- 每个结论都要落到“导入列表 + 条件上下文 + BeanDefinition 注册表”三件套：既能在断点里观察到，也能用 Lab 固化。
- 反例优先：把看似偶发的问题写成顺序、条件或覆盖的可复现分型，避免停留在日志解释。

### 调试与自检：如何“观察到”容器正在做什么

- 文件：`spring-core-modules/spring-core-beans/docs/boot-debugging-and-observability.md`
- 深化落点：
    - `SpringCoreBeansAutoConfigurationLabTest`（再对照 `SpringCoreBeansAutoConfigurationOrderingLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“机制主线”时，把关键入口串成更清晰的主线（例如：ApplicationContext#refresh → org.springframework.context.support.AbstractApplicationContext#refresh），并在关键分支处点明触发条件与结果形态。

### Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？

- 文件：`spring-core-modules/spring-core-beans/docs/boot-auto-configuration-ordering.md`
- 深化落点：
    - `SpringCoreBeansAutoConfigurationOrderingLabTest`（再对照 `SpringCoreBeansAutoConfigurationBackoffTimingLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `AutoConfigurationImportSelector#selectImports` 进，到 `ConditionEvaluator#shouldSkip` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“4. 常见误区（工程里最容易误诊的点）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### Spring Boot 自动装配如何影响 Bean（Auto-configuration）

- 文件：`spring-core-modules/spring-core-beans/docs/boot-auto-configuration-beans.md`
- 深化落点：
    - `SpringCoreBeansAutoConfigurationBackoffTimingLabTest`（再对照 `SpringCoreBeansAutoConfigurationImportOrderingLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“常见误区与边界”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。
