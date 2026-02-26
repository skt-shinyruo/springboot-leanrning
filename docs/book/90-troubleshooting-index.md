# 90 Troubleshooting Index：把问题收敛到“可验证分支”

## 学习目标

- 能把“看起来像随机”的问题收敛成可复现、可断言、可定位的分支。
- 能根据症状快速选到正确的入口：章节 → 模块目录页 → 常见坑/分支矩阵 → Book Matrix。
- 能在不引入新变量的前提下定位根因：先固定现象，再做最小修改验证。

## 概念框架

### 排障主线（五步）

- 1) **分型**：先判断属于配置/容器/代理/请求/数据/线程/安全/观测中的哪一类。
- 2) **复现**：用对应模块的 `*BookMatrixLabTest` 把现象跑成事实（优先用主线入口）。
- 3) **收敛分支**：打开模块文档的“关键分支矩阵/断点地图/常见坑”，把路径压到最短。
- 4) **证据链**：用断点验证关键分支变量（不要只靠日志猜测）。
- 5) **回归**：把修复收敛为可回归断言（回到同一入口再次运行）。

### 症状 → 入口速查

| 症状（先分型） | 先看章节 | 先跑的 Book Matrix | 模块常见坑 |
| --- | --- | --- | --- |
| 配置不生效 / Profile 覆盖异常 | [02 Boot Basics](02-spring-boot-basics.md) | `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test` | [`spring-boot-basics 常见坑`](../../spring-boot-modules/spring-boot-basics/docs/appendix/01-common-pitfalls.md) |
| Bean 找不到 / 注入歧义 / 生命周期与顺序问题 | [03 Beans](03-spring-core-beans.md) | `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test` | [`spring-core-beans 常见坑`](../../spring-core-modules/spring-core-beans/docs/appendix/01-common-pitfalls.md) |
| AOP/事务/校验注解不生效 | [04 AOP](04-spring-core-aop.md) / [05 Tx](05-spring-core-tx.md) / [07 Validation](07-spring-core-validation.md) | 对应模块 Book Matrix | 对应模块常见坑 |
| 400/406/415 / 绑定与错误响应形状 | [06 Web MVC](06-spring-boot-web-mvc.md) | `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test` | [`spring-boot-web-mvc 常见坑`](../../spring-boot-modules/spring-boot-web-mvc/docs/appendix/01-common-pitfalls.md) |
| JPA flush/脏检查/N+1 | [09 Data JPA](09-spring-boot-data-jpa.md) | `mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test` | [`spring-boot-data-jpa 常见坑`](../../spring-boot-modules/spring-boot-data-jpa/docs/appendix/01-common-pitfalls.md) |
| 异步不生效 / 异常消失 / 上下文丢失 | [11 Async](11-spring-boot-async-scheduling.md) | `mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test` | [`async-scheduling 常见坑`](../../spring-boot-modules/spring-boot-async-scheduling/docs/appendix/01-common-pitfalls.md) |
| 缓存命中异常 / key/条件表达式 / 击穿 | [12 Cache](12-spring-boot-cache.md) | `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test` | [`spring-boot-cache 常见坑`](../../spring-boot-modules/spring-boot-cache/docs/appendix/01-common-pitfalls.md) |
| 端点不可访问 / 指标缺失 / 日志级别异常 | [13 Observability](13-observability-and-actuator.md) | Actuator/Observability/Logging 各自 Book Matrix | 对应模块常见坑 |
| 401/403/CSRF / FilterChain 顺序 | [14 Security](14-spring-boot-security.md) | `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test` | [`spring-boot-security 常见坑`](../../spring-boot-modules/spring-boot-security/docs/appendix/01-common-pitfalls.md) |

## 实验入口

遇到问题时，优先按“症状所属模块”运行对应入口（只跑一个主线入口，避免噪声）：

- Boot Basics：`mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBookMatrixLabTest test`
- Beans：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`
- AOP：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`
- Tx：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`
- Web MVC：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
- Validation：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`
- Testing：`mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`
- Data JPA：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`
- Web Client：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`
- Async & Scheduling：`mvn -q -pl :spring-boot-async-scheduling -Dtest=BootAsyncSchedulingBookMatrixLabTest test`
- Cache：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`
- Actuator：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`
- Observability：`mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBookMatrixLabTest test`
- Logging：`mvn -q -pl :spring-boot-logging -Dtest=BootLoggingBookMatrixLabTest test`
- Security：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`

## 常见误区

- 一次跑全仓库，把噪声当成证据。排障优先只跑一个主线入口。
- 先改代码再复现，导致“修复是否有效”不可验证。应先固定现象，再做最小修改。
- 只看日志不分型。多数问题可以先用“分支矩阵”把路径压到最短，再决定断点位置。

## 练习

- 练习 1（分型练习）：
  - 从任意一章的“常见误区”里选一个症状，用本章的“五步排障主线”写出你的最短路径（不写结论，只写动作）。
- 练习 2（证据链练习）：
  - 选择一个你经常遇到的分支（例如 400/事务不生效/异步不生效），在对应模块的断点图中选 3 个锚点断点，记录每个断点要观察的变量。

## 小结

- 排障的目标是把“症状”收敛为“可验证分支”，而不是在日志与猜测之间来回切换。
- 先跑 Book Matrix，再用分支矩阵与断点图缩短路径，最后用同一入口回归验证。

## 延伸阅读

- 全书目录：[`README.md`](README.md)
- 术语表（分型时常用）：[`91-glossary.md`](91-glossary.md)
- 参考资料（版本与官方入口）：[`92-references.md`](92-references.md)

---

[← 上一章](14-spring-boot-security.md) | [目录](README.md) | [下一章 →](91-glossary.md)

