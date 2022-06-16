package com.cariochi.reflecto.fields;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.cariochi.reflecto.Reflecto.reflect;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.reflect.FieldUtils.getAllFieldsList;

@RequiredArgsConstructor
public class Fields {

    private final Object instance;
    private final boolean includeEnclosing;

    public Fields(Object instance) {
        this(instance,false);
    }

    public static Fields withEnclosing(Object instance) {
        return new Fields(instance, true);
    }

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

    private List<JavaField> allWithoutEnclosing() {
        return getAllFieldsList(instance.getClass()).stream()
                .map(field -> new JavaField(instance, field))
                .collect(toList());
    }

    public List<JavaField> all() {
        return includeEnclosing
                ? getEnclosingFields(Fields::allWithoutEnclosing)
                : allWithoutEnclosing();
    }

    private List<JavaField> withTypeWithoutEnclosing(Class<?> fieldType) {
        return all().stream()
                .filter(field -> fieldType.isAssignableFrom(field.getType()))
                .collect(toList());
    }

    public List<JavaField> withType(Class<?> fieldType) {
        return includeEnclosing
                ? getEnclosingFields(fields -> fields.withTypeWithoutEnclosing(fieldType))
                : withTypeWithoutEnclosing(fieldType);
    }

    private List<JavaField> withAnnotationWithoutEnclosing(Class<? extends Annotation> annotationCls) {
        return FieldUtils.getFieldsListWithAnnotation(instance.getClass(), annotationCls).stream()
                .map(field -> new JavaField(instance, field))
                .collect(toList());
    }

    public List<JavaField> withAnnotation(Class<? extends Annotation> annotationCls) {
        return includeEnclosing
                ? getEnclosingFields(fields -> fields.withAnnotationWithoutEnclosing(annotationCls))
                : withAnnotationWithoutEnclosing(annotationCls);
    }

    private List<JavaField> withTypeAndAnnotationWithoutEnclosing(Class<?> fieldType,
                                                                  Class<? extends Annotation> annotationClass) {
        return withAnnotation(annotationClass).stream()
                .filter(field -> fieldType.isAssignableFrom(field.getType()))
                .collect(toList());
    }

    public List<JavaField> withTypeAndAnnotation(Class<?> fieldType,
                                                 Class<? extends Annotation> annotationCls) {
        return includeEnclosing
                ? getEnclosingFields(fields -> fields.withTypeAndAnnotationWithoutEnclosing(fieldType, annotationCls))
                : withTypeAndAnnotationWithoutEnclosing(fieldType, annotationCls);
    }
}
