package com.learning.springboot.springcorebeans.appendix;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

@EnabledIfSystemProperty(named = "springcorebeans.explore", matches = "true")
class SpringCoreBeansCachedIntrospectionExploreTest {

    @Test
    void cachedIntrospectionResults_forClass_isCached_perBeanClass() throws Exception {
        Class<?> cached = Class.forName("org.springframework.beans.CachedIntrospectionResults");
        Method forClass = cached.getDeclaredMethod("forClass", Class.class);
        forClass.setAccessible(true);

        Object first = forClass.invoke(null, DemoBean.class);
        Object second = forClass.invoke(null, DemoBean.class);

        System.out.println("OBSERVE: CachedIntrospectionResults.forClass(beanClass) returns cached instance per class");
        assertThat(first).isSameAs(second);
    }

    static class DemoBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

