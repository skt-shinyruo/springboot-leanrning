package com.learning.springboot.springcoreresources.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.learning.springboot.springcoreresources.SpringCoreResourcesApplication;
import com.learning.springboot.springcoreresources.part01_resource_abstraction.ResourceReadingService;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * 参考实现：对齐 SpringCoreResourcesExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 *
 * <p>约束：
 * <ul>
 *   <li>保留现有 {@link ResourceReadingService#readClasspathText(String)} 的行为（Labs 依赖它的异常类型）。</li>
 *   <li>“错误处理区分”等练习内容，以 test 内的独立 helper 展示，避免破坏主线断言。</li>
 * </ul>
 */
@SpringBootTest(classes = { SpringCoreResourcesApplication.class, SpringCoreResourcesExerciseSolutionTest.SolutionConfig.class })
class SpringCoreResourcesExerciseSolutionTest {

    @Autowired
    private ResourceReadingService resourceReadingService;

    @Autowired
    private StrictResourceReader strictResourceReader;

    @Test
    void solution_addNewResourceFile_todoTxt_canBeReadFromClasspath() {
        String content = resourceReadingService.readClasspathText("classpath:data/todo.txt");
        assertThat(content).contains("TODO");
    }

    @Test
    void solution_resourceMetadata_exposesFilenameAndContentLength() {
        ResourceMetadata metadata = strictResourceReader.metadata("classpath:data/hello.txt");
        assertThat(metadata.filename()).isEqualTo("hello.txt");
        assertThat(metadata.contentLength()).isGreaterThan(0);
    }

    @Test
    void solution_sorting_isStableAcrossMultipleCalls() {
        List<String> first = resourceReadingService.listResourceLocations("classpath*:data/*.txt");
        List<String> second = resourceReadingService.listResourceLocations("classpath*:data/*.txt");
        List<String> third = resourceReadingService.listResourceLocations("classpath*:data/*.txt");

        assertThat(second).isEqualTo(first);
        assertThat(third).isEqualTo(first);
    }

    @Test
    void solution_errorHandling_distinguishesMissingVsUnreadable() throws Exception {
        assertThatThrownBy(() -> strictResourceReader.readTextStrict("classpath:data/missing.txt"))
                .isInstanceOf(MissingResourceException.class);

        Assumptions.assumeTrue(
                FileSystemCapabilities.supportsPosixFilePermissions(),
                "当前文件系统不支持 POSIX 权限，无法稳定复现“文件存在但不可读”的场景"
        );

        Path file = Files.createTempFile("spring-core-resources-unreadable-", ".txt");
        Files.writeString(file, "secret", StandardCharsets.UTF_8);
        Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("---------"));
        file.toFile().deleteOnExit();

        assertThatThrownBy(() -> strictResourceReader.readTextStrict(file.toUri().toString()))
                .isInstanceOf(UnreadableResourceException.class);
    }

    @TestConfiguration
    static class SolutionConfig {
        @Bean
        StrictResourceReader strictResourceReader(ResourcePatternResolver resolver) {
            return new StrictResourceReader(resolver);
        }
    }

    record ResourceMetadata(String filename, long contentLength) {
    }

    static class MissingResourceException extends RuntimeException {
        MissingResourceException(String location) {
            super("resource not found: " + location);
        }
    }

    static class UnreadableResourceException extends RuntimeException {
        UnreadableResourceException(String location) {
            super("resource not readable: " + location);
        }
    }

    static class StrictResourceReader {
        private final ResourcePatternResolver resolver;

        StrictResourceReader(ResourcePatternResolver resolver) {
            this.resolver = resolver;
        }

        ResourceMetadata metadata(String location) {
            Resource resource = resolver.getResource(location);
            if (!resource.exists()) {
                throw new MissingResourceException(location);
            }

            try {
                return new ResourceMetadata(resource.getFilename(), resource.contentLength());
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        String readTextStrict(String location) {
            Resource resource = resolver.getResource(location);

            if (!resource.exists()) {
                throw new MissingResourceException(location);
            }
            if (!resource.isReadable()) {
                throw new UnreadableResourceException(location);
            }

            try (var in = resource.getInputStream()) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    static class FileSystemCapabilities {
        static boolean supportsPosixFilePermissions() {
            return FileSystems.defaultSupportsPosixPermissions();
        }
    }

    static class FileSystems {
        static boolean defaultSupportsPosixPermissions() {
            try {
                return java.nio.file.FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
            } catch (Exception ignored) {
                return false;
            }
        }
    }
}

