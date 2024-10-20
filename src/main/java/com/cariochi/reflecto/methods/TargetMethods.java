package com.cariochi.reflecto.methods;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class TargetMethods extends MethodsStreamable<TargetMethod> {

    @Getter
    private final MethodsStreamable<TargetMethod> declared;

    public TargetMethods(ReflectoMethods methods, Object target) {
        super(methods.declaringType(), () -> methods.stream().map(method -> method.withTarget(target)).toList());
        declared = new MethodsStreamable<>(methods.declaringType(), () -> methods.declared().stream().map(method -> method.withTarget(target)).toList());
    }
}
