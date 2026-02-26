# 任务清单（逐章深度完善：执行阶段）

> Note：原本按偏好保留在 `helloagents/plan/`；已于 2026-01-29 按确认迁移到 `helloagents/history/2026-01/` 归档。

## A. 入口与导航（优先）

- [√] 梳理并去重：`spring-core-modules/spring-core-aop/docs/README.md` 的章节链接与推荐阅读顺序（避免重复链接与“跳读断层”）
- [√] 同步：`spring-core-modules/spring-core-aop/README.md` 的模块目标、运行/调试方法、章节索引与入口说明
- [√] 补齐：存在测试但 docs 未覆盖的主题（例如：并发/性能方向的 AOP 代理验证）
- [√] 同步知识库：`helloagents/wiki/modules/spring-core-aop.md` 增补“章节地图 + 实验入口 + 常见坑索引”

## B. 分 Part 落地（按批次推进）

- [√] Part-00 Guide：主线时间线、断点地图、分支矩阵（详见 `chapters/part-00-guide.md`）
- [√] Part-01 Proxy Fundamentals：代理心智模型、JDK vs CGLIB、自调用、final 限制、exposeProxy、调试（详见 `chapters/part-01-proxy-fundamentals.md`）
- [√] Part-02 AutoProxy & Pointcuts：AutoProxyCreator 主线、切点表达式系统、（补充）并发/性能（详见 `chapters/part-02-autoproxy-and-pointcuts.md`）
- [√] Part-03 Proxy Stacking：多代理叠加机理与实战 playbook（详见 `chapters/part-03-proxy-stacking.md`）
- [√] Appendix：常见坑与自测题（详见 `chapters/appendix.md`）

## C. 横向增强（贯穿所有章节）

- [√] 每章补一个“验证闭环”：推荐的 Lab Test + 关键断点/观察点 + 期望现象（读完即可做、做完可验证）
- [ ] 补充图表资产：时间线/对象图/调用链/决策树（优先 Mermaid，进入 ~exec 时确认 docs-site 渲染支持）
- [ ] 统一术语与符号：Proxy/Target/Advice/Advisor/Interceptor/MethodInvocation/Join point 等（避免同物多名）
- [√] 增加“排障套路”：AOP 不生效/走错代理类型/拦截链顺序异常/多代理叠加导致行为变化
- [√] 收敛“可复制命令”：统一 Maven/IDEA 运行配置建议（减少环境差异导致的学习噪音）
