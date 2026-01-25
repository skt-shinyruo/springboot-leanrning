# Spring Boot Security：目录

> 建议先把“FilterChain 主线 + 401/403 分支”跑通，再进入 CSRF、方法安全与 JWT；安全问题大多是“匹配范围与顺序”的问题。

## 从这里开始（建议顺序）

1. [主线时间线](part-00-guide/085-03-mainline-timeline.md)
2. [深挖导读](part-00-guide/086-00-deep-dive-guide.md)

## 顺读主线

- [基础认证与授权](part-01-security/087-01-basic-auth-and-authorization.md)
- [CSRF](part-01-security/088-02-csrf.md)
- [方法安全与代理](part-01-security/089-03-method-security-and-proxy.md)
- [FilterChain 与顺序](part-01-security/090-04-filter-chain-and-order.md)
- [JWT 无状态](part-01-security/091-05-jwt-stateless.md)

## 关联模块（按需串联）

- Web 请求主线：`springboot-web-mvc`
- AOP 代理边界：`spring-core-aop`

## 进阶入口（排障/关键分支）

- 断点地图（排障优先）：[086-02-breakpoint-map.md](part-00-guide/086-02-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[086-04-branch-decision-matrix.md](part-00-guide/086-04-branch-decision-matrix.md)
- 排障 playbook：[092-90-common-pitfalls.md](appendix/092-90-common-pitfalls.md)
- 自检清单：[093-99-self-check.md](appendix/093-99-self-check.md)
- 可跑入口（Book Matrix）：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`
- 可跑入口（Branch Matrix）：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBranchMatrixLabTest test`
- 可跑入口（Solutions - 本模块答案回归）：`mvn -q -pl :spring-boot-security -Dtest=*ExerciseSolutionTest test`
- 可跑入口（并发/性能 Lab - SecurityContext 并发隔离）：`mvn -q -pl :spring-boot-security -Dtest=BootSecuritySecurityContextIsolationLabTest test`

## 排坑与自检

- [常见坑](appendix/092-90-common-pitfalls.md)
- [自检](appendix/093-99-self-check.md)
