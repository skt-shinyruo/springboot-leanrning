# 03. Testing 调用链（Test Bootstrap → Slice → Context Cache）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Testing 调用链（Test Bootstrap → Slice → Context Cache）
    - 怎么使用：先跑 `GreetingControllerWebMvcLabTest` 与 `GreetingControllerSpringBootLabTest`，把“slice vs full context”差异固化成断言，再按本文理解不同 bootstrapper 如何构建上下文。
    - 原理：测试不是“跑起来就行”，关键在于：到底启动了多少上下文？哪些 auto-config 被带进来？context cache 如何影响速度与隔离？
    - 源码入口：`SpringBootTestContextBootstrapper` / `WebMvcTestContextBootstrapper` / `TestContextManager`
    - 推荐 Lab：`GreetingControllerWebMvcLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 00 - Deep Dive Guide（springboot-testing）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 断点地图（Testing Debugger Pack）](04-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. Testing 调用链（Test Bootstrap → Slice → Context Cache）**
- 建议入口：优先运行 `GreetingControllerWebMvcLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：测试不是“跑起来就行”，关键在于：到底启动了多少上下文？哪些 auto-config 被带进来？context cache 如何影响速度与隔离？
- 源码入口：`SpringBootTestContextBootstrapper` / `WebMvcTestContextBootstrapper` / `TestContextManager`



## 最短调用链

1. Test framework 发现测试类与注解（`@SpringBootTest/@WebMvcTest`）
2. 选择 bootstrapper 并构建 ApplicationContext
3. 应用 slice 规则（只加载需要的 bean/auto-config）
4. 运行测试并使用 context cache（提升速度但影响隔离）

证据链入口：

- `GreetingControllerWebMvcLabTest` / `GreetingControllerSpringBootLabTest`

## 小结与下一章

- 小结：测试不是“跑起来就行”，关键在于：到底启动了多少上下文？哪些 auto-config 被带进来？context cache 如何影响速度与隔离？
- 下一章：[第 184 章：02：断点地图](04-breakpoint-map.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`GreetingControllerWebMvcLabTest`
- Lab：`GreetingControllerSpringBootLabTest`
- Lab：`BootTestingMockBeanLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md)

<!-- BOOKIFY:END -->
