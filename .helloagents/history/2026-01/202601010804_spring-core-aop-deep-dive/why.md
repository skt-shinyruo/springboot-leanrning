# 变更提案：spring-core-aop 深化（从“能跑”到“能解释 + 能断点 + 能定位问题”）

## 需求背景

`spring-core-aop` 模块已经具备可运行示例与 Labs/Exercises，但整体文档偏“概念速览”，对读者 C（源码级目标）而言仍存在学习断层：

1. **缺少“源码可复述”的主线：** 读者能看到 proxy/自调用/final 等现象，但难以把它们映射到 AOP 的基础设施（AutoProxyCreator 作为 BPP）与关键源码断点。
2. **缺少“结论 → 实验 → 断点入口 → 观察点”的闭环：** 现有 Labs/Exercises 很好用，但部分章节没有把“应该跑哪一个测试方法、打哪几个断点、观察哪些变量”系统化写清楚。

本次变更要把模块升级为：**AOP 源码级心智模型 + 可验证实验闭环**（不追求覆盖所有 AspectJ 语法细节，但追求“能定位真实项目里 AOP 为什么不生效/为什么类型不对/为什么顺序不对”）。

## 变更内容

1. 新增 AOP 深挖指南（断点与观察点清单），让读者能从“代理产生”一路走到“advice 链执行”。
2. 扩写核心章节：代理心智模型、JDK vs CGLIB、self-invocation、final/代理限制、exposeProxy、debugging、常见坑。
3. 补充自测题（可选但推荐），把关键结论固化成“能回答 + 能验证”的清单。

## 影响范围

- **Modules:** `spring-core-aop`
- **Files:** `spring-core-aop/README.md`、`docs/aop/spring-core-aop/*.md`
- **APIs:** 无对外 API 变更
- **Data:** 无数据模型变更

## 核心场景

### Requirement: AOP 能解释 + 能断点 + 能定位问题（读者 C）
**Module:** spring-core-aop
用户完成本模块后，应能把 AOP 的“代理与织入”机制落到源码与可运行实验，并能在真实项目里快速定位“不拦截/类型不对/顺序不对”等问题。

#### Scenario: 能解释 AOP 生效的前提（call path 必须走代理）
- 能解释“为什么 this.inner() 不会被拦截”
- 能用测试验证：入口走代理 vs 不走代理的差异

#### Scenario: 能解释 JDK vs CGLIB 对类型与注入的影响
- 能解释“为什么按实现类 getBean 会失败”
- 能用测试验证代理类型与可注入类型差异

#### Scenario: 能解释代理限制（final/private/static/构造期调用）
- 能解释“为什么 final method 拦截不到”
- 能给出工程实践建议（如何避免误判）

#### Scenario: 能解释并控制 advice 链顺序
- 能解释多个切面嵌套关系（谁在外层、谁先执行）
- 能用测试证明 `@Order` 生效

#### Scenario: 能排查 pointcut 误命中/漏命中
- 能从最小切点开始验证命中
- 能给出“排查顺序”，避免把配置/pointcut 问题误当机制结论

## 风险评估

- **风险:** 文档扩写可能引入与代码不一致的描述
- **Mitigation:** 文档以现有 Labs/Exercises 为事实来源；新增“断点闭环”指引；变更后运行单模块测试作为最低验证门槛

