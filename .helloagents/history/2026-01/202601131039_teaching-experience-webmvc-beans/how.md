# Technical Design: 教学体验升级（springboot-web-mvc + spring-core-beans）

## Technical Solution

### Core Technologies

- Java 17
- Spring Boot 3.5.9（对应 Spring Framework 6.x）
- Maven（多模块聚合）
- JUnit 5（Labs/Exercises/Solutions）
- GitHub Actions（CI）

### Implementation Key Points

#### 1) 以“测试入口”为主锚点（避免文档漂移）

- 所有 Debugger Pack 的第一入口必须落在仓库内的 `*LabTest/*ExerciseTest` 方法上，避免读者从框架源码“无入口漂流”。
- 文档中对框架内部断点的描述，必须以“可替代的定位策略”兜底：
  - 先给出推荐断点（最常见版本路径）
  - 再给出 1–2 个备选关键字/类型名（便于搜索定位）
  - 强调观察对象与状态（watchpoints），弱化对具体方法名的依赖

#### 2) springboot-web-mvc Part 01：补齐 Debugger Pack（断点入口 + 观察点 + 分支证据链）

范围优先覆盖 Part 01 的 4 个核心章节：
- 校验与错误塑形
- 异常处理
- 绑定与 converters
- Filter/Interceptor 顺序

每章补齐内容的统一结构（建议固定在原有 `## Debug 建议` 下）：
- **Entrypoints（至少 2 个）：**
  - 入口 1：从对应的 `*LabTest` 方法开始（MockMvc / SpringBootTest）
  - 入口 2：框架主入口（通常是 `DispatcherServlet` 请求分发主线）
- **Watchpoints（至少 3 个）：**
  - 请求侧：path/method/headers（Accept/Content-Type）、请求 attribute
  - Handler 侧：最终命中的 `HandlerMethod`、参数解析器、binder 相关对象
  - 响应侧：status、headers、错误响应体形状（ApiError/ProblemDetail）
- **Decisive branch（至少 1 个）：**
  - 触发条件（例如：是否携带 `@Valid`、是否命中 `@ExceptionHandler`、是否发生 binding error）
  - 分歧路径（命中/不命中）
  - 可观察证据（测试断言/日志/响应体/响应头）

可选增强（如果必要）：为模块增加一个 “Breakpoint Map” 页面，把 Part 01 的高频断点集中收敛到 `docs/part-00-guide/`，并从 `docs/README.md` 的 Start Here 链接过去。

#### 3) spring-core-beans：新增 30 分钟快启章节（先快后深）

新增 `part-00-guide/01-quickstart-30min.md`，只做三件事：
- 选 3 个“高回报”的最小实验入口（基于现有 LabTest，避免新增不必要代码）
- 每个实验写清楚：跑什么、看什么、在哪打断点、看哪些观察点
- 结束后给出“下一步深挖推荐链接”（回到 `00-deep-dive-guide.md` 与 Part 01/03 的核心章节）

同时在 `docs/beans/spring-core-beans/README.md` 强化 Start Here，把快启路线放到最前面，降低第一次进入的心理负担。

- 让仓库提供可记忆的固定命令入口（脚本可直接执行；README 使用脚本示例）。
- 可选：增加“章节契约检查”脚本，检测新增章节是否包含最小实验入口块与 Debugger Pack（优先对样板模块启用）。

## Security and Performance

- **Security:**
  - 不引入任何密钥/Token；不连接生产环境；不新增外部服务依赖
  - 文档示例避免泄漏本机路径、个人信息与真实凭据
- **Performance:**
  - 文档检查保持轻量（纯静态分析、无网络依赖）
  - CI 分 job 执行，避免把 doc 检查耦合进 Maven test（便于并行与排障）

## Testing and Deployment

- **Testing:**
  - `mvn -q -pl springboot-web-mvc test`
  - `mvn -q -pl spring-core-beans test`
  - （可选）`mvn -q test` 做全仓回归
- **Deployment:** 无（纯教学工程与文档增强）

