# Spring Boot Auto-Configuration：导入、条件决策与 back-off

自动装配的难点往往不在注解本身，而在“为什么有时生效、有时不生效”。同一份依赖在不同工程里出现差异，通常来自两个事实：自动配置类是否被导入，以及条件是否满足（并在何处发生 back-off）。本模块把这两个事实拆成可运行实验，并把关键分支固定到断点与断言上。

---

## 10 分钟入口：确认导入与条件决策

- `mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`

运行后应能在调试器中回答：自动配置是通过何种 imports 机制被导入的；条件评估发生在哪个阶段；最终有哪些 BeanDefinition 被注册、哪些发生 back-off。

---

## 阅读路线（调用链 → 分支 → 正文）

1. 先建立调用链坐标（把入口压到最短）
   - [主线时间线](part-00-guide/01-mainline-timeline.md)
   - [深挖导读](part-00-guide/02-deep-dive-guide.md)
   - [AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）](part-00-guide/03-autoconfiguration-import-call-chain.md)
2. 再用断点与分支矩阵收敛关键 if/then
   - [断点地图](part-00-guide/04-breakpoint-map.md)
   - [关键分支矩阵](part-00-guide/05-branch-decision-matrix.md)
3. 最后进入正文（把条件与 back-off 跑成事实）
   - [条件装配与 backoff：为什么它“有时生效、有时不生效”](part-01-autoconfig-basics/01-conditional-and-backoff.md)

---

## 可运行入口（用于复现/回归）

- Book Matrix：`mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBookMatrixLabTest test`
- Branch Matrix：`mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationBranchMatrixLabTest test`
- 并发/性能：`mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationConcurrencyLabTest test`

---

## 排坑与自检

- [常见坑](appendix/01-common-pitfalls.md)
- [自检](appendix/02-self-check.md)
