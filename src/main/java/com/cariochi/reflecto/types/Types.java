package com.cariochi.reflecto.types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javassist.bytecode.SignatureAttribute.ClassSignature;
import javassist.util.proxy.ProxyFactory;
import lombok.experimental.UtilityClass;

import static com.cariochi.reflecto.utils.SignatureUtils.createClassSignature;

@UtilityClass
public class Types {

    public static Type any() {
        return type("?");
    }

    public static Type anyExtends(Type type) {
        return type("? extends " + type.getTypeName());
    }

    public static Type anySuper(Type type) {
        return type("? super " + type.getTypeName());
    }

    public static Type listOf(Type type) {
        return type(List.class, type);
    }

    public static Type setOf(Type type) {
        return type(Set.class, type);
    }

    public static Type mapOf(Type keyType, Type valueType) {
        return type(Map.class, keyType, valueType);
    }

    public static Type arrayOf(Type type) {
        return Types.type(type.getTypeName() + "[]");
    }

    public static Type optionalOf(Type type) {
        return Types.type(Optional.class, type);
    }

    public static Type streamOf(Type type) {
        return Types.type(Stream.class, type);
    }

    public static Type type(Type rawType, Type... typeArguments) {
        return type(TypeName.of(rawType, typeArguments));
    }

    public static Type type(String typeName) {
        return type(TypeName.parse(typeName));
    }

    private static Type type(TypeName typeName) {
        return type(createClassSignature(typeName.withParent(TypeReference.class), null));
    }

    private static Type type(ClassSignature signature) {
        final ProxyFactory factory = new ProxyFactory();
        factory.setUseCache(false);
        factory.setGenericSignature(signature.encode());
        final ParameterizedType parameterizedType = (ParameterizedType) factory.createClass().getGenericSuperclass();
        return parameterizedType.getActualTypeArguments()[0];
    }

}
