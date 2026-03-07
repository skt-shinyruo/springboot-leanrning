# 04 Spring Core AOP：代理、切点与“为什么没走切面”

## 学习目标

- 能建立可调试的代理心智模型：调用从哪里进、Advice 链如何组装、顺序如何决定。
- 能解释并复现最常见的 AOP 失效场景：self-invocation、final 限制、匹配范围与顺序问题。
- 能把 AOP 与后续主题串起来：事务、方法校验、方法安全、异步等都依赖类似的代理边界。

## 概念框架

- **AOP 的运行形态**：大多数 Spring AOP 是运行时代理（JDK 动态代理 / CGLIB），不是编译期织入。
- **三件核心事**：
  - 选谁（Pointcut / 匹配范围）
  - 做什么（Advice / 拦截逻辑）
  - 何时做（Order / 链路顺序）
- **边界**：
  - 代理只能拦截“通过代理对象发起”的调用；
  - self-invocation 绕过代理是多数“注解写了但不生效”的根因之一。

前置关系（强烈建议）：

- AOP 的很多问题根因发生在 Bean 创建阶段（何时被替换为 proxy、early reference 如何参与）：
  - 先回看 [03 Beans](03-spring-core-beans.md) 的“代理替换阶段”入口。

## 实验入口

- Book Matrix（主线入口）：
  - `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`
  - 测试类：[`SpringCoreAopBookMatrixLabTest.java`](../../spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopBookMatrixLabTest.java)
- 模块目录页（顺读主线）：
  - [`spring-core-aop/README.md`](../../spring-core-modules/spring-core-aop/README.md)
- 导航型文档（用于快速定位“代理入口/Advice 链”）：
  - AOP 调用链：[`part-00-guide/03-aop-invocation-call-chain.md`](../../spring-core-modules/spring-core-aop/docs/guide-aop-invocation-call-chain.md)
  - self-invocation：[`part-01-proxy-fundamentals/03-self-invocation.md`](../../spring-core-modules/spring-core-aop/docs/proxy-fundamentals-self-invocation.md)
  - 常见坑：[`appendix/01-common-pitfalls.md`](../../spring-core-modules/spring-core-aop/docs/appendix-common-pitfalls.md)

## 常见误区

- 以为“方法上加了注解就一定会被拦截”。实际需要满足：对象被代理 + 调用经过代理 + 切点匹配。
- 以为 private/final 方法也能被代理一致拦截。JDK 代理只代理接口方法；CGLIB 也会受 final 限制。
- 只看切面代码，不看“代理是怎么生成的、生成在哪个阶段”。排障时优先定位代理生成与链路顺序。

## 练习

- 练习 1（从现象到链路）：
  - 运行 `SpringCoreAopBookMatrixLabTest`；
  - 选择一个“未命中 Advice / 顺序异常”的场景；
  - 对照 AOP 调用链文档，找出：
  - 代理入口点（哪一层对象开始进入拦截链）；
  - Advice 链的组装位置与排序依据。
- 练习 2（为事务/校验/安全做准备）：
  - 用一句话写清：为什么 self-invocation 会绕过 AOP；
  - 并给出“工程级修复方向”（例如：拆分到另一个 Bean、使用事件/异步边界等）。

## 小结

- AOP 的关键不是“会写切面”，而是“能证明代理边界在哪里、顺序怎么决定”。
- 事务、方法校验、方法安全等主题的排障路径高度相似：先确认是否经过代理，再讨论注解参数。

## 延伸阅读

- 下一章（事务拦截器与回滚/传播）：[`05-spring-core-tx.md`](05-spring-core-tx.md)
- Beans（代理替换阶段前置）：[`03-spring-core-beans.md`](03-spring-core-beans.md)
- Validation（方法校验依赖代理）：[`07-spring-core-validation.md`](07-spring-core-validation.md)
- Security（方法安全/FilterChain 与代理）：[`14-spring-boot-security.md`](14-spring-boot-security.md)

---

[← 上一章](03-spring-core-beans.md) | [目录](README.md) | [下一章 →](05-spring-core-tx.md)

