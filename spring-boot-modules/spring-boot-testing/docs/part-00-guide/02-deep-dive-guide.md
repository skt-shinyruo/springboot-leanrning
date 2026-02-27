# 02. 00 - Deep Dive Guide（springboot-testing）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Deep Dive Guide（springboot-testing）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：按目标选择测试切片（如 `@WebMvcTest`）或全量上下文（`@SpringBootTest`）；用 mock/替身把外部依赖固定成可断言证据。
    - 原理：测试注解决定上下文装配范围 → TestContext 缓存与复用 → slice/full context 的权衡 → 断言固化机制结论 → 快速定位失败。
    - 源码入口：`org.springframework.boot.test.context.SpringBootTest` / `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest` / `org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate`
    - 推荐 Lab：`BootTestingMockBeanLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：Spring Boot Testing](01-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 01 - Slice 与 Mock（Testing）](../part-01-testing/01-slice-and-mocking.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02. 00 - Deep Dive Guide（springboot-testing）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，应当能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`

## 机制主线

这一模块的核心不是“会写测试注解”，而是把测试当成一条可控的“启动主线”：

- 选择什么注解（slice / full） → 得到什么 bean 图 → 能断言什么边界
- 用什么方式替换依赖（`@MockBean`） → 替换发生在哪里 → 误用会带来什么假阳性

### 1) 时间线：一条测试从启动到断言的主线

1. **选择测试类型**
   - `@WebMvcTest`：只启动 MVC 相关（Controller/Filter/Converter 等），默认不加载 service/repository
   - `@SpringBootTest`：完整启动（更接近真实运行时）
2. **构建测试上下文（bean 图）**
   - slice 是“缩小上下文”；full 是“完整上下文”
3. **可选：替换某些 bean**
   - `@MockBean` 把某个类型的 bean 替换成 Mockito mock（在上下文里“占位/覆盖”）
4. **执行请求并断言**
   - `@WebMvcTest` 常用 `MockMvc`
   - `@SpringBootTest(webEnvironment = RANDOM_PORT)` 常用 `TestRestTemplate`

### 2) 关键参与者（应当能解释它们解决什么问题）

- `@WebMvcTest`：把“controller 层契约”从业务实现中切出来（更快、更聚焦）
- `@SpringBootTest`：把“真实启动边界”锁定（配置、自动装配、容器行为）
- `@MockBean`：把“外部依赖/复杂依赖”替换掉，让断言只关注当前目标
- `MockMvc` vs `TestRestTemplate`：
  - MockMvc 更接近“容器内请求模拟”
  - RestTemplate 更接近“真实 HTTP 调用”

### 3) 本模块的关键分支（2–5 条，默认可回归）

1. **slice：`@WebMvcTest` 只验证 controller 契约，不依赖真实 service**
   - 验证：`GreetingControllerWebMvcLabTest#returnsGreetingFromMockedService`
2. **full：`@SpringBootTest` 走真实启动与真实 bean**
   - 验证：`GreetingControllerSpringBootLabTest#returnsGreetingFromRealService`
3. **替换边界：`@MockBean` 在 full context 中覆盖真实 bean**
   - 验证：`BootTestingMockBeanLabTest#mockBeanOverridesRealBeanInFullContext`
4. **默认参数与行为固定（避免“只测 happy path”）**
   - 验证：`GreetingControllerWebMvcLabTest#usesDefaultRequestParamValueWhenMissing` / `GreetingControllerSpringBootLabTest#usesDefaultNameWhenRequestParamIsMissing`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

建议断点（排障优先级从高到低）：

- **先断言“我到底启动了什么”**
  - 在测试类上确认使用 `@WebMvcTest` 还是 `@SpringBootTest`
- **当遇到 BeanNotFound / 组件没加载**
  - slice：检查是否需要 `@MockBean` / `@Import` / `@AutoConfigureMockMvc`
  - full：检查 profile/config 是否生效、是否被 `@MockBean` 覆盖
- 若要看 Spring Boot Test 的分流入口（可选深挖）：
  - `org.springframework.boot.test.context.SpringBootTestContextBootstrapper#buildMergedContextConfiguration`
  - `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTestContextBootstrapper#buildMergedContextConfiguration`

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`
- 建议命令：`mvn -pl :spring-boot-testing test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 如何跑实验
- 运行本模块测试：`mvn -pl :spring-boot-testing test`

## 对应 Lab（可运行）

- `GreetingControllerWebMvcLabTest`
- `GreetingControllerSpringBootLabTest`
- `BootTestingMockBeanLabTest`
- `BootTestingExerciseTest`

## 常见坑与边界

如果是带着线上问题来的，建议先对照本模块 Appendix（common pitfalls/self-check），再回到主线章节逐一核对。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`
- Exercise：`BootTestingExerciseTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-testing/01-slice-and-mocking.md](../part-01-testing/01-slice-and-mocking.md)

<!-- BOOKIFY:END -->
