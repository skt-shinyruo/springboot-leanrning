# 技术设计：spring-core-aop 深化（源码级文档 + 可验证闭环）

## 技术方案

### 核心技术

- Java 17
- Spring Boot（由父 POM 管理版本）
- AOP：`spring-boot-starter-aop`（Spring AOP + AspectJ 注解风格）
- 测试：JUnit 5 + AssertJ（`spring-boot-starter-test`）

### 实施要点

1. **以现有 Labs/Exercises 为主干：** 优先把“应该跑哪个测试方法”写进文档，避免创建大量新代码导致维护成本上升。
2. **把 AOP 的事实落到 BPP：** 明确 AOP 的实现路径是 AutoProxyCreator 作为 BeanPostProcessor 在初始化阶段返回 proxy。
3. **固化断点入口与观察点：** 提供“够用版断点清单 + watch list”，让读者能在 30 分钟内跑完一个闭环。
4. **用最小切点验证命中：** 避免 pointcut 写太宽/太窄导致误判机制结论。

## 安全与性能

- **Security:** 无生产环境操作；不引入敏感信息；不接入外部服务。
- **Performance:** 文档改动不影响运行性能；测试保持单模块可快速完成。

## 测试与验证

- `mvn -pl spring-core-aop test`
- Exercise 仍保持 `@Disabled`（学习者自行开启），但文档需要清楚指向“开启后应该验证什么”

