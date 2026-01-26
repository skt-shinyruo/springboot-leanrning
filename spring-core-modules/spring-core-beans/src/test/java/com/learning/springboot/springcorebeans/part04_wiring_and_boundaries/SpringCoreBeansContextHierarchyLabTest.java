package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringCoreBeansContextHierarchyLabTest {

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

    @Test
    void containsLocalBean_differsFromContainsBean_inChildContext() {
        try (AnnotationConfigApplicationContext parent = new AnnotationConfigApplicationContext()) {
            parent.registerBean("parentOnly", ParentOnlyBean.class, () -> new ParentOnlyBean("parent"));
            parent.registerBean("shared", SharedBean.class, () -> new SharedBean("parent-shared"));
            parent.refresh();

            try (AnnotationConfigApplicationContext child = new AnnotationConfigApplicationContext()) {
                child.setParent(parent);
                child.registerBean("shared", SharedBean.class, () -> new SharedBean("child-shared"));
                child.refresh();

                System.out.println("OBSERVE: child.containsBean(name) can see parent; child.containsLocalBean(name) only checks local registry");
                assertThat(child.containsBean("parentOnly")).isTrue();
                assertThat(child.containsLocalBean("parentOnly")).isFalse();

                assertThat(child.containsBean("shared")).isTrue();
                assertThat(child.containsLocalBean("shared")).isTrue();
            }
        }
    }

    @Test
    void typeLookupIncludingAncestors_canBecomeAmbiguous_whenParentAndChildBothProvideSameType() {
        try (AnnotationConfigApplicationContext parent = new AnnotationConfigApplicationContext()) {
            parent.registerBean("sharedParent", SharedBean.class, () -> new SharedBean("parent"));
            parent.refresh();

            try (AnnotationConfigApplicationContext child = new AnnotationConfigApplicationContext()) {
                child.setParent(parent);
                child.registerBean("sharedChild", SharedBean.class, () -> new SharedBean("child"));
                child.refresh();

                System.out.println("OBSERVE: BeanFactoryUtils.beanOfTypeIncludingAncestors(...) may throw when multiple beans exist across hierarchy");
                assertThatThrownBy(() -> BeanFactoryUtils.beanOfTypeIncludingAncestors(child, SharedBean.class))
                        .isInstanceOf(NoUniqueBeanDefinitionException.class);
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
