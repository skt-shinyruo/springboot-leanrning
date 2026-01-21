package com.learning.springboot.springcoreresources.part01_resource_abstraction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringCoreResourcesLabTest {

    @Autowired
    private ResourceReadingService resourceReadingService;

    @Test
    void readsClasspathResourceContent() {
        String content = resourceReadingService.readClasspathText("classpath:data/hello.txt");
        assertThat(content).contains("Hello from classpath");
    }

    @Test
    void loadsMultipleResourcesWithPattern() {
        List<String> descriptions = resourceReadingService.listResourceLocations("classpath*:data/*.txt");
        assertThat(descriptions).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void supportsLeadingSlashInClasspathLocation() {
        String content = resourceReadingService.readClasspathText("classpath:/data/hello.txt");
        assertThat(content).contains("Hello from classpath");
    }

    @Test
    void missingResourceCausesUncheckedIOException() {
        assertThatThrownBy(() -> resourceReadingService.readClasspathText("classpath:data/missing.txt"))
                .isInstanceOf(UncheckedIOException.class);
    }

    @Test
    void patternResultsContainExpectedFilenames() {
        List<String> descriptions = resourceReadingService.listResourceLocations("classpath*:data/*.txt");
        assertThat(descriptions).anyMatch(d -> d.contains("hello.txt"));
        assertThat(descriptions).anyMatch(d -> d.contains("info.txt"));
    }

    @Test
    void fileResourcesCanAlsoBeRead_viaResourceAbstraction() throws Exception {
        Path file = Files.createTempFile("spring-core-resources-", ".txt");
        Files.writeString(file, "Hello from file system");
        file.toFile().deleteOnExit();

        String content = resourceReadingService.readClasspathText(file.toUri().toString());
        assertThat(content).contains("Hello from file system");
    }
}

