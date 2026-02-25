# Spring Boot Security：目录

## 导读

本页是「Spring Boot Security：目录」的目录页，建议以“先跑后读”的方式使用：先选一个可运行入口把现象跑通，再按主线章节顺读，把每个结论落到可回归的断言。


> 建议先把“FilterChain 主线 + 401/403 分支”跑通，再进入 CSRF、方法安全与 JWT；安全问题大多是“匹配范围与顺序”的问题。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/01-mainline-timeline.md)
2. [深挖导读](part-00-guide/02-deep-dive-guide.md)

## 顺读主线

- [基础认证与授权](part-01-security/01-basic-auth-and-authorization.md)
- [CSRF](part-01-security/02-csrf.md)
- [方法安全与代理](part-01-security/03-method-security-and-proxy.md)
- [FilterChain 与顺序](part-01-security/04-filter-chain-and-order.md)
- [JWT 无状态](part-01-security/05-jwt-stateless.md)

## 关联模块（按需串联）

- Web 请求主线：`springboot-web-mvc`
- AOP 代理边界：`spring-core-aop`

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[04-breakpoint-map.md](part-00-guide/04-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](part-00-guide/05-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](appendix/01-common-pitfalls.md)
- 自检清单：[02-self-check.md](appendix/02-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBranchMatrixLabTest test`
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-boot-security -Dtest=*ExerciseSolutionTest test`
- 可跑入口（并发/性能 Lab - SecurityContext 并发隔离）：`mvn -q -pl :spring-boot-security -Dtest=BootSecuritySecurityContextIsolationLabTest test`

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
