package com.cariochi.reflecto.types;

import com.cariochi.reflecto.base.Streamable;
import com.cariochi.reflecto.exceptions.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static com.cariochi.reflecto.utils.MessageUtils.typeNames;

/**
 * Streamable type collection with type-name lookup helpers.
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class TypesStreamable implements Streamable<ReflectoType> {

    private final Supplier<List<ReflectoType>> listSupplier;

    @Getter(lazy = true)
    private final List<ReflectoType> list = listSupplier.get();

    /**
     * Finds a type by resolved type name.
     *
     * @param name type name
     * @return matching type, if present
     */
    public Optional<ReflectoType> find(String name) {
        return stream()
                .filter(type -> name.equals(type.name()))
                .findFirst();
    }

    /**
     * Returns a type by resolved type name.
     *
     * @param name type name
     * @return matching type
     * @throws NotFoundException when no type matches
     */
    public ReflectoType get(String name) {
        return find(name)
                .orElseThrow(() -> new NotFoundException("Type {0} not found. Available types: {1}",
                        name,
                        typeNames(stream())));
    }
}
