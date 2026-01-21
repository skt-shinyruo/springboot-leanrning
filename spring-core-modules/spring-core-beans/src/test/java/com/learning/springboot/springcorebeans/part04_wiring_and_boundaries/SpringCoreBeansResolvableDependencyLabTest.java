package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringCoreBeansResolvableDependencyLabTest {

    @Test
    void registerResolvableDependency_enablesAutowiringWithoutRegisteringABean() {
        NotABeanDependency dependency = new NotABeanDependency("from-resolvable-dependency");

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getBeanFactory().registerResolvableDependency(NotABeanDependency.class, dependency);

            context.registerBean(NeedsDependency.class);
            context.refresh();

            NeedsDependency bean = context.getBean(NeedsDependency.class);

            System.out.println("OBSERVE: resolvable dependency participates in autowiring but is not a bean");
            assertThat(bean.dependency()).isSameAs(dependency);
            assertThatThrownBy(() -> context.getBean(NotABeanDependency.class))
                    .isInstanceOf(NoSuchBeanDefinitionException.class);
        }
    }

    record NotABeanDependency(String origin) {
    }

    static class NeedsDependency {
        private final NotABeanDependency dependency;

        NeedsDependency(NotABeanDependency dependency) {
            this.dependency = dependency;
        }

        NotABeanDependency dependency() {
            return dependency;
        }
    }
}
