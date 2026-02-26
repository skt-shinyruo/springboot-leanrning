# Technical Design: springboot-web-mvc 深入扩展 II（知识地图 + Security/Observability + 排障闭环）

## Technical Solution

### Core Technologies

- Spring Boot 3.5.x（当前仓库 parent 版本）
- Spring Web MVC（Servlet 栈）
- Jackson（`HttpMessageConverter` 默认 JSON 处理）
- Spring Security（计划引入：用于演示 FilterChainProxy/401/403/CSRF）
- Spring Boot Actuator / Micrometer（计划引入：用于演示指标与观测）
- 测试：MockMvc（slice）/ `@SpringBootTest(webEnvironment=RANDOM_PORT)`（端到端）

### Implementation Key Points

1. **以 docs/README.md 为 SSOT 扩展目录结构**
   - `docs/web-mvc/springboot-web-mvc/README.md` 继续作为文档入口与索引。
   - 新增 Part 08 的目录链接，并补齐“Start Here”中的知识地图入口。

2. **知识地图：用“主轴 + 证据链”组织知识点**
   - 主轴：DispatcherServlet → HandlerMapping/Adapter → ArgumentResolver/Binder/Validator → ExceptionResolver → ReturnValueHandler/MessageConverter → write response
   - 证据链：每个阶段至少给出 1 个可跑测试断言 + 1–2 个推荐断点，避免“只讲概念不落地”。

3. **绑定/转换/校验深挖：从“错误分类”切入**
   - 补齐 `@RequestBody`（converter 路径）与 `@ModelAttribute`（binder 路径）的差异解释。
   - 补齐 `@InitBinder` 与 conversion/validation 的交汇点：如何影响绑定与错误消息。
   - 新增/增强测试用例，用不同输入稳定复现：解析失败、类型不匹配、校验失败三类 400。

4. **内容协商与 Converter：用 406/415 做“关键分支样例”**
   - 强化 `Accept`/`Content-Type`/`produces`/`consumes` 与 converter 选择的关系。
   - 扩展现有 strict media type 示例，补齐“为什么会 406/415”与“断点打哪”。

5. **异常体系：ApiError 主线 + ProblemDetail 对照，不互相破坏**
   - 保持既有 `ApiError` 形状与现有 controller/advice 行为不破坏。
   - 通过新增包/新端点提供 `ProblemDetail` 的最小对照示例，明确：
     - 什么时候应坚持自定义契约（ApiError）
     - 什么时候可拥抱框架语义（ProblemDetail）

6. **缓存与 ETag：从“能跑出 304”开始**
   - 对静态资源缓存已有覆盖的基础上，新增 API ETag 最小示例。
   - 测试维度：第一次请求拿到 ETag；第二次带 `If-None-Match` 得到 304。

7. **Security 集成：只影响新端点，解释清楚链路变化**
   - 引入 `spring-boot-starter-security` 并提供最小 `SecurityFilterChain`：
     - 新端点前缀受保护（401/403/CSRF 可复现）
     - 既有端点默认放行（保证现有 Labs 不被破坏）
   - 文档必须解释：Servlet Filter vs Spring Security Filter Chain vs HandlerInterceptor 的相对位置与调试方法。

8. **Observability：把“观测”做成可验证的最小闭环**
   - 引入 `spring-boot-starter-actuator` 并最小化暴露端点集合（health/info/metrics）。
   - 新增观测示例：
     - MVC Interceptor 计时/打点（概念与位置）
     - `http.server.requests`（或等价指标）的读取/对照
   - 测试策略：优先端到端（random port）验证 actuator 与指标链路，避免 slice 无法覆盖。

9. **测试策略：slice 与 full context 各司其职**
   - Web 层机制（resolver/converter/exception）优先 `@WebMvcTest` + MockMvc：更快、更稳定。
   - Security/Actuator 这种“跨层链路”优先 `@SpringBootTest(webEnvironment=RANDOM_PORT)`：更贴近真实行为，但需控制范围与用例数量避免变慢。

## Security and Performance

- **Security:**
  - 不在代码中写死任何密钥/Token；
  - 受保护端点只用于教学，默认用户名密码仅用于本地测试，且在文档中强调“示例配置≠生产策略”；
  - Actuator 仅暴露最小集合并限制示例用途，避免误用为“默认最佳实践”。
- **Performance:**
  - 避免在拦截器中输出大对象/请求体，日志只做必要字段；
  - SSE/Async 避免无限事件与长时间阻塞，确保测试可结束；
  - 缓存示例强调 header 的语义与边界，避免误导为“所有 API 都应开启缓存”。

## Testing and Deployment

- **Testing:**
  - `mvn -pl springboot-web-mvc test`
- **Deployment:** 本仓库为学习型工程，不提供生产部署流程；文档中如涉及生产建议，会显式标注。

