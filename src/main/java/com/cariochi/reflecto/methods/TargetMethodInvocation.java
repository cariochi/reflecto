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

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@RequiredArgsConstructor
public class TargetMethodInvocation implements IsMethod {

    private final TargetMethod method;
    private final Object[] arguments;

    @SneakyThrows
    public <V> V invoke() {
        final Method rawMethod = rawMethod();
        final boolean accessible = rawMethod.isAccessible();
        rawMethod.setAccessible(true);
        final V result = (V) rawMethod.invoke(method.target(), arguments);
        rawMethod.setAccessible(accessible);
        return result;
    }

    @Override
    public Method rawMethod() {
        return method.rawMethod();
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
    public ReflectoAnnotations annotations() {
        return method.annotations();
    }
}
