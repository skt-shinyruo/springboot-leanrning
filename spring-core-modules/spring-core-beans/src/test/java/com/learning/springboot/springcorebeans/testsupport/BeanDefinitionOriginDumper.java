package com.learning.springboot.springcorebeans.testsupport;

/*
 * 这是一个测试辅助工具：把 BeanDefinition 的“来源/工厂方法/资源描述/装饰链”输出成可读文本，便于排障与对照文档。
 * 设计目标：只读 BeanDefinition 元信息，不触发 bean 实例化。
 */

import java.util.Objects;
import java.util.StringJoiner;
import java.util.TreeMap;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

public final class BeanDefinitionOriginDumper {

    private BeanDefinitionOriginDumper() {
    }

    public static String dump(ConfigurableListableBeanFactory beanFactory, String beanName) {
        StringBuilder sb = new StringBuilder();
        sb.append("BEAN_DEFINITION_ORIGIN").append('\n');
        sb.append("- beanName: ").append(beanName).append('\n');

        if (!(beanFactory instanceof DefaultListableBeanFactory dlbf) || !dlbf.containsBeanDefinition(beanName)) {
            sb.append("- beanDefinition: (none)").append('\n');
            sb.append("- hint: 该 bean 可能来自 registerSingleton/resolvableDependency 或基础设施直接注册").append('\n');
            Class<?> type = beanFactory.getType(beanName, false);
            sb.append("- type: ").append(type == null ? "(unknown)" : type.getName()).append('\n');
            return sb.toString();
        }

        BeanDefinition bd = dlbf.getBeanDefinition(beanName);
        appendDefinition(sb, bd, 0);

        if (bd instanceof RootBeanDefinition rbd) {
            BeanDefinition originating = rbd.getOriginatingBeanDefinition();
            if (originating != null) {
                sb.append("- originatingBeanDefinition:").append('\n');
                appendDefinition(sb, originating, 1);
            }
        }

        return sb.toString();
    }

    private static void appendDefinition(StringBuilder sb, BeanDefinition bd, int indentLevel) {
        String indent = "  ".repeat(Math.max(0, indentLevel));

        sb.append(indent).append("- beanDefinitionType: ").append(bd.getClass().getName()).append('\n');
        sb.append(indent).append("- scope: ").append(normalizeScope(bd.getScope())).append('\n');
        sb.append(indent).append("- role: ").append(roleName(bd.getRole())).append('\n');
        sb.append(indent).append("- primary: ").append(bd.isPrimary()).append('\n');
        sb.append(indent).append("- autowireCandidate: ").append(bd.isAutowireCandidate()).append('\n');
        sb.append(indent).append("- lazyInit: ").append(bd.isLazyInit()).append('\n');
        sb.append(indent).append("- dependsOn: ").append(formatDependsOn(bd.getDependsOn())).append('\n');

        sb.append(indent).append("- resourceDescription: ")
                .append(Objects.toString(bd.getResourceDescription(), "(null)")).append('\n');
        sb.append(indent).append("- source: ").append(Objects.toString(bd.getSource(), "(null)")).append('\n');

        if (bd instanceof AbstractBeanDefinition abd) {
            sb.append(indent).append("- beanClassName: ").append(Objects.toString(abd.getBeanClassName(), "(null)")).append('\n');
            sb.append(indent).append("- factoryBeanName: ").append(Objects.toString(abd.getFactoryBeanName(), "(null)")).append('\n');
            sb.append(indent).append("- factoryMethodName: ").append(Objects.toString(abd.getFactoryMethodName(), "(null)")).append('\n');
            sb.append(indent).append("- synthetic: ").append(abd.isSynthetic()).append('\n');
            sb.append(indent).append("- abstract: ").append(abd.isAbstract()).append('\n');
            sb.append(indent).append("- description: ").append(Objects.toString(abd.getDescription(), "(null)")).append('\n');
            sb.append(indent).append("- initMethodName: ").append(Objects.toString(abd.getInitMethodName(), "(null)")).append('\n');
            sb.append(indent).append("- destroyMethodName: ").append(Objects.toString(abd.getDestroyMethodName(), "(null)")).append('\n');
            sb.append(indent).append("- qualifiers: ").append(formatQualifiers(abd)).append('\n');
        }
    }

    private static String normalizeScope(String scope) {
        return (scope == null || scope.isBlank()) ? "singleton" : scope;
    }

    private static String roleName(int role) {
        return switch (role) {
            case BeanDefinition.ROLE_APPLICATION -> "ROLE_APPLICATION";
            case BeanDefinition.ROLE_SUPPORT -> "ROLE_SUPPORT";
            case BeanDefinition.ROLE_INFRASTRUCTURE -> "ROLE_INFRASTRUCTURE";
            default -> String.valueOf(role);
        };
    }

    private static String formatDependsOn(String[] dependsOn) {
        if (dependsOn == null || dependsOn.length == 0) {
            return "(none)";
        }
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (String dep : dependsOn) {
            joiner.add(dep);
        }
        return joiner.toString();
    }

    private static String formatQualifiers(AbstractBeanDefinition abd) {
        if (abd.getQualifiers().isEmpty()) {
            return "(none)";
        }

        TreeMap<String, AutowireCandidateQualifier> sorted = new TreeMap<>();
        for (AutowireCandidateQualifier qualifier : abd.getQualifiers()) {
            sorted.put(qualifier.getTypeName(), qualifier);
        }

        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (AutowireCandidateQualifier qualifier : sorted.values()) {
            joiner.add(formatQualifier(qualifier));
        }
        return joiner.toString();
    }

    private static String formatQualifier(AutowireCandidateQualifier qualifier) {
        StringJoiner joiner = new StringJoiner(", ", qualifier.getTypeName() + "{", "}");
        String[] names = qualifier.attributeNames();
        TreeMap<String, Object> sorted = new TreeMap<>();
        for (String name : names) {
            sorted.put(name, qualifier.getAttribute(name));
        }
        for (var entry : sorted.entrySet()) {
            joiner.add(entry.getKey() + "=" + Objects.toString(entry.getValue(), "(null)"));
        }
        return joiner.toString();
    }
}
