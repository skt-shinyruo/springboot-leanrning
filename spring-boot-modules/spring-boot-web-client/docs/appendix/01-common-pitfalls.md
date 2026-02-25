# 01. 常见坑清单（Web Client）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：90：常见坑清单（Web Client）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：用 `RestClient/WebClient` 发起对外 HTTP 调用；用 filter 链统一日志/鉴权/重试/超时；用 mock server 测试把外部依赖固定下来。
    - 原理：构建请求 → exchange/过滤器链 → 处理状态码与异常 → 超时/取消/重试策略 → 测试验证保证可重复。
    - 源码入口：`org.springframework.web.reactive.function.client.WebClient` / `org.springframework.web.reactive.function.client.ExchangeFilterFunction` / `org.springframework.web.reactive.function.client.ExchangeFunction`
    - 推荐 Lab：`BootWebClientRestClientLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[05. 测试策略：为什么用 MockWebServer？](../part-01-web-client/05-testing-with-mockwebserver.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-web-client）](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障模板（统一结构）

当你遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，建议按下面 6 步收敛（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：你看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[04-breakpoint-map.md](../part-00-guide/04-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[05-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[02-self-check.md](02-self-check.md)

- 本章主题：**01. 常见坑清单（Web Client）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

## 机制主线

这页不展开完整机制主线；其定位更接近排障备忘录：把常见分支与可复现入口列出来，便于回到 tests 验证。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`
- 建议命令：`mvn -pl :spring-boot-web-client test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 这些坑很少在“能跑通”时暴露，往往在状态码分支、超时、重试与副作用上翻车。建议用 MockWebServer 把异常分支跑成断言。

## 只测 happy path

- 只测 200 会让你在线上第一次遇到 400/500 才知道怎么处理。
- 建议至少固化：
  - 4xx → 业务异常（通常不重试）
  - 5xx/网络错误 → 可重试/告警（视场景）

## timeout/retry 不可测

- connect timeout 很容易 flaky（受网络/系统影响）
- 建议优先用“延迟响应”复现 read/response timeout

## 幂等性没想清楚

“重试”在客户端视角是一句很轻的配置；但在服务端视角，它等价于**重复发送同一个请求**。

- 你会看到：线上偶发重复下单/重复扣款/重复写入；而你在本地只做了 happy-path 的 200 断言，完全看不出来。
- Root Cause：只有当操作语义幂等（或你有幂等键/去重机制）时，retry 才安全；否则重试会把偶发网络问题放大成“重复副作用”。
- Fix（先选语义，再谈参数）：
  - GET 通常更安全重试（但也要看服务端实现是否真的无副作用）
  - POST/PUT/DELETE 往往有副作用：重试前先设计幂等键/去重策略（本模块 Exercise 有引导）

## Filter 顺序误判：request 顺序 ≠ response 顺序

- Symptom：你按注册顺序写了多个 `ExchangeFilterFunction`，以为 request/response 都按同样顺序执行；结果 debug 时发现 response 相关逻辑“倒着来”。
- Root Cause：`WebClient` 的 filter 本质上是对 `ExchangeFunction` 的一层层包裹：request 走外→内，response 信号回流时是内→外（表现为 response 侧顺序反转）。
- Verification：`BootWebClientWebClientFilterOrderLabTest#webClientFilters_requestOrderAndResponseOrder_areDifferent`
- Breakpoints：`DefaultWebClient$DefaultRequestBodyUriSpec#exchange`、`ExchangeFilterFunction` 链路的装配与调用
- Fix：写 filter 时区分 request/response 侧的执行顺序；把“期望顺序”直接写进 Lab/Test，避免靠脑补。

## 对应 Lab（可运行）

- `BootWebClientRestClientLabTest`
- `BootWebClientWebClientLabTest`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

上一章：[part-01-web-client/05-testing-with-mockwebserver.md](../part-01-web-client/05-testing-with-mockwebserver.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](02-self-check.md)

<!-- BOOKIFY:END -->
