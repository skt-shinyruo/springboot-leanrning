# Spring Boot Web Client：目录

> 建议先把一个最小 HTTP 调用闭环跑通（RestClient/WebClient），再补齐错误处理与超时重试，最后用 MockWebServer 测试把行为固定下来。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/173-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/174-00-deep-dive-guide.md)

## 顺读主线

- [RestClient](part-01-web-client/175-01-restclient-basics.md)
- [WebClient](part-01-web-client/176-02-webclient-basics.md)
- [错误处理](part-01-web-client/177-03-error-handling.md)
- [超时与重试](part-01-web-client/178-04-timeout-and-retry.md)
- [MockWebServer 测试](part-01-web-client/179-05-testing-with-mockwebserver.md)

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[174-02-breakpoint-map.md](part-00-guide/174-02-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[174-04-branch-decision-matrix.md](part-00-guide/174-04-branch-decision-matrix.md)
- 排障 playbook：[180-90-common-pitfalls.md](appendix/180-90-common-pitfalls.md)
- 自检清单：[181-99-self-check.md](appendix/181-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`
- 练习与答案（Exercises/Solutions 约定）：[exercises-and-solutions.md](../../book/exercises-and-solutions.md)
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-boot-web-client -Dtest=*ExerciseSolutionTest test`
- 并发/性能专题（可复现实验范式）：[performance-and-concurrency.md](../../book/performance-and-concurrency.md)
- 可跑入口（并发/性能 Lab - RestClient 并发请求隔离）：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientRestClientConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/180-90-common-pitfalls.md)
- [自检](appendix/181-99-self-check.md)
