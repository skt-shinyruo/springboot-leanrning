package com.learning.springboot.springcoreresources.part00_guide;

import com.learning.springboot.springcoreresources.part01_resource_abstraction.ResourceReadingService;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringCoreResourcesExerciseTest {

    @Autowired
    private ResourceReadingService resourceReadingService;

    @Test
    @Disabled("练习：在 src/main/resources/data/ 下新增一个资源文件，并更新测试与 README")
    void exercise_addNewResourceFile() {
        String content;
        try {
            content = resourceReadingService.readClasspathText("classpath:data/todo.txt");
        } catch (RuntimeException ex) {
            throw new AssertionError("""
                    练习：在 `src/main/resources/data/` 下新增 `todo.txt`，并让 `ResourceReadingService` 能读到它。

                    下一步：
                    1) 新建文件：`spring-core-modules/spring-core-resources/src/main/resources/data/todo.txt`
                    2) 文件内容包含 `TODO`（便于本测试断言）
                    3) 重新运行：`mvn -pl spring-core-resources test`

                    建议阅读：
                    - `docs/resources/spring-core-resources/part-01-resource-abstraction/02-classpath-locations.md`
                    - `docs/resources/spring-core-resources/part-01-resource-abstraction/04-exists-and-handles.md`

                    常见坑：
                    - `getResource(...)` 返回的是 handle，不代表资源一定存在
                    - IDE 下能读到不代表打包后还能当成 File（见 jar vs filesystem）
                    """, ex);
        }

        assertThat(content)
                .as("""
                        练习：读取 `classpath:data/todo.txt` 并断言内容。

                        提示：
                        - 如果你看到异常，说明资源不存在或不可读；请按异常提示先补齐资源文件。
                        """)
                .contains("TODO");
    }

    @Test
    @Disabled("练习：实现一个方法返回 Resource metadata（filename/contentLength），并写测试验证")
    void exercise_resourceMetadata() {
        assertThat(true)
                .as("""
                        练习：实现一个方法返回 Resource metadata（filename/contentLength），并写测试验证。

                        下一步：
                        1) 在 service 中新增方法：读取 resource 后返回 filename/contentLength。
                        2) 写测试：断言 metadata 字段（不要只靠日志）。

                        建议阅读：
                        - `docs/resources/spring-core-resources/part-01-resource-abstraction/05-reading-and-encoding.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：复现 jar vs filesystem 的资源行为差异，并记录观察")
    void exercise_jarVsFilesystem() {
        assertThat(true)
                .as("""
                        练习：复现 jar vs filesystem 的资源行为差异，并记录观察。

                        下一步：
                        1) 在 IDE 运行与打包运行（jar）分别读取同一资源。
                        2) 对比：哪些 API 在 IDE 可用，但在 jar 下会失效？
                        3) 把结论写到 README 或笔记里。

                        建议阅读：
                        - `docs/resources/spring-core-resources/part-01-resource-abstraction/06-jar-vs-filesystem.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：证明 pattern 扫描结果排序稳定；必要时更新 service 实现")
    void exercise_sorting() {
        assertThat(true)
                .as("""
                        练习：证明 pattern 扫描结果排序稳定；必要时更新 service 实现。

                        下一步：
                        1) 写一个测试：多次调用 `listResourceLocations(...)`，断言返回顺序一致。
                        2) 如果不稳定，在 service 层对结果排序后再断言。

                        提示：
                        - 机制学习里，“不稳定顺序”非常容易让人误判
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：区分“资源不存在”与“资源不可读”的错误处理，并写测试覆盖")
    void exercise_errorHandling() {
        assertThat(true)
                .as("""
                        练习：区分“资源不存在”与“资源不可读”的错误处理，并写测试覆盖。

                        下一步：
                        1) 为 missing resource 与 unreadable resource 设计两种不同的错误路径（异常/返回值）。
                        2) 写测试分别触发两类场景，并断言你设计的行为。

                        建议阅读：
                        - `docs/resources/spring-core-resources/part-01-resource-abstraction/04-exists-and-handles.md`
                        """)
                .isFalse();
    }
}
