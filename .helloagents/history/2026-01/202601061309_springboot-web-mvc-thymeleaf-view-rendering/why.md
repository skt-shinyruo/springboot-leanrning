# Change Proposal: springboot-web-mvc 补齐传统 MVC 页面渲染（Thymeleaf/ModelAndView）与错误页/内容协商

## Requirement Background
当前 `springboot-web-mvc` 主要覆盖 REST Controller 主线（JSON API、校验、统一错误响应、绑定扩展、Filter/Interceptor 顺序与测试切片）。但在实际项目中，Spring MVC 还经常承担“服务端渲染页面（SSR）/表单提交”的职责：`@Controller` 返回视图、Thymeleaf 模板渲染、表单绑定与错误回显、以及 HTML 错误页（404/5xx）等。

为了让学习闭环更完整，需要在同一模块内补齐“传统 MVC 页面渲染”方向，并通过 MockMvc 与端到端测试锁定行为，避免只停留在概念层。

## Change Content
1. 增加 Thymeleaf 页面渲染主线：`@Controller` + 视图名/`ModelAndView` + 模板与静态资源
2. 增加表单提交闭环：`@ModelAttribute` 绑定 + `@Valid` 校验 + `BindingResult` 错误回显 + PRG（Post-Redirect-Get）
3. 增加错误页与内容协商示例：HTML 错误页模板（4xx/5xx/404）+ 针对页面与 API 的差异化异常处理策略（含 Accept 维度）
4. 增加配套 docs 与可复现测试：`@WebMvcTest`（页面渲染断言）+ `@SpringBootTest`（端到端 HTML 验证）

## Impact Scope
- **Modules:** `springboot-web-mvc`
- **Files:** `springboot-web-mvc/pom.xml`、`springboot-web-mvc/src/main/java/**`、`springboot-web-mvc/src/main/resources/templates/**`、`springboot-web-mvc/src/test/java/**`、`docs/web-mvc/springboot-web-mvc/**`
- **APIs:** 新增页面路由（建议前缀 `/pages/**`）；现有 `/api/**` 保持 JSON API 语义
- **Data:** 无持久化数据变更（仅示例用内存数据或无状态页面）

## Core Scenarios

<a id="r1"></a>
### Requirement: 传统 MVC 页面渲染主线
**Module:** springboot-web-mvc
提供最小可运行页面示例，用于观察“请求进入 → handler mapping → model 填充 → view resolve → thymeleaf 渲染”。

<a id="r1-s1"></a>
#### Scenario: 页面渲染可被测试验证
- 访问 `/pages/ping` 返回 HTML（非 JSON）
- MockMvc 可断言 viewName、model attribute、以及渲染后的关键文本

<a id="r2"></a>
### Requirement: 表单提交 + 校验 + 错误回显（PRG）
**Module:** springboot-web-mvc
补齐传统 MVC 中最常见的表单处理闭环：GET 展示表单 → POST 提交 → 校验失败回显 → 成功后 redirect。

<a id="r2-s1"></a>
#### Scenario: 校验失败回显
- POST 提交空字段/非法 email 时，不跳转，返回原表单页
- 页面中可见字段级错误信息与用户已输入值（可用最小断言锁定）

<a id="r2-s2"></a>
#### Scenario: 提交成功走 PRG
- POST 提交合法数据后返回 302/303（redirect）
- redirect 目标页可展示结果，并可通过 flash attributes 显示一次性提示信息

<a id="r3"></a>
### Requirement: 错误页与内容协商（HTML vs JSON）
**Module:** springboot-web-mvc
提供“同一类错误，在浏览器访问与 API 调用时表现不同”的示例：页面返回 HTML 错误页/API 返回 JSON 错误体，并用测试固定。

<a id="r3-s1"></a>
#### Scenario: 404 返回自定义 HTML 错误页
- 访问不存在页面路由时返回自定义 404（或 4xx）错误页
- 页面包含可识别标记（用于测试断言）

<a id="r3-s2"></a>
#### Scenario: Accept 驱动的错误响应形态
- 当请求 Accept 偏向 `text/html` 时，返回 HTML 错误页/错误视图
- 当请求 Accept 偏向 `application/json` 时，返回 JSON（ApiError 或 ProblemDetail）

## Risk Assessment
- **Risk:** 新增 Thymeleaf 与模板后，若错误回显/输出未转义，存在 XSS 风险
  - **Mitigation:** 使用 Thymeleaf 默认转义输出；docs 明确“不要用 `th:utext` 拼接用户输入”；仅返回必要错误信息
- **Risk:** 引入页面表单后，若未来接入 `springboot-security`，POST 表单会受 CSRF 影响
  - **Mitigation:** 本模块不默认引入 Security；docs 增补“接入 Security 后需要 CSRF token/禁用策略”的提示链接
