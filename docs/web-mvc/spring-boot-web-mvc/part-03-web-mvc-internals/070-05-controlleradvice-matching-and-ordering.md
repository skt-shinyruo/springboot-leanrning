# 第 70 章：05：ControllerAdvice 的匹配与优先级（为什么 advice 生效/不生效）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：05：ControllerAdvice 的匹配与优先级（为什么 advice 生效/不生效）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootWebMvcAdviceMatchingLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 69 章：04：ExceptionResolvers（异常从哪来、又被谁“翻译”成状态码）](069-04-exception-resolvers-and-error-flow.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 71 章：01：校验（Validation）与错误响应形状（Error Shape）](../part-01-web-mvc/071-01-validation-and-error-shaping.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05：ControllerAdvice 的匹配与优先级（为什么 advice 生效/不生效）**
- 目标：把 `@ControllerAdvice/@RestControllerAdvice` 的两个核心问题拆开讲清楚：
  1. **匹配（applicable）**：这个 advice 到底作用于哪些 controller？
  2. **优先级（order）**：当多个 advice 都能处理同一异常时，最终是谁生效？

!!! summary "本章要点"

    - `@ControllerAdvice` 的行为不是“看请求路径”，而是**看 handler/controller 的类型是否匹配**（通过 selector：`basePackages` / `annotations` / `assignableTypes` 等）。
    - selector 组合有一个容易踩坑的点：**同一个 advice 上配置多个 selector 时，匹配语义是“并集（OR）”**——任一条件命中就算 applicable，而不是“交集（AND）”。
    - 当异常发生时，选择顺序可以概括为：
      1. **先找 controller 自己的 `@ExceptionHandler`**
      2. 再找 **可适用（applicable）的 ControllerAdvice 列表**
      3. 在可适用的列表中，**按 `@Order` 排序后依次尝试匹配异常类型**（先匹配到的生效）
    - 真实工程里 “advice 不生效” 的常见原因并不是你 handler 写错了，而是：
      - advice 根本没被加载进上下文（slice 测试尤为常见）
      - selector 没匹配到 controller（包范围/注解/可赋值类型）
      - 你处理的异常类型不对（异常发生在 converter/binder 阶段）

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcAdviceMatchingLabTest`

## 机制主线（调用链 + 关键分支）

把它理解成 **两步过滤 + 一步排序**：

### 1) 入口：异常从哪进入 resolver 链

- 入口大体发生在 `DispatcherServlet` 的“handler 执行”之后
- 典型流转：`DispatcherServlet#doDispatch` → `processDispatchResult` → `processHandlerException`

### 2) 分支一：先看 controller 本地的 @ExceptionHandler

你在 controller 里声明的 `@ExceptionHandler`（同类或同 controller 的方法）通常优先于全局 advice。

### 3) 分支二：再看 ControllerAdvice（先匹配，再排序）

对 ControllerAdvice 的选择流程可以粗略理解为：

1. 收集所有 `ControllerAdviceBean`（每个对应一个 `@ControllerAdvice`/`@RestControllerAdvice` bean）
2. 对每一个 advice 做 **“是否适用于当前 controller 类型”** 的判断：
   - `basePackages`：controller 的包前缀是否命中
   - `annotations`：controller 类上是否存在指定注解
   - `assignableTypes`：controller 类型是否可赋值给指定类型
3. 把 **命中的 advice** 按 order 排序（`@Order` 数值越小优先级越高）
4. 依次尝试查找能处理当前异常类型的 `@ExceptionHandler` 方法：**第一个匹配到的生效**

> 你需要在脑子里把 “匹配集合” 与 “排序决策” 分开：先进入集合，再在集合内部比顺序。

## 源码与断点（建议从 Lab 反推）

建议断点（按“选择逻辑”优先）：

- `ExceptionHandlerExceptionResolver#doResolveHandlerMethodException`（异常处理主入口）
- `ExceptionHandlerExceptionResolver#getExceptionHandlerMethod`（本地 handler vs advice 的选择）
- `org.springframework.web.method.ControllerAdviceBean#isApplicableToBeanType`（selector 命中判断）
- `org.springframework.web.method.HandlerTypePredicate#test`（basePackages/annotations/assignableTypes 的谓词）
- `org.springframework.core.annotation.AnnotationAwareOrderComparator#sort`（排序）

观察点（调试时建议记录成证据链）：
- 最终命中的 `@ExceptionHandler` 来自哪个类（controller 本地 vs 哪个 advice）
- 当前 controller 的实际类型（是否被代理、包名是否符合 selector）

## 最小可运行实验（Lab）

本模块提供两条互补证据链：

- Lab：`BootWebMvcAdviceMatchingLabTest`
  - 覆盖 selector：`annotations` / `assignableTypes` / `basePackages`
  - 固定：不同 controller 命中不同 advice，并包含一个“多 advice 同时命中时由 @Order 决定”的对照端点
- Lab：`BootWebMvcAdviceOrderLabTest`
  - 固定：当两个 advice 都能处理同一异常时，高优先级（更小 @Order）生效

## 常见坑与边界（工程落地视角）

- **坑 1：以为 selector 看路径**
  - selector 的判断基于 controller 类型，而不是 request path；所以“路径对了但 advice 没生效”更可能是包范围/注解/类型不匹配。

- **坑 2：把多个 selector 当成 AND（结果意外扩大作用域）**
  - 例如你写了 `basePackages=...` + `annotations=...`，并不代表“只作用于该包内且带该注解的 controller”，而是“包内的 controller + 带该注解的 controller（并集）”。
  - 如果你期望更强的限定，建议用“更独特的 marker/annotation”来间接收敛范围（本模块的 Lab 即使用专用 `AdviceMatchingTagged/AdviceMatchingMarker` 做演示）。

- **坑 3：@WebMvcTest 忘了把 advice 纳入上下文**
  - slice 测试默认不会加载全量组件；建议用 `@Import(...)` 显式把目标 advice 纳入（本模块的 LabTest 都这么做）。

- **坑 4：异常发生在 converter/binder 阶段，你却只处理业务异常**
  - 406/415、解析失败、类型不匹配等并不会进入你“以为会到的”异常类型。
  - 建议：先用 `resolvedException` 固定异常类型，再决定由哪个 advice 处理。

## 小结与下一章

- 本章完成后：进入 Part 04，把 406/415 与 Jackson/契约控制结合起来，形成“可观测 + 可回归”的工程闭环。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcAdviceMatchingLabTest` / `BootWebMvcAdviceOrderLabTest`

上一章：[异常收敛与错误流](069-04-exception-resolvers-and-error-flow.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[校验与错误塑形](../part-01-web-mvc/071-01-validation-and-error-shaping.md)

<!-- BOOKIFY:END -->
