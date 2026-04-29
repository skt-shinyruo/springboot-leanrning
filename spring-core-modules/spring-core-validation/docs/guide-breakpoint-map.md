# 04. 断点地图（Validation）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（Validation）展开，主线可以概括为：对象校验走 `Validator#validate`；方法校验依赖代理（MethodValidationPostProcessor/Interceptor）；groups 决定哪些约束参与。

    先跑 `SpringCoreValidationBranchMatrixLabTest` 固化“对象校验/方法校验/groups”的断言，再用断点观察 constraint violations 的产生点与方法校验的代理边界。

    需要下探源码时，可以从 `org.springframework.validation.beanvalidation.SpringValidatorAdapter` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Validation）](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `SpringCoreValidationBranchMatrixLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：对象校验走 `Validator#validate`；方法校验依赖代理（MethodValidationPostProcessor/Interceptor）；groups 决定哪些约束参与。需要下探源码时，可以从 `org.springframework.validation.beanvalidation.SpringValidatorAdapter` / `org.springframework.validation.beanvalidation.MethodValidationPostProcessor` 这些入口切入。


## 运行入口（先运行）

- Book Matrix：`SpringCoreValidationBookMatrixLabTest`
- Branch Matrix：`SpringCoreValidationBranchMatrixLabTest`

## 断点（对象校验）

- `org.springframework.validation.beanvalidation.SpringValidatorAdapter#validate`
- `jakarta.validation.Validator#validate`（实现类由 provider 决定）

## 断点（方法校验：代理边界）

- `org.springframework.validation.beanvalidation.MethodValidationPostProcessor#afterPropertiesSet`
- `org.springframework.validation.beanvalidation.MethodValidationInterceptor#invoke`

## 观察点

- violations 列表（size/每条 message/path）
- 当前线程是否进入 AOP proxy（方法校验依赖 proxy）
- groups（默认组 vs 自定义组）

## 排障入口（Playbook）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

对象校验走 `Validator#validate`；方法校验依赖代理（MethodValidationPostProcessor/Interceptor）；groups 决定哪些约束参与。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`SpringCoreValidationBranchMatrixLabTest`
- Lab：`SpringCoreValidationMechanicsLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

