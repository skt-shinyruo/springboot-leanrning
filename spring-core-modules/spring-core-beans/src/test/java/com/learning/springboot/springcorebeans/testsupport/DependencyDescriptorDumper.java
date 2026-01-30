package com.learning.springboot.springcorebeans.testsupport;

/*
 * 这是一个测试辅助工具：把 DependencyDescriptor / InjectionPoint 的“注入点元数据”输出成可读文本，便于排障与对照文档。
 * 设计目标：把“注入点到底要什么（type/name/annotations）”变成可观察对象，且输出稳定，便于断言与跨章节引用。
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.StringJoiner;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;

public final class DependencyDescriptorDumper {

    private DependencyDescriptorDumper() {
    }

    public static String dump(DependencyDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        sb.append("DEPENDENCY_DESCRIPTOR").append('\n');
        appendInjectionPoint(sb, descriptor);

        sb.append("- required: ").append(descriptor.isRequired()).append('\n');
        sb.append("- dependencyName: ").append(Objects.toString(descriptor.getDependencyName(), "(null)")).append('\n');
        sb.append("- dependencyType: ").append(descriptor.getDependencyType().getName()).append('\n');
        sb.append("- resolvableType: ").append(Objects.toString(descriptor.getResolvableType(), "(null)")).append('\n');
        sb.append("- annotations: ").append(formatAnnotations(descriptor.getAnnotations())).append('\n');
        return sb.toString();
    }

    public static String dump(InjectionPoint injectionPoint) {
        StringBuilder sb = new StringBuilder();
        sb.append("INJECTION_POINT").append('\n');
        appendInjectionPoint(sb, injectionPoint);

        sb.append("- declaredType: ").append(injectionPoint.getDeclaredType().getName()).append('\n');
        sb.append("- dependencyName: ").append(Objects.toString(guessDependencyName(injectionPoint), "(null)")).append('\n');
        sb.append("- resolvableType: ").append(Objects.toString(resolveType(injectionPoint), "(null)")).append('\n');
        sb.append("- annotations: ").append(formatAnnotations(injectionPoint.getAnnotations())).append('\n');
        return sb.toString();
    }

    private static void appendInjectionPoint(StringBuilder sb, InjectionPoint injectionPoint) {
        Field field = injectionPoint.getField();
        if (field != null) {
            sb.append("- kind: Field").append('\n');
            sb.append("- member: ").append(field.getDeclaringClass().getName()).append('#').append(field.getName()).append('\n');
            return;
        }

        MethodParameter methodParameter = injectionPoint.getMethodParameter();
        if (methodParameter != null) {
            Member member = methodParameter.getMember();
            sb.append("- kind: MethodParameter").append('\n');
            sb.append("- member: ").append(member.getDeclaringClass().getName()).append('#').append(member.getName()).append('\n');
            sb.append("- parameterIndex: ").append(methodParameter.getParameterIndex()).append('\n');
            sb.append("- parameterType: ").append(methodParameter.getParameterType().getName()).append('\n');
            return;
        }

        sb.append("- kind: (unknown)").append('\n');
    }

    private static ResolvableType resolveType(InjectionPoint injectionPoint) {
        Field field = injectionPoint.getField();
        if (field != null) {
            return ResolvableType.forField(field);
        }
        MethodParameter methodParameter = injectionPoint.getMethodParameter();
        if (methodParameter != null) {
            return ResolvableType.forMethodParameter(methodParameter);
        }
        return ResolvableType.NONE;
    }

    private static String guessDependencyName(InjectionPoint injectionPoint) {
        Field field = injectionPoint.getField();
        if (field != null) {
            return field.getName();
        }
        MethodParameter methodParameter = injectionPoint.getMethodParameter();
        if (methodParameter != null) {
            return methodParameter.getParameterName();
        }
        return null;
    }

    private static String formatAnnotations(Annotation[] annotations) {
        if (annotations == null || annotations.length == 0) {
            return "[]";
        }

        Annotation[] sorted = Arrays.copyOf(annotations, annotations.length);
        Arrays.sort(sorted, Comparator.comparing(a -> a.annotationType().getName()));

        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (Annotation annotation : sorted) {
            joiner.add(formatAnnotation(annotation));
        }
        return joiner.toString();
    }

    private static String formatAnnotation(Annotation annotation) {
        if (annotation instanceof Qualifier qualifier) {
            return annotation.annotationType().getName() + "{value=" + qualifier.value() + "}";
        }
        return annotation.annotationType().getName();
    }
}

