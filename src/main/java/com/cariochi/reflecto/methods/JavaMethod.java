package com.cariochi.reflecto.methods;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.ClassUtils.getAllInterfaces;
import static org.apache.commons.lang3.ClassUtils.getAllSuperclasses;
import static org.apache.commons.lang3.ClassUtils.isAssignable;
import static org.apache.commons.lang3.reflect.MethodUtils.getAnnotation;

@ToString
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

    public Type getReturnType() {
        return method.getGenericReturnType();
    }

    public Type[] getParameterTypes() {
        return method.getGenericParameterTypes();
    }

    private static Method getMatchingMethod(final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        return Stream.of(List.of(cls), getAllSuperclasses(cls), getAllInterfaces(cls))
                .flatMap(List::stream)
                .flatMap(c -> Stream.of(c.getDeclaredMethods()))
                .filter(m -> methodName.equals(m.getName()) && isAssignable(parameterTypes, m.getParameterTypes(), true))
                .collect(groupingBy(m -> distance(parameterTypes, m.getParameterTypes()))).entrySet().stream()
                .min(comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue).stream()
                .flatMap(List::stream)
                .findFirst()
                .orElse(null);
    }

    private static int distance(final Class<?>[] fromClassArray, final Class<?>[] toClassArray) {
        return range(0, fromClassArray.length)
                .map(i -> {
                    final Class<?> fromClass = fromClassArray[i];
                    final Class<?> toClass = toClassArray[i];
                    return fromClass == null || fromClass.equals(toClass)
                            ? 0
                            : (isAssignable(fromClass, toClass, true) && !isAssignable(fromClass, toClass, false) ? 1 : 2);
                })
                .sum();
    }

}
