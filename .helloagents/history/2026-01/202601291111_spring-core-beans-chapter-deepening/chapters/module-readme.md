# 逐章补强建议（模块 README）

目标：把 `spring-core-modules/spring-core-beans/README.md` 从“入口索引”进一步提升为“读者第一站”，让读者能在 3 分钟内明确：跑什么、怎么看、遇到问题怎么定位章节与断点。

### spring-core-beans（模块 README）

- 关联文件：`spring-core-modules/spring-core-beans/README.md`
- 补充/完善/深入策略：
  - 增加“读者画像/预期能力”段落：区分“能用/能断点/能排障/面试冲刺”，并给出对应最短路径（链接到 `docs/README.md` 的阅读路线与 appendix）。
  - 增加“从症状定位章节”小索引：把 `NoSuchBeanDefinitionException`、`NoUniqueBeanDefinitionException`、循环依赖、@Value 解析失败、代理行为异常等症状映射到 2–3 个最短章节入口。
  - 把“版本信息”显式化：例如 Spring Framework/Spring Boot 的版本来源位置（pom 依赖）与关键差异提示，避免文档结论被误用到其他版本。
  - 增补“快速实验矩阵”：把现有 Lab/Test 按主题聚类（注册/注入/生命周期/处理器/代理/Boot/AOT），读者可按问题快速选择。
  - 增加“调试闭环入口”：引用 Debugger Pack 与断点地图，明确推荐的断点包与 watch list 的使用方式。

