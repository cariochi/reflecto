package com.cariochi.reflecto.constructors;

import com.cariochi.reflecto.base.ConstructorsStreamable;
import java.util.List;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class ReflectoConstructors extends ConstructorsStreamable {

    @Getter
    private final ConstructorsStreamable declared;

    public ReflectoConstructors(
            final Supplier<List<ReflectoConstructor>> listSupplier,
            final ConstructorGetter findSupplier,
            final Supplier<List<ReflectoConstructor>> declaredListSupplier,
            final ConstructorGetter declaredFindSupplier
    ) {
        super(listSupplier, findSupplier);
        this.declared = new ConstructorsStreamable(declaredListSupplier, declaredFindSupplier);
    }


}
