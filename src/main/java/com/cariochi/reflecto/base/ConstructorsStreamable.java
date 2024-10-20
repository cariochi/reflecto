package com.cariochi.reflecto.base;

import com.cariochi.reflecto.constructors.ReflectoConstructor;
import com.cariochi.reflecto.exceptions.NotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class ConstructorsStreamable implements Streamable<ReflectoConstructor> {

    private final Supplier<List<ReflectoConstructor>> listSupplier;
    private final ConstructorGetter findSupplier;

    @Getter(lazy = true)
    private final List<ReflectoConstructor> list = listSupplier.get();

    public Optional<ReflectoConstructor> find(Class<?>... parameterTypes) {
        try {
            return Optional.of(findSupplier.get(parameterTypes));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    public ReflectoConstructor get(Class<?>... parameterTypes) {
        return find(parameterTypes)
                .orElseThrow(() -> new NotFoundException("Constructor with parameters types {0} not found", Arrays.toString(parameterTypes)));
    }

    @FunctionalInterface
    public interface ConstructorGetter {

        ReflectoConstructor get(Class<?>... parameterTypes) throws NoSuchMethodException;

    }
}
