package com.cariochi.reflecto.methods;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.ClassUtils;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.reflect.MethodUtils.getMethodsListWithAnnotation;

@RequiredArgsConstructor
public class Methods {

    private final Object instance;

    public JavaMethod method(String name, Class<?>... argClasses) {
        return new JavaMethod(instance, substringBefore(name, "("), argClasses);
    }

    public List<JavaMethod> asList() {
        final List<Class<?>> classes = getAllSuperclassesAndInterfaces(instance.getClass());
        classes.add(0, instance.getClass());
        final Set<MethodSignature> uniqueMethods = new HashSet<>();
        return classes.stream()
                .flatMap(acls -> Stream.of(acls.getDeclaredMethods()))
                .filter(method -> uniqueMethods.add(new MethodSignature(method)))
                .map(method -> new JavaMethod(instance, method))
                .collect(toList());
    }

    public List<JavaMethod> withAnnotation(Class<? extends Annotation> annotationCls) {
        return getMethodsListWithAnnotation(instance.getClass(), annotationCls, true, true).stream()
                .map(method -> new JavaMethod(instance, method))
                .collect(toList());
    }

    private static List<Class<?>> getAllSuperclassesAndInterfaces(final Class<?> cls) {
        final List<Class<?>> allSuperClassesAndInterfaces = new ArrayList<>();
        final List<Class<?>> allSuperclasses = ClassUtils.getAllSuperclasses(cls);
        int superClassIndex = 0;
        final List<Class<?>> allInterfaces = ClassUtils.getAllInterfaces(cls);
        int interfaceIndex = 0;
        while (interfaceIndex < allInterfaces.size() || superClassIndex < allSuperclasses.size()) {
            final Class<?> acls;
            if (interfaceIndex >= allInterfaces.size()) {
                acls = allSuperclasses.get(superClassIndex++);
            } else if (superClassIndex >= allSuperclasses.size() || superClassIndex >= interfaceIndex) {
                acls = allInterfaces.get(interfaceIndex++);
            } else {
                acls = allSuperclasses.get(superClassIndex++);
            }
            allSuperClassesAndInterfaces.add(acls);
        }
        return allSuperClassesAndInterfaces;
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
