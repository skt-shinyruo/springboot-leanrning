# 02. 表单提交闭环（@ModelAttribute / BindingResult / 校验回显 / PRG）

## 导读

- 本章主题：**02. 表单提交闭环（@ModelAttribute / BindingResult / 校验回显 / PRG）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcViewLabTest`
    - Test file：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewLabTest.java`

## 机制主线

本章补齐传统 MVC 最常见的交互：**表单提交**。

- GET 展示表单（form backing object）
- POST 提交表单（绑定 + 校验）
- 校验失败：回到原页面并回显错误与用户输入
- 校验成功：redirect（PRG）到详情页，并使用 flash attribute 提示一次性消息

## 应当观察到的现象（What to observe）

1) GET `/pages/users/new` 返回表单页
- HTML 中存在表单
- model 中存在 `form`

2) POST `/pages/users`（非法输入）不会 redirect
- 仍然返回 `pages/user-form`
- 页面中出现字段错误信息
- 已输入的值会回显（避免用户重填）

3) POST `/pages/users`（合法输入）走 PRG
- 响应是 3xx redirect
- Location 指向 `/pages/users/{id}`
- redirect 后页面可展示用户信息与 flash message

## 机制解释（Why）

### 1) 绑定对象：@ModelAttribute

传统表单通常是 `application/x-www-form-urlencoded`，Spring MVC 会把表单字段绑定到 `@ModelAttribute` 对象上。

### 2) 校验：@Valid + BindingResult

对 `@ModelAttribute` 使用 `@Valid` 后，校验结果会写入 `BindingResult`。

关键点：`BindingResult` 必须紧跟在被校验的参数之后，否则校验失败会变成异常流程（更难以做“回显”）。

### 3) PRG：Post-Redirect-Get

成功后 redirect 的原因：
- 避免浏览器刷新导致重复提交（重复创建）
- URL 更“资源化”，更适合分享与回退

### 4) Flash Attributes：一次性消息

`RedirectAttributes#addFlashAttribute` 用于跨 redirect 传递一次性消息（提示“创建成功”等），在下一次请求中可读到并展示。

## 在本模块里去哪里看

- Controller：`spring-boot-modules/spring-boot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part02_view_mvc/MvcUserController.java`
- 表单 DTO：`spring-boot-modules/spring-boot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part02_view_mvc/MvcCreateUserForm.java`
- 表单模板：`spring-boot-modules/spring-boot-web-mvc/src/main/resources/templates/pages/user-form.html`
- 详情模板：`spring-boot-modules/spring-boot-web-mvc/src/main/resources/templates/pages/user-detail.html`

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootWebMvcViewLabTest`
- 建议命令：`mvn -pl :spring-boot-web-mvc test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

目标是把一个完整闭环跑通并可测试验证：

## 实验入口（先跑再看）

- MockMvc（建议先看这个）：
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewLabTest.java`
    - `rendersUserFormPage`
    - `reRendersFormWhenPostIsInvalid`
    - `redirectsWhenPostIsValid`

## 常见坑与边界

### 1) BindingResult 位置不对，导致“回显链路”断掉

关键规则：`BindingResult` 必须紧跟在被校验的 `@ModelAttribute` 参数之后。

否则校验失败会直接走异常流程（而不是回到页面渲染），你会观察到：
- 页面没有错误回显
- 甚至直接变成 400/错误页

对照证据：`BootWebMvcViewLabTest#reRendersFormWhenPostIsInvalid`

### 2) 校验失败后 redirect（错误做法）

校验失败通常不应该 redirect，否则：
- 错误信息会丢失（除非你做额外的 flash 传递）
- 用户输入也无法自然回显（体验差）

正确做法：失败时返回同一个 form view，保留 model + errors。

对照证据：`BootWebMvcViewLabTest#reRendersFormWhenPostIsInvalid`

### 3) PRG 的“Get”阶段忘了消费 flash attribute

成功后 redirect（PRG）依赖 flash attribute 传递一次性消息。
如果你在 GET 详情页没有读取/渲染该消息，就会出现：
- 创建成功但用户没看到提示
- 或提示信息永远显示/永远不显示

对照证据：`BootWebMvcViewLabTest#redirectsWhenPostIsValid`

### 4) 只用手工点浏览器，不写测试

表单闭环一旦不写测试，很容易在重构 controller/template 时悄悄断掉（尤其是错误回显与 redirect）。

建议：以 MockMvc 断言“viewName + model attribute + redirect location”为主，把 UI 行为固化。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcViewLabTest`
- Test file：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewLabTest.java`

上一章：[part-02-view-mvc/01-thymeleaf-and-view-resolver.md](01-thymeleaf-and-view-resolver.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-02-view-mvc/03-error-pages-and-content-negotiation.md](03-error-pages-and-content-negotiation.md)

<!-- BOOKIFY:END -->
