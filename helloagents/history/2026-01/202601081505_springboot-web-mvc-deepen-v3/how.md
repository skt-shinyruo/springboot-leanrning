# Technical Design: springboot-web-mvc deepen v3（Advice 匹配 + Binder suppressedFields + Converter 可观测）

## Technical Solution

### Core Technologies

- Spring Web MVC（`@ControllerAdvice` / binder / message converters）
- Spring Boot Test + MockMvc（slice Labs 固定关键分支）

### Implementation Key Points

1) **ControllerAdvice 匹配规则（可适用性 vs 优先级）**
- 新增一组 demo controllers（仅用于抛出自定义异常），统一放在 `/api/advanced/advice-matching/**`
- 新增多份 `@RestControllerAdvice`，分别使用：
  - `annotations = ...`
  - `assignableTypes = ...`
  - `basePackages = ...`
- 所有 advice 都处理同一自定义异常类型，以便测试“哪个生效”
- 通过 `@Order` 让“同时匹配时的选择结果”可预测（在匹配集合之内按顺序选）

2) **Binder suppressedFields（把“被禁止绑定字段”变成证据）**
- 在现有 `BindingDeepDiveController` 的 mass assignment 场景新增 debug endpoint
- 通过 `BindingResult#getSuppressedFields()` 输出 suppressed fields 列表
- LabTest 固定：请求携带 `admin=true` 时，`admin` 不被绑定且 `suppressedFields` 包含 `admin`

3) **HttpMessageConverter 选择可观测（selectedConverterType/selectedContentType）**
- 新增 `ResponseBodyAdvice`：在 body 写出前将
  - `selectedConverterType`
  - `selectedContentType`
  写入 response headers（仅对 `/api/advanced/message-converters/**` 生效）
- 新增 controller endpoints 覆盖：
  - `String` → `StringHttpMessageConverter`
  - `JSON` → `MappingJackson2HttpMessageConverter`
  - `byte[]` → `ByteArrayHttpMessageConverter`
  - 自定义 media type（strict json）→ 专用 converter（用子类区分，便于观察）
- LabTest 固定每个端点的“converter + content-type”证据链

## Security and Performance

- **Security:**
  - 新增端点均为教学用途且限定 `/api/advanced/**`
  - `@ControllerAdvice` 通过 basePackages 限制作用域，避免污染其它包
  - `ResponseBodyAdvice` 通过 URI 前缀限制，只输出“框架选择证据”，不包含敏感信息
- **Performance:** 所有新增逻辑只在教学路径触发；不引入全局热路径开销

## Testing and Verification

- `mvn -pl springboot-web-mvc test`

