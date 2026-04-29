# 03. 业务链路调用链（MVC → Security → Tx → JPA → Events）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：业务链路调用链（MVC → Security → Tx → JPA → Events）展开，主线可以概括为：真实项目的“问题”往往发生在边界：校验边界/安全边界/事务边界/持久化上下文边界/事件时机边界。

    先跑 `BootBusinessCaseLabTest`，把端到端链路固化成断言，再用本章把“每个边界在哪一层生效”串起来。

    需要下探源码时，可以从 `DispatcherServlet#doDispatch` / `FilterChainProxy` / `TransactionInterceptor#invoke` / `SimpleJpaRepository` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Business Case](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. 断点地图（Business Case）](guide-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

优先运行 `BootBusinessCaseLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：真实项目的“问题”往往发生在边界：校验边界/安全边界/事务边界/持久化上下文边界/事件时机边界。需要下探源码时，可以从 `DispatcherServlet#doDispatch` / `FilterChainProxy` / `TransactionInterceptor#invoke` / `SimpleJpaRepository` 这些入口切入。


## 最短调用链（应能复述）

1. HTTP 请求进入 MVC（参数绑定/校验/异常处理）
2. 进入 Security FilterChain（认证/鉴权）
3. 进入事务边界（`@Transactional`）
4. 进入数据访问（Repository/EntityManager）
5. 发布/处理事件（事件时机与事务 after-commit）
6. 返回响应（并可观测信号收敛）

证据链入口：

- `BootBusinessCaseLabTest` / `BootBusinessCaseServiceLabTest`

## 小结与下一章

真实项目的“问题”往往发生在边界：校验边界/安全边界/事务边界/持久化上下文边界/事件时机边界。

下一章见：[02：断点地图](guide-breakpoint-map.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootBusinessCaseLabTest`
- Lab：`BootBusinessCaseServiceLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-breakpoint-map.md](guide-breakpoint-map.md)

<!-- BOOKIFY:END -->
