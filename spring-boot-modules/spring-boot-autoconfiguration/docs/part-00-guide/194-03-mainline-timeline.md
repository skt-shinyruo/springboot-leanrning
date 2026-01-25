# 第 194 章：03：主线时间线：springboot-autoconfiguration
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：springboot-autoconfiguration
    - 怎么使用：本页是“导航页”。建议先跑 Book Matrix（最小实验集合），再按“导入链 → 条件决策 → 产出 bean”的顺序深挖。
    - 原理：启动期：AutoConfiguration imports → 条件评估（Condition）→ 注册 bean；运行期：你只是使用这些 bean（真正的魔法都在启动期）。
    - 源码入口：`org.springframework.boot.autoconfigure.AutoConfigurationImportSelector` / `org.springframework.context.annotation.ConditionEvaluator` / `org.springframework.boot.autoconfigure.condition.OnBeanCondition`
    - 推荐 Lab：`BootAutoConfigurationLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 195 章：00. 深挖导读](195-00-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`

## 机制主线（你要建立的叙事）

1. **AutoConfiguration 从哪里来？**（imports 文件/selector）
2. **为什么它会/不会生效？**（条件装配：property/class/bean）
3. **用户自定义 bean 为什么能覆盖默认？**（backoff：`@ConditionalOnMissingBean`）
4. **多个 auto-config 如何组合？**（顺序与叠加）

## 推荐阅读顺序（最短路径）

1. [00. 深挖导读](195-00-deep-dive-guide.md)
2. [01. AutoConfiguration 调用链](195-01-autoconfiguration-import-call-chain.md)
3. [02. 断点地图（Debugger Pack）](195-02-breakpoint-map.md)
4. [04. 关键分支矩阵](195-04-branch-decision-matrix.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAutoConfigurationLabTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/00-deep-dive-guide.md](195-00-deep-dive-guide.md)

<!-- BOOKIFY:END -->
