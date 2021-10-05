package com.cariochi.reflecto.methods;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.apache.commons.lang3.reflect.MethodUtils.getAnnotation;
import static org.apache.commons.lang3.reflect.MethodUtils.getMatchingMethod;

@RequiredArgsConstructor
public class JavaMethod {

    private final Object instance;
    private final Method method;

    public JavaMethod(Object instance, String name, Class<?>... argClasses) {
        this.instance = instance;
        this.method = getMatchingMethod(instance.getClass(), name, argClasses);
    }

    @SneakyThrows
    public <V> V invoke(Object... args) {
        method.setAccessible(true);
        return (V) method.invoke(instance, args);
    }

    public <A extends Annotation> Optional<A> findAnnotation(Class<A> annotationCls) {
        return Optional.ofNullable(getAnnotation(method, annotationCls, true, true));
    }

    public String getName() {
        return method.getName();
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

}
