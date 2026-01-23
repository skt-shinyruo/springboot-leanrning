# spring-boot-testing

本模块用于学习 Spring Boot 的测试入门：

- `@WebMvcTest`：只加载 Web 层（Controller），更快、更聚焦
- `@SpringBootTest`：加载完整应用上下文，更接近集成测试

## 你将学到什么

- 理解“测试切片（slice test）”的意义
- 会写一个 `@WebMvcTest` + `@MockBean` 的 Controller 测试
- 会写一个 `@SpringBootTest(webEnvironment=RANDOM_PORT)` 的端到端测试
- 理解 `@MockBean` 在 full context 里“覆盖真实 Bean”的效果与边界

## 前置知识

- 建议先完成 `spring-boot-web-mvc`（更容易理解 Controller 测试）
- 了解 JUnit 的基本概念（断言、测试方法）

## 关键命令

### 运行

```bash
mvn -pl :spring-boot-testing spring-boot:run
```

默认端口：`8083`

### 快速验证

```bash
curl 'http://localhost:8083/api/greeting?name=Bob'
```

### 测试

```bash
mvn -pl :spring-boot-testing test
```

## 推荐 docs 阅读顺序

（docs 目录页：[`docs/README.md`](../../docs/testing/spring-boot-testing/README.md)）

1. 导读：`docs/part-00-guide/00-deep-dive-guide.md`
2. Slice vs Full Context：`docs/part-01-testing/01-slice-and-mocking.md`
3. 常见坑清单：`docs/appendix/90-common-pitfalls.md`
4. 自测题：`docs/appendix/99-self-check.md`

对应的可运行实验（先跑后读）：
- `src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java`
- `src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerSpringBootLabTest.java`
- `src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingMockBeanLabTest.java`

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerWebMvcLabTest.java` | `@WebMvcTest` + `@MockBean`（更快、更聚焦） | ⭐ | 对照 controller 入口与 mock 的依赖 |
| Lab | `src/test/java/com/learning/springboot/boottesting/part01_testing/GreetingControllerSpringBootLabTest.java` | `@SpringBootTest(RANDOM_PORT)` 端到端验证 | ⭐ | 体会“真实链路”与启动开销 |
| Lab | `src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingMockBeanLabTest.java` | full context 里 `@MockBean` 覆盖真实 Bean | ⭐⭐ | 分清“替换 Bean”与“只 mock 一层” |
| Exercise | `src/test/java/com/learning/springboot/boottesting/part00_guide/BootTestingExerciseTest.java` | 按提示补充更多 slice/JSON/MockBean 练习 | ⭐–⭐⭐ | 从“增加一个 slice 测试”开始 |

## 常见 Debug 路径

- `@WebMvcTest` 报 Bean 缺失：先确认缺的是不是你应该 mock 的依赖（`@MockBean`）
- `@SpringBootTest` 很慢：优先用 slice test 学机制，再用全量验证集成链路
- `@MockBean` 不生效：确认 mock 的类型是否与真实注入点一致（接口/实现类差异）

## 扩展练习（可选）

- 为 `GreetingService` 增加一个“敏感词过滤”逻辑，并补充相应测试
- 增加一个 `@JsonTest`（或类似切片）来学习 JSON 序列化测试

## 参考

- Spring Boot Testing
