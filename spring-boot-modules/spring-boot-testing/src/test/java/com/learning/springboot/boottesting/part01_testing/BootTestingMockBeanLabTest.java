package com.learning.springboot.boottesting.part01_testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BootTestingMockBeanLabTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private GreetingService greetingService;

    @Test
    @SuppressWarnings("unchecked")
    void mockBeanOverridesRealBeanInFullContext() {
        when(greetingService.greet("Alice")).thenReturn("Mocked:Alice");

        Map<String, Object> response = restTemplate.getForObject("/api/greeting?name=Alice", Map.class);
        assertThat(response.get("message")).isEqualTo("Mocked:Alice");
    }

    @Test
    @SuppressWarnings("unchecked")
    void mockBeanAlsoAffectsDefaultParamFlow() {
        when(greetingService.greet("World")).thenReturn("Mocked:World");

        Map<String, Object> response = restTemplate.getForObject("/api/greeting", Map.class);
        assertThat(response.get("message")).isEqualTo("Mocked:World");
    }
}

