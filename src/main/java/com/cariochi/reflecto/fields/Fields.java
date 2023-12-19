package com.cariochi.reflecto.fields;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;

import static com.cariochi.reflecto.Reflecto.reflect;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.reflect.FieldUtils.getAllFieldsList;

@RequiredArgsConstructor
public class Fields {

    private final Object instance;
    private final boolean includeEnclosing;

    public Fields includeEnclosing() {
        return new Fields(instance, true);
    }

    public JavaField get(String name) {
        return new JavaField(instance, name);
    }

    public List<JavaField> asList() {
        return getAllFieldsList(instance.getClass()).stream()
                .flatMap(field -> javaFieldStream(instance, field))
                .collect(toList());
    }

    public List<JavaField> withType(Class<?> fieldType) {
        return asList().stream()
                .filter(field -> fieldType.isAssignableFrom(field.getType()))
                .collect(toList());
    }

    public List<JavaField> withAnnotation(Class<? extends Annotation> annotationCls) {
        return asList().stream()
                .filter(field -> field.findAnnotation(annotationCls).isPresent())
                .collect(toList());
    }

    public List<JavaField> withTypeAndAnnotation(Class<?> fieldType, Class<? extends Annotation> annotationCls) {
        return asList().stream()
                .filter(field -> field.findAnnotation(annotationCls).isPresent())
                .filter(field -> fieldType.isAssignableFrom(field.getType()))
                .collect(toList());
    }

    private Stream<JavaField> javaFieldStream(Object object, Field field) {
        final JavaField javaField = new JavaField(object, field);
        if (field.isSynthetic()) {
            return includeEnclosing
                    ? reflect(javaField.getValue()).fields().includeEnclosing().asList().stream()
                    : Stream.empty();
        }
        return Stream.of(javaField);
    }

}
