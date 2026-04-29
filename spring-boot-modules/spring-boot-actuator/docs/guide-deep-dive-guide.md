# 深挖导读：Spring Boot Actuator
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章用于把模块主线、源码入口与断点路径串起来，主线可以概括为：引入 Actuator → 端点注册与 discover → exposure 决定暴露 → Web 层映射为 HTTP 端点 → 结合安全策略与可观测信号使用。

    阅读时可以先跑 `BootActuatorExposureOverrideLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 Actuator endpoints 暴露健康检查/信息/指标；用 exposure 控制可见范围，并在生产环境结合鉴权与安全边界。

    需要下探源码时，可以从 `org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration` / `org.springframework.boot.actuate.endpoint.annotation.Endpoint` / `org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：Spring Boot Actuator](guide-mainline-timeline.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 01 - Actuator 基础与暴露](actuator-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`

## 机制主线

这一模块用“三段式分流”建立稳定心智模型：

1. **端点是否存在（Registered）**：端点有没有被注册出来（通常对应一个 endpoint bean）
2. **端点是否暴露（Exposed）**：端点有没有被映射到 HTTP（取决于 base-path、include/exclude、web/management 配置）
3. **端点是否可访问（Accessible）**：端点有没有被安全策略/网络边界允许访问（401/403/404 的分流）

只要把这三段分清楚，就能把“表面上像 Actuator 坏了”的问题拆成可验证的子问题，而不是靠猜配置。

### 1) 时间线：从启动到可访问

把一次最常见的 Actuator 启动与访问链路按时间线拆开：

1. **启动阶段：端点注册**
   - 自动配置创建并注册内置端点（如 `HealthEndpoint` / `InfoEndpoint` / `EnvironmentEndpoint`）。
2. **启动阶段：端点发现与暴露策略计算**
   - 根据 `management.endpoints.web.exposure.include/exclude` 计算“暴露集合”。
3. **启动阶段：HTTP 映射建立**
   - 由 HandlerMapping 把“暴露集合”映射到 `/actuator/**`（或自定义 base-path）。
4. **运行阶段：请求进来**
   - 先经过安全过滤链（如果开启）→ 再进入 Actuator handler → 返回 JSON。

### 2) 关键参与者（应当能点名并解释它们做什么）

- **配置入口（改的东西）**
  - `management.endpoints.web.base-path`
  - `management.endpoints.web.exposure.include` / `management.endpoints.web.exposure.exclude`
- **HTTP 层（决定“有没有路由”）**
  - `org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping`
  - `org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver`（用于 `/actuator` 根路径的 links 输出）
- **端点本体（决定“返回什么”）**
  - `org.springframework.boot.actuate.health.HealthEndpoint` / 自定义 HealthIndicator
  - `org.springframework.boot.actuate.info.InfoEndpoint`
  - `org.springframework.boot.actuate.env.EnvironmentEndpoint`（常见高敏感端点，默认不暴露）
- **安全边界（决定“能不能访问”）**
  - Spring Security 的 `SecurityFilterChain`（如果引入了 security starter）

### 3) 本模块的关键分支（2–5 条，默认可回归）

1. **默认暴露策略：env 端点默认不可访问（404）**
   - 验证：`BootActuatorLabTest#envEndpointIsNotExposedByDefault`
2. **include 覆盖：显式暴露 env 后由 404 → 200**
   - 验证：`BootActuatorExposureOverrideLabTest#envEndpointCanBeExposedViaProperties`
3. **根路径 links 只列出“暴露端点”**
   - 验证：`BootActuatorLabTest#actuatorRootListsExposedEndpoints` / `BootActuatorExposureOverrideLabTest#actuatorRootIncludesEnvLinkWhenExposed`
4. **结构契约：health/info 的 JSON 结构可断言**
   - 验证：`BootActuatorLabTest#healthIncludesCustomIndicator` / `BootActuatorLabTest#infoEndpointContainsConfiguredInfoProperties`

## 源码与断点


断点入口（从“现象”到“原因”的最短路径）：

- 暴露集合的计算与映射建立：
  - `org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping#initHandlerMethods`（看哪些 endpoint 被注册成 handler）
- links 生成（为什么 `/actuator` 看不到某个端点）：
  - `org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver#resolveLinks`
- 当接到 401/403/404 的排障分流：
  - 先看响应码（401/403 通常是安全；404 可能是没暴露/路径不对）
  - 再在 `BootActuatorLabTest` / `BootActuatorExposureOverrideLabTest` 的请求处下断点，结合 handler mapping 与 security filter chain 逐层确认

## 最小可运行实验（Lab）

- Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`
- 运行命令：`mvn -pl :spring-boot-actuator test`（或在 IDE 直接运行上面的测试类）


## 验证目标
1. 能描述“端点是否存在”与“端点是否暴露”是两件事
2. 能用最小配置复现 exposure 覆盖/退让，并用测试锁定行为
3. 能定位 Actuator 相关排障的第一现场：`/actuator`、条件报告、日志与配置来源

## 如何跑实验
- 运行本模块测试：`mvn -pl :spring-boot-actuator test`

## 对应 Lab（可运行）

- `BootActuatorLabTest`
- `BootActuatorExposureOverrideLabTest`
- `BootActuatorExerciseTest`

## 常见坑与边界

如果是带着线上问题来的，先对照本模块 Appendix（common pitfalls/self-check），再回到主线章节逐一核对。

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`
- Exercise：`BootActuatorExerciseTest`

上一章：[模块目录](../README.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[actuator-basics.md](actuator-basics.md)

<!-- BOOKIFY:END -->
