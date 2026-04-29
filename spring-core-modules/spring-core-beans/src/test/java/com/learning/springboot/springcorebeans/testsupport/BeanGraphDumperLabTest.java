package com.learning.springboot.springcorebeans.testsupport;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

public class BeanGraphDumperLabTest {

    @Test
    void dumpCandidatesAndDependencies_exposesStableBeanGraphFacts() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            RootBeanDefinition primary = new RootBeanDefinition(PrimaryWorker.class);
            primary.setPrimary(true);
            context.registerBeanDefinition("primaryWorker", primary);

            RootBeanDefinition secondary = new RootBeanDefinition(SecondaryWorker.class);
            secondary.setAutowireCandidate(false);
            secondary.setDependsOn("primaryWorker");
            context.registerBeanDefinition("secondaryWorker", secondary);

            context.refresh();

            String candidates = BeanGraphDumper.dumpCandidates(context.getBeanFactory(), Worker.class);
            assertThat(candidates).contains("CANDIDATES");
            assertThat(candidates).contains("- requiredType: " + Worker.class.getName());
            assertThat(candidates).contains("primaryWorker [scope=singleton] [primary=true]");
            assertThat(candidates).contains("secondaryWorker [scope=singleton] [primary=false] [autowireCandidate=false]");

            String dependencies = BeanGraphDumper.dumpDependencies(context.getBeanFactory(), "secondaryWorker");
            assertThat(dependencies).contains("DEPENDENCIES");
            assertThat(dependencies).contains("- beanName: secondaryWorker");
            assertThat(dependencies).contains("- dependsOn (from BeanDefinition): [primaryWorker]");
        }
    }

    interface Worker {
    }

    static class PrimaryWorker implements Worker {
    }

    static class SecondaryWorker implements Worker {
    }
}
