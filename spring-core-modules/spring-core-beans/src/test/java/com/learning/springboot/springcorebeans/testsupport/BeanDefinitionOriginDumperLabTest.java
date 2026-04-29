package com.learning.springboot.springcorebeans.testsupport;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

public class BeanDefinitionOriginDumperLabTest {

    @Test
    void dump_includesKeyBeanDefinitionFields_withoutRequiringBeanInstantiation() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            RootBeanDefinition dependency = new RootBeanDefinition(Dependency.class);
            dependency.setLazyInit(true);
            dependency.setResourceDescription("lab:dependency-definition");
            dependency.setSource("lab-source:dependency");
            context.registerBeanDefinition("dependency", dependency);

            RootBeanDefinition target = new RootBeanDefinition(Target.class);
            target.setLazyInit(true);
            target.setDependsOn("dependency");
            target.setDescription("lab:target-description");
            target.setInitMethodName("init");
            target.setDestroyMethodName("destroy");
            target.setResourceDescription("lab:target-definition");
            target.setSource("lab-source:target");

            AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(Qualifier.class);
            qualifier.setAttribute("value", "special");
            target.addQualifier(qualifier);

            context.registerBeanDefinition("target", target);
            context.refresh();

            String dump = BeanDefinitionOriginDumper.dump(context.getDefaultListableBeanFactory(), "target");
            assertThat(dump).contains("BEAN_DEFINITION_ORIGIN");
            assertThat(dump).contains("- beanName: target");
            assertThat(dump).contains("- lazyInit: true");
            assertThat(dump).contains("- dependsOn: [dependency]");
            assertThat(dump).contains("- description: lab:target-description");
            assertThat(dump).contains("- initMethodName: init");
            assertThat(dump).contains("- destroyMethodName: destroy");
            assertThat(dump).contains("org.springframework.beans.factory.annotation.Qualifier{value=special}");
        }
    }

    static class Dependency {
    }

    static class Target {
        void init() {
        }

        void destroy() {
        }
    }
}
