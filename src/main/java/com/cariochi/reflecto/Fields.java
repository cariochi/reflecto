package com.cariochi.reflecto;

import com.cariochi.reflecto.fields.JavaField;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.annotation.Annotation;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.reflect.FieldUtils.getAllFieldsList;

@RequiredArgsConstructor
public class Fields {

    private final Object instance;

    public JavaField get(String name) {
        return new JavaField(instance, name);
    }

    public List<JavaField> all() {
        return getAllFieldsList(instance.getClass()).stream()
                .map(field -> new JavaField(instance, field))
                .collect(toList());
    }

    public List<JavaField> withType(Class<?> fieldType) {
        return all().stream()
                .filter(field -> fieldType.isAssignableFrom(field.getType()))
                .collect(toList());
    }

    public List<JavaField> withAnnotation(Class<? extends Annotation> annotationCls) {
        return FieldUtils.getFieldsListWithAnnotation(instance.getClass(), annotationCls).stream()
                .map(field -> new JavaField(instance, field))
                .collect(toList());
    }

    public List<JavaField> withTypeAndAnnotation(Class<?> fieldType,
                                                 Class<? extends Annotation> annotationClass) {
        return withAnnotation(annotationClass).stream()
                .filter(field -> fieldType.isAssignableFrom(field.getType()))
                .collect(toList());
    }

}
