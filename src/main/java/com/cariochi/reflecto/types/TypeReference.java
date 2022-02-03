package com.cariochi.reflecto.types;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@EqualsAndHashCode
public abstract class TypeReference<T> {

    @Getter
    private final Type type;

    public TypeReference() {
        final ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        type = actualTypeArguments[0];
    }

}
