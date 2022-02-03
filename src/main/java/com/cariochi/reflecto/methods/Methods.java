package com.cariochi.reflecto.methods;

import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.reflect.MethodUtils.getMethodsListWithAnnotation;

@RequiredArgsConstructor
public class Methods {

    private final Object instance;

    public JavaMethod method(String name, Class<?>... argClasses) {
        return new JavaMethod(instance, substringBefore(name, "("), argClasses);
    }

    public List<JavaMethod> withAnnotation(Class<? extends Annotation> annotationCls) {
        return getMethodsListWithAnnotation(instance.getClass(), annotationCls, true, true).stream()
                .map(method -> new JavaMethod(instance, method))
                .collect(toList());
    }

}
