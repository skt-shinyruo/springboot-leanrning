package com.learning.springboot.springcorebeans.part01_ioc_container;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.learning.springboot.springcorebeans.testsupport.BeanGraphDumper;

class SpringCoreBeansBeanGraphDebugLabTest {

    @Test
    void dumpBeanGraph_candidatesAndRecordedDependencies_helpTroubleshootWhyItsInjected() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanGraphConfiguration.class)) {
            Consumer consumer = context.getBean(Consumer.class);

            System.out.println("OBSERVE: candidates are discovered by type (before narrowing rules like @Primary/@Qualifier)");
            System.out.println(BeanGraphDumper.dumpCandidates(context.getBeanFactory(), Worker.class));

            System.out.println("OBSERVE: the container records a dependency edge after choosing the final injected bean");
            System.out.println(BeanGraphDumper.dumpDependencies(context.getBeanFactory(), "consumer"));

            assertThat(consumer.worker().id()).isEqualTo("primary");
            assertThat(context.getBeanFactory().getDependenciesForBean("consumer")).contains("primaryWorker");
            assertThat(context.getBeanFactory().getDependenciesForBean("consumer")).doesNotContain("secondaryWorker");
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

    record Consumer(Worker worker) {
    }

    @Configuration
    static class BeanGraphConfiguration {

        @Bean
        @Primary
        Worker primaryWorker() {
            return new PrimaryWorker();
        }

        @Bean
        Worker secondaryWorker() {
            return new SecondaryWorker();
        }

        @Bean
        Consumer consumer(Worker worker) {
            return new Consumer(worker);
        }
    }
}
