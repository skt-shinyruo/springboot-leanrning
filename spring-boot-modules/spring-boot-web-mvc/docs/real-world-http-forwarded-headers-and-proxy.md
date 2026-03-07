# 06. 反向代理与 Forwarded Headers（X-Forwarded-*：scheme/host/prefix/ip 的真实边界）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（转发头 → request 语义 → 排障断点）"
    - 适用场景：应用部署在反向代理/LB 后（TLS 终止、域名/端口/前缀改写），但应用看到的 `HttpServletRequest` 仍是“代理到容器的那一跳”
    - Boot 开关：`server.forward-headers-strategy`（`NATIVE` / `FRAMEWORK` / `NONE`，默认 `NONE`）
    - 框架入口（FRAMEWORK）：`org.springframework.web.filter.ForwardedHeaderFilter#doFilterInternal`
    - 常见现象：重定向/回调地址/绝对链接 scheme 不对（http/https）、host/port 不对、前缀丢失、client IP 被伪造
    - 可运行证据：`BootWebMvcInternalsLabTest`（`X-Forwarded-For` → `@ClientIp`）+ `BootWebMvcForwardedHeadersSpringBootLabTest`（scheme/host/prefix/remoteAddr 的断言）

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. 条件请求（Last-Modified / If-Modified-Since / ETag / ShallowEtagHeaderFilter）](real-world-http-conditional-requests-last-modified-etag-filter.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. WebMvc 测试与排障（resolvedException / handler / 断点清单）](testing-observability-webmvc-testing-and-troubleshooting.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章补齐一个“在本地跑没问题、上了生产就怪了”的高频场景：**应用在反向代理/负载均衡后运行时，`HttpServletRequest` 看到的 scheme/host/port/contextPath 可能都不是“用户真正访问的那一层”**。

典型现象包括：

- 你在代码里拼的绝对 URL（回调地址、重定向、下载链接）突然从 `https://` 变成 `http://`
- 生成的 host/port 不对（例如变成内部域名/内网端口）
- 前缀丢失：实际部署在 `https://example.com/app/**`，但应用把自己当成 `/**`（导致 404、静态资源路径错、跳转路径错）
- 你用 `X-Forwarded-For` 取 client IP，但忘了“信任边界”，导致外网可伪造 IP

这类问题的本质不是“Spring MVC 选路/参数解析有 bug”，而是 **反向代理把“真实访问语义”放进了转发头（Forwarded/X-Forwarded-*），而应用没有（或不应该）信任它们**。

---

## 最重要的三个概念（先把边界立住）

### 1) 代理链路里有两套“地址语义”

- **用户看到的地址语义**：浏览器访问的 `https://example.com/app/api/...`
- **容器看到的地址语义**：代理转发到应用的 `http://127.0.0.1:8080/api/...`

Spring MVC 的很多行为（绝对 URL、重定向、`ServletUriComponentsBuilder`、`request.getScheme()` 等）默认只基于“容器那一跳”。

### 2) 转发头（Forwarded / X-Forwarded-*）是“把真实语义带回来”的手段

常见头（不同代理可能用不同组合）：

- `X-Forwarded-Proto`: `https`
- `X-Forwarded-Host`: `example.com`
- `X-Forwarded-Port`: `443`
- `X-Forwarded-Prefix`: `/app`
- `X-Forwarded-For`: `1.2.3.4, 5.6.7.8`（左侧通常是“最外层 client”，右侧是代理链）

### 3) 信任边界：这些头 **不是** “谁都能随便发”

如果应用直接暴露在公网，而你又开启了对转发头的处理，那么攻击者可以伪造：

- client IP（风控/日志/限流误判）
- scheme/host（生成的回调链接可被污染）

因此：**只在“请求一定会先经过可信代理”的部署拓扑下开启转发头处理**。

---

## Spring Boot 侧：该用哪个开关

Spring Boot `3.5.9` 提供统一开关：

- `server.forward-headers-strategy`
  - `NONE`：忽略转发头（默认）
  - `FRAMEWORK`：使用 Spring 的 `ForwardedHeaderFilter` 处理转发头
  - `NATIVE`：使用容器原生能力（例如 Tomcat 的 RemoteIpValve）

历史属性 `server.use-forward-headers` 已被替代为 `server.forward-headers-strategy`（新项目不建议再用旧属性）。

---

## Spring MVC 侧：你该打哪些断点（把“语义变更”看成事实）

当你怀疑“scheme/host/prefix 不对”时，优先把断点放在“语义被改写”的位置，而不是 controller：

- 入口：`org.springframework.web.filter.ForwardedHeaderFilter#doFilterInternal`
  - 观察：它如何基于 `Forwarded`/`X-Forwarded-*` 包装 request
- 使用点：业务/框架在取地址信息时的断点
  - `jakarta.servlet.http.HttpServletRequest#getScheme/getServerName/getServerPort/getContextPath`
  - （如果你在业务里生成链接）`org.springframework.web.servlet.view.RedirectView#renderMergedOutputModel`

核心目标是固定一个事实：**进入 MVC 前后，`request` 的 scheme/host/port/contextPath 到底是什么**。

---

## 证据链（本仓库里能直接跑的入口）

### 1) `X-Forwarded-For`（client IP）在本模块的最小可运行证据

本模块已经把 “从转发头取 client IP” 落成了可回归用例：

- Lab：`BootWebMvcInternalsLabTest`
  - `resolvesClientIpFromXForwardedForFirstValue`
  - `fallsBackToRemoteAddrWhenXForwardedForIsMissing`

对应实现：

- 参数注解：`@ClientIp`
- resolver：`ClientIpArgumentResolver`（优先 `X-Forwarded-For`，否则回退 `request.getRemoteAddr()`）

### 2) scheme/host/prefix 的证据链建议（带断点/日志跑一遍）

本仓库提供了一个“可回归”的最小证据链，把 `X-Forwarded-Proto/Host/Port/Prefix/For` 对 `HttpServletRequest` 语义的影响固定成断言：

- Lab：`BootWebMvcForwardedHeadersSpringBootLabTest`
  - 配置：`server.forward-headers-strategy=framework`
  - 请求头：`X-Forwarded-Proto/Host/Port/Prefix/For`
  - 断言：`scheme/serverName/serverPort/contextPath/requestUri/requestUrl/remoteAddr`

建议运行命令（先跑再断点）：

```bash
mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcForwardedHeadersSpringBootLabTest test
```

此外，在排障/学习时也建议用“断点 + curl”把语义变化看成事实：

1. 开启 `server.forward-headers-strategy=FRAMEWORK`（或在容器层启用 `NATIVE`）
2. 在 `ForwardedHeaderFilter#doFilterInternal` 打断点
3. 用 `curl` 模拟代理转发头访问任意 endpoint（关键在于断点里观察 request 的语义变化）：

```bash
curl -i http://localhost:8081/api/ping \
  -H 'X-Forwarded-Proto: https' \
  -H 'X-Forwarded-Host: example.com' \
  -H 'X-Forwarded-Port: 443' \
  -H 'X-Forwarded-Prefix: /app'
```

---

## 常见坑与边界（把“开关”变成工程决策）

- **坑 1：在公网直连时开启转发头处理**
  - 结果：攻击者能直接伪造 `X-Forwarded-*`
  - 原则：只在“上游一定是可信代理”的拓扑里启用，并让应用只接受来自代理网段的请求

- **坑 2：只处理 `X-Forwarded-For`，忽略 scheme/host/prefix**
  - 结果：日志里 IP 看着对，但生成的回调/重定向仍然错
  - 处理：需要明确你在业务里是否生成绝对链接；如果生成，就需要处理 scheme/host/port/prefix 的一致性

- **坑 3：把 `X-Forwarded-For` 当成“永远可信”**
  - 结果：风控/审计误判
  - 处理：生产环境通常需要“可信代理列表”+ “只取特定段位的 IP”（本模块的实现是教学版：取第一个）

---

## 小结与下一章

- 反向代理场景下，先把 `HttpServletRequest` 的语义固定下来（scheme/host/port/prefix），再讨论 MVC 的选路/异常/协商分支。
- 下一章回到测试与排障：把“现象 → 分支 → 断点”变成固定套路。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcInternalsLabTest`
- Lab：`BootWebMvcForwardedHeadersSpringBootLabTest`

上一章：[05. 条件请求（Last-Modified / If-Modified-Since / ETag / ShallowEtagHeaderFilter）](real-world-http-conditional-requests-last-modified-etag-filter.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. WebMvc 测试与排障（resolvedException / handler / 断点清单）](testing-observability-webmvc-testing-and-troubleshooting.md)
<!-- BOOKIFY:END -->
