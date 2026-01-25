# （Redirect）Business Case 收束（旧入口）

<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：（Redirect）Business Case 收束（旧入口）
    - 怎么使用：用端到端链路把 Web/Validation/Security/AOP/Tx/JPA/Events 串起来：遇到红测/异常时，先定位“哪个边界没生效”，再回到对应模块主线。
    - 原理：一次业务请求贯穿：MVC 入参→安全边界→事务边界→持久化上下文→事件时机→可观测信号；排障的关键是把问题归类到具体边界。
    - 源码入口：`org.springframework.web.servlet.DispatcherServlet#doDispatch` / `org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.data.jpa.repository.support.SimpleJpaRepository`
    - 推荐 Lab：`BootBusinessCaseLabTest`
<!-- CHAPTER-CARD:END -->

## 已迁移
本页为旧入口兼容页，正文已迁移到：[新位置](188-business-case.md)。

## 返回
- [模块目录](../README.md)
- [全书目录](/book/)
