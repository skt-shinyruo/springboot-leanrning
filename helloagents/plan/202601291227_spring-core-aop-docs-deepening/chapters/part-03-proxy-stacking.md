# 章节逐章补强建议（part-03-proxy-stacking 多代理叠加）

### 第 38 章：09. 多代理叠加（multi proxy stacking）

- 建议先澄清两个概念：一个代理里“多个 Advisor/Interceptor” vs “多个代理层层包裹”（真正的 stacking），并说明它们的成因不同、排障路径也不同。
- 补强“stacking 的来源”：例如多个 BeanPostProcessor/自动代理器/框架模块各自创建代理导致的层层包装（把现象落到可验证点）。
- 补强“如何识别与拆解”：提供在运行时识别多层代理的办法（如何逐层 unwrap、如何查看每层 advisors），并给最小调试路线。
- 补强“顺序控制与稳定性”：说明顺序变化会带来什么可观测的行为差异（比如事务/缓存/安全等），以及如何用 `@Order`/Ordered 等机制影响顺序。
- 给出一个 Lab/Test：要求能够稳定看到至少两层代理或可观测的链路差异，并能解释每一层的来源。

### 第 39 章：10. 实战叠加 playbook（real world stacking playbook）

- 建议用“真实场景矩阵”组织：按常见组合（安全 + 事务 + 缓存 + 自定义 tracing 等）给出典型链路与排查步骤。
- 补强“如何验证顺序”：提供一个统一的打印/断点套路，读者能直接看到 advisors/interceptors 的顺序与命中情况，而不是凭直觉猜。
- 补强“怎么写可维护的切面”：围绕 `@Order`、职责边界（切面只做横切）、避免过度依赖 exposeProxy 等给出工程建议。
- 与 Appendix 的 common pitfalls 强绑定：playbook 中出现的每个“坑位”都应能在 pitfalls 中找到对应条目（或反向链接）。

