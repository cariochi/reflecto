package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.base.Streamable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class ReflectoFields implements Streamable<ReflectoField> {

    private final Supplier<List<ReflectoField>> listSupplier;

    @Getter(lazy = true)
    private final List<ReflectoField> list = listSupplier.get();

    public Optional<ReflectoField> find(String name) {
        return stream()
                .filter(field -> name.equals(field.name()))
                .findFirst();
    }


}
