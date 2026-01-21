<!--
⚠️ GENERATED FILE - 请勿手工编辑。
- Generator: scripts/generate-spring-beans-public-api-index.py
- Source: /home/feng/.m2/repository/org/springframework/spring-beans/6.2.15/spring-beans-6.2.15-sources.jar
- Generated at: 2026-01-20 14:30:48
-->

# 96. spring-beans Public API 覆盖差距（Gap）清单（Spring Framework 6.2.15）

本文件用于把“还缺什么”变成显式清单，配合：
- 索引：`/home/feng/code/project/springboot-leanrning/docs/beans/spring-core-beans/appendix/95-spring-beans-public-api-index.md`
- 分批补齐策略：HelloAGENTS 方案包 task.md

---
## 可运行入口（建议先跑再看 Gap）

本章是“缺口清单”，推荐先跑一个能让你进入 Spring Beans 主线的 Lab，再回来按需查 Gap：

- `SpringCoreBeansContainerLabTest`
- `SpringCoreBeansBeanCreationTraceLabTest`
- `SpringCoreBeansRegistryPostProcessorLabTest`
- `SpringCoreBeansTypeConversionLabTest`

---
## 概览

- 总 public 顶层类型（按 sources.jar 统计）：**320**
- 未映射（unmapped）：**0**
- partial 覆盖（需要后续补齐/深化）：**0**
- 索引指向缺失的 chapter：**0**
- 索引指向缺失的 lab：**0**

## 结论

- 当前索引规则无缺口（0 unmapped），且索引指向的 chapter/lab 均存在。

## 坑点与排障（把索引变成“可用工具”）

- **索引不是学习路线**：Index/GAP 的价值是“定位”，不是“背诵清单”。推荐先按 `docs/README.md` 的 Start Here 跑最小 Lab，再回索引做反查定位。
- **BeanFactory vs ApplicationContext 差异**：很多“注解不生效/生命周期不触发”的现象，根因是没有安装 `AnnotationConfigProcessors`（仅 `BeanFactory` 不会自动做这件事）。
- **FactoryBean 的双重身份**：`getBean("foo")` 拿到的是“产品对象”，`getBean("&foo")` 才是 `FactoryBean` 本身；排查类型不匹配/注入歧义时先确认你拿到的到底是谁。
- **代理导致的类型错觉**：JDK Proxy 只实现接口，无法赋值给具体类；当 BPP 提前暴露早期引用/创建代理时，“按具体类注入”可能失败，优先按接口注入或切换到 class-based proxy。
- **版本差异与定位方式**：不要依赖行号；用“入口测试方法 + 关键接口名 + `rg` 关键词”定位更稳（Spring 小版本内部实现经常移动）。
