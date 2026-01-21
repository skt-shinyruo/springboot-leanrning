package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

// 演示：replaced-method / MethodReplacer 属于 beans 体系的“方法注入（method injection）”机制之一。

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.learning.springboot.springcorebeans.part05_aot_and_real_world.methodinjection.ReplaceableGreetingService;

class SpringCoreBeansReplacedMethodLabTest {

    @Test
    void replacedMethod_overridesTargetMethodViaCglibSubclassing_andIsVisibleInBeanDefinitionMethodOverrides() {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "part05_aot_and_real_world/xml/replaced-method.xml"
        )) {
            ReplaceableGreetingService service = context.getBean("service", ReplaceableGreetingService.class);

            assertThat(service.greet("Alice")).isEqualTo("replaced:Alice");
            assertThat(Enhancer.isEnhanced(service.getClass())).isTrue();
            assertThat(service.getClass()).isNotEqualTo(ReplaceableGreetingService.class);

            AbstractBeanDefinition definition = (AbstractBeanDefinition) context.getBeanFactory().getBeanDefinition("service");
            assertThat(definition.getMethodOverrides().isEmpty()).isFalse();
        }
    }
}

