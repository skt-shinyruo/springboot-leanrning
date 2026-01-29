# 章节逐章补强建议（part-02-autoproxy-and-pointcuts 自动代理与切点）

### 第 36 章：07. AutoProxyCreator 主线（autoproxy creator mainline）

- 建议把 AutoProxyCreator 的主线流程写成“算法视角”：输入是什么（bean/beanName/advisors）、输出是什么（原 bean 或代理），以及关键判断分支在哪里。
- 补强“与 BeanPostProcessor 链的关系”：解释它在容器生命周期中的位置（何时被调用、为什么），以及它如何影响最终 bean 实例。
- 补强“关键分支与缓存”：哪些结果会被缓存（避免重复判断）、哪些情况会被跳过（infrastructure class / 无 advisor 命中等）。
- 补强“循环依赖/早期引用”相关点：哪些路径会触发早期代理/提前暴露引用（只给必要深度，避免本章跑偏，但要能解释典型现象）。
- 给出一条可复现验证路径：推荐一组断点（来自断点地图）+ 对应 Lab/Test，让读者能沿着主线走通一次“决定代理 → 创建代理”。

### 第 37 章：08. 切点表达式系统（pointcut expression system）

- 建议补强“静态匹配 vs 动态匹配”的直觉：哪些表达式在创建代理时就能决定，哪些需要在运行时基于参数/注解再判断。
- 补强“表达式语义地图”：把常用 designator（如 execution/within/this/target/args/@annotation 等）按“作用对象、常见误区、与代理类型的关系”组织起来。
- 补强“代理语境下的坑”：接口代理与类代理下，`this/target/within` 等语义可能出现读者直觉偏差的地方要明确指出并给验证方式。
- 给出一组可验证的表达式用例：让读者通过 Lab/Test 看到哪些方法命中、哪些不命中，并能解释原因。

### （建议新增）并发与性能：代理在高并发下的正确性与成本

⚠️ 现状观察：模块中已存在并发相关的 Lab/Test（例如 `SpringCoreAopProxyConcurrencyLabTest`），但 docs 章节目录目前未显式覆盖这一主题，建议补齐一个对应章节并挂到 TOC。

- 深入方向建议
  - “线程安全边界”：代理对象通常是单例复用；哪些状态必须是无状态/线程安全（例如 Advice），哪些对象是每次调用独立（例如 invocation）。
  - “ThreadLocal 相关机制”：如 exposeProxy / invocation 暴露等机制在并发与嵌套调用下的风险点与最佳实践。
  - “性能成本可观测”：拦截链长度、匹配开销、反射调用/MethodHandle 等对吞吐的影响，建议用最小基准/统计观察而不是纯口头描述。
  - “实践建议”：如何写一个可复用的性能/并发验证入口（对读者友好：可运行、可复现、可定位）。

