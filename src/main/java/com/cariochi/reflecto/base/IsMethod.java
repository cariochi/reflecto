package com.cariochi.reflecto.base;

import com.cariochi.reflecto.parameters.ReflectoParameters;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Method;

public interface IsMethod {

    Method rawMethod();

    ReflectoType declaringType();

    default String name() {
        return rawMethod().getName();
    }

    default ReflectoType returnType() {
        return declaringType().reflect(rawMethod().getGenericReturnType());
    }

    default String toGenericString() {
        return rawMethod().toGenericString();
    }

    default boolean isSynthetic() {
        return rawMethod().isSynthetic();
    }

    default boolean isDefault() {
        return rawMethod().isDefault();
    }

    ReflectoAnnotations annotations();

    default ReflectoModifiers modifiers() {
        return new ReflectoModifiers(rawMethod().getModifiers());
    }

    default ReflectoParameters parameters() {
        return new ReflectoParameters(rawMethod(), declaringType());
    }

    default ReflectoExceptionTypes exceptions() {
        return new ReflectoExceptionTypes(rawMethod(), declaringType());
    }


}
