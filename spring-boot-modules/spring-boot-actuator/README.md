# spring-boot-actuator

本模块用于学习 Spring Boot Actuator 的基础用法：暴露常用端点（例如 `/actuator/health`），以及编写一个自定义 `HealthIndicator`。

## Start Here（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：[`docs/README.md`](docs/README.md)
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix/01-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix/02-self-check.md)

## 你将学到什么

- Actuator 是什么、默认有哪些端点
- 通过配置暴露端点与显示 health 详情
- 自己实现一个健康检查指标并出现在 `/actuator/health` 里

## 前置知识

- 建议先完成 `spring-boot-basics`（理解配置加载与 profile 切换）
- 了解 HTTP/JSON 的基本概念

## 关键命令

### 运行

```bash
mvn -pl :spring-boot-actuator spring-boot:run
```

默认端口：`8082`

### 快速验证

```bash
curl http://localhost:8082/actuator/health
```

你应该能看到 `components.learning`（或类似字段）出现在 health 输出中。

### 测试

```bash
mvn -pl :spring-boot-actuator test
```

## 推荐 docs 阅读顺序

（docs 目录页：[`docs/README.md`](docs/README.md)）

建议按“端点现象 → 配置开关 → 自定义指标 → 测试验证”的顺序学习：

1. [导读](docs/part-00-guide/02-deep-dive-guide.md)
2. [Actuator 基础](docs/part-01-actuator/01-actuator-basics.md)
3. [常见坑清单](docs/appendix/01-common-pitfalls.md)
4. [自测题](docs/appendix/02-self-check.md)

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶。Exercises 默认 `@Disabled`，建议逐个开启。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorLabTest.java` | health/info 默认行为 + 自定义健康检查 | ⭐ | 先用 curl 看 `/actuator/health` 输出 |
| Lab | `src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorExposureOverrideLabTest.java` | 通过 properties 改变 endpoint exposure 并验证效果 | ⭐⭐ | 回看 exposure 配置与测试断言 |
| Exercise | `src/test/java/com/learning/springboot/bootactuator/part00_guide/BootActuatorExerciseTest.java` | 按提示做“只在 dev 下显示 details/增加开关”等练习 | ⭐–⭐⭐ | 从“show-details 的 profile 差异”开始 |

## 常见 Debug 路径

- 访问不到端点：先检查是否暴露（exposure include/exclude）以及是否有 base-path
- 看不到 health details：检查 `management.endpoint.health.show-details`
- 自定义指标不出现：确认 Bean 是否被注册、命名是否冲突
- 生产环境暴露风险：先分清“对外暴露”与“内部可观测”，再决定 exposure 范围

## 扩展练习（可选）

- 把自定义健康检查改成：当某个配置项关闭时返回 `DOWN`
- 只在 `dev` profile 下显示 health details（提示：生产环境不建议默认暴露 details）

## 参考

- Spring Boot Actuator
