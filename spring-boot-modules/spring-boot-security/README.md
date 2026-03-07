# spring-boot-security

本模块用于学习 Spring Security 的高频入门与常见机制坑（从 AuthN → AuthZ → CSRF → Method Security → JWT/Stateless）。

本模块的定位是：**用可运行的最小 HTTP 边界 + 可断言的实验测试（Labs/Exercises）**，把“为什么是 401/403、为什么 POST 会被 CSRF 拦、为什么方法注解有时不生效”等现象讲清楚。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix-self-check.md)

## 本模块的学习产出

- 认证（Authentication） vs 授权（Authorization）的区别：**401 vs 403**
- `httpBasic()` 的最小闭环（用户名/密码、角色/权限）
- CSRF 默认行为：为什么 GET 没事但 POST 会 403（以及测试里如何构造 CSRF token）
- Method Security（`@PreAuthorize`）与代理：为什么 **self-invocation** 会绕过安全检查
- 多个 `SecurityFilterChain`：按路径拆链路 + 顺序/匹配规则
- JWT/Stateless 的最小闭环：Bearer token + scope 权限（不依赖外部 IdP）

## 前置知识

- 建议先完成 `spring-boot-web-mvc`（更容易理解“边界校验/错误响应/MockMvc”）
- 基本 HTTP 概念（状态码、header、请求/响应体）

## 关键命令

### 运行

```bash
mvn -pl :spring-boot-security spring-boot:run
```

默认端口：`8085`

### 快速验证（Basic Auth）

- public（无需登录）：

```bash
curl http://localhost:8085/api/public/ping
```

- secure（未登录 → 401）：

```bash
curl http://localhost:8085/api/secure/ping
```

- secure（Basic Auth 登录）：

```bash
curl -u user:password http://localhost:8085/api/secure/ping
```

- admin（普通用户 → 403）：

```bash
curl -u user:password http://localhost:8085/api/admin/ping
```

- admin（管理员 → 200）：

```bash
curl -u admin:password http://localhost:8085/api/admin/ping
```

> CSRF 与 JWT 的细节更建议通过 tests 学（更可控、可断言），见下方 Labs。

### 测试

```bash
mvn -pl :spring-boot-security test
```

## 推荐 docs 阅读顺序（从现象到机制）

（目录：见本 README 的「目录（唯一顺序来源）」）

1. [401 vs 403：Basic Auth 与授权规则](docs/security-basic-auth-and-authorization.md)
2. [CSRF：为什么 POST 会被拦？](docs/security-csrf.md)
3. [Method Security 与代理：self-invocation 陷阱](docs/security-method-security-and-proxy.md)
4. [FilterChain：多链路 + 顺序 + 自定义 Filter](docs/security-filter-chain-and-order.md)
5. [JWT/Stateless：Bearer token + scope](docs/security-jwt-stateless.md)
6. [常见坑清单](docs/appendix-common-pitfalls.md)

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java` | 401/403、Basic Auth、CSRF、JWT、FilterChain、Method Security | ⭐⭐ | 按测试方法逐个跑/断点跟进 |
| Exercise | `src/test/java/com/learning/springboot/bootsecurity/part00_guide/BootSecurityExerciseTest.java` | 自己补充 endpoint/配置/断言，强化机制理解 | ⭐⭐–⭐⭐⭐ | 从第 1 个练习开始 |

## 常见 Debug 路径

- 401 vs 403：先看响应体的 `message` 与路径（本模块统一返回 JSON 错误结构）
- CSRF 403：先确认是不是缺 token（本模块对 CSRF failure 会返回 `csrf_failed`）
- Method Security 没生效：优先怀疑 “没走代理 / self-invocation”
- JWT 授权不匹配：先看 token 的 `scope`，再看 `hasAuthority("SCOPE_xxx")` 的匹配规则

## 参考

- Spring Security Reference
- Spring Security Test（`spring-security-test`）

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页不再提供“上一章/下一章”导航。
> 原 `docs/README.md` 标题：Spring Boot Security：401/403、FilterChain 与匹配顺序

本模块以一条可运行的 FilterChain 主线为坐标，逐步拆解 401/403 的分支差异、CSRF 的边界、方法安全与代理的关系，以及 JWT 无状态认证的典型链路。安全问题在工程里往往表现为“匹配范围与顺序”：同一份配置在不同路径、不同过滤器顺序下会得到完全不同的结果，因此本模块把这些差异落到断点与断言上。

---

### 10 分钟入口：先跑通一次 401/403 分支
- `mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`

运行后应能回答：请求在 FilterChain 中被哪个过滤器拦下；为何同样“未通过认证/授权”会落到不同的响应形状；匹配顺序改变时行为为何会变化。

### 从这里开始（建议顺序）
1. [主线时间线](docs/guide-mainline-timeline.md)
2. [深挖导读](docs/guide-deep-dive-guide.md)

### 顺读主线
- [基础认证与授权](docs/security-basic-auth-and-authorization.md)
- [CSRF](docs/security-csrf.md)
- [方法安全与代理](docs/security-method-security-and-proxy.md)
- [FilterChain 与顺序](docs/security-filter-chain-and-order.md)
- [JWT 无状态](docs/security-jwt-stateless.md)

### 关联模块（按需串联）
- Web 请求主线：`springboot-web-mvc`
- AOP 代理边界：`spring-core-aop`

### 进阶入口（排障/关键分支）
- 断点地图（排障优先）：[04-breakpoint-map.md](docs/guide-breakpoint-map.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](docs/guide-branch-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](docs/appendix-common-pitfalls.md)
- 自检清单：[02-self-check.md](docs/appendix-self-check.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-security -Dtest=BootSecurityBranchMatrixLabTest test`
- Solutions（Exercises 答案回归）：`mvn -q -pl :spring-boot-security -Dtest=*ExerciseSolutionTest test`
- 并发/性能（SecurityContext 并发隔离）：`mvn -q -pl :spring-boot-security -Dtest=BootSecuritySecurityContextIsolationLabTest test`

---

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [自检](docs/appendix-self-check.md)
