# 技术设计：spring-core-beans 文档深化 + Labs/Exercises（源码级）

## 技术方案

### 核心技术

- Java 17
- Spring Boot（由父 POM 管理版本）
- 测试：JUnit 5 + AssertJ（`spring-boot-starter-test`）

### 实施要点

1. **以“可断言观察点”驱动文档：** 文档中的关键结论必须能被对应 Lab 复现（例如：`resolveDependency` 的候选选择、生命周期回调顺序、`getEarlyBeanReference` 触发点）。
2. **测试优先选择“小容器”：** 优先使用 `AnnotationConfigApplicationContext`/`DefaultListableBeanFactory` 等最小容器构建实验，避免被 Boot 自动装配噪音干扰；在需要 Boot 条件装配时再使用 `ApplicationContextRunner`。
3. **源码断点入口与 watch list 固化：** 对每个主题给出“最短 call chain + 必打断点 + 推荐观察变量”，降低读者的调试摩擦。
4. **文档与代码一致性审计：** 修正文档中对不存在类/测试的引用；新增类/测试时按 README 索引更新链接。

## 安全与性能

- **Security:** 无生产环境操作；不引入敏感信息；不接入外部服务。
- **Performance:** 测试以最小上下文为主，避免 `@SpringBootTest` 全量启动导致耗时与不稳定。

## 测试与验证

- `mvn -pl spring-core-beans test`
- 关键 Labs 必须包含可稳定断言（避免依赖日志时序）
- 构造器循环依赖等“预期失败”用断言验证异常类型与核心信息

