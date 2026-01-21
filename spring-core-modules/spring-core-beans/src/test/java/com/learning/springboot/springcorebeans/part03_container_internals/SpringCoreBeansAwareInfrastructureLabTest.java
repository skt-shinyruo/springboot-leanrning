package com.learning.springboot.springcorebeans.part03_container_internals;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;

class SpringCoreBeansAwareInfrastructureLabTest {

    @Test
    void beanFactoryAware_isInvokedByBeanFactory_butApplicationContextAware_needsAnInfrastructureProcessor() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("awareBean", new RootBeanDefinition(AwareBean.class));

        AwareBean bean = beanFactory.getBean("awareBean", AwareBean.class);

        assertThat(bean.beanFactory()).isSameAs(beanFactory);
        assertThat(bean.applicationContext()).isNull();
    }

    @Test
    void applicationContextAware_isInvokedByInfrastructureBeanPostProcessor() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        GenericApplicationContext applicationContext = new GenericApplicationContext();
        beanFactory.addBeanPostProcessor(new ManualApplicationContextAwareProcessor(applicationContext));
        beanFactory.registerBeanDefinition("awareBean", new RootBeanDefinition(AwareBean.class));

        AwareBean bean = beanFactory.getBean("awareBean", AwareBean.class);

        assertThat(bean.beanFactory()).isSameAs(beanFactory);
        assertThat(bean.applicationContext()).isSameAs(applicationContext);
    }

    static class AwareBean implements BeanFactoryAware, ApplicationContextAware {
        private BeanFactory beanFactory;
        private ApplicationContext applicationContext;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        BeanFactory beanFactory() {
            return beanFactory;
        }

        ApplicationContext applicationContext() {
            return applicationContext;
        }
    }

    static class ManualApplicationContextAwareProcessor implements BeanPostProcessor {
        private final ApplicationContext applicationContext;

        ManualApplicationContextAwareProcessor(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof ApplicationContextAware aware) {
                aware.setApplicationContext(applicationContext);
            }
            return bean;
        }
    }
}
