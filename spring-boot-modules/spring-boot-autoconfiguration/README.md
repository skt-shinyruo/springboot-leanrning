# spring-boot-autoconfiguration

本模块用“可运行的最小示例 + 可验证的测试实验（Labs / Exercises）”讲透 **Spring Boot 自动配置（Auto-Configuration）**的核心机制：

- 条件装配（`@ConditionalOnProperty/@ConditionalOnClass/@ConditionalOnMissingBean`）
- backoff（用户自定义 bean 覆盖默认配置）
- 顺序与叠加（多个 auto-config 如何组合）

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 [docs/](docs/README.md)。

## Start Here（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：[`docs/README.md`](docs/README.md)
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix/01-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix/02-self-check.md)

完成标准（你应该能解释清楚）：

- 为什么某个 bean “有时存在、有时不存在”（条件装配）
- 为什么用户自己定义 bean 后，auto-config 会 backoff（不再创建默认 bean）
- 为什么“有代理/有拦截器/有基础设施”这类能力，经常都用 auto-config 统一装配

## 关键命令

### 测试

```bash
mvn -pl :spring-boot-autoconfiguration test
```

### 运行

```bash
mvn -pl :spring-boot-autoconfiguration spring-boot:run
```

## Labs / Exercises 索引

> Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 |
| --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationLabTest.java` | 条件装配 + backoff + 顺序叠加（最小闭环） | ⭐⭐ |
| Lab（Perf/Concurrency） | `src/test/java/com/learning/springboot/bootautoconfiguration/part02_perf_concurrency/BootAutoConfigurationConcurrencyLabTest.java` | 并发读取容器产物一致性（Primary/Backoff 的结果不可漂移） | ⭐⭐ |
| Exercise | `src/test/java/com/learning/springboot/bootautoconfiguration/part00_guide/BootAutoConfigurationExerciseTest.java` | 增加 1 个条件分支并固化为断言 | ⭐⭐–⭐⭐⭐ |
