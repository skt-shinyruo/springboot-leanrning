# 如何执行逐章“补充 + 完善 + 深入”

## 执行原则（不搞统一硬标准，但要可落地）

- 先读本章，再决定补强点：先识别本章处于 AOP 的哪一段主线（代理创建 / 代理调用 / 匹配规则 / 多代理叠加 / 排障复盘），避免“预设模板”把章节写偏。
- 不做“面面俱到”的堆料：每章优先挑 3～6 个对理解最增益的补强点；宁可少而准，也不要多而散。
- 必须绑定一个“可验证入口”：每个补强点尽量落到一条可执行验证——对应 Lab Test、关键断点、日志观察点或可复现实验步骤。
- 主线优先、分支补齐：先保证读者能走通一次“创建代理 → 调用拦截链 → 观察/验证”的主路径，再展开关键分支（JDK/CGLIB、自调用、final、叠加、AutoProxyCreator 分支）。
- 把抽象名词落到对象与类：关键概念至少落到 1 个 Spring 类（FQN）+ 1 个对象图（例如 `AdvisedSupport/Advisor/MethodInterceptor/MethodInvocation/TargetSource`），避免只讲术语不讲对象。
- 章节之间要形成回路：公共素材优先沉淀到 Guide（时间线/断点地图/分支矩阵）与 Appendix（常见坑/自测）；正文章节引用这些公共资产，避免重复堆叠。

## 本次阅读输出的位置

- 每章的具体补强策略（按 Part 拆分）：`helloagents/plan/202601291227_spring-core-aop-docs-deepening/chapters/*.md`
- 实际需要修改的目标文件：`spring-core-modules/spring-core-aop/docs/**`
- 导航入口：
  - `spring-core-modules/spring-core-aop/README.md`
  - `spring-core-modules/spring-core-aop/docs/README.md`

## 执行方式（进入 ~exec 阶段时）

- 按 Part 批次推进：优先 Guide → Fundamentals → AutoProxy/Pointcut → Stacking → Appendix（先建立主线资产，再深入分支与排障）。
- 每改一章就跑对应的最小验证（Lab Test / 断点链路），确保“文档描述”与“运行现象/源码行为”一致。
- 需要新增实验/测试时，优先按模块现有风格扩展：保持“可 Debug、可重复、可定位”，并把测试入口写回章节。
- 完成一个 Part 后做一次横向回归：更新 TOC、补充跨章链接、检查术语一致性、把重复内容上收为公共资产。

