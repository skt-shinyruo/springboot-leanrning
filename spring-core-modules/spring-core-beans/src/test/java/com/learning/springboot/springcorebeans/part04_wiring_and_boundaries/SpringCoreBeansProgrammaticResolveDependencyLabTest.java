package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 最小对照：把 {@code DefaultListableBeanFactory#resolveDependency} 当成“调试 API”来使用。
 *
 * <p>目标：当你想证明“为什么注入的是它”时，不必一定依赖 {@code @Autowired} 的实际注入过程，
 * 而是可以直接构造 {@link DependencyDescriptor} 并调用 {@code resolveDependency(...)} 来观察候选收敛结果。</p>
 */
class SpringCoreBeansProgrammaticResolveDependencyLabTest {

    @Test
    void resolveDependency_byNameFallback_canResolveAmbiguity_whenDependencyNameMatchesBeanName() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean("primaryWorker", PrimaryWorker.class);
            context.registerBean("secondaryWorker", SecondaryWorker.class);
            context.refresh();

            Field field = ByNameFallbackInjectionPoint.class.getDeclaredField("secondaryWorker");
            DependencyDescriptor descriptor = new DependencyDescriptor(field, true);

            Set<String> autowiredBeanNames = new LinkedHashSet<>();
            Worker resolved = (Worker) context.getDefaultListableBeanFactory()
                    .resolveDependency(descriptor, null, autowiredBeanNames, null);

            System.out.println("OBSERVE: resolveDependency can fall back to dependencyName (field name) matching beanName");
            System.out.println("OBSERVE: dependencyName == secondaryWorker => resolved beanName => " + autowiredBeanNames);

            assertThat(resolved).isSameAs(context.getBean("secondaryWorker", Worker.class));
            assertThat(resolved.id()).isEqualTo("secondary");
            assertThat(autowiredBeanNames).contains("secondaryWorker");
        }
    }

    @Test
    void resolveDependency_throwsNoUnique_whenNoSignalCanNarrowCandidates() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean("primaryWorker", PrimaryWorker.class);
            context.registerBean("secondaryWorker", SecondaryWorker.class);
            context.refresh();

            Field field = AmbiguousInjectionPoint.class.getDeclaredField("worker");
            DependencyDescriptor descriptor = new DependencyDescriptor(field, true);

            System.out.println("OBSERVE: dependencyName == worker (does not match any beanName) and no @Qualifier/@Primary => NoUnique");
            assertThatThrownBy(() -> context.getDefaultListableBeanFactory()
                    .resolveDependency(descriptor, null, new LinkedHashSet<>(), null))
                    .isInstanceOf(NoUniqueBeanDefinitionException.class);
        }
    }

    @Test
    void resolveDependency_qualifierAnnotation_canNarrowCandidates_programmatically() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean("primaryWorker", PrimaryWorker.class);
            context.registerBean("secondaryWorker", SecondaryWorker.class);
            context.refresh();

            Field field = QualifierInjectionPoint.class.getDeclaredField("worker");
            DependencyDescriptor descriptor = new DependencyDescriptor(field, true);

            Set<String> autowiredBeanNames = new LinkedHashSet<>();
            Worker resolved = (Worker) context.getDefaultListableBeanFactory()
                    .resolveDependency(descriptor, null, autowiredBeanNames, null);

            System.out.println("OBSERVE: @Qualifier on injection point participates in resolveDependency candidate filtering");
            System.out.println("OBSERVE: resolved beanName => " + autowiredBeanNames);

            assertThat(resolved).isSameAs(context.getBean("secondaryWorker", Worker.class));
            assertThat(resolved.id()).isEqualTo("secondary");
            assertThat(autowiredBeanNames).contains("secondaryWorker");
        }
    }

    interface Worker {
        String id();
    }

    static class PrimaryWorker implements Worker {
        @Override
        public String id() {
            return "primary";
        }
    }

    static class SecondaryWorker implements Worker {
        @Override
        public String id() {
            return "secondary";
        }
    }

    static class ByNameFallbackInjectionPoint {
        @Autowired
        private Worker secondaryWorker;
    }

    static class AmbiguousInjectionPoint {
        @Autowired
        private Worker worker;
    }

    static class QualifierInjectionPoint {
        @Autowired
        @Qualifier("secondaryWorker")
        private Worker worker;
    }
}

