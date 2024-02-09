package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.Reflecto;
import com.cariochi.reflecto.base.IsField;
import com.cariochi.reflecto.invocations.model.Reflection;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Field;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.reflect.FieldUtils;

@EqualsAndHashCode
@RequiredArgsConstructor
@Accessors(fluent = true)
public class TargetField implements Reflection, IsField {

    @Getter
    private final Object target;

    private final ReflectoField field;

    @Override
    public ReflectoType type() {
        if (!field.type().isTypeVariable()) {
            return field.type();
        }
        if (target != null) {
            return Reflecto.reflect(target.getClass());
        }
        return null;
    }

    @Override
    public ReflectoType declaringType() {
        return field.declaringType();
    }

    @SneakyThrows
    public <V> V getValue() {
        return (V) FieldUtils.readField(rawField(), target, true);
    }

    @SneakyThrows
    public <V> void setValue(V value) {
        FieldUtils.writeField(rawField(), target, value, true);
    }

    @Override
    public Field rawField() {
        return field.rawField();
    }

    @Override
    public String toString() {
        return field.toString();
    }

}
