# 逐章内容级再加深建议（part-00-guide 指南）

本 Part 的再加深重点：把“怎么学”进一步落到“怎么证明/怎么排障/怎么复述”的可执行路径，避免读者只停留在导航层。

### 第 10 章：主线时间线：IoC 容器从 refresh 到创建 Bean

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/010-03-mainline-timeline.md`
- 内容级加深策略：
  - A：补“主线关键窗口最短调用链”：每个阶段至少给出 1 个入口方法与 1 个必看对象快照（definitions / processors / singleton caches）。
  - B：补“时间线误判反例”：例如把“创建顺序”与“注入选择”混为一谈；把 lazy-init 与 @Lazy 注入点混为一谈。
  - C：补“从症状回放到时间线窗口”：给 5 个症状（注入失败/代理/循环依赖/占位符/FactoryBean）对应时间线分叉点。
  - D：补“断点组 + watch list”：把时间线每一段映射到断点地图（在哪看处理器列表、在哪看缓存变化）。
  - E：补“面试复述模板”：要求用“主线→分支→证据链”三句复述，并给出示范答案结构。

### 第 11 章：00. 深挖指南：把“Bean 三层模型”落到源码与断点

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/011-00-deep-dive-guide.md`
- 内容级加深策略：
  - A：把“分层”再下沉为“第一断点入口选择器”（定义层/实例层/最终对象）并给最短调用链。
  - B：补“新手调试反例”：断点不命中/代理层过深/过早 getBean 导致现象偏移等。
  - C：把现有症状导航升级为“3 步 SOP”：症状→分层→第一断点（并给 watch list）。
  - D：补“断点稳定性注记”：哪些断点跨版本稳定，哪些可能漂移。
  - E：补“复述训练法”：把每章的“自检要点”转成面试追问的回答框架。

### 第 11 章：关键分支矩阵（Branch Decision Matrix）

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/011-04-branch-decision-matrix.md`
- 内容级加深策略：
  - A：把每个分支明确到“真实 if/return 发生点”（入口方法 + 分支条件）。
  - B：为每个分支补 1 个反例：何时这条规则不适用/会被更强信号覆盖（如 @Primary 覆盖 @Priority）。
  - C：补“分支误诊排障”：读者常把哪两个分支混淆？第一断点如何区分？
  - D：为分支矩阵提供“断点套件建议”：每类分支对应最小断点组。
  - E：补“追问题”：让读者能解释“为什么是这个顺序，而不是另一个顺序”。

### 第 12 章：01. 30 分钟快速闭环：先快后深（3 个最小实验入口）

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/012-01-quickstart-30min.md`
- 内容级加深策略：
  - A：为每个实验补“机制证据链入口”：跑完后下一步去哪下断点证明结论。
  - B：补“常见偏差反例”：为什么你可能看不到预期现象（版本差异/代理/初始化顺序等）。
  - C：补“从实验到排障”：实验结论如何映射到生产排障（第一断点在哪里）。
  - D：补“断点闭环路径”：每个实验推荐 3–5 个断点与 watch list。
  - E：补“3 分钟复述训练”：每个实验给 1 个面试式回答模板。

### 第 13 章：01：`refresh()` 调用链（容器从“定义”到“实例”的主线）

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/013-01-applicationcontext-refresh-call-chain.md`
- 内容级加深策略：
  - A：对关键节点补“为什么必须在这里做”：例如为何先 BFPP/再 BPP、为何 preInstantiateSingletons 在后半段。
  - B：补“过早 getBean 反例”：如何导致 BPP 未注册/注解不生效/占位符没解析等偏差。
  - C：补“按异常分型定位到 refresh 窗口”：解析失败 vs 创建失败 vs 运行期行为异常。
  - D：补“主线断点组”：给读者一组可复用的稳定锚点断点。
  - E：补“refresh 主线复述题”：让读者能复述 6 个关键节点及其作用。

### 第 13 章：02. 断点地图（容器主线：可复用断点/观察点清单）

- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/013-02-breakpoint-map.md`
- 内容级加深策略：
  - A：为每个断点补“它在证明什么分支”，避免断点清单变成“背方法名”。
  - B：补“断点误用反例”：哪些断点会因为版本/环境差异不稳定，如何替代。
  - C：补“从症状选择断点组”的决策表：注入/代理/循环依赖/占位符/FactoryBean 各选哪组。
  - D：把 watch list 升级为“判定标准”：变量值如何判断你处在哪条分支。
  - E：补“面试追问的断点证明”：给 3 个高频题对应的断点证明路径。

