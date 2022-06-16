package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.Reflection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Optional;

import static org.apache.commons.lang3.reflect.FieldUtils.getField;

@RequiredArgsConstructor
public class JavaField implements Reflection {

    private final Object target;
    private final Field field;

    public JavaField(Object target, String fieldName) {
        this.target = target;
        this.field = getField(target.getClass(), fieldName, true);
    }

    public String getName() {
        return field.getName();
    }

    public Object getTarget() {
        return target;
    }

    public Class<?> getTargetClass() {
        return target.getClass();
    }

    public Type getGenericType() {
        return field.getGenericType();
    }

    public Class<?> getType() {
        return field.getType();
    }

    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(field.getModifiers());
    }

    public boolean isTransient() {
        return Modifier.isTransient(field.getModifiers());
    }

    public boolean isPrimitive() {
        return getType().isPrimitive();
    }

    public <A extends Annotation> Optional<A> findAnnotation(Class<A> annotationClass) {
        return Optional.ofNullable(field.getDeclaredAnnotation(annotationClass));
    }

    @SneakyThrows
    public <V> V getValue() {
        return (V) FieldUtils.readField(field, target, true);
    }

    @SneakyThrows
    public <V> void setValue(V value) {
        FieldUtils.writeField(field, target, value, true);
    }

}
