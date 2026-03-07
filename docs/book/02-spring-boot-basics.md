# 02 Spring Boot Basics：启动与配置，把“最终值”跑成事实

当一个 Spring Boot 工程“看起来写了配置但没生效”时，问题往往不在语法，而在事实来源：同一个 key 可能同时出现在默认配置、profile 配置、环境变量、命令行参数、测试覆盖里。读者如果只盯着某个 `application.yml` 文件，很容易把原因猜错。

本章围绕一个核心问题展开：**运行时的最终配置值到底来自哪里**，以及它如何影响后续的 Bean 装配与应用行为。主线只把证据链与关键概念串起来；机制细节与断点地图放在模块文档中展开。

---

## 实验：先把“最终值”钉住

- 运行：
  - `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- 测试类（入口锚点）：
  - [`BootBasicsBookMatrixLabTest.java`](../../spring-boot-modules/spring-boot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsBookMatrixLabTest.java)
- 模块目录页（正文入口）：
  - [`spring-boot-basics/README.md`](../../spring-boot-modules/spring-boot-basics/README.md)

运行后应当能回答两件“事实问题”（先不解释原因）：

1. 激活的 profile 是什么（`Environment#getActiveProfiles()`）；
2. 目标 key 的最终值是什么（`Environment#getProperty(...)`）。

这两条事实确定后，再回到文档解释“谁覆盖谁”，读者会更容易把分支收敛到具体的可验证入口。

---

## 解释：Boot 启动与配置的三件事

### 1) 启动主线并不神秘：关键分界点在 Environment 与 Context

从 `SpringApplication#run` 到容器就绪，可以粗略划分为两个阶段：

- **Environment 准备阶段**：收集并合并配置来源，计算出“最终值”；
- **ApplicationContext 刷新阶段**：根据最终配置与条件装配结果注册 BeanDefinition、创建 bean、触发后处理器与生命周期回调。

这也是为什么配置问题经常出现在“启动早期”：很多决策在容器真正创建 bean 之前就已经完成了。

### 2) PropertySources：多个来源合并成一个“最终视图”

配置不是“某个文件生效”，而是多个来源共同参与决策。最常见的来源包括：

- 默认配置文件（`application.properties/yml`）
- profile 配置文件（`application-<profile>.properties/yml`）
- 环境变量 / 命令行参数
- 测试覆盖（例如 `@SpringBootTest(properties = ...)`）

这些来源最终都会体现在 `Environment` 的 `PropertySources` 里。排障时更可靠的做法不是背优先级表，而是直接观察：同名 key 在运行时究竟命中了哪个来源。

### 3) Profiles 与绑定：一个控制“参与者”，一个控制“形态”

在实际工程中，Profiles 常常同时影响两类结果：

- **哪些配置片段参与合并**（例如 dev 配置文件是否参与）
- **哪些 Bean 会被注册**（例如 `@Profile("dev")` 的实现是否存在）

而 `@ConfigurationProperties` 的职责是把字符串配置变成类型安全对象：它决定了“配置值以什么形态进入业务代码”，也决定了“转换失败/缺失字段/默认值”如何在测试中被断言出来。

---

## 边界：三个高频误判（以及如何快速验证）

**误判一：只要写进 `application.yml` 就一定生效。**
验证方式是回到 `Environment`：先看 active profiles，再看最终属性值。若最终值不符合预期，问题属于“来源与优先级”；若最终值正确但行为不对，问题更可能属于“条件装配/Bean 注册”。

**误判二：`@Value` 与 `@ConfigurationProperties` 可以互换。**
两者都能读配置，但“可测试性与边界”不同。`@ConfigurationProperties` 更容易形成稳定断言（类型转换、默认值、校验），也更适合作为配置的长期形态。

**误判三：只看日志、不下断点。**
配置决策发生在启动早期，日志往往不告诉“这个值来自哪里”。在模块文档的断点地图里，通常能直接命中“最终取值点”，从而快速反推来源。

---

## 小结与下一章

- 本章的核心产出是：能把“最终配置值来自哪里”跑成事实，并据此把问题分型为“值不对”还是“装配不对”。
- 配置的后果最终体现在 Bean 装配与代理边界；下一章进入容器主线：[`03-spring-core-beans.md`](03-spring-core-beans.md)。

---

[← 上一章](01-getting-started.md) | [目录](README.md) | [下一章 →](03-spring-core-beans.md)
