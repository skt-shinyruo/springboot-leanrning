# 03. Testing 调用链（Test Bootstrap → Slice → Context Cache）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕01：Testing 调用链（Test Bootstrap → Slice → Context Cache）展开，主线可以概括为：测试不是“跑起来就行”，关键在于：到底启动了多少上下文？哪些 auto-config 被带进来？context cache 如何影响速度与隔离？

    先跑 `GreetingControllerWebMvcLabTest` 与 `GreetingControllerSpringBootLabTest`，把“slice vs full context”差异固化成断言，再按本章理解不同 bootstrapper 如何构建上下文。

    需要下探源码时，可以从 `SpringBootTestContextBootstrapper` / `WebMvcTestContextBootstrapper` / `TestContextManager` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Testing](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[04. 断点地图（Testing）](guide-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `GreetingControllerWebMvcLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：测试不是“跑起来就行”，关键在于：到底启动了多少上下文？哪些 auto-config 被带进来？context cache 如何影响速度与隔离？需要下探源码时，可以从 `SpringBootTestContextBootstrapper` / `WebMvcTestContextBootstrapper` / `TestContextManager` 这些入口切入。


## 最短调用链

1. Test framework 发现测试类与注解（`@SpringBootTest/@WebMvcTest`）
2. 选择 bootstrapper 并构建 ApplicationContext
3. 应用 slice 规则（只加载需要的 bean/auto-config）
4. 运行测试并使用 context cache（提升速度但影响隔离）

证据链入口：

- `GreetingControllerWebMvcLabTest` / `GreetingControllerSpringBootLabTest`

## 小结与下一章

测试不是“跑起来就行”，关键在于：到底启动了多少上下文？哪些 auto-config 被带进来？context cache 如何影响速度与隔离？

下一章见：[02：断点地图](guide-breakpoint-map.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`GreetingControllerWebMvcLabTest`
- Lab：`GreetingControllerSpringBootLabTest`
- Lab：`BootTestingMockBeanLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-breakpoint-map.md](guide-breakpoint-map.md)

<!-- BOOKIFY:END -->
