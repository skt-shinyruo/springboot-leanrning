# Debugger Pack（断点包总入口）

> 目标：用最少的入口测试，把“主线时间线 / 关键分支 / 排障策略 / 性能并发”串成可运行的断点闭环。

## 推荐入口（从这里开始）

1. 主线调用链入口（refresh → doCreateBean）  
   - 运行：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansMainlineCallChainLabTest test`
2. 断点包入口（高频分支与排障）  
   - 运行：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBreakpointPackLabTest test`
3. 排障 Playbook 入口（现象 → 根因 → 验证）  
   - 运行：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansTroubleshootingPlaybookLabTest test`
4. 性能与并发入口（缓存/并发 getBean）  
   - 运行：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPerformanceConcurrencyLabTest test`

## 断点包索引（文档 ↔ 入口）

- 主线时间线：`part-00-guide/010-03-mainline-timeline.md`  
  入口测试：`SpringCoreBeansMainlineCallChainLabTest`
- 断点地图：`part-00-guide/013-02-breakpoint-map.md`  
  入口测试：`SpringCoreBeansBreakpointPackLabTest`
- 关键分支矩阵：`part-00-guide/011-04-branch-decision-matrix.md`  
  入口测试：`SpringCoreBeansBreakpointPackLabTest`
- 排障 Playbook：`appendix/025-90-common-pitfalls.md`  
  入口测试：`SpringCoreBeansTroubleshootingPlaybookLabTest`
- 并发与性能：`../../book/performance-and-concurrency.md`  
  入口测试：`SpringCoreBeansPerformanceConcurrencyLabTest`

## 关键断点建议（主线优先）

- `AbstractApplicationContext#refresh`
- `DefaultListableBeanFactory#preInstantiateSingletons`
- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `AbstractAutowireCapableBeanFactory#populateBean`
- `AbstractAutowireCapableBeanFactory#initializeBean`
- `DefaultListableBeanFactory#resolveDependency`

## 使用策略（1-2 次跳转定位问题）

1. 先定位阶段：看异常/现象属于“注册 / 注入解析 / 创建 / 初始化 / 代理替换”。  
2. 再定位分支：按“关键分支矩阵”缩小候选路径。  
3. 最后进入入口测试：从对应 Lab 运行并打断点确认。
