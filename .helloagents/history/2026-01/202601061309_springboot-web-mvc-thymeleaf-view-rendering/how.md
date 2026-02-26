# Technical Design: springboot-web-mvc 传统 MVC 页面渲染（Thymeleaf/ModelAndView）与错误页/内容协商

## Technical Solution

### Core Technologies
- Spring Boot 3 / Spring Web MVC（现有）
- Thymeleaf（新增：`spring-boot-starter-thymeleaf`）
- 测试：MockMvc（`@WebMvcTest`）、端到端（`@SpringBootTest(webEnvironment=RANDOM_PORT)` + `TestRestTemplate`）

### Implementation Key Points
1. **路由分层**
   - `/api/**`：保持 JSON API（现有 `@RestController` 主线）
   - `/pages/**`：新增 `@Controller` 页面主线（返回 viewName 或 `ModelAndView`）
2. **表单绑定与校验**
   - GET：返回表单页（model 中放入 form backing object）
   - POST：`@Valid @ModelAttribute` + `BindingResult`；失败返回原 view 并回显 errors；成功 redirect（PRG）并使用 `RedirectAttributes` 携带 flash message
3. **错误处理与内容协商**
   - 页面错误页：提供 `templates/error/404.html`、`templates/error/4xx.html`、`templates/error/5xx.html`（以 Spring Boot 约定优先匹配）
   - API 错误体：补齐 JSON 解析失败/类型转换失败等常见异常，统一返回 ApiError（或可选 ProblemDetail）
   - 内容协商示例：在页面异常处理兜底逻辑中基于 `Accept`（`text/html` vs `application/json`）选择返回 HTML 视图或 JSON（用于展示机制差异，并用测试锁定）
4. **最小可复现与可验证**
   - MockMvc：断言 viewName/model/关键 HTML 标记
   - SpringBootTest：真实端口下获取 HTML 并断言关键片段；验证 404/5xx 错误页

## Architecture Decision ADR

### ADR-001: 页面错误页优先采用 Boot 约定模板 + 页面/接口分层
**Context:** 需要同时支持 JSON API 与 HTML 页面，并可展示错误页（404/5xx）与差异化错误结构。  
**Decision:** `/api/**` 与 `/pages/**` 明确分层；页面错误页优先使用 Spring Boot 约定的 `templates/error/*`；API 侧继续用 `@RestControllerAdvice` 统一错误体，并补齐常见 400。  
**Rationale:** 最小侵入现有 REST 代码；错误页通过约定即可生效；分层后规则更可解释、测试更稳定。  
**Alternatives:** 用单一异常处理器强行做全局内容协商 → 拒绝原因：实现复杂、容易引入边界差异与测试不稳定。  
**Impact:** 模块结构更清晰；需新增 templates 与页面测试；docs 增加“分层 + Accept”解释。

## API Design
本变更新增页面路由，不改变现有 API 的路径；API 错误体补齐后将更稳定可断言。

## Data Model
不引入持久化数据；示例可使用内存对象/无状态页面。

## Security and Performance
- **Security:**
  - 默认使用 Thymeleaf 安全转义输出；禁止在示例中直接输出未转义的用户输入
  - 错误页不泄露堆栈/敏感信息；对外仅展示必要提示
- **Performance:**
  - 开发环境可关闭模板缓存以便学习（docs 提示生产需开启缓存）

## Testing and Deployment
- **Testing:**
  - `@WebMvcTest`：页面渲染（viewName、model、HTML marker）+ 异常/错误页基本行为
  - `@SpringBootTest`：端到端获取 HTML 响应、验证 404/5xx 模板生效
- **Deployment:** 无（学习仓库）
