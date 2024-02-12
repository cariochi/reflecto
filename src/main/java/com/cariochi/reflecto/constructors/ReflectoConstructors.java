package com.cariochi.reflecto.constructors;

import com.cariochi.reflecto.base.Streamable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoConstructors implements Streamable<ReflectoConstructor> {

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

    public interface ConstructorGetter {

        ReflectoConstructor get(Class<?>... parameterTypes) throws NoSuchMethodException;

    }

}
