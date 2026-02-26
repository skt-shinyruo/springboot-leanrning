# 逐章补强建议（Docs TOC / 目录与导航）

目标：把 `docs/README.md` 从“目录页”升级为“导航中枢”，能把读者从“问题/症状”快速带到“章节 + Lab + 断点入口”，并减少跨章节跳转成本。

### spring-core-beans 文档导航（Docs TOC）

- 关联文件：`spring-core-modules/spring-core-beans/docs/README.md`
- 补充/完善/深入策略：
  - 在目录前置“症状驱动导航（快速索引）”：用 10–15 个高频症状（注入歧义、代理、循环依赖、@Value、FactoryBean、BeanDefinition 覆盖等）映射到章节与推荐 Lab。
  - 增强“章节契约”解释：不引入统一硬性标准，但给出 2–3 个示例（例如“循环依赖/FactoryBean/占位符”）展示“结论 → 证据链 → 反例/误区”的写法，帮助读者理解每章补强目标。
  - 将“断点地图 / Debugger Pack / Knowledge Map / Troubleshooting Checklist”打通：在目录页增加这些工具型章节的定位与用途对照，避免读者不知道什么时候该去 appendix。
  - 为每个 Part 增加“本 Part 解决的问题是什么”一段摘要，并给出“从哪一章开始”与“串联阅读顺序”建议（例如 Part 04 先读 Lazy/dependsOn 再读代理产生阶段）。
  - 校验并补齐 BOOKIFY/导航链接一致性：确保章节之间的“上一章/下一章/目录”链路稳定可用。

