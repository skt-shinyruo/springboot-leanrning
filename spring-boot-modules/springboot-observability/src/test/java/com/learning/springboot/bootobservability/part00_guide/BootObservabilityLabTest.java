package com.learning.springboot.bootobservability.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BootObservabilityLabTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired(required = false)
    private ObservationRegistry observationRegistry;

    @Test
    void httpRequestProducesHttpServerRequestsMetrics() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/ping", String.class);
        assertThat(resp.getBody()).isEqualTo("pong");

        Timer timer = meterRegistry.find("http.server.requests").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isGreaterThan(0);
    }

    @Test
    void observationRegistryIsAvailableInBoot() {
        assertThat(observationRegistry).isNotNull();
    }
}
