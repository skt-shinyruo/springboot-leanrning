# spring-boot-web-client

本模块用于学习 Web Client（HTTP Client）相关主题：RestClient vs WebClient、错误处理、超时、重试、拦截器/Filter、以及可测试性。

本模块以 tests-first 为主（跑 `*LabTest`），不启动 Web 服务（`spring.main.web-application-type=none`）。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix-self-check.md)

## 关键命令

```bash
mvn -pl :spring-boot-web-client test
```

## 推荐 docs 阅读顺序

（目录：见本 README 的「目录（唯一顺序来源）」）

1. [RestClient 最小闭环](docs/web-client-restclient-basics.md)
2. [WebClient 最小闭环](docs/web-client-webclient-basics.md)
3. [错误处理：4xx/5xx → 异常](docs/web-client-error-handling.md)
4. [超时与重试](docs/web-client-timeout-and-retry.md)
5. [测试策略：MockWebServer](docs/web-client-testing-with-mockwebserver.md)
6. [常见坑清单](docs/appendix-common-pitfalls.md)

## Labs / Exercises 索引

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientRestClientLabTest.java` | RestClient：请求/headers/JSON/错误处理/超时/重试 | ⭐⭐ | 对照 MockWebServer 的 request 断言 |
| Lab | `src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientWebClientLabTest.java` | WebClient：Mono/错误处理/超时/重试/StepVerifier | ⭐⭐ | 对比 reactive vs blocking 的测试体验 |
| Exercise | `src/test/java/com/learning/springboot/bootwebclient/part00_guide/BootWebClientExerciseTest.java` | 扩展错误处理/重试策略/幂等性说明等 | ⭐⭐–⭐⭐⭐ | 从错误体解析开始 |

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Boot Web Client：超时、重试与可测试性

本模块以一个最小 HTTP 调用闭环为起点（RestClient / WebClient），逐步补齐错误处理、超时与重试等真实工程必备的边界，并用 MockWebServer 把行为固定成可回归的断言。关注点不在 API 罗列，而在“调用链在哪里、失败时如何收敛、测试如何写得稳定”。

---

### 10 分钟入口：跑通一次“请求 → 响应”闭环
- `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`

运行后应能回答：一次请求的过滤器/拦截器链条在哪里生效；超时与异常在何处被包装/传播；在测试中如何确定性地复现与断言这些边界。

### 从这里开始（建议顺序）
1. [主线时间线](docs/guide-mainline-timeline.md)
2. [深挖导读](docs/guide-deep-dive-guide.md)

### 顺读主线
- [RestClient](docs/web-client-restclient-basics.md)
- [WebClient](docs/web-client-webclient-basics.md)
- [错误处理](docs/web-client-error-handling.md)
- [超时与重试](docs/web-client-timeout-and-retry.md)
- [MockWebServer 测试](docs/web-client-testing-with-mockwebserver.md)

### 进阶入口（排障/关键分支）
- 断点地图（排障优先）：[04-breakpoint-map.md](docs/guide-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](docs/guide-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](docs/appendix-common-pitfalls.md)
- 自检清单：[02-self-check.md](docs/appendix-self-check.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-web-client -Dtest=*ExerciseSolutionTest test`
- 并发/性能（RestClient 并发请求隔离）：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientRestClientConcurrencyLabTest test`

---

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)
