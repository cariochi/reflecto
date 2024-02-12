package com.cariochi.reflecto.utils;

import com.cariochi.reflecto.types.Types;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.reflect.TypeUtils;

@UtilityClass
public class TypesUtils {

    public static Type getActualType(Type type, Type assigningType) {
        if (type instanceof Class<?>) {
            return type;
        } else if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            final boolean hasTypeVariables = Stream.of(parameterizedType.getActualTypeArguments()).anyMatch(TypeVariable.class::isInstance);
            if (hasTypeVariables) {
                return determineTypeArguments(parameterizedType, assigningType);
            } else {
                return type;
            }
        } else if (type instanceof TypeVariable<?>) {
            return assigningType == null ? null : determineTypeVariable((TypeVariable<?>) type, assigningType);
        } else if (type instanceof GenericArrayType) {
            return determineGenericArrayType((GenericArrayType) type, assigningType);
        } else {
            return type;
        }
    }

    private static Type determineTypeArguments(ParameterizedType type, Type assigningType) {
        final Type[] args = Stream.of(type.getActualTypeArguments()).map(t -> getActualType(t, assigningType)).toArray(Type[]::new);
        if (Stream.of(args).anyMatch(t -> t instanceof TypeVariable || t instanceof GenericArrayType)) {
            return type;
        }
        return Types.type((Class<?>) type.getRawType(), args);
    }

    private static Type determineTypeVariable(TypeVariable<?> type, Type assigningType) {
        if (!(assigningType instanceof ParameterizedType)) {
            return type;
        }
        final GenericDeclaration genericDeclaration = type.getGenericDeclaration();
        if (!(genericDeclaration instanceof Class<?>)) {
            return type;
        }
        final Class<?> aClass = (Class<?>) genericDeclaration;
        return Optional.ofNullable(TypeUtils.getTypeArguments(assigningType, aClass))
                .map(m -> m.get(type))
                .map(typeArgument -> getActualType(typeArgument, assigningType))
                .orElse(type);
    }

    private static Type determineGenericArrayType(GenericArrayType type, Type assigningType) {
        final Type componentType = type.getGenericComponentType();
        if (componentType instanceof ParameterizedType && assigningType != null) {
            return Types.arrayOf(getActualType(componentType, assigningType));
        } else {
            return type;
        }
    }

}
