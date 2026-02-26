# 逐章补强建议（part-02-boot-autoconfig Boot 叠加）

本 Part 的补强重点是：把“Boot 把容器搞复杂了”讲成可证明的机制链路（导入、条件、顺序、back-off），并给出调试与排障入口。

### 第 19 章：11. 调试与自检：如何“看见”容器正在做什么

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/01-debugging-and-observability.md`
- 补强策略：
  - 增加“定义层观察入口”：如何在启动期确认 BeanDefinition 是否被注册、由谁注册、条件是否命中（把观察点落到具体入口与变量）。
  - 补充 Boot 侧观测工具链：条件评估报告、debug 日志、（如有）actuator 的 bean/conditions 相关端点与使用姿势。
  - 把“看见容器”落到具体任务：例如定位注入歧义、定位谁把对象换成 proxy、定位占位符从哪里来等，并链接到对应章节。

### 09. Auto-Configuration 顺序：为什么跨 Auto-Config 的条件会“偶发失效”？

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/02-auto-config-ordering.md`
- 补强策略：
  - 将顺序问题拆成“导入顺序 vs 条件评估时机 vs 定义层是否已存在”三个维度，并给出每一维的断点入口。
  - 增补对比：`@AutoConfigureBefore/@AutoConfigureAfter/@AutoConfigureOrder` 等不同手段分别影响哪一层顺序（以及它们的边界）。
  - 增加“最小复现”建议：为读者提供一个可跑的小例子（或者规划新增 Lab）来稳定复现“偶发失效”的本质原因。

### 第 21 章：10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/03-spring-boot-auto-configuration.md`
- 补强策略：
  - 强化“导入链路证据链”：从 auto-configuration 的候选收集、导入、到注册 BeanDefinition 的关键方法链路要能被断点证明。
  - 增补“back-off/conditional”对最终 bean graph 的影响：为什么某个 bean 明明在 classpath 里却没注册、为什么被用户自定义 bean 顶掉。
  - 串联“BeanDefinition 覆盖/候选选择/FactoryBean”章节：把 Boot 的复杂性映射回容器的定义层与实例层主线，避免独立成孤岛。

