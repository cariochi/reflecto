package com.cariochi.reflecto.base;

import com.cariochi.reflecto.parameters.ReflectoParameters;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Method;

/**
 * Common metadata contract for reflected methods.
 */
public interface IsMethod {

    /**
     * Returns the underlying Java method.
     *
     * @return underlying Java method
     */
    Method rawMethod();

    /**
     * Returns the type that declares this method.
     *
     * @return type that declares this method
     */
    ReflectoType declaringType();

    /**
     * Returns the method name.
     *
     * @return method name
     */
    default String name() {
        return rawMethod().getName();
    }

    /**
     * Returns the resolved method return type.
     *
     * @return resolved method return type
     */
    ReflectoType returnType();

    /**
     * Returns the generic method signature.
     *
     * @return generic method signature
     */
    default String toGenericString() {
        return rawMethod().toGenericString();
    }

    /**
     * Checks whether the method is synthetic.
     *
     * @return {@code true} when synthetic
     */
    default boolean isSynthetic() {
        return rawMethod().isSynthetic();
    }

    /**
     * Checks whether the method is a default interface method.
     *
     * @return {@code true} when default
     */
    default boolean isDefault() {
        return rawMethod().isDefault();
    }

    /**
     * Checks whether the method accepts varargs.
     *
     * @return {@code true} when varargs
     */
    default boolean isVarArgs() {
        return rawMethod().isVarArgs();
    }

    /**
     * Returns method annotations.
     *
     * @return method annotations
     */
    ReflectoAnnotations annotations();

    /**
     * Returns method modifiers.
     *
     * @return method modifiers
     */
    default ReflectoModifiers modifiers() {
        return new ReflectoModifiers(rawMethod().getModifiers());
    }

    /**
     * Returns method parameters.
     *
     * @return method parameters
     */
    default ReflectoParameters parameters() {
        return new ReflectoParameters(rawMethod(), declaringType());
    }

    /**
     * Returns declared exception types.
     *
     * @return declared exception types
     */
    default ReflectoExceptions exceptions() {
        return new ReflectoExceptions(rawMethod(), declaringType());
    }

}
