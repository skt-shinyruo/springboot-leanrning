package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcorebeans.testsupport.BeanDefinitionOriginDumper;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 最小对照：候选选择不仅受注解影响，也会受 BeanDefinition 元数据（primary/autowireCandidate/qualifiers）影响。
 *
 * <p>真实项目排障时，经常会看到：
 * - 某个 bean “明明存在”，但注入就是找不到（可能被标成 {@code autowireCandidate=false}）
 * - 某个 bean “没写 @Primary”，但仍然是默认实现（可能是定义层被设置为 primary）
 * - {@code @Qualifier("x")} 看起来“匹配不到”，但其实 qualifier 信号存在于 BeanDefinition qualifiers 中</p>
 */
class SpringCoreBeansBeanDefinitionMetadataFlagsLabTest {

    @Test
    void beanDefinitionPrimaryFlag_participatesInCandidateSelection() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            RootBeanDefinition primary = new RootBeanDefinition(PrimaryWorker.class);
            primary.setPrimary(true);
            context.registerBeanDefinition("primaryWorker", primary);

            context.registerBeanDefinition("secondaryWorker", new RootBeanDefinition(SecondaryWorker.class));
            context.refresh();

            Field field = SingleWorkerInjectionPoint.class.getDeclaredField("worker");
            DependencyDescriptor descriptor = new DependencyDescriptor(field, true);

            Set<String> autowiredBeanNames = new LinkedHashSet<>();
            Worker resolved = (Worker) context.getDefaultListableBeanFactory()
                    .resolveDependency(descriptor, null, autowiredBeanNames, null);

            System.out.println("OBSERVE: BeanDefinition#setPrimary(true) works even without @Primary annotation");
            System.out.println(BeanDefinitionOriginDumper.dump(context.getDefaultListableBeanFactory(), "primaryWorker"));

            assertThat(resolved).isSameAs(context.getBean("primaryWorker", Worker.class));
            assertThat(autowiredBeanNames).contains("primaryWorker");
        }
    }

    @Test
    void beanDefinitionAutowireCandidateFalse_excludesBeanFromAutowiring_butBeanStillExists() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            RootBeanDefinition excluded = new RootBeanDefinition(ExcludedWorker.class);
            excluded.setAutowireCandidate(false);
            context.registerBeanDefinition("excludedWorker", excluded);

            context.registerBeanDefinition("candidateWorker", new RootBeanDefinition(CandidateWorker.class));
            context.refresh();

            Field field = SingleWorkerInjectionPoint.class.getDeclaredField("worker");
            DependencyDescriptor descriptor = new DependencyDescriptor(field, true);

            Set<String> autowiredBeanNames = new LinkedHashSet<>();
            Worker resolved = (Worker) context.getDefaultListableBeanFactory()
                    .resolveDependency(descriptor, null, autowiredBeanNames, null);

            System.out.println("OBSERVE: autowireCandidate=false bean is ignored during candidate matching");
            System.out.println(BeanDefinitionOriginDumper.dump(context.getDefaultListableBeanFactory(), "excludedWorker"));

            assertThat(resolved).isSameAs(context.getBean("candidateWorker", Worker.class));
            assertThat(context.getBean("excludedWorker", Worker.class)).isNotNull();
            assertThat(autowiredBeanNames).contains("candidateWorker");
        }
    }

    @Test
    void beanDefinitionQualifierMetadata_canBeMatchedByQualifierAnnotation() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            RootBeanDefinition special = new RootBeanDefinition(SpecialQualifiedWorker.class);
            AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(Qualifier.class);
            qualifier.setAttribute("value", "special");
            special.addQualifier(qualifier);
            context.registerBeanDefinition("specialWorker", special);

            context.registerBeanDefinition("defaultWorker", new RootBeanDefinition(DefaultWorker.class));
            context.refresh();

            Field field = QualifierWorkerInjectionPoint.class.getDeclaredField("worker");
            DependencyDescriptor descriptor = new DependencyDescriptor(field, true);

            Set<String> autowiredBeanNames = new LinkedHashSet<>();
            Worker resolved = (Worker) context.getDefaultListableBeanFactory()
                    .resolveDependency(descriptor, null, autowiredBeanNames, null);

            System.out.println("OBSERVE: BeanDefinition qualifiers participate in QualifierAnnotationAutowireCandidateResolver");
            System.out.println(BeanDefinitionOriginDumper.dump(context.getDefaultListableBeanFactory(), "specialWorker"));

            assertThat(resolved).isSameAs(context.getBean("specialWorker", Worker.class));
            assertThat(autowiredBeanNames).contains("specialWorker");
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

    static class ExcludedWorker implements Worker {
        @Override
        public String id() {
            return "excluded";
        }
    }

    static class CandidateWorker implements Worker {
        @Override
        public String id() {
            return "candidate";
        }
    }

    static class SpecialQualifiedWorker implements Worker {
        @Override
        public String id() {
            return "special";
        }
    }

    static class DefaultWorker implements Worker {
        @Override
        public String id() {
            return "default";
        }
    }

    static class SingleWorkerInjectionPoint {
        @Autowired
        private Worker worker;
    }

    static class QualifierWorkerInjectionPoint {
        @Autowired
        @Qualifier("special")
        private Worker worker;
    }
}

