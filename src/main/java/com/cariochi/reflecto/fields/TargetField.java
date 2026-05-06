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

/**
 * Field reflection bound to a concrete target object, or to {@code null} for a
 * static field.
 */
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class TargetField implements Reflection, IsField {

    private final Object target;

    private final ReflectoField field;

    /**
     * Returns the resolved field type.
     * <p>
     * For type-variable fields on a target instance, the runtime target class is
     * used as a fallback resolution context.
     */
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

    /**
     * Reads the field value, making the underlying field accessible when needed.
     *
     * @param <V> expected value type
     * @return field value
     */
    @SneakyThrows
    public <V> V getValue() {
        return (V) FieldUtils.readField(rawField(), target, true);
    }

    /**
     * Writes the field value, making the underlying field accessible when needed.
     *
     * @param value new value
     * @param <V> value type
     */
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
