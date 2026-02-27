# Spring Boot Security：401/403、FilterChain 与匹配顺序

本模块以一条可运行的 FilterChain 主线为坐标，逐步拆解 401/403 的分支差异、CSRF 的边界、方法安全与代理的关系，以及 JWT 无状态认证的典型链路。安全问题在工程里往往表现为“匹配范围与顺序”：同一份配置在不同路径、不同过滤器顺序下会得到完全不同的结果，因此本模块把这些差异落到断点与断言上。

---

## 10 分钟入口：先跑通一次 401/403 分支

- `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`

运行后应能回答：请求在 FilterChain 中被哪个过滤器拦下；为何同样“未通过认证/授权”会落到不同的响应形状；匹配顺序改变时行为为何会变化。

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

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-security -Dtest=*ExerciseSolutionTest test`
- 并发/性能（SecurityContext 并发隔离）：`mvn -q -pl :spring-boot-security -Dtest=BootSecuritySecurityContextIsolationLabTest test`

---

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
