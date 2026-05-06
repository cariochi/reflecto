package com.cariochi.reflecto.base;

import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Common metadata contract for reflected fields.
 */
public interface IsField {

    /**
     * Returns the underlying Java field.
     *
     * @return underlying Java field
     */
    Field rawField();

    /**
     * Returns the type that declares this field.
     *
     * @return type that declares this field
     */
    ReflectoType declaringType();

    /**
     * Returns the field name.
     *
     * @return field name
     */
    default String name() {
        return rawField().getName();
    }

    /**
     * Returns the field type resolved against the declaring type.
     *
     * @return resolved field type
     */
    default ReflectoType type() {
        return declaringType().reflect(rawField().getGenericType());
    }

    /**
     * Checks whether the field is synthetic.
     *
     * @return {@code true} when synthetic
     */
    default boolean isSynthetic() {
        return rawField().isSynthetic();
    }

    /**
     * Checks whether the field is an enum constant.
     *
     * @return {@code true} when this field is an enum constant
     */
    default boolean isEnumConstant() {
        return rawField().isEnumConstant();
    }

    /**
     * Returns the generic field signature.
     *
     * @return generic field signature
     */
    default String toGenericString() {
        return rawField().toGenericString();
    }

    /**
     * Returns field annotations.
     *
     * @return field annotations
     */
    default ReflectoAnnotations annotations() {
        return new ReflectoAnnotations(
                () -> List.of(rawField().getAnnotations()),
                () -> List.of(rawField().getDeclaredAnnotations())
        );
    }

    /**
     * Returns field modifiers.
     *
     * @return field modifiers
     */
    default ReflectoModifiers modifiers() {
        return new ReflectoModifiers(rawField().getModifiers());
    }

}
