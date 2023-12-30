package com.cariochi.reflecto.types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.SignatureAttribute.ArrayType;
import javassist.bytecode.SignatureAttribute.BaseType;
import javassist.bytecode.SignatureAttribute.ClassSignature;
import javassist.bytecode.SignatureAttribute.ClassType;
import javassist.bytecode.SignatureAttribute.TypeArgument;
import javassist.util.proxy.ProxyFactory;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Types {

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

    public static Type type(Class<?> rawType, Type... typeArguments) {
        return type(TypeName.of(rawType, typeArguments));
    }

    public static Type type(String typeName) {
        return type(TypeName.parse(typeName));
    }

    public static Type type(TypeName typeName) {
        return type(classSignature(typeName));
    }

    private static Type type(ClassSignature signature) {
        final ProxyFactory factory = new ProxyFactory();
        factory.setUseCache(false);
        factory.setGenericSignature(signature.encode());
        final ParameterizedType parameterizedType = (ParameterizedType) factory.createClass().getGenericSuperclass();
        return parameterizedType.getActualTypeArguments()[0];
    }

    private static ClassSignature classSignature(TypeName typeName) {
        final ClassType objectType = (ClassType) toObjectType(typeName.withParent(TypeReference.class));
        return new ClassSignature(null, objectType, null);
    }

    private static SignatureAttribute.ObjectType toObjectType(TypeName typeName) {
        final TypeArgument[] args = typeName.getArguments().stream()
                .map(Types::toObjectType)
                .map(TypeArgument::new)
                .toArray(TypeArgument[]::new);

        if (typeName.getDimension() > 0) {
            final SignatureAttribute.Type type = typeName.isPrimitive() ? baseType(typeName) : classType(typeName, args);
            return new ArrayType(typeName.getDimension(), type);
        } else {
            return classType(typeName, args);
        }
    }

    private static ClassType classType(TypeName typeName, TypeArgument[] args) {
        return new ClassType(typeName.getName(), args.length == 0 ? null : args);
    }

    private static BaseType baseType(TypeName typeName) {
        return new BaseType(typeName.getName());
    }

}
