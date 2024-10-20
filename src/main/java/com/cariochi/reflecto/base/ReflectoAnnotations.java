package com.cariochi.reflecto.base;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class ReflectoAnnotations extends AnnotationStreamable {

    @Getter
    private final AnnotationStreamable declared;

    public ReflectoAnnotations(Supplier<List<Annotation>> listSupplier, Supplier<List<Annotation>> declaredListSupplier) {
        super(listSupplier);
        this.declared = new AnnotationStreamable(declaredListSupplier);
    }
}
