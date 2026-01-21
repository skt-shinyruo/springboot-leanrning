# Technical Design: 继续深化文档（Beans：主线/Bootstrap/Guide；Web MVC：主链路）

## Technical Solution

### Core Technologies

- 文档：Markdown（GitHub Pages / docs-site MkDocs 聚合）
- Spring Framework `6.2.15`（由 Spring Boot `3.5.9` 管理）
- 验证：仓库内 `*LabTest`（可复现、可回归）

### Implementation Key Points

#### 1) Beans 主线章（18）：补齐关键分支的“源码落点”

目标：让读者能把 `refresh()` 的“第 9 步”与 `doGetBean()` 的“创建窗口”落到可断点的关键分支上。

补齐点（优先级从高到低）：

- `finishBeanFactoryInitialization` 的完整关键动作：`freezeConfiguration`、`preInstantiateSingletons`、`SmartInitializingSingleton.afterSingletonsInstantiated`
- `DefaultListableBeanFactory#preInstantiateSingletons` 的筛选分支：abstract/lazy/singleton/FactoryBean/SmartFactoryBean eager-init/可选 background init
- `AbstractBeanFactory#doGetBean` 的关键分支：缓存命中/创建/dependsOn/parentBeanFactory fallback/prototype creation guard

#### 2) Beans bootstrap 章（022）：把“注解能力”拆成处理器表 + 时间线

目标：把“注解能工作”从概念解释升级为源码级可复述：

- 功能 → 处理器 → 类型（BDRPP/BFPP/BPP） → 关键方法 → refresh 阶段
- 把“注册进 registry（定义层）”与“进入 BeanFactory/BPP 列表（实例层）”的差异讲成一条时间线
- 明确“过早 getBean”会导致哪些行为缺失（BPP 未注册 → 注入/回调/代理不生效）

#### 3) 深挖指南（011）：加入“症状驱动导航”

目标：把指南变成“像书的索引”：

- 现象 → 章节入口（优先主线章 18/022/14/16/31/35 等） → 建议断点 → 推荐 LabTest
- 提供一个最小的“排障三连问”：我现在在定义层/候选层/实例层？我处在 refresh 哪一段？我看到的是 raw/early/wrapped 哪一种对象？

#### 4) Web MVC 主链路（067）：ERROR vs ASYNC dispatch 对照

目标：降低排障时“同一个请求走两次”的误判：

- 增补 ERROR dispatch 时间线（DispatcherType.ERROR）与 `/error` 的关键落点
- 与 ASYNC dispatch 时间线（DispatcherType.ASYNC）并排对照
- 给出可断言证据链：Trace Lab 的事件序列 + Spring Boot error 的端到端实验

## Security and Performance

- **Security:** 仅文档/知识库更新；不引入密钥、不连接生产环境、不新增危险脚本
- **Performance:** 无运行时影响

## Testing and Deployment

- **Testing:**
  - `mvn -q -pl spring-core-beans test`
  - `mvn -q -pl springboot-web-mvc test`
- **Deployment:** 沿用现有 docs-site/GitHub Pages 发布流程
