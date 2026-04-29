# 01. 主线时间线：springboot-autoconfiguration
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕主线时间线：springboot-autoconfiguration展开，主线可以概括为：启动期：AutoConfiguration imports → 条件评估（Condition）→ 注册 bean；运行期：只是使用这些 bean（真正的隐式机制都在启动期）。

    本页是“导航页”。先运行 Book Matrix（最小实验集合），再按“导入链 → 条件决策 → 产出 bean”的顺序深挖。

    对照入口：`BootAutoConfigurationLabTest`。需要下探源码时，可以从 `org.springframework.boot.autoconfigure.AutoConfigurationImportSelector` / `org.springframework.context.annotation.ConditionEvaluator` / `org.springframework.boot.autoconfigure.condition.OnBeanCondition` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[模块目录](../README.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[02. 深挖导读：把“自动配置导入 + 条件决策”落到源码与断点](guide-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootAutoConfigurationLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：启动期：AutoConfiguration imports → 条件评估（Condition）→ 注册 bean；运行期：只是使用这些 bean（真正的隐式机制都在启动期）。需要下探源码时，可以从 `org.springframework.boot.autoconfigure.AutoConfigurationImportSelector` / `org.springframework.context.annotation.ConditionEvaluator` / `org.springframework.boot.autoconfigure.condition.OnBeanCondition` 这些入口切入。


## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`

## 机制主线（需要建立的叙事）

1. **AutoConfiguration 从哪里来？**（imports 文件/selector）
2. **为什么它会/不会生效？**（条件装配：property/class/bean）
3. **用户自定义 bean 为什么能覆盖默认？**（backoff：`@ConditionalOnMissingBean`）
4. **多个 auto-config 如何组合？**（顺序与叠加）

## 阅读顺序（最短路径）

1. [00. 深挖导读](guide-deep-dive-guide.md)
2. [01. AutoConfiguration 调用链](guide-autoconfiguration-import-call-chain.md)
3. [02. 断点地图（断点包）](guide-breakpoint-map.md)
4. [04. 关键分支矩阵](guide-branch-decision-matrix.md)

## 小结与下一章

启动期：AutoConfiguration imports → 条件评估（Condition）→ 注册 bean；运行期：只是使用这些 bean（真正的隐式机制都在启动期）。

下一章见：[00. 深挖导读](guide-deep-dive-guide.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootAutoConfigurationLabTest`

上一章：[模块目录](../README.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md)

<!-- BOOKIFY:END -->
