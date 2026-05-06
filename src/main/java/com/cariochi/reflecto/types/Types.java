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

/**
 * Factory methods for building runtime {@link Type} instances, including
 * parameterized types, arrays, wildcards, and parsed type declarations.
 *
 * <pre>{@code
 * Type list = Types.listOf(String.class);
 * Type map = Types.mapOf(String.class, Types.listOf(Integer.class));
 * Type parsed = Types.type("java.util.List<java.lang.String>");
 * }</pre>
 */
@UtilityClass
public class Types {

    /**
     * Creates an unbounded wildcard type ({@code ?}).
     *
     * @return wildcard type
     */
    public static Type any() {
        return type("?");
    }

    /**
     * Creates an upper-bounded wildcard type.
     *
     * @param type upper bound
     * @return wildcard type equivalent to {@code ? extends type}
     */
    public static Type anyExtends(Type type) {
        return type("? extends " + type.getTypeName());
    }

    /**
     * Creates a lower-bounded wildcard type.
     *
     * @param type lower bound
     * @return wildcard type equivalent to {@code ? super type}
     */
    public static Type anySuper(Type type) {
        return type("? super " + type.getTypeName());
    }

    /**
     * Creates {@code List<T>}.
     *
     * @param type list element type
     * @return parameterized list type
     */
    public static Type listOf(Type type) {
        return type(List.class, type);
    }

    /**
     * Creates {@code Set<T>}.
     *
     * @param type set element type
     * @return parameterized set type
     */
    public static Type setOf(Type type) {
        return type(Set.class, type);
    }

    /**
     * Creates {@code Map<K, V>}.
     *
     * @param keyType map key type
     * @param valueType map value type
     * @return parameterized map type
     */
    public static Type mapOf(Type keyType, Type valueType) {
        return type(Map.class, keyType, valueType);
    }

    /**
     * Creates an array type whose component type may itself be parameterized.
     *
     * @param type component type
     * @return array type
     */
    public static Type arrayOf(Type type) {
        return Types.type(type.getTypeName() + "[]");
    }

    /**
     * Creates {@code Optional<T>}.
     *
     * @param type optional value type
     * @return parameterized optional type
     */
    public static Type optionalOf(Type type) {
        return Types.type(Optional.class, type);
    }

    /**
     * Creates {@code Stream<T>}.
     *
     * @param type stream element type
     * @return parameterized stream type
     */
    public static Type streamOf(Type type) {
        return Types.type(Stream.class, type);
    }

    /**
     * Creates a parameterized type from a raw type and its generic arguments.
     *
     * @param rawType raw class or owner type
     * @param typeArguments generic arguments
     * @return constructed type
     */
    public static Type type(Type rawType, Type... typeArguments) {
        return type(TypeName.of(rawType, typeArguments));
    }

    /**
     * Parses a type declaration into a runtime {@link Type}.
     * <p>
     * Supports nested generic declarations, arrays, primitive arrays, inner
     * classes, and wildcards.
     *
     * <pre>{@code
     * Type type = Types.type("java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>");
     * }</pre>
     *
     * @param typeName fully qualified type declaration
     * @return parsed type
     */
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
