# 02. 深挖指南（Spring Core Validation）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕深挖指南（Spring Core Validation）展开，主线可以概括为：约束声明 → 触发校验（绑定后或方法拦截）→ 产出 violation/errors → 映射到响应；方法校验的关键边界是代理与 self-invocation。

    先运行 `SpringCoreValidationLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在 Web 入参或方法边界声明约束（`@NotNull/@Size/...`）；方法级校验通常需要 `@Validated` 触发代理；用统一错误模型返回给调用方。

    需要下探源码时，可以从 `org.springframework.validation.beanvalidation.LocalValidatorFactoryBean` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` / `org.springframework.validation.beanvalidation.SpringValidatorAdapter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：Spring Validation](guide-mainline-timeline.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 约束（Constraint）心智模型：校验对象与校验结果](validation-core-constraint-mental-model.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

本章用于说明本模块如何阅读、如何验证，以及遇到分支时从哪里下断点。
先运行 `SpringCoreValidationLabTest` 获得可复现现象，再带着断言/观察点回到正文对照机制。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

## 机制主线

Validation 的“深挖主线”不是记注解，而是把三件事分清：

1. **Constraint 是什么**：声明式约束如何变成 `ConstraintViolation`
2. **谁来触发校验**：programmatic（显式调用 Validator） vs method validation（代理触发）
3. **校验结果怎么被消费**：exception（`ConstraintViolationException`）或 violations 集合（可用于自定义错误结构）

### 1) 时间线：一次校验从输入到 violations

1. 准备一个“待校验对象”（字段/入参）
2. Validator 扫描约束（如 `@NotBlank`/`@Email`/自定义 constraint）
3. 逐条执行 `ConstraintValidator#isValid`
4. 收集 violations（propertyPath/message 等是排障的第一现场）

### 2) 时间线：method validation 为什么需要代理

1. 在 service 方法上声明“需要校验”（通常是 `@Validated` + 约束注解）
2. Spring 创建代理并在方法调用前触发校验
3. 校验失败：抛 `ConstraintViolationException`（调用方可统一处理）

如果绕开 Spring 直接 new 一个 service 并调用方法：**没有代理就不会触发 method validation**（这是最常见的误解点）。

### 3) 关键参与者

- `jakarta.validation.Validator`：校验入口（programmatic validation）
- `ConstraintViolation`：失败证据（propertyPath/message/rootBeanClass）
- `ConstraintViolationException`：method validation 常见失败载体
- groups：决定“哪些约束参与本次校验”（避免把所有约束都绑死在 Default）
- 自定义 constraint：`@Constraint(validatedBy = ...)` + `ConstraintValidator`

### 4) 本模块的关键分支（2–5 条，默认可回归）

1. **programmatic 校验：返回 violations（可用于自定义错误结构）**
   - 验证：`SpringCoreValidationLabTest#programmaticValidationFindsViolations`
2. **method validation：无效入参会抛 ConstraintViolationException（代理触发）**
   - 验证：`SpringCoreValidationLabTest#methodValidationThrowsForInvalidInput`
3. **代理边界：不经 Spring 代理调用时，method validation 不会触发（坑点）**
   - 验证：`SpringCoreValidationMechanicsLabTest#methodValidationDoesNotRunWhenCallingAServiceDirectly_withoutSpringProxy`
4. **groups 控制生效范围：同一个对象在不同 group 下 violations 不同**
   - 验证：`SpringCoreValidationMechanicsLabTest#groupsControlWhichConstraintsApply`
5. **自定义 constraint：可以定义并验证自己的约束语义**
   - 验证：`SpringCoreValidationMechanicsLabTest#customConstraintsCanBeDefinedWithConstraintValidator`

## 源码与断点


断点入口（把“为什么没校验”快速分流）：

- method validation 是否真的走代理：
  - 先在 `SpringCoreValidationLabTest#methodValidatedServiceIsAnAopProxy` 的断言处确认代理存在性
- violations 的“证据字段”用于排障：
  - 对照 `SpringCoreValidationMechanicsLabTest#constraintViolationIncludesMessageAndPropertyPath`，优先看 propertyPath/message

## 最小可运行实验（Lab）

- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`
- 运行命令：`mvn -pl :spring-core-validation test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

> 验证入口（可跑）：
> - `SpringCoreValidationLabTest`
> - `SpringCoreValidationMechanicsLabTest`

配套验证入口：
- 实验/练习：见 `src/test/java/com/learning/springboot/springcorevalidation/**`

## 常见坑与边界

如果是带着线上问题来的，先对照本模块 Appendix（common pitfalls/self-check），再回到主线章节逐一核对。

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreValidationLabTest` / `SpringCoreValidationMechanicsLabTest`

上一章：[模块目录](../README.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01-constraint-mental-model](validation-core-constraint-mental-model.md)

<!-- BOOKIFY:END -->
