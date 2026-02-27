# Spring Boot Web Client：超时、重试与可测试性

本模块以一个最小 HTTP 调用闭环为起点（RestClient / WebClient），逐步补齐错误处理、超时与重试等真实工程必备的边界，并用 MockWebServer 把行为固定成可回归的断言。关注点不在 API 罗列，而在“调用链在哪里、失败时如何收敛、测试如何写得稳定”。

---

## 10 分钟入口：跑通一次“请求 → 响应”闭环

- `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`

运行后应能回答：一次请求的过滤器/拦截器链条在哪里生效；超时与异常在何处被包装/传播；在测试中如何确定性地复现与断言这些边界。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)

## 顺读主线

- [RestClient](part-01-web-client/01-restclient-basics.md)
- [WebClient](part-01-web-client/02-webclient-basics.md)
- [错误处理](part-01-web-client/03-error-handling.md)
- [超时与重试](part-01-web-client/04-timeout-and-retry.md)
- [MockWebServer 测试](part-01-web-client/05-testing-with-mockwebserver.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[04-breakpoint-map.md](part-00-guide/04-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](part-00-guide/05-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-web-client -Dtest=*ExerciseSolutionTest test`
- 并发/性能（RestClient 并发请求隔离）：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientRestClientConcurrencyLabTest test`

---

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
