# spring-boot-actuator

本模块用于学习 Spring Boot Actuator 的基础用法：暴露常用端点（例如 `/actuator/health`），以及编写一个自定义 `HealthIndicator`。


## 本模块读法

本模块入口页承担“定位路线”的职责：先把最小实验跑成事实，再沿主线章节解释机制，最后回到排障与自检材料确认边界。

- **先跑入口**：优先使用本页给出的 Book Matrix、Branch Matrix 或最小 Lab，把现象固定成可重复断言。
- **再读主线**：按“主线时间线 → 深挖导读 → 正文主题”的顺序阅读，避免只按文件名零散跳转。
- **最后排障**：遇到问题先回到断点地图、关键分支矩阵、常见坑和自检清单，把问题收敛到章节、断点与测试入口。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`

文档入口：
- 模块目录：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`docs/appendix-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix-self-check.md`](docs/appendix-self-check.md)

## 本模块完成后应能解释的内容

- Actuator 是什么、默认有哪些端点
- 通过配置暴露端点与显示 health 详情
- 自己实现一个健康检查指标并出现在 `/actuator/health` 里

## 前置知识

- 先完成 `spring-boot-basics`（理解配置加载与 profile 切换）
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

输出中应能看到 `components.learning`（或类似字段）出现在 health 输出中。

### 测试

```bash
mvn -pl :spring-boot-actuator test
```

## docs 阅读顺序

（目录：见本 README 的「目录（唯一顺序来源）」）

按“端点现象 → 配置开关 → 自定义指标 → 测试验证”的顺序学习：

1. [导读](docs/guide-deep-dive-guide.md)
2. [Actuator 基础](docs/actuator-basics.md)
3. [常见坑清单](docs/appendix-common-pitfalls.md)
4. [自测题](docs/appendix-self-check.md)

## 实验/练习索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶。练习默认 `@Disabled`，逐个开启。

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
- 只在 `dev` profile 下显示 health details（提示：生产环境不应默认暴露 details）

## 参考

- Spring Boot Actuator

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Boot Actuator：端点、暴露与观测信号

本模块围绕 Actuator 的三个高频问题展开：端点是否存在、端点是否暴露、端点是否可访问。它的目标不是记住端点清单，而是把“为什么看不到/为什么访问不到/为什么指标不变化”这类问题压成可验证的分支，并能在断点里快速定位到决策点。

---

### 10 分钟入口：先把端点跑通
- `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`

运行后应能确认三件事实：端点的注册结果、暴露配置的最终值、访问时的安全与路径边界。

---

### 阅读路线（主线 → 排障 → 自证）
1. 建立主线坐标（章节为何这样排列）
   - [主线时间线](docs/guide-mainline-timeline.md)
   - [深挖导读](docs/guide-deep-dive-guide.md)
2. 顺读正文（把 endpoint 暴露与访问跑通）
   - [Actuator 基础](docs/actuator-basics.md)
3. 遇到问题时回到排障入口
   - [断点地图](docs/guide-breakpoint-map.md)（优先：快速命中关键分支）
   - [关键分支矩阵](docs/guide-branch-decision-matrix.md)（把现象收敛成 If/Then）
   - [常见坑](docs/appendix-common-pitfalls.md) / [自检](docs/appendix-self-check.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`
- Solutions（练习 答案回归）：`mvn -q -pl :spring-boot-actuator -Dtest=*ExerciseSolutionTest test`
- 并发/性能（并发请求驱动 metrics 增量）：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorMetricsConcurrencyLabTest test`

---

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)
