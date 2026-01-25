# 第 190 章：01：业务链路调用链（MVC → Security → Tx → JPA → Events）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：业务链路调用链（MVC → Security → Tx → JPA → Events）
    - 怎么使用：先跑 `BootBusinessCaseLabTest`，把端到端链路固化成断言，再用本文把“每个边界在哪一层生效”串起来。
    - 原理：真实项目的“问题”往往发生在边界：校验边界/安全边界/事务边界/持久化上下文边界/事件时机边界。
    - 源码入口：`DispatcherServlet#doDispatch` / `FilterChainProxy` / `TransactionInterceptor#invoke` / `SimpleJpaRepository`
    - 推荐 Lab：`BootBusinessCaseLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 190 章：00. 深挖导读](190-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 190 章：02：断点地图](190-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 最短调用链（你要能复述）

1. HTTP 请求进入 MVC（参数绑定/校验/异常处理）
2. 进入 Security FilterChain（认证/鉴权）
3. 进入事务边界（`@Transactional`）
4. 进入数据访问（Repository/EntityManager）
5. 发布/处理事件（事件时机与事务 after-commit）
6. 返回响应（并可观测信号收敛）

证据链入口：

- `BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBusinessCaseLabTest`
- Lab：`BootBusinessCaseServiceLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](190-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](190-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
