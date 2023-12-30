package com.cariochi.reflecto.types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public abstract class TypeReference<T> {

    private final Type type;

    public TypeReference() {
        final ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        type = actualTypeArguments[0];
    }

}
