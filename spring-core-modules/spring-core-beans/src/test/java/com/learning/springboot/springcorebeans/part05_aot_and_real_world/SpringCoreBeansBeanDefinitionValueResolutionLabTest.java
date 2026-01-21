package com.learning.springboot.springcorebeans.part05_aot_and_real_world;

/*
 * 本实验把“值是怎么从 BeanDefinition 落到对象属性上的”讲成可下断点的主线：
 *
 * - BeanDefinition 里保存的是“原始 value”（字符串/引用/集合/Map/Properties），不是最终对象
 * - 真正把 value 解析成对象发生在实例化阶段的属性填充：
 *     AbstractAutowireCapableBeanFactory#applyPropertyValues
 *       -> BeanDefinitionValueResolver#resolveValueIfNecessary (分派不同 value 类型)
 *       -> BeanWrapper/TypeConverterDelegate 做类型转换
 *
 * 建议结合 docs 里的断点入口与观察点阅读。
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringCoreBeansBeanDefinitionValueResolutionLabTest {

    @Test
    void beanDefinitionValueResolver_canResolve_typedString_reference_and_managedCollections() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            registerHelperBeans(context);
            registerDemoBean(context);
            context.refresh();

            DemoBean demo = context.getBean(DemoBean.class);

            assertThat(demo.port()).isEqualTo(8080);
            assertThat(demo.primaryHelper().id()).isEqualTo("h1");
            assertThat(demo.tags()).containsExactly("alpha", "beta");
            assertThat(demo.helpers().get("cn").id()).isEqualTo("h1");
            assertThat(demo.helpers().get("en").id()).isEqualTo("h2");
            assertThat(demo.settings().getProperty("mode")).isEqualTo("lab");

            System.out.println("OBSERVE: BeanDefinitionValueResolver resolves RuntimeBeanReference/TypedStringValue/Managed* into real values");
        }
    }

    private static void registerHelperBeans(AnnotationConfigApplicationContext context) {
        RootBeanDefinition h1 = new RootBeanDefinition(Helper.class);
        h1.getPropertyValues().add("id", "h1");
        context.registerBeanDefinition("h1", h1);

        RootBeanDefinition h2 = new RootBeanDefinition(Helper.class);
        h2.getPropertyValues().add("id", "h2");
        context.registerBeanDefinition("h2", h2);
    }

    private static void registerDemoBean(AnnotationConfigApplicationContext context) {
        RootBeanDefinition demo = new RootBeanDefinition(DemoBean.class);

        // Explicit TypedStringValue so you can observe this branch in BeanDefinitionValueResolver
        TypedStringValue port = new TypedStringValue("8080");
        demo.getPropertyValues().add("port", port);

        demo.getPropertyValues().add("primaryHelper", new RuntimeBeanReference("h1"));

        ManagedList<Object> tags = new ManagedList<>();
        tags.add(new TypedStringValue("alpha"));
        tags.add("beta");
        demo.getPropertyValues().add("tags", tags);

        ManagedMap<String, Object> helpers = new ManagedMap<>();
        helpers.put("cn", new RuntimeBeanReference("h1"));
        helpers.put("en", new RuntimeBeanReference("h2"));
        demo.getPropertyValues().add("helpers", helpers);

        ManagedProperties settings = new ManagedProperties();
        settings.put("mode", new TypedStringValue("lab"));
        demo.getPropertyValues().add("settings", settings);

        context.registerBeanDefinition("demoBean", demo);
    }

    static class Helper {
        private String id;

        public void setId(String id) {
            this.id = id;
        }

        String id() {
            return id;
        }
    }

    static class DemoBean {
        private int port;
        private Helper primaryHelper;
        private List<String> tags;
        private Map<String, Helper> helpers;
        private Properties settings;

        public void setPort(int port) {
            this.port = port;
        }

        public void setPrimaryHelper(Helper primaryHelper) {
            this.primaryHelper = primaryHelper;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public void setHelpers(Map<String, Helper> helpers) {
            this.helpers = helpers;
        }

        public void setSettings(Properties settings) {
            this.settings = settings;
        }

        int port() {
            return port;
        }

        Helper primaryHelper() {
            return primaryHelper;
        }

        List<String> tags() {
            return tags;
        }

        Map<String, Helper> helpers() {
            return helpers;
        }

        Properties settings() {
            return settings;
        }
    }
}

