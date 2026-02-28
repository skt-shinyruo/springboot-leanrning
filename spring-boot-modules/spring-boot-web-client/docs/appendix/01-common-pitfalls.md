# 01. 常见坑清单（Web Client）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    这页收集 Web Client 里最容易“只在真实环境才暴露”的坑：状态码分流、timeout/cancel、retry 的副作用，以及 filter 链路的顺序直觉错误。它们往往不影响本地 200 的 demo，却会在第一次线上波动时让人陷入“到底该不该重试/为什么日志顺序反了”的困惑。

    建议先跑 `BootWebClientRestClientLabTest` 与 `BootWebClientWebClientLabTest`。如果想更快把分支跑全，可以先跑 Book/Branch Matrix，把 4xx/5xx/timeout/retry 的行为固化成断言，然后再回到本章对照原因与修法。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. 测试策略：为什么用 MockWebServer？](../part-01-web-client/05-testing-with-mockwebserver.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-web-client）](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先把“状态码/超时/重试”跑成可重复事实

WebClient/RestClient 的问题很多时候不是“请求发不出去”，而是“遇到非 200 时到底走了哪条分支”。这也是为什么本章的坑点大多围绕三类选择题展开：4xx/5xx 的语义分流、timeout/cancel 的时间相关边界、以及 retry 的副作用。

为了避免靠日志猜测，建议先把两组矩阵测试跑一遍，让分支变成可重复的断言：

- `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`
- `mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`

需要下探源码时，再对照本模块的断点地图与关键分支矩阵去命中入口（filter 链与 exchange 是高频落点）：[04-breakpoint-map.md](../part-00-guide/04-breakpoint-map.md) / [05-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md)。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

## 最小可运行实验（Lab）

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`
- 建议命令：`mvn -pl :spring-boot-web-client test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 这些坑很少在“能跑通”时暴露，往往在状态码分支、超时、重试与副作用上翻车。建议用 MockWebServer 把异常分支跑成断言。

## 只测 happy path

如果测试里只断言 200，那么第一次遇到 400/500 时才会临时决定“要不要重试/要不要降级/要不要转换成业务异常”。更稳妥的做法是先把最小分流写死：4xx 通常代表业务输入/权限等可预期问题（多数场景不应重试）；5xx 与网络错误才可能进入重试或告警，但是否重试仍取决于幂等性与副作用边界。

建议至少在测试里固化两条分支：

- 4xx：转换为业务异常或可解释错误（通常不重试）
- 5xx/网络错误：允许重试/告警（视场景）

## timeout/retry 不可测

connect timeout 往往受网络与系统环境影响，很容易写成 flaky；相比之下，read/response timeout 更适合用“延迟响应”来稳定复现。把 timeout 变成可测分支的关键是：让时间相关边界成为“由测试控制的输入”，而不是靠真实网络波动碰运气。

## 幂等性没想清楚

“重试”在客户端视角是一句很轻的配置；但在服务端视角，它等价于**重复发送同一个请求**。

如果幂等性边界没有想清楚，线上就会出现“偶发重复下单/重复扣款/重复写入”，而本地测试因为只跑了 happy path 的 200 断言，根本看不出来。只有当操作语义幂等（或具备幂等键/去重机制）时，retry 才是安全的；否则重试会把偶发网络问题放大成“重复副作用”。

因此这里更推荐先选语义，再谈参数：

- GET 通常更安全重试（但也要看服务端实现是否真的无副作用）
- POST/PUT/DELETE 往往有副作用：重试前先设计幂等键/去重策略（本模块 Exercise 有引导）

## Filter 顺序误判：request 顺序 ≠ response 顺序

按注册顺序写了多个 `ExchangeFilterFunction`，以为 request/response 都按同样顺序执行；结果 debug 时发现 response 相关逻辑“倒着来”。

`WebClient` 的 filter 本质上是对 `ExchangeFunction` 的一层层包裹：request 走外→内，response 信号回流时是内→外（因此看起来像 response 侧顺序反转）。

这个现象可以用 `BootWebClientWebClientFilterOrderLabTest#webClientFilters_requestOrderAndResponseOrder_areDifferent` 直接复现。在源码里，入口通常是 `DefaultWebClient$DefaultRequestBodyUriSpec#exchange`，filter 链的装配与执行则围绕 `ExchangeFilterFunction` 展开。

写 filter 时区分 request/response 侧的执行顺序，并把“期望顺序”直接写进 Lab/Test，避免靠脑补与靠经验。

## 对应 Lab（可运行）

- `BootWebClientRestClientLabTest`
- `BootWebClientWebClientLabTest`

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

上一章：[part-01-web-client/05-testing-with-mockwebserver.md](../part-01-web-client/05-testing-with-mockwebserver.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](02-self-check.md)

<!-- BOOKIFY:END -->
