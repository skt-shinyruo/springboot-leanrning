package com.learning.springboot.springcorebeans.part03_container_internals;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcorebeans.testsupport.BeanDefinitionOriginDumper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;

/**
 * 最小对照：基础设施 Bean（processors 等）通常会标记为 {@link BeanDefinition#ROLE_INFRASTRUCTURE}。
 *
 * <p>排障价值：当你在“列 Bean 列表/追溯来源”时，role 能帮助你快速分出“业务 Bean vs 容器能力 Bean”。</p>
 */
class SpringCoreBeansInfrastructureBeanRoleLabTest {

    @Test
    void annotationConfigProcessors_areInfrastructureBeanDefinitions_byRole() {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
            context.registerBean("userService", UserService.class);
            context.refresh();

            var beanFactory = context.getDefaultListableBeanFactory();

            BeanDefinition autowiredBpp = beanFactory.getBeanDefinition(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME);
            BeanDefinition configurationProcessor = beanFactory.getBeanDefinition(AnnotationConfigUtils.CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME);
            BeanDefinition userService = beanFactory.getBeanDefinition("userService");

            System.out.println("OBSERVE: Infrastructure processors are ROLE_INFRASTRUCTURE");
            System.out.println(BeanDefinitionOriginDumper.dump(beanFactory, AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
            System.out.println(BeanDefinitionOriginDumper.dump(beanFactory, AnnotationConfigUtils.CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
            System.out.println(BeanDefinitionOriginDumper.dump(beanFactory, "userService"));

            assertThat(autowiredBpp.getRole()).isEqualTo(BeanDefinition.ROLE_INFRASTRUCTURE);
            assertThat(configurationProcessor.getRole()).isEqualTo(BeanDefinition.ROLE_INFRASTRUCTURE);
            assertThat(userService.getRole()).isEqualTo(BeanDefinition.ROLE_APPLICATION);
        }
    }

    static class UserService {
    }
}
