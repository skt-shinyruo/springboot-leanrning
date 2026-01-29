# 逐章补强建议（part-00-guide 指南）

本 Part 是全书导航底座：主线、关键分支、断点锚点的“统一语义”在这里确定。补强目标以“可执行的学习路径 + 可复用断点包”为主。

### 第 10 章：主线时间线：IoC 容器从 refresh 到创建 Bean

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/010-03-mainline-timeline.md`
- 补强策略：
  - 把时间线从“阶段名”进一步细化到“关键方法 + 关键对象”：例如每一阶段至少给出 1–2 个锚点方法与 1 个观测对象（BeanDefinition/单例缓存/BPP 列表）。
  - 增加“Boot 叠加后的时间线差异”注记：把 auto-config 导入/条件评估/工厂后处理器对时间线的影响标出来，便于后续对比 Part 02。
  - 增加“从时间线到断点包”的映射：为每个阶段给出推荐断点与 watch list（例如 `beanFactory.getBeanPostProcessors()` 的变化）。

### 第 11 章：00. 深挖指南：把“Bean 三层模型”落到源码与断点

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/011-00-deep-dive-guide.md`
- 补强策略：
  - 把“症状驱动导航”做成更可执行的分流表：每个症状给出“第一断点入口 + 关键观察变量 + 最短章节链路”。
  - 增补“调试姿势与误区”：例如 step into 的粒度、条件断点的使用、如何避免被代理/反射栈淹没。
  - 把“证据链”写法再具体化：给出 1–2 个完整示例（结论→关键方法→反例），并链接到对应章节作为范本。

### 第 11 章：关键分支矩阵（Branch Decision Matrix）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/011-04-branch-decision-matrix.md`
- 补强策略：
  - 把矩阵从“概念分支”下沉到“代码分支”层级：每个分支给出真实 if/else 的入口方法（例如依赖解析分支、实例化策略分支、early reference 分支）。
  - 增加“跨章节复用”的分支引用：比如把 “type matching / FactoryBean / @Lazy 注入点”作为常见交叉分支单列，并链接到对应章节。

### 第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/012-01-quickstart-30min.md`
- 补强策略：
  - 为每个最小实验补“预期现象 + 常见偏差”：例如为什么你可能看不到预期（被代理替换、版本差异、测试选择错误）。
  - 增加“从快启到深挖”的明确下一步：每个实验跑完后给出 2–3 个章节链接与 1 个断点入口，把读者自然引到主线。

### 第 13 章：01：`refresh()` 调用链（容器从“定义”到“实例”的主线）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/013-01-applicationcontext-refresh-call-chain.md`
- 补强策略：
  - 增加“分层视角”的 refresh 主线：定义层（BDRPP/BFPP）与实例层（BPP/doCreateBean）在调用链上的窗口要更直观。
  - 把关键扩展点“落到入口方法”：例如每个扩展点给出 1 个最关键的入口方法与 1 个可观察变量，减少只记名词的情况。
  - 增补“常见问题定位”快速路径：比如“@Autowired 不生效/@Value 不生效/@Bean 不解析”对应缺失哪个 processor、在哪个阶段能看见。

### 第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/013-02-breakpoint-map.md`
- 补强策略：
  - 将断点按“问题类型”再聚类：注册问题、注入问题、代理问题、循环依赖、占位符/SpEL、FactoryBean 等，每类给出最小断点组。
  - 强化 watch list 的“看什么/怎么看”：给出每个关键观察变量的意义（例如单例缓存、early singleton、BPP 列表、merged BD）。
  - 对断点稳定性做注记：哪些断点是“机制级稳定锚点”，哪些是“实现细节可能漂移”，帮助读者避免后续版本迁移踩坑。

