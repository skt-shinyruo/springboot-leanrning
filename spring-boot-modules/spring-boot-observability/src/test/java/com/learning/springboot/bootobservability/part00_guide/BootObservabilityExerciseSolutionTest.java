package com.learning.springboot.bootobservability.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootobservability.BootObservabilityApplication;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.MeterFilter;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;

/**
 * 参考实现：对齐 BootObservabilityExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
@SpringBootTest(
        classes = { BootObservabilityApplication.class, BootObservabilityExerciseSolutionTest.SolutionConfig.class },
        webEnvironment = WebEnvironment.RANDOM_PORT)
class BootObservabilityExerciseSolutionTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    void solution_addCustomTagAndVerifyItAppearsInMeters() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/ping", String.class);
        assertThat(resp.getBody()).isEqualTo("pong");

        Collection<Timer> timers = meterRegistry.find("http.server.requests")
                .tag("feature", "ping")
                .timers();

        assertThat(timers).isNotEmpty();
        assertThat(timers).allSatisfy(timer -> assertThat(timer.count()).isGreaterThan(0));
    }

    @TestConfiguration
    static class SolutionConfig {

        @Bean
        MeterRegistryCustomizer<MeterRegistry> solutionCommonTagCustomizer() {
            return registry -> registry.config()
                    .meterFilter(MeterFilter.commonTags(List.of(Tag.of("feature", "ping"))));
        }
    }
}
