package com.cariochi.reflecto.types;

import java.util.List;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class ReflectoTypes extends TypesStreamable {

    @Getter
    private final TypesStreamable declared;

    public ReflectoTypes(Supplier<List<ReflectoType>> listSupplier, Supplier<List<ReflectoType>> declaredListSupplier) {
        super(listSupplier);
        this.declared = new TypesStreamable(declaredListSupplier);
    }
}
