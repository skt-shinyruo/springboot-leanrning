package com.learning.springboot.springcorebeans.part01_ioc_container;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcorebeans.part01_ioc_container.componentscan.ScanComponent;
import com.learning.springboot.springcorebeans.testsupport.BeanDefinitionOriginDumper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;

class SpringCoreBeansBeanDefinitionRegistrationDiffLabTest {

    @Test
    void beanDefinitionMetadata_differsAcrossRegistrationMechanisms() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(ScanConfig.class, BeanMethodConfig.class, ImportRegistrarConfig.class);

            RootBeanDefinition programmatic = new RootBeanDefinition(ProgrammaticBean.class);
            programmatic.setLazyInit(true);
            programmatic.setResourceDescription("lab:programmatic-definition");
            programmatic.setSource("lab-source:programmatic");
            context.registerBeanDefinition("programmaticBean", programmatic);

            context.getBeanFactory().registerSingleton("singletonBean", new SingletonBean());

            context.refresh();

            DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();

            String scanDump = BeanDefinitionOriginDumper.dump(beanFactory, "scanComponent");
            String beanMethodDump = BeanDefinitionOriginDumper.dump(beanFactory, "beanMethodBean");
            String registrarDump = BeanDefinitionOriginDumper.dump(beanFactory, "registrarBean");
            String programmaticDump = BeanDefinitionOriginDumper.dump(beanFactory, "programmaticBean");
            String singletonDump = BeanDefinitionOriginDumper.dump(beanFactory, "singletonBean");

            assertThat(scanDump).contains("- beanName: scanComponent");
            assertThat(scanDump).contains("beanClassName: " + ScanComponent.class.getName());
            assertThat(scanDump).contains("factoryMethodName: (null)");

            assertThat(beanMethodDump).contains("- beanName: beanMethodBean");
            assertThat(beanMethodDump).contains("factoryMethodName: beanMethodBean");
            assertThat(beanMethodDump).doesNotContain("factoryMethodName: (null)");

            assertThat(registrarDump).contains("- beanName: registrarBean");
            assertThat(registrarDump).contains("beanClassName: " + RegistrarBean.class.getName());
            assertThat(registrarDump).contains("resourceDescription: lab:registrar-definition");

            assertThat(programmaticDump).contains("- beanName: programmaticBean");
            assertThat(programmaticDump).contains("beanClassName: " + ProgrammaticBean.class.getName());
            assertThat(programmaticDump).contains("resourceDescription: lab:programmatic-definition");

            assertThat(singletonDump).contains("- beanName: singletonBean");
            assertThat(singletonDump).contains("- beanDefinition: (none)");
            assertThat(singletonDump).contains("- hint:");
        }
    }

    @Configuration
    @ComponentScan(basePackages = "com.learning.springboot.springcorebeans.part01_ioc_container.componentscan")
    static class ScanConfig {
    }

    @Configuration
    static class BeanMethodConfig {

        @Bean
        BeanMethodBean beanMethodBean() {
            return new BeanMethodBean();
        }
    }

    static class BeanMethodBean {
    }

    @Configuration
    @Import(Registrar.class)
    static class ImportRegistrarConfig {
    }

    static class Registrar implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            RootBeanDefinition bd = new RootBeanDefinition(RegistrarBean.class);
            bd.setLazyInit(true);
            bd.setResourceDescription("lab:registrar-definition");
            bd.setSource("lab-source:registrar");
            registry.registerBeanDefinition("registrarBean", bd);
        }
    }

    static class RegistrarBean {
    }

    static class ProgrammaticBean {
    }

    static class SingletonBean {
    }
}
