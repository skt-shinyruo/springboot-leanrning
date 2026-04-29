# spring-core-aop

本模块用“可运行的最小示例 + 可验证的测试实验（实验/练习）”讲透 Spring AOP 的核心机制。

这份 `README.md` 只做索引与导航；更深入的解释请按章节阅读：见 docs/。


## 本模块读法

本模块入口页承担“定位路线”的职责：先把最小实验跑成事实，再沿主线章节解释机制，最后回到排障与自检材料确认边界。

- **先跑入口**：优先使用本页给出的 Book Matrix、Branch Matrix 或最小 Lab，把现象固定成可重复断言。
- **再读主线**：按“主线时间线 → 深挖导读 → 正文主题”的顺序阅读，避免只按文件名零散跳转。
- **最后排障**：遇到问题先回到断点地图、关键分支矩阵、常见坑和自检清单，把问题收敛到章节、断点与测试入口。

## 从这里开始（5 分钟闭环）

先把现象跑成事实，再回到 docs 顺读机制与边界：

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：
  - `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopAutoProxyBranchMatrixLabTest test`
  - `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyBranchMatrixLabTest test`
  - `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopStackingBranchMatrixLabTest test`

文档入口：
- 模块目录：见本 README 的「目录（唯一顺序来源）」
- 常见坑：[`appendix-common-pitfalls.md`](docs/appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](docs/appendix-self-check.md)

## 本模块完成后应能解释的内容

- AOP 默认通过 **代理（proxy）** 生效（Bean 可能会被包装成另一个对象）
- Advice / Pointcut 的最小闭环（本模块以 `@Around` + `@annotation(...)` 为主）
- 代理的典型限制：JDK vs CGLIB、`final` 方法、以及自调用陷阱

## 前置知识

- 先完成 `spring-core-beans`（知道什么是 Bean/容器就够）
- （可选）了解“方法调用链”与“入口是否走 Spring Bean”的区别

## 关键命令

### 运行

```bash
mvn -pl :spring-core-aop spring-boot:run
```

运行后观察控制台输出：

- 被拦截方法的 AOP 计时日志
- 自调用示例：只会拦截到 `outer(...)`，而 `inner(...)` 不会被拦截（因为内部调用没有经过代理）

### 测试

```bash
mvn -pl :spring-core-aop test
```

## docs 阅读顺序（从现象到机制）

0. [深挖指南：把“代理产生 + advice 链执行”落到源码与断点](docs/guide-deep-dive-guide.md)
1. [AOP 心智模型：代理 + 入口（call path）](docs/proxy-fundamentals-aop-proxy-mental-model.md)
2. [JDK vs CGLIB：代理类型与可注入类型差异](docs/proxy-fundamentals-jdk-vs-cglib.md)
3. [自调用陷阱：为什么 `this.inner()` 不会被拦截](docs/proxy-fundamentals-self-invocation.md)
4. [`final` 限制：为什么 final method 拦截不到](docs/proxy-fundamentals-final-and-proxy-limits.md)
5. [exposeProxy：用 `AopContext.currentProxy()` 绕过自调用（进阶）](docs/proxy-fundamentals-expose-proxy.md)
6. [Debug / 观察：如何“看见”代理与切点](docs/proxy-fundamentals-debugging.md)
7. [Advice 全家桶：@Before/@After/@AfterReturning/@AfterThrowing（语义与绑定）](docs/proxy-fundamentals-advice-types-and-binding.md)
8. [Introduction / Mixin：@DeclareParents 给 Proxy“加接口能力”](docs/proxy-fundamentals-introduction-mixin.md)
9. [TargetSource：Proxy 到底转发到谁（LazyInit/HotSwap…）](docs/proxy-fundamentals-targetsource-model.md)
10. [Proxy 的对象语义：equals/hashCode/toString/Map key（以及如何自证）](docs/proxy-fundamentals-proxy-object-semantics.md)
11. [AOP 的容器主线：AutoProxyCreator 作为 BPP（Advisor/Advice/Pointcut）](docs/autoproxy-and-pointcuts-autoproxy-creator-mainline.md)
12. [Pointcut 表达式系统：execution/within/this/target/args/@annotation/...（以及常见误判）](docs/autoproxy-and-pointcuts-pointcut-expression-system.md)
13. [除 `@EnableAspectJAutoProxy` 之外：BeanNameAutoProxyCreator / ProxyFactoryBean / XML](docs/autoproxy-and-pointcuts-other-configuration-entries.md)
14. [`@Aspect` 实例模型：singleton vs perthis/pertarget/pertypewithin（Spring AOP 语境）](docs/autoproxy-and-pointcuts-aspect-instantiation-models.md)
15. [并发 / 性能：同一 proxy 并发调用边界（ThreadLocal 不串线）](docs/perf-concurrency-proxy-concurrency-perf.md)
16. [多切面/多代理叠加与顺序：AOP/Tx/Cache/Security](docs/proxy-stacking-multi-proxy-stacking.md)
17. [真实项目叠加 Debug Playbook：AOP/Tx/Cache/Security 如何叠、如何断点验证](docs/proxy-stacking-real-world-stacking-playbook.md)
18. [Weaving vs Proxy：何时该跳到 weaving（决策表）](docs/appendix-weaving-vs-proxy-decision-matrix.md)
19. [常见坑清单（排查时对照）](docs/appendix-common-pitfalls.md)
20. [自测题：是否真正理解了 AOP？](docs/appendix-self-check.md)

## 实验/练习索引（按知识点 / 难度）

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。练习默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 延伸阅读 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopLabTest.java` | 最小 advice 闭环 + 自调用陷阱 | ⭐⭐ | [代理心智模型](docs/proxy-fundamentals-aop-proxy-mental-model.md)、[self-invocation](docs/proxy-fundamentals-self-invocation.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopProxyMechanicsLabTest.java` | JDK vs CGLIB、final 限制、advice 顺序 | ⭐⭐⭐ | [JDK vs CGLIB](docs/proxy-fundamentals-jdk-vs-cglib.md)、[final 限制](docs/proxy-fundamentals-final-and-proxy-limits.md)、[代理调试](docs/proxy-fundamentals-debugging.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopAdviceTypesAndBindingLabTest.java` | Advice 全家桶语义差异 + returning/throwing/args/@annotation/JoinPoint 绑定 | ⭐⭐ | [Advice 全家桶](docs/proxy-fundamentals-advice-types-and-binding.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopIntroductionDeclareParentsLabTest.java` | Introduction/Mixin：@DeclareParents 引入接口能力 | ⭐⭐ | [Introduction/Mixin](docs/proxy-fundamentals-introduction-mixin.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopTargetSourceLabTest.java` | TargetSource：LazyInit/HotSwap（proxy 不变、target 可变） | ⭐⭐⭐ | [TargetSource](docs/proxy-fundamentals-targetsource-model.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopProxyObjectSemanticsLabTest.java` | proxy 的对象语义：getClass/instanceof/Map key | ⭐⭐ | [Proxy 对象语义](docs/proxy-fundamentals-proxy-object-semantics.md)、[JDK vs CGLIB](docs/proxy-fundamentals-jdk-vs-cglib.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopAutoProxyCreatorInternalsLabTest.java` | AutoProxyCreator 作为 BPP 的主线 + Advisor/Advice/Pointcut 三层模型 | ⭐⭐⭐ | [AutoProxyCreator 主线](docs/autoproxy-and-pointcuts-autoproxy-creator-mainline.md)、[AOP 调用链](docs/guide-aop-invocation-call-chain.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part03_proxy_stacking/SpringCoreAopProceedNestingLabTest.java` | 拦截器链执行细节：`proceed()` 嵌套与 before/after 顺序 | ⭐⭐⭐ | [AOP 调用链](docs/guide-aop-invocation-call-chain.md)、[代理调试](docs/proxy-fundamentals-debugging.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopPointcutExpressionsLabTest.java` | pointcut 误判最小复现：this vs target（JDK/CGLIB 差异） | ⭐⭐⭐ | [切点表达式系统](docs/autoproxy-and-pointcuts-pointcut-expression-system.md)、[JDK vs CGLIB](docs/proxy-fundamentals-jdk-vs-cglib.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopRuntimePointcutCostLabTest.java` | 动态切点成本：`MethodMatcher#isRuntime()` 的 per-invocation 匹配 | ⭐⭐⭐ | [切点表达式系统](docs/autoproxy-and-pointcuts-pointcut-expression-system.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopBeanNameAutoProxyCreatorLabTest.java` | 遗留入口：BeanNameAutoProxyCreator（按 beanName 批量代理） | ⭐⭐ | [其它装配入口](docs/autoproxy-and-pointcuts-other-configuration-entries.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopXmlAopConfigLabTest.java` | 遗留入口：XML `<aop:config>`（advisor/interceptor） | ⭐⭐ | [其它装配入口](docs/autoproxy-and-pointcuts-other-configuration-entries.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopAspectInstantiationModelLabTest.java` | pertarget + prototype gate：为什么 per-clause 表面上“不生效” | ⭐⭐⭐ | [Aspect 实例模型](docs/autoproxy-and-pointcuts-aspect-instantiation-models.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part03_proxy_stacking/SpringCoreAopMultiProxyStackingLabTest.java` | 多 advisor vs 多层 proxy（嵌套代理）+ 顺序与观察方法 | ⭐⭐⭐ | [多层代理叠加](docs/proxy-stacking-multi-proxy-stacking.md)、[代理调试](docs/proxy-fundamentals-debugging.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part03_proxy_stacking/SpringCoreAopRealWorldStackingLabTest.java` | 真实叠加：Tx/Cache/Method Security 与自定义 AOP 同链路可断言 | ⭐⭐⭐ | [叠加排障手册](docs/proxy-stacking-real-world-stacking-playbook.md) |
| Lab | `src/test/java/com/learning/springboot/springcoreaop/part02_perf_concurrency/SpringCoreAopProxyConcurrencyLabTest.java` | 并发/性能边界：同一 proxy 并发调用（ThreadLocal 不串线） | ⭐⭐⭐ | [并发/性能边界](docs/perf-concurrency-proxy-concurrency-perf.md) |
| Exercise | `src/test/java/com/learning/springboot/springcoreaop/SpringCoreAopExerciseTest.java` | exposeProxy/多切面顺序/pointcut 风格等练习 | ⭐⭐–⭐⭐⭐ | 先把 Labs 理解透再做 |

## 概念 → 在本模块哪里能“看见”

| 要理解的概念 | 去读哪一章 | 去看哪个测试/代码 | 应能解释清楚 |
| --- | --- | --- | --- |
| advice 为什么能“拦截”方法 | [代理心智模型](docs/proxy-fundamentals-aop-proxy-mental-model.md) | `SpringCoreAopLabTest#adviceIsAppliedToTracedMethod` + `TracingAspect` | 代理如何把横切逻辑织入调用链 |
| 自调用为什么绕过代理 | [self-invocation](docs/proxy-fundamentals-self-invocation.md) | `SpringCoreAopLabTest#selfInvocationDoesNotTriggerAdviceForInnerMethod` + `SelfInvocationExampleService` | “走没走代理”决定 advice 生效与否 |
| JDK vs CGLIB 代理差异 | [JDK vs CGLIB](docs/proxy-fundamentals-jdk-vs-cglib.md) | `SpringCoreAopProxyMechanicsLabTest#jdkDynamicProxyIsUsedForInterfaceBasedBeans_whenProxyTargetClassIsFalse` | 为什么有时 `getBean(实现类.class)` 会失败 |
| `final` method 拦截不到 | [final 限制](docs/proxy-fundamentals-final-and-proxy-limits.md) | `SpringCoreAopProxyMechanicsLabTest#finalMethodsAreNotInterceptedByCglibProxies` | CGLIB 基于继承，无法覆盖 final |
| Advice 全家桶语义差异 | [Advice 全家桶](docs/proxy-fundamentals-advice-types-and-binding.md) | `SpringCoreAopAdviceTypesAndBindingLabTest` | success/error 两条路径的顺序 + returning/throwing/args/@annotation 绑定 |
| Introduction/Mixin（加接口能力） | [Introduction/Mixin](docs/proxy-fundamentals-introduction-mixin.md) | `SpringCoreAopIntroductionDeclareParentsLabTest` | 代理对象为何能 `instanceof NewInterface`，但 target class 不变 |
| TargetSource（target 从哪来） | [TargetSource 模型](docs/proxy-fundamentals-targetsource-model.md) | `SpringCoreAopTargetSourceLabTest` | proxy 不变、target 可切换/延迟创建；调试入口 `getTargetSource()` |
| proxy 的对象语义（类型/identity） | [Proxy 对象语义](docs/proxy-fundamentals-proxy-object-semantics.md) | `SpringCoreAopProxyObjectSemanticsLabTest` | getClass/instanceof/Map key 在 proxy 语境下为什么会误判 |
| 多个切面顺序怎么控制 | [代理调试](docs/proxy-fundamentals-debugging.md) | `SpringCoreAopProxyMechanicsLabTest#adviceOrderingCanBeControlledWithOrderAnnotation` | `@Order` 对 advice 链的影响 |
| AutoProxyCreator 为什么是 BPP | [AutoProxyCreator 主线](docs/autoproxy-and-pointcuts-autoproxy-creator-mainline.md) | `SpringCoreAopAutoProxyCreatorInternalsLabTest` | 代理何时产生、Advisor 如何筛选、为什么这个 bean 会/不会被代理 |
| pointcut 最常见误判（this vs target） | [切点表达式系统](docs/autoproxy-and-pointcuts-pointcut-expression-system.md) | `SpringCoreAopPointcutExpressionsLabTest` | 为什么同一表达式在 JDK/CGLIB 下命中不同、如何用断言验证 |
| 动态切点的成本模型 | [切点表达式系统](docs/autoproxy-and-pointcuts-pointcut-expression-system.md) | `SpringCoreAopRuntimePointcutCostLabTest` | `MethodMatcher#isRuntime()` 为什么带来 per-invocation 开销 |
| 除注解外的装配入口 | [其它装配入口](docs/autoproxy-and-pointcuts-other-configuration-entries.md) | `SpringCoreAopBeanNameAutoProxyCreatorLabTest` / `SpringCoreAopXmlAopConfigLabTest` | 没有 @Aspect 也能有 AOP：advisors → proxy → chain |
| Aspect 实例模型（prototype gate） | [Aspect 实例模型](docs/autoproxy-and-pointcuts-aspect-instantiation-models.md) | `SpringCoreAopAspectInstantiationModelLabTest` | pertarget/perthis 看似“不生效”的根因：aspect 必须 prototype |
| 并发/性能边界（ThreadLocal 不串线） | [并发/性能边界](docs/perf-concurrency-proxy-concurrency-perf.md) | `SpringCoreAopProxyConcurrencyLabTest` + `CorrelationIdAspect` | 为什么 proxy 可并发调用，但 per-invocation 状态必须线程隔离/可清理 |
| 多 advisor vs 多层 proxy（嵌套代理） | [多层代理叠加](docs/proxy-stacking-multi-proxy-stacking.md) | `SpringCoreAopMultiProxyStackingLabTest` | “叠加”到底是什么形态、顺序问题如何分流定位 |
| 真实叠加（Tx/Cache/Security）排障 | [叠加排障手册](docs/proxy-stacking-real-world-stacking-playbook.md) | `SpringCoreAopRealWorldStackingLabTest` | 真实基础设施下如何用断点与断言定位“不生效/被绕过/短路/顺序怪” |

## 常见 Debug 路径

- 先确认“是不是代理”：`AopUtils.isAopProxy(bean)`
- 再确认“是什么代理”：`AopUtils.isJdkDynamicProxy(bean)` / `AopUtils.isCglibProxy(bean)`
- 观察切点是否命中：先让 advice 里写入 `InvocationLog`（比依赖日志输出更稳定）
- 观察“叠加”实体：`bean instanceof Advised` → `((Advised) bean).getAdvisors()`
- 遇到“不拦截”的问题，优先排查：是否自调用、是否 `final`、是否调用入口走的是 Spring 管理的 bean

## 常见坑

- 表面上在调用目标对象，实际在调用代理对象（类型/调试现象会不一样）
- 自调用绕过代理：同类内部 `this.xxx()` 不会触发 advice
- `final` 方法/类的限制：CGLIB 不能覆盖 final method，JDK 代理也只能代理接口方法
- 只有 Spring 容器管理的 bean 才能被代理；`new` 出来的对象不会被拦截
- pointcut 写得“太宽/太窄”都会导致机制误判（先用最小切点验证）

## 参考

- Spring Framework Reference：AOP
- Spring Boot Reference：AOP starter（`spring-boot-starter-aop`）

## 目录（唯一顺序来源）

> 本模块 `docs/` 目录保持扁平；阅读顺序只在本 `README.md` 维护。正文页同时提供“上一章/下一章”导航，便于顺读。
> 原 `docs/README.md` 标题：Spring Core AOP：代理、切点与叠加边界

本模块把 AOP 的关键问题放回“代理对象”这一事实：代理何时产生、JDK/CGLIB 的选择如何影响边界、自调用为何绕过切面、以及多个代理叠加时如何定位最终执行的 Advice 链。阅读与排障的核心策略是先跑通代理主线，再进入 AutoProxyCreator 的装配主线，最后处理真实工程中常见的多层代理叠加。

---

### 10 分钟入口：先跑通一次 Advice 链执行
- `mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`

运行后应能回答：代理入口在哪里；Advice 链的执行顺序如何形成；自调用为何会绕过代理边界。

### Beans 前置（作为前置内容先读一次）
AOP 文档的很多“表面上像 AOP 的问题”，根因本质上发生在 **Bean 创建阶段**（什么时候被替换成 proxy / early reference 如何参与循环依赖）。

先用 Beans 的两章把“代理替换发生在哪个阶段”建立成稳定心智模型：

- [Beans Why Index（基础问题索引）](../spring-core-beans/docs/guide-why-index.md)
- [Beans：代理替换发生在哪个阶段](../spring-core-beans/docs/wiring-proxying-phase-bpp-wraps-bean.md)
- [Beans：early reference 与循环依赖](../spring-core-beans/docs/internals-early-reference-and-circular.md)

### 从这里开始（顺读路径）
1. [主线时间线](docs/guide-mainline-timeline.md)
2. [深挖导读](docs/guide-deep-dive-guide.md)
3. [AOP 调用链（从代理入口到 Advice 链执行）](docs/guide-aop-invocation-call-chain.md)

### 顺读主线
- [代理心智模型](docs/proxy-fundamentals-aop-proxy-mental-model.md)
- [JDK vs CGLIB](docs/proxy-fundamentals-jdk-vs-cglib.md)
- [self-invocation](docs/proxy-fundamentals-self-invocation.md)
- [代理限制（final 等）](docs/proxy-fundamentals-final-and-proxy-limits.md)
- [exposeProxy](docs/proxy-fundamentals-expose-proxy.md)
- [代理调试](docs/proxy-fundamentals-debugging.md)
- [Advice 全家桶：语义与绑定](docs/proxy-fundamentals-advice-types-and-binding.md)
- [Introduction / Mixin（加接口能力）](docs/proxy-fundamentals-introduction-mixin.md)
- [TargetSource（target 从哪来）](docs/proxy-fundamentals-targetsource-model.md)
- [Proxy 对象语义（equals/hashCode/Map key）](docs/proxy-fundamentals-proxy-object-semantics.md)
- [AutoProxyCreator 主线](docs/autoproxy-and-pointcuts-autoproxy-creator-mainline.md)
- [切点表达式系统](docs/autoproxy-and-pointcuts-pointcut-expression-system.md)
- [其它装配入口：BeanName/ProxyFactoryBean/XML](docs/autoproxy-and-pointcuts-other-configuration-entries.md)
- [Aspect 实例模型（prototype gate）](docs/autoproxy-and-pointcuts-aspect-instantiation-models.md)
- [并发/性能边界：同一 proxy 并发调用（ThreadLocal 不串线）](docs/perf-concurrency-proxy-concurrency-perf.md)
- [多层代理叠加](docs/proxy-stacking-multi-proxy-stacking.md)
- [叠加排障手册](docs/proxy-stacking-real-world-stacking-playbook.md)
- [Weaving vs Proxy 决策表](docs/appendix-weaving-vs-proxy-decision-matrix.md)

---

### 排障入口（从症状回到最短分支）
- 断点地图（排障优先）：[04-breakpoint-map.md](docs/guide-breakpoint-map.md)
- AOP 调用链（源码主线锚点）：[03-aop-invocation-call-chain.md](docs/guide-aop-invocation-call-chain.md)
- 关键分支矩阵（If/Then 收敛）：[05-branch-decision-matrix.md](docs/guide-branch-decision-matrix.md)
- 并发/性能边界（ThreadLocal 不串线）：[01-proxy-concurrency-perf.md](docs/perf-concurrency-proxy-concurrency-perf.md)
- 选型/边界（proxy vs weaving）：[Weaving vs Proxy 决策表](docs/appendix-weaving-vs-proxy-decision-matrix.md)
- 排障 playbook：[01-common-pitfalls.md](docs/appendix-common-pitfalls.md)
- 自检清单：[02-self-check.md](docs/appendix-self-check.md)

---

### 可运行入口（用于复现/回归）
- Book Matrix：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`
- Branch Matrix（Proxy 基础）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyBranchMatrixLabTest test`
- Branch Matrix（AutoProxy）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopAutoProxyBranchMatrixLabTest test`
- Branch Matrix（多代理叠加）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopStackingBranchMatrixLabTest test`
- Solutions（练习 答案回归）：`mvn -q -pl :spring-core-aop -Dtest=*ExerciseSolutionTest test`
- 并发/性能（同一 proxy 并发调用边界）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyConcurrencyLabTest test`

### 排坑与自检
- [常见坑](docs/appendix-common-pitfalls.md)
- [Weaving vs Proxy 决策表](docs/appendix-weaving-vs-proxy-decision-matrix.md)
- [自检](docs/appendix-self-check.md)
