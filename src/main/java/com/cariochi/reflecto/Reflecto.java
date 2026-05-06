package com.cariochi.reflecto;

import com.cariochi.reflecto.constructors.ReflectoConstructor;
import com.cariochi.reflecto.fields.ReflectoField;
import com.cariochi.reflecto.invocations.model.NullReflection;
import com.cariochi.reflecto.invocations.model.Reflection;
import com.cariochi.reflecto.methods.ReflectoMethod;
import com.cariochi.reflecto.parameters.ReflectoParameter;
import com.cariochi.reflecto.proxy.ProxyType;
import com.cariochi.reflecto.proxy.ReflectoProxy;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/**
 * Main entry point for Reflecto.
 * <p>
 * Use this class to wrap Java reflection objects, runtime instances, generic
 * {@link Type} values, or to create proxy types.
 *
 * <pre>{@code
 * Reflection bug = Reflecto.reflect(bugInstance);
 * String username = bug.perform("reporter.username");
 *
 * ReflectoType type = Reflecto.reflect(Types.listOf(String.class));
 * boolean listOfString = type.is(Types.type(List.class, String.class));
 * }</pre>
 */
@RequiredArgsConstructor(access = PRIVATE)
public class Reflecto {

    /**
     * Creates a generic-aware type reflection.
     *
     * @param type class, parameterized type, wildcard type, array type, or other Java {@link Type}
     * @return type metadata wrapper
     */
    public static ReflectoType reflect(Type type) {
        return new ReflectoType(type);
    }

    /**
     * Creates a reflection wrapper around a runtime instance.
     * <p>
     * A {@code null} instance returns a null-safe reflection that resolves to
     * {@code null} for value access and nested expressions.
     *
     * @param instance object to reflect, or {@code null}
     * @return object reflection wrapper
     */
    public static Reflection reflect(Object instance) {
        return instance == null ? new NullReflection() : reflect(instance.getClass()).reflect(instance);
    }

    /**
     * Wraps a Java field as a Reflecto field.
     *
     * @param field Java reflection field
     * @return field metadata wrapper
     */
    public static ReflectoField reflect(Field field) {
        return reflect(field.getDeclaringClass()).reflect(field);
    }

    /**
     * Wraps a Java method as a Reflecto method.
     *
     * @param method Java reflection method
     * @return method metadata wrapper
     */
    public static ReflectoMethod reflect(Method method) {
        return reflect(method.getDeclaringClass()).reflect(method);
    }

    /**
     * Wraps a Java parameter as a Reflecto parameter.
     *
     * @param parameter Java reflection parameter
     * @return parameter metadata wrapper
     */
    public static ReflectoParameter reflect(Parameter parameter) {
        return reflect(parameter.getDeclaringExecutable().getDeclaringClass()).reflect(parameter);
    }

    /**
     * Wraps a Java constructor as a Reflecto constructor.
     *
     * @param constructor Java reflection constructor
     * @return constructor metadata wrapper
     */
    public static ReflectoConstructor reflect(Constructor<?> constructor) {
        return reflect(constructor.getDeclaringClass()).reflect(constructor);
    }

    /**
     * Creates a proxy type definition from a superclass and/or interfaces.
     * <p>
     * At most one non-interface type is allowed. Parameterized types created by
     * {@link com.cariochi.reflecto.types.Types} are preserved in the generated
     * proxy signature.
     *
     * <pre>{@code
     * MyAbstractClass instance = Reflecto.proxy(MyAbstractClass.class)
     *         .with(MyHandler::new)
     *         .getConstructor(int.class)
     *         .newInstance(1);
     * }</pre>
     *
     * @param types superclass and/or interface types for the proxy
     * @return proxy type builder
     */
    public static ProxyType proxy(Type... types) {
        return ReflectoProxy.createTypeProxy(types);
    }

}
