# Spring Boot Web Client：目录

## 导读

本页是「Spring Boot Web Client：目录」的目录页，建议以“先跑后读”的方式使用：先选一个可运行入口把现象跑通，再按主线章节顺读，把每个结论落到可回归的断言。


> 建议先把一个最小 HTTP 调用闭环跑通（RestClient/WebClient），再补齐错误处理与超时重试，最后用 MockWebServer 测试把行为固定下来。

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
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-boot-web-client -Dtest=*ExerciseSolutionTest test`
- 可跑入口（并发/性能 Lab - RestClient 并发请求隔离）：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientRestClientConcurrencyLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
