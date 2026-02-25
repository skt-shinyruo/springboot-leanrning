# 03. Validation 调用链（@Valid → Validator → violations）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Validation 调用链（@Valid → Validator → violations）
    - 怎么使用：先跑 `SpringCoreValidationMechanicsLabTest`，把“校验触发与结果形态”固化成断言，再按本文把 MVC 参数校验与方法级校验的链路区分开。
    - 原理：校验不是注解本身触发，而是框架在边界处调用 Validator；方法级校验通常依赖代理（MethodValidationPostProcessor）。
    - 源码入口：`LocalValidatorFactoryBean` /（MVC）`HandlerMethodArgumentResolver` / `MethodValidationPostProcessor`
    - 推荐 Lab：`SpringCoreValidationMechanicsLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Validation）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 断点地图（Validation Debugger Pack）](04-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. Validation 调用链（@Valid → Validator → violations）**
- 建议入口：优先运行 `SpringCoreValidationMechanicsLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：校验不是注解本身触发，而是框架在边界处调用 Validator；方法级校验通常依赖代理（MethodValidationPostProcessor）。
- 源码入口：`LocalValidatorFactoryBean` /（MVC）`HandlerMethodArgumentResolver` / `MethodValidationPostProcessor`



## 最短调用链

### 1) MVC 参数校验（入口边界）

1. 请求进入 MVC（参数解析）
2. 触发 `@Valid`（边界处调用 Validator）
3. 得到 violations，并被转换成错误响应/异常

### 2) 方法级校验（代理边界）

1. 容器启动期注册 `MethodValidationPostProcessor`
2. 方法调用进入代理
3. 代理在调用前后执行参数/返回值校验

证据链入口：

- `SpringCoreValidationMechanicsLabTest`

## 小结与下一章

- 小结：校验不是注解本身触发，而是框架在边界处调用 Validator；方法级校验通常依赖代理（MethodValidationPostProcessor）。
- 下一章：[第 157 章：02：断点地图](04-breakpoint-map.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreValidationMechanicsLabTest`
- Lab：`SpringCoreValidationLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md)

<!-- BOOKIFY:END -->
