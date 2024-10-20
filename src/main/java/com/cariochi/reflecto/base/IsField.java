package com.cariochi.reflecto.base;

import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Field;
import java.util.List;

public interface IsField {

    Field rawField();

    ReflectoType declaringType();

    default String name() {
        return rawField().getName();
    }

    default ReflectoType type() {
        return declaringType().reflect(rawField().getGenericType());
    }

    default boolean isSynthetic() {
        return rawField().isSynthetic();
    }

    default boolean isEnumConstant() {
        return rawField().isEnumConstant();
    }

    default String toGenericString() {
        return rawField().toGenericString();
    }

    default ReflectoAnnotations annotations() {
        return new ReflectoAnnotations(
                () -> List.of(rawField().getAnnotations()),
                () -> List.of(rawField().getDeclaredAnnotations())
        );
    }

    default ReflectoModifiers modifiers() {
        return new ReflectoModifiers(rawField().getModifiers());
    }

}
