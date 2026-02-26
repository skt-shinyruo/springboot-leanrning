# Task List: 完成遗留未勾选任务（2026-01）

目标：把三份历史方案包中的未完成项落地为“可导航 + 可验证 + 可排障”的闭环，并回填 task 状态。

来源（SSOT）：
- `helloagents/history/2026-01/202601131039_teaching-experience-webmvc-beans/task.md`
- `helloagents/history/2026-01/202601091043_deepen_beans_aop_tx_web/task.md`
- `helloagents/history/2026-01/202601051050_spring_core_beans_deepen/task.md`

---

## 1. 路线图与知识库入口收敛
- [√] 1.1 新增 `helloagents/wiki/learning-path.md`（主线/机制线 + 第一个可运行入口）
- [√] 1.2 更新 `helloagents/wiki/overview.md` 增加路线图入口
- [√] 1.3 更新四模块知识库页（Beans/AOP/Tx/Web MVC）增加 Start Here 与路线图链接

## 2. springboot-web-mvc：Part 01 Debugger Pack + 关键分支 Labs
- [√] 2.1 补齐 Part 01 文档的 `Debug 建议`/Call-chain/关键分支说明（01~05）
- [√] 2.2 新增 `docs/web-mvc/springboot-web-mvc/part-00-guide/02-breakpoint-map.md` 并更新 docs README 导航
- [√] 2.3 补齐 Part 01 缺失知识点的最小 Labs（方法级入口），并把证据链写回文档（method validation / BindingResult / resolver priority / InitBinder / 404/405/415 等）
- [√] 2.4 完善 view 章节常见坑（Thymeleaf + ViewResolver）并绑定 Lab 入口

## 3. spring-core-beans：文档空块补齐 + 方法级入口绑定
- [√] 3.1 新增 `docs/beans/spring-core-beans/part-00-guide/02-breakpoint-map.md`（容器主线断点/观察点清单）
- [√] 3.2 补齐 `docs/beans/spring-core-beans/part-00-guide/00-deep-dive-guide.md` 等空洞段落（入口测试/断点/获得什么）
- [√] 3.3 系统补齐 docs 中 `对应测试：`/`入口：`/`一句话自检`/`排障分流`/`源码锚点` 空块（Part 01/03/04/Appendix）
- [√] 3.4 复核并补齐 Part 05（AOT & Real World）章节的模板残留与入口测试绑定（40~50）
- [√] 3.5 更新 `docs/beans/spring-core-beans/README.md` 索引与导航，确保 1~2 次跳转可达

- [√] 4.1 运行四模块测试回归：beans/aop/tx/webmvc
- [√] 4.2（可选）运行全仓回归：`mvn -q test`
- [-] 4.3（如存在）修复本次变更引入的失败（仅限本次相关）
  > Note: 本次相关模块测试均通过，无需修复。

## 5. 回填与同步
- [√] 5.1 回填三份历史 `task.md` 状态（[√]/[-]/[X]）
- [√] 5.2 同步更新知识库模块页与 `helloagents/CHANGELOG.md`
- [√] 5.3 迁移本方案包到 `helloagents/history/2026-01/` 并更新 `helloagents/history/index.md`
