package com.learning.springboot.springcorebeans.part04_wiring_and_boundaries;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

class SpringCoreBeansMergedBeanDefinitionLabTest {

    @Test
    void mergedBeanDefinition_combinesParentAndChildMetadata_andTriggersMergedDefinitionPostProcessor() {
        Probe probe = new Probe();

        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.registerBean(MergedDefinitionProbePostProcessor.class, () -> new MergedDefinitionProbePostProcessor(probe));

            RootBeanDefinition parent = new RootBeanDefinition(MergedTarget.class);
            parent.getPropertyValues().add("parentOnly", "from-parent");
            parent.setInitMethodName("parentInit");

            GenericBeanDefinition child = new GenericBeanDefinition();
            child.setBeanClass(MergedTarget.class);
            child.setParentName("parentBean");
            child.getPropertyValues().add("childOnly", "from-child");

            context.registerBeanDefinition("parentBean", parent);
            context.registerBeanDefinition("childBean", child);

            context.refresh();

            BeanDefinition rawChild = context.getBeanFactory().getBeanDefinition("childBean");

            System.out.println("OBSERVE: registry stores the raw child BeanDefinition (still has parentName)");
            assertThat(rawChild).isInstanceOf(GenericBeanDefinition.class);
            assertThat(rawChild.getParentName()).isEqualTo("parentBean");
            assertThat(rawChild.getPropertyValues().contains("parentOnly")).isFalse();
            assertThat(rawChild.getInitMethodName()).isNull();

            BeanDefinition merged = context.getBeanFactory().getMergedBeanDefinition("childBean");

            System.out.println("OBSERVE: merged BeanDefinition is a RootBeanDefinition with parent metadata merged in");
            assertThat(merged).isInstanceOf(RootBeanDefinition.class);

            RootBeanDefinition mergedRoot = (RootBeanDefinition) merged;
            assertThat(getPropertyValue(mergedRoot, "parentOnly")).isEqualTo("from-parent");
            assertThat(getPropertyValue(mergedRoot, "childOnly")).isEqualTo("from-child");
            assertThat(mergedRoot.getInitMethodName()).isEqualTo("parentInit");

            MergedTarget bean = context.getBean("childBean", MergedTarget.class);
            assertThat(bean.parentOnly()).isEqualTo("from-parent");
            assertThat(bean.childOnly()).isEqualTo("from-child");
            assertThat(bean.parentInitCalled()).isTrue();

            Probe.Snapshot snapshot = probe.snapshot("childBean");

            System.out.println("OBSERVE: MergedBeanDefinitionPostProcessor sees the merged RootBeanDefinition");
            assertThat(snapshot).isNotNull();
            assertThat(snapshot.parentOnly()).isEqualTo("from-parent");
            assertThat(snapshot.childOnly()).isEqualTo("from-child");
            assertThat(snapshot.initMethodName()).isEqualTo("parentInit");
        }
    }

    private static Object getPropertyValue(RootBeanDefinition beanDefinition, String propertyName) {
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        if (propertyValues.getPropertyValue(propertyName) == null) {
            return null;
        }
        return propertyValues.getPropertyValue(propertyName).getValue();
    }

    static class MergedTarget {
        private String parentOnly;
        private String childOnly;
        private boolean parentInitCalled;

        public void setParentOnly(String parentOnly) {
            this.parentOnly = parentOnly;
        }

        public void setChildOnly(String childOnly) {
            this.childOnly = childOnly;
        }

        public void parentInit() {
            parentInitCalled = true;
        }

        String parentOnly() {
            return parentOnly;
        }

        String childOnly() {
            return childOnly;
        }

        boolean parentInitCalled() {
            return parentInitCalled;
        }
    }

    static class Probe {

        record Snapshot(String parentOnly, String childOnly, String initMethodName) {
        }

        private final Map<String, Snapshot> snapshots = new ConcurrentHashMap<>();

        void record(String beanName, RootBeanDefinition beanDefinition) {
            snapshots.put(beanName, new Snapshot(
                    (String) getPropertyValue(beanDefinition, "parentOnly"),
                    (String) getPropertyValue(beanDefinition, "childOnly"),
                    beanDefinition.getInitMethodName()
            ));
        }

        Snapshot snapshot(String beanName) {
            return snapshots.get(beanName);
        }
    }

    static class MergedDefinitionProbePostProcessor implements MergedBeanDefinitionPostProcessor {

        private final Probe probe;

        MergedDefinitionProbePostProcessor(Probe probe) {
            this.probe = probe;
        }

        @Override
        public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName)
                throws BeansException {
            if (!beanName.equals("childBean")) {
                return;
            }
            probe.record(beanName, beanDefinition);
        }
    }
}

