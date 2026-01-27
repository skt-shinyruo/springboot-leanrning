# Technical Design: spring-core-beans 教程体系重构（源码进阶 / 面试 / 团队内训）

## Technical Solution

### Core Technologies
- Java 17
- Spring Boot 3.x（本仓库 parent）
- Spring Framework 6.x（随 Boot BOM）
- Maven Surefire（可运行 Labs/Exercises）
- Markdown 文档体系（repo 内维护，默认不引入新的站点生成器/外部依赖）

### Implementation Key Points

- **“课程入口”重构优先：** 先把 `README.md` 与 `docs/README.md` 做成可执行入口（快启、路线、验收、调试策略），再逐章深化内容，避免“堆很多文档但没人能学”。
- **统一章节模板（面向学习/排障/面试三用）：**
  1) 导读（本章解决什么问题）  
  2) 本章要点（可复述）  
  3) 本章配套实验（先跑再读）  
  4) 源码主线（关键调用链）  
  5) 关键分支/边界（决策表/反例）  
  6) 断点与观察点（watch list）  
  7) 常见坑与排障套路（现象→阶段→入口→变量→修复）  
  8) 面试高频问法（标准答案骨架）  
  9) 一句话自检 + 上下章导航  
- **主题闭环策略：** 对每个主题建立 “章节 ↔ LabTest ↔ 关键断点/观察点” 的双向映射；如果现有 Lab 不足以闭环，则补齐最小可运行测试（默认参与回归或挂到 explore 开关）。
- **内训交付补齐：** 为每个主题补“课时拆分建议 + 练习题 + 讨论点 + 常见误区”，并能与 Labs 组合成 60/90/120 分钟的教学单元。
- **维护策略：** 通过“索引与检查表”降低未来维护成本（新增章节必须挂到 TOC/知识地图/断点包；章节末尾必须有自检与导航）。

## Security and Performance

- **Security:**
  - 不在文档/测试中引入任何密钥、token、生产环境地址。
  - 新增测试避免访问外部网络服务；如必须演示外部资源，使用 mock/stub，并明确标注为非生产方案。
- **Performance:**
  - 默认回归测试保持可控；高开销探索类用例放到 `springcorebeans.explore=true` 开关下。
  - 新增 Labs 以最小上下文为原则，避免引入额外 starter 导致启动/扫描成本飙升。

## Testing and Deployment

- **Testing:**
  - 基线：`mvn -pl :spring-core-beans test`
  - 阶段性：针对新增/改动章节的对应 LabTest 单测验证
  - 回归：完成一个主题闭环后跑一次模块全量测试
- **Deployment:** None（文档与测试随仓库交付）

