package com.cariochi.reflecto.fields;

import java.util.List;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class ReflectoFields extends FieldsStreamable<ReflectoField> {

    @Getter
    private final FieldsStreamable<ReflectoField> declared;

    public ReflectoFields(Supplier<List<ReflectoField>> listSupplier, Supplier<List<ReflectoField>> declaredListSupplier) {
        super(listSupplier);
        this.declared = new FieldsStreamable<>(declaredListSupplier);
    }

}
