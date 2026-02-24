# Task List: spring-boot-async-scheduling “人写化”改造（docs + DemoRunner）

Directory: `helloagents/plan/202602230930_async-scheduling-humanize-writing/`

---

## 1. 入口页与导读（先让读者愿意读下去）
- [√] 1.1 改写 docs 目录页为“作者导言 + 阅读路线”，减少模板句与口号词：`spring-boot-modules/spring-boot-async-scheduling/docs/README.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-guide-页减少模板词与教练口吻
- [√] 1.2 改写主线时间线为更自然的“章节为什么这样排”，避免清单化：`spring-boot-modules/spring-boot-async-scheduling/docs/part-00-guide/117-03-mainline-timeline.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-guide-页减少模板词与教练口吻
- [√] 1.3 改写深挖导读为更像教材/博客的“开场白”，把验证入口下移到章末：`spring-boot-modules/spring-boot-async-scheduling/docs/part-00-guide/118-00-deep-dive-guide.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-guide-页减少模板词与教练口吻

## 2. 主线章节（正文叙事优先，证据入口下移）
- [√] 2.1 改写 119：减少“教练口吻”，把 proxy/线程切换讲成连续论证：`spring-boot-modules/spring-boot-async-scheduling/docs/part-01-async-scheduling/119-01-async-proxy-mental-model.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-主线章节读起来像连续文章
- [√] 2.2 改写 120：executor/线程/上下文段落改成更自然叙事，保留 TaskDecorator/泄漏反例但降模板：`spring-boot-modules/spring-boot-async-scheduling/docs/part-01-async-scheduling/120-02-executor-and-threading.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-主线章节读起来像连续文章
- [√] 2.3 改写 121：异常语义改写为“为什么调用方看不到”，减少 bullet 堆砌：`spring-boot-modules/spring-boot-async-scheduling/docs/part-01-async-scheduling/121-03-exceptions.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-主线章节读起来像连续文章
- [√] 2.4 改写 122：self-invocation 章节更像“经验总结 + 机制解释”，避免口号化：`spring-boot-modules/spring-boot-async-scheduling/docs/part-01-async-scheduling/122-04-self-invocation.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-主线章节读起来像连续文章
- [√] 2.5 改写 123：调度章节减少“模板坑位”，更像作者讲解并保留可验证入口：`spring-boot-modules/spring-boot-async-scheduling/docs/part-01-async-scheduling/123-05-scheduling-basics.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-主线章节读起来像连续文章
- [√] 2.6 改写 126：事务边界章节从“结论清单”改为“直觉→误解→判断方法”：`spring-boot-modules/spring-boot-async-scheduling/docs/part-01-async-scheduling/126-06-async-and-transactions.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-主线章节读起来像连续文章
- [√] 2.7 改写 127：Security/Request 上下文章节降噪，强调“为什么危险/怎么修/如何避免泄漏”：`spring-boot-modules/spring-boot-async-scheduling/docs/part-01-async-scheduling/127-07-security-and-request-context.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-主线章节读起来像连续文章
- [√] 2.8 改写 128：Boot `spring.task.*` 章节改写为“默认行为怎么理解 + 怎么反证”，减少模板句：`spring-boot-modules/spring-boot-async-scheduling/docs/part-01-async-scheduling/128-08-boot-spring-task-autoconfig.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-主线章节读起来像连续文章

## 3. Guide 工具页（保留功能，但改成读者会用的文字）
- [√] 3.1 改写断点地图为“排障叙事”，减少堆类名与模板句：`spring-boot-modules/spring-boot-async-scheduling/docs/part-00-guide/118-02-breakpoint-map.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-guide-页减少模板词与教练口吻
- [√] 3.2 改写关键分支矩阵文字描述（表格保留，但弱化口号与重复句）：`spring-boot-modules/spring-boot-async-scheduling/docs/part-00-guide/118-04-branch-decision-matrix.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-guide-页减少模板词与教练口吻

## 4. Appendix（排障与自检也要像人写的）
- [√] 4.1 Pitfalls 改写为“排障短文”，减少编号模板、保留 Proof 指针：`spring-boot-modules/spring-boot-async-scheduling/docs/appendix/124-90-common-pitfalls.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-appendix-更像排障短文习题册
- [√] 4.2 Self-check 改写为“习题册口吻”，减少问卷化措辞：`spring-boot-modules/spring-boot-async-scheduling/docs/appendix/125-99-self-check.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-appendix-更像排障短文习题册

## 5. README + DemoRunner（可运行入口也要像人写的）
- [√] 5.1 模块 README 改写为“作者说明 + 路线图 + 怎么跑”，弱化表格密度：`spring-boot-modules/spring-boot-async-scheduling/README.md`，verify why.md#requirement-文档改写后更像人写的但仍可定位与可验证#scenario-guide-页减少模板词与教练口吻
- [√] 5.2 DemoRunner 输出改写为“分节讲解”，降低机械 key/value 感：`spring-boot-modules/spring-boot-async-scheduling/src/main/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/AsyncSchedulingDemoRunner.java`，verify why.md#requirement-demrunner-输出更像作者讲解

## 6. Verification + Knowledge Base
- [√] 6.1 连续运行模块测试至少 3 次：`mvn -q -pl :spring-boot-async-scheduling test`
- [√] 6.2 运行 demo 并人工抽查输出可读性：`mvn -pl :spring-boot-async-scheduling spring-boot:run`
- [√] 6.3 同步更新知识库与变更记录，并迁移方案包到 history，verify why.md#requirement-技术正确性不变可回归
