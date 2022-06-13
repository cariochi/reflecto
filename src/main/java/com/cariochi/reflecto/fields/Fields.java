package com.cariochi.reflecto.fields;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cariochi.reflecto.Reflecto.reflect;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.reflect.FieldUtils.getAllFieldsList;

@RequiredArgsConstructor
public class Fields {

    private final Object instance;

    public JavaField field(String name) {
        return new JavaField(instance, name);
    }

    private static Object getEnclosingInstance(Object instance) {
        return reflect(instance).fields().all().stream()
                .filter(JavaField::isSynthetic)
                .findFirst()
                .map(JavaField::getValue)
                .orElse(null);
    }

    public List<JavaField> getEnclosingFields(Function<Fields, List<JavaField>> getFields) {
        return Stream.iterate(instance, Fields::getEnclosingInstance)
                .takeWhile(x -> x != null)
                .map(instance -> reflect(instance).fields())
                .flatMap(fields -> getFields.apply(fields).stream())
                .collect(toList());
    }

    public List<JavaField> all() {
        return getAllFieldsList(instance.getClass()).stream()
                .map(field -> new JavaField(instance, field))
                .collect(toList());
    }

    public List<JavaField> all(boolean includeEnclosing) {
        return includeEnclosing ? getEnclosingFields(Fields::all) : all();
    }

    public List<JavaField> withType(Class<?> fieldType) {
        return all().stream()
                .filter(field -> fieldType.isAssignableFrom(field.getType()))
                .collect(toList());
    }

    public List<JavaField> withType(Class<?> fieldType, boolean includeEnclosing) {
        return includeEnclosing
                ? getEnclosingFields(fields -> fields.withType(fieldType))
                : withType(fieldType);
    }

    public List<JavaField> withAnnotation(Class<? extends Annotation> annotationCls) {
        return FieldUtils.getFieldsListWithAnnotation(instance.getClass(), annotationCls).stream()
                .map(field -> new JavaField(instance, field))
                .collect(toList());
    }

    public List<JavaField> withAnnotation(Class<? extends Annotation> annotationCls, boolean includeEnclosing) {
        return includeEnclosing
                ? getEnclosingFields(fields -> fields.withAnnotation(annotationCls))
                : withAnnotation(annotationCls);
    }

    public List<JavaField> withTypeAndAnnotation(Class<?> fieldType,
                                                 Class<? extends Annotation> annotationClass) {
        return withAnnotation(annotationClass).stream()
                .filter(field -> fieldType.isAssignableFrom(field.getType()))
                .collect(toList());
    }

    public List<JavaField> withTypeAndAnnotation(Class<?> fieldType,
                                                 Class<? extends Annotation> annotationCls,
                                                 boolean includeEnclosing) {
        return includeEnclosing
                ? getEnclosingFields(fields -> fields.withTypeAndAnnotation(fieldType, annotationCls))
                : withTypeAndAnnotation(fieldType, annotationCls);
    }
}
