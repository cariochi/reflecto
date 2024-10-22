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
public class TargetMethod implements IsMethod {

    private final Object target;

    private final ReflectoMethod method;

    @SneakyThrows
    public <V> V invoke(Object... args) {
        return withArguments(args).invoke();
    }

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
