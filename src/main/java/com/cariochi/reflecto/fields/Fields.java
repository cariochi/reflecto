package com.cariochi.reflecto.fields;

import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

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

    public JavaField field(String name) {
        return new JavaField(instance, name);
    }

    public List<JavaField> all() {
        return getAllFieldsList(instance.getClass()).stream()
                .flatMap(field -> javaFieldStream(instance, field))
                .collect(toList());
    }

    public List<JavaField> withType(Class<?> fieldType) {
        return all().stream()
                .filter(field -> fieldType.isAssignableFrom(field.getType()))
                .collect(toList());
    }

    public List<JavaField> withAnnotation(Class<? extends Annotation> annotationCls) {
        return all().stream()
                .filter(field -> field.findAnnotation(annotationCls).isPresent())
                .collect(toList());
    }

    public List<JavaField> withTypeAndAnnotation(Class<?> fieldType, Class<? extends Annotation> annotationCls) {
        return all().stream()
                .filter(field -> field.findAnnotation(annotationCls).isPresent())
                .filter(field -> fieldType.isAssignableFrom(field.getType()))
                .collect(toList());
    }

    private Stream<JavaField> javaFieldStream(Object object, Field field) {
        final JavaField javaField = new JavaField(object, field);
        return field.isSynthetic()
                ? (includeEnclosing ? reflect(javaField.getValue()).fields().includeEnclosing().all().stream() : Stream.empty())
                : Stream.of(javaField);
    }

}
