package com.cariochi.reflecto.base;

import com.cariochi.reflecto.constructors.ReflectoConstructor;
import com.cariochi.reflecto.exceptions.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static com.cariochi.reflecto.utils.MessageUtils.constructors;
import static com.cariochi.reflecto.utils.MessageUtils.typeNames;

/**
 * Streamable constructor collection with parameter-type lookup helpers.
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class ConstructorsStreamable implements Streamable<ReflectoConstructor> {

    private final Supplier<List<ReflectoConstructor>> listSupplier;
    private final ConstructorGetter findSupplier;

    @Getter(lazy = true)
    private final List<ReflectoConstructor> list = listSupplier.get();

    /**
     * Finds a constructor by parameter types.
     *
     * @param parameterTypes constructor parameter types
     * @return matching constructor, if present
     */
    public Optional<ReflectoConstructor> find(Class<?>... parameterTypes) {
        try {
            return Optional.of(findSupplier.get(parameterTypes));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    /**
     * Returns a constructor by parameter types.
     *
     * @param parameterTypes constructor parameter types
     * @return matching constructor
     * @throws NotFoundException when no constructor matches
     */
    public ReflectoConstructor get(Class<?>... parameterTypes) {
        return find(parameterTypes)
                .orElseThrow(() -> new NotFoundException("Constructor with parameter types {0} not found. Available constructors: {1}",
                        typeNames(parameterTypes),
                        constructors(stream())));
    }

    /**
     * Constructor lookup strategy used by public and declared views.
     */
    @FunctionalInterface
    public interface ConstructorGetter {

        /**
         * Returns a constructor by parameter types.
         *
         * @param parameterTypes constructor parameter types
         * @return matching constructor
         * @throws NoSuchMethodException when no constructor matches
         */
        ReflectoConstructor get(Class<?>... parameterTypes) throws NoSuchMethodException;

    }
}
