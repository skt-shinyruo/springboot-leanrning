# 12 Spring Boot Cache：命中/回源/回写、并发击穿与过期语义

## 学习目标

- 能跑通缓存读写主线：`@Cacheable` 命中/回源、`@CachePut/@CacheEvict` 写路径与失效。
- 能把 key/condition/unless 的边界跑成可回归规则，避免“线上才发现 key 算错”。
- 能理解并验证并发击穿（stampede）与 `sync=true` 的语义，以及过期与可测试性的关系。

## 概念框架

- **缓存抽象**：
  - 注解 → `CacheInterceptor` → `CacheManager` → `Cache` 实现。
- **读路径**（`@Cacheable`）：
  - 命中：直接返回缓存值；
  - 未命中：回源执行方法 → 写入缓存 → 返回。
- **写路径**（`@CachePut/@CacheEvict`）：
  - 更新与失效的语义通常比读路径更容易出错，需要测试固定行为。
- **边界与并发**：
  - key 计算、条件表达式、并发击穿的保护机制，决定了缓存是否可靠。
- **与 AOP 的关系**：
  - 缓存同样基于代理拦截；self-invocation 可能导致注解不生效（参见 [04 AOP](04-spring-core-aop.md)）。

## 实验入口

- Book Matrix（主线入口）：
  - `mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`
  - 测试类：[`BootCacheBookMatrixLabTest.java`](../../spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheBookMatrixLabTest.java)
- 模块目录页（顺读主线）：
  - [`spring-boot-cache/README.md`](../../spring-boot-modules/spring-boot-cache/README.md)
- 导航型文档（用于定位“拦截器链路/击穿分支”）：
  - Cache 调用链：[`part-00-guide/03-cache-interceptor-call-chain.md`](../../spring-boot-modules/spring-boot-cache/docs/guide-cache-interceptor-call-chain.md)
  - `sync` 与击穿：[`part-01-cache/04-sync-stampede.md`](../../spring-boot-modules/spring-boot-cache/docs/cache-sync-stampede.md)
  - 常见坑：[`appendix/01-common-pitfalls.md`](../../spring-boot-modules/spring-boot-cache/docs/appendix-common-pitfalls.md)

## 常见误区

- 把缓存当成“只加注解就能加速”。缓存是边界语义：key、失效、并发与一致性必须可验证。
- key 计算与条件表达式未经测试。结果：命中率异常、错误共享、或缓存污染。
- 误用 `sync=true`：它保护的是“同 key 的并发回源”，不是通用并发控制。

## 练习

- 练习 1（读写主线闭环）：
  - 运行 `BootCacheBookMatrixLabTest`；
  - 选择一个 `@Cacheable` 场景，写清：命中/回源/写入发生在链路哪一段（对照 Cache 调用链）。
- 练习 2（击穿分支复盘）：
  - 选择一个并发击穿相关入口，记录：
  - 保护前后行为差异（并发下回源次数）；
  - 如何用测试把它固定成确定性结论。

## 小结

- Cache 的学习目标是：把性能边界变成可验证边界，而不是“经验调参”。
- 下一章进入 Observability & Actuator，把“如何看见系统”的入口补齐，便于验证缓存、Web、数据与线程边界的真实行为。

## 延伸阅读

- 下一章（观测与排障入口）：[`13-observability-and-actuator.md`](13-observability-and-actuator.md)
- AOP 代理边界（缓存失效的常见根因之一）：[`04-spring-core-aop.md`](04-spring-core-aop.md)
- Data JPA（缓存与一致性边界常需一起考虑）：[`09-spring-boot-data-jpa.md`](09-spring-boot-data-jpa.md)

---

[← 上一章](11-spring-boot-async-scheduling.md) | [目录](README.md) | [下一章 →](13-observability-and-actuator.md)

