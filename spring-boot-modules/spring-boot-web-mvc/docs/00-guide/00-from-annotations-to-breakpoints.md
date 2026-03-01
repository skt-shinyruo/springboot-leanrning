# 00. 从注解到断点：用一条主线学会 Spring MVC
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    这一章不是“注解大全”，而是一份书籍式的总览：用一个请求做主角，把 **入门写法（怎么写得稳）** 和 **机制排障（怎么查得快）** 串成一条线。

    读完你应当能做到两件事：

    1. **能落地**：写出一个带校验、带统一错误体、能被测试稳定覆盖的 REST API；并能完成一条页面表单的提交闭环（回显 + PRG）。
    2. **能排障**：看到 400/406/415/405/404 时，不靠“经验猜”，而是能用 `resolvedException` + 关键断点把分支钉死，快速回到根因。

    推荐先跑一次 Book/Branch Matrix，把“现象”固化为事实，再回到正文对照理解：

    - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
    - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 主线时间线：Spring Boot Web MVC](01-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读：为什么这章要“从注解讲到断点”

本章不走“概念堆叠”的路线，直接用可复现的现象开局。你只需要做三件事：

1) **把现象固定成事实**：跑 Book/Branch Matrix（不靠“感觉”）。  
2) **把常见分支复现出来**：用 curl 触发校验失败、JSON 解析失败、类型不匹配、406/415、401/403。  
3) **把分支发生点看清楚**：只下关键断点（别一上来就全局搜日志）。  

下面先给两张“速查表”。如果你只想要干货，看到这里就可以直接开跑；后面的章节再解释每一步为什么会这样。

### 速查表 A：本模块常用入口（端点 + 目的）

| 目的 | 方法 | URL | 你要观察的“证据” | 对应测试入口 |
| --- | --- | --- | --- | --- |
| 最小 JSON API | GET | `/api/ping` | 200 + `{"message":"pong"}` | `BootWebMvcLabTest#pingEndpointReturnsPong` |
| `@Valid` 生效 | POST | `/api/users` | 400 + `message=validation_failed` | `BootWebMvcLabTest#returnsValidationErrorWhenRequestIsInvalid` |
| 证明“没写 @Valid 就不校验” | POST | `/api/users/no-valid` | 200（哪怕字段非法） | `BootWebMvcLabTest#createUserSucceedsWhenControllerOmitsValidAnnotation` |
| binder 类型不匹配 | GET | `/api/advanced/binding/age?age=abc` | 400 + `message=type_mismatch` | `BootWebMvcBindingDeepDiveLabTest#returnsTypeMismatchWhenRequestParamCannotConvert` |
| binder 自动抛异常 | POST(form) | `/api/advanced/binding/form` | 400 + resolvedException=`BindException` | `BootWebMvcBindingDeepDiveLabTest#returnsValidationFailedWhenModelAttributeIsInvalid` |
| binder 手工处理 | POST(form) | `/api/advanced/binding/form-manual` | 400 但 resolvedException 为 null | `BootWebMvcBindingDeepDiveLabTest#bindingResultCanShortCircuitExceptionFlowWhenHandledManually` |
| 406（写不出） | GET | `/api/advanced/contract/ping` | 406 + resolvedException=`HttpMediaTypeNotAcceptableException` | `BootWebMvcErrorBranchMatrixLabTest#branch406_whenAcceptIsNotSupported` |
| 415（读不进） | POST | `/api/advanced/contract/echo` | 415 + resolvedException=`HttpMediaTypeNotSupportedException` | `BootWebMvcErrorBranchMatrixLabTest#branch415_whenContentTypeIsNotSupported` |
| Filter vs Interceptor 顺序 | GET | `/api/advanced/trace/sync` | events 顺序（见下文） | `BootWebMvcTraceLabTest#syncTraceRecordsFilterAndInterceptorOrder` |
| async 二次 dispatch | GET | `/api/advanced/trace/async` | events 同时出现 REQUEST/ASYNC | `BootWebMvcTraceLabTest#asyncTraceRecordsAfterConcurrentHandlingStartedAndAsyncDispatchCallbacks` |
| 401/403 边界（在 MVC 之前） | GET/POST | `/api/advanced/secure/**` | handler=null 且 resolvedException=null | `BootWebMvcSecurityVsMvcExceptionBoundaryLabTest` |

### 速查表 B：`ApiError` 的“错误码（message）”对照（本模块实际会返回）

本模块的基础主线把错误塑形为：

```json
{
  "message": "validation_failed",
  "fieldErrors": {
    "field": "reason"
  }
}
```

常见错误码与触发条件如下（按“你在接口上最常遇到的顺序”排列）：

| message | 常见触发 | HTTP | resolvedException（证据） |
| --- | --- | --- | --- |
| `validation_failed` | `@Valid` 失败（body/binder） | 400 | `MethodArgumentNotValidException` / `BindException` |
| `malformed_json` | JSON 格式不合法 | 400 | `HttpMessageNotReadableException` |
| `type_mismatch` | `age=abc` 这类类型转换失败 | 400 | `MethodArgumentTypeMismatchException` |
| `missing_parameter` | 缺少 `@RequestParam` 必填参数 | 400 | `MissingServletRequestParameterException` |
| `missing_header` | 缺少 `@RequestHeader` 必填请求头 | 400 | `MissingRequestHeaderException` |
| `method_validation_failed` | `@Validated` 方法级参数约束失败 | 400 | `HandlerMethodValidationException` / `ConstraintViolationException` |
| `method_not_supported` | 路由存在但方法不支持 | 405 | `HttpRequestMethodNotSupportedException` |
| `unsupported_media_type` | Content-Type 不支持 | 415 | `HttpMediaTypeNotSupportedException` |
| `not_acceptable` | Accept 不支持 | 406 | `HttpMediaTypeNotAcceptableException` |

### 快速运行：两条路（跑测试 / 跑服务）

这套文档的“最短闭环”不是靠读出来的，而是靠跑出来的。这里给两条最实用的运行方式：

**方式 1：只跑测试（最快，定位最精确）**

```bash
# 主线入口（把现象固定成断言）
mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test

# 关键错误分支入口（400/406/415）
mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test
```

**方式 2：启动服务，用 curl 亲手触发分支（更直观）**

```bash
mvn -pl :spring-boot-web-mvc spring-boot:run
```

默认端口：`8081`（见模块 `README.md` 的说明）。

---

## 第一部分：入门落地（写法与最佳实践）

这一部分只做一件事：把 Web 层的输入/输出写成可回归的契约（成功与失败都能稳定复现）。

### 1. 从 `@RestController` 开始：先跑通一个最小 API

一个最小 API 不需要服务层、不需要数据库，也足够让你理解 MVC 的入口与返回：

- 示例入口：`GET /api/ping`
- 代码位置：`src/main/java/com/learning/springboot/bootwebmvc/part01_web_mvc/PingController.java`

你应该注意两件事：

1. controller 方法返回的是“对象”，最终会被写回响应体（JSON）。这件事不是 `@RestController` 单独完成的，而是 Web MVC 的返回值处理器 + 消息转换器（`HttpMessageConverter`）共同完成的。
2. 你在 controller 里不需要显式写 JSON 字符串。真正的“输出格式”是由 `Accept`、`produces`、converter 选择共同决定的（这会在 406/415 一节变成排障分支）。

!!! example "动手：用 curl 跑通最小闭环"
    先启动服务（端口默认 8081）：

    ```bash
    mvn -pl :spring-boot-web-mvc spring-boot:run
    ```

    然后访问：

    ```bash
    curl -sS http://localhost:8081/api/ping
    ```

    期望响应（示例）：

    ```json
    {"message":"pong"}
    ```

### 2. 写一个真正可用的 REST：参数绑定、校验、响应体

以“创建用户”为例，你至少需要 4 件东西：

1) 路由（endpoint）  
2) 入参 DTO（`@RequestBody` 绑定 JSON）  
3) 校验（Bean Validation）  
4) 错误体（统一约定）  

对应代码在本模块是现成的：

- Controller：`src/main/java/com/learning/springboot/bootwebmvc/part01_web_mvc/UserController.java`
- DTO：`src/main/java/com/learning/springboot/bootwebmvc/part01_web_mvc/CreateUserRequest.java`
- 错误体：`src/main/java/com/learning/springboot/bootwebmvc/part01_web_mvc/ApiError.java`
- 统一异常处理：`src/main/java/com/learning/springboot/bootwebmvc/part01_web_mvc/GlobalExceptionHandler.java`

先把最关键的“分支开关”看清楚：同一个 DTO、同一套约束，只是 `@Valid` 有无不同，行为就会完全不同。

```java
@PostMapping
public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) { ... }

@PostMapping("/no-valid")
public UserResponse createUserWithoutValid(@RequestBody CreateUserRequest request) { ... }
```

!!! example "动手：3 个请求把 `@Valid` 的分支跑出来"
    1) 正常创建（200）：

    ```bash
    curl -sS -X POST http://localhost:8081/api/users \
      -H 'Content-Type: application/json' \
      -d '{"name":"Alice","email":"alice@example.com"}'
    ```

    2) 校验失败（400，错误体统一塑形为 `ApiError`）：

    ```bash
    curl -i -X POST http://localhost:8081/api/users \
      -H 'Content-Type: application/json' \
      -d '{"name":"","email":"not-an-email"}'
    ```

    期望响应体结构（示例，重点看 message 与字段 key）：

    ```json
    {"message":"validation_failed","fieldErrors":{"name":"...","email":"..."}}
    ```

    3) 故意省略 `@Valid`（200，证明“约束注解不是默认行为”）：

    ```bash
    curl -sS -X POST http://localhost:8081/api/users/no-valid \
      -H 'Content-Type: application/json' \
      -d '{"name":"","email":"not-an-email"}'
    ```

    期望响应体结构（示例，id 会变化）：

    ```json
    {"id":1,"name":"","email":"not-an-email"}
    ```

#### 2.1 `@RequestBody`：JSON 是怎么变成 DTO 的

当你写下 `@RequestBody CreateUserRequest request` 时，意味着你把“输入”交给了 body 路径：

- `Content-Type` 告诉框架“我送过来的是什么格式”
- `HttpMessageConverter` 负责“读入 + 反序列化”

最佳实践（你会在真实项目里受益）：

- 对外 API 尽量写清楚 `consumes/produces`，不要让契约漂移。
- 客户端请求记得带 `Content-Type: application/json`，否则你会把自己送进 415 分支。

本模块的“合同式写法”示例见：

- `src/main/java/com/learning/springboot/bootwebmvc/part04_contract/RestContractController.java`

!!! example "动手：未知字段（宽松 JSON vs strict JSON）"
    **宽松 JSON（默认行为）：未知字段会被忽略**

    ```bash
    curl -sS -X POST http://localhost:8081/api/users \
      -H 'Content-Type: application/json' \
      -d '{"name":"Alice","email":"alice@example.com","extra":"ignored"}'
    ```

    期望：200，并且 `name/email` 正常回显（未知字段不影响业务）。

    **strict JSON（教学用）：未知字段直接失败**

    ```bash
    curl -i -X POST http://localhost:8081/api/advanced/contract/strict-echo \
      -H 'Content-Type: application/vnd.learning.strict+json' \
      -H 'Accept: application/json' \
      -d '{"message":"hello","createdAt":"2026-01-07T16:35:00Z","extra":"should-fail"}'
    ```

    期望：400，且 `message=malformed_json`。如果开启了“未知字段名入错误体”的分支，响应会形如：

    ```json
    {"message":"malformed_json","fieldErrors":{"extra":"未知字段"}}
    ```

#### 2.2 `@Valid`：校验不是默认行为，它是一个分支

`CreateUserRequest` 上写了 `@NotBlank`、`@Email`，但**是否触发校验**，取决于 controller 方法参数上有没有 `@Valid`：

- `POST /api/users`：有 `@Valid`（会校验）
- `POST /api/users/no-valid`：故意没有 `@Valid`（不会校验）

对应代码：`UserController#createUser` 与 `UserController#createUserWithoutValid`。

这件事很“反直觉”，但它恰恰是入门阶段最该吃透的点：  
**约束注解只是“规则”；`@Valid` 才是“按下开关”。**

想把这个现象变成证据，而不是靠记忆，可以直接跑用例：

- `BootWebMvcLabTest#createUserSucceedsWhenControllerOmitsValidAnnotation`

#### 2.3 错误体（Error Shape）：为什么要统一、怎么统一

接口一旦上线，错误响应就是对外契约的一部分。

你不希望客户端在“校验失败 / 缺参 / JSON 格式错 / 类型不匹配”这些场景里拿到完全不一样的结构，更不希望把内部异常栈暴露出去。

本模块用一个很克制的错误体作为起点：

- `ApiError.message`：机器可读的错误码（例如 `validation_failed`）
- `ApiError.fieldErrors`：字段/参数级别的细节（例如 `email: "must be a well-formed email address"`）

统一异常处理的关键点：

- `@RestControllerAdvice` 把“异常 → ResponseEntity<ApiError>”做成一张翻译表
- 处理的不只是校验异常，还包括：malformed JSON、type mismatch、missing parameter/header/path variable、406/415 等

代码位置：`GlobalExceptionHandler`。

!!! example "动手：同样是 400，三种根因（把错误码跑出来）"
    下面三条请求都会返回 400，但它们来自三个完全不同的阶段。不要只盯状态码，先把 `message` 固定下来。

    1) **JSON 解析失败（converter/read 阶段）**：

    ```bash
    curl -sS -X POST http://localhost:8081/api/users \
      -H 'Content-Type: application/json' \
      -d '{"name":"Alice",'
    ```

    期望响应（示例）：

    ```json
    {"message":"malformed_json","fieldErrors":{}}
    ```

    2) **类型转换失败（argument resolver/binder 阶段）**：

    ```bash
    curl -sS -G http://localhost:8081/api/advanced/binding/age \
      -H 'Accept: application/json' \
      --data-urlencode 'age=not-a-number'
    ```

    期望响应（示例，注意 fieldErrors 的 key 是参数名）：

    ```json
    {"message":"type_mismatch","fieldErrors":{"age":"类型不匹配"}}
    ```

    3) **缺少必填参数（argument resolver 阶段）**：

    ```bash
    curl -sS -G http://localhost:8081/api/advanced/binding/age \
      -H 'Accept: application/json'
    ```

    期望响应（示例）：

    ```json
    {"message":"missing_parameter","fieldErrors":{"age":"缺少请求参数"}}
    ```

真实项目里，你可以在 `ApiError` 基础上继续扩展（例如 `timestamp/path/traceId`），但建议保持一个原则：

- **字段错误要稳定**：key 应当是“字段名/参数名”，不要塞 “某某解析失败” 的长文本
- **错误码要稳定**：客户端依赖的是 code，而不是中文/英文错误文案

#### 2.4 参数从哪来：`@RequestParam` / `@PathVariable` / `@RequestHeader`（以及缺参/类型错会发生什么）

多数接口都绕不开这三类“散件参数”：

- `@PathVariable`：路径里那一段（例如 `/api/users/{id}` 里的 `id`）
- `@RequestParam`：查询参数（例如 `?page=1&size=20`）
- `@RequestHeader`：请求头（例如 `X-Request-Id`）

在写法上它们很像，但在排障时它们的失败分支各有典型特征：

1) **缺参**（你忘了带 `age`，或者 header 没给）  
对应异常往往是：

- `MissingServletRequestParameterException`
- `MissingRequestHeaderException`
- `MissingPathVariableException`

本模块把这些异常统一塑形成 `ApiError`，你可以在 `GlobalExceptionHandler` 里看到每一类异常对应的 `message`：

- `missing_parameter` / `missing_header` / `missing_path_variable`

2) **类型不匹配**（你传了 `age=abc`，但 controller 要 `int`）  
典型异常是 `MethodArgumentTypeMismatchException`，本模块对应的错误码是 `type_mismatch`。

写到这里，你就能理解一个很实用的最佳实践：  
**对外接口的“错误码”应该刻意区分缺参与类型错**。它们对客户端的修复动作完全不同：缺参是补字段，类型错是改格式。

如果你想从最小代码开始观察 `@PathVariable` 的效果，可以看 `UserController#getUser`：

```java
@GetMapping("/{id}")
public ResponseEntity<UserResponse> getUser(@PathVariable long id) { ... }
```

这里的 `id` 不需要你手写解析，转换服务会把路径字符串转成 `long`。也正因为框架帮你做了转换，类型错时才会有那条清晰的 400/type_mismatch 分支。

#### 2.5 binder 路径：`@ModelAttribute`、表单与“为什么有时不抛异常”

在 Web MVC 里，`@RequestBody` 和 `@ModelAttribute` 不是两种写法，它们是两条通道：

- **body 通道**：`@RequestBody` → `HttpMessageConverter` 读 body（JSON、XML…）
- **binder 通道**：`@ModelAttribute`（以及默认的参数对象）→ `WebDataBinder` 绑定 query/form 参数

你在页面表单里最常用的是 binder 通道（`application/x-www-form-urlencoded`），本模块把它做成了一个可运行的“深挖入口”：

- Controller：`src/main/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BindingDeepDiveController.java`

建议你对照着看两个方法，它们只差一个参数，却会让“失败时的行为”完全不同：

- `submitForm(@Valid BindingForm form)`：**不接 `BindingResult`**，校验失败会抛 `BindException`（然后交给全局异常处理器）
- `submitFormManual(@Valid BindingForm form, BindingResult bindingResult)`：**显式接住 `BindingResult`**，校验失败就由你决定怎么返回（本模块返回 `validation_failed_manual`）

这一点对“写页面表单”尤其关键：  
页面场景下，你往往不想把失败当成异常抛出去，而是希望把错误信息带回页面做回显；这就是为什么 `BindingResult` 经常是 MVC 表单的标配。

!!! example "动手：binder 路径（表单提交）怎么跑"
    1) 表单校验失败（自动抛 `BindException`，由全局异常处理器统一塑形）：

    ```bash
    curl -sS -X POST http://localhost:8081/api/advanced/binding/form \
      -H 'Content-Type: application/x-www-form-urlencoded' \
      -H 'Accept: application/json' \
      -d 'name=' \
      -d 'email=not-an-email'
    ```

    期望响应（示例）：

    ```json
    {"message":"validation_failed","fieldErrors":{"name":"...","email":"..."}}
    ```

    2) 同样的输入，但手工接住 `BindingResult`（不走异常流，直接返回你定义的错误码）：

    ```bash
    curl -sS -X POST http://localhost:8081/api/advanced/binding/form-manual \
      -H 'Content-Type: application/x-www-form-urlencoded' \
      -H 'Accept: application/json' \
      -d 'name=' \
      -d 'email=not-an-email'
    ```

    期望响应（示例）：

    ```json
    {"message":"validation_failed_manual","fieldErrors":{"name":"...","email":"..."}}
    ```

    3) 表单输入合法（200）：

    ```bash
    curl -sS -X POST http://localhost:8081/api/advanced/binding/form \
      -H 'Content-Type: application/x-www-form-urlencoded' \
      -H 'Accept: application/json' \
      -d 'name=Alice' \
      -d 'email=alice@example.com'
    ```

#### 2.6 绑定边界：`@InitBinder` 与 mass assignment（别把小 demo 写成事故模板）

很多新手在第一次做表单/后台管理时，会直接把请求绑定到一个“看起来很方便”的对象上（甚至是持久化实体）。这会引出一个非常现实的风险：**mass assignment（批量赋值）**。

举个具体例子：你只想让用户提交 `name/email`，但请求里悄悄塞了 `admin=true`。如果你没有设置绑定边界，这个字段可能就被“顺便绑定”进去了。

本模块用 `BindingDeepDiveController#massAssignment` 演示了一个最小但有效的防线：

- 在 `@InitBinder` 里限制 `allowedFields`（只允许绑定 `name/email`）
- 即使请求里携带 `admin=true`，也不会被绑定成功

更进一步，本模块还提供了一个“把风险变成证据”的调试入口：`massAssignmentDebug` 会把 `suppressedFields`（被阻止绑定的字段）吐出来，便于排障与审计。

!!! example "动手：mass assignment（把 admin=true 送进去试试）"
    1) 直接提交 `admin=true`，但由于 `@InitBinder` 限制 allowed fields，最终不会被绑定：

    ```bash
    curl -sS -X POST http://localhost:8081/api/advanced/binding/mass-assignment \
      -H 'Content-Type: application/x-www-form-urlencoded' \
      -H 'Accept: application/json' \
      -d 'name=Alice' \
      -d 'admin=true'
    ```

    期望响应（示例）：`admin` 仍然是 `false`。

    ```json
    {"name":"Alice","admin":false}
    ```

    2) 调试入口：把 suppressed fields 作为证据吐出来（你会看到 `"admin"` 在里面）：

    ```bash
    curl -sS -X POST http://localhost:8081/api/advanced/binding/mass-assignment-debug \
      -H 'Content-Type: application/x-www-form-urlencoded' \
      -H 'Accept: application/json' \
      -d 'name=Alice' \
      -d 'admin=true'
    ```

写法上你有两种常见策略：

1) **首选：用专用 DTO（表单对象）**，不要直接绑定到领域实体  
2) **兜底：用 binder 限制 allowed fields**，把“能绑定什么”写死

这两种策略并不冲突：DTO 是结构级防线，binder 是链路级防线。

#### 2.7 方法级校验：`@Validated` + 参数约束（让 Query/Header 也能被校验）

DTO 校验（`@Valid @RequestBody`）解决的是“一个对象是否满足约束”。  
但很多时候你要校验的只是一个简单参数，比如 `age >= 0`。

这时更自然的写法是方法级校验：

- controller 上 `@Validated`
- 参数上写约束注解（例如 `@Min(0)`）

本模块的例子：

- `src/main/java/com/learning/springboot/bootwebmvc/part01_web_mvc/MethodValidationController.java`

方法级校验失败后，异常类型在不同 Spring 版本里可能略有差异（例如 `HandlerMethodValidationException` 或 `ConstraintViolationException`）。本模块在 `GlobalExceptionHandler` 里两类都处理，并且做了一件很“工程化”的事：尽量把错误 key 变成稳定的参数名。

如果你曾经被“错误体里只有 arg0/arg1”困扰过，可以直接读 `GlobalExceptionHandler#extractStableParameterName`：它优先使用 `@RequestParam/@PathVariable/@RequestHeader` 的 name/value，最后才回落到反射参数名。

!!! example "动手：方法级校验（Query 参数也能校验）"
    ```bash
    curl -sS -G http://localhost:8081/api/advanced/binding/age-validated \
      -H 'Accept: application/json' \
      --data-urlencode 'age=-1'
    ```

    期望响应（示例）：

    ```json
    {"message":"method_validation_failed","fieldErrors":{"age":"..."}}
    ```

#### 2.8 返回值与状态码：`ResponseEntity` 是你的“显式控制面板”

入门写接口时最常见的返回方式是“直接返回对象”。这当然能跑，但一旦你需要控制：

- 状态码（200/201/204/404…）
- 响应头（Location、Content-Disposition、Cache-Control…）
- content type

`ResponseEntity` 会让你的意图更清晰、可测试也更强。

本模块里有两个很典型的例子：

- `UserController#getUser`：找不到时 `ResponseEntity.notFound().build()`，明确表达“404 + 空 body”
- 文件下载：`FileTransferController#download` 会设置 `Content-Disposition` 与 `Content-Type`，让浏览器把响应当作附件下载

文件上传/下载的代码入口：

- `src/main/java/com/learning/springboot/bootwebmvc/part05_real_world/FileTransferController.java`

> 小提示：demo 里用 `byte[]` 方便理解；真实项目里大文件通常要走 streaming（避免把文件整块读进内存），这属于“真实世界 HTTP”章节会继续展开的内容。

#### 2.9 ProblemDetail：更标准的错误体（可选，但值得了解）

`ApiError` 是一种“自定义契约”，优点是你完全可控；但如果你更希望贴近标准协议，Spring 也提供了 `ProblemDetail`（RFC 7807 风格，对应 `application/problem+json`）。

本模块专门给了一个对照样例：

- Controller：`src/main/java/com/learning/springboot/bootwebmvc/part04_problemdetail/ProblemDetailDemoController.java`
- ExceptionHandler：`src/main/java/com/learning/springboot/bootwebmvc/part04_problemdetail/ProblemDetailDemoExceptionHandler.java`

建议你把它当成“工具箱里的另一种选择”，而不是“必须替换 ApiError”。更重要的原则仍然是：**选定一种形状后保持一致**，避免同一套 API 同时出现两种错误结构。

#### 2.10 Converter/Formatter：让“字符串参数”优雅地变成你的领域类型

当接口规模变大，你很快会遇到一个尴尬点：HTTP 世界里，很多输入本质都是字符串：

- `@RequestParam("id")` 是字符串
- `@PathVariable("id")` 是字符串
- 表单字段也是字符串

入门阶段我们通常直接用 `long/int/String` 接住。但真实项目里，你往往想把它们变成更有语义的类型，比如：

- `UserId`（而不是裸 `long`）
- `Money` / `Amount`（而不是裸 `BigDecimal`）
- 枚举 + 自定义解析规则

这时 “Converter/Formatter” 就是最合适的扩展点：它把“解析规则”集中起来，避免每个 controller 都写一段 `Long.parseLong(...)`。

本模块的深挖章节在这里（建议配合断点一起看）：

- `06-binding-validation/03-binding-and-converters.md`

你不需要一上来就把 Converter 体系全记住，但建议牢记一个判断标准：  
**只要解析规则需要复用（或者需要统一错误分支），就该把它收敛成 Converter/Formatter。**

### 3. `@Controller` + Thymeleaf：页面渲染与表单闭环（回显 + PRG）

Web MVC 的另一条主线是传统页面（HTML）：

- `@Controller` 返回的是 viewName（字符串）或 `ModelAndView`
- viewName 最终会被 ViewResolver 解析为模板（例如 Thymeleaf）

本模块的最小例子：

- Ping 页面：`src/main/java/com/learning/springboot/bootwebmvc/part02_view_mvc/MvcPingController.java`
- 用户表单：`src/main/java/com/learning/springboot/bootwebmvc/part02_view_mvc/MvcUserController.java`

这一部分最容易踩的坑有两个：

1) **表单校验失败为什么不抛异常？**  
因为表单通常走的是 `@ModelAttribute` + binder 路径，配合 `BindingResult`。当你显式接住 `BindingResult` 时，框架会把“错误”交给你决定如何渲染（回表单页），而不是统一抛异常走全局 handler。

2) **为什么建议 PRG（Post-Redirect-Get）？**  
因为表单提交成功后如果直接返回详情页模板，用户刷新就可能重复提交；PRG 让成功后变成一个 GET，并且用 Flash Attributes 传递一次性的提示信息。

本模块的 PRG 写法在 `MvcUserController#createUser` 里：

- 校验失败：返回 `"pages/user-form"`（回显）
- 创建成功：`redirect:/pages/users/{id}` + `addFlashAttribute("flashMessage", ...)`

#### 3.1 错误页与 Accept：为什么浏览器看到的是页面，脚本拿到的是 JSON

同一个错误（例如 404 或抛异常），在浏览器里通常会看到一张 HTML 错误页；但当你用脚本/客户端请求（带 `Accept: application/json`）时，又可能拿到 JSON 错误体。

这不是“随机行为”，而是内容协商与错误处理共同作用的结果：

- 对页面请求：更希望渲染模板（例如 `templates/error/404.html`）
- 对 API 请求：更希望返回结构化错误体（例如 `ApiError`）

本模块的“页面错误处理”入口：

- 异常 demo：`src/main/java/com/learning/springboot/bootwebmvc/part02_view_mvc/MvcErrorDemoController.java`
- 页面异常处理器：`src/main/java/com/learning/springboot/bootwebmvc/part02_view_mvc/MvcExceptionHandler.java`
- 错误页模板：`src/main/resources/templates/error/404.html` / `4xx.html` / `5xx.html`

如果你曾经纠结过“为什么我明明写了 `@ExceptionHandler`，浏览器还是跳到了错误页”，这一节建议你带着 `Accept` 头去复现：你会很直观地看见内容协商的分支。

### 4. Filter vs Interceptor：什么时候用谁（以及顺序为什么总被搞混）

很多“我以为会生效但没生效”的问题，根因是：你把逻辑放错了层。

- Filter：Servlet 最外层，进 `DispatcherServlet` 之前就可能发生（Spring Security 也在这里）
- Interceptor：MVC 链路内部（handler 前后），只对命中 handler 的请求有效

本模块用一个“事件序列”把它们的相对位置做成可观察证据：

- Filter：`src/main/java/com/learning/springboot/bootwebmvc/part03_internals/WebMvcTraceFilter.java`
- Interceptor：`src/main/java/com/learning/springboot/bootwebmvc/part03_internals/WebMvcTraceInterceptor.java`
- 注册：`src/main/java/com/learning/springboot/bootwebmvc/part03_internals/WebMvcTraceConfig.java`

同步时顺序固定：Filter 包住 MVC；异步时会出现第二次 dispatch（REQUEST → ASYNC）。

!!! example "动手：用事件序列看顺序（这是本模块最“硬”的证据）"
    1) 同步请求：`/api/advanced/trace/sync`

    ```bash
    curl -sS http://localhost:8081/api/advanced/trace/sync
    ```

    期望 `events`（固定证据，来自 `BootWebMvcTraceLabTest`）：

    ```json
    [
      "filter:before[REQUEST]",
      "interceptor:preHandle[REQUEST]",
      "handler:sync[REQUEST]",
      "interceptor:postHandle[REQUEST]",
      "interceptor:afterCompletion[REQUEST]",
      "filter:after[REQUEST]"
    ]
    ```

    2) 异步请求：`/api/advanced/trace/async`

    ```bash
    curl -sS http://localhost:8081/api/advanced/trace/async
    ```

    期望你能在事件里同时看到 **REQUEST** 与 **ASYNC** 两轮（关键点：`afterConcurrentHandlingStarted` 只在第一次 REQUEST 出现）：

    ```json
    [
      "filter:before[REQUEST]",
      "interceptor:preHandle[REQUEST]",
      "handler:async[REQUEST]",
      "interceptor:afterConcurrentHandlingStarted[REQUEST]",
      "filter:after[REQUEST]",
      "interceptor:preHandle[ASYNC]",
      "interceptor:postHandle[ASYNC]",
      "interceptor:afterCompletion[ASYNC]"
    ]
    ```

### 5. 406/415 与内容协商：REST 合同为什么要“写死”

Web 接口里最值得花时间讲清楚的两类错误是 406 与 415，因为它们几乎都不是业务 bug，而是“契约没对齐”：

- **415 Unsupported Media Type**：请求体读不进来（`Content-Type` 不支持）
- **406 Not Acceptable**：响应体写不出去（`Accept` 不支持）

本模块不仅讲概念，还给了“严格 JSON”的落地方式：

- 定义一个 vendor media type：`application/vnd.learning.strict+json`
- 用单独的 `ObjectMapper` 开启 `FAIL_ON_UNKNOWN_PROPERTIES`
- 把它放到 converter 列表前面，让它优先命中

代码位置：

- `src/main/java/com/learning/springboot/bootwebmvc/part04_contract/StrictJsonMessageConverterConfig.java`
- `src/main/java/com/learning/springboot/bootwebmvc/part04_contract/RestContractController.java`
- 更细的错误塑形：`src/main/java/com/learning/springboot/bootwebmvc/part04_contract/AdvancedApiExceptionHandler.java`

!!! example "动手：406 vs 415（用两条请求把 read/write 分支跑出来）"
    1) 415（读不进来）：`Content-Type` 不对，`@RequestBody` 读 body 时找不到可读 converter。

    ```bash
    curl -i -X POST http://localhost:8081/api/advanced/contract/echo \
      -H 'Content-Type: text/plain' \
      -d 'hello'
    ```

    期望：`HTTP/1.1 415`。  
    断点建议：`AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`。

    2) 406（写不出去）：`Accept` 不对，返回值写回响应时找不到可写 converter。

    ```bash
    curl -i http://localhost:8081/api/advanced/contract/ping \
      -H 'Accept: text/plain'
    ```

    期望：`HTTP/1.1 406`。  
    断点建议：`AbstractMessageConverterMethodProcessor#writeWithMessageConverters`。

真实项目里，“严格 JSON”不是必须，但它提供了一个很重要的思路：  
**把“前后端契约”从口头约定升级为可验证的分支**。你甚至可以把它做成灰度：对关键端点启用严格模式，其它端点保持宽松。

### 6. 测试：`@WebMvcTest` vs `@SpringBootTest`，不是谁更强，而是谁更合适

入门阶段最常见的误区是：一上来就 `@SpringBootTest` 跑全量上下文，导致测试慢、定位难；或者只用 `@WebMvcTest`，却误以为“全链路都没问题”。

建议你用一句话区分它们：

- `@WebMvcTest`：验证“Web 层契约”（路由、绑定、校验、异常塑形、内容协商），快且定位精确
- `@SpringBootTest`：验证“整体集成边界”（Filter/Security/真实端口/配置加载），更贴近真实运行

本模块两类入口都有，对比着跑一遍就有体感：

- `BootWebMvcLabTest`（切片）
- `BootWebMvcSpringBootLabTest`（全量）

!!! example "干货：测试里如何“用证据定位分支”"
    **1) 415/406：别猜，直接看 `resolvedException`**

    ```java
    MvcResult result = mockMvc.perform(post("/api/advanced/contract/echo")
            .contentType(MediaType.TEXT_PLAIN)
            .content("hello"))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(415);
    assertThat(result.getResolvedException()).isInstanceOf(HttpMediaTypeNotSupportedException.class);
    ```

    **2) Security 401/403：常见证据是 `handler == null` 且 `resolvedException == null`**

    ```java
    MvcResult result = mockMvc.perform(get("/api/advanced/secure/ping"))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(401);
    assertThat(result.getHandler()).isNull();
    assertThat(result.getResolvedException()).isNull();
    ```

### 7. 真实世界 HTTP：CORS、上传下载、缓存与条件请求（把“能用”推到“可上线”）

写 API 的时候，我们经常在本地用 `curl` 或 Postman 调通就觉得“完成了”。但一旦接口进入真实环境，会立刻遇到这些更具体的挑战：

- 前端跨域调用（浏览器 CORS）
- 文件上传下载（multipart/附件下载）
- 静态资源与缓存（CSS/JS 的缓存策略）
- 条件请求（ETag/Last-Modified/304），以及“为什么 304 没有响应体”

这些问题的共同点是：它们多半不是业务代码错，而是你需要把 HTTP 层的边界补齐。

#### 7.1 CORS：为什么你明明写了 GET 接口，浏览器却先发了一个 OPTIONS

浏览器跨域时，经常会出现“预检请求”（preflight）：

- 浏览器先发 `OPTIONS`，询问服务器：我能不能用这些 method/header 去访问？
- 服务器回答允许后，浏览器才会真的发 `GET/POST`

本模块给了一个最小可理解的 CORS 配置：

- `AdvancedCorsConfig`：`src/main/java/com/learning/springboot/bootwebmvc/part05_real_world/AdvancedCorsConfig.java`
- 示例端点：`/api/advanced/cors/**`（见 `CorsDemoController`）

读这段配置时别死记参数，抓住三个“契约点”就够：

1) 谁可以来（origin）  
2) 可以做什么（methods/headers）  
3) 预检结果可以缓存多久（maxAge）  

!!! example "动手：用 curl 模拟一次预检（OPTIONS）"
    ```bash
    curl -i -X OPTIONS http://localhost:8081/api/advanced/cors/ping \
      -H 'Origin: https://example.com' \
      -H 'Access-Control-Request-Method: GET' \
      -H 'Access-Control-Request-Headers: X-Request-Id'
    ```

    期望你能在响应头里看到类似这些字段（名字是重点，值与顺序不必死记）：

    - `Access-Control-Allow-Origin: https://example.com`
    - `Access-Control-Allow-Methods: GET`
    - `Access-Control-Allow-Headers: X-Request-Id`
    - `Access-Control-Max-Age: 3600`

#### 7.2 上传：multipart 不是 JSON，它走的是另一条解析通道

上传接口最大的不同是 `Content-Type`：

- JSON：`application/json`
- 上传：`multipart/form-data`

这意味着它不会走 `@RequestBody` 的 JSON converter，而是走 multipart 的解析与绑定。

本模块的上传入口：

- `POST /api/advanced/files/upload`
- 代码位置：`FileTransferController#upload`

入门阶段建议你记住两个实践点：

1) `@PostMapping(consumes = MULTIPART_FORM_DATA)` 是契约，不要省  
2) 上传对象用 `MultipartFile` 接住，不要自己手工解析 request

!!! example "动手：上传一个文件"
    ```bash
    curl -sS -X POST http://localhost:8081/api/advanced/files/upload \
      -H 'Accept: application/json' \
      -F 'file=@spring-boot-modules/spring-boot-web-mvc/README.md'
    ```

    期望响应包含 `id/fileName/size/contentType`。拿到 `id` 后用于下载。

#### 7.3 下载：一个“看起来简单”的接口，其实是在教你如何正确写 headers

下载接口常见 bug 不在 body，而在 headers：

- 缺少 `Content-Disposition`：浏览器不下载，直接当文本打开
- `Content-Type` 错：客户端拿不到正确的文件类型

本模块的下载入口：

- `GET /api/advanced/files/{id}`
- 代码位置：`FileTransferController#download`

!!! example "动手：下载（以及 404 的错误码）"
    1) 下载（把 `{id}` 换成上传返回的 id）：

    ```bash
    curl -OJ http://localhost:8081/api/advanced/files/{id}
    ```

    2) 下载不存在的 id（404）：  
    这个分支来自 `ResponseStatusException`（reason 为 `file_not_found`），会被错误处理器塑形为 `ApiError`。

    ```bash
    curl -i http://localhost:8081/api/advanced/files/999999 \
      -H 'Accept: application/json'
    ```

#### 7.4 静态资源与缓存：别让浏览器每次都“重新下载一遍世界”

静态资源（CSS/JS/图片）是最典型的缓存受益者：  
缓存策略合理时，页面加载会快很多；策略混乱时，你会遇到两种极端：

- 开发时改了样式，用户端怎么也不更新（缓存太强）
- 线上每次都重新拉资源，性能像没开缓存（缓存太弱）

Spring Boot 默认会把 `classpath:/static` 下的文件当作静态资源服务，本模块也提供了一个最小的静态资源示例：

- `src/main/resources/static/css/app.css`

!!! example "动手：看静态资源的缓存相关响应头"
    ```bash
    curl -I http://localhost:8081/css/app.css
    ```

    建议重点看这些头（是否出现、值是否合理）：

    - `Cache-Control`
    - `ETag`
    - `Last-Modified`

更系统的解释（缓存、版本化、调试姿势）在本模块的章节里：

- `13-real-world-http/04-static-resources-and-cache.md`

#### 7.5 条件请求：ETag/304 为什么重要、为什么 304 通常没有 body

当资源没变时，服务器没必要重复发送同样的 body。ETag 的玩法就是：

- 服务器给资源一个“内容指纹”（ETag）
- 客户端下次带上 `If-None-Match`
- 指纹命中则返回 304（Not Modified），body 省掉

本模块提供了两种写法的对照：

1) controller 里显式处理（更直观）：`ApiEtagDemoController#etagDemo`
2) 用 `ShallowEtagHeaderFilter` 自动计算（更贴近工程）：`CacheEtagFilterConfig`

入口与代码位置：

- `src/main/java/com/learning/springboot/bootwebmvc/part05_real_world/ApiEtagDemoController.java`
- `src/main/java/com/learning/springboot/bootwebmvc/part05_real_world/CacheEtagFilterConfig.java`

!!! example "动手：304（If-None-Match 命中就不回 body）"
    1) 先请求一次，记下响应头里的 `ETag`：

    ```bash
    curl -i http://localhost:8081/api/advanced/cache/etag
    ```

    2) 再带上 `If-None-Match`（把引号也带上）：

    ```bash
    curl -i http://localhost:8081/api/advanced/cache/etag \
      -H 'If-None-Match: "<上一步返回的 ETag 值>"'
    ```

    期望：`HTTP/1.1 304`，并且通常没有响应体。

### 8. 异步与 SSE：一次请求为什么会“走两遍”（以及怎么测试）

第一次接触 Servlet async 时，最容易产生错觉的是：**为什么同一个请求像是被处理了两次**？

原因很简单：第一次进入 MVC 只是“注册异步处理”；真正写回响应发生在后续的 async dispatch。

本模块用三个入口把 async 的几种典型形态拆开讲：

- `Callable`：`GET /api/advanced/async/ping`
- `DeferredResult`：`GET /api/advanced/async/deferred`（稳定触发 `asyncStarted → asyncDispatch`）
- SSE：`GET /api/advanced/sse/ping`（`text/event-stream`）

代码位置：

- `AsyncDemoController`：`src/main/java/com/learning/springboot/bootwebmvc/part06_async_sse/AsyncDemoController.java`
- `SseDemoController`：`src/main/java/com/learning/springboot/bootwebmvc/part06_async_sse/SseDemoController.java`

!!! example "动手：跑一遍 async 与 SSE（不用猜线程）"
    1) `Callable`（注意响应里会带线程名）：

    ```bash
    curl -sS http://localhost:8081/api/advanced/async/ping
    ```

    期望响应结构（示例）：

    ```json
    {"message":"pong","thread":"..."}
    ```

    2) `DeferredResult`（结果在异步线程里 set 回来）：

    ```bash
    curl -sS http://localhost:8081/api/advanced/async/deferred
    ```

    3) `DeferredResult` 超时分支（教学用：返回 `"timeout"`）：

    ```bash
    curl -sS http://localhost:8081/api/advanced/async/deferred-timeout
    ```

    4) SSE（建议加 `-N` 关闭 curl 缓冲）：

    ```bash
    curl -N http://localhost:8081/api/advanced/sse/ping
    ```

    期望你能看到两次 `event: ping`（格式类似）：

    ```text
    event:ping
    data:ping-1

    event:ping
    data:ping-2
    ```

想把“二次 dispatch”看得更清楚，可以结合 trace filter/interceptor 的事件序列（见本章第 4 节），再配合 Lab：

- `BootWebMvcAsyncSseLabTest`
- `BootWebMvcTraceLabTest`

### 9. 安全与观测：把“边界”写清楚（入门版）

这一点会在第二部分再深入，但入门阶段至少要建立两个直觉：

1) **安全通常发生在 MVC 之前**（FilterChain），401/403 很多时候与你写的 controller 无关  
2) **观测要靠证据**：耗时、选中的 converter、选中的 content type，都应该能被稳定观察到（响应头/日志/指标）

本模块把这些“直觉”落成了可运行入口：

- Security：`BootWebMvcSecurityLabTest`
- Observability：`BootWebMvcObservabilityLabTest`

!!! example "动手：安全（401/403/200）与观测（响应头/metrics）"
    **1) 401：未认证（发生在 FilterChain，通常进不了 HandlerMethod）**

    ```bash
    curl -i http://localhost:8081/api/advanced/secure/ping \
      -H 'Accept: application/json'
    ```

    **2) 200：HTTP Basic 认证通过**

    ```bash
    curl -i -u user:password http://localhost:8081/api/advanced/secure/ping \
      -H 'Accept: application/json'
    ```

    **3) 403：权限不足（USER 访问 ADMIN 资源）**

    ```bash
    curl -i -u user:password http://localhost:8081/api/advanced/secure/admin/ping \
      -H 'Accept: application/json'
    ```

    **4) 403：CSRF 缺失（即使已认证）**

    ```bash
    curl -i -u user:password -X POST http://localhost:8081/api/advanced/secure/update \
      -H 'Accept: application/json'
    ```

    > CSRF“带 token 成功”的 curl 版本写起来很绕（涉及 session/cookie）。这里更推荐直接跑固定用例：
    >
    > `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcSecurityLabTest#csrfProvidedReturns200 test`

    **5) 观测：`X-Lab-Elapsed-Ms`（Interceptor 计时写入响应头）**

    ```bash
    curl -i http://localhost:8081/api/advanced/contract/ping
    ```

    **6) 观测：Actuator metrics（`http.server.requests`）**

    ```bash
    curl -sS http://localhost:8081/actuator/metrics/http.server.requests
    ```

### 10. 并发与性能：别在 controller 里埋雷（选读）

demo 里为了简单，经常会在 controller 里放一个 `Map` 当存储。这样写能跑，但也会顺带教你一个工程事实：

- Web 请求是并发的；如果你在 controller 里持有可变共享状态，就必须考虑线程安全

本模块里 `UserController`/`MvcUserController` 用了 `ConcurrentHashMap` 与 `AtomicLong`，这是“演示用的最低限度”。真实项目里更推荐把状态交给数据库/缓存/服务层，不要把 controller 写成共享状态容器。

另外，本模块还有一个并发边界相关的 Lab（RequestScope 隔离），用于帮助你理解“每个请求的上下文”到底如何隔离：

- `BootWebMvcRequestScopeIsolationLabTest`

---

## 第二部分：机制深挖（调用链、断点、分支矩阵）

这一部分的目标也很明确：**把“排障”变成套路，而不是灵感**。当你遇到问题时，能做到：

1) 先用证据确定分支（异常类型、发生阶段）  
2) 再用断点确认“是谁做了决定”  
3) 最后把修复固化成测试（避免回归）  

### 1. 一条请求的主轴：你应该把它背成肌肉记忆

把主轴记成一句话就够：

> HTTP 请求 → FilterChain → `DispatcherServlet#doDispatch` → HandlerMapping/HandlerAdapter → 参数解析/绑定/校验 → 返回值处理/消息转换 → ExceptionResolvers 收敛错误

全图与断点入口已经整理在两章里：

- 知识地图：`00-guide/05-knowledge-map.md`
- 断点地图：`14-testing-observability/06-breakpoint-map.md`

这两份文档的价值在于：当你迷路时，它们告诉你“该把断点下在哪里”，而不是把你丢进海量日志里游泳。

### 2. 分支矩阵：400/406/415 不是三个数字，它们是三条不同的路

下面是一张“最小但够用”的矩阵。你可以把它当作排障时的第一张卡片：

| 你看到的状态码 | 更可能的根因 | 典型异常（证据） | 关键断点（决策点） | 最小复现入口（本模块） |
| --- | --- | --- | --- | --- |
| 400 | JSON 解析失败 | `HttpMessageNotReadableException` | `AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters` | `BootWebMvcErrorBranchMatrixLabTest#branch400_whenJsonIsMalformed` |
| 400 | 校验失败（body） | `MethodArgumentNotValidException` | `SpringValidatorAdapter#validate` | `BootWebMvcErrorBranchMatrixLabTest#branch400_whenValidationFails` |
| 400 | 校验失败（binder） | `BindException` | `WebDataBinder#bind` / `DataBinder#validate` | `BootWebMvcBindingDeepDiveLabTest` |
| 400 | 类型不匹配 | `MethodArgumentTypeMismatchException` | `HandlerMethodArgumentResolverComposite#resolveArgument` | `BootWebMvcErrorBranchMatrixLabTest#branch400_whenRequestParamTypeMismatch` |
| 415 | 读不到 body | `HttpMediaTypeNotSupportedException` | `readWithMessageConverters` | `BootWebMvcErrorBranchMatrixLabTest#branch415_whenContentTypeIsNotSupported` |
| 406 | 写不出 body | `HttpMediaTypeNotAcceptableException` | `AbstractMessageConverterMethodProcessor#writeWithMessageConverters` | `BootWebMvcErrorBranchMatrixLabTest#branch406_whenAcceptIsNotSupported` |

矩阵完整版与推荐断点在这里：

- `14-testing-observability/04-branch-decision-matrix.md`

### 3. 断点怎么用：不是“下很多”，而是“下对位置”

排障时建议按“从外到内”的顺序，不要一上来就怀疑 controller：

1) **先证明请求有没有进入 MVC**  
入口断点：`org.springframework.web.servlet.DispatcherServlet#doDispatch`  
如果压根没进（例如被 Security 拦在 FilterChain），你在 controller 里加日志加到天亮也看不到。

2) **再证明选路有没有命中 handler**  
入口断点：`RequestMappingHandlerMapping#getHandlerInternal`  
观察 `mappedHandler` 是否为 null（404 的第一手证据）。

3) **再看参数是怎么来的、在哪一步失败的**  
入口断点：`HandlerMethodArgumentResolverComposite#resolveArgument`  
`@RequestBody` 的读入在 `readWithMessageConverters`，`@ModelAttribute` 的绑定在 `WebDataBinder#bind`。

4) **最后才看异常如何被翻译**  
入口断点：`DispatcherServlet#processHandlerException` / `ExceptionHandlerExceptionResolver#doResolveHandlerMethodException`  
在这里你能一眼看出：是你写的 `@ControllerAdvice` 生效，还是回落到默认 resolver。

断点清单可以直接照搬本模块的“断点地图”：

- `14-testing-observability/06-breakpoint-map.md`

### 4. `@ControllerAdvice` 为什么会“没生效”：先问匹配，再问顺序

当你写了 `@RestControllerAdvice/@ControllerAdvice`，却发现响应体还是默认错误页（或根本没走到你的 handler），这通常不是“Spring 不工作”，而是你漏了两个问题：

1) **它匹配到你这个 controller 了吗？**（matching）  
2) **如果有多个 advice 都能处理，它排在第几个？**（ordering）  

#### 4.1 匹配：advice 不是全局生效，它可以被限定范围

`@ControllerAdvice` 可以用 selector 限定适用范围，例如 `basePackages/annotations/assignableTypes`。这意味着：  
你写的 advice 可能根本没有进入“候选集合”。

本模块提供了一套专门用来理解 matching 的演示：

- 代码包：`src/main/java/com/learning/springboot/bootwebmvc/part10_advice_matching/`
- Lab：`BootWebMvcAdviceMatchingLabTest`

这套演示的好处是：它把“哪个 advice 生效”直接写进响应体（`ApiError.message`），你不用靠猜就能确认匹配是否发生。

!!! example "动手：advice matching（看 message/selector 就知道命中的 advice）"
    下面这些请求都会抛同一种业务异常（`AdviceMatchingDemoException`），区别只在 controller 的“所属包/标记接口/标记注解”不同：

    ```bash
    curl -sS http://localhost:8081/api/advanced/advice-matching/plain
    curl -sS http://localhost:8081/api/advanced/advice-matching/annotations-only
    curl -sS http://localhost:8081/api/advanced/advice-matching/assignable-only
    curl -sS http://localhost:8081/api/advanced/advice-matching/base-package-only
    curl -sS http://localhost:8081/api/advanced/advice-matching/composite
    ```

    你不需要猜命中了哪个 advice：响应体会直接告诉你。

    - `message` 是命中的 advice 名称（例如 `advice_global` / `advice_annotations` / `advice_assignable` / `advice_base_packages`）
    - `fieldErrors.selector` 是命中的 selector（`basePackages` / `annotations` / `assignableTypes`）

#### 4.2 顺序：当两个 advice 都能处理同一个异常时，谁先处理？

当多个 advice 同时命中时，`@Order` 决定优先级。  
本模块用 `part09_advice_order` 做了一个最小演示：

- `HighPriorityAdvice`（`@Order(1)`）
- `LowPriorityAdvice`（`@Order(2)`）
- 对应 controller：`AdviceOrderController#boom`
- Lab：`BootWebMvcAdviceOrderLabTest`

!!! example "动手：@Order（谁先处理）"
    ```bash
    curl -sS http://localhost:8081/api/advanced/advice-order/boom
    ```

    期望：命中 `@Order(1)` 的 advice（示例响应）：

    ```json
    {"message":"high_priority_advice","fieldErrors":{"source":"high"}}
    ```

当你真正遇到“为什么没走我写的 handler”时，建议用两类证据去验证：

- 证据 1：在响应体里把“生效来源”写出来（教学里用 `ApiError.message`；工程里可用日志/traceId）
- 证据 2：在断点里看 resolver 的决策过程（推荐断点：`ExceptionHandlerExceptionResolver#doResolveHandlerMethodException`）

更深入的机制解释见：

- `10-exception-resolvers/05-controlleradvice-matching-and-ordering.md`

### 5. Security 与 MVC 的边界：为什么 401/403 常常“发生在你看不见的地方”

当你引入 Spring Security 后，最需要建立的第一条边界是：

> Security 在 FilterChain 里，通常早于 DispatcherServlet；也就是说很多 401/403 发生时，controller 根本没有被调用。

本模块专门有一组 Labs 把这条边界做成可复现事实：

- `BootWebMvcSecurityLabTest`
- `BootWebMvcSecurityVsMvcExceptionBoundaryLabTest`

对应配置也写得很“教学友好”：

- `src/main/java/com/learning/springboot/bootwebmvc/part08_security_observability/SecurityConfig.java`

建议你读 `SecurityConfig` 时特别关注两点：

1) **为什么会有两条 `SecurityFilterChain`**  
因为教学端点希望演示 CSRF/权限，而其它端点为了不影响既有 labs，需要默认放行并关闭 CSRF。  
这正是工程里常见的做法：用 matcher 把不同区域的安全策略拆开。

2) **为什么 `@Order` 很重要**  
多条 chain 并存时，先匹配的先处理；顺序错了，你以为“只保护 /secure/**”，结果把全站都保护了。

这里有个很现实的最佳实践：  
**把“安全失败的分支”在测试里稳定复现**（包括 CSRF 缺失导致的 403），否则你永远在“环境差异”里纠缠。

### 6. 观测（Observability）：让耗时、选择的 converter 变成证据

排障不只是“看异常”。很多问题是“性能/耗时/契约选择”的问题，最有效的做法是把关键决策写成可观察信号：

- 用 `Interceptor` 记录耗时，把结果写到响应头（发生在 body 写出之前）
  - `TimingInterceptor`：`src/main/java/com/learning/springboot/bootwebmvc/part08_security_observability/TimingInterceptor.java`
  - `TimingResponseBodyAdvice`：`src/main/java/com/learning/springboot/bootwebmvc/part08_security_observability/TimingResponseBodyAdvice.java`
- 用 `ResponseBodyAdvice` 把“选了哪个 converter/Content-Type”写到响应头，专门用来理解 406/内容协商
  - `MessageConverterTraceAdvice`：`src/main/java/com/learning/springboot/bootwebmvc/part03_internals/MessageConverterTraceAdvice.java`

本模块的 message converter trace 还提供了两个“非常适合拿来做证据”的响应头：

- `X-Lab-Selected-Converter`
- `X-Lab-Selected-Content-Type`

对应端点与代码：

- `MessageConverterTraceController`：`src/main/java/com/learning/springboot/bootwebmvc/part03_internals/MessageConverterTraceController.java`

!!! example "动手：用响应头固定“选择了哪个 converter”"
    1) JSON：

    ```bash
    curl -i http://localhost:8081/api/advanced/message-converters/json \
      -H 'Accept: application/json'
    ```

    期望响应头包含（示例）：

    - `X-Lab-Selected-Converter: MappingJackson2HttpMessageConverter`
    - `X-Lab-Selected-Content-Type: application/json`

    2) strict JSON（vendor media type）：

    ```bash
    curl -i http://localhost:8081/api/advanced/message-converters/strict-json \
      -H 'Accept: application/vnd.learning.strict+json'
    ```

    期望 `X-Lab-Selected-Converter` 变成 `StrictJsonMessageConverter`。

当你在真实项目里遇到“为什么返回的不是我以为的格式”时，这类证据比翻配置、猜协商规则有效得多。

这些做法在真实项目里同样成立：你可以不把它们暴露给公网，但可以在 debug 环境、内部端点、或者日志/指标里保留“证据”。

---

## 30 分钟练习：把“读懂”变成“做会”

如果你只打算投入半小时，建议按这个顺序跑：

1. 跑 Book Matrix（先把主线跑通）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
2. 跑 Branch Matrix（把 400/406/415 固化为分支证据）：`mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`
3. 在 IDE 里只下 3 个断点观察一次分支：
   - `DispatcherServlet#doDispatch`
   - `readWithMessageConverters`（读）
   - `writeWithMessageConverters`（写）
4. 回到应用层读一章：`06-binding-validation/01-validation-and-error-shaping.md`

---

## 小结与下一章

这一章做了两件事：

1. 把“入门写法”讲成闭环：Controller → DTO/校验 → 统一错误体 → 合同（406/415）→ 测试固化；
2. 把“机制排障”讲成套路：用分支矩阵先定性，再用断点定点，最后把修复变成可回归事实。

下一章开始进入“主线时间线”，你会把这一章的直觉（写法与排障）落到更精确的调用链与阶段边界上：

- [01. 主线时间线：Spring Boot Web MVC](01-mainline-timeline.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootWebMvcBookMatrixLabTest`
- Matrix：`BootWebMvcErrorBranchMatrixLabTest`
- Lab：`BootWebMvcLabTest` / `BootWebMvcSpringBootLabTest`
- Lab：`BootWebMvcBindingDeepDiveLabTest`
- Lab：`BootWebMvcMessageConverterTraceLabTest`
- Lab：`BootWebMvcSecurityLabTest` / `BootWebMvcSecurityVsMvcExceptionBoundaryLabTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 主线时间线：Spring Boot Web MVC](01-mainline-timeline.md)
<!-- BOOKIFY:END -->
