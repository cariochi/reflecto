package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.base.IsField;
import com.cariochi.reflecto.base.Streamable;
import com.cariochi.reflecto.exceptions.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class FieldsStreamable<T extends IsField> implements Streamable<T> {

    private final Supplier<List<T>> listSupplier;

    @Getter(lazy = true)
    private final List<T> list = listSupplier.get();

    public Optional<T> find(String name) {
        return stream()
                .filter(field -> name.equals(field.name()))
                .findFirst();
    }

    public T get(String name) {
        return find(name)
                .orElseThrow(() -> new NotFoundException("Field {0} not found", name));
    }
}
