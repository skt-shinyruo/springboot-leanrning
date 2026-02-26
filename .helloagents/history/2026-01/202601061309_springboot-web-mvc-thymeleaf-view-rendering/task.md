# Task List: springboot-web-mvc 传统 MVC 页面渲染（Thymeleaf/ModelAndView）与错误页/内容协商

Directory: `helloagents/history/2026-01/202601061309_springboot-web-mvc-thymeleaf-view-rendering/`

---

## 1. springboot-web-mvc（依赖与基础配置）
- [√] 1.1 新增 Thymeleaf 依赖到 `springboot-web-mvc/pom.xml`，验证 why.md#r1-r1-s1
- [√] 1.2 增加学习向配置（模板缓存/错误页展示等）到 `springboot-web-mvc/src/main/resources/application.properties`，验证 why.md#r3-r3-s1

## 2. 传统 MVC 页面：Controller + Templates + 静态资源
- [√] 2.1 新增页面 Ping 示例（`@Controller` + viewName）到 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part02_view_mvc/MvcPingController.java`，并新增模板 `springboot-web-mvc/src/main/resources/templates/pages/ping.html`，验证 why.md#r1-s1
- [√] 2.2 新增表单 DTO（`@ModelAttribute` + `@Valid`）到 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part02_view_mvc/MvcCreateUserForm.java`，并新增表单模板 `springboot-web-mvc/src/main/resources/templates/pages/user-form.html`，验证 why.md#r2-s1
- [√] 2.3 新增传统 MVC 用户流程 Controller（GET 展示/POST 提交/redirect）到 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part02_view_mvc/MvcUserController.java`，并新增详情页模板 `springboot-web-mvc/src/main/resources/templates/pages/user-detail.html`，验证 why.md#r2-s2
- [√] 2.4 新增最小静态资源（CSS）到 `springboot-web-mvc/src/main/resources/static/css/app.css`，并在模板中引用，验证 why.md#r1-s1

## 3. 错误页与内容协商（HTML vs JSON）
- [√] 3.1 新增页面异常处理（含 Accept 内容协商兜底）到 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part02_view_mvc/MvcExceptionHandler.java`，验证 why.md#r3-s2
- [√] 3.2 新增错误页模板 `springboot-web-mvc/src/main/resources/templates/error/404.html`、`springboot-web-mvc/src/main/resources/templates/error/4xx.html`、`springboot-web-mvc/src/main/resources/templates/error/5xx.html`，验证 why.md#r3-s1
- [√] 3.3 补齐 API 侧常见 400（malformed JSON/类型转换）统一错误体到 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part01_web_mvc/GlobalExceptionHandler.java`，验证 why.md#r3-s2

## 4. Docs 更新（学习路径与机制解释）
- [√] 4.1 增加传统 MVC 主线文档到 `docs/web-mvc/springboot-web-mvc/part-02-view-mvc/`（Thymeleaf/ModelAndView/表单校验与回显/错误页与内容协商），并更新索引 `docs/web-mvc/springboot-web-mvc/README.md`
- [√] 4.2 更新模块总览与命令说明：`springboot-web-mvc/README.md`（新增页面路由、阅读顺序、Labs/Exercises 入口）

## 5. Testing（MockMvc + 端到端）
- [√] 5.1 新增页面渲染 MockMvc 测试到 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewLabTest.java`：验证 `/pages/ping`、表单 GET/POST、错误回显，覆盖 why.md#r1-s1、why.md#r2-s1
- [√] 5.2 新增错误页/内容协商测试到 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcErrorViewLabTest.java`：验证 404 HTML 错误页与 Accept=JSON 分支，覆盖 why.md#r3-s1、why.md#r3-s2
- [√] 5.3 新增端到端集成测试到 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewSpringBootLabTest.java`：真实端口获取 HTML 并断言关键 marker，覆盖 why.md#r1-s1、why.md#r3-s1

## 6. Security Check
- [√] 6.1 执行安全检查（输入校验、错误信息泄露、模板转义、敏感信息处理），记录关键结论到 `helloagents/CHANGELOG.md`

## 7. Knowledge Base 同步与收尾
- [√] 7.1 同步模块知识库：更新 `helloagents/wiki/modules/springboot-web-mvc.md`（新增传统 MVC 学习入口、测试入口、更新时间）
- [√] 7.2 更新变更日志：`helloagents/CHANGELOG.md`
- [√] 7.3 【Mandatory】执行完成后将解决方案包迁移到 `helloagents/history/2026-01/` 并更新 `helloagents/history/index.md`（按 G11 规则）
