# spring-core-beans：内容级再加深策略（按章节）

## 官方文档对照（版本语境）

- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）
- Spring Boot：`3.5.9`

- Spring Framework Reference（Beans）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（容器扩展点）：https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html


本目录提供 `spring-core-beans` **全章**的“内容级再加深”策略清单：不是固定补模板，也不是统一填空，而是基于每个章节主题给出更适合的 **补充/完善/深入**方向。

## 使用方式（推荐）

1) 先读正文：按 `docs/README.md` 的目录阅读对应章节（并运行章节对应的 Lab/Test）。
2) 再参阅策略：回到本目录，打开对应 Part 的策略文件，找到该章节的小节。
3) 挑“本章最缺的那块拼图”继续往下挖（不要试图把每章补成同一种形态）：
   - 如果本章已经有概念与结论，但读者“跑不出来/证不出来”：优先补 **最短可复现入口** 与 **断点验证路线**（跑完知道去哪里证明）。
   - 如果本章已经有实验与断点，但容易误判：优先补 **对照/边界反例** 与 **最短排错路径**（把主观判断变成可验证步骤）。
   - 如果本章天然会跨到 AOP/TX/Boot：优先补 **最短跳转建议**（为什么要跳、跳过去验证什么），而不是堆链接。

## 两条最常用入口（从哪里回到正文）

- **从现象进入**：
  1) 在 [知识地图](../appendix/03-knowledge-map.md) 找到现象行，直接拿到“章节 + 断点组 + 推荐 Lab”
  2) 先跑推荐 Lab 固化现象，再按断点组命中关键分支（必要时回到 [断点地图](../part-00-guide/07-breakpoint-map.md) 选 C 组降噪）
  3) 回到章节正文（章节卡片 + 继续加深提示块），把观察结果收敛为结论/反例/排错路径
- **从断点进入**：
  1) 在 [断点地图](../part-00-guide/07-breakpoint-map.md) 选一组断点（C1–C7）先把阶段定位清楚
  2) 再用章节末尾的 `### 对应 Lab/Test` 入口把同类场景跑出来（避免只停留在“看到了调用栈”）
  3) 回到本目录策略文件，把断点观察收敛为“关键分支 + 最短证据链 + 边界对照”（按该章需要选择性补强）

## 落地示例（把“策略”变成“正文内容”）

以 [01. Bean 注册入口：扫描、@Bean、@Import、registrar（已合并）](../part-01-ioc-container/01-bean-registration.md) 为例，“继续深化”更接近是把读者的下一步动作写清楚，而不是补一套固定小标题：

- 把“结论”写成可断言：例如“这个 beanName 的 BeanDefinition 是否进入 registry”是第一分流点（先判定定义层，再谈实例/代理）。
- 把“跑实验”与“证明结论”绑定：明确跑完哪个 Lab/Test 之后，去哪个入口方法打断点，观察哪个变量能证明本章结论（避免“跑了但不知道证明了什么”）。
- 把常见误判收敛成最短排错路径：把“现象 → 第一入口 → 关键分支 → 结论”写成读者可以照着走的步骤（并回链到对应段落/章节）。

## 验证方式（避免“写了很多但不可用”）

每次按策略补完一章，建议用“可复现 + 可证明”做快速自检：

1) **可复现：** 本章推荐的 Lab/Test 能一键跑通，现象稳定（不依赖偶然时序/日志猜测）。
2) **可证明：** 能在 IDE 里按正文给出的断点/观察点看到关键变量变化，并据此解释“为什么会这样”。
3) **可复述：** 能用 1–2 句说清本章要解决的问题，并给出最短证据链（入口 → 分支 → 结果）。

## 策略文件索引

- 模块入口与目录页：
  - `module-readme.md`（模块 README 深化）
  - `docs-root.md`（Docs TOC 深化）
- Part 00（Guide）：
  - `part-00-guide.md`
- Part 01（IoC Container）：
  - `part-01-ioc-container.md`
- Part 02（Boot Auto-Config）：
  - `part-02-boot-autoconfig.md`
- Part 03（Container Internals）：
  - `part-03-container-internals.md`
- Part 04（Wiring & Boundaries）：
  - `part-04-wiring-and-boundaries.md`
- Part 05（AOT & Real World）：
  - `part-05-aot-and-real-world.md`
- Appendix（工具型章节）：
  - `appendix.md`
