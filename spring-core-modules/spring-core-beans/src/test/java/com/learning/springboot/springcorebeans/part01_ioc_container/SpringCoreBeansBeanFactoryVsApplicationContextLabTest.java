package com.learning.springboot.springcorebeans.part01_ioc_container;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

class SpringCoreBeansBeanFactoryVsApplicationContextLabTest {

    @Test
    void beanFactory_isTheCoreContainer_withoutApplicationLevelFacilities() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("exampleBean", new RootBeanDefinition(ExampleBean.class));

        ExampleBean bean = beanFactory.getBean("exampleBean", ExampleBean.class);

        assertThat(bean).isNotNull();
        assertThat(beanFactory).isNotInstanceOf(ApplicationEventPublisher.class);
        assertThat(beanFactory).isNotInstanceOf(MessageSource.class);
        assertThat(beanFactory).isNotInstanceOf(ResourceLoader.class);
    }

    @Test
    void applicationContext_addsEventsMessagesAndResources_andHooksThemIntoRefresh() {
        List<String> events = new ArrayList<>();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            StaticMessageSource messageSource = new StaticMessageSource();
            messageSource.addMessage("greeting", Locale.CHINA, "你好");

            context.registerBean("messageSource", StaticMessageSource.class, () -> messageSource);
            context.addApplicationListener((ContextRefreshedEvent ignored) -> events.add("contextRefreshed"));

            context.refresh();

            assertThat(context).isInstanceOf(ApplicationEventPublisher.class);
            assertThat(context).isInstanceOf(MessageSource.class);
            assertThat(context).isInstanceOf(ResourceLoader.class);

            Resource resource = context.getResource("classpath:mockito-extensions/org.mockito.plugins.MockMaker");
            assertThat(resource.exists()).isTrue();
            assertThat(context.getMessage("greeting", null, Locale.CHINA)).isEqualTo("你好");
        }

        assertThat(events).contains("contextRefreshed");
    }

    static class ExampleBean {
    }
}

