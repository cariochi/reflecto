package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.base.IsMethod;
import com.cariochi.reflecto.base.ReflectoAnnotations;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Method;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;


@EqualsAndHashCode
@RequiredArgsConstructor
public class TargetMethod implements IsMethod {

    private final Object target;
    private final ReflectoMethod method;

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

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <V> V invoke(Object... args) {
        final Method rawMethod = rawMethod();
        final boolean accessible = rawMethod.isAccessible();
        rawMethod.setAccessible(true);
        final V result = (V) rawMethod.invoke(target, args);
        rawMethod.setAccessible(accessible);
        return result;
    }

    @Override
    public String toString() {
        return method.toString();
    }

}
