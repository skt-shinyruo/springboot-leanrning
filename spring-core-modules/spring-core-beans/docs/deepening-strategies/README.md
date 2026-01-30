# spring-core-beans：内容级再加深策略（按章节）

本目录提供 `spring-core-beans` **全章**的“内容级再加深”策略清单：不是固定补模板，也不是统一填空，而是基于每个章节主题给出更适合的 **补充/完善/深入**方向。

## 怎么用（推荐）

1) 先读正文：按 `docs/README.md` 的目录阅读对应章节（并跑章节对应的 Lab/Test）。
2) 再看策略：回到本目录，打开对应 Part 的策略文件，找到该章节的小节。
3) 按 A–E 维度选 2–4 个点深挖：
   - A：源码证据链（方法级下沉）
   - B：边界条件与反例（可复现）
   - C：生产排障分型与 SOP（从症状到第一断点）
   - D：断点与 watch list 强化（可观察）
   - E：面试复述与追问（可证明）

> 提示：A–E 是“观察维度”，不是“强制模板”。每章只需要补强最缺的环节，避免重复堆叠。

## 落地示例（把“策略”变成“正文内容”）

以 [02. Bean 注册入口：扫描、@Bean、@Import、registrar](../part-01-ioc-container/02-bean-registration.md) 为例，A–E 维度可以这样“落到正文里”（不是复制粘贴模板，而是补齐缺口）：

- A（证据链）：在正文中明确“最终注册落点”和最短调用链（例如 registry 写入点与配置类解析点），并把它写成可断言的结论（跑哪个 Lab、看哪个变量能证明）。
- B（边界反例）：补 1–2 个“看起来像注册问题但其实不是”的反例（例如定义已注册但注入失败其实是候选收敛问题；或实例层 registerSingleton 绕过 BPP 导致注解不生效）。
- C（排障 SOP）：把“没注册/被条件排除/名字冲突/覆盖策略冲突”分型成 3–4 条 SOP，每条给出第一断点入口与判断标准。
- D（断点观察）：把断点从“方法名清单”升级为“断点 + watch list + 判定标准”（例如看 source/factoryMethodName 推断来源）。
- E（面试复述）：在章末给 2–3 个追问题，把“结论→证据链→反例→修复策略”固化为可复述结构。

## 验证方式（避免“写了很多但不可用”）

每次按策略补完一章，建议至少做一次最小验证闭环（10/30/3）：

1) **10 分钟**：单跑本章推荐 Lab/Test，保证现象与断言稳定。
2) **30 分钟**：命中 3–5 个稳定锚点断点，并用 watch list 看见关键变量变化（能解释“为什么是这样”）。
3) **3 分钟**：用“结论→证据链→反例/误区”复述本章（可对照 `appendix/93-interview-playbook.md`）。

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
