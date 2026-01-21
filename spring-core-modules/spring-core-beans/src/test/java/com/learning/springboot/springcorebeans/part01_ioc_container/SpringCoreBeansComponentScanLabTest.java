package com.learning.springboot.springcorebeans.part01_ioc_container;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcorebeans.part01_ioc_container.componentscan.ExcludedComponent;
import com.learning.springboot.springcorebeans.part01_ioc_container.componentscan.NamedScanComponent;
import com.learning.springboot.springcorebeans.part01_ioc_container.componentscan.ScanComponent;
import com.learning.springboot.springcorebeans.part01_ioc_container.componentscan.ScanService;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

class SpringCoreBeansComponentScanLabTest {

    @Test
    void componentScan_registersStereotypes_andRespectsExplicitBeanName() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DefaultScanConfig.class)) {
            assertThat(context.containsBean("scanComponent")).isTrue();
            assertThat(context.getBean(ScanComponent.class)).isNotNull();

            assertThat(context.containsBean("scanService")).isTrue();
            assertThat(context.getBean(ScanService.class)).isNotNull();

            assertThat(context.containsBean("namedScanComponent")).isTrue();
            assertThat(context.getBean(NamedScanComponent.class)).isNotNull();
        }
    }

    @Test
    void componentScan_excludeFilters_canPreventRegistrationEvenIfAnnotated() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ExcludingScanConfig.class)) {
            assertThat(context.containsBean("excludedComponent")).isFalse();
            assertThat(context.getBeansOfType(ExcludedComponent.class)).isEmpty();
        }
    }

    @Configuration
    @ComponentScan(basePackages = "com.learning.springboot.springcorebeans.part01_ioc_container.componentscan")
    static class DefaultScanConfig {
    }

    @Configuration
    @ComponentScan(
            basePackages = "com.learning.springboot.springcorebeans.part01_ioc_container.componentscan",
            excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ExcludedComponent.class)
    )
    static class ExcludingScanConfig {
    }
}

