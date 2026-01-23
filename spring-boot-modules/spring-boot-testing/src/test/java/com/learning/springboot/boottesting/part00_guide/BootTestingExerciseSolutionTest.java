package com.learning.springboot.boottesting.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.springboot.boottesting.BootTestingApplication;
import com.learning.springboot.boottesting.part01_testing.GreetingService;
import com.learning.springboot.boottesting.part01_testing.NonWebSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 参考实现：对齐 BootTestingExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
@SpringBootTest(
        classes = { BootTestingApplication.class, BootTestingExerciseSolutionTest.OverrideConfig.class },
        properties = "spring.main.banner-mode=off"
)
@AutoConfigureMockMvc
class BootTestingExerciseSolutionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @DynamicPropertySource
    static void dynamicProps(DynamicPropertyRegistry registry) {
        registry.add("exercise.dynamic", () -> "dyn");
    }

    @Test
    void solution_webMvcValidation_invalidRequestReturns400() throws Exception {
        mockMvc.perform(post("/api/echo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void solution_sliceVsFull_nonWebBeanExistsInSpringBootTest() {
        assertThat(applicationContext.getBeansOfType(NonWebSupport.class)).hasSize(1);
    }

    @Test
    void solution_testConfigurationOverride_overridesBeanDeterministically() throws Exception {
        mockMvc.perform(get("/api/greeting").param("name", "Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OVERRIDE:Alice"));
    }

    @Test
    void solution_dynamicPropertySource_valueIsVisibleInEnvironment() {
        assertThat(environment.getProperty("exercise.dynamic")).isEqualTo("dyn");
    }

    @TestConfiguration
    static class OverrideConfig {

        @Bean
        @Primary
        GreetingService overriddenGreetingService() {
            return new GreetingService() {
                @Override
                public String greet(String name) {
                    return "OVERRIDE:" + name;
                }
            };
        }
    }
}

