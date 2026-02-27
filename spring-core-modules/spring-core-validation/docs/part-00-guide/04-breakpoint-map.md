# 04. 断点地图（Validation Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Validation Debugger Pack）
    - 怎么使用：先跑 `SpringCoreValidationBranchMatrixLabTest` 固化“对象校验/方法校验/groups”的断言，再用断点观察 constraint violations 的产生点与方法校验的代理边界。
    - 原理：对象校验走 `Validator#validate`；方法校验依赖代理（MethodValidationPostProcessor/Interceptor）；groups 决定哪些约束参与。
    - 源码入口：`org.springframework.validation.beanvalidation.SpringValidatorAdapter` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor`
    - 推荐 Lab：`SpringCoreValidationBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Validation）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[05. 关键分支矩阵（Branch Decision Matrix）](05-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04. 断点地图（Validation Debugger Pack）**
- 建议入口：优先运行 `SpringCoreValidationBranchMatrixLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：对象校验走 `Validator#validate`；方法校验依赖代理（MethodValidationPostProcessor/Interceptor）；groups 决定哪些约束参与。
- 源码入口：`org.springframework.validation.beanvalidation.SpringValidatorAdapter` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor`



## 运行入口（先运行）

- Book Matrix：`SpringCoreValidationBookMatrixLabTest`
- Branch Matrix：`SpringCoreValidationBranchMatrixLabTest`

## 断点（对象校验）

- `org.springframework.validation.beanvalidation.SpringValidatorAdapter#validate`
- `jakarta.validation.Validator#validate`（实现类由 provider 决定）

## 断点（方法校验：代理边界）

- `org.springframework.validation.beanvalidation.MethodValidationPostProcessor#afterPropertiesSet`
- `org.springframework.validation.beanvalidation.MethodValidationInterceptor#invoke`

## Watchpoints（建议）

- violations 列表（size/每条 message/path）
- 当前线程是否进入 AOP proxy（方法校验依赖 proxy）
- groups（默认组 vs 自定义组）

## 排障入口（Playbook）

- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

- 小结：对象校验走 `Validator#validate`；方法校验依赖代理（MethodValidationPostProcessor/Interceptor）；groups 决定哪些约束参与。
- 下一章：[第 157 章：04：关键分支矩阵（Branch Decision Matrix）](05-branch-decision-matrix.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`SpringCoreValidationBranchMatrixLabTest`
- Lab：`SpringCoreValidationMechanicsLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](05-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

