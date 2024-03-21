package com.cariochi.reflecto.utils;

import com.cariochi.reflecto.fields.ReflectoField;
import com.cariochi.reflecto.methods.ReflectoMethod;
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

@UtilityClass
public class MethodsUtils {

    public static Optional<ReflectoMethod> findMatchingMethod(List<ReflectoMethod> methods, String methodName, List<ReflectoType> argTypes) {
        return methods.stream()
                .filter(method -> methodName.equals(method.name()) && isAssignable(argTypes, method.parameters().types()))
                .collect(groupingBy(method -> distance(argTypes, method.parameters().types()))).entrySet().stream()
                .min(comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue).stream()
                .flatMap(List::stream)
                .findFirst();
    }


    private static int distance(List<ReflectoType> fromTypes, List<ReflectoType> toTypes) {
        return range(0, fromTypes.size())
                .map(i -> {
                    final ReflectoType fromType = fromTypes.get(i);
                    final ReflectoType toType = toTypes.get(i);
                    return fromType == null || fromType.equals(toType)
                            ? 0
                            : (isAssignable(fromType, toType, true) && !isAssignable(fromType, toType, false) ? 1 : 2);
                })
                .sum();
    }

    private static boolean isAssignable(List<ReflectoType> fromTypes, List<ReflectoType> toTypes) {
        if (fromTypes.size() != toTypes.size()) {
            return false;
        }
        for (int i = 0; i < fromTypes.size(); i++) {
            if (!isAssignable(fromTypes.get(i), toTypes.get(i), true)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAssignable(ReflectoType fromType, ReflectoType toType, boolean autoboxing) {
        return fromType.is(toType.actualType(), autoboxing);
    }

    public static List<ReflectoMethod> collectMethods(ReflectoType declaringType, boolean includeEnclosing) {
        final List<ReflectoMethod> methods = new ArrayList<>(collectMethods(declaringType).values());
        if (includeEnclosing) {
            Stream.of(declaringType.actualClass().getDeclaredFields())
                    .map(declaringType::reflect)
                    .map(MethodsUtils::getEnclosingMethods)
                    .flatMap(List::stream)
                    .forEach(methods::add);
        }
        return methods;
    }

    private static Map<MethodSignature, ReflectoMethod> collectMethods(ReflectoType type) {

        final Map<MethodSignature, ReflectoMethod> thisMethods = Stream.of(type.actualClass().getDeclaredMethods())
                .collect(toMap(
                        MethodSignature::new,
                        type::reflect,
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

    private static List<ReflectoMethod> getEnclosingMethods(ReflectoField syntheticField) {
        final ArrayList<ReflectoMethod> enclosingMethods = new ArrayList<>();

        syntheticField.type().methods().stream()
                .peek(f -> f.syntheticParent(syntheticField))
                .forEach(enclosingMethods::add);

        Stream.of(syntheticField.type().actualClass().getDeclaredFields())
                .map(syntheticField.type()::reflect)
                .filter(ReflectoField::isSynthetic)
                .peek(field -> field.syntheticParent(syntheticField))
                .map(MethodsUtils::getEnclosingMethods)
                .flatMap(List::stream)
                .forEach(enclosingMethods::add);

        return enclosingMethods;
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
