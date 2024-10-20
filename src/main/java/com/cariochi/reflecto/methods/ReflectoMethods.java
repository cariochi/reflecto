package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.types.ReflectoType;
import java.util.List;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class ReflectoMethods extends MethodsStreamable<ReflectoMethod> {

    @Getter
    private final MethodsStreamable<ReflectoMethod> declared;

    public ReflectoMethods(ReflectoType declaringType,
                           Supplier<List<ReflectoMethod>> listSupplier,
                           Supplier<List<ReflectoMethod>> declaredListSupplier) {
        super(declaringType, listSupplier);
        this.declared = new MethodsStreamable<>(declaringType, declaredListSupplier);
    }
}
