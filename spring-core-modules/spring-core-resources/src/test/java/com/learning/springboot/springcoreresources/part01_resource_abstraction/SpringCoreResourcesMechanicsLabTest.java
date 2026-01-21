package com.learning.springboot.springcoreresources.part01_resource_abstraction;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

class SpringCoreResourcesMechanicsLabTest {

    @Test
    void getResourceReturnsAHandle_evenIfTheResourceDoesNotExist() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("classpath:data/does-not-exist.txt");
        assertThat(resource.exists()).isFalse();
    }

    @Test
    void classpathResourceCanBeReadAsBytes() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("classpath:data/hello.txt");

        try (var in = resource.getInputStream()) {
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(text).contains("Hello from classpath");
        }
    }

    @Test
    void classpathStarPatternLoadsResourcesFromClasspath() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:data/*.txt");

        assertThat(resources).hasSizeGreaterThanOrEqualTo(2);
        assertThat(resources).anySatisfy(r -> assertThat(r.getFilename()).isEqualTo("hello.txt"));
        assertThat(resources).anySatisfy(r -> assertThat(r.getFilename()).isEqualTo("info.txt"));
    }

    @Test
    void resourceDescriptionsHelpWithDebugging() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("classpath:data/hello.txt");

        assertThat(resource.getDescription()).contains("hello.txt");
    }
}
