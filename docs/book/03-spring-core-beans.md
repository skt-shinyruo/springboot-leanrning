# 03 Spring Core Beans：IoC 容器、Bean 生命周期与扩展点

## 本章要回答的问题

- 能用“定义阶段 → 创建阶段 → 代理替换阶段”的语言描述容器主线，而不仅是背注解。
- 能区分并解释常见扩展点：`BeanFactoryPostProcessor` / `BeanPostProcessor` / `Aware` / 生命周期回调。
- 能在调试器里找到容器关键锚点：`refresh`、依赖解析、实例化、初始化、代理包裹。

## 主线框架

- **两层对象模型**：
  - `BeanDefinition`：定义层（元数据、依赖、作用域、工厂方法等）。
  - Bean 实例：运行时对象（可能被代理替换）。
- **容器主线（概览）**：
  - `ApplicationContext#refresh`：定义收敛与容器刷新入口。
  - 后处理器链：定义层增强（BFPP）与实例层增强（BPP）。
  - 创建链路：依赖解析 → 实例化 → 属性填充 → 初始化回调 →（可能）代理替换。
- **代理与边界**：
  - AOP/事务/方法校验/方法安全大多依赖代理；代理替换发生在 Bean 生命周期的特定窗口。

本章与后续章节的关系：

- AOP/事务/校验/安全都建立在“Bean 何时被代理替换”的事实之上：
  - 下一章：[04 AOP](04-spring-core-aop.md)
  - 后续：[05 Tx](05-spring-core-tx.md)、[07 Validation](07-spring-core-validation.md)、[14 Security](14-spring-boot-security.md)

## 实验入口

- Book Matrix（主线入口）：
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`
  - 测试类：[`SpringCoreBeansBookMatrixLabTest.java`](../../spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBookMatrixLabTest.java)
- 模块目录页（顺读主线）：
  - [`spring-core-beans/README.md`](../../spring-core-modules/spring-core-beans/README.md)
- 导航型文档（优先用来定位断点）：
  - Why Index（从“为什么”进入）：[`guide-why-index.md`](../../spring-core-modules/spring-core-beans/docs/guide-why-index.md)
  - 30 分钟快启：[`guide-quickstart-30min.md`](../../spring-core-modules/spring-core-beans/docs/guide-quickstart-30min.md)
  - 代理替换阶段（AOP/事务前置）：[`proxying-phase.md`](../../spring-core-modules/spring-core-beans/docs/proxying-phase.md)
  - 常见坑：[`appendix-common-pitfalls.md`](../../spring-core-modules/spring-core-beans/docs/appendix-common-pitfalls.md)

## 常见误区

- 把“Bean”理解为“普通对象 + 依赖注入”。忽略了定义层、后处理器链与代理替换窗口。
- 以为循环依赖都能自动解决。constructor 注入与 setter/字段注入的边界不同，且代理会引入额外复杂度。
- 把 AOP/事务不生效当成“注解没写对”。很多时候根因在 Bean 创建阶段（未被代理/被 early reference 绕过/顺序问题）。

## 验证练习

- 练习 1（把主线跑成可调试证据链）：
  - 运行 `SpringCoreBeansBookMatrixLabTest`；
  - 在 `ApplicationContext#refresh` 处命中断点（以模块断点图为准），观察：
  - BFPP/BPP 注册顺序；
  - 目标 Bean 何时被创建、何时被替换为 proxy。
- 练习 2（为后续章节做准备）：
  - 找到一个“代理相关”的现象入口，把它记录为三段话：
  - 现象（测试断言描述）；
  - 关键方法（断点命中点）；
  - 误区（如果只看注解会怎么判断）。

## 小结

- Beans 章节的验收口径是：能解释容器主线，并能用断点证明“代理替换发生在哪个阶段”。
- AOP/事务/校验/安全的多数问题，本质是“边界与顺序”的问题；下一章开始进入代理主线。

## 延伸阅读

- 下一章（代理心智模型）：[`04-spring-core-aop.md`](04-spring-core-aop.md)
- 资源抽象（资源加载与模式扫描）：[`../../spring-core-modules/spring-core-resources/README.md`](../../spring-core-modules/spring-core-resources/README.md)
- Profiles（条件装配前置概念）：[`../../spring-core-modules/spring-core-profiles/README.md`](../../spring-core-modules/spring-core-profiles/README.md)

---

[← 上一章](02-spring-boot-basics.md) | [目录](README.md) | [下一章 →](04-spring-core-aop.md)
