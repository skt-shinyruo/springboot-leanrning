package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.annotation.PostConstruct;

import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;

class SpringCoreBeansInjectionPhaseLabTest {

    @Test
    void fieldInjection_happensDuringPropertyPopulation_betweenAfterInstantiationAndPostConstruct() {
        Probe probe = new Probe();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(context);

            context.registerBean(InjectionPhaseProbePostProcessor.class, () -> new InjectionPhaseProbePostProcessor(probe));
            context.registerBean(Dependency.class);
            context.registerBean(FieldInjectedTarget.class);
            context.refresh();

            FieldInjectedTarget target = context.getBean(FieldInjectedTarget.class);

            System.out.println("OBSERVE: Field injection is NOT available in the constructor");
            System.out.println("OBSERVE: Field injection becomes available after instantiation (during property population)");
            System.out.println("OBSERVE: @PostConstruct sees injected dependencies (because it runs after injection)");

            assertThat(target.dependencyWasNullInConstructor()).isTrue();
            assertThat(probe.snapshot(TargetKey.FIELD, Phase.AFTER_INSTANTIATION).dependencyPresent()).isFalse();
            assertThat(probe.snapshot(TargetKey.FIELD, Phase.BEFORE_INITIALIZATION).dependencyPresent()).isTrue();
            assertThat(target.dependencyWasPresentInPostConstruct()).isTrue();
        }
    }

    @Test
    void constructorInjection_usesAutowiredConstructor_whenMultipleConstructorsExist() {
        Probe probe = new Probe();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(context);

            context.registerBean(InjectionPhaseProbePostProcessor.class, () -> new InjectionPhaseProbePostProcessor(probe));
            context.registerBean(Dependency.class);
            context.registerBean(ConstructorInjectedTarget.class);
            context.refresh();

            ConstructorInjectedTarget target = context.getBean(ConstructorInjectedTarget.class);

            System.out.println("OBSERVE: Constructor injection provides dependencies during object construction");
            System.out.println("OBSERVE: With multiple constructors, @Autowired selects the injection constructor");

            assertThat(target.constructorUsed()).isEqualTo("autowired");
            assertThat(target.dependencyPresentInConstructor()).isTrue();
            assertThat(probe.snapshot(TargetKey.CONSTRUCTOR, Phase.AFTER_INSTANTIATION).dependencyPresent()).isTrue();
            assertThat(target.dependencyWasPresentInPostConstruct()).isTrue();
        }
    }

    enum TargetKey {
        FIELD,
        CONSTRUCTOR
    }

    enum Phase {
        AFTER_INSTANTIATION,
        DURING_PROPERTY_POPULATION,
        BEFORE_INITIALIZATION
    }

    static class Snapshot {
        private final boolean dependencyPresent;

        Snapshot(boolean dependencyPresent) {
            this.dependencyPresent = dependencyPresent;
        }

        boolean dependencyPresent() {
            return dependencyPresent;
        }
    }

    static class Probe {
        private final Map<TargetKey, Map<Phase, Snapshot>> snapshots = new EnumMap<>(TargetKey.class);

        void record(TargetKey targetKey, Phase phase, Snapshot snapshot) {
            snapshots.computeIfAbsent(targetKey, ignored -> new EnumMap<>(Phase.class)).put(phase, snapshot);
        }

        Snapshot snapshot(TargetKey targetKey, Phase phase) {
            return snapshots.getOrDefault(targetKey, Map.of()).get(phase);
        }
    }

    static class InjectionPhaseProbePostProcessor implements InstantiationAwareBeanPostProcessor {
        private final Probe probe;

        InjectionPhaseProbePostProcessor(Probe probe) {
            this.probe = probe;
        }

        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
            if (bean instanceof FieldInjectedTarget target) {
                probe.record(TargetKey.FIELD, Phase.AFTER_INSTANTIATION, new Snapshot(target.dependency() != null));
            }
            if (bean instanceof ConstructorInjectedTarget target) {
                probe.record(TargetKey.CONSTRUCTOR, Phase.AFTER_INSTANTIATION, new Snapshot(target.dependency() != null));
            }
            return true;
        }

        @Override
        public org.springframework.beans.PropertyValues postProcessProperties(
                org.springframework.beans.PropertyValues pvs,
                Object bean,
                String beanName
        ) throws BeansException {
            if (bean instanceof FieldInjectedTarget target) {
                probe.record(TargetKey.FIELD, Phase.DURING_PROPERTY_POPULATION, new Snapshot(target.dependency() != null));
            }
            if (bean instanceof ConstructorInjectedTarget target) {
                probe.record(TargetKey.CONSTRUCTOR, Phase.DURING_PROPERTY_POPULATION, new Snapshot(target.dependency() != null));
            }
            return pvs;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof FieldInjectedTarget target) {
                probe.record(TargetKey.FIELD, Phase.BEFORE_INITIALIZATION, new Snapshot(target.dependency() != null));
            }
            if (bean instanceof ConstructorInjectedTarget target) {
                probe.record(TargetKey.CONSTRUCTOR, Phase.BEFORE_INITIALIZATION, new Snapshot(target.dependency() != null));
            }
            return bean;
        }
    }

    static class Dependency {
    }

    static class FieldInjectedTarget {

        @org.springframework.beans.factory.annotation.Autowired
        private Dependency dependency;

        private final boolean dependencyWasNullInConstructor;
        private boolean dependencyWasPresentInPostConstruct;

        FieldInjectedTarget() {
            dependencyWasNullInConstructor = dependency == null;
        }

        @PostConstruct
        void init() {
            dependencyWasPresentInPostConstruct = dependency != null;
        }

        boolean dependencyWasNullInConstructor() {
            return dependencyWasNullInConstructor;
        }

        boolean dependencyWasPresentInPostConstruct() {
            return dependencyWasPresentInPostConstruct;
        }

        Dependency dependency() {
            return dependency;
        }
    }

    static class ConstructorInjectedTarget {

        private final Dependency dependency;
        private final String constructorUsed;
        private final boolean dependencyPresentInConstructor;
        private boolean dependencyWasPresentInPostConstruct;

        ConstructorInjectedTarget() {
            dependency = null;
            constructorUsed = "no-arg";
            dependencyPresentInConstructor = false;
        }

        @org.springframework.beans.factory.annotation.Autowired
        ConstructorInjectedTarget(Dependency dependency) {
            this.dependency = dependency;
            constructorUsed = "autowired";
            dependencyPresentInConstructor = dependency != null;
        }

        @PostConstruct
        void init() {
            dependencyWasPresentInPostConstruct = dependency != null;
        }

        String constructorUsed() {
            return constructorUsed;
        }

        boolean dependencyPresentInConstructor() {
            return dependencyPresentInConstructor;
        }

        boolean dependencyWasPresentInPostConstruct() {
            return dependencyWasPresentInPostConstruct;
        }

        Dependency dependency() {
            return dependency;
        }
    }
}
