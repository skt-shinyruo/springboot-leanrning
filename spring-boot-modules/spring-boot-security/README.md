# spring-boot-security

本模块用于学习 Spring Security 的高频入门与常见机制坑（从 AuthN → AuthZ → CSRF → Method Security → JWT/Stateless）。

本模块的定位是：**用可运行的最小 HTTP 边界 + 可断言的实验测试（Labs/Exercises）**，把“为什么是 401/403、为什么 POST 会被 CSRF 拦、为什么方法注解有时不生效”等现象讲清楚。

## 你将学到什么

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

（docs 目录页：[`docs/README.md`](docs/README.md)）

1. [401 vs 403：Basic Auth 与授权规则](docs/part-01-security/087-01-basic-auth-and-authorization.md)
2. [CSRF：为什么 POST 会被拦？](docs/part-01-security/088-02-csrf.md)
3. [Method Security 与代理：self-invocation 陷阱](docs/part-01-security/089-03-method-security-and-proxy.md)
4. [FilterChain：多链路 + 顺序 + 自定义 Filter](docs/part-01-security/090-04-filter-chain-and-order.md)
5. [JWT/Stateless：Bearer token + scope](docs/part-01-security/091-05-jwt-stateless.md)
6. [常见坑清单](docs/appendix/092-90-common-pitfalls.md)

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
