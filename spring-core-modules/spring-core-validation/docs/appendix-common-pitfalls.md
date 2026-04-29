# 01. 常见坑清单（排查时对照）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕常见坑清单（排查时对照）展开，主线可以概括为：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。

    先运行 `SpringCoreValidationLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。

    需要下探源码时，可以从 `org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[06. Debug / 观察：如何排查“校验为什么没生效？”](validation-core-debugging.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[02. 自测题（Spring Core Validation）](appendix-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页不是新的主线章节，而是把已读过的机制拿回来验证、排障和自检。读法如下：

1. 先运行 Book Matrix、Branch Matrix 或本页列出的最小 Lab，把现象固定成可重复结果。
2. 再按现象、题目或坑点定位对应章节、断点和关键变量。
3. 最后用对应实验/测试 收束答案；如果答案仍然只停留在概念层面，再回到正文补齐机制。

### 排障骨架（统一结构）

当遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，可以按下面 6 步收敛问题（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/观察点/关键日志收齐：[04-breakpoint-map.md](guide-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[05-branch-decision-matrix.md](guide-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[02-self-check.md](appendix-self-check.md)


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`
## 最小可运行实验（Lab）

- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`
- 运行命令：`mvn -pl :spring-core-validation test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 验证入口（可跑）：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

## 坑 1：以为 `@Valid` 自动让 service 方法校验

- 会看到：Controller 入参校验正常，但 service 方法参数校验“不触发”，于是误以为注解没生效。
方法参数校验需要 Spring 代理拦截（见 [03. method-validation-proxy](validation-core-method-validation-proxy.md)），本质上是“method interceptor 在运行时做校验”，不是编译期隐式机制。

把 method validation 当成 AOP 一类问题排：先确认 bean/入口/代理，再看约束本身。

## 坑 2：忘了加 `@Validated`

- 会看到：`@Valid` 写在方法参数上，但不抛 `ConstraintViolationException`，像是“完全没校验”。
对照 `MethodValidatedUserService` 的类级别 `@Validated`；并顺手确认调用入口不是同类自调用（下一条）。

## 坑 3：自调用导致 method validation 不触发

- 规律：同 AOP/Tx，自调用绕过代理。
学习阶段先用 tests 把它复现成断言，再讨论设计规避方式（拆分 bean、从外部入口调用、或改用更明确的边界）。

## 坑 4：Group 没指定导致以为规则“失效”

- 会看到：编写了 `@NotBlank(groups=Create.class)`，但 validate(Default.class) 没有 violations，于是以为规则没生效。
group 决定“启用哪组规则”——没选中就等价于“没声明”。

把“当前在运行哪个 group”写清楚（尤其是方法校验与 Web 入参校验混在一起时）。

## 坑 5：把 violations 当成字符串拼接错误

- 会看到：只看见“校验失败”，却不知道失败在哪个字段、因为什么规则，排障成本急剧上升。
先学会读 `propertyPath` 与 `message`：它们是结构化证据，不是“日志文本”；把它们映射成统一错误模型再返回给调用方。

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

上一章：[06-debugging](validation-core-debugging.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[99-self-check](appendix-self-check.md)

<!-- BOOKIFY:END -->
