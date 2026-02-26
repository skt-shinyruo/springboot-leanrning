# Technical Design: spring-core-beans 深挖升级（结构重构 + 知识点补齐 + 可复现实验）

## Technical Solution

### Core Technologies
- Java 17
- Spring Boot 3.5.x（对应 Spring Framework 6.x）
- JUnit 5（以测试作为最小启动器）

### Implementation Key Points

1. **以“refresh 时间线”为主叙事骨架**
   - 将 Bean 行为映射到容器启动关键阶段：定义注册 → BFPP/BDRPP → 注册 BPP → 单例创建/初始化 → 收尾事件。
   - 每章明确它在时间线中的位置：发生时机、影响对象（定义/实例/代理）、可观察入口。

2. **每章统一契约（A–G）并强化可复现闭环**
   - A：本章定位（解决什么问题/适用场景）
   - B：关键结论（1–5 条可复述结论）
   - C：主线（与上一章/下一章的承接）
   - D：源码导航（关键类/关键方法/关键数据结构）
   - E：最小实验（对应 Lab/Exercise；推荐运行命令；条件断点模板；观察点清单）
   - F：常见坑与边界（失败案例/误区/如何修复）
   - G：小结与预告（下一章为何需要）

3. **结构重构策略：避免大规模重命名，改用“增量新增 + TOC 重排”**
   - 为降低链接断裂风险，尽量不改动已有章节文件名与编号。
   - 新增专题采用新编号追加（例如 `18+`、`36+`），并在 `docs/README.md` 里按主线重新组织阅读顺序。

4. **知识点补齐的选题原则**
   - 以“读者高频困惑点 + 源码关键路径 + 可复现性”为三要素筛选。
   - 每个新增专题必须对应至少一个 Lab（若已有 Lab，则补齐文档指向；若缺失，则新增最小 Lab）。

5. **实验体系强化的落地策略**
   - 以 JUnit 测试方法为最小复现单元，确保：
     - 可精确运行：`mvn -pl spring-core-beans -Dtest=Class#method test`
     - 可条件断点降噪：约定 `beanName`/`bdName` 等变量命名以便设置条件
     - 可观测：提供 `BeanDefinition` 来源、依赖图、候选列表、缓存状态等 dump 工具

## Architecture Decision ADR

### ADR-1: 以“增量新增 + TOC 重排”替代“全量重命名重排”
**Context:** 需要加强“书籍式结构与主线叙事”，但大规模重命名会导致链接断裂、维护成本与合并冲突显著上升。  
**Decision:** 以保持现有章节文件名为前提，通过新增专题章节与 `docs/README.md` 的阅读顺序重排实现结构强化。  
**Rationale:** 兼顾读者体验与维护成本，降低变更风险。  
**Alternatives:** 全量重命名与重编号 → Rejection reason: 链接断裂与维护成本过高。  
**Impact:** 章节编号可能出现“非连续”，但可通过目录与导航保证阅读主线。

## Security and Performance
- **Security:**
  - 不引入任何密钥/Token/个人数据到文档与测试
  - 不执行生产环境相关操作，不引入高风险命令（rm -rf/数据库破坏等）
- **Performance:**
  - Labs 以最小容器启动与精确到方法运行降低耗时
  - 通过条件断点与观察点清单降低调试噪声

## Testing and Deployment
- **Testing:**
  - 运行模块测试：`mvn -pl spring-core-beans test`
  - 关键章节对应 Lab 精确运行：`mvn -pl spring-core-beans -Dtest=...#... test`
  - 如需：接入仓库已有 `scripts/test-module.sh` 的模块级验证
- **Deployment:** 无部署变更（仅文档与测试增强）

