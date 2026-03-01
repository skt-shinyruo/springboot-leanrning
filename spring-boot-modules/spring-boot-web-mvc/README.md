# spring-boot-web-mvc

本模块用于学习 Spring MVC 的常见入门点，并覆盖两条主线：

- **REST API（JSON）主线**：`@RestController`、参数校验（Validation）、统一错误响应（`@RestControllerAdvice`）
- **传统 MVC（HTML）主线**：`@Controller`、Thymeleaf 页面渲染、表单提交（绑定/校验/回显/PRG）、错误页与内容协商（Accept：HTML vs JSON）

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`

文档入口：
- 模块目录（Docs TOC）：[`docs/README.md`](docs/README.md)
- 总览（先把写法与排障串起来）：[`docs/00-guide/00-from-annotations-to-breakpoints.md`](docs/00-guide/00-from-annotations-to-breakpoints.md)
- 常见坑：[`docs/appendix/01-common-pitfalls.md`](docs/appendix/01-common-pitfalls.md)
- 自检：[`docs/appendix/02-self-check.md`](docs/appendix/02-self-check.md)

## 本模块的学习产出

- 用 `@RestController` 编写 JSON API
- 用 `@Valid` + `jakarta.validation` 做请求参数校验
- 用 `@RestControllerAdvice` 统一返回错误响应
- 用 `@Controller` 返回 viewName / `ModelAndView` 渲染 Thymeleaf 页面
- 表单提交闭环：`@ModelAttribute` + `BindingResult` + 校验失败回显 + PRG（Post-Redirect-Get）+ Flash Attributes
- 错误页模板（`templates/error/*`）与 Accept 驱动的响应形态（HTML vs JSON）
- 对比 `@WebMvcTest`（切片）与 `@SpringBootTest`（全量上下文）的测试体验

## 前置知识

- 建议先完成 `spring-boot-basics`（至少理解配置加载与启动过程）
- 了解 HTTP/JSON 的基本概念（状态码、请求体、响应体）
- （可选）了解 Bean Validation 的基本注解（`@NotBlank`、`@Email`）

## 关键命令

### 运行

```bash
mvn -pl :spring-boot-web-mvc spring-boot:run
```

默认端口：`8081`

### 快速验证

- Ping：

```bash
curl http://localhost:8081/api/ping
```

- 创建用户（正常）：

```bash
curl -X POST http://localhost:8081/api/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"Alice","email":"alice@example.com"}'
```

- 创建用户（触发校验失败）：

```bash
curl -X POST http://localhost:8081/api/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"","email":"not-an-email"}'
```

- 访问页面（HTML）：

```bash
curl -H 'Accept: text/html' http://localhost:8081/pages/ping
```

- 表单页（建议用浏览器打开）：
  - `http://localhost:8081/pages/users/new`

### 测试

```bash
mvn -pl :spring-boot-web-mvc test
```

## 推荐 docs 阅读顺序

建议按 “总览（先把写法与排障串起来）→ 入口 → REST 主线 → 页面主线 → 常见坑/自测题” 的顺序学习：

（docs 目录页：[`docs/README.md`](docs/README.md)）

0. [从注解到断点：用一条主线学会 Spring MVC](docs/00-guide/00-from-annotations-to-breakpoints.md)
1. [校验与错误响应形状](docs/06-binding-validation/01-validation-and-error-shaping.md)
2. [统一异常处理与坏输入](docs/10-exception-resolvers/02-exception-handling.md)
3. [请求绑定与 Converter/Formatter](docs/06-binding-validation/03-binding-and-converters.md)
4. [Interceptor vs Filter：入口与顺序](docs/04-handleradapter-interceptor/04-interceptor-and-filter-ordering.md)
5. [传统 MVC 页面渲染入门（Thymeleaf/ViewResolver）](docs/09-return-value-view/01-thymeleaf-and-view-resolver.md)
6. [表单提交闭环（绑定/校验/回显/PRG）](docs/09-return-value-view/02-form-binding-validation-prg.md)
7. [错误页与内容协商（Accept：HTML vs JSON）](docs/11-boot-error/03-error-pages-and-content-negotiation.md)
8. [常见坑清单](docs/appendix/01-common-pitfalls.md)

对应的可运行实验（先跑后读）：
- `src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java`（`@WebMvcTest` 切片）
- `src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcSpringBootLabTest.java`（`@SpringBootTest` 全量）
- `src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewLabTest.java`（页面渲染 MockMvc）
- `src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcErrorViewLabTest.java`（错误页 + Accept）
- `src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewSpringBootLabTest.java`（页面渲染端到端）

## 概念 → 在本模块哪里能“看见”

| 要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 应能解释清楚 |
| --- | --- | --- | --- |
| 校验在边界触发 | [docs/06-binding-validation/01](docs/06-binding-validation/01-validation-and-error-shaping.md) | `BootWebMvcLabTest#returnsValidationErrorWhenRequestIsInvalid` + `CreateUserRequest` | 为什么需要 `@Valid`，失败时异常从哪来 |
| 统一错误响应形状 | [docs/06-binding-validation/01](docs/06-binding-validation/01-validation-and-error-shaping.md) | `GlobalExceptionHandler` + `ApiError` | 为什么要自定义错误结构，结构由谁决定 |
| malformed JSON vs 校验失败 | [docs/10-exception-resolvers/02](docs/10-exception-resolvers/02-exception-handling.md) | `BootWebMvcLabTest#returnsBadRequestWhenJsonIsMalformed` + `BootWebMvcExerciseTest#exercise_handleMalformedJson` | 两类 400 的根因差异 |
| Converter/Formatter 扩展绑定 | [docs/06-binding-validation/03](docs/06-binding-validation/03-binding-and-converters.md) | `BootWebMvcExerciseTest#exercise_converterFormatter` | String 如何变成自定义类型 |
| Interceptor 生效范围与顺序 | [docs/04-handleradapter-interceptor/04](docs/04-handleradapter-interceptor/04-interceptor-and-filter-ordering.md) | `BootWebMvcExerciseTest#exercise_interceptor` | 为什么它只对 `/api/**` 生效 |
| `@Controller` 返回 viewName | [docs/09-return-value-view/01](docs/09-return-value-view/01-thymeleaf-and-view-resolver.md) | `MvcPingController` + `ping.html` | 为什么返回 String 却渲染了 HTML |
| 表单校验回显（BindingResult） | [docs/09-return-value-view/02](docs/09-return-value-view/02-form-binding-validation-prg.md) | `MvcUserController` + `user-form.html` | 为什么校验失败不会抛异常，而是回到表单页 |
| 错误页与 Accept | [docs/11-boot-error/03](docs/11-boot-error/03-error-pages-and-content-negotiation.md) | `templates/error/*` + `MvcExceptionHandler` | 为什么同一个错误在浏览器和脚本里长得不一样 |

> 想从机制层理解“校验为什么有时不生效”，可进一步阅读 `spring-core-modules/spring-core-validation/docs/part-01-validation-core/03-method-validation-proxy.md`（方法参数校验与代理）。

## Labs / Exercises 索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶。Exercises 默认 `@Disabled`，建议逐个开启。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java` | `@WebMvcTest` 切片：Controller/校验/错误结构 | ⭐ | 看 `GlobalExceptionHandler` 的错误结构与字段 |
| Lab | `src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcSpringBootLabTest.java` | `@SpringBootTest`：RANDOM_PORT 下的端到端行为 | ⭐ | 对比切片与全量上下文加载范围 |
| Exercise | `src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java` | 按提示扩展字段/错误响应/测试断言 | ⭐–⭐⭐ | 从“新增字段 + 校验”开始 |
| Lab | `src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewLabTest.java` | 页面渲染：viewName/model/HTML 断言 + 表单回显/redirect | ⭐ | 对照 `MvcUserController` 与 templates 的绑定关系 |
| Lab | `src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcErrorViewLabTest.java` | 错误页：404/5xx HTML 模板 + Accept=JSON 分支 | ⭐–⭐⭐ | 对照 `templates/error/*` 与 `MvcExceptionHandler` |
| Lab | `src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewSpringBootLabTest.java` | 页面渲染：端到端获取 HTML（真实端口） | ⭐ | 对比 MockMvc 与真实运行差异 |

## 常见 Debug 路径

- `400 Bad Request`：先看响应体里 `fieldErrors`，再定位到对应 DTO 的校验注解
- 校验没触发：确认 controller 入参是否带 `@Valid`、是否走到 `GlobalExceptionHandler`
- JSON 解析失败：优先检查请求体是否是合法 JSON（以及字段名是否匹配）
- 页面回显没生效：确认 POST 方法里参数是否为 `@Valid @ModelAttribute` + `BindingResult`（并且 BindingResult 紧跟其后）
- 404：确认路由是否正确（`/api/...` 或 `/pages/...`），并观察自定义错误页是否生效（`templates/error/404.html`）

## 扩展练习（可选）

- 给 `CreateUserRequest` 增加一个字段 `age`，要求 `>= 18`
- 把错误响应结构扩展为：包含 `timestamp`、`path` 等信息

## 参考

- Spring MVC
- Bean Validation (Jakarta Validation)
