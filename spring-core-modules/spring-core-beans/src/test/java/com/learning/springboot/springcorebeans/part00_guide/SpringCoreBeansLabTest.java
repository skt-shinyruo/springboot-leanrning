package com.learning.springboot.springcorebeans.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.learning.springboot.springcorebeans.part01_ioc_container.DirectPrototypeConsumer;
import com.learning.springboot.springcorebeans.part01_ioc_container.FormattingService;
import com.learning.springboot.springcorebeans.part01_ioc_container.LifecycleLogger;
import com.learning.springboot.springcorebeans.part01_ioc_container.ProviderPrototypeConsumer;
import com.learning.springboot.springcorebeans.part01_ioc_container.TextFormatter;

@SpringBootTest
class SpringCoreBeansLabTest {

    @Autowired
    private FormattingService formattingService;

    @Autowired
    private DirectPrototypeConsumer directPrototypeConsumer;

    @Autowired
    private ProviderPrototypeConsumer providerPrototypeConsumer;

    @Autowired
    private LifecycleLogger lifecycleLogger;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void usesQualifierToResolveMultipleBeans() {
        assertThat(formattingService.format("Hello")).isEqualTo("HELLO");
    }

    @Test
    void demonstratesPrototypeScopeBehavior() {
        UUID direct1 = directPrototypeConsumer.currentId();
        UUID direct2 = directPrototypeConsumer.currentId();
        assertThat(direct1).isEqualTo(direct2);

        UUID provider1 = providerPrototypeConsumer.newId();
        UUID provider2 = providerPrototypeConsumer.newId();
        assertThat(provider1).isNotEqualTo(provider2);
    }

    @Test
    void postConstructRunsDuringContextInitialization() {
        assertThat(lifecycleLogger.isInitialized()).isTrue();
    }

    @Test
    void containerCanProvideAllFormatterBeansByType() {
        assertThat(applicationContext.getBeansOfType(TextFormatter.class))
                .containsKeys("upperFormatter", "lowerFormatter");
    }

    @Test
    void missingBeanLookupsFailFast() {
        assertThatThrownBy(() -> applicationContext.getBean("doesNotExist"))
                .isInstanceOf(NoSuchBeanDefinitionException.class);
    }
}
