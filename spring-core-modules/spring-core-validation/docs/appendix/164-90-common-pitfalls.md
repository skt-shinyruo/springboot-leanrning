# 第 164 章：90. 常见坑清单（建议反复对照）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：常见坑清单（建议反复对照）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。
    - 原理：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。
    - 源码入口：`org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter`
    - 推荐 Lab：`SpringCoreValidationLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 163 章：06. Debug / 观察：如何排查“校验为什么没生效？”](../part-01-validation-core/163-06-debugging.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 165 章：自测题（Spring Core Validation）](165-99-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障模板（统一结构）

当你遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，建议按下面 6 步收敛（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：你看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :spring-core-validation -Dtest=SpringCoreValidationBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[157-02-breakpoint-map.md](../part-00-guide/157-02-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[157-04-branch-decision-matrix.md](../part-00-guide/157-04-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[165-99-self-check.md](165-99-self-check.md)

- 本章主题：**90. 常见坑清单（建议反复对照）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

## 机制主线

- （本章主线内容暂以契约骨架兜底；建议结合源码与测试用例补齐主线解释。）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-validation test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 验证入口（可跑）：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

## 坑 1：以为 `@Valid` 自动让 service 方法校验

- 事实：方法参数校验需要 Spring 代理拦截（见 [03. method-validation-proxy](../part-01-validation-core/160-03-method-validation-proxy.md)）

## 坑 2：忘了加 `@Validated`

- 现象：`@Valid` 写在方法参数上，但不抛 `ConstraintViolationException`
- 建议：对照 `MethodValidatedUserService` 的类级别 `@Validated`

## 坑 3：自调用导致 method validation 不触发

- 规律：同 AOP/Tx，自调用绕过代理
- 建议：学习阶段先用 tests 复现，再讨论设计规避方式

## 坑 4：Group 没指定导致你以为规则“失效”

- 现象：你写了 `@NotBlank(groups=Create.class)`，但 validate(Default.class) 没有 violations
- 解释：group 决定“启用哪组规则”

## 坑 5：把 violations 当成字符串拼接错误

- 建议：学会看 `propertyPath` 与 `message`，它们是结构化信息，不是“日志文本”

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

上一章：[06-debugging](../part-01-validation-core/163-06-debugging.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[99-self-check](165-99-self-check.md)

<!-- BOOKIFY:END -->
