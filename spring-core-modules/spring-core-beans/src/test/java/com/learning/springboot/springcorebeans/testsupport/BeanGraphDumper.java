package com.learning.springboot.springcorebeans.testsupport;

/*
 * 这是一个测试辅助工具：把“候选集合/依赖边/销毁顺序提示”以稳定文本输出，便于在 Lab 中做可观察性对照。
 * 设计目标：尽量不触发 bean 实例化（避免因为 dump 本身改变容器行为），只基于 BeanFactory/BeanDefinition 元信息输出。
 */

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public final class BeanGraphDumper {

    private BeanGraphDumper() {
    }

    public static String dumpCandidates(ConfigurableListableBeanFactory beanFactory, Class<?> requiredType) {
        String[] names = beanFactory.getBeanNamesForType(requiredType, true, false);
        Arrays.sort(names, Comparator.naturalOrder());

        StringBuilder sb = new StringBuilder();
        sb.append("CANDIDATES").append('\n');
        sb.append("- requiredType: ").append(requiredType.getName()).append('\n');

        if (names.length == 0) {
            sb.append("- candidates: (none)").append('\n');
            return sb.toString();
        }

        sb.append("- candidates:").append('\n');
        for (String name : names) {
            sb.append("  - ").append(name);

            BeanDefinition bd = getBeanDefinitionIfPresent(beanFactory, name);
            if (bd != null) {
                sb.append(" [scope=").append(normalizeScope(bd.getScope())).append(']');
                sb.append(" [primary=").append(bd.isPrimary()).append(']');
                sb.append(" [autowireCandidate=").append(bd.isAutowireCandidate()).append(']');

                String origin = originHint(bd);
                if (origin != null) {
                    sb.append(" [origin=").append(origin).append(']');
                }
            } else {
                sb.append(" [beanDefinition=(none)]");
            }

            Class<?> type = beanFactory.getType(name, false);
            if (type != null) {
                sb.append(" -> ").append(type.getName());
            }
            sb.append('\n');
        }

        sb.append("NOTE: 单依赖选择时，还会继续应用 @Primary/@Priority/@Qualifier/按名回退 等收敛规则。").append('\n');
        return sb.toString();
    }

    public static String dumpDependencies(ConfigurableListableBeanFactory beanFactory, String beanName) {
        StringBuilder sb = new StringBuilder();
        sb.append("DEPENDENCIES").append('\n');
        sb.append("- beanName: ").append(beanName).append('\n');

        String[] dependsOnFromBd = getDependsOnFromBeanDefinition(beanFactory, beanName);
        if (dependsOnFromBd != null && dependsOnFromBd.length > 0) {
            sb.append("- dependsOn (from BeanDefinition): ").append(format(dependsOnFromBd)).append('\n');
        } else {
            sb.append("- dependsOn (from BeanDefinition): (none)").append('\n');
        }

        sb.append("- dependenciesForBean (resolved injection edges): ")
                .append(format(beanFactory.getDependenciesForBean(beanName))).append('\n');

        sb.append("- dependentBeans (who depends on this bean): ")
                .append(format(beanFactory.getDependentBeans(beanName))).append('\n');

        sb.append("HINT: 销毁顺序 = 依赖边的逆序：dependent 先 destroy，dependency 后 destroy。").append('\n');
        return sb.toString();
    }

    private static BeanDefinition getBeanDefinitionIfPresent(ConfigurableListableBeanFactory beanFactory, String beanName) {
        if (!(beanFactory instanceof DefaultListableBeanFactory dlbf)) {
            return null;
        }
        if (!dlbf.containsBeanDefinition(beanName)) {
            return null;
        }
        return dlbf.getBeanDefinition(beanName);
    }

    private static String[] getDependsOnFromBeanDefinition(ConfigurableListableBeanFactory beanFactory, String beanName) {
        BeanDefinition bd = getBeanDefinitionIfPresent(beanFactory, beanName);
        return bd == null ? null : bd.getDependsOn();
    }

    private static String normalizeScope(String scope) {
        return (scope == null || scope.isBlank()) ? "singleton" : scope;
    }

    private static String format(String[] values) {
        if (values == null || values.length == 0) {
            return "(none)";
        }
        return Arrays.toString(values);
    }

    private static String originHint(BeanDefinition bd) {
        if (bd instanceof AbstractBeanDefinition abd) {
            if (abd.getFactoryBeanName() != null || abd.getFactoryMethodName() != null) {
                return "factory=" + Objects.toString(abd.getFactoryBeanName(), "(static)")
                        + "#" + Objects.toString(abd.getFactoryMethodName(), "(unknown)");
            }
            if (abd.getBeanClassName() != null) {
                return "class=" + abd.getBeanClassName();
            }
        }
        if (bd.getResourceDescription() != null) {
            return "resource=" + bd.getResourceDescription();
        }
        return null;
    }
}

