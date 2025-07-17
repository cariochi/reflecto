package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.base.IsMethod;
import com.cariochi.reflecto.base.ReflectoAnnotations;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Method;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@RequiredArgsConstructor
public class ReflectoMethodInvocation implements IsMethod {

    private final ReflectoMethod method;
    private final Object[] arguments;

    public TargetMethodInvocation withTarget(Object target) {
        if (modifiers().isStatic()) {
            return new TargetMethodInvocation(method.asStatic(), arguments);
        }
        return new TargetMethodInvocation(method.withTarget(target), arguments);
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
