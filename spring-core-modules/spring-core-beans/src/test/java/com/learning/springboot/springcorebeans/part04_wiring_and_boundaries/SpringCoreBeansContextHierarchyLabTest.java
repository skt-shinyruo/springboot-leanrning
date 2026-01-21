package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringCoreBeansContextHierarchyLabTest {

    @Test
    void childContext_canSeeParentBeans_butParentCannotSeeChildBeans() {
        try (AnnotationConfigApplicationContext parent = new AnnotationConfigApplicationContext()) {
            parent.registerBean("parentOnly", ParentOnlyBean.class, () -> new ParentOnlyBean("parent"));
            parent.registerBean("shared", SharedBean.class, () -> new SharedBean("parent-shared"));
            parent.refresh();

            try (AnnotationConfigApplicationContext child = new AnnotationConfigApplicationContext()) {
                child.setParent(parent);
                child.registerBean("shared", SharedBean.class, () -> new SharedBean("child-shared"));
                child.registerBean("childOnly", ChildOnlyBean.class, () -> new ChildOnlyBean("child"));
                child.refresh();

                System.out.println("OBSERVE: child can access parent beans, and can override by name within child");
                assertThat(child.getBean(ParentOnlyBean.class).origin()).isEqualTo("parent");
                assertThat(child.getBean("shared", SharedBean.class).origin()).isEqualTo("child-shared");
                assertThat(parent.getBean("shared", SharedBean.class).origin()).isEqualTo("parent-shared");

                assertThat(child.getBean(ChildOnlyBean.class).origin()).isEqualTo("child");
                assertThatThrownBy(() -> parent.getBean(ChildOnlyBean.class))
                        .isInstanceOf(NoSuchBeanDefinitionException.class);
            }
        }
    }

    record ParentOnlyBean(String origin) {
    }

    record SharedBean(String origin) {
    }

    record ChildOnlyBean(String origin) {
    }
}
