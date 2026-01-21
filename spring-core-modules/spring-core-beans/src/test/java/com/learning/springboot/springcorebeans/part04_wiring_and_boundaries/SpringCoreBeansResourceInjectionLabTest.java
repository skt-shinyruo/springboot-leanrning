package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;

class SpringCoreBeansResourceInjectionLabTest {

    @Test
    void withoutAnnotationConfigProcessors_resourceIsIgnored() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean("dependency", Dependency.class, () -> new Dependency("main"));
            context.registerBean("otherDependency", Dependency.class, () -> new Dependency("other"));
            context.registerBean(Target.class);
            context.refresh();

            Target target = context.getBean(Target.class);

            System.out.println("OBSERVE: Without annotation processors, @Resource is ignored");
            System.out.println("OBSERVE: @Resource needs CommonAnnotationBeanPostProcessor to perform injection");

            assertThat(target.dependency()).isNull();
            assertThat(target.explicitlyNamedDependency()).isNull();
        }
    }

    @Test
    void registerAnnotationConfigProcessors_enablesResourceAndResolvesByNameFirst() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(context);

            context.registerBean("dependency", Dependency.class, () -> new Dependency("main"));
            context.registerBean("otherDependency", Dependency.class, () -> new Dependency("other"));
            context.registerBean(Target.class);
            context.refresh();

            Target target = context.getBean(Target.class);

            System.out.println("OBSERVE: With annotation processors, @Resource injection runs during property population");
            System.out.println("OBSERVE: Default @Resource uses the field name as the bean name (name-first resolution)");
            System.out.println("OBSERVE: Name-first resolution stays deterministic even if multiple beans share the same type");

            assertThat(target.dependency().id()).isEqualTo("main");
            assertThat(target.explicitlyNamedDependency().id()).isEqualTo("other");
        }
    }

    record Dependency(String id) {
    }

    static class Target {

        @Resource
        private Dependency dependency;

        @Resource(name = "otherDependency")
        private Dependency explicitlyNamedDependency;

        Dependency dependency() {
            return dependency;
        }

        Dependency explicitlyNamedDependency() {
            return explicitlyNamedDependency;
        }
    }
}

