# springboot-web-client

本模块用于学习 Web Client（HTTP Client）相关主题：RestClient vs WebClient、错误处理、超时、重试、拦截器/Filter、以及可测试性。

本模块以 tests-first 为主（跑 `*LabTest`），不启动 Web 服务（`spring.main.web-application-type=none`）。

## 关键命令

```bash
mvn -pl :springboot-web-client test
```

## 推荐 docs 阅读顺序

（docs 目录页：[`docs/README.md`](../../docs/web-client/springboot-web-client/README.md)）

1. [RestClient 最小闭环](../../docs/web-client/springboot-web-client/part-01-web-client/01-restclient-basics.md)
2. [WebClient 最小闭环](../../docs/web-client/springboot-web-client/part-01-web-client/02-webclient-basics.md)
3. [错误处理：4xx/5xx → 异常](../../docs/web-client/springboot-web-client/part-01-web-client/03-error-handling.md)
4. [超时与重试](../../docs/web-client/springboot-web-client/part-01-web-client/04-timeout-and-retry.md)
5. [测试策略：MockWebServer](../../docs/web-client/springboot-web-client/part-01-web-client/05-testing-with-mockwebserver.md)
6. [常见坑清单](../../docs/web-client/springboot-web-client/appendix/90-common-pitfalls.md)

## Labs / Exercises 索引

> 说明：⭐=入门，⭐⭐=进阶，⭐⭐⭐=挑战。Exercises 默认 `@Disabled`。

| 类型 | 入口 | 知识点 | 难度 | 下一步 |
| --- | --- | --- | --- | --- |
| Lab | `src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientRestClientLabTest.java` | RestClient：请求/headers/JSON/错误处理/超时/重试 | ⭐⭐ | 对照 MockWebServer 的 request 断言 |
| Lab | `src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientWebClientLabTest.java` | WebClient：Mono/错误处理/超时/重试/StepVerifier | ⭐⭐ | 对比 reactive vs blocking 的测试体验 |
| Exercise | `src/test/java/com/learning/springboot/bootwebclient/part00_guide/BootWebClientExerciseTest.java` | 扩展错误处理/重试策略/幂等性说明等 | ⭐⭐–⭐⭐⭐ | 从错误体解析开始 |
