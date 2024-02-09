package com.cariochi.reflecto.utils;

import com.cariochi.reflecto.methods.ReflectoMethod;
import com.cariochi.reflecto.parameters.ReflectoParameter;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Value;
import lombok.experimental.UtilityClass;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.ClassUtils.isAssignable;

@UtilityClass
public class MethodsUtils {

    public static Optional<ReflectoMethod> findMatchingMethod(List<ReflectoMethod> methods, String methodName, Class<?>... parameterTypes) {
        return methods.stream()
                .filter(m -> methodName.equals(m.name()) && isAssignable(parameterTypes, getActualParameterTypes(m), true))
                .collect(groupingBy(m -> distance(parameterTypes, getActualParameterTypes(m)))).entrySet().stream()
                .min(comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue).stream()
                .flatMap(List::stream)
                .findFirst();
    }

    public static List<ReflectoMethod> getMethods(ReflectoType type) {
        return new ArrayList<>(collectMethods(type).values());
    }

    private static Map<MethodSignature, ReflectoMethod> collectMethods(ReflectoType type) {

        final Map<MethodSignature, ReflectoMethod> thisMethods = Stream.of(type.actualClass().getDeclaredMethods())
                .collect(toMap(
                        MethodSignature::new,
                        m -> new ReflectoMethod(m, type),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        if (type.superType() != null) {
            final Map<MethodSignature, ReflectoMethod> superMethods = collectMethods(type.superType());
            mergeMethods(superMethods, thisMethods);
        }

        type.interfaces().forEach(superInterface -> {
            final Map<MethodSignature, ReflectoMethod> superMethods = collectMethods(superInterface);
            mergeMethods(superMethods, thisMethods);

        });

        return thisMethods;
    }

    private static void mergeMethods(Map<MethodSignature, ReflectoMethod> superMethods, Map<MethodSignature, ReflectoMethod> thisMethods) {
        superMethods.forEach((s, m) -> {
            final ReflectoMethod thisMethod = thisMethods.get(s);
            if (thisMethod == null) {
                thisMethods.put(s, m);
            } else {
                thisMethod.addSuperMethod(m);
            }
        });
    }

    private static Class<?>[] getActualParameterTypes(ReflectoMethod method) {
        return method.parameters().stream().map(ReflectoParameter::type).map(ReflectoType::actualClass).toArray(Class[]::new);
    }

    private static int distance(Class<?>[] fromClassArray, Class<?>[] toClassArray) {
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

    @Value
    private static class MethodSignature {

        String name;
        Type[] parameterTypes;

        public MethodSignature(Method method) {
            this.name = method.getName();
            this.parameterTypes = method.getGenericParameterTypes();
        }

    }

}
