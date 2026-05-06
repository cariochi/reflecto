package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.base.IsMethod;
import com.cariochi.reflecto.base.ReflectoAnnotations;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Method;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

/**
 * Method reflection bound to a concrete target object, or to {@code null} for a
 * static method.
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@RequiredArgsConstructor
public class TargetMethod implements IsMethod {

    private final Object target;

    private final ReflectoMethod method;

    /**
     * Invokes the method immediately.
     *
     * @param args invocation arguments
     * @param <V> expected result type
     * @return invocation result
     */
    @SneakyThrows
    public <V> V invoke(Object... args) {
        return withArguments(args).invoke();
    }

    /**
     * Creates an invocation with pre-bound arguments.
     *
     * @param args invocation arguments
     * @return target method invocation
     */
    public TargetMethodInvocation withArguments(Object... args) {
        return new TargetMethodInvocation(this, args);
    }

    @Override
    public ReflectoType declaringType() {
        return method.declaringType();
    }

    @Override
    public ReflectoType returnType() {
        return method.returnType();
    }

    @Override
    public Method rawMethod() {
        return method.rawMethod();
    }

    @Override
    public ReflectoAnnotations annotations() {
        return method.annotations();
    }

    @Override
    public String toString() {
        return method.toString();
    }

}
